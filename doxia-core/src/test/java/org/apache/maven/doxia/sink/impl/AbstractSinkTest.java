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

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.stream.IntStream;

import org.apache.maven.doxia.AbstractModuleTest;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

/**
 * Abstract base class to test sinks.
 */
public abstract class AbstractSinkTest extends AbstractModuleTest {
    private final CharArrayWriter testWriter = new CharArrayWriter();
    private Sink sink;

    /**
     * Resets the writer and creates a new sink with it.
     */
    @BeforeEach
    protected void setUp() {
        testWriter.reset();
        sink = createSink(testWriter);
    }

    /**
     * Ability to wrap the xmlFragment with a roottag and namespaces, when required
     *
     * @param xmlFragment XML fragment to wrap
     * @return valid XML
     */
    protected String wrapXml(String xmlFragment) {
        return xmlFragment;
    }

    /**
     * Transforms a given string to be compatible to XML comments.
     *
     * @param comment The string to transform.
     *
     * @return The given string transformed to be compatible to XML comments.
     *
     * @see <a href="http://www.w3.org/TR/2000/REC-xml-20001006#sec-comments">
     *   http://www.w3.org/TR/2000/REC-xml-20001006#sec-comments</a>
     * @since 1.7
     */
    protected static String toXmlComment(final String comment) {
        String processed = comment;

        if (processed != null) {
            while (processed.contains("--")) {
                processed = processed.replace("--", "- -");
            }

            if (processed.endsWith("-")) {
                processed += " ";
            }
        }

        return processed;
    }

    // ---------------------------------------------------------------------
    // Common test cases
    // ----------------------------------------------------------------------

    /**
     * Tests that the current sink is able to render the common test document. If the sink is an Xml sink defined
     * by {@link #isXmlSink()}, it uses an Xml Writer defined by {@link #getXmlTestWriter(String)}. If not, it uses
     * the Writer defined by {@link #getTestWriter(String)}.
     *
     * @see SinkTestDocument
     * @throws IOException If the target test document could not be generated.
     * @see #isXmlSink()
     * @see #getTestWriter(String)
     * @see #getXmlTestWriter(String)
     */
    @Test
    public final void testTestDocument() throws IOException {
        Writer writer = (isXmlSink() ? getXmlTestWriter("testDocument") : getTestWriter("testDocument"));
        Sink testSink = createSink(writer);

        try {
            SinkTestDocument.generate(testSink);
        } finally {
            testSink.close();
        }
    }

    /**
     * Checks that the sequence <code>[title(), text(title), title_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getTitleBlock getTitleBlock}(title).
     */
    @Test
    public void testTitle() {
        String title = "Grodek";
        sink.title();
        sink.text(title);
        sink.title_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getTitleBlock(title);

        assertEquals(expected, actual, "Wrong title!");
    }

    /**
     * Checks that the sequence <code>[author(), text(author), author_()]
     * </code>, invoked on the current sink, produces the same result as
     * {@link #getAuthorBlock getAuthorBlock}(author).
     */
    @Test
    public void testAuthor() {
        String author = "Georg_Trakl";
        sink.author();
        sink.text(author);
        sink.author_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getAuthorBlock(author);

        assertEquals(expected, actual, "Wrong author!");
    }

    /**
     * Checks that the sequence <code>[date(), text(date), date_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getDateBlock getDateBlock}(date).
     */
    @Test
    public void testDate() {
        String date = "1914";
        sink.date();
        sink.text(date);
        sink.date_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getDateBlock(date);

        assertEquals(expected, actual, "Wrong date!");
    }

    /**
     * Checks that the sequence <code>[head(), head_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getHeadBlock getHeadBlock()}.
     */
    @Test
    public void testHead() {
        sink.head();
        sink.head_();
        sink.flush();
        sink.close();

        String actual = normalizeLineEnds(testWriter.toString());
        String expected = normalizeLineEnds(getHeadBlock());

        assertEquals(expected, actual, "Wrong head!");
    }

    /**
     * Checks that the sequence <code>[body(), body_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getBodyBlock getBodyBlock()}.
     */
    @Test
    public void testBody() {
        sink.body();
        sink.body_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getBodyBlock();

        assertEquals(expected, actual, "Wrong body!");
    }

    /**
     * Checks that the sequence <code>[article(), article_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getArticleBlock getArticleBlock()}.
     */
    @Test
    public void testArticle() {
        sink.article();
        sink.article_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getArticleBlock();

        assertEquals(expected, actual, "Wrong article!");
    }

    /**
     * Checks that the sequence <code>[navigation(), navigation_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getNavigationBlock getNavigationBlock()}.
     */
    @Test
    public void testNavigation() {
        sink.navigation();
        sink.navigation_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getNavigationBlock();

        assertEquals(expected, actual, "Wrong navigation!");
    }

