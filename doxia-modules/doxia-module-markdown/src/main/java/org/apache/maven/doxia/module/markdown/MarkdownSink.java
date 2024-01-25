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

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.AbstractTextSink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.util.HtmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Markdown generator implementation.
 * <br>
 * <b>Note</b>: The encoding used is UTF-8.
 */
public class MarkdownSink extends AbstractTextSink implements MarkdownMarkup {
    private static final Logger LOGGER = LoggerFactory.getLogger(MarkdownSink.class);

    // ----------------------------------------------------------------------
    // Instance fields
    // ----------------------------------------------------------------------

    /**  A buffer that holds the current text when headerFlag or bufferFlag set to <code>true</code>. The content of this buffer is already escaped. */
    private StringBuilder buffer;

    /**  author. */
    private Collection<String> authors;

    /**  title. */
    private String title;

    /**  date. */
    private String date;

    /**  linkName. */
    private String linkName;

    /** tableHeaderCellFlag, set to {@code true} for table rows containing at least one table header cell  */
    private boolean tableHeaderCellFlag;

    /**  number of cells in a table. */
    private int cellCount;

    /**  justification of table cells per column. */
    private List<Integer> cellJustif;

    /**  is header row */
    private boolean isFirstTableRow;

    /**  The writer to use. */
    private final PrintWriter writer;

    /** {@code true} when last written character in {@link #writer} was a line separator, or writer is still at the beginning */
    private boolean isWriterAtStartOfNewLine;

    /** Keep track of end markup for inline events. */
    protected Queue<Queue<String>> inlineStack = Collections.asLifoQueue(new LinkedList<>());

    /** The context of the surrounding elements as stack (LIFO) */
    protected Queue<ElementContext> elementContextStack = Collections.asLifoQueue(new LinkedList<>());

    private String figureSrc;

    /** Most important contextual metadata (of the surrounding element) */
    enum ElementContext {
        HEAD("head", true, null, true),
        BODY("body", true, MarkdownSink::escapeMarkdown),
        // only the elements, which affect rendering of children and are different from BODY or HEAD are listed here
        FIGURE("", false, MarkdownSink::escapeMarkdown, true),
        CODE_BLOCK("code block", true, null),
        CODE_SPAN("code span", false, null),
        TABLE_CAPTION("table caption", false, MarkdownSink::escapeMarkdown),
        TABLE_CELL("table cell", false, MarkdownSink::escapeForTableCell, true),
        // same parameters as BODY but paragraphs inside lists are handled differently
        LIST("list (regular)", true, MarkdownSink::escapeMarkdown),
        NUMBERED_LIST("list (numbered)", true, MarkdownSink::escapeMarkdown);

        final String name;
        /**
         * {@code true} if block element, otherwise {@code false} for inline elements
         */
        final boolean isBlock;
        /**
         * The function to call to escape the given text. The function is supposed to return the escaped text or return just the given text if no escaping is necessary in this context
         */
        final UnaryOperator<String> escapeFunction;

        /**
         * if {@code true} requires buffering any text appearing inside this context
         */
        final boolean requiresBuffering;

        ElementContext(String name, boolean isBlock, UnaryOperator<String> escapeFunction) {
            this(name, isBlock, escapeFunction, false);
        }

        ElementContext(String name, boolean isBlock, UnaryOperator<String> escapeFunction, boolean requiresBuffering) {
            this.name = name;
            this.isBlock = isBlock;
            this.escapeFunction = escapeFunction;
            this.requiresBuffering = requiresBuffering;
        }

        private String escape(String text) {
            // is escaping necessary at all?
            if (escapeFunction == null) {
                return text;
            } else {
                return escapeFunction.apply(text);
            }
        }
    }
    // ----------------------------------------------------------------------
    // Public protected methods
    // ----------------------------------------------------------------------

    /**
     * Constructor, initialize the Writer and the variables.
     *
     * @param writer not null writer to write the result. <b>Should</b> be an UTF-8 Writer.
     */
    protected MarkdownSink(Writer writer) {
        this.writer = new PrintWriter(writer);
        isWriterAtStartOfNewLine = true;

        init();
    }

    private void leaveContext(ElementContext expectedContext) {
        ElementContext removedContext = elementContextStack.remove();
        if (removedContext != expectedContext) {
            throw new IllegalStateException("Unexpected context " + removedContext);
        }
    }
    /**
     * Returns the buffer that holds the current text.
     *
     * @return A StringBuffer.
     */
    protected StringBuilder getBuffer() {
        return buffer;
    }

