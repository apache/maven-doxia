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
package org.apache.maven.doxia.module.markdown;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.impl.Xhtml5BaseSink;
import org.apache.maven.doxia.util.DoxiaStringUtils;
import org.apache.maven.doxia.util.HtmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Markdown generator implementation.
 * <br>
 * <b>Note</b>: The encoding used is UTF-8.
 * Extends the Xhtml5 sink as in some context HTML needs to be emitted.
 */
public class MarkdownSink extends Xhtml5BaseSink implements MarkdownMarkup {
    private static final Logger LOGGER = LoggerFactory.getLogger(MarkdownSink.class);

    // ----------------------------------------------------------------------
    // Instance fields
    // ----------------------------------------------------------------------

    /** author. */
    private Collection<String> authors;

    /** title. */
    private String title;

    /** date. */
    private String date;

    /** linkName. */
    private String linkName;

    /** tableHeaderCellFlag, set to {@code true} for table rows containing at least one table header cell */
    private boolean tableHeaderCellFlag;

    /** number of cells in a table. */
    private int cellCount;

    /** justification of table cells per column. */
    private List<Integer> cellJustif;

    /** is header row */
    private boolean isFirstTableRow;

    /** The inner decorated writer to buffer the text of contexts requiring buffering. Writing to this and {@code bufferingWriter} has the same effect. */
    private final BufferingStackWriter bufferingStackWriter;

    /** The outer decorated writer taking care of remembering the last two written lines. Writing to this and {@code writer} has the same effect. */
    private final LastTwoLinesAwareWriter lineAwareWriter;

    private static final String USE_XHTML_SINK = "XhtmlSink";

    /** Keep track of end markup for inline events. Special value  {@link #USE_XHTML_SINK} is used to indicate usage of the Xhtml5BaseSink.inline_()*/
    protected Queue<Queue<String>> inlineStack;

    /** The context of the surrounding elements as stack (LIFO) */
    protected Queue<ElementContext> elementContextStack;

    private String figureSrc;

    /** flag if the current verbatim block added a HTML context or not */
    private boolean isVerbatimHtmlContext;

    @FunctionalInterface
    interface TextEscapeFunction {
        String escape(ElementContext context, LastTwoLinesAwareWriter writer, String text);
    }
    /** Most important contextual metadata (of elements). This contains information about necessary escaping rules, potential prefixes and newlines */
    enum ElementContext {
        ROOT_WITH_BUFFERING(
                Type.GENERIC_CONTAINER,
                true,
                ElementContext::escapeMarkdown,
                true), // only needs buffering until head()_ is called to make sure to emit metadata first
        ROOT_WITHOUT_BUFFERING(
                Type.GENERIC_CONTAINER,
                true,
                null,
                false), // used after head()_/body() to prevent unnecessary buffering
        HEAD(Type.GENERIC_CONTAINER, false, null, true),
        BODY(Type.GENERIC_CONTAINER, true, ElementContext::escapeMarkdown),
        // only the elements, which affect rendering of children and are different from BODY or HEAD are listed here
        FIGURE(Type.INLINE, false, ElementContext::escapeMarkdown, true),
        HEADING(Type.LEAF_BLOCK, false, ElementContext::escapeMarkdown),
        CODE_BLOCK(Type.LEAF_BLOCK, false, null),
        CODE_SPAN(Type.INLINE, false, null, true),
        TABLE(Type.CONTAINER_BLOCK, false, null, false, "", true),
        TABLE_CAPTION(Type.INLINE, false, ElementContext::escapeMarkdown),
        TABLE_ROW(Type.INLINE, false, null, true), // special handling of newlines
        TABLE_CELL(
                Type.INLINE,
                false,
                ElementContext::escapeForTableCell,
                false), // special type, as allows containing inlines, but not starting on a separate line
        // same parameters as BODY but paragraphs inside list items are handled differently
        LIST_ITEM(Type.CONTAINER_BLOCK, false, ElementContext::escapeMarkdown, false, INDENT),
        BLOCKQUOTE(Type.CONTAINER_BLOCK, false, ElementContext::escapeMarkdown, false, BLOCKQUOTE_START_MARKUP),
        HTML_BLOCK(Type.GENERIC_CONTAINER, true, ElementContext::escapeHtml, false, "", false);

        /**
         * @see <a href="https://spec.commonmark.org/0.30/#blocks-and-inlines">CommonMark, 3 Blocks and inlines</a>
         */
        enum Type {
            /**
             * Container with no special meaning for (nested) child element contexts
             */
            GENERIC_CONTAINER,
            /**
             * Is supposed to start on a new line, and must have a prefix (for nested blocks)
             */
            CONTAINER_BLOCK,
            /**
             * Is supposed to start on a new line, must not contain any other block element context (neither leaf nor container)
             */
            LEAF_BLOCK,
            /**
             * Are not allowed to contain any other element context (i.e. leaf contexts), except for some other inlines (depends on the actual type)
             */
            INLINE
        }
        /**
         * {@code true} if block element, otherwise {@code false} for inline elements
         */
        final Type type;

        /**
         * The function to call to escape the given text. The function is supposed to return the escaped text or return just the given text if no escaping is necessary in this context
         */
        final TextEscapeFunction escapeFunction;

