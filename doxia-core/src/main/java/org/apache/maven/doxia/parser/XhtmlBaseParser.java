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
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.util.DoxiaUtils;

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

    /** Counts section level. */
    private int sectionLevel;

    /** Verbatim level, increased whenever a &lt;pre&gt; tag is encountered. */
    private int verbatimLevel;

    /** Used to recognize the case of img inside figure. */
    private boolean inFigure;

    /** Decoration properties, eg for texts. */
    private final SinkEventAttributeSet decoration = new SinkEventAttributeSet();;

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

        if ( isVerbatim() )
        {
            handleVerbatim( parser, sink );
        }
        else if ( parser.getName().equals( Tag.H2.toString() ) )
        {
            handleSectionStart( sink, Sink.SECTION_LEVEL_1, attribs );
        }
        else if ( parser.getName().equals( Tag.H3.toString() ) )
        {
            handleSectionStart( sink, Sink.SECTION_LEVEL_2, attribs );
        }
        else if ( parser.getName().equals( Tag.H4.toString() ) )
        {
            handleSectionStart( sink, Sink.SECTION_LEVEL_3, attribs );
        }
        else if ( parser.getName().equals( Tag.H5.toString() ) )
        {
            handleSectionStart( sink, Sink.SECTION_LEVEL_4, attribs );
        }
        else if ( parser.getName().equals( Tag.H6.toString() ) )
        {
            handleSectionStart( sink, Sink.SECTION_LEVEL_5, attribs );
        }
        else if ( parser.getName().equals( Tag.U.toString() ) )
        {
            decoration.addAttribute( SinkEventAttributes.DECORATION, "underline" );
        }
        else if ( parser.getName().equals( Tag.S.toString() ) || parser.getName().equals( Tag.STRIKE.toString() )
                || parser.getName().equals( "del" ) )
        {
            decoration.addAttribute( SinkEventAttributes.DECORATION, "line-through" );
        }
        else if ( parser.getName().equals( Tag.SUB.toString() ) )
        {
            decoration.addAttribute( SinkEventAttributes.VALIGN, "sub" );
        }
        else if ( parser.getName().equals( Tag.SUP.toString() ) )
        {
            decoration.addAttribute( SinkEventAttributes.VALIGN, "sup" );
        }
        else if ( parser.getName().equals( Tag.P.toString() ) )
        {
            handlePStart( sink, attribs );
        }
        else if ( parser.getName().equals( Tag.DIV.toString() ) )
        {
            visited = handleDivStart( parser, attribs, sink );
        }
        else if ( parser.getName().equals( Tag.PRE.toString() ) )
        {
            handlePreStart( attribs, sink );
        }
        else if ( parser.getName().equals( Tag.UL.toString() ) )
        {
            sink.list( attribs );
        }
        else if ( parser.getName().equals( Tag.OL.toString() ) )
        {
            handleOLStart( parser, sink, attribs );
        }
        else if ( parser.getName().equals( Tag.LI.toString() ) )
        {
            handleLIStart( sink, attribs );
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
            sink.bold();
        }
        else if ( ( parser.getName().equals( Tag.I.toString() ) )
                || ( parser.getName().equals( Tag.EM.toString() ) ) )
        {
            handleFigureCaptionStart( sink, attribs );
        }
        else if ( ( parser.getName().equals( Tag.CODE.toString() ) )
                || ( parser.getName().equals( Tag.SAMP.toString() ) )
                || ( parser.getName().equals( Tag.TT.toString() ) ) )
        {
            sink.monospaced();
        }
        else if ( parser.getName().equals( Tag.A.toString() ) )
        {
            handleAStart( parser, sink, attribs );
        }
        else if ( parser.getName().equals( Tag.TABLE.toString() ) )
        {
            handleTableStart( sink, attribs, parser );
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
            sink.tableCaption( attribs );
        }
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
            handleImgStart( parser, sink, attribs );
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

        if ( isVerbatim() )
        {
            handleVerbatimEnd( parser, sink );
        }
        else if ( parser.getName().equals( Tag.P.toString() ) )
        {
            if ( !inFigure )
            {
                sink.paragraph_();
            }
        }
        else if ( parser.getName().equals( Tag.U.toString() )
                || parser.getName().equals( Tag.S.toString() )
                || parser.getName().equals( Tag.STRIKE.toString() )
                || parser.getName().equals( "del" ) )
        {
            decoration.removeAttribute( SinkEventAttributes.DECORATION );
        }
        else if ( parser.getName().equals( Tag.SUB.toString() )
                || parser.getName().equals( Tag.SUP.toString() ) )
        {
            decoration.removeAttribute( SinkEventAttributes.VALIGN );
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
            handleListItemEnd( sink );
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
            handleFigureCaptionEnd( sink );
        }
        else if ( ( parser.getName().equals( Tag.CODE.toString() ) )
                || ( parser.getName().equals( Tag.SAMP.toString() ) )
                || ( parser.getName().equals( Tag.TT.toString() ) ) )
        {
            sink.monospaced_();
        }
        else if ( parser.getName().equals( Tag.A.toString() ) )
        {
            handleAEnd( sink );
        }

        // ----------------------------------------------------------------------
        // Tables
        // ----------------------------------------------------------------------

        else if ( parser.getName().equals( Tag.TABLE.toString() ) )
        {
            sink.tableRows_();

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
        String text = getText( parser );

        /*
         * NOTE: Don't do any whitespace trimming here. Whitespace normalization has already been performed by the
         * parser so any whitespace that makes it here is significant.
         */
        if ( StringUtils.isNotEmpty( text ) )
        {
            sink.text( text, decoration );
        }
    }

    /** {@inheritDoc} */
    protected void handleCdsect( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        sink.text( getText( parser ) );
    }

    /** {@inheritDoc} */
    protected void handleComment( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = getText( parser );

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
        String text = getText( parser );

        int[] holder = new int[] {0, 0};
        char[] chars = parser.getTextCharacters( holder );
        String textChars = String.valueOf( chars, holder[0], holder[1] );

        if ( "#160".equals( textChars ) || "nbsp".equals( textChars ) )
        {
            sink.nonBreakingSpace();
        }
        else
        {
            if ( getLocalEntities().containsKey( textChars ) )
            {
                sink.rawText( text );
            }
            else
            {
                sink.text( text );
            }
        }
    }

    /**
     * Handles an unkown event.
     *
     * @param parser the parser to get the event from.
     * @param sink the sink to receive the event.
     * @param type the tag event type. This should be one of HtmlMarkup.TAG_TYPE_SIMPLE,
     * HtmlMarkup.TAG_TYPE_START or HtmlMarkup.TAG_TYPE_END. It will be passed as the first
     * argument of the required parameters to the Sink
     * {@link org.apache.maven.doxia.sink.Sink#unkown(String,Object[],SinkEventAttributes) unkown}
     * method.
     */
    protected void handleUnknown( XmlPullParser parser, Sink sink, int type )
    {
        Object[] required = new Object[] { new Integer( type ) };

        SinkEventAttributeSet attribs = getAttributesFromParser( parser );

        sink.unknown( parser.getName(), required, attribs );
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
            if ( sectionLevel == Sink.SECTION_LEVEL_5 )
            {
                sink.section5_();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_4 )
            {
                sink.section4_();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_3 )
            {
                sink.section3_();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_2 )
            {
                sink.section2_();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_1 )
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

    /**
     * Checks if the given id is a valid Doxia id and if not, returns a transformed one.
     *
     * @param id The id to validate.
     * @return A transformed id or the original id if it was already valid.
     * @see DoxiaUtils#encodeId(String)
     */
    protected String validAnchor( String id )
    {
        if ( !DoxiaUtils.isValidId( id ) )
        {
            getLog().warn( "Modified invalid anchor name: " + id );

            return DoxiaUtils.encodeId( id );
        }

        return id;
    }

    private void handleAEnd( Sink sink )
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

    private void handleAStart( XmlPullParser parser, Sink sink, SinkEventAttributeSet attribs )
    {
        String href = parser.getAttributeValue( null, Attribute.HREF.toString() );

        if ( href != null )
        {
            sink.link( href, attribs );
            isLink = true;
        }
        else
        {
            String name = parser.getAttributeValue( null, Attribute.NAME.toString() );

            if ( name != null )
            {
                sink.anchor( validAnchor( name ), attribs );
                isAnchor = true;
            }
            else
            {
                String id = parser.getAttributeValue( null, Attribute.ID.toString() );
                if ( id != null )
                {
                    sink.anchor( validAnchor( id ), attribs );
                    isAnchor = true;
                }
            }
        }
    }

    private boolean handleDivStart( XmlPullParser parser, SinkEventAttributeSet attribs, Sink sink )
    {
        boolean visited = true;

        String divclass = parser.getAttributeValue( null, Attribute.CLASS.toString() );

        if ( "figure".equals( divclass ) )
        {
            this.inFigure = true;
            SinkEventAttributeSet atts = new SinkEventAttributeSet( attribs );
            atts.removeAttribute( SinkEventAttributes.CLASS );
            sink.figure( atts );
        }
        else
        {
            visited = false;
        }

        return visited;
    }

    private void handleFigureCaptionEnd( Sink sink )
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

    private void handleFigureCaptionStart( Sink sink, SinkEventAttributeSet attribs )
    {
        if ( inFigure )
        {
            sink.figureCaption( attribs );
        }
        else
        {
            sink.italic();
        }
    }

    private void handleImgStart( XmlPullParser parser, Sink sink, SinkEventAttributeSet attribs )
    {
        String src = parser.getAttributeValue( null, Attribute.SRC.toString() );

        if ( src != null )
        {
            sink.figureGraphics( src, attribs );
        }
    }

    private void handleLIStart( Sink sink, SinkEventAttributeSet attribs )
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

    private void handleListItemEnd( Sink sink )
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

    private void handleOLStart( XmlPullParser parser, Sink sink, SinkEventAttributeSet attribs )
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

    private void handlePStart( Sink sink, SinkEventAttributeSet attribs )
    {
        if ( !inFigure )
        {
            sink.paragraph( attribs );
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
    private void handlePreStart( SinkEventAttributeSet attribs, Sink sink )
    {
        verbatim();
        attribs.removeAttribute( SinkEventAttributes.DECORATION );
        sink.verbatim( attribs );
    }

    private void handleSectionStart( Sink sink, int sectionLevel, SinkEventAttributeSet attribs )
    {
        closeOpenSections( sectionLevel, sink );
        sink.section( sectionLevel, attribs );
        sink.sectionTitle( sectionLevel, attribs );
    }

    private void handleTableStart( Sink sink, SinkEventAttributeSet attribs, XmlPullParser parser )
    {
        sink.table( attribs );
        String border = parser.getAttributeValue( null, Attribute.BORDER.toString() );
        boolean grid = true;

        if ( "0".equals( border ) )
        {
            grid = false;
        }

        String align = parser.getAttributeValue( null, Attribute.ALIGN.toString() );
        int[] justif = {Sink.JUSTIFY_LEFT};

        if ( "center".equals( align ) )
        {
            justif[0] = Sink.JUSTIFY_CENTER;
        }
        else if ( "right".equals( align ) )
        {
            justif[0] = Sink.JUSTIFY_RIGHT;
        }

        sink.tableRows( justif, grid );
    }

    private void handleVerbatim( XmlPullParser parser, Sink sink )
    {
        if ( parser.getName().equals( Tag.PRE.toString() ) )
        {
            verbatim();
        }

        sink.text( getText( parser ) );
    }

    private void handleVerbatimEnd( XmlPullParser parser, Sink sink )
    {
        if ( parser.getName().equals( Tag.PRE.toString() ) )
        {
            verbatim_();
            if ( isVerbatim() )
            {
                sink.text( getText( parser ) );
            }
            else
            {
                sink.verbatim_();
            }
        }
        else
        {
            sink.text( getText( parser ) );
        }
    }
}
