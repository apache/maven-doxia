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
package org.apache.maven.doxia.sink.impl;

import javax.swing.text.AttributeSet;
import javax.swing.text.html.HTML.Attribute;

import java.io.StringWriter;
import java.io.Writer;

import org.apache.maven.doxia.markup.Markup;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet.Semantics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for Xhtml5BaseSink.
 */
class Xhtml5BaseSinkTest {
    protected static final String LS = Markup.EOL;
    private final SinkEventAttributes attributes = SinkEventAttributeSet.BOLD;
    private Writer writer;

    private static final String EOL = System.lineSeparator();

    @BeforeEach
    void setUp() {
        writer = new StringWriter();
    }

    @Test
    void spaceAfterClosingTag() {
        // DOXIA-189
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.paragraph();
            sink.text("There should be no space before the ");
            sink.italic();
            sink.text("period");
            sink.italic_();
            sink.text(".");
            sink.paragraph_();
        }

        String actual = writer.toString();
        String expected = "<p>There should be no space before the <i>period</i>.</p>";

        assertEquals(expected, actual);
    }

    /**
     */
    @Test
    void nestedTables() {
        // DOXIA-177
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {

            sink.table();
            sink.tableRows(new int[] {Sink.JUSTIFY_CENTER}, false);
            sink.tableRow();
            sink.tableCell();
            sink.text("cell11");
            sink.tableCell_();
            sink.tableCell();
            sink.text("cell12");
            sink.tableCell_();
            sink.tableRow_();

            sink.tableRow();
            sink.tableCell();
            sink.table(SinkEventAttributeSet.LEFT);
            sink.tableRows(new int[] {Sink.JUSTIFY_LEFT}, false);
            sink.tableRow();
            sink.tableCell();
            sink.text("nestedTable1Cell11");
            sink.tableCell_();
            sink.tableCell();
            sink.text("nestedTable1Cell12");
            sink.tableCell_();
            sink.tableRow_();
            sink.tableRow();
            sink.tableCell();

            sink.table(SinkEventAttributeSet.RIGHT);
            sink.tableRows(new int[] {Sink.JUSTIFY_RIGHT}, false);
            sink.tableRow();
            sink.tableCell();
            sink.text("nestedTable2Cell11");
            sink.tableCell_();
            sink.tableCell();
            sink.text("nestedTable2Cell12");
            sink.tableCell_();
            sink.tableRow_();
            sink.tableRow();
            sink.tableCell();
            sink.text("nestedTable2Cell21");
            sink.tableCell_();
            sink.tableCell();
            sink.text("nestedTable2Cell22");
            sink.tableCell_();
            sink.tableRow_();
            sink.tableRows_();
            sink.tableCaption();
            sink.text("caption3");
            sink.tableCaption_();
            sink.table_();

            sink.tableCell_();
            sink.tableCell();
            sink.text("nestedTable1Cell22");
            sink.tableCell_();
            sink.tableRow_();
            sink.tableRows_();
            sink.tableCaption();
            sink.text("caption2");
            sink.tableCaption_();
            sink.table_();

            sink.tableCell_();
            sink.tableCell();
            sink.text("cell22");
            sink.tableCell_();
            sink.tableRow_();
            sink.tableRows_();
            sink.tableCaption();
            sink.text("caption&1");
            sink.tableCaption_();
            sink.table_();
        }

        String actual = writer.toString();
        assertTrue(actual.contains("<table class=\"bodyTable\">" + "<caption>caption&amp;1</caption>"));
        assertTrue(actual.contains("<table class=\"bodyTable\">" + "<caption>caption2</caption>"));
        assertTrue(actual.contains("<table class=\"bodyTable\">" + "<caption>caption3</caption>"));

        assertTrue(actual.contains("<td style=\"text-align: center;\">cell11</td>"));
        assertTrue(actual.contains("<td style=\"text-align: left;\">nestedTable1Cell11</td>"));
        assertTrue(actual.contains("<td style=\"text-align: right;\">nestedTable2Cell11</td>"));
        assertTrue(actual.contains("<td>nestedTable1Cell22</td>"));
        assertTrue(actual.contains("<td>cell22</td>"));
    }

    /**
     * Test of article method, of class Xhtml5BaseSink.
     */
    @Test
    void article() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.article();
            sink.article_();
        }

        assertEquals("<article></article>", writer.toString());

        writer = new StringWriter();

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.article(attributes);
            sink.article_();
        }

        assertEquals("<article style=\"bold\"></article>", writer.toString());
    }

    /**
     * Test of navigation method, of class Xhtml5BaseSink.
     */
    @Test
    void navigation() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.navigation();
            sink.navigation_();
        }

        assertEquals("<nav></nav>", writer.toString());

        writer = new StringWriter();

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.navigation(attributes);
            sink.navigation_();
        }
        assertEquals("<nav style=\"bold\"></nav>", writer.toString());
    }

    /**
     * Test of sidebar method, of class Xhtml5BaseSink.
     */
    @Test
    void sidebar() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.sidebar();
            sink.sidebar_();
        }

        assertEquals("<aside></aside>", writer.toString());

        writer = new StringWriter();

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.sidebar(attributes);
            sink.sidebar_();
        }

        assertEquals("<aside style=\"bold\"></aside>", writer.toString());
    }

    /**
     * Test of section method, of class Xhtml5BaseSink.
     */
    @Test
    void section() {
        final int level = Xhtml5BaseSink.SECTION_LEVEL_1;

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.section(level, attributes);
            sink.sectionTitle(level, attributes);
            sink.sectionTitle_(level);
            sink.section_(level);
        }

        assertEquals("<section style=\"bold\">" + LS + "<h1 style=\"bold\"></h1></section>", writer.toString());
    }

    /**
     * Test of section method, of class Xhtml5BaseSink.
     */
    @Test
    void sectionAttributes() {
        final int level = Xhtml5BaseSink.SECTION_LEVEL_1;
        final SinkEventAttributeSet set =
                new SinkEventAttributeSet("name", "section name", "class", "foo", "id", "bar");

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.section(level, set);
            sink.sectionTitle(level, null);
            sink.sectionTitle_(level);
            sink.section_(level);
        }

        assertEquals("<section class=\"foo\" id=\"bar\">" + LS + "<h1></h1></section>", writer.toString());
    }

    /**
     * Test of section1 method, of class Xhtml5BaseSink.
     */
    @Test
    void section1() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.section1();
            sink.sectionTitle1();
            sink.sectionTitle1_();
            sink.section1_();
        }

        assertEquals("<section>" + LS + "<h1></h1></section>", writer.toString());
    }

    /**
     * Test of section2 method, of class Xhtml5BaseSink.
     */
    @Test
    void section2() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.section2();
            sink.sectionTitle2();
            sink.sectionTitle2_();
            sink.section2_();
        }

        assertEquals("<section>" + LS + "<h2></h2></section>", writer.toString());
    }

    /**
     * Test of section3 method, of class Xhtml5BaseSink.
     */
    @Test
    void section3() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.section3();
            sink.sectionTitle3();
            sink.sectionTitle3_();
            sink.section3_();
        }

        assertEquals("<section>" + LS + "<h3></h3></section>", writer.toString());
    }

    /**
     * Test of section4 method, of class Xhtml5BaseSink.
     */
    @Test
    void section4() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.section4();
            sink.sectionTitle4();
            sink.sectionTitle4_();
            sink.section4_();
        }

        assertEquals("<section>" + LS + "<h4></h4></section>", writer.toString());
    }

    /**
     * Test of section5 method, of class Xhtml5BaseSink.
     */
    @Test
    void section5() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.section5();
            sink.sectionTitle5();
            sink.sectionTitle5_();
            sink.section5_();
        }

        assertEquals("<section>" + LS + "<h5></h5></section>", writer.toString());
    }

    /**
     * Test of header method, of class Xhtml5BaseSink.
     */
    @Test
    void header() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.header();
            sink.header_();
        }

        assertEquals("<header></header>", writer.toString());

        writer = new StringWriter();

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.header(attributes);
            sink.header_();
        }

        assertEquals("<header style=\"bold\"></header>", writer.toString());
    }

    /**
     * Test of content method, of class Xhtml5BaseSink.
     */
    @Test
    void content() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.content();
            sink.content();
            sink.content_();
            sink.content_();
        }

        assertEquals("<main>" + EOL + "<div class=\"content\"></div></main>", writer.toString());

        writer = new StringWriter();

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.content(attributes);
            sink.content(attributes);
            sink.content_();
            sink.content_();
        }

        assertEquals(
                "<main style=\"bold\">" + EOL + "<div style=\"bold\" class=\"content\"></div></main>",
                writer.toString());
    }

    /**
     * Test of footer method, of class Xhtml5BaseSink.
     */
    @Test
    void footer() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.footer();
            sink.footer_();
        }

        assertEquals("<footer></footer>", writer.toString());

        writer = new StringWriter();

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.footer(attributes);
            sink.footer_();
        }

        assertEquals("<footer style=\"bold\"></footer>", writer.toString());
    }

    /**
     * Test of list method, of class Xhtml5BaseSink.
     */
    @Test
    void list() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.list();
            sink.listItem();
            sink.listItem_();
            sink.list_();
        }

        assertEquals("<ul>" + LS + "<li></li></ul>", writer.toString());

        writer = new StringWriter();

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.list(attributes);
            sink.listItem(attributes);
            sink.listItem_();
            sink.list_();
        }

        assertEquals("<ul style=\"bold\">" + LS + "<li style=\"bold\"></li></ul>", writer.toString());
    }

    /**
     * Test of numberedList method, of class Xhtml5BaseSink.
     */
    @Test
    void numberedList() {
        final int numbering = Xhtml5BaseSink.NUMBERING_DECIMAL;

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.numberedList(numbering);
            sink.numberedListItem();
            sink.numberedListItem_();
            sink.numberedList_();
        }

        assertEquals("<ol style=\"list-style-type: decimal;\">" + LS + "<li></li></ol>", writer.toString());

        writer = new StringWriter();

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.numberedList(numbering, attributes);
            sink.numberedListItem(attributes);
            sink.numberedListItem_();
            sink.numberedList_();
        }

        assertEquals(
                "<ol style=\"list-style-type: decimal; bold\">" + LS + "<li style=\"bold\"></li></ol>",
                writer.toString());
    }

    /**
     * Test of definitionList method, of class Xhtml5BaseSink.
     */
    @Test
    void definitionList() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.definitionList();
            sink.definedTerm();
            sink.definedTerm_();
            sink.definition();
            sink.definition_();
            sink.definitionList_();
        }

        assertEquals("<dl>" + LS + "<dt></dt>" + LS + "<dd></dd></dl>", writer.toString());

        writer = new StringWriter();

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.definitionList(attributes);
            sink.definedTerm(attributes);
            sink.definedTerm_();
            sink.definition(attributes);
            sink.definition_();
            sink.definitionList_();
        }

        assertEquals(
                "<dl style=\"bold\">" + LS + "<dt style=\"bold\"></dt>" + LS + "<dd style=\"bold\"></dd></dl>",
                writer.toString());
    }

    /**
     * Test of figure method, of class Xhtml5BaseSink.
     */
    @Test
    void figure() {
        final String src = "src.jpg";

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.figure(attributes);
            sink.figureGraphics(src, attributes);
            sink.figureCaption(attributes);
            sink.figureCaption_();
            sink.figure_();
        }

        assertEquals(
                "<figure style=\"bold\">" + "<img src=\"src.jpg\" style=\"bold\" />"
                        + "<figcaption style=\"bold\"></figcaption></figure>",
                writer.toString());
    }

    /**
     * Test of figureGraphics method, of class Xhtml5BaseSink.
     */
    @Test
    void figureGraphics() {
        String src = "source.png";

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.figureGraphics(src, attributes);
        }

        assertEquals("<img src=\"source.png\" style=\"bold\" />", writer.toString());
    }

    /**
     * Test of paragraph method, of class Xhtml5BaseSink.
     */
    @Test
    void paragraph() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.paragraph();
            sink.paragraph_();
        }

        assertEquals("<p></p>", writer.toString());

        writer = new StringWriter();

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.paragraph(attributes);
            sink.paragraph_();
        }

        assertEquals("<p style=\"bold\"></p>", writer.toString());
    }

    /**
     * Test of data method, of class Xhtml5BaseSink.
     */
    @Test
    void data() {
        String value = "value";

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.data(value, attributes);
            sink.data_();
        }

        assertEquals("<data value=\"value\" style=\"bold\"></data>", writer.toString());
    }

    /**
     * Test of time method, of class Xhtml5BaseSink.
     */
    @Test
    void time() {
        String datetime = "datetime";

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.time(datetime, attributes);
            sink.time_();
        }

        assertEquals("<time datetime=\"datetime\" style=\"bold\"></time>", writer.toString());
    }

    /**
     * Test of address method, of class Xhtml5BaseSink.
     */
    @Test
    void address() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.address();
            sink.address_();
        }

        assertEquals("<address></address>", writer.toString());

        writer = new StringWriter();

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.address(attributes);
            sink.address_();
        }

        assertEquals("<address style=\"bold\"></address>", writer.toString());
    }

    /**
     * Test of blockquote method, of class Xhtml5BaseSink.
     */
    @Test
    void blockquote() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.blockquote();
            sink.blockquote_();
        }

        assertEquals("<blockquote></blockquote>", writer.toString());

        writer = new StringWriter();

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.blockquote(attributes);
            sink.blockquote_();
        }

        assertEquals("<blockquote style=\"bold\"></blockquote>", writer.toString());
    }

    /**
     * Test of division method, of class Xhtml5BaseSink.
     */
    @Test
    void division() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.division();
            sink.division_();
        }

        assertEquals("<div></div>", writer.toString());

        writer = new StringWriter();

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.division(attributes);
            sink.division_();
        }

        assertEquals("<div style=\"bold\"></div>", writer.toString());
    }

    /**
     * Test of verbatim method, of class Xhtml5BaseSink.
     */
    @Test
    void verbatim() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.verbatim(SinkEventAttributeSet.SOURCE);
            sink.verbatim_();
        }

        assertEquals("<pre><code></code></pre>", writer.toString());

        checkVerbatimAttributes(attributes, "<pre style=\"bold\"></pre>");

        final SinkEventAttributes att = new SinkEventAttributeSet(SinkEventAttributes.ID, "id");
        checkVerbatimAttributes(att, "<pre id=\"id\"></pre>");

        att.addAttribute(Attribute.CLASS, "class");
        checkVerbatimAttributes(att, "<pre id=\"id\" class=\"class\"></pre>");

        att.addAttribute(SinkEventAttributes.DECORATION, "source");
        checkVerbatimAttributes(att, "<pre id=\"id\" class=\"class\"><code></code></pre>");

        att.removeAttribute(Attribute.CLASS.toString());
        checkVerbatimAttributes(att, "<pre id=\"id\"><code></code></pre>");
    }

    private void checkVerbatimAttributes(final SinkEventAttributes att, final String expected) {

        writer = new StringWriter();

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.verbatim(att);
            sink.verbatim_();
        }

        assertEquals(expected, writer.toString());
    }

    @Test
    void verbatimSourceWithNewline() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.verbatim(SinkEventAttributeSet.SOURCE);
            sink.text(EOL + "firstLine");
            sink.text(EOL + "secondLine");
            sink.verbatim_();
        }
        // first newline must be stripped to render the same as with verbatim without source (special <pre> semantics)
        assertEquals("<pre><code>firstLine" + EOL + "secondLine</code></pre>", writer.toString());
    }

    /**
     * Test of horizontalRule method, of class Xhtml5BaseSink.
     */
    @Test
    void horizontalRule() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.horizontalRule();
            sink.horizontalRule(attributes);
        }

        assertEquals("<hr /><hr style=\"bold\" />", writer.toString());
    }

    /**
     * Test of table method, of class Xhtml5BaseSink.
     */
    @Test
    void table() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.table(attributes);
            sink.table_();
        }

        assertEquals("</table>", writer.toString());
    }

    /**
     * Test of tableRows method, of class Xhtml5BaseSink.
     */
    @Test
    void tableRows() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.table();
            sink.tableRows();
            sink.tableRows_();
            sink.table_();
        }

        assertEquals("<table class=\"bodyTable\"></table>", writer.toString());
    }

    /**
     * Test of tableRow method, of class Xhtml5BaseSink.
     */
    @Test
    void tableRow() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.table();
            sink.tableRows();
            sink.tableRow(attributes);
            sink.tableRow_();
            sink.tableRow();
            sink.tableRow_();
            sink.tableRows_();
            sink.table_();
        }

        String xmlExpected = "<table class=\"bodyTable\">" + EOL + "<tr style=\"bold\" class=\"a\"></tr>" + EOL
                + "<tr class=\"b\"></tr></table>";

        assertEquals(xmlExpected, writer.toString());
    }

    /**
     * Test striping for hidden rows in tableRow method.
     */
    @Test
    void hiddenTableRowStriping() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            SinkEventAttributeSet attributes2 = new SinkEventAttributeSet();
            SinkEventAttributeSet attributes3 = new SinkEventAttributeSet();
            attributes3.addAttributes(attributes);

            sink.table();
            sink.tableRows();
            sink.tableRow();
            sink.tableRow_();
            sink.tableRow(attributes);
            sink.tableRow_();
            attributes2.addAttribute(SinkEventAttributes.CLASS, "hidden xyz abc");
            sink.tableRow(attributes2);
            sink.tableRow_();
            attributes2.addAttribute(SinkEventAttributes.CLASS, "abc hidden xyz");
            sink.tableRow(attributes2);
            sink.tableRow_();
            sink.tableRow();
            sink.tableRow_();
            attributes2.addAttribute(SinkEventAttributes.CLASS, "not-hidden xyz");
            sink.tableRow(attributes2);
            sink.tableRow_();
            attributes2.addAttribute(SinkEventAttributes.CLASS, "xyz not-hidden");
            sink.tableRow(attributes2);
            sink.tableRow_();
            attributes3.addAttribute(SinkEventAttributes.CLASS, "xyz abc hidden");
            sink.tableRow(attributes3);
            sink.tableRow_();
            attributes2.addAttribute(SinkEventAttributes.CLASS, "xyz hidden-not");
            sink.tableRow(attributes2);
            sink.tableRow_();
            sink.tableRow();
            sink.tableRow_();
            sink.tableRows_();
            sink.table_();
        }

        StringBuilder expected = new StringBuilder("<table class=\"bodyTable\">");
        expected.append(EOL).append("<tr class=\"a\"></tr>").append(EOL);
        expected.append("<tr style=\"bold\" class=\"b\"></tr>").append(EOL);
        expected.append("<tr class=\"a hidden xyz abc\"></tr>").append(EOL);
        expected.append("<tr class=\"a abc hidden xyz\"></tr>").append(EOL);
        expected.append("<tr class=\"a\"></tr>").append(EOL);
        expected.append("<tr class=\"b not-hidden xyz\"></tr>").append(EOL);
        expected.append("<tr class=\"a xyz not-hidden\"></tr>").append(EOL);
        expected.append("<tr style=\"bold\" class=\"b xyz abc hidden\"></tr>").append(EOL);
        expected.append("<tr class=\"b xyz hidden-not\"></tr>").append(EOL);
        expected.append("<tr class=\"a\"></tr></table>");

        String xmlExpected = expected.toString();
        assertEquals(xmlExpected, writer.toString());
    }

    /**
     * Test of tableCell method, of class Xhtml5BaseSink.
     */
    @Test
    void tableCell() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.tableCell(attributes);
            sink.tableCell_();
        }

        assertEquals("<td style=\"bold\"></td>", writer.toString());
    }

    /**
     * Test of tableHeaderCell method, of class Xhtml5BaseSink.
     */
    @Test
    void tableHeaderCell() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.tableHeaderCell(attributes);
            sink.tableHeaderCell_();
        }

        assertEquals("<th style=\"bold\"></th>", writer.toString());
    }

    /**
     * Test of tableCaption method, of class Xhtml5BaseSink.
     */
    @Test
    void tableCaption() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.table();
            sink.tableRows();
            sink.tableCaption(attributes);
            sink.text("caption");
            sink.tableCaption_();
            sink.tableRows_();
            sink.table_();
        }

        assertEquals(
                "<table class=\"bodyTable\">" + "<caption style=\"bold\">caption</caption></table>", writer.toString());
    }

    /**
     * Test of anchor method, of class Xhtml5BaseSink.
     */
    @Test
    void anchor() {
        String name = "anchor";
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.anchor(name, attributes);
            sink.anchor_();
        }

        assertEquals("<a id=\"anchor\" style=\"bold\"></a>", writer.toString());
    }

    /**
     * Test of link method, of class Xhtml5BaseSink.
     */
    @Test
    void link() {
        final String name = "link.html";

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.link(name, attributes);
            sink.link_();
        }

        assertEquals("<a style=\"bold\" href=\"link.html\"></a>", writer.toString());
    }

    /**
     * Test of link method for an external link.
     */
    @Test
    void linkExternal() {
        final String name = "https://www.apache.org";

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.link(name, attributes);
            sink.link_();
        }

        assertEquals(
                "<a style=\"bold\" class=\"externalLink\" href=\"https://www.apache.org\"></a>", writer.toString());
    }

    /**
     * Test of link method for an external link when a css class is provided.
     */
    @Test
    void linkExternalClassExtend() {
        final String name = "https://www.apache.org";
        SinkEventAttributeSet attributes2 = new SinkEventAttributeSet();
        attributes2.addAttributes(attributes);
        attributes2.addAttribute(SinkEventAttributes.CLASS, "cs1 cs2");

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.link(name, attributes2);
            sink.link_();
        }

        assertEquals(
                "<a style=\"bold\" class=\"externalLink cs1 cs2\" href=\"https://www.apache.org\"></a>",
                writer.toString());
    }

    /**
     * Test of inline method, of class Xhtml5BaseSink.
     */
    @Test
    void inline() {
        String text = "a text & \u00c6";

        writer = new StringWriter();

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.inline(SinkEventAttributeSet.Semantics.EMPHASIS);
            sink.inline(SinkEventAttributeSet.Semantics.STRONG);
            sink.inline(SinkEventAttributeSet.Semantics.SMALL);
            sink.inline(SinkEventAttributeSet.Semantics.LINE_THROUGH);
            sink.inline(SinkEventAttributeSet.Semantics.CITATION);
            sink.inline(SinkEventAttributeSet.Semantics.QUOTE);
            sink.inline(SinkEventAttributeSet.Semantics.DEFINITION);
            sink.inline(SinkEventAttributeSet.Semantics.ABBREVIATION);
            sink.inline(SinkEventAttributeSet.Semantics.ITALIC);
            sink.inline(SinkEventAttributeSet.Semantics.BOLD);
            sink.inline(SinkEventAttributeSet.Semantics.CODE);
            sink.inline(SinkEventAttributeSet.Semantics.VARIABLE);
            sink.inline(SinkEventAttributeSet.Semantics.SAMPLE);
            sink.inline(SinkEventAttributeSet.Semantics.KEYBOARD);
            sink.inline(SinkEventAttributeSet.Semantics.SUPERSCRIPT);
            sink.inline(SinkEventAttributeSet.Semantics.SUBSCRIPT);
            sink.inline(SinkEventAttributeSet.Semantics.ANNOTATION);
            sink.inline(SinkEventAttributeSet.Semantics.HIGHLIGHT);
            sink.inline(SinkEventAttributeSet.Semantics.RUBY);
            sink.inline(SinkEventAttributeSet.Semantics.RUBY_BASE);
            sink.inline(SinkEventAttributeSet.Semantics.RUBY_TEXT);
            sink.inline(SinkEventAttributeSet.Semantics.RUBY_TEXT_CONTAINER);
            sink.inline(SinkEventAttributeSet.Semantics.RUBY_PARANTHESES);
            sink.inline(SinkEventAttributeSet.Semantics.BIDIRECTIONAL_ISOLATION);
            sink.inline(SinkEventAttributeSet.Semantics.BIDIRECTIONAL_OVERRIDE);
            sink.inline(SinkEventAttributeSet.Semantics.PHRASE);
            sink.inline(SinkEventAttributeSet.Semantics.INSERT);
            sink.inline(SinkEventAttributeSet.Semantics.DELETE);
            sink.text(text);
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
            sink.inline_();
        }

        assertEquals(
                "<em><strong><small><s><cite><q><dfn><abbr><i><b><code><var><samp><kbd><sup><sub><u><mark><ruby><rb><rt><rtc><rp><bdi><bdo><span><ins><del>a text &amp; &#xc6;</del></ins></span></bdo></bdi></rp></rtc></rt></rb></ruby></mark></u></sub></sup></kbd></samp></var></code></b></i></abbr></dfn></q></cite></s></small></strong></em>",
                writer.toString());
    }

    /**
     * Test of italic/bold/code method, of class Xhtml5BaseSink.
     */
    @Test
    void italic() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.inline(SinkEventAttributeSet.Semantics.ITALIC);
            sink.inline_();
            sink.inline(SinkEventAttributeSet.Semantics.BOLD);
            sink.inline_();
            sink.inline(SinkEventAttributeSet.Semantics.CODE);
            sink.inline_();
        }

        assertEquals("<i></i><b></b><code></code>", writer.toString());
    }

    /**
     * Test of lineBreak/lineBreakOpportunity/pageBreak/nonBreakingSpace method, of class Xhtml5BaseSink.
     */
    @Test
    void lineBreak() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.lineBreak(attributes);
            sink.lineBreakOpportunity(attributes);
            sink.pageBreak();
            sink.nonBreakingSpace();
        }

        assertEquals("<br style=\"bold\" /><wbr style=\"bold\" /><!-- PB -->&#160;", writer.toString());
    }

    /**
     * Test of text method, of class Xhtml5BaseSink.
     */
    @Test
    void text() {
        String text = "a text & \u00c6";

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.text(text);
        }

        assertEquals("a text &amp; &#xc6;", writer.toString());

        writer = new StringWriter();

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.text(text, attributes);
        }

        assertEquals("<span style=\"font-weight: bold\">a text &amp; &#xc6;</span>", writer.toString());
    }

    /**
     * Test of text method, of class Xhtml5BaseSink.
     */
    @Test
    void textWithAttributes() {
        String text = "text";

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.text(text, Semantics.STRONG);
        }

        assertEquals("<strong>text</strong>", writer.toString());
    }

    /**
     * Test of rawText method, of class Xhtml5BaseSink.
     */
    @Test
    void rawText() {
        String text = "raw text";

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.rawText(text);
        }

        assertEquals("raw text", writer.toString());
    }

    /**
     * Test of comment method, of class Xhtml5BaseSink.
     */
    @Test
    void comment() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.comment("a comment");
            sink.comment(" a comment");
            sink.comment("a comment ");
            sink.comment(" a comment ");
        }

        assertEquals("<!--a comment--><!-- a comment--><!--a comment --><!-- a comment -->", writer.toString());
    }

    /**
     * Test of unknown method, of class Xhtml5BaseSink.
     */
    @Test
    void unknown() {
        final String name = "unknown";
        final Object[] requiredParams = null;

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.unknown(name, requiredParams, attributes);
        }

        assertEquals("", writer.toString());
    }

    /**
     * Test entities in attribute values.
     */
    @Test
    void attributeEntities() {
        final Object[] startTag = new Object[] {Xhtml5BaseSink.TAG_TYPE_START};
        final Object[] endTag = new Object[] {Xhtml5BaseSink.TAG_TYPE_END};
        final String script = Xhtml5BaseSink.SCRIPT.toString();
        final SinkEventAttributes src =
                new SinkEventAttributeSet(SinkEventAttributes.SRC, "http://ex.com/ex.js?v=l&l=e");

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.unknown(script, startTag, src);
            sink.unknown(script, endTag, null);

            sink.figureGraphics("http://ex.com/ex.jpg?v=l&l=e", src);
        }

        String result = writer.toString();

        assertTrue(result.contains("ex.js?v=l&amp;l=e"));
        assertTrue(result.contains("ex.jpg?v=l&amp;l=e"));
    }

    /**
     * Test of entity.
     */
    @Test
    void entity() {
        // DOXIA-314
        String text = "a text '&#x1d7ed;'";

        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.text(text);
        }

        assertEquals("a text '&#x1d7ed;'", writer.toString());
    }

    /**
     * Test unicode characters in tables. DOXIA-433.
     */
    @Test
    void specialCharacters() {
        try (Xhtml5BaseSink sink = new Xhtml5BaseSink(writer)) {
            sink.table();
            sink.tableRows();
            sink.tableRow(null);
            sink.tableCell();
            sink.text("\u2713", null);
            sink.tableCell_();
            sink.tableRow_();
            sink.tableRows_();
            sink.table_();
        }

        final String result = writer.toString();

        assertTrue(result.contains("&#x2713;"));
    }

    @Test
    void multipleInlineAttributes() {
        try (Sink sink = new Xhtml5BaseSink(writer)) {
            SinkEventAttributeSet attributes = new SinkEventAttributeSet();
            // set different attributes (semantic, style and decoration)
            attributes.addAttributes(SinkEventAttributeSet.LINETHROUGH);
            attributes.addAttributes(SinkEventAttributeSet.BOLD);
            attributes.addAttributes(SinkEventAttributeSet.Semantics.SUPERSCRIPT);
            sink.inline(attributes);
            sink.text("text");
            sink.inline_();
        }
        // the attribute order should be kept
        String expected = "<sup style=\"text-decoration-line: line-through; font-weight: bold\">text</sup>";
        assertEquals(expected, writer.toString());
    }

    @Test
    void addStyleWithStringValue() {
        SinkEventAttributes attributes = new SinkEventAttributeSet();
        Xhtml5BaseSink.addStyle(attributes, "font-weight", "bold");
        assertEquals("font-weight: bold", attributes.getAttribute(SinkEventAttributes.STYLE));
        Xhtml5BaseSink.addStyle(attributes, "font-size", "12px");
        assertEquals(1, attributes.getAttributeCount());
        assertEquals("font-weight: bold; font-size: 12px", attributes.getAttribute(SinkEventAttributes.STYLE));
    }

    @Test
    void addStyleWithAttributeSetValue() {
        SinkEventAttributes attributes = new SinkEventAttributeSet();
        SinkEventAttributeSet styleAttributes = new SinkEventAttributeSet();
        styleAttributes.addAttribute("font-style", "italic");
        attributes.addAttribute(SinkEventAttributeSet.STYLE, styleAttributes);

        Xhtml5BaseSink.addStyle(attributes, "font-weight", "bold");
        assertEquals("font-style: italic; font-weight: bold", SinkUtils.asCssString((AttributeSet)
                attributes.getAttribute(SinkEventAttributes.STYLE)));
        Xhtml5BaseSink.addStyle(attributes, "font-weight", "lighter");
        assertEquals("font-style: italic; font-weight: lighter", SinkUtils.asCssString((AttributeSet)
                attributes.getAttribute(SinkEventAttributes.STYLE)));
        Xhtml5BaseSink.addStyle(attributes, "font-size", "12px");
        assertEquals("font-style: italic; font-weight: lighter; font-size: 12px", SinkUtils.asCssString((AttributeSet)
                attributes.getAttribute(SinkEventAttributes.STYLE)));
    }
}
