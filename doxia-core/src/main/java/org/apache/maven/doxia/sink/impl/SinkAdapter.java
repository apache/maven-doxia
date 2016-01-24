package org.apache.maven.doxia.sink.impl;

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

import javax.swing.text.MutableAttributeSet;

import org.apache.maven.doxia.sink.SinkEventAttributes;

/**
 * Empty implementation of the <code>Sink</code> interface. Useful for testing purposes.
 *
 * @since 1.0
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class SinkAdapter
    extends AbstractSink
{
    @Override
    public void head()
    {
        // nop
    }

    @Override
    public void head_()
    {
        // nop
    }

    @Override
    public void body()
    {
        // nop
    }

    @Override
    public void body_()
    {
        // nop
    }

    @Override
    public void section1()
    {
        // nop
    }

    @Override
    public void section1_()
    {
        // nop
    }

    @Override
    public void section2()
    {
        // nop
    }

    @Override
    public void section2_()
    {
        // nop
    }

    @Override
    public void section3()
    {
        // nop
    }

    @Override
    public void section3_()
    {
        // nop
    }

    @Override
    public void section4()
    {
        // nop
    }

    @Override
    public void section4_()
    {
        // nop
    }

    @Override
    public void section5()
    {
        // nop
    }

    @Override
    public void section5_()
    {
        // nop
    }
    
    @Override
    public void section6()
    {
        // nop
    }
    
    @Override
    public void section6_()
    {
        // nop
    }

    @Override
    public void list()
    {
        // nop
    }

    @Override
    public void list_()
    {
        // nop
    }

    @Override
    public void listItem()
    {
        // nop
    }

    @Override
    public void listItem_()
    {
        // nop
    }

    @Override
    public void numberedList( int numbering )
    {
        // nop
    }

    @Override
    public void numberedList_()
    {
        // nop
    }

    @Override
    public void numberedListItem()
    {
        // nop
    }

    @Override
    public void numberedListItem_()
    {
        // nop
    }

    @Override
    public void definitionList()
    {
        // nop
    }

    @Override
    public void definitionList_()
    {
        // nop
    }

    @Override
    public void definitionListItem()
    {
        // nop
    }

    @Override
    public void definitionListItem_()
    {
        // nop
    }

    @Override
    public void definition()
    {
        // nop
    }

    @Override
    public void definition_()
    {
        // nop
    }

    @Override
    public void figure()
    {
        // nop
    }

    @Override
    public void figure_()
    {
        // nop
    }

    @Override
    public void table()
    {
        // nop
    }

    @Override
    public void table_()
    {
        // nop
    }

    @Override
    public void tableRows( int[] justification, boolean grid )
    {
        // nop
    }

    @Override
    public void tableRows_()
    {
        // nop
    }

    @Override
    public void tableRow()
    {
        // nop
    }

    @Override
    public void tableRow_()
    {
        // nop
    }

    @Override
    public void title()
    {
        // nop
    }

    @Override
    public void title_()
    {
        // nop
    }

    @Override
    public void author()
    {
        // nop
    }

    @Override
    public void author_()
    {
        // nop
    }

    @Override
    public void date()
    {
        // nop
    }

    @Override
    public void date_()
    {
        // nop
    }

    @Override
    public void sectionTitle()
    {
        // nop
    }

    @Override
    public void sectionTitle_()
    {
        // nop
    }

    @Override
    public void sectionTitle1()
    {
        // nop
    }

    @Override
    public void sectionTitle1_()
    {
        // nop
    }

    @Override
    public void sectionTitle2()
    {
        // nop
    }

    @Override
    public void sectionTitle2_()
    {
        // nop
    }

    @Override
    public void sectionTitle3()
    {
        // nop
    }

    @Override
    public void sectionTitle3_()
    {
        // nop
    }

    @Override
    public void sectionTitle4()
    {
        // nop
    }

    @Override
    public void sectionTitle4_()
    {
        // nop
    }

    @Override
    public void sectionTitle5()
    {
        // nop
    }

    @Override
    public void sectionTitle5_()
    {
        // nop
    }

    @Override
    public void sectionTitle6()
    {
        // nop
    }

    @Override
    public void sectionTitle6_()
    {
        // nop
    }

    @Override
    public void paragraph()
    {
        // nop
    }

    @Override
    public void paragraph_()
    {
        // nop
    }

    @Override
    public void verbatim( boolean boxed )
    {
        // nop
    }

    @Override
    public void verbatim_()
    {
        // nop
    }

    @Override
    public void definedTerm()
    {
        // nop
    }

    @Override
    public void definedTerm_()
    {
        // nop
    }

    @Override
    public void figureCaption()
    {
        // nop
    }

    @Override
    public void figureCaption_()
    {
        // nop
    }

    @Override
    public void tableCell()
    {
        // nop
    }

    @Override
    public void tableCell( String width )
    {
        // nop
    }

    @Override
    public void tableCell_()
    {
        // nop
    }

    @Override
    public void tableHeaderCell()
    {
        // nop
    }

    @Override
    public void tableHeaderCell( String width )
    {
        // nop
    }

    @Override
    public void tableHeaderCell_()
    {
        // nop
    }

    @Override
    public void tableCaption()
    {
        // nop
    }

    @Override
    public void tableCaption_()
    {
        // nop
    }

    @Override
    public void figureGraphics( String name )
    {
        // nop
    }

    @Override
    public void horizontalRule()
    {
        // nop
    }

    @Override
    public void pageBreak()
    {
        // nop
    }

    @Override
    public void anchor( String name )
    {
        // nop
    }

    @Override
    public void anchor_()
    {
        // nop
    }

    @Override
    public void link( String name )
    {
        // nop
    }

    @Override
    public void link_()
    {
        // nop
    }

    @Override
    public void italic()
    {
        // nop
    }

    @Override
    public void italic_()
    {
        // nop
    }

    @Override
    public void bold()
    {
        // nop
    }

    @Override
    public void bold_()
    {
        // nop
    }

    @Override
    public void monospaced()
    {
        // nop
    }

    @Override
    public void monospaced_()
    {
        // nop
    }

    @Override
    public void lineBreak()
    {
        // nop
    }

    @Override
    public void nonBreakingSpace()
    {
        // nop
    }

    @Override
    public void text( String text )
    {
        // nop
    }

    @Override
    public void rawText( String text )
    {
        // nop
    }

    @Override
    public void comment( String comment )
    {
        // nop
    }

    @Override
    public void flush()
    {
        // nop
    }

    @Override
    public void close()
    {
        // nop
    }
    @Override
    public void head( SinkEventAttributes attributes )
    {
        head();
    }

    @Override
    public void title( SinkEventAttributes attributes )
    {
        title();
    }

    @Override
    public void author( SinkEventAttributes attributes )
    {
        author();
    }

    @Override
    public void date( SinkEventAttributes attributes )
    {
        date();
    }

    @Override
    public void body( SinkEventAttributes attributes )
    {
        body();
    }

    @Override
    public void section( int level, SinkEventAttributes attributes )
    {
        if ( level == SECTION_LEVEL_1 )
        {
            section1();
        }
        else if ( level == SECTION_LEVEL_2 )
        {
            section2();
        }
        else if ( level == SECTION_LEVEL_3 )
        {
            section3();
        }
        else if ( level == SECTION_LEVEL_4 )
        {
            section4();
        }
        else if ( level == SECTION_LEVEL_5 )
        {
            section5();
        }
    }

    @Override
    public void section_( int level )
    {
        if ( level == SECTION_LEVEL_1 )
        {
            section1_();
        }
        else if ( level == SECTION_LEVEL_2 )
        {
            section2_();
        }
        else if ( level == SECTION_LEVEL_3 )
        {
            section3_();
        }
        else if ( level == SECTION_LEVEL_4 )
        {
            section4_();
        }
        else if ( level == SECTION_LEVEL_5 )
        {
            section5_();
        }
    }

    @Override
    public void sectionTitle( int level, SinkEventAttributes attributes )
    {
        if ( level == SECTION_LEVEL_1 )
        {
            sectionTitle1();
        }
        else if ( level == SECTION_LEVEL_2 )
        {
            sectionTitle2();
        }
        else if ( level == SECTION_LEVEL_3 )
        {
            sectionTitle3();
        }
        else if ( level == SECTION_LEVEL_4 )
        {
            sectionTitle4();
        }
        else if ( level == SECTION_LEVEL_5 )
        {
            sectionTitle5();
        }
    }

    @Override
    public void sectionTitle_( int level )
    {
        if ( level == SECTION_LEVEL_1 )
        {
            sectionTitle1_();
        }
        else if ( level == SECTION_LEVEL_2 )
        {
            sectionTitle2_();
        }
        else if ( level == SECTION_LEVEL_3 )
        {
            sectionTitle3_();
        }
        else if ( level == SECTION_LEVEL_4 )
        {
            sectionTitle4_();
        }
        else if ( level == SECTION_LEVEL_5 )
        {
            sectionTitle5_();
        }
    }

    @Override
    public void list( SinkEventAttributes attributes )
    {
        list();
    }

    @Override
    public void listItem( SinkEventAttributes attributes )
    {
        listItem();
    }

    @Override
    public void numberedList( int numbering, SinkEventAttributes attributes )
    {
        numberedList( numbering );
    }

    @Override
    public void numberedListItem( SinkEventAttributes attributes )
    {
        numberedListItem();
    }

    @Override
    public void definitionList( SinkEventAttributes attributes )
    {
        definitionList();
    }

    @Override
    public void definitionListItem( SinkEventAttributes attributes )
    {
        definitionListItem();
    }

    @Override
    public void definition( SinkEventAttributes attributes )
    {
        definition();
    }

    @Override
    public void definedTerm( SinkEventAttributes attributes )
    {
        definedTerm();
    }

    @Override
    public void figure( SinkEventAttributes attributes )
    {
        figure();
    }

    @Override
    public void figureCaption( SinkEventAttributes attributes )
    {
        figureCaption();
    }

    @Override
    public void figureGraphics( String src, SinkEventAttributes attributes )
    {
        figureGraphics( src );
    }

    @Override
    public void table( SinkEventAttributes attributes )
    {
        table();
    }

    @Override
    public void tableRow( SinkEventAttributes attributes )
    {
        tableRow();
    }

    @Override
    public void tableCell( SinkEventAttributes attributes )
    {
        tableCell();
    }

    @Override
    public void tableHeaderCell( SinkEventAttributes attributes )
    {
        tableHeaderCell();
    }

    @Override
    public void tableCaption( SinkEventAttributes attributes )
    {
        tableCaption();
    }

    @Override
    public void paragraph( SinkEventAttributes attributes )
    {
        paragraph();
    }

    @Override
    public void verbatim( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes( attributes, SinkUtils.SINK_VERBATIM_ATTRIBUTES );

        boolean boxed = false;

        if ( atts != null && atts.isDefined( SinkEventAttributes.DECORATION ) )
        {
            boxed = "boxed".equals( atts.getAttribute( SinkEventAttributes.DECORATION ).toString() );
        }

        verbatim( boxed );
    }

    @Override
    public void horizontalRule( SinkEventAttributes attributes )
    {
        horizontalRule();
    }

    @Override
    public void anchor( String name, SinkEventAttributes attributes )
    {
        anchor( name );
    }

    @Override
    public void link( String name, SinkEventAttributes attributes )
    {
        link( name );
    }

    @Override
    public void lineBreak( SinkEventAttributes attributes )
    {
        lineBreak();
    }

    @Override
    public void text( String text, SinkEventAttributes attributes )
    {
        text( text );
    }

    @Override
    public void unknown( String name, Object[] requiredParams, SinkEventAttributes attributes )
    {
        // nop
    }
}
