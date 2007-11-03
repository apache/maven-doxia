package org.apache.maven.doxia.module.fml;

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

import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.parser.XhtmlBaseParser;
import org.apache.maven.doxia.sink.Sink;

import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Parse Fml questions and answers, these may contain arbitrary xdoc elements.
 *
 * @author ltheussl
 * @version $Id$
 * @since 1.0
 */
public class FmlContentParser
    extends XhtmlBaseParser
    implements FmlMarkup
{
    /** {@inheritDoc} */
    protected void handleStartTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        if ( parser.getName().equals( SOURCE_TAG.toString() ) )
        {
            sink.verbatim( true );
        }
        else if ( !baseStartTag( parser, sink ) )
        {
            // TODO: log( "Unrecognized start tag!" );
        }
    }

    /** {@inheritDoc} */
    protected void handleEndTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        if ( parser.getName().equals( SOURCE_TAG.toString() ) )
        {
            sink.verbatim_();
        }
        else if ( !baseEndTag( parser, sink ) )
        {
            // TODO: log( "Unrecognized end tag!" );
        }
    }
}
