/*
 * Copyright (c) 2001 Pixware. 
 *
 * Author: Hussein Shafie
 *
 * This file is part of the Pixware APT tools.
 * For conditions of distribution and use, see the accompanying legal.txt file.
 */
package org.codehaus.doxia.module.rtf;

public class AlphaNumerals
{
    public static String toString( int n )
    {
        return toString( n, false );
    }

    public static String toString( int n, boolean lowerCase )
    {
        StringBuffer alpha = new StringBuffer();
        char zeroLetter = lowerCase ? '`' : '@';

        while ( n > 0 )
        {
            char letter = (char) ( zeroLetter + ( n % 27 ) );
            if ( letter == zeroLetter )
                letter = '0';
            alpha.insert( 0, letter );

            n /= 27;
        }

        return alpha.toString();
    }

    // -----------------------------------------------------------------------

    public static void main( String[] args ) throws NumberFormatException
    {
        for ( int i = 0; i < args.length; ++i )
        {
            String arg = args[i];
            System.out.println( arg + " = " +
                                AlphaNumerals.toString( Integer.parseInt( arg ) ) );
        }
    }
}
