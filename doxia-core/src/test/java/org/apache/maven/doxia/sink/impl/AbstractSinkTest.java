package org.apache.maven.doxia.sink.impl;

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

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.maven.doxia.AbstractModuleTest;
import org.apache.maven.doxia.logging.PlexusLoggerWrapper;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.util.IOUtil;
import org.xmlunit.matchers.CompareMatcher;

import static org.junit.Assert.assertThat;

/**
 * Abstract base class to test sinks.
 *
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractSinkTest
    extends AbstractModuleTest
{
    private final CharArrayWriter testWriter = new CharArrayWriter();
    private Sink sink;

    /**
     * Resets the writer and creates a new sink with it.
     *
     * {@inheritDoc}
     *
     * @throws java.lang.Exception if something goes wrong.
     */
    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        testWriter.reset();
        sink = createSink( testWriter );
        sink.enableLogging( new PlexusLoggerWrapper( ( ( DefaultPlexusContainer )getContainer() ).getLogger() ) );
    }

    /**
     * Ability to wrap the xmlFragment with a roottag and namespaces, when required
     *
     * @param xmlFragment
     * @return valid XML
     */
    protected String wrapXml( String xmlFragment )
    {
        return xmlFragment;
    }

    /**
     * Transforms a given string to be compatible to XML comments.
     *
     * @param comment The string to transform.
     *
     * @return The given string transformed to be compatible to XML comments.
     *
     * @see <a href="http://www.w3.org/TR/2000/REC-xml-20001006#sec-comments">
     *   http://www.w3.org/TR/2000/REC-xml-20001006#sec-comments</a>
     * @since 1.7
     */
    protected static String toXmlComment( final String comment )
    {
        String processed = comment;

        if ( processed != null )
        {
            while ( processed.contains( "--" ) )
            {
                processed = processed.replace( "--", "- -" );
            }

            if ( processed.endsWith( "-" ) )
            {
                processed += " ";
            }
        }

        return processed;
    }

    // ---------------------------------------------------------------------
    // Common test cases
    // ----------------------------------------------------------------------

    /**
     * Tests that the current sink is able to render the common test document. If the sink is an Xml sink defined
     * by {@link #isXmlSink()}, it uses an Xml Writer defined by {@link #getXmlTestWriter(String)}. If not, it uses
     * the Writer defined by {@link #getTestWriter(String)}.
     *
     * @see SinkTestDocument
     * @throws IOException If the target test document could not be generated.
     * @see #isXmlSink()
     * @see #getTestWriter(String)
     * @see #getXmlTestWriter(String)
     */
    public final void testTestDocument() throws IOException
    {
        Writer writer = ( isXmlSink() ? getXmlTestWriter( "testDocument" ) : getTestWriter( "testDocument" ) );
        Sink testSink = createSink( writer );

        try
        {
            SinkTestDocument.generate( testSink );
        }
        finally
        {
            testSink.close();
            IOUtil.close( writer );
        }
    }

    /**
     * Checks that the sequence <code>[title(), text( title ), title_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getTitleBlock getTitleBlock}( title ).
     */
    public void testTitle()
    {
        String title = "Grodek";
        sink.title();
        sink.text( title );
        sink.title_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getTitleBlock( title );

        assertEquals( "Wrong title!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[author(), text( author ), author_()]
     * </code>, invoked on the current sink, produces the same result as
     * {@link #getAuthorBlock getAuthorBlock}( author ).
     */
    public void testAuthor()
    {
        String author = "Georg_Trakl";
        sink.author();
        sink.text( author );
        sink.author_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getAuthorBlock( author );

        assertEquals( "Wrong author!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[date(), text( date ), date_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getDateBlock getDateBlock}( date ).
     */
    public void testDate()
    {
        String date = "1914";
        sink.date();
        sink.text( date );
        sink.date_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getDateBlock( date );

        assertEquals( "Wrong date!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[head(), head_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getHeadBlock getHeadBlock()}.
     */
    public void testHead()
    {
        sink.head();
        sink.head_();
        sink.flush();
        sink.close();

        String actual = normalizeLineEnds( testWriter.toString() );
        String expected = normalizeLineEnds( getHeadBlock() );

        assertEquals( "Wrong head!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[body(), body_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getBodyBlock getBodyBlock()}.
     */
    public void testBody()
    {
        sink.body();
        sink.body_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getBodyBlock();

        assertEquals( "Wrong body!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[sectionTitle(), text( title ),
     * sectionTitle_()]</code>, invoked on the current sink, produces
     * the same result as
     * {@link #getSectionTitleBlock getSectionTitleBlock}( title ).
     */
    public void testSectionTitle()
    {
        String title = "Title";
        sink.sectionTitle();
        sink.text( title );
        sink.sectionTitle_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getSectionTitleBlock( title );

        assertEquals( "Wrong sectionTitle!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[section1(), sectionTitle1(),
     * text( title ), sectionTitle1_(), section1_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getSection1Block getSection1Block}( title ).
     */
    public void testSection1()
    {
        String title = "Title1";
        sink.section1();
        sink.sectionTitle1();
        sink.text( title );
        sink.sectionTitle1_();
        sink.section1_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getSection1Block( title );

        assertEquals( "Wrong section1 block!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[section2(), sectionTitle2(),
     * text( title ), sectionTitle2_(), section2_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getSection2Block getSection2Block}( title ).
     */
    public void testSection2()
    {
        String title = "Title2";
        sink.section2();
        sink.sectionTitle2();
        sink.text( title );
        sink.sectionTitle2_();
        sink.section2_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getSection2Block( title );

        assertEquals( "Wrong section2 block!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[section3(), sectionTitle3(),
     * text( title ), sectionTitle3_(), section3_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getSection3Block getSection3Block}( title ).
     */
    public void testSection3()
    {
        String title = "Title3";
        sink.section3();
        sink.sectionTitle3();
        sink.text( title );
        sink.sectionTitle3_();
        sink.section3_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getSection3Block( title );

        assertEquals( "Wrong section3 block!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[section4(), sectionTitle4(),
     * text( title ), sectionTitle4_(), section4_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getSection4Block getSection4Block}( title ).
     *
     */
    public void testSection4()
    {
        String title = "Title4";
        sink.section4();
        sink.sectionTitle4();
        sink.text( title );
        sink.sectionTitle4_();
        sink.section4_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getSection4Block( title );

        assertEquals( "Wrong section4 block!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[section5(), sectionTitle5(),
     * text( title ), sectionTitle5_(), section5_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getSection5Block getSection5Block}( title ).
     */
    public void testSection5()
    {
        String title = "Title5";
        sink.section5();
        sink.sectionTitle5();
        sink.text( title );
        sink.sectionTitle5_();
        sink.section5_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getSection5Block( title );

        assertEquals( "Wrong section5 block!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[list(), listItem(), text( item ),
     * listItem_(), list_()]</code>, invoked on the current sink, produces
     * the same result as {@link #getListBlock getListBlock}( item ).
     *
     */
    public void testList()
    {
        String item = "list_item";
        sink.list();
        sink.listItem();
        sink.text( item );
        sink.listItem_();
        sink.list_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getListBlock( item );

        assertEquals( "Wrong list!", expected, actual );
    }

    /**
     * Checks that the sequence <code>
     * [numberedList( Sink.NUMBERING_LOWER_ROMAN ), numberedListItem(),
     * text( item ), numberedListItem_(), numberedList_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getNumberedListBlock getNumberedListBlock}( item ).
     */
    public void testNumberedList()
    {
        String item = "numbered_list_item";
        sink.numberedList( Sink.NUMBERING_LOWER_ROMAN );
        sink.numberedListItem();
        sink.text( item );
        sink.numberedListItem_();
        sink.numberedList_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getNumberedListBlock( item );

        assertEquals( "Wrong numbered list!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[definitionList(), definitionListItem(),
     * definedTerm(), text( definum ), definedTerm_(), definition(),
     * text( definition ), definition_(), definitionListItem_(),
     * definitionList_()]</code>, invoked on the current sink, produces the same
     * result as {@link #getDefinitionListBlock getDefinitionListBlock}
     * ( definum, definition ).
     */
    public void testDefinitionList()
    {
        String definum = "definum";
        String definition = "definition";
        sink.definitionList();
        sink.definitionListItem();
        sink.definedTerm();
        sink.text( definum );
        sink.definedTerm_();
        sink.definition();
        sink.text( definition );
        sink.definition_();
        sink.definitionListItem_();
        sink.definitionList_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getDefinitionListBlock( definum, definition );

        assertEquals( "Wrong definition list!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[figure(), figureGraphics( source ),
     * figureCaption(), text( caption ), figureCaption_(), figure_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getFigureBlock getFigureBlock}( source, caption ).
     */
    public void testFigure() throws Exception
    {
        String source = "figure.jpg";
        String caption = "Figure_caption";
        sink.figure();
        sink.figureGraphics( source );
        sink.figureCaption();
        sink.text( caption );
        sink.figureCaption_();
        sink.figure_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getFigureBlock( source, caption );

        if ( isXmlSink() )
        {
            assertThat ( wrapXml( actual ), CompareMatcher.isIdenticalTo( wrapXml( expected ) ));
        }
        else
        {
            assertEquals( actual, expected );
        }
    }


    public void testFigureWithoutCaption() throws Exception
    {
        String source = "figure.jpg";
        sink.figure();
        sink.figureGraphics( source );
        sink.figure_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getFigureBlock( source, null );

        if ( isXmlSink() )
        {
            assertThat ( wrapXml( actual ), CompareMatcher.isIdenticalTo( wrapXml( expected ) ));
        }
        else
        {
            assertEquals( actual, expected );
        }
    }

    /**
     * Checks that the sequence <code>[table(),
     * tableRows( Sink.JUSTIFY_CENTER, false ), tableRow(), tableCell(),
     * text( cell ), tableCell_(), tableRow_(), tableRows_(), tableCaption(),
     * text( caption ), tableCaption_(), table_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getTableBlock getTableBlock}( cell, caption ).
     */
    public void testTable() throws Exception
    {
        String cell = "cell";
        String caption = "Table_caption";
        int[] justify = { Sink.JUSTIFY_CENTER };
        sink.table();
        sink.tableRows( justify, false );
        sink.tableRow();
        sink.tableCell();
        sink.text( cell );
        sink.tableCell_();
        sink.tableRow_();
        sink.tableRows_();
        sink.tableCaption();
        sink.text( caption );
        sink.tableCaption_();
        sink.table_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getTableBlock( cell, caption );

        if ( isXmlSink() )
        {
            assertThat ( wrapXml( actual ), CompareMatcher.isIdenticalTo( wrapXml( expected ) ));
        }
        else
        {
            assertEquals( actual, expected );
        }
    }

    /**
     * Checks that the sequence <code>[paragraph(), text( text ),
     * paragraph_()]</code>, invoked on the current sink, produces
     * the same result as {@link #getParagraphBlock getParagraphBlock}( text ).
     */
    public void testParagraph()
    {
        String text = "Text";
        sink.paragraph();
        sink.text( text );
        sink.paragraph_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getParagraphBlock( text );

        assertEquals( "Wrong paragraph!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[verbatim( SinkEventAttributeSet.BOXED ), text( text ),
     * verbatim_()]</code>, invoked on the current sink, produces the
     * same result as {@link #getVerbatimBlock getVerbatimBlock}( text ).
     */
    public void testVerbatim()
    {
        String text = "Text";
        sink.verbatim( SinkEventAttributeSet.BOXED );
        sink.text( text );
        sink.verbatim_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getVerbatimBlock( text );

        assertEquals( "Wrong verbatim!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[horizontalRule()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getHorizontalRuleBlock getHorizontalRuleBlock()}.
     */
    public void testHorizontalRule()
    {
        sink.horizontalRule();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getHorizontalRuleBlock();

        assertEquals( "Wrong horizontal rule!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[pageBreak()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getPageBreakBlock getPageBreakBlock()}.
     */
    public void testPageBreak()
    {
        sink.pageBreak();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getPageBreakBlock();

        assertEquals( "Wrong pageBreak!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[anchor( anchor ), text( anchor ),
     * anchor_()]</code>, invoked on the current sink, produces the same
     * result as {@link #getAnchorBlock getAnchorBlock}( anchor ).
     */
    public void testAnchor()
    {
        String anchor = "Anchor";
        sink.anchor( anchor );
        sink.text( anchor );
        sink.anchor_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getAnchorBlock( anchor );

        assertEquals( "Wrong anchor!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[link( link ), text( text ),
     * link_()]</code>, invoked on the current sink, produces the same
     * result as {@link #getLinkBlock getLinkBlock}( link, text ).
     */
    public void testLink()
    {
        String link = "#Link";
        String text = "Text";
        sink.link( link );
        sink.text( text );
        sink.link_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getLinkBlock( link, text );

        assertEquals( "Wrong link!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[italic(), text( text ), italic_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getItalicBlock getItalicBlock}( text ).
     */
    public void testItalic()
    {
        String text = "Italic";
        sink.italic();
        sink.text( text );
        sink.italic_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getItalicBlock( text );

        assertEquals( "Wrong italic!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[bold(), text( text ), bold_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getBoldBlock getBoldBlock}( text ).
     */
    public void testBold()
    {
        String text = "Bold";
        sink.bold();
        sink.text( text );
        sink.bold_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getBoldBlock( text );

        assertEquals( "Wrong bold!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[monospaced(), text( text ),
     * monospaced_()]</code>, invoked on the current sink, produces the same
     * result as {@link #getMonospacedBlock getMonospacedBlock}( text ).
     */
    public void testMonospaced()
    {
        String text = "Monospaced";
        sink.monospaced();
        sink.text( text );
        sink.monospaced_();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getMonospacedBlock( text );

        assertEquals( "Wrong monospaced!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[lineBreak()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getLineBreakBlock getLineBreakBlock()}.
     */
    public void testLineBreak()
    {
        sink.lineBreak();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getLineBreakBlock();

        assertEquals( "Wrong lineBreak!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[nonBreakingSpace()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getNonBreakingSpaceBlock getNonBreakingSpaceBlock()}.
     */
    public void testNonBreakingSpace()
    {
        sink.nonBreakingSpace();
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getNonBreakingSpaceBlock();

        assertEquals( "Wrong nonBreakingSpace!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[text( text )]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getTextBlock getTextBlock()}.
     */
    public void testText()
    {
        String text = "~,_=,_-,_+,_*,_[,_],_<,_>,_{,_},_\\";
        sink.text( text );
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getTextBlock( text );

        assertEquals( "Wrong text!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[rawText( text )]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getRawTextBlock getRawTextBlock}( text ).
     */
    public void testRawText()
    {
        String text = "~,_=,_-,_+,_*,_[,_],_<,_>,_{,_},_\\";
        sink.rawText( text );
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getRawTextBlock( text );

        assertEquals( "Wrong rawText!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[comment(comment)]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getCommentBlock getCommentBlock}( comment ).
     * @since 1.1.1
     */
    public void testComment()
    {
        String comment = "Simple comment with ----";
        sink.comment( comment );
        sink.flush();
        sink.close();

        String actual = testWriter.toString();
        String expected = getCommentBlock( comment );

        assertEquals( "Wrong comment!", expected, actual );

        testWriter.reset();
        sink = createSink( testWriter );
        sink.enableLogging( new PlexusLoggerWrapper( ( ( DefaultPlexusContainer )getContainer() ).getLogger() ) );

        comment = "-";
        sink.comment( comment );
        sink.flush();
        sink.close();

        actual = testWriter.toString();
        expected = getCommentBlock( comment );

        assertEquals( "Wrong comment!", expected, actual );
    }

    // ----------------------------------------------------------------------
    // Utility methods
    // ----------------------------------------------------------------------

    /**
     * Returns the sink that is currently being tested.
     * @return The current test sink.
     */
    protected Sink getSink()
    {
        return sink;
    }

    /**
     * Returns a String representation of all events that have been written to the sink.
     * @return The Sink content as a String.
     */
    protected String getSinkContent()
    {
        return testWriter.toString();
    }

    /**
     * Returns the directory where all sink test output will go.
     * @return The test output directory.
     */
    protected String getOutputDir()
    {
        return "sink/";
    }

    // ----------------------------------------------------------------------
    // Abstract methods the individual SinkTests must provide
    // ----------------------------------------------------------------------

    /**
     * This method allows to use the correct Writer in {@link #testTestDocument()}.
     *
     * @return <code>true</code> if the Sink is an XML one, <code>false</code> otherwise.
     * @see #testTestDocument()
     */
    protected abstract boolean isXmlSink();

    /**
     * Return a new instance of the sink that is being tested.
     * @param writer The writer for the sink.
     * @return A new sink.
     */
    protected abstract Sink createSink( Writer writer );

    /**
     * Returns a title block generated by this sink.
     * @param title The title to use.
     * @return The result of invoking a title block on the current sink.
     * @see #testTitle()
     */
    protected abstract String getTitleBlock( String title );

    /**
     * Returns an author block generated by this sink.
     * @param author The author to use.
     * @return The result of invoking an author block on the current sink.
     * @see #testAuthor()
     */
    protected abstract String getAuthorBlock( String author );

    /**
     * Returns a date block generated by this sink.
     * @param date The date to use.
     * @return The result of invoking a date block on the current sink.
     * @see #testDate()
     */
    protected abstract String getDateBlock( String date );

    /**
     * Returns a head block generated by this sink.
     * @return The result of invoking a head block on the current sink.
     * @see #testHead()
     */
    protected abstract String getHeadBlock();

    /**
     * Returns a body block generated by this sink.
     * @return The result of invoking a body block on the current sink.
     * @see #testBody()
     */
    protected abstract String getBodyBlock();

    /**
     * Returns a SectionTitle block generated by this sink.
     * @param title The title to use.
     * @return The result of invoking a SectionTitle block on the current sink.
     * @see #testSectionTitle()
     */
    protected abstract String getSectionTitleBlock( String title );

    /**
     * Returns a Section1 block generated by this sink.
     * @param title The title to use.
     * @return The result of invoking a Section1 block on the current sink.
     * @see #testSection1()
     */
    protected abstract String getSection1Block( String title );

    /**
     * Returns a Section2 block generated by this sink.
     * @param title The title to use.
     * @return The result of invoking a Section2 block on the current sink.
     * @see #testSection2()
     */
    protected abstract String getSection2Block( String title );

    /**
     * Returns a Section3 block generated by this sink.
     * @param title The title to use.
     * @return The result of invoking a Section3 block on the current sink.
     * @see #testSection3()
     */
    protected abstract String getSection3Block( String title );

    /**
     * Returns a Section4 block generated by this sink.
     * @param title The title to use.
     * @return The result of invoking a Section4 block on the current sink.
     * @see #testSection4()
     */
    protected abstract String getSection4Block( String title );

    /**
     * Returns a Section5 block generated by this sink.
     * @param title The title to use.
     * @return The result of invoking a Section5 block on the current sink.
     * @see #testSection5()
     */
    protected abstract String getSection5Block( String title );

    /**
     * Returns a list block generated by this sink.
     * @param item The item to use.
     * @return The result of invoking a list block on the current sink.
     * @see #testList()
     */
    protected abstract String getListBlock( String item );

    /**
     * Returns a NumberedList block generated by this sink.
     * @param item The item to use.
     * @return The result of invoking a NumberedList block on the current sink.
     * @see #testNumberedList()
     */
    protected abstract String getNumberedListBlock( String item );

    /**
     * Returns a DefinitionList block generated by this sink.
     * @param definum The term to define.
     * @param definition The definition.
     * @return The result of invoking a DefinitionList block on the current sink.
     * @see #testDefinitionList()
     */
    protected abstract String getDefinitionListBlock( String definum,
        String definition );

    /**
     * Returns a Figure block generated by this sink.
     * @param source The figure source string.
     * @param caption The caption to use (may be null).
     * @return The result of invoking a Figure block on the current sink.
     * @see #testFigure()
     */
    protected abstract String getFigureBlock( String source, String caption );

    /**
     * Returns a Table block generated by this sink.
     * @param cell A tabel cell to use.
     * @param caption The caption to use (may be null).
     * @return The result of invoking a Table block on the current sink.
     * @see #testTable()
     */
    protected abstract String getTableBlock( String cell, String caption );

    /**
     * Returns a Paragraph block generated by this sink.
     * @param text The text to use.
     * @return The result of invoking a Paragraph block on the current sink.
     * @see #testParagraph()
     */
    protected abstract String getParagraphBlock( String text );

    /**
     * Returns a Verbatim block generated by this sink.
     * @param text The text to use.
     * @return The result of invoking a Verbatim block on the current sink.
     * @see #testVerbatim()
     */
    protected abstract String getVerbatimBlock( String text );

    /**
     * Returns a HorizontalRule block generated by this sink.
     * @return The result of invoking a HorizontalRule block on the current sink.
     * @see #testHorizontalRule()
     */
    protected abstract String getHorizontalRuleBlock();

    /**
     * Returns a PageBreak block generated by this sink.
     * @return The result of invoking a PageBreak block on the current sink.
     * @see #testPageBreak()
     */
    protected abstract String getPageBreakBlock();

    /**
     * Returns a Anchor block generated by this sink.
     * @param anchor The anchor to use.
     * @return The result of invoking a Anchor block on the current sink.
     * @see #testAnchor()
     */
    protected abstract String getAnchorBlock( String anchor );

    /**
     * Returns a Link block generated by this sink.
     * @param link The link to use.
     * @param text The link text.
     * @return The result of invoking a Link block on the current sink.
     * @see #testLink()
     */
    protected abstract String getLinkBlock( String link, String text );

    /**
     * Returns a Italic block generated by this sink.
     * @param text The text to use.
     * @return The result of invoking a Italic block on the current sink.
     * @see #testItalic()
     */
    protected abstract String getItalicBlock( String text );

    /**
     * Returns a Bold block generated by this sink.
     * @param text The text to use.
     * @return The result of invoking a Bold block on the current sink.
     * @see #testBold()
     */
    protected abstract String getBoldBlock( String text );

    /**
     * Returns a Monospaced block generated by this sink.
     * @param text The text to use.
     * @return The result of invoking a Monospaced block on the current sink.
     * @see #testMonospaced()
     */
    protected abstract String getMonospacedBlock( String text );

    /**
     * Returns a LineBreak block generated by this sink.
     * @return The result of invoking a LineBreak block on the current sink.
     * @see #testLineBreak()
     */
    protected abstract String getLineBreakBlock();

    /**
     * Returns a NonBreakingSpace block generated by this sink.
     * @return The result of invoking a NonBreakingSpace block
     * on the current sink.
     * @see #testNonBreakingSpace()
     */
    protected abstract String getNonBreakingSpaceBlock();

    /**
     * Returns a Text block generated by this sink.
     * @param text The text to use.
     * @return The result of invoking a Text block on the current sink.
     * @see #testText()
     */
    protected abstract String getTextBlock( String text );

    /**
     * Returns a RawText block generated by this sink.
     * @param text The text to use.
     * @return The result of invoking a RawText block on the current sink.
     * @see #testRawText()
     */
    protected abstract String getRawTextBlock( String text );

    /**
     * Returns a comment block generated by this sink.
     * @param text The text to use.
     * @return The result of invoking a comment block on the current sink.
     * @see #testComment()
     * @since 1.1.1
     */
    protected abstract String getCommentBlock( String text );

    protected final void verifyValignSup( String text )
    {
        sink.text( "ValignSup", new SinkEventAttributeSet( SinkEventAttributes.VALIGN, "sup"  ) );
        sink.flush();
        sink.close();

        String actual = testWriter.toString();

        assertEquals( "Wrong valign sup!", text, actual );
    }

    protected final void verifyValignSub( String text )
    {
        sink.text( "ValignSub", new SinkEventAttributeSet( SinkEventAttributes.VALIGN, "sub"  ) );
        sink.flush();
        sink.close();

        String actual = testWriter.toString();

        assertEquals( "Wrong valign sub!", text, actual );
    }

    protected final void verifyDecorationUnderline( String text )
    {
        sink.text( "DecorationUnderline", new SinkEventAttributeSet( SinkEventAttributes.DECORATION, "underline"  ) );
        sink.flush();
        sink.close();

        String actual = testWriter.toString();

        assertEquals( "Wrong decoration underline!", text, actual );
    }

    protected final void verifyDecorationLineThrough( String text )
    {
        sink.text( "DecorationLineThrough", new SinkEventAttributeSet( SinkEventAttributes.DECORATION, "line-through"  ) );
        sink.flush();
        sink.close();

        String actual = testWriter.toString();

        assertEquals( "Wrong decoration line-through!", text, actual );
    }


}
