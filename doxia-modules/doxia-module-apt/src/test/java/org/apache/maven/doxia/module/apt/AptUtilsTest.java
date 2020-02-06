package org.apache.maven.doxia.module.apt;

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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test AptUtils.
 *
 * @author ltheussl
 * @version $Id$
 */
public class AptUtilsTest
{
    /**
     * Test of isExternalLink method, of class AptUtils.
     */
    @Test
    public void testIsExternalLink()
    {
        String link = "http://maven.apache.org/";
        assertTrue( "Should be an external link: " + link, AptUtils.isExternalLink( link ) );

        link = "https://maven.apache.org/";
        assertTrue( "Should be an external link: " + link, AptUtils.isExternalLink( link ) );

        link = "HTTPS://MAVEN.APACHE.ORG/";
        assertTrue( "Should be an external link: " + link, AptUtils.isExternalLink( link ) );

        link = "ftp:/maven.apache.org/";
        assertTrue( "Should be an external link: " + link, AptUtils.isExternalLink( link ) );

        link = "mailto:maven@apache.org";
        assertTrue( "Should be an external link: " + link, AptUtils.isExternalLink( link ) );

        link = "file:/index.html";
        assertTrue( "Should be an external link: " + link, AptUtils.isExternalLink( link ) );

        link = "resource_type://domain:port/filepathname?query_string#anchor";
        assertTrue( "Should be an external link: " + link, AptUtils.isExternalLink( link ) );

        link = "index.html";
        assertFalse( "Should NOT be an external link: " + link, AptUtils.isExternalLink( link ) );

        link = "example.pdf";
        assertFalse( "Should NOT be an external link: " + link, AptUtils.isExternalLink( link ) );

        link = "./index.html";
        assertFalse( "Should NOT be an external link: " + link, AptUtils.isExternalLink( link ) );

        link = "../index.html";
        assertFalse( "Should NOT be an external link: " + link, AptUtils.isExternalLink( link ) );

        // Windows style separators "\" are not allowed

        link = "file:\\index.html";
        assertFalse( "Should NOT be an external link: " + link, AptUtils.isExternalLink( link ) );

        link = ".\\index.html";
        assertFalse( "Should NOT be an external link: " + link, AptUtils.isExternalLink( link ) );

        link = "..\\index.html";
        assertFalse( "Should NOT be an external link: " + link, AptUtils.isExternalLink( link ) );
    }

    /**
     * Test of isInternalLink method, of class AptUtils.
     */
    @Test
    public void testIsInternalLink()
    {
        String link = "index.html";
        assertTrue( "Should be an internal link: " + link, AptUtils.isInternalLink( link ) );
        link = "file:/index.html";
        assertFalse( "Should NOT be an internal link: " + link, AptUtils.isInternalLink( link ) );
        link = "./index.html";
        assertFalse( "Should NOT be an internal link: " + link, AptUtils.isInternalLink( link ) );
    }

    /**
     * Test of isLocalLink method, of class AptUtils.
     */
    @Test
    public void testIsLocalLink()
    {
        String link = "/index.html";
        assertTrue( "Should be a local link: " + link, AptUtils.isLocalLink( link ) );

        link = "./index.html";
        assertTrue( "Should be a local link: " + link, AptUtils.isLocalLink( link ) );

        link = "../index.html";
        assertTrue( "Should be a local link: " + link, AptUtils.isLocalLink( link ) );

        link = "index.html";
        assertFalse( "Should NOT be a local link: " + link, AptUtils.isLocalLink( link ) );

        link = ".\\index.html";
        assertFalse( "Should NOT be a local link: " + link, AptUtils.isLocalLink( link ) );

        link = "\\index.html";
        assertFalse( "Should NOT be a local link: " + link, AptUtils.isLocalLink( link ) );

        link = "..\\index.html";
        assertFalse( "Should NOT be a local link: " + link, AptUtils.isLocalLink( link ) );
    }

    /**
     * Test of encodeAnchor method, of class AptUtils.
     */
    @Test
    public void testEncodeAnchor()
    {
        assertNull( AptUtils.encodeAnchor( null ) );
        assertEquals( "a123_:_.-aBc", AptUtils.encodeAnchor( " 12!3 :_.&-a)Bc " ) );
    }

    /**
     * Test of encodeFragment method, of class AptUtils.
     */
    @SuppressWarnings( "deprecation" )
    @Test
    public void testEncodeFragment()
    {
        assertNull( AptUtils.encodeFragment( null ) );
        assertEquals( "abc0d", AptUtils.encodeFragment( "a B&c0)D" ) );
    }

    /**
     * Test of linkToKey method, of class AptUtils.
     */
    @SuppressWarnings( "deprecation" )
    @Test
    public void testLinkToKey()
    {
        assertEquals( "abc56au", AptUtils.linkToKey( "aB$%C56 a&\\/'U" ) );
    }
}
