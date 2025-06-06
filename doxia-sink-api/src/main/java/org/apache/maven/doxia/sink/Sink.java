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
package org.apache.maven.doxia.sink;

/**
 * A <i>Sink</i> consumes Doxia events to produce a resultant output format
 * (eg Docbook, PDF, XHTML...).
 * <p>
 *   Doxia allows you to transform any supported input document format (ie for which a Parser exists)
 *   into any supported output document format (ie for which a Sink exists).
 * </p>
 * <p>
 *   A parser is responsible for reading an input document and emitting a sequence of Doxia events
 *   which can then be consumed by a Doxia Sink. Thus, you can parse any front- end format
 *   (eg APT, FML, Xdoc, ...) and have them all contribute to a final XHTML version of a web site.
 *   All documents being parsed result in a stream of Doxia events (eg paragraph, bold, italic,
 *   text,...), which are then fed into a XHTML Sink to produce a set of XHTML pages.
 * </p>
 * <p>
 *   A Sink is ultimately responsible for the final format and structure of the output document.
 *   For example, you can take a collection of APT documents, let a Parser emit a series of Doxia
 *   events and have that be fed into a Sink to produce a single PDF, a book, a site, or a
 *   Word document. The Sink is fully responsible for the final output.
 * </p>
 * <p>
 *   You can easily integrate any custom (XML, Wiki,...) format by creating a Doxia Parser which
 *   reads your input document and produces a proper sequence of Doxia events.
 *   Those can then be fed into an arbitrary Sink to produce any desired final output.
 * </p>
 * <p>
 * <b>Note</b>: All implemented sink <b>should</b> use UTF-8 as encoding.
 * </p>
 *
 * @since 1.0-alpha-6
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @author ltheussl
 */
public interface Sink extends AutoCloseable {

    /**
     * A numbering to handle a number list.
     * @see #numberedList(int,SinkEventAttributes)
     */
    int NUMBERING_DECIMAL = 0;

    /**
     * A numbering to handle a lower alpha list.
     * @see #numberedList(int,SinkEventAttributes)
     */
    int NUMBERING_LOWER_ALPHA = 1;

    /**
     * A numbering to handle a upper alpha list.
     * @see #numberedList(int,SinkEventAttributes)
     */
    int NUMBERING_UPPER_ALPHA = 2;

    /**
     * A numbering to handle a lower roman list.
     * @see #numberedList(int,SinkEventAttributes)
     */
    int NUMBERING_LOWER_ROMAN = 3;

    /**
     * A numbering to handle a upper roman list.
     * @see #numberedList(int,SinkEventAttributes)
     */
    int NUMBERING_UPPER_ROMAN = 4;

    /**
     * A level 1 section (section).
     * @see #section(int,SinkEventAttributes)
     */
    int SECTION_LEVEL_1 = 1;

    /**
     * A level 2 section (subsection).
     * @see #section(int,SinkEventAttributes)
     */
    int SECTION_LEVEL_2 = 2;

    /**
     * A level 3 section (sub-subsection).
     * @see #section(int,SinkEventAttributes)
     */
    int SECTION_LEVEL_3 = 3;

    /**
     * A level 4 section (sub-sub-subsection).
     * @see #section(int,SinkEventAttributes)
     */
    int SECTION_LEVEL_4 = 4;

    /**
     * A level 5 section (sub-sub-sub-subsection).
     * @see #section(int,SinkEventAttributes)
     */
    int SECTION_LEVEL_5 = 5;

    /**
     * A level 6 section.
     * @see #section(int,SinkEventAttributes)
     * @since 2.0.0
     */
    int SECTION_LEVEL_6 = 6;

    /**
     * Default alignment for table cells.
     * Actual value depends on the implementation.
     * @see #tableRows(int[], boolean)
     * @since 2.1.0
     */
    int JUSTIFY_DEFAULT = -1;

    /**
     * Center alignment for table cells.
     * @see #tableRows(int[], boolean)
     */
    int JUSTIFY_CENTER = 0;

    /**
     * Left alignment for table cells.
     * @see #tableRows(int[], boolean)
     */
    int JUSTIFY_LEFT = 1;

    /**
     * Right alignment for table cells.
     * @see #tableRows(int[], boolean)
     */
    int JUSTIFY_RIGHT = 2;

    /**
     * Starts the head element. Shortcut for {@link #head(SinkEventAttributes)} with first argument being {@code null}.
     *
     * @see #head(SinkEventAttributes)
     */
    void head();

    /**
     * Starts the head element.
     *
     * <p>
     *   This contains information about the current document, (eg its title) that is not
     *   considered document content. The head element is optional but if it exists, it has to be
     *   unique within a sequence of Sink events that produces one output document, and it has
     *   to come before the {@link #body(SinkEventAttributes)} element.
     * </p>
     * <p>
     *   The canonical sequence of events for the head element is:
     * </p>
     * <pre>
     *   sink.head();
     *
     *   sink.title();
     *   sink.text("Title");
     *   sink.title_();
     *
     *   sink.author();
     *   sink.text("Author");
     *   sink.author_();
     *
     *   sink.date();
     *   sink.text("Date");
     *   sink.date_();
     *
     *   sink.head_();
     * </pre>
     * <p>
     *   but none of the enclosed events is required.  However, if they exist they have to occur
     *   in the order shown, and the title() and date() events have to be unique (author() events
     *   may occur any number of times).
     * </p>
     * <p>
     *   Supported attributes are:
     * </p>
     * <blockquote>
     *   {@link SinkEventAttributes#PROFILE PROFILE}, {@link SinkEventAttributes#LANG LANG}.
     * </blockquote>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void head(SinkEventAttributes attributes);

    /**
     * Ends the head element.
     */
    void head_();

    /**
     * Starts the title element. Shortcut for {@link #title(SinkEventAttributes)} with first argument being {@code null}.
     *
     * @see #title(SinkEventAttributes)
     */
    void title();

    /**
     * Starts the title element. This is used to identify the document.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     * @see #head(SinkEventAttributes)
     */
    void title(SinkEventAttributes attributes);

    /**
     * Ends the title element.
     */
    void title_();

