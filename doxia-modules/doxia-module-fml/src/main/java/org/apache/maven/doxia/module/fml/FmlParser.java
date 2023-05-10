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
package org.apache.maven.doxia.module.fml;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.swing.text.html.HTML.Attribute;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.macro.MacroRequest;
import org.apache.maven.doxia.macro.manager.MacroNotFoundException;
import org.apache.maven.doxia.module.fml.model.Faq;
import org.apache.maven.doxia.module.fml.model.Faqs;
import org.apache.maven.doxia.module.fml.model.Part;
import org.apache.maven.doxia.parser.AbstractXmlParser;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.impl.Xhtml5BaseSink;
import org.apache.maven.doxia.util.DoxiaUtils;
import org.apache.maven.doxia.util.HtmlTools;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parse a fml model and emit events into the specified doxia Sink.
 *
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @author ltheussl
 * @since 1.0
 */
@Singleton
@Named("fml")
public class FmlParser extends AbstractXmlParser implements FmlMarkup {
    private static final Logger LOGGER = LoggerFactory.getLogger(FmlParser.class);

    /** Collect a faqs model. */
    private Faqs faqs;

    /** Collect a part. */
    private Part currentPart;

    /** Collect a single faq. */
    private Faq currentFaq;

    /** Used to collect text events. */
    private StringBuilder buffer;

    /** The source content of the input reader. Used to pass into macros. */
    private String sourceContent;

    /** A macro name. */
    private String macroName;

    /** The macro parameters. */
    private Map<String, Object> macroParameters = new HashMap<>();

    /** {@inheritDoc} */
    public void parse(Reader source, Sink sink, String reference) throws ParseException {
        this.faqs = null;
        this.sourceContent = null;
        init();

        try (Reader reader = source) {
            StringWriter contentWriter = new StringWriter();
            IOUtils.copy(reader, contentWriter);
            sourceContent = contentWriter.toString();
        } catch (IOException ex) {
            throw new ParseException("Error reading the input source", ex);
        }

        try {
            Reader tmp = new StringReader(sourceContent);

            this.faqs = new Faqs();

            // this populates faqs
            super.parse(tmp, sink, reference);

            writeFaqs(sink);
        } finally {
            this.faqs = null;
            this.sourceContent = null;
            setSecondParsing(false);
            init();
        }
    }

    /** {@inheritDoc} */
    protected void handleStartTag(XmlPullParser parser, Sink sink)
            throws XmlPullParserException, MacroExecutionException {
        if (parser.getName().equals(FAQS_TAG.toString())) {
            String title = parser.getAttributeValue(null, "title");

            if (title != null) {
                faqs.setTitle(title);
            }

            String toplink = parser.getAttributeValue(null, "toplink");

            if (toplink != null) {
                if (toplink.equalsIgnoreCase("true")) {
                    faqs.setToplink(true);
                } else {
                    faqs.setToplink(false);
                }
            }
        } else if (parser.getName().equals(PART_TAG.toString())) {
            currentPart = new Part();

            currentPart.setId(parser.getAttributeValue(null, Attribute.ID.toString()));

            if (currentPart.getId() == null) {
                throw new XmlPullParserException("id attribute required for <part> at: (" + parser.getLineNumber() + ":"
                        + parser.getColumnNumber() + ")");
            } else if (!DoxiaUtils.isValidId(currentPart.getId())) {
                String linkAnchor = DoxiaUtils.encodeId(currentPart.getId());

                LOGGER.debug("Modified invalid link '{}' to '{}'", currentPart.getId(), linkAnchor);

                currentPart.setId(linkAnchor);
            }
        } else if (parser.getName().equals(TITLE.toString())) {
            buffer = new StringBuilder();
            buffer.append(LESS_THAN).append(parser.getName()).append(GREATER_THAN);
        } else if (parser.getName().equals(FAQ_TAG.toString())) {
            currentFaq = new Faq();

            currentFaq.setId(parser.getAttributeValue(null, Attribute.ID.toString()));

            if (currentFaq.getId() == null) {
                throw new XmlPullParserException("id attribute required for <faq> at: (" + parser.getLineNumber() + ":"
                        + parser.getColumnNumber() + ")");
            } else if (!DoxiaUtils.isValidId(currentFaq.getId())) {
                String linkAnchor = DoxiaUtils.encodeId(currentFaq.getId());

                LOGGER.debug("Modified invalid link '{}' to '{}'", currentFaq.getId(), linkAnchor);

                currentFaq.setId(linkAnchor);
            }
        } else if (parser.getName().equals(QUESTION_TAG.toString())) {
            buffer = new StringBuilder();
            buffer.append(LESS_THAN).append(parser.getName()).append(GREATER_THAN);
        } else if (parser.getName().equals(ANSWER_TAG.toString())) {
            buffer = new StringBuilder();
            buffer.append(LESS_THAN).append(parser.getName()).append(GREATER_THAN);

        }

        // ----------------------------------------------------------------------
        // Macro
        // ----------------------------------------------------------------------

        else if (parser.getName().equals(MACRO_TAG.toString())) {
            handleMacroStart(parser);
        } else if (parser.getName().equals(PARAM.toString())) {
            handleParamStart(parser, sink);
        } else if (buffer != null) {
            buffer.append(LESS_THAN).append(parser.getName());

            int count = parser.getAttributeCount();

            for (int i = 0; i < count; i++) {
                buffer.append(SPACE).append(parser.getAttributeName(i));

                buffer.append(EQUAL).append(QUOTE);

                // TODO: why are attribute values HTML-encoded?
                buffer.append(HtmlTools.escapeHTML(parser.getAttributeValue(i)));

                buffer.append(QUOTE);
            }

            buffer.append(GREATER_THAN);
        }
    }

