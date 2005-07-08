/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.doxia.macro;

import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class MacroRequest
{
    private Map parameters;

    public Map getParameters()
    {
        return parameters;
    }

    public MacroRequest( Map parameters )
    {
        this.parameters = parameters;
    }

    public Object getParameter( String key )
    {
        return parameters.get( key );
    }
}
