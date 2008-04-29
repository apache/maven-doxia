package org.apache.maven.doxia.sink;

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

import javax.swing.text.MutableAttributeSet;

/**
 * A set of attributes for a sink event.
 * <p>
 * All sink methods that produce some presentation-level output should have at least
 * one form that allows to pass in a Set of SinkEventAttributes. For instance in
 * <pre>void text( String text, SinkEventAttributes attributes );</pre>
 * the <code>attributes</code> parameter can be used to specify some text styling
 * options, or other optional parameters.
 * </p>
 * <p>
 * What kind of attributes are supported depends on the event and the sink
 * implementation. The sink API just specifies a list of suggested attribute
 * names, that sinks are expected to recognize, and parsers are expected to use
 * preferably when emitting events.
 * </p>
 * <p>
 * It is recommended that for simple attributes, both keys and values should be
 * lower-case Strings, but this is not mandatory. One example of an exception is
 * the {@link #STYLE} attribute, whose value may itself be an AttributeSet again.
 * </p>
 * <p>
 * The <b>base attributes</b> that are supported by almost all events are
 * {@link #CLASS}, {@link #ID}, {@link #LANG}, {@link #STYLE} and {@link #TITLE}.
 * </p>
 *
 * @author ltheussl
 * @version $Id$
 * @since 1.0-beta-1
 */
public interface SinkEventAttributes
    extends MutableAttributeSet
{
    // base

    /**
     * The class of the event element.
     */
    String CLASS = "class";

    /**
     * A unique id for the event element.
     */
    String ID = "id";

    /**
     * The language code for the event element.
     */
    String LANG = "lang";

    /**
     * An inline style definition.
     * @todo specify recommended format with AttributSet values
     */
    String STYLE = "style";

    /**
     * A text to display in a tool tip.
     */
    String TITLE = "title";

    // img

    /**
     * Specifies the alignment of the event element within its parent element.
     * Generally supported values are "left", "right", "center", "justify".
     */
    String ALIGN = "align";

    /**
     * Defines a short description of the event element.
     */
    String ALT = "alt";

    /**
     * Defines a border around an event element.
     */
    String BORDER = "border";

    /**
     * Defines the height of an event element.
     */
    String HEIGHT = "height";

    /**
     * Defines white space on the left and right side of an event element.
     */
    String HSPACE = "hspace";

    /**
     * Defines an image as a server-side image map.
     * Only used by the figureGraphics Sink event.
     */
    String ISMAP = "ismap";

    /**
     * The URL of an external resource, eg an image.
     */
    String SRC = "src";

    /**
     * Defines an image as a client-side image map.
     */
    String USEMAP = "usemap";

    /**
     * Defines white space on the top and bottom of the event element.
     */
    String VSPACE = "vspace";

    /**
     * Sets the width of  an event element.
     */
    String WIDTH = "width";

    // hr

    /**
     * Used to indicate that an element comes with a shadow.
     */
    String NOSHADE = "noshade";

    /**
     * Specifies the size, or thickness, or height of an event element.
     */
    String SIZE = "size";

    // anchor

    /**
     * Specifies the name of an anchor.
     */
    String NAME = "name";

    // link

    /**
     * Specifies the character encoding of text associated with an event element.
     */
    String CHARSET = "charset";

    /**
     * May be used in conjunction with {@link #SHAPE}.
     * Valid values are the same as for the corresponding HTML attributes.
     */
    String COORDS = "coords";

    /**
     * The target URL of an event element, eg a link.
     */
    String HREF = "href";

    /**
     * Specifies the base language of the target URL.
     * Used in conjunction with {@link #HREF}.
     */
    String HREFLANG = "hreflang";

    /**
     * For references to external resourcs, specifies the relationship between
     * the current document and the target URL.
     * Valid values are the same as for the corresponding HTML attribute.
     */
    String REL = "rel";

    /**
     * For references to external resourcs, specifies the relationship between
     * the target URL and the current document.
     * Valid values are the same as for the corresponding HTML attribute.
     */
    String REV = "rev";

    /**
     * Defines the type of region to be defined for a mapping.
     * Used with the {@link #COORDS} attribute.
     */
    String SHAPE = "shape";

    /**
     * Where to open the target URL.
     * Valid values are the same as for the corresponding HTML attribute.
     */
    String TARGET = "target";

    /**
     * Specifies the MIME (Multipurpose Internet Mail Extensions) type of an
     * external resource URL, eg a link.
     */
    String TYPE = "type";

    // table

    /**
     * Specifies the background color of an event element.
     */
    String BGCOLOR = "bgcolor";

    /**
     * Specifies the space between cell walls and contents.
     */
    String CELLPADDING = "cellpadding";

    /**
     * Specifies the space between cells.
     */
    String CELLSPACING = "cellspacing";

    /**
     * Specifies which sides of a border surrounding an element should be visible.
     * Valid values are the same as for the corresponding HTML attribute.
     */
    String FRAME = "frame";

    /**
     * Specifies horizontal/vertical divider lines between certain elements, eg table cells.
     */
    String RULES = "rules";

    /**
     * Specifies a summary of an event attribute for
     * speech-synthesizing/non-visual target output.
     */
    String SUMMARY = "summary";

    // table cell

    /**
     * Specifies an abbreviated version of the content in an element.
     */
    String ABBRV = "abbrv";

    /**
     * Defines a name for a cell.
     */
    String AXIS = "axis";

    /**
     * Indicates the number of columns a cell should span. Used in tables.
     */
    String COLSPAN = "colspan";

    /**
     * A space-separated list of cell IDs that supply header information for the cell.
     */
    String HEADERS = "headers";

    /**
     * Whether to disable or enable automatic text wrapping for an element. 
     */
    String NOWRAP = "nowrap";

    /**
     * Indicates the number of rows a cell should span. Used in tables.
     */
    String ROWSPAN = "rowspan";

    /**
     * A general scope parameter. In Particular, for table cells this
     * specifies if the cell provides header information for the rest of the
     * row that contains it ("row"), or for the rest of the column ("col"),
     * or for the rest of the row group that contains it ("rowgroup"),
     * or for the rest of the column group that contains it ("colgroup").
     */
    String SCOPE = "scope";

    /**
     * Specifies the vertical alignment of an element. Generally accepted values
     * are "top", "baseline", "middle", "bottom", "sup", "sub".
     */
    String VALIGN = "valign";

    // text
    /**
     * Specifies a decoration for an element. Generally accepted values
     * are "underline", "overline", "line-through", "boxed".
     */
    String DECORATION = "decoration";
}
