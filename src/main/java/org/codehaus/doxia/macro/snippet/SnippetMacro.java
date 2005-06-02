package org.codehaus.doxia.macro.snippet;

import org.codehaus.doxia.macro.MacroRequest;
import org.codehaus.doxia.macro.AbstractMacro;
import org.codehaus.doxia.sink.Sink;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @plexus.component
 *   role="org.codehaus.doxia.macro.Macro"
 *   role-hint="snippet"
 */
public class SnippetMacro
    extends AbstractMacro
{
    static final String EOL = System.getProperty( "line.separator" );

    private Map cache = new HashMap();

    private long timeout = 60 * 60 * 1000; // one hour default cache

    private Map timeCached = new HashMap();

    private boolean debug = false;

    public void execute( Sink sink, MacroRequest request )
        throws Exception
    {
        String id = (String) request.getParameter( "id" );

        required( id, "id" );

        String urlParam = (String) request.getParameter( "url" );

        required( urlParam, "url" );

        URL url;

        try
        {
            url = new URL( urlParam );
        }
        catch ( MalformedURLException e )
        {
            throw new IllegalArgumentException( urlParam + " is a malformed URL" );
        }

        String lang = (String) request.getParameter( "lang" );

        StringBuffer snippet = getSnippet( url, id );

        sink.verbatim( true );

        sink.text( snippet.toString() );

        sink.verbatim_();
    }

    StringBuffer getSnippet( URL url, String id ) throws IOException
    {
        StringBuffer result;

        String cachedSnippet = (String) getCachedSnippet( url, id );

        if ( cachedSnippet != null )
        {
            result = new StringBuffer( cachedSnippet );

            if ( debug )
            {
                result.append( "(Served from cache)" );
            }
        }
        else
        {
            result = new SnippetReader( url ).readSnippet( id );

            cacheSnippet( url, id, result.toString() );

            if ( debug )
            {
                result.append( "(Fetched from url, cache content " + cache + ")" );
            }
        }

        return result;
    }

    private Object getCachedSnippet( URL url, String id )
    {
        if ( isCacheTimedout( url, id ) )
        {
            removeFromCache( url, id );
        }
        return cache.get( globalSnippetId( url, id ) );
    }

    boolean isCacheTimedout( URL url, String id )
    {
        return timeInCache( url, id ) >= timeout;
    }

    long timeInCache( URL url, String id )
    {
        return System.currentTimeMillis() - getTimeCached( url, id );
    }

    long getTimeCached( URL url, String id )
    {
        String globalId = globalSnippetId( url, id );

        return timeCached.containsKey( globalId ) ? ( (Long) timeCached.get( globalId ) ).longValue() : 0;
    }

    private void removeFromCache( URL url, String id )
    {
        String globalId = globalSnippetId( url, id );

        timeCached.remove( globalId );

        cache.remove( globalId );
    }

    private String globalSnippetId( URL url, String id )
    {
        return url + " " + id;
    }

    private void required( String id, String param )
    {
        if ( id == null || "".equals( id ) )
        {
            throw new IllegalArgumentException( param + " is a required parameter" );
        }
    }

    public void cacheSnippet( URL url, String id, String content )
    {
        cache.put( globalSnippetId( url, id ), content );

        timeCached.put( globalSnippetId( url, id ), new Long( System.currentTimeMillis() ) );
    }

    public void setCacheTimeout( int timeout )
    {
        this.timeout = timeout;
    }
}
