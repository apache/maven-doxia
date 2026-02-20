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
package org.apache.maven.doxia.macro.snippet;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.macro.MacroRequest;
import org.apache.maven.doxia.parser.Xhtml5BaseParser;
import org.apache.maven.doxia.sink.impl.SinkEventElement;
import org.apache.maven.doxia.sink.impl.SinkEventTestingSink;
import org.codehaus.plexus.testing.PlexusTest;
import org.junit.jupiter.api.Test;

import static org.codehaus.plexus.testing.PlexusExtension.getBasedir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test snippet macro.
 *
 * @author ltheussl
 */
@PlexusTest
class SnippetMacroTest {
    /**
     * Test of execute method, of class SnippetMacro.
     *
     * @throws MacroExecutionException if a macro fails during testing.
     */
    @Test
    void execute() throws Exception {
        Map<String, Object> macroParameters = new HashMap<>();
        macroParameters.put("file", "src/test/resources/macro/snippet/testSnippet.txt");
        macroParameters.put("encoding", "UTF-8");

        SinkEventTestingSink sink = executeSnippetMacro(macroParameters);

        Iterator<SinkEventElement> it = sink.getEventList().iterator();
        assertEquals("verbatim", it.next().getName());
        SinkEventElement event = it.next();
        assertEquals("text", event.getName());
        String snippet = (String) event.getArgs()[0];
        assertEquals("verbatim_", it.next().getName());
        assertFalse(it.hasNext());

        assertTrue(snippet.contains("preamble"));
        assertTrue(snippet.contains("first snippet"));
        assertTrue(snippet.contains("interlude"));
        assertTrue(snippet.contains("second snippet"));
        assertTrue(snippet.contains("conclusion"));

        // again

        macroParameters.put("id", "firstId");
        macroParameters.put("verbatim", "");
        sink = executeSnippetMacro(macroParameters);

        it = sink.getEventList().iterator();
        assertEquals("verbatim", it.next().getName());
        event = it.next();
        assertEquals("text", event.getName());
        snippet = (String) event.getArgs()[0];
        assertEquals("verbatim_", it.next().getName());
        assertFalse(it.hasNext());

        assertFalse(snippet.contains("preamble"));
        assertTrue(snippet.contains("first snippet"));
        assertFalse(snippet.contains("interlude"));
        assertFalse(snippet.contains("second snippet"));
        assertFalse(snippet.contains("conclusion"));

        // again

        macroParameters.put("id", "secondId");
        macroParameters.put("verbatim", "false");
        sink = executeSnippetMacro(macroParameters);

        it = sink.getEventList().iterator();
        event = it.next();
        assertEquals("rawText", event.getName());
        snippet = (String) event.getArgs()[0];
        assertFalse(it.hasNext());

        assertFalse(snippet.contains("preamble"));
        assertFalse(snippet.contains("first snippet"));
        assertFalse(snippet.contains("interlude"));
        assertTrue(snippet.contains("second snippet"));
        assertFalse(snippet.contains("conclusion"));

        // again

        macroParameters.put("id", "thirdId");
        macroParameters.put("verbatim", "false");
        sink = executeSnippetMacro(macroParameters);

        it = sink.getEventList().iterator();
        event = it.next();
        assertEquals("rawText", event.getName());
        snippet = (String) event.getArgs()[0];
        assertFalse(it.hasNext());

        // no need to verify the absence of the first and second snippets if tests above were successful
        assertTrue(snippet.contains("Этот сниппет в формате Unicode (UTF-8)"));

        // again
        // Shouldn't work because no snippet called "first" exists, only "firstId"
        macroParameters.put("id", "first");
        macroParameters.put("verbatim", "");
        macroParameters.put("ignoreDownloadError", "false");
        assertThrows(Exception.class, () -> executeSnippetMacro(macroParameters));
    }

    @Test
    void ignoreDownloadError() throws Exception {
        Map<String, Object> macroParameters = new HashMap<>();
        macroParameters.put("debug", "true");
        macroParameters.put("ignoreDownloadError", "true");
        macroParameters.put("url", "http://foo.bar.com/wine.txt");

        SinkEventTestingSink sink = executeSnippetMacro(macroParameters);

        Iterator<SinkEventElement> it = sink.getEventList().iterator();
        assertEquals("verbatim", it.next().getName());
        SinkEventElement event = it.next();
        assertEquals("text", event.getName());
        String snippet = (String) event.getArgs()[0];
        assertTrue(snippet.contains("Error during retrieving content"));
    }

    @Test
    public void testIndentHandling() throws MacroExecutionException {
        Map<String, Object> macroParameters = new HashMap<>();
        macroParameters.put("file", "src/test/resources/macro/snippet/testSnippet-indent.txt");
        macroParameters.put("encoding", "UTF-8");
        macroParameters.put("id", "firstId");
        macroParameters.put("verbatim", "false");

        // 1. Ensure leading tabs are trimmed
        SinkEventTestingSink sink = executeSnippetMacro(macroParameters);
        String snippet = (String) sink.getEventList().get(0).getArgs()[0];
        assertEquals(1, sink.getEventList().size(), "There should only be one event");
        assertEquals("first snippet\n", snippet);

        // 2. Ensure leading spaces are trimmed
        macroParameters.put("id", "secondId");
        sink = executeSnippetMacro(macroParameters);
        snippet = (String) sink.getEventList().get(0).getArgs()[0];
        assertEquals("second snippet\n", snippet);

        // 3. Ensure empty lines don't break the minIndent calculation
        macroParameters.put("id", "thirdId");
        sink = executeSnippetMacro(macroParameters);
        snippet = (String) sink.getEventList().get(0).getArgs()[0];
        String[] lines = snippet.split(System.getProperty("line.separator"));
        assertEquals("Line1", lines[0], "There should not be any leading indentation since the minIndent is two");
        assertEquals("	Line2", lines[1]);
        assertEquals("", lines[2]);
        assertEquals("", lines[3]);
        assertEquals("", lines[4]);
        assertEquals("	", lines[5], "The tab should be preserved");
        assertEquals("		Line6", lines[6]);
    }

    private SinkEventTestingSink executeSnippetMacro(Map<String, Object> macroParameters)
            throws MacroExecutionException {
        File basedir = new File(getBasedir());

        Xhtml5BaseParser parser = new Xhtml5BaseParser();

        SinkEventTestingSink sink = new SinkEventTestingSink();

        MacroRequest request = new MacroRequest(null, parser, macroParameters, basedir);
        SnippetMacro macro = new SnippetMacro();
        macro.execute(sink, request);

        return sink;
    }
}
