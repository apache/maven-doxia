package org.apache.maven.doxia.book;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class BookDoxiaException
    extends Exception
{
    public BookDoxiaException( String message )
    {
        super( message );
    }

    public BookDoxiaException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
