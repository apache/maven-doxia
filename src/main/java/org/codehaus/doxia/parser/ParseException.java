package org.codehaus.doxia.parser;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ParseException
    extends Exception
{
    protected String fileName;
    protected int lineNumber;

    public ParseException( String message )
    {
        this( null, message, null, -1 );
    }

    public ParseException( String message, Exception e )
    {
        this( e, message, null, -1 );
    }

    public ParseException( Exception e )
    {
        this( e, null, null, -1 );
    }

    public ParseException( Exception e, String fileName, int lineNumber )
    {
        this( e, null, fileName, lineNumber );
    }

    public ParseException( Exception e, String message, String fileName, int lineNumber )
    {
        super( ( message == null ) ? ( ( e == null ) ? null : e.getMessage() ) : message, e );

        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    public String getFileName()
    {
        return fileName;
    }

    public int getLineNumber()
    {
        return lineNumber;
    }
}
