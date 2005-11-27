/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.maven.doxia.module.confluence;

import org.apache.maven.doxia.module.apt.AptParseException;public interface ConfluenceSource
{
    String getNextLine()
        throws ConfluenceParseException;

    String getName();

    int getLineNumber();
}

