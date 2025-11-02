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
package org.apache.maven.doxia.util;

import java.net.URLEncoder;

import org.codehaus.plexus.testing.PlexusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test case for <code>HtmlTools</code>.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 */
@PlexusTest
class HtmlToolsTest {
    /**
     * Verify the expected results.
     */
    @Test
    void escapeHTML() {
        assertEquals("", HtmlTools.escapeHTML(null));
        assertEquals("", HtmlTools.escapeHTML(""));
        assertEquals("\u0009", HtmlTools.escapeHTML("\u0009"));
        assertEquals("\u0001", HtmlTools.escapeHTML("\u0001"));

        // Predefined entities
        assertEquals("&lt;", HtmlTools.escapeHTML("<"));
        assertEquals("&gt;", HtmlTools.escapeHTML(">"));
        assertEquals("&amp;", HtmlTools.escapeHTML("&"));
        assertEquals("&quot;", HtmlTools.escapeHTML("\""));
        assertEquals("&apos;", HtmlTools.escapeHTML("\'"));
        assertEquals("\'", HtmlTools.escapeHTML("\'", false));

        // xml mode
        assertEquals("&amp;amp;", HtmlTools.escapeHTML("&amp;"));
        assertEquals("\u00e4", HtmlTools.escapeHTML("\u00e4", true));
        assertEquals("&#xe4;", HtmlTools.escapeHTML("\u00e4", false));
        assertEquals("&#x159;", HtmlTools.escapeHTML("\u0159", false));
        assertEquals("&#x1d7ed;", HtmlTools.escapeHTML("\uD835\uDFED", false));
    }

    /**
     * Verify the expected results.
     */
    @Test
    void unescapeHTML() {
        assertNull(HtmlTools.unescapeHTML(null));
        assertEquals("", HtmlTools.unescapeHTML(""));
        assertEquals("\u0009", HtmlTools.unescapeHTML("\u0009"));
        assertEquals("\u0001", HtmlTools.unescapeHTML("\u0001"));
        assertEquals("<", HtmlTools.unescapeHTML("&lt;"));
        assertEquals(">", HtmlTools.unescapeHTML("&gt;"));
        assertEquals("&", HtmlTools.unescapeHTML("&amp;"));
        assertEquals("\"", HtmlTools.unescapeHTML("&quot;"));
        assertEquals("&apos;", HtmlTools.unescapeHTML("&apos;"));
        assertEquals("\'", HtmlTools.unescapeHTML("&apos;", true));
        assertEquals("&amp;", HtmlTools.unescapeHTML("&amp;amp;"));
        assertEquals("&lt;Fran&ccedil;ais&gt;", HtmlTools.unescapeHTML("&amp;lt;Fran&amp;ccedil;ais&amp;gt;"));
        assertEquals("\u0159", HtmlTools.unescapeHTML("&#x159;"));
        assertEquals("\uD808\uDF45", HtmlTools.unescapeHTML("&#x12345;"));
        assertEquals("\uD835\uDFED", HtmlTools.unescapeHTML("&#x1d7ed;"));
        assertEquals("\uD808\uDF45\uD835\uDFED", HtmlTools.unescapeHTML("&#x12345;&#x1d7ed;"));
        assertEquals("&#x1d7ed &#x1d7ed", HtmlTools.unescapeHTML("&#x1d7ed &#x1d7ed"));
        assertEquals("&#x1d7ed \uD835\uDFED", HtmlTools.unescapeHTML("&#x1d7ed &#x1d7ed;"));
        assertEquals("&#xQWER;", HtmlTools.unescapeHTML("&#xQWER;"));
        assertEquals("\u00E5", HtmlTools.unescapeHTML("&#229;"));
        assertEquals(
                "<>&\"\u00E5\u0159\uD835\uDFED", HtmlTools.unescapeHTML("&lt;&gt;&amp;&quot;&#229;&#x159;&#x1d7ed;"));
    }

    /**
     * Verify the expected results.
     *
     * @throws Exception should not happen.
     */
    @Test
    void encodeURL() throws Exception {
        assertNull(HtmlTools.encodeURL(null));
        assertEquals("", HtmlTools.encodeURL(""));
        assertEquals(
                "http://www.example.com/?This%20is%20a%20simple%20test.",
                HtmlTools.encodeURL("http://www.example.com/?This is a simple test."));

        assertEquals(
                "http://www.example.com/?This%20is%20a%20simple%20&%20short%20test.",
                HtmlTools.encodeURL("http://www.example.com/?This is a simple & short test."));

        String url = "\uD808\uDF45";
        assertEquals(HtmlTools.encodeURL(url), URLEncoder.encode(url, "UTF-8"));
    }

    /**
     * Verify the expected results.
     */
    @Test
    void getHtmlTag() {
        assertNull(HtmlTools.getHtmlTag(null));
        assertNull(HtmlTools.getHtmlTag(""));
        assertNull(HtmlTools.getHtmlTag("weirdHtmlTag"));
        assertNotNull(HtmlTools.getHtmlTag("strong"));
    }
}