    /** {@inheritDoc} */
    protected void handleEndTag(XmlPullParser parser, Sink sink)
            throws XmlPullParserException, MacroExecutionException {
        if (parser.getName().equals(FAQS_TAG.toString())) {
            // Do nothing
            return;
        } else if (parser.getName().equals(PART_TAG.toString())) {
            faqs.addPart(currentPart);

            currentPart = null;
        } else if (parser.getName().equals(FAQ_TAG.toString())) {
            if (currentPart == null) {
                throw new XmlPullParserException(
                        "Missing <part>  at: (" + parser.getLineNumber() + ":" + parser.getColumnNumber() + ")");
            }

            currentPart.addFaq(currentFaq);

            currentFaq = null;
        } else if (parser.getName().equals(QUESTION_TAG.toString())) {
            if (currentFaq == null) {
                throw new XmlPullParserException(
                        "Missing <faq> at: (" + parser.getLineNumber() + ":" + parser.getColumnNumber() + ")");
            }

            buffer.append(LESS_THAN).append(SLASH).append(parser.getName()).append(GREATER_THAN);

            currentFaq.setQuestion(buffer.toString());

            buffer = null;
        } else if (parser.getName().equals(ANSWER_TAG.toString())) {
            if (currentFaq == null) {
                throw new XmlPullParserException(
                        "Missing <faq> at: (" + parser.getLineNumber() + ":" + parser.getColumnNumber() + ")");
            }

            buffer.append(LESS_THAN).append(SLASH).append(parser.getName()).append(GREATER_THAN);

            currentFaq.setAnswer(buffer.toString());

            buffer = null;
        } else if (parser.getName().equals(TITLE.toString())) {
            if (currentPart == null) {
                throw new XmlPullParserException(
                        "Missing <part> at: (" + parser.getLineNumber() + ":" + parser.getColumnNumber() + ")");
            }

            buffer.append(LESS_THAN).append(SLASH).append(parser.getName()).append(GREATER_THAN);

            currentPart.setTitle(buffer.toString());

            buffer = null;
        }

        // ----------------------------------------------------------------------
        // Macro
        // ----------------------------------------------------------------------

        else if (parser.getName().equals(MACRO_TAG.toString())) {
            handleMacroEnd(buffer);
        } else if (parser.getName().equals(PARAM.toString())) {
            if (!(macroName != null && !macroName.isEmpty())) {
                handleUnknown(parser, sink, TAG_TYPE_END);
            }
        } else if (buffer != null) {
            if (buffer.length() > 0 && buffer.charAt(buffer.length() - 1) == SPACE) {
                buffer.deleteCharAt(buffer.length() - 1);
            }

            buffer.append(LESS_THAN).append(SLASH).append(parser.getName()).append(GREATER_THAN);
        }
    }