        /**
         * if {@code true} requires buffering any text appearing inside this context
         */
        final boolean requiresBuffering;

        /**
         * prefix to be used for each line of (nested) block elements inside the current container context (only not empty for {@link #type} being {@link Type#CONTAINER_BLOCK})
         */
        final String prefix;

        /**
         * Only relevant for block element, if set to {@code true} the element requires to be surrounded by blank lines.
         */
        final boolean requiresSurroundingByBlankLines;

        /**
         * If markup linebreaks (i.e. insignificant linebreaks in the source) are allowed in this context.
         * This is relevant for markdown as in some contexts (e.g. list items) linebreaks are always significant (while for HTML they wouldn't be)
         */
        final boolean allowsMarkupLinebreaks;

        ElementContext(Type type, boolean allowsMarkupLinebreaks, TextEscapeFunction escapeFunction) {
            this(type, allowsMarkupLinebreaks, escapeFunction, false);
        }

        ElementContext(
                Type type,
                boolean allowsMarkupLinebreaks,
                TextEscapeFunction escapeFunction,
                boolean requiresBuffering) {
            this(type, allowsMarkupLinebreaks, escapeFunction, requiresBuffering, "");
        }

        ElementContext(
                Type type,
                boolean allowsMarkupLinebreaks,
                TextEscapeFunction escapeFunction,
                boolean requiresBuffering,
                String prefix) {
            this(type, allowsMarkupLinebreaks, escapeFunction, requiresBuffering, prefix, false);
        }

        ElementContext(
                Type type,
                boolean allowsMarkupLinebreaks,
                TextEscapeFunction escapeFunction,
                boolean requiresBuffering,
                String prefix,
                boolean requiresSurroundingByBlankLines) {
            this.type = type;
            this.allowsMarkupLinebreaks = allowsMarkupLinebreaks;
            this.escapeFunction = escapeFunction;
            this.requiresBuffering = requiresBuffering;
            if (type != Type.CONTAINER_BLOCK && prefix.length() != 0) {
                throw new IllegalArgumentException("Only container blocks may define a prefix (for nesting)");
            }
            this.prefix = prefix;
            this.requiresSurroundingByBlankLines = requiresSurroundingByBlankLines;
        }

        /**
         * Must be called for each inline text to be emitted directly within this context (not relevant for nested context)
         * @param text
         * @return the escaped text (may be same as {@code text} when no escaping is necessary)
         */
        String escape(LastTwoLinesAwareWriter writer, String text) {
            // is escaping necessary at all?
            if (escapeFunction == null) {
                return text;
            } else {
                return escapeFunction.escape(this, writer, text);
            }
        }

        /**
         *
         * @return {@code true} for all block types, {@code false} otherwise
         */
        boolean isBlock() {
            return type == Type.CONTAINER_BLOCK || type == Type.LEAF_BLOCK;
        }

        /**
         *
         * @return {@code true} if only HTML is allowed in this context
         */
        boolean isHtml() {
            return this.equals(HTML_BLOCK);
        }
        /**
         *
         * @return {@code true} for all containers (allowing block elements as children), {@code false} otherwise
         */
        boolean isContainer() {
            return type == Type.CONTAINER_BLOCK || type == Type.GENERIC_CONTAINER;
        }

        public boolean isAllowsMarkupLinebreaks() {
            return allowsMarkupLinebreaks;
        }

        /**
         * First use XML escaping (leveraging the predefined entities, for browsers)
         * afterwards escape special characters in a text with a leading backslash (for markdown parsers)
         *
         * <pre>
         * \, `, *, _, {, }, [, ], (, ), #, +, -, ., !
         * </pre>
         *
         * @param text the string to escape, may be null
         * @return the text escaped, "" if null String input
         * @see <a href="https://daringfireball.net/projects/markdown/syntax#backslash">Backslash Escapes</a>
         */
        private String escapeMarkdown(LastTwoLinesAwareWriter writer, String text) {
            if (text == null) {
                return "";
            }
            text = escapeHtml(writer, text); // assume UTF-8 output, i.e. only use the mandatory XML entities
            int length = text.length();
            StringBuilder buffer = new StringBuilder(length);

            for (int i = 0; i < length; ++i) {
                char c = text.charAt(i);
                switch (c) {
                    case '\\':
                    case '_':
                    case '`':
                    case '[':
                    case ']':
                    case '(':
                    case ')':
                    case '!':
                        // always escape the previous characters as potentially everywhere relevant
                        buffer.append(escapeMarkdown(c));
                        break;
                    case '*':
                    case '+':
                    case '-':
                        // only relevant for unordered lists or horizontal rules
                        if (isInBlankLine(buffer, writer)) {
                            buffer.append(escapeMarkdown(c));
                        } else {
                            buffer.append(c);
                        }
                        break;
                    case '=':
                    case '#':
                        if (this == HEADING || isInBlankLine(buffer, writer)) {
                            buffer.append(escapeMarkdown(c));
                        } else {
                            buffer.append(c);
                        }
                        break;
                    case '.':
                        if (isAfterDigit(buffer, writer)) {
                            buffer.append(escapeMarkdown(c));
                        } else {
                            buffer.append(c);
                        }
                        break;
                    default:
                        buffer.append(c);
                }
            }
            return buffer.toString();
        }

