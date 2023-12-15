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

import java.io.IOException;
import java.io.Writer;

import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple text-based implementation of the <code>Sink</code> interface.
 * Useful for testing purposes.
 */
public class TextSink extends AbstractSink {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextSink.class);

    /** For writing the result. */
    private final Writer out;

    /** Constructor.
     * @param writer The writer for writing the result.
     */
    public TextSink(Writer writer) {
        this.out = writer;
    }

    @Override
    public void head_() {
        writeln("end:head");
    }

    @Override
    public void body_() {
        writeln("end:body");
    }

    @Override
    public void article_() {
        writeln("end:article");
    }

    @Override
    public void navigation_() {
        writeln("end:navigation");
    }

    @Override
    public void sidebar_() {
        writeln("end:sidebar");
    }

    @Override
    public void list_() {
        writeln("end:list");
    }

    @Override
    public void listItem_() {
        writeln("end:listItem");
    }

    @Override
    public void numberedList_() {
        writeln("end:numberedList");
    }

    @Override
    public void numberedListItem_() {
        writeln("end:numberedListItem");
    }

    @Override
    public void definitionList_() {
        writeln("end:definitionList");
    }

    @Override
    public void definitionListItem_() {
        writeln("end:definitionListItem");
    }

    @Override
    public void definition_() {
        writeln("end:definition");
    }

    @Override
    public void figure_() {
        writeln("end:figure");
    }

    @Override
    public void table_() {
        writeln("end:table");
    }

    @Override
    public void tableRows_() {
        writeln("end:tableRows");
    }

    @Override
    public void tableRow_() {
        writeln("end:tableRow");
    }

    @Override
    public void title_() {
        writeln("end:title");
    }

    @Override
    public void author_() {
        writeln("end:author");
    }

    @Override
    public void date_() {
        writeln("end:date");
    }

    @Override
    public void header_() {
        writeln("end:header");
    }

    @Override
    public void content_() {
        writeln("end:content");
    }

    @Override
    public void footer_() {
        writeln("end:footer");
    }

    @Override
    public void paragraph_() {
        writeln("end:paragraph");
    }

    @Override
    public void data_() {
        writeln("end:data");
    }

    @Override
    public void time_() {
        writeln("end:time");
    }

    @Override
    public void address_() {
        writeln("end:address");
    }

    @Override
    public void blockquote_() {
        writeln("end:blockquote");
    }

    @Override
    public void division_() {
        writeln("end:division");
    }

    @Override
    public void verbatim_() {
        writeln("end:verbatim");
    }

    @Override
    public void definedTerm_() {
        writeln("end:definedTerm");
    }

    @Override
    public void figureCaption_() {
        writeln("end:figureCaption");
    }

    @Override
    public void tableCell_() {
        writeln("end:tableCell");
    }

    @Override
    public void tableHeaderCell_() {
        writeln("end:tableHeaderCell");
    }

    @Override
    public void tableCaption_() {
        writeln("end:tableCaption");
    }

    @Override
    public void pageBreak() {
        write("pageBreak");
    }

    @Override
    public void anchor_() {
        writeln("end:anchor");
    }

    @Override
    public void link_() {
        writeln("end:link");
    }

    @Override
    public void inline_() {
        writeln("end:inline");
    }

    @Override
    public void italic() {
        write("begin:italic");
    }

    @Override
    public void italic_() {
        writeln("end:italic");
    }

    @Override
    public void bold() {
        write("begin:bold");
    }

    @Override
    public void bold_() {
        writeln("end:bold");
    }

    @Override
    public void monospaced() {
        write("begin:monospaced");
    }

    @Override
    public void monospaced_() {
        writeln("end:monospaced");
    }

    @Override
    public void nonBreakingSpace() {
        write("nonBreakingSpace");
    }

    @Override
    public void rawText(String text) {
        write("rawText: " + text);
    }

    @Override
    public void comment(String comment) {
        write("comment: " + comment);
    }

    @Override
    public void flush() {
        try {
            out.flush();
        } catch (IOException e) {
            LOGGER.warn("Could not flush sink", e);
        }
    }

    @Override
    public void close() {
        try {
            out.close();
        } catch (IOException e) {
            LOGGER.warn("Could not close sink", e);
        }
    }

    @Override
    public void head(SinkEventAttributes attributes) {
        writeln("begin:head");
    }

    @Override
    public void title(SinkEventAttributes attributes) {
        writeln("begin:title");
    }

    @Override
    public void author(SinkEventAttributes attributes) {
        writeln("begin:author");
    }

    @Override
    public void date(SinkEventAttributes attributes) {
        writeln("begin:date");
    }

    @Override
    public void body(SinkEventAttributes attributes) {
        writeln("begin:body");
    }

    @Override
    public void article(SinkEventAttributes attributes) {
        writeln("begin:article");
    }

    @Override
    public void navigation(SinkEventAttributes attributes) {
        writeln("begin:navigation");
    }

    @Override
    public void sidebar(SinkEventAttributes attributes) {
        writeln("begin:head");
    }

    @Override
    public void section(int level, SinkEventAttributes attributes) {
        write("begin:section" + level);
    }

    @Override
    public void section_(int level) {
        writeln("end:section" + level);
    }

    @Override
    public void sectionTitle(int level, SinkEventAttributes attributes) {
        write("begin:sectionTitle" + level);
    }

    @Override
    public void sectionTitle_(int level) {
        writeln("end:sectionTitle" + level);
    }

    @Override
    public void header(SinkEventAttributes attributes) {
        write("begin:header");
    }

    @Override
    public void content(SinkEventAttributes attributes) {
        write("begin:content");
    }

    @Override
    public void footer(SinkEventAttributes attributes) {
        write("begin:footer");
    }

    @Override
    public void list(SinkEventAttributes attributes) {
        writeln("begin:list");
    }

    @Override
    public void listItem(SinkEventAttributes attributes) {
        write("begin:listItem");
    }

    @Override
    public void numberedList(int numbering, SinkEventAttributes attributes) {
        writeln("begin:numberedList, numbering: " + numbering);
    }

    @Override
    public void numberedListItem(SinkEventAttributes attributes) {
        write("begin:numberedListItem");
    }

    @Override
    public void definitionList(SinkEventAttributes attributes) {
        writeln("begin:definitionList");
    }

    @Override
    public void definitionListItem(SinkEventAttributes attributes) {
        write("begin:definitionListItem");
    }

    @Override
    public void definition(SinkEventAttributes attributes) {
        write("begin:definition");
    }

    @Override
    public void definedTerm(SinkEventAttributes attributes) {
        write("begin:definedTerm");
    }

    @Override
    public void figure(SinkEventAttributes attributes) {
        write("begin:figure" + SinkUtils.getAttributeString(attributes));
    }

    @Override
    public void figureCaption(SinkEventAttributes attributes) {
        write("begin:figureCaption");
    }

    @Override
    public void figureGraphics(String src, SinkEventAttributes attributes) {
        write("figureGraphics, name: " + src);
    }

    @Override
    public void table(SinkEventAttributes attributes) {
        writeln("begin:table");
    }

    @Override
    public void tableRows(int[] justification, boolean grid) {
        writeln("begin:tableRows");
    }

    @Override
    public void tableRow(SinkEventAttributes attributes) {
        write("begin:tableRow");
    }

    @Override
    public void tableCell(SinkEventAttributes attributes) {
        write("begin:tableCell");
    }

    @Override
    public void tableHeaderCell(SinkEventAttributes attributes) {
        write("begin:tableHeaderCell");
    }

    @Override
    public void tableCaption(SinkEventAttributes attributes) {
        write("begin:tableCaption");
    }

    @Override
    public void paragraph(SinkEventAttributes attributes) {
        write("begin:paragraph");
    }

    @Override
    public void data(String value, SinkEventAttributes attributes) {
        write("begin:data, value: " + value);
    }

    @Override
    public void time(String datetime, SinkEventAttributes attributes) {
        write("begin:time, datetime: " + datetime);
    }

    @Override
    public void address(SinkEventAttributes attributes) {
        write("begin:address");
    }

    @Override
    public void blockquote(SinkEventAttributes attributes) {
        write("begin:blockquote");
    }

    @Override
    public void division(SinkEventAttributes attributes) {
        write("begin:division");
    }

    @Override
    public void verbatim(SinkEventAttributes attributes) {
        boolean source = false;

        if (attributes != null && attributes.isDefined(SinkEventAttributes.DECORATION)) {
            source = "source"
                    .equals(attributes
                            .getAttribute(SinkEventAttributes.DECORATION)
                            .toString());
        }

        write("begin:verbatim, source: " + source);
    }

    @Override
    public void horizontalRule(SinkEventAttributes attributes) {
        write("horizontalRule");
    }

    @Override
    public void anchor(String name, SinkEventAttributes attributes) {
        write("begin:anchor, name: " + name);
    }

    @Override
    public void link(String name, SinkEventAttributes attributes) {
        write("begin:link, name: " + name);
    }

    @Override
    public void inline(SinkEventAttributes attributes) {
        write("begin:inline");
    }

    @Override
    public void lineBreak(SinkEventAttributes attributes) {
        write("lineBreak");
    }

    @Override
    public void lineBreakOpportunity(SinkEventAttributes attributes) {
        write("lineBreakOpportunity");
    }

    @Override
    public void text(String text, SinkEventAttributes attributes) {
        write("text: " + text);
    }

    @Override
    public void unknown(String name, Object[] requiredParams, SinkEventAttributes attributes) {
        write("unknown: " + name);
    }

    /**
     * Writes the given string + EOL.
     *
     * @param text The text to write.
     */
    private void write(String text) {
        try {
            out.write(text + EOL);
        } catch (IOException e) {
            LOGGER.warn("Could not write to sink", e);
        }
    }

    /**
     * Writes the given string + two EOLs.
     *
     * @param text The text to write.
     */
    private void writeln(String text) {
        write(text);
        write(EOL);
    }
}
