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
package org.apache.maven.doxia.module.xdoc;

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

import javax.swing.text.html.HTML.Attribute;

import java.io.StringWriter;
import java.io.Writer;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.AbstractSinkTest;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.junit.jupiter.api.Test;

import static org.apache.maven.doxia.util.HtmlTools.escapeHTML;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @since 1.0
 */
public class XdocSinkTest extends AbstractSinkTest {
    /** {@inheritDoc} */
    @Test
    public void testSection1() {
        String title = "Title1";
        getSink().section1();
        getSink().sectionTitle1();
        getSink().text(title);
        getSink().sectionTitle1_();
        getSink().section1_();
        getSink().flush();
        getSink().close();

        String actual = getSinkContent();
        String expected = getSection1Block(title);

        assertEquals(expected, actual, "Wrong section1 block!");
    }

    /** {@inheritDoc} */
    @Test
    public void testSection2() {
        String title = "Title2";
        getSink().section2();
        getSink().sectionTitle2();
        getSink().text(title);
        getSink().sectionTitle2_();
        getSink().section2_();
        getSink().flush();
        getSink().close();

        String actual = getSinkContent();
        String expected = getSection2Block(title);

        assertEquals(expected, actual, "Wrong section2 block!");
    }

    /** {@inheritDoc} */
    protected String outputExtension() {
        return "xml";
    }

    /** {@inheritDoc} */
    protected Sink createSink(Writer writer) {
        return new XdocSink(writer, "UTF-8");
    }

    /** {@inheritDoc} */
    protected boolean isXmlSink() {
        return true;
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
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<document xmlns=\"http://maven.apache.org/XDOC/2.0\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xsi:schemaLocation=\"http://maven.apache.org/XDOC/2.0 https://maven.apache.org/xsd/xdoc-2.0.xsd\">"
                + "<properties></properties>";
    }

    /** {@inheritDoc} */
    protected String getBodyBlock() {
        return "<body></body></document>";
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
        return "<section name=\"" + title + "\"></section>";
    }

    /** {@inheritDoc} */
    protected String getSection2Block(String title) {
        return "<subsection name=\"" + title + "\"></subsection>";
    }

    /** {@inheritDoc} */
    protected String getSection3Block(String title) {
        return "<header>" + EOL + "<h3>" + title + "</h3></header>";
    }

    /** {@inheritDoc} */
    protected String getSection4Block(String title) {
        return "<header>" + EOL + "<h4>" + title + "</h4></header>";
    }

    /** {@inheritDoc} */
    protected String getSection5Block(String title) {
        return "<header>" + EOL + "<h5>" + title + "</h5></header>";
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
        return "<ul>\n<li>" + item + "</li></ul>";
    }

    /** {@inheritDoc} */
    protected String getNumberedListBlock(String item) {
        return "<ol style=\"list-style-type: lower-roman\">\n<li>" + item + "</li></ol>";
    }

    /** {@inheritDoc} */
    protected String getDefinitionListBlock(String definum, String definition) {
        return "<dl>\n<dt>" + definum + "</dt>\n<dd>" + definition + "</dd></dl>";
    }

    /** {@inheritDoc} */
    protected String getFigureBlock(String source, String caption) {
        String figureBlock = "<figure><img src=\"" + escapeHTML(source) + "\"  alt=\"\" />";
        if (caption != null) {
            figureBlock += "<figcaption>" + caption + "</figcaption>";
        }
        figureBlock += "</figure>";
        return figureBlock;
    }

    /** {@inheritDoc} */
    protected String getTableBlock(String cell, String caption) {
        return "<table border=\"0\"><caption>" + caption + "</caption>\n<tr>\n<td style=\"text-align: center;\">" + cell
                + "</td></tr></table>";
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
    protected String getVerbatimBlock(String text) {
        return "<source>" + text + "</source>";
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
        return "<tt>" + text + "</tt>";
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
        return escapeHTML(text);
    }

    /** {@inheritDoc} */
    protected String getRawTextBlock(String text) {
        return "~,_=,_-,_+,_*,_[,_],_<,_>,_{,_},_\\";
    }

    /**
     * Test verbatim.
     */
    @Test
    public void testBoxedVerbatim() {
        Writer writer = new StringWriter();
        XdocSink sink = null;

        try {
            sink = new XdocSink(writer);

            sink.verbatim(null);
            sink.verbatim_();
            sink.verbatim(SinkEventAttributeSet.BOXED);
            sink.verbatim_();
        } finally {
            sink.close();
        }

        assertEquals("<pre></pre><source></source>", writer.toString());
    }

    /**
     * Test link.
     */
    @Test
    public void testLinkWithTarget() {
        Writer writer = new StringWriter();
        XdocSink sink = null;

        try {
            sink = new XdocSink(writer);

            sink.link("name");
            sink.link_();
            SinkEventAttributes attrs = new SinkEventAttributeSet();
            attrs.addAttribute(Attribute.TARGET, "nirvana");
            sink.link("name", attrs);
            sink.link_();
        } finally {
            sink.close();
        }

        assertEquals("<a href=\"name\"></a><a target=\"nirvana\" href=\"name\"></a>", writer.toString());
    }

    /** {@inheritDoc} */
    protected String getCommentBlock(String text) {
        return "<!--" + toXmlComment(text) + "-->";
    }
}
