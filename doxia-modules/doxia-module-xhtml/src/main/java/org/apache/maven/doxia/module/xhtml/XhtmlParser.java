package org.apache.maven.doxia.module.xhtml;

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

import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.parser.XhtmlBaseParser;
import org.apache.maven.doxia.sink.Sink;

import org.apache.maven.doxia.sink.SinkEventAttributeSet;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Parse an xhtml model and emit events into a Doxia Sink.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @since 1.0
 * @plexus.component role="org.apache.maven.doxia.parser.Parser" role-hint="xhtml"
 */
public class XhtmlParser
    extends XhtmlBaseParser
    implements XhtmlMarkup
{
    /** For boxed verbatim. */
    private boolean boxed;

    /** Empty elements don't write a closing tag. */
    private boolean isEmptyElement;

    /** {@inheritDoc} */
    protected void handleStartTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        isEmptyElement = parser.isEmptyElementTag();

        SinkEventAttributeSet attribs = getAttributesFromParser( parser );

        if ( parser.getName().equals( Tag.HTML.toString() ) )
        {
            //Do nothing
            return;
        }
        else if ( parser.getName().equals( Tag.HEAD.toString() ) )
        {
            sink.head( attribs );
        }
        else if ( parser.getName().equals( Tag.TITLE.toString() ) )
        {
            sink.title( attribs );
        }
        else if ( parser.getName().equals( Tag.META.toString() ) )
        {
            String name = parser.getAttributeValue( null, Attribute.NAME.toString() );

            String content = parser.getAttributeValue( null, Attribute.CONTENT.toString() );

            attribs.removeAttribute( Attribute.NAME.toString() );
            attribs.removeAttribute( Attribute.CONTENT.toString() );

            if ( "author".equals( name ) )
            {
                sink.author( attribs );

                sink.text( content );

                sink.author_();
            }
            else if ( "date".equals( name ) )
            {
                sink.date( attribs );

                sink.text( content );

                sink.date_();
            }
            else
            {
                sink.unknown( "meta", new String[] {name, content}, attribs );
            }
        }
        /*
         * The ADDRESS element may be used by authors to supply contact information
         * for a model or a major part of a model such as a form. This element
         *  often appears at the beginning or end of a model.
         */
        else if ( parser.getName().equals( Tag.ADDRESS.toString() ) )
        {
            sink.author( attribs );
        }
        else if ( parser.getName().equals( Tag.BODY.toString() ) )
        {
            sink.body( attribs );
        }
        else if ( parser.getName().equals( Tag.DIV.toString() ) )
        {
            String divclass = parser.getAttributeValue( null, Attribute.CLASS.toString() );

            if ( "source".equals( divclass ) )
            {
                this.boxed = true;
            }

            super.baseStartTag( parser, sink ); // pick up other divs
        }
        /*
         * The PRE element tells visual user agents that the enclosed text is
         * "preformatted". When handling preformatted text, visual user agents:
         * - May leave white space intact.
         * - May render text with a fixed-pitch font.
         * - May disable automatic word wrap.
         * - Must not disable bidirectional processing.
         * Non-visual user agents are not required to respect extra white space
         * in the content of a PRE element.
         */
        else if ( parser.getName().equals( Tag.PRE.toString() ) )
        {
            if ( boxed )
            {
                attribs.addAttributes( SinkEventAttributeSet.BOXED );
            }

            verbatim();

            sink.verbatim( attribs );
        }
        else if ( !baseStartTag( parser, sink ) )
        {
            if ( isEmptyElement )
            {
                handleUnknown( parser, sink, TAG_TYPE_SIMPLE );
            }
            else
            {
                handleUnknown( parser, sink, TAG_TYPE_START );
            }

            if ( getLog().isDebugEnabled() )
            {
                String position = "[" + parser.getLineNumber() + ":"
                    + parser.getColumnNumber() + "]";
                String tag = "<" + parser.getName() + ">";

                getLog().debug( "Unrecognized xhtml tag: " + tag + " at " + position );
            }
        }
    }

    /** {@inheritDoc} */
    protected void handleEndTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        if ( parser.getName().equals( Tag.HTML.toString() ) )
        {
            //Do nothing
            return;
        }
        else if ( parser.getName().equals( Tag.HEAD.toString() ) )
        {
            sink.head_();
        }
        else if ( parser.getName().equals( Tag.TITLE.toString() ) )
        {
            sink.title_();
        }
        else if ( parser.getName().equals( Tag.BODY.toString() ) )
        {
            consecutiveSections( 0, sink );

            sink.body_();
        }
        else if ( parser.getName().equals( Tag.ADDRESS.toString() ) )
        {
            sink.author_();
        }
        else if ( parser.getName().equals( Tag.DIV.toString() ) )
        {
            this.boxed = false;
            super.baseEndTag( parser, sink );
        }
        else if ( !baseEndTag( parser, sink ) )
        {
            if ( !isEmptyElement )
            {
                handleUnknown( parser, sink, TAG_TYPE_END );
            }
        }

        isEmptyElement = false;
    }

}
