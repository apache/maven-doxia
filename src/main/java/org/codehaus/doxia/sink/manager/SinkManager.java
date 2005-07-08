/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.doxia.sink.manager;

import org.codehaus.doxia.sink.Sink;

import java.util.Collection;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface SinkManager
{
    String ROLE = SinkManager.class.getName();

    Collection getSinks();

    Sink getSink( String id )
        throws SinkNotFoundException;
}
