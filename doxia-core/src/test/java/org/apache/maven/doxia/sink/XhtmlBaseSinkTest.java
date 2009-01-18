package org.apache.maven.doxia.sink;

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

import java.io.StringWriter;
import java.io.Writer;

import org.codehaus.plexus.PlexusTestCase;

/**
 * Test for XhtmlBaseSink.
 *
 * @author ltheussl
 * @version $Id$
 * @since 1.1
 */
public class XhtmlBaseSinkTest
    extends PlexusTestCase
{
    /** @throws Exception */
    public void testSpaceAfterClosingTag()
        throws Exception
    {
        // DOXIA-189
        XhtmlBaseSink sink = null;

        Writer writer =  new StringWriter();
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.paragraph();
            sink.text( "There should be no space before the " );
            sink.italic();
            sink.text( "period" );
            sink.italic_();
            sink.text( "." );
            sink.paragraph_();
        }
        finally
        {
            if ( sink != null )
            {
                sink.close();
            }
        }

        String actual = writer.toString();
        String expected = "<p>There should be no space before the <i>period</i>.</p>";

        assertEquals( expected, actual );
    }

    /** @throws Exception */
    public void testNestedTables()
        throws Exception
    {
        // DOXIA-177
        XhtmlBaseSink sink = null;
        Writer writer =  new StringWriter();

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.table();
            sink.tableRows( new int[] {0}, false );
            sink.tableCaption();
            sink.text( "caption1" );
            sink.tableCaption_();
            sink.tableRow();
            sink.tableCell();
            sink.table();
            sink.tableRows( new int[] {0}, false );
            sink.tableRow();
            sink.tableCell();
            sink.text( "nestedTableCell" );
            sink.tableCell_();
            sink.tableRow_();
            sink.tableRows_();
            sink.tableCaption();
            sink.text( "caption2" );
            sink.tableCaption_();
            sink.table_();
            sink.tableCell_();
            sink.tableRow_();
            sink.tableRows_();
            sink.table_();
        }
        finally
        {
            sink.close();
        }

        String actual = writer.toString();
        assertTrue( actual.indexOf( "nestedTableCell" ) != 1 );
        assertTrue( actual.indexOf( "class=\"bodyTable\"><caption>caption1</caption><tr" ) != 1 );
        assertTrue( actual.indexOf( "class=\"bodyTable\"><caption>caption2</caption><tr" ) != 1 );
    }
}
