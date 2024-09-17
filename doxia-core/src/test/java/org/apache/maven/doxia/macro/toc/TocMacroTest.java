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
package org.apache.maven.doxia.macro.toc;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.maven.doxia.index.IndexEntry;
import org.apache.maven.doxia.index.IndexEntry.Type;
import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.macro.MacroRequest;
import org.apache.maven.doxia.markup.Markup;
import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Xhtml5BaseParser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.CreateAnchorsForIndexEntriesFactory;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.impl.SinkEventElement;
import org.apache.maven.doxia.sink.impl.SinkEventTestingSink;
import org.apache.maven.doxia.sink.impl.Xhtml5BaseSink;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test toc macro.
 *
 * @author ltheussl
 */
public class TocMacroTest {
    /**
     * Test of execute method, of class TocMacro.
     *
     * @throws MacroExecutionException if a macro fails during testing.
     */
    @Test
    public void testExecute() throws MacroExecutionException {
        String sourceContent = "<div><h1>h11</h1><h1>h12</h1><h2>h2</h2><h3>h3</h3><h1>h13</h1></div>";

        Xhtml5BaseParser parser = new Xhtml5BaseParser();

        Map<String, Object> macroParameters = new HashMap<>();
        macroParameters.put("section", "sec1");

        File basedir = new File("");

        SinkEventTestingSink sink = new SinkEventTestingSink();
        MacroRequest request = new MacroRequest(sourceContent, parser, macroParameters, basedir);
        TocMacro macro = new TocMacro();
        macro.execute(sink, request);

        Iterator<SinkEventElement> it = sink.getEventList().iterator();
        assertEquals("list", (it.next()).getName());
        assertEquals("listItem", (it.next()).getName());
        assertEquals("link", (it.next()).getName());
        assertEquals("text", (it.next()).getName());
        assertEquals("link_", (it.next()).getName());
        assertEquals("listItem_", (it.next()).getName());
        assertEquals("listItem", (it.next()).getName());
        assertEquals("link", (it.next()).getName());
        assertEquals("text", (it.next()).getName());
        assertEquals("link_", (it.next()).getName());
        assertEquals("list", (it.next()).getName());
        assertEquals("listItem", (it.next()).getName());
        assertEquals("link", (it.next()).getName());
        assertEquals("text", (it.next()).getName());
        assertEquals("link_", (it.next()).getName());
        assertEquals("list", (it.next()).getName());
        assertEquals("listItem", (it.next()).getName());
        assertEquals("link", (it.next()).getName());
        assertEquals("text", (it.next()).getName());
        assertEquals("link_", (it.next()).getName());
        assertEquals("listItem_", (it.next()).getName());
        assertEquals("list_", (it.next()).getName());
        assertEquals("listItem_", (it.next()).getName());
        assertEquals("list_", (it.next()).getName());
        assertEquals("listItem_", (it.next()).getName());
        assertEquals("listItem", (it.next()).getName());
        assertEquals("link", (it.next()).getName());
        assertEquals("text", (it.next()).getName());
        assertEquals("link_", (it.next()).getName());
        assertEquals("listItem_", (it.next()).getName());
        assertEquals("list_", (it.next()).getName());
        assertFalse(it.hasNext());

        // test parameters
        parser = new Xhtml5BaseParser();
        macroParameters.put("section", "2");
        macroParameters.put("fromDepth", "1");
        macroParameters.put("toDepth", "2");
        macroParameters.put("class", "myClass");
        macroParameters.put("id", "myId");

        sink.reset();
        request = new MacroRequest(sourceContent, parser, macroParameters, basedir);
        macro.execute(sink, request);

        it = sink.getEventList().iterator();
        SinkEventElement event = it.next();
        assertEquals("list", event.getName());
        SinkEventAttributeSet atts = (SinkEventAttributeSet) event.getArgs()[0];
        assertEquals("myId", atts.getAttribute("id"));
        assertEquals("myClass", atts.getAttribute("class"));
        assertEquals("listItem", (it.next()).getName());
        assertEquals("link", (it.next()).getName());
        event = it.next();
        assertEquals("text", event.getName());
        assertEquals("h12", event.getArgs()[0]);
        assertEquals("link_", (it.next()).getName());
        assertEquals("list", (it.next()).getName());
        assertEquals("listItem", (it.next()).getName());
        assertEquals("link", (it.next()).getName());
        event = it.next();
        assertEquals("text", event.getName());
        assertEquals("h2", event.getArgs()[0]);
        assertEquals("link_", (it.next()).getName());
        assertEquals("listItem_", (it.next()).getName());
        assertEquals("list_", (it.next()).getName());
        assertEquals("listItem_", (it.next()).getName());
        assertEquals("list_", (it.next()).getName());
        assertFalse(it.hasNext());
    }

