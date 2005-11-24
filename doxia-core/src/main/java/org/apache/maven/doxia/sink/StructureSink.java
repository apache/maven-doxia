package org.apache.maven.doxia.sink;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;

public class StructureSink
{

    // -----------------------------------------------------------------------

    public static boolean isExternalLink( String text )
    {
        text = text.toLowerCase();
        return ( text.indexOf( "http:/" ) == 0 || text.indexOf( "https:/" ) == 0 || text.indexOf( "ftp:/" ) == 0 ||
            text.indexOf( "mailto:" ) == 0 || text.indexOf( "file:/" ) == 0 ||
            text.indexOf( ".." + File.separatorChar ) == 0 || text.indexOf( "." + File.separatorChar ) == 0 );
    }

    public static String linkToKey( String text )
    {
        int length = text.length();
        StringBuffer buffer = new StringBuffer( length );

        for ( int i = 0; i < length; ++i )
        {
            char c = text.charAt( i );
            if ( Character.isLetterOrDigit( c ) )
            {
                buffer.append( Character.toLowerCase( c ) );
            }
        }

        return buffer.toString();
    }
}
