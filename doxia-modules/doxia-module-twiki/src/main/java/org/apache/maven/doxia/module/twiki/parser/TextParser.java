package org.apache.maven.doxia.module.twiki.parser;

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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parse almost plain text in search of WikiWords, links, ...
 *
 * @author Juan F. Codagnone
 * @since Nov 4, 2005
 */
public class TextParser
{
    /**
     * pattern to detect WikiWords
     */
    private static final Pattern WIKIWORD_PATTERN =
        Pattern.compile( "(!?([A-Z]\\w*[.])?([A-Z][a-z]+){2,}(#\\w*)?)" );
    /**
     * pattern to detect SpecificLinks links [[reference][text]]
     */
    private static final Pattern SPECIFICLINK_PATTERN =
        Pattern.compile( "!?\\[\\[(.+)\\]\\[(.+)\\]\\]" );
    /**
     * pattern to detect ForcedLinks links [[reference asd]]
     */
    private static final Pattern FORCEDLINK_PATTERN =
        Pattern.compile( "(!)?(\\[\\[(.+)\\]\\])" );
    /**
     * anchor name
     */
    private static final Pattern ANCHOR_PATTERN =
        Pattern.compile( "#(([A-Z][A-Za-z]*){2,})" );
    /**
     * url word
     */
    private static final Pattern URL_PATTERN =
        Pattern.compile( "(\\w+):[/][/][^\\s]*" );

    /**
     * @param line line to parse
     * @return a list of block that represents the input
     */
    public final List parse( final String line )
    {
        final List ret = new ArrayList();

        final Matcher linkMatcher = SPECIFICLINK_PATTERN.matcher( line );
        final Matcher wikiMatcher = WIKIWORD_PATTERN.matcher( line );
        final Matcher forcedLinkMatcher = FORCEDLINK_PATTERN.matcher( line );
        final Matcher anchorMatcher = ANCHOR_PATTERN.matcher( line );
        final Matcher urlMatcher = URL_PATTERN.matcher( line );

        if ( linkMatcher.find() )
        {
            parseLink( line, ret, linkMatcher );
        }
        else if ( wikiMatcher.find() && startLikeWord( wikiMatcher, line ) )
        {
            parseWiki( line, ret, wikiMatcher );
        }
        else if ( forcedLinkMatcher.find() )
        {
            parseForcedLink( line, ret, forcedLinkMatcher );
        }
        else if ( anchorMatcher.find() && isAWord( anchorMatcher, line ) )
        {
            parseAnchor( line, ret, anchorMatcher );
        }
        else if ( urlMatcher.find() && isAWord( urlMatcher, line ) )
        {
            parseUrl( line, ret, urlMatcher );
        }
        else
        {
            if ( line.length() != 0 )
            {
                ret.add( new TextBlock( line ) );
            }
        }

        return ret;
    }

    private void parseUrl( final String line, final List ret,
                           final Matcher urlMatcher )
    {
        ret.addAll( parse( line.substring( 0, urlMatcher.start() ) ) );
        final String url = urlMatcher.group( 0 );
        ret.add( new LinkBlock( url, url ) );
        ret.addAll( parse( line.substring( urlMatcher.end(), line.length() ) ) );
    }

    private void parseAnchor( final String line, final List ret,
                              final Matcher anchorMatcher )
    {
        ret.addAll( parse( line.substring( 0, anchorMatcher.start() ) ) );
        ret.add( new AnchorBlock( anchorMatcher.group( 1 ) ) );
        ret.addAll( parse( line.substring( anchorMatcher.end(), line.length() ) ) );
    }

    private void parseForcedLink( final String line, final List ret,
                                  final Matcher forcedLinkMatcher )
    {
        if ( forcedLinkMatcher.group( 1 ) != null )
        {
            ret.add( new TextBlock( forcedLinkMatcher.group( 2 ) ) );
        }
        else
        {
            final String showText = forcedLinkMatcher.group( 3 );
            // mailto link:
            if ( showText.trim().startsWith( "mailto:" ) )
            {
                String s = showText.trim();
                int i = s.indexOf( ' ' );
                if ( i == -1 )
                {
                    ret.add( new TextBlock( s ) );
                }
                else
                {
                    ret.add( new LinkBlock( s.substring( 0, i ),
                                            s.substring( i ).trim() ) );
                }
            }
            else
            {
                final StringTokenizer tokenizer =
                    new StringTokenizer( showText );
                final StringBuffer sb = new StringBuffer();

                while ( tokenizer.hasMoreElements() )
                {
                    final String s = tokenizer.nextToken();
                    sb.append( s.substring( 0, 1 ).toUpperCase() );
                    sb.append( s.substring( 1 ) );
                }

                ret.addAll( parse( line.substring( 0, forcedLinkMatcher.start() ) ) );
                ret.add( new WikiWordBlock( sb.toString(), showText ) );
                ret.addAll( parse( line.substring( forcedLinkMatcher.end(), line.length() ) ) );
            }
        }
    }

    private void parseWiki( final String line, final List ret,
                            final Matcher wikiMatcher )
    {
        final String wikiWord = wikiMatcher.group();
        ret.addAll( parse( line.substring( 0, wikiMatcher.start() ) ) );
        if ( wikiWord.startsWith( "!" ) )
        { // link prevention
            ret.add( new TextBlock( wikiWord.substring( 1 ) ) );
        }
        else
        {
            ret.add( new WikiWordBlock( wikiWord ) );
        }
        ret.addAll( parse( line.substring( wikiMatcher.end(), line.length() ) ) );
    }


    private void parseLink( final String line, final List ret,
                            final Matcher linkMatcher )
    {
        ret.addAll( parse( line.substring( 0, linkMatcher.start() ) ) );
        if ( line.charAt( linkMatcher.start() ) == '!' )
        {
            ret.add( new TextBlock( line.substring( linkMatcher.start() + 1,
                                                    linkMatcher.end() ) ) );
        }
        else
        {
            ret.add( new LinkBlock( linkMatcher.group( 1 ),
                                    linkMatcher.group( 2 ) ) );
        }
        ret.addAll( parse( line.substring( linkMatcher.end(), line.length() ) ) );
    }

    /**
     * @param m    matcher to test
     * @param line line to test
     * @return <code>true</code> if the match on m represent a word (must be
     *         a space before the word or must be the begining of the line)
     */
    private boolean isAWord( final Matcher m, final String line )
    {
        return startLikeWord( m, line ) && endLikeWord( m, line );
    }

    private boolean startLikeWord( final Matcher m, final String line )
    {
        final int start = m.start();

        boolean ret = false;
        if ( start == 0 )
        {
            ret = true;
        }
        else if ( start > 0 )
        {
            if ( isSpace( line.charAt( start - 1 ) ) )
            {
                ret = true;
            }
        }

        return ret;
    }

    private boolean endLikeWord( final Matcher m, final String line )
    {
        final int end = m.end();

        boolean ret = true;
        if ( end < line.length() )
        {
            ret = isSpace( line.charAt( end ) );
        }

        return ret;
    }

    /**
     * @param c char to test
     * @return <code>true</code> if c is a space char
     */
    private boolean isSpace( final char c )
    {
        return c == ' ' || c == '\t';
    }
}
