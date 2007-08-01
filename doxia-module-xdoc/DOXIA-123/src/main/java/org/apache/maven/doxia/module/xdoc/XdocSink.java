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
import java.util.Enumeration;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.sink.SinkAdapter;
import org.apache.maven.doxia.util.HtmlTools;
import org.apache.maven.doxia.util.LineBreaker;
import org.apache.maven.doxia.parser.Parser;

/**
 * A doxia Sink which produces an xdoc model.
 *
 * @author <a href="mailto:james@jamestaylor.org">James Taylor</a>
 * @version $Id$
 * @since 1.0
 */
public class XdocSink
    extends SinkAdapter
    implements XdocMarkup
{
    // ----------------------------------------------------------------------
    // Instance fields
    // ----------------------------------------------------------------------

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

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Constructor, initialize the LineBreaker.
     *
     * @param writer The writer to write the result.
     */
    public XdocSink( Writer writer )
    {
        this.out = new LineBreaker( writer );
    }

    // ----------------------------------------------------------------------
    // Public protected methods
    // ----------------------------------------------------------------------

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

    /**
     * {@inheritDoc}
     * @see XdocMarkup#DOCUMENT_TAG
     * @see XdocMarkup#PROPERTIES_TAG
     */
    public void head()
    {
        resetState();

        headFlag = true;

        markup( "<?xml version=\"1.0\" ?>" + EOL );

        writeStartTag( DOCUMENT_TAG );

        writeStartTag( PROPERTIES_TAG );
    }

    /**
     * {@inheritDoc}
     * @see XdocMarkup#DOCUMENT_TAG
     * @see XdocMarkup#PROPERTIES_TAG
     */
    public void head_()
    {
        headFlag = false;

        writeEndTag( PROPERTIES_TAG );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void title_()
    {
        if ( buffer.length() > 0 )
        {
            writeStartTag( Tag.TITLE );
            content( buffer.toString() );
            writeEndTag( Tag.TITLE );
            buffer = new StringBuffer();
        }
    }

    /**
     * {@inheritDoc}
     * @see XdocMarkup#AUTHOR_TAG
     */
    public void author_()
    {
        if ( buffer.length() > 0 )
        {
            writeStartTag( AUTHOR_TAG );
            content( buffer.toString() );
            writeEndTag( AUTHOR_TAG );
            buffer = new StringBuffer();
        }
    }

    /**
     * {@inheritDoc}
     * @see XdocMarkup#DATE_TAG
     */
    public void date_()
    {
        if ( buffer.length() > 0 )
        {
            writeStartTag( DATE_TAG );
            content( buffer.toString() );
            writeEndTag( DATE_TAG );
            buffer = new StringBuffer();
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
     * @see XdocMarkup#DOCUMENT_TAG
     */
    public void body_()
    {
        writeEndTag( Tag.BODY );

        writeEndTag( DOCUMENT_TAG );

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
     * @see XdocMarkup#SECTION_TAG
     * @see XdocMarkup#SUBSECTION_TAG
     */
    private void onSection( int depth )
    {
        if ( depth == 1 )
        {
            markup( START_MARKUP + SECTION_TAG.toString() + SPACE_MARKUP + Attribute.NAME + EQUAL_MARKUP
                + QUOTE_MARKUP );
        }
        else if ( depth == 2 )
        {
            markup( START_MARKUP + SUBSECTION_TAG.toString() + SPACE_MARKUP + Attribute.NAME + EQUAL_MARKUP
                + QUOTE_MARKUP );
        }
    }

    /**
     * Starts a section title.
     *
     * @param depth The level of the section title.
     * @see javax.swing.text.html.HTML.Tag#H4
     * @see javax.swing.text.html.HTML.Tag#H5
     * @see javax.swing.text.html.HTML.Tag#H6
     */
    private void onSectionTitle( int depth )
    {
        if ( depth == 3 )
        {
            writeStartTag( Tag.H4 );
        }
        else if ( depth == 4 )
        {
            writeStartTag( Tag.H5 );
        }
        else if ( depth == 5 )
        {
            writeStartTag( Tag.H6 );
        }

        titleFlag = true;
    }

    /**
     * Ends a section title.
     *
     * @param depth The level of the section title.
     * @see javax.swing.text.html.HTML.Tag#H4
     * @see javax.swing.text.html.HTML.Tag#H5
     * @see javax.swing.text.html.HTML.Tag#H6
     */
    private void onSectionTitle_( int depth )
    {
        if ( depth == 1 || depth == 2 )
        {
            markup( QUOTE_MARKUP + END_MARKUP );
        }
        else if ( depth == 3 )
        {
            writeEndTag( Tag.H4 );
        }
        else if ( depth == 4 )
        {
            writeEndTag( Tag.H5 );
        }
        else if ( depth == 5 )
        {
            writeEndTag( Tag.H6 );
        }

        titleFlag = false;
    }

    /**
     * Ends a section.
     *
     * @param depth The level of the section.
     * @see XdocMarkup#SECTION_TAG
     * @see XdocMarkup#SUBSECTION_TAG
     */
    private void onSection_( int depth )
    {
        if ( depth == 1 )
        {
            writeEndTag( SECTION_TAG );
        }
        else if ( depth == 2 )
        {
            writeEndTag( SUBSECTION_TAG );
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
        itemFlag = false;
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#LI
     */
    public void listItem()
    {
        writeStartTag( Tag.LI );
        itemFlag = true;
        // What follows is at least a paragraph.
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
        itemFlag = false;
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#LI
     */
    public void numberedListItem()
    {
        writeStartTag( Tag.LI );
        itemFlag = true;
        // What follows is at least a paragraph.
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
        itemFlag = false;
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
        itemFlag = true;
        // What follows is at least a paragraph.
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
        markup( START_MARKUP + Tag.IMG );
    }

    /** {@inheritDoc} */
    public void figure_()
    {
        markup( SPACE_MARKUP + SLASH_MARKUP + END_MARKUP );
    }

    /** {@inheritDoc} */
    public void figureGraphics( String s )
    {
        markup( SPACE_MARKUP + Attribute.SRC + EQUAL_MARKUP + QUOTE_MARKUP + s + QUOTE_MARKUP );
    }

    /** {@inheritDoc} */
    public void figureCaption()
    {
        markup( SPACE_MARKUP + Attribute.ALT + EQUAL_MARKUP + QUOTE_MARKUP );
    }

    /** {@inheritDoc} */
    public void figureCaption_()
    {
        markup( QUOTE_MARKUP );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#P
     */
    public void paragraph()
    {
        if ( !itemFlag )
        {
            writeStartTag( Tag.P );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#P
     */
    public void paragraph_()
    {
        if ( itemFlag )
        {
            itemFlag = false;
        }
        else
        {
            writeEndTag( Tag.P );
        }
    }

    /**
     * {@inheritDoc}
     * @see XdocMarkup#SOURCE_TAG
     * @see javax.swing.text.html.HTML.Tag#PRE
     */
    public void verbatim( boolean boxed )
    {
        verbatimFlag = true;
        boxedFlag = boxed;
        if ( boxed )
        {
            writeStartTag( SOURCE_TAG );
        }
        else
        {
            writeStartTag( Tag.PRE );
        }
    }

    /**
     * {@inheritDoc}
     * @see XdocMarkup#SOURCE_TAG
     * @see javax.swing.text.html.HTML.Tag#PRE
     */
    public void verbatim_()
    {
        if ( boxedFlag )
        {
            writeEndTag( SOURCE_TAG );
        }
        else
        {
            writeEndTag( Tag.PRE );
        }

        verbatimFlag = false;

        boxedFlag = false;
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
     * The default align is <code>center</code>.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TABLE
     */
    public void table()
    {
        MutableAttributeSet att = new SimpleAttributeSet();
        att.addAttribute( Attribute.ALIGN, "center" );

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
     * The default align is <code>center</code>.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TABLE
     */
    public void tableRows( int[] justification, boolean grid )
    {
        MutableAttributeSet att = new SimpleAttributeSet();
        att.addAttribute( Attribute.ALIGN, "center" );
        att.addAttribute( Attribute.BORDER, ( grid ? "1" : "0" ) );

        writeStartTag( Tag.TABLE, att );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TABLE
     */
    public void tableRows_()
    {
        writeEndTag( Tag.TABLE );
    }

    /**
     * The default valign is <code>top</code>.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TR
     */
    public void tableRow()
    {
        MutableAttributeSet att = new SimpleAttributeSet();
        att.addAttribute( Attribute.VALIGN, "top" );

        writeStartTag( Tag.TR, att );
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

        MutableAttributeSet att = null;

        if ( justif != null )
        {
            att = new SimpleAttributeSet();
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
     * @see javax.swing.text.html.HTML.Tag#P
     * @see javax.swing.text.html.HTML.Tag#I
     */
    public void tableCaption()
    {
        writeStartTag( Tag.P );
        writeStartTag( Tag.I );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#P
     * @see javax.swing.text.html.HTML.Tag#I
     */
    public void tableCaption_()
    {
        writeEndTag( Tag.I );
        writeEndTag( Tag.P );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#A
     */
    public void anchor( String name )
    {
        if ( !headFlag && !titleFlag )
        {
            String id = HtmlTools.encodeId( name );

            MutableAttributeSet att = new SimpleAttributeSet();
            att.addAttribute( Attribute.ID, id );
            att.addAttribute( Attribute.NAME, id );

            writeStartTag( Tag.A, att );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#A
     */
    public void anchor_()
    {
        if ( !headFlag && !titleFlag )
        {
            writeEndTag( Tag.A );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#A
     */
    public void link( String name )
    {
        if ( !headFlag && !titleFlag )
        {
            MutableAttributeSet att = new SimpleAttributeSet();
            att.addAttribute( Attribute.HREF, name );

            writeStartTag( Tag.A, att );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#A
     */
    public void link_()
    {
        if ( !headFlag && !titleFlag )
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
        if ( !headFlag && !titleFlag )
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
        if ( !headFlag && !titleFlag )
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
        if ( !headFlag && !titleFlag )
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
        if ( !headFlag && !titleFlag )
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
        if ( !headFlag && !titleFlag )
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
        if ( !headFlag && !titleFlag )
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
        if ( headFlag || titleFlag )
        {
            buffer.append( EOL );
        }
        else
        {
            writeSimpleTag( Tag.BR );
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
     * @see org.apache.maven.doxia.util.HtmlTools#escapeHTML(String)
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
     * @see org.apache.maven.doxia.util.HtmlTools#encodeURL(String)
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

    // ----------------------------------------------------------------------
    // TODO Move these in core utils
    // ----------------------------------------------------------------------

    /**
     * Starts a Tag, for instance:
     * <pre>
     * &lt;tag&gt;
     * </pre>
     *
     * @param t a non null tag
     * @see #writeStartTag(Tag, MutableAttributeSet)
     */
    private void writeStartTag ( Tag t )
    {
        writeStartTag ( t, null );
    }

    /**
     * Starts a Tag with attributes, for instance:
     * <pre>
     * &lt;tag attName="attValue"&gt;
     * </pre>
     *
     * @param t a non null tag
     * @param att a set of attributes
     * @see #writeStartTag(Tag, MutableAttributeSet, boolean)
     */
    private void writeStartTag ( Tag t, MutableAttributeSet att )
    {
        writeStartTag ( t, att, false );
    }

    /**
     * Starts a Tag with attributes, for instance:
     * <pre>
     * &lt;tag attName="attValue"&gt;
     * </pre>
     *
     * @param t a non null tag
     * @param att a set of attributes
     * @param isSimpleTag boolean to write as a simple tag
     */
    private void writeStartTag( Tag t, MutableAttributeSet att, boolean isSimpleTag )
    {
        if ( t == null )
        {
            throw new IllegalArgumentException( "A tag is required" );
        }

        StringBuffer sb = new StringBuffer();
        sb.append( START_MARKUP );
        sb.append( t.toString() );

        if ( att != null )
        {
            Enumeration names = att.getAttributeNames();

            while ( names.hasMoreElements() )
            {
                Object key = names.nextElement();
                Object value = att.getAttribute( key );

                if ( value instanceof AttributeSet )
                {
                    // ignored
                }
                else
                {
                    sb.append( SPACE_MARKUP ).append( key.toString() ).append( EQUAL_MARKUP ).append( QUOTE_MARKUP )
                        .append( value.toString() ).append( QUOTE_MARKUP );
                }
            }
        }

        if ( isSimpleTag )
        {
            sb.append( SPACE_MARKUP ).append( SLASH_MARKUP );
        }

        sb.append( END_MARKUP );

        if ( isSimpleTag )
        {
            sb.append( EOL );
        }

        markup( sb.toString() );
    }

    /**
     * Ends a Tag, for instance:
     * <pre>
     * &lt;/tag&gt;
     * </pre>
     *
     * @param t a tag
     */
    private void writeEndTag( Tag t )
    {
        StringBuffer sb = new StringBuffer();
        sb.append( START_MARKUP );
        sb.append( SLASH_MARKUP );
        sb.append( t.toString() );
        sb.append( END_MARKUP );

        sb.append( EOL );

        markup( sb.toString() );
    }

    /**
     * Starts a simple Tag, for instance:
     * <pre>
     * &lt;tag /&gt;
     * </pre>
     *
     * @param t a non null tag
     * @see #writeSimpleTag(Tag, MutableAttributeSet)
     */
    private void writeSimpleTag( Tag t )
    {
        writeSimpleTag( t, null );
    }

    /**
     * Starts a simple Tag with attributes, for instance:
     * <pre>
     * &lt;tag attName="attValue" /&gt;
     * </pre>
     *
     * @param t a non null tag
     * @param att a set of attributes
     * @see #writeStartTag(Tag, MutableAttributeSet, boolean)
     */
    private void writeSimpleTag ( Tag t, MutableAttributeSet att )
    {
        writeStartTag ( t, att, true );
    }
}