    /**
     * Starts an author element. Shortcut for {@link #author(SinkEventAttributes)} with first argument being {@code null}.
     *
     * @see #author(SinkEventAttributes)
     */
    void author();

    /**
     * Starts an author element. This is used to identify the author of the document.
     *
     * <p>
     *   Supported attributes are: {@link SinkEventAttributes#EMAIL EMAIL}.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     * @see #head(SinkEventAttributes)
     */
    void author(SinkEventAttributes attributes);

    /**
     * Ends an author element.
     */
    void author_();

    /**
     * Starts the date element. Shortcut for {@link #date(SinkEventAttributes)} with first argument being {@code null}.
     *
     * @see #date(SinkEventAttributes)
     */
    void date();

    /**
     * Starts the date element. This is used to identify the date of the document: there is no strict definition
     * if it is <b>creation date</b> or <b>last modification date</b>, which are the 2 classical semantics.
     * There is no formal formatting requirements either.
     * <br>
     * The date is recommended (but it is not a requirement) to be aligned to the
     * <a href="http://www.iso.org/iso/iso_catalogue/catalogue_tc/catalogue_detail.htm?csnumber=26780">ISO-8601</a>
     * standard, i.e.:
     * <pre>YYYY-MM-DD</pre>
     * where
     * <ul>
     * <li><code>YYYY</code> is the year in the Gregorian calendar,</li>
     * <li><code>MM</code> is the month of the year between 01 (January) and 12 (December),</li>
     * <li>and <code>DD</code> is the day of the month between 01 and 31.</li>
     * </ul>
     *
     * <p>
     *   Supported attributes are: none.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     * @see #head(SinkEventAttributes)
     */
    void date(SinkEventAttributes attributes);

    /**
     * Ends the date element.
     */
    void date_();

    /**
     * Starts the body of a document. Shortcut for {@link #body(SinkEventAttributes)} with first argument being {@code null}.
     *
     * @see #body(SinkEventAttributes)
     */
    void body();

    /**
     * Starts the body of a document. This contains the document's content.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     * @see #head(SinkEventAttributes)
     */
    void body(SinkEventAttributes attributes);

    /**
     * Ends the body element.
     */
    void body_();

    /**
     * Starts an article within a document. Shortcut for {@link #article(SinkEventAttributes)} with first argument being {@code null}.
     *
     * @see #article(SinkEventAttributes)
     */
    void article();

    /**
     * Starts an article within a document.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 2.0
     */
    void article(SinkEventAttributes attributes);

    /**
     * Ends the article element.
     */
    void article_();

    /**
     * Starts a navigation section within a document. Shortcut for {@link #navigation(SinkEventAttributes)} with first argument being {@code null}.
     *
     * @see #navigation(SinkEventAttributes)
     */
    void navigation();

    /**
     * Starts a navigation section within a document.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 2.0
     * @see #navigation(SinkEventAttributes)
     */
    void navigation(SinkEventAttributes attributes);

    /**
     * Ends the navigation element.
     */
    void navigation_();

    /**
     * Starts a sidebar section within a document. Shortcut for {@link #sidebar(SinkEventAttributes)} with first argument being {@code null}.
     *
     * @see #sidebar(SinkEventAttributes)
     */
    void sidebar();

    /**
     * Starts a sidebar section within a document.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 2.0
     */
    void sidebar(SinkEventAttributes attributes);

    /**
     * Ends the sidebar element.
     */
    void sidebar_();

    /**
     * Was never properly evaluated by any Sink implementation because section titles (and sections) always started from 1 (not from 0).
     * @deprecated Use {@link #sectionTitle1()} instead.
     */
    @Deprecated
    void sectionTitle();

    /**
     * Was never properly evaluated by any Sink implementation because section titles (and sections) always started from 1 (not from 0).
     * @deprecated Use {@link #sectionTitle1_()} instead.
     */
    @Deprecated
    void sectionTitle_();

    /**
     * Starts a first heading element which contains the topic of the section. Shortcut for {@link #section(int, SinkEventAttributes)} with first argument being {@code 1} and second argument being {@code null}.
     *
     * @see #section(int,SinkEventAttributes)
     */
    void section1();

    /**
     * Ends a first heading element. Shortcut for {@link #section_(int)} with argument being {@code 1}.
     */
    void section1_();

    /**
     * Starts a first title heading element. This element is optional, but if it exists,
     * it has to be contained, and be the first element, within a {@link #section1()} element.
     * <p>
     * Shortcut for {@link #sectionTitle(int, SinkEventAttributes)} with first argument being {@code 1} and second argument being {@code null}.
     *
     * @see #sectionTitle(int,SinkEventAttributes)
     */
    void sectionTitle1();

    /**
     * Ends a first title heading element. Shortcut for {@link #sectionTitle_(int)} with argument being {@code 1}.
     */
    void sectionTitle1_();

    /**
     * Starts a second heading element which contains the topic of the section.
     * This has to be contained within a {@link #section1()} element.
     * <p>
     * Shortcut for {@link #section(int, SinkEventAttributes)} with first argument being {@code 2} and second argument being {@code null}.
     *
     * @see #section(int,SinkEventAttributes)
     */
    void section2();

    /**
     * Ends a second heading element. Shortcut for {@link #section_(int)} with argument being {@code 2}.
     */
    void section2_();

    /**
     * Starts a second title heading element. This element is optional, but if it exists,
     * it has to be contained, and be the first element, within a {@link #section2()} element.
     * <p>
     * Shortcut for {@link #sectionTitle(int, SinkEventAttributes)} with first argument being {@code 2} and second argument being {@code null}.
     * @see #sectionTitle(int,SinkEventAttributes)
     */
    void sectionTitle2();

    /**
     * Ends a second title heading element. Shortcut for {@link #sectionTitle_(int)} with argument being {@code 2}.
     */
    void sectionTitle2_();

    /**
     * Starts a third heading element which contains the topic of the section.
     * This has to be contained within a {@link #section2()} element.
     * <p>
     * Shortcut for {@link #section(int, SinkEventAttributes)} with first argument being {@code 3} and second argument being {@code null}.
     *
     * @see #section(int,SinkEventAttributes)
     */
    void section3();

