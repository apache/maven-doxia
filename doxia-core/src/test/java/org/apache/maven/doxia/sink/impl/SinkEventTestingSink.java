package org.apache.maven.doxia.sink.impl;

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

import java.util.LinkedList;
import java.util.List;

import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.AbstractSink;

/**
 * This sink is used for testing purposes in order to check wether
 * the input of some parser is well-formed.
 *
 * @author ltheussl
 * @version $Id$
 * @since 1.1
 */
public class SinkEventTestingSink
    extends AbstractSink
{
    /** The list of sink events. */
    private final List<SinkEventElement> events = new LinkedList<>();

    /**
     * Return the collected list of SinkEventElements.
     *
     * @return the collected list of SinkEventElements.
     */
    public List<SinkEventElement> getEventList()
    {
        return this.events;
    }

    /** Clears the list of sink events. */
    public void reset()
    {
        this.events.clear();
    }

      //
     // sink methods
    //

    @Override
    public void head()
    {
        addEvent( "head" );
    }

    @Override
    public void head_()
    {
        addEvent( "head_" );
    }

    @Override
    public void body()
    {
        addEvent( "body" );
    }

    @Override
    public void body_()
    {
        addEvent( "body_" );
    }

    @Override
    public void article()
    {
        addEvent( "article" );
    }

    @Override
    public void article_()
    {
        addEvent( "article_" );
    }

    @Override
    public void navigation()
    {
        addEvent( "navigation" );
    }

    @Override
    public void navigation_()
    {
        addEvent( "navigation_" );
    }

    @Override
    public void sidebar()
    {
        addEvent( "sidebar" );
    }

    @Override
    public void sidebar_()
    {
        addEvent( "sidebar_" );
    }

    @Override
    public void section1()
    {
        addEvent( "section1" );
    }

    @Override
    public void section1_()
    {
        addEvent( "section1_" );
    }

    @Override
    public void section2()
    {
        addEvent( "section2" );
    }

    @Override
    public void section2_()
    {
        addEvent( "section2_" );
    }

    @Override
    public void section3()
    {
        addEvent( "section3" );
    }

    @Override
    public void section3_()
    {
        addEvent( "section3_" );
    }

    @Override
    public void section4()
    {
        addEvent( "section4" );
    }

    @Override
    public void section4_()
    {
        addEvent( "section4_" );
    }

    @Override
    public void section5()
    {
        addEvent( "section5" );
    }

    @Override
    public void section5_()
    {
        addEvent( "section5_" );
    }

    @Override
    public void section6()
    {
        addEvent( "section6" );
    }

    @Override
    public void section6_()
    {
        addEvent( "section6_" );
    }

    @Override
    public void list()
    {
        addEvent( "list" );
    }

    @Override
    public void list_()
    {
        addEvent( "list_" );
    }

    @Override
    public void listItem()
    {
        addEvent( "listItem" );
    }

    @Override
    public void listItem_()
    {
        addEvent( "listItem_" );
    }

    @Override
    public void numberedList( int numbering )
    {
        addEvent( "numberedList", new Object[] { numbering } );
    }

    @Override
    public void numberedList_()
    {
        addEvent( "numberedList_" );
    }

    @Override
    public void numberedListItem()
    {
        addEvent( "numberedListItem" );
    }

    @Override
    public void numberedListItem_()
    {
        addEvent( "numberedListItem_" );
    }

    @Override
    public void definitionList()
    {
        addEvent( "definitionList" );
    }

    @Override
    public void definitionList_()
    {
        addEvent( "definitionList_" );
    }

    @Override
    public void definitionListItem()
    {
        addEvent( "definitionListItem" );
    }

    @Override
    public void definitionListItem_()
    {
        addEvent( "definitionListItem_" );
    }

    @Override
    public void definition()
    {
        addEvent( "definition" );
    }

    @Override
    public void definition_()
    {
        addEvent( "definition_" );
    }

    @Override
    public void figure()
    {
        addEvent( "figure" );
    }

    @Override
    public void figure_()
    {
        addEvent( "figure_" );
    }

    @Override
    public void table()
    {
        addEvent( "table" );
    }

    @Override
    public void table_()
    {
        addEvent( "table_" );
    }

    @Override
    public void tableRows( int[] justification, boolean grid )
    {
        addEvent( "tableRows", new Object[] { justification, grid } );
    }

    @Override
    public void tableRows_()
    {
        addEvent( "tableRows_" );
    }

    @Override
    public void tableRow()
    {
        addEvent( "tableRow" );
    }

    @Override
    public void tableRow_()
    {
        addEvent( "tableRow_" );
    }

    @Override
    public void title()
    {
        addEvent( "title" );
    }

    @Override
    public void title_()
    {
        addEvent( "title_" );
    }

    @Override
    public void author()
    {
        addEvent( "author" );
    }

    @Override
    public void author_()
    {
        addEvent( "author_" );
    }

    @Override
    public void date()
    {
        addEvent( "date" );
    }

    @Override
    public void date_()
    {
        addEvent( "date_" );
    }

    @Override
    public void sectionTitle()
    {
        addEvent( "sectionTitle" );
    }

    @Override
    public void sectionTitle_()
    {
        addEvent( "sectionTitle_" );
    }

    @Override
    public void sectionTitle1()
    {
        addEvent( "sectionTitle1" );
    }

    @Override
    public void sectionTitle1_()
    {
        addEvent( "sectionTitle1_" );
    }

    @Override
    public void sectionTitle2()
    {
        addEvent( "sectionTitle2" );
    }

    @Override
    public void sectionTitle2_()
    {
        addEvent( "sectionTitle2_" );
    }

    @Override
    public void sectionTitle3()
    {
        addEvent( "sectionTitle3" );
    }

    @Override
    public void sectionTitle3_()
    {
        addEvent( "sectionTitle3_" );
    }

    @Override
    public void sectionTitle4()
    {
        addEvent( "sectionTitle4" );
    }

    @Override
    public void sectionTitle4_()
    {
        addEvent( "sectionTitle4_" );
    }

    @Override
    public void sectionTitle5()
    {
        addEvent( "sectionTitle5" );
    }

    @Override
    public void sectionTitle5_()
    {
        addEvent( "sectionTitle5_" );
    }

    @Override
    public void sectionTitle6()
    {
        addEvent( "sectionTitle6" );
    }

    @Override
    public void sectionTitle6_()
    {
        addEvent( "sectionTitle6_" );
    }

    @Override
    public void header()
    {
        addEvent( "header" );
    }

    @Override
    public void header_()
    {
        addEvent( "header_" );
    }

    @Override
    public void content()
    {
        addEvent( "content" );
    }

    @Override
    public void content_()
    {
        addEvent( "content_" );
    }

    @Override
    public void footer()
    {
        addEvent( "footer" );
    }

    @Override
    public void footer_()
    {
        addEvent( "footer_" );
    }

    @Override
    public void paragraph()
    {
        addEvent( "paragraph" );
    }

    @Override
    public void paragraph_()
    {
        addEvent( "paragraph_" );
    }

    @Override
    public void data( String value )
    {
        addEvent( "data", new Object[] {value} );
    }

    @Override
    public void data_()
    {
        addEvent( "data_" );
    }

    @Override
    public void time( String datetime )
    {
        addEvent( "time", new Object[] {datetime} );
    }

    @Override
    public void time_()
    {
        addEvent( "time_" );
    }

    @Override
    public void address()
    {
        addEvent( "address" );
    }

    @Override
    public void address_()
    {
        addEvent( "address_" );
    }

    @Override
    public void blockquote()
    {
        addEvent( "blockquote" );
    }

    @Override
    public void blockquote_()
    {
        addEvent( "blockquote_" );
    }

    @Override
    public void division()
    {
        addEvent( "division" );
    }

    @Override
    public void division_()
    {
        addEvent( "division_" );
    }

    @Override
    public void verbatim( boolean boxed )
    {
        addEvent( "verbatim", new Object[] { boxed } );
    }

    @Override
    public void verbatim_()
    {
        addEvent( "verbatim_" );
    }

    @Override
    public void definedTerm()
    {
        addEvent( "definedTerm" );
    }

    @Override
    public void definedTerm_()
    {
        addEvent( "definedTerm_" );
    }

    @Override
    public void figureCaption()
    {
        addEvent( "figureCaption" );
    }

    @Override
    public void figureCaption_()
    {
        addEvent( "figureCaption_" );
    }

    @Override
    public void tableCell()
    {
        addEvent( "tableCell" );
    }

    @Override
    public void tableCell( String width )
    {
        addEvent( "tableCell", new Object[] {width} );
    }

    @Override
    public void tableCell_()
    {
        addEvent( "tableCell_" );
    }

    @Override
    public void tableHeaderCell()
    {
        addEvent( "tableHeaderCell" );
    }

    @Override
    public void tableHeaderCell( String width )
    {
        addEvent( "tableHeaderCell", new Object[] {width} );
    }

    @Override
    public void tableHeaderCell_()
    {
        addEvent( "tableHeaderCell_" );
    }

    @Override
    public void tableCaption()
    {
        addEvent( "tableCaption" );
    }

    @Override
    public void tableCaption_()
    {
        addEvent( "tableCaption_" );
    }

    @Override
    public void figureGraphics( String name )
    {
        addEvent( "figureGraphics", new Object[] {name} );
    }

    @Override
    public void horizontalRule()
    {
        addEvent( "horizontalRule" );
    }

    @Override
    public void pageBreak()
    {
        addEvent( "pageBreak" );
    }

    @Override
    public void anchor( String name )
    {
        addEvent( "anchor", new Object[] {name} );
    }

    @Override
    public void anchor_()
    {
        addEvent( "anchor_" );
    }

    @Override
    public void link( String name )
    {
        addEvent( "link", new Object[] {name} );
    }

    @Override
    public void link_()
    {
        addEvent( "link_" );
    }

    @Override
    public void inline()
    {
        addEvent( "inline" );
    }

    @Override
    public void inline_()
    {
        addEvent( "inline_" );
    }

    @Override
    public void italic()
    {
        addEvent( "italic" );
    }

    @Override
    public void italic_()
    {
        addEvent( "italic_" );
    }

    @Override
    public void bold()
    {
        addEvent( "bold" );
    }

    @Override
    public void bold_()
    {
        addEvent( "bold_" );
    }

    @Override
    public void monospaced()
    {
        addEvent( "monospaced" );
    }

    @Override
    public void monospaced_()
    {
        addEvent( "monospaced_" );
    }

    @Override
    public void lineBreak()
    {
        addEvent( "lineBreak" );
    }

    @Override
    public void lineBreakOpportunity()
    {
        addEvent( "lineBreakOpportunity" );
    }

    @Override
    public void nonBreakingSpace()
    {
        addEvent( "nonBreakingSpace" );
    }

    @Override
    public void text( String text )
    {
        addEvent( "text", new Object[] {text} );
    }

    @Override
    public void rawText( String text )
    {
        addEvent( "rawText", new Object[] {text} );
    }

    @Override
    public void comment( String comment )
    {
        addEvent( "comment", new Object[] {comment} );
    }

    @Override
    public void flush()
    {
        addEvent( "flush" );
    }

    @Override
    public void close()
    {
        addEvent( "close" );
    }

    @Override
    public void head( SinkEventAttributes attributes )
    {
        addEvent( "head", new Object[] {attributes} );
    }

    @Override
    public void title( SinkEventAttributes attributes )
    {
        addEvent( "title", new Object[] {attributes} );
    }

    @Override
    public void author( SinkEventAttributes attributes )
    {
        addEvent( "author", new Object[] {attributes} );
    }

    @Override
    public void date( SinkEventAttributes attributes )
    {
        addEvent( "date", new Object[] {attributes} );
    }

    @Override
    public void body( SinkEventAttributes attributes )
    {
        addEvent( "body", new Object[] {attributes} );
    }

    @Override
    public void article( SinkEventAttributes attributes )
    {
        addEvent( "article", new Object[] {attributes} );
    }

    @Override
    public void navigation( SinkEventAttributes attributes )
    {
        addEvent( "navigation", new Object[] {attributes} );
    }

    @Override
    public void sidebar( SinkEventAttributes attributes )
    {
        addEvent( "sidebar", new Object[] {attributes} );
    }

    @Override
    public void section( int level, SinkEventAttributes attributes )
    {
        addEvent( "section" + level, new Object[] {attributes} );
    }

    @Override
    public void section_( int level )
    {
        addEvent( "section" + level + "_" );
    }

    @Override
    public void sectionTitle( int level, SinkEventAttributes attributes )
    {
        addEvent( "sectionTitle" + level, new Object[] {attributes} );
    }

    @Override
    public void sectionTitle_( int level )
    {

        addEvent( "sectionTitle" + level + "_" );
    }

    @Override
    public void header( SinkEventAttributes attributes )
    {
        addEvent( "header", new Object[] {attributes} );
    }

    @Override
    public void content( SinkEventAttributes attributes )
    {
        addEvent( "content", new Object[] {attributes} );
    }

    @Override
    public void footer( SinkEventAttributes attributes )
    {
        addEvent( "footer", new Object[] {attributes} );
    }

    @Override
    public void list( SinkEventAttributes attributes )
    {
        addEvent( "list", new Object[] {attributes} );
    }

    @Override
    public void listItem( SinkEventAttributes attributes )
    {
        addEvent( "listItem", new Object[] {attributes} );
    }

    @Override
    public void numberedList( int numbering, SinkEventAttributes attributes )
    {
        addEvent( "numberedList", new Object[] { numbering, attributes } );
    }

    @Override
    public void numberedListItem( SinkEventAttributes attributes )
    {
        addEvent( "numberedListItem", new Object[] {attributes} );
    }

    @Override
    public void definitionList( SinkEventAttributes attributes )
    {
        addEvent( "definitionList", new Object[] {attributes} );
    }

    @Override
    public void definitionListItem( SinkEventAttributes attributes )
    {
        addEvent( "definitionListItem", new Object[] {attributes} );
    }

    @Override
    public void definition( SinkEventAttributes attributes )
    {
        addEvent( "definition", new Object[] {attributes} );
    }

    @Override
    public void definedTerm( SinkEventAttributes attributes )
    {
        addEvent( "definedTerm", new Object[] {attributes} );
    }

    @Override
    public void figure( SinkEventAttributes attributes )
    {
        addEvent( "figure", new Object[] {attributes} );
    }

    @Override
    public void figureCaption( SinkEventAttributes attributes )
    {
        addEvent( "figureCaption", new Object[] {attributes} );
    }

    @Override
    public void figureGraphics( String src, SinkEventAttributes attributes )
    {
        addEvent( "figureGraphics", new Object[] {src, attributes} );
    }

    @Override
    public void table( SinkEventAttributes attributes )
    {
        addEvent( "table", new Object[] {attributes} );
    }

    @Override
    public void tableRow( SinkEventAttributes attributes )
    {
        addEvent( "tableRow", new Object[] {attributes} );
    }

    @Override
    public void tableCell( SinkEventAttributes attributes )
    {
        addEvent( "tableCell", new Object[] {attributes} );
    }

    @Override
    public void tableHeaderCell( SinkEventAttributes attributes )
    {
        addEvent( "tableHeaderCell", new Object[] {attributes} );
    }

    @Override
    public void tableCaption( SinkEventAttributes attributes )
    {
        addEvent( "tableCaption", new Object[] {attributes} );
    }

    @Override
    public void paragraph( SinkEventAttributes attributes )
    {
        addEvent( "paragraph", new Object[] {attributes} );
    }

    @Override
    public void data( String value, SinkEventAttributes attributes )
    {
        addEvent( "data", new Object[] {value, attributes} );
    }

    @Override
    public void time( String datetime, SinkEventAttributes attributes )
    {
        addEvent( "time", new Object[] {datetime, attributes} );
    }

    @Override
    public void address( SinkEventAttributes attributes )
    {
        addEvent( "address", new Object[] {attributes} );
    }

    @Override
    public void blockquote( SinkEventAttributes attributes )
    {
        addEvent( "blockquote", new Object[] {attributes} );
    }

    @Override
    public void division( SinkEventAttributes attributes )
    {
        addEvent( "division", new Object[] {attributes} );
    }

    @Override
    public void verbatim( SinkEventAttributes attributes )
    {
        addEvent( "verbatim", new Object[] {attributes} );
    }

    @Override
    public void horizontalRule( SinkEventAttributes attributes )
    {
        addEvent( "horizontalRule", new Object[] {attributes} );
    }

    @Override
    public void anchor( String name, SinkEventAttributes attributes )
    {
        addEvent( "anchor", new Object[] {name, attributes} );
    }

    @Override
    public void link( String name, SinkEventAttributes attributes )
    {
        addEvent( "link", new Object[] {name, attributes} );
    }

    @Override
    public void inline( SinkEventAttributes attributes )
    {
        addEvent( "inline", new Object[] {attributes} );
    }

    @Override
    public void lineBreak( SinkEventAttributes attributes )
    {
        addEvent( "lineBreak", new Object[] {attributes} );
    }

    @Override
    public void lineBreakOpportunity( SinkEventAttributes attributes )
    {
        addEvent( "lineBreakOpportunity", new Object[] {attributes} );
    }

    @Override
    public void text( String text, SinkEventAttributes attributes )
    {
        addEvent( "text", new Object[] {text, attributes} );
    }

    @Override
    public void unknown( String name, Object[] requiredParams, SinkEventAttributes attributes )
    {
        addEvent( "unknown", new Object[] {name, requiredParams, attributes} );
    }

      //
     // private
    //

    /**
     * Adds a no-arg event to the list of events.
     *
     * @param string the name of the event.
     */
    private void addEvent( String string )
    {
        addEvent( string, null );
    }

    /**
     * Adds an event to the list of events.
     *
     * @param string the name of the event.
     * @param arguments The array of arguments to the sink method.
     */
    private void addEvent( String string, Object[] arguments )
    {
        events.add( new SinkEventElement( string, arguments ) );
    }

}
