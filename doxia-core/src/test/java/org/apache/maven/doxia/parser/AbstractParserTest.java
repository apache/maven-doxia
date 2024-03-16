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
package org.apache.maven.doxia.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.ListIterator;

import org.apache.maven.doxia.AbstractModuleTest;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.impl.SinkEventElement;
import org.apache.maven.doxia.sink.impl.SinkEventTestingSink;
import org.apache.maven.doxia.sink.impl.SinkWrapper;
import org.apache.maven.doxia.sink.impl.SinkWrapperFactory;
import org.apache.maven.doxia.sink.impl.TextSink;
import org.apache.maven.doxia.sink.impl.WellformednessCheckingSink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Test the parsing of sample input files.
 * <br>
 * <b>Note</b>: you have to provide a sample "test." + outputExtension()
 * file in the test resources directory if you extend this class.
 * @since 1.0
 */
public abstract class AbstractParserTest extends AbstractModuleTest {
    /**
     * Example text which usually requires escaping in different markup languages as they have special meaning there
     */
    public static final String TEXT_WITH_SPECIAL_CHARS = "<>{}=#*";

    /**
     * Create a new instance of the parser to test.
     *
     * @return the parser to test.
     */
    protected abstract AbstractParser createParser();

    /**
     * Returns the directory where all parser test output will go.
     *
     * @return The test output directory.
     */
    protected String getOutputDir() {
        return "parser/";
    }

    /**
     * Parse a test document '"test." + outputExtension()'
     * with parser from {@link #createParser()}, and output to a new
     * {@link WellformednessCheckingSink}. Asserts that output is well-formed.
     *
     * @throws IOException if the test document cannot be read.
     * @throws ParseException if the test document cannot be parsed.
     */
    @Test
    public final void testParser() throws IOException, ParseException {
        WellformednessCheckingSink sink = new WellformednessCheckingSink();

        try (Reader reader = getTestReader("test", outputExtension())) {
            createParser().parse(reader, sink);

            assertTrue(
                    sink.isWellformed(),
                    "Parser output not well-formed, last offending element: " + sink.getOffender());
        }
    }

    /**
     * Parse a test document '"test." + outputExtension()'
     * with parser from {@link #createParser()}, and output to a text file,
     * using the {@link org.apache.maven.doxia.sink.impl.TextSink TextSink}.
     *
     * @throws IOException if the test document cannot be read.
     * @throws ParseException if the test document cannot be parsed.
     */
    @Test
    public final void testDocument() throws IOException, ParseException {
        try (Writer writer = getTestWriter("test", "txt");
                Reader reader = getTestReader("test", outputExtension())) {
            Sink sink = new TextSink(writer);
            createParser().parse(reader, sink);
        }
    }

    private static final class TestSinkWrapperFactory implements SinkWrapperFactory {

        private final int priority;

        public TestSinkWrapperFactory(int priority) {
            super();
            this.priority = priority;
        }

        @Override
        public Sink createWrapper(Sink sink) {
            return new SinkWrapper(sink) {

                @Override
                public void text(String text, SinkEventAttributes attributes) {
                    super.text("beforeWrapper" + priority + text + "afterWrapper" + priority, attributes);
                }
            };
        }

        @Override
        public int getPriority() {
            return priority;
        }
    }

    @Test
    public final void testSinkWrapper() throws ParseException, IOException {
        Parser parser = createParser();

        parser.addSinkWrapperFactory(new TestSinkWrapperFactory(1));
        parser.addSinkWrapperFactory(new TestSinkWrapperFactory(0));
        parser.addSinkWrapperFactory(new TestSinkWrapperFactory(2)); // this must be the first in the pipeline

        try (StringWriter writer = new StringWriter();
                Reader reader = getTestReader("test", outputExtension())) {
            Sink sink = new TextSink(writer);
            parser.parse(reader, sink);

            // assert order of sink wrappers: wrapper with lower prio is called with prefix from wrapper with higher
            // prio, therefore lower prio prefix/suffix is emitted prior higher prio prefix/suffix
            assertTrue(writer.toString().contains("beforeWrapper0beforeWrapper1beforeWrapper2"));
            assertTrue(writer.toString().contains("afterWrapper2afterWrapper1afterWrapper0"));
        }
    }

    /**
     * Override this method if the parser always emits some static prefix for incomplete documents
     * and consume the prefix related events from the given {@link Iterator}.
     * @param eventIterator the iterator
     */
    protected void assertEventPrefix(Iterator<SinkEventElement> eventIterator) {
        // do nothing by default, i.e. assume no prefix
    }

    /**
     * Override this method if the parser always emits some static suffix for incomplete documents
     * and consume the suffix related events from the given {@link Iterator}.
     * @param eventIterator the iterator
     */
    protected void assertEventSuffix(Iterator<SinkEventElement> eventIterator) {
        assertFalse(eventIterator.hasNext(), "didn't expect any further events but got at least one");
    }

    /**
     * @return markup representing the verbatim text {@value #TEXT_WITH_SPECIAL_CHARS} (needs to be properly escaped).
     * {@code null} can be returned to skip the test for a particular parser.
     */
    protected abstract String getVerbatimSource();

