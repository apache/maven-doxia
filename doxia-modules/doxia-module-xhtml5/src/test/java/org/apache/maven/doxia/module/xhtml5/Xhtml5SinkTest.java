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
package org.apache.maven.doxia.module.xhtml5;

import java.io.StringWriter;
import java.io.Writer;

import org.apache.maven.doxia.markup.HtmlMarkup;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.AbstractSinkTest;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.util.HtmlTools;
import org.junit.jupiter.api.Test;

import static org.apache.maven.doxia.util.HtmlTools.escapeHTML;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Xhtml5SinkTest extends AbstractSinkTest {

    protected String outputExtension() {
        return "html";
    }

    protected Sink createSink(Writer writer) {
        return new Xhtml5Sink(writer, "UTF-8");
    }

    protected boolean isXmlSink() {
        return true;
    }

    /**
     * Test link generation.
     *
     */
    @Test
    void links() {
        Xhtml5Sink sink = null;
        Writer writer = new StringWriter();
        try {
            sink = (Xhtml5Sink) createSink(writer);
            sink.link("http:/www.xdoc.com");
            sink.link_();
            sink.link("./index.html#anchor");
            sink.link_();
            sink.link("../index.html#anchor");
            sink.link_();
            sink.link("index.html");
            sink.link_();
        } finally {
            if (sink != null) {
                sink.close();
            }
        }

        String actual = writer.toString();
        assertTrue(actual.contains("<a class=\"externalLink\" href=\"http:/www.xdoc.com\"></a>"));
        assertTrue(actual.contains("<a href=\"./index.html#anchor\"></a>"));
        assertTrue(actual.contains("<a href=\"../index.html#anchor\"></a>"));
        assertTrue(actual.contains("<a href=\"index.html\"></a>"));
    }

    protected String getTitleBlock(String title) {
        return "<title>" + title + "</title>";
    }

    protected String getAuthorBlock(String author) {
        return author;
    }

    protected String getDateBlock(String date) {
        return date;
    }

    protected String getHeadBlock() {
        return "<!DOCTYPE html\">"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n<head>\n<title></title>\n<meta charset=\"UTF-8\"/></head>";
    }

    protected String getBodyBlock() {
        return "<body></body></html>";
    }

    protected String getArticleBlock() {
        return "<article></article>";
    }

    protected String getNavigationBlock() {
        return "<nav></nav>";
    }

    protected String getSidebarBlock() {
        return "<aside></aside>";
    }

    protected String getSection1Block(String title) {
        return "<section><header>\n<h1>" + title + "</h1></header></section>";
    }

    protected String getSection2Block(String title) {
        return "<section><header>\n<h2>" + title + "</h2></header></section>";
    }

    protected String getSection3Block(String title) {
        return "<section><header>\n<h3>" + title + "</h3></header></section>";
    }

    protected String getSection4Block(String title) {
        return "<section><header>\n<h4>" + title + "</h4></header></section>";
    }

    protected String getSection5Block(String title) {
        return "<section><header>\n<h5>" + title + "</h5></header></section>";
    }

    protected String getSection6Block(String title) {
        return "<section><header>\n<h6>" + title + "</h6></header></section>";
    }

    protected String getHeaderBlock() {
        return "<header></header>";
    }

    protected String getContentBlock() {
        return "<main>" + EOL + "<div class=\"content\"></div></main>";
    }

    protected String getFooterBlock() {
        return "<footer></footer>";
    }

    protected String getListBlock(String item) {
        return "<ul>\n<li>" + item + "</li></ul>";
    }

    protected String getNumberedListBlock(String item) {
        return "<ol style=\"list-style-type: lower-roman;\">\n<li>" + item + "</li></ol>";
    }

    protected String getDefinitionListBlock(String definum, String definition) {
        return "<dl>\n<dt>" + definum + "</dt>\n<dd>" + definition + "</dd></dl>";
    }

    protected String getFigureBlock(String source, String caption) {
        String figureBlock = "<figure><img src=\"" + escapeHTML(source, true) + "\" />";
        if (caption != null) {
            figureBlock += "<figcaption>" + caption + "</figcaption>";
        }
        figureBlock += "</figure>";
        return figureBlock;
    }

    protected String getTableBlock(String cell, String caption) {
        return "<table class=\"bodyTable\">"
                + "<caption>" + caption + "</caption>\n"
                + "<tr class=\"a\">\n"
                + "<td style=\"text-align: center;\">cell</td>\n"
                + "<td>" + cell + "</td>\n"
                + "<td>" + cell + "</td>"
                + "</tr></table>";
    }

    @Override
    protected String getTableWithHeaderBlock(String... rowPrefixes) {
        return "<table class=\"bodyTable\">\n"
                + "<tr class=\"a\">\n"
                + "<th>" + rowPrefixes[0] + "0</th>\n"
                + "<th>" + rowPrefixes[0] + "1</th>\n"
                + "<th>" + rowPrefixes[0] + "2</th></tr>\n"
                + "<tr class=\"b\">\n"
                + "<td style=\"text-align: left;\">" + rowPrefixes[1] + "0</td>\n"
                + "<td style=\"text-align: right;\">" + rowPrefixes[1] + "1</td>\n"
                + "<td style=\"text-align: center;\">" + rowPrefixes[1] + "2</td></tr>\n"
                + "<tr class=\"a\">\n"
                + "<td style=\"text-align: left;\">" + rowPrefixes[2] + "0</td>\n"
                + "<td style=\"text-align: right;\">" + rowPrefixes[2] + "1</td>\n"
                + "<td style=\"text-align: center;\">" + rowPrefixes[2] + "2</td></tr>"
                + "</table>";
    }

    protected String getParagraphBlock(String text) {
        return "<p>" + text + "</p>";
    }

    protected String getDataBlock(String value, String text) {
        return "<data value=\"" + value + "\">" + text + "</data>";
    }

    protected String getTimeBlock(String datetime, String text) {
        return "<time datetime=\"" + datetime + "\">" + text + "</time>";
    }

    protected String getAddressBlock(String text) {
        return "<address>" + text + "</address>";
    }

    protected String getBlockquoteBlock(String text) {
        return "<blockquote>" + text + "</blockquote>";
    }

    protected String getDivisionBlock(String text) {
        return "<div>" + text + "</div>";
    }

    protected String getVerbatimBlock(String text) {
        return "<pre>" + text + "</pre>";
    }

    protected String getVerbatimSourceBlock(String text) {
        return "<pre><code>" + text + "</code></pre>";
    }

    protected String getHorizontalRuleBlock() {
        return "<hr />";
    }

    protected String getPageBreakBlock() {
        return "<!-- PB -->";
    }

    protected String getAnchorBlock(String anchor) {
        return "<a id=\"" + anchor + "\">" + anchor + "</a>";
    }

    protected String getLinkBlock(String link, String text) {
        return "<a href=\"" + link + "\">" + text + "</a>";
    }

    protected String getInlineBlock(String text) {
        return text;
    }

    protected String getInlineItalicBlock(String text) {
        return "<i>" + text + "</i>";
    }

    protected String getInlineBoldBlock(String text) {
        return "<b>" + text + "</b>";
    }

    protected String getInlineCodeBlock(String text) {
        return "<code>" + text + "</code>";
    }

    protected String getInlineDeleteBlock(String text) {
        return "<del>" + text + "</del>";
    }

    protected String getLineBreakBlock() {
        return "<br />";
    }

    protected String getLineBreakOpportunityBlock() {
        return "<wbr />";
    }

    protected String getNonBreakingSpaceBlock() {
        return "&#160;";
    }

    protected String getTextBlock(String text) {
        return HtmlTools.escapeHTML(text, false);
    }

    protected String getRawTextBlock(String text) {
        return text;
    }

    /**
     * Test entities is section titles and paragraphs.
     */
    @Test
    void entities() {
        Xhtml5Sink sink = null;
        Writer writer = new StringWriter();

        try {
            sink = new Xhtml5Sink(writer);
            sink.section(Sink.SECTION_LEVEL_1, null);
            sink.header();
            sink.sectionTitle(Sink.SECTION_LEVEL_1, null);
            sink.text("&", null);
            sink.sectionTitle_(Sink.SECTION_LEVEL_1);
            sink.header_();
            sink.paragraph(null);
            sink.text("&", null);
            sink.paragraph_();
            sink.section_(Sink.SECTION_LEVEL_1);
        } finally {
            sink.close();
        }

        assertEquals("<section><header>\n<h1>&amp;</h1></header>\n<p>&amp;</p></section>", writer.toString());
    }

    /**
     * Test head events.
     */
    @Test
    public void head() {
        Xhtml5Sink sink = null;
        Writer writer = new StringWriter();

        try {
            sink = new Xhtml5Sink(writer);
            sink.head();
            sink.title();
            sink.text("Title");
            sink.title_();
            sink.comment("A comment");
            sink.author();
            // note: this is really illegal, there should be no un-resolved entities emitted into text()
            sink.text("&#x123;&");
            sink.author_();
            SinkEventAttributeSet atts = new SinkEventAttributeSet(1);
            atts.addAttribute("href", "http://maven.apache.org/");
            sink.unknown("base", new Object[] {HtmlMarkup.TAG_TYPE_SIMPLE}, atts);
            sink.head_();
        } finally {
            sink.close();
        }

        String expected =
                "<head>\n<title>Title</title><!--A comment--><meta name=\"author\" content=\"&#x123;&amp;\" />"
                        + "<base href=\"http://maven.apache.org/\" /></head>";
        String actual = writer.toString();
        assertTrue(actual.contains(expected), actual);
    }

    @Override
    protected String getCommentBlock(String text) {
        return "<!--" + toXmlComment(text) + "-->";
    }

    @Override
    protected String getCommentBlockFollowedByParagraph(String comment, String paragraph) {
        return getCommentBlock(comment) + getParagraphBlock(paragraph); // no line break in between
    }
}