        private static boolean isAfterDigit(StringBuilder buffer, LastTwoLinesAwareWriter writer) {
            if (buffer.length() > 0) {
                return Character.isDigit(buffer.charAt(buffer.length() - 1));
            } else {
                return writer.isAfterDigit();
            }
        }

        private static boolean isInBlankLine(StringBuilder buffer, LastTwoLinesAwareWriter writer) {
            if (DoxiaStringUtils.isBlank(buffer.toString())) {
                return writer.isInBlankLine();
            }
            return false;
        }

        private static String escapeMarkdown(char c) {
            return "\\" + c;
        }

        private String escapeHtml(LastTwoLinesAwareWriter writer, String text) {
            return HtmlTools.escapeHTML(text, true);
        }

        /**
         * Escapes the pipe character according to <a href="https://github.github.com/gfm/#tables-extension-">GFM Table Extension</a> in addition
         * to the regular markdown escaping.
         * @param text
         * @return the escaped text
         * @see {@link #escapeMarkdown(String)
         */
        private String escapeForTableCell(LastTwoLinesAwareWriter writer, String text) {
            return escapeMarkdown(writer, text).replace("|", "\\|");
        }
    }
    // ----------------------------------------------------------------------
    // Public protected methods
    // ----------------------------------------------------------------------

    protected static MarkdownSink newInstance(Writer writer) {
        BufferingStackWriter bufferingStackWriter = new BufferingStackWriter(writer);
        LastTwoLinesAwareWriter lineAwareWriter = new LastTwoLinesAwareWriter(bufferingStackWriter);
        return new MarkdownSink(lineAwareWriter, bufferingStackWriter);
    }

    /**
     * Constructor, initialize the Writer and the variables.
     *
     * @param writer not null writer to write the result. <b>Should</b> be an UTF-8 Writer.
     */
    private MarkdownSink(LastTwoLinesAwareWriter lineAwareWriter, BufferingStackWriter bufferingStackWriter) {
        super(lineAwareWriter);
        this.lineAwareWriter = lineAwareWriter;
        this.bufferingStackWriter = bufferingStackWriter;
        initInternal();
    }

    private void initInternal() {
        this.authors = new LinkedList<>();
        this.title = null;
        this.date = null;
        this.linkName = null;
        this.tableHeaderCellFlag = false;
        this.cellCount = 0;
        this.cellJustif = null;
        this.elementContextStack = Collections.asLifoQueue(new LinkedList<>());
        this.inlineStack = Collections.asLifoQueue(new LinkedList<>());
        startContext(ElementContext.ROOT_WITH_BUFFERING);
    }

    private void endContext(ElementContext expectedContext) {
        ElementContext removedContext = elementContextStack.remove();
        if (removedContext != expectedContext) {
            throw new IllegalStateException("Unexpected context " + removedContext + ", expected " + expectedContext);
        }
        if (removedContext.isBlock()) {
            endBlock(removedContext.requiresSurroundingByBlankLines
                    || (isInListItem() && (removedContext == ElementContext.BLOCKQUOTE)
                            || (removedContext == ElementContext.CODE_BLOCK)));
        }
        if (removedContext.requiresBuffering) {
            // remove buffer from stack (assume it has been evaluated already)
            bufferingStackWriter.removeBuffer();
        }
    }

    private void startContext(ElementContext newContext) {
        if (newContext.requiresBuffering) {
            bufferingStackWriter.addBuffer();
        }
        if (newContext.isBlock()) {
            // every block element within a list item must be surrounded by blank lines
            startBlock(newContext.requiresSurroundingByBlankLines
                    || (isInListItem() && (newContext == ElementContext.BLOCKQUOTE)
                            || (newContext == ElementContext.CODE_BLOCK)));
        }
        elementContextStack.add(newContext);
    }

    private String toogleToRootContextWithoutBuffering(boolean dumpBuffer) {
        final String buffer;
        if (elementContextStack.element() == ElementContext.ROOT_WITH_BUFFERING) {
            buffer = bufferingStackWriter.getCurrentBuffer().toString();
            endContext(ElementContext.ROOT_WITH_BUFFERING);
            if (dumpBuffer) {
                write(buffer);
            }
            startContext(ElementContext.ROOT_WITHOUT_BUFFERING);
        } else if (elementContextStack.element() != ElementContext.ROOT_WITHOUT_BUFFERING) {
            throw new IllegalStateException("Unexpected context " + elementContextStack.element()
                    + ", expected ROOT_WITH_BUFFERING or ROOT_WITHOUT_BUFFERING");
        } else {
            buffer = "";
        }
        return buffer;
    }
    /**
     * Ensures that the {@link #writer} is currently at the beginning of a new line.
     * Optionally writes a line separator to ensure that.
     */
    private void ensureBeginningOfLine() {
        // make sure that we are at the start of a line without adding unnecessary blank lines
        if (!lineAwareWriter.isWriterAtStartOfNewLine()) {
            write(EOL);
        }
    }

