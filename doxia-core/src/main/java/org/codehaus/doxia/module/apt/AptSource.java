/*
 * Copyright (c) 1999,2000 Pixware. 
 *
 * Author: Hussein Shafie
 *
 * This file is part of the Pixware APT tools.
 * For conditions of distribution and use, see the accompanying legal.txt file.
 */
package org.codehaus.doxia.module.apt;

public interface AptSource
{
    String getNextLine()
        throws AptParseException;

    String getName();

    int getLineNumber();
}

