/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.doxia.plugin.manager;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: PluginNotFoundException.java,v 1.2 2004/09/15 17:14:29 jvanzyl Exp $
 */
public class PluginNotFoundException
    extends Exception
{
    public PluginNotFoundException( String message )
    {
        super( message );
    }

    public PluginNotFoundException( Throwable cause )
    {
        super( cause );
    }

    public PluginNotFoundException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
