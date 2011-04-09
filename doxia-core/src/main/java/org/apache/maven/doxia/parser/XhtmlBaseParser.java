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

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.text.html.HTML.Attribute;

import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.markup.HtmlMarkup;
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
 * @since 1.1
 */
public class XhtmlBaseParser
    extends AbstractXmlParser
        implements HtmlMarkup
{
    /** True if a &lt;script&gt;&lt;/script&gt; block is read. CDATA sections within are handled as rawText. */
    private boolean scriptBlock;

    /** Used to distinguish &lt;a href=""&gt; from &lt;a name=""&gt;. */
    private boolean isLink;

    /** Used to distinguish &lt;a href=""&gt; from &lt;a name=""&gt;. */
    private boolean isAnchor;

    /** Used for nested lists. */
    private int orderedListDepth = 0;

    /** Counts section level. */
    private int sectionLevel;

    /** Verbatim flag, true whenever we are inside a &lt;pre&gt; tag. */
    private boolean inVerbatim;

    /** Used to recognize the case of img inside figure. */
    private boolean inFigure;

    /** Decoration properties, eg for texts. */
    private final SinkEventAttributeSet decoration = new SinkEventAttributeSet();

    /** Map of warn messages with a String as key to describe the error type and a Set as value.
     * Using to reduce warn messages. */
    private Map<String, Set<String>> warnMessages;

    /** {@inheritDoc} */
    public void parse( Reader source, Sink sink )
        throws ParseException
    {
        init();

        try
        {
            super.parse( source, sink );
        }
        finally
        {
            logWarnings();

            setSecondParsing( false );
            init();
        }
    }

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

        if ( parser.getName().equals( HtmlMarkup.H2.toString() ) )
        {
            handleSectionStart( sink, Sink.SECTION_LEVEL_1, attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.H3.toString() ) )
        {
            handleSectionStart( sink, Sink.SECTION_LEVEL_2, attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.H4.toString() ) )
        {
            handleSectionStart( sink, Sink.SECTION_LEVEL_3, attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.H5.toString() ) )
        {
            handleSectionStart( sink, Sink.SECTION_LEVEL_4, attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.H6.toString() ) )
        {
            handleSectionStart( sink, Sink.SECTION_LEVEL_5, attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.U.toString() ) )
        {
            decoration.addAttribute( SinkEventAttributes.DECORATION, "underline" );
        }
        else if ( parser.getName().equals( HtmlMarkup.S.toString() )
                || parser.getName().equals( HtmlMarkup.STRIKE.toString() )
                || parser.getName().equals( "del" ) )
        {
            decoration.addAttribute( SinkEventAttributes.DECORATION, "line-through" );
        }
        else if ( parser.getName().equals( HtmlMarkup.SUB.toString() ) )
        {
            decoration.addAttribute( SinkEventAttributes.VALIGN, "sub" );
        }
        else if ( parser.getName().equals( HtmlMarkup.SUP.toString() ) )
        {
            decoration.addAttribute( SinkEventAttributes.VALIGN, "sup" );
        }
        else if ( parser.getName().equals( HtmlMarkup.P.toString() ) )
        {
            handlePStart( sink, attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.DIV.toString() ) )
        {
            visited = handleDivStart( parser, attribs, sink );
        }
        else if ( parser.getName().equals( HtmlMarkup.PRE.toString() ) )
        {
            handlePreStart( attribs, sink );
        }
        else if ( parser.getName().equals( HtmlMarkup.UL.toString() ) )
        {
            sink.list( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.OL.toString() ) )
        {
            handleOLStart( parser, sink, attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.LI.toString() ) )
        {
            handleLIStart( sink, attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.DL.toString() ) )
        {
            sink.definitionList( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.DT.toString() ) )
        {
            sink.definitionListItem( attribs );
            sink.definedTerm( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.DD.toString() ) )
        {
            sink.definition( attribs );
        }
        else if ( ( parser.getName().equals( HtmlMarkup.B.toString() ) )
                || ( parser.getName().equals( HtmlMarkup.STRONG.toString() ) ) )
        {
            sink.bold();
        }
        else if ( ( parser.getName().equals( HtmlMarkup.I.toString() ) )
                || ( parser.getName().equals( HtmlMarkup.EM.toString() ) ) )
        {
            handleFigureCaptionStart( sink, attribs );
        }
        else if ( ( parser.getName().equals( HtmlMarkup.CODE.toString() ) )
                || ( parser.getName().equals( HtmlMarkup.SAMP.toString() ) )
                || ( parser.getName().equals( HtmlMarkup.TT.toString() ) ) )
        {
            sink.monospaced();
        }
        else if ( parser.getName().equals( HtmlMarkup.A.toString() ) )
        {
            handleAStart( parser, sink, attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.TABLE.toString() ) )
        {
            handleTableStart( sink, attribs, parser );
        }
        else if ( parser.getName().equals( HtmlMarkup.TR.toString() ) )
        {
            sink.tableRow( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.TH.toString() ) )
        {
            sink.tableHeaderCell( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.TD.toString() ) )
        {
            sink.tableCell( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.CAPTION.toString() ) )
        {
            sink.tableCaption( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.BR.toString() ) )
        {
            sink.lineBreak( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.HR.toString() ) )
        {
            sink.horizontalRule( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.IMG.toString() ) )
        {
            handleImgStart( parser, sink, attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.SCRIPT.toString() ) )
        {
            handleUnknown( parser, sink, TAG_TYPE_START );
            scriptBlock = true;
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

        if ( parser.getName().equals( HtmlMarkup.P.toString() ) )
        {
            if ( !inFigure )
            {
                sink.paragraph_();
            }
        }
        else if ( parser.getName().equals( HtmlMarkup.U.toString() )
                || parser.getName().equals( HtmlMarkup.S.toString() )
                || parser.getName().equals( HtmlMarkup.STRIKE.toString() )
                || parser.getName().equals( "del" ) )
        {
            decoration.removeAttribute( SinkEventAttributes.DECORATION );
        }
        else if ( parser.getName().equals( HtmlMarkup.SUB.toString() )
                || parser.getName().equals( HtmlMarkup.SUP.toString() ) )
        {
            decoration.removeAttribute( SinkEventAttributes.VALIGN );
        }
        else if ( parser.getName().equals( HtmlMarkup.DIV.toString() ) )
        {
            if ( inFigure )
            {
                sink.figure_();
                this.inFigure = false;
            }
            else
            {
                visited = false;
            }
        }
        else if ( parser.getName().equals( HtmlMarkup.PRE.toString() ) )
        {
            verbatim_();

            sink.verbatim_();
        }
        else if ( parser.getName().equals( HtmlMarkup.UL.toString() ) )
        {
            sink.list_();
        }
        else if ( parser.getName().equals( HtmlMarkup.OL.toString() ) )
        {
            sink.numberedList_();
            orderedListDepth--;
        }
        else if ( parser.getName().equals( HtmlMarkup.LI.toString() ) )
        {
            handleListItemEnd( sink );
        }
        else if ( parser.getName().equals( HtmlMarkup.DL.toString() ) )
        {
            sink.definitionList_();
        }
        else if ( parser.getName().equals( HtmlMarkup.DT.toString() ) )
        {
            sink.definedTerm_();
        }
        else if ( parser.getName().equals( HtmlMarkup.DD.toString() ) )
        {
            sink.definition_();
            sink.definitionListItem_();
        }
        else if ( ( parser.getName().equals( HtmlMarkup.B.toString() ) )
                || ( parser.getName().equals( HtmlMarkup.STRONG.toString() ) ) )
        {
            sink.bold_();
        }
        else if ( ( parser.getName().equals( HtmlMarkup.I.toString() ) )
                || ( parser.getName().equals( HtmlMarkup.EM.toString() ) ) )
        {
            handleFigureCaptionEnd( sink );
        }
        else if ( ( parser.getName().equals( HtmlMarkup.CODE.toString() ) )
                || ( parser.getName().equals( HtmlMarkup.SAMP.toString() ) )
                || ( parser.getName().equals( HtmlMarkup.TT.toString() ) ) )
        {
            sink.monospaced_();
        }
        else if ( parser.getName().equals( HtmlMarkup.A.toString() ) )
        {
            handleAEnd( sink );
        }

        // ----------------------------------------------------------------------
        // Tables
        // ----------------------------------------------------------------------

        else if ( parser.getName().equals( HtmlMarkup.TABLE.toString() ) )
        {
            sink.tableRows_();

            sink.table_();
        }
        else if ( parser.getName().equals( HtmlMarkup.TR.toString() ) )
        {
            sink.tableRow_();
        }
        else if ( parser.getName().equals( HtmlMarkup.TH.toString() ) )
        {
            sink.tableHeaderCell_();
        }
        else if ( parser.getName().equals( HtmlMarkup.TD.toString() ) )
        {
            sink.tableCell_();
        }
        else if ( parser.getName().equals( HtmlMarkup.CAPTION.toString() ) )
        {
            sink.tableCaption_();
        }
        else if ( parser.getName().equals( HtmlMarkup.H2.toString() ) )
        {
            sink.sectionTitle1_();
        }
        else if ( parser.getName().equals( HtmlMarkup.H3.toString() ) )
        {
            sink.sectionTitle2_();
        }
        else if ( parser.getName().equals( HtmlMarkup.H4.toString() ) )
        {
            sink.sectionTitle3_();
        }
        else if ( parser.getName().equals( HtmlMarkup.H5.toString() ) )
        {
            sink.sectionTitle4_();
        }
        else if ( parser.getName().equals( HtmlMarkup.H6.toString() ) )
        {
            sink.sectionTitle5_();
        }
        else if ( parser.getName().equals( HtmlMarkup.SCRIPT.toString() ) )
        {
            handleUnknown( parser, sink, TAG_TYPE_END );

            scriptBlock = false;
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
         *
         * NOTE: text within script tags is ignored, scripting code should be embedded in CDATA.
         */
        if ( StringUtils.isNotEmpty( text ) && !isScriptBlock() )
        {
            sink.text( text, decoration );
        }
    }

    /** {@inheritDoc} */
    protected void handleComment( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = getText( parser ).trim();

        if ( "PB".equals( text ) )
        {
            sink.pageBreak();
        }
        else
        {
            sink.comment( text );
        }
    }

    /** {@inheritDoc} */
    protected void handleCdsect( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = getText( parser );

        if ( isScriptBlock() )
        {
            sink.unknown( CDATA, new Object[] {new Integer( CDATA_TYPE ), text}, null );
        }
        else
        {
            sink.text( text );
        }
    }

    /**
     * Make sure sections are nested consecutively.
     *
     * <p>
     * HTML doesn't have any sections, only sectionTitles (&lt;h2&gt; etc), that means we have to
     * open close any sections that are missing in between.
     * </p>
     *
     * <p>
     * For instance, if the following sequence is parsed:
     * <pre>
     * &lt;h3&gt;&lt;/h3&gt;
     * &lt;h6&gt;&lt;/h6&gt;
     * </pre>
     * we have to insert two section starts before we open the <code>&lt;h6&gt;</code>.
     * In the following sequence
     * <pre>
     * &lt;h6&gt;&lt;/h6&gt;
     * &lt;h3&gt;&lt;/h3&gt;
     * </pre>
     * we have to close two sections before we open the <code>&lt;h3&gt;</code>.
     * </p>
     *
     * <p>The current level is set to newLevel afterwards.</p>
     *
     * @param newLevel the new section level, all upper levels have to be closed.
     * @param sink the sink to receive the events.
     */
    protected void consecutiveSections( int newLevel, Sink sink )
    {
        closeOpenSections( newLevel, sink );
        openMissingSections( newLevel, sink );

        this.sectionLevel = newLevel;
    }

    /**
     * Close open sections.
     *
     * @param newLevel the new section level, all upper levels have to be closed.
     * @param sink the sink to receive the events.
     */
    private void closeOpenSections( int newLevel, Sink sink )
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
    }

    /**
     * Open missing sections.
     *
     * @param newLevel the new section level, all lower levels have to be opened.
     * @param sink the sink to receive the events.
     */
    private void openMissingSections( int newLevel, Sink sink )
    {
        while ( this.sectionLevel < newLevel - 1 )
        {
            this.sectionLevel++;

            if ( sectionLevel == Sink.SECTION_LEVEL_5 )
            {
                sink.section5();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_4 )
            {
                sink.section4();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_3 )
            {
                sink.section3();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_2 )
            {
                sink.section2();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_1 )
            {
                sink.section1();
            }
        }
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
     * Stop verbatim mode.
     */
    protected void verbatim_()
    {
        this.inVerbatim = false;
    }

    /**
     * Start verbatim mode.
     */
    protected void verbatim()
    {
        this.inVerbatim = true;
    }

    /**
     * Checks if we are currently inside a &lt;pre&gt; tag.
     *
     * @return true if we are currently in verbatim mode.
     */
    protected boolean isVerbatim()
    {
        return this.inVerbatim;
    }

    /**
     * Checks if we are currently inside a &lt;script&gt; tag.
     *
     * @return true if we are currently inside <code>&lt;script&gt;</code> tags.
     *
     * @since 1.1.1.
     */
    protected boolean isScriptBlock()
    {
        return this.scriptBlock;
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
            String linkAnchor = DoxiaUtils.encodeId( id, true );

            String msg = "Modified invalid link: '" + id + "' to '" + linkAnchor + "'";
            logMessage( "modifiedLink", msg );

            return linkAnchor;
        }

        return id;
    }

    /** {@inheritDoc} */
    protected void init()
    {
        super.init();

        this.scriptBlock = false;
        this.isLink = false;
        this.isAnchor = false;
        this.orderedListDepth = 0;
        this.sectionLevel = 0;
        this.inVerbatim = false;
        this.inFigure = false;
        while ( this.decoration.getAttributeNames().hasMoreElements() )
        {
            this.decoration.removeAttribute( this.decoration.getAttributeNames().nextElement() );
        }
        this.warnMessages = null;
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
            int hashIndex = href.indexOf( "#" );
            if ( hashIndex != -1 && !DoxiaUtils.isExternalLink( href ) )
            {
                String hash = href.substring( hashIndex + 1 );

                if ( !DoxiaUtils.isValidId( hash ) )
                {
                    href = href.substring( 0, hashIndex ) + "#" + DoxiaUtils.encodeId( hash, true );

                    String msg = "Modified invalid link: '" + hash + "' to '" + href + "'";
                    logMessage( "modifiedLink", msg );
                }
            }
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

    private void handleSectionStart( Sink sink, int level, SinkEventAttributeSet attribs )
    {
        consecutiveSections( level, sink );
        sink.section( level, attribs );
        sink.sectionTitle( level, attribs );
    }

    private void handleTableStart( Sink sink, SinkEventAttributeSet attribs, XmlPullParser parser )
    {
        sink.table( attribs );
        String border = parser.getAttributeValue( null, Attribute.BORDER.toString() );
        boolean grid = true;

        if ( border == null || "0".equals( border ) )
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

    /**
     * If debug mode is enabled, log the <code>msg</code> as is, otherwise add unique msg in <code>warnMessages</code>.
     *
     * @param key not null
     * @param msg not null
     * @see #parse(Reader, Sink)
     * @since 1.1.1
     */
    private void logMessage( String key, String msg )
    {
        msg = "[XHTML Parser] " + msg;
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( msg );

            return;
        }

        if ( warnMessages == null )
        {
            warnMessages = new HashMap<String, Set<String>>();
        }

        Set<String> set = warnMessages.get( key );
        if ( set == null )
        {
            set = new TreeSet<String>();
        }
        set.add( msg );
        warnMessages.put( key, set );
    }

    /**
     * @since 1.1.1
     */
    private void logWarnings()
    {
        if ( getLog().isWarnEnabled() && this.warnMessages != null && !isSecondParsing() )
        {
            for ( Map.Entry<String, Set<String>> entry : this.warnMessages.entrySet() )
            {
                for ( String msg : entry.getValue() )
                {
                    getLog().warn( msg );
                }
            }

            this.warnMessages = null;
        }
    }
}
