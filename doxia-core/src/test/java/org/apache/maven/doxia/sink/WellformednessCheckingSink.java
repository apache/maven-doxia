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
    private final Stack<String> elements = new Stack<String>();

    private final List<String> errors = new LinkedList<String>();

    /** {@inheritDoc} */
    public void head()
    {
        startElement( "head" );
    }

    /** {@inheritDoc} */
    public void head_()
    {
        checkWellformedness( "head" );
    }

    /** {@inheritDoc} */
    public void body()
    {
        startElement( "body" );
    }

    /** {@inheritDoc} */
    public void body_()
    {
        checkWellformedness( "body" );
    }

    /** {@inheritDoc} */
    public void section1()
    {
        startElement( "section1" );
    }

    /** {@inheritDoc} */
    public void section1_()
    {
        checkWellformedness( "section1" );
    }

    /** {@inheritDoc} */
    public void section2()
    {
        startElement( "section2" );
    }

    /** {@inheritDoc} */
    public void section2_()
    {
        checkWellformedness( "section2" );
    }

    /** {@inheritDoc} */
    public void section3()
    {
        startElement( "section3" );
    }

    /** {@inheritDoc} */
    public void section3_()
    {
        checkWellformedness( "section3" );
    }

    /** {@inheritDoc} */
    public void section4()
    {
        startElement( "section4" );
    }

    /** {@inheritDoc} */
    public void section4_()
    {
        checkWellformedness( "section4" );
    }

    /** {@inheritDoc} */
    public void section5()
    {
        startElement( "section5" );
    }

    /** {@inheritDoc} */
    public void section5_()
    {
        checkWellformedness( "section5" );
    }

    /** {@inheritDoc} */
    public void list()
    {
        startElement( "list" );
    }

    /** {@inheritDoc} */
    public void list_()
    {
        checkWellformedness( "list" );
    }

    /** {@inheritDoc} */
    public void listItem()
    {
        startElement( "listItem" );
    }

    /** {@inheritDoc} */
    public void listItem_()
    {
        checkWellformedness( "listItem" );
    }

    /** {@inheritDoc} */
    public void numberedList( int numbering )
    {
        startElement( "numberedList" );
    }

    /** {@inheritDoc} */
    public void numberedList_()
    {
        checkWellformedness( "numberedList" );
    }

    /** {@inheritDoc} */
    public void numberedListItem()
    {
        startElement( "numberedListItem" );
    }

    /** {@inheritDoc} */
    public void numberedListItem_()
    {
        checkWellformedness( "numberedListItem" );
    }

    /** {@inheritDoc} */
    public void definitionList()
    {
        startElement( "definitionList" );
    }

    /** {@inheritDoc} */
    public void definitionList_()
    {
        checkWellformedness( "definitionList" );
    }

    /** {@inheritDoc} */
    public void definitionListItem()
    {
        startElement( "definitionListItem" );
    }

    /** {@inheritDoc} */
    public void definitionListItem_()
    {
        checkWellformedness( "definitionListItem" );
    }

    /** {@inheritDoc} */
    public void definition()
    {
        startElement( "definition" );
    }

    /** {@inheritDoc} */
    public void definition_()
    {
        checkWellformedness( "definition" );
    }

    /** {@inheritDoc} */
    public void figure()
    {
        startElement( "figure" );
    }

    /** {@inheritDoc} */
    public void figure_()
    {
        checkWellformedness( "figure" );
    }

    /**
     * {@inheritDoc}
     * @since 1.7
     */
    public void ssi( final String directive )
    {
        // ignore
    }

    /** {@inheritDoc} */
    public void table()
    {
        startElement( "table" );
    }

    /** {@inheritDoc} */
    public void table_()
    {
        checkWellformedness( "table" );
    }

    /** {@inheritDoc} */
    public void tableRows( int[] justification, boolean grid )
    {
        startElement( "tableRows" );
    }

    /** {@inheritDoc} */
    public void tableRows_()
    {
        checkWellformedness( "tableRows" );
    }

    /** {@inheritDoc} */
    public void tableRow()
    {
        startElement( "tableRow" );
    }

    /** {@inheritDoc} */
    public void tableRow_()
    {
        checkWellformedness( "tableRow" );
    }

    /** {@inheritDoc} */
    public void title()
    {
        startElement( "title" );
    }

    /** {@inheritDoc} */
    public void title_()
    {
        checkWellformedness( "title" );
    }

    /** {@inheritDoc} */
    public void author()
    {
        startElement( "author" );
    }

    /** {@inheritDoc} */
    public void author_()
    {
        checkWellformedness( "author" );
    }

    /** {@inheritDoc} */
    public void date()
    {
        startElement( "date" );
    }

    /** {@inheritDoc} */
    public void date_()
    {
        checkWellformedness( "date" );
    }

    /** {@inheritDoc} */
    public void sectionTitle()
    {
        startElement( "sectionTitle" );
    }

    /** {@inheritDoc} */
    public void sectionTitle_()
    {
        checkWellformedness( "sectionTitle" );
    }

    /** {@inheritDoc} */
    public void sectionTitle1()
    {
        startElement( "sectionTitle1" );
    }

    /** {@inheritDoc} */
    public void sectionTitle1_()
    {
        checkWellformedness( "sectionTitle1" );
    }

    /** {@inheritDoc} */
    public void sectionTitle2()
    {
        startElement( "sectionTitle2" );
    }

    /** {@inheritDoc} */
    public void sectionTitle2_()
    {
        checkWellformedness( "sectionTitle2" );
    }

    /** {@inheritDoc} */
    public void sectionTitle3()
    {
        startElement( "sectionTitle3" );
    }

    /** {@inheritDoc} */
    public void sectionTitle3_()
    {
        checkWellformedness( "sectionTitle3" );
    }

    /** {@inheritDoc} */
    public void sectionTitle4()
    {
        startElement( "sectionTitle4" );
    }

    /** {@inheritDoc} */
    public void sectionTitle4_()
    {
        checkWellformedness( "sectionTitle4" );
    }

    /** {@inheritDoc} */
    public void sectionTitle5()
    {
        startElement( "sectionTitle5" );
    }

    /** {@inheritDoc} */
    public void sectionTitle5_()
    {
        checkWellformedness( "sectionTitle5" );
    }

    /** {@inheritDoc} */
    public void paragraph()
    {
        startElement( "paragraph" );
    }

    /** {@inheritDoc} */
    public void paragraph_()
    {
        checkWellformedness( "paragraph" );
    }

    /** {@inheritDoc} */
    public void verbatim( boolean boxed )
    {
        startElement( "verbatim" );
    }

    /** {@inheritDoc} */
    public void verbatim_()
    {
        checkWellformedness( "verbatim" );
    }

    /** {@inheritDoc} */
    public void definedTerm()
    {
        startElement( "definedTerm" );
    }

    /** {@inheritDoc} */
    public void definedTerm_()
    {
        checkWellformedness( "definedTerm" );
    }

    /** {@inheritDoc} */
    public void figureCaption()
    {
        startElement( "figureCaption" );
    }

    /** {@inheritDoc} */
    public void figureCaption_()
    {
        checkWellformedness( "figureCaption" );
    }

    /** {@inheritDoc} */
    public void tableCell()
    {
        startElement( "tableCell" );
    }

    /** {@inheritDoc} */
    public void tableCell( String width )
    {
        startElement( "tableCell" );
    }

    /** {@inheritDoc} */
    public void tableCell_()
    {
        checkWellformedness( "tableCell" );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell()
    {
        startElement( "tableHeaderCell" );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell( String width )
    {
        startElement( "tableHeaderCell" );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell_()
    {
        checkWellformedness( "tableHeaderCell" );
    }

    /** {@inheritDoc} */
    public void tableCaption()
    {
        startElement( "tableCaption" );
    }

    /** {@inheritDoc} */
    public void tableCaption_()
    {
        checkWellformedness( "tableCaption" );
    }

    /** {@inheritDoc} */
    public void figureGraphics( String name )
    {
        // nop
    }

    /** {@inheritDoc} */
    public void horizontalRule()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void pageBreak()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void anchor( String name )
    {
        startElement( "anchor" );
    }

    /** {@inheritDoc} */
    public void anchor_()
    {
        checkWellformedness( "anchor" );
    }

    /** {@inheritDoc} */
    public void link( String name )
    {
        startElement( "link" );
    }

    /** {@inheritDoc} */
    public void link_()
    {
        checkWellformedness( "link" );
    }

    /** {@inheritDoc} */
    public void italic()
    {
        startElement( "italic" );
    }

    /** {@inheritDoc} */
    public void italic_()
    {
        checkWellformedness( "italic" );
    }

    /** {@inheritDoc} */
    public void bold()
    {
        startElement( "bold" );
    }

    /** {@inheritDoc} */
    public void bold_()
    {
        checkWellformedness( "bold" );
    }

    /** {@inheritDoc} */
    public void monospaced()
    {
        startElement( "monospaced" );
    }

    /** {@inheritDoc} */
    public void monospaced_()
    {
        checkWellformedness( "monospaced" );
    }

    /** {@inheritDoc} */
    public void lineBreak()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void nonBreakingSpace()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void text( String text )
    {
        // nop
    }

    /** {@inheritDoc} */
    public void rawText( String text )
    {
        // nop
    }

    /** {@inheritDoc} */
    public void comment( String comment )
    {
        // nop
    }

    /** {@inheritDoc} */
    public void flush()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void close()
    {
        this.elements.clear();
        this.errors.clear();
    }

    /**
     * Finds out wether the wellformedness-contraints of the model have been
     * violated.
     *
     * @return false for non-wellformed models
     */
    public boolean isWellformed()
    {
        return errors.isEmpty();
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

        return errors.get( errors.size() - 1 );
    }

    /**
     * Gets the list of errors found during wellformedness-check
     *
     * @return a list of String of error messages
     */
    public List<String> getOffenders()
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
        String expected = elements.pop();

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
    public void lineBreak( SinkEventAttributes attributes )
    {
        lineBreak();
    }

    /** {@inheritDoc} */
    public void text( String text, SinkEventAttributes attributes )
    {
        text( text );
    }

    /** {@inheritDoc} */
    public void unknown( String name, Object[] requiredParams, SinkEventAttributes attributes )
    {
        // ignore
    }
}
