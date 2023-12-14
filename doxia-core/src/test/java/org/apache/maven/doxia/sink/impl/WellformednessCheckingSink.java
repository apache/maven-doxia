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

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.apache.maven.doxia.sink.SinkEventAttributes;

/**
 * This sink is used for testing purposes in order to check wether
 * the input of some parser is well-formed.
 *
 * @author <a href="mailto:lars@trieloff.net">Lars Trieloff</a>
 */
public class WellformednessCheckingSink extends AbstractSink {
    private final Stack<String> elements = new Stack<>();

    private final List<String> errors = new LinkedList<>();

    @Override
    public void head_() {
        checkWellformedness("head");
    }

    @Override
    public void body_() {
        checkWellformedness("body");
    }

    @Override
    public void article_() {
        checkWellformedness("article");
    }

    @Override
    public void navigation_() {
        checkWellformedness("navigation");
    }

    @Override
    public void sidebar_() {
        checkWellformedness("sidebar");
    }

    @Override
    public void header_() {
        checkWellformedness("header");
    }

    @Override
    public void content_() {
        checkWellformedness("content");
    }

    @Override
    public void footer_() {
        checkWellformedness("footer");
    }

    @Override
    public void list_() {
        checkWellformedness("list");
    }

    @Override
    public void listItem_() {
        checkWellformedness("listItem");
    }

    @Override
    public void numberedList_() {
        checkWellformedness("numberedList");
    }

    @Override
    public void numberedListItem_() {
        checkWellformedness("numberedListItem");
    }

    @Override
    public void definitionList_() {
        checkWellformedness("definitionList");
    }

    @Override
    public void definitionListItem_() {
        checkWellformedness("definitionListItem");
    }

    @Override
    public void definition_() {
        checkWellformedness("definition");
    }

    @Override
    public void figure_() {
        checkWellformedness("figure");
    }

    @Override
    public void table_() {
        checkWellformedness("table");
    }

    @Override
    public void tableRows_() {
        checkWellformedness("tableRows");
    }

    @Override
    public void tableRow_() {
        checkWellformedness("tableRow");
    }

    @Override
    public void title_() {
        checkWellformedness("title");
    }

    @Override
    public void author_() {
        checkWellformedness("author");
    }

    @Override
    public void date_() {
        checkWellformedness("date");
    }

    @Override
    public void paragraph_() {
        checkWellformedness("paragraph");
    }

    @Override
    public void data_() {
        checkWellformedness("data");
    }

    @Override
    public void time_() {
        checkWellformedness("time");
    }

    @Override
    public void address_() {
        checkWellformedness("address");
    }

    @Override
    public void blockquote_() {
        checkWellformedness("blockquote");
    }

    @Override
    public void division_() {
        checkWellformedness("division");
    }

    @Override
    public void verbatim_() {
        checkWellformedness("verbatim");
    }

    @Override
    public void definedTerm_() {
        checkWellformedness("definedTerm");
    }

    @Override
    public void figureCaption_() {
        checkWellformedness("figureCaption");
    }

    @Override
    public void tableCell_() {
        checkWellformedness("tableCell");
    }

    @Override
    public void tableHeaderCell_() {
        checkWellformedness("tableHeaderCell");
    }

    @Override
    public void tableCaption_() {
        checkWellformedness("tableCaption");
    }

    @Override
    public void pageBreak() {
        // nop
    }

    @Override
    public void anchor_() {
        checkWellformedness("anchor");
    }

    @Override
    public void link_() {
        checkWellformedness("link");
    }

    @Override
    public void inline_() {
        checkWellformedness("inline");
    }

    @Override
    public void italic() {
        startElement("italic");
    }

    @Override
    public void italic_() {
        checkWellformedness("italic");
    }

    @Override
    public void bold() {
        startElement("bold");
    }

    @Override
    public void bold_() {
        checkWellformedness("bold");
    }

    @Override
    public void monospaced() {
        startElement("monospaced");
    }

    @Override
    public void monospaced_() {
        checkWellformedness("monospaced");
    }

    @Override
    public void nonBreakingSpace() {
        // nop
    }

    @Override
    public void rawText(String text) {
        // nop
    }

    @Override
    public void comment(String comment) {
        // nop
    }

    @Override
    public void flush() {
        // nop
    }

    @Override
    public void close() {
        this.elements.clear();
        this.errors.clear();
    }

    /**
     * Finds out wether the wellformedness-contraints of the model have been
     * violated.
     *
     * @return false for non-wellformed models
     */
    public boolean isWellformed() {
        return errors.isEmpty();
    }

    /**
     * Gets the offending element that breaks the wellformedness as well
     * as the exepected element.
     *
     * @return the expected and acual elements
     */
    public String getOffender() {
        if (isWellformed()) {
            return null;
        }

        return errors.get(errors.size() - 1);
    }

    /**
     * Gets the list of errors found during wellformedness-check
     *
     * @return a list of String of error messages
     */
    public List<String> getOffenders() {
        return errors;
    }

