/*
 * CopyrightPlugin (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.doxia.module;

import org.codehaus.doxia.sink.SinkAdapter;
import org.codehaus.doxia.sink.StructureSink;

import java.io.UnsupportedEncodingException;

public class HTMLSink extends SinkAdapter
{
    public static String escapeHTML( String text )
    {
        if ( text == null )
        {
            return "";
        }

        int length = text.length();
        StringBuffer buffer = new StringBuffer( length );

        for ( int i = 0; i < length; ++i )
        {
            char c = text.charAt( i );
            switch ( c )
            {
                case '<':
                    buffer.append( "&lt;" );
                    break;
                case '>':
                    buffer.append( "&gt;" );
                    break;
                case '&':
                    buffer.append( "&amp;" );
                    break;
                case '\"':
                    buffer.append( "&quot;" );
                    break;
                default:
                    buffer.append( c );
            }
        }

        return buffer.toString();
    }

    public static String encodeURL( String text )
    {
        StringBuffer encoded = new StringBuffer();
        int length = text.length();

        char[] unicode = new char[1];

        for ( int i = 0; i < length; ++i )
        {
            char c = text.charAt( i );

            switch ( c )
            {
                case ';':
                case '/':
                case '?':
                case ':':
                case '@':
                case '&':
                case '=':
                case '+':
                case '$':
                case ',':
                case '[':
                case ']': // RFC 2732 (IPV6)
                case '-':
                case '_':
                case '.':
                case '!':
                case '~':
                case '*':
                case '\'':
                case '(':
                case ')':
                case '#':  // XLink mark
                    encoded.append( c );
                    break;
                default:
                    if ( ( c >= 'a' && c <= 'z' ) ||
                        ( c >= 'A' && c <= 'Z' ) ||
                        ( c >= '0' && c <= '9' ) )
                    {
                        encoded.append( c );
                    }
                    else
                    {
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
                            String hex = Integer.toHexString( bytes[j] & 0xFF );

                            encoded.append( '%' );
                            if ( hex.length() == 1 )
                                encoded.append( '0' );
                            encoded.append( hex );
                        }
                    }
            }
        }

        return encoded.toString();
    }

    public static String encodeFragment( String text )
    {
        return encodeURL( StructureSink.linkToKey( text ) );
    }
}