    /**
     * Ends a third heading element. Shortcut for {@link #section_(int)} with argument being {@code 3}.
     */
    void section3_();

    /**
     * Starts a third title heading element. This element is optional, but if it exists,
     * it has to be contained, and be the first element, within a {@link #section3()} element.
     * <p>
     * Shortcut for {@link #sectionTitle(int, SinkEventAttributes)} with first argument being {@code 3} and second argument being {@code null}.
     * @see #sectionTitle(int,SinkEventAttributes)
     */
    void sectionTitle3();

    /**
     * Ends a third title heading element. Shortcut for {@link #sectionTitle_(int)} with argument being {@code 3}.
     */
    void sectionTitle3_();

    /**
     * Starts a 4th heading element which contains the topic of the section.
     * This has to be contained within a {@link #section3()} element.
     * <p>
     * Shortcut for {@link #section(int, SinkEventAttributes)} with first argument being {@code 4} and second argument being {@code null}.
     *
     * @see #section(int,SinkEventAttributes)
     */
    void section4();

    /**
     * Ends a 4th heading element. Shortcut for {@link #section_(int)} with argument being {@code 4}.
     */
    void section4_();

    /**
     * Starts a 4th title heading element. This element is optional, but if it exists,
     * it has to be contained, and be the first element, within a {@link #section4()} element.
     * <p>
     * Shortcut for {@link #sectionTitle(int, SinkEventAttributes)} with first argument being {@code 4} and second argument being {@code null}.
     *
     * @see #sectionTitle(int,SinkEventAttributes)
     */
    void sectionTitle4();

    /**
     * Ends a 4th title heading element. Shortcut for {@link #sectionTitle_(int)} with argument being {@code 4}.
     */
    void sectionTitle4_();

    /**
     * Starts a 5th heading element which contains the topic of the section.
     * This has to be contained within a {@link #section5()} element.
     * <p>
     * Shortcut for {@link #section(int, SinkEventAttributes)} with first argument being {@code 5} and second argument being {@code null}.
     *
     * @see #section(int,SinkEventAttributes)
     */
    void section5();

    /**
     * Ends a 5th heading element. Shortcut for {@link #section_(int)} with argument being {@code 5}.
     */
    void section5_();

    /**
     * Starts a 5th title heading element. This element is optional, but if it exists,
     * it has to be contained, and be the first element, within a {@link #section5()} element.
     * <p>
     * Shortcut for {@link #sectionTitle(int, SinkEventAttributes)} with first argument being {@code 5} and second argument being {@code null}.
     *
     * @see #sectionTitle(int,SinkEventAttributes)
     */
    void sectionTitle5();

    /**
     * Ends a 5th title heading element. Shortcut for {@link #sectionTitle_(int)} with argument being {@code 5}.
     * @since 2.0.0
     */
    void sectionTitle5_();

    /**
     * Starts a 6th heading element which contains the topic of the section.
     * This has to be contained within a {@link #section6()} element.
     * <p>
     * Shortcut for {@link #section(int, SinkEventAttributes)} with first argument being {@code 6} and second argument being {@code null}.
     *
     * @see #section(int,SinkEventAttributes)
     * @since 2.0.0
     */
    void section6();

    /**
     * Ends a 6th heading element. Shortcut for {@link #section_(int)} with argument being {@code 6}.
     *
     * @since 2.0.0
     */
    void section6_();

    /**
     * Starts a 6th title heading element. This element is optional, but if it exists,
     * it has to be contained, and be the first element, within a {@link #section5()} element.
     * <p>
     * Shortcut for {@link #sectionTitle(int, SinkEventAttributes)} with first argument being {@code 6} and second argument being {@code null}.
     *
     * @see #sectionTitle(int,SinkEventAttributes)
     * @since 2.0.0
     */
    void sectionTitle6();

    /**
     * Ends a 6th title heading element. Shortcut for {@link #sectionTitle_(int)} with argument being {@code 6}.
     *
     * @since 2.0.0
     */
    void sectionTitle6_();

    /**
     * Start a new section at the given level.
     *
     * <p>
     *   Sections with higher level have to be entirely contained within sections of lower level.
     * </p>
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     * </p>
     *
     * @param level the section level (must be a value between {@value #SECTION_LEVEL_1} and {@value #SECTION_LEVEL_6}).
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void section(int level, SinkEventAttributes attributes);

    /**
     * Ends a section at the given level.
     *
     * @param level the section level (must be a value between {@value #SECTION_LEVEL_1} and {@value #SECTION_LEVEL_6}).
     * @since 1.1
     */
    void section_(int level);

    /**
     * Start a new section title at the given level.
     *
     * <p>
     *    This element is optional, but if it exists, it has to be contained, and be the first
     *    element, within a corresponding {@link #section(int,SinkEventAttributes) section}
     *    element of the same level.
     * </p>
     * <p>
     *   <b>NOTE:</b> It is strongly recommended not to make section titles implicit anchors.
     *   Neither Parsers nor Sinks should insert any content that is not explicitly present
     *   in the original source document, as this would lead to undefined behaviour for
     *   multi-format processing chains. However, while Parsers <b>must never</b> emit anchors
     *   for section titles, some specialized Sinks may implement such a feature if the resulting
     *   output documents are not going to be further processed (and this is properly documented).
     * </p>
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes} plus
     *   {@link SinkEventAttributes#ALIGN ALIGN}.
     * </p>
     *
     * @param level the section title level (must be a value between {@value #SECTION_LEVEL_1} and {@value #SECTION_LEVEL_6}).
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void sectionTitle(int level, SinkEventAttributes attributes);

    /**
     * Ends a section title at the given level.
     *
     * @param level the section title level (must be a value between {@value #SECTION_LEVEL_1} and {@value #SECTION_LEVEL_6}).
     * @since 1.1
     */
    void sectionTitle_(int level);

    /**
     * Start a new header within the section or body. Shortcut for {@link #header(SinkEventAttributes)} with argument being {@code null}.
     */
    void header();

    /**
     * Start a new header within the section or body.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 2.0
     */
    void header(SinkEventAttributes attributes);

