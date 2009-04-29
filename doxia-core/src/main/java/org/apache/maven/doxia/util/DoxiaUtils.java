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

import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * General Doxia utility methods. The methods in this class should not assume
 * any specific Doxia module or document format.
 *
 * @author ltheussl
 * @since 1.1
 * @version $Id$
 */
public class DoxiaUtils
{

    private static final int MINUS_ONE = 0xFF;

    /**
     * Checks if the given string corresponds to an internal link,
     * ie it is a link to an anchor within the same document.
     *
     * @param link The link to check.
     * @return True if the link starts with "#".
     *
     * @see #isExternalLink(String)
     * @see #isLocalLink(String)
     */
    public static boolean isInternalLink( String link )
    {
        return link.startsWith( "#" );
    }

    /**
     * Checks if the given string corresponds to an external URI,
     * ie is not a link within the same document nor a relative link
     * to another document (a local link) of the same site.
     *
     * @param link The link to check.
     * @return True if the link (ignoring case) starts with either "http:/",
     * "https:/", "ftp:/", "mailto:", "file:/", or contains the string "://".
     * Note that Windows style separators "\" are not allowed
     * for URIs, see  http://www.ietf.org/rfc/rfc2396.txt , section 2.4.3.
     *
     * @see #isInternalLink(String)
     * @see #isLocalLink(String)
     */
    public static boolean isExternalLink( String link )
    {
        String text = link.toLowerCase( Locale.ENGLISH );

        return ( text.indexOf( "http:/" ) == 0 || text.indexOf( "https:/" ) == 0
            || text.indexOf( "ftp:/" ) == 0 || text.indexOf( "mailto:" ) == 0
            || text.indexOf( "file:/" ) == 0 || text.indexOf( "://" ) != -1 );
    }

    /**
     * Checks if the given string corresponds to a relative link to another document
     * within the same site, ie it is neither an {@link #isInternalLink(String) internal}
     * nor an {@link #isExternalLink(String) external} link.
     *
     * @param link The link to check.
     * @return True if the link is neither an external nor an internal link.
     *
     * @see #isExternalLink(String)
     * @see #isInternalLink(String)
     */
    public static boolean isLocalLink( String link )
    {
        return ( !isExternalLink( link ) && !isInternalLink( link ) );
    }

    /**
     * Construct a valid Doxia id.
     *
     * <p>
     *   This method is equivalent to {@link #encodeId(java.lang.String, boolean) encodeId( id, false )}.
     * </p>
     *
     * @param id The id to be encoded.
     * @return The trimmed and encoded id, or null if id is null.
     * @see #encodeId(java.lang.String, boolean)
     */
    public static String encodeId( String id )
    {
        return encodeId( id, false );
    }

