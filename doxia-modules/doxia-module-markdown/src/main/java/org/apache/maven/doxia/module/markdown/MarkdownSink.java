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
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.AbstractTextSink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
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
    private StringBuffer buffer;

    /**  author. */
    private Collection<String> authors;

    /**  title. */
    private String title;

    /**  date. */
    private String date;

    /**  linkName. */
    private String linkName;

    /** startFlag. */
    private boolean startFlag;

    /**  tableCaptionFlag. */
    private boolean tableCaptionFlag;

    /** tableCellFlag, set to {@code true} inside table (header) cells */
    private boolean tableCellFlag;

    /** tableRowHeaderFlag, set to {@code true} for table rows containing at least one table header cell  */
    private boolean tableHeaderCellFlag;

    /**  headerFlag. */
    private boolean headerFlag;

    /**  bufferFlag, set to {@code true} in certain elements to prevent direct writing during {@link #text(String, SinkEventAttributes)} */
    private boolean bufferFlag;

    /**  verbatimFlag. */
    private boolean verbatimFlag;

    /** figure flag, set to {@code true} between {@link #figure(SinkEventAttributes)} and {@link #figure_()} events */
    private boolean figureFlag;

    /**  number of cells in a table. */
    private int cellCount;

    /**  The writer to use. */
    private final PrintWriter writer;

    /** {@code true} when last written character in {@link #writer} was a line separator, or writer is still at the beginning */
    private boolean isWriterAtStartOfNewLine;

    /**  justification of table cells per column. */
    private List<Integer> cellJustif;

    /**  is header row */
    private boolean isFirstTableRow;

    /**  listNestingLevel, 0 outside the list, 1 for the top-level list, 2 for a nested list, 3 for a list nested inside a nested list, .... */
    private int listNestingLevel;

    /**  listStyles. */
    private final Stack<String> listStyles;

    /** Keep track of the closing tags for inline events. */
    protected Stack<List<String>> inlineStack = new Stack<>();

    private String figureSrc;

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
        this.listStyles = new Stack<>();

        init();
    }

    /**
     * Returns the buffer that holds the current text.
     *
     * @return A StringBuffer.
     */
    protected StringBuffer getBuffer() {
        return buffer;
    }

    /**
     * Used to determine whether we are in head mode.
     *
     * @param headFlag True for head mode.
     */
    protected void setHeadFlag(boolean headFlag) {
        this.headerFlag = headFlag;
    }

    @Override
    protected void init() {
        super.init();

        resetBuffer();

        this.listNestingLevel = 0;

        this.authors = new LinkedList<>();
        this.title = null;
        this.date = null;
        this.linkName = null;
        this.startFlag = true;
        this.tableCaptionFlag = false;
        this.tableCellFlag = false;
        this.tableHeaderCellFlag = false;
        this.headerFlag = false;
        this.bufferFlag = false;
        this.verbatimFlag = false;
        this.figureFlag = false;
        this.cellCount = 0;
        this.cellJustif = null;
        this.listStyles.clear();
        this.inlineStack.clear();
    }

    /**
     * Reset the StringBuilder.
     */
    protected void resetBuffer() {
        buffer = new StringBuffer();
    }

    @Override
    public void head(SinkEventAttributes attributes) {
        boolean startFlag = this.startFlag;

        init();

        headerFlag = true;
        this.startFlag = startFlag;
    }

    @Override
    public void head_() {
        headerFlag = false;

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
            write(StringUtils.repeat(SECTION_TITLE_START_MARKUP, level) + SPACE);
        }
    }

    @Override
    public void sectionTitle_(int level) {
        if (level > 0) {
            write(BLANK_LINE);
        }
    }

    @Override
    public void list(SinkEventAttributes attributes) {
        listNestingLevel++;
        listStyles.push(LIST_UNORDERED_ITEM_START_MARKUP);
    }

    @Override
    public void list_() {
        listNestingLevel--;
        if (listNestingLevel == 0) {
            write(EOL); // end add blank line (together with the preceding EOL of the item) only in case this was not
            // nested
        }
        listStyles.pop();
    }

    @Override
    public void listItem(SinkEventAttributes attributes) {
        orderedOrUnorderedListItem();
    }

    @Override
    public void listItem_() {
        orderedOrUnorderedListItem_();
    }

    /** {@inheritDoc} */
    public void numberedList(int numbering, SinkEventAttributes attributes) {
        listNestingLevel++;
        // markdown only supports decimal numbering
        if (numbering != NUMBERING_DECIMAL) {
            LOGGER.warn(
                    "{}Markdown only supports numbered item with decimal style ({}) but requested was style {}, falling back to decimal style",
                    getLocationLogPrefix(),
                    NUMBERING_DECIMAL,
                    numbering);
        }
        String style = LIST_ORDERED_ITEM_START_MARKUP;
        listStyles.push(style);
    }

    @Override
    public void numberedList_() {
        listNestingLevel--;
        if (listNestingLevel == 0) {
            write(EOL); // end add blank line (together with the preceding EOL of the item) only in case this was not
            // nested
        }
        listStyles.pop();
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
        write(getListPrefix());
    }

    private void orderedOrUnorderedListItem_() {
        ensureBeginningOfLine();
    }

    private String getListPrefix() {
        StringBuilder prefix = new StringBuilder();
        for (int indent = 1; indent < listNestingLevel; indent++) {
            prefix.append("    "); // 4 spaces per indentation level
        }
        prefix.append(listStyles.peek());
        prefix.append(SPACE);
        return prefix.toString();
    }

    @Override
    public void definitionList(SinkEventAttributes attributes) {
        LOGGER.warn(
                "{}Definition list not natively supported in Markdown, rendering HTML instead", getLocationLogPrefix());
        write("<dl>" + EOL);
    }

    @Override
    public void definitionList_() {
        verbatimFlag = true;
        write("</dl>" + BLANK_LINE);
    }

    @Override
    public void definedTerm(SinkEventAttributes attributes) {
        write("<dt>");
        verbatimFlag = false;
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
        LOGGER.warn("Ignoring unsupported page break in Markdown");
    }

    @Override
    public void paragraph(SinkEventAttributes attributes) {
        ensureBeginningOfLine();
    }

    @Override
    public void paragraph_() {
        if (tableCellFlag || listNestingLevel > 0) {
            // ignore paragraphs in table cells or lists
        } else {
            write(BLANK_LINE);
        }
    }

    @Override
    public void verbatim(SinkEventAttributes attributes) {
        ensureBeginningOfLine();
        verbatimFlag = true;
        write(VERBATIM_START_MARKUP + EOL);
    }

    @Override
    public void verbatim_() {
        ensureBeginningOfLine();
        write(VERBATIM_END_MARKUP + BLANK_LINE);
        verbatimFlag = false;
    }

    @Override
    public void horizontalRule(SinkEventAttributes attributes) {
        ensureBeginningOfLine();
        write(HORIZONTAL_RULE_MARKUP + BLANK_LINE);
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

        write(TABLE_ROW_PREFIX);

        write(buffer.toString());

        resetBuffer();

        write(EOL);

        if (isFirstTableRow) {
            // emit delimiter row
            writeTableDelimiterRow();
            isFirstTableRow = false;
        }

        // only reset cell count if this is the last row
        cellCount = 0;
    }

    private void writeEmptyTableHeader() {
        write(TABLE_ROW_PREFIX);
        for (int i = 0; i < cellCount; i++) {
            write(StringUtils.repeat(String.valueOf(SPACE), 3) + TABLE_CELL_SEPARATOR_MARKUP);
        }
        write(EOL);
    }

    /** Emit the delimiter row which determines the alignment */
    private void writeTableDelimiterRow() {
        write(TABLE_ROW_PREFIX);
        int justification = Sink.JUSTIFY_LEFT;
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
                default:
                    write(TABLE_COL_LEFT_ALIGNED_MARKUP);
                    break;
            }
            write(TABLE_CELL_SEPARATOR_MARKUP);
        }
        write(EOL);
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
        tableCellFlag = true;
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
        tableCellFlag = false;
        buffer.append(TABLE_CELL_SEPARATOR_MARKUP);
        cellCount++;
    }

    @Override
    public void tableCaption(SinkEventAttributes attributes) {
        tableCaptionFlag = true;
    }

    @Override
    public void tableCaption_() {
        tableCaptionFlag = false;
    }

    @Override
    public void figure(SinkEventAttributes attributes) {
        figureSrc = null;
        figureFlag = true;
    }

    @Override
    public void figureCaption(SinkEventAttributes attributes) {
        bufferFlag = true;
    }

    @Override
    public void figureCaption_() {
        bufferFlag = false;
    }

    @Override
    public void figureGraphics(String name, SinkEventAttributes attributes) {
        figureSrc = escapeMarkdown(name);
        if (!figureFlag) {
            Object alt = attributes.getAttribute(SinkEventAttributes.ALT);
            if (alt == null) {
                alt = "";
            }
            writeImage(escapeMarkdown(alt.toString()), name);
        }
    }

    @Override
    public void figure_() {
        writeImage(buffer.toString(), figureSrc);
        figureFlag = false;
    }

    private void writeImage(String alt, String src) {
        write("![");
        write(alt);
        write("](" + src + ")");
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
        if (!headerFlag) {
            write(LINK_START_1_MARKUP);
            linkName = name;
        }
    }

    @Override
    public void link_() {
        if (!headerFlag) {
            write(LINK_START_2_MARKUP);
            text(linkName.startsWith("#") ? linkName.substring(1) : linkName);
            write(LINK_END_MARKUP);
            linkName = null;
        }
    }

    /**
     * A link with a target.
     *
     * @param name The name of the link.
     * @param target The link target.
     */
    void link(String name, String target) {
        if (!headerFlag) {
            write(LINK_START_1_MARKUP);
        }
    }

    @Override
    public void inline(SinkEventAttributes attributes) {
        if (!headerFlag && !verbatimFlag) {
            List<String> tags = new ArrayList<>();

            if (attributes != null) {
                // in XHTML "<em>" is used, but some tests still rely on the outdated "<italic>"
                if (attributes.containsAttribute(SinkEventAttributes.SEMANTICS, "em")
                        || attributes.containsAttribute(SinkEventAttributes.SEMANTICS, "italic")) {
                    write(ITALIC_START_MARKUP);
                    tags.add(0, ITALIC_END_MARKUP);
                }
                // in XHTML "<strong>" is used, but some tests still rely on the outdated "<bold>"
                if (attributes.containsAttribute(SinkEventAttributes.SEMANTICS, "strong")
                        || attributes.containsAttribute(SinkEventAttributes.SEMANTICS, "bold")) {
                    write(BOLD_START_MARKUP);
                    tags.add(0, BOLD_END_MARKUP);
                }

                if (attributes.containsAttribute(SinkEventAttributes.SEMANTICS, "code")) {
                    write(MONOSPACED_START_MARKUP);
                    tags.add(0, MONOSPACED_END_MARKUP);
                }
            }

            inlineStack.push(tags);
        }
    }

    @Override
    public void inline_() {
        if (!headerFlag && !verbatimFlag) {
            for (String tag : inlineStack.pop()) {
                write(tag);
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
        if (headerFlag || bufferFlag) {
            buffer.append(EOL);
        } else if (verbatimFlag) {
            write(EOL);
        } else {
            write("" + SPACE + SPACE + EOL);
        }
    }

    @Override
    public void nonBreakingSpace() {
        if (headerFlag || bufferFlag) {
            buffer.append(NON_BREAKING_SPACE_MARKUP);
        } else {
            write(NON_BREAKING_SPACE_MARKUP);
        }
    }

    @Override
    public void text(String text, SinkEventAttributes attributes) {
        if (attributes != null) {
            inline(attributes);
        }
        if (tableCaptionFlag) {
            // table caption cannot even be emitted via XHTML in markdown as there is no suitable location
            LOGGER.warn("{}Ignoring unsupported table caption in Markdown", getLocationLogPrefix());
        } else if (headerFlag || bufferFlag) {
            buffer.append(escapeMarkdown(text));
        } else if (verbatimFlag) {
            verbatimContent(text);
        } else {
            content(text);
        }
        if (attributes != null) {
            inline_();
        }
    }

    @Override
    public void rawText(String text) {
        write(text);
    }

    @Override
    public void comment(String comment) {
        rawText((startFlag ? "" : EOL) + COMMENT_START + comment + COMMENT_END);
    }

    /**
     * {@inheritDoc}
     *
     * Unkown events just log a warning message but are ignored otherwise.
     * @see org.apache.maven.doxia.sink.Sink#unknown(String,Object[],SinkEventAttributes)
     */
    @Override
    public void unknown(String name, Object[] requiredParams, SinkEventAttributes attributes) {
        LOGGER.warn("{}Unknown Sink event '" + name + "', ignoring!", getLocationLogPrefix());
    }

    /**
     * Write text to output.
     *
     * @param text The text to write.
     */
    protected void write(String text) {
        startFlag = false;
        String unifiedText = unifyEOLs(text);
        if (tableCellFlag) {
            buffer.append(escapeForTableCell(unifiedText));
        } else {
            isWriterAtStartOfNewLine = unifiedText.endsWith(EOL);
            writer.write(unifiedText);
        }
    }

    /**
     * Write Markdown escaped text to output.
     *
     * @param text The text to write.
     */
    protected void content(String text) {
        write(escapeMarkdown(text));
    }

    /**
     * Write verbatim text to output.
     *
     * @param text The text to write.
     */
    protected void verbatimContent(String text) {
        write(text);
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
     * Escape special characters in a text in Markdown:
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
     * Escapes the pipe character according to <a href="https://github.github.com/gfm/#tables-extension-">GFM Table Extension</a>.
     * @param text
     * @return
     *
     */
    private static String escapeForTableCell(String text) {
        // assume already contains the regular markdown escape sequences
        return text.replace("|", "\\|");
    }

    /**
     * Ensures that the {@link #writer} is currently at the beginning of a new line.
     * Optionally writes a line separator to ensure that.
     */
    private void ensureBeginningOfLine() {
        // make sure that we are at the start of a line without adding unnecessary blank lines
        if (!isWriterAtStartOfNewLine) {
            write(EOL);
        }
    }
}
