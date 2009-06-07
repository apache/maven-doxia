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
import java.util.Locale;

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
    private final IndexEntry chapterIndex;

    /**
     * Default constructor.
     *
     * @param out the Writer.
     * @param chapterIndex the chapter IndexEntry.
     * @param i18n I18N.
     * @param locale wanted locale.
     */
    public ChapterXdocBookSink( Writer out, IndexEntry chapterIndex, I18N i18n, Locale locale )
    {
        super( out, i18n, locale );

        this.chapterIndex = chapterIndex;
    }

    /** {@inheritDoc} */
    protected void navigationPanel()
    {
        write( "<!--Navigation Panel-->" );

        write( "<table width=\"100%\" align=\"center\">" );
        write( "<tr>" );

        // -----------------------------------------------------------------------
        // Prev
        // -----------------------------------------------------------------------

        IndexEntry prevChapter = chapterIndex.getPrevEntry();

        write( "<td><div align=\"left\">" );

        previous( prevChapter );

        write( "</div></td>" );

        // -----------------------------------------------------------------------
        // Parent
        // -----------------------------------------------------------------------

        write( "<td><div align=\"center\">" );
        up();
        write( "</div></td>" );

        // -----------------------------------------------------------------------
        // Next
        // -----------------------------------------------------------------------

        write( "<td><div align=\"right\">" );

        next();

        write( "</div></td>" );

        write( "</tr>" );
        write( "</table>" );

        write( "<!--End of Navigation Panel-->" );
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
                write( "<i>Start of book</i>" );
            }
            else
            {
                write( getString( "previous" ) + ": <a href=\"" + lastEntry.getId() + ".html\">" );
                content( lastEntry.getTitle() );
                write( "</a>" );
            }
        }
        else
        {
            write( getString( "previous" ) + ":<a href=\"index.html\">" + getString( "toc" ) + "</a>" );
        }
    }

    /**
     * Add parent/up link.
     */
    protected void up()
    {
        write( getString( "up" ) + ": <a href=\"index.html\">" + getString( "toc" ) + "</a>" );
    }

    /**
     * Add next link
     */
    protected void next()
    {
        IndexEntry firstEntry = chapterIndex.getFirstEntry();
        if ( firstEntry == null )
        {
            write( "<i>End of book</i>" );
        }
        else
        {
            write( getString( "next" ) + ": <a href=\"" + firstEntry.getId() + ".html\">" );
            content( firstEntry.getTitle() );
            write( "</a>" );
        }
    }
}
