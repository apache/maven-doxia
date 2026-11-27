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

import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import org.apache.maven.doxia.parser.AbstractParser;
import org.apache.maven.doxia.parser.Xhtml5BaseParser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SinkContractValidatorTest {

    private final SinkEventTestingSink target = new SinkEventTestingSink();

    private final SinkContractValidator collect = new SinkContractValidator(target, SinkContractValidator.Mode.COLLECT);

    private final SinkContractValidator failFast = new SinkContractValidator(target);

    private void assertValid() {
        collect.close();
        assertEquals(Collections.emptyList(), collect.getViolations());
    }

    private void assertViolations(int count, String... messageParts) {
        collect.close();
        List<String> violations = collect.getViolations();
        assertEquals(count, violations.size(), violations::toString);
        for (String part : messageParts) {
            assertTrue(
                    violations.stream().anyMatch(v -> v.contains(part)),
                    () -> "no violation contains '" + part + "': " + violations);
        }
    }

    private static SinkEventAttributes attributes(String name, String value) {
        return new SinkEventAttributeSet(name, value);
    }

    // -- nesting

    @Test
    void properlyNestedElementsAreValid() {
        collect.body();
        collect.paragraph();
        collect.bold();
        collect.text("text");
        collect.bold_();
        collect.paragraph_();
        collect.body_();
        assertValid();
    }

    @Test
    void endEventOfOtherElement() {
        collect.paragraph();
        collect.bold();
        collect.paragraph_();
        assertViolations(1, "Sink event 'paragraph_()'", "expected bold_()", "Open elements: paragraph > bold");
    }

    @Test
    void endEventWithoutStartEvent() {
        collect.paragraph_();
        assertViolations(1, "paragraph_()", "there is no open element", "Open elements: none");
    }

    @Test
    void unclosedElementAtClose() {
        collect.body();
        collect.paragraph();
        assertViolations(1, "Sink event 'close'", "expected paragraph_(), body_()", "Open elements: body > paragraph");
    }

    @Test
    void finishReportsUnclosedElementsAndResetsState() {
        collect.body();
        collect.paragraph();
        collect.finish();
        assertEquals(1, collect.getViolations().size());
        assertTrue(collect.getViolations().get(0).contains("Sink event 'finish'"));
        collect.body();
        collect.body_();
        collect.finish();
        assertEquals(1, collect.getViolations().size());
    }

    @Test
    void finishThrowsInFailFastMode() {
        failFast.body();
        assertThrows(SinkContractViolationException.class, failFast::finish);
        failFast.finish();
    }

    @Test
    void flushDoesNotCheckUnclosedElements() {
        collect.body();
        collect.flush();
        failFast.body();
        failFast.flush();
        assertEquals(Collections.emptyList(), collect.getViolations());
    }

    @Test
    void sectionEndLevelMustMatch() {
        collect.section(1, null);
        collect.section(2, null);
        collect.section_(1);
        assertViolations(1, "section_(1)", "expected section_(2)");
    }

    // -- sections

    @Test
    void sectionLevelsMustBeInRange() {
        collect.section(1, null);
        collect.section(6, null);
        collect.section_(6);
        collect.section_(1);
        assertValid();
        collect.section(7, null);
        collect.section_(7);
        collect.section(0, null);
        collect.section_(0);
        assertViolations(4, "section(7)", "section_(7)", "section(0)", "between 1 and 6");
    }

    @Test
    void sectionMustBeContainedInSectionOfLowerLevel() {
        collect.section1();
        collect.section2();
        collect.section2_();
        collect.section1_();
        collect.section1();
        collect.section1_();
        assertValid();
        collect.section(2, null);
        collect.section(1, null);
        collect.section_(1);
        collect.section_(2);
        assertViolations(1, "section(1)", "enclosing section is section(2)");
    }

    @Test
    void sectionTitleMustFollowSectionOfSameLevel() {
        collect.section(2, null);
        collect.sectionTitle(2, null);
        collect.text("title");
        collect.sectionTitle_(2);
        collect.paragraph();
        collect.paragraph_();
        collect.section_(2);
        assertValid();
    }

    @Test
    void sectionTitleWithOtherLevel() {
        collect.section(2, null);
        collect.sectionTitle(3, null);
        collect.sectionTitle_(3);
        collect.section_(2);
        assertViolations(1, "Sink event 'sectionTitle(3)'", "first element within 'section(3)'", "'section(2)'");
    }

    @Test
    void sectionTitleMustBeFirstElement() {
        collect.section(1, null);
        collect.paragraph();
        collect.paragraph_();
        collect.sectionTitle(1, null);
        collect.sectionTitle_(1);
        collect.section_(1);
        assertViolations(1, "Sink event 'sectionTitle(1)'", "first element within 'section(1)'");
    }

    @Test
    void sectionTitleOutsideOfSection() {
        collect.sectionTitle(1, null);
        collect.sectionTitle_(1);
        assertViolations(1, "sectionTitle(1)", "none (top level)");
    }

    @Test
    void commentsAndPrettyPrintingDoNotCountAsElementBeforeSectionTitle() {
        collect.section(1, null);
        collect.comment("comment");
        collect.markupLineBreak(1);
        collect.sectionTitle(1, null);
        collect.sectionTitle_(1);
        collect.section_(1);
        assertValid();
    }

    // -- head

    @Test
    void headWithTitleAuthorsAndDate() {
        collect.head();
        collect.title();
        collect.title_();
        collect.author();
        collect.author_();
        collect.author();
        collect.author_();
        collect.date();
        collect.date_();
        collect.head_();
        collect.body();
        collect.body_();
        assertValid();
    }

    @Test
    void headChildrenInWrongOrder() {
        collect.head();
        collect.date();
        collect.date_();
        collect.author();
        collect.author_();
        collect.title();
        collect.title_();
        collect.head_();
        assertViolations(2, "author must come before date", "title must be unique and come before");
    }

    @Test
    void headChildrenMustBeUnique() {
        collect.head();
        collect.title();
        collect.title_();
        collect.title();
        collect.title_();
        collect.date();
        collect.date_();
        collect.date();
        collect.date_();
        collect.head_();
        assertViolations(2, "title must be unique", "date must be unique");
    }

    @Test
    void headMustBeUniqueAndBeforeBody() {
        collect.head();
        collect.head_();
        collect.head();
        collect.head_();
        assertViolations(1, "head element must be unique");
    }

    @Test
    void headAfterBody() {
        collect.body();
        collect.body_();
        collect.head();
        collect.head_();
        assertViolations(1, "head element must come before the body");
    }

    @Test
    void closeStartsNewDocument() {
        collect.head();
        collect.head_();
        collect.close();
        collect.head();
        collect.head_();
        assertValid();
    }

    // -- lists

    @Test
    void list() {
        collect.list();
        collect.listItem();
        collect.text("a");
        collect.list();
        collect.listItem();
        collect.listItem_();
        collect.list_();
        collect.listItem_();
        collect.list_();
        assertValid();
    }

    @Test
    void emptyList() {
        collect.list();
        collect.list_();
        assertViolations(1, "list_()", "at least one list item");
    }

    @Test
    void listMustStartWithListItem() {
        collect.list();
        collect.text("text");
        collect.listItem();
        collect.listItem_();
        collect.list_();
        assertViolations(1, "Sink event 'text'", "'listItem' as the first element");
    }

    @Test
    void listItemOutsideOfList() {
        collect.numberedList(Sink.NUMBERING_DECIMAL);
        collect.listItem();
        collect.listItem_();
        collect.numberedList_();
        assertViolations(1, "Sink event 'listItem'", "direct child of 'list'");
    }

    @Test
    void numberedList() {
        collect.numberedList(Sink.NUMBERING_UPPER_ROMAN);
        collect.numberedListItem();
        collect.numberedListItem_();
        collect.numberedList_();
        assertValid();
        collect.numberedList(Sink.NUMBERING_DECIMAL);
        collect.numberedList_();
        assertViolations(1, "numberedList_()", "at least one list item");
    }

    @Test
    void numberedListItemOutsideOfNumberedList() {
        collect.list();
        collect.numberedListItem();
        collect.numberedListItem_();
        collect.list_();
        assertViolations(1, "direct child of 'numberedList'");
    }

    @Test
    void definitionList() {
        collect.definitionList();
        collect.definitionListItem();
        collect.definedTerm();
        collect.definedTerm_();
        collect.definition();
        collect.definition_();
        collect.definitionListItem_();
        collect.definitionList_();
        assertValid();
    }

    @Test
    void definitionListItemWithoutDefinition() {
        collect.definitionList();
        collect.definitionListItem();
        collect.definedTerm();
        collect.definedTerm_();
        collect.definitionListItem_();
        collect.definitionList_();
        assertViolations(1, "definitionListItem_()", "exactly one definedTerm and one definition");
    }

    @Test
    void definitionBeforeDefinedTerm() {
        collect.definitionList();
        collect.definitionListItem();
        collect.definition();
        collect.definition_();
        collect.definedTerm();
        collect.definedTerm_();
        collect.definitionListItem_();
        collect.definitionList_();
        assertViolations(2, "definition must come after the definedTerm", "definedTerm must come before");
    }

    @Test
    void definitionListItemWithTwoTerms() {
        collect.definitionList();
        collect.definitionListItem();
        collect.definedTerm();
        collect.definedTerm_();
        collect.definedTerm();
        collect.definedTerm_();
        collect.definition();
        collect.definition_();
        collect.definitionListItem_();
        collect.definitionList_();
        assertViolations(2, "exactly one definedTerm", "exactly one definedTerm and one definition");
    }

    @Test
    void definitionListItemOutsideOfDefinitionList() {
        collect.list();
        collect.listItem();
        collect.definitionListItem();
        collect.definitionListItem_();
        collect.listItem_();
        collect.list_();
        assertTrue(collect.getViolations().stream().anyMatch(v -> v.contains("direct child of 'definitionList'")));
    }

    @Test
    void definedTermAndDefinitionDirectlyInDefinitionList() {
        collect.definitionList();
        collect.definedTerm();
        collect.definedTerm_();
        collect.definition();
        collect.definition_();
        collect.definitionList_();
        assertValid();
    }

    @Test
    void definedTermOutsideOfDefinitionList() {
        collect.paragraph();
        collect.definedTerm();
        collect.definedTerm_();
        collect.definition();
        collect.definition_();
        collect.paragraph_();
        assertViolations(2, "Sink event 'definedTerm'", "Sink event 'definition'", "within a 'definitionList'");
    }

    // -- figures

    @Test
    void figureWithGraphicsAndCaptionInEitherOrder() {
        collect.figure();
        collect.figureGraphics("a.png");
        collect.figureCaption();
        collect.text("caption");
        collect.figureCaption_();
        collect.figure_();
        collect.figure();
        collect.figureCaption();
        collect.figureCaption_();
        collect.figureGraphics("a.png");
        collect.figure_();
        collect.figureGraphics("standalone.png");
        assertValid();
    }

    @Test
    void figureWithoutGraphics() {
        collect.figure();
        collect.figureCaption();
        collect.figureCaption_();
        collect.figure_();
        assertViolations(1, "figure_()", "must contain figureGraphics");
    }

    @Test
    void figureWithTwoCaptions() {
        collect.figure();
        collect.figureGraphics("a.png");
        collect.figureCaption();
        collect.figureCaption_();
        collect.figureCaption();
        collect.figureCaption_();
        collect.figure_();
        assertViolations(1, "at most one figureCaption");
    }

    @Test
    void figureCaptionOutsideOfFigure() {
        collect.figureCaption();
        collect.figureCaption_();
        assertViolations(1, "direct child of 'figure'");
    }

    // -- tables

    private void row(Sink sink) {
        sink.tableRow();
        sink.tableCell();
        sink.tableCell_();
        sink.tableHeaderCell();
        sink.tableHeaderCell_();
        sink.tableRow_();
    }

    @Test
    void tableWithCaptionBeforeOrAfterRows() {
        collect.table();
        collect.tableRows(new int[] {Sink.JUSTIFY_LEFT, Sink.JUSTIFY_DEFAULT}, true);
        row(collect);
        collect.tableRows_();
        collect.tableCaption();
        collect.tableCaption_();
        collect.table_();
        collect.table();
        collect.tableCaption();
        collect.tableCaption_();
        collect.tableRows(null, false);
        row(collect);
        collect.tableRows_();
        collect.table_();
        assertValid();
    }

    @Test
    void tableWithoutRows() {
        collect.table();
        collect.tableCaption();
        collect.tableCaption_();
        collect.table_();
        assertViolations(1, "table_()", "must contain tableRows");
    }

    @Test
    void tableWithTwoCaptions() {
        collect.table();
        collect.tableCaption();
        collect.tableCaption_();
        collect.tableCaption();
        collect.tableCaption_();
        collect.tableRows();
        collect.tableRows_();
        collect.table_();
        assertViolations(1, "at most one tableCaption");
    }

    @Test
    void tableRowOutsideOfTableRows() {
        collect.table();
        collect.tableRow();
        collect.tableRow_();
        collect.table_();
        assertViolations(2, "Sink event 'tableRow'", "direct child of 'tableRows'", "must contain tableRows");
    }

    @Test
    void tableCellOutsideOfTableRow() {
        collect.table();
        collect.tableRows();
        collect.tableCell();
        collect.tableCell_();
        collect.tableHeaderCell();
        collect.tableHeaderCell_();
        collect.tableRows_();
        collect.table_();
        assertViolations(2, "direct child of 'tableRow'");
    }

    @Test
    void tableRowsOutsideOfTable() {
        collect.tableRows();
        collect.tableRows_();
        assertViolations(1, "direct child of 'table'");
    }

    @Test
    void tableJustification() {
        collect.table();
        collect.tableRows(new int[] {Sink.JUSTIFY_CENTER, Sink.JUSTIFY_RIGHT, Sink.JUSTIFY_LEFT}, false);
        collect.tableRows_();
        collect.table_();
        assertValid();
        collect.table();
        collect.tableRows(new int[] {Sink.JUSTIFY_LEFT, 3}, false);
        collect.tableRows_();
        collect.table_();
        assertViolations(1, "justification 3");
    }

    // -- verbatim

    @Test
    void verbatim() {
        collect.verbatim(attributes(SinkEventAttributes.DECORATION, "source"));
        collect.text("code");
        collect.paragraph();
        collect.paragraph_();
        collect.verbatim_();
        collect.verbatim();
        collect.inline();
        collect.inline_();
        collect.verbatim_();
        assertValid();
    }

    @Test
    void fontSemanticsInVerbatim() {
        collect.verbatim();
        collect.inline(attributes(SinkEventAttributes.SEMANTICS, "code"));
        collect.inline_();
        collect.inline(attributes(SinkEventAttributes.SEMANTICS, "bold"));
        collect.inline_();
        collect.text("text", attributes(SinkEventAttributes.SEMANTICS, "italic"));
        collect.text("text", attributes(SinkEventAttributes.SEMANTICS, "monospaced"));
        collect.verbatim_();
        collect.inline(attributes(SinkEventAttributes.SEMANTICS, "bold"));
        collect.inline_();
        assertViolations(3, "Sink event 'inline'", "'bold'", "'italic'", "'monospaced'");
    }

    @Test
    void oneInvalidEventIsOneViolation() {
        collect.list();
        collect.numberedListItem();
        collect.numberedListItem_();
        collect.list_();
        collect.sectionTitle(7, null);
        collect.sectionTitle_(7);
        assertViolations(3);
    }

    @Test
    void nestedVerbatim() {
        collect.verbatim();
        collect.blockquote();
        collect.verbatim();
        collect.verbatim_();
        collect.blockquote_();
        collect.verbatim_();
        assertViolations(1, "Sink event 'verbatim'", "may not be nested");
    }

    @Test
    void fontChangingEventsAndFiguresInVerbatim() {
        collect.verbatim();
        collect.italic();
        collect.italic_();
        collect.bold();
        collect.bold_();
        collect.monospaced();
        collect.monospaced_();
        collect.figure();
        collect.figureGraphics("a.png");
        collect.figure_();
        collect.verbatim_();
        assertViolations(4, "Sink event 'italic'", "Sink event 'bold'", "Sink event 'monospaced'", "'figure'");
    }

    @Test
    void verbatimDecoration() {
        collect.verbatim(attributes(SinkEventAttributes.DECORATION, "boxed"));
        collect.verbatim_();
        assertViolations(1, "'boxed'", "must be 'source'");
    }

    // -- parameters

    @Test
    void anchorName() {
        collect.anchor("a-b_c.d1");
        collect.anchor_();
        collect.anchor("_leadingUnderscore");
        collect.anchor_();
        collect.anchor(null);
        collect.anchor_();
        assertValid();
        collect.anchor("1abc");
        collect.anchor_();
        collect.anchor("a b");
        collect.anchor_();
        collect.anchor("");
        collect.anchor_();
        assertViolations(3, "'1abc'", "'a b'", "not a valid anchor name");
    }

    @Test
    void linkName() {
        collect.link("#anchor");
        collect.link_();
        collect.link("https://maven.apache.org/doxia/index.html?a=b#c");
        collect.link_();
        collect.link("relative/path.html");
        collect.link_();
        assertValid();
        collect.link("not a uri");
        collect.link_();
        assertViolations(1, "'not a uri'", "not a valid URI");
    }

    @Test
    void semantics() {
        collect.inline(attributes(SinkEventAttributes.SEMANTICS, "emphasis"));
        collect.text("code", SinkEventAttributeSet.Semantics.CODE);
        collect.text("text", attributes(SinkEventAttributes.SEMANTICS, "strong"));
        collect.inline_();
        collect.text("text");
        collect.text("text", null);
        assertValid();
        collect.inline(attributes(SinkEventAttributes.SEMANTICS, "wobbly"));
        collect.inline_();
        collect.text("text", attributes(SinkEventAttributes.SEMANTICS, "shiny"));
        assertViolations(2, "Sink event 'inline'", "'wobbly'", "Sink event 'text'", "'shiny'");
    }

    // -- modes

    @Test
    void failFastThrowsBeforeDelegating() {
        failFast.body();
        SinkContractViolationException e = assertThrows(SinkContractViolationException.class, failFast::paragraph_);
        assertTrue(e.getMessage().contains("Sink event 'paragraph_()'"), e.getMessage());
        assertTrue(e.getMessage().contains("expected body_()"), e.getMessage());
        assertTrue(e.getMessage().contains("Open elements: body"), e.getMessage());
        assertEquals(1, target.getEventList().size());
        assertTrue(failFast.getViolations().isEmpty());
    }

    @Test
    void failFastAcceptsValidSequence() {
        failFast.body();
        failFast.section(1, null);
        failFast.sectionTitle(1, null);
        failFast.sectionTitle_(1);
        failFast.section_(1);
        failFast.body_();
        failFast.close();
    }

    @Test
    void failFastThrowsOnUnclosedElementAtCloseAndStillClosesDelegate() {
        failFast.body();
        assertThrows(SinkContractViolationException.class, failFast::close);
        // the state was reset, so a new document can start
        failFast.body();
        failFast.body_();
        failFast.close();
    }

    @Test
    void collectModeForwardsAllEventsUnchanged() {
        collect.paragraph();
        collect.section(9, null);
        collect.text("text");
        collect.paragraph_();
        assertFalse(collect.getViolations().isEmpty());
        assertEquals(4, target.getEventList().size());
        assertEquals("paragraph", target.getEventList().get(0).getName());
        assertEquals("section9", target.getEventList().get(1).getName());
    }

    @Test
    void violationsSurviveClose() {
        collect.paragraph_();
        collect.close();
        assertEquals(1, collect.getViolations().size());
    }

    @Test
    void getViolationsReturnsCopy() {
        collect.paragraph_();
        collect.getViolations().clear();
        assertEquals(1, collect.getViolations().size());
    }

    @Test
    void modeMustNotBeNull() {
        assertThrows(IllegalArgumentException.class, () -> new SinkContractValidator(target, null));
    }

    // -- factory

    @Test
    void factoryCreatesValidatorsInGivenMode() {
        SinkContractValidatorFactory factory = new SinkContractValidatorFactory(SinkContractValidator.Mode.COLLECT);
        Sink first = factory.createWrapper(target);
        Sink second = factory.createWrapper(new SinkEventTestingSink());
        assertTrue(first instanceof SinkContractValidator);
        assertEquals(target, ((SinkContractValidator) first).getWrappedSink());
        first.paragraph_();
        second.bold_();
        assertEquals(2, factory.getViolations().size());
        assertTrue(factory.getViolations().get(0).contains("paragraph_()"));
        assertTrue(factory.getViolations().get(1).contains("bold_()"));
    }

    @Test
    void factoryDefaultsToFailFast() {
        Sink sink = new SinkContractValidatorFactory().createWrapper(target);
        assertThrows(SinkContractViolationException.class, sink::paragraph_);
    }

    @Test
    void factoryIsTheOutermostWrapper() {
        assertEquals(Integer.MAX_VALUE, new SinkContractValidatorFactory().getPriority());
        assertTrue(new SinkContractValidatorFactory().getPriority()
                > new UniqueAnchorNamesValidatorFactory().getPriority());
    }

    /** A minimal parser which, like all parsers, only wraps the sink and never closes it. */
    private static final class OpenElementParser extends AbstractParser {

        @Override
        public void parse(Reader source, Sink sink, String reference) {
            Sink wrapped = getWrappedSink(sink);
            wrapped.body();
            wrapped.paragraph();
            wrapped.text("text");
        }

        @Override
        public int getType() {
            return UNKNOWN_TYPE;
        }
    }

    @Test
    void collectModeThroughParserReportsOpenElements() throws Exception {
        OpenElementParser parser = new OpenElementParser();
        SinkContractValidatorFactory factory = new SinkContractValidatorFactory(SinkContractValidator.Mode.COLLECT);
        parser.addSinkWrapperFactory(factory);
        parser.parse(new StringReader(""), target);
        assertEquals(2, target.getEventList().size() - 1);
        List<String> violations = factory.getViolations();
        assertEquals(1, violations.size(), violations::toString);
        assertTrue(violations.get(0).contains("expected paragraph_(), body_()"), violations.get(0));
        factory.finish();
        assertEquals(1, factory.getViolations().size());
        factory.reset();
        assertEquals(Collections.emptyList(), factory.getViolations());
    }

    @Test
    void failFastThroughParserReportsOpenElementsOnFinish() throws Exception {
        OpenElementParser parser = new OpenElementParser();
        SinkContractValidatorFactory factory = new SinkContractValidatorFactory();
        parser.addSinkWrapperFactory(factory);
        parser.parse(new StringReader(""), target);
        assertThrows(SinkContractViolationException.class, factory::finish);
        factory.reset();
        factory.finish();
    }

    @Test
    void validDocumentThroughXhtml5ParserHasNoViolations() throws Exception {
        Xhtml5BaseParser parser = new Xhtml5BaseParser();
        SinkContractValidatorFactory factory = new SinkContractValidatorFactory(SinkContractValidator.Mode.COLLECT);
        parser.addSinkWrapperFactory(factory);
        parser.parse(new StringReader("<html><body><p>text <b>bold</b></p></body></html>"), new SinkAdapter());
        factory.finish();
        assertEquals(Collections.emptyList(), factory.getViolations());
    }
}
