package org.apache.maven.doxia.module.xhtml;

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

import java.util.Stack;

import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.parser.AbstractXmlParser;
import org.apache.maven.doxia.sink.Sink;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Parse an xhtml model and emit events into the specified doxia Sink.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @since 1.0
 * @plexus.component role="org.apache.maven.doxia.parser.Parser" role-hint="xhtml"
 */
public class XhtmlParser
    extends AbstractXmlParser
    implements XhtmlMarkup
{
    /**
     * This stack is needed to keep track of the different link and anchor-types
     * which utilize the same element
     */
    private Stack linktypes = new Stack();

    /**
     * Indicates the last a-tag denoted a link
     */
    private static final String LINK = "link";

    /**
     * Indicates the last a-tag denoted an anchor
     */
    private static final String ANCHOR = "anchor";

    /** Used for nested lists. */
    private int orderedListDepth = 0;

    /** For tables. */
    private boolean hasCaption;

    /** Counts section level. */
    private int sectionLevel;

    /** {@inheritDoc} */
    protected void handleStartTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        if ( parser.getName().equals( Tag.HTML.toString() ) )
        {
            //Do nothing
            return;
        }
        else if ( parser.getName().equals( Tag.TITLE.toString() ) )
        {
            sink.title();
        }
        else if ( parser.getName().equals( Tag.META.toString() ) )
        {
            String name = parser.getAttributeValue( null, Attribute.NAME.toString() );

            String content = parser.getAttributeValue( null, Attribute.CONTENT.toString() );

            if ( "author".equals( name ) )
            {
                sink.author();

                sink.text( content );

                sink.author_();
            }
            else if ( "date".equals( name ) )
            {
                sink.date();

                sink.text( content );

                sink.date_();
            }
        }
        /*
         * The ADDRESS element may be used by authors to supply contact information
         * for a model or a major part of a model such as a form. This element
         *  often appears at the beginning or end of a model.
         */
        else if ( parser.getName().equals( Tag.ADDRESS.toString() ) )
        {
            sink.author();
        }
        else if ( parser.getName().equals( Tag.BODY.toString() ) )
        {
            sink.body();
        }
        else if ( parser.getName().equals( Tag.H2.toString() ) )
        {
            closeOpenSections( Sink.SECTION_LEVEL_1, sink );

            sink.section1();

            sink.sectionTitle1();
        }
        else if ( parser.getName().equals( Tag.H3.toString() ) )
        {
            closeOpenSections( Sink.SECTION_LEVEL_2, sink );

            sink.section2();

            sink.sectionTitle2();
        }
        else if ( parser.getName().equals( Tag.H4.toString() ) )
        {
            closeOpenSections( Sink.SECTION_LEVEL_3, sink );

            sink.section3();

            sink.sectionTitle3();
        }
        else if ( parser.getName().equals( Tag.H5.toString() ) )
        {
            closeOpenSections( Sink.SECTION_LEVEL_4, sink );

            sink.section4();

            sink.sectionTitle4();
        }
        else if ( parser.getName().equals( Tag.H6.toString() ) )
        {
            closeOpenSections( Sink.SECTION_LEVEL_5, sink );

            sink.section5();

            sink.sectionTitle5();
        }
        else if ( parser.getName().equals( Tag.P.toString() ) )
        {
            sink.paragraph();
        }
        /*
         * The PRE element tells visual user agents that the enclosed text is
         * "preformatted". When handling preformatted text, visual user agents:
         * - May leave white space intact.
         * - May render text with a fixed-pitch font.
         * - May disable automatic word wrap.
         * - Must not disable bidirectional processing.
         * Non-visual user agents are not required to respect extra white space
         * in the content of a PRE element.
         */
        else if ( parser.getName().equals( Tag.PRE.toString() ) )
        {
            sink.verbatim( true );
        }
        else if ( parser.getName().equals( Tag.UL.toString() ) )
        {
            sink.list();
        }
        else if ( parser.getName().equals( Tag.OL.toString() ) )
        {
            int numbering = Sink.NUMBERING_DECIMAL;

            // this will have to be generalized if we handle styles
            String style = parser.getAttributeValue( null, Attribute.STYLE.toString() );

            if ( style != null )
            {
                if ( "list-style-type: upper-alpha".equals( style ) )
                {
                    numbering = Sink.NUMBERING_UPPER_ALPHA;
                }
                else if ( "list-style-type: lower-alpha".equals( style ) )
                {
                    numbering = Sink.NUMBERING_LOWER_ALPHA;
                }
                else if ( "list-style-type: upper-roman".equals( style ) )
                {
                    numbering = Sink.NUMBERING_UPPER_ROMAN;
                }
                else if ( "list-style-type: lower-roman".equals( style ) )
                {
                    numbering = Sink.NUMBERING_LOWER_ROMAN;
                }
                else if ( "list-style-type: decimal".equals( style ) )
                {
                    numbering = Sink.NUMBERING_DECIMAL;
                }
            }

            sink.numberedList( numbering );
            orderedListDepth++;
        }
        else if ( parser.getName().equals( Tag.LI.toString() ) )
        {
            if ( orderedListDepth == 0 )
            {
                sink.listItem();
            }
            else
            {
                sink.numberedListItem();
            }
        }
        else if ( parser.getName().equals( Tag.DL.toString() ) )
        {
            sink.definitionList();
        }
        else if ( parser.getName().equals( Tag.DT.toString() ) )
        {
            sink.definitionListItem();
            sink.definedTerm();
        }
        else if ( parser.getName().equals( Tag.DD.toString() ) )
        {
            sink.definition();
        }
        else if ( parser.getName().equals( Tag.HEAD.toString() ) )
        {
            sink.head();
        }
        else if ( ( parser.getName().equals( Tag.B.toString() ) ) || ( parser.getName().equals( Tag.STRONG.toString() ) ) )
        {
            sink.bold();
        }
        else if ( ( parser.getName().equals( Tag.I.toString() ) ) || ( parser.getName().equals( Tag.EM.toString() ) ) )
        {
            sink.italic();
        }
        else if ( ( parser.getName().equals( Tag.CODE.toString() ) ) || ( parser.getName().equals( Tag.SAMP.toString() ) ) ||
            ( parser.getName().equals( Tag.TT.toString() ) ) )
        {
            sink.monospaced();
        }
        else if ( parser.getName().equals( Tag.A.toString() ) )
        {
            String href = parser.getAttributeValue( null, Attribute.HREF.toString() );
            String name = parser.getAttributeValue( null, Attribute.NAME.toString() );
            String id = parser.getAttributeValue( null, Attribute.ID.toString() );
            if ( href != null )
            {
                String link = href;

                if ( link.startsWith( "#" ) )
                {
                    link = link.substring( 1 );
                }

                sink.link( link );
                this.linktypes.push( XhtmlParser.LINK );
            }
            else if ( name != null )
            {
                sink.anchor( name );
                this.linktypes.push( XhtmlParser.ANCHOR );
            }
            else if ( id != null )
            {
                sink.anchor( id );
                this.linktypes.push( XhtmlParser.ANCHOR );
            }
        }
        else if ( parser.getName().equals( Tag.BR.toString() ) )
        {
            sink.lineBreak();
        }
        else if ( parser.getName().equals( Tag.HR.toString() ) )
        {
            sink.horizontalRule();
        }
        else if ( parser.getName().equals( Tag.IMG.toString() ) )
        {
            String src = parser.getAttributeValue( null, Attribute.SRC.toString() );
            String title = parser.getAttributeValue( null, Attribute.TITLE.toString() );
            String alt = parser.getAttributeValue( null, Attribute.ALT.toString() );

            sink.figure();

            if ( src != null )
            {
                sink.figureGraphics( src );
            }

            if ( title != null )
            {
                sink.figureCaption();
                sink.text( title );
                sink.figureCaption_();
            }
            else if ( alt != null )
            {
                sink.figureCaption();
                sink.text( alt );
                sink.figureCaption_();
            }

            sink.figure_();
        }
        // ----------------------------------------------------------------------
        // Tables
        // ----------------------------------------------------------------------

        else if ( parser.getName().equals( Tag.TABLE.toString() ) )
        {
            sink.table();

            String border = parser.getAttributeValue( null, Attribute.BORDER.toString() );

            boolean grid = true;

            if ( "0".equals( border ) )
            {
                grid = false;
            }

            String align = parser.getAttributeValue( null, Attribute.ALIGN.toString() );

            int[] justif = { JUSTIFY_CENTER };

            if ( "left".equals( align ) )
            {
                justif[0] = JUSTIFY_LEFT;
            }
            else if ( "right".equals( align ) )
            {
                justif[0] = JUSTIFY_RIGHT;
            }

            sink.tableRows( justif, grid );
        }
        else if ( parser.getName().equals( Tag.TR.toString() ) )
        {
            sink.tableRow();
        }
        else if ( parser.getName().equals( Tag.TH.toString() ) )
        {
            String colspan = parser.getAttributeValue( null, Attribute.COLSPAN.toString() );
            if ( colspan ==  null )
            {
                sink.tableHeaderCell();
            }
            else
            {
                sink.tableHeaderCell( colspan );
            }
        }
        else if ( parser.getName().equals( Tag.TD.toString() ) )
        {
            String colspan = parser.getAttributeValue( null, Attribute.COLSPAN.toString() );
            if ( colspan ==  null )
            {
                sink.tableCell();
            }
            else
            {
                sink.tableCell( colspan );
            }
        }
        else if ( parser.getName().equals( Tag.CAPTION.toString() ) )
        {
            sink.tableRows_();
            this.hasCaption = true;
            sink.tableCaption();
        }
    }

    /** {@inheritDoc} */
    protected void handleEndTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        if ( parser.getName().equals( Tag.HTML.toString() ) )
        {
            //Do nothing
            return;
        }
        else if ( parser.getName().equals( Tag.TITLE.toString() ) )
        {
            sink.title_();
        }
        else if ( parser.getName().equals( Tag.ADDRESS.toString() ) )
        {
            sink.author_();
        }
        else if ( parser.getName().equals( Tag.BODY.toString() ) )
        {
            closeOpenSections( 0, sink );

            sink.body_();
        }
        else if ( parser.getName().equals( Tag.P.toString() ) )
        {
            sink.paragraph_();
        }
        else if ( parser.getName().equals( Tag.PRE.toString() ) )
        {
            sink.verbatim_();
        }
        else if ( parser.getName().equals( Tag.UL.toString() ) )
        {
            sink.list_();
        }
        else if ( parser.getName().equals( Tag.OL.toString() ) )
        {
            sink.numberedList_();
            orderedListDepth--;
        }
        else if ( parser.getName().equals( Tag.LI.toString() ) )
        {
            if ( orderedListDepth == 0 )
            {
                sink.listItem_();
            }
            else
            {
                sink.numberedListItem_();
            }
        }
        else if ( parser.getName().equals( Tag.DL.toString() ) )
        {
            sink.definitionList_();
        }
        else if ( parser.getName().equals( Tag.DT.toString() ) )
        {
            sink.definedTerm_();
        }
        else if ( parser.getName().equals( Tag.DD.toString() ) )
        {
            sink.definition_();
            sink.definitionListItem_();
        }
        else if ( parser.getName().equals( Tag.HEAD.toString() ) )
        {
            sink.head_();
        }
        else if ( ( parser.getName().equals( Tag.B.toString() ) ) || ( parser.getName().equals( Tag.STRONG.toString() ) ) )
        {
            sink.bold_();
        }
        else if ( ( parser.getName().equals( Tag.I.toString() ) ) || ( parser.getName().equals( Tag.EM.toString() ) ) )
        {
            sink.italic_();
        }
        else if ( ( parser.getName().equals( Tag.CODE.toString() ) ) || ( parser.getName().equals( Tag.SAMP.toString() ) ) ||
            ( parser.getName().equals( Tag.TT.toString() ) ) )
        {
            sink.monospaced_();
        }
        else if ( parser.getName().equals( Tag.A.toString() ) )
        {
            String linktype = (String) this.linktypes.pop();
            //the equals operation is ok here, because we always use the class constant
            if ( linktype == XhtmlParser.LINK )
            {
                sink.link_();
            }
            else
            {
                sink.anchor_();
            }
        }
        // ----------------------------------------------------------------------
        // Tables
        // ----------------------------------------------------------------------

        else if ( parser.getName().equals( Tag.TABLE.toString() ) )
        {
            if ( !hasCaption )
            {
                sink.tableRows_();
            }

            this.hasCaption = false;

            sink.table_();
        }
        else if ( parser.getName().equals( Tag.TR.toString() ) )
        {
            sink.tableRow_();
        }
        else if ( parser.getName().equals( Tag.TH.toString() ) )
        {
            sink.tableHeaderCell_();
        }
        else if ( parser.getName().equals( Tag.TD.toString() ) )
        {
            sink.tableCell_();
        }
        else if ( parser.getName().equals( Tag.CAPTION.toString() ) )
        {
            sink.tableCaption_();
        }
        else if ( parser.getName().equals( Tag.H2.toString() ) )
        {
            sink.sectionTitle1_();
        }
        else if ( parser.getName().equals( Tag.H3.toString() ) )
        {
            sink.sectionTitle2_();
        }
        else if ( parser.getName().equals( Tag.H4.toString() ) )
        {
            sink.sectionTitle3_();
        }
        else if ( parser.getName().equals( Tag.H5.toString() ) )
        {
            sink.sectionTitle4_();
        }
        else if ( parser.getName().equals( Tag.H6.toString() ) )
        {
            sink.sectionTitle5_();
        }
    }

    /** {@inheritDoc} */
    protected void handleText( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = parser.getText();

        if ( StringUtils.isNotEmpty( text.trim() ) )
        {
            // emit separate text events for different lines
            String[] lines = StringUtils.split( text, EOL );

            for ( int i = 0; i < lines.length; i++ )
            {
                sink.text( lines[i] );
            }
        }
    }

    /** {@inheritDoc} */
    protected void handleCdsect( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = parser.getText();

        sink.rawText( text );
    }

    /** {@inheritDoc} */
    protected void handleComment( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = parser.getText();

        if ( "PB".equals( text.trim() ) )
        {
            sink.pageBreak();
        }
        else
        {
            sink.comment( text.trim() );
        }
    }

    /** {@inheritDoc} */
    protected void handleEntity( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = parser.getText();

        int[] holder = new int[] {0, 0};
        char[] chars = parser.getTextCharacters( holder );
        String textChars = String.valueOf( chars, holder[0], holder[1] );

        if ( "#160".equals( textChars ) )
        {
            sink.nonBreakingSpace();
        }
        else
        {
            sink.text( text );
        }
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * Close open sections. The current level is set to newLevel afterwards.
     *
     * @param newLevel the new section level, all upper levels have to be closed.
     * @param sink the sink to receive the events.
     */
    private void closeOpenSections( int newLevel, Sink sink )
    {
        while ( this.sectionLevel >= newLevel )
        {
            if ( sectionLevel == Sink.SECTION_LEVEL_5)
            {
                sink.section5_();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_4)
            {
                sink.section4_();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_3)
            {
                sink.section3_();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_2)
            {
                sink.section2_();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_1)
            {
                sink.section1_();
            }

            this.sectionLevel--;
        }

        this.sectionLevel = newLevel;
    }

}
