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

import javax.inject.Inject;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.AbstractSinkTest;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet.Semantics;
import org.apache.maven.doxia.sink.impl.SinkEventTestingSink;
import org.apache.maven.doxia.util.DoxiaStringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

/**
 * Test the <code>MarkdownSink</code> class
 */
class MarkdownSinkTest extends AbstractSinkTest {
    @Inject
    protected MarkdownParser parser;

    protected String outputExtension() {
        return "md";
    }

    protected Sink createSink(Writer writer) {
        return new MarkdownSink(writer);
    }

    protected boolean isXmlSink() {
        return false;
    }

    protected String getTitleBlock(String title) {
        return title;
    }

    protected String getAuthorBlock(String author) {
        return getEscapedText(author);
    }

    protected String getDateBlock(String date) {
        return date;
    }

    protected String getHeadBlock() {
        return "";
    }

    protected String getBodyBlock() {
        return "";
    }

    protected String getArticleBlock() {
        return "";
    }

    protected String getNavigationBlock() {
        return "";
    }

    protected String getSidebarBlock() {
        return "";
    }

    protected String getSectionBlock(String title, int level) {
        return DoxiaStringUtils.repeat(MarkdownMarkup.SECTION_TITLE_START_MARKUP, level) + SPACE + title + EOL + EOL;
    }

    protected String getSection1Block(String title) {
        return getSectionBlock(title, 1);
    }

    protected String getSection2Block(String title) {
        return getSectionBlock(title, 2);
    }

    protected String getSection3Block(String title) {
        return getSectionBlock(title, 3);
    }

    protected String getSection4Block(String title) {
        return getSectionBlock(title, 4);
    }

    protected String getSection5Block(String title) {
        return getSectionBlock(title, 5);
    }

    protected String getSection6Block(String title) {
        return getSectionBlock(title, 6);
    }

    protected String getHeaderBlock() {
        return "";
    }

    protected String getContentBlock() {
        return "";
    }

    protected String getFooterBlock() {
        return "";
    }

    protected String getListBlock(String item) {
        return MarkdownMarkup.LIST_UNORDERED_ITEM_START_MARKUP + getEscapedText(item) + EOL;
    }

    protected String getNumberedListBlock(String item) {
        return MarkdownMarkup.LIST_ORDERED_ITEM_START_MARKUP + getEscapedText(item) + EOL;
    }

    protected String getDefinitionListBlock(String definum, String definition) {
        // don't reuse constants from compile classes to improve accuracy of tests
        return "<dl>" + EOL + "<dt>" + getEscapedText(definum) + "</dt>" + EOL + "<dd>" + getEscapedText(definition)
                + "</dd>" + EOL + "</dl>" + EOL + EOL;
    }

    protected String getFigureBlock(String source, String caption) {
        return "![" + (caption != null ? getEscapedText(caption) : "") + "](" + source + ")";
    }

    protected String getTableBlock(String cell, String caption) {
        return MarkdownMarkup.TABLE_ROW_PREFIX + "   " + MarkdownMarkup.TABLE_CELL_SEPARATOR_MARKUP + "   "
                + MarkdownMarkup.TABLE_CELL_SEPARATOR_MARKUP + "   " + MarkdownMarkup.TABLE_CELL_SEPARATOR_MARKUP + EOL
                + MarkdownMarkup.TABLE_ROW_PREFIX
                + ":---:" + MarkdownMarkup.TABLE_CELL_SEPARATOR_MARKUP + "---"
                + MarkdownMarkup.TABLE_CELL_SEPARATOR_MARKUP + "---" + MarkdownMarkup.TABLE_CELL_SEPARATOR_MARKUP + EOL
                + MarkdownMarkup.TABLE_ROW_PREFIX
                + cell + MarkdownMarkup.TABLE_CELL_SEPARATOR_MARKUP + cell + MarkdownMarkup.TABLE_CELL_SEPARATOR_MARKUP
                + cell + MarkdownMarkup.TABLE_CELL_SEPARATOR_MARKUP + EOL;
    }

