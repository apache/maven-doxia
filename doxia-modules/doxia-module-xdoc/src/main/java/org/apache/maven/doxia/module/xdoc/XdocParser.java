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
import org.apache.maven.doxia.parser.AbstractXmlParser;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.util.HtmlTools;

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
    extends AbstractXmlParser
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

    /** For tables. */
    private boolean hasCaption;

    /** Counts section level. */
    private int sectionLevel;

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
            closeOpenSections( Sink.SECTION_LEVEL_1, sink );

            sink.section1();

            sink.sectionTitle1();

            // TODO this should go into a sink? kept for compat for the moment
            sink.anchor( HtmlTools.encodeId( parser.getAttributeValue( null, Attribute.NAME.toString() ) ) );
            sink.anchor_();

            sink.text( parser.getAttributeValue( null, Attribute.NAME.toString() ) );

            sink.sectionTitle1_();
        }
        else if ( parser.getName().equals( SUBSECTION_TAG.toString() ) )
        {
            closeOpenSections( Sink.SECTION_LEVEL_2, sink );

            sink.section2();

            sink.sectionTitle2();

            // TODO this should go into a sink? kept for compat for the moment
            sink.anchor( HtmlTools.encodeId( parser.getAttributeValue( null, Attribute.NAME.toString() ) ) );
            sink.anchor_();

            sink.text( parser.getAttributeValue( null, Attribute.NAME.toString() ) );

            sink.sectionTitle2_();
        }
        else if ( parser.getName().equals( Tag.H4.toString() ) )
        {
            closeOpenSections( Sink.SECTION_LEVEL_3, sink );

            sink.section3();

            sink.sectionTitle3();
        }
        else if ( parser.getName().equals( Tag.H5.toString() ) )
        {
            closeOpenSections( Sink.SECTION_LEVEL_4, sink );

            sink.section4();

            sink.sectionTitle4();
        }
        else if ( parser.getName().equals( Tag.H6.toString() ) )
        {
            closeOpenSections( Sink.SECTION_LEVEL_5, sink );

            sink.section5();

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
            int numbering = Sink.NUMBERING_DECIMAL;

            // this will have to be generalized if we handle styles
            String style = parser.getAttributeValue( null, Attribute.STYLE.toString() );

            if ( style != null )
            {
                if ( "list-style-type: upper-alpha".equals( style ) )
                {
                    numbering = Sink.NUMBERING_UPPER_ALPHA;
                }
                else if ( "list-style-type: lower-alpha".equals( style ) )
                {
                    numbering = Sink.NUMBERING_LOWER_ALPHA;
                }
                else if ( "list-style-type: upper-roman".equals( style ) )
                {
                    numbering = Sink.NUMBERING_UPPER_ROMAN;
                }
                else if ( "list-style-type: lower-roman".equals( style ) )
                {
                    numbering = Sink.NUMBERING_LOWER_ROMAN;
                }
                else if ( "list-style-type: decimal".equals( style ) )
                {
                    numbering = Sink.NUMBERING_DECIMAL;
                }
            }

            sink.numberedList( numbering );
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

            String border = parser.getAttributeValue( null, Attribute.BORDER.toString() );

            boolean grid = true;

            if ( "0".equals( border ) )
            {
                grid = false;
            }

            String align = parser.getAttributeValue( null, Attribute.ALIGN.toString() );

            int[] justif = { JUSTIFY_CENTER };

            if ( "left".equals( align ) )
            {
                justif[0] = JUSTIFY_LEFT;
            }
            else if ( "right".equals( align ) )
            {
                justif[0] = JUSTIFY_RIGHT;
            }

            sink.tableRows( justif, grid );
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
        else if ( parser.getName().equals( Tag.CAPTION.toString() ) )
        {
            sink.tableRows_();
            this.hasCaption = true;
            sink.tableCaption();
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

    /** {@inheritDoc} */
    protected void handleEndTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
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
            closeOpenSections( 0, sink );

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
            if ( !hasCaption )
            {
                sink.tableRows_();
            }

            this.hasCaption = false;

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
        else if ( parser.getName().equals( Tag.CAPTION.toString() ) )
        {
            sink.tableCaption_();
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
            sink.rawText( String.valueOf( LESS_THAN ) + String.valueOf( SLASH ) );

            sink.rawText( parser.getName() );

            sink.rawText( String.valueOf( GREATER_THAN ) );
        }
        else
        {
            isEmptyElement = false;
        }
    }

    /** {@inheritDoc} */
    protected void handleText( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = parser.getText();

        if ( !"".equals( text.trim() ) )
        {
            sink.text( text );
        }
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * Handles raw text events.
     *
     * @param sink the sink to receive the events.
     * @param parser A parser.
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

    /**
     * Close open h4, h5, h6 sections. The current level is set to newLevel afterwards.
     *
     * @param newLevel the new section level, all upper levels have to be closed.
     * @param sink the sink to receive the events.
     */
    private void closeOpenSections( int newLevel, Sink sink )
    {
        while ( this.sectionLevel >= newLevel )
        {
            if ( sectionLevel == Sink.SECTION_LEVEL_5)
            {
                sink.section5_();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_4)
            {
                sink.section4_();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_3)
            {
                sink.section3_();
            }

            this.sectionLevel--;
        }

        this.sectionLevel = newLevel;
    }

}
