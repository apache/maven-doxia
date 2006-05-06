package org.apache.maven.doxia.editor;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DoxiaEditorException
    extends Exception
{
    public DoxiaEditorException( String message )
    {
        super( message );
    }

    public DoxiaEditorException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
