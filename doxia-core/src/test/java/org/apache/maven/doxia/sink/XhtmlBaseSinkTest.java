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
 * @since 1.0-beta-1
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

        try
        {
            Writer writer =  new StringWriter();

            sink = new XhtmlBaseSink( writer );

            sink.paragraph();
            sink.text( "There should be no space before the " );
            sink.italic();
            sink.text( "period" );
            sink.italic_();
            sink.text( "." );
            sink.paragraph_();

            String actual = writer.toString();
            String expected = "<p>There should be no space before the <i>period</i>.</p>";

            assertEquals( expected, actual );
        }
        finally
        {
            if ( sink != null )
            {
                sink.close();
            }
        }
    }
}
