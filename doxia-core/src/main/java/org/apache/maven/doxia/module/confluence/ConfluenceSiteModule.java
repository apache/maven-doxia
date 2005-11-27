/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.maven.doxia.module.confluence;

import org.apache.maven.doxia.site.module.AbstractSiteModule;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:AptSiteModule.java 348605 2005-11-24 12:02:44 +1100 (Thu, 24 Nov 2005) brett $
 * @plexus.component role="org.apache.maven.doxia.site.module.SiteModule"
 * role-hint="confluence"
 */
public class ConfluenceSiteModule
    extends AbstractSiteModule
{
    public String getSourceDirectory()
    {
        return "confluence";
    }

    public String getExtension()
    {
        return "confluence";
    }

    public String getParserId()
    {
        return "confluence";
    }
}
