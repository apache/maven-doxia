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

import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.codehaus.plexus.testing.PlexusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test case for <code>DoxiaUtils</code>.
 *
 * @author ltheussl
 */
@PlexusTest
public class DoxiaUtilsTest
{
    /**
     * Verify the expected results.
     */
    @Test
    public void testIsInternalLink()
    {
        String link = "#anchor";
        assertTrue( DoxiaUtils.isInternalLink( link ), "Should be an internal link: " + link );

        link = "http://maven.apache.org/index.html#anchor";
        assertFalse( DoxiaUtils.isInternalLink( link ), "Should NOT be an internal link: " + link );

        link = "./index.html";
        assertFalse( DoxiaUtils.isInternalLink( link ), "Should NOT be an internal link: " + link );
    }

    /**
     * Verify the expected results.
     */
    @Test
    public void testIsExternalLink()
    {
        String link = "http://maven.apache.org/";
        assertTrue( DoxiaUtils.isExternalLink( link ), "Should be an external link: " + link );

        link = "https://maven.apache.org/";
        assertTrue( DoxiaUtils.isExternalLink( link ), "Should be an external link: " + link );

        link = "HTTPS://MAVEN.APACHE.ORG/";
        assertTrue( DoxiaUtils.isExternalLink( link ), "Should be an external link: " + link );

        link = "ftp:/maven.apache.org/";
        assertTrue( DoxiaUtils.isExternalLink( link ), "Should be an external link: " + link );

        link = "mailto:maven@apache.org";
        assertTrue( DoxiaUtils.isExternalLink( link ), "Should be an external link: " + link );

        link = "file:/index.html";
        assertTrue( DoxiaUtils.isExternalLink( link ), "Should be an external link: " + link );

        link = "resource_type://domain:port/filepathname?query_string#anchor";
        assertTrue( DoxiaUtils.isExternalLink( link ), "Should be an external link: " + link );

        link = "index.html";
        assertFalse( DoxiaUtils.isExternalLink( link ), "Should NOT be an external link: " + link );

        link = "example.pdf";
        assertFalse( DoxiaUtils.isExternalLink( link ), "Should NOT be an external link: " + link );

        link = "./index.html";
        assertFalse( DoxiaUtils.isExternalLink( link ), "Should NOT be an external link: " + link );

        link = "../index.html";
        assertFalse( DoxiaUtils.isExternalLink( link ), "Should NOT be an external link: " + link );

        // Windows style separators "\" are not allowed

        link = "file:\\index.html";
        assertFalse( DoxiaUtils.isExternalLink( link ), "Should NOT be an external link: " + link );

        link = ".\\index.html";
        assertFalse( DoxiaUtils.isExternalLink( link ), "Should NOT be an external link: " + link );

        link = "..\\index.html";
        assertFalse( DoxiaUtils.isExternalLink( link ), "Should NOT be an external link: " + link );
    }

    /**
     * Verify the expected results.
     */
    @Test
    public void testIsLocalLink()
    {
        String link = "index.html";
        assertTrue( DoxiaUtils.isLocalLink( link ), "Should be a local link: " + link );

        link = "./index.html";
        assertTrue( DoxiaUtils.isLocalLink( link ), "Should be a local link: " + link );

        link = "../index.html";
        assertTrue( DoxiaUtils.isLocalLink( link ), "Should be a local link: " + link );

        link = "#anchor";
        assertFalse( DoxiaUtils.isLocalLink( link ), "Should NOT be a local link: " + link );

        link = "http://maven.apache.org/";
        assertFalse( DoxiaUtils.isLocalLink( link ), "Should NOT be a local link: " + link );

    }

