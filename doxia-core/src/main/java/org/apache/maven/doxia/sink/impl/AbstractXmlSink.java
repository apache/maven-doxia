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
package org.apache.maven.doxia.sink.impl;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Tag;

import java.util.Objects;

import org.apache.maven.doxia.markup.HtmlTag;
import org.apache.maven.doxia.markup.XmlMarkup;

/**
 * An abstract <code>Sink</code> for xml markup syntax.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @since 1.0
 */
public abstract class AbstractXmlSink extends SinkAdapter implements XmlMarkup {
    /** Default namespace prepended to all tags */
    private String nameSpace;

    private boolean firstTag = true;

    private boolean insertNewline = true;

    /**
     * <p>Setter for the field <code>insertNewline</code>.</p>
     *
     * @param insertNewline a boolean.
     */
    public void setInsertNewline(boolean insertNewline) {
        this.insertNewline = insertNewline;
    }

    /**
     * Sets the default namespace that is prepended to all tags written by this sink.
     *
     * @param ns the default namespace.
     * @since 1.1
     */
    public void setNameSpace(String ns) {
        this.nameSpace = ns;
    }

    /**
     * Return the default namespace that is prepended to all tags written by this sink.
     *
     * @return the current default namespace.
     * @since 1.1
     */
    public String getNameSpace() {
        return this.nameSpace;
    }

    /**
     * Starts a Tag. For instance:
     * <pre>
     * &lt;tag&gt;
     * </pre>
     *
     * @param t a non null tag
     * @see #writeStartTag(HtmlTag, javax.swing.text.MutableAttributeSet)
     * @deprecated use {@link #writeStartTag(HtmlTag)}; the {@code javax.swing} tag type goes in 2.3.0.
     */
    @Deprecated
    protected void writeStartTag(Tag t) {
        writeStartTag(t, null);
    }

    /**
     * Starts a Tag with attributes. For instance:
     * <pre>
     * &lt;tag attName="attValue"&gt;
     * </pre>
     *
     * @param t a non null tag.
     * @param att a set of attributes. May be null.
     * @see #writeStartTag(HtmlTag, javax.swing.text.MutableAttributeSet, boolean)
     * @deprecated use {@link #writeStartTag(HtmlTag, javax.swing.text.MutableAttributeSet)}; the
     *      {@code javax.swing} tag type goes in 2.3.0.
     */
    @Deprecated
    protected void writeStartTag(Tag t, MutableAttributeSet att) {
        writeStartTag(t, att, false);
    }

    /**
     * Starts a Tag with attributes. For instance:
     * <pre>
     * &lt;tag attName="attValue"&gt;
     * </pre>
     *
     * @param t a non null tag.
     * @param att a set of attributes. May be null.
     * @param isEmptyElement boolean to write as tag for an <a href="https://www.w3.org/TR/xml/#d0e2480">empty element</a>.
     * @deprecated use {@link #writeStartTag(HtmlTag, javax.swing.text.MutableAttributeSet, boolean)}; the
     *      {@code javax.swing} tag type goes in 2.3.0.
     */
    @Deprecated
    protected void writeStartTag(Tag t, MutableAttributeSet att, boolean isEmptyElement) {
        writeStartTag(toHtmlTag(t), att, isEmptyElement);
    }

    /**
     * Starts a Tag. For instance:
     * <pre>
     * &lt;tag&gt;
     * </pre>
     *
     * @param t a non null tag
     * @see #writeStartTag(HtmlTag, javax.swing.text.MutableAttributeSet)
     * @since 2.2.0
     */
    protected void writeStartTag(HtmlTag t) {
        writeStartTag(t, null);
    }

    /**
     * Starts a Tag with attributes. For instance:
     * <pre>
     * &lt;tag attName="attValue"&gt;
     * </pre>
     *
     * @param t a non null tag.
     * @param att a set of attributes. May be null.
     * @see #writeStartTag(HtmlTag, javax.swing.text.MutableAttributeSet, boolean)
     * @since 2.2.0
     */
    protected void writeStartTag(HtmlTag t, MutableAttributeSet att) {
        writeStartTag(t, att, false);
    }

