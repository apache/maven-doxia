package org.apache.maven.doxia.book.services.renderer;

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

import org.apache.maven.doxia.Doxia;
import org.apache.maven.doxia.book.context.BookContext;
import org.apache.maven.doxia.book.BookDoxiaException;
import org.apache.maven.doxia.book.model.BookModel;
import org.apache.maven.doxia.book.model.Chapter;
import org.apache.maven.doxia.book.model.Section;
import org.apache.maven.doxia.book.services.renderer.xhtml.XhtmlBookSink;
import org.apache.maven.doxia.sink.render.RenderingContext;
import org.apache.maven.doxia.parser.manager.ParserNotFoundException;
import org.apache.maven.doxia.parser.ParseException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * <p>XHtmlBookRenderer class.</p>
 *
 * @plexus.component role-hint="xhtml"
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class XHtmlBookRenderer
    extends AbstractLogEnabled
    implements BookRenderer
{
    /**
     * @plexus.requirement
     */
    private Doxia doxia;

    // ----------------------------------------------------------------------
    // BookRenderer Implementation
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void renderBook( BookContext context )
        throws BookDoxiaException
    {
        BookModel book = context.getBook();

        if ( !context.getOutputDirectory().exists() )
        {
            if ( !context.getOutputDirectory().mkdirs() )
            {
                throw new BookDoxiaException( "Could not make directory: "
                            + context.getOutputDirectory().getAbsolutePath() + "." );
            }
        }

        File bookFile = new File( context.getOutputDirectory(), book.getId() + ".xhtml" );

        Writer fileWriter;

        try
        {
            fileWriter = new FileWriter( bookFile );
        }
        catch ( IOException e )
        {
            throw new BookDoxiaException( "Error while opening file.", e );
        }

        XhtmlBookSink sink = new XhtmlBookSink( fileWriter,
              new RenderingContext( context.getOutputDirectory(), bookFile.getAbsolutePath() ) );

        try
        {
            sink.bookHead();
            sink.bookTitle();
            sink.text( context.getBook().getTitle() );
            sink.bookTitle_();
            sink.bookAuthor();
            sink.text( context.getBook().getAuthor() );
            sink.bookAuthor_();
            sink.bookDate();
            sink.text( context.getBook().getDate() );
            sink.bookDate_();
            sink.bookHead_();
            sink.bookBody();

            int chapterNumber = 1;

            for ( Chapter chapter : book.getChapters() )
            {
                sink.sectionTitle();
                sink.text( Integer.toString( chapterNumber ) + ". " + chapter.getTitle() );
                sink.sectionTitle_();

                renderChapter( sink, chapter, context );

                chapterNumber++;
            }

            sink.bookBody_();
        }
        finally
        {
            sink.flush();

            sink.close();

            IOUtil.close( fileWriter );
        }
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    /**
     * Write a chapter.
     *
     * @param sink the XhtmlBookSink.
     * @param chapter the Chapter.
     * @param context the BookContext.
     * @throws BookDoxiaException if the chapter cannot be written.
     */
    private void renderChapter( XhtmlBookSink sink, Chapter chapter, BookContext context )
        throws BookDoxiaException
    {
        for ( Section section : chapter.getSections() )
        {
            renderSection( sink, section, context );
        }
    }

    /**
     * Write a section.
     *
     * @param sink the XhtmlBookSink.
     * @param section the Section.
     * @param context the BookContext.
     * @throws BookDoxiaException if the section cannot be written.
     */
    private void renderSection( XhtmlBookSink sink, Section section, BookContext context )
        throws BookDoxiaException
    {
        sink.section2();

        BookContext.BookFile bookFile = context.getFiles().get( section.getId() );

        if ( bookFile == null )
        {
            throw new BookDoxiaException( "No document that matches section with id=" + section.getId() + "." );
        }

        Reader reader = null;
        try
        {
            reader = ReaderFactory.newReader( bookFile.getFile(), context.getInputEncoding() );
            doxia.parse( reader, bookFile.getParserId(), sink );
        }
        catch ( ParserNotFoundException e )
        {
            throw new BookDoxiaException( "Parser not found: "
                      + bookFile.getParserId() + ".", e );
        }
        catch ( ParseException e )
        {
            throw new BookDoxiaException( "Error while parsing document: "
                      + bookFile.getFile().getAbsolutePath() + ".", e );
        }
        catch ( FileNotFoundException e )
        {
            throw new BookDoxiaException( "Could not find document: "
                      + bookFile.getFile().getAbsolutePath() + ".", e );
        }
        catch ( IOException e )
        {
            throw new BookDoxiaException( "Error while rendering book: "
                      + bookFile.getFile().getAbsolutePath() + ".", e );
        }
        finally
        {
            IOUtil.close( reader );
        }

        sink.section2_();
    }
}
