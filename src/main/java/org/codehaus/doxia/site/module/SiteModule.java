package org.codehaus.doxia.site.module;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: SiteModule.java,v 1.4 2004/09/15 21:16:44 jvanzyl Exp $
 */
public interface SiteModule
{
    String ROLE = SiteModule.class.getName();

    String getSourceDirectory();

    String getExtension();

    String getParserId();
}
