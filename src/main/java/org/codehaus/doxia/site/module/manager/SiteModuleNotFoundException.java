/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.doxia.site.module.manager;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: SiteModuleNotFoundException.java,v 1.1 2004/09/15 01:04:04 jvanzyl Exp $
 */
public class SiteModuleNotFoundException
    extends Exception
{
    public SiteModuleNotFoundException( String message )
    {
        super( message );
    }

    public SiteModuleNotFoundException( Throwable cause )
    {
        super( cause );
    }

    public SiteModuleNotFoundException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
