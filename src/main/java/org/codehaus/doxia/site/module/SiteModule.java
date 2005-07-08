package org.codehaus.doxia.site.module;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface SiteModule
{
    String ROLE = SiteModule.class.getName();

    String getSourceDirectory();

    String getExtension();

    String getParserId();
}
