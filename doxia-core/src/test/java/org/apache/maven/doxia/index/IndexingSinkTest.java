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

import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.sink.impl.SinkEventTestingSink;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IndexingSinkTest {

    @Test
    void getUniqueId() {
        try (IndexingSink sink = new IndexingSink(new IndexEntry("root"))) {
            assertEquals("root_1", sink.getUniqueId("root"));
            assertEquals("root_2", sink.getUniqueId("root"));
            assertEquals("newid", sink.getUniqueId("newid"));
        }
    }

    @Test
    void indexingSinkWithComplexSink() {
        SinkEventTestingSink resultSink = new SinkEventTestingSink();
        IndexingSink sink = new IndexingSink(resultSink);
        sink.section1();
        sink.sectionTitle1();
        sink.text("title1");
        sink.sectionTitle1_();
        sink.section2();
        sink.section3();
        sink.sectionTitle3();
        sink.text("Title 3");
        sink.sectionTitle3_();
        sink.section3_();
        sink.section2_();
        sink.section1_();
        sink.close();

        // make sure that all events are emitted downstream
        AbstractParserTest.assertSinkEquals(
                resultSink.getEventList().iterator(),
                "section1",
                "sectionTitle1",
                "text",
                "sectionTitle1_",
                "section2",
                "section3",
                "sectionTitle3",
                "text",
                "sectionTitle3_",
                "section3_",
                "section2_",
                "section1_",
                "close");

        // evaluate captured index data
        IndexEntry entry = sink.getRootEntry();
        assertIndexEntry("index", null, 1, entry);
        assertIndexEntry("title1", "title1", 1, entry.getFirstEntry());
        assertIndexEntry(null, null, 1, entry.getFirstEntry().getFirstEntry());
        assertIndexEntry(
                "Title_3", "Title 3", 0, entry.getFirstEntry().getFirstEntry().getFirstEntry());
    }

    private void assertIndexEntry(String id, String title, int numChildren, IndexEntry entry) {
        assertNotNull(entry);
        assertEquals(id, entry.getId());
        assertEquals(title, entry.getTitle());
        assertEquals(numChildren, entry.getChildEntries().size());
    }
}