    /**
     * Checks wether a newly encountered end-tag breaks the wellformedness
     * of the model.
     *
     * @param actual the local-name of the encountered element
     */
    private void checkWellformedness(String actual) {
        String expected = elements.pop();

        if (!expected.equals(actual)) {
            errors.add("Encountered closing: " + actual + ", expected " + expected);
        }
    }

    /**
     * Starts a new element and puts it on the stack in order to calculate
     * wellformedness of the model at a later point of time.
     *
     * @param string the local-name of the start-tag
     */
    private void startElement(String string) {
        elements.push(string);
    }

    @Override
    public void head(SinkEventAttributes attributes) {
        startElement("head");
    }

    @Override
    public void title(SinkEventAttributes attributes) {
        startElement("title");
    }

    @Override
    public void author(SinkEventAttributes attributes) {
        startElement("author");
    }

    @Override
    public void date(SinkEventAttributes attributes) {
        startElement("date");
    }

    @Override
    public void body(SinkEventAttributes attributes) {
        startElement("body");
    }

    @Override
    public void article(SinkEventAttributes attributes) {
        startElement("article");
    }

    @Override
    public void navigation(SinkEventAttributes attributes) {
        startElement("navigation");
    }

    @Override
    public void sidebar(SinkEventAttributes attributes) {
        startElement("sidebar");
    }

    @Override
    public void section(int level, SinkEventAttributes attributes) {
        startElement("section" + level);
    }

    @Override
    public void section_(int level) {
        checkWellformedness("section" + level);
    }

    @Override
    public void sectionTitle(int level, SinkEventAttributes attributes) {
        startElement("sectionTitle" + level);
    }

    @Override
    public void sectionTitle_(int level) {
        checkWellformedness("sectionTitle" + level);
    }

    @Override
    public void header(SinkEventAttributes attributes) {
        startElement("header");
    }

    @Override
    public void content(SinkEventAttributes attributes) {
        startElement("content");
    }

    @Override
    public void footer(SinkEventAttributes attributes) {
        startElement("footer");
    }

    @Override
    public void list(SinkEventAttributes attributes) {
        startElement("list");
    }

    @Override
    public void listItem(SinkEventAttributes attributes) {
        startElement("listItem");
    }

    @Override
    public void numberedList(int numbering, SinkEventAttributes attributes) {
        startElement("numberedList");
    }

    @Override
    public void numberedListItem(SinkEventAttributes attributes) {
        startElement("numberedListItem");
    }

    @Override
    public void definitionList(SinkEventAttributes attributes) {
        startElement("definitionList");
    }

    @Override
    public void definitionListItem(SinkEventAttributes attributes) {
        startElement("definitionListItem");
    }

    @Override
    public void definition(SinkEventAttributes attributes) {
        startElement("definition");
    }

    @Override
    public void definedTerm(SinkEventAttributes attributes) {
        startElement("definedTerm");
    }

    @Override
    public void figure(SinkEventAttributes attributes) {
        startElement("figure");
    }

    @Override
    public void figureCaption(SinkEventAttributes attributes) {
        startElement("figureCaption");
    }

    @Override
    public void figureGraphics(String src, SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void table(SinkEventAttributes attributes) {
        startElement("table");
    }

    @Override
    public void tableRows(int[] justification, boolean grid) {
        startElement("tableRows");
    }

    @Override
    public void tableRow(SinkEventAttributes attributes) {
        startElement("tableRow");
    }

    @Override
    public void tableCell(SinkEventAttributes attributes) {
        startElement("tableCell");
    }

    @Override
    public void tableHeaderCell(SinkEventAttributes attributes) {
        startElement("tableHeaderCell");
    }

    @Override
    public void tableCaption(SinkEventAttributes attributes) {
        startElement("tableCaption");
    }

    @Override
    public void paragraph(SinkEventAttributes attributes) {
        startElement("paragraph");
    }

    @Override
    public void data(String value, SinkEventAttributes attributes) {
        startElement("data");
    }

    @Override
    public void time(String datetime, SinkEventAttributes attributes) {
        startElement("time");
    }

    @Override
    public void address(SinkEventAttributes attributes) {
        startElement("address");
    }

    @Override
    public void blockquote(SinkEventAttributes attributes) {
        startElement("blockquote");
    }

    @Override
    public void division(SinkEventAttributes attributes) {
        startElement("division");
    }

    @Override
    public void verbatim(SinkEventAttributes attributes) {
        startElement("verbatim");
    }

    @Override
    public void horizontalRule(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void anchor(String name, SinkEventAttributes attributes) {
        startElement("anchor");
    }

    @Override
    public void link(String name, SinkEventAttributes attributes) {
        startElement("link");
    }

    @Override
    public void inline(SinkEventAttributes attributes) {
        startElement("inline");
    }

    @Override
    public void lineBreak(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void lineBreakOpportunity(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void text(String text, SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void unknown(String name, Object[] requiredParams, SinkEventAttributes attributes) {
        // ignore
    }
}
