package org.apache.maven.doxia.book.services.renderer;

import org.apache.maven.doxia.Doxia;
import org.apache.maven.doxia.module.xdoc.XdocSink;
import org.apache.maven.doxia.book.BookDoxiaException;
import org.apache.maven.doxia.book.context.BookContext;
import org.apache.maven.doxia.book.context.IndexEntry;
import org.apache.maven.doxia.book.model.BookModel;
import org.apache.maven.doxia.book.model.Chapter;
import org.apache.maven.doxia.book.model.Section;
import org.apache.maven.doxia.book.services.renderer.xdoc.XdocBookSink;
import org.apache.maven.doxia.editor.io.PipelineSink;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.manager.ParserNotFoundException;
import org.apache.maven.doxia.sink.Sink;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
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

    // ----------------------------------------------------------------------
    // BookRenderer Implementation
    // ----------------------------------------------------------------------

    public void renderBook( BookContext context )
        throws BookDoxiaException
    {
        BookModel book = context.getBook();

        if ( !context.getOutputDirectory().exists() )
        {
            if ( !context.getOutputDirectory().mkdirs() )
            {
                throw new BookDoxiaException(
                    "Could not make directory: " + context.getOutputDirectory().getAbsolutePath() + "." );
            }
        }

        // -----------------------------------------------------------------------
        //
        // -----------------------------------------------------------------------

        renderBook( book, context );
    }

    // -----------------------------------------------------------------------
    // Private
    // -----------------------------------------------------------------------

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
            throw new BookDoxiaException( "Error while rendering index page to: '" + index.getAbsolutePath() + "'.", e );
        }

        // -----------------------------------------------------------------------
        // Render the index.html files for each chapter
        // -----------------------------------------------------------------------

        // TODO: Implement

        // -----------------------------------------------------------------------
        // Render all the chapters
        // -----------------------------------------------------------------------

        Iterator ii = context.getIndex().getChildEntries().iterator();

        for ( Iterator it = book.getChapters().iterator(); it.hasNext(); )
        {
            Chapter chapter = (Chapter) it.next();

            renderChapter( chapter, context, (IndexEntry) ii.next() );
        }
    }

    // -----------------------------------------------------------------------
    // Index Rendering
    // -----------------------------------------------------------------------

    private void writeBookIndex( File index, BookModel book, BookContext context )
        throws IOException
    {
        XdocSink sink = new XdocSink( new FileWriter( index ) );

        // -----------------------------------------------------------------------
        // Head
        // -----------------------------------------------------------------------

        sink.head();

        sink.title();
        sink.text( book.getTitle() + " - Index" );
        sink.title_();

        sink.head_();

        // -----------------------------------------------------------------------
        // Body
        // -----------------------------------------------------------------------

        sink.body();

        sink.section1();
        sink.sectionTitle1();
        sink.text( book.getTitle() + " - Index" );
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

    private void writeSubsectionIndexForBookIndex( XdocSink sink, IndexEntry sectionIndex, IndexEntry subsectionIndex )
    {
        sink.listItem();
        sink.link( sectionIndex.getId() + ".html#" + subsectionIndex.getId() );
        sink.text( subsectionIndex.getTitle() );
        sink.link_();
        sink.listItem_();
    }

    // -----------------------------------------------------------------------
    // Rendering
    // -----------------------------------------------------------------------

    private void renderChapter( Chapter chapter, BookContext context, IndexEntry chapterIndex )
        throws BookDoxiaException
    {
        Iterator ii = chapterIndex.getChildEntries().iterator();

        for ( Iterator it = chapter.getSections().iterator(); it.hasNext(); )
        {
            Section section = (Section) it.next();

            renderSection( context, section, (IndexEntry) ii.next() );
        }
    }

    private void renderSection( BookContext context, Section section, IndexEntry sectionIndex )
        throws BookDoxiaException
    {
        try
        {
            FileWriter writer = new FileWriter( context.getOutputDirectory() + "/" + section.getId() + ".xml" );

            XdocBookSink sink = new XdocBookSink( writer, sectionIndex );

            BookContext.BookFile bookFile = (BookContext.BookFile) context.getFiles().get( section.getId() );

            if ( bookFile == null )
            {
                throw new BookDoxiaException(
                    "No document that matches section with id=" + section.getId() + "." );
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
                throw new BookDoxiaException(
                    "Error while parsing document: " + bookFile.getFile().getAbsolutePath() + ".", e );
            }
            catch ( FileNotFoundException e )
            {
                throw new BookDoxiaException(
                    "Could not find document: " + bookFile.getFile().getAbsolutePath() + ".", e );
            }
        }
        catch ( IOException e )
        {
            throw new BookDoxiaException( "Error while rendering book.", e );
        }
    }
}
