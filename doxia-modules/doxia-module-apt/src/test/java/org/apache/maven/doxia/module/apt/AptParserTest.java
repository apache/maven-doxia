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

import javax.inject.Inject;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

import org.apache.maven.doxia.parser.AbstractParser;
import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.impl.SinkEventElement;
import org.apache.maven.doxia.sink.impl.SinkEventTestingSink;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 */
class AptParserTest extends AbstractParserTest {

    @Inject
    private AptParser parser;

    protected AbstractParser createParser() {
        return parser;
    }

    protected String parseFileToAptSink(String file) throws ParseException, IOException {
        try (StringWriter output = new StringWriter();
                Reader reader = getTestReader(file)) {
            Sink sink = new AptSink(output);
            createParser().parse(reader, sink);

            return output.toString();
        }
    }

    @Test
    void lineBreak() throws Exception {
        String linebreak = parseFileToAptSink("test/linebreak");

        assertTrue(linebreak.contains("Line\\" + EOL + "break."));
    }

    @Test
    void snippetMacro() throws Exception {
        String macro = parseFileToAptSink("test/macro");

        assertTrue(macro.contains("<modelVersion\\>4.0.0\\</modelVersion\\>"));
    }

    @Test
    void commentsBeforeTitle() throws Exception {
        String comments = parseFileToAptSink("test/comments");

        assertEquals(
                0,
                comments.indexOf("~~ comments before title" + EOL + "~~ like a license header, for example" + EOL
                        + " -----" + EOL + " Test DOXIA-379"));
    }

    @Test
    void commentsAfterParagraph() throws Exception {
        SinkEventTestingSink sink = new SinkEventTestingSink();
        try (Reader reader = getTestReader("test/comments2")) {
            createParser().parse(reader, sink);
        }

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertSinkStartsWith(
                it,
                "head",
                "head_",
                "body",
                "section1",
                "sectionTitle1",
                "text",
                "sectionTitle1_",
                "paragraph",
                "text",
                "paragraph_");
        assertSinkEquals(it.next(), "comment", "some comment", Boolean.TRUE);
        assertSinkEquals(it.next(), "comment", "another comment", Boolean.TRUE);
        assertSinkEquals(it, "paragraph", "text", "paragraph_", "section1_", "body_");
    }

    @Test
    void snippet() throws Exception {
        // DOXIA-259

        SinkEventTestingSink sink = new SinkEventTestingSink();
        try (Reader reader = getTestReader("test/snippet")) {
            createParser().parse(reader, sink);
        }

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertSinkEquals(
                it,
                "head",
                "head_",
                "body",
                "list",
                "listItem",
                "text",
                "verbatim",
                "text",
                "verbatim_",
                "paragraph",
                "text",
                "paragraph_",
                "listItem_",
                "listItem",
                "text",
                "verbatim",
                "text",
                "verbatim_",
                "paragraph",
                "text",
                "paragraph_",
                "listItem_",
                "list_",
                "body_");
    }

    @Test
    void fontStyles() throws Exception {
        SinkEventTestingSink sink = new SinkEventTestingSink();
        try (Reader reader = getTestReader("test/font")) {
            createParser().parse(reader, sink);
        }

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertSinkStartsWith(it, "head", "head_", "body", "paragraph", "text", "italic");
        assertSinkEquals(it.next(), "text", "<Italic>", null);
        assertSinkStartsWith(it, "italic_", "text", "bold");
        assertSinkEquals(it.next(), "text", "<Bold>", null);
        assertSinkStartsWith(it, "bold_", "text", "monospaced");
        assertSinkEquals(it.next(), "text", "<Monospaced>", null);
    }

    @Test
    void snippetTrailingSpace() throws Exception {
        // DOXIA-425
        String text = "%{snippet|id=myid|file=pom.xml}  " + EOL;

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse(text, sink);

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertSinkEquals(it, "head", "head_", "body", "verbatim", "text", "verbatim_", "body_");
    }

    @Test
    void tocMacro() throws Exception {
        String toc = parseFileToAptSink("test/toc");

        // No section, only subsection 1 and 2
        assertTrue(toc.contains("* {{{SubSection_1.1}SubSection 1.1}}"));
        assertFalse(toc.contains("* {{{SubSection_1.1.2.1.1}SubSection 1.1.2.1.1}}"));
    }

    /**
     * Parses the test document test.apt and re-emits
     * it into parser/test.apt.
     *
     * @throws IOException if the test file cannot be read.
     * @throws ParseException if the test file cannot be parsed.
     */
    @Test
    void checkTestDocument() throws Exception {
        try (Writer writer = getTestWriter("test");
                Reader reader = getTestReader("test")) {
            Sink sink = new AptSink(writer);
            createParser().parse(reader, sink);
        }
    }

