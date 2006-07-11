package org.apache.maven.doxia.book.services.indexer;

import org.apache.maven.doxia.book.context.BookContext;
import org.apache.maven.doxia.book.context.IndexEntry;
import org.apache.maven.doxia.book.context.BookIndex;
import org.apache.maven.doxia.book.model.Book;
import org.apache.maven.doxia.book.model.Chapter;
import org.apache.maven.doxia.book.model.Section;
import org.apache.maven.doxia.book.BookDoxiaException;
import org.apache.maven.doxia.parser.manager.ParserNotFoundException;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.Doxia;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.util.Iterator;
import java.io.FileReader;
import java.io.FileNotFoundException;

/**
 * @plexus.component
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultBookIndexer
    extends AbstractLogEnabled
    implements BookIndexer
{
    /**
     * @plexus.requirement
     */
    private Doxia doxia;

    // ----------------------------------------------------------------------
    // BookIndexer Implementation
    // ----------------------------------------------------------------------

    public void indexBook( Book book, BookContext bookContext )
        throws BookDoxiaException
    {
        BookIndex index = new BookIndex();

        for ( Iterator it = book.getChapters().iterator(); it.hasNext(); )
        {
            Chapter chapter = (Chapter) it.next();

            indexChapter( bookContext, index, chapter );
        }

        bookContext.setIndex( index );
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    private void indexChapter( BookContext context, IndexEntry bookEntry, Chapter chapter )
        throws BookDoxiaException
    {
        IndexEntry chapterEntry = new IndexEntry( bookEntry, chapter.getId( ) );

        chapterEntry.setTitle( chapter.getTitle() );

        for ( Iterator it = chapter.getSections().iterator(); it.hasNext(); )
        {
            Section section = (Section) it.next();

            indexSection( context, chapterEntry, section );
        }
    }

    private void indexSection( BookContext bookContext, IndexEntry chapterEntry, Section section )
        throws BookDoxiaException
    {
        BookContext.BookFile bookFile = (BookContext.BookFile) bookContext.getFiles().get( section.getId() );

        if ( bookFile == null )
        {
            throw new BookDoxiaException( "No document that matches section with id=" + section.getId() + "." );
        }

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        IndexEntry sectionEntry = new IndexEntry( chapterEntry, section.getId() );

        BookIndexingSink sink = new BookIndexingSink( sectionEntry );

        try
        {
            doxia.parse( new FileReader( bookFile.getFile() ), bookFile.getParserId(), sink );
        }
        catch ( ParserNotFoundException e )
        {
            throw new BookDoxiaException( "Parser not found: " + bookFile.getParserId() + ".", e );
        }
        catch ( ParseException e )
        {
            throw new BookDoxiaException( "Error while parsing document: " + bookFile.getFile().getAbsolutePath() + ".", e );
        }
        catch ( FileNotFoundException e )
        {
            throw new BookDoxiaException( "Could not find document: " + bookFile.getFile().getAbsolutePath() + ".", e );
        }

        sectionEntry.setTitle( sink.getTitle() );
    }
}
