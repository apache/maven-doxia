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
 * @version $Id$
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
    private static final Pattern SPECIFICLINK_PATTERN = Pattern.compile( "!?\\[\\[([^\\]]+)\\]\\[([^\\]]+)\\]\\]" );

    /**
     * pattern to detect ForcedLinks links [[reference asd]]
     */
    private static final Pattern FORCEDLINK_PATTERN = Pattern.compile( "(!)?(\\[\\[(.+)\\]\\])" );

    /**
     * anchor name
     */
    private static final Pattern ANCHOR_PATTERN = Pattern.compile( "#(([A-Z][A-Za-z]*){2,})" );

    /**
     * url word
     */
    private static final Pattern URL_PATTERN = Pattern.compile( "(\\w+):[/][/][^\\s]*" );

    /**
     *  image pattern specification
     */
    private static final Pattern IMAGE_PATTERN = Pattern.compile( "(.*)\\.(png|jpg|gif|bmp)" );

    /**
     *  image tag pattern specification (used for images at relative URLs)
     */
    private static final Pattern IMAGE_TAG_PATTERN =
        Pattern.compile( "<img\\b.*?\\bsrc=([\"'])(.*?)\\1.*>", Pattern.CASE_INSENSITIVE );

    /** HTML tag pattern */
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile( "<(/?)([\\w]*)(.*?)(/?)>", Pattern.DOTALL );

    /**
     * resolves wikiWordLinks
     */
    private final WikiWordLinkResolver wikiWordLinkResolver;

    /** resolves noautolink tag */
    private boolean noautolink;

    /**
     * Creates the TextParser.
     *
     * @param resolver resolver for wikiWord links
     */
    public TextParser( final WikiWordLinkResolver resolver )
    {
        this.wikiWordLinkResolver = resolver;
    }

    /**
     * <p>parse.</p>
     *
     * @param line line to parse
     * @return a list of block that represents the input
     */
    public final List<Block> parse( final String line )
    {
        final List<Block> ret = new ArrayList<>();

        final Matcher linkMatcher = SPECIFICLINK_PATTERN.matcher( line );
        final Matcher wikiMatcher = WIKIWORD_PATTERN.matcher( line );
        final Matcher forcedLinkMatcher = FORCEDLINK_PATTERN.matcher( line );
        final Matcher anchorMatcher = ANCHOR_PATTERN.matcher( line );
        final Matcher urlMatcher = URL_PATTERN.matcher( line );
        final Matcher imageTagMatcher = IMAGE_TAG_PATTERN.matcher( line );

        final Matcher tagMatcher = HTML_TAG_PATTERN.matcher( line );
        Matcher xhtmlMatcher = null;
        if ( tagMatcher.find() )
        {
            String tag = tagMatcher.group( 2 );

            Pattern pattern =
                Pattern.compile( "(\\<" + tag + ".*\\>)(.*)?(\\<\\/" + tag + "\\>)(.*)?", Pattern.DOTALL );
            xhtmlMatcher = pattern.matcher( line );
        }

        if ( xhtmlMatcher != null && xhtmlMatcher.find() )
        {
            parseXHTML( line, ret, xhtmlMatcher );
        }
        else if ( linkMatcher.find() )
        {
            parseLink( line, ret, linkMatcher );
        }
        else if ( wikiMatcher.find() && startLikeWord( wikiMatcher, line ) && !noautolink )
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
        else if ( imageTagMatcher.find() )
        {
            parseImage( line, ret, imageTagMatcher );
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

    /**
     * Parses the image tag
     * @param line the line to parse
     * @param ret where the results live
     * @param imageTagMatcher image tag matcher
     */
    private void parseImage( final String line, final List<Block> ret, final Matcher imageTagMatcher )
    {
        ret.addAll( parse( line.substring( 0, imageTagMatcher.start() ) ) );
        final String src = imageTagMatcher.group( 2 );
        ret.add( new ImageBlock( src ) );
        ret.addAll( parse( line.substring( imageTagMatcher.end() ) ) );
    }

    /**
     * Parses the url
     * @param line the line to parse
     * @param ret where the results live
     * @param urlMatcher url matcher
     */
    private void parseUrl( final String line, final List<Block> ret, final Matcher urlMatcher )
    {
        ret.addAll( parse( line.substring( 0, urlMatcher.start() ) ) );
        final String url = urlMatcher.group( 0 );
        final Matcher imageMatcher = IMAGE_PATTERN.matcher( url );
        if ( imageMatcher.matches() )
        {
            ret.add( new ImageBlock( url ) );
        }
        else
        {
            ret.add( new LinkBlock( url, new TextBlock( url ) ) );
        }
        ret.addAll( parse( line.substring( urlMatcher.end() ) ) );
    }

    /**
     * Parses the anchor
     * @param line the line to parse
     * @param ret where the results live
     * @param anchorMatcher anchor matcher
     */
    private void parseAnchor( final String line, final List<Block> ret, final Matcher anchorMatcher )
    {
        ret.addAll( parse( line.substring( 0, anchorMatcher.start() ) ) );
        ret.add( new AnchorBlock( anchorMatcher.group( 1 ) ) );
        ret.addAll( parse( line.substring( anchorMatcher.end() ) ) );
    }

    /**
     * Parses the link
     * @param line line to parse
     * @param ret where the results live
     * @param forcedLinkMatcher forced link matcher
     */
    private void parseForcedLink( final String line, final List<Block> ret, final Matcher forcedLinkMatcher )
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
                    ret.add( new LinkBlock( s.substring( 0, i ), new TextBlock( s.substring( i ).trim() ) ) );
                }
            }
            else
            {
                ret.addAll( parse( line.substring( 0, forcedLinkMatcher.start() ) ) );
                ret.add( createLink( showText, showText ) );
                ret.addAll( parse( line.substring( forcedLinkMatcher.end() ) ) );
            }
        }
    }

    /**
     * Decides between a WikiWordBlock or a a LinkBlock
     * @param link the link text
     * @param showText the show text.
     * @return either a WikiWordBlock or a LinkBlock
     */
    private Block createLink( final String link, final String showText )
    {
        final Block content;
        if ( URL_PATTERN.matcher( showText ).matches() && IMAGE_PATTERN.matcher( showText ).matches() )
        {
            content = new ImageBlock( showText );
        }
        else
        {
            content = new TextBlock( showText );
        }

        if ( URL_PATTERN.matcher( link ).matches() )
        {
            return new LinkBlock( link, content );
        }

        final StringTokenizer tokenizer = new StringTokenizer( link );
        final StringBuilder sb = new StringBuilder();

        while ( tokenizer.hasMoreElements() )
        {
            final String s = tokenizer.nextToken();
            sb.append( s.substring( 0, 1 ).toUpperCase() );
            sb.append( s.substring( 1 ) );
        }
        return new WikiWordBlock( sb.toString(), content, wikiWordLinkResolver );
    }

    /**
     * Parses a wiki word
     * @param line the line to parse
     * @param ret where the results live
     * @param wikiMatcher wiki matcher
     */
    private void parseWiki( final String line, final List<Block> ret, final Matcher wikiMatcher )
    {
        final String wikiWord = wikiMatcher.group();
        ret.addAll( parse( line.substring( 0, wikiMatcher.start() ) ) );
        if ( wikiWord.startsWith( "!" ) )
        { // link prevention
            ret.add( new TextBlock( wikiWord.substring( 1 ) ) );
        }
        else
        {
            ret.add( new WikiWordBlock( wikiWord, wikiWordLinkResolver ) );
        }
        ret.addAll( parse( line.substring( wikiMatcher.end() ) ) );
    }

    /**
     * Parses a link
     * @param line the line to parse
     * @param ret where the results live
     * @param linkMatcher link matcher
     */
    private void parseLink( final String line, final List<Block> ret, final Matcher linkMatcher )
    {
        ret.addAll( parse( line.substring( 0, linkMatcher.start() ) ) );
        if ( line.charAt( linkMatcher.start() ) == '!' )
        {
            ret.add( new TextBlock( line.substring( linkMatcher.start() + 1, linkMatcher.end() ) ) );
        }
        else
        {
            ret.add( createLink( linkMatcher.group( 1 ), linkMatcher.group( 2 ) ) );
        }
        ret.addAll( parse( line.substring( linkMatcher.end() ) ) );
    }

    /**
     * Parses xhtml.
     *
     * @param line the line to parse
     * @param ret where the results live
     * @param xhtmlMatcher xhtml matcher
     */
    private void parseXHTML( final String line, final List<Block> ret, final Matcher xhtmlMatcher )
    {
        ret.addAll( parse( line.substring( 0, xhtmlMatcher.start() ) ) );
        if ( xhtmlMatcher.group( 1 ).contains( "noautolink" ) )
        {
            noautolink = true;
        }
        else
        {
            ret.add( new XHTMLBlock( xhtmlMatcher.group( 1 ) ) );
        }

        ret.addAll( parse( xhtmlMatcher.group( 2 ) ) );

        if ( xhtmlMatcher.group( 1 ).contains( "noautolink" ) )
        {
            noautolink = false;
        }
        else
        {
            ret.add( new XHTMLBlock( xhtmlMatcher.group( 3 ) ) );
        }

        ret.addAll( parse( xhtmlMatcher.group( 4 ) ) );
    }

    /**
     * @param m    matcher to test
     * @param line line to test
     * @return <code>true</code> if the match on m represent a word (must be
     *         a space before the word or must be the beginning of the line)
     */
    private boolean isAWord( final Matcher m, final String line )
    {
        return startLikeWord( m, line ) && endLikeWord( m, line );
    }

    /**
     * @param m matcher to test
     * @param line line to test
     * @return true if it is the beginning of a word
     */
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

    /**
     * @param m matcher to test
     * @param line line to test
     * @return true if it is the end of a word
     */
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