    /**
     * Ensures that the {@link #writer} is preceded by a blank line.
     * Optionally writes a blank line or just line delimiter to ensure that.
     */
    private void ensureBlankLine() {
        // prevent duplicate blank lines
        if (!lineAwareWriter.isWriterAfterBlankLine()) {
            if (lineAwareWriter.isWriterAtStartOfNewLine()) {
                write(EOL);
            } else {
                write(BLANK_LINE);
            }
        }
    }

    private void startBlock(boolean requireBlankLine) {
        if (requireBlankLine) {
            ensureBlankLine();
        } else {
            ensureBeginningOfLine();
        }
        write(getLinePrefix());
    }

    private void endBlock(boolean requireBlankLine) {
        if (requireBlankLine) {
            ensureBlankLine();
        } else {
            ensureBeginningOfLine();
        }
    }

    /**
     * @return the prefix to be used for each line in the current context (i.e. the prefix of the current container context and all its ancestors), may be empty
     */
    private String getLinePrefix() {
        StringBuilder prefix = new StringBuilder();
        elementContextStack.stream().filter(c -> c.prefix.length() > 0).forEachOrdered(c -> prefix.insert(0, c.prefix));
        return prefix.toString();
    }

    private boolean isInListItem() {
        return elementContextStack.stream()
                .filter(c -> c == ElementContext.LIST_ITEM)
                .findFirst()
                .isPresent();
    }

    @Override
    protected void init() {
        super.init();
        initInternal();
    }

    @Override
    public void head(SinkEventAttributes attributes) {
        startContext(ElementContext.HEAD);
    }

    @Override
    public void head_() {
        endContext(ElementContext.HEAD);
        String priorHeadBuffer = toogleToRootContextWithoutBuffering(false);
        // only write head block if really necessary
        if (title == null && authors.isEmpty() && date == null) {
            return;
        }
        write(METADATA_MARKUP + EOL);
        if (title != null) {
            write("title: " + title + EOL);
        }
        if (!authors.isEmpty()) {
            write("author: " + EOL);
            for (String author : authors) {
                write("  - " + author + EOL);
            }
        }
        if (date != null) {
            write("date: " + date + EOL);
        }
        write(METADATA_MARKUP + BLANK_LINE);
        write(priorHeadBuffer);
    }

    @Override
    public void body(SinkEventAttributes attributes) {
        toogleToRootContextWithoutBuffering(true);
        startContext(ElementContext.BODY);
    }

    @Override
    public void body_() {
        endContext(ElementContext.BODY);
    }

    @Override
    public void title_() {
        String buffer = bufferingStackWriter.getAndClearCurrentBuffer();
        if (!buffer.isEmpty()) {
            this.title = buffer;
        }
    }

    @Override
    public void author_() {
        String buffer = bufferingStackWriter.getAndClearCurrentBuffer();
        if (!buffer.isEmpty()) {
            authors.add(buffer);
        }
    }

    @Override
    public void date_() {
        String buffer = bufferingStackWriter.getAndClearCurrentBuffer();
        if (!buffer.isEmpty()) {
            date = buffer;
        }
    }

    @Override
    public void section(int level, SinkEventAttributes attributes) {
        // not supported as often used around sectionTitles which would otherwise no longer be emitted as markdown
    }

    @Override
    public void section_(int level) {
        // not supported as often used around sectionTitles which would otherwise no longer be emitted as markdown
    }

    @Override
    public void header(SinkEventAttributes attributes) {
        // not supported as often used around sectionTitles which would otherwise no longer be emitted as markdown
    }

    @Override
    public void header_() {
        // not supported as often used around sectionTitles which would otherwise no longer be emitted as markdown
    }

    @Override
    public void sectionTitle(int level, SinkEventAttributes attributes) {
        startContext(ElementContext.HEADING);
        if (level > 0) {
            write(DoxiaStringUtils.repeat(SECTION_TITLE_START_MARKUP, level) + SPACE);
        }
    }

    @Override
    public void sectionTitle_(int level) {
        endContext(ElementContext.HEADING);
        if (level > 0) {
            ensureBlankLine(); // always end headings with blank line to increase compatibility with arbitrary MD
            // editors
        }
    }

    @Override
    public void list(SinkEventAttributes attributes) {
        if (elementContextStack.element().isHtml()) {
            super.list(attributes);
        }
    }

    @Override
    public void list_() {
        ensureBeginningOfLine();
    }

    @Override
    public void listItem(SinkEventAttributes attributes) {
        startContext(ElementContext.LIST_ITEM);
        write(LIST_UNORDERED_ITEM_START_MARKUP);
    }

    @Override
    public void listItem_() {
        endContext(ElementContext.LIST_ITEM);
    }

    @Override
    public void numberedList(int numbering, SinkEventAttributes attributes) {
        // markdown only supports decimal numbering
        if (numbering != NUMBERING_DECIMAL) {
            LOGGER.warn(
                    "{}Markdown only supports numbered item with decimal style ({}) but requested was style {}, falling back to decimal style",
                    getLocationLogPrefix(),
                    NUMBERING_DECIMAL,
                    numbering);
        }
    }

    @Override
    public void numberedList_() {
        ensureBeginningOfLine();
    }

