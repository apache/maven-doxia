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
package org.apache.maven.doxia.parser;

import javax.swing.text.html.HTML.Attribute;

import java.io.Reader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.markup.HtmlMarkup;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.EventCapturingSinkProxy;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.util.DoxiaUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common base parser for xhtml5 events.
 */
public class Xhtml5BaseParser extends AbstractXmlParser implements HtmlMarkup {
    private static final Logger LOGGER = LoggerFactory.getLogger(Xhtml5BaseParser.class);

    /** Used to identify if a class string contains `bodyTableBorder` */
    private static final Pattern BODYTABLEBORDER_CLASS_PATTERN =
            Pattern.compile("(?:.*\\s|^)bodyTableBorder(?:\\s.*|$)");

    private static final Set<String> UNMATCHED_XHTML5_ELEMENTS = new HashSet<>();
    private static final Set<String> UNMATCHED_XHTML5_SIMPLE_ELEMENTS = new HashSet<>();

    static {
        UNMATCHED_XHTML5_SIMPLE_ELEMENTS.add(HtmlMarkup.AREA.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.AUDIO.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.BUTTON.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.CANVAS.toString());
        UNMATCHED_XHTML5_SIMPLE_ELEMENTS.add(HtmlMarkup.COL.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.COLGROUP.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.COMMAND.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.DATA.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.DATALIST.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.DETAILS.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.DIALOG.toString());
        UNMATCHED_XHTML5_SIMPLE_ELEMENTS.add(HtmlMarkup.EMBED.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.FIELDSET.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.FORM.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.HGROUP.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.IFRAME.toString());
        UNMATCHED_XHTML5_SIMPLE_ELEMENTS.add(HtmlMarkup.INPUT.toString());
        UNMATCHED_XHTML5_SIMPLE_ELEMENTS.add(HtmlMarkup.KEYGEN.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.LABEL.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.LEGEND.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.MAP.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.MENU.toString());
        UNMATCHED_XHTML5_SIMPLE_ELEMENTS.add(HtmlMarkup.MENUITEM.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.METER.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.NOSCRIPT.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.OBJECT.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.OPTGROUP.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.OPTION.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.OUTPUT.toString());
        UNMATCHED_XHTML5_SIMPLE_ELEMENTS.add(HtmlMarkup.PARAM.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.PICTURE.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.PROGRESS.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.SELECT.toString());
        UNMATCHED_XHTML5_SIMPLE_ELEMENTS.add(HtmlMarkup.SOURCE.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.SUMMARY.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.SVG.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.TEMPLATE.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.TEXTAREA.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.TBODY.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.THEAD.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.TFOOT.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.TIME.toString());
        UNMATCHED_XHTML5_SIMPLE_ELEMENTS.add(HtmlMarkup.TRACK.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.VAR.toString());
        UNMATCHED_XHTML5_ELEMENTS.add(HtmlMarkup.VIDEO.toString());
    }

    /**
     * True if a &lt;script&gt;&lt;/script&gt; or &lt;style&gt;&lt;/style&gt; block is read. CDATA sections within are
     * handled as rawText.
     */
    private boolean scriptBlock;

    /** Used to distinguish &lt;a href=""&gt; from &lt;a name=""&gt;. */
    private boolean isLink;

    /** Used to distinguish &lt;a href=""&gt; from &lt;a name=""&gt;. */
    private boolean isAnchor;

    /** Used for nested lists. */
    private int orderedListDepth = 0;

    /** Counts section nesting level of the sections manually set in the HTML document */
    private int sectionLevel;

    /** Counts current heading level. This is either the {@link #sectionLevel} if no artificial sections are currently open
     * for headings or a number higher or lower than {@link #sectionLevel} (for all section currently opened/closed for a preceding heading).
     * The heading level only changes when a new heading starts, or a section starts or ends. */
    private int headingLevel;

    /** Verbatim flag, true whenever we are inside a &lt;pre&gt; tag. */
    private boolean inVerbatim;

    /** Used to keep track of closing tags for content events */
    private Stack<String> divStack = new Stack<>();

