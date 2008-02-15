package org.apache.maven.doxia.module.xdoc;

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
import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.macro.manager.MacroNotFoundException;
import org.apache.maven.doxia.macro.MacroRequest;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.XhtmlBaseParser;
import org.apache.maven.doxia.sink.Sink;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Parse an xdoc model and emit events into the specified doxia Sink.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @since 1.0
 * @plexus.component role="org.apache.maven.doxia.parser.Parser" role-hint="xdoc"
 */
public class XdocParser
    extends XhtmlBaseParser
    implements XdocMarkup
{
    /** The source content of the input reader. Used to pass into macros. */
    private String sourceContent;

    /** True if a &lt;script&gt;&lt;/script&gt; block is read. CDATA sections within are handled as rawText. */
    private boolean scriptBlock;

    /** Empty elements don't write a closing tag. */
    private boolean isEmptyElement;

    /** A macro name. */
    private String macroName;

    /** The macro parameters. */
    private Map macroParameters = new HashMap();


    /** {@inheritDoc} */
    public void parse( Reader source, Sink sink )
        throws ParseException
    {
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

        Reader tmp = new StringReader( sourceContent );

        super.parse( tmp, sink );
    }

    /** {@inheritDoc} */
    protected void handleStartTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        isEmptyElement = parser.isEmptyElementTag();

        if ( parser.getName().equals( DOCUMENT_TAG.toString() ) )
        {
            //Do nothing
            return;
        }
        else if ( parser.getName().equals( Tag.HEAD.toString() ) )
        {
            sink.head();
        }
        else if ( parser.getName().equals( Tag.TITLE.toString() ) )
        {
            sink.title();
        }
        else if ( parser.getName().equals( AUTHOR_TAG.toString() ) )
        {
            sink.author();
        }
        else if ( parser.getName().equals( DATE_TAG.toString() ) )
        {
            sink.date();
        }
        else if ( parser.getName().equals( Tag.BODY.toString() ) )
        {
            sink.body();
        }
        else if ( parser.getName().equals( SECTION_TAG.toString() ) )
        {
            closeOpenSections( Sink.SECTION_LEVEL_1, sink );

            sink.section1();

            sink.sectionTitle1();

            sink.text( parser.getAttributeValue( null, Attribute.NAME.toString() ) );

            sink.sectionTitle1_();
        }
        else if ( parser.getName().equals( SUBSECTION_TAG.toString() ) )
        {
            closeOpenSections( Sink.SECTION_LEVEL_2, sink );

            sink.section2();

            sink.sectionTitle2();

            sink.text( parser.getAttributeValue( null, Attribute.NAME.toString() ) );

            sink.sectionTitle2_();
        }
        else if ( parser.getName().equals( SOURCE_TAG.toString() ) )
        {
            sink.verbatim( true );
        }
        else if ( parser.getName().equals( PROPERTIES_TAG.toString() ) )
        {
            sink.head();
        }

        // ----------------------------------------------------------------------
        // Macro
        // ----------------------------------------------------------------------

        else if ( parser.getName().equals( MACRO_TAG.toString() ) )
        {
            if ( !secondParsing )
            {
                macroName = parser.getAttributeValue( null, Attribute.NAME.toString() );

                if ( StringUtils.isEmpty( macroName ) )
                {
                    // TODO use logging?
                    throw new IllegalArgumentException( "The '" + Attribute.NAME.toString() + "' attribute for the '"
                        + MACRO_TAG.toString() + "' tag is required." );
                }
            }
        }
        else if ( parser.getName().equals( Tag.PARAM.toString() ) )
        {
            if ( !secondParsing )
            {
                if ( StringUtils.isNotEmpty( macroName ) )
                {
                    if ( macroParameters == null )
                    {
                        macroParameters = new HashMap();
                    }

                    String paramName = parser.getAttributeValue( null, Attribute.NAME.toString() );
                    String paramValue = parser.getAttributeValue( null, Attribute.VALUE.toString() );

                    if ( StringUtils.isEmpty( paramName ) || StringUtils.isEmpty( paramValue ) )
                    {
                        throw new IllegalArgumentException( "'" + Attribute.NAME.toString() + "' and '"
                            + Attribute.VALUE.toString() + "' attributes for the '" + Tag.PARAM.toString()
                            + "' tag are required inside the '" + MACRO_TAG.toString() + "' tag." );
                    }

                    macroParameters.put( paramName, paramValue );
                }
                else
                {
                    // param tag from non-macro object, see MSITE-288
                    // TODO: remove
                    handleRawText( sink, parser );
                }
            }
        }
        else if ( parser.getName().equals( Tag.SCRIPT.toString() ) )
        {
            handleRawText( sink, parser );
            scriptBlock = true;
        }
        else if ( !baseStartTag( parser, sink ) )
        {
            // TODO: remove
            handleRawText( sink, parser );
            if ( getLog().isWarnEnabled() )
            {
                String position = "[" + parser.getLineNumber() + ":"
                    + parser.getColumnNumber() + "]";
                String tag = "<" + parser.getName() + ">";

                getLog().warn( "Unrecognized tag: " + tag + " at " + position );
            }
        }
    }

    /** {@inheritDoc} */
    protected void handleEndTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        if ( parser.getName().equals( DOCUMENT_TAG.toString() ) )
        {
            //Do nothing
            return;
        }
        else if ( parser.getName().equals( Tag.HEAD.toString() ) )
        {
            sink.head_();
        }
        else if ( parser.getName().equals( Tag.BODY.toString() ) )
        {
            closeOpenSections( 0, sink );

            sink.body_();
        }
        else if ( parser.getName().equals( Tag.TITLE.toString() ) )
        {
            sink.title_();
        }
        else if ( parser.getName().equals( AUTHOR_TAG.toString() ) )
        {
            sink.author_();
        }
        else if ( parser.getName().equals( DATE_TAG.toString() ) )
        {
            sink.date_();
        }
        else if ( parser.getName().equals( SOURCE_TAG.toString() ) )
        {
            sink.verbatim_();
        }
        else if ( parser.getName().equals( PROPERTIES_TAG.toString() ) )
        {
            sink.head_();
        }

        // ----------------------------------------------------------------------
        // Macro
        // ----------------------------------------------------------------------

        else if ( parser.getName().equals( MACRO_TAG.toString() ) )
        {
            if ( !secondParsing )
            {
                if ( StringUtils.isNotEmpty( macroName ) )
                {
                    // TODO handles specific macro attributes
                    macroParameters.put( "sourceContent", sourceContent );

                    XdocParser xdocParser = new XdocParser();
                    xdocParser.setSecondParsing( true );
                    macroParameters.put( "parser", xdocParser );

                    MacroRequest request = new MacroRequest( macroParameters, getBasedir() );

                    try
                    {
                        executeMacro( macroName, request, sink );
                    }
                    catch ( MacroNotFoundException me )
                    {
                        throw new MacroExecutionException( "Macro not found: " + macroName, me );
                    }
                }
            }

            // Reinit macro
            macroName = null;
            macroParameters = null;
        }
        else if ( parser.getName().equals( Tag.PARAM.toString() ) )
        {
            if ( !StringUtils.isNotEmpty( macroName ) )
            {
                // TODO: remove
                sink.rawText( String.valueOf( LESS_THAN ) + String.valueOf( SLASH )
                    + Tag.PARAM.toString() + String.valueOf( GREATER_THAN ));
            }
        }
        else if ( parser.getName().equals( SECTION_TAG.toString() ) )
        {
            closeOpenSections( 0, sink );

            sink.section1_();
        }
        else if ( parser.getName().equals( SUBSECTION_TAG.toString() ) )
        {
            closeOpenSections( Sink.SECTION_LEVEL_1, sink );

            sink.section2_();
        }
        else if ( parser.getName().equals( Tag.SCRIPT.toString() ) )
        {
            // TODO: this is HTML specific, factor out into a specialized parser

            sink.rawText( String.valueOf( LESS_THAN ) + String.valueOf( SLASH ) );

            sink.rawText( parser.getName() );

            sink.rawText( String.valueOf( GREATER_THAN ) );

            scriptBlock = false;
        }
        else if ( !baseEndTag( parser, sink ) )
        {
            if ( !isEmptyElement )
            {
                // TODO: this is HTML specific, factor out into a specialized parser

                sink.rawText( String.valueOf( LESS_THAN ) + String.valueOf( SLASH ) );

                sink.rawText( parser.getName() );

                sink.rawText( String.valueOf( GREATER_THAN ) );
            }
            else
            {
                isEmptyElement = false;
            }
        }
    }

    /** {@inheritDoc} */
    protected void handleCdsect( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = parser.getText();

        if ( scriptBlock )
        {
            sink.rawText( text );
        }
        else
        {
            sink.text( text );
        }
    }

    /**
     * Close open h4, h5, h6 sections. The current level is set to newLevel afterwards.
     *
     * @param newLevel the new section level, all upper levels have to be closed.
     * @param sink the sink to receive the events.
     */
    protected void closeOpenSections( int newLevel, Sink sink )
    {
        while ( getSectionLevel() >= newLevel )
        {
            if ( getSectionLevel() == Sink.SECTION_LEVEL_5)
            {
                sink.section5_();
            }
            else if ( getSectionLevel() == Sink.SECTION_LEVEL_4)
            {
                sink.section4_();
            }
            else if ( getSectionLevel() == Sink.SECTION_LEVEL_3)
            {
                sink.section3_();
            }

            setSectionLevel( getSectionLevel() - 1 );
        }

        setSectionLevel( newLevel );
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * Handles raw text events.
     *
     * @param sink the sink to receive the events.
     * @param parser A parser.
     * @todo this is HTML specific, factor out into a specialized parser
     */
    private void handleRawText( Sink sink, XmlPullParser parser )
    {
        sink.rawText( String.valueOf( LESS_THAN ) );

        sink.rawText( parser.getName() );

        int count = parser.getAttributeCount();

        for ( int i = 0; i < count; i++ )
        {
            sink.rawText( String.valueOf( SPACE ) );

            sink.rawText( parser.getAttributeName( i ) );

            sink.rawText( String.valueOf( EQUAL ) );

            sink.rawText( String.valueOf( QUOTE ) );

            sink.rawText( parser.getAttributeValue( i ) );

            sink.rawText( String.valueOf( QUOTE ) );
        }

        sink.rawText( String.valueOf( GREATER_THAN ) );
    }

}
