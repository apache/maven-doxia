/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.maven.doxia.module.confluence.parser;

import org.apache.maven.doxia.module.common.ByLineSource;
import org.apache.maven.doxia.module.confluence.parser.Block;
import org.apache.maven.doxia.parser.ParseException;

/**
 * Parse a twiki syntax block
 *
 * @author Juan F. Codagnone
 * @since Nov 1, 2005
 */
public interface BlockParser
{

    /**
     * @param line text line
     * @return <code>true</code> if this class can handle this line
     */
    boolean accept( String line );

    /**
     * @param line   a line of text
     * @param source the source of lines
     * @return a block
     * @throws org.apache.maven.doxia.parser.ParseException on error
     */
    Block visit( String line, ByLineSource source ) throws ParseException;
}
