/*
 *  Copyright 2005 Zauber <info /at/ zauber dot com dot ar>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.maven.doxia.module.twiki.parser;

import java.util.Arrays;


/**
 * tests the WikiWord parsing (and things like that)
 *
 * @author Juan F. Codagnone
 * @since Nov 4, 2005
 */
public class WordsTest extends AbstractBlockTestCase
{
    /**
     * used to convert lists to arrays
     */
    private static final Block [] TOARRAY = new Block[]{};

    /**
     * ...
     */
    public final void testText()
    {
        Block [] blocks, expected;

        expected = new Block[]{new TextBlock( "     Some text    " )};
        blocks = textParser.parse( "     Some text    " ).toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );
    }

    /**
     * ...
     */
    public final void testWikiWords()
    {
        Block [] blocks, expected;

        expected = new Block[]{new WikiWordBlock( "WikiWord" )};
        blocks = textParser.parse( "WikiWord" ).toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );

        // this is not a wiki word
        expected = new Block[]{new TextBlock( "Wiki" )};
        blocks = textParser.parse( "Wiki" ).toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );

        expected = new Block[]{new TextBlock( "Web." )};
        blocks = textParser.parse( "Web." ).toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );

        expected = new Block[]{new TextBlock( "fooWikiBar" )};
        blocks = textParser.parse( "fooWikiBar" ).toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );

        expected = new Block[]{
            new WikiWordBlock( "WikiWord" ), new TextBlock( "...." )
        };
        blocks = textParser.parse( "WikiWord...." ).toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );
    }

    /**
     * ...
     */
    public final void testWebWikiWords()
    {
        Block [] blocks, expected;

        expected = new Block[]{new WikiWordBlock( "Web.WikiWord" )};
        blocks = textParser.parse( "Web.WikiWord" ).toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );

        expected = new Block[]{new WikiWordBlock( "My1Web.WikiWord" )};
        blocks = textParser.parse( "My1Web.WikiWord" ).toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );
    }

    /**
     * ...
     */
    public final void testWebAnchorWikiWords()
    {
        Block [] blocks, expected;

        expected = new Block[]{new WikiWordBlock( "WikiWord#anchor" )};
        blocks = textParser.parse( "WikiWord#anchor" ).toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );

        expected = new Block[]{new WikiWordBlock( "MyWeb.WikiWord#anchor" )};
        blocks = textParser.parse( "MyWeb.WikiWord#anchor" ).toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );

    }

    /**
     * test Specific Links
     */
    public final void testSpecificLinks()
    {
        Block [] blocks, expected;

        expected = new Block[]{new LinkBlock( "reference", "text" )};
        blocks = textParser.parse( "[[reference][text]]" ).toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );

        expected = new Block[]{
            new TextBlock( "foo" ),
            new LinkBlock( "reference", "text" ),
            new TextBlock( "bar" ),
        };
        blocks = textParser.parse( "foo[[reference][text]]bar" ).toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );

        expected = new Block[]{
            new TextBlock( " foo " ),
            new LinkBlock( "reference", "text" ),
            new TextBlock( " bar " ),
        };
        blocks = textParser.parse( " foo [[reference][text]] bar " )
            .toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );
    }

    /**
     * test Specific Links
     */
    public final void testSpecificLinkPrevention()
    {
        Block [] blocks, expected;

        expected = new Block[]{new TextBlock( "[[reference][text]]" )};
        blocks = textParser.parse( "![[reference][text]]" ).toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );
    }

    /**
     * ...
     */
    public final void testPreventLinkingWikiWord()
    {
        Block [] blocks, expected;

        expected = new Block[]{
            new TextBlock( " " ),
            new TextBlock( "WikiWord" ),
            new TextBlock( " " ),
        };
        blocks = textParser.parse( " !WikiWord " ).toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );

        expected = new Block[]{new TextBlock( " !!WikiWord " )};
        blocks = textParser.parse( " !!WikiWord " ).toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );
    }

    /**
     * ej [[Main.TWiki rules]] would be wikiword Main.TWikiRules
     */
    public final void testForcedLinks()
    {
        Block [] blocks, expected;

        expected = new Block[]{
            new WikiWordBlock( "WikiSyntax", "wiki syntax" ),
        };
        blocks = textParser.parse( "[[wiki syntax]]" ).toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );

        expected = new Block[]{
            new TextBlock( "[[wiki syntax]]" ),
        };
        blocks = textParser.parse( "![[wiki syntax]]" ).toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );

        expected = new Block[]{
            new TextBlock( "foo" ),
            new WikiWordBlock( "WikiSyntax", "wiki syntax" ),
            new TextBlock( "bar" ),
        };
        blocks = textParser.parse( "foo[[wiki syntax]]bar" ).toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );
    }

    /**
     * ...
     */
    public final void testMailtoForcedLinks()
    {
        Block [] blocks, expected;

        expected = new Block[]{
            new LinkBlock( "mailto:a@z.com", "Mail" ),
        };
        blocks = textParser.parse( "[[mailto:a@z.com Mail]]" ).toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );
    }

    /**
     * ...
     */
    public final void testAnchors()
    {
        Block [] blocks, expected;

        expected = new Block[]{
            new TextBlock( "mary has #anchor a little lamb" ),
        };
        blocks = textParser.parse( "mary has #anchor a little lamb" ).toArray(
            TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );

        expected = new Block[]{
            new TextBlock( "mary has " ),
            new AnchorBlock( "AnchorName" ),
            new TextBlock( " a little lamb" ),
        };
        blocks = textParser.parse( "mary has #AnchorName a little lamb" )
            .toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );

        expected = new Block[]{
            new TextBlock( "mary has #AnchorName1233 a little lamb" ),
        };
        blocks = textParser.parse( "mary has #AnchorName1233 a little lamb" )
            .toArray( TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );
    }

    /**
     * unit test
     */
    public final void testAutomaticLink()
    {
        Block [] blocks, expected;

        expected = new Block[]{
            new TextBlock( "Go to " ),
            new LinkBlock( "http://twiki.com", "http://twiki.com" ),
            new TextBlock( " and ..." ),
        };
        blocks = textParser.parse( "Go to http://twiki.com and ..." ).toArray(
            TOARRAY );
        assertTrue( Arrays.equals( expected, blocks ) );
    }
}