    /**
     * Checks that the sequence <code>[sidebar(), sidebar_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getSidebarBlock getSidebarBlock()}.
     */
    @Test
    public void testSidebar() {
        sink.sidebar();
        sink.sidebar_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getSidebarBlock();

        assertEquals(expected, actual, "Wrong sidebar!");
    }

    /**
     * Checks that the sequence <code>[sectionTitle(), text(title),
     * sectionTitle_()]</code>, invoked on the current sink, produces
     * the same result as
     * {@link #getSectionTitleBlock getSectionTitleBlock}(title).
     */
    @Test
    public void testSectionTitle() {
        String title = "Title";
        sink.sectionTitle();
        sink.text(title);
        sink.sectionTitle_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getSectionTitleBlock(title);

        assertEquals(expected, actual, "Wrong sectionTitle!");
    }

    /**
     * Checks that the sequence <code>[section1(), sectionTitle1(),
     * text(title), sectionTitle1_(), section1_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getSection1Block getSection1Block}(title).
     */
    @Test
    public void testSection1() {
        String title = "Title1";
        sink.section1();
        sink.header();
        sink.sectionTitle1();
        sink.text(title);
        sink.sectionTitle1_();
        sink.header_();
        sink.section1_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getSection1Block(title);

        assertEquals(expected, actual, "Wrong section1 block!");
    }

    /**
     * Checks that the sequence <code>[section2(), sectionTitle2(),
     * text(title), sectionTitle2_(), section2_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getSection2Block getSection2Block}(title).
     */
    @Test
    public void testSection2() {
        String title = "Title2";
        sink.section2();
        sink.header();
        sink.sectionTitle2();
        sink.text(title);
        sink.sectionTitle2_();
        sink.header_();
        sink.section2_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getSection2Block(title);

        assertEquals(expected, actual, "Wrong section2 block!");
    }

    /**
     * Checks that the sequence <code>[section3(), sectionTitle3(),
     * text(title), sectionTitle3_(), section3_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getSection3Block getSection3Block}(title).
     */
    @Test
    public void testSection3() {
        String title = "Title3";
        sink.section3();
        sink.header();
        sink.sectionTitle3();
        sink.text(title);
        sink.sectionTitle3_();
        sink.header_();
        sink.section3_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getSection3Block(title);

        assertEquals(expected, actual, "Wrong section3 block!");
    }

    /**
     * Checks that the sequence <code>[section4(), sectionTitle4(),
     * text(title), sectionTitle4_(), section4_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getSection4Block getSection4Block}(title).
     *
     */
    @Test
    public void testSection4() {
        String title = "Title4";
        sink.section4();
        sink.header();
        sink.sectionTitle4();
        sink.text(title);
        sink.sectionTitle4_();
        sink.header_();
        sink.section4_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getSection4Block(title);

        assertEquals(expected, actual, "Wrong section4 block!");
    }

    /**
     * Checks that the sequence <code>[section5(), sectionTitle5(),
     * text(title), sectionTitle5_(), section5_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getSection5Block getSection5Block}(title).
     */
    @Test
    public void testSection5() {
        String title = "Title5";
        sink.section5();
        sink.header();
        sink.sectionTitle5();
        sink.text(title);
        sink.sectionTitle5_();
        sink.header_();
        sink.section5_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getSection5Block(title);

        assertEquals(expected, actual, "Wrong section5 block!");
    }

    /**
     * Checks that the sequence <code>[section6(), sectionTitle6(),
     * text(title), sectionTitle6_(), section6_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getSection6Block}.
     */
    @Test
    public void testSection6() {
        String title = "Title6";
        sink.section6();
        sink.header();
        sink.sectionTitle6();
        sink.text(title);
        sink.sectionTitle6_();
        sink.header_();
        sink.section6_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getSection6Block(title);

        assertEquals(expected, actual, "Wrong section6 block!");
    }

    /**
     * Checks that the sequence <code>[header(), header_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getHeaderBlock getHeaderBlock()}.
     */
    @Test
    public void testHeader() {
        sink.header();
        sink.header_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getHeaderBlock();

        assertEquals(expected, actual, "Wrong header!");
    }

    /**
     * Checks that the sequence <code>[content(), content(), content_(), content_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getContentBlock getContentBlock()}.
     */
    @Test
    public void testContent() {
        sink.content();
        sink.content();
        sink.content_();
        sink.content_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getContentBlock();

        assertEquals(expected, actual, "Wrong content!");
    }

    /**
     * Checks that the sequence <code>[footer(), footer_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getHeaderBlock getHeaderBlock()}.
     */
    @Test
    public void testFooter() {
        sink.footer();
        sink.footer_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getFooterBlock();

        assertEquals(expected, actual, "Wrong footer!");
    }

    /**
     * Checks that the sequence <code>[list(), listItem(), text(item),
     * listItem_(), list_()]</code>, invoked on the current sink, produces
     * the same result as {@link #getListBlock getListBlock}(item).
     *
     */
    @Test
    public void testList() {
        String item = "list_item";
        sink.list();
        sink.listItem();
        sink.text(item);
        sink.listItem_();
        sink.list_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getListBlock(item);

        assertEquals(expected, actual, "Wrong list!");
    }

