package org.apache.maven.doxia.util;

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
 * A collection of utility functions (static methods) operating on Strings and
 * on lists of Strings. (What is called a list of Strings here is simply an
 * array of Strings.)
 */
public class StringUtil
{
    /**
     * The empty list of Strings.
     */
    public static final String[] EMPTY_LIST = new String[0];

    /**
     * Searches String <code>string</code> within list <code>strings</code>.
     *
     * @param strings the list to be searched
     * @param string  the String to search for
     * @return the index of the searched string within list or -1 if not found
     */
    public static int indexOf( String[] strings, String string )
    {
        for ( int i = 0; i < strings.length; ++i )
        {
            // string can be null, strings[i] cannot.
            if ( strings[i].equals( string ) )
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Tests if list <code>strings</code> contains String <code>string</code>.
     *
     * @param strings the list to be searched
     * @param string  the String to search for
     * @return <code>true</code> the string is found and <code>false</code>
     *         otherwise
     */
    public static boolean contains( String[] strings, String string )
    {
        for ( int i = 0; i < strings.length; ++i )
        {
            // string can be null, strings[i] cannot.
            if ( strings[i].equals( string ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Inserts a String inside a list of Strings.
     *
     * @param strings the list where a String is to be inserted
     * @param string  the String to insert
     * @param index   the insertion index
     * @return a list containing all the items of list <code>strings</code>
     *         plus String <code>string</code> inserted at position <code>index</code>
     */
    public static String[] insertAt( String[] strings, String string, int index )
    {
        String[] newStrings = new String[strings.length + 1];

        if ( index > 0 )
        {
            System.arraycopy( strings, 0, newStrings, 0, index );
        }

        int tail = strings.length - index;
        if ( tail > 0 )
        {
            System.arraycopy( strings, index, newStrings, index + 1, tail );
        }

        newStrings[index] = string;

        return newStrings;
    }

    /**
     * Inserts a String as first item of a list of Strings.
     *
     * @param strings the list where a String is to be inserted
     * @param string  the String to insert
     * @return a list containing all the items of list <code>strings</code>
     *         plus String <code>string</code> inserted at its beginning
     */
    public static String[] prepend( String[] strings, String string )
    {
        String[] newStrings = new String[strings.length + 1];

        newStrings[0] = string;
        System.arraycopy( strings, 0, newStrings, 1, strings.length );

        return newStrings;
    }

    /**
     * Inserts a String as last item of a list of Strings.
     *
     * @param strings the list where a String is to be inserted
     * @param string  the String to insert
     * @return a list containing all the items of list <code>strings</code>
     *         plus String <code>string</code> inserted at its end
     */
    public static String[] append( String[] strings, String string )
    {
        String[] newStrings = new String[strings.length + 1];

        System.arraycopy( strings, 0, newStrings, 0, strings.length );
        newStrings[strings.length] = string;

        return newStrings;
    }

    /**
     * Removes a String from a list of Strings.
     *
     * @param strings the list where a String is to be removed
     * @param string  the String to remove
     * @return a list containing all the items of list <code>strings</code>
     *         less String <code>string</code> if such String is contained in the
     *         list; the original list otherwise.
     */
    public static String[] remove( String[] strings, String string )
    {
        int index = indexOf( strings, string );
        if ( index < 0 )
        {
            return strings;
        }
        else
        {
            return removeAt( strings, index );
        }
    }

    /**
     * Removes an item specified by its position from a list of Strings.
     *
     * @param strings the list where an item is to be removed
     * @param index   the position of the item to remove
     * @return a list containing all the items of list <code>strings</code>
     *         less the item at position <code>index</code>.
     */
    public static String[] removeAt( String[] strings, int index )
    {
        String[] newStrings = new String[strings.length - 1];

        if ( index > 0 )
        {
            System.arraycopy( strings, 0, newStrings, 0, index );
        }

        int first = index + 1;
        int tail = strings.length - first;
        if ( tail > 0 )
        {
            System.arraycopy( strings, first, newStrings, index, tail );
        }

        return newStrings;
    }

    // -----------------------------------------------------------------------

    /**
     * Like {@link #escape(String)} but puts a double quote character (<tt>'\"'</tt>)
     * around the escaped string.
     *
     * @param string The string to protect.
     * @return The escaped string.
     */
    public static String protect( String string )
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append( '\"' );
        escape( string, buffer );
        buffer.append( '\"' );

        return buffer.toString();
    }

    /**
     * Returns the specified string with all non-ASCII characters and
     * non-printable ASCII characters replaced by the corresponding Java
     * escape sequences (that is <tt>'\n'</tt>, <tt>'\u00E9'</tt>, etc).
     *
     * @param string the String to be escaped
     * @return the specified string with all non-ASCII characters and
     *         non-printable ASCII characters replaced by the corresponding Java
     *         escape sequences
     */
    public static String escape( String string )
    {
        StringBuffer buffer = new StringBuffer();
        escape( string, buffer );
        return buffer.toString();
    }

    /**
     * Dumps the specified string with all non-ASCII characters and
     * non-printable ASCII characters replaced by the corresponding Java
     * escape sequences (that is <tt>'\n'</tt>, <tt>'\u00E9'</tt>, etc),
     * into the given StringBuffer.
     *
     * @param string the String to be escaped.
     * @param buffer the StringBuffer to hold the result.
     */
    private static void escape( String string, StringBuffer buffer )
    {
        int length = string.length();
        for ( int i = 0; i < length; ++i )
        {
            char c = string.charAt( i );

            switch ( c )
            {
                case '\b':
                    buffer.append( '\\' );
                    buffer.append( 'b' );
                    break;
                case '\t':
                    buffer.append( '\\' );
                    buffer.append( 't' );
                    break;
                case '\n':
                    buffer.append( '\\' );
                    buffer.append( 'n' );
                    break;
                case '\f':
                    buffer.append( '\\' );
                    buffer.append( 'f' );
                    break;
                case '\r':
                    buffer.append( '\\' );
                    buffer.append( 'r' );
                    break;
                case '\"':
                    buffer.append( '\\' );
                    buffer.append( '\"' );
                    break;
                case '\'':
                    buffer.append( '\\' );
                    buffer.append( '\'' );
                    break;
                case '\\':
                    buffer.append( '\\' );
                    buffer.append( '\\' );
                    break;
                default:
                    if ( c >= 0x0020 && c <= 0x007E )
                    {
                        buffer.append( c );
                    }
                    else
                    {
                        buffer.append( "\\u" );

                        String hex = Integer.toString( (int) c, 16 );

                        int hexLength = hex.length();
                        while ( hexLength < 4 )
                        {
                            buffer.append( '0' );
                            ++hexLength;
                        }

                        buffer.append( hex );
                    }
            }
        }
    }

    /**
     * Like {@link #unescape(String)} but removes the double quote characters
     * (<tt>'\"'</tt>), if any, before unescaping the string.
     *
     * @param string The string to escape.
     * @return The escaped string.
     */
    public static String unprotect( String string )
    {
        int length = string.length();

        if ( length >= 2 && string.charAt( 0 ) == '\"' && string.charAt( length - 1 ) == '\"' )
        {
            return unescape( string, 1, length - 2 );
        }
        else
        {
            return unescape( string, 0, length );
        }
    }

    /**
     * Returns the specified string with Java escape sequences (that is
     * <tt>'\n'</tt>, <tt>'\u00E9'</tt>, etc) replaced by the corresponding
     * character.
     *
     * @param string the String to be unescaped
     * @return the specified string with Java escape sequences replaced by the
     *         corresponding character
     */
    public static String unescape( String string )
    {
        return unescape( string, 0, string.length() );
    }

    /**
     * Returns the specified string with Java escape sequences (that is
     * <tt>'\n'</tt>, <tt>'\u00E9'</tt>, etc) replaced by the corresponding
     * character.
     *
     * @param string the String to be unescaped
     * @param offset The offset to start with.
     * @param length The length of the string to escape.
     * @return the specified string with Java escape sequences replaced by the
     *         corresponding character
     */
    private static String unescape( String string, int offset, int length )
    {
        StringBuffer buffer = new StringBuffer();

        int end = offset + length;
        for ( int i = offset; i < end; ++i )
        {
            char c = string.charAt( i );

            switch ( c )
            {
                case '\\':
                    if ( i + 1 == end )
                    {
                        buffer.append( c );
                    }
                    else
                    {
                        switch ( string.charAt( i + 1 ) )
                        {
                            case 'b':
                                buffer.append( '\b' );
                                ++i;
                                break;
                            case 't':
                                buffer.append( '\t' );
                                ++i;
                                break;
                            case 'n':
                                buffer.append( '\n' );
                                ++i;
                                break;
                            case 'f':
                                buffer.append( '\f' );
                                ++i;
                                break;
                            case 'r':
                                buffer.append( '\r' );
                                ++i;
                                break;
                            case '\"':
                                buffer.append( '\"' );
                                ++i;
                                break;
                            case '\'':
                                buffer.append( '\'' );
                                ++i;
                                break;
                            case '\\':
                                buffer.append( '\\' );
                                ++i;
                                break;
                            case 'u':
                                if ( i + 5 < end )
                                {
                                    try
                                    {
                                        int escaped = Integer.parseInt( string.substring( i + 2, i + 6 ), 16 );
                                        buffer.append( (char) escaped );
                                        i += 5;
                                    }
                                    catch ( NumberFormatException ignore )
                                    {
                                        buffer.append( c );
                                    }
                                }
                                break;
                            default:
                                buffer.append( c );
                        }
                    }
                    break;
                default:
                    buffer.append( c );
            }
        }

        return buffer.toString();
    }

    /**
     * A simple test for {@link #escape(String)} and {@link #unescape(String)}.
     *
     * @param args An array of Strings to test.
     */
    public static final void main( String[] args )
    {
        for ( int i = 0; i < args.length; ++i )
        {
            String arg = args[i];
            String arg2 = protect( arg );

            System.out.print( "'" );
            System.out.print( arg );
            System.out.print( "' = " );
            System.out.print( arg2 );
            System.out.print( " = " );
            System.out.println( unprotect( arg2 ) );
        }
    }

    // -----------------------------------------------------------------------

    /**
     * Returns the specified string with its first character converted to
     * upper case.
     *
     * @param string the String to be processed
     * @return the specified string with its first character converted to
     *         upper case
     */
    public static String capitalize( String string )
    {
        int length = string.length();

        if ( length == 0 )
        {
            return string;
        }
        else if ( length == 1 )
        {
            return string.toUpperCase();
        }
        else
        {
            return ( Character.toUpperCase( string.charAt( 0 ) ) + string.substring( 1 ) );
        }
    }

    /**
     * Returns the specified string with its first character converted to
     * lower case.
     *
     * @param string the String to be processed
     * @return the specified string with its first character converted to
     *         lower case
     */
    public static String uncapitalize( String string )
    {
        int length = string.length();

        if ( length == 0 )
        {
            return string;
        }
        else if ( length == 1 )
        {
            return string.toLowerCase();
        }
        else
        {
            return ( Character.toLowerCase( string.charAt( 0 ) ) + string.substring( 1 ) );
        }
    }

    // -----------------------------------------------------------------------

    /**
     * Splits String <code>string</code> at occurences of char
     * <code>separatorChar</code>.
     *
     * @param string        the String to be split
     * @param separatorChar the char where to split
     * @return the list of substrings resulting from splitting String
     *         <code>string</code> at occurences of char <code>separatorChar</code>
     *         <p>Note that each occurence of <code>separatorChar</code> specifies the
     *         end of a substring. Therefore, the returned list may contain empty
     *         substrings if consecutive <code>separatorChar</code>s are found in
     *         String <code>string</code>.
     */
    public static String[] split( String string, char separatorChar )
    {
        // Count elements ---

        int elementCount = 0;
        int sep = 0;
        while ( ( sep = string.indexOf( separatorChar, sep ) ) >= 0 )
        {
            ++elementCount;
            ++sep;
        }
        ++elementCount;

        // Build element array ---

        String[] elements = new String[elementCount];

        elementCount = 0;
        sep = 0;
        int nextSep;
        while ( ( nextSep = string.indexOf( separatorChar, sep ) ) >= 0 )
        {
            elements[elementCount++] = ( sep == nextSep ) ? "" : string.substring( sep, nextSep );
            sep = nextSep + 1;
        }
        elements[elementCount++] = string.substring( sep );

        return elements;
    }

    /**
     * Joins the items of the specified list of Strings using specified
     * separator char.
     *
     * @param strings       the list where items are to be joined
     * @param separatorChar the char used to join items
     * @return a string where all list items have been joined
     */
    public static String join( String[] strings, char separatorChar )
    {
        StringBuffer buffer = new StringBuffer();

        int stringCount = strings.length;
        if ( stringCount > 0 )
        {
            buffer.append( strings[0] );
            for ( int i = 1; i < stringCount; ++i )
            {
                buffer.append( separatorChar );
                buffer.append( strings[i] );
            }
        }

        return buffer.toString();
    }

    /**
     * Joins the items of the specified list of Strings using specified
     * separator String.
     *
     * @param strings   the list where items are to be joined
     * @param separator the String used to join items
     * @return a string where all list items have been joined
     */
    public static String join( String[] strings, String separator )
    {
        StringBuffer buffer = new StringBuffer();

        int stringCount = strings.length;
        if ( stringCount > 0 )
        {
            buffer.append( strings[0] );
            for ( int i = 1; i < stringCount; ++i )
            {
                buffer.append( separator );
                buffer.append( strings[i] );
            }
        }

        return buffer.toString();
    }

    // -----------------------------------------------------------------------

    /**
     * Replaces substring <code>oldSub</code> by substring <code>newSub</code>
     * inside String <code>string</code>.
     *
     * @param string the String where replacements are to be performed
     * @param oldSub the substring to replace
     * @param newSub the replacement substring
     * @return a string where all replacements have been performed
     * @see String#replaceAll(String, String)
     */
    public static String replaceAll( String string, String oldSub, String newSub )
    {
        StringBuffer replaced = new StringBuffer();
        int oldSubLength = oldSub.length();
        int begin, end;

        begin = 0;
        while ( ( end = string.indexOf( oldSub, begin ) ) >= 0 )
        {
            if ( end > begin )
            {
                replaced.append( string.substring( begin, end ) );
            }
            replaced.append( newSub );
            begin = end + oldSubLength;
        }
        if ( begin < string.length() )
        {
            replaced.append( string.substring( begin ) );
        }

        return replaced.toString();
    }
}
