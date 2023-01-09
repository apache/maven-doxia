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
package org.apache.maven.doxia.sink.impl;

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

import javax.swing.text.AttributeSet;

import java.util.Enumeration;

import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test SinkEventAttributeSet.
 *
 * @author ltheussl
 */
public class SinkEventAttributeSetTest {
    private SinkEventAttributeSet sinkEventAttributeSet;

    @BeforeEach
    public void setUp() {
        this.sinkEventAttributeSet = new SinkEventAttributeSet();
    }

    /**
     * Test of constructors, of class SinkEventAttributeSet.
     */
    @Test
    public void testConstructor() {
        try {
            new SinkEventAttributeSet("key");
            fail("missing attribute value!");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    /**
     * Test of isEmpty method, of class SinkEventAttributeSet.
     */
    @Test
    public void testIsEmpty() {
        assertTrue(sinkEventAttributeSet.isEmpty());
        sinkEventAttributeSet.addAttributes(SinkEventAttributeSet.BOLD);
        assertFalse(sinkEventAttributeSet.isEmpty());
    }

    /**
     * Test of getAttributeCount method, of class SinkEventAttributeSet.
     */
    @Test
    public void testGetAttributeCount() {
        assertEquals(0, sinkEventAttributeSet.getAttributeCount());
        sinkEventAttributeSet.addAttribute("name1", "value1");
        assertEquals(1, sinkEventAttributeSet.getAttributeCount());
        sinkEventAttributeSet.removeAttribute("name2");
        assertEquals(1, sinkEventAttributeSet.getAttributeCount());
        sinkEventAttributeSet.removeAttribute("name1");
        assertEquals(0, sinkEventAttributeSet.getAttributeCount());

        sinkEventAttributeSet.addAttributes(SinkEventAttributeSet.BOLD);
        assertEquals(1, sinkEventAttributeSet.getAttributeCount());
        sinkEventAttributeSet.removeAttributes(SinkEventAttributeSet.BOXED);
        assertEquals(1, sinkEventAttributeSet.getAttributeCount());
        sinkEventAttributeSet.removeAttributes(SinkEventAttributeSet.BOLD);
        assertEquals(0, sinkEventAttributeSet.getAttributeCount());
    }

    /**
     * Test of isDefined method, of class SinkEventAttributeSet.
     */
    @Test
    public void testIsDefined() {
        assertFalse(sinkEventAttributeSet.isDefined(SinkEventAttributes.DECORATION));
        sinkEventAttributeSet.addAttributes(SinkEventAttributeSet.BOXED);
        assertTrue(sinkEventAttributeSet.isDefined(SinkEventAttributes.DECORATION));
    }

    /**
     * Test of isEqual method, of class SinkEventAttributeSet.
     */
    @Test
    public void testIsEqual() {
        SinkEventAttributes instance = new SinkEventAttributeSet(SinkEventAttributeSet.BOLD);
        sinkEventAttributeSet.addAttributes(SinkEventAttributeSet.BOLD);
        assertTrue(instance.isEqual(sinkEventAttributeSet));
        instance.addAttributes(SinkEventAttributeSet.BOXED);
        assertFalse(instance.isEqual(sinkEventAttributeSet));
    }

    /**
     * Test of equals method, of class SinkEventAttributeSet.
     */
    @Test
    @SuppressWarnings("SimplifiableJUnitAssertion")
    public void testEquals() {
        assertFalse(sinkEventAttributeSet.equals(null));
        //noinspection EqualsWithItself
        assertTrue(sinkEventAttributeSet.equals(sinkEventAttributeSet));

        SinkEventAttributes instance = new SinkEventAttributeSet(SinkEventAttributeSet.BOLD);
        sinkEventAttributeSet.addAttributes(SinkEventAttributeSet.BOLD);
        assertTrue(instance.equals(sinkEventAttributeSet));
        instance.addAttributes(SinkEventAttributeSet.BOXED);
        assertFalse(instance.equals(sinkEventAttributeSet));
    }

    /**
     * Test of copyAttributes method, of class SinkEventAttributeSet.
     */
    @Test
    public void testCopyAttributes() {
        sinkEventAttributeSet.addAttributes(SinkEventAttributeSet.ITALIC);
        AttributeSet instance = sinkEventAttributeSet.copyAttributes();
        assertTrue(instance.isEqual(sinkEventAttributeSet));
    }

    /**
     * Test of getAttributeNames method, of class SinkEventAttributeSet.
     */
    @Test
    public void testGetAttributeNames() {
        sinkEventAttributeSet.addAttributes(SinkEventAttributeSet.UNDERLINE);
        Enumeration<String> result = sinkEventAttributeSet.getAttributeNames();
        assertEquals("decoration", result.nextElement());
        assertFalse(result.hasMoreElements());
    }

    /**
     * Test of getAttribute method, of class SinkEventAttributeSet.
     */
    @Test
    public void testGetAttribute() {
        sinkEventAttributeSet.addAttribute("key", "value");
        assertEquals("value", sinkEventAttributeSet.getAttribute("key"));
        assertNull(sinkEventAttributeSet.getAttribute("bla"));
    }

    /**
     * Test of containsAttribute method, of class SinkEventAttributeSet.
     */
    @Test
    public void testContainsAttribute() {
        sinkEventAttributeSet.addAttribute("key", "value");
        assertTrue(sinkEventAttributeSet.containsAttribute("key", "value"));
        assertFalse(sinkEventAttributeSet.containsAttribute("key", "valu"));
    }

    /**
     * Test of containsAttributes method, of class SinkEventAttributeSet.
     */
    @Test
    public void testContainsAttributes() {
        sinkEventAttributeSet.addAttributes(SinkEventAttributeSet.JUSTIFY);
        assertTrue(sinkEventAttributeSet.containsAttributes(SinkEventAttributeSet.JUSTIFY));
        assertFalse(sinkEventAttributeSet.containsAttributes(SinkEventAttributeSet.BOXED));
    }

    /**
     * Test of addAttribute method, of class SinkEventAttributeSet.
     */
    @Test
    public void testAddAttribute() {
        assertFalse(sinkEventAttributeSet.containsAttribute("key", "value"));
        sinkEventAttributeSet.addAttribute("key", "value");
        assertTrue(sinkEventAttributeSet.containsAttribute("key", "value"));
        sinkEventAttributeSet.removeAttribute("key");
        assertFalse(sinkEventAttributeSet.containsAttribute("key", "value"));
    }

    /**
     * Test of add/removeAttributes methods, of class SinkEventAttributeSet.
     */
    @Test
    public void testAddAttributes() {
        assertFalse(sinkEventAttributeSet.containsAttributes(SinkEventAttributeSet.JUSTIFY));
        sinkEventAttributeSet.addAttributes(SinkEventAttributeSet.JUSTIFY);
        assertTrue(sinkEventAttributeSet.containsAttributes(SinkEventAttributeSet.JUSTIFY));

        sinkEventAttributeSet.removeAttributes(SinkEventAttributeSet.JUSTIFY);
        assertFalse(sinkEventAttributeSet.containsAttributes(SinkEventAttributeSet.JUSTIFY));

        sinkEventAttributeSet.addAttributes(SinkEventAttributeSet.JUSTIFY);
        sinkEventAttributeSet.removeAttributes(SinkEventAttributeSet.JUSTIFY.getAttributeNames());
        assertFalse(sinkEventAttributeSet.containsAttributes(SinkEventAttributeSet.JUSTIFY));

        sinkEventAttributeSet.setResolveParent(SinkEventAttributeSet.JUSTIFY);
        assertTrue(sinkEventAttributeSet.containsAttributes(SinkEventAttributeSet.JUSTIFY));

        sinkEventAttributeSet.removeAttributes((AttributeSet) null); // should do nothing
    }

    /**
     * Test of getResolveParent method, of class SinkEventAttributeSet.
     */
    @Test
    public void testGetResolveParent() {
        assertNull(sinkEventAttributeSet.getResolveParent());
        sinkEventAttributeSet.setResolveParent(SinkEventAttributeSet.CENTER);
        assertNotNull(sinkEventAttributeSet.getResolveParent());
    }

    /**
     * Test of clone method, of class SinkEventAttributeSet.
     */
    @Test
    public void testClone() {
        Object result = sinkEventAttributeSet.clone();
        assertEquals(sinkEventAttributeSet, result);

        sinkEventAttributeSet.addAttributes(SinkEventAttributeSet.MONOSPACED);
        assertNotEquals(sinkEventAttributeSet, result);

        result = sinkEventAttributeSet.clone();
        assertEquals(sinkEventAttributeSet, result);
        sinkEventAttributeSet.setResolveParent(SinkEventAttributeSet.CENTER);
        // assertFalse( sinkEventAttributeSet.equals( result ) );

        result = sinkEventAttributeSet.clone();
        assertEquals(sinkEventAttributeSet, result);
        sinkEventAttributeSet.setResolveParent(SinkEventAttributeSet.BOXED);
        // assertFalse( sinkEventAttributeSet.equals( result ) );
    }

    /**
     * Test of hashCode method, of class SinkEventAttributeSet.
     */
    @Test
    public void testHashCode() {
        int oldValue = sinkEventAttributeSet.hashCode();
        sinkEventAttributeSet.addAttributes(SinkEventAttributeSet.BOLD);
        int newValue = sinkEventAttributeSet.hashCode();
        assertNotEquals(oldValue, newValue);

        oldValue = newValue;
        sinkEventAttributeSet.setResolveParent(SinkEventAttributeSet.CENTER);
        newValue = sinkEventAttributeSet.hashCode();
        assertNotEquals(oldValue, newValue);
    }

    /**
     * Test of toString method, of class SinkEventAttributeSet.
     */
    @Test
    public void testToString() {
        String expected = "";
        assertEquals(expected, sinkEventAttributeSet.toString());

        sinkEventAttributeSet.addAttributes(SinkEventAttributeSet.BOXED);
        expected = " decoration=boxed";
        assertEquals(expected, sinkEventAttributeSet.toString());

        sinkEventAttributeSet.addAttributes(SinkEventAttributeSet.CENTER);
        expected = " decoration=boxed align=center";
        assertEquals(expected, sinkEventAttributeSet.toString());
    }
}