    @Override
    public void numberedListItem(SinkEventAttributes attributes) {
        startContext(ElementContext.LIST_ITEM);
        write(LIST_ORDERED_ITEM_START_MARKUP);
    }

    @Override
    public void numberedListItem_() {
        listItem_(); // identical for both numbered and not numbered list item
    }

    @Override
    public void definitionList(SinkEventAttributes attributes) {
        LOGGER.warn(
                "{}Definition list not natively supported in Markdown, rendering HTML instead", getLocationLogPrefix());
        startContext(ElementContext.HTML_BLOCK);
        write("<dl>" + EOL);
    }

    @Override
    public void definitionList_() {
        write("</dl>");
        endContext(ElementContext.HTML_BLOCK);
    }

    @Override
    public void definedTerm(SinkEventAttributes attributes) {
        write("<dt>");
    }

    @Override
    public void definedTerm_() {
        write("</dt>" + EOL);
    }

    @Override
    public void definition(SinkEventAttributes attributes) {
        write("<dd>");
    }

    @Override
    public void definition_() {
        write("</dd>" + EOL);
    }

    @Override
    public void pageBreak() {
        LOGGER.warn("{}Ignoring unsupported page break in Markdown", getLocationLogPrefix());
    }

    @Override
    public void paragraph(SinkEventAttributes attributes) {
        // ignore paragraphs outside container contexts
        if (elementContextStack.element().isContainer()) {
            ensureBlankLine();
            write(getLinePrefix());
        } else {
            LOGGER.warn(
                    "{}Paragraphs outside of container contexts are not supported in Markdown, ignoring paragraph event in context {}",
                    getLocationLogPrefix(),
                    elementContextStack.element());
        }
    }

    @Override
    public void paragraph_() {
        // ignore paragraphs outside container contexts
        if (elementContextStack.element().isContainer()) {
            ensureBlankLine();
        }
    }

    @Override
    public void verbatim(SinkEventAttributes attributes) {
        if (!elementContextStack.element().isContainer()) {
            // markdown doesn't allow block elements but one can instead rely on html blocks for this
            startContext(ElementContext.HTML_BLOCK);
            isVerbatimHtmlContext = true;
        } else {
            isVerbatimHtmlContext = false;
        }

        if (elementContextStack.element().isHtml()) {
            super.verbatim(attributes);
        } else {
            // if no source attribute, then don't emit an info string
            startContext(ElementContext.CODE_BLOCK);
            write(VERBATIM_START_MARKUP);
            if (attributes != null && attributes.containsAttributes(SinkEventAttributeSet.SOURCE)) {
                write("unknown"); // unknown language
            }
            write(EOL);
            write(getLinePrefix());
        }
    }

    @Override
    public void verbatim_() {
        if (elementContextStack.element().isHtml()) {
            super.verbatim_();
            if (isVerbatimHtmlContext) {
                endContext(ElementContext.HTML_BLOCK);
                isVerbatimHtmlContext = false;
            }
        } else {
            ensureBeginningOfLine();
            write(getLinePrefix());
            write(VERBATIM_END_MARKUP + BLANK_LINE);
            endContext(ElementContext.CODE_BLOCK);
        }
    }

    @Override
    public void blockquote(SinkEventAttributes attributes) {
        if (elementContextStack.element().isHtml()) {
            super.blockquote(attributes);
        } else {
            startContext(ElementContext.BLOCKQUOTE);
            write(BLOCKQUOTE_START_MARKUP);
        }
    }

    @Override
    public void blockquote_() {
        if (elementContextStack.element().isHtml()) {
            super.blockquote_();
        } else {
            endContext(ElementContext.BLOCKQUOTE);
        }
    }

    @Override
    public void horizontalRule(SinkEventAttributes attributes) {
        ensureBeginningOfLine();
        write(HORIZONTAL_RULE_MARKUP + BLANK_LINE);
        write(getLinePrefix());
    }

    @Override
    public void table(SinkEventAttributes attributes) {
        if (elementContextStack.element().isHtml()) {
            super.table(attributes);
        } else {
            startContext(ElementContext.TABLE);
        }
    }

    @Override
    public void table_() {
        if (elementContextStack.element().isHtml()) {
            super.table_();
        } else {
            endContext(ElementContext.TABLE);
        }
    }

    @Override
    public void tableRows(int[] justification, boolean grid) {
        if (elementContextStack.element().isHtml()) {
            super.tableRows(justification, grid);
        } else {
            if (justification != null) {
                cellJustif = Arrays.stream(justification).boxed().collect(Collectors.toCollection(ArrayList::new));
            } else {
                cellJustif = new ArrayList<>();
            }
            // grid flag is not supported
            isFirstTableRow = true;
        }
    }

    @Override
    public void tableRows_() {
        if (elementContextStack.element().isHtml()) {
            super.tableRows_();
        } else {
            cellJustif = null;
        }
    }

    @Override
    public void tableRow(SinkEventAttributes attributes) {
        if (elementContextStack.element().isHtml()) {
            super.tableRow(attributes);
        } else {
            startContext(ElementContext.TABLE_ROW);
            cellCount = 0;
        }
    }

