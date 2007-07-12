package org.apache.maven.doxia.macro.snippet;

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

import org.apache.maven.doxia.macro.AbstractMacro;
import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.macro.MacroRequest;
import org.apache.maven.doxia.sink.Sink;
import org.codehaus.plexus.util.StringUtils;

import java.io.IOException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @plexus.component role-hint="snippet"
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
        throws MacroExecutionException
    {
        String id = (String) request.getParameter( "id" );

        required( id, "id" );

        String urlParam = (String) request.getParameter( "url" );

        String fileParam = (String) request.getParameter( "file" );

        URL url;

        if ( !StringUtils.isEmpty( urlParam ) )
        {
            try
            {
                url = new URL( urlParam );
            }
            catch ( MalformedURLException e )
            {
                throw new IllegalArgumentException( urlParam + " is a malformed URL" );
            }
        }
        else if ( !StringUtils.isEmpty( fileParam ) )
        {
            File f = new File( fileParam );

            if ( !f.isAbsolute() )
            {
                f = new File( request.getBasedir(), fileParam );
            }

            try
            {
                url = f.toURL();
            }
            catch ( MalformedURLException e )
            {
                throw new IllegalArgumentException( urlParam + " is a malformed URL" );
            }
        }
        else
        {
            throw new IllegalArgumentException( "Either the 'url' or the 'file' param has to be given." );
        }

        StringBuffer snippet;

        try
        {
            snippet = getSnippet( url, id );
        }
        catch ( IOException e )
        {
            throw new MacroExecutionException( "Error reading snippet", e );
        }

        sink.verbatim( true );

        sink.text( snippet.toString() );

        sink.verbatim_();
    }

    private StringBuffer getSnippet( URL url, String id )
        throws IOException
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
                result.append( "(Fetched from url, cache content " ).append( cache ).append( ")" );
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
