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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.maven.doxia.Doxia;
import org.apache.maven.doxia.book.BookDoxiaException;
import org.apache.maven.doxia.book.context.BookContext;
import org.apache.maven.doxia.book.model.BookModel;
import org.apache.maven.doxia.book.model.Chapter;
import org.apache.maven.doxia.book.model.Section;
import org.apache.maven.doxia.book.services.renderer.docbook.DocBookBookSink;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.manager.ParserNotFoundException;
import org.apache.maven.doxia.sink.Sink;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.WriterFactory;

/**
 * An implementation of <code>BookRenderer</code> for docbook
 *
 * @plexus.component role-hint="doc-book"
 * @author Eric Redmond
 * @version $Id$
 */
public class DocbookBookRenderer
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

        File bookFile = new File( context.getOutputDirectory(), book.getId() + ".xml" );

        Writer fileWriter;
        try
        {
            fileWriter = WriterFactory.newXmlWriter( bookFile );
        }
        catch ( IOException e )
        {
            throw new BookDoxiaException( "Error while opening file.", e );
        }

        // ----------------------------------------------------------------------
        // Create the Dockbook File
        // ----------------------------------------------------------------------

        // TODO: Write out TOC?

        DocBookBookSink sink = new DocBookBookSink( fileWriter );

        try
        {
            sink.book();

            // TODO: symmetrize bookHead?

            if ( StringUtils.isNotEmpty( book.getTitle() ) )
            {
                sink.bookTitle();
                sink.text( book.getTitle() );
                sink.bookTitle_();
            }

            if ( StringUtils.isNotEmpty( book.getDate() ) )
            {
                sink.bookDate();
                sink.text( book.getDate() );
                sink.bookDate_();
            }

            if ( StringUtils.isNotEmpty( book.getAuthor() ) )
            {
                sink.bookAuthor();
                sink.text( book.getAuthor() );
                sink.bookAuthor_();
            }

            sink.bookHead_();

            for ( Chapter chapter : book.getChapters() )
            {
                sink.chapter();

                if ( StringUtils.isNotEmpty( chapter.getTitle() ) )
                {
                    sink.chapterTitle();
                    sink.text( chapter.getTitle() );
                    sink.chapterTitle_();
                }

                renderChapter( chapter, context, sink );

                sink.chapter_();
            }

            sink.book_();
        }
        finally
        {
            sink.flush();

            sink.close();

            IOUtil.close( fileWriter );
        }
    }

    /**
     * Write a chapter.
     *
     * @param writer the writer.
     * @param chapter the Chapter.
     * @param context the BookContext.
     * @param sink a Sink.
     * @throws BookDoxiaException if the chapter cannot be written.
     */
    private void renderChapter( Chapter chapter, BookContext context, Sink sink )
        throws BookDoxiaException
    {
        for ( Section section : chapter.getSections() )
        {
            renderSection( section, context, sink );
        }
    }

    /**
     * Write a section.
     *
     * @param writer the writer.
     * @param section the Section.
     * @param context the BookContext.
     * @param sink a Sink.
     * @throws BookDoxiaException if the section cannot be written.
     */
    private void renderSection( Section section, BookContext context, Sink sink )
        throws BookDoxiaException
    {
        BookContext.BookFile bookFile = (BookContext.BookFile) context.getFiles().get( section.getId() );

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
            throw new BookDoxiaException( "Parser not found: " + bookFile.getParserId() + ".", e );
        }
        catch ( ParseException e )
        {
            throw new BookDoxiaException(
                                          "Error while parsing document: " + bookFile.getFile().getAbsolutePath() + ".",
                                          e );
        }
        catch ( FileNotFoundException e )
        {
            throw new BookDoxiaException( "Could not find document: " + bookFile.getFile().getAbsolutePath() + ".", e );
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
    }
}
