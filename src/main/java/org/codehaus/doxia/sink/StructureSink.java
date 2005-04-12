/*
 * CopyrightPlugin (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.doxia.sink;

import java.io.File;

public class StructureSink
{

    // -----------------------------------------------------------------------

    public static boolean isExternalLink( String text )
    {
        text = text.toLowerCase();
        return ( text.indexOf( "http:/" ) == 0 ||
            text.indexOf( "https:/" ) == 0 ||
            text.indexOf( "ftp:/" ) == 0 ||
            text.indexOf( "mailto:" ) == 0 ||
            text.indexOf( "file:/" ) == 0 ||
            text.indexOf( ".." + File.separatorChar ) == 0 ||
            text.indexOf( "." + File.separatorChar ) == 0 );
    }

    public static String linkToKey( String text )
    {
        int length = text.length();
        StringBuffer buffer = new StringBuffer( length );

        for ( int i = 0; i < length; ++i )
        {
            char c = text.charAt( i );
            if ( Character.isLetterOrDigit( c ) )
                buffer.append( Character.toLowerCase( c ) );
        }

        return buffer.toString();
    }
}
