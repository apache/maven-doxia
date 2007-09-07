package org.apache.maven.doxia.book.services.renderer.xdoc;

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

import org.apache.maven.doxia.index.IndexEntry;
import org.codehaus.plexus.i18n.I18N;

/**
 * A <code>XdocSink</code> implementation for chapter in a book.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class ChapterXdocBookSink
    extends AbstractXdocBookSink
{
   /** the chapter IndexEntry. */
    private IndexEntry chapterIndex;

    /**
     * Default constructor.
     *
     * @param out the Writer.
     * @param chapterIndex the chapter IndexEntry.
     * @param i18n I18N.
     */
    public ChapterXdocBookSink( Writer out, IndexEntry chapterIndex, I18N i18n )
    {
        super( out, i18n );

        this.chapterIndex = chapterIndex;
    }

    /**
     * @see org.apache.maven.doxia.book.services.renderer.xdoc.AbstractXdocBookSink#navigationPanel()
     */
    protected void navigationPanel()
    {
        markup( "<!--Navigation Panel-->" + EOL );

        markup( "<table width=\"100%\" align=\"center\">" + EOL );
        markup( "<tr>" + EOL );

        // -----------------------------------------------------------------------
        // Prev
        // -----------------------------------------------------------------------

        IndexEntry prevChapter = chapterIndex.getPrevEntry();

        markup( "<td><div align='left'>" );

        previous( prevChapter );

        markup( "</div></td>" + EOL );

        // -----------------------------------------------------------------------
        // Parent
        // -----------------------------------------------------------------------

        markup( "<td><div align='center'>" );
        up();
        markup( "</div></td>" + EOL );

        // -----------------------------------------------------------------------
        // Next
        // -----------------------------------------------------------------------

        markup( "<td><div align='right'>" );

        next();

        markup( "</div></td>" + EOL );

        markup( "</tr>" + EOL );
        markup( "</table>" + EOL );

        markup( "<!--End of Navigation Panel-->" + EOL );
    }

    /**
     * Add previous link.
     *
     * @param prevChapter the previous IndexEntry.
     */
    protected void previous( IndexEntry prevChapter )
    {
        if ( prevChapter != null )
        {
            IndexEntry lastEntry = prevChapter.getLastEntry();
            if ( lastEntry == null )
            {
                markup( "<i>Start of book</i>" );
            }
            else
            {
                markup( getString( "previous" ) + ": <a href='" + lastEntry.getId() + ".html'>" );
                content( lastEntry.getTitle() );
                markup( "</a>" );
            }
        }
        else
        {
            markup( getString( "previous" ) + ":<a href='index.html'>" + getString( "toc" ) + "</a>" );
        }
    }

    /**
     * Add parent/up link.
     */
    protected void up()
    {
        markup( getString( "up" ) + ": <a href='index.html'>" + getString( "toc" ) + "</a>" );
    }

    /**
     * Add next link
     */
    protected void next()
    {
        IndexEntry firstEntry = chapterIndex.getFirstEntry();
        if ( firstEntry == null )
        {
            markup( "<i>End of book</i>" );
        }
        else
        {
            markup( getString( "next" ) + ": <a href='" + firstEntry.getId() + ".html'>" );
            content( firstEntry.getTitle() );
            markup( "</a>" );
        }
    }
}