    /**
     * Verify the expected results.
     */
    @Test
    public void testEncodeId()
    {
        assertNull( DoxiaUtils.encodeId( null ) );
        assertEquals( DoxiaUtils.encodeId( "" ), "a" );
        assertEquals( DoxiaUtils.encodeId( " " ), "a" );
        assertEquals( DoxiaUtils.encodeId( " _ " ), "a_" );
        assertEquals( DoxiaUtils.encodeId( "1" ), "a1" );
        assertEquals( DoxiaUtils.encodeId( "1anchor" ), "a1anchor" );
        assertEquals( DoxiaUtils.encodeId( "_anchor" ), "a_anchor" );
        assertEquals( DoxiaUtils.encodeId( "a b-c123 " ), "a_b-c123" );
        assertEquals( DoxiaUtils.encodeId( "   anchor" ), "anchor" );
        assertEquals( DoxiaUtils.encodeId( "myAnchor" ), "myAnchor" );
        assertEquals( DoxiaUtils.encodeId( "my&Anchor" ), "my.26Anchor" );
        assertEquals( DoxiaUtils.encodeId( "H\u00E5kon" ), "H.C3.A5kon" );
        assertEquals( DoxiaUtils.encodeId( "H\u00E5kon", true ), "Hkon" );
        assertEquals( DoxiaUtils.encodeId( "Theu\u00DFl" ), "Theu.C3.9Fl" );
        assertEquals( DoxiaUtils.encodeId( "Theu\u00DFl", true ), "Theul" );
    }

    /**
     * Verify the expected results.
     */
    @Test
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
        assertFalse( DoxiaUtils.isValidId( "Theu\u00DFl" ) );
        assertTrue( DoxiaUtils.isValidId( "Theu.C3.9Fl" ) );
        assertFalse( DoxiaUtils.isValidId( "Theu%C3%9Fl" ) );
    }

    /**
     * Verify the expected results.
     */
    @Test
    public void testParseDate()
    {
        final int year = 1973;
        final int month = Calendar.FEBRUARY;
        final int day = 27;

        try
        {
            final Date feb27 = new GregorianCalendar( year, month, day ).getTime();
            assertEquals( feb27, DoxiaUtils.parseDate( "27.02.1973" ) );
            assertEquals( feb27, DoxiaUtils.parseDate( "27. 02. 1973" ) );
            assertEquals( feb27, DoxiaUtils.parseDate( "1973-02-27" ) );
            assertEquals( feb27, DoxiaUtils.parseDate( "1973/02/27" ) );
            assertEquals( feb27, DoxiaUtils.parseDate( "27 Feb 1973" ) );
            assertEquals( feb27, DoxiaUtils.parseDate( "27 Feb. 1973" ) );
            assertEquals( feb27, DoxiaUtils.parseDate( "Feb. 27, 1973" ) );
            assertEquals( feb27, DoxiaUtils.parseDate( "Feb 27, '73" ) );
            assertEquals( feb27, DoxiaUtils.parseDate( "February 27, 1973" ) );
            assertEquals( feb27, DoxiaUtils.parseDate( "19730227" ) );

            assertEquals( new GregorianCalendar( year, Calendar.JANUARY, 1 ).getTime(), DoxiaUtils.parseDate( "1973" ) );

            final Date feb1 = new GregorianCalendar( year, Calendar.FEBRUARY, 1 ).getTime();
            assertEquals( feb1, DoxiaUtils.parseDate( "February 1973" ) );
            assertEquals( feb1, DoxiaUtils.parseDate( "Feb. 1973" ) );
            assertEquals( feb1, DoxiaUtils.parseDate( "February '73" ) );
            assertEquals( feb1, DoxiaUtils.parseDate( "Feb. '73" ) );

            assertNotNull( DoxiaUtils.parseDate( "Today" ) );
            assertNotNull( DoxiaUtils.parseDate( "NOW" ) );
        }
        catch ( ParseException ex )
        {
            fail( ex.getMessage() );
        }

        try
        {
            DoxiaUtils.parseDate( "yesterday" ).getTime();
            fail();
        }
        catch ( ParseException ex )
        {
            assertNotNull( ex );
        }
    }
}
