package org.apache.maven.doxia.editor.action.manager;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.apache.maven.doxia.editor.action.manager.ActionManager;
import org.apache.maven.doxia.editor.action.AbstractDoxiaAction;
import org.apache.maven.doxia.editor.Application;

import javax.swing.*;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultActionManager
    extends AbstractLogEnabled
    implements ActionManager, Initializable
{
    private Application application;

    private Map actions = new HashMap();

    // ----------------------------------------------------------------------
    // ActionManager Implementation
    // ----------------------------------------------------------------------

    public Action getAction( String name )
    {
        Action action = (Action) actions.get( name );

        if ( action == null )
        {
            throw new RuntimeException( "No such action '" + name + "'." );
        }

        if ( action instanceof AbstractDoxiaAction )
        {
            ((AbstractDoxiaAction) action).setApplication( application );
        }

        return action;
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
    }
}
