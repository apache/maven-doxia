package org.apache.maven.doxia.book.services.renderer.xdoc;

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
import java.util.Locale;

import org.apache.maven.doxia.module.xdoc.XdocSink;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.util.StringUtils;

/**
 * Abstract <code>XdocSink</code> implementation for book.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public abstract class AbstractXdocBookSink
    extends XdocSink
{
    /** I18N for localized messages. */
    private I18N i18n;

    /**
     * Default constructor.
     *
     * @param out a Writer.
     * @param i18n I18N.
     */
    public AbstractXdocBookSink( Writer out, I18N i18n )
    {
        super( out );

        this.i18n = i18n;
    }

    /**
     * @see org.apache.maven.doxia.module.xdoc.XdocSink#head()
     */
    public void head()
    {
        resetState();

        headFlag = true;

        markup( "<?xml version=\"1.0\" ?>" + EOL );

        markup( "<document>" + EOL );

        markup( "<properties>" + EOL );

    }

    /**
     * @see org.apache.maven.doxia.module.xdoc.XdocSink#head_()
     */
    public void head_()
    {
        headFlag = false;

        markup( "</properties>" + EOL );
    }

    /**
     * @see org.apache.maven.doxia.module.xdoc.XdocSink#author_()
     */
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

    /**
     * @see org.apache.maven.doxia.module.xdoc.XdocSink#date_()
     */
    public void date_()
    {
        // nop
    }

    /**
     * @see org.apache.maven.doxia.module.xdoc.XdocSink#body()
     */
    public void body()
    {
        markup( "<body>" + EOL );

        navigationPanel();

        horizontalRule();
    }

    /**
     * @see org.apache.maven.doxia.module.xdoc.XdocSink#body_()
     */
    public void body_()
    {
        horizontalRule();

        navigationPanel();

        markup( "</body>" + EOL );

        markup( "</document>" + EOL );

        out.flush();

        resetState();
    }

    /**
     * @see org.apache.maven.doxia.module.xdoc.XdocSink#title_()
     */
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

    // -----------------------------------------------------------------------
    // Protected
    // -----------------------------------------------------------------------

    /**
     * Gets a trimmed String for the given key from the resource bundle defined by Plexus.
     *
     * @param key the key for the desired string
     * @return the string for the given key
     */
    protected String getString( String key )
    {
        if ( StringUtils.isEmpty( key ) )
        {
            throw new IllegalArgumentException( "The key cannot be empty" );
        }

        return i18n.getString( "book-renderer", Locale.getDefault(), key ).trim();
    }

    /**
     * Add a navigation panel.
     */
    protected abstract void navigationPanel();
}
