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

import static org.junit.Assert.assertArrayEquals;

/**
 * tests the WikiWord parsing (and things like that)
 *
 * @author Juan F. Codagnone
 * @since Nov 4, 2005
 */
public class WordsTest
    extends AbstractBlockTestCase
{
    /**
     * used to convert lists to arrays
     */
    private static final Block[] TOARRAY = new Block[] {};

    /**
     * Resolves links for wikiWords
     */
    private final WikiWordLinkResolver resolver = new XHTMLWikiWordLinkResolver();

    /**
     * ...
     */
    public final void testText()
    {
        Block[] blocks, expected;

        expected = new Block[] { new TextBlock( "     Some text    " ) };
        blocks = textParser.parse( "     Some text    " ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );
    }

    /**
     * ...
     */
    public final void testWikiWords()
    {
        Block[] blocks, expected;

        expected = new Block[] { new WikiWordBlock( "WikiWord", resolver ) };
        blocks = textParser.parse( "WikiWord" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );

        // this is not a wiki word
        expected = new Block[] { new TextBlock( "Wiki" ) };
        blocks = textParser.parse( "Wiki" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );

        expected = new Block[] { new TextBlock( "Web." ) };
        blocks = textParser.parse( "Web." ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );

        expected = new Block[] { new TextBlock( "fooWikiBar" ) };
        blocks = textParser.parse( "fooWikiBar" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );

        expected = new Block[] { new WikiWordBlock( "WikiWord", resolver ), new TextBlock( "...." ) };
        blocks = textParser.parse( "WikiWord...." ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );
    }

    /**
     * ...
     */
    public final void testWebWikiWords()
    {
        Block[] blocks, expected;

        expected = new Block[] { new WikiWordBlock( "Web.WikiWord", resolver ) };
        blocks = textParser.parse( "Web.WikiWord" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );

        expected = new Block[] { new WikiWordBlock( "My1Web.WikiWord", resolver ) };
        blocks = textParser.parse( "My1Web.WikiWord" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );
    }

    /**
     * ...
     */
    public final void testWebAnchorWikiWords()
    {
        Block[] blocks, expected;

        expected = new Block[] { new WikiWordBlock( "WikiWord#anchor", resolver ) };
        blocks = textParser.parse( "WikiWord#anchor" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );

        expected = new Block[] { new WikiWordBlock( "MyWeb.WikiWord#anchor", resolver ) };
        blocks = textParser.parse( "MyWeb.WikiWord#anchor" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );

    }

    /**
     * test Specific Links
     */
    public final void testURLSpecificLinks()
    {
        Block[] blocks, expected;

        expected = new Block[] { new LinkBlock( "http://reference.com", new TextBlock( "text" ) ) };
        blocks = textParser.parse( "[[http://reference.com][text]]" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );

        expected =
            new Block[] { new TextBlock( "foo" ),
                new LinkBlock( "http://reference.com", new TextBlock( "text" ) ), new TextBlock( "bar" ) };
        blocks = textParser.parse( "foo[[http://reference.com][text]]bar" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );

        expected =
            new Block[] { new TextBlock( " foo " ),
                new LinkBlock( "http://reference.com", new TextBlock( "text" ) ), new TextBlock( " bar " ) };
        blocks = textParser.parse( " foo [[http://reference.com][text]] bar " ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );

        expected =
            new Block[] {
                new LinkBlock( "http://www.apache.org/licenses/LICENSE-2.0",
                               new TextBlock( "Apache License, version 2.0" ) ),
                new TextBlock( ". You can download it " ),
                new WikiWordBlock( "DoxiaDownload", new TextBlock( "here" ), resolver ) };
        blocks = textParser.parse(
                                    "[[http://www.apache.org/licenses/LICENSE-2.0]"
                                        + "[Apache License, version 2.0]]. You can download it "
                                        + "[[DoxiaDownload][here]]" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );

    }

    /**
     * test Specific Links with wikiWords
     */
    public final void testWikiSpecificLinks()
    {
        Block[] blocks, expected;

        expected = new Block[] { new WikiWordBlock( "Reference", new TextBlock( "text" ), resolver ) };
        blocks = textParser.parse( "[[reference][text]]" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );

        expected =
            new Block[] { new TextBlock( "foo" ),
                new WikiWordBlock( "ReferenceLink", new TextBlock( "text" ), resolver ), new TextBlock( "bar" ) };
        blocks = textParser.parse( "foo[[referenceLink][text]]bar" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );

        expected =
            new Block[] { new TextBlock( " foo " ),
                new WikiWordBlock( "ReferenceLink", new TextBlock( "text" ), resolver ), new TextBlock( " bar " ) };
        blocks = textParser.parse( " foo [[reference link][text]] bar " ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );
    }

    /**
     * test Specific Links
     */
    public final void testSpecificLinkPrevention()
    {
        Block[] blocks, expected;

        expected = new Block[] { new TextBlock( "[[reference][text]]" ) };
        blocks = textParser.parse( "![[reference][text]]" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );
    }

    /**
     * ...
     */
    public final void testPreventLinkingWikiWord()
    {
        Block[] blocks, expected;

        expected = new Block[] { new TextBlock( " " ), new TextBlock( "WikiWord" ), new TextBlock( " " ) };
        blocks = textParser.parse( " !WikiWord " ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );

        expected = new Block[] { new TextBlock( " !!WikiWord " ) };
        blocks = textParser.parse( " !!WikiWord " ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );
    }

    /**
     * ej [[Main.TWiki rules]] would be wikiword Main.TWikiRules
     */
    public final void testForcedLinks()
    {
        Block[] blocks, expected;

        expected = new Block[] { new WikiWordBlock( "WikiSyntax", new TextBlock( "wiki syntax" ), resolver ) };
        blocks = textParser.parse( "[[wiki syntax]]" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );

        expected = new Block[] { new TextBlock( "[[wiki syntax]]" ) };
        blocks = textParser.parse( "![[wiki syntax]]" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );

        expected =
            new Block[] { new TextBlock( "foo" ),
                new WikiWordBlock( "WikiSyntax", new TextBlock( "wiki syntax" ), resolver ),
                new TextBlock( "bar" ) };
        blocks = textParser.parse( "foo[[wiki syntax]]bar" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );

        expected =
            new Block[] { new TextBlock( "foo" ),
                new LinkBlock( "http://twiki.com", new TextBlock( "http://twiki.com" ) ), new TextBlock( "bar" ) };
        blocks = textParser.parse( "foo[[http://twiki.com]]bar" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );
    }

    /**
     * ...
     */
    public final void testMailtoForcedLinks()
    {
        Block[] blocks, expected;

        expected = new Block[] { new LinkBlock( "mailto:a@z.com", new TextBlock( "Mail" ) ) };
        blocks = textParser.parse( "[[mailto:a@z.com Mail]]" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );
    }

    /**
     * ...
     */
    public final void testAnchors()
    {
        Block[] blocks, expected;

        expected = new Block[] { new TextBlock( "mary has #anchor a little lamb" ) };
        blocks = textParser.parse( "mary has #anchor a little lamb" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );

        expected =
            new Block[] { new TextBlock( "mary has " ), new AnchorBlock( "AnchorName" ),
                new TextBlock( " a little lamb" ) };
        blocks = textParser.parse( "mary has #AnchorName a little lamb" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );

        expected = new Block[] { new TextBlock( "mary has #AnchorName1233 a little lamb" ) };
        blocks = textParser.parse( "mary has #AnchorName1233 a little lamb" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );
    }

    /**
     * unit test
     */
    public final void testAutomaticLink()
    {
        Block[] blocks, expected;

        expected =
            new Block[] { new TextBlock( "Go to " ),
                new LinkBlock( "http://twiki.com", new TextBlock( "http://twiki.com" ) ),
                new TextBlock( " and ..." ) };
        blocks = textParser.parse( "Go to http://twiki.com and ..." ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );
    }

    /** unit test */
    public final void testAutomaticImage()
    {
        Block[] blocks, expected;

        expected =
            new Block[] { new LinkBlock( "http://twiki.org", new ImageBlock( "http://twiki.org/logo.png" ) ) };
        blocks = textParser.parse( "[[http://twiki.org][http://twiki.org/logo.png]]" ).toArray( TOARRAY );
        assertArrayEquals( expected, blocks );
    }

    /** unit test */
    public final void testLinkImage()
    {
        Block[] blocks, expected;

        expected =
            new Block[] { new TextBlock( "Go to " ), new ImageBlock( "http://twiki.com/image.png" ),
                new TextBlock( " thisisnotanimage.png and ..." ) };
        blocks = textParser.parse( "Go to http://twiki.com/image.png " + "thisisnotanimage.png and ..." )
                            .toArray( TOARRAY );
        assertArrayEquals( expected, blocks );
    }

    /**
     * Test image inserted with a html img tag
     */
    public final void testRelativeImage()
    {
        Block[] blocks, expected;

        expected =
            new Block[] { new TextBlock( "My summer house: " ), new ImageBlock( "images/summerhouse.png" ),
                new TextBlock( " isn't it great?!" ) };
        blocks = textParser
                            .parse(
                                    "My summer house: <img class=\"some_class\" src=\"images/summerhouse.png\"/> isn't it great?!" )
                            .toArray( TOARRAY );
        assertArrayEquals( expected, blocks );
    }
}
