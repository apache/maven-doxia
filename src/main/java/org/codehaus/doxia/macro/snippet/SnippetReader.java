package org.codehaus.doxia.macro.snippet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SnippetReader
{
    private static final String EOL = System.getProperty( "line.separator" );

    private URL source;

    public SnippetReader( URL source )
    {
        this.source = source;
    }

    public StringBuffer readSnippet( String snippetId ) throws IOException
    {
        List lines = readLines( snippetId );
        int minIndent = minIndent( lines );
        StringBuffer result = new StringBuffer();
        for ( Iterator iterator = lines.iterator(); iterator.hasNext(); )
        {
            String line = (String) iterator.next();
            result.append( line.substring( minIndent ) );
            result.append( EOL );
        }
        return result;
    }

    int minIndent( List lines )
    {
        int minIndent = Integer.MAX_VALUE;
        for ( Iterator iterator = lines.iterator(); iterator.hasNext(); )
        {
            String line = (String) iterator.next();
            minIndent = Math.min( minIndent, indent( line ) );
        }
        return minIndent;
    }

    int indent( String line )
    {
        char[] chars = line.toCharArray();
        int indent = 0;
        for ( ; indent < chars.length; indent++ )
        {
            if ( chars[indent] != ' ' )
            {
                break;
            }
        }
        return indent;
    }

    private List readLines( String snippetId ) throws IOException
    {
        BufferedReader reader = new BufferedReader( new InputStreamReader( source.openStream() ) );
        List lines = new ArrayList();
        try
        {
            boolean capture = false;
            String line;
            while ( ( line = reader.readLine() ) != null )
            {
                if ( isStart( snippetId, line ) )
                {
                    capture = true;
                }
                else if ( isEnd( snippetId, line ) )
                {
                    break;
                }
                else if ( capture )
                {
                    lines.add( line );
                }
            }
        }
        finally
        {
            reader.close();
        }
        return lines;
    }

    protected boolean isStart( String snippetId, String line )
    {
        return isDemarcator( snippetId, "START", line );
    }

    protected boolean isDemarcator( String snippetId, String what, String line )
    {
        String upper = line.toUpperCase();
        return upper.indexOf( what.toUpperCase() ) != -1 &&
            upper.indexOf( "SNIPPET" ) != -1 && line.indexOf( snippetId ) != -1;
    }

    protected boolean isEnd( String snippetId, String line )
    {
        return isDemarcator( snippetId, "END", line );
    }
}
