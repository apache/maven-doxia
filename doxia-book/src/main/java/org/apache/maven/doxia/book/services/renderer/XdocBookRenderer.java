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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import org.apache.maven.doxia.sink.PipelineSink;
import org.apache.maven.doxia.index.IndexEntry;
import org.apache.maven.doxia.module.xdoc.XdocSink;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.manager.ParserNotFoundException;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.util.HtmlTools;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.StringUtils;

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
     * @param key the key for the desired string
     * @return the string for the given key
     */
    protected String getString( String key )
    {
        if ( StringUtils.isEmpty( key ) )
        {
            throw new IllegalArgumentException( "The key cannot be empty" );
        }

        // TODO Handle locale
        return i18n.getString( "book-renderer", Locale.getDefault(), key ).trim();
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

        Iterator ii = context.getIndex().getChildEntries().iterator();

        for ( Iterator it = book.getChapters().iterator(); it.hasNext(); )
        {
            Chapter chapter = (Chapter) it.next();

            renderChapter( chapter, context, (IndexEntry) ii.next() );
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
        FileWriter writer = new FileWriter( index );

        XdocSink sink = new IndexXdocBookSink( writer, context.getIndex().getFirstEntry(), i18n );

        // -----------------------------------------------------------------------
        // Head
        // -----------------------------------------------------------------------

        sink.head();

        sink.title();
        sink.text( book.getTitle() + " - " + getString( "toc" ) );
        sink.title_();

        sink.head_();

        // -----------------------------------------------------------------------
        // Body
        // -----------------------------------------------------------------------

        sink.body();

        sink.section1();
        sink.sectionTitle1();
        sink.text( book.getTitle() + " - " + getString( "toc" ) );
        sink.sectionTitle1_();

        sink.list();
        for ( Iterator it = context.getIndex().getChildEntries().iterator(); it.hasNext(); )
        {
            writeChapterIndexForBookIndex( sink, (IndexEntry) it.next() );
        }
        sink.list_();

        sink.section1_();

        sink.body_();
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
        for ( Iterator it = chapterEntry.getChildEntries().iterator(); it.hasNext(); )
        {
            IndexEntry sectionIndex = (IndexEntry) it.next();
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
        for ( Iterator it = sectionIndex.getChildEntries().iterator(); it.hasNext(); )
        {
            IndexEntry subsectionIndex = (IndexEntry) it.next();
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
            writeChapterIndex( index, chapter, chapterIndex );
        }
        catch ( IOException e )
        {
            throw new BookDoxiaException( "Error while rendering index page to: '"
                        + index.getAbsolutePath() + "'.", e );
        }

        // -----------------------------------------------------------------------
        // Render all section pages
        // -----------------------------------------------------------------------

        Iterator ii = chapterIndex.getChildEntries().iterator();

        for ( Iterator it = chapter.getSections().iterator(); it.hasNext(); )
        {
            Section section = (Section) it.next();

            renderSection( context, section, (IndexEntry) ii.next() );
        }
    }

    /**
     * Write a chapter index
     *
     * @param index the File.
     * @param chapter the Chapter.
     * @param chapterIndex the IndexEntry.
     * @throws IOException if any.
     */
    private void writeChapterIndex( File index, Chapter chapter, IndexEntry chapterIndex )
        throws IOException
    {
        FileWriter writer = new FileWriter( index );

        ChapterXdocBookSink sink = new ChapterXdocBookSink( writer, chapterIndex, i18n );

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
        for ( Iterator it = chapterIndex.getChildEntries().iterator(); it.hasNext(); )
        {
            IndexEntry sectionIndex = (IndexEntry) it.next();
            writeSectionIndexForBookIndex( sink, sectionIndex );
        }
        sink.list_();

        sink.section1_();

        sink.body_();
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
            FileWriter writer = new FileWriter( context.getOutputDirectory() + "/" + section.getId() + ".xml" );

            SectionXdocBookSink sink = new SectionXdocBookSink( writer, sectionIndex, i18n );

            BookContext.BookFile bookFile = (BookContext.BookFile) context.getFiles().get( section.getId() );

            if ( bookFile == null )
            {
                throw new BookDoxiaException( "No document that matches section with id=" + section.getId() + "." );
            }

            List pipeline = new ArrayList();
            //            pipeline.add( DebugSink.newInstance() );
            pipeline.add( sink );
            Sink pipelineSink = PipelineSink.newInstance( pipeline );

            try
            {
                doxia.parse( new FileReader( bookFile.getFile() ), bookFile.getParserId(), pipelineSink );
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
        }
        catch ( IOException e )
        {
            throw new BookDoxiaException( "Error while rendering book.", e );
        }
    }
}
