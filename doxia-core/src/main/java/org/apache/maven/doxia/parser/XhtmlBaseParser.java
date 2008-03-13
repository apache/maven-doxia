package org.apache.maven.doxia.parser;

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

import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributeSet;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Common base parser for xhtml events.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author ltheussl
 * @version $Id$
 * @since 1.0
 */
public class XhtmlBaseParser
    extends AbstractXmlParser
{
    /** Used to distinguish &lt;a href=""&gt; from &lt;a name=""&gt;. */
    private boolean isLink;

    /** Used to distinguish &lt;a href=""&gt; from &lt;a name=""&gt;. */
    private boolean isAnchor;

    /** Used for nested lists. */
    private int orderedListDepth = 0;

    /** For tables. */
    private boolean hasCaption;

    /** Counts section level. */
    private int sectionLevel;

    /** Verbatim level, increased whenever a &lt;pre&gt; tag is encountered. */
    private int verbatimLevel;

    /** Used to recognize the case of img inside figure. */
    private boolean inFigure;

    /** Decoration properties, eg for texts. */
    private String decoration;

    /**
     * <p>
     *   Goes through a common list of possible html start tags. These include only tags that can go into
     *   the body of a xhtml document and so should be re-usable by different xhtml-based parsers.
     * </p>
     * <p>
     *   The currently handled tags are:
     * </p>
     * <p>
     *   <code>
     *      &lt;h2&gt;, &lt;h3&gt;, &lt;h4&gt;, &lt;h5&gt;, &lt;h6&gt;, &lt;p&gt;, &lt;pre&gt;,
     *      &lt;ul&gt;, &lt;ol&gt;, &lt;li&gt;, &lt;dl&gt;, &lt;dt&gt;, &lt;dd&gt;, &lt;b&gt;, &lt;strong&gt;,
     *      &lt;i&gt;, &lt;em&gt;, &lt;code&gt;, &lt;samp&gt;, &lt;tt&gt;, &lt;a&gt;, &lt;table&gt;, &lt;tr&gt;,
     *      &lt;th&gt;, &lt;td&gt;, &lt;caption&gt;, &lt;br/&gt;, &lt;hr/&gt;, &lt;img/&gt;.
     *   </code>
     * </p>
     *
     * @param parser A parser.
     * @param sink the sink to receive the events.
     * @return True if the event has been handled by this method, i.e. the tag was recognized, false otherwise.
     */
    protected boolean baseStartTag( XmlPullParser parser, Sink sink )
    {
        boolean visited = true;

        SinkEventAttributeSet attribs = getAttributesFromParser( parser );

        if ( parser.getName().equals( Tag.H2.toString() ) )
        {
            closeOpenSections( Sink.SECTION_LEVEL_1, sink );

            sink.section( Sink.SECTION_LEVEL_1, attribs );

            sink.sectionTitle( Sink.SECTION_LEVEL_1, attribs );
        }
        else if ( parser.getName().equals( Tag.H3.toString() ) )
        {
            closeOpenSections( Sink.SECTION_LEVEL_2, sink );

            sink.section( Sink.SECTION_LEVEL_2, attribs );

            sink.sectionTitle( Sink.SECTION_LEVEL_2, attribs );
        }
        else if ( parser.getName().equals( Tag.H4.toString() ) )
        {
            closeOpenSections( Sink.SECTION_LEVEL_3, sink );

            sink.section( Sink.SECTION_LEVEL_3, attribs );

            sink.sectionTitle( Sink.SECTION_LEVEL_3, attribs );
        }
        else if ( parser.getName().equals( Tag.H5.toString() ) )
        {
            closeOpenSections( Sink.SECTION_LEVEL_4, sink );

            sink.section( Sink.SECTION_LEVEL_4, attribs );

            sink.sectionTitle( Sink.SECTION_LEVEL_4, attribs );
        }
        else if ( parser.getName().equals( Tag.H6.toString() ) )
        {
            closeOpenSections( Sink.SECTION_LEVEL_5, sink );

            sink.section( Sink.SECTION_LEVEL_5, attribs );

            sink.sectionTitle( Sink.SECTION_LEVEL_5, attribs );
        }
        else if ( parser.getName().equals( Tag.U.toString() ) )
        {
            this.decoration = "underline";
        }
        else if ( parser.getName().equals( Tag.S.toString() )
                || parser.getName().equals( Tag.STRIKE.toString() )
                || parser.getName().equals( "del" ) )
        {
            this.decoration = "line-through";
        }
        else if ( parser.getName().equals( Tag.SUB.toString() ) )
        {
            this.decoration = "sub";
        }
        else if ( parser.getName().equals( Tag.SUP.toString() ) )
        {
            this.decoration = "sup";
        }
        else if ( parser.getName().equals( Tag.P.toString() ) )
        {
            if ( !inFigure )
            {
                sink.paragraph( attribs );
            }
        }
        else if ( parser.getName().equals( Tag.DIV.toString() ) )
        {
            String divclass = parser.getAttributeValue( null, Attribute.CLASS.toString() );

            if ( "figure".equals( divclass ) )
            {
                this.inFigure = true;
                SinkEventAttributeSet atts = new SinkEventAttributeSet( attribs );
                atts.removeAttribute( SinkEventAttributeSet.CLASS );
                sink.figure( atts );
            }
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
            verbatim();

            attribs.removeAttribute( SinkEventAttributeSet.DECORATION );

            sink.verbatim( attribs );
        }
        else if ( parser.getName().equals( Tag.UL.toString() ) )
        {
            sink.list( attribs );
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

            sink.numberedList( numbering, attribs );
            orderedListDepth++;
        }
        else if ( parser.getName().equals( Tag.LI.toString() ) )
        {
            if ( orderedListDepth == 0 )
            {
                sink.listItem( attribs );
            }
            else
            {
                sink.numberedListItem( attribs );
            }
        }
        else if ( parser.getName().equals( Tag.DL.toString() ) )
        {
            sink.definitionList( attribs );
        }
        else if ( parser.getName().equals( Tag.DT.toString() ) )
        {
            sink.definitionListItem( attribs );
            sink.definedTerm( attribs );
        }
        else if ( parser.getName().equals( Tag.DD.toString() ) )
        {
            sink.definition( attribs );
        }
        else if ( ( parser.getName().equals( Tag.B.toString() ) )
                || ( parser.getName().equals( Tag.STRONG.toString() ) ) )
        {
            sink.bold( attribs );
        }
        else if ( ( parser.getName().equals( Tag.I.toString() ) )
                || ( parser.getName().equals( Tag.EM.toString() ) ) )
        {
            if ( inFigure )
            {
                sink.figureCaption( attribs );
            }
            else
            {
                sink.italic( attribs );
            }
        }
        else if ( ( parser.getName().equals( Tag.CODE.toString() ) )
                || ( parser.getName().equals( Tag.SAMP.toString() ) )
                || ( parser.getName().equals( Tag.TT.toString() ) ) )
        {
            sink.monospaced( attribs );
        }
        else if ( parser.getName().equals( Tag.A.toString() ) )
        {
            String href = parser.getAttributeValue( null, Attribute.HREF.toString() );

            if ( href != null )
            {
                String link = href;

                if ( link.startsWith( "#" ) )
                {
                    link = link.substring( 1 );
                }

                sink.link( link, attribs  );

                isLink = true;
            }
            else
            {
                String name = parser.getAttributeValue( null, Attribute.NAME.toString() );

                if ( name != null )
                {
                    sink.anchor( name, attribs  );

                    isAnchor = true;
                }
                else
                {
                    String id = parser.getAttributeValue( null, Attribute.ID.toString() );

                    if ( id != null )
                    {
                        sink.anchor( id, attribs  );

                        isAnchor = true;
                    }
                }
            }
        }

        // ----------------------------------------------------------------------
        // Tables
        // ----------------------------------------------------------------------

        else if ( parser.getName().equals( Tag.TABLE.toString() ) )
        {
            sink.table( attribs );

            String border = parser.getAttributeValue( null, Attribute.BORDER.toString() );

            boolean grid = true;

            if ( "0".equals( border ) )
            {
                grid = false;
            }

            String align = parser.getAttributeValue( null, Attribute.ALIGN.toString() );

            int[] justif = { JUSTIFY_LEFT };

            if ( "center".equals( align ) )
            {
                justif[0] = JUSTIFY_CENTER;
            }
            else if ( "right".equals( align ) )
            {
                justif[0] = JUSTIFY_RIGHT;
            }

            sink.tableRows( justif, grid );
        }
        else if ( parser.getName().equals( Tag.TR.toString() ) )
        {
            sink.tableRow( attribs );
        }
        else if ( parser.getName().equals( Tag.TH.toString() ) )
        {
            sink.tableHeaderCell( attribs );
        }
        else if ( parser.getName().equals( Tag.TD.toString() ) )
        {
            sink.tableCell( attribs );
        }
        else if ( parser.getName().equals( Tag.CAPTION.toString() ) )
        {
            sink.tableRows_();
            this.hasCaption = true;
            sink.tableCaption( attribs );
        }

        // ----------------------------------------------------------------------
        // Empty elements: <br/>, <hr/> and <img />
        // ----------------------------------------------------------------------

        else if ( parser.getName().equals( Tag.BR.toString() ) )
        {
            sink.lineBreak( attribs );
        }
        else if ( parser.getName().equals( Tag.HR.toString() ) )
        {
            sink.horizontalRule( attribs );
        }
        else if ( parser.getName().equals( Tag.IMG.toString() ) )
        {
            String src = parser.getAttributeValue( null, Attribute.SRC.toString() );

            if ( src != null )
            {
                sink.figureGraphics( src, attribs );
            }
        }
        else
        {
            visited = false;
        }

        return visited;
    }

    /**
     * <p>
     *   Goes through a common list of possible html end tags.
     *   These should be re-usable by different xhtml-based parsers.
     *   The tags handled here are the same as for {@link #baseStartTag(XmlPullParser,Sink)},
     *   except for the empty elements (<code>&lt;br/&gt;, &lt;hr/&gt;, &lt;img/&gt;<code>).
     * </p>
     *
     * @param parser A parser.
     * @param sink the sink to receive the events.
     * @return True if the event has been handled by this method, false otherwise.
     */
    protected boolean baseEndTag( XmlPullParser parser, Sink sink )
    {
        boolean visited = true;

        if ( parser.getName().equals( Tag.P.toString() ) )
        {
            if ( !inFigure )
            {
                sink.paragraph_();
            }
        }
        else if ( parser.getName().equals( Tag.U.toString() )
                || parser.getName().equals( Tag.S.toString() )
                || parser.getName().equals( Tag.STRIKE.toString() )
                || parser.getName().equals( "del" )
                || parser.getName().equals( Tag.SUB.toString() )
                || parser.getName().equals( Tag.SUP.toString() ) )
        {
            this.decoration = null;
        }
        else if ( parser.getName().equals( Tag.DIV.toString() ) )
        {
            if ( inFigure )
            {
                sink.figure_();
                this.inFigure = false;
            }
        }
        else if ( parser.getName().equals( Tag.PRE.toString() ) )
        {
            verbatim_();

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
        else if ( ( parser.getName().equals( Tag.B.toString() ) )
                || ( parser.getName().equals( Tag.STRONG.toString() ) ) )
        {
            sink.bold_();
        }
        else if ( ( parser.getName().equals( Tag.I.toString() ) )
                || ( parser.getName().equals( Tag.EM.toString() ) ) )
        {
            if ( inFigure )
            {
                sink.figureCaption_();
            }
            else
            {
                sink.italic_();
            }
        }
        else if ( ( parser.getName().equals( Tag.CODE.toString() ) )
                || ( parser.getName().equals( Tag.SAMP.toString() ) )
                || ( parser.getName().equals( Tag.TT.toString() ) ) )
        {
            sink.monospaced_();
        }
        else if ( parser.getName().equals( Tag.A.toString() ) )
        {
            if ( isLink )
            {
                sink.link_();

                isLink = false;
            }
            else if ( isAnchor )
            {
                sink.anchor_();

                isAnchor = false;
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
        else
        {
            visited = false;
        }

        return visited;
    }

    /**
     * {@inheritDoc}
     *
     * Just calls {@link #baseStartTag(XmlPullParser,Sink)}, this should be
     * overridden by implementing parsers to include additional tags.
     */
    protected void handleStartTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        if ( !baseStartTag( parser, sink ) )
        {
            if ( getLog().isWarnEnabled() )
            {
                String position = "[" + parser.getLineNumber() + ":"
                    + parser.getColumnNumber() + "]";
                String tag = "<" + parser.getName() + ">";

                getLog().warn( "Unrecognized xml tag: " + tag + " at " + position );
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * Just calls {@link #baseEndTag(XmlPullParser,Sink)}, this should be
     * overridden by implementing parsers to include additional tags.
     */
    protected void handleEndTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        if ( !baseEndTag( parser, sink ) )
        {
            // unrecognized tag is already logged in StartTag
        }
    }

    /** {@inheritDoc} */
    protected void handleText( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = parser.getText();

        /*
         * NOTE: Don't do any whitespace trimming here. Whitespace normalization has already been performed by the
         * parser so any whitespace that makes it here is significant.
         */
        if ( StringUtils.isNotEmpty( text ) )
        {
            SinkEventAttributeSet atts = new SinkEventAttributeSet();

            if ( StringUtils.isNotEmpty( decoration ) )
            {
                if ( "underline".equals( decoration ) || "line-through".equals( decoration ) )
                {
                    atts.addAttribute( SinkEventAttributeSet.DECORATION, decoration);
                }
                else if ( "sub".equals( decoration ) || "sup".equals( decoration ) )
                {
                    atts.addAttribute( SinkEventAttributeSet.VALIGN, decoration);
                }
            }

            // Emit separate text events for different lines, e.g. the input
            // "\nLine1\n\nLine2\n\n" should deliver the event sequence "\n", "Line1\n", "\n", "Line2\n", "\n".
            // In other words, the concatenation of the text events must deliver the input sequence.
            // (according to section 2.11 of the XML spec, parsers must normalize line breaks to "\n")
            String[] lines = text.split( "\n", -1 );

            for ( int i = 0; i < lines.length - 1; i++ )
            {
                sink.text( lines[i] + EOL, atts );
            }

            if ( lines[lines.length - 1].length() > 0 )
            {
                sink.text( lines[lines.length - 1], atts );
            }
        }
    }

    /** {@inheritDoc} */
    protected void handleCdsect( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        sink.text( parser.getText() );
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

    /**
     * Close open sections. The current level is set to newLevel afterwards.
     *
     * @param newLevel the new section level, all upper levels have to be closed.
     * @param sink the sink to receive the events.
     */
    protected void closeOpenSections( int newLevel, Sink sink )
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

    /**
     * Return the current section level.
     *
     * @return the current section level.
     */
    protected int getSectionLevel()
    {
        return this.sectionLevel;
    }

    /**
     * Set the current section level.
     *
     * @param newLevel the new section level.
     */
    protected void setSectionLevel( int newLevel )
    {
        this.sectionLevel = newLevel;
    }

    /**
     * Decrease the current verbatim level.
     */
    protected void verbatim_()
    {
        verbatimLevel--;
    }

    /**
     * Increases the current verbatim level.
     * A value of 0 means that we are not in verbatim mode, every nested &lt;pre&gt; tag increases the level.
     */
    protected void verbatim()
    {
        verbatimLevel++;
    }

    /**
     * Checks if we are currently insid a &lt;pre&gt; tag.
     *
     * @return true if we are currently in verbatim mode.
     */
    protected boolean isVerbatim()
    {
        return ( this.verbatimLevel != 0 );
    }

}
