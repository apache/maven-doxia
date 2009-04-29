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
 * Test case for <code>DoxiaUtils</code>.
 *
 * @author ltheussl
 * @version $Id$
 */
public class DoxiaUtilsTest
    extends PlexusTestCase
{
    /**
     * Verify the expected results.
     */
    public void testIsInternalLink()
    {
        String link = "#anchor";
        assertTrue( "Should be an internal link: " + link,
            DoxiaUtils.isInternalLink( link ) );

        link = "http://maven.apache.org/index.html#anchor";
        assertFalse( "Should NOT be an internal link: " + link,
            DoxiaUtils.isInternalLink( link ) );

        link = "./index.html";
        assertFalse( "Should NOT be an internal link: " + link,
            DoxiaUtils.isInternalLink( link ) );
    }

    /**
     * Verify the expected results.
     */
    public void testIsExternalLink()
    {
        String link = "http://maven.apache.org/";
        assertTrue( "Should be an external link: " + link,
            DoxiaUtils.isExternalLink( link ) );

        link = "https://maven.apache.org/";
        assertTrue( "Should be an external link: " + link,
            DoxiaUtils.isExternalLink( link ) );

        link = "HTTPS://MAVEN.APACHE.ORG/";
        assertTrue( "Should be an external link: " + link,
            DoxiaUtils.isExternalLink( link ) );

        link = "ftp:/maven.apache.org/";
        assertTrue( "Should be an external link: " + link,
            DoxiaUtils.isExternalLink( link ) );

        link = "mailto:maven@apache.org";
        assertTrue( "Should be an external link: " + link,
            DoxiaUtils.isExternalLink( link ) );

        link = "file:/index.html";
        assertTrue( "Should be an external link: " + link,
            DoxiaUtils.isExternalLink( link ) );

        link = "resource_type://domain:port/filepathname?query_string#anchor";
        assertTrue( "Should be an external link: " + link,
            DoxiaUtils.isExternalLink( link ) );

        link = "index.html";
        assertFalse( "Should NOT be an external link: " + link,
            DoxiaUtils.isExternalLink( link ) );

        link = "example.pdf";
        assertFalse( "Should NOT be an external link: " + link,
            DoxiaUtils.isExternalLink( link ) );

        link = "./index.html";
        assertFalse( "Should NOT be an external link: " + link,
            DoxiaUtils.isExternalLink( link ) );

        link = "../index.html";
        assertFalse( "Should NOT be an external link: " + link,
            DoxiaUtils.isExternalLink( link ) );

        // Windows style separators "\" are not allowed

        link = "file:\\index.html";
        assertFalse( "Should NOT be an external link: " + link,
            DoxiaUtils.isExternalLink( link ) );

        link = ".\\index.html";
        assertFalse( "Should NOT be an external link: " + link,
            DoxiaUtils.isExternalLink( link ) );

        link = "..\\index.html";
        assertFalse( "Should NOT be an external link: " + link,
            DoxiaUtils.isExternalLink( link ) );
    }

    /**
     * Verify the expected results.
     */
    public void testIsLocalLink()
    {
        String link = "index.html";
        assertTrue( "Should be a local link: " + link,
            DoxiaUtils.isLocalLink( link ) );

        link = "./index.html";
        assertTrue( "Should be a local link: " + link,
            DoxiaUtils.isLocalLink( link ) );

        link = "../index.html";
        assertTrue( "Should be a local link: " + link,
            DoxiaUtils.isLocalLink( link ) );

        link = "#anchor";
        assertFalse( "Should NOT be a local link: " + link,
            DoxiaUtils.isLocalLink( link ) );

        link = "http://maven.apache.org/";
        assertFalse( "Should NOT be a local link: " + link,
            DoxiaUtils.isLocalLink( link ) );

    }

    /**
     * Verify the expected results.
     */
    public void testEncodeId()
    {
        assertEquals( DoxiaUtils.encodeId( null ), null );
        assertEquals( DoxiaUtils.encodeId( "" ), "a" );
        assertEquals( DoxiaUtils.encodeId( " " ), "a" );
        assertEquals( DoxiaUtils.encodeId( " _ " ), "a_" );
        assertEquals( DoxiaUtils.encodeId( "1" ), "a1" );
        assertEquals( DoxiaUtils.encodeId( "1anchor" ), "a1anchor" );
        assertEquals( DoxiaUtils.encodeId( "_anchor" ), "a_anchor" );
        assertEquals( DoxiaUtils.encodeId( "a b-c123 " ), "a_b-c123" );
        assertEquals( DoxiaUtils.encodeId( "   anchor" ), "anchor" );
        assertEquals( DoxiaUtils.encodeId( "myAnchor" ), "myAnchor" );
        assertEquals( DoxiaUtils.encodeId( "my&Anchor" ), "my%26Anchor" );
        assertEquals( DoxiaUtils.encodeId( "Håkon" ), "H%c3%a5kon" );
        assertEquals( DoxiaUtils.encodeId( "Håkon", true ), "Hkon" );
        assertEquals( DoxiaUtils.encodeId( "Theußl" ), "Theu%c3%9fl" );
        assertEquals( DoxiaUtils.encodeId( "Theußl", true ), "Theul" );
    }

    /**
     * Verify the expected results.
     */
    public void testIsValidId()
    {
        assertFalse( DoxiaUtils.isValidId( null ) );
        assertFalse( DoxiaUtils.isValidId( "" ) );
        assertFalse( DoxiaUtils.isValidId( " " ) );
        assertFalse( DoxiaUtils.isValidId( " _ " ) );
        assertFalse( DoxiaUtils.isValidId( "1" ) );
        assertFalse( DoxiaUtils.isValidId( "1anchor" ) );
        assertFalse( DoxiaUtils.isValidId( "_anchor" ) );
        assertFalse( DoxiaUtils.isValidId( "a b-c123 " ) );
        assertFalse( DoxiaUtils.isValidId( "   anchor" ) );
        assertFalse( DoxiaUtils.isValidId( "my&Anchor" ) );
        assertTrue( DoxiaUtils.isValidId( "myAnchor" ) );
        assertTrue( DoxiaUtils.isValidId( "a_" ) );
        assertTrue( DoxiaUtils.isValidId( "a-" ) );
        assertTrue( DoxiaUtils.isValidId( "a:" ) );
        assertTrue( DoxiaUtils.isValidId( "a." ) );
        assertTrue( DoxiaUtils.isValidId( "index.html" ) );
        assertFalse( DoxiaUtils.isValidId( "Theußl" ) );
    }
}
