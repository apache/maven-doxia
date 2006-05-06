package org.apache.maven.doxia.editor.action.manager;

import javax.swing.*;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface ActionManager
{
    String ROLE = ActionManager.class.getName();

    Action getAction( String name );
}
