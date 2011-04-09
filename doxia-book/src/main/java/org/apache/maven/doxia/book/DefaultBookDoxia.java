package org.apache.maven.doxia.book;

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

import org.apache.maven.doxia.book.context.BookContext;
import org.apache.maven.doxia.book.model.BookModel;
import org.apache.maven.doxia.book.services.indexer.BookIndexer;
import org.apache.maven.doxia.book.services.io.BookIo;
import org.apache.maven.doxia.book.services.renderer.BookRenderer;
import org.apache.maven.doxia.book.services.validation.BookValidator;
import org.apache.maven.doxia.book.services.validation.ValidationResult;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of BookDoxia.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @plexus.component
 */
public class DefaultBookDoxia
    extends AbstractLogEnabled
    implements BookDoxia
{
    /**
     * @plexus.requirement
     */
    private BookIo bookIo;

    /**
     * @plexus.requirement
     */
    private BookValidator bookValidator;

    /**
     * @plexus.requirement
     */
    private BookIndexer bookIndexer;

    /**
     * @plexus.requirement role="org.apache.maven.doxia.book.services.renderer.BookRenderer"
     */
    private Map<String, BookRenderer> bookRenderers;

    // ----------------------------------------------------------------------
    // BookDoxia Implementation
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    public BookModel loadBook( File bookDescriptor )
        throws BookDoxiaException
    {
        return bookIo.readBook( bookDescriptor );
    }

    /** {@inheritDoc} */
    public void renderBook( BookModel book, String bookRendererId, List<File> files, File outputDirectory )
        throws BookDoxiaException
    {
        renderBook( book, bookRendererId, files, outputDirectory, Locale.getDefault(), "UTF-8", "UTF-8" );
    }

    /** {@inheritDoc} */
    public void renderBook( BookModel book, String bookRendererId, List<File> files, File outputDirectory,
                            Locale locale, String inputEncoding, String outputEncoding )
        throws BookDoxiaException
    {
        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        ValidationResult validationResult = bookValidator.validateBook( book );

        if ( !validationResult.isAllOk() )
        {
            throw new InvalidBookDescriptorException( validationResult );
        }

        // ----------------------------------------------------------------------
        // Create and initialize the context
        // ----------------------------------------------------------------------

        BookContext context = new BookContext();

        context.setBook( book );

        context.setOutputDirectory( outputDirectory );

        context.setLocale( locale );

        context.setInputEncoding( inputEncoding );

        context.setOutputEncoding( outputEncoding );

        // -----------------------------------------------------------------------
        //
        // -----------------------------------------------------------------------

        bookIo.loadFiles( context, files );

        // ----------------------------------------------------------------------
        // Generate indexes
        // ----------------------------------------------------------------------

        bookIndexer.indexBook( book, context );

        // ----------------------------------------------------------------------
        // Render the book
        // ----------------------------------------------------------------------

        BookRenderer bookRenderer = bookRenderers.get( bookRendererId );

        if ( bookRenderer == null )
        {
            throw new BookDoxiaException( "No such book renderer '" + bookRendererId + "'." );
        }

        bookRenderer.renderBook( context );
    }

    /**
     * Returns a Set of ids of the BookRenderers that are available in this BookDoxia.
     *
     * @return Set
     */
    public Set<String> getAvailableBookRenderers()
    {
        return Collections.unmodifiableSet( bookRenderers.keySet() );
    }

}
