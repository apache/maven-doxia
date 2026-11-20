/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.doxia.sink.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.util.DoxiaUtils;

/**
 * A {@link SinkWrapper} which checks that the events sent to the wrapped sink follow the rules
 * documented in the {@link Sink} interface, both in its statements and in the canonical event sequences it shows
 * (for example for {@code head}, {@code figure}, {@code table} and lists). Those rules are independent of the
 * sink implementation. In {@link Mode#COLLECT collect mode} the wrapped sink receives all events unchanged,
 * whether or not they are valid; in {@link Mode#FAIL_FAST fail-fast mode} an event violating a rule is not
 * forwarded.
 * <p>
 * The following rules are checked:
 * </p>
 * <ul>
 *   <li>Every start event is closed by its matching end event, in proper nesting order
 *       (including the level of {@code section} and {@code sectionTitle}), and no element is
 *       left open when {@link #close()} is called.</li>
 *   <li>Section levels (also for {@code sectionTitle}) are between {@link Sink#SECTION_LEVEL_1} and
 *       {@link Sink#SECTION_LEVEL_6}, and a section is contained in sections of a lower level only.</li>
 *   <li>A {@code sectionTitle} is the first element within a {@code section} of the same level.</li>
 *   <li>{@code head} is unique, comes before {@code body} and contains {@code title}, {@code author}
 *       and {@code date} in this order, with {@code title} and {@code date} at most once.</li>
 *   <li>A {@code list} and a {@code numberedList} start with a list item, list items are direct children
 *       of the list of their kind, a {@code definitionListItem} is a direct child of a {@code definitionList}
 *       and contains exactly one {@code definedTerm} followed by exactly one {@code definition}.</li>
 *   <li>A {@code figure} contains {@code figureGraphics} and at most one {@code figureCaption}.</li>
 *   <li>A {@code table} contains {@code tableRows} (containing {@code tableRow}s, containing
 *       {@code tableCell}s and {@code tableHeaderCell}s) and at most one {@code tableCaption};
 *       the justification given to {@code tableRows} only uses the {@code JUSTIFY_*} constants.</li>
 *   <li>{@code verbatim} is not nested and contains neither {@code figure}, {@code italic}, {@code bold}
 *       nor {@code monospaced}, which includes {@code inline} and {@code text} with these semantics;
 *       its {@code decoration} attribute can only be {@code source}.</li>
 *   <li>Anchor names are valid Doxia ids ({@link DoxiaUtils#isValidId(String)}, which accepts a leading
 *       underscore like Doxia's own sinks) and link names can be parsed as a URI by {@link java.net.URI}
 *       (RFC 2396 parsing).</li>
 *   <li>The {@code semantics} attribute of {@code inline} and {@code text} has one of the documented values.</li>
 * </ul>
 * <p>
 * Rules which are neither stated nor shown in a canonical event sequence in the Javadoc of {@link Sink}
 * are not checked.
 * This wrapper is opt-in: it is neither registered automatically nor enabled by default.
 * It can be used programmatically or through {@link SinkContractValidatorFactory}.
 * </p>
 * <p>
 * In {@link Mode#FAIL_FAST fail-fast mode} the first violation is reported with a
 * {@link SinkContractViolationException} before the event is passed on. In {@link Mode#COLLECT collect mode}
 * all violations are recorded and can be retrieved with {@link #getViolations()}; one event produces at most one
 * violation. Nothing closes a wrapper created by a parser, so call {@link #finish()} (or {@link #close()}) at the
 * end of each document to detect elements left open.
 * </p>
 * <p>
 * An instance validates a single stream of events and is not thread-safe.
 * </p>
 *
 * @since 2.3.0
 */
public class SinkContractValidator extends SinkWrapper {

    /**
     * How a violation of the {@link Sink} contract is reported.
     */
    public enum Mode {
        /** Throw a {@link SinkContractViolationException} on the first violation. */
        FAIL_FAST,
        /** Record each violation, see {@link SinkContractValidator#getViolations()}. */
        COLLECT
    }

    /** The values of the semantics attribute, taken from the constants of {@link SinkEventAttributeSet.Semantics}. */
    private static final Set<String> SEMANTICS_VALUES;