    /**
     * Test DOXIA-366.
     *
     * @throws MacroExecutionException if a macro fails during testing.
     */
    @Test
    public void testTocStyle() throws MacroExecutionException {
        String sourceContent =
                "<div><h1>h<b>11</b></h1><h1>h<i>12</i></h1><h2>h<tt>2</tt></h2><h3>h3</h3><h1>h13</h1></div>";

        Xhtml5BaseParser parser = new Xhtml5BaseParser();

        Map<String, Object> macroParameters = new HashMap<>();
        macroParameters.put("section", "sec1");

        File basedir = new File("");

        StringWriter out = new StringWriter();
        Xhtml5BaseSink sink = new Xhtml5BaseSink(out);
        MacroRequest request = new MacroRequest(sourceContent, parser, macroParameters, basedir);
        TocMacro macro = new TocMacro();
        macro.execute(sink, request);

        assertTrue(out.toString().contains("<a href=\"#h11\">h11</a>"));
        assertTrue(out.toString().contains("<a href=\"#h12\">h12</a>"));
        assertTrue(out.toString().contains("<a href=\"#h2\">h2</a>"));
    }

    @Test
    public void testGenerateAnchors() throws ParseException, MacroExecutionException {
        String sourceContent = "<h1>1 Headline</h1>";
        File basedir = new File("");
        Xhtml5BaseParser parser = new Xhtml5BaseParser();
        MacroRequest request = new MacroRequest(sourceContent, parser, new HashMap<>(), basedir);
        TocMacro macro = new TocMacro();
        SinkEventTestingSink sink = new SinkEventTestingSink();

        macro.execute(sink, request);
        parser.parse(sourceContent, sink);

        Iterator<SinkEventElement> it = sink.getEventList().iterator();
        AbstractParserTest.assertSinkStartsWith(it, "list", "listItem");
        SinkEventElement link = it.next();
        assertEquals("link", link.getName());
        String actualLinkTarget = (String) link.getArgs()[0];
        AbstractParserTest.assertSinkEquals(it.next(), "text", "1 Headline", null);
        AbstractParserTest.assertSinkEquals(
                it, "link_", "listItem_", "list_", "section1", "sectionTitle1", "text", "sectionTitle1_");

        // check html output as well (without the actual TOC)
        StringWriter out = new StringWriter();
        Sink sink2 = new Xhtml5BaseSink(out);
        parser.addSinkWrapperFactory(new CreateAnchorsForIndexEntriesFactory());
        parser.parse(sourceContent, sink2);
        assertEquals(
                "<section><a id=\"" + actualLinkTarget.substring(1) + "\"></a>" + Markup.EOL + "<h1>1 Headline</h1>",
                out.toString());
    }

    @Test
    void testWriteTocWithEmptyAndNotApplicableIndexEntries() {
        TocMacro macro = new TocMacro();
        SinkEventTestingSink sink = new SinkEventTestingSink();
        final SinkEventAttributeSet atts = new SinkEventAttributeSet();
        IndexEntry rootEntry = new IndexEntry("root");
        new IndexEntry(rootEntry, null, Type.SECTION_1);
        // toc item on level 1
        IndexEntry entry = new IndexEntry(rootEntry, "id2", Type.SECTION_1);
        entry.setTitle("title 1");
        // toc item on level 2 (below toc item 1)
        new IndexEntry(entry, "id2-1", Type.SECTION_2).setTitle("title 1 - subtitle1");
        // item not relevant for toc (should be skipped)
        entry = new IndexEntry(rootEntry, "id3", Type.FIGURE);
        // toc item below skipped item
        new IndexEntry(entry, "id3-1", Type.SECTION_1).setTitle("title 4");
        macro.writeTocForIndexEntry(sink, atts, rootEntry);

        Iterator<SinkEventElement> it = sink.getEventList().iterator();
        AbstractParserTest.assertSinkStartsWith(it, "list");
        assertListItem(it, "#id2", "title 1");
        AbstractParserTest.assertSinkStartsWith(it, "list");
        assertListItem(it, "#id2-1", "title 1 - subtitle1");
        AbstractParserTest.assertSinkStartsWith(it, "listItem_", "list_", "listItem_");
        assertListItem(it, "#id3-1", "title 4");
        AbstractParserTest.assertSinkEquals(it, "listItem_", "list_");
    }

    void assertListItem(Iterator<SinkEventElement> it, String link, String text) {
        AbstractParserTest.assertSinkStartsWith(it, "listItem");
        AbstractParserTest.assertSinkEquals(it.next(), "link", link, null);
        AbstractParserTest.assertSinkEquals(it.next(), "text", text, null);
        AbstractParserTest.assertSinkStartsWith(it, "link_");
    }
}
