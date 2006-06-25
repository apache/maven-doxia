package org.apache.maven.doxia.book.services.indexer;

import org.apache.maven.doxia.book.context.BookContext;
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
        BookIndexingSink sink = new BookIndexingSink( bookContext );

        for ( Iterator it = book.getChapters().iterator(); it.hasNext(); )
        {
            Chapter chapter = (Chapter) it.next();

            indexChapter( bookContext, chapter, sink );
        }
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    private void indexChapter( BookContext bookContext, Chapter chapter, BookIndexingSink sink )
        throws BookDoxiaException
    {
        for ( Iterator it = chapter.getSections().iterator(); it.hasNext(); )
        {
            Section section = (Section) it.next();

            indexSection( bookContext, section, sink );
        }
    }

    private void indexSection( BookContext bookContext, Section section, BookIndexingSink sink )
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
    }
}
