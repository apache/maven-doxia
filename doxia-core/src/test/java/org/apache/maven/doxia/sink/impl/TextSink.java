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
    public void head() {
        writeln("begin:head");
    }

    @Override
    public void head_() {
        writeln("end:head");
    }

    @Override
    public void body() {
        writeln("begin:body");
    }

    @Override
    public void body_() {
        writeln("end:body");
    }

    @Override
    public void article() {
        writeln("begin:article");
    }

    @Override
    public void article_() {
        writeln("end:article");
    }

    @Override
    public void navigation() {
        writeln("begin:navigation");
    }

    @Override
    public void navigation_() {
        writeln("end:navigation");
    }

    @Override
    public void sidebar() {
        writeln("begin:sidebar");
    }

    @Override
    public void sidebar_() {
        writeln("end:sidebar");
    }

    @Override
    public void section1() {
        write("begin:section1");
    }

    @Override
    public void section1_() {
        writeln("end:section1");
    }

    @Override
    public void section2() {
        write("begin:section2");
    }

    @Override
    public void section2_() {
        writeln("end:section2");
    }

    @Override
    public void section3() {
        write("begin:section3");
    }

    @Override
    public void section3_() {
        writeln("end:section3");
    }

    @Override
    public void section4() {
        write("begin:section4");
    }

    @Override
    public void section4_() {
        writeln("end:section4");
    }

    @Override
    public void section5() {
        write("begin:section5");
    }

    @Override
    public void section5_() {
        writeln("end:section5");
    }

    @Override
    public void list() {
        writeln("begin:list");
    }

    @Override
    public void list_() {
        writeln("end:list");
    }

    @Override
    public void listItem() {
        write("begin:listItem");
    }

    @Override
    public void listItem_() {
        writeln("end:listItem");
    }

    @Override
    public void numberedList(int numbering) {
        writeln("begin:numberedList, numbering: " + numbering);
    }

    @Override
    public void numberedList_() {
        writeln("end:numberedList");
    }

    @Override
    public void numberedListItem() {
        write("begin:numberedListItem");
    }

    @Override
    public void numberedListItem_() {
        writeln("end:numberedListItem");
    }

    @Override
    public void definitionList() {
        writeln("begin:definitionList");
    }

    @Override
    public void definitionList_() {
        writeln("end:definitionList");
    }

    @Override
    public void definitionListItem() {
        write("begin:definitionListItem");
    }

    @Override
    public void definitionListItem_() {
        writeln("end:definitionListItem");
    }

    @Override
    public void definition() {
        write("begin:definition");
    }

    @Override
    public void definition_() {
        writeln("end:definition");
    }

    @Override
    public void figure() {
        write("begin:figure");
    }

    @Override
    public void figure_() {
        writeln("end:figure");
    }

    @Override
    public void table() {
        writeln("begin:table");
    }

    @Override
    public void table_() {
        writeln("end:table");
    }

    @Override
    public void tableRows() {
        writeln("begin:tableRows");
    }

    @Override
    public void tableRows_() {
        writeln("end:tableRows");
    }

    @Override
    public void tableRow() {
        write("begin:tableRow");
    }

    @Override
    public void tableRow_() {
        writeln("end:tableRow");
    }

    @Override
    public void title() {
        write("begin:title");
    }

    @Override
    public void title_() {
        writeln("end:title");
    }

    @Override
    public void author() {
        write("begin:author");
    }

    @Override
    public void author_() {
        writeln("end:author");
    }

    @Override
    public void date() {
        write("begin:date");
    }

    @Override
    public void date_() {
        writeln("end:date");
    }

    @Override
    public void sectionTitle() {
        write("begin:sectionTitle");
    }

    @Override
    public void sectionTitle_() {
        writeln("end:sectionTitle");
    }

    @Override
    public void sectionTitle1() {
        write("begin:sectionTitle1");
    }

    @Override
    public void sectionTitle1_() {
        writeln("end:sectionTitle1");
    }

    @Override
    public void sectionTitle2() {
        write("begin:sectionTitle2");
    }

    @Override
    public void sectionTitle2_() {
        writeln("end:sectionTitle2");
    }

    @Override
    public void sectionTitle3() {
        write("begin:sectionTitle3");
    }

    @Override
    public void sectionTitle3_() {
        writeln("end:sectionTitle3");
    }

    @Override
    public void sectionTitle4() {
        write("begin:sectionTitle4");
    }

    @Override
    public void sectionTitle4_() {
        writeln("end:sectionTitle4");
    }

    @Override
    public void sectionTitle5() {
        write("begin:sectionTitle5");
    }

    @Override
    public void sectionTitle5_() {
        writeln("end:sectionTitle5");
    }

    @Override
    public void header() {
        write("begin:header");
    }

    @Override
    public void header_() {
        writeln("end:header");
    }

    @Override
    public void content() {
        write("begin:content");
    }

    @Override
    public void content_() {
        writeln("end:content");
    }

    @Override
    public void footer() {
        write("begin:footer");
    }

    @Override
    public void footer_() {
        writeln("end:footer");
    }

    @Override
    public void paragraph() {
        write("begin:paragraph");
    }

    @Override
    public void paragraph_() {
        writeln("end:paragraph");
    }

    @Override
    public void data(String value) {
        write("begin:data, value: " + value);
    }

    @Override
    public void data_() {
        writeln("end:data");
    }

    @Override
    public void time(String datetime) {
        write("begin:time, datetime: " + datetime);
    }

    @Override
    public void time_() {
        writeln("end:time");
    }

    @Override
    public void address() {
        write("begin:address");
    }

    @Override
    public void address_() {
        writeln("end:address");
    }

    @Override
    public void blockquote() {
        write("begin:blockquote");
    }

    @Override
    public void blockquote_() {
        writeln("end:blockquote");
    }

    @Override
    public void division() {
        write("begin:division");
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
    public void definedTerm() {
        write("begin:definedTerm");
    }

    @Override
    public void definedTerm_() {
        writeln("end:definedTerm");
    }

    @Override
    public void figureCaption() {
        write("begin:figureCaption");
    }

    @Override
    public void figureCaption_() {
        writeln("end:figureCaption");
    }

    @Override
    public void tableCell() {
        write("begin:tableCell");
    }

    @Override
    public void tableCell_() {
        writeln("end:tableCell");
    }

    @Override
    public void tableHeaderCell() {
        write("begin:tableHeaderCell");
    }

    @Override
    public void tableHeaderCell_() {
        writeln("end:tableHeaderCell");
    }

    @Override
    public void tableCaption() {
        write("begin:tableCaption");
    }

    @Override
    public void tableCaption_() {
        writeln("end:tableCaption");
    }

    @Override
    public void figureGraphics(String name) {
        write("figureGraphics, name: " + name);
    }

    @Override
    public void horizontalRule() {
        write("horizontalRule");
    }

    @Override
    public void pageBreak() {
        write("pageBreak");
    }

    @Override
    public void anchor(String name) {
        write("begin:anchor, name: " + name);
    }

    @Override
    public void anchor_() {
        writeln("end:anchor");
    }

    @Override
    public void link(String name) {
        write("begin:link, name: " + name);
    }

    @Override
    public void link_() {
        writeln("end:link");
    }

    @Override
    public void inline() {
        write("begin:inline");
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
    public void lineBreak() {
        write("lineBreak");
    }

    @Override
    public void lineBreakOpportunity() {
        write("lineBreakOpportunity");
    }

    @Override
    public void nonBreakingSpace() {
        write("nonBreakingSpace");
    }

    @Override
    public void text(String text) {
        write("text: " + text);
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
        head();
    }

    @Override
    public void title(SinkEventAttributes attributes) {
        title();
    }

    @Override
    public void author(SinkEventAttributes attributes) {
        author();
    }

    @Override
    public void date(SinkEventAttributes attributes) {
        date();
    }

    @Override
    public void body(SinkEventAttributes attributes) {
        body();
    }

    @Override
    public void article(SinkEventAttributes attributes) {
        article();
    }

    @Override
    public void navigation(SinkEventAttributes attributes) {
        navigation();
    }

    @Override
    public void sidebar(SinkEventAttributes attributes) {
        sidebar();
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
        header();
    }

    @Override
    public void content(SinkEventAttributes attributes) {
        content();
    }

    @Override
    public void footer(SinkEventAttributes attributes) {
        footer();
    }

    @Override
    public void list(SinkEventAttributes attributes) {
        list();
    }

    @Override
    public void listItem(SinkEventAttributes attributes) {
        listItem();
    }

    @Override
    public void numberedList(int numbering, SinkEventAttributes attributes) {
        numberedList(numbering);
    }

    @Override
    public void numberedListItem(SinkEventAttributes attributes) {
        numberedListItem();
    }

    @Override
    public void definitionList(SinkEventAttributes attributes) {
        definitionList();
    }

    @Override
    public void definitionListItem(SinkEventAttributes attributes) {
        definitionListItem();
    }

    @Override
    public void definition(SinkEventAttributes attributes) {
        definition();
    }

    @Override
    public void definedTerm(SinkEventAttributes attributes) {
        definedTerm();
    }

    @Override
    public void figure(SinkEventAttributes attributes) {
        write("begin:figure" + SinkUtils.getAttributeString(attributes));
    }

    @Override
    public void figureCaption(SinkEventAttributes attributes) {
        figureCaption();
    }

    @Override
    public void figureGraphics(String src, SinkEventAttributes attributes) {
        figureGraphics(src);
    }

    @Override
    public void table(SinkEventAttributes attributes) {
        table();
    }

    @Override
    public void tableRows(int[] justification, boolean grid) {
        tableRows();
    }

    @Override
    public void tableRow(SinkEventAttributes attributes) {
        tableRow();
    }

    @Override
    public void tableCell(SinkEventAttributes attributes) {
        tableCell();
    }

    @Override
    public void tableHeaderCell(SinkEventAttributes attributes) {
        tableHeaderCell();
    }

    @Override
    public void tableCaption(SinkEventAttributes attributes) {
        tableCaption();
    }

    @Override
    public void paragraph(SinkEventAttributes attributes) {
        paragraph();
    }

    @Override
    public void data(String value, SinkEventAttributes attributes) {
        data(value);
    }

    @Override
    public void time(String datetime, SinkEventAttributes attributes) {
        time(datetime);
    }

    @Override
    public void address(SinkEventAttributes attributes) {
        address();
    }

    @Override
    public void blockquote(SinkEventAttributes attributes) {
        blockquote();
    }

    @Override
    public void division(SinkEventAttributes attributes) {
        division();
    }

    @Override
    public void verbatim() {
        verbatim(null);
    }

    @Override
    public void verbatim(SinkEventAttributes attributes) {
        boolean boxed = false;

        if (attributes != null && attributes.isDefined(SinkEventAttributes.DECORATION)) {
            boxed = "boxed"
                    .equals(attributes
                            .getAttribute(SinkEventAttributes.DECORATION)
                            .toString());
        }

        write("begin:verbatim, boxed: " + boxed);
    }

    @Override
    public void horizontalRule(SinkEventAttributes attributes) {
        horizontalRule();
    }

    @Override
    public void anchor(String name, SinkEventAttributes attributes) {
        anchor(name);
    }

    @Override
    public void link(String name, SinkEventAttributes attributes) {
        link(name);
    }

    @Override
    public void inline(SinkEventAttributes attributes) {
        inline();
    }

    @Override
    public void lineBreak(SinkEventAttributes attributes) {
        lineBreak();
    }

    @Override
    public void lineBreakOpportunity(SinkEventAttributes attributes) {
        lineBreakOpportunity();
    }

    @Override
    public void text(String text, SinkEventAttributes attributes) {
        text(text);
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
