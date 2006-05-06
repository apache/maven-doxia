package org.apache.maven.doxia.editor.action;

import org.apache.maven.doxia.editor.Application;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractDoxiaAction
    extends AbstractAction
{
    private Application application;

    public Application getApplication()
    {
        return application;
    }

    public void setApplication( Application application )
    {
        this.application = application;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected abstract void doAction( ActionEvent e )
        throws Exception;

    public final void actionPerformed( ActionEvent event )
    {
        try
        {
            doAction( event );
        }
        catch ( Exception e )
        {
            e.printStackTrace( System.out );
        }
    }
}
