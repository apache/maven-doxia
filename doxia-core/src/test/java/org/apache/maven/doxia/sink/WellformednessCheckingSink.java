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

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


/**
 * This sink is used for testing purposes in order to check wether
 * the input of some parser is well-formed.
 *
 * @author <a href="mailto:lars@trieloff.net">Lars Trieloff</a>
 * @version $Id$
 */
public class WellformednessCheckingSink
    extends AbstractSink
{
    private Stack elements = new Stack();

    private List errors = new LinkedList();

    public void head()
    {
        startElement( "head" );
    }

    public void head_()
    {
        checkWellformedness( "head" );
    }

    public void body()
    {
        startElement( "body" );
    }

    public void body_()
    {
        checkWellformedness( "body" );
    }

    public void section1()
    {
        startElement( "section1" );
    }

    public void section1_()
    {
        checkWellformedness( "section1" );
    }

    public void section2()
    {
        startElement( "section2" );
    }

    public void section2_()
    {
        checkWellformedness( "section2" );
    }

    public void section3()
    {
        startElement( "section3" );
    }

    public void section3_()
    {
        checkWellformedness( "section3" );
    }

    public void section4()
    {
        startElement( "section4" );
    }

    public void section4_()
    {
        checkWellformedness( "section4" );
    }

    public void section5()
    {
        startElement( "section5" );
    }

    public void section5_()
    {
        checkWellformedness( "section5" );
    }

    public void list()
    {
        startElement( "list" );
    }

    public void list_()
    {
        checkWellformedness( "list" );
    }

    public void listItem()
    {
        startElement( "listItem" );
    }

    public void listItem_()
    {
        checkWellformedness( "listItem" );
    }

    public void numberedList( int numbering )
    {
        startElement( "numberedList" );
    }

    public void numberedList_()
    {
        checkWellformedness( "numberedList" );
    }

    public void numberedListItem()
    {
        startElement( "numberedListItem" );
    }

    public void numberedListItem_()
    {
        checkWellformedness( "numberedListItem" );
    }

    public void definitionList()
    {
        startElement( "definitionList" );
    }

    public void definitionList_()
    {
        checkWellformedness( "definitionList" );
    }

    public void definitionListItem()
    {
        startElement( "definitionListItem" );
    }

    public void definitionListItem_()
    {
        checkWellformedness( "definitionListItem" );
    }

    public void definition()
    {
        startElement( "definition" );
    }

    public void definition_()
    {
        checkWellformedness( "definition" );
    }

    public void figure()
    {
        startElement( "figure" );
    }

    public void figure_()
    {
        checkWellformedness( "figure" );
    }

    public void table()
    {
        startElement( "table" );
    }

    public void table_()
    {
        checkWellformedness( "table" );
    }

    public void tableRows( int[] justification, boolean grid )
    {
        startElement( "tableRows" );
    }

    public void tableRows_()
    {
        checkWellformedness( "tableRows" );
    }

    public void tableRow()
    {
        startElement( "tableRow" );
    }

    public void tableRow_()
    {
        checkWellformedness( "tableRow" );
    }

    public void title()
    {
        startElement( "title" );
    }

    public void title_()
    {
        checkWellformedness( "title" );
    }

    public void author()
    {
        startElement( "author" );
    }

    public void author_()
    {
        checkWellformedness( "author" );
    }

    public void date()
    {
        startElement( "date" );
    }

    public void date_()
    {
        checkWellformedness( "date" );
    }

    public void sectionTitle()
    {
        startElement( "sectionTitle" );
    }

    public void sectionTitle_()
    {
        checkWellformedness( "sectionTitle" );
    }

    public void sectionTitle1()
    {
        startElement( "sectionTitle1" );
    }

    public void sectionTitle1_()
    {
        checkWellformedness( "sectionTitle1" );
    }

    public void sectionTitle2()
    {
        startElement( "sectionTitle2" );
    }

    public void sectionTitle2_()
    {
        checkWellformedness( "sectionTitle2" );
    }

    public void sectionTitle3()
    {
        startElement( "sectionTitle3" );
    }

    public void sectionTitle3_()
    {
        checkWellformedness( "sectionTitle3" );
    }

    public void sectionTitle4()
    {
        startElement( "sectionTitle4" );
    }

    public void sectionTitle4_()
    {
        checkWellformedness( "sectionTitle4" );
    }

    public void sectionTitle5()
    {
        startElement( "sectionTitle5" );
    }

    public void sectionTitle5_()
    {
        checkWellformedness( "sectionTitle5" );
    }

    public void paragraph()
    {
        startElement( "paragraph" );
    }

    public void paragraph_()
    {
        checkWellformedness( "paragraph" );
    }

    public void verbatim( boolean boxed )
    {
        startElement( "verbatim" );
    }

    public void verbatim_()
    {
        checkWellformedness( "verbatim" );
    }

    public void definedTerm()
    {
        startElement( "definedTerm" );
    }

    public void definedTerm_()
    {
        checkWellformedness( "definedTerm" );
    }

    public void figureCaption()
    {
        startElement( "figureCaption" );
    }

    public void figureCaption_()
    {
        checkWellformedness( "figureCaption" );
    }

    public void tableCell()
    {
        startElement( "tableCell" );
    }

    public void tableCell( String width )
    {
        startElement( "tableCell" );
    }

    public void tableCell_()
    {
        checkWellformedness( "tableCell" );
    }

    public void tableHeaderCell()
    {
        startElement( "tableHeaderCell" );
    }

    public void tableHeaderCell( String width )
    {
        startElement( "tableHeaderCell" );
    }

    public void tableHeaderCell_()
    {
        checkWellformedness( "tableHeaderCell" );
    }

    public void tableCaption()
    {
        startElement( "tableCaption" );
    }

    public void tableCaption_()
    {
        checkWellformedness( "tableCaption" );
    }

    public void figureGraphics( String name )
    {
    }

    public void horizontalRule()
    {
    }

    public void pageBreak()
    {
    }

    public void anchor( String name )
    {
        startElement( "anchor" );
    }

    public void anchor_()
    {
        checkWellformedness( "anchor" );
    }

    public void link( String name )
    {
        startElement( "link" );
    }

    public void link_()
    {
        checkWellformedness( "link" );
    }

    public void italic()
    {
        startElement( "italic" );
    }

    public void italic_()
    {
        checkWellformedness( "italic" );
    }

    public void bold()
    {
        startElement( "bold" );
    }

    public void bold_()
    {
        checkWellformedness( "bold" );
    }

    public void monospaced()
    {
        startElement( "monospaced" );
    }

    public void monospaced_()
    {
        checkWellformedness( "monospaced" );
    }

    public void lineBreak()
    {
    }

    public void nonBreakingSpace()
    {
    }

    public void text( String text )
    {
    }

    public void rawText( String text )
    {
    }

    /** {@inheritDoc} */
    public void comment( String comment )
    {
    }

    public void flush()
    {
    }

    public void close()
    {
    }

    /**
     * Finds out wether the wellformedness-contraints of the model have been
     * violated.
     *
     * @return false for non-wellformed models
     */
    public boolean isWellformed()
    {
        return errors.size() == 0;
    }

    /**
     * Gets the offending element that breaks the wellformedness as well
     * as the exepected element.
     *
     * @return the expected and acual elements
     */
    public String getOffender()
    {
        if ( isWellformed() )
        {
            return null;
        }

        return (String) errors.get( errors.size() - 1 );
    }

    /**
     * Gets the list of errors found during wellformedness-check
     *
     * @return a list of String of error messages
     */
    public List getOffenders()
    {
        return errors;
    }

    /**
     * Checks wether a newly encountered end-tag breaks the wellformedness
     * of the model.
     *
     * @param actual the local-name of the encountered element
     */
    private void checkWellformedness( String actual )
    {
        String expected = (String) elements.pop();

        if ( !expected.equals( actual ) )
        {
            errors.add( "Encountered closing: " + actual + ", expected " + expected );
        }
    }

    /**
     * Starts a new element and puts it on the stack in order to calculate
     * wellformedness of the model at a later point of time.
     *
     * @param string the local-name of the start-tag
     */
    private void startElement( String string )
    {
        elements.push( string );
    }

    /** {@inheritDoc} */
    public void head( SinkEventAttributes attributes )
    {
        head();
    }

    /** {@inheritDoc} */
    public void title( SinkEventAttributes attributes )
    {
        title();
    }

    /** {@inheritDoc} */
    public void author( SinkEventAttributes attributes )
    {
        author();
    }

    /** {@inheritDoc} */
    public void date( SinkEventAttributes attributes )
    {
        date();
    }

    /** {@inheritDoc} */
    public void body( SinkEventAttributes attributes )
    {
        body();
    }

    /** {@inheritDoc} */
    public void section( int level, SinkEventAttributes attributes )
    {
        startElement( "section" + level );
    }

    /** {@inheritDoc} */
    public void section_( int level )
    {
        checkWellformedness( "section" + level );
    }

    /** {@inheritDoc} */
    public void sectionTitle( int level, SinkEventAttributes attributes )
    {
        startElement( "sectionTitle" + level );
    }

    /** {@inheritDoc} */
    public void sectionTitle_( int level )
    {
        checkWellformedness( "sectionTitle" + level );
    }

    /** {@inheritDoc} */
    public void list( SinkEventAttributes attributes )
    {
        list();
    }

    /** {@inheritDoc} */
    public void listItem( SinkEventAttributes attributes )
    {
        listItem();
    }

    /** {@inheritDoc} */
    public void numberedList( int numbering, SinkEventAttributes attributes )
    {
        numberedList( numbering );
    }

    /** {@inheritDoc} */
    public void numberedListItem( SinkEventAttributes attributes )
    {
        numberedListItem();
    }

    /** {@inheritDoc} */
    public void definitionList( SinkEventAttributes attributes )
    {
        definitionList();
    }

    /** {@inheritDoc} */
    public void definitionListItem( SinkEventAttributes attributes )
    {
        definitionListItem();
    }

    /** {@inheritDoc} */
    public void definition( SinkEventAttributes attributes )
    {
        definition();
    }

    /** {@inheritDoc} */
    public void definedTerm( SinkEventAttributes attributes )
    {
        definedTerm();
    }

    /** {@inheritDoc} */
    public void figure( SinkEventAttributes attributes )
    {
        figure();
    }

    /** {@inheritDoc} */
    public void figureCaption( SinkEventAttributes attributes )
    {
        figureCaption();
    }

    /** {@inheritDoc} */
    public void figureGraphics( String src, SinkEventAttributes attributes )
    {
        figureGraphics( src );
    }

    /** {@inheritDoc} */
    public void table( SinkEventAttributes attributes )
    {
        table();
    }

    /** {@inheritDoc} */
    public void tableRow( SinkEventAttributes attributes )
    {
        tableRow();
    }

    /** {@inheritDoc} */
    public void tableCell( SinkEventAttributes attributes )
    {
        tableCell();
    }

    /** {@inheritDoc} */
    public void tableHeaderCell( SinkEventAttributes attributes )
    {
        tableHeaderCell();
    }

    /** {@inheritDoc} */
    public void tableCaption( SinkEventAttributes attributes )
    {
        tableCaption();
    }

    /** {@inheritDoc} */
    public void paragraph( SinkEventAttributes attributes )
    {
        paragraph();
    }

    /** {@inheritDoc} */
    public void verbatim( SinkEventAttributes attributes )
    {
        verbatim( false );
    }

    /** {@inheritDoc} */
    public void horizontalRule( SinkEventAttributes attributes )
    {
        horizontalRule();
    }

    /** {@inheritDoc} */
    public void anchor( String name, SinkEventAttributes attributes )
    {
        anchor( name );
    }

    /** {@inheritDoc} */
    public void link( String name, SinkEventAttributes attributes )
    {
        link( name );
    }

    /** {@inheritDoc} */
    public void italic( SinkEventAttributes attributes )
    {
        italic();
    }

    /** {@inheritDoc} */
    public void bold( SinkEventAttributes attributes )
    {
        bold();
    }

    /** {@inheritDoc} */
    public void monospaced( SinkEventAttributes attributes )
    {
        monospaced();
    }

    /** {@inheritDoc} */
    public void lineBreak( SinkEventAttributes attributes )
    {
        lineBreak();
    }

    /** {@inheritDoc} */
    public void text( String text, SinkEventAttributes attributes )
    {
        text( text );
    }
}
