package org.codehaus.doxia.module.fml;

import org.codehaus.doxia.site.module.AbstractSiteModule;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id: XdocParser.java,v 1.11 2004/11/02 05:00:40 jvanzyl Exp $
 * 
 * @plexus.component
 *   role="org.codehaus.doxia.site.module.SiteModule"
 *   role-hint="fml"
 */
public class FmlSiteModule
    extends AbstractSiteModule
{

    public String getSourceDirectory()
    {
        return "fml";
    }

    public String getExtension()
    {
        return "fml";
    }

    public String getParserId()
    {
        return "fml";
    }

}
