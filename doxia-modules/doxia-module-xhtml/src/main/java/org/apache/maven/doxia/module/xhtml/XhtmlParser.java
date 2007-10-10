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
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Parse an xhtml model and emit events into the specified doxia
 * Sink.
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
     * This stack is needed to keep track of the section nesting. Each time
     * a lower section heading is encounted, this stack raises, each time a
     * higher section heading is encountered, this stack lowers.
     */
    private Stack sections = new Stack();

    /**
     * Indicates the last a-tag denoted a link
     */
    private static final String LINK = "link";

    /**
     * Indicates the last a-tag denoted an anchor
     */
    private static final String ANCHOR = "anchor";

    /** {@inheritDoc} */
    protected void handleStartTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        if ( parser.getName().equals( Tag.TITLE.toString() ) )
        {
            sink.title();
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
        else if ( parser.getName().equals( Tag.H1.toString() ) || parser.getName().equals( Tag.H2.toString() ) ||
            parser.getName().equals( Tag.H3.toString() ) || parser.getName().equals( Tag.H4.toString() ) ||
            parser.getName().equals( Tag.H5.toString() ) )
        {
            this.closeSubordinatedSections( parser.getName(), sink );
            this.startSection( this.sections.size(), sink );
            this.startSectionTitle( this.sections.size(), sink );
            this.sections.push( parser.getName() );

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
        else if ( ( parser.getName().equals( Tag.CODE.toString() ) ) || ( parser.getName().equals( Tag.SAMP.toString() ) ) ||
            ( parser.getName().equals( Tag.TT.toString() ) ) )
        {
            sink.monospaced();
        }
        else if ( parser.getName().equals( Tag.UL.toString() ) )
        {
            sink.list();
        }
        else if ( parser.getName().equals( Tag.OL.toString() ) )
        {
            sink.numberedList( Sink.NUMBERING_DECIMAL );
        }
        else if ( parser.getName().equals( Tag.LI.toString() ) )
        {
            sink.listItem();
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
        else if ( parser.getName().equals( Tag.A.toString() ) )
        {
            String href = parser.getAttributeValue( null, Attribute.HREF.toString() );
            String name = parser.getAttributeValue( null, Attribute.NAME.toString() );
            String id = parser.getAttributeValue( null, Attribute.ID.toString() );
            if ( href != null )
            {
                sink.link( href );
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
            sink.figure();
            String src = parser.getAttributeValue( null, Attribute.SRC.toString() );
            String title = parser.getAttributeValue( null, Attribute.TITLE.toString() );
            String alt = parser.getAttributeValue( null, Attribute.ALT.toString() );
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
        }
        else if ( parser.getName().equals( Tag.TR.toString() ) )
        {
            sink.tableRow();
        }
        else if ( parser.getName().equals( Tag.TH.toString() ) )
        {
            sink.tableCell();
        }
        else if ( parser.getName().equals( Tag.TD.toString() ) )
        {
            sink.tableCell();
        }
    }

    /** {@inheritDoc} */
    protected void handleEndTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        if ( parser.getName().equals( Tag.TITLE.toString() ) )
        {
            sink.title_();
        }
        else if ( parser.getName().equals( Tag.ADDRESS.toString() ) )
        {
            sink.author_();
        }
        else if ( parser.getName().equals( Tag.BODY.toString() ) )
        {
            //close all sections that are still open
            closeSubordinatedSections( "h0", sink );
            sink.body_();
        }
        else if ( parser.getName().equals( Tag.H1.toString() ) || parser.getName().equals( Tag.H2.toString() ) ||
            parser.getName().equals( Tag.H3.toString() ) || parser.getName().equals( Tag.H4.toString() ) ||
            parser.getName().equals( Tag.H5.toString() ) )
        {
            this.closeSectionTitle( this.sections.size() - 1, sink );
        }
        else if ( parser.getName().equals( Tag.P.toString() ) )
        {
            sink.paragraph_();
        }
        else if ( parser.getName().equals( Tag.PRE.toString() ) )
        {
            sink.verbatim_();
        }
        else if ( ( parser.getName().equals( Tag.CODE.toString() ) ) || ( parser.getName().equals( Tag.SAMP.toString() ) ) ||
            ( parser.getName().equals( Tag.TT.toString() ) ) )
        {
            sink.monospaced_();
        }
        else if ( parser.getName().equals( Tag.UL.toString() ) )
        {
            sink.list_();
        }
        else if ( parser.getName().equals( Tag.OL.toString() ) )
        {
            sink.numberedList_();
        }
        else if ( parser.getName().equals( Tag.LI.toString() ) )
        {
            sink.listItem_();
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
            sink.table_();
        }
        else if ( parser.getName().equals( Tag.TR.toString() ) )
        {
            sink.tableRow_();
        }
        else if ( parser.getName().equals( Tag.TH.toString() ) )
        {
            sink.tableCell_();
        }
        else if ( parser.getName().equals( Tag.TD.toString() ) )
        {
            sink.tableCell_();
        }
    }

    /** {@inheritDoc} */
    protected void handleText( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = parser.getText();

        if ( !"".equals( text.trim() ) )
        {
            sink.text( text );
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
            sink.comment( text );
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

    private void closeSubordinatedSections( String level, Sink sink )
    {
        if ( this.sections.size() > 0 )
        {
            String heading = (String) this.sections.peek();
            int otherlevel = Integer.parseInt( heading.substring( 1 ) );
            int mylevel = Integer.parseInt( level.substring( 1 ) );
            if ( otherlevel >= mylevel )
            {
                closeSection( this.sections.size(), sink );
                closeSubordinatedSections( level, sink );
            }
        }
    }

    /**
     * Close a section of the specified level.
     *
     * @param level level of the section to close
     * @param sink  the sink to write to
     */
    private void closeSection( int level, Sink sink )
    {
        this.sections.pop();
        switch ( level )
        {
            case 1:
                sink.section1_();
                break;
            case 2:
                sink.section2_();
                break;
            case 3:
                sink.section3_();
                break;
            case 4:
                sink.section4_();
                break;
            case 5:
                sink.section5_();
                break;
        }
    }

    /**
     * Starts a new section of the specified level
     *
     * @param level level of the new section
     * @param sink  the sink to write to
     */
    private void startSection( int level, Sink sink )
    {
        switch ( level )
        {
            case 0:
                sink.section1();
                break;
            case 1:
                sink.section2();
                break;
            case 2:
                sink.section3();
                break;
            case 3:
                sink.section4();
                break;
            case 4:
                sink.section5();
                break;
        }
    }

    /**
     * Closes the title of a section
     *
     * @param level level of the section
     * @param sink  the sink to write to
     */
    private void closeSectionTitle( int level, Sink sink )
    {
        switch ( level )
        {
            case 0:
                sink.sectionTitle1_();
                break;
            case 1:
                sink.sectionTitle2_();
                break;
            case 2:
                sink.sectionTitle3_();
                break;
            case 3:
                sink.sectionTitle4_();
                break;
            case 4:
                sink.sectionTitle5_();
                break;
        }
    }

    /**
     * Starts the title of a new section
     *
     * @param level level of the new section
     * @param sink  the sink to write to
     */
    private void startSectionTitle( int level, Sink sink )
    {
        switch ( level )
        {
            case 0:
                sink.sectionTitle1();
                break;
            case 1:
                sink.sectionTitle2();
                break;
            case 2:
                sink.sectionTitle3();
                break;
            case 3:
                sink.sectionTitle4();
                break;
            case 4:
                sink.sectionTitle5();
                break;
        }
    }
}
