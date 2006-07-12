package org.apache.maven.doxia.book.services.io;

import org.apache.maven.doxia.book.model.BookModel;
import org.apache.maven.doxia.book.BookDoxiaException;
import org.apache.maven.doxia.book.context.BookContext;

import java.io.File;
import java.util.List;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface BookIo
{
    String ROLE = BookIo.class.getName();

    BookModel readBook( File bookDescriptor )
        throws BookDoxiaException;

    void loadFiles( BookContext context, List files );
}
