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
 * A <code>XdocSink</code> implementation for section in a book
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class SectionXdocBookSink
    extends AbstractXdocBookSink
{
    /** indexEntry. */
    private final IndexEntry indexEntry;

    /**
     * Default constructor.
     *
     * @param out the Writer to use.
     * @param indexEntry the IndexEntry.
     * @param i18n the I18N.
     * @param locale wanted locale.
     */
    public SectionXdocBookSink( Writer out, IndexEntry indexEntry, I18N i18n, Locale locale )
    {
        super( out, i18n, locale );

        this.indexEntry = indexEntry;
    }

    /** {@inheritDoc} */
    protected void navigationPanel()
    {
        write( "<!--Navigation Panel-->" );

        write( "<table width=\"100%\" align=\"center\" border=\"0\">" );
        write( "<tr>" );

        IndexEntry parent = indexEntry.getParent();

        // -----------------------------------------------------------------------
        // Prev
        // -----------------------------------------------------------------------

        IndexEntry prevEntry = indexEntry.getPrevEntry();

        write( "<td align=\"left\">" );

        previous( parent, prevEntry );

        write( "</td>" );

        // -----------------------------------------------------------------------
        // Parent
        // -----------------------------------------------------------------------

        write( "<td align=\"center\">" );
        up( parent );
        write( "</td>" );

        // -----------------------------------------------------------------------
        // Next
        // -----------------------------------------------------------------------

        IndexEntry nextEntry = indexEntry.getNextEntry();

        write( "<td align=\"right\">" );

        next( parent, nextEntry );

        write( "</td>" );

        write( "</tr>" );
        write( "</table>" );

        write( "<!--End of Navigation Panel-->" );
    }

    /**
     * Add previous link.
     *
     * @param parent the parent IndexEntry.
     * @param prevEntry the previous IndexEntry.
     */
    protected void previous( IndexEntry parent, IndexEntry prevEntry )
    {
        if ( prevEntry != null )
        {
            write( getString( "previous" ) + ": <a href=\"" + prevEntry.getId() + ".html\">" );
            content( prevEntry.getTitle() );
            write( "</a>" );
        }
        else
        {
            write( getString( "previous" ) + ": <a href=\"" + parent.getId() + ".html\">" );
            content( parent.getTitle() );
            write( "</a>" );
        }
    }

    /**
     * Add parent/up link.
     *
     * @param parent the parent IndexEntry.
     * @see org.apache.maven.doxia.book.services.renderer.xdoc.ChapterXdocBookSink#up()
     */
    protected void up( IndexEntry parent )
    {
        write( getString( "up" ) + ": <a href=\"" + parent.getId() + ".html\">" + parent.getTitle() + "</a>" );
    }

    /**
     * Add next link.
     *
     * @param parent the parent IndexEntry.
     * @param nextEntry the next IndexEntry.
     */
    protected void next( IndexEntry parent, IndexEntry nextEntry )
    {
        if ( nextEntry != null )
        {
            write( getString( "next" ) + ": <a href=\"" + nextEntry.getId() + ".html\">" );
            content( nextEntry.getTitle() );
            write( "</a>" );
        }
        else
        {
            IndexEntry nextChapter = parent.getNextEntry();

            if ( nextChapter == null )
            {
                write( "<i>End of book</i>" );
            }
            else
            {
                write( getString( "next" ) + ": <a href=\"" + nextChapter.getId() + ".html\">" );
                content( nextChapter.getTitle() );
                write( "</a>" );
            }
        }
    }
}
