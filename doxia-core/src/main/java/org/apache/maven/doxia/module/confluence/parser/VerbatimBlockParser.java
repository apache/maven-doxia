/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.maven.doxia.module.confluence.parser;

import org.apache.maven.doxia.module.common.ByLineSource;
import org.apache.maven.doxia.parser.ParseException;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class VerbatimBlockParser
    implements BlockParser
{
    static  String LS = System.getProperty( "line.separator" );

    public boolean accept( String line )
    {
        if ( line.startsWith( "{code}" ) || line.startsWith( "{noformat}" ) )
        {
            return true;
        }

        return false;
    }

    public Block visit( String line, ByLineSource source )
        throws ParseException
    {
        StringBuffer text = new StringBuffer();

        while ( ( line = source.getNextLine() ) != null )
        {
            if ( line.startsWith( "{code}" ) || line.startsWith( "{noformat}" ) )
            {
                break;
            }

            // TODO
            text.append( line ).append( LS );
        }


        return new VerbatimBlock( text.toString() );
    }
}