    /**
     * Test a verbatim block (no code) given through {@link #getVerbatimSource()}
     * @throws ParseException
     */
    @Test
    public void testVerbatim() throws ParseException {
        String source = getVerbatimSource();
        assumeTrue(source != null, "parser does not support simple verbatim text");
        AbstractParser parser = createParser();
        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse(source, sink);
        ListIterator<SinkEventElement> it = sink.getEventList().listIterator();
        assertEventPrefix(it);
        assertEquals("verbatim", it.next().getName());
        assertConcatenatedTextEquals(it, TEXT_WITH_SPECIAL_CHARS, true);
        assertEquals("verbatim_", it.next().getName());
        assertEventSuffix(it);
    }

    /**
     * @return markup representing the verbatim code block {@value #TEXT_WITH_SPECIAL_CHARS} (needs to be properly escaped).
     * {@code null} can be returned to skip the test for a particular parser.
     */
    protected abstract String getVerbatimCodeSource();

    /**
     * Test a verbatim code block given through {@link #getVerbatimCodeSource()}
     * @throws ParseException
     */
    @Test
    public void testVerbatimCode() throws ParseException {
        String source = getVerbatimCodeSource();
        assumeTrue(source != null, "parser does not support verbatim code");
        AbstractParser parser = createParser();
        SinkEventTestingSink sink = new SinkEventTestingSink();

        parser.parse(source, sink);
        ListIterator<SinkEventElement> it = sink.getEventList().listIterator();
        assertEventPrefix(it);
        SinkEventElement verbatimEvt = it.next();
        assertEquals("verbatim", verbatimEvt.getName());
        SinkEventAttributeSet atts = (SinkEventAttributeSet) verbatimEvt.getArgs()[0];

        // either verbatim event has attribute "source" or additional "inline" event
        boolean isInlineCode;
        if (atts.isEmpty()) {
            isInlineCode = true;
            assertSinkAttributesEqual(it.next(), "inline", SinkEventAttributeSet.Semantics.CODE);
        } else {
            isInlineCode = false;
            assertEquals(SinkEventAttributeSet.SOURCE, atts);
        }
        assertConcatenatedTextEquals(it, TEXT_WITH_SPECIAL_CHARS, true);
        if (isInlineCode) {
            assertEquals("inline_", it.next().getName());
        }
        assertEquals("verbatim_", it.next().getName());
        assertEventSuffix(it);
    }

    public static void assertSinkEquals(SinkEventElement element, String name, Object... args) {
        Assertions.assertEquals(name, element.getName(), "Name of element doesn't match");
        Assertions.assertArrayEquals(args, element.getArgs(), "Arguments don't match");
    }

    public static void assertSinkAttributeEquals(SinkEventElement element, String name, String attr, String value) {
        Assertions.assertEquals(name, element.getName());
        SinkEventAttributeSet atts = (SinkEventAttributeSet) element.getArgs()[0];
        Assertions.assertEquals(value, atts.getAttribute(attr));
    }

    public static void assertSinkAttributesEqual(
            SinkEventElement element, String name, SinkEventAttributes expectedAttributes) {
        Assertions.assertEquals(name, element.getName());
        SinkEventAttributeSet atts = (SinkEventAttributeSet) element.getArgs()[0];
        Assertions.assertEquals(expectedAttributes, atts);
    }

    /**
     * Consumes all consecutive text events from the given {@link ListIterator} and compares the concatenated text with the expected text
     * @param it the iterator to traverse, is positioned to the last text event once this method finishes
     * @param expectedText the expected text which is compared with the concatenated text of all text events
     * @param trimText {@code true} to trim the actual text before comparing with the expected one, otherwise compare without trimming
     */
    void assertConcatenatedTextEquals(ListIterator<SinkEventElement> it, String expectedText, boolean trimText) {
        StringBuilder builder = new StringBuilder();
        while (it.hasNext()) {
            SinkEventElement currentEvent = it.next();
            if (currentEvent.getName() != "text") {
                it.previous();
                break;
            }
            builder.append(currentEvent.getArgs()[0]);
        }
        String actualValue = builder.toString();
        if (trimText) {
            actualValue = actualValue.trim();
        }
        assertEquals(expectedText, actualValue);
    }

    public static void assertSinkEquals(Iterator<SinkEventElement> it, String... names) {
        StringBuilder expected = new StringBuilder();
        StringBuilder actual = new StringBuilder();

        for (String name : names) {
            expected.append(name).append('\n');
        }

        while (it.hasNext()) {
            actual.append(it.next().getName()).append('\n');
        }

        Assertions.assertEquals(expected.toString(), actual.toString());
    }

    public static void assertSinkStartsWith(Iterator<SinkEventElement> it, String... names) {
        StringBuilder expected = new StringBuilder();
        StringBuilder actual = new StringBuilder();

        for (String name : names) {
            expected.append(name).append('\n');
            if (it.hasNext()) {
                actual.append(it.next().getName()).append('\n');
            }
        }
        Assertions.assertEquals(expected.toString(), actual.toString());
    }
}
