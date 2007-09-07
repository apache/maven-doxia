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
 * A <code>XdocSink</code> implementation for section in a book
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class SectionXdocBookSink
    extends AbstractXdocBookSink
{
    /** indexEntry. */
    private IndexEntry indexEntry;

    /**
     * Default constructor.
     *
     * @param out the Writer to use.
     * @param indexEntry the IndexEntry.
     * @param i18n the I18N.
     */
    public SectionXdocBookSink( Writer out, IndexEntry indexEntry, I18N i18n )
    {
        super( out, i18n );

        this.indexEntry = indexEntry;
    }

    /**
     * @see org.apache.maven.doxia.book.services.renderer.xdoc.AbstractXdocBookSink#navigationPanel()
     */
    protected void navigationPanel()
    {
        markup( "<!--Navigation Panel-->" + EOL );

        markup( "<table width=\"100%\" align=\"center\">" + EOL );
        markup( "<tr>" + EOL );

        IndexEntry parent = indexEntry.getParent();

        // -----------------------------------------------------------------------
        // Prev
        // -----------------------------------------------------------------------

        IndexEntry prevEntry = indexEntry.getPrevEntry();

        markup( "<td><div align='left'>" );

        previous( parent, prevEntry );

        markup( "</div></td>" + EOL );

        // -----------------------------------------------------------------------
        // Parent
        // -----------------------------------------------------------------------

        markup( "<td><div align='center'>" );
        up( parent );
        markup( "</div></td>" + EOL );

        // -----------------------------------------------------------------------
        // Next
        // -----------------------------------------------------------------------

        IndexEntry nextEntry = indexEntry.getNextEntry();

        markup( "<td><div align='right'>" );

        next( parent, nextEntry );

        markup( "</div></td>" + EOL );

        markup( "</tr>" + EOL );
        markup( "</table>" + EOL );

        markup( "<!--End of Navigation Panel-->" + EOL );
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
            markup( getString( "previous" ) + ": <a href='" + prevEntry.getId() + ".html'>" );
            content( prevEntry.getTitle() );
            markup( "</a>" );
        }
        else
        {
            markup( getString( "previous" ) + ": <a href='" + parent.getId() + ".html'>" );
            content( parent.getTitle() );
            markup( "</a>" );
        }
    }

    /**
     * Add parent/up link.
     *
     * @param parent the parent IndexEntry.
     * @see org.apache.maven.doxia.book.services.renderer.xdoc.ChapterXdocBookSink#up(org.apache.maven.doxia.book.context.IndexEntry)
     */
    protected void up( IndexEntry parent )
    {
        markup( getString( "up" ) + ": <a href='" + parent.getId() + ".html'>" + parent.getTitle() + "</a>" );
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
            markup( getString( "next" ) + ": <a href='" + nextEntry.getId() + ".html'>" );
            content( nextEntry.getTitle() );
            markup( "</a>" );
        }
        else
        {
            IndexEntry nextChapter = parent.getNextEntry();

            if ( nextChapter == null )
            {
                markup( "<i>End of book</i>" );
            }
            else
            {
                markup( getString( "next" ) + ": <a href='" + nextChapter.getId() + ".html'>" );
                content( nextChapter.getTitle() );
                markup( "</a>" );
            }
        }
    }
}