    /** {@inheritDoc} */
    protected void handleText(XmlPullParser parser, Sink sink) throws XmlPullParserException {
        if (buffer != null) {
            buffer.append(parser.getText());
        }
        // only significant text content in fml files is in <question>, <answer> or <title>
    }

    /** {@inheritDoc} */
    protected void handleCdsect(XmlPullParser parser, Sink sink) throws XmlPullParserException {
        String cdSection = parser.getText();

        if (buffer != null) {
            buffer.append(LESS_THAN)
                    .append(BANG)
                    .append(LEFT_SQUARE_BRACKET)
                    .append(CDATA)
                    .append(LEFT_SQUARE_BRACKET)
                    .append(cdSection)
                    .append(RIGHT_SQUARE_BRACKET)
                    .append(RIGHT_SQUARE_BRACKET)
                    .append(GREATER_THAN);
        } else {
            sink.text(cdSection);
        }
    }

    /** {@inheritDoc} */
    protected void handleComment(XmlPullParser parser, Sink sink) throws XmlPullParserException {
        String comment = parser.getText();

        if (buffer != null) {
            buffer.append(LESS_THAN)
                    .append(BANG)
                    .append(MINUS)
                    .append(MINUS)
                    .append(comment)
                    .append(MINUS)
                    .append(MINUS)
                    .append(GREATER_THAN);
        } else {
            if (isEmitComments()) {
                sink.comment(comment);
            }
        }
    }

