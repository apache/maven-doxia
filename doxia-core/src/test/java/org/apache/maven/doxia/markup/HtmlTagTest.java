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
package org.apache.maven.doxia.markup;

import javax.swing.text.html.HTML.Tag;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link HtmlTag} against the {@link HtmlMarkup} constants it will replace.
 */
class HtmlTagTest {

    /**
     * Every tag Doxia declares must be known to HtmlTag and must answer isBlock() exactly as it
     * does today. A tag whose block value moves changes the whitespace of every generated site.
     */
    @Test
    void knownTagsAnswerAsHtmlMarkupDoes() throws IllegalAccessException {
        List<Field> tagFields = new ArrayList<>();

        for (Field field : HtmlMarkup.class.getDeclaredFields()) {
            if (field.getType() == Tag.class) {
                tagFields.add(field);
            }
        }

        assertTrue(tagFields.size() > 100, "expected HtmlMarkup to declare the HTML5 tags");

        for (Field field : tagFields) {
            Tag tag = (Tag) field.get(null);
            HtmlTag htmlTag = HtmlTag.valueOf(tag.toString());

            assertNotNull(htmlTag, "HtmlTag does not know " + field.getName());
            assertEquals(tag.toString(), htmlTag.getName());
            assertEquals(tag.isBlock(), htmlTag.isBlock(), "block value moved for " + field.getName());
        }
    }

    @Test
    void unknownTagName() {
        assertNull(HtmlTag.valueOf("no-such-tag"));
    }

    @Test
    void equality() {
        assertEquals(HtmlTag.valueOf("p"), new HtmlTag("p", true));
        assertEquals(HtmlTag.valueOf("p").hashCode(), new HtmlTag("p", true).hashCode());
        assertEquals("p", HtmlTag.valueOf("p").toString());
    }
}
