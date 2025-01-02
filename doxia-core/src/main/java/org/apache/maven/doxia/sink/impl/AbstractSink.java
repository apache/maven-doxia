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

import org.apache.maven.doxia.markup.Markup;
import org.apache.maven.doxia.sink.EmptyLocator;
import org.apache.maven.doxia.sink.Locator;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;

/**
 * An abstract base class that defines some convenience methods for sinks.
 * Also acts as compatibility bridge for Doxia 1.0 methods which have overloaded variants in Doxia &gt; 1.0 (taking an additional argument {@link SinkEventAttributes}).
 * This implementation just delegates the former to the latter with argument {@link SinkEventAttributes} being {@code null}.
 *
 * @author ltheussl
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @since 1.1
 */
public abstract class AbstractSink implements Sink, Markup {

    private Locator locator;

    // -- start default implementation for legacy doxia 1.0 methods which have overridden variants in Doxia 1.1 or 2.0,
    // all sink implementation derived from this must only override the Doxia > 1.0 variants
    @Override
    public final void head() {
        head(null);
    }

    @Override
    public final void title() {
        title(null);
    }

    @Override
    public final void author() {
        author(null);
    }

    @Override
    public final void date() {
        date(null);
    }

    @Override
    public final void body() {
        body(null);
    }

    @Override
    public final void article() {
        article(null);
    }

    @Override
    public final void navigation() {
        navigation(null);
    }

    @Override
    public final void sidebar() {
        sidebar(null);
    }

    @Override
    public final void sectionTitle() {
        // noop
    }

    @Override
    public final void section1() {
        section(1, null);
    }

    @Override
    public final void sectionTitle_() {
        // noop
    }

    @Override
    public final void section1_() {
        section_(1);
    }

    @Override
    public final void sectionTitle1() {
        sectionTitle(1, null);
    }

    @Override
    public final void sectionTitle1_() {
        sectionTitle_(1);
    }

    @Override
    public final void section2() {
        section(2, null);
    }

    @Override
    public final void section2_() {
        section_(2);
    }

    @Override
    public final void sectionTitle2() {
        sectionTitle(2, null);
    }

    @Override
    public final void sectionTitle2_() {
        sectionTitle_(2);
    }

    @Override
    public final void section3() {
        section(3, null);
    }

    @Override
    public final void section3_() {
        section_(3);
    }

    @Override
    public final void sectionTitle3() {
        sectionTitle(3, null);
    }

    @Override
    public final void sectionTitle3_() {
        sectionTitle_(3);
    }

    @Override
    public final void section4() {
        section(4, null);
    }

    @Override
    public final void section4_() {
        section_(4);
    }

    @Override
    public final void sectionTitle4() {
        sectionTitle(4, null);
    }

    @Override
    public final void sectionTitle4_() {
        sectionTitle_(4);
    }

    @Override
    public final void section5() {
        section(5, null);
    }

    @Override
    public final void section5_() {
        section_(5);
    }

    @Override
    public final void sectionTitle5() {
        sectionTitle(5, null);
    }

    @Override
    public final void sectionTitle5_() {
        sectionTitle_(5);
    }

    @Override
    public final void section6() {
        section(6, null);
    }

    @Override
    public final void section6_() {
        section_(6);
    }

    @Override
    public final void sectionTitle6() {
        sectionTitle(6, null);
    }

    @Override
    public final void sectionTitle6_() {
        sectionTitle_(6);
    }

    @Override
    public final void header() {
        header(null);
    }

    @Override
    public final void content() {
        content(null);
    }

    @Override
    public final void footer() {
        footer(null);
    }

    @Override
    public final void list() {
        list(null);
    }

    @Override
    public final void listItem() {
        listItem(null);
    }

    @Override
    public final void numberedList(int numbering) {
        numberedList(numbering, null);
    }

    @Override
    public final void numberedListItem() {
        numberedListItem(null);
    }

    @Override
    public final void definitionList() {
        definitionList(null);
    }

    @Override
    public final void definitionListItem() {
        definitionListItem(null);
    }

