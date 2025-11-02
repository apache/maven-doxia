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
package org.apache.maven.doxia.module.apt;

import javax.swing.text.MutableAttributeSet;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.AbstractTextSink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.impl.SinkUtils;
import org.apache.maven.doxia.util.DoxiaStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * APT generator implementation.
 * <br>
 * <b>Note</b>: The encoding used is UTF-8.
 *
 * @author eredmond
 * @since 1.0
 */
public class AptSink extends AbstractTextSink implements AptMarkup {
    private static final Logger LOGGER = LoggerFactory.getLogger(AptSink.class);

    // ----------------------------------------------------------------------
    // Instance fields
    // ----------------------------------------------------------------------

    /**  A buffer that holds the current text when headerFlag or bufferFlag set to <code>true</code>. */
    private StringBuffer buffer;

    /**  A buffer that holds the table caption. */
    private StringBuilder tableCaptionBuffer;

    /**  authors. */
    private Collection<String> authors;

    /**  title. */
    private String title;

    /**  date. */
    private String date;

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

    /**  verbatim source. */
    private boolean isSource;

    /**  gridFlag for tables. */
    private boolean gridFlag;

    /**  number of cells in a table. */
    private int cellCount;

    /**  The writer to use. */
    private final PrintWriter writer;

    /**  justification of table cells. */
    private int[] cellJustif;

    /**  a line of a row in a table. */
    private String rowLine;

