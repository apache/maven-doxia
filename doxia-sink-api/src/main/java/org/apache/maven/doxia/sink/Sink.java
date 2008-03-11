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
 * A <i>Sink</i> consumes Doxia events in a resultant output format like
 * Docbook, PDF, or XHTML.
 * <p>
 * Doxia allows you to parse any supported input document format
 * (ie for which a parser exists) and generate any supported output
 * document format (ie for which a sink exists).
 * </p>
 * <p>
 * The upshot is that you can parse any front-end format, the parser is
 * responsible for emitting the standard Doxia events which can then be
 * consumed by any Doxia Sink. This is what allow us to parse the front-
 * end format like APT, FML, and Xdoc for the Maven site plugin and have
 * them all contribute to the final XHTML version of a site. All
 * documents being parsed results in a stream of Doxia events
 * (paragraph, bold, italic, text) which are then fed in the XHTML sink
 * which results in a set of XHTML pages.
 * </p>
 * <p>
 * A sink is ultimately responsible for the final format and structure.
 * So, for example, you can take a series of APT documents and have that
 * be fed into a Sink which makes a single PDF, a book, a site, or a
 * Word document. The Sink is fully responsible for the final output.
 * Once you have Doxia events you can leverage any existing Sink. So if
 * you wanted to integrate your custom XML format, or custom Wiki
 * format, you would create a Doxia parser which could then be fed into
 * any Sink to produce your desired final output.
 * </p>
 *
 * @since 1.0
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @author ltheussl
 * @version $Id$
 */
