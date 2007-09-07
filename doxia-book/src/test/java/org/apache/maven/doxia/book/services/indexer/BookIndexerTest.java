package org.apache.maven.doxia.book.services.indexer;

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

import org.apache.maven.doxia.book.context.BookContext;
import org.apache.maven.doxia.book.model.BookModel;
import org.apache.maven.doxia.book.services.io.BookIo;
import org.apache.maven.doxia.index.IndexEntry;
import org.apache.maven.doxia.util.HtmlTools;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class BookIndexerTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        BookIo io = (BookIo) lookup( BookIo.ROLE );

        BookIndexer indexer = (BookIndexer) lookup( BookIndexer.ROLE );

        BookModel book = io.readBook( getTestFile( "src/test/resources/book-1.xml" ) );

        BookContext context = new BookContext();

        io.loadFiles( context, FileUtils.getFiles( getTestFile( "src/test/resources/book-1" ), "*.apt", "" ) );

        indexer.indexBook( book, context );

        IndexEntry root = context.getIndex();

        assertNotNull( root );

        assertEquals( 2, root.getChildEntries().size() );

        IndexEntry c1 = assertIndexEntry( root, 0, "Chapter 1", "chapter-1", 2 );

        // -----------------------------------------------------------------------
        // Section 1
        // -----------------------------------------------------------------------

        IndexEntry s1 = assertIndexEntry( c1, 0, "Section 1", "section-1", 5 );

        IndexEntry ss1 = assertIndexEntry( s1, 0, "Subsection 1", "Subsection_1", 1 );

        assertIndexEntry( ss1, 0, "Subsubsection 1", "Subsubsection_1", 0 );

        assertIndexEntry( s1, 1, "Subsection 2", "Subsection_2", 0 );

        assertIndexEntry( s1, 2, "Subsection 3", "Subsection_3", 0 );

        assertIndexEntry( s1, 3, "Subsection 4", "Subsection_4", 0 );

        // -----------------------------------------------------------------------
        // Section 2
        // -----------------------------------------------------------------------

        IndexEntry s2 = assertIndexEntry( c1, 1, "Section 2", "section-2", 1 );

        assertIndexEntry( s2, 0, "Section 1.10.32 of \"de Finibus Bonorum et Malorum\", written by Cicero in 45 BC",
                          "Section_1.10.32_of_\"de_Finibus_Bonorum_et_Malorum\",_written_by_Cicero_in_45_BC", 0 );

        // -----------------------------------------------------------------------
        // Chapter 2
        // -----------------------------------------------------------------------

        IndexEntry c2 = assertIndexEntry( root, 1, "Chapter 2", "chapter-2", 2 );

        IndexEntry s3 = assertIndexEntry( c2, 0, "Section 3", "section-3", 1 );

        assertIndexEntry( s3, 0, "1914 translation by H. Rackham", "1914_translation_by_H._Rackham", 0 );

        IndexEntry s4 = assertIndexEntry( c2, 1, "Section 4", "section-4", 1 );

        assertIndexEntry( s4, 0, "Section 1.10.33 of \"de Finibus Bonorum et Malorum\", written by Cicero in 45 BC",
                          "Section_1.10.33_of_\"de_Finibus_Bonorum_et_Malorum\",_written_by_Cicero_in_45_BC", 0 );
    }

    private IndexEntry assertIndexEntry( IndexEntry parent, int childIndex, String title, String id, int childCount )
    {
        assertTrue( "parent: " + parent.getId() +  ", " +
            "required count: " + childCount + ", " +
            "actual count: " + parent.getChildEntries().size(),
                    childIndex < parent.getChildEntries().size() );

        IndexEntry indexEntry = (IndexEntry) parent.getChildEntries().get( childIndex );

        assertEquals( title, indexEntry.getTitle() );

        assertEquals( HtmlTools.encodeId( id ), indexEntry.getId() );

        assertEquals( childCount, indexEntry.getChildEntries().size() );

        return indexEntry;
    }
}