    /**
     * Construct a valid Doxia id.
     *
     * <p>
     *   A valid Doxia id obeys the same constraints as an HTML ID or NAME token.
     *   According to the <a href="http://www.w3.org/TR/html4/types.html#type-name">
     *   HTML 4.01 specification section 6.2 SGML basic types</a>:
     * </p>
     * <p>
     *   <i>ID and NAME tokens must begin with a letter ([A-Za-z]) and may be
     *   followed by any number of letters, digits ([0-9]), hyphens ("-"),
     *   underscores ("_"), colons (":"), and periods (".").</i>
     * </p>
     * <p>
     *   According to <a href="http://www.w3.org/TR/xhtml1/#C_8">XHTML 1.0
     *   section C.8. Fragment Identifiers</a>:
     * </p>
     * <p>
     *   <i>When defining fragment identifiers to be backward-compatible, only
     *   strings matching the pattern [A-Za-z][A-Za-z0-9:_.-]* should be used.</i>
     * </p>
     * <p>
     *   To achieve this we need to convert the <i>id</i> String. Two conversions
     *   are necessary and one is done to get prettier ids:
     * </p>
     * <ol>
     *   <li>Remove whitespace at the start and end before starting to process</li>
     *   <li>If the first character is not a letter, prepend the id with the letter 'a'</li>
     *   <li>Any spaces are replaced with an underscore '_'</li>
     *   <li>
     *     Any characters not matching the above pattern are either dropped,
     *     or replaced according to the rules specified in the
     *     <a href="http://www.w3.org/TR/html4/appendix/notes.html#non-ascii-chars">HTML specs</a>.
     *   </li>
     * </ol>
     * <p>
     *   For letters, the case is preserved in the conversion.
     * </p>
     *
     * <p>
     * Here are some examples:
     * </p>
     * <pre>
     * DoxiaUtils.encodeId( null )        = null
     * DoxiaUtils.encodeId( "" )          = ""
     * DoxiaUtils.encodeId( " _ " )       = "a_"
     * DoxiaUtils.encodeId( "1" )         = "a1"
     * DoxiaUtils.encodeId( "1anchor" )   = "a1anchor"
     * DoxiaUtils.encodeId( "_anchor" )   = "a_anchor"
     * DoxiaUtils.encodeId( "a b-c123 " ) = "a_b-c123"
     * DoxiaUtils.encodeId( "   anchor" ) = "anchor"
     * DoxiaUtils.encodeId( "myAnchor" )  = "myAnchor"
     * </pre>
     *
     * @param id The id to be encoded.
     * @param chop true if non-ASCII characters should be ignored.
     * If false, any non-ASCII characters will be replaced as specified above.
     * @return The trimmed and encoded id, or null if id is null.
     *
     * @since 1.1.1
     */
    public static String encodeId( String id, boolean chop )
    {
        if ( id == null )
        {
            return null;
        }

        id = id.trim();
        int length = id.length();
        StringBuffer buffer = new StringBuffer( length );

        for ( int i = 0; i < length; ++i )
        {
            char c = id.charAt( i );

            if ( ( i == 0 ) && ( !isAciiLetter( c ) ) )
            {
                buffer.append( "a" );
            }

            if ( c == ' ' )
            {
                buffer.append( "_" );
            }
            else if ( isAciiLetter( c ) || isAciiDigit( c ) || ( c == '-' ) || ( c == '_' ) || ( c == ':' )
                            || ( c == '.' ) )
            {
                buffer.append( c );
            }
            else if ( !chop )
            {
                char[] unicode = new char[1];
                byte[] bytes;

                try
                {
                    unicode[0] = c;
                    bytes = ( new String( unicode, 0, 1 ) ).getBytes( "UTF8" );
                }
                catch ( UnsupportedEncodingException cannotHappen )
                {
                    bytes = new byte[0];
                }

                for ( int j = 0; j < bytes.length; ++j )
                {
                    String hex = byteToHex( bytes[j] );

                    buffer.append( '%' );

                    if ( hex.length() == 1 )
                    {
                        buffer.append( '0' );
                    }

                    buffer.append( hex );
                }
            }
        }

        return buffer.toString();
    }

    /**
     * Convert a byte to it's hexadecimal equivalent.
     *
     * @param b the byte value.
     * @return the result of Integer.toHexString( b & 0xFF ).
     *
     * @since 1.1.1
     */
    public static String byteToHex( byte b )
    {
        return Integer.toHexString( b & MINUS_ONE );
    }

    /**
     * Determines if the specified text is a valid id according to the rules
     * laid out in {@link #encodeId(String)}.
     *
     * @param text The text to be tested.
     * @return <code>true</code> if the text is a valid id, otherwise <code>false</code>.
     * @see #encodeId(String)
     */
    public static boolean isValidId( String text )
    {
        if ( text == null || text.length() == 0 )
        {
            return false;
        }

        for ( int i = 0; i < text.length(); ++i )
        {
            char c = text.charAt( i );

            if ( i == 0 && !isAciiLetter( c ) )
            {
                return false;
            }

            if ( c == ' ' )
            {
                return false;
            }
            else if ( !isAciiLetter( c ) && !isAciiDigit( c ) && c != '-' && c != '_' && c != ':' && c != '.' )
            {
                return false;
            }
        }

        return true;
    }

      //
     // private
    //

    private static boolean isAciiLetter( char c )
    {
        return ( ( c >= 'a' && c <= 'z' ) || ( c >= 'A' && c <= 'Z' ) );
    }

    private static boolean isAciiDigit( char c )
    {
        return ( ( c >= '0' && c <= '9' ) );
    }

    private DoxiaUtils()
    {
        // utility class
    }

}
