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

import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.impl.SinkUtils;
import org.apache.maven.doxia.sink.impl.XhtmlBaseSink;
import org.apache.maven.doxia.util.HtmlTools;

import org.codehaus.plexus.util.StringUtils;

/**
 * <a href="http://maven.apache.org/doxia/references/xdoc-format.html">Xdoc</a> Sink implementation.
 * <br>
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

    private String languageId;

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
     * @since 1.1
     */
    protected XdocSink( Writer writer, String encoding )
    {
        this( writer );
        this.encoding = encoding;
    }

    /**
     * Constructor, initialize the Writer and tells which encoding and languageId are used.
     *
     * @param writer not null writer to write the result.
     * @param encoding the encoding used, that should be written to the generated HTML content
     * if not <code>null</code>.
     * @param languageId language identifier for the root element as defined by
     * <a href="ftp://ftp.isi.edu/in-notes/bcp/bcp47.txt">IETF BCP 47</a>, Tags for the Identification of Languages;
     * in addition, the empty string may be specified.
     * @since 1.1
     */
    protected XdocSink( Writer writer, String encoding, String languageId )
    {
        this( writer, encoding );

        this.languageId = languageId;
    }

    // ----------------------------------------------------------------------
    // Public protected methods
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    protected void init()
    {
        super.init();

        boxedFlag = false;
    }

    /**
     * {@inheritDoc}
     * @see #head(org.apache.maven.doxia.sink.SinkEventAttributes)
     */
    public void head()
    {
        head( null );
    }

    /**
     * {@inheritDoc}
     * @see XdocMarkup#DOCUMENT_TAG
     * @see XdocMarkup#PROPERTIES_TAG
     */
    public void head( SinkEventAttributes attributes )
    {
        init();

        setHeadFlag( true );

        write( "<?xml version=\"1.0\"" );
        if ( encoding != null )
        {
            write( " encoding=\"" + encoding + "\"" );
        }
        write( "?>" );

        MutableAttributeSet atts = new SinkEventAttributeSet();
        atts.addAttribute( "xmlns", XDOC_NAMESPACE );
        atts.addAttribute( "xmlns:xsi", XML_NAMESPACE );
        atts.addAttribute( "xsi:schemaLocation", XDOC_NAMESPACE + " " + XDOC_SYSTEM_ID );

        if ( languageId != null )
        {
            atts.addAttribute( Attribute.LANG.toString(), languageId );
            atts.addAttribute( "xml:lang", languageId );
        }

        if ( attributes != null )
        {
            atts.addAttributes( attributes );
        }

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
        writeStartTag( TITLE );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void title_()
    {
        content( getTextBuffer().toString() );

        writeEndTag( TITLE );

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
            String text = HtmlTools.escapeHTML( getTextBuffer().toString() );
            // hack: un-escape numerical entities that have been escaped above
            // note that numerical entities should really be written as one unicode character in the first place
            text = StringUtils.replace( text, "&amp;#", "&#" );
            write( text );
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
     * @see #body(org.apache.maven.doxia.sink.SinkEventAttributes)
     */
    public void body()
    {
       body( null );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#BODY
     */
    public void body( SinkEventAttributes attributes )
    {
        writeStartTag( BODY, attributes );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#BODY
     * @see XdocMarkup#DOCUMENT_TAG
     */
    public void body_()
    {
        writeEndTag( BODY );

        writeEndTag( DOCUMENT_TAG );

        flush();

        init();
    }

    // ----------------------------------------------------------------------
    // Sections
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * Starts a section.
     * @see XdocMarkup#SECTION_TAG
     * @see XdocMarkup#SUBSECTION_TAG
     */
    protected void onSection( int depth, SinkEventAttributes attributes )
    {
        if ( depth == SECTION_LEVEL_1 )
        {
            write( LESS_THAN + SECTION_TAG.toString()
                    + SinkUtils.getAttributeString(
                        SinkUtils.filterAttributes( attributes, SinkUtils.SINK_BASE_ATTRIBUTES ) )
                    + SPACE + Attribute.NAME + EQUAL + QUOTE );
        }
        else if ( depth == SECTION_LEVEL_2 )
        {
            write( LESS_THAN + SUBSECTION_TAG.toString()
                    + SinkUtils.getAttributeString(
                        SinkUtils.filterAttributes( attributes, SinkUtils.SINK_BASE_ATTRIBUTES  ) )
                    + SPACE + Attribute.NAME + EQUAL + QUOTE );
        }
    }

    /**
     * {@inheritDoc}
     *
     * Ends a section.
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
     * {@inheritDoc}
     *
     * Starts a section title.
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
            writeStartTag( H4, atts );
        }
        else if ( depth == SECTION_LEVEL_4 )
        {
            writeStartTag( H5, atts );
        }
        else if ( depth == SECTION_LEVEL_5 )
        {
            writeStartTag( H6, atts );
        }
    }

    /**
     * {@inheritDoc}
     *
     * Ends a section title.
     * @see javax.swing.text.html.HTML.Tag#H4
     * @see javax.swing.text.html.HTML.Tag#H5
     * @see javax.swing.text.html.HTML.Tag#H6
     */
    protected void onSectionTitle_( int depth )
    {
        if ( depth == SECTION_LEVEL_1 || depth == SECTION_LEVEL_2 )
        {
            write( String.valueOf( QUOTE ) + GREATER_THAN );
        }
        else if ( depth == SECTION_LEVEL_3 )
        {
            writeEndTag( H4 );
        }
        else if ( depth == SECTION_LEVEL_4 )
        {
            writeEndTag( H5 );
        }
        else if ( depth == SECTION_LEVEL_5 )
        {
            writeEndTag( H6 );
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
            boxed = "boxed".equals( atts.getAttribute( SinkEventAttributes.DECORATION ) );
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
            writeStartTag( PRE, atts );
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
            writeEndTag( PRE );
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
        // similar to super.tableRows( justification, grid ) but without class.

        this.tableRows = true;

        setCellJustif( justification );

        if ( this.tableAttributes == null )
        {
            this.tableAttributes = new SinkEventAttributeSet( 0 );
        }

        MutableAttributeSet att = new SinkEventAttributeSet();

        if ( !tableAttributes.isDefined( Attribute.BORDER.toString() ) )
        {
            att.addAttribute( Attribute.BORDER, ( grid ? "1" : "0" ) );
        }

        att.addAttributes( tableAttributes );

        tableAttributes.removeAttributes( tableAttributes );

        writeStartTag( TABLE, att );
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

        writeStartTag( TR, att );

        setCellCount( 0 );
    }

    public void close()
    {
        super.close();

        init();
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

        att.addAttribute( Attribute.HREF, HtmlTools.escapeHTML( name ) );

        if ( target != null )
        {
            att.addAttribute( Attribute.TARGET, target );
        }

        writeStartTag( A, att );
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