    /**
     * Ends a header element.
     */
    void header_();

    /**
     * Start the main content section between the header and the
     * footer within the sections and/or body.
     * Shortcut for {@link #content(SinkEventAttributes)} with argument being {@code null}.
     */
    void content();

    /**
     * Start the main content section between the header and the
     * footer within the sections and/or body.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 2.0
     */
    void content(SinkEventAttributes attributes);

    /**
     * Ends a main content section.
     */
    void content_();

    /**
     * Start a new footer within the section or body. Shortcut for {@link #footer(SinkEventAttributes)} with argument being {@code null}.
     */
    void footer();

    /**
     * Start a new footer within the section or body.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 2.0
     */
    void footer(SinkEventAttributes attributes);

    /**
     * Ends a footer element.
     */
    void footer_();

    /**
     * Starts an unordered list element. Shortcut for {@link #list(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #list(SinkEventAttributes)
     */
    void list();

    /**
     * Starts an unordered list.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     * </p>
     * A list must contain at least one {@link #listItem(SinkEventAttributes)} or {@link #listItem()} as direct successor of this method.
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void list(SinkEventAttributes attributes);

    /**
     * Ends an unordered list element.
     */
    void list_();

    /**
     * Starts a list item element within an unordered list. Shortcut for {@link #listItem(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #listItem(SinkEventAttributes)
     */
    void listItem();

    /**
     * Starts a list item element within an unordered list.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     * </p>
     * Nested lists must have the following Sink method sequence:
     * <ol>
     * <li>{@link #listItem(int,SinkEventAttributes)} or {@link #listItem(int)}</li>
     * <li>{@link #list(int,SinkEventAttributes)} or {@link #list(int)}</li>
     * <li>{@link #listItem(int,SinkEventAttributes)} or {@link #listItem(int)}</li>
     * <li>{@code ...}</li>
     * <li>{@link #listItem_()}</li>
     * <li>{@link #list_()}</li>
     * <li>{@code ...}</li>
     * <li>{@link #listItem_()}</li>
     * <li>{@link #list_()}</li>
     * </ol>
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void listItem(SinkEventAttributes attributes);

    /**
     * Ends a list item element within an unordered list.
     */
    void listItem_();

    /**
     * Starts an ordered list element. Shortcut for {@link #numberedList(int, SinkEventAttributes)} with first argument being {@code numbering} and second argument being {@code null}.
     *
     * @param numbering the numbering style.
     * @see #numberedList(int,SinkEventAttributes)
     */
    void numberedList(int numbering);

    /**
     * Starts an ordered list element.
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     * </p>
     * A list must contain at least one {@link #numberedListItem(SinkEventAttributes)} or {@link #numberedListItem()} as direct successor of this method.
     * @param numbering the numbering style.
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     * @see #NUMBERING_DECIMAL
     * @see #NUMBERING_LOWER_ALPHA
     * @see #NUMBERING_LOWER_ROMAN
     * @see #NUMBERING_UPPER_ALPHA
     * @see #NUMBERING_UPPER_ROMAN
     */
    void numberedList(int numbering, SinkEventAttributes attributes);

    /**
     * Ends an ordered list element.
     */
    void numberedList_();

    /**
     * Starts a list item element within an ordered list. Shortcut for {@link #numberedListItem(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #numberedListItem(SinkEventAttributes)
     */
    void numberedListItem();

    /**
     * Starts a list item element within an ordered list.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     * </p>
     * Nested lists must have the following Sink method sequence:
     * <ol>
     * <li>{@link #numberedListItem(int,SinkEventAttributes)} or {@link #numberedListItem(int)}</li>
     * <li>{@link #numberedList(int,SinkEventAttributes)} or {@link #numberedList(int)}</li>
     * <li>{@link #numberedListItem(int,SinkEventAttributes)} or {@link #numberedListItem(int)}</li>
     * <li>{@code ...}</li>
     * <li>{@link #numberedListItem_()}</li>
     * <li>{@link #numberedList_()}</li>
     * <li>{@code ...}</li>
     * <li>{@link #numberedListItem_()}</li>
     * <li>{@link #numberedList_()}</li>
     * </ol>
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void numberedListItem(SinkEventAttributes attributes);

    /**
     * Ends a list item element within an ordered list.
     */
    void numberedListItem_();

    /**
     * Starts a definition list element. Shortcut for {@link #definitionList(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #definitionList(SinkEventAttributes)
     */
    void definitionList();

    /**
     * Starts a definition list.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void definitionList(SinkEventAttributes attributes);

    /**
     * Ends a definition list element.
     */
    void definitionList_();

    /**
     * Starts a list item element within a definition list. Shortcut for {@link #definitionListItem(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #definitionListItem(SinkEventAttributes)
     */
    void definitionListItem();

    /**
     * Starts a list item element within a definition list.
     *
     * <p>
     *   Every definitionListItem has to contain exactly one {@link #definedTerm(SinkEventAttributes)}
     *   and one {@link #definition(SinkEventAttributes)}, in this order.
     * </p>
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void definitionListItem(SinkEventAttributes attributes);

    /**
     * Ends a list item element within a definition list.
     */
    void definitionListItem_();

    /**
     * Starts a definition element within a definition list. Shortcut for {@link #definition(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #definition(SinkEventAttributes)
     */
    void definition();

    /**
     * Starts a definition element within a definition list.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void definition(SinkEventAttributes attributes);

    /**
     * Ends a definition element within a definition list.
     */
    void definition_();

    /**
     * Starts a definition term element within a definition list. Shortcut for {@link #definedTerm(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #definedTerm(SinkEventAttributes)
     */
    void definedTerm();

    /**
     * Starts a definition term element within a definition list.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void definedTerm(SinkEventAttributes attributes);

    /**
     * Ends a definition term element within a definition list.
     */
    void definedTerm_();

    /**
     * Starts a basic image embedding element. Shortcut for {@link #figure(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #figure(SinkEventAttributes)
     */
    void figure();

