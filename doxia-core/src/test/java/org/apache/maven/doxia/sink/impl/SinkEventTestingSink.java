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

import org.apache.maven.doxia.sink.SinkEventAttributes;

/**
 * This sink is used for testing purposes in order to check wether
 * the input of some parser is well-formed.
 *
 * @author ltheussl
 * @since 1.1
 */
public class SinkEventTestingSink extends AbstractSink {
    /** The list of sink events. */
    private final List<SinkEventElement> events = new LinkedList<>();

    /**
     * Return the collected list of SinkEventElements.
     *
     * @return the collected list of SinkEventElements.
     */
    public List<SinkEventElement> getEventList() {
        return this.events;
    }

    /** Clears the list of sink events. */
    public void reset() {
        this.events.clear();
    }

    //
    // sink methods
    //

    @Override
    public void head_() {
        addEvent("head_");
    }

    @Override
    public void body_() {
        addEvent("body_");
    }

    @Override
    public void article_() {
        addEvent("article_");
    }

    @Override
    public void navigation_() {
        addEvent("navigation_");
    }

    @Override
    public void sidebar_() {
        addEvent("sidebar_");
    }

    @Override
    public void list_() {
        addEvent("list_");
    }

    @Override
    public void listItem_() {
        addEvent("listItem_");
    }

    @Override
    public void numberedList_() {
        addEvent("numberedList_");
    }

    @Override
    public void numberedListItem_() {
        addEvent("numberedListItem_");
    }

    @Override
    public void definitionList_() {
        addEvent("definitionList_");
    }

    @Override
    public void definitionListItem_() {
        addEvent("definitionListItem_");
    }

    @Override
    public void definition_() {
        addEvent("definition_");
    }

    @Override
    public void figure_() {
        addEvent("figure_");
    }

    @Override
    public void table_() {
        addEvent("table_");
    }

    @Override
    public void tableRows(int[] justification, boolean grid) {
        addEvent("tableRows", new Object[] {justification, grid});
    }

    @Override
    public void tableRows_() {
        addEvent("tableRows_");
    }

    @Override
    public void tableRow_() {
        addEvent("tableRow_");
    }

    @Override
    public void title_() {
        addEvent("title_");
    }

    @Override
    public void author_() {
        addEvent("author_");
    }

    @Override
    public void date_() {
        addEvent("date_");
    }

    @Override
    public void header_() {
        addEvent("header_");
    }

    @Override
    public void content_() {
        addEvent("content_");
    }

    @Override
    public void footer_() {
        addEvent("footer_");
    }

    @Override
    public void paragraph_() {
        addEvent("paragraph_");
    }

    @Override
    public void data_() {
        addEvent("data_");
    }

    @Override
    public void time_() {
        addEvent("time_");
    }

    @Override
    public void address_() {
        addEvent("address_");
    }

    @Override
    public void blockquote_() {
        addEvent("blockquote_");
    }

    @Override
    public void division_() {
        addEvent("division_");
    }

    @Override
    public void verbatim_() {
        addEvent("verbatim_");
    }

    @Override
    public void definedTerm_() {
        addEvent("definedTerm_");
    }

    @Override
    public void figureCaption_() {
        addEvent("figureCaption_");
    }

    @Override
    public void tableCell_() {
        addEvent("tableCell_");
    }

    @Override
    public void tableHeaderCell_() {
        addEvent("tableHeaderCell_");
    }

    @Override
    public void tableCaption_() {
        addEvent("tableCaption_");
    }

    @Override
    public void pageBreak() {
        addEvent("pageBreak");
    }

    @Override
    public void anchor_() {
        addEvent("anchor_");
    }

    @Override
    public void link_() {
        addEvent("link_");
    }

    @Override
    public void inline_() {
        addEvent("inline_");
    }

    @Override
    public void italic() {
        addEvent("italic");
    }

    @Override
    public void italic_() {
        addEvent("italic_");
    }

    @Override
    public void bold() {
        addEvent("bold");
    }

    @Override
    public void bold_() {
        addEvent("bold_");
    }

    @Override
    public void monospaced() {
        addEvent("monospaced");
    }

    @Override
    public void monospaced_() {
        addEvent("monospaced_");
    }

    @Override
    public void nonBreakingSpace() {
        addEvent("nonBreakingSpace");
    }

    @Override
    public void rawText(String text) {
        addEvent("rawText", new Object[] {text});
    }

    @Override
    public void comment(String comment, boolean isBlockComment) {
        addEvent("comment", new Object[] {comment, isBlockComment});
    }

    @Override
    public void comment(String comment) {
        addEvent("comment", new Object[] {comment});
    }

    @Override
    public void flush() {
        addEvent("flush");
    }

    @Override
    public void close() {
        addEvent("close");
    }

    @Override
    public void head(SinkEventAttributes attributes) {
        addEvent("head", new Object[] {attributes});
    }

    @Override
    public void title(SinkEventAttributes attributes) {
        addEvent("title", new Object[] {attributes});
    }

    @Override
    public void author(SinkEventAttributes attributes) {
        addEvent("author", new Object[] {attributes});
    }

    @Override
    public void date(SinkEventAttributes attributes) {
        addEvent("date", new Object[] {attributes});
    }

    @Override
    public void body(SinkEventAttributes attributes) {
        addEvent("body", new Object[] {attributes});
    }

    @Override
    public void article(SinkEventAttributes attributes) {
        addEvent("article", new Object[] {attributes});
    }

    @Override
    public void navigation(SinkEventAttributes attributes) {
        addEvent("navigation", new Object[] {attributes});
    }

