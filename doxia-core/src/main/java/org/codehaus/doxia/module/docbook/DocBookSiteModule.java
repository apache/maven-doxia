package org.codehaus.doxia.module.docbook;

import org.codehaus.doxia.site.module.AbstractSiteModule;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 * 
 * @plexus.component
 *   role="org.codehaus.doxia.site.module.SiteModule"
 *   role-hint="doc-book"
 */
public class DocBookSiteModule
    extends AbstractSiteModule
{
    public String getSourceDirectory()
    {
        return "docbook";
    }

    public String getExtension()
    {
        return "xml";
    }

    public String getParserId()
    {
        return "doc-book";
    }
}
