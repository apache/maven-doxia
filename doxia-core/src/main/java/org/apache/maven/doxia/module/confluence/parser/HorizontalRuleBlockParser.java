/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.maven.doxia.module.confluence.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.doxia.module.common.ByLineSource;
import org.apache.maven.doxia.parser.ParseException;

public class HorizontalRuleBlockParser
    implements BlockParser
{
    public final boolean accept( final String line )
    {
        return line.startsWith( "----" );
    }

    public final Block visit( final String line, final ByLineSource source )
        throws ParseException
    {
        return new HorizontalRuleBlock();
    }
}
