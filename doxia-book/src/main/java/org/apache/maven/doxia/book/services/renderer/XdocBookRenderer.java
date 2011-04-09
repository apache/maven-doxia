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
import java.util.Iterator;
import java.util.Locale;

import org.apache.maven.doxia.Doxia;
import org.apache.maven.doxia.book.BookDoxiaException;
import org.apache.maven.doxia.book.context.BookContext;
import org.apache.maven.doxia.book.model.BookModel;
import org.apache.maven.doxia.book.model.Chapter;
import org.apache.maven.doxia.book.model.Section;
import org.apache.maven.doxia.book.services.renderer.xdoc.ChapterXdocBookSink;
import org.apache.maven.doxia.book.services.renderer.xdoc.IndexXdocBookSink;
import org.apache.maven.doxia.book.services.renderer.xdoc.SectionXdocBookSink;
import org.apache.maven.doxia.index.IndexEntry;
import org.apache.maven.doxia.module.xdoc.XdocSink;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.manager.ParserNotFoundException;
import org.apache.maven.doxia.util.HtmlTools;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.WriterFactory;

/**
 * An implementation of <code>BookRenderer</code> for Xdoc
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @plexus.component role-hint="xdoc"
 */
public class XdocBookRenderer
    extends AbstractLogEnabled
    implements BookRenderer
{
    /**
     * @plexus.requirement
     */
    private Doxia doxia;

    /**
     * @plexus.requirement
     */
    private I18N i18n;

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

        renderBook( book, context );
    }

    // -----------------------------------------------------------------------
    // Protected
    // -----------------------------------------------------------------------

    /**
     * Gets a trimmed String for the given key from the resource bundle defined by Plexus.
     *
     * @param locale the locale used
     * @param key the key for the desired string
     * @return the string for the given key and the given locale
     */
    protected String getString( Locale locale, String key )
    {
        if ( StringUtils.isEmpty( key ) )
        {
            throw new IllegalArgumentException( "The key cannot be empty" );
        }

        return i18n.getString( "book-renderer", locale, key ).trim();
    }

    // -----------------------------------------------------------------------
    // Private
    // -----------------------------------------------------------------------

    /**
     * Render the book, ie the book index and all chapter index and pages
     *
     * @param book the BookModel.
     * @param context the BookContext.
     * @throws BookDoxiaException if any
     */
    private void renderBook( BookModel book, BookContext context )
        throws BookDoxiaException
    {
        // -----------------------------------------------------------------------
        // Render the book index.xml page
        // -----------------------------------------------------------------------

        File index = new File( context.getOutputDirectory(), "index.xml" );

        try
        {
            writeBookIndex( index, book, context );
        }
        catch ( IOException e )
        {
            throw new BookDoxiaException( "Error while rendering index page to: '"
                        + index.getAbsolutePath() + "'.", e );
        }

        // -----------------------------------------------------------------------
        // Render all the chapter pages
        // -----------------------------------------------------------------------

        Iterator<IndexEntry> ii = context.getIndex().getChildEntries().iterator();

        for ( Chapter chapter : book.getChapters() )
        {
            renderChapter( chapter, context, ii.next() );
        }
    }

    /**
     * Write the book index, ie a TOC.
     *
     * @param index the File.
     * @param book the BookModel.
     * @param context the BookContext.
     * @throws IOException if any
     */
    private void writeBookIndex( File index, BookModel book, BookContext context )
        throws IOException
    {
        Writer writer = WriterFactory.newXmlWriter( index );

        XdocSink sink = new IndexXdocBookSink( writer, context.getIndex().getFirstEntry(), i18n, context.getLocale() );

        try
        {
            // -----------------------------------------------------------------------
            // Head
            // -----------------------------------------------------------------------

            sink.head();

            sink.title();
            sink.text( book.getTitle() + " - " + getString( context.getLocale(), "toc" ) );
            sink.title_();

            sink.head_();

            // -----------------------------------------------------------------------
            // Body
            // -----------------------------------------------------------------------

            sink.body();

            sink.section1();
            sink.sectionTitle1();
            sink.text( book.getTitle() + " - " + getString( context.getLocale(), "toc" ) );
            sink.sectionTitle1_();

            sink.list();
            for ( IndexEntry entry : context.getIndex().getChildEntries() )
            {
                writeChapterIndexForBookIndex( sink, entry );
            }
            sink.list_();

            sink.section1_();

            sink.body_();
        }
        finally
        {
            sink.flush();

            sink.close();

            IOUtil.close( writer );
        }
    }

    /**
     * Write the chapter index for the book index.
     *
     * @param sink the XdocSink.
     * @param chapterEntry the chapter IndexEntry.
     */
    private void writeChapterIndexForBookIndex( XdocSink sink, IndexEntry chapterEntry )
    {
        sink.listItem();
        sink.link( chapterEntry.getId() + ".html" );
        sink.text( chapterEntry.getTitle() );
        sink.link_();

        sink.list();
        for ( IndexEntry sectionIndex : chapterEntry.getChildEntries() )
        {
            writeSectionIndexForBookIndex( sink, sectionIndex );
        }
        sink.list_();

        sink.listItem_();
    }

    /**
     * Write the section index for the book index.
     *
     * @param sink the XdocSink.
     * @param sectionIndex the section IndexEntry.
     */
    private void writeSectionIndexForBookIndex( XdocSink sink, IndexEntry sectionIndex )
    {
        sink.listItem();
        sink.link( sectionIndex.getId() + ".html" );
        sink.text( sectionIndex.getTitle() );
        sink.link_();

        sink.list();
        for ( IndexEntry subsectionIndex : sectionIndex.getChildEntries() )
        {
            writeSubsectionIndexForBookIndex( sink, sectionIndex, subsectionIndex );
        }
        sink.list_();

        sink.listItem_();
    }

    /**
     * Write subsection index for the book index.
     *
     * @param sink the XdocSink.
     * @param sectionIndex the section IndexEntry.
     * @param subsectionIndex the subsection IndexEntry.
     */
    private void writeSubsectionIndexForBookIndex( XdocSink sink, IndexEntry sectionIndex, IndexEntry subsectionIndex )
    {
        sink.listItem();
        sink.link( sectionIndex.getId() + ".html#" + HtmlTools.encodeId( subsectionIndex.getId() ) );
        sink.text( subsectionIndex.getTitle() );
        sink.link_();
        sink.listItem_();
    }

    // -----------------------------------------------------------------------
    // Rendering
    // -----------------------------------------------------------------------

    /**
     * Render the chapter index and all section pages.
     *
     * @param chapter the Chapter.
     * @param context the BookContext.
     * @param chapterIndex the IndexEntry.
     * @throws BookDoxiaException if any
     */
    private void renderChapter( Chapter chapter, BookContext context, IndexEntry chapterIndex )
        throws BookDoxiaException
    {
        // -----------------------------------------------------------------------
        // Render the chapter index page
        // -----------------------------------------------------------------------

        File index = new File( context.getOutputDirectory(), chapter.getId() + ".xml" );

        try
        {
            writeChapterIndex( index, chapter, chapterIndex, context );
        }
        catch ( IOException e )
        {
            throw new BookDoxiaException( "Error while rendering index page to: '"
                        + index.getAbsolutePath() + "'.", e );
        }

        // -----------------------------------------------------------------------
        // Render all section pages
        // -----------------------------------------------------------------------

        Iterator<IndexEntry> ii = chapterIndex.getChildEntries().iterator();

        for ( Section section : chapter.getSections() )
        {
            renderSection( context, section, ii.next() );
        }
    }

    /**
     * Write a chapter index
     *
     * @param index the File.
     * @param context the context.
     * @param chapter the Chapter.
     * @param chapterIndex the IndexEntry.
     * @throws IOException if any.
     */
    private void writeChapterIndex( File index, Chapter chapter, IndexEntry chapterIndex, BookContext context )
        throws IOException
    {
        Writer writer = WriterFactory.newXmlWriter( index );

        ChapterXdocBookSink sink = new ChapterXdocBookSink( writer, chapterIndex, i18n, context.getLocale() );

        try
        {
            // -----------------------------------------------------------------------
            // Head
            // -----------------------------------------------------------------------

            sink.head();

            sink.title();
            sink.text( chapter.getTitle() );
            sink.title_();

            sink.head_();

            // -----------------------------------------------------------------------
            // Body
            // -----------------------------------------------------------------------

            sink.body();

            sink.section1();
            sink.sectionTitle1();
            sink.text( chapter.getTitle() );
            sink.sectionTitle1_();

            sink.list();
            for ( IndexEntry sectionIndex : chapterIndex.getChildEntries() )
            {
                writeSectionIndexForBookIndex( sink, sectionIndex );
            }
            sink.list_();

            sink.section1_();

            sink.body_();
        }
        finally
        {
            sink.flush();

            sink.close();

            IOUtil.close( writer );
        }
    }

    /**
     * Render all section pages.
     *
     * @param context the BookContext.
     * @param section the Section.
     * @param sectionIndex the IndexEntry.
     * @throws BookDoxiaException if any.
     */
    private void renderSection( BookContext context, Section section, IndexEntry sectionIndex )
        throws BookDoxiaException
    {
        try
        {
            Writer writer = WriterFactory.newXmlWriter( new File( context.getOutputDirectory()
                    + "/" + section.getId() + ".xml" ) );

            SectionXdocBookSink sink = new SectionXdocBookSink( writer, sectionIndex, i18n, context.getLocale() );

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
                throw new BookDoxiaException( "Error while parsing document: " + bookFile.getFile().getAbsolutePath()
                    + ".", e );
            }
            catch ( FileNotFoundException e )
            {
                throw new BookDoxiaException( "Could not find document: " + bookFile.getFile().getAbsolutePath() + ".",
                                              e );
            }
            finally
            {
                sink.flush();
                sink.close();

                IOUtil.close( reader );
                IOUtil.close( writer );
            }
        }
        catch ( IOException e )
        {
            throw new BookDoxiaException( "Error while rendering book.", e );
        }
    }
}