    /**
     * Starts a basic image embedding element.
     *
     * <p>
     *   The canonical sequence of events for the figure element is:
     * </p>
     * <pre>
     *   sink.figure();
     *
     *   sink.figureGraphics("figure.png");
     *
     *   sink.figureCaption();
     *   sink.text("Figure caption",);
     *   sink.figureCaption_();
     *
     *   sink.figure_();
     * </pre>
     * <p>
     *   where the figureCaption element is optional.
     * </p>
     * <p>
     *   However, <strong>NOTE</strong> that the order of figureCaption and
     *   figureGraphics events is arbitrary,
     *   ie a parser may emit the figureCaption before or after the figureGraphics.
     *   Implementing sinks should be prepared to handle both possibilities.
     * </p>
     * <p>
     *   <strong>NOTE</strong> also that the figureGraphics() event does not have to be embedded
     *   inside figure(), in particular for in-line images the figureGraphics() should be used
     *   stand-alone (in HTML language, figureGraphics() produces a <code>&lt;img&gt;</code>
     *   tag, while figure() opens a paragraph- or <code>&lt;div&gt;</code>- like environment).
     * </p>
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void figure(SinkEventAttributes attributes);

    /**
     * Ends a basic image embedding element.
     */
    void figure_();

    /**
     * Starts a caption of an image element. Shortcut for {@link #figureCaption(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #figureCaption(SinkEventAttributes)
     */
    void figureCaption();

    /**
     * Starts a figure caption.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     * @see #figure(SinkEventAttributes)
     */
    void figureCaption(SinkEventAttributes attributes);

    /**
     * Ends a caption of an image.
     */
    void figureCaption_();

    /**
     * Adds a graphic element. Shortcut for {@link #figureGraphics(String, SinkEventAttributes)} with first argument being {@code src} and second argument being {@code null}.
     *
     * @param src the source
     */
    void figureGraphics(String src);

    /**
     * Adds a graphic element.
     *
     * <p>
     *   The <code>src</code> parameter should be a valid link, ie it can be an absolute
     *   URL or a link relative to the current source document.
     * </p>
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes} plus:
     * </p>
     * <blockquote>
     *   {@link SinkEventAttributes#SRC SRC}, {@link SinkEventAttributes#ALT ALT},
     *   {@link SinkEventAttributes#WIDTH WIDTH}, {@link SinkEventAttributes#HEIGHT HEIGHT},
     *   {@link SinkEventAttributes#ALIGN ALIGN}, {@link SinkEventAttributes#BORDER BORDER},
     *   {@link SinkEventAttributes#HSPACE HSPACE}, {@link SinkEventAttributes#VSPACE VSPACE},
     *   {@link SinkEventAttributes#ISMAP ISMAP}, {@link SinkEventAttributes#USEMAP USEMAP}.
     * </blockquote>
     * <p>
     *   If the {@link SinkEventAttributes#SRC SRC} attribute is specified in SinkEventAttributes,
     *   it will be overridden by the <code>src</code> parameter.
     * </p>
     *
     * @param src the image source, a valid URL.
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     * @see #figure(SinkEventAttributes)
     */
    void figureGraphics(String src, SinkEventAttributes attributes);

    /**
     * Starts a table element for marking up tabular information in a document. Shortcut for {@link #table(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #table(SinkEventAttributes)
     */
    void table();

    /**
     * Starts a table.
     *
     * <p>
     *   The canonical sequence of events for the table element is:
     * </p>
     * <pre>
     *   sink.table();
     *
     *   sink.tableRows(justify, true);
     *
     *   sink.tableRow();
     *   sink.tableCell();
     *   sink.text("cell 1,1");
     *   sink.tableCell_();
     *   sink.tableCell();
     *   sink.text("cell 1,2");
     *   sink.tableCell_();
     *   sink.tableRow_();
     *
     *   sink.tableRows_();
     *
     *   sink.tableCaption();
     *   sink.text("Table caption");
     *   sink.tableCaption_();
     *
     *   sink.table_();
     *
     * </pre>
     * <p>
     *   where the tableCaption element is optional.
     * </p>
     * <p>
     *   However, <strong>NOTE</strong> that the order of tableCaption and
     *   {@link #tableRows(int[],boolean)} events is arbitrary,
     *   ie a parser may emit the tableCaption before or after the tableRows.
     *   Implementing sinks should be prepared to handle both possibilities.
     * </p>
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes} plus:
     * </p>
     * <blockquote>
     *   {@link SinkEventAttributes#ALIGN ALIGN}, {@link SinkEventAttributes#BGCOLOR BGCOLOR},
     *   {@link SinkEventAttributes#BORDER BORDER}, {@link SinkEventAttributes#CELLPADDING CELLPADDING},
     *   {@link SinkEventAttributes#CELLSPACING CELLSPACING}, {@link SinkEventAttributes#FRAME FRAME},
     *   {@link SinkEventAttributes#RULES RULES}, {@link SinkEventAttributes#SUMMARY SUMMARY},
     *   {@link SinkEventAttributes#WIDTH WIDTH}.
     * </blockquote>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void table(SinkEventAttributes attributes);

    /**
     * Ends a table element.
     */
    void table_();

    /**
     * Starts an element that contains rows of table data. Shortcut for {@link #tableRows(int[], boolean)} with first argument being {@code null} and second being {@code false}.
     *
     * @see #tableRows(int[], boolean)
     */
    void tableRows();

    /**
     * Starts an element that contains rows of table data.
     *
     * @param justification the default justification of columns.
     * This can be overridden by individual table rows or table cells.
     * If null a left alignment is assumed by default. If this array
     * has less elements than there are columns in the table then the value of
     * the last array element will be taken as default for the remaining table cells.
     * Each element of the array must be one of the following constants:
     * {@link #JUSTIFY_LEFT}, {@link #JUSTIFY_CENTER}, {@link #JUSTIFY_RIGHT} or {@link #JUSTIFY_DEFAULT}.
     * @param grid true to render a grid, false otherwise.
     * @see #table(SinkEventAttributes)
     * @see #JUSTIFY_CENTER
     * @see #JUSTIFY_LEFT
     * @see #JUSTIFY_RIGHT
     * @see #JUSTIFY_DEFAULT
     */
    void tableRows(int[] justification, boolean grid);