    /**  listNestingIndent. */
    private String listNestingIndent;

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
    protected AptSink(Writer writer) {
        this.writer = new PrintWriter(writer);
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

    /**
     * {@inheritDoc}
     */
    protected void init() {
        super.init();

        resetBuffer();

        this.tableCaptionBuffer = new StringBuilder();
        this.listNestingIndent = "";

        this.authors = new LinkedList<>();
        this.title = null;
        this.date = null;
        this.startFlag = true;
        this.tableCaptionFlag = false;
        this.tableCellFlag = false;
        this.headerFlag = false;
        this.bufferFlag = false;
        this.itemFlag = false;
        this.verbatimFlag = false;
        this.isSource = false;
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
    public void head(SinkEventAttributes attributes) {
        boolean startFlag = this.startFlag;

        init();

        headerFlag = true;
        this.startFlag = startFlag;
    }

    /**
     * {@inheritDoc}
     */
    public void head_() {
        headerFlag = false;

        write(HEADER_START_MARKUP + EOL);
        if (title != null) {
            write(" " + title + EOL);
        }
        write(HEADER_START_MARKUP + EOL);
        for (String author : authors) {
            write(" " + author + EOL);
        }
        write(HEADER_START_MARKUP + EOL);
        if (date != null) {
            write(" " + date + EOL);
        }
        write(HEADER_START_MARKUP + EOL);
    }

    /**
     * {@inheritDoc}
     */
    public void title_() {
        if (buffer.length() > 0) {
            title = buffer.toString();
            resetBuffer();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void author_() {
        if (buffer.length() > 0) {
            authors.add(buffer.toString());
            resetBuffer();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void date_() {
        if (buffer.length() > 0) {
            date = buffer.toString();
            resetBuffer();
        }
    }

    @Override
    public void section_(int level) {
        write(EOL);
    }

    @Override
    public void sectionTitle(int level, SinkEventAttributes attributes) {
        if (level > 5) {
            LOGGER.warn(
                    "{}Replacing unsupported section title level {} in APT with level 5",
                    getLocationLogPrefix(),
                    level);
            level = 5;
        }
        if (level == 1) {
            write(EOL);
        } else if (level > 1) {
            write(EOL + DoxiaStringUtils.repeat(SECTION_TITLE_START_MARKUP, level - 1));
        }
    }

    @Override
    public void sectionTitle_(int level) {
        if (level >= 1) {
            write(EOL + EOL);
        }
    }

    @Override
    public void list(SinkEventAttributes attributes) {
        listNestingIndent += " ";
        listStyles.push(LIST_START_MARKUP);
        write(EOL);
    }

    /**
     * {@inheritDoc}
     */
    public void list_() {
        if (listNestingIndent.length() <= 1) {
            write(EOL + listNestingIndent + LIST_END_MARKUP + EOL);
        } else {
            write(EOL);
        }
        listNestingIndent = DoxiaStringUtils.removeEnd(listNestingIndent, " ");
        listStyles.pop();
        itemFlag = false;
    }

    @Override
    public void listItem(SinkEventAttributes attributes) {
        // if (!numberedList)
        // write(EOL + listNestingIndent + "*");
        // else
        numberedListItem();
        itemFlag = true;
    }

    /**
     * {@inheritDoc}
     */
    public void listItem_() {
        write(EOL);
        itemFlag = false;
    }

    @Override
    public void numberedList(int numbering, SinkEventAttributes attributes) {
        listNestingIndent += " ";
        write(EOL);

        String style;
        switch (numbering) {
            case NUMBERING_UPPER_ALPHA:
                style = String.valueOf(NUMBERING_UPPER_ALPHA_CHAR);
                break;
            case NUMBERING_LOWER_ALPHA:
                style = String.valueOf(NUMBERING_LOWER_ALPHA_CHAR);
                break;
            case NUMBERING_UPPER_ROMAN:
                style = String.valueOf(NUMBERING_UPPER_ROMAN_CHAR);
                break;
            case NUMBERING_LOWER_ROMAN:
                style = String.valueOf(NUMBERING_LOWER_ROMAN_CHAR);
                break;
            case NUMBERING_DECIMAL:
            default:
                style = String.valueOf(NUMBERING);
        }

        listStyles.push(style);
    }

    /**
     * {@inheritDoc}
     */
    public void numberedList_() {
        if (listNestingIndent.length() <= 1) {
            write(EOL + listNestingIndent + LIST_END_MARKUP + EOL);
        } else {
            write(EOL);
        }
        listNestingIndent = DoxiaStringUtils.removeEnd(listNestingIndent, " ");
        listStyles.pop();
        itemFlag = false;
    }

    @Override
    public void numberedListItem(SinkEventAttributes attributes) {
        String style = listStyles.peek();
        if (style.equals(String.valueOf(STAR))) {
            write(EOL + listNestingIndent + STAR + SPACE);
        } else {
            write(EOL
                    + listNestingIndent
                    + LEFT_SQUARE_BRACKET
                    + LEFT_SQUARE_BRACKET
                    + style
                    + RIGHT_SQUARE_BRACKET
                    + RIGHT_SQUARE_BRACKET
                    + SPACE);
        }
        itemFlag = true;
    }

    /**
     * {@inheritDoc}
     */
    public void numberedListItem_() {
        write(EOL);
        itemFlag = false;
    }

    @Override
    public void definitionList(SinkEventAttributes attributes) {
        listNestingIndent += " ";
        listStyles.push("");
        write(EOL);
    }

    /**
     * {@inheritDoc}
     */
    public void definitionList_() {
        if (listNestingIndent.length() <= 1) {
            write(EOL + listNestingIndent + LIST_END_MARKUP + EOL);
        } else {
            write(EOL);
        }
        listNestingIndent = DoxiaStringUtils.removeEnd(listNestingIndent, " ");
        listStyles.pop();
        itemFlag = false;
    }

    @Override
    public void definedTerm(SinkEventAttributes attributes) {
        write(EOL + " [");
    }

    /**
     * {@inheritDoc}
     */
    public void definedTerm_() {
        write("] ");
    }

    @Override
    public void definition(SinkEventAttributes attributes) {
        itemFlag = true;
    }

    /**
     * {@inheritDoc}
     */
    public void definition_() {
        write(EOL);
        itemFlag = false;
    }

    /**
     * {@inheritDoc}
     */
    public void pageBreak() {
        write(EOL + PAGE_BREAK + EOL);
    }

    @Override
    public void paragraph(SinkEventAttributes attributes) {
        if (tableCellFlag) {
            // ignore paragraphs in table cells
        } else if (itemFlag) {
            write(EOL + EOL + "  " + listNestingIndent);
        } else {
            write(EOL + " ");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void paragraph_() {
        if (tableCellFlag) {
            // ignore paragraphs in table cells
        } else {
            write(EOL + EOL);
        }
    }

    @Override
    public void verbatim(SinkEventAttributes attributes) {
        MutableAttributeSet atts = SinkUtils.filterAttributes(attributes, SinkUtils.SINK_VERBATIM_ATTRIBUTES);

        boolean source = false;

        if (atts != null && atts.isDefined(SinkEventAttributes.DECORATION)) {
            source = "source"
                    .equals(atts.getAttribute(SinkEventAttributes.DECORATION).toString());
        }

        verbatimFlag = true;
        this.isSource = source;
        write(EOL);
        if (source) {
            write(EOL + VERBATIM_SOURCE_START_MARKUP + EOL);
        } else {
            write(EOL + VERBATIM_START_MARKUP + EOL);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void verbatim_() {
        if (isSource) {
            write(EOL + VERBATIM_SOURCE_END_MARKUP + EOL);
        } else {
            write(EOL + VERBATIM_END_MARKUP + EOL);
        }
        isSource = false;
        verbatimFlag = false;
    }

    @Override
    public void horizontalRule(SinkEventAttributes attributes) {
        write(EOL + HORIZONTAL_RULE_MARKUP + EOL);
    }

    @Override
    public void table(SinkEventAttributes attributes) {
        write(EOL);
    }

    /**
     * {@inheritDoc}
     */
    public void table_() {
        if (rowLine != null) {
            write(rowLine);
        }
        rowLine = null;

        if (tableCaptionBuffer.length() > 0) {
            text(tableCaptionBuffer.toString() + EOL);
        }

        resetTableCaptionBuffer();
    }

    @Override
    public void tableRows(int[] justification, boolean grid) {
        cellJustif = justification;
        gridFlag = grid;
    }

    /**
     * {@inheritDoc}
     */
    public void tableRows_() {
        cellJustif = null;
        gridFlag = false;
    }

    @Override
    public void tableRow(SinkEventAttributes attributes) {
        bufferFlag = true;
        cellCount = 0;
    }

    /**
     * {@inheritDoc}
     */
    public void tableRow_() {
        bufferFlag = false;

        // write out the header row first, then the data in the buffer
        buildRowLine();

        write(rowLine);

        // TODO: This will need to be more clever, for multi-line cells
        if (gridFlag) {
            write(TABLE_ROW_SEPARATOR_MARKUP);
        }

        write(buffer.toString());

        resetBuffer();

        write(EOL);

        // only reset cell count if this is the last row
        cellCount = 0;
    }

    /** Construct a table row. */
    private void buildRowLine() {
        StringBuilder rLine = new StringBuilder();
        rLine.append(TABLE_ROW_START_MARKUP);

        for (int i = 0; i < cellCount; i++) {
            if (cellJustif != null && i < cellJustif.length) {
                switch (cellJustif[i]) {
                    case Sink.JUSTIFY_LEFT:
                        rLine.append(TABLE_COL_LEFT_ALIGNED_MARKUP);
                        break;
                    case Sink.JUSTIFY_RIGHT:
                        rLine.append(TABLE_COL_RIGHT_ALIGNED_MARKUP);
                        break;
                    default:
                        // default = centered
                        rLine.append(TABLE_COL_CENTERED_ALIGNED_MARKUP);
                }
            } else {
                rLine.append(TABLE_COL_CENTERED_ALIGNED_MARKUP);
            }
        }
        rLine.append(EOL);

        this.rowLine = rLine.toString();
    }

    @Override
    public void tableCell(SinkEventAttributes attributes) {
        tableCell(false);
    }

    @Override
    public void tableHeaderCell(SinkEventAttributes attributes) {
        tableCell(true);
    }

    /**
     * Starts a table cell.
     *
     * @param headerRow If this cell is part of a header row.
     */
    public void tableCell(boolean headerRow) {
        if (headerRow) {
            buffer.append(TABLE_CELL_SEPARATOR_MARKUP);
        }
        tableCellFlag = true;
    }

    /**
     * {@inheritDoc}
     */
    public void tableCell_() {
        endTableCell();
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public void tableCaption_() {
        tableCaptionFlag = false;
    }

    /**
     * {@inheritDoc}
     */
    public void figureCaption_() {
        write(EOL);
    }

    @Override
    public void figureGraphics(String name, SinkEventAttributes attributes) {
        write(EOL + "[" + name + "] ");
    }

    @Override
    public void anchor(String name, SinkEventAttributes attributes) {
        write(ANCHOR_START_MARKUP);
    }

    /**
     * {@inheritDoc}
     */
    public void anchor_() {
        write(ANCHOR_END_MARKUP);
    }

    @Override
    public void link(String name, SinkEventAttributes attributes) {
        if (!headerFlag) {
            write(LINK_START_1_MARKUP);
            text(name.startsWith("#") ? name.substring(1) : name);
            write(LINK_START_2_MARKUP);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void link_() {
        if (!headerFlag) {
            write(LINK_END_MARKUP);
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
            text(target);
            write(LINK_START_2_MARKUP);
            text(name);
        }
    }

    /** {@inheritDoc} */
    public void inline(SinkEventAttributes attributes) {
        if (!headerFlag) {
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

    /**
     * {@inheritDoc}
     */
    public void inline_() {
        if (!headerFlag) {
            for (String tag : inlineStack.pop()) {
                write(tag);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void italic() {
        inline(SinkEventAttributeSet.Semantics.ITALIC);
    }

    /**
     * {@inheritDoc}
     */
    public void italic_() {
        inline_();
    }

    /**
     * {@inheritDoc}
     */
    public void bold() {
        inline(SinkEventAttributeSet.Semantics.BOLD);
    }

    /**
     * {@inheritDoc}
     */
    public void bold_() {
        inline_();
    }

    /**
     * {@inheritDoc}
     */
    public void monospaced() {
        inline(SinkEventAttributeSet.Semantics.CODE);
    }

    /**
     * {@inheritDoc}
     */
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
            write("\\" + EOL);
        }
    }

    /**
     * {@inheritDoc}
     */
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
            tableCaptionBuffer.append(text);
        } else if (headerFlag || bufferFlag) {
            buffer.append(text);
        } else if (verbatimFlag) {
            verbatimContent(text);
        } else {
            content(text);
        }
        if (attributes != null) {
            inline_();
        }
    }

    /** {@inheritDoc} */
    public void rawText(String text) {
        write(text);
    }

    /** {@inheritDoc} */
    public void comment(String comment) {
        comment(comment, false);
    }

    @Override
    public void comment(String comment, boolean endsWithLineBreak) {
        rawText("" + COMMENT + COMMENT + comment + EOL); // comments always end with a line break in APT
    }

    /**
     * {@inheritDoc}
     *
     * Unkown events just log a warning message but are ignored otherwise.
     * @see org.apache.maven.doxia.sink.Sink#unknown(String,Object[],SinkEventAttributes)
     */
    public void unknown(String name, Object[] requiredParams, SinkEventAttributes attributes) {
        LOGGER.warn("{}Unknown Sink event '{}', ignoring!", getLocationLogPrefix(), name);
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
            writer.write(unifyEOLs(text));
        }
    }

    /**
     * Write Apt escaped text to output.
     *
     * @param text The text to write.
     */
    protected void content(String text) {
        write(escapeAPT(text));
    }

    /**
     * Write Apt escaped text to output.
     *
     * @param text The text to write.
     */
    protected void verbatimContent(String text) {
        write(escapeAPT(text));
    }

    /**
     * {@inheritDoc}
     */
    public void flush() {
        writer.flush();
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        writer.close();

        init();
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * Escape special characters in a text in APT:
     *
     * <pre>
     * \~, \=, \-, \+, \*, \[, \], \<, \>, \{, \}, \\
     * </pre>
     *
     * @param text the String to escape, may be null
     * @return the text escaped, "" if null String input
     */
    static String escapeAPT(String text) {
        if (text == null) {
            return "";
        }

        int length = text.length();
        StringBuilder buffer = new StringBuilder(length);

        for (int i = 0; i < length; ++i) {
            char c = text.charAt(i);
            switch (c) { // 0080
                case '\\':
                case '~':
                case '=':
                case '-':
                case '+':
                case '*':
                case '[':
                case ']':
                case '<':
                case '>':
                case '{':
                case '}':
                    buffer.append('\\');
                    buffer.append(c);
                    break;
                default:
                    buffer.append(c);
            }
        }

        return buffer.toString();
    }
}