    @Override
    public void tableRow_() {
        if (elementContextStack.element().isHtml()) {
            super.tableRow_();
        } else {
            String buffer = bufferingStackWriter.getAndClearCurrentBuffer();
            endContext(ElementContext.TABLE_ROW);
            if (isFirstTableRow && !tableHeaderCellFlag) {
                // emit empty table header as this is mandatory for GFM table extension
                // (https://stackoverflow.com/a/17543474/5155923)
                writeEmptyTableHeader();
                writeTableDelimiterRow();
                tableHeaderCellFlag = false;
                isFirstTableRow = false;
                // afterwards emit the first row
            }
            write(TABLE_ROW_PREFIX);
            write(buffer);
            write(EOL);
            if (isFirstTableRow) {
                // emit delimiter row
                writeTableDelimiterRow();
                isFirstTableRow = false;
            }
            // only reset cell count if this is the last row
            cellCount = 0;
        }
    }

    private void writeEmptyTableHeader() {
        write(TABLE_ROW_PREFIX);
        for (int i = 0; i < cellCount; i++) {
            write(DoxiaStringUtils.repeat(String.valueOf(SPACE), 3) + TABLE_CELL_SEPARATOR_MARKUP);
        }
        write(EOL);
        write(getLinePrefix());
    }

    /** Emit the delimiter row which determines the alignment */
    private void writeTableDelimiterRow() {
        write(TABLE_ROW_PREFIX);
        int justification = Sink.JUSTIFY_DEFAULT;
        for (int i = 0; i < cellCount; i++) {
            // keep previous column's alignment in case too few are specified
            if (cellJustif != null && cellJustif.size() > i) {
                justification = cellJustif.get(i);
            }
            switch (justification) {
                case Sink.JUSTIFY_RIGHT:
                    write(TABLE_COL_RIGHT_ALIGNED_MARKUP);
                    break;
                case Sink.JUSTIFY_CENTER:
                    write(TABLE_COL_CENTER_ALIGNED_MARKUP);
                    break;
                case Sink.JUSTIFY_LEFT:
                    write(TABLE_COL_LEFT_ALIGNED_MARKUP);
                    break;
                default:
                    write(TABLE_COL_DEFAULT_ALIGNED_MARKUP);
                    break;
            }
            write(TABLE_CELL_SEPARATOR_MARKUP);
        }
        write(EOL);
    }

    @Override
    public void tableCell(SinkEventAttributes attributes) {
        if (elementContextStack.element().isHtml()) {
            super.tableCell(attributes);
        } else {
            startContext(ElementContext.TABLE_CELL);
            if (attributes != null) {
                // evaluate alignment attributes
                final int cellJustification;
                if (attributes.containsAttributes(SinkEventAttributeSet.LEFT)) {
                    cellJustification = Sink.JUSTIFY_LEFT;
                } else if (attributes.containsAttributes(SinkEventAttributeSet.RIGHT)) {
                    cellJustification = Sink.JUSTIFY_RIGHT;
                } else if (attributes.containsAttributes(SinkEventAttributeSet.CENTER)) {
                    cellJustification = Sink.JUSTIFY_CENTER;
                } else {
                    cellJustification = -1;
                }
                if (cellJustification > -1) {
                    if (cellJustif.size() > cellCount) {
                        cellJustif.set(cellCount, cellJustification);
                    } else if (cellJustif.size() == cellCount) {
                        cellJustif.add(cellJustification);
                    } else {
                        // create non-existing justifications for preceding columns
                        for (int precedingCol = cellJustif.size(); precedingCol < cellCount; precedingCol++) {
                            cellJustif.add(Sink.JUSTIFY_DEFAULT);
                        }
                        cellJustif.add(cellJustification);
                    }
                }
            }
        }
    }

    @Override
    public void tableHeaderCell(SinkEventAttributes attributes) {
        if (elementContextStack.element().isHtml()) {
            super.tableHeaderCell(attributes);
        } else {
            tableCell(attributes);
            tableHeaderCellFlag = true;
        }
    }

    @Override
    public void tableCell_() {
        if (elementContextStack.element().isHtml()) {
            super.tableCell_();
        } else {
            endTableCell();
        }
    }

    @Override
    public void tableHeaderCell_() {
        if (elementContextStack.element().isHtml()) {
            super.tableHeaderCell_();
        } else {
            endTableCell();
        }
    }

    /**
     * Ends a table cell.
     */
    private void endTableCell() {
        endContext(ElementContext.TABLE_CELL);
        write(TABLE_CELL_SEPARATOR_MARKUP);
        cellCount++;
    }

    @Override
    public void tableCaption(SinkEventAttributes attributes) {
        if (elementContextStack.element().isHtml()) {
            super.tableCaption(attributes);
        } else {
            elementContextStack.add(ElementContext.TABLE_CAPTION);
        }
    }

    @Override
    public void tableCaption_() {
        if (elementContextStack.element().isHtml()) {
            super.tableCaption_();
        } else {
            endContext(ElementContext.TABLE_CAPTION);
        }
    }

    @Override
    public void figure(SinkEventAttributes attributes) {
        if (elementContextStack.element().isHtml()) {
            super.figure(attributes);
        } else {
            figureSrc = null;
            startContext(ElementContext.FIGURE);
        }
    }