    /**
     * Ends an element that contains rows of table data.
     */
    void tableRows_();

    /**
     * Starts a row element which acts as a container for a row of table cells. Shortcut for {@link #tableRow(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #tableRow(SinkEventAttributes)
     */
    void tableRow();

    /**
     * Starts a table row.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes} plus:
     * </p>
     * <blockquote>
     *   {@link SinkEventAttributes#ALIGN ALIGN}, {@link SinkEventAttributes#BGCOLOR BGCOLOR},
     *   {@link SinkEventAttributes#VALIGN VALIGN}.
     * </blockquote>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void tableRow(SinkEventAttributes attributes);

    /**
     * Ends a row element.
     */
    void tableRow_();

    /**
     * Starts a cell element which defines a cell that contains data. Shortcut for {@link #tableCell(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #tableCell(SinkEventAttributes)
     */
    void tableCell();

    /**
     * Starts a table cell.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes} plus:
     * </p>
     * <blockquote>
     *   {@link SinkEventAttributes#ABBRV ABBRV}, {@link SinkEventAttributes#ALIGN ALIGN},
     *   {@link SinkEventAttributes#AXIS AXIS}, {@link SinkEventAttributes#BGCOLOR BGCOLOR},
     *   {@link SinkEventAttributes#COLSPAN COLSPAN}, {@link SinkEventAttributes#HEADERS HEADERS},
     *   {@link SinkEventAttributes#HEIGHT HEIGHT}, {@link SinkEventAttributes#NOWRAP NOWRAP},
     *   {@link SinkEventAttributes#ROWSPAN ROWSPAN}, {@link SinkEventAttributes#SCOPE SCOPE},
     *   {@link SinkEventAttributes#VALIGN VALIGN}, {@link SinkEventAttributes#WIDTH WIDTH}.
     * </blockquote>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void tableCell(SinkEventAttributes attributes);

    /**
     * Ends a cell element.
     */
    void tableCell_();

    /**
     * Starts a cell element which defines a cell that contains header information. Shortcut for {@link #tableHeaderCell(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #tableHeaderCell(SinkEventAttributes)
     */
    void tableHeaderCell();

    /**
     * Starts a table header cell.
     *
     * <p>
     *   Supported attributes are the same as for {@link #tableCell(SinkEventAttributes) tableCell}.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void tableHeaderCell(SinkEventAttributes attributes);

    /**
     * Ends a cell header element.
     */
    void tableHeaderCell_();

    /**
     * Starts a caption element of a table. Shortcut for {@link #tableCaption(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #tableCaption(SinkEventAttributes)
     */
    void tableCaption();

    /**
     * Starts a table caption.
     *
     * <p>
     *   Note that the order of tableCaption and
     *   {@link #tableRows(int[],boolean)} events is arbitrary,
     *   ie a parser may emit the tableCaption before or after the tableRows.
     *   Implementing sinks should be prepared to handle both possibilities.
     * </p>
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}
     *   plus {@link SinkEventAttributes#ALIGN ALIGN}.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     * @see #table(SinkEventAttributes)
     */
    void tableCaption(SinkEventAttributes attributes);

    /**
     * Ends a caption element of a table.
     */
    void tableCaption_();

    /**
     * Starts an element which represents a paragraph. Shortcut for {@link #paragraph(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #paragraph(SinkEventAttributes)
     */
    void paragraph();

    /**
     * Starts a paragraph.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}
     *   plus {@link SinkEventAttributes#ALIGN ALIGN}.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void paragraph(SinkEventAttributes attributes);

    /**
     * Ends a paragraph element.
     */
    void paragraph_();

    /**
     * Starts a data element which groups together other elements representing microformats. Shortcut for {@link #data(String, SinkEventAttributes)} with first argument being {code value} and second argument being {@code null}.
     *
     * @see #data(String, SinkEventAttributes)
     * @param value a {@link java.lang.String} object.
     */
    void data(String value);

    /**
     * Starts a data element which groups together other elements representing microformats.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}
     *   plus {@link SinkEventAttributes#VALUE VALUE}.
     * </p>
     *
     * @param value the machine readable value of the data, may be <code>null</code>.
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 2.0
     */
    void data(String value, SinkEventAttributes attributes);

    /**
     * Ends an data element.
     */
    void data_();

    /**
     * Starts a time element which groups together other elements representing a time. Shortcut for {@link #time(String, SinkEventAttributes)} with first argument being {code datetime} and second argument being {@code null}.
     *
     * @see #time(String, SinkEventAttributes)
     */
    void time(String datetime);

    /**
     * Starts a time element which groups together other elements representing a time.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}
     *   plus {@link SinkEventAttributes#DATETIME DATETIME}.
     * </p>
     *
     * @param datetime the machine readable value of the time, may be <code>null</code>.
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 2.0
     */
    void time(String datetime, SinkEventAttributes attributes);

    /**
     * Ends a time element.
     */
    void time_();

    /**
     * Starts an address element. Shortcut for {@link #address(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #address(SinkEventAttributes)
     */
    void address();

    /**
     * Starts an address element.
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 2.0
     */
    void address(SinkEventAttributes attributes);

    /**
     * Ends an address element.
     */
    void address_();

    /**
     * Starts a blockquote element. Shortcut for {@link #blockquote(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #blockquote(SinkEventAttributes)
     */
    void blockquote();

    /**
     * Starts a blockquote element.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 2.0
     */
    void blockquote(SinkEventAttributes attributes);

    /**
     * Ends an blockquote element.
     */
    void blockquote_();

    /**
     * Starts a division element grouping together other elements. Shortcut for {@link #division(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #division(SinkEventAttributes)
     */
    void division();

    /**
     * Starts a division element grouping together other elements.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}
     *   plus {@link SinkEventAttributes#ALIGN ALIGN}.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 2.0
     */
    void division(SinkEventAttributes attributes);

    /**
     * Ends a division element.
     */
    void division_();

    /**
     * Starts a verbatim block, ie a block where whitespace has semantic relevance. Shortcut for {@link #verbatim(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #verbatim(SinkEventAttributes)
     */
    void verbatim();