    @Override
    public void sidebar(SinkEventAttributes attributes) {
        addEvent("sidebar", new Object[] {attributes});
    }

    @Override
    public void section(int level, SinkEventAttributes attributes) {
        addEvent("section" + level, new Object[] {attributes});
    }

    @Override
    public void section_(int level) {
        addEvent("section" + level + "_");
    }

    @Override
    public void sectionTitle(int level, SinkEventAttributes attributes) {
        addEvent("sectionTitle" + level, new Object[] {attributes});
    }

    @Override
    public void sectionTitle_(int level) {

        addEvent("sectionTitle" + level + "_");
    }

    @Override
    public void header(SinkEventAttributes attributes) {
        addEvent("header", new Object[] {attributes});
    }

    @Override
    public void content(SinkEventAttributes attributes) {
        addEvent("content", new Object[] {attributes});
    }

    @Override
    public void footer(SinkEventAttributes attributes) {
        addEvent("footer", new Object[] {attributes});
    }

    @Override
    public void list(SinkEventAttributes attributes) {
        addEvent("list", new Object[] {attributes});
    }

    @Override
    public void listItem(SinkEventAttributes attributes) {
        addEvent("listItem", new Object[] {attributes});
    }

    @Override
    public void numberedList(int numbering, SinkEventAttributes attributes) {
        addEvent("numberedList", new Object[] {numbering, attributes});
    }

    @Override
    public void numberedListItem(SinkEventAttributes attributes) {
        addEvent("numberedListItem", new Object[] {attributes});
    }

    @Override
    public void definitionList(SinkEventAttributes attributes) {
        addEvent("definitionList", new Object[] {attributes});
    }

    @Override
    public void definitionListItem(SinkEventAttributes attributes) {
        addEvent("definitionListItem", new Object[] {attributes});
    }

    @Override
    public void definition(SinkEventAttributes attributes) {
        addEvent("definition", new Object[] {attributes});
    }

    @Override
    public void definedTerm(SinkEventAttributes attributes) {
        addEvent("definedTerm", new Object[] {attributes});
    }

    @Override
    public void figure(SinkEventAttributes attributes) {
        addEvent("figure", new Object[] {attributes});
    }

    @Override
    public void figureCaption(SinkEventAttributes attributes) {
        addEvent("figureCaption", new Object[] {attributes});
    }

    @Override
    public void figureGraphics(String src, SinkEventAttributes attributes) {
        addEvent("figureGraphics", new Object[] {src, attributes});
    }

    @Override
    public void table(SinkEventAttributes attributes) {
        addEvent("table", new Object[] {attributes});
    }

    @Override
    public void tableRow(SinkEventAttributes attributes) {
        addEvent("tableRow", new Object[] {attributes});
    }

    @Override
    public void tableCell(SinkEventAttributes attributes) {
        addEvent("tableCell", new Object[] {attributes});
    }

    @Override
    public void tableHeaderCell(SinkEventAttributes attributes) {
        addEvent("tableHeaderCell", new Object[] {attributes});
    }

    @Override
    public void tableCaption(SinkEventAttributes attributes) {
        addEvent("tableCaption", new Object[] {attributes});
    }

    @Override
    public void paragraph(SinkEventAttributes attributes) {
        addEvent("paragraph", new Object[] {attributes});
    }

    @Override
    public void data(String value, SinkEventAttributes attributes) {
        addEvent("data", new Object[] {value, attributes});
    }

    @Override
    public void time(String datetime, SinkEventAttributes attributes) {
        addEvent("time", new Object[] {datetime, attributes});
    }

    @Override
    public void address(SinkEventAttributes attributes) {
        addEvent("address", new Object[] {attributes});
    }

    @Override
    public void blockquote(SinkEventAttributes attributes) {
        addEvent("blockquote", new Object[] {attributes});
    }

    @Override
    public void division(SinkEventAttributes attributes) {
        addEvent("division", new Object[] {attributes});
    }

    @Override
    public void verbatim(SinkEventAttributes attributes) {
        addEvent("verbatim", new Object[] {attributes});
    }

    @Override
    public void horizontalRule(SinkEventAttributes attributes) {
        addEvent("horizontalRule", new Object[] {attributes});
    }

    @Override
    public void anchor(String name, SinkEventAttributes attributes) {
        addEvent("anchor", new Object[] {name, attributes});
    }

    @Override
    public void link(String name, SinkEventAttributes attributes) {
        addEvent("link", new Object[] {name, attributes});
    }

    @Override
    public void inline(SinkEventAttributes attributes) {
        addEvent("inline", new Object[] {attributes});
    }

    @Override
    public void lineBreak(SinkEventAttributes attributes) {
        addEvent("lineBreak", new Object[] {attributes});
    }

    @Override
    public void lineBreakOpportunity(SinkEventAttributes attributes) {
        addEvent("lineBreakOpportunity", new Object[] {attributes});
    }

    @Override
    public void text(String text, SinkEventAttributes attributes) {
        addEvent("text", new Object[] {text, attributes});
    }

    @Override
    public void unknown(String name, Object[] requiredParams, SinkEventAttributes attributes) {
        addEvent("unknown", new Object[] {name, requiredParams, attributes});
    }

    //
    // private
    //

    /**
     * Adds a no-arg event to the list of events.
     *
     * @param string the name of the event.
     */
    private void addEvent(String string) {
        addEvent(string, null);
    }

    /**
     * Adds an event to the list of events.
     *
     * @param string the name of the event.
     * @param arguments The array of arguments to the sink method.
     */
    private void addEvent(String string, Object[] arguments) {
        events.add(new SinkEventElement(string, arguments));
    }
}
