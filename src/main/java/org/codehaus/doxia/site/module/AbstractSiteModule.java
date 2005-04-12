package org.codehaus.doxia.site.module;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: AbstractSiteModule.java,v 1.6 2004/11/02 05:00:40 jvanzyl Exp $
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
