package org.apache.maven.doxia.editor.windows;

import org.apache.maven.doxia.editor.Application;
import org.apache.maven.doxia.editor.EditorDocumentListener;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class EditorWindow
    extends JFrame
{
    private Application application;

    private JToolBar toolBar;

    private JTextPane textPane;

    private Document document;

    public EditorWindow( Application application )
        throws HeadlessException
    {
        this.application = application;

        // ----------------------------------------------------------------------
        // Initialize the widgets
        // ----------------------------------------------------------------------

        textPane = new JTextPane();

        document = textPane.getDocument();

        document.addDocumentListener( new EditorDocumentListener() );

        toolBar = new JToolBar();
        toolBar.add( application.getActionManager().getAction( "open-document" ) ).setText( "Open" );
        toolBar.add( application.getActionManager().getAction( "save-document" ) ).setText( "Save" );

        getContentPane().add( textPane, BorderLayout.CENTER );
        getContentPane().add( toolBar, BorderLayout.PAGE_START );
        setSize( 600, 500 );
    }


    public Document getDocument()
    {
        return document;
    }
}
