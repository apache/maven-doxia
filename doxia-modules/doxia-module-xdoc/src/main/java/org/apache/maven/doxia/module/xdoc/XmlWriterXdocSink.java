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

import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.SinkAdapter;
import org.apache.maven.doxia.util.HtmlTools;
import org.apache.maven.doxia.util.StructureSinkUtils;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.XMLWriter;

/**
 * A doxia Sink which produces an xdoc document.
 *
 * @author juan <a href="mailto:james@jamestaylor.org">James Taylor</a>
 * @author Juan F. Codagnone  (replaced println with XmlWriterXdocSink)
 * @version $Id$
 * @component
 */
public class XmlWriterXdocSink
    extends SinkAdapter
{
    /** The XMLWriter to write the result. */
    private final XMLWriter writer;

    /** Used to collect text events. */
    private StringBuffer buffer = new StringBuffer();

    /** An indication on if we're inside a head. */
    private boolean headFlag;

    /** An indication on if we're in verbatim mode. */
    private boolean verbatimFlag;

    /** Justification of table cells. */
    private int[] cellJustif;

    /** Number of cells in a table row. */
    private int cellCount;

    /** An indication on if we're inside a item. */
    private int itemFlag;

    /** An indication on if we're inside a section title. */
    private boolean sectionTitleFlag;

    /**
     * Constructor, initialize the XMLWriter.
     *
     * @param out The XMLWriter to write the result.
     */
    public XmlWriterXdocSink( XMLWriter out )
    {
        if ( out == null )
        {
            throw new IllegalArgumentException( "Argument can't be null!" );
        }
        this.writer = out;
    }

    /**
     * Reset all variables.
     */
    protected void resetState()
    {
        headFlag = false;
        buffer = new StringBuffer();
        itemFlag = 0;
        verbatimFlag = false;
        cellJustif = null;
        cellCount = 0;
        sectionTitleFlag = false;
    }

    /** {@inheritDoc} */
    public void head()
    {
        resetState();

        headFlag = true;

        writer.startElement( "document" );
        writer.startElement( "properties" );
    }

    /** {@inheritDoc} */
    public void head_()
    {
        headFlag = false;

        writer.endElement(); // properties
    }

    /** {@inheritDoc} */
    public void title_()
    {
        if ( buffer.length() > 0 )
        {
            writer.startElement( "title" );
            content( buffer.toString() );
            writer.endElement(); // title
            buffer = new StringBuffer();
        }
    }

    /** {@inheritDoc} */
    public void author_()
    {
        if ( buffer.length() > 0 )
        {
            writer.startElement( "author" );
            content( buffer.toString() );
            writer.endElement(); // author
            buffer = new StringBuffer();
        }
    }

    /** {@inheritDoc} */
    public void date_()
    {
        if ( buffer.length() > 0 )
        {
            writer.startElement( "date" );
            content( buffer.toString() );
            writer.endElement();
            buffer = new StringBuffer();
        }
    }

    /** {@inheritDoc} */
    public void body()
    {
        writer.startElement( "body" );
    }

    /** {@inheritDoc} */
    public void body_()
    {
        writer.endElement(); // body

        writer.endElement(); // document

        resetState();
    }

    /** {@inheritDoc} */
    public void section1()
    {
        writer.startElement( "section" );
    }

    /** {@inheritDoc} */
    public void section2()
    {
        writer.startElement( "subsection" );
    }

    /** {@inheritDoc} */
    public void section3()
    {
        writer.startElement( "subsection" );
    }

    /** {@inheritDoc} */
    public void section4()
    {
        writer.startElement( "subsection" );
    }

    /** {@inheritDoc} */
    public void section5()
    {
        writer.startElement( "subsection" );
    }

    /** {@inheritDoc} */
    public void sectionTitle()
    {
        sectionTitleFlag = true;
        buffer = new StringBuffer();
    }

    /** {@inheritDoc} */
    public void sectionTitle_()
    {
        sectionTitleFlag = false;
        writer.addAttribute( "name", buffer.toString() );
    }

    /** {@inheritDoc} */
    public void section1_()
    {
        writer.endElement();
    }

    /** {@inheritDoc} */
    public void section2_()
    {
        writer.endElement();
    }

    /** {@inheritDoc} */
    public void section3_()
    {
        writer.endElement();
    }

    /** {@inheritDoc} */
    public void section4_()
    {
        writer.endElement();
    }

    /** {@inheritDoc} */
    public void section5_()
    {
        writer.endElement();
    }

    /** {@inheritDoc} */
    public void list()
    {
        writer.startElement( "ul" );
    }

    /** {@inheritDoc} */
    public void list_()
    {
        writer.endElement();
    }

    /** {@inheritDoc} */
    public void listItem()
    {
        writer.startElement( "li" );
        itemFlag++;
        // What follows is at least a paragraph.
    }

    /** {@inheritDoc} */
    public void listItem_()
    {
        writer.endElement();
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
        writer.startElement( "ol" );
        writer.addAttribute( "style", "list-style-type: " + style );
    }

    /** {@inheritDoc} */
    public void numberedList_()
    {
        writer.endElement();
    }

    /** {@inheritDoc} */
    public void numberedListItem()
    {
        writer.startElement( "li" );
        itemFlag++;
        // What follows is at least a paragraph.
    }

    /** {@inheritDoc} */
    public void numberedListItem_()
    {
        writer.endElement();
    }

    /** {@inheritDoc} */
    public void definitionList()
    {
        writer.startElement( "dl" );
        writer.addAttribute( "compact", "compact" );
    }

    /** {@inheritDoc} */
    public void definitionList_()
    {
        writer.endElement();
    }

    /** {@inheritDoc} */
    public void definedTerm()
    {
        writer.startElement( "dt" );
        writer.startElement( "b" );
    }

    /** {@inheritDoc} */
    public void definedTerm_()
    {
        writer.endElement();
        writer.endElement();
    }

    /** {@inheritDoc} */
    public void definition()
    {
        writer.startElement( "dd" );
        itemFlag++;
        // What follows is at least a paragraph.
    }

    /** {@inheritDoc} */
    public void definition_()
    {
        writer.endElement();
    }

    /** {@inheritDoc} */
    public void paragraph()
    {
        if ( itemFlag == 0 )
        {
            writer.startElement( "p" );
        }
    }

    /** {@inheritDoc} */
    public void paragraph_()
    {
        if ( itemFlag == 0 )
        {
            writer.endElement();
        }
        else
        {
            itemFlag--;
        }
    }

    /** {@inheritDoc} */
    public void verbatim( boolean boxed )
    {
        verbatimFlag = true;
        if ( boxed )
        {
            writer.startElement( "source" );
        }
        else
        {
            writer.startElement( "pre" );
        }
    }

    /** {@inheritDoc} */
    public void verbatim_()
    {
        writer.endElement();

        verbatimFlag = false;
    }

    /** {@inheritDoc} */
    public void horizontalRule()
    {
        writer.startElement( "hr" );
        writer.endElement();
    }

    /** {@inheritDoc} */
    public void table()
    {
        writer.startElement( "table" );
        writer.addAttribute( "align", "center" );
    }

    /** {@inheritDoc} */
    public void table_()
    {
        writer.endElement();
    }

    /** {@inheritDoc} */
    public void tableRows( int[] justification, boolean grid )
    {
        writer.startElement( "table" );
        writer.addAttribute( "align", "center" );
        writer.addAttribute( "border", String.valueOf( grid ? 1 : 0 ) );
        this.cellJustif = justification;
    }

    /** {@inheritDoc} */
    public void tableRows_()
    {
        writer.endElement();
    }

    /** {@inheritDoc} */
    public void tableRow()
    {
        writer.startElement( "tr" );
        writer.addAttribute( "valign", "top" );
        cellCount = 0;
    }

    /** {@inheritDoc} */
    public void tableRow_()
    {
        writer.endElement();
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

        writer.startElement( "t" + ( headerRow ? 'h' : 'd' ) );
        if ( justif != null )
        {
            writer.addAttribute( "align", justif );
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
        writer.endElement();
        ++cellCount;
    }

    /** {@inheritDoc} */
    public void tableCaption()
    {
        writer.startElement( "p" );
        writer.startElement( "i" );
    }

    /** {@inheritDoc} */
    public void tableCaption_()
    {
        writer.endElement();
        writer.endElement();
    }

    /** {@inheritDoc} */
    public void anchor( String name )
    {
        if ( !headFlag )
        {
            String id = StructureSinkUtils.linkToKey( name );
            writer.startElement( "a" );
            writer.addAttribute( "id", id );
            writer.addAttribute( "name", id );
        }
    }

    /** {@inheritDoc} */
    public void anchor_()
    {
        if ( !headFlag )
        {
            writer.endElement();
        }
    }

    /** {@inheritDoc} */
    public void link( String name )
    {
        if ( !headFlag )
        {
            writer.startElement( "a" );
            writer.addAttribute( "href", name );
        }
    }

    /** {@inheritDoc} */
    public void link_()
    {
        if ( !headFlag )
        {
            writer.endElement();
        }
    }

    /** {@inheritDoc} */
    public void italic()
    {
        if ( !headFlag )
        {
            writer.startElement( "i" );
        }
    }

    /** {@inheritDoc} */
    public void italic_()
    {
        if ( !headFlag )
        {
            writer.endElement();
        }
    }

    /** {@inheritDoc} */
    public void bold()
    {
        if ( !headFlag )
        {
            writer.startElement( "b" );
        }
    }

    /** {@inheritDoc} */
    public void bold_()
    {
        if ( !headFlag )
        {
            writer.endElement();
        }
    }

    /** {@inheritDoc} */
    public void monospaced()
    {
        if ( !headFlag )
        {
            writer.startElement( "tt" );
        }
    }

    /** {@inheritDoc} */
    public void monospaced_()
    {
        if ( !headFlag )
        {
            writer.endElement();
        }
    }

    /** {@inheritDoc} */
    public void lineBreak()
    {
        if ( headFlag )
        {
            buffer.append( '\n' );
        }
        else
        {
            writer.startElement( "br" );
            writer.endElement();
        }
    }

    /** {@inheritDoc} */
    public void nonBreakingSpace()
    {
        if ( headFlag )
        {
            buffer.append( ' ' );
        }
        else
        {
            writer.writeText( "&#160;" );
        }
    }

    /** {@inheritDoc} */
    public void text( String text )
    {
        if ( headFlag )
        {
            buffer.append( text );
        }
        else if ( sectionTitleFlag )
        {
            buffer.append( text );
        }
        else
        {
            if ( verbatimFlag )
            {
                verbatimContent( text );
            }
            else
            {
                content( text );
            }
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------


    /**
     * Write HTML escaped text to output.
     *
     * @param text The text to write.
     */
    protected void content( String text )
    {
        writer.writeText( escapeHTML( text ) );
    }

    /**
     * Write text to output, preserving white space.
     *
     * @param text The text to write.
     */
    protected void verbatimContent( String text )
    {
        writer.writeText( StringUtils.replace( text, " ", "&nbsp;" ) );
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
    }

    /** {@inheritDoc} */
    public void close()
    {
    }
}