    static {
        Set<String> values = new TreeSet<>();
        for (Field field : SinkEventAttributeSet.Semantics.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers())
                    && SinkEventAttributes.class.isAssignableFrom(field.getType())) {
                try {
                    values.add(String.valueOf(
                            ((SinkEventAttributes) field.get(null)).getAttribute(SinkEventAttributes.SEMANTICS)));
                } catch (IllegalAccessException e) {
                    throw new ExceptionInInitializerError(e);
                }
            }
        }
        SEMANTICS_VALUES = Collections.unmodifiableSet(values);
    }

    private static final Set<String> FONT_SEMANTICS =
            Collections.unmodifiableSet(new TreeSet<>(Arrays.asList("italic", "bold", "monospaced")));

    /** The element which must directly contain the given element. */
    private static final Map<String, String> REQUIRED_PARENT = new HashMap<>();

    static {
        REQUIRED_PARENT.put("listItem", "list");
        REQUIRED_PARENT.put("numberedListItem", "numberedList");
        REQUIRED_PARENT.put("definitionListItem", "definitionList");
        REQUIRED_PARENT.put("figureCaption", "figure");
        REQUIRED_PARENT.put("tableRows", "table");
        REQUIRED_PARENT.put("tableCaption", "table");
        REQUIRED_PARENT.put("tableRow", "tableRows");
        REQUIRED_PARENT.put("tableCell", "tableRow");
        REQUIRED_PARENT.put("tableHeaderCell", "tableRow");
    }

    /** An open element. */
    private static final class Frame {
        private final String name;
        private final int level;
        private int events;
        private final Map<String, Integer> counts = new HashMap<>();

        Frame(String name, int level) {
            this.name = name;
            this.level = level;
        }

        int count(String child) {
            Integer count = counts.get(child);
            return count == null ? 0 : count;
        }

        boolean is(String otherName, int otherLevel) {
            return name.equals(otherName) && level == otherLevel;
        }

        @Override
        public String toString() {
            return level > 0 ? name + "(" + level + ")" : name;
        }
    }

    private final Mode mode;

    private final Deque<Frame> stack = new ArrayDeque<>();

    private final List<String> violations = new ArrayList<>();

    private boolean headSeen;

    private boolean bodySeen;

    private String pending;

    /**
     * Creates a validator in {@link Mode#FAIL_FAST fail-fast mode}.
     *
     * @param delegate the sink receiving all events
     */
    public SinkContractValidator(Sink delegate) {
        this(delegate, Mode.FAIL_FAST);
    }

    /**
     * Creates a validator.
     *
     * @param delegate the sink receiving all events
     * @param mode how violations are reported
     */
    public SinkContractValidator(Sink delegate, Mode mode) {
        super(delegate);
        if (mode == null) {
            throw new IllegalArgumentException("mode must not be null");
        }
        this.mode = mode;
    }

    /**
     * Returns the violations recorded so far. The list is kept when the sink is closed.
     * In {@link Mode#FAIL_FAST fail-fast mode} the list is always empty.
     *
     * @return a copy of the violation messages in the order they occurred, never {@code null}
     */
    public List<String> getViolations() {
        return new ArrayList<>(violations);
    }

    // -- checks

    private void start(String name) {
        start(name, 0);
    }

    private void start(String name, int level) {
        doStart(name, level);
        commit();
    }

    private void end(String name, int level) {
        doEnd(name, level);
        commit();
    }

    private void leaf(String name) {
        doLeaf(name);
        commit();
    }

    private void doStart(String name, int level) {
        Frame frame = new Frame(name, level);
        String event = frame.toString();
        Frame parent = stack.peek();
        boolean first = parent == null || parent.events == 0;

        String requiredParent = REQUIRED_PARENT.get(name);
        if (requiredParent != null && (parent == null || !parent.name.equals(requiredParent))) {
            violation(
                    event,
                    "must be a direct child of '" + requiredParent + "' but the enclosing element is "
                            + describe(parent));
        }
        if ((name.equals("definedTerm") || name.equals("definition"))
                && (parent == null
                        || !(parent.name.equals("definitionList") || parent.name.equals("definitionListItem")))) {
            violation(
                    event,
                    "must be within a 'definitionList' or 'definitionListItem' but the enclosing element is "
                            + describe(parent));
        }
        if (parent != null && parent.events == 0) {
            if (parent.name.equals("list") && !name.equals("listItem")) {
                violation(event, "must be 'listItem' as the first element of a list");
            } else if (parent.name.equals("numberedList") && !name.equals("numberedListItem")) {
                violation(event, "must be 'numberedListItem' as the first element of a numbered list");
            }
        }
        if (parent != null) {
            parent.events++;
            parent.counts.merge(name, 1, Integer::sum);
        }

        switch (name) {
            case "head":
                if (headSeen) {
                    violation(event, "the head element must be unique within a document");
                }
                if (bodySeen) {
                    violation(event, "the head element must come before the body element");
                }
                headSeen = true;
                break;
            case "body":
                bodySeen = true;
                break;
            case "title":
            case "author":
            case "date":
                checkHeadChild(name, event, parent);
                break;
            case "section":
                checkSectionNesting(event, level);
                break;
            case "sectionTitle":
                if (parent == null || !parent.is("section", level)) {
                    violation(
                            event,
                            "must be the first element within 'section(" + level + ")' but the enclosing "
                                    + "element is " + describe(parent));
                } else if (!first) {
                    violation(event, "must be the first element within 'section(" + level + ")'");
                }
                break;
            case "definedTerm":
                if (parent != null && parent.name.equals("definitionListItem") && parent.count(name) > 1) {
                    violation(event, "a definitionListItem must contain exactly one definedTerm");
                } else if (parent != null
                        && parent.name.equals("definitionListItem")
                        && parent.count("definition") > 0) {
                    violation(event, "the definedTerm must come before the definition");
                }
                break;
            case "definition":
                if (parent != null && parent.name.equals("definitionListItem")) {
                    if (parent.count("definedTerm") == 0) {
                        violation(event, "the definition must come after the definedTerm");
                    } else if (parent.count(name) > 1) {
                        violation(event, "a definitionListItem must contain exactly one definition");
                    }
                }
                break;
            case "figureCaption":
                if (parent != null && parent.is("figure", 0) && parent.count(name) > 1) {
                    violation(event, "a figure may contain at most one figureCaption");
                }
                break;
            case "tableCaption":
                if (parent != null && parent.is("table", 0) && parent.count(name) > 1) {
                    violation(event, "a table may contain at most one tableCaption");
                }
                break;
            case "verbatim":
                if (isOpen("verbatim")) {
                    violation(event, "verbatim blocks may not be nested");
                }
                break;
            case "figure":
            case "italic":
            case "bold":
            case "monospaced":
                if (isOpen("verbatim")) {
                    violation(event, "not allowed within a verbatim block");
                }
                break;
            default:
                break;
        }
        stack.push(frame);
    }

    private void checkHeadChild(String name, String event, Frame parent) {
        if (parent == null || !parent.name.equals("head")) {
            return;
        }
        if (name.equals("title")
                && (parent.count("title") > 1 || parent.count("author") > 0 || parent.count("date") > 0)) {
            violation(event, "title must be unique and come before author and date within head");
        } else if (name.equals("author") && parent.count("date") > 0) {
            violation(event, "author must come before date within head");
        } else if (name.equals("date") && parent.count("date") > 1) {
            violation(event, "date must be unique within head");
        }
    }

    private void checkSectionNesting(String event, int level) {
        for (Frame frame : stack) {
            if (frame.name.equals("section")) {
                if (frame.level >= level) {
                    violation(
                            event,
                            "a section must be entirely contained in a section of a lower level but the enclosing "
                                    + "section is " + frame);
                }
                return;
            }
        }
    }

    private void end(String name) {
        end(name, 0);
    }

    private void doEnd(String name, int level) {
        Frame top = stack.peek();
        String event = name + "_(" + (level > 0 ? String.valueOf(level) : "") + ")";
        if (top != null && top.is(name, level)) {
            checkComplete(top, event);
            stack.pop();
            return;
        }
        if (top == null) {
            violation(event, "there is no open element to close");
            return;
        }
        violation(event, "expected " + endEvent(top));
        // resynchronize on the matching element if there is one, so that one mistake is not reported repeatedly
        boolean found = false;
        for (Frame frame : stack) {
            if (frame.is(name, level)) {
                found = true;
                break;
            }
        }
        if (found) {
            while (!stack.peek().is(name, level)) {
                stack.pop();
            }
            stack.pop();
        }
    }

    private void checkComplete(Frame frame, String event) {
        switch (frame.name) {
            case "list":
            case "numberedList":
                if (frame.events == 0) {
                    violation(event, "a list must contain at least one list item");
                }
                break;
            case "definitionListItem":
                if (frame.count("definedTerm") != 1 || frame.count("definition") != 1) {
                    violation(event, "a definitionListItem must contain exactly one definedTerm and one definition");
                }
                break;
            case "figure":
                if (frame.count("figureGraphics") == 0) {
                    violation(event, "a figure must contain figureGraphics");
                }
                break;
            case "table":
                if (frame.count("tableRows") == 0) {
                    violation(event, "a table must contain tableRows");
                }
                break;
            default:
                break;
        }
    }

    private void doLeaf(String name) {
        Frame parent = stack.peek();
        if (parent != null) {
            if (parent.events == 0) {
                if (parent.name.equals("list")) {
                    violation(name, "must be 'listItem' as the first element of a list");
                } else if (parent.name.equals("numberedList")) {
                    violation(name, "must be 'numberedListItem' as the first element of a numbered list");
                }
            }
            parent.events++;
            parent.counts.merge(name, 1, Integer::sum);
        }
    }

    private void checkLevel(String event, int level) {
        if (level < Sink.SECTION_LEVEL_1 || level > Sink.SECTION_LEVEL_6) {
            violation(
                    event + "(" + level + ")",
                    "the level must be between " + Sink.SECTION_LEVEL_1 + " and " + Sink.SECTION_LEVEL_6);
        }
    }

    private void checkJustification(int[] justification) {
        if (justification == null) {
            return;
        }
        for (int value : justification) {
            if (value != Sink.JUSTIFY_DEFAULT
                    && value != Sink.JUSTIFY_CENTER
                    && value != Sink.JUSTIFY_LEFT
                    && value != Sink.JUSTIFY_RIGHT) {
                violation(
                        "tableRows",
                        "the justification " + value + " must be one of JUSTIFY_DEFAULT (" + Sink.JUSTIFY_DEFAULT
                                + "), JUSTIFY_CENTER (" + Sink.JUSTIFY_CENTER + "), JUSTIFY_LEFT (" + Sink.JUSTIFY_LEFT
                                + ") or JUSTIFY_RIGHT (" + Sink.JUSTIFY_RIGHT + ")");
            }
        }
    }

    private void checkAnchorName(String name) {
        if (name != null && !DoxiaUtils.isValidId(name)) {
            violation(
                    "anchor",
                    "the name '" + name + "' is not a valid anchor name (a letter or underscore, followed by "
                            + "letters, digits, underscores, hyphens and periods)");
        }
    }

    private void checkLinkName(String name) {
        if (name == null) {
            return;
        }
        try {
            new URI(name);
        } catch (URISyntaxException e) {
            violation("link", "the name '" + name + "' is not a valid URI: " + e.getReason());
        }
    }

    private void checkSemantics(String event, SinkEventAttributes attributes) {
        if (attributes == null) {
            return;
        }
        Object value = attributes.getAttribute(SinkEventAttributes.SEMANTICS);
        if (value == null) {
            return;
        }
        String semantics = String.valueOf(value);
        if (!SEMANTICS_VALUES.contains(semantics)) {
            violation(
                    event,
                    "the value '" + value + "' of the attribute " + SinkEventAttributes.SEMANTICS + " must be one of "
                            + SEMANTICS_VALUES);
        } else if (FONT_SEMANTICS.contains(semantics) && isOpen("verbatim")) {
            violation(event, "the semantics '" + value + "' is not allowed within a verbatim block");
        }
    }

    private void checkVerbatimDecoration(SinkEventAttributes attributes) {
        if (attributes == null) {
            return;
        }
        Object value = attributes.getAttribute(SinkEventAttributes.DECORATION);
        if (value != null && !"source".equals(String.valueOf(value))) {
            violation(
                    "verbatim",
                    "the value '" + value + "' of the attribute " + SinkEventAttributes.DECORATION
                            + " must be 'source'");
        }
    }

    /** Returns the violation message for elements still open, or {@code null} if none are. */
    String unclosedViolation(String event) {
        if (stack.isEmpty()) {
            return null;
        }
        StringBuilder expected = new StringBuilder();
        for (Frame frame : stack) {
            if (expected.length() > 0) {
                expected.append(", ");
            }
            expected.append(endEvent(frame));
        }
        return format(event, "elements are still open, expected " + expected);
    }

    private boolean isOpen(String name) {
        for (Frame frame : stack) {
            if (frame.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private static String describe(Frame frame) {
        return frame == null ? "none (top level)" : "'" + frame + "'";
    }

    private static String endEvent(Frame frame) {
        return frame.name + "_(" + (frame.level > 0 ? String.valueOf(frame.level) : "") + ")";
    }

    private String openElements() {
        if (stack.isEmpty()) {
            return "none";
        }
        StringBuilder path = new StringBuilder();
        Iterator<Frame> outerToInner = stack.descendingIterator();
        while (outerToInner.hasNext()) {
            if (path.length() > 0) {
                path.append(" > ");
            }
            path.append(outerToInner.next());
        }
        return path.toString();
    }

    private String format(String event, String problem) {
        return getLocationLogPrefix() + "Sink event '" + event + "' violates the Sink contract: " + problem
                + ". Open elements: " + openElements();
    }

    private void violation(String event, String problem) {
        String message = format(event, problem);
        if (mode == Mode.FAIL_FAST) {
            throw new SinkContractViolationException(message);
        }
        if (pending == null) {
            pending = message;
        }
    }

    private void commit() {
        if (pending != null) {
            violations.add(pending);
            pending = null;
        }
    }

    // -- events

    @Override
    public void head(SinkEventAttributes attributes) {
        start("head");
        super.head(attributes);
    }

    @Override
    public void head_() {
        end("head");
        super.head_();
    }

    @Override
    public void title(SinkEventAttributes attributes) {
        start("title");
        super.title(attributes);
    }

    @Override
    public void title_() {
        end("title");
        super.title_();
    }

    @Override
    public void author(SinkEventAttributes attributes) {
        start("author");
        super.author(attributes);
    }

    @Override
    public void author_() {
        end("author");
        super.author_();
    }

    @Override
    public void date(SinkEventAttributes attributes) {
        start("date");
        super.date(attributes);
    }

    @Override
    public void date_() {
        end("date");
        super.date_();
    }

    @Override
    public void body(SinkEventAttributes attributes) {
        start("body");
        super.body(attributes);
    }

    @Override
    public void body_() {
        end("body");
        super.body_();
    }

    @Override
    public void article(SinkEventAttributes attributes) {
        start("article");
        super.article(attributes);
    }

    @Override
    public void article_() {
        end("article");
        super.article_();
    }

    @Override
    public void navigation(SinkEventAttributes attributes) {
        start("navigation");
        super.navigation(attributes);
    }

    @Override
    public void navigation_() {
        end("navigation");
        super.navigation_();
    }

    @Override
    public void sidebar(SinkEventAttributes attributes) {
        start("sidebar");
        super.sidebar(attributes);
    }

    @Override
    public void sidebar_() {
        end("sidebar");
        super.sidebar_();
    }

    @Override
    public void header(SinkEventAttributes attributes) {
        start("header");
        super.header(attributes);
    }

    @Override
    public void header_() {
        end("header");
        super.header_();
    }

    @Override
    public void content(SinkEventAttributes attributes) {
        start("content");
        super.content(attributes);
    }

    @Override
    public void content_() {
        end("content");
        super.content_();
    }

    @Override
    public void footer(SinkEventAttributes attributes) {
        start("footer");
        super.footer(attributes);
    }

    @Override
    public void footer_() {
        end("footer");
        super.footer_();
    }

    @Override
    public void list(SinkEventAttributes attributes) {
        start("list");
        super.list(attributes);
    }

    @Override
    public void list_() {
        end("list");
        super.list_();
    }

    @Override
    public void listItem(SinkEventAttributes attributes) {
        start("listItem");
        super.listItem(attributes);
    }

    @Override
    public void listItem_() {
        end("listItem");
        super.listItem_();
    }

    @Override
    public void numberedListItem(SinkEventAttributes attributes) {
        start("numberedListItem");
        super.numberedListItem(attributes);
    }

    @Override
    public void numberedListItem_() {
        end("numberedListItem");
        super.numberedListItem_();
    }

    @Override
    public void definitionList(SinkEventAttributes attributes) {
        start("definitionList");
        super.definitionList(attributes);
    }

    @Override
    public void definitionList_() {
        end("definitionList");
        super.definitionList_();
    }

    @Override
    public void definitionListItem(SinkEventAttributes attributes) {
        start("definitionListItem");
        super.definitionListItem(attributes);
    }

    @Override
    public void definitionListItem_() {
        end("definitionListItem");
        super.definitionListItem_();
    }

    @Override
    public void definition(SinkEventAttributes attributes) {
        start("definition");
        super.definition(attributes);
    }

    @Override
    public void definition_() {
        end("definition");
        super.definition_();
    }

    @Override
    public void definedTerm(SinkEventAttributes attributes) {
        start("definedTerm");
        super.definedTerm(attributes);
    }

    @Override
    public void definedTerm_() {
        end("definedTerm");
        super.definedTerm_();
    }

    @Override
    public void figure(SinkEventAttributes attributes) {
        start("figure");
        super.figure(attributes);
    }

    @Override
    public void figure_() {
        end("figure");
        super.figure_();
    }

    @Override
    public void figureCaption(SinkEventAttributes attributes) {
        start("figureCaption");
        super.figureCaption(attributes);
    }

    @Override
    public void figureCaption_() {
        end("figureCaption");
        super.figureCaption_();
    }

    @Override
    public void table(SinkEventAttributes attributes) {
        start("table");
        super.table(attributes);
    }

    @Override
    public void table_() {
        end("table");
        super.table_();
    }

    @Override
    public void tableRow(SinkEventAttributes attributes) {
        start("tableRow");
        super.tableRow(attributes);
    }

    @Override
    public void tableRow_() {
        end("tableRow");
        super.tableRow_();
    }

    @Override
    public void tableCell(SinkEventAttributes attributes) {
        start("tableCell");
        super.tableCell(attributes);
    }

    @Override
    public void tableCell_() {
        end("tableCell");
        super.tableCell_();
    }

    @Override
    public void tableHeaderCell(SinkEventAttributes attributes) {
        start("tableHeaderCell");
        super.tableHeaderCell(attributes);
    }

    @Override
    public void tableHeaderCell_() {
        end("tableHeaderCell");
        super.tableHeaderCell_();
    }

    @Override
    public void tableCaption(SinkEventAttributes attributes) {
        start("tableCaption");
        super.tableCaption(attributes);
    }

    @Override
    public void tableCaption_() {
        end("tableCaption");
        super.tableCaption_();
    }

    @Override
    public void paragraph(SinkEventAttributes attributes) {
        start("paragraph");
        super.paragraph(attributes);
    }

    @Override
    public void paragraph_() {
        end("paragraph");
        super.paragraph_();
    }

    @Override
    public void address(SinkEventAttributes attributes) {
        start("address");
        super.address(attributes);
    }

    @Override
    public void address_() {
        end("address");
        super.address_();
    }

    @Override
    public void blockquote(SinkEventAttributes attributes) {
        start("blockquote");
        super.blockquote(attributes);
    }

    @Override
    public void blockquote_() {
        end("blockquote");
        super.blockquote_();
    }

    @Override
    public void division(SinkEventAttributes attributes) {
        start("division");
        super.division(attributes);
    }

    @Override
    public void division_() {
        end("division");
        super.division_();
    }

    @Override
    public void section(int level, SinkEventAttributes attributes) {
        checkLevel("section", level);
        start("section", level);
        super.section(level, attributes);
    }

    @Override
    public void section_(int level) {
        checkLevel("section_", level);
        end("section", level);
        super.section_(level);
    }

    @Override
    public void sectionTitle(int level, SinkEventAttributes attributes) {
        checkLevel("sectionTitle", level);
        start("sectionTitle", level);
        super.sectionTitle(level, attributes);
    }

    @Override
    public void sectionTitle_(int level) {
        checkLevel("sectionTitle_", level);
        end("sectionTitle", level);
        super.sectionTitle_(level);
    }

    @Override
    public void numberedList(int numbering, SinkEventAttributes attributes) {
        start("numberedList");
        super.numberedList(numbering, attributes);
    }

    @Override
    public void numberedList_() {
        end("numberedList");
        super.numberedList_();
    }

    @Override
    public void tableRows(int[] justification, boolean grid) {
        checkJustification(justification);
        start("tableRows");
        super.tableRows(justification, grid);
    }

    @Override
    public void tableRows_() {
        end("tableRows");
        super.tableRows_();
    }

    @Override
    public void data(String value, SinkEventAttributes attributes) {
        start("data");
        super.data(value, attributes);
    }

    @Override
    public void data_() {
        end("data");
        super.data_();
    }

    @Override
    public void time(String datetime, SinkEventAttributes attributes) {
        start("time");
        super.time(datetime, attributes);
    }

    @Override
    public void time_() {
        end("time");
        super.time_();
    }

    @Override
    public void verbatim(SinkEventAttributes attributes) {
        checkVerbatimDecoration(attributes);
        start("verbatim");
        super.verbatim(attributes);
    }

    @Override
    public void verbatim_() {
        end("verbatim");
        super.verbatim_();
    }

    @Override
    public void anchor(String name, SinkEventAttributes attributes) {
        checkAnchorName(name);
        start("anchor");
        super.anchor(name, attributes);
    }

    @Override
    public void anchor_() {
        end("anchor");
        super.anchor_();
    }

    @Override
    public void link(String name, SinkEventAttributes attributes) {
        checkLinkName(name);
        start("link");
        super.link(name, attributes);
    }

    @Override
    public void link_() {
        end("link");
        super.link_();
    }

    @Override
    public void inline(SinkEventAttributes attributes) {
        checkSemantics("inline", attributes);
        start("inline");
        super.inline(attributes);
    }

    @Override
    public void inline_() {
        end("inline");
        super.inline_();
    }

    @Override
    public void italic() {
        start("italic");
        super.italic();
    }

    @Override
    public void italic_() {
        end("italic");
        super.italic_();
    }

    @Override
    public void bold() {
        start("bold");
        super.bold();
    }

    @Override
    public void bold_() {
        end("bold");
        super.bold_();
    }

    @Override
    public void monospaced() {
        start("monospaced");
        super.monospaced();
    }

    @Override
    public void monospaced_() {
        end("monospaced");
        super.monospaced_();
    }

    @Override
    public void horizontalRule(SinkEventAttributes attributes) {
        leaf("horizontalRule");
        super.horizontalRule(attributes);
    }

    @Override
    public void pageBreak() {
        leaf("pageBreak");
        super.pageBreak();
    }

    @Override
    public void lineBreak(SinkEventAttributes attributes) {
        leaf("lineBreak");
        super.lineBreak(attributes);
    }

    @Override
    public void lineBreakOpportunity(SinkEventAttributes attributes) {
        leaf("lineBreakOpportunity");
        super.lineBreakOpportunity(attributes);
    }

    @Override
    public void nonBreakingSpace() {
        leaf("nonBreakingSpace");
        super.nonBreakingSpace();
    }

    @Override
    public void text(String text, SinkEventAttributes attributes) {
        checkSemantics("text", attributes);
        leaf("text");
        super.text(text, attributes);
    }

    @Override
    public void rawText(String text) {
        leaf("rawText");
        super.rawText(text);
    }

    @Override
    public void figureGraphics(String src, SinkEventAttributes attributes) {
        leaf("figureGraphics");
        super.figureGraphics(src, attributes);
    }

    @Override
    public void unknown(String name, Object[] requiredParams, SinkEventAttributes attributes) {
        leaf("unknown");
        super.unknown(name, requiredParams, attributes);
    }

    /**
     * Checks that no element is left open and resets the state of the validator for the next document.
     * This is needed because nothing closes the wrapper a parser creates. In fail-fast mode an unclosed element
     * is reported with a {@link SinkContractViolationException}, in collect mode it is recorded.
     * Recorded violations are kept.
     */
    public void finish() {
        finish("finish");
    }

    private void finish(String event) {
        String unclosed = unclosedViolation(event);
        stack.clear();
        headSeen = false;
        bodySeen = false;
        pending = null;
        if (unclosed != null) {
            if (mode == Mode.FAIL_FAST) {
                throw new SinkContractViolationException(unclosed);
            }
            violations.add(unclosed);
        }
    }

    /**
     * Calls {@link #finish()} and closes the wrapped sink, also if finishing fails.
     */
    @Override
    public void close() {
        try {
            finish("close");
        } finally {
            super.close();
        }
    }
}
