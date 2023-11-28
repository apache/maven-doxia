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
package org.apache.maven.doxia.index;

import org.apache.maven.doxia.index.IndexEntry.Type;
import org.apache.maven.doxia.sink.Sink;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 */
public class IndexEntryTest {
    /**
     * Test IndexEntry.
     */
    @Test
    public void testIndexEntry() {
        IndexEntry root = new IndexEntry(null);

        assertIndexEntry(root, Type.UNKNOWN, null, 0, null, null);

        // -----------------------------------------------------------------------
        // Chapter 1
        // -----------------------------------------------------------------------

        IndexEntry chapter1 = new IndexEntry(root, "chapter-1", Type.SECTION_1);

        assertIndexEntry(root, Type.UNKNOWN, null, 1, null, null);

        assertIndexEntry(chapter1, Type.SECTION_1, root, 0, null, null);

        // -----------------------------------------------------------------------
        // Chapter 2
        // -----------------------------------------------------------------------

        IndexEntry chapter2 = new IndexEntry(root, "chapter-2", Type.SECTION_1);

        assertIndexEntry(root, Type.UNKNOWN, null, 2, null, null);

        assertIndexEntry(chapter1, Type.SECTION_1, root, 0, null, chapter2);
        assertIndexEntry(chapter2, Type.SECTION_1, root, 0, chapter1, null);

        chapter2.setTitle("Title 2");
        assertTrue(chapter2.toString().contains("Title 2"));
    }

    private void assertIndexEntry(
            IndexEntry entry,
            Type type,
            IndexEntry parent,
            int childCount,
            IndexEntry prevEntry,
            IndexEntry nextEntry) {
        assertEquals(type, entry.getType());

        assertEquals(parent, entry.getParent());

        assertEquals(childCount, entry.getChildEntries().size());

        assertEquals(prevEntry, entry.getPrevEntry());

        assertEquals(nextEntry, entry.getNextEntry());
    }

    @Test
    public void testTypeFromSectionLevel() {
        assertThrows(IllegalArgumentException.class, () -> Type.fromSectionLevel(0));
        assertEquals(Type.SECTION_3, Type.fromSectionLevel(Sink.SECTION_LEVEL_3));
        assertThrows(IllegalArgumentException.class, () -> Type.fromSectionLevel(7));
    }
}
