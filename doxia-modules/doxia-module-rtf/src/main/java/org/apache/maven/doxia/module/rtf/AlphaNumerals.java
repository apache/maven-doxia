package org.apache.maven.doxia.module.rtf;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * @version $Id$
 */
class AlphaNumerals
{
    static String toString( int n )
    {
        return toString( n, false );
    }

    static String toString( int n, boolean lowerCase )
    {
        StringBuffer alpha = new StringBuffer();
        char zeroLetter = lowerCase ? '`' : '@';

        while ( n > 0 )
        {
            char letter = (char) ( zeroLetter + ( n % 27 ) );
            if ( letter == zeroLetter )
            {
                letter = '0';
            }
            alpha.insert( 0, letter );

            n /= 27;
        }

        return alpha.toString();
    }

    // -----------------------------------------------------------------------

    public static void main( String[] args )
        throws NumberFormatException
    {
        for ( int i = 0; i < args.length; ++i )
        {
            String arg = args[i];
            System.out.println( arg + " = " + AlphaNumerals.toString( Integer.parseInt( arg ) ) );
        }
    }
}
