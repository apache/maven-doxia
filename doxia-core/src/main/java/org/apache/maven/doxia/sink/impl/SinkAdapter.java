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

import org.apache.maven.doxia.sink.SinkEventAttributes;

/**
 * Empty implementation of the <code>Sink</code> interface. Useful for testing purposes.
 *
 * @since 1.0
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 */
public class SinkAdapter extends AbstractSink {

    @Override
    public void head_() {
        // nop
    }

    @Override
    public void body_() {
        // nop
    }

    @Override
    public void article_() {
        // nop
    }

    @Override
    public void navigation_() {
        // nop
    }

    @Override
    public void sidebar_() {
        // nop
    }

    @Override
    public void list_() {
        // nop
    }

    @Override
    public void listItem_() {
        // nop
    }

    @Override
    public void numberedList_() {
        // nop
    }

    @Override
    public void numberedListItem_() {
        // nop
    }

    @Override
    public void definitionList_() {
        // nop
    }

    @Override
    public void definitionListItem_() {
        // nop
    }

    @Override
    public void definition_() {
        // nop
    }

    @Override
    public void figure_() {
        // nop
    }

    @Override
    public void table_() {
        // nop
    }

    @Override
    public void tableRows_() {
        // nop
    }

    @Override
    public void tableRow_() {
        // nop
    }

    @Override
    public void title_() {
        // nop
    }

    @Override
    public void author_() {
        // nop
    }

    @Override
    public void date_() {
        // nop
    }

    @Override
    public void header_() {
        // nop
    }

    @Override
    public void content_() {
        // nop
    }

    @Override
    public void footer_() {
        // nop
    }

    @Override
    public void paragraph_() {
        // nop
    }

    @Override
    public void data_() {
        // nop
    }

    @Override
    public void time_() {
        // nop
    }

    @Override
    public void address_() {
        // nop
    }

    @Override
    public void blockquote_() {
        // nop
    }

    @Override
    public void division_() {
        // nop
    }

    @Override
    public void verbatim_() {
        // nop
    }

    @Override
    public void definedTerm_() {
        // nop
    }

    @Override
    public void figureCaption_() {
        // nop
    }

    @Override
    public void tableCell_() {
        // nop
    }

    @Override
    public void tableHeaderCell_() {
        // nop
    }

    @Override
    public void tableCaption_() {
        // nop
    }

    @Override
    public void pageBreak() {
        // nop
    }

    @Override
    public void anchor_() {
        // nop
    }

    @Override
    public void link_() {
        // nop
    }

    @Override
    public void inline_() {
        // nop
    }

    @Override
    public void italic() {
        // nop
    }

    @Override
    public void italic_() {
        // nop
    }

    @Override
    public void bold() {
        // nop
    }

    @Override
    public void bold_() {
        // nop
    }

    @Override
    public void monospaced() {
        // nop
    }

    @Override
    public void monospaced_() {
        // nop
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
        // nop
    }

    @Override
    public void head(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void title(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void author(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void date(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void body(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void article(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void navigation(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void sidebar(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void section(int level, SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void section_(int level) {
        // nop
    }

    @Override
    public void sectionTitle(int level, SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void sectionTitle_(int level) {
        // nop
    }

    @Override
    public void header(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void content(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void footer(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void list(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void listItem(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void numberedList(int numbering, SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void numberedListItem(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void definitionList(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void definitionListItem(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void definition(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void definedTerm(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void figure(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void figureCaption(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void figureGraphics(String src, SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void table(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void tableRows(int[] justification, boolean grid) {
        // nop
    }

    @Override
    public void tableRow(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void tableCell(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void tableHeaderCell(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void tableCaption(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void paragraph(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void data(String value, SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void time(String datetime, SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void address(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void blockquote(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void division(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void verbatim(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void horizontalRule(SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void anchor(String name, SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void link(String name, SinkEventAttributes attributes) {
        // nop
    }

    @Override
    public void inline(SinkEventAttributes attributes) {
        // nop
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
        // nop
    }
}
