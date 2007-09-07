package org.apache.maven.doxia.book.services.renderer.xhtml;

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

import org.apache.maven.doxia.module.xhtml.XhtmlSink;
import org.apache.maven.doxia.module.xhtml.decoration.render.RenderingContext;

import java.io.Writer;

import javax.swing.text.html.HTML.Tag;

/**
 * An Xhtml Sink that doesn't write out head or body elements.
 *
 * @author ltheussl
 * @version $Id$
 */
public class XhtmlBookSink
    extends XhtmlSink
{
    /**
     * Construct a new XhtmlBookSink.
     *
     * @param out the writer for the sink.
     * @param context the RenderingContext.
     */
    public XhtmlBookSink( Writer out, RenderingContext context )
    {
        super( out, context );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /** Does nothing. */
    public void head()
    {
        resetState();

        setHeadFlag( true );
    }

    /** Does nothing. */
    public void head_()
    {
        setHeadFlag( false );
    }

    /** Does nothing. */
    public void title()
    {
        // noop
    }

    /** Does nothing. */
    public void title_()
    {
        resetBuffer();
    }

    /** Does nothing. */
    public void author_()
    {
        resetBuffer();
    }

    /** Does nothing. */
    public void date_()
    {
        resetBuffer();
    }

    /** Does nothing. */
    public void body()
    {
        // noop
    }

    /** Does nothing. */
    public void body_()
    {
        // noop
    }

    /** Calls super.head(). */
    public void bookHead()
    {
        super.head();
    }

    /** Calls super.head_(). */
    public void bookHead_()
    {
        super.head_();
    }

    /** Calls super.title(). */
    public void bookTitle()
    {
        super.title();
    }

    /** Calls super.title_(). */
    public void bookTitle_()
    {
        super.title_();
    }

    /** Calls super.head_(). */
    public void bookAuthor_()
    {
        super.author_();
    }

    /** Calls super.head_(). */
    public void bookDate_()
    {
        super.date_();
    }

    /**  Calls super.body(). */
    public void bookBody()
    {
        super.body();
    }

    /** Calls super.body_(). */
    public void bookBody_()
    {
        super.body_();
    }

    /** {@inheritDoc} */
    public void sectionTitle()
    {
        writeStartTag( Tag.H1 );
    }

    /** {@inheritDoc} */
    public void sectionTitle_()
    {
        writeEndTag( Tag.H1 );
    }
}