    /**
     * Checks that the sequence <code>
     * [numberedList(Sink.NUMBERING_LOWER_ROMAN), numberedListItem(),
     * text(item), numberedListItem_(), numberedList_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getNumberedListBlock getNumberedListBlock}(item).
     */
    @Test
    public void testNumberedList() {
        String item = "numbered_list_item";
        sink.numberedList(Sink.NUMBERING_LOWER_ROMAN);
        sink.numberedListItem();
        sink.text(item);
        sink.numberedListItem_();
        sink.numberedList_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getNumberedListBlock(item);

        assertEquals(expected, actual, "Wrong numbered list!");
    }

    /**
     * Checks that the sequence <code>[definitionList(), definitionListItem(),
     * definedTerm(), text(definum), definedTerm_(), definition(),
     * text(definition), definition_(), definitionListItem_(),
     * definitionList_()]</code>, invoked on the current sink, produces the same
     * result as {@link #getDefinitionListBlock getDefinitionListBlock}
     * (definum, definition).
     */
    @Test
    public void testDefinitionList() {
        String definum = "definum";
        String definition = "definition";
        sink.definitionList();
        sink.definitionListItem();
        sink.definedTerm();
        sink.text(definum);
        sink.definedTerm_();
        sink.definition();
        sink.text(definition);
        sink.definition_();
        sink.definitionListItem_();
        sink.definitionList_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getDefinitionListBlock(definum, definition);

        assertEquals(expected, actual, "Wrong definition list!");
    }

