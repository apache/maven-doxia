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

import javax.swing.text.MutableAttributeSet;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.AbstractTextSink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.impl.SinkUtils;
import org.apache.maven.doxia.sink.impl.Xhtml5BaseSink;
import org.apache.maven.doxia.util.DoxiaUtils;
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

    /**
     * A buffer that holds the current text when the current context requires buffering.
     * The content of this buffer is already escaped.
     */
    private Queue<StringBuilder> bufferStack = Collections.asLifoQueue(new LinkedList<>());

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

    /** The writer to use. */
    private final PrintWriter writer;

    /** A temporary writer used to buffer the last two lines */
    private final LastTwoLinesBufferingWriter bufferingWriter;

    /** Keep track of end markup for inline events. */
    protected Queue<Queue<String>> inlineStack = Collections.asLifoQueue(new LinkedList<>());

    /** The context of the surrounding elements as stack (LIFO) */
    protected Queue<ElementContext> elementContextStack = Collections.asLifoQueue(new LinkedList<>());

    private String figureSrc;

    @FunctionalInterface
    interface TextEscapeFunction {
        String escape(ElementContext context, LastTwoLinesBufferingWriter writer, String text);
    }
    /** Most important contextual metadata (of elements). This contains information about necessary escaping rules, potential prefixes and newlines */
    enum ElementContext {
        HEAD(Type.GENERIC_CONTAINER, null, true),
        BODY(Type.GENERIC_CONTAINER, ElementContext::escapeMarkdown),
        // only the elements, which affect rendering of children and are different from BODY or HEAD are listed here
        FIGURE(Type.INLINE, ElementContext::escapeMarkdown, true),
        HEADING(Type.LEAF_BLOCK, ElementContext::escapeMarkdown),
        CODE_BLOCK(Type.LEAF_BLOCK, null),
        CODE_SPAN(Type.INLINE, null, true),
        TABLE_CAPTION(Type.INLINE, ElementContext::escapeMarkdown),
        TABLE_ROW(Type.CONTAINER_BLOCK, null, true),
        TABLE_CELL(
                Type.LEAF_BLOCK,
                ElementContext::escapeForTableCell,
                false), // special type, as allows containing inlines, but not starting on a separate line
        // same parameters as BODY but paragraphs inside list items are handled differently
        LIST_ITEM(Type.CONTAINER_BLOCK, ElementContext::escapeMarkdown, false, INDENT),
        BLOCKQUOTE(Type.CONTAINER_BLOCK, ElementContext::escapeMarkdown, false, BLOCKQUOTE_START_MARKUP),
        HTML_BLOCK(Type.LEAF_BLOCK, ElementContext::escapeHtml, false, "", true);

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

        ElementContext(Type type, TextEscapeFunction escapeFunction) {
            this(type, escapeFunction, false);
        }

        ElementContext(Type type, TextEscapeFunction escapeFunction, boolean requiresBuffering) {
            this(type, escapeFunction, requiresBuffering, "");
        }

        ElementContext(Type type, TextEscapeFunction escapeFunction, boolean requiresBuffering, String prefix) {
            this(type, escapeFunction, requiresBuffering, prefix, false);
        }

        ElementContext(
                Type type,
                TextEscapeFunction escapeFunction,
                boolean requiresBuffering,
                String prefix,
                boolean requiresSurroundingByBlankLines) {
            this.type = type;
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
        String escape(LastTwoLinesBufferingWriter writer, String text) {
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
         * @return {@code true} for all containers (allowing block elements as children), {@code false} otherwise
         */
        boolean isContainer() {
            return type == Type.CONTAINER_BLOCK || type == Type.GENERIC_CONTAINER;
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
        private String escapeMarkdown(LastTwoLinesBufferingWriter writer, String text) {
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
                        if (writer.isWriterAtStartOfNewLine()) {
                            buffer.append(escapeMarkdown(c));
                        } else {
                            buffer.append(c);
                        }
                        break;
                    case '#':
                        if (this == HEADING || writer.isWriterAtStartOfNewLine()) {
                            buffer.append(escapeMarkdown(c));
                        } else {
                            buffer.append(c);
                        }
                        break;
                    case '.':
                        if (writer.isAfterDigit()) {
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

        private static String escapeMarkdown(char c) {
            return "\\" + c;
        }

        private String escapeHtml(LastTwoLinesBufferingWriter writer, String text) {
            return HtmlTools.escapeHTML(text, true);
        }

        /**
         * Escapes the pipe character according to <a href="https://github.github.com/gfm/#tables-extension-">GFM Table Extension</a> in addition
         * to the regular markdown escaping.
         * @param text
         * @return the escaped text
         * @see {@link #escapeMarkdown(String)
         */
        private String escapeForTableCell(LastTwoLinesBufferingWriter writer, String text) {

            return escapeMarkdown(writer, text).replace("|", "\\|");
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
        this.bufferingWriter = new LastTwoLinesBufferingWriter(writer);
        this.writer = new PrintWriter(bufferingWriter);

        init();
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
            bufferStack.remove();
        }
    }

    private void startContext(ElementContext newContext) {
        if (newContext.requiresBuffering) {
            bufferStack.add(new StringBuilder());
        }
        if (newContext.isBlock()) {
            // every block element within a list item must
            startBlock(newContext.requiresSurroundingByBlankLines
                    || (isInListItem() && (newContext == ElementContext.BLOCKQUOTE)
                            || (newContext == ElementContext.CODE_BLOCK)));
        }
        elementContextStack.add(newContext);
    }

    /**
     * Ensures that the {@link #writer} is currently at the beginning of a new line.
     * Optionally writes a line separator to ensure that.
     */
    private void ensureBeginningOfLine() {
        // make sure that we are at the start of a line without adding unnecessary blank lines
        if (!bufferingWriter.isWriterAtStartOfNewLine()) {
            writeUnescaped(EOL);
        }
    }

    /**
     * Ensures that the {@link #writer} is preceded by a blank line.
     * Optionally writes a blank line or just line delimiter to ensure that.
     */
    private void ensureBlankLine() {
        // prevent duplicate blank lines
        if (!bufferingWriter.isWriterAfterBlankLine()) {
            if (bufferingWriter.isWriterAtStartOfNewLine()) {
                writeUnescaped(EOL);
            } else {
                writeUnescaped(BLANK_LINE);
            }
        }
    }

    private void startBlock(boolean requireBlankLine) {
        if (requireBlankLine) {
            ensureBlankLine();
        } else {
            ensureBeginningOfLine();
        }
        writeUnescaped(getLinePrefix());
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
    /**
     * Returns the buffer that holds the text of the current context (or the closest container context with a buffer).
     * @return The StringBuilder representing the current buffer, never {@code null}
     * @throws NoSuchElementException if no buffer is available
     */
    protected StringBuilder getCurrentBuffer() {
        return bufferStack.element();
    }

    /**
     * Returns the content of the buffer of the current context (or the closest container context with a buffer).
     * The buffer is reset to an empty string in this method.
     * @return the content of the buffer as a string or {@code null} if no buffer is available
     */
    protected String consumeBuffer() {
        StringBuilder buffer = bufferStack.peek();
        if (buffer == null) {
            return null;
        } else {
            String content = buffer.toString();
            buffer.setLength(0);
            return content;
        }
    }

    @Override
    protected void init() {
        super.init();

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

    @Override
    public void head(SinkEventAttributes attributes) {
        init();
        // remove default body context here
        endContext(ElementContext.BODY);
        startContext(ElementContext.HEAD);
    }

    @Override
    public void head_() {
        endContext(ElementContext.HEAD);
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
        startContext(ElementContext.BODY);
        elementContextStack.add(ElementContext.BODY);
    }

    @Override
    public void body_() {
        endContext(ElementContext.BODY);
    }

    @Override
    public void title_() {
        String buffer = consumeBuffer();
        if (buffer != null && !buffer.isEmpty()) {
            this.title = buffer.toString();
        }
    }

    @Override
    public void author_() {
        String buffer = consumeBuffer();
        if (buffer != null && !buffer.isEmpty()) {
            authors.add(buffer);
        }
    }

    @Override
    public void date_() {
        String buffer = consumeBuffer();
        if (buffer != null && !buffer.isEmpty()) {
            date = buffer.toString();
        }
    }

    @Override
    public void sectionTitle(int level, SinkEventAttributes attributes) {
        if (level > 0) {
            ensureBeginningOfLine();
            writeUnescaped(StringUtils.repeat(SECTION_TITLE_START_MARKUP, level) + SPACE);
        }
    }

    @Override
    public void sectionTitle_(int level) {
        if (level > 0) {
            ensureBlankLine(); // always end headings with blank line to increase compatibility with arbitrary MD
            // editors
        }
    }

    @Override
    public void list_() {
        ensureBeginningOfLine();
    }

    @Override
    public void listItem(SinkEventAttributes attributes) {
        startContext(ElementContext.LIST_ITEM);
        writeUnescaped(LIST_UNORDERED_ITEM_START_MARKUP);
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
        writeUnescaped(LIST_ORDERED_ITEM_START_MARKUP);
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
        writeUnescaped("<dl>" + EOL);
    }

    @Override
    public void definitionList_() {
        writeUnescaped("</dl>");
        endContext(ElementContext.HTML_BLOCK);
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
        // ignore paragraphs outside container contexts
        if (elementContextStack.element().isContainer()) {
            ensureBlankLine();
            writeUnescaped(getLinePrefix());
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
        // if no source attribute, then don't emit an info string
        startContext(ElementContext.CODE_BLOCK);
        writeUnescaped(VERBATIM_START_MARKUP);
        if (attributes != null && attributes.containsAttributes(SinkEventAttributeSet.SOURCE)) {
            writeUnescaped("unknown"); // unknown language
        }
        writeUnescaped(EOL);
        writeUnescaped(getLinePrefix());
    }

    @Override
    public void verbatim_() {
        ensureBeginningOfLine();
        writeUnescaped(getLinePrefix());
        writeUnescaped(VERBATIM_END_MARKUP + BLANK_LINE);
        endContext(ElementContext.CODE_BLOCK);
    }

    @Override
    public void blockquote(SinkEventAttributes attributes) {
        startContext(ElementContext.BLOCKQUOTE);
        writeUnescaped(BLOCKQUOTE_START_MARKUP);
    }

    @Override
    public void blockquote_() {
        endContext(ElementContext.BLOCKQUOTE);
    }

    @Override
    public void horizontalRule(SinkEventAttributes attributes) {
        ensureBeginningOfLine();
        writeUnescaped(HORIZONTAL_RULE_MARKUP + BLANK_LINE);
        writeUnescaped(getLinePrefix());
    }

    @Override
    public void table(SinkEventAttributes attributes) {
        ensureBlankLine();
        writeUnescaped(getLinePrefix());
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
        startContext(ElementContext.TABLE_ROW);
        cellCount = 0;
    }

    @Override
    public void tableRow_() {
        String buffer = consumeBuffer();
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

        writeUnescaped(TABLE_ROW_PREFIX);
        writeUnescaped(buffer);
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
        writeUnescaped(getLinePrefix());
    }

    /** Emit the delimiter row which determines the alignment */
    private void writeTableDelimiterRow() {
        writeUnescaped(TABLE_ROW_PREFIX);
        int justification = Sink.JUSTIFY_DEFAULT;
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
                case Sink.JUSTIFY_LEFT:
                    writeUnescaped(TABLE_COL_LEFT_ALIGNED_MARKUP);
                    break;
                default:
                    writeUnescaped(TABLE_COL_DEFAULT_ALIGNED_MARKUP);
                    break;
            }
            writeUnescaped(TABLE_CELL_SEPARATOR_MARKUP);
        }
        writeUnescaped(EOL);
    }

    @Override
    public void tableCell(SinkEventAttributes attributes) {
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
        endContext(ElementContext.TABLE_CELL);
        writeUnescaped(TABLE_CELL_SEPARATOR_MARKUP);
        cellCount++;
    }

    @Override
    public void tableCaption(SinkEventAttributes attributes) {
        elementContextStack.add(ElementContext.TABLE_CAPTION);
    }

    @Override
    public void tableCaption_() {
        endContext(ElementContext.TABLE_CAPTION);
    }

    @Override
    public void figure(SinkEventAttributes attributes) {
        figureSrc = null;
        startContext(ElementContext.FIGURE);
    }

    @Override
    public void figureGraphics(String name, SinkEventAttributes attributes) {
        figureSrc = name;
        // is it a standalone image (outside a figure)?
        if (elementContextStack.peek() != ElementContext.FIGURE) {
            Object alt = attributes.getAttribute(SinkEventAttributes.ALT);
            if (alt == null) {
                alt = "";
            }
            writeImage(elementContextStack.element().escape(bufferingWriter, alt.toString()), name);
        }
    }

    @Override
    public void figure_() {
        StringBuilder buffer = getCurrentBuffer();
        String label = "";
        if (buffer != null) {
            label = buffer.toString();
        }
        endContext(ElementContext.FIGURE);
        writeImage(label, figureSrc);
    }

    private void writeImage(String alt, String src) {
        writeUnescaped("![");
        writeUnescaped(alt);
        writeUnescaped("](" + src + ")");
    }

    /** {@inheritDoc} */
    public void anchor(String name, SinkEventAttributes attributes) {
        // emit html anchor as markdown does not support anchors
        MutableAttributeSet atts = SinkUtils.filterAttributes(attributes, SinkUtils.SINK_BASE_ATTRIBUTES);

        String id = name;

        if (!DoxiaUtils.isValidId(id)) {
            id = DoxiaUtils.encodeId(name);

            LOGGER.debug("{}Modified invalid anchor name '{}' to '{}'", getLocationLogPrefix(), name, id);
        }

        MutableAttributeSet att = new SinkEventAttributeSet();
        att.addAttribute(SinkEventAttributes.ID, id);
        att.addAttributes(atts);
        StringBuilder htmlAnchor = new StringBuilder("<a");
        htmlAnchor.append(SinkUtils.getAttributeString(att));
        htmlAnchor.append(">");
        htmlAnchor.append("</a>"); // close anchor tag immediately otherwise markdown would not be allowed afterwards
        writeUnescaped(htmlAnchor.toString());
    }

    @Override
    public void anchor_() {
        // anchor is always empty html element, i.e. already closed with anchor()
    }

    /** {@inheritDoc} */
    public void link(String name, SinkEventAttributes attributes) {
        if (elementContextStack.element() == ElementContext.CODE_BLOCK) {
            LOGGER.warn("{}Ignoring unsupported link inside code block", getLocationLogPrefix());
        } else if (elementContextStack.element() == ElementContext.CODE_SPAN) {
            // emit link outside the code span, i.e. insert at the beginning of the buffer
            getCurrentBuffer().insert(0, LINK_START_1_MARKUP);
            linkName = name;
        } else {
            writeUnescaped(LINK_START_1_MARKUP);
            linkName = name;
        }
    }

    @Override
    public void link_() {
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
            writeUnescaped(LINK_START_2_MARKUP + linkName + LINK_END_MARKUP);
        }
        linkName = null;
    }

    @Override
    public void inline(SinkEventAttributes attributes) {
        Queue<String> endMarkups = Collections.asLifoQueue(new LinkedList<>());

        boolean requiresHtml = elementContextStack.element() == ElementContext.HTML_BLOCK;
        if (attributes != null
                && elementContextStack.element() != ElementContext.CODE_BLOCK
                && elementContextStack.element() != ElementContext.CODE_SPAN) {
            // code excludes other styles in markdown
            if (attributes.containsAttribute(SinkEventAttributes.SEMANTICS, "code")
                    || attributes.containsAttribute(SinkEventAttributes.SEMANTICS, "monospaced")
                    || attributes.containsAttribute(SinkEventAttributes.STYLE, "monospaced")) {
                if (requiresHtml) {
                    writeUnescaped("<code>");
                    endMarkups.add("</code>");
                } else {
                    startContext(ElementContext.CODE_SPAN);
                    writeUnescaped(MONOSPACED_START_MARKUP);
                    endMarkups.add(MONOSPACED_END_MARKUP);
                }
            } else {
                // in XHTML "<em>" is used, but some tests still rely on the outdated "<italic>"
                if (attributes.containsAttribute(SinkEventAttributes.SEMANTICS, "emphasis")
                        || attributes.containsAttribute(SinkEventAttributes.SEMANTICS, "italic")
                        || attributes.containsAttribute(SinkEventAttributes.STYLE, "italic")) {
                    if (requiresHtml) {
                        writeUnescaped("<em>");
                        endMarkups.add("</em>");
                    } else {
                        writeUnescaped(ITALIC_START_MARKUP);
                        endMarkups.add(ITALIC_END_MARKUP);
                    }
                }
                // in XHTML "<strong>" is used, but some tests still rely on the outdated "<bold>"
                if (attributes.containsAttribute(SinkEventAttributes.SEMANTICS, "strong")
                        || attributes.containsAttribute(SinkEventAttributes.SEMANTICS, "bold")
                        || attributes.containsAttribute(SinkEventAttributes.STYLE, "bold")) {
                    if (requiresHtml) {
                        writeUnescaped("<strong>");
                        endMarkups.add("</strong>");
                    } else {
                        writeUnescaped(BOLD_START_MARKUP);
                        endMarkups.add(BOLD_END_MARKUP);
                    }
                }
            }
        }
        inlineStack.add(endMarkups);
    }

    @Override
    public void inline_() {
        for (String endMarkup : inlineStack.remove()) {
            if (endMarkup.equals(MONOSPACED_END_MARKUP)) {
                String buffer = getCurrentBuffer().toString();
                endContext(ElementContext.CODE_SPAN);
                writeUnescaped(buffer);
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
        if (elementContextStack.element() == ElementContext.CODE_BLOCK) {
            writeUnescaped(EOL);
        } else {
            writeUnescaped("" + SPACE + SPACE + EOL);
        }
        writeUnescaped(getLinePrefix());
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
            String unifiedText = currentContext.escape(bufferingWriter, unifyEOLs(text));
            // ignore newlines only, because those are emitted often coming from linebreaks in HTML with no semantical
            // meaning
            if (!unifiedText.equals(EOL)) {
                String prefix = getLinePrefix();
                if (prefix.length() > 0) {
                    unifiedText = unifiedText.replaceAll(EOL, EOL + prefix);
                }
            }
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
        comment(comment, false);
    }

    @Override
    public void comment(String comment, boolean endsWithLineBreak) {
        rawText(Xhtml5BaseSink.encodeAsHtmlComment(comment, endsWithLineBreak, getLocationLogPrefix()));
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

    protected void writeUnescaped(String text) {
        StringBuilder buffer = bufferStack.peek();
        if (buffer != null) {
            buffer.append(text);
        } else {
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
}
