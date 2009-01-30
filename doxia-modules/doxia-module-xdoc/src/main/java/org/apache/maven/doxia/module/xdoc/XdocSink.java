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

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.sink.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.SinkUtils;
import org.apache.maven.doxia.sink.XhtmlBaseSink;
import org.apache.maven.doxia.util.HtmlTools;

/**
 * <a href="http://maven.apache.org/doxia/references/xdoc-format.html">Xdoc</a> Sink implementation.
 * <br/>
 * It uses the Xdoc XSD <a href="http://maven.apache.org/xsd/xdoc-2.0.xsd">
 * http://maven.apache.org/xsd/xdoc-2.0.xsd</a>.
 *
 * @author <a href="mailto:james@jamestaylor.org">James Taylor</a>
 * @version $Id$
 * @since 1.0
 */
public class XdocSink
    extends XhtmlBaseSink
    implements XdocMarkup
{
    // ----------------------------------------------------------------------
    // Instance fields
    // ----------------------------------------------------------------------

    /** An indication on if we're inside a box (verbatim). */
    private boolean boxedFlag;

    private String encoding;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    /**
     * Constructor, initialize the Writer.
     *
     * @param writer not null writer to write the result. <b>Should</b> be an UTF-8 Writer.
     * You could use <code>newXmlWriter</code> methods from {@link org.codehaus.plexus.util.WriterFactory}.
     */
    protected XdocSink( Writer writer )
    {
        super( writer );
    }

    /**
     * Constructor, initialize the Writer and tells which encoding is used.
     *
     * @param writer not null writer to write the result.
     * @param encoding the encoding used, that should be written to the generated HTML content
     * if not <code>null</code>.
     */
    protected XdocSink( Writer writer, String encoding )
    {
        this( writer );
        this.encoding = encoding;
    }

    // ----------------------------------------------------------------------
    // Public protected methods
    // ----------------------------------------------------------------------

    /**
     * Reset all variables.
     */
    protected void resetState()
    {
        super.resetState();
        boxedFlag = false;
    }

    /**
     * {@inheritDoc}
     * @see XdocMarkup#DOCUMENT_TAG
     * @see XdocMarkup#PROPERTIES_TAG
     */
    public void head()
    {
        resetState();

        setHeadFlag( true );

        write( "<?xml version=\"1.0\"" );
        if ( encoding != null )
        {
            write( " encoding=\"" + encoding + "\"" );
        }
        write( "?>" );

        MutableAttributeSet atts = new SinkEventAttributeSet();
        atts.addAttribute( "xmlns", "http://maven.apache.org/XDOC/2.0" );
        atts.addAttribute( "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance" );
        atts.addAttribute( "xsi:schemaLocation", "http://maven.apache.org/XDOC/2.0 http://maven.apache.org/xsd/xdoc-2.0.xsd" );

        writeStartTag( DOCUMENT_TAG, atts );

        writeStartTag( PROPERTIES_TAG );
    }

    /**
     * {@inheritDoc}
     * @see XdocMarkup#DOCUMENT_TAG
     * @see XdocMarkup#PROPERTIES_TAG
     */
    public void head_()
    {
        setHeadFlag( false );

        writeEndTag( PROPERTIES_TAG );
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
        content( getTextBuffer().toString() );

        writeEndTag( Tag.TITLE );

        resetTextBuffer();
    }

    /**
     * {@inheritDoc}
     * @see XdocMarkup#AUTHOR_TAG
     */
    public void author_()
    {
        if ( getTextBuffer().length() > 0 )
        {
            writeStartTag( AUTHOR_TAG );
            content( getTextBuffer().toString() );
            writeEndTag( AUTHOR_TAG );
            resetTextBuffer();
        }
    }

    /**
     * {@inheritDoc}
     * @see XdocMarkup#DATE_TAG
     */
    public void date_()
    {
        if ( getTextBuffer().length() > 0 )
        {
            writeStartTag( DATE_TAG );
            content( getTextBuffer().toString() );
            writeEndTag( DATE_TAG );
            resetTextBuffer();
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

        flush();

        resetState();
    }

    // ----------------------------------------------------------------------
    // Sections
    // ----------------------------------------------------------------------

    /**
     * Starts a section.
     *
     * @param depth The level of the section.
     * @see XdocMarkup#SECTION_TAG
     * @see XdocMarkup#SUBSECTION_TAG
     */
    protected void onSection( int depth, SinkEventAttributes attributes )
    {
        if ( depth == SECTION_LEVEL_1 )
        {
            write( String.valueOf( LESS_THAN ) + SECTION_TAG.toString() + String.valueOf( SPACE ) + Attribute.NAME
                + String.valueOf( EQUAL ) + String.valueOf( QUOTE )
                + SinkUtils.getAttributeString( SinkUtils.filterAttributes(
                    attributes, SinkUtils.SINK_BASE_ATTRIBUTES  ) ) );
        }
        else if ( depth == SECTION_LEVEL_2 )
        {
            write( String.valueOf( LESS_THAN ) + SUBSECTION_TAG.toString() + String.valueOf( SPACE ) + Attribute.NAME
                + String.valueOf( EQUAL ) + String.valueOf( QUOTE )
                + SinkUtils.getAttributeString( SinkUtils.filterAttributes(
                    attributes, SinkUtils.SINK_BASE_ATTRIBUTES  ) ) );
        }
    }

    /**
     * Ends a section.
     *
     * @param depth The level of the section.
     * @see XdocMarkup#SECTION_TAG
     * @see XdocMarkup#SUBSECTION_TAG
     */
    protected void onSection_( int depth )
    {
        if ( depth == SECTION_LEVEL_1 )
        {
            writeEndTag( SECTION_TAG );
        }
        else if ( depth == SECTION_LEVEL_2 )
        {
            writeEndTag( SUBSECTION_TAG );
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
    protected void onSectionTitle( int depth, SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_SECTION_ATTRIBUTES  );

        if ( depth == SECTION_LEVEL_3 )
        {
            writeStartTag( Tag.H4, atts );
        }
        else if ( depth == SECTION_LEVEL_4 )
        {
            writeStartTag( Tag.H5, atts );
        }
        else if ( depth == SECTION_LEVEL_5 )
        {
            writeStartTag( Tag.H6, atts );
        }
    }

    /**
     * Ends a section title.
     *
     * @param depth The level of the section title.
     * @see javax.swing.text.html.HTML.Tag#H4
     * @see javax.swing.text.html.HTML.Tag#H5
     * @see javax.swing.text.html.HTML.Tag#H6
     */
    protected void onSectionTitle_( int depth )
    {
        if ( depth == SECTION_LEVEL_1 || depth == SECTION_LEVEL_2 )
        {
            write( String.valueOf( QUOTE ) + String.valueOf( GREATER_THAN ) );
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
     * @see XdocMarkup#SOURCE_TAG
     * @see javax.swing.text.html.HTML.Tag#PRE
     */
    public void verbatim( SinkEventAttributes attributes )
    {
        setVerbatimFlag( true );

        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_VERBATIM_ATTRIBUTES  );


        if ( atts == null )
        {
            atts = new SinkEventAttributeSet();
        }

        boolean boxed = false;

        if ( atts.isDefined( SinkEventAttributes.DECORATION ) )
        {
            boxed = "boxed".equals(
                (String) atts.getAttribute( SinkEventAttributes.DECORATION ) );
        }

        boxedFlag = boxed;
        atts.removeAttribute( SinkEventAttributes.DECORATION );

        if ( boxed )
        {
            writeStartTag( SOURCE_TAG, atts );
        }
        else
        {
            atts.removeAttribute( Attribute.ALIGN.toString() );
            writeStartTag( Tag.PRE, atts );
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

        setVerbatimFlag( false );

        boxedFlag = false;
    }

    /**
     * The default align is <code>center</code>.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TABLE
     */
    public void tableRows( int[] justification, boolean grid )
    {
        setCellJustif( justification );

        MutableAttributeSet att = new SinkEventAttributeSet();
        att.addAttribute( Attribute.ALIGN, "center" );
        att.addAttribute( Attribute.BORDER, ( grid ? "1" : "0" ) );

        writeStartTag( Tag.TABLE, att );
    }

    /**
     * The default valign is <code>top</code>.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TR
     */
    public void tableRow()
    {
        MutableAttributeSet att = new SinkEventAttributeSet();
        att.addAttribute( Attribute.VALIGN, "top" );

        writeStartTag( Tag.TR, att );

        setCellCount( 0 );
    }

    /**
     * Adds a link with an optional target.
     *
     * @param name the link name.
     * @param target the link target, may be null.
     */
    public void link( String name, String target )
    {
        if ( isHeadFlag() )
        {
            return;
        }

        MutableAttributeSet att = new SinkEventAttributeSet();

        if ( target != null )
        {
            att.addAttribute( Attribute.TARGET, target );
        }

        att.addAttribute( Attribute.HREF, HtmlTools.escapeHTML( name ) );

        writeStartTag( Tag.A, att );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Write text to output, preserving white space.
     *
     * @param text The text to write.
     * @deprecated use write(String)
     */
    protected void markup( String text )
    {
        write( text );
    }
}
