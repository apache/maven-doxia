package org.codehaus.doxia.site.module;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @plexus.component
 */
public abstract class AbstractSiteModule
    implements SiteModule
{
    private String sourceDirectory;

    private String extension;

    public String getSourceDirectory()
    {
        return sourceDirectory;
    }

    public String getExtension()
    {
        return extension;
    }
}
