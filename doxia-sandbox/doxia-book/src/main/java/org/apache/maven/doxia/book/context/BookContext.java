package org.apache.maven.doxia.book.context;

import org.apache.maven.doxia.book.model.Book;

import java.util.Map;
import java.util.HashMap;
import java.io.File;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class BookContext
{
    private Book book;

    private Map files;

    private File outputDirectory;

    private BookIndex index;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public static class BookFile
    {
        private File file;

        private String parserId;

        public BookFile( File file, String parserId )
        {
            this.file = file;
            this.parserId = parserId;
        }

        public File getFile()
        {
            return file;
        }

        public String getParserId()
        {
            return parserId;
        }
    }

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    public Book getBook()
    {
        return book;
    }

    public void setBook( Book book )
    {
        this.book = book;
    }

    public Map getFiles()
    {
        if ( files == null )
        {
            files = new HashMap();
        }

        return files;
    }

    public void setFiles( Map files )
    {
        this.files = files;
    }

    public File getOutputDirectory()
    {
        return outputDirectory;
    }

    public void setOutputDirectory( File outputDirectory )
    {
        this.outputDirectory = outputDirectory;
    }

    public BookIndex getIndex()
    {
        return index;
    }

    public void setIndex( BookIndex index )
    {
        this.index = index;
    }
}