    /**
     * Starts a verbatim block, ie a block where whitespace has semantic relevance.
     *
     * <p>
     *   Text in a verbatim block must only be wrapped at the linebreaks in the source,
     *   and spaces should not be collapsed. It should be displayed in a fixed-width font to
     *   retain the formatting but the overall size may be chosen by the implementation.
     * </p>
     *
     * <p>
     *   Most Sink events may be emitted within a verbatim block, the only elements explicitly
     *   forbidden are font-changing events and figures. Also, verbatim blocks may not be nested.
     * </p>
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes} plus:
     * </p>
     * <blockquote>
     *   {@link SinkEventAttributes#DECORATION DECORATION} (values: "source"),
     *   {@link SinkEventAttributes#ALIGN ALIGN}, {@link SinkEventAttributes#WIDTH WIDTH}.
     * </blockquote>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void verbatim(SinkEventAttributes attributes);

    /**
     * Ends a verbatim element.
     */
    void verbatim_();

    /**
     * Adding a separator of sections from a text to each other. Shortcut for {@link #horizontalRule(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #horizontalRule(SinkEventAttributes)
     */
    void horizontalRule();

    /**
     * Adds a horizontal separator rule.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes} plus:
     * </p>
     * <blockquote>
     *   {@link SinkEventAttributes#ALIGN ALIGN}, {@link SinkEventAttributes#NOSHADE NOSHADE},
     *   {@link SinkEventAttributes#SIZE SIZE}, {@link SinkEventAttributes#WIDTH WIDTH}.
     * </blockquote>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void horizontalRule(SinkEventAttributes attributes);

    /**
     * Adding a new page separator.
     */
    void pageBreak();

    /**
     * Starts an element which defines an anchor. Shortcut for {@link #anchor(String, SinkEventAttributes)} with first argument being {@code name} and second argument being {@code null}.
     *
     * @param name the name of the anchor.
     * @see #anchor(String,SinkEventAttributes)
     */
    void anchor(String name);

    /**
     * Starts an element which defines an anchor.
     *
     * <p>
     *   The <code>name</code> parameter has to be a valid SGML NAME token.
     *   According to the <a href="http://www.w3.org/TR/html4/types.html#type-name">
     *   HTML 4.01 specification section 6.2 SGML basic types</a>:
     * </p>
     * <p>
     *   <i>ID and NAME tokens must begin with a letter ([A-Za-z]) and may be
     *   followed by any number of letters, digits ([0-9]), hyphens ("-"),
     *   underscores ("_"), colons (":"), and periods (".").</i>
     * </p>
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     *   If {@link SinkEventAttributes#NAME NAME} is specified in the SinkEventAttributes,
     *   it will be overwritten by the <code>name</code> parameter.
     * </p>
     *
     * @param name the name of the anchor. This has to be a valid SGML NAME token.
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void anchor(String name, SinkEventAttributes attributes);

    /**
     * Ends an anchor element.
     */
    void anchor_();

    /**
     * Starts an element which defines a link. Shortcut for {@link #link(String, SinkEventAttributes)} with first argument being {@code name} and second argument being {@code null}.
     *
     * @param name the name of the link.
     * @see #link(String,SinkEventAttributes)
     */
    void link(String name);

    /**
     * Starts a link.
     *
     * <p>
     *   The <code>name</code> parameter has to be a valid URI according to
     *   <a href="https://datatracker.ietf.org/doc/html/rfc3986">RFC 3986</a>,
     *   i.e. for internal links (links to an anchor within the same source
     *   document), <code>name</code> should start with the character "#".
     *   This also implies that all unsafe characters are already encoded.
     * </p>
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes} plus:
     * </p>
     * <blockquote>
     *   {@link SinkEventAttributes#CHARSET CHARSET}, {@link SinkEventAttributes#COORDS COORDS},
     *   {@link SinkEventAttributes#HREF HREF}, {@link SinkEventAttributes#HREFLANG HREFLANG},
     *   {@link SinkEventAttributes#REL REL}, {@link SinkEventAttributes#REV REV},
     *   {@link SinkEventAttributes#SHAPE SHAPE}, {@link SinkEventAttributes#TARGET TARGET},
     *   {@link SinkEventAttributes#TYPE TYPE}.
     * </blockquote>
     * <p>
     *   If {@link SinkEventAttributes#HREF HREF} is specified in the
     *   SinkEventAttributes, it will be overwritten by the <code>name</code> parameter.
     * </p>
     *
     * @param name the name of the link.
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     * @see java.net.URI#toASCIIString()
     */
    void link(String name, SinkEventAttributes attributes);

    /**
     * Ends a link element.
     */
    void link_();

    /**
     * Starts an inline element. Shortcut for {@link #inline(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #inline(SinkEventAttributes)
     */
    void inline();

    /**
     * Starts an inline element.
     *
     * <p>
     *   The inline method is similar to {@link #text(String,SinkEventAttributes)}, but
     *   allows you to wrap arbitrary elements in addition to text.
     * </p>
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes} plus
     * </p>
     * <blockquote>
     *   {@link SinkEventAttributes#SEMANTICS SEMANTICS} (values "emphasis", "strong",
     *   "small", "line-through", "citation", "quote", "definition", "abbreviation",
     *   "italic", "bold", "monospaced", "variable", "sample", "keyboard", "superscript",
     *   "subscript", "annotation", "highlight", "ruby", "rubyBase", "rubyText",
     *   "rubyTextContainer", "rubyParentheses", "bidirectionalIsolation",
     *   "bidirectionalOverride", "phrase", "insert", "delete").
     * </blockquote>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 2.0
     */
    void inline(SinkEventAttributes attributes);

    /**
     * Ends an inline element.
     */
    void inline_();

    /**
     * Starts an italic element.
     *
     * Alternatively one may use {@link #text(String,SinkEventAttributes)} with
     *              {@link SinkEventAttributes#STYLE STYLE} instead.
     */
    void italic();

    /**
     * Ends an italic element.
     *
     * Alternatively one may use {@link #text(String,SinkEventAttributes)} with
     *              {@link SinkEventAttributes#STYLE STYLE} instead.
     */
    void italic_();

