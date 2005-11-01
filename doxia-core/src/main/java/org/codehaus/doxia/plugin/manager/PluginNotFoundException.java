/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.doxia.plugin.manager;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
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
