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

import java.io.Writer;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.sink.XhtmlBaseSink;
import org.apache.maven.doxia.sink.SinkEventAttributeSet;

/**
 * Xhtml sink implementation.
 *
 * @author Jason van Zyl
 * @author ltheussl
 * @version $Id$
 * @since 1.0
 */
public class XhtmlSink
    extends XhtmlBaseSink
    implements XhtmlMarkup
{
    /** XHTML 1.0 public id: "-//W3C//DTD XHTML 1.0 Transitional//EN" */
    public static final String XHTML_PUBLIC_ID = "-//W3C//DTD XHTML 1.0 Transitional//EN";

    /** XHTML 1.0 system id: "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" */
    public static final String XHTML_SYSTEM_ID = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd";

    /** XHTML xmlns: "http://www.w3.org/1999/xhtml" */
    public static final String XHTML_XMLLNS = "http://www.w3.org/1999/xhtml";

    // ----------------------------------------------------------------------
    // Instance fields
    // ----------------------------------------------------------------------

    private String encoding;

    /** An indication on if we're inside a head title. */
    private boolean headTitleFlag;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    /**
     * Constructor, initialize the Writer.
     *
     * @param writer not null writer to write the result.
     */
    protected XhtmlSink( Writer writer )
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
    protected XhtmlSink( Writer writer, String encoding )
    {
        super( writer );

        this.encoding = encoding;
    }

    /** {@inheritDoc} */
    public void head()
    {
        resetState();

        setHeadFlag( true );

        write( "<!DOCTYPE html PUBLIC \"" + XHTML_PUBLIC_ID + "\" \"" + XHTML_SYSTEM_ID + "\">" );
        write( "<html xmlns=\"" + XHTML_XMLLNS + "\">" );

        writeStartTag( Tag.HEAD );
    }

    /** {@inheritDoc} */
    public void head_()
    {
        if ( !isHeadTitleFlag() )
        {
            // The content of element type "head" must match
            // "((script|style|meta|link|object|isindex)*,
            //  ((title,(script|style|meta|link|object|isindex)*,
            //  (base,(script|style|meta|link|object|isindex)*)?)|(base,(script|style|meta|link|object|isindex)*,
            //  (title,(script|style|meta|link|object|isindex)*))))"
            writeStartTag( Tag.TITLE );
            writeEndTag( Tag.TITLE );
        }

        setHeadFlag( false );
        setHeadTitleFlag( false );

        if ( encoding != null )
        {
            write( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + encoding + "\"/>" );
        }

        writeEndTag( Tag.HEAD );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void title()
    {
        setHeadTitleFlag( true );

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
     * @see javax.swing.text.html.HTML.Tag#META
     */
    public void author_()
    {
        if ( getTextBuffer().length() > 0 )
        {
            MutableAttributeSet att = new SinkEventAttributeSet();
            att.addAttribute( Attribute.NAME, "author" );
            att.addAttribute( Attribute.CONTENT, getTextBuffer().toString() );

            writeSimpleTag( Tag.META, att );

            resetTextBuffer();
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#META
     */
    public void date_()
    {
        if ( getTextBuffer().length() > 0 )
        {
            MutableAttributeSet att = new SinkEventAttributeSet();
            att.addAttribute( Attribute.NAME, "date" );
            att.addAttribute( Attribute.CONTENT, getTextBuffer().toString() );

            writeSimpleTag( Tag.META, att );

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
    // Public protected methods
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    protected void write( String text )
    {
        super.write( text );
    }

    /**
     * @param headTitleFlag an header title flag.
     */
    protected void setHeadTitleFlag( boolean headTitleFlag )
    {
        this.headTitleFlag = headTitleFlag;
    }

    /**
     * @return the current headTitleFlag.
     */
    protected boolean isHeadTitleFlag()
    {
        return this.headTitleFlag ;
    }
}
