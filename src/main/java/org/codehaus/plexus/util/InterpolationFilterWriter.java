package org.codehaus.plexus.util;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;


public class InterpolationFilterWriter
    extends FilterWriter
{
    private int index = 0;

    private String beginToken;

    private int beginTokenLength;

    private StringBuffer queuedData = new StringBuffer();

    private int beginTokenIndex;

    private int endTokenIndex;

    private String endToken;

    private int endTokenLength;

    private static String DEFAULT_BEGIN_TOKEN = "@";

    private static String DEFAULT_END_TOKEN = "@";

    private Map variables;

    public InterpolationFilterWriter( Writer out, Map variables )
    {
        this( out, variables, DEFAULT_BEGIN_TOKEN, DEFAULT_END_TOKEN );
    }

    public InterpolationFilterWriter( Writer out, Map variables, String beginToken, String endToken )
    {
        super( out );

        this.variables = variables;

        this.beginToken = beginToken;

        beginTokenLength = beginToken.length();

        this.endToken = endToken;

        endTokenLength = endToken.length();
    }


    public void write( String s )
        throws IOException
    {
        int length = s.length();

        for ( int i = 0; i < length; i++ )
        {
            write( s.charAt( i ) );
        }
    }

    public void write( char[] buf, int offset, int len )
        throws IOException
    {
        for ( int i = offset; i < len; i++ )
        {
            write( (char) buf[i] );
        }
    }

    public void write( int c )
        throws IOException
    {
        if ( index == 0 && c == beginToken.toCharArray()[beginTokenIndex] )
        {
            if ( beginTokenIndex == ( beginTokenLength - 1 ) )
            {
                beginTokenIndex = 0;
            }
            else
            {
                beginTokenIndex++;
            }

            queuedData.append( (char) c );

            index++;
        }
        else if ( index > 0 && c == endToken.toCharArray()[endTokenIndex] )
        {
            if ( endTokenIndex == ( endTokenLength - 1 ) )
            {
                queuedData.append( (char) c );

                endTokenIndex = 0;

                index = 0;

                interpolate( queuedData.toString() );

                queuedData = new StringBuffer();
            }
            else
            {
                queuedData.append( (char) c );

                endTokenIndex++;

                index++;
            }
        }
        else if ( index > 0 )
        {
            queuedData.append( (char) c );

            index++;
        }
        else
        {
            out.write( c );
        }
    }

    private void interpolate( String s )
        throws IOException
    {
        String key = s.substring( beginTokenLength, s.length() - endTokenLength );

        Object o = variables.get( key );

        if ( key == null )
        {
            out.write( s );
        }
        else
        {
            out.write( o.toString() );
        }
    }
}
