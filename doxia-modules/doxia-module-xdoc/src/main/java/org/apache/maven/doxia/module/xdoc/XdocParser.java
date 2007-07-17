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

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.doxia.macro.MacroRequest;
import org.apache.maven.doxia.parser.AbstractParser;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;

/**
 * Parse an xdoc model and emit events into the specified doxia
 * Sink.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:XdocParser.java 348605 2005-11-24 12:02:44 +1100 (Thu, 24 Nov 2005) brett $
 * @plexus.component role="org.apache.maven.doxia.parser.Parser"
 * role-hint="xdoc"
 */
public class XdocParser
    extends AbstractParser
{
    private String sourceContent;

    private boolean isLink;
    private boolean isAnchor;
    private boolean isEmptyElement;
    private int orderedListDepth = 0;

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
        catch ( Exception ex )
        {
            throw new ParseException( "Error parsing the model.", ex );
        }
    }

    public void parseXdoc( XmlPullParser parser, Sink sink )
        throws Exception
    {
        int eventType = parser.getEventType();

        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
                if ( parser.getName().equals( "document" ) )
                {
                    //Do nothing
                }
                else if ( parser.getName().equals( "title" ) )
                {
                    sink.title();
                }
                else if ( parser.getName().equals( "author" ) )
                {
                    sink.author();
                }
                else if ( parser.getName().equals( "body" ) )
                {
                    sink.body();
                }
                else if ( parser.getName().equals( "section" ) )
                {
                    sink.section1();

                    sink.sectionTitle1();

                    sink.text( parser.getAttributeValue( null, "name" ) );

                    sink.sectionTitle1_();
                }
                else if ( parser.getName().equals( "subsection" ) )
                {
                    sink.section2();

                    sink.sectionTitle2();

                    sink.text( parser.getAttributeValue( null, "name" ) );

                    sink.sectionTitle2_();
                }
                // TODO section3 section4 section5
                else if ( parser.getName().equals( "h4" ) )
                {
                    sink.sectionTitle3();
                }
                else if ( parser.getName().equals( "h5" ) )
                {
                    sink.sectionTitle4();
                }
                else if ( parser.getName().equals( "h6" ) )
                {
                    sink.sectionTitle5();
                }
                else if ( parser.getName().equals( "p" ) )
                {
                    sink.paragraph();
                }
                else if ( parser.getName().equals( "source" ) )
                {
                    sink.verbatim( true );
                }
                else if ( parser.getName().equals( "ul" ) )
                {
                    sink.list();
                }
                else if ( parser.getName().equals( "ol" ) )
                {
                    sink.numberedList( Sink.NUMBERING_DECIMAL );
                    orderedListDepth++;
                }
                else if ( parser.getName().equals( "li" ) )
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
                else if ( parser.getName().equals( "dl" ) )
                {
                    sink.definitionList();
                }
                else if ( parser.getName().equals( "dt" ) )
                {
                    sink.definitionListItem();
                    sink.definedTerm();
                }
                else if ( parser.getName().equals( "dd" ) )
                {
                    sink.definition();
                }
                else if ( parser.getName().equals( "properties" ) )
                {
                    sink.head();
                }
                else if ( parser.getName().equals( "b" ) )
                {
                    sink.bold();
                }
                else if ( parser.getName().equals( "i" ) )
                {
                    sink.italic();
                }
                else if ( parser.getName().equals( "tt" ) )
                {
                    sink.monospaced();
                }
                else if ( parser.getName().equals( "a" ) )
                {
                    String href = parser.getAttributeValue( null, "href" );
                    if ( href != null )
                    {
                        sink.link( href );
                        isLink = true;
                    }
                    else
                    {
                        String name = parser.getAttributeValue( null, "name" );
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
                else if ( parser.getName().equals( "macro" ) )
                {
                    if ( !secondParsing )
                    {
                        String macroName = parser.getAttributeValue( null, "name" );

                        int count = parser.getAttributeCount();

                        Map parameters = new HashMap();

                        for ( int i = 1; i < count; i++ )
                        {
                            parameters.put( parser.getAttributeName( i ), parser.getAttributeValue( i ) );
                        }

                        parameters.put( "sourceContent", sourceContent );

                        XdocParser xdocParser = new XdocParser();
                        xdocParser.setSecondParsing( true );
                        parameters.put( "parser", xdocParser );

                        MacroRequest request = new MacroRequest( parameters, getBasedir() );

                        executeMacro( macroName, request, sink );
                    }
                }

                // ----------------------------------------------------------------------
                // Tables
                // ----------------------------------------------------------------------

                else if ( parser.getName().equals( "table" ) )
                {
                    sink.table();
                }
                else if ( parser.getName().equals( "tr" ) )
                {
                    sink.tableRow();
                }
                else if ( parser.getName().equals( "th" ) )
                {
                    String colspan = parser.getAttributeValue( null, "colspan" );
                    if ( colspan ==  null)
                    {
                        sink.tableHeaderCell();
                    }
                    else
                    {
                        sink.tableHeaderCell( colspan );
                    }
                }
                else if ( parser.getName().equals( "td" ) )
                {
                    String colspan = parser.getAttributeValue( null, "colspan" );
                    if ( colspan ==  null)
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

                else if ( parser.getName().equals( "br" ) )
                {
                    sink.lineBreak();
                }
                else if ( parser.getName().equals( "hr" ) )
                {
                    sink.horizontalRule();
                }
                else if ( parser.getName().equals( "img" ) )
                {
                    String src = parser.getAttributeValue( null, "src" );
                    String alt = parser.getAttributeValue( null, "alt" );

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

                isEmptyElement = parser.isEmptyElementTag();

            }
            else if ( eventType == XmlPullParser.END_TAG )
            {
                if ( parser.getName().equals( "document" ) )
                {
                    //Do nothing
                }
                else if ( parser.getName().equals( "title" ) )
                {
                    sink.title_();
                }
                else if ( parser.getName().equals( "author" ) )
                {
                    sink.author_();
                }
                else if ( parser.getName().equals( "body" ) )
                {
                    sink.body_();
                }
                else if ( parser.getName().equals( "p" ) )
                {
                    sink.paragraph_();
                }
                else if ( parser.getName().equals( "source" ) )
                {
                    sink.verbatim_();
                }
                else if ( parser.getName().equals( "ul" ) )
                {
                    sink.list_();
                }
                else if ( parser.getName().equals( "ol" ) )
                {
                    sink.numberedList_();
                    orderedListDepth--;
                }
                else if ( parser.getName().equals( "li" ) )
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
                else if ( parser.getName().equals( "dl" ) )
                {
                    sink.definitionList_();
                }
                else if ( parser.getName().equals( "dt" ) )
                {
                    sink.definedTerm_();
                }
                else if ( parser.getName().equals( "dd" ) )
                {
                    sink.definition_();
                    sink.definitionListItem_();
                }
                else if ( parser.getName().equals( "properties" ) )
                {
                    sink.head_();
                }
                else if ( parser.getName().equals( "b" ) )
                {
                    sink.bold_();
                }
                else if ( parser.getName().equals( "i" ) )
                {
                    sink.italic_();
                }
                else if ( parser.getName().equals( "tt" ) )
                {
                    sink.monospaced_();
                }
                else if ( parser.getName().equals( "a" ) )
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
                else if ( parser.getName().equals( "macro" ) )
                {
                    //Do nothing
                }

                // ----------------------------------------------------------------------
                // Tables
                // ----------------------------------------------------------------------

                else if ( parser.getName().equals( "table" ) )
                {
                    sink.table_();
                }
                else if ( parser.getName().equals( "tr" ) )
                {
                    sink.tableRow_();
                }
                else if ( parser.getName().equals( "th" ) )
                {
                    sink.tableHeaderCell_();
                }
                else if ( parser.getName().equals( "td" ) )
                {
                    sink.tableCell_();
                }

                // ----------------------------------------------------------------------
                // Sections
                // ----------------------------------------------------------------------

                else if ( parser.getName().equals( "section" ) )
                {
                    sink.section1_();
                }
                else if ( parser.getName().equals( "subsection" ) )
                {
                    sink.section2_();
                }
                else if ( parser.getName().equals( "h4" ) )
                {
                    sink.sectionTitle3_();
                }
                else if ( parser.getName().equals( "h5" ) )
                {
                    sink.sectionTitle4_();
                }
                else if ( parser.getName().equals( "h6" ) )
                {
                    sink.sectionTitle5_();
                }
                else if ( !isEmptyElement )
                {
                    sink.rawText( "</" );

                    sink.rawText( parser.getName() );

                    sink.rawText( ">" );
                }
                else
                {
                    isEmptyElement = false;
                }

            }
            else if ( eventType == XmlPullParser.TEXT )
            {
                String text = parser.getText();
                if ( !"".equals( text.trim() ) )
                {
                    sink.text( text );
                }
            }

            eventType = parser.next();
        }
    }

    private void handleRawText( Sink sink, XmlPullParser parser )
    {
        sink.rawText( "<" );

        sink.rawText( parser.getName() );

        int count = parser.getAttributeCount();

        for ( int i = 0; i < count; i++ )
        {
            sink.rawText( " " );

            sink.rawText( parser.getAttributeName( i ) );

            sink.rawText( "=" );

            sink.rawText( "\"" );

            sink.rawText( parser.getAttributeValue( i ) );

            sink.rawText( "\"" );
        }

        sink.rawText( ">" );
    }
}
