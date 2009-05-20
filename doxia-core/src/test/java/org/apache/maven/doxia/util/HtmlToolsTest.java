package org.apache.maven.doxia.util;

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

import org.codehaus.plexus.PlexusTestCase;

/**
 * Test case for <code>HtmlTools</code>.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class HtmlToolsTest
    extends PlexusTestCase
{
    /**
     * Verify the expected results.
     */
    public void testEscapeHTML()
    {
        assertEquals( HtmlTools.escapeHTML( null ), "" );
        assertEquals( HtmlTools.escapeHTML( "" ), "" );
        assertEquals( HtmlTools.escapeHTML( "\u0009" ), "\u0009" );
        assertEquals( HtmlTools.escapeHTML( "\u0001" ), "\u0001" );

        // Predefined entities
        assertEquals( HtmlTools.escapeHTML( "<" ), "&lt;" );
        assertEquals( HtmlTools.escapeHTML( ">" ), "&gt;" );
        assertEquals( HtmlTools.escapeHTML( "&" ), "&amp;" );
        assertEquals( HtmlTools.escapeHTML( "\"" ), "&quot;" );
        assertEquals( HtmlTools.escapeHTML( "\'" ), "&apos;" );
        assertEquals( HtmlTools.escapeHTML( "\'", false ), "\'" );

        // xml mode
        assertEquals( HtmlTools.escapeHTML( "&amp;" ), "&amp;amp;" );
        assertEquals( HtmlTools.escapeHTML( "\u00e4", true ), "\u00e4" );
        assertEquals( HtmlTools.escapeHTML( "\u00e4", false ), "&#xe4;" );
        assertEquals( HtmlTools.escapeHTML( "\u0159", false ), "&#x159;" );
        assertEquals( HtmlTools.escapeHTML( "\uD835\uDFED", false ), "&#x1d7ed;" );
    }

    /**
     * Verify the expected results.
     */
    public void testUnescapeHTML()
    {
        assertNull( HtmlTools.unescapeHTML( null ) );
        assertEquals( "", HtmlTools.unescapeHTML( "" ) );
        assertEquals( "\u0009", HtmlTools.unescapeHTML( "\u0009" ) );
        assertEquals( "\u0001", HtmlTools.unescapeHTML( "\u0001" ) );
        assertEquals( "<", HtmlTools.unescapeHTML( "&lt;" ) );
        assertEquals( ">", HtmlTools.unescapeHTML( "&gt;" ) );
        assertEquals( "&", HtmlTools.unescapeHTML( "&amp;" ) );
        assertEquals( "\"", HtmlTools.unescapeHTML( "&quot;" ) );
        assertEquals( "&apos;", HtmlTools.unescapeHTML( "&apos;" ) );
        assertEquals( "\'", HtmlTools.unescapeHTML( "&apos;", true ) );
        assertEquals( "&amp;", HtmlTools.unescapeHTML( "&amp;amp;" ) );
        assertEquals( "&lt;Fran&ccedil;ais&gt;", HtmlTools.unescapeHTML( "&amp;lt;Fran&amp;ccedil;ais&amp;gt;" ) );
        assertEquals( "\u0159", HtmlTools.unescapeHTML( "&#x159;" ) );
        assertEquals( "\uD808\uDF45", HtmlTools.unescapeHTML( "&#x12345;" ) );
        assertEquals( "\uD835\uDFED", HtmlTools.unescapeHTML( "&#x1d7ed;" ) );
        assertEquals( "\uD808\uDF45\uD835\uDFED", HtmlTools.unescapeHTML( "&#x12345;&#x1d7ed;" ) );
        assertEquals( "&#x1d7ed &#x1d7ed", HtmlTools.unescapeHTML( "&#x1d7ed &#x1d7ed" ) );
        assertEquals( "&#x1d7ed \uD835\uDFED", HtmlTools.unescapeHTML( "&#x1d7ed &#x1d7ed;" ) );
        assertEquals( "&#xQWER;", HtmlTools.unescapeHTML( "&#xQWER;" ) );
    }

    /**
     * Verify the expected results.
     */
    public void testEncodeId()
    {
        assertEquals( HtmlTools.encodeId( null ), null );
        assertEquals( HtmlTools.encodeId( "" ), "a" );
        assertEquals( HtmlTools.encodeId( " " ), "a" );
        assertEquals( HtmlTools.encodeId( " _ " ), "a_" );
        assertEquals( HtmlTools.encodeId( "1" ), "a1" );
        assertEquals( HtmlTools.encodeId( "1anchor" ), "a1anchor" );
        assertEquals( HtmlTools.encodeId( "_anchor" ), "a_anchor" );
        assertEquals( HtmlTools.encodeId( "a b-c123 " ), "a_b-c123" );
        assertEquals( HtmlTools.encodeId( "   anchor" ), "anchor" );
        assertEquals( HtmlTools.encodeId( "myAnchor" ), "myAnchor" );
        assertEquals( HtmlTools.encodeId( "H\u00E5kon" ), "Hkon" );
        assertEquals( HtmlTools.encodeId( "Theu\u00DFl" ), "Theul" );
    }

    /**
     * Verify the expected results.
     */
    public void testEncodeURL()
    {
        assertNull( HtmlTools.encodeURL( null ) );
        assertEquals( HtmlTools.encodeURL( "" ), "" );
        assertEquals( HtmlTools.encodeURL(
            "http://www.example.com/?This is a simple test." ),
            "http://www.example.com/?This%20is%20a%20simple%20test." );

        assertEquals( HtmlTools.encodeURL(
            "http://www.example.com/?This is a simple & short test." ),
            "http://www.example.com/?This%20is%20a%20simple%20&%20short%20test." );
    }

    /**
     * Verify the expected results.
     */
    public void testIsId()
    {
        assertFalse( HtmlTools.isId( null ) );
        assertFalse( HtmlTools.isId( "" ) );
        assertFalse( HtmlTools.isId( " " ) );
        assertFalse( HtmlTools.isId( " _ " ) );
        assertFalse( HtmlTools.isId( "1" ) );
        assertFalse( HtmlTools.isId( "1anchor" ) );
        assertFalse( HtmlTools.isId( "_anchor" ) );
        assertFalse( HtmlTools.isId( "a b-c123 " ) );
        assertFalse( HtmlTools.isId( "   anchor" ) );
        assertTrue( HtmlTools.isId( "myAnchor" ) );
        assertTrue( HtmlTools.isId( "a_" ) );
        assertTrue( HtmlTools.isId( "a-" ) );
        assertTrue( HtmlTools.isId( "a:" ) );
        assertTrue( HtmlTools.isId( "a." ) );
        assertFalse( HtmlTools.isId( "Theu\u00DFl" ) );
    }

    /**
     * Verify the expected results.
     */
    public void testGetHtmlTag()
    {
        assertNull( HtmlTools.getHtmlTag( "" ) );
        assertNull( HtmlTools.getHtmlTag( "weirdHtmlTag" ) );
        assertNotNull( HtmlTools.getHtmlTag( "strong" ) );
    }
}