    @Test
    void verbatimSource() throws Exception {
        String text =
                "+--" + EOL + "verbatim source" + EOL + "+--" + EOL + "---" + EOL + "verbatim" + EOL + "---" + EOL;

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse(text, sink);

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertSinkStartsWith(it, "head", "head_", "body");
        assertSinkEquals(it.next(), "verbatim", SinkEventAttributeSet.SOURCE);
        assertSinkStartsWith(it, "text", "verbatim_");

        assertSinkEquals(it.next(), "verbatim", new Object[] {null});
        assertSinkEquals(it, "text", "verbatim_", "body_");
    }

    @Test
    void multiLinesInTableCells() throws Exception {
        String text = "*----------*--------------+----------------:" + EOL + " cell 1, | cell 1,2       | cell 1,3"
                + EOL + " 1       |                | "
                + EOL + "*----------*--------------+----------------:"
                + EOL + " cell 2,1 | cell 2,       | cell 2,3"
                + EOL + "          | 2             |"
                + EOL + "*----------*--------------+----------------:"
                + EOL + " cell 3,1 | cell 3,2      | cell 3,"
                + EOL + "          |               | 3"
                + EOL + "*----------*--------------+----------------:"
                + EOL;

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse(text, sink);

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertSinkStartsWith(it, "head", "head_", "body", "table", "tableRows", "tableRow", "tableCell");
        assertSinkEquals(it.next(), "text", "cell 1, 1", null);

        assertSinkStartsWith(it, "tableCell_", "tableCell");
        assertSinkEquals(it.next(), "text", "cell 1,2", null);

        assertSinkStartsWith(it, "tableCell_", "tableCell");
        assertSinkEquals(it.next(), "text", "cell 1,3", null);

        assertSinkStartsWith(it, "tableCell_", "tableRow_", "tableRow", "tableCell");
        assertSinkEquals(it.next(), "text", "cell 2,1", null);

        assertSinkStartsWith(it, "tableCell_", "tableCell");
        assertSinkEquals(it.next(), "text", "cell 2, 2", null);

        assertSinkStartsWith(it, "tableCell_", "tableCell");
        assertSinkEquals(it.next(), "text", "cell 2,3", null);

        assertSinkStartsWith(it, "tableCell_", "tableRow_", "tableRow", "tableCell");
        assertSinkEquals(it.next(), "text", "cell 3,1", null);

        assertSinkStartsWith(it, "tableCell_", "tableCell");
        assertSinkEquals(it.next(), "text", "cell 3,2", null);

        assertSinkStartsWith(it, "tableCell_", "tableCell");
        assertSinkEquals(it.next(), "text", "cell 3, 3", null);

        assertSinkEquals(it, "tableCell_", "tableRow_", "tableRows_", "table_", "body_");
    }

    @Test
    void lineBreakInTableCells() throws Exception {
        String text = "*----------*--------------+----------------:" + EOL + " cell 1,\\ | cell 1,2       | cell 1,3"
                + EOL + " 1       |                | "
                + EOL + "*----------*--------------+----------------:"
                + EOL + " cell 2,1 | cell 2,\\     | cell 2,3"
                + EOL + "          | 2             |"
                + EOL + "*----------*--------------+----------------:"
                + EOL + " cell 3,1 | cell 3,2      | cell 3,\\"
                + EOL + "          |               | 3"
                + EOL + "*----------*--------------+----------------:"
                + EOL;

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse(text, sink);

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertSinkStartsWith(it, "head", "head_", "body", "table", "tableRows", "tableRow", "tableCell");
        assertSinkEquals(it.next(), "text", "cell 1,\u00A0", null);

        assertEquals("lineBreak", it.next().getName());
        assertSinkEquals(it.next(), "text", "1", null);

        assertSinkStartsWith(it, "tableCell_", "tableCell");
        assertSinkEquals(it.next(), "text", "cell 1,2", null);

        assertSinkStartsWith(it, "tableCell_", "tableCell");
        assertSinkEquals(it.next(), "text", "cell 1,3", null);

        assertSinkStartsWith(it, "tableCell_", "tableRow_", "tableRow", "tableCell");
        assertSinkEquals(it.next(), "text", "cell 2,1", null);

        assertSinkStartsWith(it, "tableCell_", "tableCell");
        assertSinkEquals(it.next(), "text", "cell 2,\u00A0", null);

        assertEquals("lineBreak", it.next().getName());
        assertSinkEquals(it.next(), "text", "2", null);

        assertSinkStartsWith(it, "tableCell_", "tableCell");
        assertSinkEquals(it.next(), "text", "cell 2,3", null);

        assertSinkStartsWith(it, "tableCell_", "tableRow_", "tableRow", "tableCell");
        assertSinkEquals(it.next(), "text", "cell 3,1", null);

        assertSinkStartsWith(it, "tableCell_", "tableCell");
        assertSinkEquals(it.next(), "text", "cell 3,2", null);

        assertSinkStartsWith(it, "tableCell_", "tableCell");
        assertSinkEquals(it.next(), "text", "cell 3,\u00A0", null);

        assertEquals("lineBreak", it.next().getName());
        assertSinkEquals(it.next(), "text", "3", null);

        assertSinkEquals(it, "tableCell_", "tableRow_", "tableRows_", "table_", "body_");
    }

