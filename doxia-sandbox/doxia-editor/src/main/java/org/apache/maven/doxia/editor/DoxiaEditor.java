package org.apache.maven.doxia.editor;

import org.apache.maven.doxia.editor.windows.EditorWindow;
import org.codehaus.plexus.embed.Embedder;

import javax.swing.*;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DoxiaEditor
{
    public static void main( String[] args )
        throws Exception
    {
        new DoxiaEditor().work();
    }

    private void work()
        throws Exception
    {
        Embedder embedder = new Embedder();

        embedder.start();

        Application application = (Application) embedder.lookup( Application.ROLE );

        EditorWindow window = new EditorWindow( application );

        application.setEditorWindow( window );

        window.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );

        application.getActionManager().getAction( "open-document" ).actionPerformed( null );

        // ----------------------------------------------------------------------
        // Power on
        // ----------------------------------------------------------------------

        window.show();

        // ----------------------------------------------------------------------
        // Power off
        // ----------------------------------------------------------------------

        embedder.release( application );
    }
}