    /** Used to wrap the definedTerm with its definition, even when one is omitted */
    boolean hasDefinitionListItem = false;

    private LinkedList<String> capturedSinkEventNames;

    /** {@inheritDoc} */
    @Override
    public void parse(Reader source, Sink sink, String reference) throws ParseException {
        init();

        try {
            capturedSinkEventNames = new LinkedList<>();
            Sink capturingSink = EventCapturingSinkProxy.newInstance(sink, capturedSinkEventNames);
            super.parse(source, capturingSink, reference);
        } finally {
            setSecondParsing(false);
            init();
        }
    }

    /**
     * {@inheritDoc}
     *
     * Adds all XHTML (HTML 5.2) entities to the parser so that they can be recognized and resolved
     * without additional DTD.
     */
    @Override
    protected void initXmlParser(XmlPullParser parser) throws XmlPullParserException {
        super.initXmlParser(parser);
    }

    /**
     * <p>
     *   Goes through a common list of possible html5 start tags. These include only tags that can go into
     *   the body of an xhtml5 document and so should be re-usable by different xhtml-based parsers.
     * </p>
     * <p>
     *   The currently handled tags are:
     * </p>
     * <p>
     *   <code>
     *      &lt;article&gt;, &lt;nav&gt;, &lt;aside&gt;, &lt;section&gt;, &lt;h1&gt;, &lt;h2&gt;, &lt;h3&gt;,
     *      &lt;h4&gt;, &lt;h5&gt;, &lt;header&gt;, &lt;main&gt;, &lt;footer&gt;, &lt;em&gt;, &lt;strong&gt;,
     *      &lt;small&gt;, &lt;s&gt;, &lt;cite&gt;, &lt;q&gt;, &lt;dfn&gt;, &lt;abbr&gt;, &lt;i&gt;,
     *      &lt;b&gt;, &lt;code&gt;, &lt;samp&gt;, &lt;kbd&gt;, &lt;sub&gt;, &lt;sup&gt;, &lt;u&gt;,
     *      &lt;mark&gt;, &lt;ruby&gt;, &lt;rb&gt;, &lt;rt&gt;, &lt;rtc&gt;, &lt;rp&gt;, &lt;bdi&gt;,
     *      &lt;bdo&gt;, &lt;span&gt;, &lt;ins&gt;, &lt;del&gt;, &lt;p&gt;, &lt;pre&gt;, &lt;ul&gt;,
     *      &lt;ol&gt;, &lt;li&gt;, &lt;dl&gt;, &lt;dt&gt;, &lt;dd&gt;, &lt;a&gt;, &lt;table&gt;,
     *      &lt;tr&gt;, &lt;th&gt;, &lt;td&gt;, &lt;caption&gt;, &lt;br/&gt;, &lt;wbr/&gt;, &lt;hr/&gt;,
     *      &lt;img/&gt;.
     *   </code>
     * </p>
     *
     * @param parser A parser.
     * @param sink the sink to receive the events.
     * @return True if the event has been handled by this method, i.e. the tag was recognized, false otherwise.
     */
    protected boolean baseStartTag(XmlPullParser parser, Sink sink) {
        SinkEventAttributeSet attribs = getAttributesFromParser(parser);
        return baseStartTag(parser.getName(), attribs, sink);
    }

