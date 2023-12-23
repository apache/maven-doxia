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

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.AbstractSink;

/**
 * By default a {@link SinkWrapper} just delegates each method to the wrapped sink's method.
 * For certain sink methods a derived wrapper may modify the sink before/after or instead of calling the delegate's method.
 * Sink wrappers can either be registered manually via {@link Parser#registerSinkWrapperFactory(int, SinkWrapperFactory)} or
 * are automatically registered if provided as JSR330 component.
 * In addition Sink wrappers can be used programmatically without an according factory.
 * @since 2.0.0
 */
public class SinkWrapper extends AbstractSink {

    private Sink delegate;

    public SinkWrapper(Sink delegate) {
        super();
        this.delegate = delegate;
    }

    public Sink getWrappedSink() {
        return delegate;
    }

    public void setWrappedSink(Sink sink) {
        delegate = sink;
    }

    @Override
    public void head(SinkEventAttributes attributes) {
        delegate.head(attributes);
    }

    @Override
    public void head_() {
        delegate.head_();
    }

    @Override
    public void title(SinkEventAttributes attributes) {
        delegate.title(attributes);
    }

    @Override
    public void title_() {
        delegate.title_();
    }

    @Override
    public void author(SinkEventAttributes attributes) {
        delegate.author(attributes);
    }

    @Override
    public void author_() {
        delegate.author_();
    }

    @Override
    public void date(SinkEventAttributes attributes) {
        delegate.date(attributes);
    }

    @Override
    public void date_() {
        delegate.date_();
    }

    @Override
    public void body(SinkEventAttributes attributes) {
        delegate.body(attributes);
    }

    @Override
    public void body_() {
        delegate.body_();
    }

    @Override
    public void article(SinkEventAttributes attributes) {
        delegate.article(attributes);
    }

    @Override
    public void article_() {
        delegate.article_();
    }

    @Override
    public void navigation(SinkEventAttributes attributes) {
        delegate.navigation(attributes);
    }

    @Override
    public void navigation_() {
        delegate.navigation_();
    }

    @Override
    public void sidebar(SinkEventAttributes attributes) {
        delegate.sidebar(attributes);
    }

    @Override
    public void sidebar_() {
        delegate.sidebar_();
    }

    @Override
    public void section(int level, SinkEventAttributes attributes) {
        delegate.section(level, attributes);
    }

    @Override
    public void section_(int level) {
        delegate.section_(level);
    }

    @Override
    public void sectionTitle(int level, SinkEventAttributes attributes) {
        delegate.sectionTitle(level, attributes);
    }

    @Override
    public void sectionTitle_(int level) {
        delegate.sectionTitle_(level);
    }

    @Override
    public void header(SinkEventAttributes attributes) {
        delegate.header(attributes);
    }

    @Override
    public void header_() {
        delegate.header_();
    }

    @Override
    public void content(SinkEventAttributes attributes) {
        delegate.content(attributes);
    }

    @Override
    public void content_() {
        delegate.content_();
    }

    @Override
    public void footer(SinkEventAttributes attributes) {
        delegate.footer(attributes);
    }

    @Override
    public void footer_() {
        delegate.footer_();
    }

    @Override
    public void list(SinkEventAttributes attributes) {
        delegate.list(attributes);
    }

    @Override
    public void list_() {
        delegate.list_();
    }

    @Override
    public void listItem(SinkEventAttributes attributes) {
        delegate.listItem(attributes);
    }

    @Override
    public void listItem_() {
        delegate.listItem_();
    }

    @Override
    public void numberedList(int numbering, SinkEventAttributes attributes) {
        delegate.numberedList(numbering, attributes);
    }

    @Override
    public void numberedList_() {
        delegate.numberedList_();
    }

    @Override
    public void numberedListItem(SinkEventAttributes attributes) {
        delegate.numberedListItem(attributes);
    }

    @Override
    public void numberedListItem_() {
        delegate.numberedListItem_();
    }

    @Override
    public void definitionList(SinkEventAttributes attributes) {
        delegate.definitionList(attributes);
    }

    @Override
    public void definitionList_() {
        delegate.definitionList_();
    }

    @Override
    public void definitionListItem(SinkEventAttributes attributes) {
        delegate.definitionListItem(attributes);
    }

    @Override
    public void definitionListItem_() {
        delegate.definitionListItem_();
    }

    @Override
    public void definition(SinkEventAttributes attributes) {
        delegate.definition(attributes);
    }

    @Override
    public void definition_() {
        delegate.definition_();
    }

    @Override
    public void definedTerm(SinkEventAttributes attributes) {
        delegate.definedTerm(attributes);
    }

    @Override
    public void definedTerm_() {
        delegate.definedTerm_();
    }

    @Override
    public void figure(SinkEventAttributes attributes) {
        delegate.figure(attributes);
    }

    @Override
    public void figure_() {
        delegate.figure_();
    }

