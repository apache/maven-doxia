/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.doxia.macro;

import org.codehaus.doxia.sink.Sink;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: Macro.java,v 1.3 2004/09/15 15:07:55 jvanzyl Exp $
 */
public interface Macro
{
    String ROLE = Macro.class.getName();

    void execute( Sink sink, MacroRequest request )
        throws Exception;

}