    @Override
    public final void definition() {
        definition(null);
    }

    @Override
    public final void definedTerm() {
        definedTerm(null);
    }

    @Override
    public final void figure() {
        figure(null);
    }

    @Override
    public final void figureCaption() {
        figureCaption(null);
    }

    @Override
    public final void figureGraphics(String name) {
        figureGraphics(name, null);
    }

    @Override
    public final void table() {
        table(null);
    }

    @Override
    public final void tableRows() {
        tableRows(null, false);
    }

    @Override
    public final void tableRow() {
        tableRow(null);
    }

    @Override
    public final void tableCell() {
        tableCell(null);
    }

    @Override
    public final void tableHeaderCell() {
        tableHeaderCell(null);
    }

    @Override
    public final void tableCaption() {
        tableCaption(null);
    }

    @Override
    public final void paragraph() {
        paragraph(null);
    }

    @Override
    public final void data(String value) {
        data(value, null);
    }

    @Override
    public final void time(String datetime) {
        time(datetime, null);
    }

    @Override
    public final void address() {
        address(null);
    }

    @Override
    public final void blockquote() {
        blockquote(null);
    }

    @Override
    public final void division() {
        division(null);
    }

    @Override
    public final void verbatim() {
        verbatim(null);
    }

    @Override
    public final void horizontalRule() {
        horizontalRule(null);
    }

    @Override
    public final void anchor(String name) {
        anchor(name, null);
    }

    @Override
    public final void link(String name) {
        link(name, null);
    }

    @Override
    public final void inline() {
        inline(null);
    }

    @Override
    public final void lineBreak() {
        lineBreak(null);
    }

    @Override
    public final void lineBreakOpportunity() {
        lineBreakOpportunity(null);
    }

    @Override
    public final void text(String text) {
        text(text, null);
    }
    // -- end default implementation for legacy doxia 1.0 methods which have overridden variants in Doxia 1.1 or 2.0

    /**
     * Parses the given String and replaces all occurrences of
     * '\n', '\r' and '\r\n' with the system EOL. All Sinks should
     * make sure that text output is filtered through this method.
     *
     * @param text the text to scan.
     *      May be null in which case null is returned.
     * @return a String that contains only System EOLs.
     */
    protected static String unifyEOLs(String text) {
        if (text == null) {
            return null;
        }

        int length = text.length();

        StringBuilder buffer = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            if (text.charAt(i) == '\r') {
                if ((i + 1) < length && text.charAt(i + 1) == '\n') {
                    i++;
                }

                buffer.append(EOL);
            } else if (text.charAt(i) == '\n') {
                buffer.append(EOL);
            } else {
                buffer.append(text.charAt(i));
            }
        }

        return buffer.toString();
    }

    /**
     * This is called in {@link #head()} or in {@link #close()}, and can be used
     * to set the sink into a clear state so it can be re-used.
     *
     * @since 1.1.2
     */
    protected void init() {
        // nop
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    @Override
    public Locator getDocumentLocator() {
        if (locator == null) {
            return EmptyLocator.INSTANCE;
        }
        return locator;
    }

    protected String getLocationLogPrefix() {
        return formatLocation(getDocumentLocator());
    }

    /**
     * Creates a string with line/column information. Inspired by
     * {@code o.a.m.model.building.ModelProblemUtils.formatLocation(...)}.
     *
     * @param locator The locator must not be {@code null}.
     * @return The formatted location or an empty string if unknown, never {@code null}.
     */
    public static String formatLocation(Locator locator) {
        StringBuilder buffer = new StringBuilder();

        if (locator.getReference() != null) {
            buffer.append(locator.getReference());
        } else {
            buffer.append("Unknown source");
        }
        if (locator.getLineNumber() > 0) {
            buffer.append(", line ").append(locator.getLineNumber());
        }
        if (locator.getColumnNumber() > 0) {
            buffer.append(", column ").append(locator.getColumnNumber());
        }
        if (buffer.length() > 0) {
            buffer.append(": ");
        }
        return buffer.toString();
    }
}
