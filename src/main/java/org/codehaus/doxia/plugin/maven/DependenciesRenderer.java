package org.codehaus.doxia.plugin.maven;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.codehaus.doxia.sink.Sink;

import java.util.Iterator;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: DependenciesRenderer.java,v 1.4 2004/09/15 15:07:56 jvanzyl Exp $
 */
public class DependenciesRenderer
    extends AbstractRenderer
{
    private Model model;

    public DependenciesRenderer( Sink sink, Model model )
    {
        super( sink );

        this.model = model;
    }

    // How to i18n these ...
    public String getTitle()
    {
        return "Project Dependencies";
    }

    public void renderBody()
    {
        section( getTitle() );

        startTable();

        tableHeader( new String[]{"GroupId", "ArtifactId", "Version"} );

        for ( Iterator i = model.getDependencies().iterator(); i.hasNext(); )
        {
            Dependency d = (Dependency) i.next();

            tableRow( new String[]{d.getGroupId(), d.getArtifactId(), d.getVersion()} );
        }

        endTable();
    }
}
