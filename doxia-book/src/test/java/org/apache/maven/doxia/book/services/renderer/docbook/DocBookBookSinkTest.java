package org.apache.maven.doxia.book.services.renderer.docbook;

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

import java.io.Writer;

import java.util.Locale;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;

import org.apache.maven.doxia.module.docbook.DocBookSink;
import org.apache.maven.doxia.sink.AbstractSinkTest;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkUtils;

import org.apache.maven.doxia.util.DoxiaUtils;
import org.codehaus.plexus.util.FileUtils;

/**
 * Test the book path of the DockBook sink
 * @author Dave Syer
 */
public class DocBookBookSinkTest extends AbstractSinkTest
{
    /** {@inheritDoc} */
    protected boolean isXmlSink()
    {
        return true;
    }

    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "docbook";
    }

    /** {@inheritDoc} */
    protected Sink createSink( Writer writer )
    {
        return new DocBookBookSink( writer );
    }

      //
     // DocBookBookSink specific
    //

    /**
     * Checks that the sequence <code>[bookTitle(), text( title ), bookTitle_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getBookTitleBlock getBookTitleBlock}( title ).
     * NewLines are ignored.
     */
    public void testBookTitle()
    {
        DocBookBookSink sink = (DocBookBookSink) getSink();

        String title = "Grodek";
        sink.bookTitle();
        sink.text( title );
        sink.bookTitle_();
        sink.flush();

        String actual = getSinkContent();
        String expected = getBookTitleBlock( title );

        assertEquals( "Wrong book title!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[bookAuthor(), text( author ), bookAuthor_()]
     * </code>, invoked on the current sink, produces the same result as
     * {@link #getBookAuthorBlock getBookAuthorBlock}( author ).
     * NewLines are ignored.
     */
    public void testBookAuthor()
    {
        DocBookBookSink sink = (DocBookBookSink) getSink();

        String author = "Georg Trakl";
        sink.bookAuthor();
        sink.text( author );
        sink.bookAuthor_();
        sink.flush();

        String actual = getSinkContent();
        String expected = getBookAuthorBlock( author );

        assertEquals( "Wrong book author!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[bookDate(), text( date ), bookDate_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getBookDateBlock getBookDateBlock}( date ). NewLines are ignored.
     */
    public void testBookDate()
    {
        DocBookBookSink sink = (DocBookBookSink) getSink();

        String date = "1914";
        sink.bookDate();
        sink.text( date );
        sink.bookDate_();
        sink.flush();

        String actual = getSinkContent();
        String expected = getBookDateBlock( date );

        assertEquals( "Wrong book date!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[bookHead(), bookHead_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getBookHeadBlock getBookHeadBlock()}. NewLines are ignored.
     */
    public void testBookHead()
    {
        DocBookBookSink sink = (DocBookBookSink) getSink();

        //sink.bookHead();
        sink.bookHead_();
        sink.flush();

        String actual = getSinkContent();
        String expected = getBookHeadBlock();

        assertEquals( "Wrong book head!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[book(), book_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getBookBlock getBookBlock()}. NewLines are ignored.
     */
    public void testBook()
    {
        DocBookBookSink sink = (DocBookBookSink) getSink();

        sink.book();
        sink.book_();
        sink.flush();

        String actual = getSinkContent();
        String expected = getBookBlock();

        assertEquals( "Wrong book body!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[chapterTitle(), text( title ),
     * chapterTitle_()]</code>, invoked on the current sink, produces
     * the same result as
     * {@link #getChapterTitleBlock getChapterTitleBlock}( title ).
     * NewLines are ignored.
     */
    public void testChapterTitle()
    {
        DocBookBookSink sink = (DocBookBookSink) getSink();

        String title = "Title";
        sink.chapterTitle();
        sink.text( title );
        sink.chapterTitle_();
        sink.flush();

        String actual = getSinkContent();
        String expected = getChapterTitleBlock( title );

        assertEquals( "Wrong chapterTitle!", expected, actual );
    }

    /**
     * Checks that the sequence <code>[chapter(), chapter_()]</code>,
     * invoked on the current sink, produces the same result as
     * {@link #getChapterBlock getChapterBlock}().
     * NewLines are ignored.
     */
    public void testChapter()
    {
        DocBookBookSink sink = (DocBookBookSink) getSink();

        sink.chapter();
        sink.chapter_();
        sink.flush();

        String actual = getSinkContent();
        String expected = getChapterBlock();

        assertEquals( "Wrong chapter block!", expected, actual );
    }

    /**
     * Returns a title block generated by this sink.
     * @param title The title to use.
     * @return The result of invoking a title block on the current sink.
     * @see #testBookTitle()
     */
    protected String getBookTitleBlock( String title )
    {
        return "<bookinfo><title>" + title + "</title>";
    }

    /**
     * Returns an author block generated by this sink.
     * @param author The author to use.
     * @return The result of invoking an author block on the current sink.
     * @see #testBookAuthor()
     */
    protected String getBookAuthorBlock( String author )
    {
        return "<bookinfo><corpauthor>" + author + "</corpauthor>";
    }

    /**
     * Returns a date block generated by this sink.
     * @param date The date to use.
     * @return The result of invoking a date block on the current sink.
     * @see #testBookDate()
     */
    protected String getBookDateBlock( String date )
    {
        return "<bookinfo><date>" + date + "</date>";
    }

    /**
     * Returns a head block generated by this sink.
     * @return The result of invoking a head block on the current sink.
     * @see #testBookHead()
     */
    protected String getBookHeadBlock()
    {
        return "";
    }

    /**
     * Returns a body block generated by this sink.
     * @return The result of invoking a body block on the current sink.
     * @see #testBook()
     */
    protected String getBookBlock()
    {
        return "<?xml version=\"1.0\"?><!DOCTYPE book PUBLIC \"" + DocBookSink.DEFAULT_XML_PUBLIC_ID
            + "\" \"" + DocBookSink.DEFAULT_XML_SYSTEM_ID + "\"><book></book>";
    }

    /**
     * Returns a SectionTitle block generated by this sink.
     * @param title The title to use.
     * @return The result of invoking a SectionTitle block on the current sink.
     * @see #testChapterTitle()
     */
    protected String getChapterTitleBlock( String title )
    {
        return "<title>" + title + "</title>";
    }

    /**
     * Returns a Section1 block generated by this sink.
     * @param title The title to use.
     * @return The result of invoking a Section1 block on the current sink.
     * @see #testChapter()
     */
    protected String getChapterBlock()
    {
        return "<chapter></chapter>";
    }


      //
     // from DocBookSink
    //


    /** {@inheritDoc} */
    protected String getTitleBlock( String title )
    {
        return "";
    }

    /** {@inheritDoc} */
    protected String getAuthorBlock( String author )
    {
        return "";
    }

    /** {@inheritDoc} */
    protected String getDateBlock( String date )
    {
        return "<date>" + date + "</date>";
    }

    /** {@inheritDoc} */
    protected String getHeadBlock()
    {
        return "";
    }

    /** {@inheritDoc} */
    protected String getBodyBlock()
    {
        return "";
    }

    /** {@inheritDoc} */
    protected String getSectionTitleBlock( String title )
    {
        return "<title>" + title + "</title>";
    }

    /** {@inheritDoc} */
    protected String getSection1Block( String title )
    {
        return "<section><title>" + title + "</title></section>";
    }

    /** {@inheritDoc} */
    protected String getSection2Block( String title )
    {
        return "<section><title>" + title + "</title></section>";
    }

    /** {@inheritDoc} */
    protected String getSection3Block( String title )
    {
        return "<section><title>" + title + "</title></section>";
    }

    /** {@inheritDoc} */
    protected String getSection4Block( String title )
    {
        return "<section><title>" + title + "</title></section>";
    }

    /** {@inheritDoc} */
    protected String getSection5Block( String title )
    {
        return "<section><title>" + title + "</title></section>";
    }

    /** {@inheritDoc} */
    protected String getListBlock( String item )
    {
        return "<itemizedlist><listitem><para>" + item  + "</para></listitem></itemizedlist>";
    }

    /** {@inheritDoc} */
    protected String getNumberedListBlock( String item )
    {
        return "<orderedlist numeration=\"lowerroman\"><listitem><para>"
            + item  + "</para></listitem></orderedlist>";
    }

    /** {@inheritDoc} */
    protected String getDefinitionListBlock( String definum, String definition )
    {
        return "<variablelist><varlistentry><term>" + definum
            + "</term><listitem><para>" + definition
            + "</para></listitem></varlistentry></variablelist>";
    }

    /** {@inheritDoc} */
    protected String getFigureBlock( String source, String caption )
    {
        String format = FileUtils.extension( source ).toUpperCase( Locale.ENGLISH );

        return "<mediaobject><imageobject>"
                + "<imagedata fileref=\"" + source + "\" format=\"" + format + "\" />"
                + "</imageobject><caption><para>" + caption + "</para></caption></mediaobject>";
    }

    /** {@inheritDoc} */
    protected String getTableBlock( String cell, String caption )
    {
        // Using the same set ordering than the JVM
        MutableAttributeSet att = new SimpleAttributeSet();
        att.addAttribute( "frame", "none" );
        att.addAttribute( "rowsep", "0" );
        att.addAttribute( "colsep", "0" );

        return "<table" + SinkUtils.getAttributeString( att ) + "><title>" + caption
            + "</title><tgroup cols=\"1\"><colspec align=\"center\" /><tbody><row><entry>"
            + cell  + "</entry></row></tbody></tgroup></table>";
    }

    /** {@inheritDoc} */
    protected String getParagraphBlock( String text )
    {
        return "<para>" + text + "</para>";
    }

    /** {@inheritDoc} */
    protected String getVerbatimBlock( String text )
    {
        return "<programlisting>" + text + "</programlisting>";
    }

    /** {@inheritDoc} */
    protected String getHorizontalRuleBlock()
    {
        return "<!-- HR -->";
    }

    /** {@inheritDoc} */
    protected String getPageBreakBlock()
    {
        return "<!-- PB -->";
    }

    /** {@inheritDoc} */
    protected String getAnchorBlock( String anchor )
    {
        return "<anchor id=\"" + anchor + "\" />" + anchor + "<!-- anchor_end -->";
    }

    /** {@inheritDoc} */
    protected String getLinkBlock( String link, String text )
    {
        String linkend = DoxiaUtils.isInternalLink( link ) ? link.substring( 1 ) : link;
        return "<link linkend=\"" + linkend + "\">" + text + "</link>";
    }

    /** {@inheritDoc} */
    protected String getItalicBlock( String text )
    {
        return "<emphasis>" + text + "</emphasis>";
    }

    /** {@inheritDoc} */
    protected String getBoldBlock( String text )
    {
        return "<emphasis role=\"bold\">" + text + "</emphasis>";
    }

    /** {@inheritDoc} */
    protected String getMonospacedBlock( String text )
    {
        return "<literal>" + text + "</literal>";
    }

    /** {@inheritDoc} */
    protected String getLineBreakBlock()
    {
        return "<!-- LB -->";
    }

    /** {@inheritDoc} */
    protected String getNonBreakingSpaceBlock()
    {
        return "&#x00A0;";
    }

    /** {@inheritDoc} */
    protected String getTextBlock( String text )
    {
        // TODO: retreive those from the sink
        return "~,_=,_-,_+,_*,_[,_],_&lt;,_&gt;,_{,_},_\\";
    }

    /** {@inheritDoc} */
    protected String getRawTextBlock( String text )
    {
        // TODO
        return "";
    }

    /** {@inheritDoc} */
    protected String getCommentBlock( String text )
    {
        return "<!-- Simple comment with - - - - -->";
    }
}