    @Override
    public void figureCaption(SinkEventAttributes attributes) {
        delegate.figureCaption(attributes);
    }

    @Override
    public void figureCaption_() {
        delegate.figureCaption_();
    }

    @Override
    public void figureGraphics(String src, SinkEventAttributes attributes) {
        delegate.figureGraphics(src, attributes);
    }

    @Override
    public void table(SinkEventAttributes attributes) {
        delegate.table(attributes);
    }

    @Override
    public void table_() {
        delegate.table_();
    }

    @Override
    public void tableRows(int[] justification, boolean grid) {
        delegate.tableRows(justification, grid);
    }

    @Override
    public void tableRows_() {
        delegate.tableRows_();
    }

    @Override
    public void tableRow(SinkEventAttributes attributes) {
        delegate.tableRow(attributes);
    }

    @Override
    public void tableRow_() {
        delegate.tableRow_();
    }

    @Override
    public void tableCell(SinkEventAttributes attributes) {
        delegate.tableCell(attributes);
    }

    @Override
    public void tableCell_() {
        delegate.tableCell_();
    }

    @Override
    public void tableHeaderCell(SinkEventAttributes attributes) {
        delegate.tableHeaderCell(attributes);
    }

    @Override
    public void tableHeaderCell_() {
        delegate.tableHeaderCell_();
    }

    @Override
    public void tableCaption(SinkEventAttributes attributes) {
        delegate.tableCaption(attributes);
    }

    @Override
    public void tableCaption_() {
        delegate.tableCaption_();
    }

    @Override
    public void paragraph(SinkEventAttributes attributes) {
        delegate.paragraph(attributes);
    }

    @Override
    public void paragraph_() {
        delegate.paragraph_();
    }

    @Override
    public void data(String value, SinkEventAttributes attributes) {
        delegate.data(value, attributes);
    }

    @Override
    public void data_() {
        delegate.data_();
    }

    @Override
    public void time(String datetime, SinkEventAttributes attributes) {
        delegate.time(datetime, attributes);
    }

    @Override
    public void time_() {
        delegate.time_();
    }

    @Override
    public void address(SinkEventAttributes attributes) {
        delegate.address(attributes);
    }

    @Override
    public void address_() {
        delegate.address_();
    }

    @Override
    public void blockquote(SinkEventAttributes attributes) {
        delegate.blockquote(attributes);
    }

    @Override
    public void blockquote_() {
        delegate.blockquote_();
    }

    @Override
    public void division(SinkEventAttributes attributes) {
        delegate.division(attributes);
    }

    @Override
    public void division_() {
        delegate.division_();
    }

    @Override
    public void verbatim(SinkEventAttributes attributes) {
        delegate.verbatim(attributes);
    }

    @Override
    public void verbatim_() {
        delegate.verbatim_();
    }

    @Override
    public void horizontalRule(SinkEventAttributes attributes) {
        delegate.horizontalRule(attributes);
    }

    @Override
    public void pageBreak() {
        delegate.pageBreak();
    }

    @Override
    public void anchor(String name, SinkEventAttributes attributes) {
        delegate.anchor(name, attributes);
    }

    @Override
    public void anchor_() {
        delegate.anchor_();
    }

    @Override
    public void link(String name, SinkEventAttributes attributes) {
        delegate.link(name, attributes);
    }

    @Override
    public void link_() {
        delegate.link_();
    }

    @Override
    public void inline(SinkEventAttributes attributes) {
        delegate.inline(attributes);
    }

    @Override
    public void inline_() {
        delegate.inline_();
    }

    @Override
    public void italic() {
        delegate.italic();
    }

    @Override
    public void italic_() {
        delegate.italic_();
    }

    @Override
    public void bold() {
        delegate.bold();
    }

    @Override
    public void bold_() {
        delegate.bold_();
    }

    @Override
    public void monospaced() {
        delegate.monospaced();
    }

    @Override
    public void monospaced_() {
        delegate.monospaced_();
    }

    @Override
    public void lineBreak(SinkEventAttributes attributes) {
        delegate.lineBreak(attributes);
    }

    @Override
    public void lineBreakOpportunity(SinkEventAttributes attributes) {
        delegate.lineBreakOpportunity(attributes);
    }

    @Override
    public void nonBreakingSpace() {
        delegate.nonBreakingSpace();
    }

    @Override
    public void text(String text, SinkEventAttributes attributes) {
        delegate.text(text, attributes);
    }

    @Override
    public void rawText(String text) {
        delegate.rawText(text);
    }

    @Override
    public void comment(String comment) {
        delegate.comment(comment);
    }

    @Override
    public void unknown(String name, Object[] requiredParams, SinkEventAttributes attributes) {
        delegate.unknown(name, requiredParams, attributes);
    }

    @Override
    public void flush() {
        delegate.flush();
    }

    @Override
    public void close() {
        delegate.close();
    }
}