    protected boolean baseStartTag(String elementName, SinkEventAttributeSet attribs, Sink sink) {
        boolean visited = true;

        if (elementName.equals(HtmlMarkup.ARTICLE.toString())) {
            sink.article(attribs);
        } else if (elementName.equals(HtmlMarkup.NAV.toString())) {
            sink.navigation(attribs);
        } else if (elementName.equals(HtmlMarkup.ASIDE.toString())) {
            sink.sidebar(attribs);
        } else if (elementName.equals(HtmlMarkup.SECTION.toString())) {
            handleSectionStart(sink, attribs);
        } else if (elementName.equals(HtmlMarkup.H1.toString())) {
            handleHeadingStart(sink, Sink.SECTION_LEVEL_1, attribs);
        } else if (elementName.equals(HtmlMarkup.H2.toString())) {
            handleHeadingStart(sink, Sink.SECTION_LEVEL_2, attribs);
        } else if (elementName.equals(HtmlMarkup.H3.toString())) {
            handleHeadingStart(sink, Sink.SECTION_LEVEL_3, attribs);
        } else if (elementName.equals(HtmlMarkup.H4.toString())) {
            handleHeadingStart(sink, Sink.SECTION_LEVEL_4, attribs);
        } else if (elementName.equals(HtmlMarkup.H5.toString())) {
            handleHeadingStart(sink, Sink.SECTION_LEVEL_5, attribs);
        } else if (elementName.equals(HtmlMarkup.H6.toString())) {
            handleHeadingStart(sink, Sink.SECTION_LEVEL_6, attribs);
        } else if (elementName.equals(HtmlMarkup.HEADER.toString())) {
            sink.header(attribs);
        } else if (elementName.equals(HtmlMarkup.MAIN.toString())) {
            sink.content(attribs);
        } else if (elementName.equals(HtmlMarkup.FOOTER.toString())) {
            sink.footer(attribs);
        } else if (elementName.equals(HtmlMarkup.EM.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.EMPHASIS);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.STRONG.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.STRONG);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.SMALL.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.SMALL);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.S.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.LINE_THROUGH);
            sink.inline(attribs);
            /* deprecated line-through support */
        } else if (elementName.equals(HtmlMarkup.CITE.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.CITATION);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.Q.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.QUOTE);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.DFN.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.DEFINITION);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.ABBR.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.ABBREVIATION);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.I.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.ITALIC);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.B.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.BOLD);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.CODE.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.CODE);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.VAR.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.VARIABLE);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.SAMP.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.SAMPLE);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.KBD.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.KEYBOARD);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.SUP.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.SUPERSCRIPT);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.SUB.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.SUBSCRIPT);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.U.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.ANNOTATION);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.MARK.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.HIGHLIGHT);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.RUBY.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.RUBY);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.RB.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.RUBY_BASE);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.RT.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.RUBY_TEXT);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.RTC.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.RUBY_TEXT_CONTAINER);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.RP.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.RUBY_PARANTHESES);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.BDI.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.BIDIRECTIONAL_ISOLATION);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.BDO.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.BIDIRECTIONAL_OVERRIDE);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.SPAN.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.PHRASE);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.INS.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.INSERT);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.DEL.toString())) {
            attribs.addAttributes(SinkEventAttributeSet.Semantics.DELETE);
            sink.inline(attribs);
        } else if (elementName.equals(HtmlMarkup.P.toString())) {
            handlePStart(sink, attribs);
        } else if (elementName.equals(HtmlMarkup.DIV.toString())) {
            handleDivStart(attribs, sink);
        } else if (elementName.equals(HtmlMarkup.PRE.toString())) {
            handlePreStart(attribs, sink);
        } else if (elementName.equals(HtmlMarkup.UL.toString())) {
            sink.list(attribs);
        } else if (elementName.equals(HtmlMarkup.OL.toString())) {
            handleOLStart(sink, attribs);
        } else if (elementName.equals(HtmlMarkup.LI.toString())) {
            handleLIStart(sink, attribs);
        } else if (elementName.equals(HtmlMarkup.DL.toString())) {
            sink.definitionList(attribs);
        } else if (elementName.equals(HtmlMarkup.DT.toString())) {
            if (hasDefinitionListItem) {
                // close previous listItem
                sink.definitionListItem_();
            }
            sink.definitionListItem(attribs);
            hasDefinitionListItem = true;
            sink.definedTerm(attribs);
        } else if (elementName.equals(HtmlMarkup.DD.toString())) {
            if (!hasDefinitionListItem) {
                sink.definitionListItem(attribs);
            }
            sink.definition(attribs);
        } else if (elementName.equals(HtmlMarkup.FIGURE.toString())) {
            sink.figure(attribs);
        } else if (elementName.equals(HtmlMarkup.FIGCAPTION.toString())) {
            sink.figureCaption(attribs);
        } else if (elementName.equals(HtmlMarkup.A.toString())) {
            handleAStart(sink, attribs);
        } else if (elementName.equals(HtmlMarkup.TABLE.toString())) {
            handleTableStart(sink, attribs);
        } else if (elementName.equals(HtmlMarkup.TR.toString())) {
            sink.tableRow(attribs);
        } else if (elementName.equals(HtmlMarkup.TH.toString())) {
            sink.tableHeaderCell(attribs);
        } else if (elementName.equals(HtmlMarkup.TD.toString())) {
            sink.tableCell(attribs);
        } else if (elementName.equals(HtmlMarkup.CAPTION.toString())) {
            sink.tableCaption(attribs);
        } else if (elementName.equals(HtmlMarkup.BR.toString())) {
            sink.lineBreak(attribs);
        } else if (elementName.equals(HtmlMarkup.WBR.toString())) {
            sink.lineBreakOpportunity(attribs);
        } else if (elementName.equals(HtmlMarkup.HR.toString())) {
            sink.horizontalRule(attribs);
        } else if (elementName.equals(HtmlMarkup.IMG.toString())) {
            handleImgStart(sink, attribs);
        } else if (elementName.equals(HtmlMarkup.BLOCKQUOTE.toString())) {
            sink.blockquote(attribs);
        } else if (UNMATCHED_XHTML5_ELEMENTS.contains(elementName)) {
            handleUnknown(elementName, attribs, sink, TAG_TYPE_START);
        } else if (UNMATCHED_XHTML5_SIMPLE_ELEMENTS.contains(elementName)) {
            handleUnknown(elementName, attribs, sink, TAG_TYPE_SIMPLE);
        } else if (elementName.equals(HtmlMarkup.SCRIPT.toString())
                || elementName.equals(HtmlMarkup.STYLE.toString())) {
            handleUnknown(elementName, attribs, sink, TAG_TYPE_START);
            scriptBlock = true;
        } else {
            visited = false;
        }

        return visited;
    }

    /**
     * <p>
     *   Goes through a common list of possible html end tags.
     *   These should be re-usable by different xhtml-based parsers.
     *   The tags handled here are the same as for {@link #baseStartTag(XmlPullParser,Sink)},
     *   except for the empty elements ({@code <br/>, <hr/>, <img/>}).
     * </p>
     *
     * @param parser A parser.
     * @param sink the sink to receive the events.
     * @return True if the event has been handled by this method, false otherwise.
     */
    protected boolean baseEndTag(XmlPullParser parser, Sink sink) {
        SinkEventAttributeSet attribs = getAttributesFromParser(parser);
        return baseEndTag(parser.getName(), attribs, sink);
    }

    protected boolean baseEndTag(String elementName, SinkEventAttributeSet attribs, Sink sink) {
        boolean visited = true;

        if (elementName.equals(HtmlMarkup.P.toString())) {
            sink.paragraph_();
        } else if (elementName.equals(HtmlMarkup.DIV.toString())) {
            handleDivEnd(sink);
        } else if (elementName.equals(HtmlMarkup.PRE.toString())) {
            verbatim_();

            sink.verbatim_();
        } else if (elementName.equals(HtmlMarkup.UL.toString())) {
            sink.list_();
        } else if (elementName.equals(HtmlMarkup.OL.toString())) {
            sink.numberedList_();
            orderedListDepth--;
        } else if (elementName.equals(HtmlMarkup.LI.toString())) {
            handleListItemEnd(sink);
        } else if (elementName.equals(HtmlMarkup.DL.toString())) {
            if (hasDefinitionListItem) {
                sink.definitionListItem_();
                hasDefinitionListItem = false;
            }
            sink.definitionList_();
        } else if (elementName.equals(HtmlMarkup.DT.toString())) {
            sink.definedTerm_();
        } else if (elementName.equals(HtmlMarkup.DD.toString())) {
            sink.definition_();
            sink.definitionListItem_();
            hasDefinitionListItem = false;
        } else if (elementName.equals(HtmlMarkup.FIGURE.toString())) {
            sink.figure_();
        } else if (elementName.equals(HtmlMarkup.FIGCAPTION.toString())) {
            sink.figureCaption_();
        } else if (elementName.equals(HtmlMarkup.A.toString())) {
            handleAEnd(sink);
        } else if (elementName.equals(HtmlMarkup.EM.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.STRONG.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.SMALL.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.S.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.CITE.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.Q.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.DFN.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.ABBR.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.I.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.B.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.CODE.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.VAR.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.SAMP.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.KBD.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.SUP.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.SUB.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.U.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.MARK.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.RUBY.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.RB.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.RT.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.RTC.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.RP.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.BDI.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.BDO.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.SPAN.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.INS.toString())) {
            sink.inline_();
        } else if (elementName.equals(HtmlMarkup.DEL.toString())) {
            sink.inline_();
        }

        // ----------------------------------------------------------------------
        // Tables
        // ----------------------------------------------------------------------

        else if (elementName.equals(HtmlMarkup.TABLE.toString())) {
            sink.tableRows_();
            sink.table_();
        } else if (elementName.equals(HtmlMarkup.TR.toString())) {
            sink.tableRow_();
        } else if (elementName.equals(HtmlMarkup.TH.toString())) {
            sink.tableHeaderCell_();
        } else if (elementName.equals(HtmlMarkup.TD.toString())) {
            sink.tableCell_();
        } else if (elementName.equals(HtmlMarkup.CAPTION.toString())) {
            sink.tableCaption_();
        } else if (elementName.equals(HtmlMarkup.ARTICLE.toString())) {
            sink.article_();
        } else if (elementName.equals(HtmlMarkup.NAV.toString())) {
            sink.navigation_();
        } else if (elementName.equals(HtmlMarkup.ASIDE.toString())) {
            sink.sidebar_();
        } else if (elementName.equals(HtmlMarkup.SECTION.toString())) {
            handleSectionEnd(sink);
        } else if (elementName.equals(HtmlMarkup.H1.toString())) {
            sink.sectionTitle1_();
        } else if (elementName.equals(HtmlMarkup.H2.toString())) {
            sink.sectionTitle2_();
        } else if (elementName.equals(HtmlMarkup.H3.toString())) {
            sink.sectionTitle3_();
        } else if (elementName.equals(HtmlMarkup.H4.toString())) {
            sink.sectionTitle4_();
        } else if (elementName.equals(HtmlMarkup.H5.toString())) {
            sink.sectionTitle5_();
        } else if (elementName.equals(HtmlMarkup.H6.toString())) {
            sink.sectionTitle6_();
        } else if (elementName.equals(HtmlMarkup.HEADER.toString())) {
            sink.header_();
        } else if (elementName.equals(HtmlMarkup.MAIN.toString())) {
            sink.content_();
        } else if (elementName.equals(HtmlMarkup.FOOTER.toString())) {
            sink.footer_();
        } else if (elementName.equals(HtmlMarkup.BLOCKQUOTE.toString())) {
            sink.blockquote_();
        } else if (UNMATCHED_XHTML5_ELEMENTS.contains(elementName)) {
            handleUnknown(elementName, attribs, sink, TAG_TYPE_END);
        } else if (elementName.equals(HtmlMarkup.SCRIPT.toString())
                || elementName.equals(HtmlMarkup.STYLE.toString())) {
            handleUnknown(elementName, attribs, sink, TAG_TYPE_END);

            scriptBlock = false;
        } else {
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
    protected void handleStartTag(XmlPullParser parser, Sink sink)
            throws XmlPullParserException, MacroExecutionException {
        if (!baseStartTag(parser, sink)) {
            LOGGER.warn(
                    "Unrecognized xml tag <{}> at [{}:{}]",
                    parser.getName(),
                    parser.getLineNumber(),
                    parser.getColumnNumber());
        }
    }

    /**
     * {@inheritDoc}
     *
     * Just calls {@link #baseEndTag(XmlPullParser,Sink)}, this should be
     * overridden by implementing parsers to include additional tags.
     */
    protected void handleEndTag(XmlPullParser parser, Sink sink)
            throws XmlPullParserException, MacroExecutionException {
        if (!baseEndTag(parser, sink)) {
            // unrecognized tag is already logged in StartTag
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void handleText(XmlPullParser parser, Sink sink) throws XmlPullParserException {
        String text = getText(parser);

        /*
         * NOTE: Don't do any whitespace trimming here. Whitespace normalization has already been performed by the
         * parser so any whitespace that makes it here is significant.
         *
         * NOTE: text within script tags is ignored, scripting code should be embedded in CDATA.
         */
        if ((text != null && !text.isEmpty()) && !isScriptBlock()) {
            sink.text(text);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void handleComment(XmlPullParser parser, Sink sink) throws XmlPullParserException {
        String text = getText(parser);

        if ("PB".equals(text.trim())) {
            sink.pageBreak();
        } else {
            if (isEmitComments()) {
                sink.comment(text);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void handleCdsect(XmlPullParser parser, Sink sink) throws XmlPullParserException {
        String text = getText(parser);

        if (isScriptBlock()) {
            sink.unknown(CDATA, new Object[] {CDATA_TYPE, text}, null);
        } else {
            sink.text(text);
        }
    }

    /**
     * Shortcut for {@link #emitHeadingSections(int, Sink, boolean)} with last argument being {@code true}
     * @param newLevel
     * @param sink
     * @param attribs
     * @deprecated Use {@link #emitHeadingSections(int, Sink, boolean)} instead.
     */
    @Deprecated
    protected void consecutiveSections(int newLevel, Sink sink, SinkEventAttributeSet attribs) {
        emitHeadingSections(newLevel, sink, true);
    }

    /**
     * Make sure sections are nested consecutively and correctly inserted for the given heading level
     *
     * <p>
     * HTML5 heading tags H1 to H5 imply same level sections in Sink API (compare with {@link Sink#sectionTitle(int, SinkEventAttributes)}).
     * However (X)HTML5 allows headings without explicit surrounding section elements and is also
     * less strict with non-consecutive heading levels.
     * This methods both closes open sections which have been added for previous headings and/or opens
     * sections necessary for the new heading level.
     * At least one section needs to be opened directly prior the heading due to Sink API restrictions.
     * </p>
     *
     * <p>
     * For instance, if the following sequence is parsed:
     * </p>
     * <pre>
     * &lt;h2&gt;&lt;/h2&gt;
     * &lt;h5&gt;&lt;/h5&gt;
     * </pre>
     * <p>
     * we have to insert two section starts before we open the <code>&lt;h5&gt;</code>.
     * In the following sequence
     * </p>
     * <pre>
     * &lt;h5&gt;&lt;/h5&gt;
     * &lt;h2&gt;&lt;/h2&gt;
     * </pre>
     * <p>
     * we have to close two sections before we open the <code>&lt;h2&gt;</code>.
     * </p>
     *
     * <p>The current heading level is set to newLevel afterwards.</p>
     *
     * @param newLevel the new section level, all upper levels have to be closed.
     * @param sink the sink to receive the events.
     * @param enforceNewSection whether to enforce a new section or not
     */
    protected void emitHeadingSections(int newLevel, Sink sink, boolean enforceNewSection) {
        int lowerBoundSectionLevel = newLevel;
        if (enforceNewSection) {
            // close one more if either last event was not section start or the new level is lower than the current one
            // (in this case the last event may be a section start event but for another level)
            if (!isLastEventSectionStart() || newLevel < this.headingLevel) {
                lowerBoundSectionLevel--;
            }
        }
        closeOpenHeadingSections(lowerBoundSectionLevel, sink);
        openMissingHeadingSections(newLevel, sink);

        this.headingLevel = newLevel;
    }

    private boolean isLastEventSectionStart() {
        String lastEventName = capturedSinkEventNames.pollLast();
        if (lastEventName == null) {
            return false;
        }
        return lastEventName.startsWith("section")
                && !lastEventName.endsWith("_")
                && !lastEventName.startsWith("sectionTitle");
    }

    /**
     * Close open heading sections.
     *
     * @param newLevel the new section level, all upper levels have to be closed.
     * @param sink the sink to receive the events.
     */
    private void closeOpenHeadingSections(int newLevel, Sink sink) {
        while (this.headingLevel > newLevel) {
            if (headingLevel >= Sink.SECTION_LEVEL_1 && headingLevel <= Sink.SECTION_LEVEL_6) {
                sink.section_(headingLevel);
            }

            this.headingLevel--;
        }
        // enforce the previous element is a section
    }

    /**
     * Open missing heading sections.
     *
     * @param newLevel the new section level, all lower levels have to be opened.
     * @param sink the sink to receive the events.
     */
    private void openMissingHeadingSections(int newLevel, Sink sink) {
        while (this.headingLevel < newLevel) {
            this.headingLevel++;

            if (headingLevel >= Sink.SECTION_LEVEL_1 && headingLevel <= Sink.SECTION_LEVEL_6) {
                sink.section(headingLevel, null);
            }
        }
    }

    /**
     * Return the current section level.
     *
     * @return the current section level.
     */
    protected int getSectionLevel() {
        return this.headingLevel;
    }

    /**
     * Set the current section level.
     *
     * @param newLevel the new section level.
     */
    protected void setSectionLevel(int newLevel) {
        this.headingLevel = newLevel;
    }

    /**
     * Stop verbatim mode.
     */
    protected void verbatim_() {
        this.inVerbatim = false;
    }

    /**
     * Start verbatim mode.
     */
    protected void verbatim() {
        this.inVerbatim = true;
    }

    /**
     * Checks if we are currently inside a &lt;pre&gt; tag.
     *
     * @return true if we are currently in verbatim mode.
     */
    protected boolean isVerbatim() {
        return this.inVerbatim;
    }

    /**
     * Checks if we are currently inside a &lt;script&gt; tag.
     *
     * @return true if we are currently inside <code>&lt;script&gt;</code> tags.
     * @since 1.1.1.
     */
    protected boolean isScriptBlock() {
        return this.scriptBlock;
    }

    /**
     * Checks if the given id is a valid Doxia id and if not, returns a transformed one.
     *
     * @param id The id to validate.
     * @return A transformed id or the original id if it was already valid.
     * @see DoxiaUtils#encodeId(String)
     */
    protected String validAnchor(String id) {
        if (!DoxiaUtils.isValidId(id)) {
            String linkAnchor = DoxiaUtils.encodeId(id);

            LOGGER.debug("Modified invalid link '{}' to '{}'", id, linkAnchor);

            return linkAnchor;
        }

        return id;
    }

    /** {@inheritDoc} */
    @Override
    protected void init() {
        super.init();

        this.scriptBlock = false;
        this.isLink = false;
        this.isAnchor = false;
        this.orderedListDepth = 0;
        this.headingLevel = 0;
        this.inVerbatim = false;
    }

    private void handleAEnd(Sink sink) {
        if (isLink) {
            sink.link_();
            isLink = false;
        } else if (isAnchor) {
            sink.anchor_();
            isAnchor = false;
        }
    }

    private void handleAStart(Sink sink, SinkEventAttributeSet attribs) {
        String href = (String) attribs.getAttribute(Attribute.HREF.toString());

        if (href != null) {
            int hashIndex = href.indexOf('#');
            if (hashIndex != -1 && !DoxiaUtils.isExternalLink(href)) {
                String hash = href.substring(hashIndex + 1);

                if (!DoxiaUtils.isValidId(hash)) {
                    href = href.substring(0, hashIndex) + "#" + DoxiaUtils.encodeId(hash);

                    LOGGER.debug("Modified invalid link '{}' to '{}'", hash, href);
                }
            }
            sink.link(href, attribs);
            isLink = true;
        } else {
            String id = (String) attribs.getAttribute(Attribute.ID.toString());
            if (id != null) {
                sink.anchor(validAnchor(id), attribs);
                isAnchor = true;
            }
        }
    }

    private boolean handleDivStart(SinkEventAttributeSet attribs, Sink sink) {
        String divClass = (String) attribs.getAttribute(Attribute.CLASS.toString());

        this.divStack.push(divClass);

        if ("content".equals(divClass)) {
            SinkEventAttributeSet atts = new SinkEventAttributeSet(attribs);
            atts.removeAttribute(SinkEventAttributes.CLASS);
            sink.content(atts);
        }
        if ("verbatim".equals(divClass) || "verbatim source".equals(divClass)) {
            return false;
        } else {
            sink.division(attribs);
        }

        return true;
    }

    private boolean handleDivEnd(Sink sink) {
        String divClass = divStack.pop();

        if ("content".equals(divClass)) {
            sink.content_();
        }
        if ("verbatim".equals(divClass) || "verbatim source".equals(divClass)) {
            return false;
        } else {
            sink.division_();
        }

        return true;
    }

    private void handleImgStart(Sink sink, SinkEventAttributeSet attribs) {
        String src = (String) attribs.getAttribute(Attribute.SRC.toString());

        if (src != null) {
            sink.figureGraphics(src, attribs);
        }
    }

    private void handleLIStart(Sink sink, SinkEventAttributeSet attribs) {
        if (orderedListDepth == 0) {
            sink.listItem(attribs);
        } else {
            sink.numberedListItem(attribs);
        }
    }

    private void handleListItemEnd(Sink sink) {
        if (orderedListDepth == 0) {
            sink.listItem_();
        } else {
            sink.numberedListItem_();
        }
    }

    private void handleOLStart(Sink sink, SinkEventAttributeSet attribs) {
        int numbering = Sink.NUMBERING_DECIMAL;
        // this will have to be generalized if we handle styles
        String style = (String) attribs.getAttribute(Attribute.STYLE.toString());

        if (style != null) {
            switch (style) {
                case "list-style-type: upper-alpha;":
                    numbering = Sink.NUMBERING_UPPER_ALPHA;
                    break;
                case "list-style-type: lower-alpha;":
                    numbering = Sink.NUMBERING_LOWER_ALPHA;
                    break;
                case "list-style-type: upper-roman;":
                    numbering = Sink.NUMBERING_UPPER_ROMAN;
                    break;
                case "list-style-type: lower-roman;":
                    numbering = Sink.NUMBERING_LOWER_ROMAN;
                    break;
                case "list-style-type: decimal;":
                    numbering = Sink.NUMBERING_DECIMAL;
                    break;
                default:
                    // ignore all other
            }
        }

        sink.numberedList(numbering, attribs);
        orderedListDepth++;
    }

    private void handlePStart(Sink sink, SinkEventAttributeSet attribs) {
        sink.paragraph(attribs);
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
    private void handlePreStart(SinkEventAttributeSet attribs, Sink sink) {
        verbatim();
        sink.verbatim(attribs);
    }

    private void handleSectionStart(Sink sink, SinkEventAttributeSet attribs) {
        emitHeadingSections(sectionLevel, sink, false);
        sink.section(++sectionLevel, attribs);
        this.headingLevel = sectionLevel;
    }

    private void handleHeadingStart(Sink sink, int level, SinkEventAttributeSet attribs) {
        emitHeadingSections(level, sink, true);
        sink.sectionTitle(level, attribs);
    }

    private void handleSectionEnd(Sink sink) {
        emitHeadingSections(sectionLevel, sink, false);
        sink.section_(sectionLevel--);
        this.headingLevel = sectionLevel;
    }

    private void handleTableStart(Sink sink, SinkEventAttributeSet attribs) {
        sink.table(attribs);
        String givenTableClass = (String) attribs.getAttribute(Attribute.CLASS.toString());
        boolean grid = false;
        if (givenTableClass != null
                && BODYTABLEBORDER_CLASS_PATTERN.matcher(givenTableClass).matches()) {
            grid = true;
        }

        sink.tableRows(null, grid);
    }
}