    @Override
    protected String getTableWithHeaderBlock(String... rowPrefixes) {
        StringBuilder expectedMarkup = new StringBuilder();
        expectedMarkup
                .append(MarkdownMarkup.TABLE_ROW_PREFIX)
                .append(getEscapedText(rowPrefixes[0]))
                .append("0|")
                .append(getEscapedText(rowPrefixes[0]))
                .append("1|")
                .append(getEscapedText(rowPrefixes[0]))
                .append("2|")
                .append(EOL);
        expectedMarkup
                .append(MarkdownMarkup.TABLE_ROW_PREFIX)
                .append(":---|---:|:---:|")
                .append(EOL);
        for (int n = 1; n < rowPrefixes.length; n++) {
            expectedMarkup
                    .append(MarkdownMarkup.TABLE_ROW_PREFIX)
                    .append(getEscapedText(rowPrefixes[n]))
                    .append("0|")
                    .append(getEscapedText(rowPrefixes[n]))
                    .append("1|")
                    .append(getEscapedText(rowPrefixes[n]))
                    .append("2|")
                    .append(EOL);
        }
        return expectedMarkup.toString();
    }

    protected String getParagraphBlock(String text) {
        return text + EOL + EOL;
    }

    protected String getDataBlock(String value, String text) {
        return text;
    }

    protected String getTimeBlock(String datetime, String text) {
        return text;
    }

    protected String getAddressBlock(String text) {
        return text;
    }

    protected String getBlockquoteBlock(String text) {
        return "> " + text + EOL;
    }

    protected String getDivisionBlock(String text) {
        return text;
    }

    protected String getVerbatimBlock(String text) {
        return MarkdownMarkup.VERBATIM_START_MARKUP + EOL + text + EOL + MarkdownMarkup.VERBATIM_END_MARKUP + EOL + EOL;
    }

    protected String getVerbatimSourceBlock(String text) {
        return MarkdownMarkup.VERBATIM_START_MARKUP + "unknown" + EOL + text + EOL + MarkdownMarkup.VERBATIM_END_MARKUP
                + EOL + EOL;
    }

    protected String getHorizontalRuleBlock() {
        return MarkdownMarkup.HORIZONTAL_RULE_MARKUP + EOL + EOL;
    }

    protected String getPageBreakBlock() {
        return "";
    }

    protected String getAnchorBlock(String anchor) {
        return "<a id=\"" + anchor + "\"></a>" + anchor;
    }

    protected String getLinkBlock(String link, String text) {
        return MarkdownMarkup.LINK_START_1_MARKUP
                + text
                + MarkdownMarkup.LINK_START_2_MARKUP
                + link
                + MarkdownMarkup.LINK_END_MARKUP;
    }

    protected String getInlineBlock(String text) {
        return text;
    }

    protected String getInlineItalicBlock(String text) {
        return MarkdownMarkup.ITALIC_START_MARKUP + text + MarkdownMarkup.ITALIC_END_MARKUP;
    }

    protected String getInlineBoldBlock(String text) {
        return MarkdownMarkup.BOLD_START_MARKUP + text + MarkdownMarkup.BOLD_END_MARKUP;
    }

    protected String getInlineCodeBlock(String text) {
        return MarkdownMarkup.MONOSPACED_START_MARKUP + text + MarkdownMarkup.MONOSPACED_END_MARKUP;
    }

    protected String getItalicBlock(String text) {
        return MarkdownMarkup.ITALIC_START_MARKUP + text + MarkdownMarkup.ITALIC_END_MARKUP;
    }

    protected String getBoldBlock(String text) {
        return MarkdownMarkup.BOLD_START_MARKUP + text + MarkdownMarkup.BOLD_END_MARKUP;
    }

    protected String getMonospacedBlock(String text) {
        return text;
    }

    protected String getLineBreakBlock() {
        return "" + SPACE + SPACE + EOL;
    }

    protected String getLineBreakOpportunityBlock() {
        return "";
    }

    protected String getNonBreakingSpaceBlock() {
        return MarkdownMarkup.NON_BREAKING_SPACE_MARKUP;
    }

    protected String getTextBlock(String text) {
        if (text.equals("~,_=,_-,_+,_*,_[,_],_<,_>,_{,_},_\\")) {
            // i.e. XML entities for <>&"' and Markdown escape sequences for characters outlined in
            // https://daringfireball.net/projects/markdown/syntax#backslash
            return "~,\\_=,\\_-,\\_+,\\_*,\\_\\[,\\_\\],\\_&lt;,\\_&gt;,\\_{,\\_},\\_\\\\";
        } else {
            // assume BODY context everywhere else
            return getEscapedText(text);
        }
    }

