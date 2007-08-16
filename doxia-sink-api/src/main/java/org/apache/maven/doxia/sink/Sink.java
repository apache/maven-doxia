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
 * @version $Id$
 */
public interface Sink
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
     * Ending the head element.
     */
    void head_();

    /**
     * Starting the title element which is used to identify the document.
     */
    void title();

    /**
     * Ending the title element.
     */
    void title_();

    /**
     * Starting the author element which is used to identify the author of the document.
     */
    void author();

    /**
     * Ending the author element.
     */
    void author_();

    /**
     * Starting the date element which is used to identify the date of the document.
     */
    void date();

    /**
     * Ending the date element.
     */
    void date_();

    /**
     * Starting the body of a document which contains the document's content.
     */
    void body();

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
     * Starting an unordered list element.
     */
    void list();

    /**
     * Ending an unordered list element.
     */
    void list_();

    /**
     * Starting a list item element within an unordered list.
     */
    void listItem();

    /**
     * Ending a list item element within an unordered list.
     */
    void listItem_();

    /**
     * Starting an ordered list element.
     *
     * @param numbering the numbering wanted
     * @see #NUMBERING_DECIMAL
     * @see #NUMBERING_LOWER_ALPHA
     * @see #NUMBERING_LOWER_ROMAN
     * @see #NUMBERING_UPPER_ALPHA
     * @see #NUMBERING_UPPER_ROMAN
     */
    void numberedList( int numbering );

    /**
     * Ending an ordered list element.
     */
    void numberedList_();

    /**
     * Starting a list item element within an ordered list.
     */
    void numberedListItem();

    /**
     * Ending a list item element within an ordered list.
     */
    void numberedListItem_();

    /**
     * Starting a definition list element.
     */
    void definitionList();

    /**
     * Ending a definition list element.
     */
    void definitionList_();

    /**
     * Starting a list item element within a definition list.
     */
    void definitionListItem();

    /**
     * Ending a list item element within a definition list.
     */
    void definitionListItem_();

    /**
     * Starting a definition element within a definition list.
     */
    void definition();

    /**
     * Ending a definition element within a definition list.
     */
    void definition_();

    /**
     * Starting a definition term element within a definition list.
     */
    void definedTerm();

    /**
     * Starting a definition term element within a definition list.
     */
    void definedTerm_();

    /**
     * Starting a basic image embedding element.
     */
    void figure();

    /**
     * Ending a basic image embedding element.
     */
    void figure_();

    /**
     * Starting a caption of an image element.
     */
    void figureCaption();

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
     * Starting a table element for marking up tabular information in a document.
     */
    void table();

    /**
     * Ending a table element.
     */
    void table_();

    /**
     * Starting an element contains rows of table data.
     *
     * @param justification the justification wanted
     * @param grid true to provide a grid, false otherwise
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
     * @param width the size of the cell
     */
    void tableCell( String width );

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
     * @param width the size of the header cell
     */
    void tableHeaderCell( String width );

    /**
     * Ending a cell header element.
     */
    void tableHeaderCell_();

    /**
     * Starting a caption element of a table.
     */
    void tableCaption();

    /**
     * Ending a caption element of a table.
     */
    void tableCaption_();

    /**
     * Starting an element which represents a paragraph.
     */
    void paragraph();

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
     * Ending a verbatim element.
     */
    void verbatim_();

    /**
     * Adding a separator of sections from a text to each other.
     */
    void horizontalRule();

    /**
     * Adding a new page separator.
     */
    void pageBreak();

    /**
     * Starting an element which defines an anchor.
     *
     * @param name the name of the anchor
     */
    void anchor( String name );

    /**
     * Ending an anchor element.
     */
    void anchor_();

    /**
     * Starting an element which defines a link.
     *
     * @param name the name of the link
     */
    void link( String name );

    /**
     * Ending a link element.
     */
    void link_();

    /**
     * Starting an italic element.
     */
    void italic();

    /**
     * Ending an italic element.
     */
    void italic_();

    /**
     * Starting a bold element.
     */
    void bold();

    /**
     * Ending a bold element.
     */
    void bold_();

    /**
     * Starting a monospaced element.
     */
    void monospaced();

    /**
     * Ending a monospaced element.
     */
    void monospaced_();

    /**
     * Adding a line breaks.
     */
    void lineBreak();

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
     * Adding a raw text, <i>ie</i> a text without any special formatting operations.
     *
     * @param text The text to write.
     */
    void rawText( String text );

    /**
     * Flush the writer or the stream, if needed.
     */
    void flush();

    /**
     * Close the writer or the stream, if needed.
     */
    void close();
}
