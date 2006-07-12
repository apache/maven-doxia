package org.apache.maven.doxia.book.services.indexer;

import org.apache.maven.doxia.book.model.BookModel;
import org.apache.maven.doxia.book.context.BookContext;
import org.apache.maven.doxia.book.BookDoxiaException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface BookIndexer
{
    String ROLE = BookIndexer.class.getName();

    void indexBook( BookModel book, BookContext bookContext )
        throws BookDoxiaException;
}
