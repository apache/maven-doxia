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

import java.io.Writer;

import org.apache.maven.doxia.util.HtmlTools;
import org.apache.maven.doxia.sink.SinkAdapter;
import org.apache.maven.doxia.util.LineBreaker;
import org.apache.maven.doxia.parser.Parser;

/**
 * A doxia Sink which produces an xdoc model.
 *
 * @author <a href="mailto:james@jamestaylor.org">James Taylor</a>
 * @version $Id:XdocSink.java 348605 2005-11-24 12:02:44 +1100 (Thu, 24 Nov 2005) brett $
 */
public class XdocSink
    extends SinkAdapter
{
    /** System-dependent EOL. */
    protected static final String EOL = System.getProperty( "line.separator" );

    /** The LineBreaker to write the result. */
    protected LineBreaker out;

    /** Used to collect text events. */
    protected StringBuffer buffer = new StringBuffer();

    /** An indication on if we're inside a head. */
    protected boolean headFlag;

    /**
     * An indication on if we're inside a title.
     *
     * This will prevent the styling of titles.
     */
    protected boolean titleFlag;

    /** An indication on if we're inside a item. */
    private boolean itemFlag;

    /** An indication on if we're inside a box (verbatim). */
    private boolean boxedFlag;

    /** An indication on if we're in verbatim mode. */
    private boolean verbatimFlag;

    /** Justification of table cells. */
    private int[] cellJustif;

    /** Number of cells in a table row. */
    private int cellCount;

    /**
     * Constructor, initialize the LineBreaker.
     *
     * @param writer The writer to write the result.
     */
    public XdocSink( Writer writer )
    {
        this.out = new LineBreaker( writer );
    }

    /**
     * Reset all variables.
     */
    protected void resetState()
    {
        headFlag = false;
        buffer = new StringBuffer();
        itemFlag = false;
        boxedFlag = false;
        verbatimFlag = false;
        cellJustif = null;
        cellCount = 0;
    }

    /** {@inheritDoc} */
    public void head()
    {
        resetState();

        headFlag = true;

        markup( "<?xml version=\"1.0\" ?>" + EOL );

        markup( "<document>" + EOL );

        markup( "<properties>" + EOL );
    }

    /** {@inheritDoc} */
    public void head_()
    {
        headFlag = false;

        markup( "</properties>" + EOL );
    }

    /** {@inheritDoc} */
    public void title_()
    {
        if ( buffer.length() > 0 )
        {
            markup( "<title>" );
            content( buffer.toString() );
            markup( "</title>" + EOL );
            buffer = new StringBuffer();
        }
    }

    /** {@inheritDoc} */
    public void author_()
    {
        if ( buffer.length() > 0 )
        {
            markup( "<author>" );
            content( buffer.toString() );
            markup( "</author>" + EOL );
            buffer = new StringBuffer();
        }
    }

    /** {@inheritDoc} */
    public void date_()
    {
        if ( buffer.length() > 0 )
        {
            markup( "<date>" );
            content( buffer.toString() );
            markup( "</date>" );
            buffer = new StringBuffer();
        }
    }

    /** {@inheritDoc} */
    public void body()
    {
        markup( "<body>" + EOL );
    }

    /** {@inheritDoc} */
    public void body_()
    {
        markup( "</body>" + EOL );

        markup( "</document>" + EOL );

        out.flush();

        resetState();
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void section1()
    {
        onSection( 1 );
    }

    /** {@inheritDoc} */
    public void sectionTitle1()
    {
        onSectionTitle( 1 );
    }

    /** {@inheritDoc} */
    public void sectionTitle1_()
    {
        onSectionTitle_( 1 );
    }

    /** {@inheritDoc} */
    public void section1_()
    {
        onSection_( 1 );
    }

    /** {@inheritDoc} */
    public void section2()
    {
        onSection( 2 );
    }

    /** {@inheritDoc} */
    public void sectionTitle2()
    {
        onSectionTitle( 2 );
    }

    /** {@inheritDoc} */
    public void sectionTitle2_()
    {
        onSectionTitle_( 2 );
    }

    /** {@inheritDoc} */
    public void section2_()
    {
        onSection_( 2 );
    }

    /** {@inheritDoc} */
    public void section3()
    {
        onSection( 3 );
    }

    /** {@inheritDoc} */
    public void sectionTitle3()
    {
        onSectionTitle( 3 );
    }

    /** {@inheritDoc} */
    public void sectionTitle3_()
    {
        onSectionTitle_( 3 );
    }

    /** {@inheritDoc} */
    public void section3_()
    {
        onSection_( 3 );
    }

    /** {@inheritDoc} */
    public void section4()
    {
        onSection( 4 );
    }

    /** {@inheritDoc} */
    public void sectionTitle4()
    {
        onSectionTitle( 4 );
    }

    /** {@inheritDoc} */
    public void sectionTitle4_()
    {
        onSectionTitle_( 4 );
    }

    /** {@inheritDoc} */
    public void section4_()
    {
        onSection_( 4 );
    }

    /** {@inheritDoc} */
    public void section5()
    {
        onSection( 5 );
    }

    /** {@inheritDoc} */
    public void sectionTitle5()
    {
        onSectionTitle( 5 );
    }

    /** {@inheritDoc} */
    public void sectionTitle5_()
    {
        onSectionTitle_( 5 );
    }

    /** {@inheritDoc} */
    public void section5_()
    {
        onSection_( 5 );
    }

    /**
     * Starts a section.
     *
     * @param depth The level of the section.
     */
    private void onSection( int depth )
    {
        if ( depth == 1 )
        {
            markup( "<section name=\"" );
        }
        else if ( depth == 2 )
        {
            markup( "<subsection name=\"" );
        }
    }

    /**
     * Starts a section title.
     *
     * @param depth The level of the section title.
     */
    private void onSectionTitle( int depth )
    {
        if ( depth == 3 )
        {
            markup( "<h4>" );
        }
        else if ( depth == 4 )
        {
            markup( "<h5>" );
        }
        else if ( depth == 5 )
        {
            markup( "<h6>" );
        }

        titleFlag = true;
    }

    /**
     * Ends a section title.
     *
     * @param depth The level of the section title.
     */
    private void onSectionTitle_( int depth )
    {
        if ( depth == 1 || depth == 2 )
        {
            markup( "\">" );
        }
        else if ( depth == 3 )
        {
            markup( "</h4>" );
        }
        else if ( depth == 4 )
        {
            markup( "</h5>" );
        }
        else if ( depth == 5 )
        {
            markup( "</h6>" );
        }

        titleFlag = false;
    }

    /**
     * Ends a section.
     *
     * @param depth The level of the section.
     */
    private void onSection_( int depth )
    {
        if ( depth == 1 )
        {
            markup( "</section>" );
        }
        else if ( depth == 2 )
        {
            markup( "</subsection>" );
        }
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void list()
    {
        markup( "<ul>" + EOL );
    }

    /** {@inheritDoc} */
    public void list_()
    {
        markup( "</ul>" );
        itemFlag = false;
    }

    /** {@inheritDoc} */
    public void listItem()
    {
        markup( "<li>" );
        itemFlag = true;
        // What follows is at least a paragraph.
    }

    /** {@inheritDoc} */
    public void listItem_()
    {
        markup( "</li>" + EOL );
    }

    /** {@inheritDoc} */
    public void numberedList( int numbering )
    {
        String style;
        switch ( numbering )
        {
            case NUMBERING_UPPER_ALPHA:
                style = "upper-alpha";
                break;
            case NUMBERING_LOWER_ALPHA:
                style = "lower-alpha";
                break;
            case NUMBERING_UPPER_ROMAN:
                style = "upper-roman";
                break;
            case NUMBERING_LOWER_ROMAN:
                style = "lower-roman";
                break;
            case NUMBERING_DECIMAL:
            default:
                style = "decimal";
        }
        markup( "<ol style=\"list-style-type: " + style + "\">" + EOL );
    }

    /** {@inheritDoc} */
    public void numberedList_()
    {
        markup( "</ol>" );
        itemFlag = false;
    }

    /** {@inheritDoc} */
    public void numberedListItem()
    {
        markup( "<li>" );
        itemFlag = true;
        // What follows is at least a paragraph.
    }

    /** {@inheritDoc} */
    public void numberedListItem_()
    {
        markup( "</li>" + EOL );
    }

    /** {@inheritDoc} */
    public void definitionList()
    {
        markup( "<dl>" + EOL );
    }

    /** {@inheritDoc} */
    public void definitionList_()
    {
        markup( "</dl>" );
        itemFlag = false;
    }

    /** {@inheritDoc} */
    public void definedTerm()
    {
        markup( "<dt>" );
    }

    /** {@inheritDoc} */
    public void definedTerm_()
    {
        markup( "</dt>" + EOL );
    }

    /** {@inheritDoc} */
    public void definition()
    {
        markup( "<dd>" );
        itemFlag = true;
        // What follows is at least a paragraph.
    }

    /** {@inheritDoc} */
    public void definition_()
    {
        markup( "</dd>" + EOL );
    }

    /** {@inheritDoc} */
    public void figure()
    {
        markup( "<img" );
    }

    /** {@inheritDoc} */
    public void figure_()
    {
        markup( " />" );
    }

    /** {@inheritDoc} */
    public void figureGraphics( String s )
    {
        markup( " src=\"" + s + "\"" );
    }

    /** {@inheritDoc} */
    public void figureCaption()
    {
        markup( " alt=\"" );
    }

    /** {@inheritDoc} */
    public void figureCaption_()
    {
        markup( "\"" );
    }

    /** {@inheritDoc} */
    public void paragraph()
    {
        if ( !itemFlag )
        {
            markup( "<p>" );
        }
    }

    /** {@inheritDoc} */
    public void paragraph_()
    {
        if ( itemFlag )
        {
            itemFlag = false;
        }
        else
        {
            markup( "</p>" );
        }
    }

    /** {@inheritDoc} */
    public void verbatim( boolean boxed )
    {
        verbatimFlag = true;
        boxedFlag = boxed;
        if ( boxed )
        {
            markup( "<source>" );
        }
        else
        {
            markup( "<pre>" );
        }
    }

    /** {@inheritDoc} */
    public void verbatim_()
    {
        if ( boxedFlag )
        {
            markup( "</source>" );
        }
        else
        {
            markup( "</pre>" );
        }

        verbatimFlag = false;

        boxedFlag = false;
    }

    /** {@inheritDoc} */
    public void horizontalRule()
    {
        markup( "<hr />" );
    }

    /** {@inheritDoc} */
    public void table()
    {
        markup( "<table align=\"center\">" + EOL );
    }

    /** {@inheritDoc} */
    public void table_()
    {
        markup( "</table>" );
    }

    /** {@inheritDoc} */
    public void tableRows( int[] justification, boolean grid )

    {
        markup( "<table align=\"center\" border=\"" + ( grid ? 1 : 0 ) + "\">" + EOL );
        this.cellJustif = justification;
    }

    /** {@inheritDoc} */
    public void tableRows_()
    {
        markup( "</table>" );
    }

    /** {@inheritDoc} */
    public void tableRow()
    {
        markup( "<tr valign=\"top\">" + EOL );
        cellCount = 0;
    }

    /** {@inheritDoc} */
    public void tableRow_()
    {
        markup( "</tr>" + EOL );
        cellCount = 0;
    }

    /** {@inheritDoc} */
    public void tableCell()
    {
        tableCell( false );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell()
    {
        tableCell( true );
    }

    /**
     * Starts a table cell.
     *
     * @param headerRow If this cell is part of a header row.
     */
    public void tableCell( boolean headerRow )
    {
        String justif = null;

        if ( cellJustif != null )
        {
            switch ( cellJustif[cellCount] )
            {
                case Parser.JUSTIFY_LEFT:
                    justif = "left";
                    break;
                case Parser.JUSTIFY_RIGHT:
                    justif = "right";
                    break;
                case Parser.JUSTIFY_CENTER:
                default:
                    justif = "center";
                    break;
            }
        }

        if ( justif != null )
        {
            markup( "<t" + ( headerRow ? 'h' : 'd' ) + " align=\"" + justif + "\">" );
        }
        else
        {
            markup( "<t" + ( headerRow ? 'h' : 'd' ) + ">" );
        }
    }

    /** {@inheritDoc} */
    public void tableCell_()
    {
        tableCell_( false );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell_()
    {
        tableCell_( true );
    }

    /**
     * Ends a table cell.
     *
     * @param headerRow If this cell is part of a header row.
     */
    public void tableCell_( boolean headerRow )
    {
        markup( "</t" + ( headerRow ? 'h' : 'd' ) + ">" + EOL );
        ++cellCount;
    }

    /** {@inheritDoc} */
    public void tableCaption()
    {
        markup( "<p><i>" );
    }

    /** {@inheritDoc} */
    public void tableCaption_()
    {
        markup( "</i></p>" );
    }

    /** {@inheritDoc} */
    public void anchor( String name )
    {
        if ( !headFlag && !titleFlag )
        {
            String id = HtmlTools.encodeId( name );
            markup( "<a id=\"" + id + "\" name=\"" + id + "\">" );
        }
    }

    /** {@inheritDoc} */
    public void anchor_()
    {
        if ( !headFlag && !titleFlag )
        {
            markup( "</a>" );
        }
    }

    /** {@inheritDoc} */
    public void link( String name )
    {
        if ( !headFlag && !titleFlag )
        {
            markup( "<a href=\"" + name + "\">" );
        }
    }

    /** {@inheritDoc} */
    public void link_()
    {
        if ( !headFlag && !titleFlag )
        {
            markup( "</a>" );
        }
    }

    /** {@inheritDoc} */
    public void italic()
    {
        if ( !headFlag && !titleFlag )
        {
            markup( "<i>" );
        }
    }

    /** {@inheritDoc} */
    public void italic_()
    {
        if ( !headFlag && !titleFlag )
        {
            markup( "</i>" );
        }
    }

    /** {@inheritDoc} */
    public void bold()
    {
        if ( !headFlag && !titleFlag )
        {
            markup( "<b>" );
        }
    }

    /** {@inheritDoc} */
    public void bold_()
    {
        if ( !headFlag && !titleFlag )
        {
            markup( "</b>" );
        }
    }

    /** {@inheritDoc} */
    public void monospaced()
    {
        if ( !headFlag && !titleFlag )
        {
            markup( "<tt>" );
        }
    }

    /** {@inheritDoc} */
    public void monospaced_()
    {
        if ( !headFlag && !titleFlag )
        {
            markup( "</tt>" );
        }
    }

    /** {@inheritDoc} */
    public void lineBreak()
    {
        if ( headFlag || titleFlag )
        {
            buffer.append( EOL );
        }
        else
        {
            markup( "<br />" );
        }
    }

    /** {@inheritDoc} */
    public void nonBreakingSpace()
    {
        if ( headFlag || titleFlag )
        {
            buffer.append( ' ' );
        }
        else
        {
            markup( "&#160;" );
        }
    }

    /** {@inheritDoc} */
    public void text( String text )
    {
        if ( headFlag )
        {
            buffer.append( text );
        }
        else if ( verbatimFlag )
        {
            verbatimContent( text );
        }
        else
        {
            content( text );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Write text to output, preserving white space.
     *
     * @param text The text to write.
     */
    protected void markup( String text )
    {
        out.write( text, true );
    }

    /**
     * Write HTML escaped text to output, not preserving white space.
     *
     * @param text The text to write.
     */
    protected void content( String text )
    {
        out.write( escapeHTML( text ), false );
    }

    /**
     * Write HTML escaped text to output, preserving white space.
     *
     * @param text The text to write.
     */
    protected void verbatimContent( String text )
    {
        out.write( escapeHTML( text ), true );
    }

    /**
     * Forward to HtmlTools.escapeHTML( text ).
     *
     * @param text the String to escape, may be null
     * @return the text escaped, "" if null String input
     * @see org.apache.maven.doxia.util.HtmlTools#escapeHTML(String).
     */
    public static String escapeHTML( String text )
    {
        return HtmlTools.escapeHTML( text );
    }

    /**
     * Forward to HtmlTools.encodeURL( text ).
     *
     * @param text the String to encode, may be null.
     * @return the text encoded, null if null String input.
     * @see org.apache.maven.doxia.util.HtmlTools#encodeURL(String).
     */
    public static String encodeURL( String text )
    {
        return HtmlTools.encodeURL( text );
    }

    /** {@inheritDoc} */
    public void flush()
    {
        out.flush();
    }

    /** {@inheritDoc} */
    public void close()
    {
        out.close();
    }
}
