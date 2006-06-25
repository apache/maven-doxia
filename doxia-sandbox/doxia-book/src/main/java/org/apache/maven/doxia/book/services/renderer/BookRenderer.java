package org.apache.maven.doxia.book.services.renderer;

import org.apache.maven.doxia.book.context.BookContext;
import org.apache.maven.doxia.book.BookDoxiaException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface BookRenderer
{
    String ROLE = BookRenderer.class.getName();

    void renderBook( BookContext context )
        throws BookDoxiaException;
}
