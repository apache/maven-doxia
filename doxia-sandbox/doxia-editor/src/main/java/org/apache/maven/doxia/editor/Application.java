package org.apache.maven.doxia.editor;

import org.apache.maven.doxia.editor.action.manager.ActionManager;
import org.apache.maven.doxia.editor.action.AbstractDoxiaAction;
import org.apache.maven.doxia.editor.windows.EditorWindow;
import org.apache.maven.doxia.editor.io.DoxiaDocumentBuilder;
import org.apache.maven.doxia.Doxia;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface Application
{
    String ROLE = Application.class.getName();

    ActionManager getActionManager();

    Object lookup( String role );

    EditorWindow getEditorWindow();

    void setEditorWindow( EditorWindow editorWindow );

    Doxia getDoxia();

    DoxiaDocumentBuilder getDoxiaDocumentBuilder();
}