    @Override
    protected void init() {
        super.init();

        resetBuffer();

        this.authors = new LinkedList<>();
        this.title = null;
        this.date = null;
        this.linkName = null;
        this.tableHeaderCellFlag = false;
        this.cellCount = 0;
        this.cellJustif = null;
        this.elementContextStack.clear();
        this.inlineStack.clear();
        // always set a default context (at least for tests not emitting a body)
        elementContextStack.add(ElementContext.BODY);
    }

    /**
     * Reset the StringBuilder.
     */
    protected void resetBuffer() {
        buffer = new StringBuilder();
    }

    @Override
    public void head(SinkEventAttributes attributes) {
        init();
        // remove default body context here
        leaveContext(ElementContext.BODY);
        elementContextStack.add(ElementContext.HEAD);
    }

    @Override
    public void head_() {
        leaveContext(ElementContext.HEAD);
        // only write head block if really necessary
        if (title == null && authors.isEmpty() && date == null) {
            return;
        }
        writeUnescaped(METADATA_MARKUP + EOL);
        if (title != null) {
            writeUnescaped("title: " + title + EOL);
        }
        if (!authors.isEmpty()) {
            writeUnescaped("author: " + EOL);
            for (String author : authors) {
                writeUnescaped("  - " + author + EOL);
            }
        }
        if (date != null) {
            writeUnescaped("date: " + date + EOL);
        }
        writeUnescaped(METADATA_MARKUP + BLANK_LINE);
    }

    @Override
    public void body(SinkEventAttributes attributes) {
        elementContextStack.add(ElementContext.BODY);
    }

    @Override
    public void body_() {
        leaveContext(ElementContext.BODY);
    }

    @Override
    public void title_() {
        if (buffer.length() > 0) {
            title = buffer.toString();
            resetBuffer();
        }
    }

    @Override
    public void author_() {
        if (buffer.length() > 0) {
            authors.add(buffer.toString());
            resetBuffer();
        }
    }

    @Override
    public void date_() {
        if (buffer.length() > 0) {
            date = buffer.toString();
            resetBuffer();
        }
    }

    @Override
    public void sectionTitle(int level, SinkEventAttributes attributes) {
        if (level > 0) {
            writeUnescaped(StringUtils.repeat(SECTION_TITLE_START_MARKUP, level) + SPACE);
        }
    }

    @Override
    public void sectionTitle_(int level) {
        if (level > 0) {
            writeUnescaped(BLANK_LINE);
        }
    }

    @Override
    public void list(SinkEventAttributes attributes) {
        elementContextStack.add(ElementContext.LIST);
    }

    @Override
    public void list_() {
        endNumberedOrRegularList(false);
    }

    @Override
    public void listItem(SinkEventAttributes attributes) {
        orderedOrUnorderedListItem();
    }

    @Override
    public void listItem_() {
        orderedOrUnorderedListItem_();
    }

    @Override
    public void numberedList(int numbering, SinkEventAttributes attributes) {
        elementContextStack.add(ElementContext.NUMBERED_LIST);
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
        endNumberedOrRegularList(true);
    }

    private void endNumberedOrRegularList(boolean isNumberedList) {
        ElementContext context = isNumberedList ? ElementContext.NUMBERED_LIST : ElementContext.LIST;
        leaveContext(context);
        if (getListNestingLevel() < 0) {
            writeUnescaped(
                    EOL); // end add blank line (together with the preceding EOL of the item) only in case this was not
            // nested
        }
    }

    @Override
    public void numberedListItem(SinkEventAttributes attributes) {
        orderedOrUnorderedListItem();
    }

    @Override
    public void numberedListItem_() {
        orderedOrUnorderedListItem_();
    }

    private void orderedOrUnorderedListItem() {
        writeUnescaped(getListItemPrefix());
    }

    private void orderedOrUnorderedListItem_() {
        ensureBeginningOfLine();
    }

    /**
     *
     * @return the nesting level of the list. -1 for outside a list, 0 for non-nested list, 1 for list inside a list, ....
     */
    private int getListNestingLevel() {
        return (int) (elementContextStack.stream()
                        .filter(e -> e == ElementContext.LIST)
                        .count()
                - 1);
    }

    /**
     *
     * @return {@code true} if in numbered list, {@code false} if inside regular list
     * @throws IllegalStateException if not inside a list at all
     */
    private boolean isInNumberedList() {
        ElementContext context = elementContextStack.element();
        if (context == ElementContext.LIST) {
            return false;
        } else if (context == ElementContext.NUMBERED_LIST) {
            return true;
        } else {
            throw new IllegalStateException("Not inside a list but inside " + context);
        }
    }