    /**
     * Starts a bold element.
     *
     * Alternatively one may use {@link #text(String,SinkEventAttributes)} with
     *              {@link SinkEventAttributes#STYLE STYLE} instead.
     */
    void bold();

    /**
     * Ends a bold element.
     *
     * Alternatively one may use {@link #text(String,SinkEventAttributes)} with
     *              {@link SinkEventAttributes#STYLE STYLE} instead.
     */
    void bold_();

    /**
     * Starts a monospaced element.
     *
     * Alternatively one may use {@link #text(String,SinkEventAttributes)} with
     *              {@link SinkEventAttributes#STYLE STYLE} instead.
     */
    void monospaced();

    /**
     * Ends a monospaced element.
     *
     * Alternatively one may use {@link #text(String,SinkEventAttributes)} with
     *              {@link SinkEventAttributes#STYLE STYLE} instead.
     */
    void monospaced_();

    /**
     * Adds a line break. Shortcut for {@link #lineBreak(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #lineBreak(SinkEventAttributes)
     */
    void lineBreak();

    /**
     * Adds a line break.
     *
     * <p>
     *   Supported attributes are:
     * </p>
     * <blockquote>
     *   {@link SinkEventAttributes#ID ID}, {@link SinkEventAttributes#CLASS CLASS},
     *   {@link SinkEventAttributes#TITLE TITLE}, {@link SinkEventAttributes#STYLE STYLE}.
     * </blockquote>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void lineBreak(SinkEventAttributes attributes);

    /**
     * Adds a line break opportunity. Shortcut for {@link #lineBreakOpportunity(SinkEventAttributes)} with argument being {@code null}.
     *
     * @see #lineBreak(SinkEventAttributes)
     */
    void lineBreakOpportunity();

    /**
     * Adds a line break opportunity.
     *
     * <p>
     *   Supported attributes are:
     * </p>
     * <blockquote>
     *   {@link SinkEventAttributes#ID ID}, {@link SinkEventAttributes#CLASS CLASS},
     *   {@link SinkEventAttributes#TITLE TITLE}, {@link SinkEventAttributes#STYLE STYLE}.
     * </blockquote>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 2.0
     */
    void lineBreakOpportunity(SinkEventAttributes attributes);

    /**
     * Adding a non breaking space, <i>ie</i> a space without any special formatting operations.
     */
    void nonBreakingSpace();

    /**
     * Adding a text. Shortcut for {@link #text(String, SinkEventAttributes)} with first argument being {@code text} and second argument being {@code null}.
     *
     * @param text The text to write.
     * @see #text(String,SinkEventAttributes)
     */
    void text(String text);

    /**
     * Adds a text.
     *
     * <p>
     *   The <code>text</code> parameter should contain only real content, ie any
     *   ignorable/collapsable whitespace/EOLs or other pretty-printing should
     *   be removed/normalized by a parser.
     * </p>
     * <p>
     *   If <code>text</code> contains any variants of line terminators, they should
     *   be normalized to the System EOL by an implementing Sink.
     * </p>
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes} plus
     * </p>
     * <blockquote>
     *   {@link SinkEventAttributes#SEMANTICS SEMANTICS} (values "emphasis", "strong",
     *   "small", "line-through", "citation", "quote", "definition", "abbreviation",
     *   "italic", "bold", "monospaced", "variable", "sample", "keyboard", "superscript",
     *   "subscript", "annotation", "highlight", "ruby", "rubyBase", "rubyText",
     *   "rubyTextContainer", "rubyParentheses", "bidirectionalIsolation",
     *   "bidirectionalOverride", "phrase", "insert", "delete").
     * </blockquote>
     * <p>
     *   The following attributes are deprecated:
     * </p>
     * <blockquote>
     *   {@link SinkEventAttributes#VALIGN VALIGN} (values "sub", "sup"),
     *   {@link SinkEventAttributes#DECORATION DECORATION} (values "underline", "overline", "line-through"),
     *   {@link SinkEventAttributes#STYLE STYLE} (values "italic", "bold", "monospaced").
     * </blockquote>
     *
     * @param text The text to write.
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void text(String text, SinkEventAttributes attributes);

    /**
     * Adding a raw text, <i>ie</i> a text without any special formatting operations.
     *
     * @param text The text to write.
     */
    void rawText(String text);

    /**
     * Add a comment.
     * Semantically the same as {@link #comment(String, boolean)} with second argument being {@code false}.
     *
     * @param comment The comment to write.
     * @since 1.1
     * @see #comment(String, boolean)
     */
    void comment(String comment);

    /**
     * Add a comment. The default implementation will just call {@link #comment(String)}.
     *
     * @param comment The comment to write.
     * @param endsWithLineBreak If {@code true} comment ends with a line break, i.e. nothing else should follow on the same line
     * @since 2.1.0
     */
    default void comment(String comment, boolean endsWithLineBreak) {
        comment(comment);
    }

    /**
     * Add an unknown event. This may be used by parsers to notify a general Sink about
     * an event that doesn't fit into any event defined by the Sink API.
     * Depending on the parameters, a Sink may decide whether or not to process the event,
     * emit it as raw text, as a comment, log it, etc.
     *
     * @param name The name of the event.
     * @param requiredParams An optional array of required parameters to the event.
     * May be <code>null</code>.
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void unknown(String name, Object[] requiredParams, SinkEventAttributes attributes);

    /**
     * Flush the writer or the stream, if needed.
     * Flushing a previously-flushed Sink has no effect.
     */
    void flush();

    /**
     * Close the writer or the stream, if needed.
     * Closing a previously-closed Sink has no effect.
     */
    void close();

    /**
     * Sets the locator which exposes location information for a particular Sink event.
     * @param locator the locator (never {@code null}).
     * @since 2.0.0
     */
    default void setDocumentLocator(Locator locator) {}

    /**
     * Returns the locator which exposes location information for a particular Sink event.
     * @return the locator (never {@code null}).
     * @since 2.0.0
     */
    default Locator getDocumentLocator() {
        return EmptyLocator.INSTANCE;
    }
}
