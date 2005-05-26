package org.codehaus.doxia;

import org.codehaus.doxia.site.renderer.SiteRenderer;
import org.codehaus.plexus.PlexusTestCase;

import java.io.File;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: SiteRenderingTest.java,v 1.6 2004/09/15 15:07:56 jvanzyl Exp $
 */
public class SiteRenderingTest
    extends PlexusTestCase
{
    public void testApt()
        throws Exception
    {
        SiteRenderer r = (SiteRenderer) lookup( SiteRenderer.ROLE );

        File siteDirectory = new File( getBasedir(), "src/test/site" );

        File generatedSiteDirectory = new File( getBasedir(), "src/test/site-generated" );

        File outputDirectory = new File( getBasedir(), "target/site" );

        if ( !outputDirectory.exists() )
        {
            outputDirectory.mkdirs();
        }

        File resourcesDirectory = new File( siteDirectory, "resources" );

        r.render( siteDirectory.getPath(), generatedSiteDirectory.getPath(), outputDirectory.getPath(),
                  resourcesDirectory );
    }
}
