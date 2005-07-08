package org.codehaus.doxia.sink.manager;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class SinkNotFoundException
    extends Exception
{
    public SinkNotFoundException( String message )
    {
        super( message );
    }

    public SinkNotFoundException( Throwable cause )
    {
        super( cause );
    }

    public SinkNotFoundException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
