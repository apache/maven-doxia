package org.codehaus.doxia.site.renderer;

import org.codehaus.doxia.module.xhtml.XhtmlSink;

import java.io.File;
import java.io.InputStream;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface SiteRenderer
{
    String ROLE = SiteRenderer.class.getName();

    void render( String siteDirectory, String generatedSiteDirectory, String outputDirectory,
                 File resourcesDirectory )
        throws Exception;

    void render( String siteDirectory, String generatedSiteDirectory, String outputDirectory, String flavour,
                 File resourcesDirectory )
        throws Exception;

    void render( String siteDirectory, String generatedSiteDirectory, String outputDirectory, String flavour,
                 String siteDescriptorName, File resourcesDirectory )
        throws Exception;

    void render( String siteDirectory, String generatedSiteDirectory, String outputDirectory, String flavour,
                 InputStream siteDescriptor, File resourcesDirectory )
        throws Exception;

    XhtmlSink createSink( File moduleBasedir, String doc, String outputDirectory, File siteDescriptor, String flavour )
        throws Exception;

    XhtmlSink createSink( File moduleBasedir, String doc, String outputDirectory, InputStream siteDescriptor,
                          String flavour )
        throws Exception;

    void copyResources( String outputDirectory, String flavour )
        throws Exception;
}