    /** {@inheritDoc} */
    protected void handleEntity(XmlPullParser parser, Sink sink) throws XmlPullParserException {
        if (buffer != null) {
            if (parser.getText() != null) {
                String text = parser.getText();

                // parser.getText() returns the entity replacement text
                // (&lt; -> <), need to re-escape them
                if (text.length() == 1) {
                    text = HtmlTools.escapeHTML(text);
                }

                buffer.append(text);
            }
        } else {
            super.handleEntity(parser, sink);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void init() {
        super.init();

        this.currentFaq = null;
        this.currentPart = null;
        this.buffer = null;
        this.macroName = null;
        this.macroParameters = null;
    }

    /**
     * TODO import from XdocParser, probably need to be generic.
     *
     * @param parser not null
     * @throws MacroExecutionException if any
     */
    private void handleMacroStart(XmlPullParser parser) throws MacroExecutionException {
        if (!isSecondParsing()) {
            macroName = parser.getAttributeValue(null, Attribute.NAME.toString());

            if (macroParameters == null) {
                macroParameters = new HashMap<>();
            }

            if (macroName == null || macroName.isEmpty()) {
                throw new MacroExecutionException("The '" + Attribute.NAME.toString() + "' attribute for the '"
                        + MACRO_TAG.toString() + "' tag is required.");
            }
        }
    }

    /**
     * TODO import from XdocParser, probably need to be generic.
     *
     * @param buffer not null
     * @throws MacroExecutionException if any
     */
    private void handleMacroEnd(StringBuilder buffer) throws MacroExecutionException {
        if (!isSecondParsing()) {
            if (macroName != null && !macroName.isEmpty()) {
                MacroRequest request = new MacroRequest(sourceContent, new FmlParser(), macroParameters, getBasedir());

                try {
                    StringWriter sw = new StringWriter();
                    Xhtml5BaseSink sink = new Xhtml5BaseSink(sw);
                    executeMacro(macroName, request, sink);
                    sink.close();
                    buffer.append(sw.toString());
                } catch (MacroNotFoundException me) {
                    throw new MacroExecutionException("Macro not found: " + macroName, me);
                }
            }
        }

        // Reinit macro
        macroName = null;
        macroParameters = null;
    }

    /**
     * TODO import from XdocParser, probably need to be generic.
     *
     * @param parser not null
     * @param sink not null
     * @throws MacroExecutionException if any
     */
    private void handleParamStart(XmlPullParser parser, Sink sink) throws MacroExecutionException {
        if (!isSecondParsing()) {
            if (macroName != null && !macroName.isEmpty()) {
                String paramName = parser.getAttributeValue(null, Attribute.NAME.toString());
                String paramValue = parser.getAttributeValue(null, Attribute.VALUE.toString());

                if ((paramName == null || paramName.isEmpty()) || (paramValue == null || paramValue.isEmpty())) {
                    throw new MacroExecutionException("'" + Attribute.NAME.toString()
                            + "' and '" + Attribute.VALUE.toString() + "' attributes for the '" + PARAM.toString()
                            + "' tag are required inside the '" + MACRO_TAG.toString() + "' tag.");
                }

                macroParameters.put(paramName, paramValue);
            } else {
                // param tag from non-macro object, see MSITE-288
                handleUnknown(parser, sink, TAG_TYPE_START);
            }
        }
    }

    /**
     * Writes the faqs to the specified sink.
     *
     * @param sink The sink to consume the event.
     * @throws ParseException if something goes wrong.
     */
    private void writeFaqs(Sink sink) throws ParseException {
        FmlContentParser xdocParser = new FmlContentParser();

        sink.head();
        sink.title();
        sink.text(faqs.getTitle());
        sink.title_();
        sink.head_();

        sink.body();
        sink.section1();
        sink.anchor("top");
        sink.anchor_();
        sink.sectionTitle1();
        sink.text(faqs.getTitle());
        sink.sectionTitle1_();

        // ----------------------------------------------------------------------
        // Write summary
        // ----------------------------------------------------------------------

        for (Part part : faqs.getParts()) {
            if (StringUtils.isNotEmpty(part.getTitle())) {
                sink.paragraph();
                sink.inline(SinkEventAttributeSet.Semantics.BOLD);
                xdocParser.parse(part.getTitle(), sink);
                sink.inline_();
                sink.paragraph_();
            }

            sink.numberedList(Sink.NUMBERING_DECIMAL);

            for (Faq faq : part.getFaqs()) {
                sink.numberedListItem();
                sink.link("#" + faq.getId());

                if (StringUtils.isNotEmpty(faq.getQuestion())) {
                    xdocParser.parse(faq.getQuestion(), sink);
                } else {
                    throw new ParseException("Missing <question> for FAQ '" + faq.getId() + "'");
                }

                sink.link_();
                sink.numberedListItem_();
            }

            sink.numberedList_();
        }

        sink.section1_();

        // ----------------------------------------------------------------------
        // Write content
        // ----------------------------------------------------------------------

        for (Part part : faqs.getParts()) {
            if (StringUtils.isNotEmpty(part.getTitle())) {
                sink.section1();
                sink.anchor(part.getId());
                sink.anchor_();
                sink.sectionTitle1();
                xdocParser.parse(part.getTitle(), sink);
                sink.sectionTitle1_();
            }

            sink.definitionList();

            for (Iterator<Faq> faqIterator = part.getFaqs().iterator(); faqIterator.hasNext(); ) {
                Faq faq = faqIterator.next();

                sink.anchor(faq.getId());
                sink.anchor_();

                sink.definedTerm();

                if (StringUtils.isNotEmpty(faq.getQuestion())) {
                    xdocParser.parse(faq.getQuestion(), sink);
                } else {
                    throw new ParseException("Missing <question> for FAQ '" + faq.getId() + "'");
                }

                sink.definedTerm_();

                sink.definition();

                if (StringUtils.isNotEmpty(faq.getAnswer())) {
                    xdocParser.parse(faq.getAnswer(), sink);
                } else {
                    throw new ParseException("Missing <answer> for FAQ '" + faq.getId() + "'");
                }

                if (faqs.isToplink()) {
                    writeTopLink(sink);
                }

                if (faqIterator.hasNext()) {
                    sink.horizontalRule();
                }

                sink.definition_();
            }

            sink.definitionList_();

            if (StringUtils.isNotEmpty(part.getTitle())) {
                sink.section1_();
            }
        }

        sink.body_();
    }

    /**
     * Writes a toplink element.
     *
     * @param sink The sink to consume the event.
     */
    private void writeTopLink(Sink sink) {
        SinkEventAttributeSet atts = new SinkEventAttributeSet();
        atts.addAttribute(SinkEventAttributeSet.STYLE, "text-align: right;");
        sink.paragraph(atts);
        sink.link("#top");
        sink.text("[top]");
        sink.link_();
        sink.paragraph_();
    }
}
