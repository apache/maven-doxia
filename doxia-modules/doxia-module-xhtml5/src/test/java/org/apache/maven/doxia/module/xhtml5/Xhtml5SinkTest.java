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
import org.junit.jupiter.api.Test;

import static org.apache.maven.doxia.util.HtmlTools.escapeHTML;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Xhtml5SinkTest extends AbstractSinkTest {
    /** {@inheritDoc} */
    protected String outputExtension() {
        return "html";
    }

    /** {@inheritDoc} */
    protected Sink createSink(Writer writer) {
        return new Xhtml5Sink(writer, "UTF-8");
    }

    /** {@inheritDoc} */
    protected boolean isXmlSink() {
        return true;
    }

    /**
     * Test link generation.
     *
     */
    @Test
    public void testLinks() {
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

    /** {@inheritDoc} */
    protected String getTitleBlock(String title) {
        return "<title>" + title + "</title>";
    }

    /** {@inheritDoc} */
    protected String getAuthorBlock(String author) {
        return author;
    }

    /** {@inheritDoc} */
    protected String getDateBlock(String date) {
        return date;
    }

    /** {@inheritDoc} */
    protected String getHeadBlock() {
        return "<!DOCTYPE html\">" + "<html xmlns=\"http://www.w3.org/1999/xhtml\">" + EOL + "<head>" + EOL
                + "<title></title>" + EOL + "<meta charset=\"UTF-8\"/></head>";
    }

    /** {@inheritDoc} */
    protected String getBodyBlock() {
        return "<body></body></html>";
    }

    /** {@inheritDoc} */
    protected String getArticleBlock() {
        return "<article></article>";
    }

    /** {@inheritDoc} */
    protected String getNavigationBlock() {
        return "<nav></nav>";
    }

    /** {@inheritDoc} */
    protected String getSidebarBlock() {
        return "<aside></aside>";
    }

    /** {@inheritDoc} */
    protected String getSectionTitleBlock(String title) {
        return title;
    }

    /** {@inheritDoc} */
    protected String getSection1Block(String title) {
        return "<section><header>" + EOL + "<h1>" + title + "</h1></header></section>";
    }

    /** {@inheritDoc} */
    protected String getSection2Block(String title) {
        return "<section><header>" + EOL + "<h2>" + title + "</h2></header></section>";
    }

    /** {@inheritDoc} */
    protected String getSection3Block(String title) {
        return "<section><header>" + EOL + "<h3>" + title + "</h3></header></section>";
    }

    /** {@inheritDoc} */
    protected String getSection4Block(String title) {
        return "<section><header>" + EOL + "<h4>" + title + "</h4></header></section>";
    }

    /** {@inheritDoc} */
    protected String getSection5Block(String title) {
        return "<section><header>" + EOL + "<h5>" + title + "</h5></header></section>";
    }

    /** {@inheritDoc} */
    protected String getSection6Block(String title) {
        return "<section><header>" + EOL + "<h6>" + title + "</h6></header></section>";
    }

    /** {@inheritDoc} */
    protected String getHeaderBlock() {
        return "<header></header>";
    }

    /** {@inheritDoc} */
    protected String getContentBlock() {
        return "<main>" + EOL + "<div class=\"content\"></div></main>";
    }

    /** {@inheritDoc} */
    protected String getFooterBlock() {
        return "<footer></footer>";
    }

    /** {@inheritDoc} */
    protected String getListBlock(String item) {
        return "<ul>" + EOL + "<li>" + item + "</li></ul>";
    }

    /** {@inheritDoc} */
    protected String getNumberedListBlock(String item) {
        return "<ol style=\"list-style-type: lower-roman;\">" + EOL + "<li>" + item + "</li></ol>";
    }

    /** {@inheritDoc} */
    protected String getDefinitionListBlock(String definum, String definition) {
        return "<dl>" + EOL + "<dt>" + definum + "</dt>" + EOL + "<dd>" + definition + "</dd></dl>";
    }

    /** {@inheritDoc} */
    protected String getFigureBlock(String source, String caption) {
        String figureBlock = "<figure><img src=\"" + escapeHTML(source, true) + "\" />";
        if (caption != null) {
            figureBlock += "<figcaption>" + caption + "</figcaption>";
        }
        figureBlock += "</figure>";
        return figureBlock;
    }

    /** {@inheritDoc} */
    protected String getTableBlock(String cell, String caption) {
        return "<table class=\"bodyTable\">"
                + "<caption>Table caption</caption><tr class=\"a\">" + EOL + "<td>cell</td></tr>"
                + "</table>";
    }

    @Override
    protected String getTableWithHeaderBlock(String... rowPrefixes) {
        return "<table class=\"bodyTable\">" + EOL
                + "<tr class=\"a\">" + EOL
                + "<th>" + rowPrefixes[0] + "0</th>" + EOL
                + "<th>" + rowPrefixes[0] + "1</th>" + EOL
                + "<th>" + rowPrefixes[0] + "2</th></tr>" + EOL
                + "<tr class=\"b\">" + EOL
                + "<td style=\"text-align: left;\">" + rowPrefixes[1] + "0</td>" + EOL
                + "<td style=\"text-align: right;\">" + rowPrefixes[1] + "1</td>" + EOL
                + "<td style=\"text-align: center;\">" + rowPrefixes[1] + "2</td></tr>" + EOL
                + "<tr class=\"a\">" + EOL
                + "<td style=\"text-align: left;\">" + rowPrefixes[2] + "0</td>" + EOL
                + "<td style=\"text-align: right;\">" + rowPrefixes[2] + "1</td>" + EOL
                + "<td style=\"text-align: center;\">" + rowPrefixes[2] + "2</td></tr>"
                + "</table>";
    }

    // Disable testTable until the order of attributes issue is clarified
    // TODO: remove
    /** {@inheritDoc} */
    @Test
    public void testTable() {
        assertEquals("", "", "Dummy!");
    }

    /** {@inheritDoc} */
    protected String getParagraphBlock(String text) {
        return "<p>" + text + "</p>";
    }

    /** {@inheritDoc} */
    protected String getDataBlock(String value, String text) {
        return "<data value=\"" + value + "\">" + text + "</data>";
    }

    /** {@inheritDoc} */
    protected String getTimeBlock(String datetime, String text) {
        return "<time datetime=\"" + datetime + "\">" + text + "</time>";
    }

    /** {@inheritDoc} */
    protected String getAddressBlock(String text) {
        return "<address>" + text + "</address>";
    }

    /** {@inheritDoc} */
    protected String getBlockquoteBlock(String text) {
        return "<blockquote>" + text + "</blockquote>";
    }

    /** {@inheritDoc} */
    protected String getDivisionBlock(String text) {
        return "<div>" + text + "</div>";
    }

    /** {@inheritDoc} */
    protected String getVerbatimSourceBlock(String text) {
        return "<pre><code>" + text + "</code></pre>";
    }

    /** {@inheritDoc} */
    protected String getHorizontalRuleBlock() {
        return "<hr />";
    }

    /** {@inheritDoc} */
    protected String getPageBreakBlock() {
        return "<!-- PB -->";
    }

    /** {@inheritDoc} */
    protected String getAnchorBlock(String anchor) {
        return "<a id=\"" + anchor + "\">" + anchor + "</a>";
    }

    /** {@inheritDoc} */
    protected String getLinkBlock(String link, String text) {
        return "<a href=\"" + link + "\">" + text + "</a>";
    }

    /** {@inheritDoc} */
    protected String getInlineBlock(String text) {
        return text;
    }

    /** {@inheritDoc} */
    protected String getInlineItalicBlock(String text) {
        return "<i>" + text + "</i>";
    }

    /** {@inheritDoc} */
    protected String getInlineBoldBlock(String text) {
        return "<b>" + text + "</b>";
    }

    /** {@inheritDoc} */
    protected String getInlineCodeBlock(String text) {
        return "<code>" + text + "</code>";
    }

    /** {@inheritDoc} */
    protected String getItalicBlock(String text) {
        return "<i>" + text + "</i>";
    }

    /** {@inheritDoc} */
    protected String getBoldBlock(String text) {
        return "<b>" + text + "</b>";
    }

    /** {@inheritDoc} */
    protected String getMonospacedBlock(String text) {
        return "<code>" + text + "</code>";
    }

    /** {@inheritDoc} */
    protected String getLineBreakBlock() {
        return "<br />";
    }

    /** {@inheritDoc} */
    protected String getLineBreakOpportunityBlock() {
        return "<wbr />";
    }

    /** {@inheritDoc} */
    protected String getNonBreakingSpaceBlock() {
        return "&#160;";
    }

    /** {@inheritDoc} */
    protected String getTextBlock(String text) {
        // TODO: need to be able to retreive those from outside the sink
        return "~,_=,_-,_+,_*,_[,_],_&lt;,_&gt;,_{,_},_\\";
    }

    /** {@inheritDoc} */
    protected String getRawTextBlock(String text) {
        return text;
    }

    /**
     * Test entities is section titles and paragraphs.
     */
    @Test
    public void testEntities() {
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

        assertEquals(
                "<section><header>" + EOL + "<h1>&amp;</h1></header>" + EOL + "<p>&amp;</p></section>",
                writer.toString());
    }

    /**
     * Test head events.
     */
    @Test
    public void testHead() {
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
                "<head>" + EOL + "<title>Title</title><!--A comment--><meta name=\"author\" content=\"&#x123;&amp;\" />"
                        + "<base href=\"http://maven.apache.org/\" /></head>";
        String actual = writer.toString();
        assertTrue(actual.contains(expected), actual);
    }

    /** {@inheritDoc} */
    protected String getCommentBlock(String text) {
        return "<!--" + toXmlComment(text) + "-->";
    }
}
