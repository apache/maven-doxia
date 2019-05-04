package org.apache.maven.doxia.module.xhtml5;

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

import java.io.Writer;

import org.apache.maven.doxia.module.AbstractIdentityTest;
import org.apache.maven.doxia.module.xhtml5.Xhtml5Parser;
import org.apache.maven.doxia.module.xhtml5.Xhtml5Sink;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.codehaus.plexus.util.StringUtils;

/**
 * Check that piping a full model through an Xhtml5Parser and an Xhtml5Sink
 * leaves the model unchanged.
 */
public class Xhtml5IdentityTest
    extends AbstractIdentityTest
{
    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        super.setUp();
        assertIdentity( true );
    }

    /** {@inheritDoc} */
    protected Sink createSink( Writer writer )
    {
        Xhtml5Sink sink = new Xhtml5Sink( writer );
        sink.setInsertNewline( false );
        return sink;
    }

    /** {@inheritDoc} */
    protected Parser createParser()
    {
        return new Xhtml5Parser();
    }

    /** {@inheritDoc} */
    protected String getExpected()
    {
        // DOXIA-177
        String expected = super.getExpected();

        String startCaption = "begin:tableCaption";
        String endCaption = "end:tableCaption";

        int iStartCaption = expected.indexOf( startCaption );
        int iEndCaption = expected.indexOf( endCaption ) + endCaption.length();

        String captionTag = expected.substring( iStartCaption, iEndCaption ) + EOL + EOL + EOL;
        expected = StringUtils.replace( expected, captionTag, "" );

        int iStartTableRows =
            expected.substring( 0, iStartCaption ).lastIndexOf( "begin:tableRows" ) + "begin:tableRows".length();

        StringBuilder text = new StringBuilder();
        text.append( expected, 0, iStartTableRows );
        text.append( EOL ).append( EOL ).append( EOL );
        text.append( captionTag.subSequence( 0, captionTag.indexOf( "end:tableCaption" )
            + "end:tableCaption".length() ) );
        text.append( expected.substring( iStartTableRows ) );

        return text.toString();
    }
}
