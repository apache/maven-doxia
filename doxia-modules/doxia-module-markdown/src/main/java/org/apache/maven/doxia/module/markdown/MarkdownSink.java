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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.AbstractTextSink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.codehaus.plexus.util.StringUtils;
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

    /**  A buffer that holds the current text when headerFlag or bufferFlag set to <code>true</code>. */
    private StringBuffer buffer;

    /**  A buffer that holds the table caption. */
    private StringBuilder tableCaptionBuffer;

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

    /**  tableCellFlag. */
    private boolean tableCellFlag;

    /**  headerFlag. */
    private boolean headerFlag;

    /**  bufferFlag. */
    private boolean bufferFlag;

    /**  itemFlag. */
    private boolean itemFlag;

    /**  verbatimFlag. */
    private boolean verbatimFlag;

    /**  gridFlag for tables. */
    private boolean gridFlag;

    /**  number of cells in a table. */
    private int cellCount;

    /**  The writer to use. */
    private final PrintWriter writer;

    /** {@code true} when last written character in {@link #writer} was a line separator, or writer is still at the beginning */
    private boolean isWriterAtStartOfNewLine;

    /**  justification of table cells. */
    private int[] cellJustif;

    /**  a line of a row in a table. */
    private String rowLine;

    /**  is header row */
    private boolean headerRow;

    /**  listNestingLevel, 0 outside the list, 1 for the top-level list, 2 for a nested list, 3 for a list nested inside a nested list, .... */
    private int listNestingLevel;

    /**  listStyles. */
    private final Stack<String> listStyles;

    /** Keep track of the closing tags for inline events. */
    protected Stack<List<String>> inlineStack = new Stack<>();

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

        this.tableCaptionBuffer = new StringBuilder();
        this.listNestingLevel = 0;

        this.authors = new LinkedList<>();
        this.title = null;
        this.date = null;
        this.linkName = null;
        this.startFlag = true;
        this.tableCaptionFlag = false;
        this.tableCellFlag = false;
        this.headerFlag = false;
        this.bufferFlag = false;
        this.itemFlag = false;
        this.verbatimFlag = false;
        this.gridFlag = false;
        this.cellCount = 0;
        this.cellJustif = null;
        this.rowLine = null;
        this.listStyles.clear();
        this.inlineStack.clear();
    }

    /**
     * Reset the StringBuilder.
     */
    protected void resetBuffer() {
        buffer = new StringBuffer();
    }

    /**
     * Reset the TableCaptionBuffer.
     */
    protected void resetTableCaptionBuffer() {
        tableCaptionBuffer = new StringBuilder();
    }

    @Override
    public void head() {
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

    private void sectionTitle(int level) {
        write(StringUtils.repeat(SECTION_TITLE_START_MARKUP, level) + SPACE);
    }

    @Override
    public void sectionTitle1() {
        sectionTitle(1);
    }

    @Override
    public void sectionTitle1_() {
        write(BLANK_LINE);
    }

    @Override
    public void sectionTitle2() {
        sectionTitle(2);
    }

    @Override
    public void sectionTitle2_() {
        write(BLANK_LINE);
    }

    @Override
    public void sectionTitle3() {
        sectionTitle(3);
    }

    @Override
    public void sectionTitle3_() {
        write(BLANK_LINE);
    }

    @Override
    public void sectionTitle4() {
        sectionTitle(4);
    }

    @Override
    public void sectionTitle4_() {
        write(BLANK_LINE);
    }

    @Override
    public void sectionTitle5() {
        sectionTitle(5);
    }

    @Override
    public void sectionTitle5_() {
        write(BLANK_LINE);
    }

    @Override
    public void list() {
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
        itemFlag = false;
    }

    @Override
    public void listItem() {
        orderedOrUnorderedListItem();
    }

    @Override
    public void listItem_() {
        orderedOrUnorderedListItem_();
    }

    /** {@inheritDoc} */
    public void numberedList(int numbering) {
        listNestingLevel++;
        // markdown only supports decimal numbering
        if (numbering != NUMBERING_DECIMAL) {
            LOGGER.warn(
                    "Markdown only supports numbered item with decimal style ({}) but requested was style {}, falling back to decimal style",
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
        itemFlag = false;
    }

    @Override
    public void numberedListItem() {
        orderedOrUnorderedListItem();
    }

    @Override
    public void numberedListItem_() {
        orderedOrUnorderedListItem_();
    }

    private void orderedOrUnorderedListItem() {
        write(getListPrefix());
        itemFlag = true;
    }

    private void orderedOrUnorderedListItem_() {
        ensureBeginningOfLine();
        itemFlag = false;
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
    public void definitionList() {
        LOGGER.warn("Definition list not natively supported in Markdown, rendering HTML instead");
        write("<dl>" + EOL);
    }

    public void definitionList_() {
        verbatimFlag = true;
        write("</dl>" + BLANK_LINE);
    }

    public void definedTerm() {
        write("<dt>");
        verbatimFlag = false;
    }

    @Override
    public void definedTerm_() {
        write("</dt>" + EOL);
    }

    @Override
    public void definition() {
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
    public void paragraph() {
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

    /** {@inheritDoc} */
    @Override
    public void verbatim() {
        verbatim(null);
    }

    /** {@inheritDoc} */
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
    public void horizontalRule() {
        ensureBeginningOfLine();
        write(HORIZONTAL_RULE_MARKUP + BLANK_LINE);
    }

    @Override
    public void table() {
        ensureBeginningOfLine();
    }

    @Override
    public void table_() {
        if (tableCaptionBuffer.length() > 0) {
            text(tableCaptionBuffer.toString() + EOL);
        }

        resetTableCaptionBuffer();
    }

    /** {@inheritDoc} */
    public void tableRows(int[] justification, boolean grid) {
        cellJustif = null; // justification;
        gridFlag = grid;
        headerRow = true;
    }

    @Override
    public void tableRows_() {
        cellJustif = null;
        gridFlag = false;
    }

    @Override
    public void tableRow() {
        bufferFlag = true;
        cellCount = 0;
    }

    @Override
    public void tableRow_() {
        bufferFlag = false;

        // write out the header row first, then the data in the buffer
        buildRowLine();

        write(TABLE_ROW_SEPARATOR_MARKUP);

        write(buffer.toString());

        resetBuffer();

        write(EOL);

        if (headerRow) {
            write(rowLine);
            headerRow = false;
        }

        // only reset cell count if this is the last row
        cellCount = 0;
    }

    /** Construct a table row. */
    private void buildRowLine() {
        StringBuilder rLine = new StringBuilder(TABLE_ROW_SEPARATOR_MARKUP);

        for (int i = 0; i < cellCount; i++) {
            if (cellJustif != null) {
                switch (cellJustif[i]) {
                    case 1:
                        rLine.append(TABLE_COL_LEFT_ALIGNED_MARKUP);
                        break;
                    case 2:
                        rLine.append(TABLE_COL_RIGHT_ALIGNED_MARKUP);
                        break;
                    default:
                        rLine.append(TABLE_COL_DEFAULT_ALIGNED_MARKUP);
                }
            } else {
                rLine.append(TABLE_COL_DEFAULT_ALIGNED_MARKUP);
            }
        }
        rLine.append(EOL);

        this.rowLine = rLine.toString();
    }

    @Override
    public void tableCell() {
        tableCell(false);
    }

    @Override
    public void tableHeaderCell() {
        tableCell(true);
    }

    /**
     * Starts a table cell.
     *
     * @param headerRow If this cell is part of a header row.
     */
    public void tableCell(boolean headerRow) {
        tableCellFlag = true;
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
    public void tableCaption() {
        tableCaptionFlag = true;
    }

    @Override
    public void tableCaption_() {
        tableCaptionFlag = false;
    }

    @Override
    public void figureCaption_() {
        write(EOL);
    }

    /** {@inheritDoc} */
    public void figureGraphics(String name) {
        write("<img src=\"" + name + "\" />");
    }

    /** {@inheritDoc} */
    public void anchor(String name) {
        // write(ANCHOR_START_MARKUP + name);
        // TODO get implementation from Xhtml5 base sink
    }

    @Override
    public void anchor_() {
        // write(ANCHOR_END_MARKUP);
    }

    /** {@inheritDoc} */
    public void link(String name) {
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
    public void link(String name, String target) {
        if (!headerFlag) {
            write(LINK_START_1_MARKUP);
        }
    }

    @Override
    public void inline() {
        inline(null);
    }

    /** {@inheritDoc} */
    public void inline(SinkEventAttributes attributes) {
        if (!headerFlag && !verbatimFlag) {
            List<String> tags = new ArrayList<>();

            if (attributes != null) {

                if (attributes.containsAttribute(SinkEventAttributes.SEMANTICS, "italic")) {
                    write(ITALIC_START_MARKUP);
                    tags.add(0, ITALIC_END_MARKUP);
                }

                if (attributes.containsAttribute(SinkEventAttributes.SEMANTICS, "bold")) {
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
    public void lineBreak() {
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
    public void text(String text) {
        if (tableCaptionFlag) {
            tableCaptionBuffer.append(text);
        } else if (headerFlag || bufferFlag) {
            buffer.append(text);
        } else if (verbatimFlag) {
            verbatimContent(text);
        } else {
            content(text);
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
        LOGGER.warn("Unknown Sink event '" + name + "', ignoring!");
    }

    /**
     * Write text to output.
     *
     * @param text The text to write.
     */
    protected void write(String text) {
        startFlag = false;
        if (tableCellFlag) {
            buffer.append(text);
        } else {
            String unifiedText = unifyEOLs(text);
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
