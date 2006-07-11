package org.apache.maven.doxia.book.services.renderer;

import org.apache.maven.doxia.Doxia;
import org.apache.maven.doxia.book.BookDoxiaException;
import org.apache.maven.doxia.book.context.BookContext;
import org.apache.maven.doxia.book.context.IndexEntry;
import org.apache.maven.doxia.book.model.Book;
import org.apache.maven.doxia.book.model.Chapter;
import org.apache.maven.doxia.book.model.Section;
import org.apache.maven.doxia.book.services.renderer.xdoc.XdocBookSink;
import org.apache.maven.doxia.editor.io.DebugSink;
import org.apache.maven.doxia.editor.io.PipelineSink;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.manager.ParserNotFoundException;
import org.apache.maven.doxia.sink.Sink;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
        Book book = context.getBook();

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

    private void renderBook( Book book, BookContext context )
        throws BookDoxiaException
    {
        Iterator ii = context.getIndex().getChildEntries().iterator();

        for ( Iterator it = book.getChapters().iterator(); it.hasNext(); )
        {
            Chapter chapter = (Chapter) it.next();

            renderChapter( chapter, context, (IndexEntry) ii.next() );
        }
    }

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
            pipeline.add( DebugSink.newInstance() );
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
