/*
 * Copyright (c) 1999,2000-01 Pixware. 
 *
 * Author: Hussein Shafie
 *
 * This file is part of the Pixware APT tools.
 * For conditions of distribution and use, see the accompanying legal.txt file.
 */
package org.codehaus.doxia.sink;

public interface Sink
{
    String ROLE = Sink.class.getName();

    static final int NUMBERING_DECIMAL = 0;
    static final int NUMBERING_LOWER_ALPHA = 1;
    static final int NUMBERING_UPPER_ALPHA = 2;
    static final int NUMBERING_LOWER_ROMAN = 3;
    static final int NUMBERING_UPPER_ROMAN = 4;

    void head();

    void head_();

    void body();

    void body_();

    void section1();

    void section1_();

    void section2();

    void section2_();

    void section3();

    void section3_();

    void section4();

    void section4_();

    void section5();

    void section5_();

    void list();

    void list_();

    void listItem();

    void listItem_();

    void numberedList( int numbering );

    void numberedList_();

    void numberedListItem();

    void numberedListItem_();

    void definitionList();

    void definitionList_();

    void definitionListItem();

    void definitionListItem_();

    void definition();

    void definition_();

    void figure();

    void figure_();

    void table();

    void table_();

    void tableRows( int[] justification, boolean grid );

    void tableRows_();

    void tableRow();

    void tableRow_();

    void title();

    void title_();

    void author();

    void author_();

    void date();

    void date_();

    void sectionTitle();

    void sectionTitle_();

    void sectionTitle1();

    void sectionTitle1_();

    void sectionTitle2();

    void sectionTitle2_();

    void sectionTitle3();

    void sectionTitle3_();

    void sectionTitle4();

    void sectionTitle4_();

    void sectionTitle5();

    void sectionTitle5_();

    void paragraph();

    void paragraph_();

    void verbatim( boolean boxed );

    void verbatim_();

    void definedTerm();

    void definedTerm_();

    void figureCaption();

    void figureCaption_();

    void tableCell();
    
    void tableCell(String width);

    void tableCell_();

    void tableHeaderCell();
    
    void tableHeaderCell(String width);

    void tableHeaderCell_();

    void tableCaption();

    void tableCaption_();

    void figureGraphics( String name );

    void horizontalRule();

    void pageBreak();

    void anchor( String name );

    void anchor_();

    void link( String name );

    void link_();

    void italic();

    void italic_();

    void bold();

    void bold_();

    void monospaced();

    void monospaced_();

    void lineBreak();

    void nonBreakingSpace();

    void text( String text );

    void rawText( String text );

    void flush();

    void close();
}

