package org.apache.maven.doxia.book;

import java.io.File;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface BookDoxia
{
    String ROLE = BookDoxia.class.getName();

    void renderBook( File bookDescriptor, String bookRendererId, List files, File outputDirectory )
        throws BookDoxiaException;
}
