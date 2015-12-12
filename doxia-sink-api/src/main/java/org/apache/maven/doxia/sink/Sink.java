package org.apache.maven.doxia.sink;

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

import org.apache.maven.doxia.logging.LogEnabled;

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
 * @version $Id$
 */
public interface Sink
    extends LogEnabled
{
    /** The Plexus Sink Role. */
    String ROLE = Sink.class.getName();

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
     * Starts the head element.
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
     *   sink.text( "Title" );
     *   sink.title_();
     *
     *   sink.author();
     *   sink.text( "Author" );
     *   sink.author_();
     *
     *   sink.date();
     *   sink.text( "Date" );
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
    void head( SinkEventAttributes attributes );

    /**
     * Ends the head element.
     */
    void head_();

    /**
     * Starts the title element.
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
    void title( SinkEventAttributes attributes );

    /**
     * Ends the title element.
     */
    void title_();

    /**
     * Starts an author element.
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
    void author( SinkEventAttributes attributes );

    /**
     * Ends an author element.
     */
    void author_();

    /**
     * Starts the date element.
     * <br/>
     * The date is recommended (but it is not a requirement) to be align to the
     * <a href="http://www.iso.org/iso/iso_catalogue/catalogue_tc/catalogue_detail.htm?csnumber=26780">ISO-8601</a>
     * standard, i.e.:
     * <pre>
     * YYYY-MM-DD
     * </pre>
     * where
     * <ul>
     * <li>YYYY is the year in the Gregorian calendar</li>
     * <li>MM is the month of the year between 01 (January) and 12 (December)</li>
     * <li>and DD is the day of the month between 01 and 31</li>
     * </ul>
     *
     * @see #date(SinkEventAttributes)
     */
    void date();

    /**
     * Starts the date element. This is used to identify the date of the document.
     *
     * <p>
     *   Supported attributes are: none.
     * </p>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     * @see #head(SinkEventAttributes)
     */
    void date( SinkEventAttributes attributes );

    /**
     * Ends the date element.
     */
    void date_();

    /**
     * Starts the body of a document.
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
    void body( SinkEventAttributes attributes );

    /**
     * Ends the body element.
     */
    void body_();

    /**
     * Starts a title heading element.
     */
    void sectionTitle();

    /**
     * Ends a title heading element.
     */
    void sectionTitle_();

    /**
     * Starts a first heading element which contains the topic of the section.
     *
     * @see #section(int,SinkEventAttributes)
     */
    void section1();

    /**
     * Ends a first heading element.
     */
    void section1_();

    /**
     * Starts a first title heading element. This element is optional, but if it exists,
     * it has to be contained, and be the first element, within a {@link #section1()} element.
     *
     * @see #sectionTitle(int,SinkEventAttributes)
     */
    void sectionTitle1();

    /**
     * Ends a first title heading element.
     */
    void sectionTitle1_();

    /**
     * Starts a second heading element which contains the topic of the section.
     * This has to be contained within a {@link #section1()} element.
     *
     * @see #section(int,SinkEventAttributes)
     */
    void section2();

    /**
     * Ends a second heading element.
     */
    void section2_();

    /**
     * Starts a second title heading element. This element is optional, but if it exists,
     * it has to be contained, and be the first element, within a {@link #section2()} element.
     *
     * @see #sectionTitle(int,SinkEventAttributes)
     */
    void sectionTitle2();

    /**
     * Ends a second title heading element.
     */
    void sectionTitle2_();

    /**
     * Starts a third heading element which contains the topic of the section.
     * This has to be contained within a {@link #section2()} element.
     *
     * @see #section(int,SinkEventAttributes)
     */
    void section3();

    /**
     * Ends a third heading element.
     */
    void section3_();

    /**
     * Starts a third title heading element. This element is optional, but if it exists,
     * it has to be contained, and be the first element, within a {@link #section3()} element.
     *
     * @see #sectionTitle(int,SinkEventAttributes)
     */
    void sectionTitle3();

    /**
     * Ends a third title heading element.
     */
    void sectionTitle3_();

    /**
     * Starts a 4th heading element which contains the topic of the section.
     * This has to be contained within a {@link #section3()} element.
     *
     * @see #section(int,SinkEventAttributes)
     */
    void section4();

    /**
     * Ends a 4th heading element.
     */
    void section4_();

    /**
     * Starts a 4th title heading element. This element is optional, but if it exists,
     * it has to be contained, and be the first element, within a {@link #section4()} element.
     *
     * @see #sectionTitle(int,SinkEventAttributes)
     */
    void sectionTitle4();

    /**
     * Ends a 4th title heading element.
     */
    void sectionTitle4_();

    /**
     * Starts a 5th heading element which contains the topic of the section.
     * This has to be contained within a {@link #section4()} element.
     *
     * @see #section(int,SinkEventAttributes)
     */
    void section5();

    /**
     * Ends a 5th heading element.
     */
    void section5_();

    /**
     * Starts a 5th title heading element. This element is optional, but if it exists,
     * it has to be contained, and be the first element, within a {@link #section5()} element.
     *
     * @see #sectionTitle(int,SinkEventAttributes)
     */
    void sectionTitle5();

    /**
     * Ends a 5th title heading element.
     */
    void sectionTitle5_();

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
     * @param level the section level.
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void section( int level, SinkEventAttributes attributes );

    /**
     * Ends a section at the given level.
     *
     * @param level the section level.
     * @since 1.1
     */
    void section_( int level );

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
     * @param level the section title level.
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void sectionTitle( int level, SinkEventAttributes attributes );

    /**
     * Ends a section title at the given level.
     *
     * @param level the section title level.
     * @since 1.1
     */
    void sectionTitle_( int level );

    /**
     * Starts an unordered list element.
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
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void list( SinkEventAttributes attributes );

    /**
     * Ends an unordered list element.
     */
    void list_();

    /**
     * Starts a list item element within an unordered list.
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
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void listItem( SinkEventAttributes attributes );

    /**
     * Ends a list item element within an unordered list.
     */
    void listItem_();

    /**
     * Starts an ordered list element.
     *
     * @param numbering the numbering style.
     * @see #numberedList(int,SinkEventAttributes)
     */
    void numberedList( int numbering );

    /**
     * Starts an ordered list element.
     *
     * <p>
     *   Supported attributes are the {@link SinkEventAttributes base attributes}.
     * </p>
     *
     * @param numbering the numbering style.
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     * @see #NUMBERING_DECIMAL
     * @see #NUMBERING_LOWER_ALPHA
     * @see #NUMBERING_LOWER_ROMAN
     * @see #NUMBERING_UPPER_ALPHA
     * @see #NUMBERING_UPPER_ROMAN
     */
    void numberedList( int numbering, SinkEventAttributes attributes );

    /**
     * Ends an ordered list element.
     */
    void numberedList_();

    /**
     * Starts a list item element within an ordered list.
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
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void numberedListItem( SinkEventAttributes attributes );

    /**
     * Ends a list item element within an ordered list.
     */
    void numberedListItem_();

    /**
     * Starts a definition list element.
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
    void definitionList( SinkEventAttributes attributes );

    /**
     * Ends a definition list element.
     */
    void definitionList_();

    /**
     * Starts a list item element within a definition list.
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
    void definitionListItem( SinkEventAttributes attributes );

    /**
     * Ends a list item element within a definition list.
     */
    void definitionListItem_();

    /**
     * Starts a definition element within a definition list.
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
    void definition( SinkEventAttributes attributes );

    /**
     * Ends a definition element within a definition list.
     */
    void definition_();

    /**
     * Starts a definition term element within a definition list.
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
    void definedTerm( SinkEventAttributes attributes );

    /**
     * Starts a definition term element within a definition list.
     */
    void definedTerm_();

    /**
     * Starts a basic image embedding element.
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
     *   sink.figureGraphics( "figure.png" );
     *
     *   sink.figureCaption();
     *   sink.text( "Figure caption",);
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
    void figure( SinkEventAttributes attributes );

    /**
     * Ends a basic image embedding element.
     */
    void figure_();

    /**
     * Starts a caption of an image element.
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
    void figureCaption( SinkEventAttributes attributes );

    /**
     * Ends a caption of an image.
     */
    void figureCaption_();

    /**
     * Adding a source of a graphic.
     *
     * @param name the source
     */
    void figureGraphics( String name );

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
    void figureGraphics( String src, SinkEventAttributes attributes );

    /**
     * Starts a table element for marking up tabular information in a document.
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
     *   sink.tableRows( justify, true );
     *
     *   sink.tableRow();
     *   sink.tableCell();
     *   sink.text( "cell 1,1" );
     *   sink.tableCell_();
     *   sink.tableCell();
     *   sink.text( "cell 1,2" );
     *   sink.tableCell_();
     *   sink.tableRow_();
     *
     *   sink.tableRows_();
     *
     *   sink.tableCaption();
     *   sink.text( "Table caption" );
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
    void table( SinkEventAttributes attributes );

    /**
     * Ends a table element.
     */
    void table_();

    /**
     * Starts an element that contains rows of table data.
     *
     * @param justification the default justification of columns.
     * This can be overridden by individual table rows or table cells.
     * If null a left alignment is assumed by default. If this array
     * has less elements than there are columns in the table then the value of
     * the last array element will be taken as default for the remaining table cells.
     * @param grid true to provide a grid, false otherwise.
     * @see #table(SinkEventAttributes)
     * @see #JUSTIFY_CENTER
     * @see #JUSTIFY_LEFT
     * @see #JUSTIFY_RIGHT
     */
    void tableRows( int[] justification, boolean grid );

    /**
     * Ends an element that contains rows of table data.
     */
    void tableRows_();

    /**
     * Starts a row element which acts as a container for a row of table cells.
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
    void tableRow( SinkEventAttributes attributes );

    /**
     * Ends a row element.
     */
    void tableRow_();

    /**
     * Starts a cell element which defines a cell that contains data.
     *
     * @see #tableCell(SinkEventAttributes)
     */
    void tableCell();

    /**
     * Starts a cell element which defines a cell that contains data.
     *
     * @param width the size of the cell.
     * @deprecated Use #tableCell(SinkEventAttributes) instead.
     */
    void tableCell( String width );

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
    void tableCell( SinkEventAttributes attributes );

    /**
     * Ends a cell element.
     */
    void tableCell_();

    /**
     * Starts a cell element which defines a cell that contains header information.
     *
     * @see #tableHeaderCell(SinkEventAttributes)
     */
    void tableHeaderCell();

    /**
     * Starts a cell element which defines a cell that contains header information.
     *
     * @param width the size of the header cell.
     * @deprecated Use #tableHeaderCell(SinkEventAttributes) instead.
     */
    void tableHeaderCell( String width );

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
    void tableHeaderCell( SinkEventAttributes attributes );

    /**
     * Ends a cell header element.
     */
    void tableHeaderCell_();

    /**
     * Starts a caption element of a table.
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
    void tableCaption( SinkEventAttributes attributes );

    /**
     * Ends a caption element of a table.
     */
    void tableCaption_();

    /**
     * Starts an element which represents a paragraph.
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
    void paragraph( SinkEventAttributes attributes );

    /**
     * Ends a paragraph element.
     */
    void paragraph_();

    /**
     * Starts an element which indicates that whitespace in the enclosed text has semantic relevance.
     *
     * @param boxed true to add a box, false otherwise
     * @deprecated Use #verbatim(SinkEventAttributes) instead.
     */
    void verbatim( boolean boxed );

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
     *   {@link SinkEventAttributes#DECORATION DECORATION} (value: "boxed"),
     *   {@link SinkEventAttributes#ALIGN ALIGN}, {@link SinkEventAttributes#WIDTH WIDTH}.
     * </blockquote>
     *
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void verbatim( SinkEventAttributes attributes );

    /**
     * Ends a verbatim element.
     */
    void verbatim_();

    /**
     * Adding a separator of sections from a text to each other.
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
    void horizontalRule( SinkEventAttributes attributes );

    /**
     * Adding a new page separator.
     */
    void pageBreak();

    /**
     * Starts an element which defines an anchor.
     *
     * @param name the name of the anchor.
     * @see #anchor(String,SinkEventAttributes)
     */
    void anchor( String name );

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
    void anchor( String name, SinkEventAttributes attributes );

    /**
     * Ends an anchor element.
     */
    void anchor_();

    /**
     * Starts an element which defines a link.
     *
     * @param name the name of the link.
     * @see #link(String,SinkEventAttributes)
     */
    void link( String name );

    /**
     * Starts a link.
     *
     * <p>
     *   The <code>name</code> parameter has to be a valid html <code>href</code>
     *   parameter, ie for internal links (links to an anchor within the same source
     *   document), <code>name</code> should start with the character "#".
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
     */
    void link( String name, SinkEventAttributes attributes );

    /**
     * Ends a link element.
     */
    void link_();

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
     * Adds a line break.
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
    void lineBreak( SinkEventAttributes attributes );

    /**
     * Adding a non breaking space, <i>ie</i> a space without any special formatting operations.
     */
    void nonBreakingSpace();

    /**
     * Adding a text.
     *
     * @param text The text to write.
     * @see #text(String,SinkEventAttributes)
     */
    void text( String text );

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
     *   {@link SinkEventAttributes#VALIGN VALIGN} (values "sub", "sup"),
     *   {@link SinkEventAttributes#DECORATION DECORATION} (values "underline", "overline", "line-through"),
     *   {@link SinkEventAttributes#STYLE STYLE} (values "italic", "bold", "monospaced").
     * </blockquote>
     *
     * @param text The text to write.
     * @param attributes A set of {@link SinkEventAttributes}, may be <code>null</code>.
     * @since 1.1
     */
    void text( String text, SinkEventAttributes attributes );

    /**
     * Adding a raw text, <i>ie</i> a text without any special formatting operations.
     *
     * @param text The text to write.
     */
    void rawText( String text );

    /**
     * Add a comment.
     *
     * @param comment The comment to write.
     * @since 1.1
     */
    void comment( String comment );

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
    void unknown( String name, Object[] requiredParams, SinkEventAttributes attributes );

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
}
