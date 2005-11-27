/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.maven.doxia.module.confluence.parser;

import org.apache.maven.doxia.module.common.ByLineSource;
import org.apache.maven.doxia.parser.ParseException;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:$
 */
public class SectionBlockParser
    implements BlockParser
{
    public boolean accept( String line )
    {
        if ( line.startsWith( "h1." ) )
        {
            return true;
        }
        else if ( line.startsWith( "h2." ) )
        {
            return true;
        }
        else if ( line.startsWith( "h3." ) )
        {
            return true;
        }
        else if ( line.startsWith( "h4." ) )
        {
            return true;
        }
        else if ( line.startsWith( "h5." ) )
        {
            return true;
        }

        return false;
    }

    public Block visit( String line, ByLineSource source )
        throws ParseException
    {
        int level = Integer.parseInt( Character.toString( line.charAt( 1 ) ) );

        String title = line.substring( 3 );

        return new SectionBlock( title, level );
    }
}
