package org.codehaus.doxia.parser.manager;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: ParserNotFoundException.java,v 1.1 2004/09/15 15:14:29 jvanzyl Exp $
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