    @Override
    public void figureCaption(SinkEventAttributes attributes) {
        if (elementContextStack.element().isHtml()) {
            super.figureCaption(attributes);
        }
    }

    @Override
    public void figureCaption_() {
        if (elementContextStack.element().isHtml()) {
            super.figureCaption_();
        }
    }

    @Override
    public void figureGraphics(String name, SinkEventAttributes attributes) {
        if (elementContextStack.element().isHtml()) {
            super.figureGraphics(name, attributes);
        } else {
            figureSrc = name;
            // is it a standalone image (outside a figure)?
            if (elementContextStack.peek() != ElementContext.FIGURE) {
                Object alt = attributes.getAttribute(SinkEventAttributes.ALT);
                if (alt == null) {
                    alt = "";
                }
                writeImage(elementContextStack.element().escape(lineAwareWriter, alt.toString()), name);
            }
        }
    }

    @Override
    public void figure_() {
        if (elementContextStack.element().isHtml()) {
            super.figure_();
        } else {
            String label = bufferingStackWriter.getCurrentBuffer().toString();
            endContext(ElementContext.FIGURE);
            writeImage(label, figureSrc);
        }
    }

    private void writeImage(String alt, String src) {
        write("![");
        write(alt);
        write("](" + src + ")");
    }

    public void anchor(String name, SinkEventAttributes attributes) {
        super.anchor(name, attributes);
        if (!elementContextStack.element().isHtml()) {
            // close anchor tag immediately otherwise markdown would not be allowed afterwards
            write("</a>");
        }
    }

    @Override
    public void anchor_() {
        if (elementContextStack.element().isHtml()) {
            super.anchor_();
        } else {
            // anchor is always empty html element, i.e. already closed with anchor()
        }
    }

    public void link(String name, SinkEventAttributes attributes) {
        if (elementContextStack.element().isHtml()) {
            super.link(name, attributes);
        } else {
            if (elementContextStack.element() == ElementContext.CODE_BLOCK) {
                LOGGER.warn("{}Ignoring unsupported link inside code block", getLocationLogPrefix());
            } else if (elementContextStack.element() == ElementContext.CODE_SPAN) {
                // emit link outside the code span, i.e. insert at the beginning of the buffer
                bufferingStackWriter.getCurrentBuffer().insert(0, LINK_START_1_MARKUP);
                linkName = name;
            } else {
                write(LINK_START_1_MARKUP);
                linkName = name;
            }
        }
    }

    @Override
    public void link_() {
        if (elementContextStack.element().isHtml()) {
            super.link_();
        } else {
            if (elementContextStack.element() == ElementContext.CODE_BLOCK) {
                return;
            } else if (elementContextStack.element() == ElementContext.CODE_SPAN) {
                // defer emitting link end markup until inline_() is called
                StringBuilder linkEndMarkup = new StringBuilder();
                linkEndMarkup.append(LINK_START_2_MARKUP);
                linkEndMarkup.append(linkName);
                linkEndMarkup.append(LINK_END_MARKUP);
                Queue<String> endMarkups = new LinkedList<>(inlineStack.poll());
                endMarkups.add(linkEndMarkup.toString());
                inlineStack.add(endMarkups);
            } else {
                write(LINK_START_2_MARKUP + linkName + LINK_END_MARKUP);
            }
            linkName = null;
        }
    }

    @Override
    public void inline(SinkEventAttributes attributes) {
        Queue<String> endMarkups = Collections.asLifoQueue(new LinkedList<>());
        if (elementContextStack.element().isHtml()) {
            super.inline(attributes);
            endMarkups.add(USE_XHTML_SINK);
        } else {
            boolean requiresHtml = elementContextStack.element() == ElementContext.HTML_BLOCK;
            if (attributes != null
                    && elementContextStack.element() != ElementContext.CODE_BLOCK
                    && elementContextStack.element() != ElementContext.CODE_SPAN) {
                // code excludes other styles in markdown
                if (attributes.containsAttributes(SinkEventAttributeSet.Semantics.CODE)
                        || attributes.containsAttributes(SinkEventAttributeSet.Semantics.MONOSPACED)
                        || attributes.containsAttributes(SinkEventAttributeSet.MONOSPACED)) {
                    if (requiresHtml) {
                        write("<code>");
                        endMarkups.add("</code>");
                    } else {
                        startContext(ElementContext.CODE_SPAN);
                        write(MONOSPACED_START_MARKUP);
                        endMarkups.add(MONOSPACED_END_MARKUP);
                    }
                } else {
                    SinkEventAttributeSet remainingAttributes = new SinkEventAttributeSet(attributes);
                    // in XHTML "<em>" is used, but some tests still rely on the outdated "<italic>"
                    if (filterAttributes(
                            remainingAttributes,
                            SinkEventAttributeSet.Semantics.EMPHASIS,
                            SinkEventAttributeSet.Semantics.ITALIC,
                            SinkEventAttributeSet.ITALIC)) {
                        if (requiresHtml) {
                            write("<em>");
                            endMarkups.add("</em>");
                        } else {
                            write(ITALIC_START_MARKUP);
                            endMarkups.add(ITALIC_END_MARKUP);
                        }
                    }
                    // in XHTML "<strong>" is used, but some tests still rely on the outdated "<bold>"
                    if (filterAttributes(
                            remainingAttributes,
                            SinkEventAttributeSet.Semantics.STRONG,
                            SinkEventAttributeSet.Semantics.BOLD,
                            SinkEventAttributeSet.BOLD)) {
                        if (requiresHtml) {
                            write("<strong>");
                            endMarkups.add("</strong>");
                        } else {
                            write(BOLD_START_MARKUP);
                            endMarkups.add(BOLD_END_MARKUP);
                        }
                    }
                    // <del> is supported via GFM strikethrough extension
                    if (filterAttributes(remainingAttributes, SinkEventAttributeSet.Semantics.DELETE)) {
                        if (requiresHtml) {
                            write("<del>");
                            endMarkups.add("</del>");
                        } else {
                            write(STRIKETHROUGH_START_MARKUP);
                            endMarkups.add(STRIKETHROUGH_END_MARKUP);
                        }
                    }
                    if (!remainingAttributes.isEmpty()) {
                        // use HTML for other inline semantics which are not natively supported in Markdown (e.g.
                        // subscript, superscript, small, etc.)
                        super.inline(remainingAttributes);
                        endMarkups.add(USE_XHTML_SINK);
                    }
                }
            }
        }
        inlineStack.add(endMarkups);
    }