    @Test
    void doxia38() throws Exception {
        String text =
                "*----------*--------------*---------------*" + EOL + "| Centered |   Centered   |   Centered    |"
                        + EOL + "*----------*--------------+---------------:"
                        + EOL + "| Centered | Left-aligned | Right-aligned |"
                        + EOL + "*----------*--------------+---------------:";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse(text, sink);

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertSinkStartsWith(it, "head", "head_", "body", "table", "tableRows", "tableRow");
        assertSinkAttributeEquals(it.next(), "tableCell", SinkEventAttributeSet.ALIGN, "center");
        assertSinkEquals(it.next(), "text", "Centered", null);
        assertEquals("tableCell_", it.next().getName());

        assertSinkAttributeEquals(it.next(), "tableCell", SinkEventAttributeSet.ALIGN, "center");
        assertSinkEquals(it.next(), "text", "Centered", null);
        assertEquals("tableCell_", it.next().getName());

        assertSinkAttributeEquals(it.next(), "tableCell", SinkEventAttributeSet.ALIGN, "center");
        assertSinkEquals(it.next(), "text", "Centered", null);
        assertSinkStartsWith(it, "tableCell_", "tableRow_", "tableRow");

        assertSinkAttributeEquals(it.next(), "tableCell", SinkEventAttributeSet.ALIGN, "center");
        assertSinkEquals(it.next(), "text", "Centered", null);
        assertEquals("tableCell_", it.next().getName());

        assertSinkAttributeEquals(it.next(), "tableCell", SinkEventAttributeSet.ALIGN, "left");
        assertSinkEquals(it.next(), "text", "Left-aligned", null);
        assertEquals("tableCell_", it.next().getName());

        assertSinkAttributeEquals(it.next(), "tableCell", SinkEventAttributeSet.ALIGN, "right");
        assertSinkEquals(it.next(), "text", "Right-aligned", null);
        assertSinkEquals(it, "tableCell_", "tableRow_", "tableRows_", "table_", "body_");
    }

    @Test
    void specialCharactersInTables() throws Exception {
        // DOXIA-323, DOXIA-433
        String text = "  \\~ \\= \\- \\+ \\* \\[ \\] \\< \\> \\{ \\} \\\\ \\u2713" + EOL
                + EOL
                + "*--------------------------------------------------+---------------+" + EOL
                + "| \\~ \\= \\- \\+ \\* \\[ \\] \\< \\> \\{ \\} \\\\ \\u2713 | special chars |" + EOL
                + "*--------------------------------------------------+---------------+";

        SinkEventTestingSink sink = new SinkEventTestingSink();
        parser.parse(text, sink);

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertSinkStartsWith(it, "head", "head_", "body", "paragraph");
        assertSinkEquals(it.next(), "text", "~ = - + * [ ] < > { } \\ \u2713", null);

        assertSinkStartsWith(it, "paragraph_", "table", "tableRows", "tableRow", "tableCell");
        assertSinkEquals(it.next(), "text", "~ = - + * [ ] < > { } \\ \u2713", null);

        assertSinkEquals(
                it, "tableCell_", "tableCell", "text", "tableCell_", "tableRow_", "tableRows_", "table_", "body_");
    }

    @Test
    void spacesAndBracketsInAnchors() throws Exception {
        final String text = "  {Anchor with spaces (and brackets)}" + EOL
                + "  Link to {{Anchor with spaces (and brackets)}}" + EOL
                + "  {{{http://fake.api#method(with, args)}method(with, args)}}" + EOL;

        final SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse(text, sink);

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertSinkStartsWith(it, "head", "head_", "body", "paragraph");
        assertSinkEquals(it.next(), "anchor", "Anchor_with_spaces_.28and_brackets.29", null);

        assertSinkEquals(it.next(), "text", "Anchor with spaces (and brackets)", null);

        assertSinkStartsWith(it, "anchor_", "text");
        assertSinkEquals(it.next(), "link", "#Anchor_with_spaces_.28and_brackets.29", null);

        assertSinkEquals(it.next(), "text", "Anchor with spaces (and brackets)", null);

        assertSinkStartsWith(it, "link_", "text");
        assertSinkEquals(it.next(), "link", "http://fake.api#method(with, args)", null);

        assertSinkEquals(it.next(), "text", "method(with, args)", null);

        assertSinkEquals(it, "link_", "paragraph_", "body_");
    }

