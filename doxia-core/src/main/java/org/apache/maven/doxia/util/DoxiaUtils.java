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

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

import javax.imageio.ImageIO;

import javax.swing.text.MutableAttributeSet;

import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;

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
     * If link is not null, then exactly one of the three methods
     * {@link #isInternalLink(java.lang.String)}, {@link #isExternalLink(java.lang.String)} and
     * {@link #isLocalLink(java.lang.String)} will return true.
     *
     * @param link The link to check. Not null.
     * @return True if the link starts with "#".
     *
     * @throws NullPointerException if link is null.
     *
     * @see #isExternalLink(String)
     * @see #isLocalLink(String)
     */
    public static boolean isInternalLink( final String link )
    {
        return link.startsWith( "#" );
    }

    /**
     * Checks if the given string corresponds to an external URI,
     * ie is not a link within the same document nor a relative link
     * to another document (a local link) of the same site.
     * If link is not null, then exactly one of the three methods
     * {@link #isInternalLink(java.lang.String)}, {@link #isExternalLink(java.lang.String)} and
     * {@link #isLocalLink(java.lang.String)} will return true.
     *
     * @param link The link to check. Not null.
     *
     * @return True if the link (ignoring case) starts with either "http:/",
     * "https:/", "ftp:/", "mailto:", "file:/", or contains the string "://".
     * Note that Windows style separators "\" are not allowed
     * for URIs, see  http://www.ietf.org/rfc/rfc2396.txt , section 2.4.3.
     *
     * @throws NullPointerException if link is null.
     *
     * @see #isInternalLink(String)
     * @see #isLocalLink(String)
     */
    public static boolean isExternalLink( final String link )
    {
        String text = link.toLowerCase( Locale.ENGLISH );

        return ( text.startsWith( "http:/" ) || text.startsWith( "https:/" )
            || text.startsWith( "ftp:/" ) || text.startsWith( "mailto:" )
            || text.startsWith( "file:/" ) || text.contains( "://" ) );
    }

    /**
     * Checks if the given string corresponds to a relative link to another document
     * within the same site, ie it is neither an {@link #isInternalLink(String) internal}
     * nor an {@link #isExternalLink(String) external} link.
     * If link is not null, then exactly one of the three methods
     * {@link #isInternalLink(java.lang.String)}, {@link #isExternalLink(java.lang.String)} and
     * {@link #isLocalLink(java.lang.String)} will return true.
     *
     * @param link The link to check. Not null.
     *
     * @return True if the link is neither an external nor an internal link.
     *
     * @throws NullPointerException if link is null.
     *
     * @see #isExternalLink(String)
     * @see #isInternalLink(String)
     */
    public static boolean isLocalLink( final String link )
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
     *      May be null in which case null is returned.
     *
     * @return The trimmed and encoded id, or null if id is null.
     *
     * @see #encodeId(java.lang.String, boolean)
     */
    public static String encodeId( final String id )
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
     * DoxiaUtils.encodeId( "" )          = "a"
     * DoxiaUtils.encodeId( "  " )        = "a"
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
     *      May be null in which case null is returned.
     * @param chop true if non-ASCII characters should be ignored.
     * If false, any non-ASCII characters will be replaced as specified above.
     *
     * @return The trimmed and encoded id, or null if id is null.
     * If id is not null, the return value is guaranteed to be a valid Doxia id.
     *
     * @see #isValidId(java.lang.String)
     *
     * @since 1.1.1
     */
    public static String encodeId( final String id, final boolean chop )
    {
        if ( id == null )
        {
            return null;
        }

        final String idd = id.trim();
        int length = idd.length();

        if ( length == 0 )
        {
            return "a";
        }

        StringBuilder buffer = new StringBuilder( length );

        for ( int i = 0; i < length; ++i )
        {
            char c = idd.charAt( i );

            if ( ( i == 0 ) && ( !isAsciiLetter( c ) ) )
            {
                buffer.append( 'a' );
            }

            if ( c == ' ' )
            {
                buffer.append( '_' );
            }
            else if ( isAsciiLetter( c ) || isAsciiDigit( c ) || ( c == '-' ) || ( c == '_' ) || ( c == ':' )
                            || ( c == '.' ) )
            {
                buffer.append( c );
            }
            else if ( !chop )
            {

                byte[] bytes = String.valueOf( c ).getBytes( StandardCharsets.UTF_8 );

                for ( byte aByte : bytes )
                {
                    buffer.append( '.' );
                    buffer.append( String.format( "%02X", aByte ) );
                }
            }
        }

        return buffer.toString();
    }

    /**
     * Convert a byte to it's hexadecimal equivalent.
     *
     * @param b the byte value.
     * @return the result of Integer.toHexString( b &amp; 0xFF ).
     *
     * @since 1.1.1
     * @deprecated Use {@code String.format( "%02X", bytes[j] )}
     */
    @Deprecated
    public static String byteToHex( final byte b )
    {
        return Integer.toHexString( b & MINUS_ONE );
    }

    /**
     * Determines if the specified text is a valid id according to the rules
     * laid out in {@link #encodeId(String)}.
     *
     * @param text The text to be tested.
     *      May be null in which case false is returned.
     *
     * @return <code>true</code> if the text is a valid id, otherwise <code>false</code>.
     *
     * @see #encodeId(String)
     */
    public static boolean isValidId( final String text )
    {
        if ( text == null || text.length() == 0 )
        {
            return false;
        }

        for ( int i = 0; i < text.length(); ++i )
        {
            char c = text.charAt( i );

            if ( isAsciiLetter( c ) )
            {
                continue;
            }

            if ( ( i == 0 ) || ( c == ' ' ) || ( !isAsciiDigit( c ) && c != '-' && c != '_' && c != ':' && c != '.' ) )
            {
                return false;
            }
        }

        return true;
    }

    private static final SimpleDateFormat DATE_PARSER = new SimpleDateFormat( "", Locale.ENGLISH );
    private static final ParsePosition DATE_PARSE_POSITION = new ParsePosition( 0 );
    private static final String[] DATE_PATTERNS = new String[]
    {
        "yyyy-MM-dd", "yyyy/MM/dd", "yyyyMMdd", "yyyy", "dd.MM.yyyy", "dd MMM yyyy",
        "dd MMM. yyyy", "MMMM yyyy", "MMM. dd, yyyy", "MMM. yyyy", "MMMM dd, yyyy",
        "MMM d, ''yy", "MMM. ''yy", "MMMM ''yy"
    };

    /**
     * <p>Parses a string representing a date by trying different date patterns.</p>
     *
     * <p>The following date patterns are tried (in the given order):</p>
     *
     * <pre>"yyyy-MM-dd", "yyyy/MM/dd", "yyyyMMdd", "yyyy", "dd.MM.yyyy", "dd MMM yyyy",
     *  "dd MMM. yyyy", "MMMM yyyy", "MMM. dd, yyyy", "MMM. yyyy", "MMMM dd, yyyy",
     *  "MMM d, ''yy", "MMM. ''yy", "MMMM ''yy"</pre>
     *
     * <p>A parse is only sucessful if it parses the whole of the input string.
     * If no parse patterns match, a ParseException is thrown.</p>
     *
     * <p>As a special case, the strings <code>"today"</code> and <code>"now"</code>
     * (ignoring case) return the current date.</p>
     *
     * @param str the date to parse, not null.
     *
     * @return the parsed date, or the current date if the input String (ignoring case) was
     *      <code>"today"</code> or <code>"now"</code>.
     *
     * @throws ParseException if no pattern matches.
     * @throws NullPointerException if str is null.
     *
     * @since 1.1.1.
     */
    public static Date parseDate( final String str )
            throws ParseException
    {
        if ( "today".equalsIgnoreCase( str ) || "now".equalsIgnoreCase( str ) )
        {
            return new Date();
        }

        for ( String datePattern : DATE_PATTERNS )
        {
            DATE_PARSER.applyPattern( datePattern );
            DATE_PARSE_POSITION.setIndex( 0 );
            final Date date = DATE_PARSER.parse( str, DATE_PARSE_POSITION );

            if ( date != null && DATE_PARSE_POSITION.getIndex() == str.length() )
            {
                return date;
            }
        }

        throw new ParseException( "Unable to parse date: " + str, -1 );
    }

      //
     // private
    //

    private static boolean isAsciiLetter( final char c )
    {
        return ( ( c >= 'a' && c <= 'z' ) || ( c >= 'A' && c <= 'Z' ) );
    }

    private static boolean isAsciiDigit( final char c )
    {
        return ( c >= '0' && c <= '9' );
    }

    /**
     * Determine width and height of an image. If successful, the returned SinkEventAttributes
     * contain width and height attribute keys whose values are the width and height of the image (as a String).
     *
     * @param logo a String containing either a URL or a path to an image file. Not null.
     *
     * @return a set of SinkEventAttributes, or null if no ImageReader was found to read the image.
     *
     * @throws java.io.IOException if an error occurs during reading.
     * @throws NullPointerException if logo is null.
     *
     * @since 1.1.1
     */
    public static MutableAttributeSet getImageAttributes( final String logo )
            throws IOException
    {
        BufferedImage img = null;

        if ( isExternalLink( logo ) )
        {
            img = ImageIO.read( new URL( logo ) );
        }
        else
        {
            img = ImageIO.read( new File( logo ) );
        }

        if ( img == null )
        {
            return null;
        }

        MutableAttributeSet atts = new SinkEventAttributeSet();
        atts.addAttribute( SinkEventAttributeSet.WIDTH, Integer.toString( img.getWidth() ) );
        atts.addAttribute( SinkEventAttributeSet.HEIGHT, Integer.toString( img.getHeight() ) );
        // add other attributes?

        return atts;
    }

    private DoxiaUtils()
    {
        // utility class
    }
}
