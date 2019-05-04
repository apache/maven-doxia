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
import org.apache.maven.doxia.macro.Macro;
import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.macro.MacroRequest;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * A macro that prints out the content of a file or a URL.
 *
 * @version $Id$
 */
@Component( role = Macro.class, hint = "snippet" )
public class SnippetMacro
    extends AbstractMacro
{
    /**
     * Holds the cache.
     */
    private static Map<String, String> cache = new HashMap<>();

    private static final int HOUR = 60;

    /**
     * One hour default cache.
     */
    private long timeout = HOUR * HOUR * 1000;

    /**
     * Holds the time cache.
     */
    private static Map<String, Long> timeCached = new HashMap<>();

    /**
     * Debug.
     */
    private boolean debug = false;

    /**
     * in case of Exception during snippet download error will ignored and empty content returned.
     */
    private boolean ignoreDownloadError = true;

    /**
     * {@inheritDoc}
     */
    public void execute( Sink sink, MacroRequest request )
        throws MacroExecutionException
    {
        String id = (String) request.getParameter( "id" );

        String urlParam = (String) request.getParameter( "url" );

        String fileParam = (String) request.getParameter( "file" );

        String debugParam = (String) request.getParameter( "debug" );

        if ( debugParam != null )
        {
            this.debug = Boolean.parseBoolean( debugParam );
        }

        String ignoreDownloadErrorParam = (String) request.getParameter( "ignoreDownloadError" );

        if ( ignoreDownloadErrorParam != null )
        {
            this.ignoreDownloadError = Boolean.parseBoolean( ignoreDownloadErrorParam );
        }

        boolean verbatim = true;

        String verbatimParam = (String) request.getParameter( "verbatim" );

        if ( verbatimParam != null && !"".equals( verbatimParam ) )
        {
            verbatim = Boolean.valueOf( verbatimParam );
        }

        String encoding = (String) request.getParameter( "encoding" );

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
                url = f.toURI().toURL();
            }
            catch ( MalformedURLException e )
            {
                throw new IllegalArgumentException( fileParam + " is a malformed URL" );
            }
        }
        else
        {
            throw new IllegalArgumentException( "Either the 'url' or the 'file' param has to be given." );
        }

        StringBuffer snippet;

        try
        {
            snippet = getSnippet( url, encoding, id );
        }
        catch ( IOException e )
        {
            throw new MacroExecutionException( "Error reading snippet", e );
        }

        if ( verbatim )
        {
            sink.verbatim( SinkEventAttributeSet.BOXED );

            sink.text( snippet.toString() );

            sink.verbatim_();
        }
        else
        {
            sink.rawText( snippet.toString() );
        }
    }

    /**
     * Return a snippet of the given url.
     *
     * @param url The URL to parse.
     * @param encoding The encoding of the URL to parse.
     * @param id  The id of the snippet.
     * @return The snippet.
     * @throws IOException if something goes wrong.
     */
    private StringBuffer getSnippet( URL url, String encoding, String id )
        throws IOException
    {
        StringBuffer result;

        String cachedSnippet = getCachedSnippet( url, id );

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
            try
            {
                result = new SnippetReader( url, encoding ).readSnippet( id );
                cacheSnippet( url, id, result.toString() );
                if ( debug )
                {
                    result.append( "(Fetched from url, cache content " ).append( cache ).append( ")" );
                }
            }
            catch ( IOException e )
            {
                if ( ignoreDownloadError )
                {
                    getLog().debug( "IOException which reading " + url + ": " + e );
                    result =
                        new StringBuffer( "Error during retrieving content skip as ignoreDownloadError activated." );
                }
                else
                {
                    throw e;
                }
            }
        }
        return result;
    }

    /**
     * Return a snippet from the cache.
     *
     * @param url The URL to parse.
     * @param id  The id of the snippet.
     * @return The snippet.
     */
    private String getCachedSnippet( URL url, String id )
    {
        if ( isCacheTimedout( url, id ) )
        {
            removeFromCache( url, id );
        }
        return cache.get( globalSnippetId( url, id ) );
    }

    /**
     * Return true if the snippet has been cached longer than
     * the current timeout.
     *
     * @param url The URL to parse.
     * @param id  The id of the snippet.
     * @return True if timeout exceeded.
     */
    boolean isCacheTimedout( URL url, String id )
    {
        return timeInCache( url, id ) >= timeout;
    }

    /**
     * Return the time the snippet has been cached.
     *
     * @param url The URL to parse.
     * @param id  The id of the snippet.
     * @return The cache time.
     */
    long timeInCache( URL url, String id )
    {
        return System.currentTimeMillis() - getTimeCached( url, id );
    }

    /**
     * Return the absolute value of when the snippet has been cached.
     *
     * @param url The URL to parse.
     * @param id  The id of the snippet.
     * @return The cache time.
     */
    long getTimeCached( URL url, String id )
    {
        String globalId = globalSnippetId( url, id );

        return timeCached.containsKey( globalId ) ? timeCached.get( globalId ) : 0;
    }

    /**
     * Removes the snippet from the cache.
     *
     * @param url The URL to parse.
     * @param id  The id of the snippet.
     */
    private void removeFromCache( URL url, String id )
    {
        String globalId = globalSnippetId( url, id );

        timeCached.remove( globalId );

        cache.remove( globalId );
    }

    /**
     * Return a global identifier for the snippet.
     *
     * @param url The URL to parse.
     * @param id  The id of the snippet.
     * @return An identifier, concatenated url and id,
     *         or just url.toString() if id is empty or null.
     */
    private String globalSnippetId( URL url, String id )
    {
        if ( StringUtils.isEmpty( id ) )
        {
            return url.toString();
        }

        return url + " " + id;
    }

    /**
     * Puts the given snippet into the cache.
     *
     * @param url     The URL to parse.
     * @param id      The id of the snippet.
     * @param content The content of the snippet.
     */
    public void cacheSnippet( URL url, String id, String content )
    {
        cache.put( globalSnippetId( url, id ), content );

        timeCached.put( globalSnippetId( url, id ), System.currentTimeMillis() );
    }

    /**
     * Set the cache timeout.
     *
     * @param time The timeout to set.
     */
    public void setCacheTimeout( int time )
    {
        this.timeout = time;
    }
}