    private String getListItemPrefix() {
        StringBuilder prefix = new StringBuilder();
        prefix.append(StringUtils.repeat(INDENT, getListNestingLevel())); // 4 spaces per list nesting level
        prefix.append(isInNumberedList() ? LIST_ORDERED_ITEM_START_MARKUP : LIST_UNORDERED_ITEM_START_MARKUP);
        prefix.append(SPACE);
        return prefix.toString();
    }

    @Override
    public void definitionList(SinkEventAttributes attributes) {
        LOGGER.warn(
                "{}Definition list not natively supported in Markdown, rendering HTML instead", getLocationLogPrefix());
        writeUnescaped("<dl>" + EOL);
    }

    @Override
    public void definitionList_() {
        writeUnescaped("</dl>" + BLANK_LINE);
    }

    @Override
    public void definedTerm(SinkEventAttributes attributes) {
        writeUnescaped("<dt>");
    }

    @Override
    public void definedTerm_() {
        writeUnescaped("</dt>" + EOL);
    }

    @Override
    public void definition(SinkEventAttributes attributes) {
        writeUnescaped("<dd>");
    }

    @Override
    public void definition_() {
        writeUnescaped("</dd>" + EOL);
    }

    @Override
    public void pageBreak() {
        LOGGER.warn("Ignoring unsupported page break in Markdown");
    }

    @Override
    public void paragraph(SinkEventAttributes attributes) {
        // ignore paragraphs in inline elements
        if (elementContextStack.element().isBlock) {
            ensureBeginningOfLine();
            if (elementContextStack.element() == ElementContext.LIST) {
                // indentation is mandatory inside lists (only for first line)
                writeUnescaped(INDENT);
            }
        }
    }

    @Override
    public void paragraph_() {
        // ignore paragraphs in inline elements
        if (elementContextStack.element().isBlock) {
            writeUnescaped(BLANK_LINE);
        }
    }

    @Override
    public void verbatim(SinkEventAttributes attributes) {
        // always assume is supposed to be monospaced (i.e. emitted inside a <pre><code>...</code></pre>)
        ensureBeginningOfLine();
        elementContextStack.add(ElementContext.CODE_BLOCK);
        writeUnescaped(VERBATIM_START_MARKUP + EOL);
    }

    @Override
    public void verbatim_() {
        ensureBeginningOfLine();
        writeUnescaped(VERBATIM_END_MARKUP + BLANK_LINE);
        leaveContext(ElementContext.CODE_BLOCK);
    }

    @Override
    public void horizontalRule(SinkEventAttributes attributes) {
        ensureBeginningOfLine();
        writeUnescaped(HORIZONTAL_RULE_MARKUP + BLANK_LINE);
    }

    @Override
    public void table(SinkEventAttributes attributes) {
        ensureBeginningOfLine();
    }

    @Override
    public void tableRows(int[] justification, boolean grid) {
        if (justification != null) {
            cellJustif = Arrays.stream(justification).boxed().collect(Collectors.toCollection(ArrayList::new));
        } else {
            cellJustif = new ArrayList<>();
        }
        // grid flag is not supported
        isFirstTableRow = true;
    }

    @Override
    public void tableRows_() {
        cellJustif = null;
    }

    @Override
    public void tableRow(SinkEventAttributes attributes) {
        cellCount = 0;
    }

    @Override
    public void tableRow_() {
        if (isFirstTableRow && !tableHeaderCellFlag) {
            // emit empty table header as this is mandatory for GFM table extension
            // (https://stackoverflow.com/a/17543474/5155923)
            writeEmptyTableHeader();
            writeTableDelimiterRow();
            tableHeaderCellFlag = false;
            isFirstTableRow = false;
            // afterwards emit the first row
        }

        writeUnescaped(TABLE_ROW_PREFIX);

        writeUnescaped(buffer.toString());

        resetBuffer();

        writeUnescaped(EOL);

        if (isFirstTableRow) {
            // emit delimiter row
            writeTableDelimiterRow();
            isFirstTableRow = false;
        }

        // only reset cell count if this is the last row
        cellCount = 0;
    }

    private void writeEmptyTableHeader() {
        writeUnescaped(TABLE_ROW_PREFIX);
        for (int i = 0; i < cellCount; i++) {
            writeUnescaped(StringUtils.repeat(String.valueOf(SPACE), 3) + TABLE_CELL_SEPARATOR_MARKUP);
        }
        writeUnescaped(EOL);
    }

