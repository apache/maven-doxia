package org.apache.maven.doxia.editor.model;

import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class Element
{
    private List children;

    public List getChildren()
    {
        if ( children == null )
        {
            children = new ArrayList();
        }

        return children;
    }

    public void setChildren( List children )
    {
        this.children = children;
    }
}