public interface Sink
    extends LogEnabled
{
    /**
     * The Plexus Sink Role
     */
    String ROLE = Sink.class.getName();

    /**
     * A numbering to handle a number list.
     * @see #numberedList(int)
     */
    int NUMBERING_DECIMAL = 0;

    /**
     * A numbering to handle a lower alpha list.
     * @see #numberedList(int)
     */
    int NUMBERING_LOWER_ALPHA = 1;

    /**
     * A numbering to handle a upper alpha list.
     * @see #numberedList(int)
     */
    int NUMBERING_UPPER_ALPHA = 2;

    /**
     * A numbering to handle a lower roman list.
     * @see #numberedList(int)
     */
    int NUMBERING_LOWER_ROMAN = 3;

    /**
     * A numbering to handle a upper roman list.
     * @see #numberedList(int)
     */
    int NUMBERING_UPPER_ROMAN = 4;

    /**
     * A level 1 section (section).
     * @see #section1()
     */
    int SECTION_LEVEL_1 = 1;

    /**
     * A level 2 section (subsection).
     * @see #section2()
     */
    int SECTION_LEVEL_2 = 2;

    /**
     * A level 3 section (sub-subsection).
     * @see #section3()
     */
    int SECTION_LEVEL_3 = 3;

    /**
     * A level 4 section (sub-sub-subsection).
     * @see #section4()
     */
    int SECTION_LEVEL_4 = 4;

    /**
     * A level 5 section (sub-sub-sub-subsection).
     * @see #section5()
     */
    int SECTION_LEVEL_5 = 5;

    /**
     * Starting the head element which contains information about the current document,
     * such as its title, that is not considered document content.
     */
    void head();

    /**
     * Starts the head element.
     * Supported attributes are: "profile", DIR, LANG.
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void head( SinkEventAttributes attributes );

    /**
     * Ending the head element.
     */
    void head_();

    /**
     * Starting the title element which is used to identify the document.
     */
    void title();

    /**
     * Starts the title.
     * Supported attributes are the {@link SinkEventAttributes base attributes}.
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void title( SinkEventAttributes attributes );

    /**
     * Ending the title element.
     */
    void title_();

    /**
     * Starting the author element which is used to identify the author of the document.
     */
    void author();

    /**
     * Starts an author element.
     * Supported attributes are: "email".
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void author( SinkEventAttributes attributes );

    /**
     * Ending the author element.
     */
    void author_();

    /**
     * Starting the date element which is used to identify the date of the document.
     */
    void date();

    /**
     * Starts a date element.
     * Supported attributes are: none.
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void date( SinkEventAttributes attributes );

    /**
     * Ending the date element.
     */
    void date_();

    /**
     * Starting the body of a document which contains the document's content.
     */
    void body();

    /**
     * Starts the body of a document which contains the document's content.
     * Supported attributes are the {@link SinkEventAttributes base attributes}.
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void body( SinkEventAttributes attributes );
    /**
     * Ending the body element.
     */
    void body_();

    /**
     * Starting a title heading element.
     */
    void sectionTitle();

    /**
     * Ending a title heading element.
     */
    void sectionTitle_();

    /**
     * Starting a first heading element which contains the topic of the section.
     */
    void section1();

    /**
     * Ending a first heading element.
     */
    void section1_();

    /**
     * Starting a first title heading element.
     */
    void sectionTitle1();

    /**
     * Ending a first title heading element.
     */
    void sectionTitle1_();

    /**
     * Starting a second heading element which contains the topic of the section.
     */
    void section2();

    /**
     * Ending a second heading element.
     */
    void section2_();

    /**
     * Starting a second title heading element.
     */
    void sectionTitle2();

    /**
     * Ending a second title heading element.
     */
    void sectionTitle2_();

    /**
     * Starting a third heading element which contains the topic of the section.
     */
    void section3();

    /**
     * Ending a third heading element.
     */
    void section3_();

    /**
     * Starting a third title heading element.
     */
    void sectionTitle3();

    /**
     * Ending a third title heading element.
     */
    void sectionTitle3_();

    /**
     * Starting a 4th heading element which contains the topic of the section.
     */
    void section4();

    /**
     * Ending a 4th heading element.
     */
    void section4_();

    /**
     * Starting a 4th title heading element.
     */
    void sectionTitle4();

    /**
     * Ending a 4th title heading element.
     */
    void sectionTitle4_();

    /**
     * Starting a 5th heading element which contains the topic of the section.
     */
    void section5();

    /**
     * Ending a 5th heading element.
     */
    void section5_();

    /**
     * Starting a 5th title heading element.
     */
    void sectionTitle5();

    /**
     * Ending a 5th title heading element.
     */
    void sectionTitle5_();

    /**
     * Start a new section at the given level.
     * Supported attributes are the {@link SinkEventAttributes base attributes}.
     *
     * @param level the section level.
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void section( int level, SinkEventAttributes attributes );

    /**
     * Ends a section at the given level.
     *
     * @param level the section level.
     */
    void section_( int level );

    /**
     * Start a new section title at the given level.
     * Supported attributes are the {@link SinkEventAttributes base attributes} and ALIGN.
     *
     * @param level the section title level.
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void sectionTitle( int level, SinkEventAttributes attributes );

    /**
     * Ends a section title at the given level.
     *
     * @param level the section title level.
     */
    void sectionTitle_( int level );

    /**
     * Starting an unordered list element.
     */
    void list();

    /**
     * Starts an unordered list.
     * Supported attributes are the {@link SinkEventAttributes base attributes}.
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void list( SinkEventAttributes attributes );
    /**
     * Ending an unordered list element.
     */
    void list_();

    /**
     * Starting a list item element within an unordered list.
     */
    void listItem();

    /**
     * Starts a list item element within an unordered list.
     * Supported attributes are the {@link SinkEventAttributes base attributes}.
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void listItem( SinkEventAttributes attributes );

    /**
     * Ending a list item element within an unordered list.
     */
    void listItem_();

    /**
     * Starting an ordered list element.
     *
     * @param numbering the numbering style.
     * @see #NUMBERING_DECIMAL
     * @see #NUMBERING_LOWER_ALPHA
     * @see #NUMBERING_LOWER_ROMAN
     * @see #NUMBERING_UPPER_ALPHA
     * @see #NUMBERING_UPPER_ROMAN
     */
    void numberedList( int numbering );

    /**
     * Starts an ordered list element.
     * Supported attributes are the {@link SinkEventAttributes base attributes}.
     *
     * @param numbering the numbering style.
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void numberedList( int numbering, SinkEventAttributes attributes );
    /**
     * Ending an ordered list element.
     */
    void numberedList_();

    /**
     * Starting a list item element within an ordered list.
     */
    void numberedListItem();

    /**
     * Starts a list item element within an ordered list.
     * Supported attributes are the {@link SinkEventAttributes base attributes}.
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void numberedListItem( SinkEventAttributes attributes );

    /**
     * Ending a list item element within an ordered list.
     */
    void numberedListItem_();

    /**
     * Starting a definition list element.
     */
    void definitionList();

    /**
     * Starts a definition list.
     * Supported attributes are the {@link SinkEventAttributes base attributes}.
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void definitionList( SinkEventAttributes attributes );

    /**
     * Ending a definition list element.
     */
    void definitionList_();

    /**
     * Starting a list item element within a definition list.
     */
    void definitionListItem();

    /**
     * Starting a list item element within a definition list.
     * Supported attributes are the {@link SinkEventAttributes base attributes}.
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void definitionListItem( SinkEventAttributes attributes );

    /**
     * Ending a list item element within a definition list.
     */
    void definitionListItem_();

    /**
     * Starting a definition element within a definition list.
     */
    void definition();

    /**
     * Starts a definition element within a definition list.
     * Supported attributes are the {@link SinkEventAttributes base attributes}.
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void definition( SinkEventAttributes attributes );

    /**
     * Ending a definition element within a definition list.
     */
    void definition_();

    /**
     * Starting a definition term element within a definition list.
     */
    void definedTerm();

    /**
     * Starts a definition term element within a definition list.
     * Supported attributes are the {@link SinkEventAttributes base attributes}.
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void definedTerm( SinkEventAttributes attributes );

    /**
     * Starting a definition term element within a definition list.
     */
    void definedTerm_();

    /**
     * Starting a basic image embedding element.
     */
    void figure();

    /**
     * Starting a basic image embedding element.
     * Supported attributes are the {@link SinkEventAttributes base attributes}.
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void figure( SinkEventAttributes attributes );

    /**
     * Ending a basic image embedding element.
     */
    void figure_();

    /**
     * Starting a caption of an image element.
     */
    void figureCaption();

    /**
     * Starts a figure caption.
     * Supported attributes are the {@link SinkEventAttributes base attributes}.
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void figureCaption( SinkEventAttributes attributes );
    /**
     * Ending a caption of an image.
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
     * Supported attributes are the {@link SinkEventAttributes base attributes} plus:
     * <blockquote>
     * SRC, ALT, WIDTH, HEIGHT, ALIGN, BORDER, HSPACE, VSPACE, ISMAP, USEMAP.
     * </blockquote>
     * If the SRC attribute is specified, it will be overidden by the src parameter.
     *
     * @param src the image source.
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void figureGraphics( String src, SinkEventAttributes attributes );

    /**
     * Starting a table element for marking up tabular information in a document.
     */
    void table();

    /**
     * Starts a table.
     * Supported attributes are the {@link SinkEventAttributes base attributes} plus:
     * <blockquote>ALIGN, BGCOLOR, BORDER, CELLPADDING, CELLSPACING,
     * "frame", "rules", "summary", WIDTH.</blockquote>
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void table( SinkEventAttributes attributes );

    /**
     * Ending a table element.
     */
    void table_();

    /**
     * Starting an element contains rows of table data.
     *
     * @param justification the default justification of columns.
     * This can be overridden by individual table rows or table cells.
     * If null a left alignment is assumed by default. If this array
     * has less elements than there are columns in the table then the value of
     * the last array element will be taken as default for the remaining table cells.
     * @param grid true to provide a grid, false otherwise.
     */
    void tableRows( int[] justification, boolean grid );

    /**
     * Ending an element contains rows of table data.
     */
    void tableRows_();

    /**
     * Starting a row element which acts as a container for a row of table cells.
     */
    void tableRow();

    /**
     * Starts a table row.
     * Supported attributes are the {@link SinkEventAttributes base attributes} plus:
     * <blockquote>ALIGN, BGCOLOR, VALIGN.</blockquote>
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void tableRow( SinkEventAttributes attributes );

    /**
     * Ending a row element.
     */
    void tableRow_();

    /**
     * Starting a cell element which defines a cell that contains data.
     */
    void tableCell();

    /**
     * Starting a cell element which defines a cell that contains data.
     *
     * @param width the size of the cell.
     */
    void tableCell( String width );

    /**
     * Starts a table cell.
     * Supported attributes are the {@link SinkEventAttributes base attributes} plus:
     * <blockquote>"abbrv", ALIGN, "axis", BGCOLOR, COLSPAN, "headers",
     * HEIGHT, NOWRAP, ROWSPAN, "scope", VALIGN, WIDTH.</blockquote>
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void tableCell( SinkEventAttributes attributes );

    /**
     * Ending a cell element.
     */
    void tableCell_();

    /**
     * Starting a cell element which defines a cell that contains header information.
     */
    void tableHeaderCell();

    /**
     * Starting a cell element which defines a cell that contains header information.
     *
     * @param width the size of the header cell.
     */
    void tableHeaderCell( String width );

    /**
     * Starts a table header cell.
     * Supported attributes are the {@link SinkEventAttributes base attributes} plus:
     * <blockquote>"abbrv", ALIGN, "axis", BGCOLOR, COLSPAN, "headers",
     * HEIGHT, NOWRAP, ROWSPAN, "scope", VALIGN, WIDTH.</blockquote>
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void tableHeaderCell( SinkEventAttributes attributes );

    /**
     * Ending a cell header element.
     */
    void tableHeaderCell_();

    /**
     * Starting a caption element of a table.
     */
    void tableCaption();

    /**
     * Starts a table caption.
     * Supported attributes are the {@link SinkEventAttributes base attributes} plus ALIGN.
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void tableCaption( SinkEventAttributes attributes );

    /**
     * Ending a caption element of a table.
     */
    void tableCaption_();

    /**
     * Starting an element which represents a paragraph.
     */
    void paragraph();

    /**
     * Starts a paragraph.
     * Supported attributes are the {@link SinkEventAttributes base attributes} plus ALIGN.
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void paragraph( SinkEventAttributes attributes );

    /**
     * Ending a paragraph element.
     */
    void paragraph_();

    /**
     * Starting an element which indicates that whitespace in the enclosed text has semantic relevance.
     *
     * @param boxed true to add a box, false otherwise
     */
    void verbatim( boolean boxed );

    /**
     * Starts a verbatim block, ie a block where whitespace has semantic relevance.
     * Supported attributes are the {@link SinkEventAttributes base attributes} plus: "boxed", ALIGN, WIDTH.
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void verbatim( SinkEventAttributes attributes );

    /**
     * Ending a verbatim element.
     */
    void verbatim_();

    /**
     * Adding a separator of sections from a text to each other.
     */
    void horizontalRule();

    /**
     * Adds a horizontal separator rule.
     * Supported attributes are the {@link SinkEventAttributes base attributes} plus:
     * ALIGN, NOSHADE, SIZE, WIDTH.
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void horizontalRule( SinkEventAttributes attributes );

    /**
     * Adding a new page separator.
     */
    void pageBreak();

    /**
     * Starting an element which defines an anchor.
     *
     * @param name the name of the anchor.
     */
    void anchor( String name );

    /**
     * Starts an element which defines an anchor.
     * Supported attributes are the {@link SinkEventAttributes base attributes}.
     * If NAME is specified in the SinkEventAttributes,
     * it will be overwritten by the name parameter.
     *
     * @param name the name of the anchor.
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void anchor( String name, SinkEventAttributes attributes );
    /**
     * Ending an anchor element.
     */
    void anchor_();

    /**
     * Starting an element which defines a link.
     *
     * @param name the name of the link.
     */
    void link( String name );

    /**
     * Starts a link.
     * Supported attributes are the {@link SinkEventAttributes base attributes} plus:
     * <blockquote>"charset", COORDS, HREF, "hreflang", REL, REV, SHAPE,
     * TARGET, TYPE.</blockquote> If HREF is specified in the
     * SinkEventAttributes, it will be overwritten by the name parameter.
     *
     * @param name the name of the link.
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void link( String name, SinkEventAttributes attributes );
    /**
     * Ending a link element.
     */
    void link_();

    /**
     * Starting an italic element.
     */
    void italic();

    /**
     * Starts an italic element.
     * Supported attributes are the {@link SinkEventAttributes base attributes}.
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void italic( SinkEventAttributes attributes );

    /**
     * Ending an italic element.
     */
    void italic_();

    /**
     * Starting a bold element.
     */
    void bold();

    /**
     * Starts a bold element.
     * Supported attributes are the {@link SinkEventAttributes base attributes}.
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void bold( SinkEventAttributes attributes );

    /**
     * Ending a bold element.
     */
    void bold_();

    /**
     * Starting a monospaced element.
     */
    void monospaced();

    /**
     * Starts a monospaced element.
     * Supported attributes are the {@link SinkEventAttributes base attributes}.
     *
     * @param attributes A set of {@link SinkEventAttributes}.
     */
    void monospaced( SinkEventAttributes attributes );

    /**
     * Ending a monospaced element.
     */
    void monospaced_();

    /**
     * Adds a line break.
     */
    void lineBreak();

    /**
     * Adds a line break.
     * Supported attributes are ID, CLASS, TITLE and STYLE.
     *
     * @param attributes A set of {@link SinkEventAttributes}.
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
     */
    void text( String text );
    
    /**
     * Adds a text. 
     * Supported attributes are the {@link SinkEventAttributes base attributes}
     * plus VALIGN (values "sub", "sup") and DECORATION (values "underline",
     * "overline", "line-through").
     *
     * @param text The text to write.
     * @param attributes A set of {@link SinkEventAttributes}.
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
     */
    void comment( String comment );

    /**
     * Flush the writer or the stream, if needed.
     */
    void flush();

    /**
     * Close the writer or the stream, if needed.
     */
    void close();
}
