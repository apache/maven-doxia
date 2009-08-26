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
    private final I18N i18n;

    /** The wanted locale */
    private final Locale locale;

    /**
     * Default constructor.
     *
     * @param out a Writer.
     * @param i18n I18N.
     * @param locale the wanted locale.
     */
    public AbstractXdocBookSink( Writer out, I18N i18n, Locale locale )
    {
        super( out );

        this.i18n = i18n;
        this.locale = locale;
    }

    /** {@inheritDoc} */
    public void date_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void body()
    {
        writeStartTag( BODY );

        write( "<section name=\"\">" );

        navigationPanel();
        horizontalRule();

        write( "</section>" );
    }

    /** {@inheritDoc} */
    public void body_()
    {
        write( "<section name=\"\">" );

        horizontalRule();

        navigationPanel();

        write( "</section>" );

        writeEndTag( BODY );

        writeEndTag( DOCUMENT_TAG );

        flush();

        close();

        init();
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

        return i18n.getString( "book-renderer", locale, key ).trim();
    }

    /**
     * Add a navigation panel.
     */
    protected abstract void navigationPanel();
}
