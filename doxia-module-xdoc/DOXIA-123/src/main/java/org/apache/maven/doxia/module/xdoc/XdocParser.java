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
import org.apache.maven.doxia.parser.AbstractParser;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.MXParser;
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
    extends AbstractParser
    implements XdocMarkup
{
    /** The source content of the input reader. Used to pass into macros. */
    private String sourceContent;

    /** Used to distinguish <a href=""> from <a name="">. */
    private boolean isLink;

    /** Used to distinguish <a href=""> from <a name="">. */
    private boolean isAnchor;

    /** Empty elements don't write a closing tag. */
    private boolean isEmptyElement;

    /** Used for nested lists. */
    private int orderedListDepth = 0;

    /** A macro name. */
    private String macroName;

    /** The macro parameters. */
    private Map macroParameters = new HashMap();

    /** {@inheritDoc} */
    public void parse( Reader reader, Sink sink )
        throws ParseException
    {
        try
        {
            StringWriter contentWriter = new StringWriter();
            IOUtil.copy( reader, contentWriter );
            sourceContent = contentWriter.toString();

            XmlPullParser parser = new MXParser();

            parser.setInput( new StringReader( sourceContent ) );

            parseXdoc( parser, sink );
        }
        catch ( XmlPullParserException ex )
        {
            throw new ParseException( "Error parsing the model!", ex );
        }
        catch ( IOException ex )
        {
            throw new ParseException( "Error reading the input model!", ex );
        }
        catch ( MacroExecutionException ex )
        {
            throw new ParseException( "Macro execution failed!", ex );
        }
    }

    /**
     * Parse the model from the XmlPullParser into the given sink.
     *
     * @param parser A parser.
     * @param sink the sink to receive the events.
     * @throws XmlPullParserException if there's a problem parsing the model
     * @throws MacroExecutionException if there's a problem executing a macro
     */
    public void parseXdoc( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        int eventType = parser.getEventType();

        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
                handleStartTag( parser, sink );
            }
            else if ( eventType == XmlPullParser.END_TAG )
            {
                handleEndTag( parser, sink );
            }
            else if ( eventType == XmlPullParser.TEXT )
            {
                handleText( parser, sink );
            }

            try
            {
                eventType = parser.next();
            }
            catch ( IOException io )
            {
                throw new XmlPullParserException(
                    "Error parsing the model!", parser, io );
            }
        }
    }

    /**
     * Goes through the possible start tags.
     *
     * @param parser A parser.
     * @param sink the sink to receive the events.
     * @throws XmlPullParserException if there's a problem parsing the model
     */
    private void handleStartTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        isEmptyElement = parser.isEmptyElementTag();

        if ( parser.getName().equals( DOCUMENT_TAG.toString() ) )
        {
            //Do nothing
            return;
        }
        else if ( parser.getName().equals( Tag.TITLE.toString() ) )
        {
            sink.title();
        }
        else if ( parser.getName().equals( AUTHOR_TAG.toString() ) )
        {
            sink.author();
        }
        else if ( parser.getName().equals( Tag.BODY.toString() ) )
        {
            sink.body();
        }
        else if ( parser.getName().equals( SECTION_TAG.toString() ) )
        {
            // TODO this should go into a sink? kept for compat for the moment
            sink.anchor( parser.getAttributeValue( null, Attribute.NAME.toString() ) );
            sink.anchor_();

            sink.section1();

            sink.sectionTitle1();

            sink.text( parser.getAttributeValue( null, Attribute.NAME.toString() ) );

            sink.sectionTitle1_();
        }
        else if ( parser.getName().equals( SUBSECTION_TAG.toString() ) )
        {
            // TODO this should go into a sink? kept for compat for the moment
            sink.anchor( parser.getAttributeValue( null, Attribute.NAME.toString() ) );
            sink.anchor_();

            sink.section2();

            sink.sectionTitle2();

            sink.text( parser.getAttributeValue( null, Attribute.NAME.toString() ) );

            sink.sectionTitle2_();
        }
        // TODO section3 section4 section5
        else if ( parser.getName().equals( Tag.H4.toString() ) )
        {
            sink.sectionTitle3();
        }
        else if ( parser.getName().equals( Tag.H5.toString() ) )
        {
            sink.sectionTitle4();
        }
        else if ( parser.getName().equals( Tag.H6.toString() ) )
        {
            sink.sectionTitle5();
        }
        else if ( parser.getName().equals( Tag.P.toString() ) )
        {
            sink.paragraph();
        }
        else if ( parser.getName().equals( SOURCE_TAG.toString() ) )
        {
            sink.verbatim( true );
        }
        else if ( parser.getName().equals( Tag.UL.toString() ) )
        {
            sink.list();
        }
        else if ( parser.getName().equals( Tag.OL.toString() ) )
        {
            sink.numberedList( Sink.NUMBERING_DECIMAL );
            orderedListDepth++;
        }
        else if ( parser.getName().equals( Tag.LI.toString() ) )
        {
            if ( orderedListDepth == 0 )
            {
                sink.listItem();
            }
            else
            {
                sink.numberedListItem();
            }
        }
        else if ( parser.getName().equals( Tag.DL.toString() ) )
        {
            sink.definitionList();
        }
        else if ( parser.getName().equals( Tag.DT.toString() ) )
        {
            sink.definitionListItem();
            sink.definedTerm();
        }
        else if ( parser.getName().equals( Tag.DD.toString() ) )
        {
            sink.definition();
        }
        else if ( parser.getName().equals( PROPERTIES_TAG.toString() ) )
        {
            sink.head();
        }
        else if ( parser.getName().equals( Tag.B.toString() ) )
        {
            sink.bold();
        }
        else if ( parser.getName().equals( Tag.I.toString() ) )
        {
            sink.italic();
        }
        else if ( parser.getName().equals( Tag.TT.toString() ) )
        {
            sink.monospaced();
        }
        else if ( parser.getName().equals( Tag.A.toString() ) )
        {
            String href = parser.getAttributeValue( null, Attribute.HREF.toString() );
            if ( href != null )
            {
                sink.link( href );
                isLink = true;
            }
            else
            {
                String name = parser.getAttributeValue( null, Attribute.NAME.toString() );
                if ( name != null )
                {
                    sink.anchor( name );
                    isAnchor = true;
                }
                else
                {
                    handleRawText( sink, parser );
                }
            }
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
            }
        }

        // ----------------------------------------------------------------------
        // Tables
        // ----------------------------------------------------------------------

        else if ( parser.getName().equals( Tag.TABLE.toString() ) )
        {
            sink.table();
        }
        else if ( parser.getName().equals( Tag.TR.toString() ) )
        {
            sink.tableRow();
        }
        else if ( parser.getName().equals( Tag.TH.toString() ) )
        {
            String colspan = parser.getAttributeValue( null, Attribute.COLSPAN.toString() );
            if ( colspan ==  null )
            {
                sink.tableHeaderCell();
            }
            else
            {
                sink.tableHeaderCell( colspan );
            }
        }
        else if ( parser.getName().equals( Tag.TD.toString() ) )
        {
            String colspan = parser.getAttributeValue( null, Attribute.COLSPAN.toString() );
            if ( colspan ==  null )
            {
                sink.tableCell();
            }
            else
            {
                sink.tableCell( colspan );
            }
        }

        // ----------------------------------------------------------------------
        // Empty elements: <br/>, <hr/> and <img />
        // ----------------------------------------------------------------------

        else if ( parser.getName().equals( Tag.BR.toString() ) )
        {
            sink.lineBreak();
        }
        else if ( parser.getName().equals( Tag.HR.toString() ) )
        {
            sink.horizontalRule();
        }
        else if ( parser.getName().equals( Tag.IMG.toString() ) )
        {
            String src = parser.getAttributeValue( null, Attribute.SRC.toString() );
            String alt = parser.getAttributeValue( null, Attribute.ALT.toString() );

            sink.figure();
            sink.figureGraphics( src );

            if ( alt != null )
            {
                sink.figureCaption();
                sink.text( alt );
                sink.figureCaption_();
            }

            sink.figure_();
        }
        else
        {
            handleRawText( sink, parser );
        }

    }

    /**
     * Goes through the possible end tags.
     *
     * @param parser A parser.
     * @param sink the sink to receive the events.
     * @throws MacroExecutionException if there's a problem executing a macro
     */
    private void handleEndTag( XmlPullParser parser, Sink sink )
        throws MacroExecutionException
    {
        if ( parser.getName().equals( DOCUMENT_TAG.toString() ) )
        {
            //Do nothing
            return;
        }
        else if ( parser.getName().equals( Tag.TITLE.toString() ) )
        {
            sink.title_();
        }
        else if ( parser.getName().equals( AUTHOR_TAG.toString() ) )
        {
            sink.author_();
        }
        else if ( parser.getName().equals( Tag.BODY.toString() ) )
        {
            sink.body_();
        }
        else if ( parser.getName().equals( Tag.P.toString() ) )
        {
            sink.paragraph_();
        }
        else if ( parser.getName().equals( SOURCE_TAG.toString() ) )
        {
            sink.verbatim_();
        }
        else if ( parser.getName().equals( Tag.UL.toString() ) )
        {
            sink.list_();
        }
        else if ( parser.getName().equals( Tag.OL.toString() ) )
        {
            sink.numberedList_();
            orderedListDepth--;
        }
        else if ( parser.getName().equals( Tag.LI.toString() ) )
        {
            if ( orderedListDepth == 0 )
            {
                sink.listItem_();
            }
            else
            {
                sink.numberedListItem_();
            }
        }
        else if ( parser.getName().equals( Tag.DL.toString() ) )
        {
            sink.definitionList_();
        }
        else if ( parser.getName().equals( Tag.DT.toString() ) )
        {
            sink.definedTerm_();
        }
        else if ( parser.getName().equals( Tag.DD.toString() ) )
        {
            sink.definition_();
            sink.definitionListItem_();
        }
        else if ( parser.getName().equals( PROPERTIES_TAG.toString() ) )
        {
            sink.head_();
        }
        else if ( parser.getName().equals( Tag.B.toString() ) )
        {
            sink.bold_();
        }
        else if ( parser.getName().equals( Tag.I.toString() ) )
        {
            sink.italic_();
        }
        else if ( parser.getName().equals( Tag.TT.toString() ) )
        {
            sink.monospaced_();
        }
        else if ( parser.getName().equals( Tag.A.toString() ) )
        {
            if ( isLink )
            {
                sink.link_();
                isLink = false;
            }
            else if ( isAnchor )
            {
                sink.anchor_();
                isAnchor = false;
            }
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
            // do nothing
        }

        // ----------------------------------------------------------------------
        // Tables
        // ----------------------------------------------------------------------

        else if ( parser.getName().equals( Tag.TABLE.toString() ) )
        {
            sink.table_();
        }
        else if ( parser.getName().equals( Tag.TR.toString() ) )
        {
            sink.tableRow_();
        }
        else if ( parser.getName().equals( Tag.TH.toString() ) )
        {
            sink.tableHeaderCell_();
        }
        else if ( parser.getName().equals( Tag.TD.toString() ) )
        {
            sink.tableCell_();
        }
        else if ( parser.getName().equals( SECTION_TAG.toString() ) )
        {
            sink.section1_();
        }
        else if ( parser.getName().equals( SUBSECTION_TAG.toString() ) )
        {
            sink.section2_();
        }
        else if ( parser.getName().equals( Tag.H4.toString() ) )
        {
            sink.sectionTitle3_();
        }
        else if ( parser.getName().equals( Tag.H5.toString() ) )
        {
            sink.sectionTitle4_();
        }
        else if ( parser.getName().equals( Tag.H6.toString() ) )
        {
            sink.sectionTitle5_();
        }
        else if ( !isEmptyElement )
        {
            sink.rawText( START_MARKUP + SLASH_MARKUP );

            sink.rawText( parser.getName() );

            sink.rawText( END_MARKUP );
        }
        else
        {
            isEmptyElement = false;
        }
    }

    /**
     * Handles text events.
     *
     * @param parser A parser.
     * @param sink the sink to receive the events.
     */
    private void handleText( XmlPullParser parser, Sink sink )
    {
        String text = parser.getText();

        if ( !"".equals( text.trim() ) )
        {
            sink.text( text );
        }
    }

    /**
     * Handles raw text events.
     *
     * @param sink the sink to receive the events.
     * @param parser A parser.
     */
    private void handleRawText( Sink sink, XmlPullParser parser )
    {
        sink.rawText( START_MARKUP );

        sink.rawText( parser.getName() );

        int count = parser.getAttributeCount();

        for ( int i = 0; i < count; i++ )
        {
            sink.rawText( SPACE_MARKUP );

            sink.rawText( parser.getAttributeName( i ) );

            sink.rawText( EQUAL_MARKUP );

            sink.rawText( QUOTE_MARKUP );

            sink.rawText( parser.getAttributeValue( i ) );

            sink.rawText( QUOTE_MARKUP );
        }

        sink.rawText( END_MARKUP );
    }
}
