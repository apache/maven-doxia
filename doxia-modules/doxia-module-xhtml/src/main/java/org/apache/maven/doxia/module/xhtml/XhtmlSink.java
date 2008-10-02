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
import java.util.Map;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.module.xhtml.decoration.render.RenderingContext;
import org.apache.maven.doxia.sink.XhtmlBaseSink;
import org.apache.maven.doxia.sink.SinkEventAttributeSet;
import org.codehaus.plexus.util.StringUtils;

/**
 * Xhtml sink implementation. <br/>
 * <b>Note</b>: The encoding used is UTF-8.
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
    // ----------------------------------------------------------------------
    // Instance fields
    // ----------------------------------------------------------------------

    // TODO: this doesn't belong here
    private RenderingContext renderingContext;


    // ----------------------------------------------------------------------
    // Constructors
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
        super( writer );

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

    /** {@inheritDoc} */
    public void head()
    {
        resetState();

        setHeadFlag( true );

        write( "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" );
        write( "<html xmlns=\"http://www.w3.org/1999/xhtml\">" );

        writeStartTag( Tag.HEAD );
    }

    /** {@inheritDoc} */
    public void head_()
    {
        setHeadFlag( false );

        // always UTF-8
        write( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>" );

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
        content( getBuffer().toString() );

        writeEndTag( Tag.TITLE );

        resetBuffer();
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#META
     */
    public void author_()
    {
        if ( getBuffer().length() > 0 )
        {
            MutableAttributeSet att = new SinkEventAttributeSet();
            att.addAttribute( Attribute.NAME, "author" );
            att.addAttribute( Attribute.CONTENT, getBuffer().toString() );

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
        if ( getBuffer().length() > 0 )
        {
            MutableAttributeSet att = new SinkEventAttributeSet();
            att.addAttribute( Attribute.NAME, "date" );
            att.addAttribute( Attribute.CONTENT, getBuffer().toString() );

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
    // Public protected methods
    // ----------------------------------------------------------------------

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

        super.write( text );
    }

    /**
     * @return the current rendering context
     */
    public RenderingContext getRenderingContext()
    {
        return renderingContext;
    }
}
