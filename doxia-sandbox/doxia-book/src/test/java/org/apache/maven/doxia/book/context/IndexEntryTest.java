package org.apache.maven.doxia.book.context;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class IndexEntryTest
    extends TestCase
{
    public void testIndexEntry()
    {
        IndexEntry root = new IndexEntry( null );

        assertIndexEntry( root, null, 0, null, null );

        // -----------------------------------------------------------------------
        // Chapter 1
        // -----------------------------------------------------------------------

        IndexEntry chapter1 = new IndexEntry( root, "chapter-1" );

        assertIndexEntry( root, null, 1, null, null );

        assertIndexEntry( chapter1, root, 0, null, null );

        // -----------------------------------------------------------------------
        // Chapter 2
        // -----------------------------------------------------------------------

        IndexEntry chapter2 = new IndexEntry( root, "chapter-2" );

        assertIndexEntry( root, null, 2, null, null );

        assertIndexEntry( chapter1, root, 0, null, chapter2 );
        assertIndexEntry( chapter2, root, 0, chapter1, null );
    }

    private void assertIndexEntry( IndexEntry entry, IndexEntry parent, int childCount, IndexEntry prevEntry, IndexEntry nextEntry )
    {
        assertEquals( parent, entry.getParent() );

        assertEquals( childCount, entry.getChildEntries().size() );

        assertEquals( prevEntry, entry.getPrevEntry() );

        assertEquals( nextEntry, entry.getNextEntry() );
    }
}