    /**
     * Checks that the sequence <code>[figure(), figureGraphics(source),
     * figureCaption(), text(caption), figureCaption_(), figure_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getFigureBlock getFigureBlock}(source, caption).
     */
    @Test
    public void testFigure() {
        String source = "figure.jpg";
        String caption = "Figure_caption";
        sink.figure();
        sink.figureGraphics(source);
        sink.figureCaption();
        sink.text(caption);
        sink.figureCaption_();
        sink.figure_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getFigureBlock(source, caption);

        if (isXmlSink()) {
            assertThat(wrapXml(actual), isIdenticalTo(wrapXml(expected)));
        } else {
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testFigureWithoutCaption() {
        String source = "figure.jpg";
        sink.figure();
        sink.figureGraphics(source);
        sink.figure_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getFigureBlock(source, null);

        if (isXmlSink()) {
            assertThat(wrapXml(actual), isIdenticalTo(wrapXml(expected)));
        } else {
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testFigureFromUrl() {
        String source = "http://www.gravatar.com/avatar/cdbe99fe3d6af6a18dd8c35b0687a50b?d=mm&s=60";
        sink.figure();
        sink.figureGraphics(source);
        sink.figure_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getFigureBlock(source, null);

        if (isXmlSink()) {
            assertThat(wrapXml(actual), isIdenticalTo(wrapXml(expected)));
        } else {
            assertEquals(expected, actual);
        }
    }

    /**
     * Checks that the sequence <code>[table(),
     * tableRows({Sink.JUSTIFY_CENTER, JUSTIFY_DEFAULT}, false), tableRow(),
     * tableCell(), text(cell), tableCell_(),
     * tableCell(), text(cell), tableCell_(),
     * tableCell(), text(cell), tableCell_(),
     * tableRow_(), tableRows_(), tableCaption(), text(caption), tableCaption_(), table_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getTableBlock getTableBlock}(cell, caption).
     */
    @Test
    public void testTable() {
        String cell = "cell";
        String caption = "Table_caption";
        int[] justify = {Sink.JUSTIFY_CENTER, Sink.JUSTIFY_DEFAULT};
        sink.table();
        sink.tableRows(justify, false);
        sink.tableRow();
        sink.tableCell();
        sink.text(cell);
        sink.tableCell_();
        sink.tableCell();
        sink.text(cell);
        sink.tableCell_();
        sink.tableCell();
        sink.text(cell);
        sink.tableCell_();
        sink.tableRow_();
        sink.tableRows_();
        sink.tableCaption();
        sink.text(caption);
        sink.tableCaption_();
        sink.table_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getTableBlock(cell, caption);

        if (isXmlSink()) {
            assertThat(wrapXml(actual), isIdenticalTo(wrapXml(expected)));
        } else {
            assertEquals(expected, actual);
        }
    }

    /**
     * Checks that the sequence of table with header methods
     * invoked on the current sink, produces the same result as
     * {@link #getTableWithHeaderBlock(String...)}.
     */
    @Test
    public void testTableWithHeader() {
        int[] justify = {Sink.JUSTIFY_LEFT, Sink.JUSTIFY_RIGHT, Sink.JUSTIFY_CENTER};
        sink.table();
        sink.tableRows(justify, false);
        try (IntStream cellStream = getCellStreamForNewRow(3)) {
            cellStream.forEach(col -> {
                sink.tableHeaderCell();
                sink.text("header_" + col);
                sink.tableHeaderCell_();
            });
        }
        try (IntStream cellStream = getCellStreamForNewRow(3)) {
            cellStream.forEach(col -> {
                sink.tableCell();
                sink.text("row1_" + col);
                sink.tableCell_();
            });
        }
        try (IntStream cellStream = getCellStreamForNewRow(3)) {
            cellStream.forEach(col -> {
                sink.tableCell();
                sink.text("row2_" + col);
                sink.tableCell_();
            });
        }

        sink.tableRows_();
        sink.table_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getTableWithHeaderBlock("header_", "row1_", "row2_");

        if (isXmlSink()) {
            assertThat(wrapXml(actual), isIdenticalTo(wrapXml(expected)));
        } else {
            assertEquals(expected, actual);
        }
    }

    private IntStream getCellStreamForNewRow(int numColumns) {
        sink.tableRow();
        return IntStream.range(0, numColumns).onClose(() -> sink.tableRow_());
    }

    /**
     * Checks that the sequence <code>[paragraph(), text(text),
     * paragraph_()]</code>, invoked on the current sink, produces
     * the same result as {@link #getParagraphBlock getParagraphBlock}(text).
     */
    @Test
    public void testParagraph() {
        String text = "Text";
        sink.paragraph();
        sink.text(text);
        sink.paragraph_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getParagraphBlock(text);

        assertEquals(expected, actual, "Wrong paragraph!");
    }

    /**
     * Checks that the sequence <code>[data(), text(text),
     * data_()]</code>, invoked on the current sink, produces
     * the same result as {@link #getDataBlock getDataBlock}(text).
     */
    @Test
    public void testData() {
        String value = "Value";
        String text = "Text";
        sink.data(value);
        sink.text(text);
        sink.data_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getDataBlock(value, text);

        assertEquals(expected, actual, "Wrong data!");
    }

    /**
     * Checks that the sequence <code>[time(), text(text),
     * time_()]</code>, invoked on the current sink, produces
     * the same result as {@link #getTimeBlock getTimeBlock}(text).
     */
    @Test
    public void testTime() {
        String datetime = "DateTime";
        String text = "Text";
        sink.time(datetime);
        sink.text(text);
        sink.time_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getTimeBlock(datetime, text);

        assertEquals(expected, actual, "Wrong time!");
    }

    /**
     * Checks that the sequence <code>[address(), text(text),
     * address_()]</code>, invoked on the current sink, produces
     * the same result as {@link #getAddressBlock getAddressBlock}(text).
     */
    @Test
    public void testAddress() {
        String text = "Text";
        sink.address();
        sink.text(text);
        sink.address_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getAddressBlock(text);

        assertEquals(expected, actual, "Wrong address!");
    }

    /**
     * Checks that the sequence <code>[blockquote(), text(text),
     * blockquote_()]</code>, invoked on the current sink, produces
     * the same result as {@link #getBlockquoteBlock}(text).
     */
    @Test
    public void testBlockquote() {
        String text = "Text";
        sink.blockquote();
        sink.text(text);
        sink.blockquote_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getBlockquoteBlock(text);

        assertEquals(expected, actual, "Wrong blockquote!");
    }

    /**
     * Checks that the sequence <code>[division(), text(text),
     * division_()]</code>, invoked on the current sink, produces
     * the same result as {@link #getDivisionBlock getDivisionBlock}(text).
     */
    @Test
    public void testDivider() {
        String text = "Text";
        sink.division();
        sink.text(text);
        sink.division_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getDivisionBlock(text);

        assertEquals(expected, actual, "Wrong division!");
    }

    /**
     * Checks that the sequence <code>[verbatim(null), text(text),
     * verbatim_()]</code>, invoked on the current sink, produces the
     * same result as {@link #getVerbatimeBlock getVerbatimBlock}(text).
     */
    @Test
    public void testVerbatim() {
        String text = "Text";
        sink.verbatim(null);
        sink.text(text);
        sink.verbatim_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getVerbatimBlock(text);

        assertEquals(expected, actual, "Wrong verbatim!");
    }

    /**
     * Checks that the sequence <code>[verbatim(SinkEventAttributeSet.SOURCE), text(text),
     * verbatim_()]</code>, invoked on the current sink, produces the
     * same result as {@link #getVerbatimSourceBlock getVerbatimSourceBlock}(text).
     */
    @Test
    public void testVerbatimSource() {
        String text = "Text";
        sink.verbatim(SinkEventAttributeSet.SOURCE);
        sink.text(text);
        sink.verbatim_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getVerbatimSourceBlock(text);

        assertEquals(expected, actual, "Wrong verbatim source block!");
    }

    /**
     * Checks that the sequence <code>[horizontalRule()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getHorizontalRuleBlock getHorizontalRuleBlock()}.
     */
    @Test
    public void testHorizontalRule() {
        sink.horizontalRule();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getHorizontalRuleBlock();

        assertEquals(expected, actual, "Wrong horizontal rule!");
    }

    /**
     * Checks that the sequence <code>[pageBreak()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getPageBreakBlock getPageBreakBlock()}.
     */
    @Test
    public void testPageBreak() {
        sink.pageBreak();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getPageBreakBlock();

        assertEquals(expected, actual, "Wrong pageBreak!");
    }

    /**
     * Checks that the sequence <code>[anchor(anchor), text(anchor),
     * anchor_()]</code>, invoked on the current sink, produces the same
     * result as {@link #getAnchorBlock getAnchorBlock}(anchor).
     */
    @Test
    public void testAnchor() {
        String anchor = "Anchor";
        sink.anchor(anchor);
        sink.text(anchor);
        sink.anchor_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getAnchorBlock(anchor);

        assertEquals(expected, actual, "Wrong anchor!");
    }

    /**
     * Checks that the sequence <code>[link(link), text(text),
     * link_()]</code>, invoked on the current sink, produces the same
     * result as {@link #getLinkBlock getLinkBlock}(link, text).
     */
    @Test
    public void testLink() {
        String link = "#Link";
        String text = "Text";
        sink.link(link);
        sink.text(text);
        sink.link_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getLinkBlock(link, text);

        assertEquals(expected, actual, "Wrong link!");
    }

    /**
     * Checks that the sequence <code>[inline(), text(text), inline_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getInlineBlock getInlineBlock}(text).
     */
    @Test
    public void testInline() {
        String text = "Inline";
        sink.inline();
        sink.text(text);
        sink.inline_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getInlineBlock(text);

        assertEquals(expected, actual, "Wrong inline!");
    }

    /**
     * Checks that the sequence <code>[inline(bold), text(text), inline_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getInlineBoldBlock getInlineBoldBlock}(text).
     */
    @Test
    public void testInlineBold() {
        String text = "InlineBold";
        sink.inline(SinkEventAttributeSet.Semantics.BOLD);
        sink.text(text);
        sink.inline_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getInlineBoldBlock(text);

        assertEquals(expected, actual, "Wrong inline bold!");
    }

    /**
     * Checks that the sequence <code>[inline(italic), text(text), inline_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getInlineBoldBlock getInlineBoldBlock}(text).
     */
    @Test
    public void testInlineItalic() {
        String text = "InlineItalic";
        sink.inline(SinkEventAttributeSet.Semantics.ITALIC);
        sink.text(text);
        sink.inline_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getInlineItalicBlock(text);

        assertEquals(expected, actual, "Wrong inline italic!");
    }

    /**
     * Checks that the sequence <code>[inline(code), text(text), inline_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getInlineBoldBlock getInlineBoldBlock}(text).
     */
    @Test
    public void testInlineCode() {
        String text = "InlineCode";
        sink.inline(SinkEventAttributeSet.Semantics.CODE);
        sink.text(text);
        sink.inline_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getInlineCodeBlock(text);

        assertEquals(expected, actual, "Wrong inline code!");
    }

    /**
     * Checks that the sequence <code>[lineBreak()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getLineBreakBlock getLineBreakBlock()}.
     */
    @Test
    public void testLineBreak() {
        sink.lineBreak();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getLineBreakBlock();

        assertEquals(expected, actual, "Wrong lineBreak!");
    }

    /**
     * Checks that the sequence <code>[lineBreakOpportunity()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getLineBreakOpportunityBlock getLineBreakOpportunityBlock()}.
     */
    @Test
    public void testLineBreakOpportunity() {
        sink.lineBreakOpportunity();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getLineBreakOpportunityBlock();

        assertEquals(expected, actual, "Wrong lineBreakOpportunity!");
    }

    /**
     * Checks that the sequence <code>[nonBreakingSpace()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getNonBreakingSpaceBlock getNonBreakingSpaceBlock()}.
     */
    @Test
    public void testNonBreakingSpace() {
        sink.nonBreakingSpace();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getNonBreakingSpaceBlock();

        assertEquals(expected, actual, "Wrong nonBreakingSpace!");
    }

    /**
     * Checks that the sequence <code>[text(text)]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getTextBlock getTextBlock()}(text).
     */
    @Test
    public void testText() {
        String text = "~,_=,_-,_+,_*,_[,_],_<,_>,_{,_},_\\";
        sink.text(text);
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getTextBlock(text);

        assertEquals(expected, actual, "Wrong text!");
    }

    /**
     * Checks that the sequence <code>[rawText(text)]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getRawTextBlock getRawTextBlock}(text).
     */
    @Test
    public void testRawText() {
        String text = "~,_=,_-,_+,_*,_[,_],_<,_>,_{,_},_\\";
        sink.rawText(text);
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getRawTextBlock(text);

        assertEquals(expected, actual, "Wrong rawText!");
    }

    /**
     * Checks that the sequence <code>[comment(comment)]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getCommentBlock getCommentBlock}(comment).
     * @since 1.1.1
     */
    @Test
    public void testComment() {
        String comment = "Simple comment with ----";
        sink.comment(comment);
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getCommentBlock(comment);

        assertEquals(expected, actual, "Wrong comment!");

        testWriter.reset();
        sink = createSink(testWriter);

        comment = "-";
        sink.comment(comment);
        sink.flush();
        sink.close();

        actual = testWriter.toString();
        expected = getCommentBlock(comment);

        assertEquals(expected, actual, "Wrong comment!");
    }

    /**
     * Checks the line separator between two consecutive comments.
     */
    @Test
    public void testTwoConsecutiveInlineComments() {
        String comment = "Simple comment";
        sink.comment(comment);
        sink.comment(comment);
        sink.flush();
        sink.close();
        assertEquals(getCommentBlock(comment) + getCommentBlock(comment), testWriter.toString(), "Wrong comment!");
    }

    /**
     * Checks the line separator between two consecutive comments.
     */
    @Test
    public void testTwoConsecutiveBlockComments() {
        String comment = "Simple comment";
        sink.comment(comment, true);
        sink.comment(comment, true);
        sink.flush();
        sink.close();
        assertEquals(
                getCommentBlock(comment) + EOL + getCommentBlock(comment) + EOL,
                testWriter.toString(),
                "Wrong comment!");
    }

    /**
     * Checks the line separator between comment and paragraph (in most markup languages a block element which needs to start in the new line)
     */
    @Test
    public void testCommentFollowedByParagraph() {
        String comment = "Simple comment";
        sink.comment(comment);
        sink.paragraph();
        sink.text("Paragraph");
        sink.paragraph_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getCommentBlockFollowedByParagraph(comment, "Paragraph");

        assertEquals(expected, actual, "Wrong comment!");
    }

    // ----------------------------------------------------------------------
    // Utility methods
    // ----------------------------------------------------------------------

    /**
     * Returns the sink that is currently being tested.
     * @return The current test sink.
     */
    protected Sink getSink() {
        return sink;
    }

    /**
     * Returns a String representation of all events that have been written to the sink.
     * @return The Sink content as a String.
     */
    protected String getSinkContent() {
        return testWriter.toString();
    }

    /**
     * Returns the directory where all sink test output will go.
     * @return The test output directory.
     */
    protected String getOutputDir() {
        return "sink/";
    }

    // ----------------------------------------------------------------------
    // Abstract methods the individual SinkTests must provide
    // ----------------------------------------------------------------------

    /**
     * This method allows to use the correct Writer in {@link #testTestDocument()}.
     *
     * @return <code>true</code> if the Sink is an XML one, <code>false</code> otherwise.
     * @see #testTestDocument()
     */
    protected abstract boolean isXmlSink();

    /**
     * Return a new instance of the sink that is being tested.
     * @param writer The writer for the sink.
     * @return A new sink.
     */
    protected abstract Sink createSink(Writer writer);

    /**
     * Returns a title block generated by this sink.
     * @param title The title to use.
     * @return The result of invoking a title block on the current sink.
     * @see #testTitle()
     */
    protected abstract String getTitleBlock(String title);

    /**
     * Returns an author block generated by this sink.
     * @param author The author to use.
     * @return The result of invoking an author block on the current sink.
     * @see #testAuthor()
     */
    protected abstract String getAuthorBlock(String author);

    /**
     * Returns a date block generated by this sink.
     * @param date The date to use.
     * @return The result of invoking a date block on the current sink.
     * @see #testDate()
     */
    protected abstract String getDateBlock(String date);

    /**
     * Returns a head block generated by this sink.
     * @return The result of invoking a head block on the current sink.
     * @see #testHead()
     */
    protected abstract String getHeadBlock();

    /**
     * Returns a body block generated by this sink.
     * @return The result of invoking a body block on the current sink.
     * @see #testBody()
     */
    protected abstract String getBodyBlock();

    /**
     * Returns an article block generated by this sink.
     * @return The result of invoking an article block on the current sink.
     * @see #testArticle()
     */
    protected abstract String getArticleBlock();

    /**
     * Returns an navigation block generated by this sink.
     * @return The result of invoking an navigation block on the current sink.
     * @see #testNavigation()
     */
    protected abstract String getNavigationBlock();

    /**
     * Returns a sidebar block generated by this sink.
     * @return The result of invoking an sidebar block on the current sink.
     * @see #testSidebar()
     */
    protected abstract String getSidebarBlock();

    /**
     * Returns a SectionTitle block generated by this sink.
     * @param title The title to use.
     * @return The result of invoking a SectionTitle block on the current sink.
     * @see #testSectionTitle()
     */
    protected abstract String getSectionTitleBlock(String title);

    /**
     * Returns a Section1 block generated by this sink.
     * @param title The title to use.
     * @return The result of invoking a Section1 block on the current sink.
     * @see #testSection1()
     */
    protected abstract String getSection1Block(String title);

    /**
     * Returns a Section2 block generated by this sink.
     * @param title The title to use.
     * @return The result of invoking a Section2 block on the current sink.
     * @see #testSection2()
     */
    protected abstract String getSection2Block(String title);

    /**
     * Returns a Section3 block generated by this sink.
     * @param title The title to use.
     * @return The result of invoking a Section3 block on the current sink.
     * @see #testSection3()
     */
    protected abstract String getSection3Block(String title);

    /**
     * Returns a Section4 block generated by this sink.
     * @param title The title to use.
     * @return The result of invoking a Section4 block on the current sink.
     * @see #testSection4()
     */
    protected abstract String getSection4Block(String title);

    /**
     * Returns a Section5 block generated by this sink.
     * @param title The title to use.
     * @return The result of invoking a Section5 block on the current sink.
     * @see #testSection5()
     */
    protected abstract String getSection5Block(String title);

    /**
     * Returns a Section6 block generated by this sink.
     * @param title The title to use.
     * @return The result of invoking a Section6 block on the current sink.
     * @see #testSection6()
     */
    protected abstract String getSection6Block(String title);

    /**
     * Returns a header block generated by this sink.
     * @return The result of invoking a header block on the current sink.
     * @see #testHeader()
     */
    protected abstract String getHeaderBlock();

    /**
     * Returns a content block generated by this sink.
     * @return The result of invoking a content block on the current sink.
     * @see #testContent()
     */
    protected abstract String getContentBlock();

    /**
     * Returns a footer block generated by this sink.
     * @return The result of invoking a footer block on the current sink.
     * @see #testFooter()
     */
    protected abstract String getFooterBlock();

    /**
     * Returns a list block generated by this sink.
     * @param item The item to use.
     * @return The result of invoking a list block on the current sink.
     * @see #testList()
     */
    protected abstract String getListBlock(String item);

    /**
     * Returns a NumberedList block generated by this sink.
     * @param item The item to use.
     * @return The result of invoking a NumberedList block on the current sink.
     * @see #testNumberedList()
     */
    protected abstract String getNumberedListBlock(String item);

    /**
     * Returns a DefinitionList block generated by this sink.
     * @param definum The term to define.
     * @param definition The definition.
     * @return The result of invoking a DefinitionList block on the current sink.
     * @see #testDefinitionList()
     */
    protected abstract String getDefinitionListBlock(String definum, String definition);

    /**
     * Returns a Figure block generated by this sink.
     * @param source The figure source string.
     * @param caption The caption to use (may be null).
     * @return The result of invoking a Figure block on the current sink.
     * @see #testFigure()
     */
    protected abstract String getFigureBlock(String source, String caption);

    /**
     * Returns a Table block generated by this sink.
     * @param cell A table cell to use.
     * @param caption The caption to use (may be null).
     * @return The result of invoking a Table block on the current sink.
     * @see #testTable()
     */
    protected abstract String getTableBlock(String cell, String caption);

    /**
     * Returns a Table with header block on the current sink.
     * @param rowPrefixes the text prefix used in the individual rows.
     * @return the result of invoking a Table with header block on the current sink.
     * @see #testTableWithHeader()
     */
    protected abstract String getTableWithHeaderBlock(String... rowPrefixes);

    /**
     * Returns a Paragraph block generated by this sink.
     * @param text The text to use.
     * @return The result of invoking a Paragraph block on the current sink.
     * @see #testParagraph()
     */
    protected abstract String getParagraphBlock(String text);

    /**
     * Returns a Data block generated by this sink.
     * @param value The value to use.
     * @param text The text to use.
     * @return The result of invoking a Data block on the current sink.
     * @see #testData()
     */
    protected abstract String getDataBlock(String value, String text);

    /**
     * Returns a Time block generated by this sink.
     * @param datetime The datetime to use.
     * @param text The text to use.
     * @return The result of invoking a Time block on the current sink.
     * @see #testTime()
     */
    protected abstract String getTimeBlock(String datetime, String text);

    /**
     * Returns an Address block generated by this sink.
     * @param text The text to use.
     * @return The result of invoking an Address block on the current sink.
     * @see #testAddress()
     */
    protected abstract String getAddressBlock(String text);

    /**
     * Returns a Blockquote block generated by this sink.
     * @param text The text to use.
     * @return The result of invoking a Blockquote block on the current sink.
     * @see #testBlockquote()
     */
    protected abstract String getBlockquoteBlock(String text);

    /**
     * Returns a Division block generated by this sink.
     * @param text The text to use.
     * @return The result of invoking a Division block on the current sink.
     * @see #testDivider()
     */
    protected abstract String getDivisionBlock(String text);

    /**
     * Returns a Verbatim block generated by this sink.
     * @param text The text to use.
     * @return The result of invoking a Verbatim block on the current sink.
     * @see #testVerbatim()
     */
    protected abstract String getVerbatimBlock(String text);

    /**
     * Returns a Verbatim source block generated by this sink.
     * @param text The text to use.
     * @return The result of invoking a Verbatim source block on the current sink.
     * @see #testVerbatimSource()
     */
    protected abstract String getVerbatimSourceBlock(String text);

    /**
     * Returns a HorizontalRule block generated by this sink.
     * @return The result of invoking a HorizontalRule block on the current sink.
     * @see #testHorizontalRule()
     */
    protected abstract String getHorizontalRuleBlock();

    /**
     * Returns a PageBreak block generated by this sink.
     * @return The result of invoking a PageBreak block on the current sink.
     * @see #testPageBreak()
     */
    protected abstract String getPageBreakBlock();

    /**
     * Returns a Anchor block generated by this sink.
     * @param anchor The anchor to use.
     * @return The result of invoking a Anchor block on the current sink.
     * @see #testAnchor()
     */
    protected abstract String getAnchorBlock(String anchor);

    /**
     * Returns a Link block generated by this sink.
     * @param link The link to use.
     * @param text The link text.
     * @return The result of invoking a Link block on the current sink.
     * @see #testLink()
     */
    protected abstract String getLinkBlock(String link, String text);

    /**
     * Returns an Inline block generated by this sink.
     * @param text The text to use.
     * @return The result of invoking a Inline block on the current sink.
     * @see #testInline()
     */
    protected abstract String getInlineBlock(String text);

    /**
     * Returns an Inline italic block generated by this sink.
     * @param text The text to use.
     * @return The result of invoking a Inline italic block on the current sink.
     * @see #testInlineItalic()
     */
    protected abstract String getInlineItalicBlock(String text);

    /**
     * Returns an Inline bold block generated by this sink.
     * @param text The text to use.
     * @return The result of invoking a Inline bold block on the current sink.
     * @see #testInlineBold()
     */
    protected abstract String getInlineBoldBlock(String text);

    /**
     * Returns an Inline code block generated by this sink.
     * @param text The text to use.
     * @return The result of invoking a Inline code block on the current sink.
     * @see #testInlineBold()
     */
    protected abstract String getInlineCodeBlock(String text);

    /**
     * Returns a LineBreak block generated by this sink.
     * @return The result of invoking a LineBreak block on the current sink.
     * @see #testLineBreak()
     */
    protected abstract String getLineBreakBlock();

    /**
     * Returns a LineBreakOpportunity block generated by this sink.
     * @return The result of invoking a LineBreakOpportunity block on the
     * current sink.
     * @see #testLineBreakOpportunity()
     */
    protected abstract String getLineBreakOpportunityBlock();

    /**
     * Returns a NonBreakingSpace block generated by this sink.
     * @return The result of invoking a NonBreakingSpace block
     * on the current sink.
     * @see #testNonBreakingSpace()
     */
    protected abstract String getNonBreakingSpaceBlock();

    /**
     * Returns a Text block generated by this sink.
     * @param text The text to use.
     * @return The result of invoking a Text block on the current sink.
     * @see #testText()
     */
    protected abstract String getTextBlock(String text);

    /**
     * Returns a RawText block generated by this sink.
     * @param text The text to use.
     * @return The result of invoking a RawText block on the current sink.
     * @see #testRawText()
     */
    protected abstract String getRawTextBlock(String text);

    /**
     * Returns a comment block generated by this sink.
     * @param text The text to use.
     * @return The result of invoking a comment block on the current sink.
     * @see #testComment()
     * @since 1.1.1
     */
    protected abstract String getCommentBlock(String text);

    /**
     * Returns a comment block generated by this sink followed by a paragraph block
     * @param text The text to use.
     * @return The result of invoking a comment block followed by a paragraph block on the current sink.
     * @see #testCommentFollowedByParagraph()
     * @since 2.1.0
     */
    protected abstract String getCommentBlockFollowedByParagraph(String comment, String paragraph);

    protected final void verifyValignSup(String text) {
        sink.text("ValignSup", new SinkEventAttributeSet(SinkEventAttributes.VALIGN, "sup"));
        sink.flush();
        sink.close();

        String actual = testWriter.toString();

        assertEquals("Wrong valign sup!", text, actual);
    }

    protected final void verifyValignSub(String text) {
        sink.text("ValignSub", new SinkEventAttributeSet(SinkEventAttributes.VALIGN, "sub"));
        sink.flush();
        sink.close();

        String actual = testWriter.toString();

        assertEquals("Wrong valign sub!", text, actual);
    }

    protected final void verifyDecorationUnderline(String text) {
        sink.text("DecorationUnderline", new SinkEventAttributeSet(SinkEventAttributes.DECORATION, "underline"));
        sink.flush();
        sink.close();

        String actual = testWriter.toString();

        assertEquals("Wrong decoration underline!", text, actual);
    }

    protected final void verifyDecorationLineThrough(String text) {
        sink.text("DecorationLineThrough", new SinkEventAttributeSet(SinkEventAttributes.DECORATION, "line-through"));
        sink.flush();
        sink.close();

        String actual = testWriter.toString();

        assertEquals("Wrong decoration line-through!", text, actual);
    }
}
