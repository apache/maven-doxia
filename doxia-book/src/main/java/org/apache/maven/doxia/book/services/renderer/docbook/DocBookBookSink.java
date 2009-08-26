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

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.module.docbook.DocBookSink;

/**
 * An Docbook Sink that doesn't write out head or body elements for every section of a book, and has some convenience
 * methods relating to the construction of a Doxia Book.
 *
 * @author Dave Syer
 * @version $Id$
 * @since 1.1
 */
public class DocBookBookSink
    extends DocBookSink
{
    /** Indicates if we're inside a head. */
    private boolean hasHead = false;

    /**
     * Construct a new DocBookSink.
     *
     * @param out the writer for the sink.
     */
    public DocBookBookSink( Writer out )
    {
        super( out );

        setSystemId( "http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd" );
        setPublicId( "-//OASIS//DTD DocBook V4.4//EN" );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * Does nothing because we don't want the header from each document to crop up in the middle of a book.
     */
    public void head()
    {
        // noop
    }

    /**
     * {@inheritDoc}
     *
     * Does nothing because we don't want the header from each document to crop up in the middle of a book.
     */
    public void head_()
    {
        // noop
    }

    /**
     * {@inheritDoc}
     *
     * Marks the skip flag to true so that this element's text is not emitted by the base class.
     */
    public void title()
    {
        setSkip( true );
    }

    /**
     * {@inheritDoc}
     *
     * Marks the skip flag to false so that rendering can resume.
     */
    public void title_()
    {
        setSkip( false );
    }

    /**
     * {@inheritDoc}
     *
     * Marks the skip flag to true so that this element's text is not emitted by the base class.
     */
    public void author()
    {
        setSkip( true );
    }

    /**
     * {@inheritDoc}
     *
     * Marks the skip flag to false so that rendering can resume.
     */
    public void author_()
    {
        setSkip( false );
    }

    /**
     * {@inheritDoc}
     *
     * Does nothing because we don't want the header from each document to crop up in the middle of a book.
     */
    public void body()
    {
        // noop
    }

    /**
     * {@inheritDoc}
     *
     * Does nothing because we don't want the header from each document to crop up in the middle of a book.
     */
    public void body_()
    {
        // noop
    }

    /**
     * Emit the start tag for the book.
     *
     * @see org.apache.maven.doxia.module.docbook.DocbookMarkup#BOOK_TAG
     */
    public void book()
    {
        init();

        MutableAttributeSet att = writeXmlHeader( "book" );

        writeStartTag( BOOK_TAG, att );

    }

    /**
     * Emit the end tag for the book.
     *
     * @see org.apache.maven.doxia.module.docbook.DocbookMarkup#BOOK_TAG
     */
    public void book_()
    {
        writeEndTag( BOOK_TAG );
        flush();
    }

    /** If no header matter has yet been encountered emit the book info start tag. */
    private void bookHead()
    {
        if ( !hasHead )
        {
            writeStartTag( BOOKINFO_TAG );
            hasHead = true;
        }
    }

    /**
     * If some header matter has been encountered emit the book info end tag.
     */
    public void bookHead_()
    {
        if ( hasHead )
        {
            writeEndTag( BOOKINFO_TAG );
            hasHead = false;
        }
    }

    /**
     * Emit the title start tag for the whole book.
     */
    public void bookTitle()
    {
        bookHead();
        writeStartTag( Tag.TITLE );
    }

    /**
     * Emit the title end tag for the whole book.
     */
    public void bookTitle_()
    {
        super.title_();
    }

    /**
     * Emit the author start tag for the whole book.
     */
    public void bookAuthor()
    {
        bookHead();
        super.author();
    }

   /**
    * Emit the author end tag for the whole book.
    */
   public void bookAuthor_()
    {
        super.author_();
    }

   /**
    * Emit the date start tag for the whole book.
    */
   public void bookDate()
    {
        bookHead();
        super.date();
    }

    /**
     * Emit the date end tag for the whole book.
     */
    public void bookDate_()
    {
        super.date_();
    }

    /**
     * Emit the chapter start tag.
     */
    public void chapter()
    {
        writeStartTag( CHAPTER_TAG );
    }

    /**
     * Emit the chapter end tag.
     */
    public void chapter_()
    {
        writeEndTag( CHAPTER_TAG );
    }

    /**
     * Emit the chapter title start tag.
     */
    public void chapterTitle()
    {
        writeStartTag( Tag.TITLE );
    }

    /**
     * Emit the chapter title end tag.
     */
    public void chapterTitle_()
    {
        writeEndTag( Tag.TITLE );
    }
}