    @Test
    void sectionTitleAnchors() throws Exception {
        // DOXIA-420
        String text = "Enhancements to the APT format" + EOL + EOL + "{Title with anchor}" + EOL;

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse(text, sink);

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertSinkEquals(
                it,
                "head",
                "head_",
                "body",
                "section1",
                "sectionTitle1",
                "text",
                "sectionTitle1_",
                "section1_",
                "section1",
                "sectionTitle1",
                "anchor",
                "text",
                "anchor_",
                "sectionTitle1_",
                "section1_",
                "body_");
    }

    @Test
    void tableHeaders() throws Exception {
        // DOXIA-404
        String text = "*-----------+-----------+" + EOL + "|| Header 1 || Header 2 |"
                + EOL + "*-----------+-----------+"
                + EOL + "  Cell 1    | Cell 2    |"
                + EOL + "*-----------+-----------+"
                + EOL + "  Cell 3    | Cell 4    |"
                + EOL + "*-----------+-----------+"
                + EOL;

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse(text, sink);

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertSinkStartsWith(it, "head", "head_", "body", "table", "tableRows");
        assertSinkStartsWith(
                it,
                "tableRow",
                "tableHeaderCell",
                "text",
                "tableHeaderCell_",
                "tableHeaderCell",
                "text",
                "tableHeaderCell_",
                "tableRow_");
        assertSinkStartsWith(
                it, "tableRow", "tableCell", "text", "tableCell_", "tableCell", "text", "tableCell_", "tableRow_");
        assertSinkStartsWith(
                it, "tableRow", "tableCell", "text", "tableCell_", "tableCell", "text", "tableCell_", "tableRow_");
        assertSinkEquals(it, "tableRows_", "table_", "body_");
    }

    @Test
    void escapedPipeInTableCell() throws Exception {
        // DOXIA-479
        String text = "*---+---+" + EOL + "| cell \\| pipe | next cell " + EOL + "*---+---+" + EOL;

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse(text, sink);

        Iterator<SinkEventElement> it = sink.getEventList().iterator();
        assertSinkStartsWith(it, "head", "head_", "body", "table", "tableRows", "tableRow", "tableCell");
        assertSinkEquals(it.next(), "text", "cell | pipe", null);
        assertSinkStartsWith(it, "tableCell_", "tableCell");
        assertSinkEquals(it.next(), "text", "next cell", null);
        assertSinkEquals(it, "tableCell_", "tableRow_", "tableRows_", "table_", "body_");
    }

    @Test
    void literalAnchor() throws Exception {
        // DOXIA-397
        String text =
                "{{{../apidocs/groovyx/net/http/ParserRegistry.html##parseText(org.apache.http.HttpResponse)}ParserRegistry}}";

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse(text, sink);

        Iterator<SinkEventElement> it = sink.getEventList().iterator();
        assertSinkStartsWith(it, "head", "head_", "body", "section1", "sectionTitle1");
        assertSinkEquals(
                it.next(),
                "link",
                "../apidocs/groovyx/net/http/ParserRegistry.html#parseText(org.apache.http.HttpResponse)",
                null);
        assertSinkEquals(it.next(), "text", "ParserRegistry", null);
        assertSinkEquals(it, "link_", "sectionTitle1_", "section1_", "body_");
    }

    @Test
    void multipleAuthors() throws Exception {
        // DOXIA-691
        String head = parseFileToAptSink("test/authors");

        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse(head, sink);

        Iterator<SinkEventElement> it = sink.getEventList().iterator();

        assertSinkEquals(
                it, "head", "title", "text", "title_", "author", "text", "author_", "author", "text", "author_",
                "head_", "body", "body_");
        assertSinkEquals(sink.getEventList().get(5), "text", "Konrad Windszus", null);
        assertSinkEquals(sink.getEventList().get(8), "text", "Another author", null);
    }

    @Override
    protected String outputExtension() {
        return "apt";
    }

    @Override
    protected void assertEventPrefix(Iterator<SinkEventElement> eventIterator) {
        assertSinkStartsWith(eventIterator, "head", "head_", "body");
    }

    @Override
    protected void assertEventSuffix(Iterator<SinkEventElement> eventIterator) {
        assertSinkEquals(eventIterator, "body_");
    }

    @Override
    protected String getVerbatimSource() {
        return "---" + EOL + "<>{}=#*" + EOL + "---" + EOL;
    }

    @Override
    protected String getVerbatimCodeSource() {
        return "+--" + EOL + "<>{}=#*" + EOL + "+--" + EOL;
    }
}