    /**
     * Starts a Tag with attributes. For instance:
     * <pre>
     * &lt;tag attName="attValue"&gt;
     * </pre>
     *
     * @param t a non null tag.
     * @param att a set of attributes. May be null.
     * @param isEmptyElement boolean to write as tag for an <a href="https://www.w3.org/TR/xml/#d0e2480">empty element</a>.
     * @since 2.2.0
     */
    protected void writeStartTag(HtmlTag t, MutableAttributeSet att, boolean isEmptyElement) {
        Objects.requireNonNull(t, "t cannot be null");

        StringBuilder sb = new StringBuilder();

        if (insertNewline && t.isBlock() && !firstTag) {
            sb.append(EOL);
        }
        firstTag = false;

        sb.append(LESS_THAN);

        if (nameSpace != null) {
            sb.append(nameSpace).append(':');
        }

        sb.append(t.toString());

        sb.append(SinkUtils.getAttributeString(att));

        if (isEmptyElement) {
            sb.append(SPACE).append(SLASH);
        }

        sb.append(GREATER_THAN);

        write(sb.toString());
    }

    /**
     * Writes a system EOL.
     *
     * @since 1.1
     */
    protected void writeEOL() {
        write(EOL);
    }

    /**
     * Ends a Tag without writing an EOL. For instance: <pre>&lt;/tag&gt;</pre>.
     *
     * @param t a tag.
     * @deprecated use {@link #writeEndTag(HtmlTag)}; the {@code javax.swing} tag type goes in 2.3.0.
     */
    @Deprecated
    protected void writeEndTag(Tag t) {
        writeEndTag(toHtmlTag(t));
    }

    /**
     * Ends a Tag without writing an EOL. For instance: <pre>&lt;/tag&gt;</pre>.
     *
     * @param t a tag.
     * @since 2.2.0
     */
    protected void writeEndTag(HtmlTag t) {
        Objects.requireNonNull(t, "t cannot be null");

        StringBuilder sb = new StringBuilder();
        sb.append(LESS_THAN);
        sb.append(SLASH);

        if (nameSpace != null) {
            sb.append(nameSpace).append(':');
        }

        sb.append(t.toString());
        sb.append(GREATER_THAN);

        write(sb.toString());
    }

    /**
     * Starts a simple Tag. For instance:
     * <pre>
     * &lt;tag /&gt;
     * </pre>
     *
     * @param t a non null tag
     * @see #writeSimpleTag(HtmlTag, javax.swing.text.MutableAttributeSet)
     * @deprecated use {@link #writeSimpleTag(HtmlTag)}; the {@code javax.swing} tag type goes in 2.3.0.
     */
    @Deprecated
    protected void writeSimpleTag(Tag t) {
        writeSimpleTag(t, null);
    }

    /**
     * Starts a simple Tag with attributes. For instance:
     * <pre>
     * &lt;tag attName="attValue" /&gt;
     * </pre>
     *
     * @param t a non null tag.
     * @param att a set of attributes. May be null.
     * @see #writeStartTag(HtmlTag, javax.swing.text.MutableAttributeSet, boolean)
     * @deprecated use {@link #writeSimpleTag(HtmlTag, javax.swing.text.MutableAttributeSet)}; the
     *      {@code javax.swing} tag type goes in 2.3.0.
     */
    @Deprecated
    protected void writeSimpleTag(Tag t, MutableAttributeSet att) {
        writeStartTag(t, att, true);
    }

    /**
     * Starts a simple Tag. For instance:
     * <pre>
     * &lt;tag /&gt;
     * </pre>
     *
     * @param t a non null tag
     * @see #writeSimpleTag(HtmlTag, javax.swing.text.MutableAttributeSet)
     * @since 2.2.0
     */
    protected void writeSimpleTag(HtmlTag t) {
        writeSimpleTag(t, null);
    }

    /**
     * Starts a simple Tag with attributes. For instance:
     * <pre>
     * &lt;tag attName="attValue" /&gt;
     * </pre>
     *
     * @param t a non null tag.
     * @param att a set of attributes. May be null.
     * @see #writeStartTag(HtmlTag, javax.swing.text.MutableAttributeSet, boolean)
     * @since 2.2.0
     */
    protected void writeSimpleTag(HtmlTag t, MutableAttributeSet att) {
        writeStartTag(t, att, true);
    }

    /**
     * Returns the Doxia tag of the same name, falling back to a tag that answers exactly as the
     * given one does, so that a tag Doxia does not declare keeps its own block value.
     */
    private static HtmlTag toHtmlTag(Tag t) {
        Objects.requireNonNull(t, "t cannot be null");

        HtmlTag known = HtmlTag.valueOf(t.toString());

        return known != null ? known : new HtmlTag(t.toString(), t.isBlock());
    }

    /**
     * Write a text to the sink.
     *
     * @param text the given text to write
     */
    protected abstract void write(String text);
}