    protected String getRawTextBlock(String text) {
        return text;
    }

    /**
     * Escapes as if in the BODY context and the writer is at the start of a new line.
     * @param text
     * @return the text with all special characters escaped
     */
    private String getEscapedText(String text) {
        return MarkdownSink.ElementContext.BODY.escape(new LastTwoLinesBufferingWriter(new StringWriter()), text);
    }

    @Override
    protected String getCommentBlock(String text) {
        return "<!--" + toXmlComment(text) + "-->";
    }

    @Override
    protected String getCommentBlockFollowedByParagraph(String comment, String paragraph) {
        return getCommentBlock(comment) + EOL + EOL + getParagraphBlock(paragraph); // paragraph separated by blank line
    }

    @Test
    void multipleAuthors() {
        final Sink sink = getSink();
        sink.head();
        sink.author();
        sink.text("first author");
        sink.author_();
        sink.author();
        sink.text("second author");
        sink.author_();
        sink.head_();
        sink.flush();
        sink.close();

        String expected = MarkdownMarkup.METADATA_MARKUP + EOL
                + "author: " + EOL
                + "  - first author" + EOL
                + "  - second author" + EOL
                + MarkdownMarkup.METADATA_MARKUP + EOL + EOL;

        assertEquals(expected, getSinkContent(), "Wrong metadata section");
    }

    /** Test MD -> SETSink vs. Test MD -> MDSink -> SETSink */
    @Test
    void roundtrip() throws Exception {
        parseFile(parser, "test", getSink());

        final SinkEventTestingSink regeneratedSink = new SinkEventTestingSink();
        try (Reader reader = new StringReader(getSinkContent())) {
            parser.parse(reader, regeneratedSink);
        }

        final SinkEventTestingSink originalSink = new SinkEventTestingSink();
        parseFile(parser, "test", originalSink);
        // strip empty lines from sink content

        // compare sink events from parsing original markdown with sink events from re-generated markdown
        try {
            assertIterableEquals(originalSink.getEventList(), regeneratedSink.getEventList());
        } catch (AssertionError e) {
            // emit generated markdown to ease debugging
            System.err.println(getSinkContent());
            throw e;
        }
    }

    private void parseFile(Parser parser, String file, Sink sink) throws ParseException, IOException {
        try (Reader reader = getTestReader(file)) {
            parser.parse(reader, sink);
        }
    }

    @Test
    void linksParagraphsAndStylesInTableCells() {
        final String linkTarget = "target";
        final String linkText = "link";
        final String paragraphText = "paragraph text with |";
        final Sink sink = getSink();
        sink.table();
        sink.tableRows();
        sink.tableRow();
        sink.tableCell();
        sink.link(linkTarget);
        sink.text(linkText);
        sink.link_();
        sink.tableCell_();
        sink.tableCell();
        sink.paragraph();
        sink.text(paragraphText);
        sink.bold();
        sink.text("bold");
        sink.bold_();
        sink.paragraph_();
        sink.tableCell_();
        sink.tableRow_();
        sink.tableRows_();
        sink.table_();
        sink.flush();
        sink.close();

        String expected =
                "|   |   |" + EOL + "|---|---|" + EOL + "|[link](target)|paragraph text with \\|**bold**|" + EOL;

        assertEquals(expected, getSinkContent(), "Wrong link or paragraph markup in table cell");
    }

    @Test
    void inlineCodeWithSpecialCharacters() {
        String text = "Test&<>*_";
        final Sink sink = getSink();
        sink.inline(SinkEventAttributeSet.Semantics.CODE);
        sink.text(text);
        sink.inline_();
        sink.flush();
        sink.close();

        String expected = "`" + text + "`";

        assertEquals(expected, getSinkContent(), "Wrong inline code!");
    }

