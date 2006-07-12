package org.apache.maven.doxia.book;

import org.apache.maven.doxia.book.model.BookModel;

import java.io.File;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface BookDoxia
{
    String ROLE = BookDoxia.class.getName();

    BookModel loadBook( File bookDescriptor )
        throws BookDoxiaException;

    void renderBook( BookModel book, String bookRendererId, List files, File outputDirectory )
        throws BookDoxiaException;
}
