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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.html.HTML.Attribute;

import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.macro.manager.MacroNotFoundException;
import org.apache.maven.doxia.macro.MacroRequest;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.parser.Xhtml5BaseParser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Parse an xhtml model and emit events into a Doxia Sink.
 */
@Component( role = Parser.class, hint = "xhtml5" )
public class Xhtml5Parser
    extends Xhtml5BaseParser
    implements Xhtml5Markup
{
    /**
     * The role hint for the {@link Xhtml5Parser} Plexus component.
     */
    public static final String ROLE_HINT = "xhtml5";

    /** For boxed verbatim. */
    private boolean boxed;

    /** Empty elements don't write a closing tag. */
    private boolean isEmptyElement;

    /**
     * The source content of the input reader. Used to pass into macros.
     */
    private String sourceContent;

    /** {@inheritDoc} */
    protected void handleStartTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        isEmptyElement = parser.isEmptyElementTag();

        SinkEventAttributeSet attribs = getAttributesFromParser( parser );

        if ( parser.getName().equals( HTML.toString() ) )
        {
            //Do nothing
            return;
        }
        else if ( parser.getName().equals( HEAD.toString() ) )
        {
            sink.head( attribs );
        }
        else if ( parser.getName().equals( TITLE.toString() ) )
        {
            sink.title( attribs );
        }
        else if ( parser.getName().equals( META.toString() ) )
        {
            String name = parser.getAttributeValue( null, Attribute.NAME.toString() );
            String content = parser.getAttributeValue( null, Attribute.CONTENT.toString() );

            if ( "author".equals( name ) )
            {
                sink.author( null );

                sink.text( content );

                sink.author_();
            }
            else if ( "date".equals( name ) )
            {
                sink.date( null );

                sink.text( content );

                sink.date_();
            }
            else
            {
                sink.unknown( "meta", new Object[] { TAG_TYPE_SIMPLE }, attribs );
            }
        }
        /*
         * The ADDRESS element may be used by authors to supply contact information
         * for a model or a major part of a model such as a form. This element
         *  often appears at the beginning or end of a model.
         */
        else if ( parser.getName().equals( ADDRESS.toString() ) )
        {
            sink.address( attribs );
        }
        else if ( parser.getName().equals( BODY.toString() ) )
        {
            sink.body( attribs );
        }
        else if ( parser.getName().equals( DIV.toString() ) )
        {
            String divclass = parser.getAttributeValue( null, Attribute.CLASS.toString() );

            if ( "source".equals( divclass ) )
            {
                this.boxed = true;
            }

            baseStartTag( parser, sink ); // pick up other divs
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
        else if ( parser.getName().equals( PRE.toString() ) )
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

                getLog().debug( "Unrecognized xhtml5 tag: " + tag + " at " + position );
            }
        }
    }

    /** {@inheritDoc} */
    protected void handleEndTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        if ( parser.getName().equals( HTML.toString() ) )
        {
            //Do nothing
            return;
        }
        else if ( parser.getName().equals( HEAD.toString() ) )
        {
            sink.head_();
        }
        else if ( parser.getName().equals( TITLE.toString() ) )
        {
            sink.title_();
        }
        else if ( parser.getName().equals( BODY.toString() ) )
        {
            consecutiveSections( 0, sink, null );

            sink.body_();
        }
        else if ( parser.getName().equals( ADDRESS.toString() ) )
        {
            sink.address_();
        }
        else if ( parser.getName().equals( DIV.toString() ) )
        {
            this.boxed = false;
            baseEndTag( parser, sink );
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

    /** {@inheritDoc} */
    @Override
    protected void handleComment( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = getText( parser ).trim();

        if ( text.startsWith( "MACRO" ) && !isSecondParsing() )
        {
            processMacro( text, sink );
        }
        else
        {
            super.handleComment( parser, sink );
        }
    }

    /** process macro embedded in XHTML commment */
    private void processMacro( String text, Sink sink )
        throws XmlPullParserException
    {
        String s = text.substring( text.indexOf( '{' ) + 1, text.indexOf( '}' ) );
        s = escapeForMacro( s );
        String[] params = StringUtils.split( s, "|" );
        String macroName = params[0];

        Map<String, Object> parameters = new HashMap<>();
        for ( int i = 1; i < params.length; i++ )
        {
            String[] param = StringUtils.split( params[i], "=" );
            if ( param.length == 1 )
            {
                throw new XmlPullParserException( "Missing 'key=value' pair for macro parameter: " + params[i] );
            }

            String key = unescapeForMacro( param[0] );
            String value = unescapeForMacro( param[1] );
            parameters.put( key, value );
        }

        MacroRequest request = new MacroRequest( sourceContent, new Xhtml5Parser(), parameters, getBasedir() );

        try
        {
            executeMacro( macroName, request, sink );
        }
        catch ( MacroExecutionException e )
        {
            throw new XmlPullParserException( "Unable to execute macro in the document: " + macroName );
        }
        catch ( MacroNotFoundException me )
        {
            throw new XmlPullParserException( "Macro not found: " + macroName );
        }
    }

    /**
     * escapeForMacro
     *
     * @param s String
     * @return String
     */
    private String escapeForMacro( String s )
    {
        if ( s == null || s.length() < 1 )
        {
            return s;
        }

        String result = s;

        // use some outrageously out-of-place chars for text
        // (these are device control one/two in unicode)
        result = StringUtils.replace( result, "\\=", "\u0011" );
        result = StringUtils.replace( result, "\\|", "\u0012" );

        return result;
    }

    /**
     * unescapeForMacro
     *
     * @param s String
     * @return String
     */
    private String unescapeForMacro( String s )
    {
        if ( s == null || s.length() < 1 )
        {
            return s;
        }

        String result = s;

        result = StringUtils.replace( result, "\u0011", "=" );
        result = StringUtils.replace( result, "\u0012", "|" );

        return result;
    }

    /** {@inheritDoc} */
    protected void init()
    {
        super.init();

        this.boxed = false;
        this.isEmptyElement = false;
    }

    /** {@inheritDoc} */
    public void parse( Reader source, Sink sink )
        throws ParseException
    {
        this.sourceContent = null;

        try
        {
            StringWriter contentWriter = new StringWriter();
            IOUtil.copy( source, contentWriter );
            sourceContent = contentWriter.toString();
        }
        catch ( IOException ex )
        {
            throw new ParseException( "Error reading the input source: " + ex.getMessage(), ex );
        }
        finally
        {
            IOUtil.close( source );
        }

        try
        {
            super.parse( new StringReader( sourceContent ), sink );
        }
        finally
        {
            this.sourceContent = null;
        }
    }
}