    /** Emit the delimiter row which determines the alignment */
    private void writeTableDelimiterRow() {
        writeUnescaped(TABLE_ROW_PREFIX);
        int justification = Sink.JUSTIFY_LEFT;
        for (int i = 0; i < cellCount; i++) {
            // keep previous column's alignment in case too few are specified
            if (cellJustif != null && cellJustif.size() > i) {
                justification = cellJustif.get(i);
            }
            switch (justification) {
                case Sink.JUSTIFY_RIGHT:
                    writeUnescaped(TABLE_COL_RIGHT_ALIGNED_MARKUP);
                    break;
                case Sink.JUSTIFY_CENTER:
                    writeUnescaped(TABLE_COL_CENTER_ALIGNED_MARKUP);
                    break;
                default:
                    writeUnescaped(TABLE_COL_LEFT_ALIGNED_MARKUP);
                    break;
            }
            writeUnescaped(TABLE_CELL_SEPARATOR_MARKUP);
        }
        writeUnescaped(EOL);
    }

    @Override
    public void tableCell(SinkEventAttributes attributes) {
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
                        cellJustif.add(Sink.JUSTIFY_LEFT);
                    }
                    cellJustif.add(cellJustification);
                }
            }
        }
        elementContextStack.add(ElementContext.TABLE_CELL);
    }

    @Override
    public void tableHeaderCell(SinkEventAttributes attributes) {
        tableCell(attributes);
        tableHeaderCellFlag = true;
    }

    @Override
    public void tableCell_() {
        endTableCell();
    }

    @Override
    public void tableHeaderCell_() {
        endTableCell();
    }

    /**
     * Ends a table cell.
     */
    private void endTableCell() {
        leaveContext(ElementContext.TABLE_CELL);
        buffer.append(TABLE_CELL_SEPARATOR_MARKUP);
        cellCount++;
    }

    @Override
    public void tableCaption(SinkEventAttributes attributes) {
        elementContextStack.add(ElementContext.TABLE_CAPTION);
    }

    @Override
    public void tableCaption_() {
        leaveContext(ElementContext.TABLE_CAPTION);
    }

    @Override
    public void figure(SinkEventAttributes attributes) {
        figureSrc = null;
        elementContextStack.add(ElementContext.FIGURE);
    }

    @Override
    public void figureGraphics(String name, SinkEventAttributes attributes) {
        figureSrc = escapeMarkdown(name);
        // is it a standalone image (outside a figure)?
        if (elementContextStack.peek() != ElementContext.FIGURE) {
            Object alt = attributes.getAttribute(SinkEventAttributes.ALT);
            if (alt == null) {
                alt = "";
            }
            writeImage(escapeMarkdown(alt.toString()), name);
        }
    }

    @Override
    public void figure_() {
        leaveContext(ElementContext.FIGURE);
        writeImage(buffer.toString(), figureSrc);
    }

    private void writeImage(String alt, String src) {
        writeUnescaped("![");
        writeUnescaped(alt);
        writeUnescaped("](" + src + ")");
    }

    /** {@inheritDoc} */
    public void anchor(String name, SinkEventAttributes attributes) {
        // write(ANCHOR_START_MARKUP + name);
        // TODO get implementation from Xhtml5 base sink
    }

    @Override
    public void anchor_() {
        // write(ANCHOR_END_MARKUP);
    }

    /** {@inheritDoc} */
    public void link(String name, SinkEventAttributes attributes) {
        writeUnescaped(LINK_START_1_MARKUP);
        linkName = name;
    }

    @Override
    public void link_() {
        writeUnescaped(LINK_START_2_MARKUP);
        text(linkName.startsWith("#") ? linkName.substring(1) : linkName);
        writeUnescaped(LINK_END_MARKUP);
        linkName = null;
    }

    @Override
    public void inline(SinkEventAttributes attributes) {
        Queue<String> endMarkups = Collections.asLifoQueue(new LinkedList<>());

        if (attributes != null
                && elementContextStack.element() != ElementContext.CODE_BLOCK
                && elementContextStack.element() != ElementContext.CODE_SPAN) {
            // code excludes other styles in markdown
            if (attributes.containsAttribute(SinkEventAttributes.SEMANTICS, "code")
                    || attributes.containsAttribute(SinkEventAttributes.SEMANTICS, "monospaced")
                    || attributes.containsAttribute(SinkEventAttributes.STYLE, "monospaced")) {
                writeUnescaped(MONOSPACED_START_MARKUP);
                endMarkups.add(MONOSPACED_END_MARKUP);
                elementContextStack.add(ElementContext.CODE_SPAN);
            } else {
                // in XHTML "<em>" is used, but some tests still rely on the outdated "<italic>"
                if (attributes.containsAttribute(SinkEventAttributes.SEMANTICS, "em")
                        || attributes.containsAttribute(SinkEventAttributes.SEMANTICS, "italic")
                        || attributes.containsAttribute(SinkEventAttributes.STYLE, "italic")) {
                    writeUnescaped(ITALIC_START_MARKUP);
                    endMarkups.add(ITALIC_END_MARKUP);
                }
                // in XHTML "<strong>" is used, but some tests still rely on the outdated "<bold>"
                if (attributes.containsAttribute(SinkEventAttributes.SEMANTICS, "strong")
                        || attributes.containsAttribute(SinkEventAttributes.SEMANTICS, "bold")
                        || attributes.containsAttribute(SinkEventAttributes.STYLE, "bold")) {
                    writeUnescaped(BOLD_START_MARKUP);
                    endMarkups.add(BOLD_END_MARKUP);
                }
            }
        }
        inlineStack.add(endMarkups);
    }

    @Override
    public void inline_() {
        for (String endMarkup : inlineStack.remove()) {
            if (endMarkup.equals(MONOSPACED_END_MARKUP)) {
                leaveContext(ElementContext.CODE_SPAN);
            }
            writeUnescaped(endMarkup);
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
        writeUnescaped("" + SPACE + SPACE + EOL);
    }

    @Override
    public void nonBreakingSpace() {
        writeUnescaped(NON_BREAKING_SPACE_MARKUP);
    }

    @Override
    public void text(String text, SinkEventAttributes attributes) {
        if (attributes != null) {
            inline(attributes);
        }
        ElementContext currentContext = elementContextStack.element();
        if (currentContext == ElementContext.TABLE_CAPTION) {
            // table caption cannot even be emitted via XHTML in markdown as there is no suitable location
            LOGGER.warn("{}Ignoring unsupported table caption in Markdown", getLocationLogPrefix());
        } else {
            String unifiedText = currentContext.escape(unifyEOLs(text));
            writeUnescaped(unifiedText);
        }
        if (attributes != null) {
            inline_();
        }
    }

    @Override
    public void rawText(String text) {
        writeUnescaped(text);
    }

    @Override
    public void comment(String comment) {
        rawText(COMMENT_START + comment + COMMENT_END);
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

    /**
     *
     * @return {@code true} if any of the parent contexts require buffering
     */
    private boolean requiresBuffering() {
        return elementContextStack.stream().anyMatch(c -> c.requiresBuffering);
    }

    protected void writeUnescaped(String text) {
        if (requiresBuffering()) {
            buffer.append(text);
        } else {
            isWriterAtStartOfNewLine = text.endsWith(EOL);
            writer.write(text);
        }
    }

    @Override
    public void flush() {
        writer.flush();
    }

    @Override
    public void close() {
        writer.close();

        init();
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * First use XML escaping (leveraging the predefined entities, for browsers)
     * afterwards escape special characters in a text with a leading backslash (for markdown parsers)
     *
     * <pre>
     * \, `, *, _, {, }, [, ], (, ), #, +, -, ., !
     * </pre>
     *
     * @param text the String to escape, may be null
     * @return the text escaped, "" if null String input
     * @see <a href="https://daringfireball.net/projects/markdown/syntax#backslash">Backslash Escapes</a>
     */
    private static String escapeMarkdown(String text) {
        if (text == null) {
            return "";
        }
        text = HtmlTools.escapeHTML(text, true); // assume UTF-8 output, i.e. only use the mandatory XML entities
        int length = text.length();
        StringBuilder buffer = new StringBuilder(length);

        for (int i = 0; i < length; ++i) {
            char c = text.charAt(i);
            switch (c) {
                case '\\':
                case '`':
                case '*':
                case '_':
                case '{':
                case '}':
                case '[':
                case ']':
                case '(':
                case ')':
                case '#':
                case '+':
                case '-':
                case '.':
                case '!':
                    buffer.append('\\');
                    buffer.append(c);
                    break;
                default:
                    buffer.append(c);
            }
        }

        return buffer.toString();
    }

    /**
     * Escapes the pipe character according to <a href="https://github.github.com/gfm/#tables-extension-">GFM Table Extension</a> in addition
     * to the regular markdown escaping.
     * @param text
     * @return the escaped text
     * @see {@link #escapeMarkdown(String)
     */
    private static String escapeForTableCell(String text) {

        return escapeMarkdown(text).replace("|", "\\|");
    }

    /**
     * Ensures that the {@link #writer} is currently at the beginning of a new line.
     * Optionally writes a line separator to ensure that.
     */
    private void ensureBeginningOfLine() {
        // make sure that we are at the start of a line without adding unnecessary blank lines
        if (!isWriterAtStartOfNewLine) {
            writeUnescaped(EOL);
        }
    }
}
