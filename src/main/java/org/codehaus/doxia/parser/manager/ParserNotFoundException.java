package org.codehaus.doxia.parser.manager;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ParserNotFoundException
    extends Exception
{
    public ParserNotFoundException( String message )
    {
        super( message );
    }

    public ParserNotFoundException( Throwable cause )
    {
        super( cause );
    }

    public ParserNotFoundException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
