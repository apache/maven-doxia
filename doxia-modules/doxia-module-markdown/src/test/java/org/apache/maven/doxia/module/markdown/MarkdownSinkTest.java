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
import java.io.Writer;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.AbstractSinkTest;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet.Semantics;
import org.apache.maven.doxia.sink.impl.SinkEventTestingSink;
import org.apache.maven.doxia.util.HtmlTools;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

/**
 * Test the <code>MarkdownSink</code> class
 */
public class MarkdownSinkTest extends AbstractSinkTest {
    @Inject
    protected MarkdownParser parser;

    /** {@inheritDoc} */
    protected String outputExtension() {
        return "md";
    }

    /** {@inheritDoc} */
    protected Sink createSink(Writer writer) {
        return new MarkdownSink(writer);
    }

    /** {@inheritDoc} */
    protected boolean isXmlSink() {
        return false;
    }

    /** {@inheritDoc} */
    protected String getTitleBlock(String title) {
        return title;
    }

    /** {@inheritDoc} */
    protected String getAuthorBlock(String author) {
        return getEscapedText(author);
    }

    /** {@inheritDoc} */
    protected String getDateBlock(String date) {
        return date;
    }

    /** {@inheritDoc} */
    protected String getHeadBlock() {
        return "";
    }

    /** {@inheritDoc} */
    protected String getBodyBlock() {
        return "";
    }

    /** {@inheritDoc} */
    protected String getArticleBlock() {
        return "";
    }

    /** {@inheritDoc} */
    protected String getNavigationBlock() {
        return "";
    }

    /** {@inheritDoc} */
    protected String getSidebarBlock() {
        return "";
    }

    /** {@inheritDoc} */
    protected String getSectionTitleBlock(String title) {
        return title;
    }

