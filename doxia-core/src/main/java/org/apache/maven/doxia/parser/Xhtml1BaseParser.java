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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;

/**
 * Acts as bridge between legacy parsers relying on <a href="https://www.w3.org/TR/xhtml1/">XHTML 1.0 Transitional (based on HTML4)</a>
 * and the {@link Xhtml5BaseParser} only supporting (X)HTML5 elements/attributes.
 *
 * Adds support for elements/attributes which <a href="https://html.spec.whatwg.org/#non-conforming-features">became obsolete in HTML5</a> but are
 * commonly used for XDoc/FML.
 *
 * @see <a href="https://www.w3.org/TR/html5-diff/">HTML5 Differences from HTML4</a>.
 */
public class Xhtml1BaseParser extends Xhtml5BaseParser {

    private static final class AttributeMapping {
        private final String sourceName;
        private final String targetName;
        private final UnaryOperator<String> valueMapper;
        private final MergeSemantics mergeSemantics;

        enum MergeSemantics {
            OVERWRITE,
            IGNORE,
            PREPEND
        }

        AttributeMapping(String sourceAttribute, String targetAttribute, MergeSemantics mergeSemantics) {
            this(sourceAttribute, targetAttribute, UnaryOperator.identity(), mergeSemantics);
        }

        AttributeMapping(
                String sourceName,
                String targetName,
                UnaryOperator<String> valueMapper,
                MergeSemantics mergeSemantics) {
            super();
            this.sourceName = sourceName;
            this.targetName = targetName;
            this.valueMapper = valueMapper;
            this.mergeSemantics = mergeSemantics;
        }

        public String getSourceName() {
            return sourceName;
        }

        public String getTargetName() {
            return targetName;
        }

        public UnaryOperator<String> getValueMapper() {
            return valueMapper;
        }

        public String mergeValue(String oldValue, String newValue) {
            final String mergedValue;
            switch (mergeSemantics) {
                case IGNORE:
                    mergedValue = oldValue;
                    break;
                case OVERWRITE:
                    mergedValue = newValue;
                    break;
                default:
                    mergedValue = newValue + " " + oldValue;
            }
            return mergedValue;
        }
    }

    static final String mapAlignToStyle(String alignValue) {
        switch (alignValue) {
            case "center":
            case "left":
            case "right":
                return "text-align: " + alignValue + ";";
            default:
                return null;
        }
    }

    /**
     * All obsolete attributes in a map with key = affected element name, value = collection if {@link AttributeMapping}s
     */
    private static final Map<String, Collection<AttributeMapping>> ATTRIBUTE_MAPPING_TABLE = new HashMap<>();

    /**
     * All obsolete elements in a map with key = obsolete element name, value = non-obsolete replacement element name
     */
    private static final Map<String, String> ELEMENT_MAPPING_TABLE = new HashMap<>();

    static {
        ATTRIBUTE_MAPPING_TABLE.put(
                "a", Collections.singleton(new AttributeMapping("name", "id", AttributeMapping.MergeSemantics.IGNORE)));

        Collection<AttributeMapping> tableMappings = Arrays.asList(
                new AttributeMapping(
                        "border",
                        "class",
                        (v) -> (v != null && !v.equals("0")) ? "bodyTableBorder" : null,
                        AttributeMapping.MergeSemantics.PREPEND),
                new AttributeMapping(
                        "align", "style", Xhtml1BaseParser::mapAlignToStyle, AttributeMapping.MergeSemantics.PREPEND));
        ATTRIBUTE_MAPPING_TABLE.put("table", tableMappings);
        ATTRIBUTE_MAPPING_TABLE.put(
                "td",
                Collections.singleton(new AttributeMapping(
                        "align", "style", Xhtml1BaseParser::mapAlignToStyle, AttributeMapping.MergeSemantics.PREPEND)));
        ATTRIBUTE_MAPPING_TABLE.put(
                "th",
                Collections.singleton(new AttributeMapping(
                        "align", "style", Xhtml1BaseParser::mapAlignToStyle, AttributeMapping.MergeSemantics.PREPEND)));
        ELEMENT_MAPPING_TABLE.put("tt", "code");
        ELEMENT_MAPPING_TABLE.put("strike", "del");
    }

    /**
     * Translates obsolete XHTML 1.0 attributes/elements to valid XHTML5 ones before calling the underlying {@link Xhtml5BaseParser}.
     */
    @Override
    protected boolean baseStartTag(XmlPullParser parser, Sink sink) {
        SinkEventAttributeSet attribs = getAttributesFromParser(parser);
        String elementName = parser.getName();
        Collection<AttributeMapping> attributeMappings = ATTRIBUTE_MAPPING_TABLE.get(elementName);
        if (attributeMappings != null) {
            for (AttributeMapping attributeMapping : attributeMappings) {
                String attributeValue = (String) attribs.getAttribute(attributeMapping.getSourceName());
                if (attributeValue != null) {
                    String newValue = attributeMapping.getValueMapper().apply(attributeValue);
                    if (newValue != null) {
                        String oldValue = (String) attribs.getAttribute(attributeMapping.getTargetName());
                        if (oldValue != null) {
                            newValue = attributeMapping.mergeValue(oldValue, newValue);
                        }
                        attribs.addAttribute(attributeMapping.getTargetName(), newValue);
                    }
                    attribs.removeAttribute(attributeMapping.getSourceName());
                }
            }
        }
        String mappedElementName = ELEMENT_MAPPING_TABLE.getOrDefault(elementName, elementName);
        return super.baseStartTag(mappedElementName, attribs, sink);
    }

    @Override
    protected boolean baseEndTag(XmlPullParser parser, Sink sink) {
        SinkEventAttributeSet attribs = getAttributesFromParser(parser);
        String elementName = parser.getName();
        String mappedElementName = ELEMENT_MAPPING_TABLE.getOrDefault(elementName, elementName);
        return super.baseEndTag(mappedElementName, attribs, sink);
    }
}
