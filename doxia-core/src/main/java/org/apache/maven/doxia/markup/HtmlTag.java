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
package org.apache.maven.doxia.markup;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An HTML tag name together with the one property Doxia reads from it: whether a start tag may be
 * preceded by a line break. It replaces {@code javax.swing.text.html.HTML.Tag} in the sink API,
 * which carries an HTML 4 table Doxia has outgrown.
 * <p>
 * The block values of the {@linkplain #valueOf(String) known tags} are those the {@link HtmlMarkup}
 * constants report today, copied unchanged so that no generated site's whitespace moves. Several of
 * them are wrong for HTML5 &#x2014; {@code section}, {@code article} and {@code figure} are not
 * treated as block, while {@code menu} and {@code title} are &#x2014; and correcting them is a
 * separate change with its own before-and-after site comparison.
 * </p>
 *
 * @since 2.2.0
 */
public final class HtmlTag {

    private static final Map<String, HtmlTag> KNOWN_TAGS = knownTags();

    private final String name;

    private final boolean block;

    /**
     * Constructs a tag.
     *
     * @param name the tag name, not null.
     * @param block whether a start tag may be preceded by a line break.
     */
    public HtmlTag(String name, boolean block) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.block = block;
    }

    /**
     * Returns the tag of the given name known to Doxia.
     *
     * @param name a tag name, not null.
     * @return the tag, or null if Doxia declares no tag of that name.
     */
    public static HtmlTag valueOf(String name) {
        return KNOWN_TAGS.get(Objects.requireNonNull(name, "name cannot be null"));
    }

    /**
     * Returns the tag name.
     *
     * @return the tag name, never null.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether a start tag may be preceded by a line break.
     *
     * @return true if the tag is a block-level tag.
     */
    public boolean isBlock() {
        return block;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HtmlTag)) {
            return false;
        }
        HtmlTag that = (HtmlTag) other;
        return block == that.block && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, block);
    }

    private static void register(Map<String, HtmlTag> tags, String name, boolean block) {
        tags.put(name, new HtmlTag(name, block));
    }

    private static Map<String, HtmlTag> knownTags() {
        Map<String, HtmlTag> tags = new LinkedHashMap<>();
        register(tags, "a", false);
        register(tags, "abbr", false);
        register(tags, "address", false);
        register(tags, "area", false);
        register(tags, "article", false);
        register(tags, "aside", false);
        register(tags, "audio", false);
        register(tags, "b", false);
        register(tags, "base", false);
        register(tags, "bdi", false);
        register(tags, "bdo", false);
        register(tags, "blockquote", true);
        register(tags, "body", true);
        register(tags, "br", false);
        register(tags, "button", false);
        register(tags, "canvas", false);
        register(tags, "caption", false);
        register(tags, "cite", false);
        register(tags, "code", false);
        register(tags, "col", false);
        register(tags, "colgroup", false);
        register(tags, "command", false);
        register(tags, "data", false);
        register(tags, "datalist", false);
        register(tags, "dd", true);
        register(tags, "del", false);
        register(tags, "details", false);
        register(tags, "dfn", false);
        register(tags, "dialog", false);
        register(tags, "div", true);
        register(tags, "dl", true);
        register(tags, "dt", true);
        register(tags, "em", false);
        register(tags, "embed", false);
        register(tags, "fieldset", false);
        register(tags, "figcaption", false);
        register(tags, "figure", false);
        register(tags, "footer", false);
        register(tags, "form", false);
        register(tags, "h1", true);
        register(tags, "h2", true);
        register(tags, "h3", true);
        register(tags, "h4", true);
        register(tags, "h5", true);
        register(tags, "h6", true);
        register(tags, "head", true);
        register(tags, "header", false);
        register(tags, "hgroup", false);
        register(tags, "hr", false);
        register(tags, "html", false);
        register(tags, "i", false);
        register(tags, "iframe", false);
        register(tags, "img", false);
        register(tags, "input", false);
        register(tags, "ins", false);
        register(tags, "kbd", false);
        register(tags, "keygen", false);
        register(tags, "label", false);
        register(tags, "legend", false);
        register(tags, "li", true);
        register(tags, "link", false);
        register(tags, "main", false);
        register(tags, "map", false);
        register(tags, "mark", false);
        register(tags, "menu", true);
        register(tags, "menuitem", false);
        register(tags, "meta", false);
        register(tags, "meter", false);
        register(tags, "nav", false);
        register(tags, "noscript", false);
        register(tags, "object", false);
        register(tags, "ol", true);
        register(tags, "optgroup", false);
        register(tags, "option", false);
        register(tags, "output", false);
        register(tags, "p", true);
        register(tags, "param", false);
        register(tags, "picture", false);
        register(tags, "pre", true);
        register(tags, "progress", false);
        register(tags, "q", false);
        register(tags, "rb", false);
        register(tags, "rp", false);
        register(tags, "rt", false);
        register(tags, "rtc", false);
        register(tags, "ruby", false);
        register(tags, "s", false);
        register(tags, "samp", false);
        register(tags, "script", false);
        register(tags, "section", false);
        register(tags, "select", false);
        register(tags, "small", false);
        register(tags, "source", false);
        register(tags, "span", false);
        register(tags, "strong", false);
        register(tags, "style", false);
        register(tags, "sub", false);
        register(tags, "summary", false);
        register(tags, "sup", false);
        register(tags, "svg", false);
        register(tags, "table", true);
        register(tags, "tbody", false);
        register(tags, "td", true);
        register(tags, "template", false);
        register(tags, "textarea", false);
        register(tags, "tfoot", false);
        register(tags, "th", true);
        register(tags, "thead", false);
        register(tags, "time", false);
        register(tags, "title", true);
        register(tags, "tr", true);
        register(tags, "track", false);
        register(tags, "u", false);
        register(tags, "ul", true);
        register(tags, "var", false);
        register(tags, "video", false);
        register(tags, "wbr", false);
        return Collections.unmodifiableMap(tags);
    }
}
