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
package org.apache.maven.doxia.module.xhtml5;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Attribute;

import java.io.Writer;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.impl.Xhtml5BaseSink;
import org.apache.maven.doxia.util.HtmlTools;

/**
 * <a href="https://www.w3.org/TR/html52/">XHTML 5.2</a> sink implementation.
 */
public class Xhtml5Sink extends Xhtml5BaseSink implements Xhtml5Markup {
    // ----------------------------------------------------------------------
    // Instance fields
    // ----------------------------------------------------------------------

    private String encoding;

    private String languageId;

    /** An indication on if we're inside a head title. */
    private boolean headTitleFlag;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    /**
     * Constructor, initialize the Writer.
     *
     * @param writer not null writer to write the result.
     */
    protected Xhtml5Sink(Writer writer) {
        super(writer);
    }

    /**
     * Constructor, initialize the Writer and tells which encoding is used.
     *
     * @param writer not null writer to write the result.
     * @param encoding the encoding used, that should be written to the generated HTML content
     * if not <code>null</code>.
     */
    protected Xhtml5Sink(Writer writer, String encoding) {
        super(writer);

        this.encoding = encoding;
    }

    /**
     * Constructor, initialize the Writer and tells which encoding and languageId are used.
     *
     * @param writer not null writer to write the result.
     * @param encoding the encoding used, that should be written to the generated HTML content
     * if not <code>null</code>.
     * @param languageId language identifier for the root element as defined by
     * <a href="ftp://ftp.isi.edu/in-notes/bcp/bcp47.txt">IETF BCP 47</a>, Tags for the Identification of Languages;
     * in addition, the empty string may be specified.
     */
    protected Xhtml5Sink(Writer writer, String encoding, String languageId) {
        this(writer, encoding);

        this.languageId = languageId;
    }

    @Override
    public void head(SinkEventAttributes attributes) {
        init();

        setHeadFlag(true);

        write("<!DOCTYPE html>");

        MutableAttributeSet atts = new SinkEventAttributeSet();
        if (attributes != null) {
            atts.addAttributes(attributes);
        }
        atts.addAttribute("xmlns", XHTML5_NAMESPACE);

        if (languageId != null) {
            atts.addAttribute(Attribute.LANG.toString(), languageId);
            atts.addAttribute("xml:lang", languageId);
        }

        writeStartTag(HTML, atts);

        writeStartTag(HEAD);
    }

    /**
     * {@inheritDoc}
     */
    public void head_() {
        if (!isHeadTitleFlag()) {
            // The content of element type "head" must match
            // "((script|style|meta|link|object|isindex)*,
            //  ((title,(script|style|meta|link|object|isindex)*,
            //  (base,(script|style|meta|link|object|isindex)*)?)|(base,(script|style|meta|link|object|isindex)*,
            //  (title,(script|style|meta|link|object|isindex)*))))"
            writeStartTag(TITLE);
            writeEndTag(TITLE);
        }

        setHeadFlag(false);
        setHeadTitleFlag(false);

        if (encoding != null) {
            write("<meta charset=\"" + encoding + "\"/>");
        }

        writeEndTag(HEAD);
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void title(SinkEventAttributes attributes) {
        setHeadTitleFlag(true);

        writeStartTag(TITLE, attributes);
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void title_() {
        content(getTextBuffer().toString());

        writeEndTag(TITLE);

        resetTextBuffer();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.swing.text.html.HTML.Tag#META
     */
    public void author_() {
        if (getTextBuffer().length() > 0) {
            MutableAttributeSet att = new SinkEventAttributeSet();
            att.addAttribute(Attribute.NAME, "author");
            String text = HtmlTools.escapeHTML(getTextBuffer().toString());
            // hack: un-escape numerical entities that have been escaped above
            // note that numerical entities should really be added as one unicode character in the first place
            text = StringUtils.replace(text, "&amp;#", "&#");
            att.addAttribute(Attribute.CONTENT, text);

            writeSimpleTag(META, att);

            resetTextBuffer();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.swing.text.html.HTML.Tag#META
     */
    public void date_() {
        if (getTextBuffer().length() > 0) {
            MutableAttributeSet att = new SinkEventAttributeSet();
            att.addAttribute(Attribute.NAME, "date");
            att.addAttribute(Attribute.CONTENT, getTextBuffer().toString());

            writeSimpleTag(META, att);

            resetTextBuffer();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.swing.text.html.HTML.Tag#BODY
     */
    @Override
    public void body(SinkEventAttributes attributes) {
        writeStartTag(BODY, attributes);
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.swing.text.html.HTML.Tag#BODY
     * @see javax.swing.text.html.HTML.Tag#HTML
     */
    public void body_() {
        writeEndTag(BODY);

        writeEndTag(HTML);

        flush();

        init();
    }

    // ----------------------------------------------------------------------
    // Public protected methods
    // ----------------------------------------------------------------------

    /**
     * <p>Setter for the field <code>headTitleFlag</code>.</p>
     *
     * @param headTitleFlag an header title flag.
     * @since 1.1
     */
    protected void setHeadTitleFlag(boolean headTitleFlag) {
        this.headTitleFlag = headTitleFlag;
    }

    /**
     * <p>isHeadTitleFlag.</p>
     *
     * @return the current headTitleFlag.
     * @since 1.1
     */
    protected boolean isHeadTitleFlag() {
        return this.headTitleFlag;
    }
}
