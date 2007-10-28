package org.apache.maven.doxia.module.xhtml;

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

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.module.xhtml.decoration.render.RenderingContext;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.AbstractXmlSink;
import org.apache.maven.doxia.sink.StructureSink;
import org.apache.maven.doxia.util.HtmlTools;
import org.codehaus.plexus.util.StringUtils;

/**
 * Xhtml sink implementation.
 *
 * @author Jason van Zyl
 * @version $Id$
 * @since 1.0
 * @plexus.component role="org.apache.maven.doxia.sink.Sink" role-hint="xhtml"
 */
public class XhtmlSink
    extends AbstractXmlSink
    implements XhtmlMarkup
{
    // ----------------------------------------------------------------------
    // Instance fields
    // ----------------------------------------------------------------------

    /** The PrintWriter to write the result. */
    private PrintWriter writer;

    /** Used to collect text events. */
    private StringBuffer buffer = new StringBuffer();

    /** An indication on if we're inside a head. */
    private boolean headFlag;

    /** An indication on if we're in verbatim mode. */
    private boolean verbatimFlag;

    // TODO: this doesn't belong here
    private RenderingContext renderingContext;

    /** Justification of table cells. */
    private int[] cellJustif;

    /** Number of cells in a table row. */
    private int cellCount;

    /** Used to style successive table rows differently. */
    private boolean evenTableRow = true;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * @param writer
     */
    public XhtmlSink( Writer writer )
    {
        this( writer, null );
    }

    /**
     * @param writer
     * @param renderingContext
     */
    public XhtmlSink( Writer writer, RenderingContext renderingContext )
    {
        this.writer = new PrintWriter( writer );

        this.renderingContext = renderingContext;
    }

    /**
     * @param writer
     * @param renderingContext
     * @param directives
     * @todo directives Map is not used
     */
    public XhtmlSink( Writer writer, RenderingContext renderingContext, Map directives )
    {
        this( writer, renderingContext );
    }

    // ----------------------------------------------------------------------
    // Public protected methods
    // ----------------------------------------------------------------------

    /**
     * @return the current buffer
     */
    protected StringBuffer getBuffer()
    {
        return buffer;
    }

    /**
     * @param headFlag an header flag
     */
    protected void setHeadFlag( boolean headFlag )
    {
        this.headFlag = headFlag;
    }

    /**
     * Reset all variables.
     */
    protected void resetState()
    {
        headFlag = false;
        resetBuffer();
        verbatimFlag = false;
        cellJustif = null;
        cellCount = 0;
    }

    /**
     * Reset the buffer.
     */
    protected void resetBuffer()
    {
        buffer = new StringBuffer();
    }

    /** {@inheritDoc} */
    public void head()
    {
        resetState();

        headFlag = true;

        write( "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" );
        write( "<html xmlns=\"http://www.w3.org/1999/xhtml\">" );

        writeStartTag( Tag.HEAD );
    }

    /** {@inheritDoc} */
    public void head_()
    {
        headFlag = false;

        writeEndTag( Tag.HEAD );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void title()
    {
        writeStartTag( Tag.TITLE );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void title_()
    {
        content( buffer.toString() );

        writeEndTag( Tag.TITLE );

        resetBuffer();
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#META
     */
    public void author_()
    {
        if ( buffer.length() > 0 )
        {
            MutableAttributeSet att = new SimpleAttributeSet();
            att.addAttribute( Attribute.NAME, "author" );
            att.addAttribute( Attribute.CONTENT, buffer.toString() );

            writeSimpleTag( Tag.META, att );

            resetBuffer();
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#META
     */
    public void date_()
    {
        if ( buffer.length() > 0 )
        {
            MutableAttributeSet att = new SimpleAttributeSet();
            att.addAttribute( Attribute.NAME, "date" );
            att.addAttribute( Attribute.CONTENT, buffer.toString() );

            writeSimpleTag( Tag.META, att );

            resetBuffer();
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#BODY
     */
    public void body()
    {
        writeStartTag( Tag.BODY );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#BODY
     * @see javax.swing.text.html.HTML.Tag#HTML
     */
    public void body_()
    {
        writeEndTag( Tag.BODY );

        writeEndTag( Tag.HTML );

        flush();

        resetState();
    }

    // ----------------------------------------------------------------------
    // Sections
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void section1()
    {
        onSection( SECTION_LEVEL_1 );
    }

    /** {@inheritDoc} */
    public void sectionTitle1()
    {
        onSectionTitle( SECTION_LEVEL_1 );
    }

    /** {@inheritDoc} */
    public void sectionTitle1_()
    {
        onSectionTitle_( SECTION_LEVEL_1 );
    }

    /** {@inheritDoc} */
    public void section1_()
    {
        onSection_( SECTION_LEVEL_1 );
    }

    /** {@inheritDoc} */
    public void section2()
    {
        onSection( SECTION_LEVEL_2 );
    }

    /** {@inheritDoc} */
    public void sectionTitle2()
    {
        onSectionTitle( SECTION_LEVEL_2 );
    }

    /** {@inheritDoc} */
    public void sectionTitle2_()
    {
        onSectionTitle_( SECTION_LEVEL_2 );
    }

    /** {@inheritDoc} */
    public void section2_()
    {
        onSection_( SECTION_LEVEL_2 );
    }

    /** {@inheritDoc} */
    public void section3()
    {
        onSection( SECTION_LEVEL_3 );
    }

    /** {@inheritDoc} */
    public void sectionTitle3()
    {
        onSectionTitle( SECTION_LEVEL_3 );
    }

    /** {@inheritDoc} */
    public void sectionTitle3_()
    {
        onSectionTitle_( SECTION_LEVEL_3 );
    }

    /** {@inheritDoc} */
    public void section3_()
    {
        onSection_( SECTION_LEVEL_3 );
    }

    /** {@inheritDoc} */
    public void section4()
    {
        onSection( SECTION_LEVEL_4 );
    }

    /** {@inheritDoc} */
    public void sectionTitle4()
    {
        onSectionTitle( SECTION_LEVEL_4 );
    }

    /** {@inheritDoc} */
    public void sectionTitle4_()
    {
        onSectionTitle_( SECTION_LEVEL_4 );
    }

    /** {@inheritDoc} */
    public void section4_()
    {
        onSection_( SECTION_LEVEL_4 );
    }

    /** {@inheritDoc} */
    public void section5()
    {
        onSection( SECTION_LEVEL_5 );
    }

    /** {@inheritDoc} */
    public void sectionTitle5()
    {
        onSectionTitle( SECTION_LEVEL_5 );
    }

    /** {@inheritDoc} */
    public void sectionTitle5_()
    {
        onSectionTitle_( SECTION_LEVEL_5 );
    }

    /** {@inheritDoc} */
    public void section5_()
    {
        onSection_( SECTION_LEVEL_5 );
    }

    /**
     * Starts a section. The default class style is <code>section</code>.
     *
     * @param depth The level of the section.
     * @see javax.swing.text.html.HTML.Tag#DIV
     */
    private void onSection( int depth )
    {
        if ( depth >= SECTION_LEVEL_1 && depth <= SECTION_LEVEL_5 )
        {
            MutableAttributeSet att = new SimpleAttributeSet();
            att.addAttribute( Attribute.CLASS, "section" );

            writeStartTag( Tag.DIV, att );
        }
    }

    /**
     * Ends a section.
     *
     * @param depth The level of the section.
     * @see javax.swing.text.html.HTML.Tag#DIV
     */
    private void onSection_( int depth )
    {
        if ( depth >= SECTION_LEVEL_1 && depth <= SECTION_LEVEL_5 )
        {
            writeEndTag( Tag.DIV );
        }
    }

    /**
     * Starts a section title.
     *
     * @param depth The level of the section title.
     * @see javax.swing.text.html.HTML.Tag#H2
     * @see javax.swing.text.html.HTML.Tag#H3
     * @see javax.swing.text.html.HTML.Tag#H4
     * @see javax.swing.text.html.HTML.Tag#H5
     * @see javax.swing.text.html.HTML.Tag#H6
     */
    private void onSectionTitle( int depth )
    {
        if ( depth == SECTION_LEVEL_1 )
        {
            writeStartTag( Tag.H2 );
        }
        else if ( depth == SECTION_LEVEL_2 )
        {
            writeStartTag( Tag.H3 );
        }
        else if ( depth == SECTION_LEVEL_3 )
        {
            writeStartTag( Tag.H4 );
        }
        else if ( depth == SECTION_LEVEL_4 )
        {
            writeStartTag( Tag.H5 );
        }
        else if ( depth == SECTION_LEVEL_5 )
        {
            writeStartTag( Tag.H6 );
        }
    }

    /**
     * Ends a section title.
     *
     * @param depth The level of the section title.
     * @see javax.swing.text.html.HTML.Tag#H2
     * @see javax.swing.text.html.HTML.Tag#H3
     * @see javax.swing.text.html.HTML.Tag#H4
     * @see javax.swing.text.html.HTML.Tag#H5
     * @see javax.swing.text.html.HTML.Tag#H6
     */
    private void onSectionTitle_( int depth )
    {
        if ( depth == SECTION_LEVEL_1 )
        {
            writeEndTag( Tag.H2 );
        }
        else if ( depth == SECTION_LEVEL_2 )
        {
            writeEndTag( Tag.H3 );
        }
        else if ( depth == SECTION_LEVEL_3 )
        {
            writeEndTag( Tag.H4 );
        }
        else if ( depth == SECTION_LEVEL_4 )
        {
            writeEndTag( Tag.H5 );
        }
        else if ( depth == SECTION_LEVEL_5 )
        {
            writeEndTag( Tag.H6 );
        }
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#UL
     */
    public void list()
    {
        writeStartTag( Tag.UL );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#UL
     */
    public void list_()
    {
        writeEndTag( Tag.UL );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#LI
     */
    public void listItem()
    {
        writeStartTag( Tag.LI );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#LI
     */
    public void listItem_()
    {
        writeEndTag( Tag.LI );
    }

    /**
     * The default list style depends on the numbering.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#OL
     */
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

        MutableAttributeSet att = new SimpleAttributeSet();
        att.addAttribute( Attribute.STYLE, "list-style-type: " + style );

        writeStartTag( Tag.OL, att );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#OL
     */
    public void numberedList_()
    {
        writeEndTag( Tag.OL );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#LI
     */
    public void numberedListItem()
    {
        writeStartTag( Tag.LI );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#LI
     */
    public void numberedListItem_()
    {
        writeEndTag( Tag.LI );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DL
     */
    public void definitionList()
    {
        writeStartTag( Tag.DL );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DL
     */
    public void definitionList_()
    {
        writeEndTag( Tag.DL );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DT
     */
    public void definedTerm()
    {
        writeStartTag( Tag.DT );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DT
     */
    public void definedTerm_()
    {
        writeEndTag( Tag.DT );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DD
     */
    public void definition()
    {
        writeStartTag( Tag.DD );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DD
     */
    public void definition_()
    {
        writeEndTag( Tag.DD );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#IMG
     */
    public void figure()
    {
        write( String.valueOf( LESS_THAN ) + Tag.IMG );
    }

    /** {@inheritDoc} */
    public void figure_()
    {
        write( String.valueOf( SPACE ) + String.valueOf( SLASH ) + String.valueOf( GREATER_THAN ) );
    }

    /** {@inheritDoc} */
    public void figureGraphics( String name )
    {
        write( String.valueOf( SPACE ) + Attribute.SRC + String.valueOf( EQUAL ) + String.valueOf( QUOTE ) + name
            + String.valueOf( QUOTE ) );
    }

    /** {@inheritDoc} */
    public void figureCaption()
    {
        write( String.valueOf( SPACE ) + Attribute.ALT + String.valueOf( EQUAL ) + String.valueOf( QUOTE ) );
    }

    /** {@inheritDoc} */
    public void figureCaption_()
    {
        write( String.valueOf( QUOTE ) );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#P
     */
    public void paragraph()
    {
        writeStartTag( Tag.P );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#P
     */
    public void paragraph_()
    {
        writeEndTag( Tag.P );
    }

    /**
     * The default class style is <code>source</code>.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DIV
     * @see javax.swing.text.html.HTML.Tag#PRE
     */
    public void verbatim( boolean boxed )
    {
        verbatimFlag = true;

        MutableAttributeSet att = new SimpleAttributeSet();

        if ( boxed )
        {
            att.addAttribute( Attribute.CLASS, "source" );
        }

        writeStartTag( Tag.DIV, att );
        writeStartTag( Tag.PRE );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DIV
     * @see javax.swing.text.html.HTML.Tag#PRE
     */
    public void verbatim_()
    {
        writeEndTag( Tag.PRE );
        writeEndTag( Tag.DIV );

        verbatimFlag = false;

    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#HR
     */
    public void horizontalRule()
    {
        writeSimpleTag( Tag.HR );
    }

    /**
     * The default class style is <code>bodyTable</code>.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TABLE
     */
    public void table()
    {
        MutableAttributeSet att = new SimpleAttributeSet();
        att.addAttribute( Attribute.CLASS, "bodyTable" );

        writeStartTag( Tag.TABLE, att );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TABLE
     */
    public void table_()
    {
        writeEndTag( Tag.TABLE );
    }

    /**
     * {@inheritDoc}
     * @see XhtmlMarkup#TBODY_TAG
     */
    public void tableRows( int[] justification, boolean grid )
    {
        writeStartTag( TBODY_TAG );

        cellJustif = justification;
    }

    /**
     * {@inheritDoc}
     * @see XhtmlMarkup#TBODY_TAG
     */
    public void tableRows_()
    {
        writeEndTag( TBODY_TAG );

        cellJustif = null;
    }

    /**
     * The default class style is <code>a</code> or <code>b</code> depending the row id.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TR
     */
    public void tableRow()
    {
        MutableAttributeSet att = new SimpleAttributeSet();

        if ( evenTableRow )
        {
            att.addAttribute( Attribute.CLASS, "a" );
        }
        else
        {
            att.addAttribute( Attribute.CLASS, "b" );
        }

        writeStartTag( Tag.TR, att );

        evenTableRow = !evenTableRow;

        cellCount = 0;
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TR
     */
    public void tableRow_()
    {
        writeEndTag( Tag.TR );

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
     * @param headerRow true if it is an header row
     * @see javax.swing.text.html.HTML.Tag#TH
     * @see javax.swing.text.html.HTML.Tag#TD
     */
    public void tableCell( boolean headerRow )
    {
        tableCell( headerRow, null );
    }

    /** {@inheritDoc} */
    public void tableCell( String width )
    {
        tableCell( false, width );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell( String width )
    {
        tableCell( true, width );
    }

    /**
     * @param headerRow true if it is an header row
     * @param width the cell size
     * @see javax.swing.text.html.HTML.Tag#TH
     * @see javax.swing.text.html.HTML.Tag#TD
     */
    public void tableCell( boolean headerRow, String width )
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


        Tag t = ( headerRow ? Tag.TH : Tag.TD );

        MutableAttributeSet att = new SimpleAttributeSet();

        if ( width != null )
        {
            att.addAttribute( Attribute.WIDTH, width );
        }

        if ( justif != null )
        {
            att.addAttribute( Attribute.ALIGN, justif );
        }

        writeStartTag( t, att );
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
     * @param headerRow true if it is an header row
     * @see javax.swing.text.html.HTML.Tag#TH
     * @see javax.swing.text.html.HTML.Tag#TD
     */
    public void tableCell_( boolean headerRow )
    {
        Tag t = ( headerRow ? Tag.TH : Tag.TD );

        writeEndTag( t );

        ++cellCount;
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#CAPTION
     */
    public void tableCaption()
    {
        // TODO: tableCaption should be written before tableRows
        writeStartTag( Tag.CAPTION );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#CAPTION
     */
    public void tableCaption_()
    {
        writeEndTag( Tag.CAPTION );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#A
     */
    public void anchor( String name )
    {
        if ( !headFlag )
        {
            String id = HtmlTools.encodeId( name );

            MutableAttributeSet att = new SimpleAttributeSet();

            if ( id != null )
            {
                att.addAttribute( Attribute.NAME, id );
            }

            writeStartTag( Tag.A, att );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#A
     */
    public void anchor_()
    {
        if ( !headFlag )
        {
            writeEndTag( Tag.A );
        }
    }

    /** {@inheritDoc} */
    public void link( String name )
    {
        link( name, null );
    }

    /**
     * The default style class for external link is <code>externalLink</code>.
     *
     * @param name
     * @param target
     * @see javax.swing.text.html.HTML.Tag#A
     */
    public void link( String name, String target )
    {
        if ( headFlag )
        {
            return;
        }

        MutableAttributeSet att = new SimpleAttributeSet();

        if ( target != null )
        {
            att.addAttribute( Attribute.TARGET, target );
        }

        if ( StructureSink.isExternalLink( name ) || isExternalHtml( name ) )
        {
            if ( isExternalLink( name ) )
            {
                att.addAttribute( Attribute.CLASS, "externalLink" );
            }

            att.addAttribute( Attribute.HREF, HtmlTools.escapeHTML( name ) );
        }
        else
        {
            att.addAttribute( Attribute.HREF, "#" + HtmlTools.escapeHTML( name ) );
        }

        writeStartTag( Tag.A, att );
    }

    /**
     * {@link StructureSink#isExternalLink(String)} also treats links to other documents as
     * external links, those should not have a class="externalLink" attribute.
     */
    private boolean isExternalLink( String href )
    {
        String text = href.toLowerCase();
        return ( text.indexOf( "http:/" ) == 0 || text.indexOf( "https:/" ) == 0
            || text.indexOf( "ftp:/" ) == 0 || text.indexOf( "mailto:" ) == 0
            || text.indexOf( "file:/" ) == 0 );

    }

    /**
     * Legacy: treat links to other html documents as external links.
     * Note that links to other file formats (images, pdf) will still be broken,
     * links to other documents should always start with "./" or "../".
     */
    private boolean isExternalHtml( String href )
    {
        String text = href.toLowerCase();
        return ( text.indexOf( ".html#" ) != -1 || text.indexOf( ".htm#" ) != -1
            || text.endsWith( ".htm" ) || text.endsWith( ".html" )
            || !HtmlTools.isId( text ) );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#A
     */
    public void link_()
    {
        if ( !headFlag )
        {
            writeEndTag( Tag.A );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#I
     */
    public void italic()
    {
        if ( !headFlag )
        {
            writeStartTag( Tag.I );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#I
     */
    public void italic_()
    {
        if ( !headFlag )
        {
            writeEndTag( Tag.I );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#B
     */
    public void bold()
    {
        if ( !headFlag )
        {
            writeStartTag( Tag.B );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#B
     */
    public void bold_()
    {
        if ( !headFlag )
        {
            writeEndTag( Tag.B );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TT
     */
    public void monospaced()
    {
        if ( !headFlag )
        {
            writeStartTag( Tag.TT );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TT
     */
    public void monospaced_()
    {
        if ( !headFlag )
        {
            writeEndTag( Tag.TT );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#BR
     */
    public void lineBreak()
    {
        if ( headFlag )
        {
            buffer.append( EOL );
        }
        else
        {
            writeSimpleTag( Tag.BR );
        }
    }

    /** {@inheritDoc} */
    public void pageBreak()
    {
        comment( "PB" );
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
            write( "&#160;" );
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

    /** {@inheritDoc} */
    public void rawText( String text )
    {
        write( text );
    }

    /** {@inheritDoc} */
    public void comment( String comment )
    {
        StringBuffer buffer = new StringBuffer( comment.length() + 9 );

        buffer.append( LESS_THAN ).append( BANG ).append( MINUS ).append( MINUS ).append( SPACE );

        buffer.append( comment );

        buffer.append( SPACE ).append( MINUS ).append( MINUS ).append( GREATER_THAN );

        rawText( buffer.toString() );
    }

    /** {@inheritDoc} */
    public void flush()
    {
        writer.flush();
    }

    /** {@inheritDoc} */
    public void close()
    {
        writer.close();
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
        write( escapeHTML( text ) );
    }

    /**
     * Write HTML escaped text to output.
     *
     * @param text The text to write.
     */
    protected void verbatimContent( String text )
    {
        write( escapeHTML( text ) );
    }

    /**
     * Forward to HtmlTools.escapeHTML( text ).
     *
     * @param text the String to escape, may be null
     * @return the text escaped, "" if null String input
     * @see org.apache.maven.doxia.util.HtmlTools#escapeHTML(String)
     */
    public static String escapeHTML( String text )
    {
        return HtmlTools.escapeHTML( text );
    }

    /**
     * @param text
     * @return a fragment encoded
     * @see #encodeURL(String)
     */
    public static String encodeFragment( String text )
    {
        return encodeURL( StructureSink.linkToKey( text ) );
    }

    /**
     * Forward to HtmlTools.encodeURL( text ).
     *
     * @param text the String to encode, may be null.
     * @return the text encoded, null if null String input.
     * @see org.apache.maven.doxia.util.HtmlTools#encodeURL(String)
     */
    public static String encodeURL( String text )
    {
        return HtmlTools.encodeURL( text );
    }

    /** {@inheritDoc} */
    protected void write( String text )
    {
        // TODO: this doesn't belong here
        if ( renderingContext != null )
        {
            String relativePathToBasedir = renderingContext.getRelativePath();

            if ( relativePathToBasedir == null )
            {
                text = StringUtils.replace( text, "$relativePath", "." );
            }
            else
            {
                text = StringUtils.replace( text, "$relativePath", relativePathToBasedir );
            }
        }

        writer.write( text );
    }

    /**
     * @return the current rendering context
     */
    public RenderingContext getRenderingContext()
    {
        return renderingContext;
    }
}