    private static boolean filterAttributes(MutableAttributeSet attributes, AttributeSet... attributesToFilter) {
        boolean hasAny = false;
        for (AttributeSet attributeSet : attributesToFilter) {
            if (attributes.containsAttributes(attributeSet)) {
                hasAny = true;
                attributes.removeAttributes(attributeSet);
            }
        }
        return hasAny;
    }

    @Override
    public void inline_() {
        for (String endMarkup : inlineStack.remove()) {
            if (USE_XHTML_SINK.equals(endMarkup)) {
                super.inline_();
            } else {
                if (endMarkup.equals(MONOSPACED_END_MARKUP)) {
                    String buffer = bufferingStackWriter.getCurrentBuffer().toString();
                    endContext(ElementContext.CODE_SPAN);
                    write(buffer);
                }
                write(endMarkup);
            }
        }
    }

    @Override
    public void italic() {
        inline(SinkEventAttributeSet.Semantics.ITALIC);
    }

    @Override
    public void italic_() {
        inline_();
    }

    @Override
    public void bold() {
        inline(SinkEventAttributeSet.Semantics.BOLD);
    }

    @Override
    public void bold_() {
        inline_();
    }

    @Override
    public void monospaced() {
        inline(SinkEventAttributeSet.Semantics.CODE);
    }

    @Override
    public void monospaced_() {
        inline_();
    }

    @Override
    public void lineBreak(SinkEventAttributes attributes) {
        if (elementContextStack.element() == ElementContext.TABLE_CELL) {
            super.lineBreak(attributes);
        } else {
            if (elementContextStack.element() == ElementContext.CODE_BLOCK) {
                write(EOL);
            } else {
                write("" + SPACE + SPACE + EOL);
            }
            write(getLinePrefix());
        }
    }

    @Override
    public void nonBreakingSpace() {
        write(NON_BREAKING_SPACE_MARKUP);
    }

    @Override
    public void text(String text, SinkEventAttributes attributes) {
        if (elementContextStack.element().isHtml()) {
            super.text(text, attributes);
        } else {
            if (attributes != null) {
                inline(attributes);
            }
            ElementContext currentContext = elementContextStack.element();
            if (currentContext == ElementContext.TABLE_CAPTION) {
                // table caption cannot even be emitted via XHTML in markdown as there is no suitable location
                LOGGER.warn("{}Ignoring unsupported table caption in Markdown", getLocationLogPrefix());
            } else {
                String unifiedText = currentContext.escape(lineAwareWriter, unifyEOLs(text));
                // ignore newlines only, because those are emitted often coming from linebreaks in HTML with no
                // semantical
                // meaning
                if (!unifiedText.equals(EOL)) {
                    String prefix = getLinePrefix();
                    if (prefix.length() > 0) {
                        unifiedText = unifiedText.replaceAll(EOL, EOL + prefix);
                    }
                }
                write(unifiedText);
            }
            if (attributes != null) {
                inline_();
            }
        }
    }

    @Override
    public void rawText(String text) {
        write(text);
    }

    /**
     * {@inheritDoc}
     *
     * Unknown events just log a warning message but are ignored otherwise.
     * @see org.apache.maven.doxia.sink.Sink#unknown(String,Object[],SinkEventAttributes)
     */
    @Override
    public void unknown(String name, Object[] requiredParams, SinkEventAttributes attributes) {
        LOGGER.warn("{}Unknown Sink event '" + name + "', ignoring!", getLocationLogPrefix());
    }

    @Override
    public void markupLineBreak(int indentLevel) {
        // not allowed in all contexts
        if (elementContextStack.element().isAllowsMarkupLinebreaks()) {
            if (!lineAwareWriter.isWriterAfterBlankLine()) {
                super.markupLineBreak(indentLevel);
            }
        }
    }

    @Override
    public void close() {
        toogleToRootContextWithoutBuffering(true);
        super.close();
    }
}
