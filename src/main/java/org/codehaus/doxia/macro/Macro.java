/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.doxia.macro;

import org.codehaus.doxia.sink.Sink;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface Macro
{
    String ROLE = Macro.class.getName();

    void execute( Sink sink, MacroRequest request )
        throws Exception;

}