    @Test
    void nestedListWithBlockquotesParagraphsAndCode() {
        final Sink sink = getSink();
        sink.list();

        sink.listItem();
        sink.text("item1");
        sink.list();
        sink.listItem();
        sink.text("item1a");
        sink.listItem_();
        sink.list_();
        sink.listItem_();

        sink.listItem();
        sink.blockquote();
        sink.text("blockquote");
        sink.blockquote_();
        sink.listItem_();

        sink.listItem();
        sink.text("item3");
        sink.paragraph();
        sink.text("item3paragraph2");
        sink.paragraph_();
        sink.listItem_();

        sink.listItem();
        sink.text("item4");
        sink.verbatim();
        sink.text("item4verbatim");
        sink.lineBreak();
        sink.text("item4verbatimline2");
        sink.verbatim_();
        sink.listItem_();

        sink.list_();
        sink.flush();
        sink.close();

        String expected = "- item1" + EOL
                + "    - item1a" + EOL
                + "- " + EOL + EOL
                + "    > blockquote" + EOL + EOL
                + "- item3" + EOL
                + EOL
                + "    item3paragraph2" + EOL
                + EOL
                + "- item4" + EOL + EOL
                + "    ```" + EOL
                + "    item4verbatim" + EOL
                + "    item4verbatimline2" + EOL
                + "    ```" + EOL
                + EOL;

        assertEquals(expected, getSinkContent(), "Wrong inline code!");
    }

    @Test
    void headingAfterInlineElement() {
        try (Sink sink = getSink()) {
            sink.text("Text");
            sink.section1();
            sink.sectionTitle1();
            sink.text("Section1");
            sink.sectionTitle1_();
            sink.section1_();
        }
        // heading must be on a new line
        String expected = "Text" + EOL + "# Section1" + EOL + EOL;
        assertEquals(expected, getSinkContent(), "Wrong heading after inline element!");
    }

    @Test
    void codeLink() {
        try (Sink sink = getSink()) {
            sink.inline(Semantics.CODE);
            sink.link("http://example.com");
            sink.text("label");
            sink.link_();
            sink.inline_();
        }
        // heading must be on a new line
        String expected = "[`label`](http://example.com)";
        assertEquals(expected, getSinkContent(), "Wrong link on code!");
    }

    @Test
    void multilineVerbatimSourceAfterListItem() {
        try (Sink sink = getSink()) {
            sink.list();
            sink.listItem();
            sink.text("Before");
            sink.verbatim(SinkEventAttributeSet.SOURCE);
            sink.text("codeline1" + EOL + "codeline2");
            sink.verbatim_();
            sink.text("After");
            sink.listItem_();
            sink.list_();
        }

        String expected =
                "- Before" + EOL + EOL + MarkdownMarkup.INDENT + MarkdownMarkup.VERBATIM_START_MARKUP + "unknown" + EOL
                        + MarkdownMarkup.INDENT + "codeline1" + EOL + MarkdownMarkup.INDENT + "codeline2" + EOL
                        + MarkdownMarkup.INDENT + MarkdownMarkup.VERBATIM_END_MARKUP + EOL + EOL + "After" + EOL;
        assertEquals(expected, getSinkContent(), "Wrong verbatim!");
    }

    @Test
    void definitionListWithInlineStyles() {
        try (Sink sink = getSink()) {
            sink.definitionList();
            sink.definedTerm();
            sink.text("term");
            sink.definedTerm_();
            sink.definition();
            sink.text("prefix ");
            sink.text("code", SinkEventAttributeSet.Semantics.CODE);
            sink.text("suffix<a>test</a>", SinkEventAttributeSet.Semantics.EMPHASIS);
            sink.definition_();
            sink.definitionList_();
        }
        String expected = "<dl>" + EOL + "<dt>term</dt>" + EOL
                + "<dd>prefix <code>code</code><em>suffix&lt;a&gt;test&lt;/a&gt;</em></dd>" + EOL + "</dl>" + EOL + EOL;
        assertEquals(expected, getSinkContent(), "Wrong heading after inline element!");
    }

    @Test
    void nestedListBeingTight() {
        try (Sink sink = getSink()) {
            sink.list();
            sink.listItem();
            sink.text("item 1");
            sink.listItem_();
            sink.listItem();
            sink.text("item 2");
            sink.list();
            sink.listItem();
            sink.text("item 2 a");
            sink.listItem_();
            sink.listItem();
            sink.text("item 2 b");
            sink.listItem_();
            sink.list_();
            sink.listItem_();
            sink.list_();
            sink.listItem();
            sink.text("item 3");
            sink.listItem_();
            sink.list();
        }
        String expected = "- item 1" + EOL
                + "- item 2" + EOL
                + "    - item 2 a" + EOL
                + "    - item 2 b" + EOL
                + "- item 3" + EOL;
        assertEquals(expected, getSinkContent());
    }
}