    protected String getSectionBlock(String title, int level) {
        return StringUtils.repeat(MarkdownMarkup.SECTION_TITLE_START_MARKUP, level) + SPACE + title + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection1Block(String title) {
        return getSectionBlock(title, 1);
    }

    /** {@inheritDoc} */
    protected String getSection2Block(String title) {
        return getSectionBlock(title, 2);
    }

    /** {@inheritDoc} */
    protected String getSection3Block(String title) {
        return getSectionBlock(title, 3);
    }

    /** {@inheritDoc} */
    protected String getSection4Block(String title) {
        return getSectionBlock(title, 4);
    }

    /** {@inheritDoc} */
    protected String getSection5Block(String title) {
        return getSectionBlock(title, 5);
    }

    /** {@inheritDoc} */
    protected String getSection6Block(String title) {
        return getSectionBlock(title, 6);
    }

    /** {@inheritDoc} */
    protected String getHeaderBlock() {
        return "";
    }

    /** {@inheritDoc} */
    protected String getContentBlock() {
        return "";
    }

    /** {@inheritDoc} */
    protected String getFooterBlock() {
        return "";
    }

    /** {@inheritDoc} */
    protected String getListBlock(String item) {
        return MarkdownMarkup.LIST_UNORDERED_ITEM_START_MARKUP + getEscapedText(item) + EOL;
    }

    /** {@inheritDoc} */
    protected String getNumberedListBlock(String item) {
        return MarkdownMarkup.LIST_ORDERED_ITEM_START_MARKUP + getEscapedText(item) + EOL;
    }

    /** {@inheritDoc} */
    protected String getDefinitionListBlock(String definum, String definition) {
        // don't reuse constants from compile classes to improve accuracy of tests
        return "<dl>" + EOL + "<dt>" + getEscapedText(definum) + "</dt>" + EOL + "<dd>" + getEscapedText(definition)
                + "</dd>" + EOL + "</dl>" + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getFigureBlock(String source, String caption) {
        return "![" + (caption != null ? getEscapedText(caption) : "") + "](" + getEscapedText(source) + ")";
    }

    /** {@inheritDoc} */
    protected String getTableBlock(String cell, String caption) {
        return MarkdownMarkup.TABLE_ROW_PREFIX + "   " + MarkdownMarkup.TABLE_CELL_SEPARATOR_MARKUP + EOL
                + MarkdownMarkup.TABLE_ROW_PREFIX
                + ":---:" + MarkdownMarkup.TABLE_CELL_SEPARATOR_MARKUP + EOL + MarkdownMarkup.TABLE_ROW_PREFIX
                + cell + MarkdownMarkup.TABLE_CELL_SEPARATOR_MARKUP + EOL;
    }

    @Override
    protected String getTableWithHeaderBlock(String... rowPrefixes) {
        StringBuilder expectedMarkup = new StringBuilder();
        expectedMarkup.append(MarkdownMarkup.TABLE_ROW_PREFIX + getEscapedText(rowPrefixes[0]) + "0|"
                + getEscapedText(rowPrefixes[0]) + "1|" + getEscapedText(rowPrefixes[0]) + "2|" + EOL);
        expectedMarkup.append(MarkdownMarkup.TABLE_ROW_PREFIX + "---|---:|:---:|" + EOL);
        for (int n = 1; n < rowPrefixes.length; n++) {
            expectedMarkup.append(MarkdownMarkup.TABLE_ROW_PREFIX + getEscapedText(rowPrefixes[n]) + "0|"
                    + getEscapedText(rowPrefixes[n]) + "1|" + getEscapedText(rowPrefixes[n]) + "2|" + EOL);
        }
        return expectedMarkup.toString();
    }

    /** {@inheritDoc} */
    protected String getParagraphBlock(String text) {
        return text + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getDataBlock(String value, String text) {
        return text;
    }

    /** {@inheritDoc} */
    protected String getTimeBlock(String datetime, String text) {
        return text;
    }

    /** {@inheritDoc} */
    protected String getAddressBlock(String text) {
        return text;
    }

    /** {@inheritDoc} */
    protected String getBlockquoteBlock(String text) {
        return "> " + text + EOL;
    }

    /** {@inheritDoc} */
    protected String getDivisionBlock(String text) {
        return text;
    }

    /** {@inheritDoc} */
    protected String getVerbatimSourceBlock(String text) {
        return MarkdownMarkup.VERBATIM_START_MARKUP + EOL + text + EOL + MarkdownMarkup.VERBATIM_END_MARKUP + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getHorizontalRuleBlock() {
        return MarkdownMarkup.HORIZONTAL_RULE_MARKUP + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getPageBreakBlock() {
        return "";
    }

    /** {@inheritDoc} */
    protected String getAnchorBlock(String anchor) {
        return anchor;
    }

    /** {@inheritDoc} */
    protected String getLinkBlock(String link, String text) {
        String lnk = link.startsWith("#") ? link.substring(1) : link;
        return MarkdownMarkup.LINK_START_1_MARKUP
                + text
                + MarkdownMarkup.LINK_START_2_MARKUP
                + lnk
                + MarkdownMarkup.LINK_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getInlineBlock(String text) {
        return text;
    }

    /** {@inheritDoc} */
    protected String getInlineItalicBlock(String text) {
        return MarkdownMarkup.ITALIC_START_MARKUP + text + MarkdownMarkup.ITALIC_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getInlineBoldBlock(String text) {
        return MarkdownMarkup.BOLD_START_MARKUP + text + MarkdownMarkup.BOLD_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getInlineCodeBlock(String text) {
        return MarkdownMarkup.MONOSPACED_START_MARKUP + text + MarkdownMarkup.MONOSPACED_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getItalicBlock(String text) {
        return MarkdownMarkup.ITALIC_START_MARKUP + text + MarkdownMarkup.ITALIC_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getBoldBlock(String text) {
        return MarkdownMarkup.BOLD_START_MARKUP + text + MarkdownMarkup.BOLD_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getMonospacedBlock(String text) {
        return text;
    }

    /** {@inheritDoc} */
    protected String getLineBreakBlock() {
        return "" + SPACE + SPACE + EOL;
    }

    /** {@inheritDoc} */
    protected String getLineBreakOpportunityBlock() {
        return "";
    }

    /** {@inheritDoc} */
    protected String getNonBreakingSpaceBlock() {
        return MarkdownMarkup.NON_BREAKING_SPACE_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getTextBlock(String text) {
        // this is only called once, therefore hard-code the expected result
        // return escaped format of "~,_=,_-,_+,_*,_[,_],_<,_>,_{,_},_\\";
        // i.e. XML entities for <>&"' and Markdown escape sequences for characters outlined in
        // https://daringfireball.net/projects/markdown/syntax#backslash
        return "~,\\_=,\\_\\-,\\_\\+,\\_\\*,\\_\\[,\\_\\],\\_&lt;,\\_&gt;,\\_\\{,\\_\\},\\_\\\\";
    }

    /** {@inheritDoc} */
    protected String getRawTextBlock(String text) {
        return text;
    }

    /**
     * Escapes special characters outlined in <a href="https://daringfireball.net/projects/markdown/syntax#backslash">Markdown Spec</a>
     * @param text
     * @return the text with all special characters escaped
     */
    private String getEscapedText(String text) {
        text = HtmlTools.escapeHTML(text, true);
        return text.replaceAll("\\\\|\\`|\\*|_|\\{|\\}|\\[|\\]|\\(|\\)|#|\\+|\\-|\\.|\\!", "\\\\$0");
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
    public void testMultipleAuthors() {
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
    public void testRoundtrip() throws IOException, ParseException {
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
    public void testLinksParagraphsAndStylesInTableCells() {
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
    public void testInlineCodeWithSpecialCharacters() {
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
    public void testNestedListWithBlockquotesParagraphsAndCode() {
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
    public void testHeadingAfterInlineElement() {
        try (final Sink sink = getSink()) {
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
    public void testCodeLink() {
        try (final Sink sink = getSink()) {
            sink.inline(Semantics.CODE);
            sink.link("http://example.com");
            sink.text("label");
            sink.link_();
            sink.inline_();
        }
        // heading must be on a new line
        String expected = "[`label`](http://example\\.com)";
        assertEquals(expected, getSinkContent(), "Wrong link on code!");
    }

    @Test
    public void testMultilineVerbatimSourceAfterListItem() {
        try (final Sink sink = getSink()) {
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

        String expected = "- Before" + EOL + EOL + MarkdownMarkup.INDENT + MarkdownMarkup.VERBATIM_START_MARKUP + EOL
                + MarkdownMarkup.INDENT + "codeline1" + EOL + MarkdownMarkup.INDENT + "codeline2" + EOL
                + MarkdownMarkup.INDENT + MarkdownMarkup.VERBATIM_END_MARKUP + EOL + EOL + "After" + EOL;
        assertEquals(expected, getSinkContent(), "Wrong verbatim!");
    }

    @Test
    public void testDefinitionListWithInlineStyles() {
        try (final Sink sink = getSink()) {
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
    public void testNestedListBeingTight() {
        try (final Sink sink = getSink()) {
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
