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
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
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
 * Common base parser for XHTML5 (now <a href="https://html.spec.whatwg.org/multipage/#toc-the-xhtml-syntax">HTML Living standard, XML syntax</a>) elements and attributes.
 * 
 * @see <a href="https://html.spec.whatwg.org/multipage/introduction.html#history-2">HTML Living standard, history</a>
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

    /** If true, the next text event is at the beginning of a line inside a block element, i.e. after a block tag or a line break/end block tag. */
    protected boolean isBeginningOfLineInsideBlock = true;

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
        isBeginningOfLineInsideBlock = true;
        switch (elementName) {
            case "article":
                sink.article(attribs);
                break;
            case "nav":
                sink.navigation(attribs);
                break;
            case "aside":
                sink.sidebar(attribs);
                break;
            case "section":
                handleSectionStart(sink, attribs);
                break;
            case "h1":
                handleHeadingStart(sink, Sink.SECTION_LEVEL_1, attribs);
                break;
            case "h2":
                handleHeadingStart(sink, Sink.SECTION_LEVEL_2, attribs);
                break;
            case "h3":
                handleHeadingStart(sink, Sink.SECTION_LEVEL_3, attribs);
                break;
            case "h4":
                handleHeadingStart(sink, Sink.SECTION_LEVEL_4, attribs);
                break;
            case "h5":
                handleHeadingStart(sink, Sink.SECTION_LEVEL_5, attribs);
                break;
            case "h6":
                handleHeadingStart(sink, Sink.SECTION_LEVEL_6, attribs);
                break;
            case "header":
                sink.header(attribs);
                break;
            case "main":
                sink.content(attribs);
                break;
            case "footer":
                sink.footer(attribs);
                break;
            case "em":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.EMPHASIS);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "strong":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.STRONG);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "small":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.SMALL);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "s":
                /* deprecated line-through support */
                attribs.addAttributes(SinkEventAttributeSet.Semantics.LINE_THROUGH);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "cite":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.CITATION);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "q":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.QUOTE);
                sink.inline(attribs);
                break;
            case "dfn":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.DEFINITION);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "abbr":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.ABBREVIATION);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "i":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.ITALIC);
                sink.inline(attribs);
                break;
            case "b":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.BOLD);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "code":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.CODE);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "var":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.VARIABLE);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "samp":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.SAMPLE);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "kbd":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.KEYBOARD);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "sup":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.SUPERSCRIPT);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "sub":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.SUBSCRIPT);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "u":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.ANNOTATION);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "mark":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.HIGHLIGHT);
                sink.inline(attribs);
                break;
            case "ruby":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.RUBY);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "rb":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.RUBY_BASE);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "rt":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.RUBY_TEXT);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "rtc":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.RUBY_TEXT_CONTAINER);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "rp":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.RUBY_PARANTHESES);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "bdi":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.BIDIRECTIONAL_ISOLATION);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "bdo":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.BIDIRECTIONAL_OVERRIDE);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "span":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.PHRASE);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "ins":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.INSERT);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "del":
                attribs.addAttributes(SinkEventAttributeSet.Semantics.DELETE);
                sink.inline(attribs);
                isBeginningOfLineInsideBlock = false;
                break;
            case "p":
                handlePStart(sink, attribs);
                break;
            case "div":
                handleDivStart(attribs, sink);
                break;
            case "pre":
                handlePreStart(attribs, sink);
                break;
            case "ul":
                sink.list(attribs);
                break;
            case "ol":
                handleOLStart(sink, attribs);
                break;
            case "li":
                handleLIStart(sink, attribs);
                break;
            case "dl":
                sink.definitionList(attribs);
                break;
            case "dt":
                if (hasDefinitionListItem) {
                    // close previous listItem
                    sink.definitionListItem_();
                }
                sink.definitionListItem(attribs);
                hasDefinitionListItem = true;
                sink.definedTerm(attribs);
                break;
            case "dd":
                if (!hasDefinitionListItem) {
                    sink.definitionListItem(attribs);
                }
                sink.definition(attribs);
                break;
            case "figure":
                sink.figure(attribs);
                break;
            case "figcaption":
                sink.figureCaption(attribs);
                break;
            case "a":
                isBeginningOfLineInsideBlock = false;
                handleAStart(sink, attribs);
                break;
            case "table":
                handleTableStart(sink, attribs);
                break;
            case "tr":
                sink.tableRow(attribs);
                break;
            case "th":
                sink.tableHeaderCell(attribs);
                break;
            case "td":
                sink.tableCell(attribs);
                break;
            case "caption":
                sink.tableCaption(attribs);
                break;
            case "br":
                sink.lineBreak(attribs);
                break;
            case "wbr":
                sink.lineBreakOpportunity(attribs);
                break;
            case "hr":
                sink.horizontalRule(attribs);
                break;
            case "img":
                isBeginningOfLineInsideBlock = false;
                handleImgStart(sink, attribs);
                break;
            case "blockquote":
                sink.blockquote(attribs);
                break;
            case "script":
            case "style":
                handleUnknown(elementName, attribs, sink, TAG_TYPE_START);
                scriptBlock = true;
                break;
            default:
                if (UNMATCHED_XHTML5_ELEMENTS.contains(elementName)) {
                    handleUnknown(elementName, attribs, sink, TAG_TYPE_START);
                } else if (UNMATCHED_XHTML5_SIMPLE_ELEMENTS.contains(elementName)) {
                    handleUnknown(elementName, attribs, sink, TAG_TYPE_SIMPLE);
                } else {
                    visited = false;
                }
                break;
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
        isBeginningOfLineInsideBlock = true;

        switch (elementName) {
            case "p":
                sink.paragraph_();
                break;
            case "div":
                handleDivEnd(sink);
                break;
            case "pre":
                verbatim_();
                sink.verbatim_();
                break;
            case "ul":
                sink.list_();
                break;
            case "ol":
                sink.numberedList_();
                orderedListDepth--;
                break;
            case "li":
                handleListItemEnd(sink);
                break;
            case "dl":
                if (hasDefinitionListItem) {
                    sink.definitionListItem_();
                    hasDefinitionListItem = false;
                }
                sink.definitionList_();
                break;
            case "dt":
                sink.definedTerm_();
                break;
            case "dd":
                sink.definition_();
                sink.definitionListItem_();
                hasDefinitionListItem = false;
                break;
            case "figure":
                sink.figure_();
                break;
            case "figcaption":
                sink.figureCaption_();
                break;
            case "a":
                isBeginningOfLineInsideBlock = false;
                handleAEnd(sink);
                break;
            case "em":
            case "strong":
            case "small":
            case "s":
            case "cite":
            case "q":
            case "dfn":
            case "abbr":
            case "i":
            case "b":
            case "code":
            case "var":
            case "samp":
            case "kbd":
            case "sup":
            case "sub":
            case "u":
            case "mark":
            case "ruby":
            case "rb":
            case "rt":
            case "rtc":
            case "rp":
            case "bdi":
            case "bdo":
            case "span":
            case "ins":
            case "del":
                sink.inline_();
                isBeginningOfLineInsideBlock = false;
                break;

            // ----------------------------------------------------------------------
            // Tables
            // ----------------------------------------------------------------------

            case "table":
                sink.tableRows_();
                sink.table_();
                break;
            case "tr":
                sink.tableRow_();
                break;
            case "th":
                sink.tableHeaderCell_();
                break;
            case "td":
                sink.tableCell_();
                break;
            case "caption":
                sink.tableCaption_();
                break;
            case "article":
                sink.article_();
                break;
            case "nav":
                sink.navigation_();
                break;
            case "aside":
                sink.sidebar_();
                break;
            case "section":
                handleSectionEnd(sink);
                break;
            case "h1":
                sink.sectionTitle1_();
                break;
            case "h2":
                sink.sectionTitle2_();
                break;
            case "h3":
                sink.sectionTitle3_();
                break;
            case "h4":
                sink.sectionTitle4_();
                break;
            case "h5":
                sink.sectionTitle5_();
                break;
            case "h6":
                sink.sectionTitle6_();
                break;
            case "header":
                sink.header_();
                break;
            case "main":
                sink.content_();
                break;
            case "footer":
                sink.footer_();
                break;
            case "img":
                isBeginningOfLineInsideBlock = false;
                break;
            case "blockquote":
                sink.blockquote_();
                break;
            case "script":
            case "style":
                handleUnknown(elementName, attribs, sink, TAG_TYPE_END);
                scriptBlock = false;
                break;
            default:
                if (UNMATCHED_XHTML5_ELEMENTS.contains(elementName)) {
                    handleUnknown(elementName, attribs, sink, TAG_TYPE_END);
                } else {
                    visited = false;
                }
                break;
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

    @Override
    protected void handleText(XmlPullParser parser, Sink sink) throws XmlPullParserException {
        String text = getText(parser);

        if (!inVerbatim && text != null) {
            // do special whitespace processing as outlined in
            // https://developer.mozilla.org/en-US/docs/Web/CSS/Guides/Text/Whitespace
            if (isBeginningOfLineInsideBlock) {
                // normalize linebreaks
                processInsignificantLineBreaks(sink, text);
                // trim leading whitespace from text being emitted
                // https://developer.mozilla.org/en-US/docs/Web/CSS/Guides/Text/Whitespace#trimming_and_positioning
                String regex = "^\\s+";
                text = text.replaceAll(regex, "");
            }

            // assume white-space-collapse: collapse for all non-verbatim text (outside of <pre>)
            text = collapseWhitespace(text);
        }
        if ((text != null && !text.isEmpty()) && !isScriptBlock()) {
            sink.text(text);
            isBeginningOfLineInsideBlock = false;
        }
    }

    /**
     * Process all line-breaks in the given text which are not significant for the output, i.e. all line-breaks which are not within a verbatim block and
     * are at the beginning of the given text.
     * In addition it emits information about the whitespace characters following the line-breaks as they may be relevant for the output (e.g. for indentation).
     *
     * @param sink the sink to receive the events.
     * @param text the text to process.
     */
    protected void processInsignificantLineBreaks(Sink sink, String text) {
        CharacterIterator it = new StringCharacterIterator(text.replaceAll("\\r\\n?", "\n"));

        boolean wasNewLine = false;
        int indentLevel = 0;
        //
        while (it.current() != CharacterIterator.DONE) {
            char c = it.current();
            if (c == '\n') {
                if (wasNewLine) {
                    sink.markupLineBreak(indentLevel);
                }
                indentLevel = 0;
                wasNewLine = true;
            } else if (Character.isWhitespace(c)) {
                indentLevel++;
            } else {
                // once non-whitespace character is reached we assume everything following is relevant and emitted
                // within the text event
                break;
            }
            it.next();
        }
        if (wasNewLine) {
            // if the text ends with a newline, we need to emit the last line break
            sink.markupLineBreak(indentLevel);
        }
    }

    /**
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/Guides/Text/Whitespace#how_does_css_process_whitespace">How does CSS process whitespace?</a>
     * @see <a href="https://drafts.csswg.org/css-text-4/#white-space-processing">CSS Text Module Level 4 - White Space Processing</a>
     *
     * @param text
     * @return
     */
    private static String collapseWhitespace(String text) {
        // replace all sequences of whitespace characters with a single space (this includes newlines, tabs, etc.)
        return text.replaceAll("\\s+", " ");
    }

    @Override
    protected void handleComment(XmlPullParser parser, Sink sink) throws XmlPullParserException {
        isBeginningOfLineInsideBlock = false;
        String text = getText(parser);

        if ("PB".equals(text.trim())) {
            sink.pageBreak();
        } else {
            if (isEmitComments()) {
                sink.comment(text);
            }
        }
    }

    @Override
    protected void handleCdsect(XmlPullParser parser, Sink sink) throws XmlPullParserException {
        isBeginningOfLineInsideBlock = false;
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
            if (hashIndex != -1
                    && !DoxiaUtils.isExternalLink(href)
                    && !"external".equals(attribs.getAttribute(Attribute.REL.toString()))) {
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
            if (id == null) {
                // although the "name" attribute is obsolete in HTML5, it is still allowed
                // (https://www.w3.org/TR/html5-diff/#obsolete-attributes)
                id = (String) attribs.getAttribute(Attribute.NAME.toString());
            }
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
