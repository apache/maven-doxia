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

import javax.swing.text.html.HTML.Tag;

/**
 * List of <code>Html</code> tags.
 * <p>
 * This should contain all valid HTML5 tags, comprising the tags
 * in {@link javax.swing.text.html.HTML.Tag} plus several others.
 * </p>
 *
 * @see <a href=
 *      "https://www.tutorialrepublic.com/html-reference/html5-tags.php">
 *      https://www.tutorialrepublic.com/html-reference/html5-tags.php</a>
 * @author ltheussl
 * @since 1.0
 */
@SuppressWarnings("checkstyle:interfaceistype")
public interface HtmlMarkup extends XmlMarkup {

    /** A simple HTML tag. Eg <code>&lt;br/&gt;</code>. */
    int TAG_TYPE_SIMPLE = 1;

    /** A start HTML tag. Eg <code>&lt;p&gt;</code>. */
    int TAG_TYPE_START = 2;

    /** An end HTML tag. Eg <code>&lt;/p&gt;</code>. */
    int TAG_TYPE_END = 3;

    /**
     * An HTML entity. Eg <code>&amp;lt;</code>.
     *
     * @since 1.1.1.
     */
    int ENTITY_TYPE = 4;

    /**
     * A CDATA type event.
     *
     * @since 1.1.1.
     */
    int CDATA_TYPE = 5;

    // ----------------------------------------------------------------------
    // All HTML5 1.0 tags
    // ----------------------------------------------------------------------

    /** HTML5 tag for <code>a</code>. */
    Tag A = Tag.A;

    /** HTML5 tag for <code>abbr</code>. */
    Tag ABBR = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "abbr";
        }
    };

    /** HTML5 tag for <code>address</code>. */
    Tag ADDRESS = Tag.ADDRESS;

    /** HTML5 tag for <code>area</code>. */
    Tag AREA = Tag.AREA;

    /** HTML5 tag for <code>article</code>. */
    Tag ARTICLE = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "article";
        }
    };

    /** HTML5 tag for <code>aside</code>. */
    Tag ASIDE = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "aside";
        }
    };

    /** HTML5 tag for <code>audio</code>. */
    Tag AUDIO = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "audio";
        }
    };

    /** HTML5 tag for <code>b</code>. */
    Tag B = Tag.B;

    /** HTML5 tag for <code>base</code>. */
    Tag BASE = Tag.BASE;

    /** HTML5 tag for <code>bdi</code>. */
    Tag BDI = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "bdi";
        }
    };

    /** HTML5 tag for <code>bdo</code>. */
    Tag BDO = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "bdo";
        }
    };

    /** HTML5 tag for <code>blockquote</code>. */
    Tag BLOCKQUOTE = Tag.BLOCKQUOTE;

    /** HTML5 tag for <code>body</code>. */
    Tag BODY = Tag.BODY;

    /** HTML5 tag for <code>br</code>. */
    Tag BR = Tag.BR;

    /** HTML5 tag for <code>button</code>. */
    Tag BUTTON = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "button";
        }
    };

    /** HTML5 tag for <code>canvas</code>. */
    Tag CANVAS = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "canvas";
        }
    };

    /** HTML5 tag for <code>caption</code>. */
    Tag CAPTION = Tag.CAPTION;

    /** HTML5 tag for <code>cite</code>. */
    Tag CITE = Tag.CITE;

    /** HTML5 tag for <code>code</code>. */
    Tag CODE = Tag.CODE;

    /** HTML5 tag for <code>col</code>. */
    Tag COL = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "col";
        }
    };

    /** HTML5 tag for <code>colgroup</code>. */
    Tag COLGROUP = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "colgroup";
        }
    };

    /** HTML5 tag for <code>command</code>. */
    Tag COMMAND = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "command";
        }
    };

    /** HTML5 tag for <code>data</code>. */
    Tag DATA = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "data";
        }
    };

    /** HTML5 tag for <code>datalist</code>. */
    Tag DATALIST = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "datalist";
        }
    };

    /** HTML5 tag for <code>dd</code>. */
    Tag DD = Tag.DD;

    /** HTML5 tag for <code>del</code>. */
    Tag DEL = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "del";
        }
    };

    /** HTML5 tag for <code>details</code>. */
    Tag DETAILS = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "details";
        }
    };

    /** HTML5 tag for <code>dfn</code>. */
    Tag DFN = Tag.DFN;

    /** HTML5 tag for <code>dialog</code>. */
    Tag DIALOG = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "dialog";
        }
    };

    /** HTML5 tag for <code>div</code>. */
    Tag DIV = Tag.DIV;

    /** HTML5 tag for <code>dl</code>. */
    Tag DL = Tag.DL;

    /** HTML5 tag for <code>dt</code>. */
    Tag DT = Tag.DT;

    /** HTML5 tag for <code>em</code>. */
    Tag EM = Tag.EM;

    /** HTML5 tag for <code>embed</code>. */
    Tag EMBED = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "embed";
        }
    };

    /** HTML5 tag for <code>fieldset</code>. */
    Tag FIELDSET = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "fieldset";
        }
    };

    /** HTML5 tag for <code>figcaption</code>. */
    Tag FIGCAPTION = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "figcaption";
        }
    };

    /** HTML5 tag for <code>figure</code>. */
    Tag FIGURE = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "figure";
        }
    };

    /** HTML5 tag for <code>footer</code>. */
    Tag FOOTER = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "footer";
        }
    };

    /** HTML5 tag for <code>form</code>. */
    Tag FORM = Tag.FORM;

    /** HTML5 tag for <code>h1</code>. */
    Tag H1 = Tag.H1;

    /** HTML5 tag for <code>h2</code>. */
    Tag H2 = Tag.H2;

    /** HTML5 tag for <code>h3</code>. */
    Tag H3 = Tag.H3;

    /** HTML5 tag for <code>h4</code>. */
    Tag H4 = Tag.H4;

    /** HTML5 tag for <code>h5</code>. */
    Tag H5 = Tag.H5;

    /** HTML5 tag for <code>head</code>. */
    Tag HEAD = Tag.HEAD;

    /** HTML5 tag for <code>header</code>. */
    Tag HEADER = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "header";
        }
    };

    /** HTML5 tag for <code>hgroup</code>. */
    Tag HGROUP = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "hgroup";
        }
    };

    /** HTML5 tag for <code>hr</code>. */
    Tag HR = Tag.HR;

    /** HTML5 tag for <code>html</code>. */
    Tag HTML = Tag.HTML;

    /** HTML5 tag for <code>i</code>. */
    Tag I = Tag.I;

    /** HTML5 tag for <code>iframe</code>. */
    Tag IFRAME = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "iframe";
        }
    };

    /** HTML5 tag for <code>img</code>. */
    Tag IMG = Tag.IMG;

    /** HTML5 tag for <code>input</code>. */
    Tag INPUT = Tag.INPUT;

    /** HTML5 tag for <code>ins</code>. */
    Tag INS = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "ins";
        }
    };

    /** HTML5 tag for <code>kbd</code>. */
    Tag KBD = Tag.KBD;

    /** HTML5 tag for <code>keygen</code>. */
    Tag KEYGEN = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "keygen";
        }
    };

    /** HTML5 tag for <code>label</code>. */
    Tag LABEL = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "label";
        }
    };

    /** HTML5 tag for <code>legend</code>. */
    Tag LEGEND = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "legend";
        }
    };

    /** HTML5 tag for <code>li</code>. */
    Tag LI = Tag.LI;

    /** HTML5 tag for <code>link</code>. */
    Tag LINK = Tag.LINK;

    /** HTML5 tag for <code>map</code>. */
    Tag MAP = Tag.MAP;

    /** HTML5 tag for <code>main</code>. */
    Tag MAIN = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "main";
        }
    };

    /** HTML5 tag for <code>mark</code>. */
    Tag MARK = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "mark";
        }
    };

    /** HTML5 tag for <code>menu</code>. */
    Tag MENU = Tag.MENU;

    /** HTML5 tag for <code>menuitem</code>. */
    Tag MENUITEM = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "menuitem";
        }
    };

    /** HTML5 tag for <code>meta</code>. */
    Tag META = Tag.META;

    /** HTML5 tag for <code>meter</code>. */
    Tag METER = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "meter";
        }
    };

    /** HTML5 tag for <code>nav</code>. */
    Tag NAV = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "nav";
        }
    };

    /** HTML5 tag for <code>noscript</code>. */
    Tag NOSCRIPT = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "noscript";
        }
    };

    /** HTML5 tag for <code>object</code>. */
    Tag OBJECT = Tag.OBJECT;

    /** HTML5 tag for <code>ol</code>. */
    Tag OL = Tag.OL;

    /** HTML5 tag for <code>optgroup</code>. */
    Tag OPTGROUP = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "optgroup";
        }
    };

    /** HTML5 tag for <code>option</code>. */
    Tag OPTION = Tag.OPTION;

    /** HTML5 tag for <code>output</code>. */
    Tag OUTPUT = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "output";
        }
    };

    /** HTML5 tag for <code>p</code>. */
    Tag P = Tag.P;

    /** HTML5 tag for <code>param</code>. */
    Tag PARAM = Tag.PARAM;

    /** HTML5 tag for <code>picture</code>. */
    Tag PICTURE = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "picture";
        }
    };

    /** HTML5 tag for <code>pre</code>. */
    Tag PRE = Tag.PRE;

    /** HTML5 tag for <code>progress</code>. */
    Tag PROGRESS = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "progress";
        }
    };

    /** HTML5 tag for <code>q</code>. */
    Tag Q = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "q";
        }
    };

    /** HTML5 tag for <code>rb</code>. */
    Tag RB = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "rb";
        }
    };

    /** HTML5 tag for <code>rp</code>. */
    Tag RP = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "rp";
        }
    };

    /** HTML5 tag for <code>rt</code>. */
    Tag RT = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "rt";
        }
    };

    /** HTML5 tag for <code>rtc</code>. */
    Tag RTC = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "rtc";
        }
    };

    /** HTML5 tag for <code>ruby</code>. */
    Tag RUBY = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "ruby";
        }
    };

    /** HTML5 tag for <code>s</code>. */
    Tag S = Tag.S;

    /** HTML5 tag for <code>samp</code>. */
    Tag SAMP = Tag.SAMP;

    /** HTML5 tag for <code>script</code>. */
    Tag SCRIPT = Tag.SCRIPT;

    /** HTML5 tag for <code>section</code>. */
    Tag SECTION = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "section";
        }
    };

    /** HTML5 tag for <code>select</code>. */
    Tag SELECT = Tag.SELECT;

    /** HTML5 tag for <code>small</code>. */
    Tag SMALL = Tag.SMALL;

    /** HTML5 tag for <code>source</code>. */
    Tag SOURCE = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "source";
        }
    };

    /** HTML5 tag for <code>span</code>. */
    Tag SPAN = Tag.SPAN;

    /** HTML5 tag for <code>strong</code>. */
    Tag STRONG = Tag.STRONG;

    /** HTML5 tag for <code>style</code>. */
    Tag STYLE = Tag.STYLE;

    /** HTML5 tag for <code>sub</code>. */
    Tag SUB = Tag.SUB;

    /** HTML5 tag for <code>summary</code>. */
    Tag SUMMARY = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "summary";
        }
    };

    /** HTML5 tag for <code>sup</code>. */
    Tag SUP = Tag.SUP;

    /** HTML5 tag for <code>svg</code>. */
    Tag SVG = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "svg";
        }
    };

    /** HTML5 tag for <code>table</code>. */
    Tag TABLE = Tag.TABLE;

    /** HTML5 tag for <code>tbody</code>. */
    Tag TBODY = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "tbody";
        }
    };

    /** HTML5 tag for <code>td</code>. */
    Tag TD = Tag.TD;

    /** HTML5 tag for <code>template</code>. */
    Tag TEMPLATE = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "template";
        }
    };

    /** HTML5 tag for <code>textarea</code>. */
    Tag TEXTAREA = Tag.TEXTAREA;

    /** HTML5 tag for <code>tfoot</code>. */
    Tag TFOOT = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "tfoot";
        }
    };

    /** HTML5 tag for <code>th</code>. */
    Tag TH = Tag.TH;

    /** HTML5 tag for <code>thead</code>. */
    Tag THEAD = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "thead";
        }
    };

    /** HTML5 tag for <code>time</code>. */
    Tag TIME = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "time";
        }
    };

    /** HTML5 tag for <code>title</code>. */
    Tag TITLE = Tag.TITLE;

    /** HTML5 tag for <code>tr</code>. */
    Tag TR = Tag.TR;

    /** HTML5 tag for <code>track</code>. */
    Tag TRACK = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "track";
        }
    };

    /** HTML5 tag for <code>u</code>. */
    Tag U = Tag.U;

    /** HTML5 tag for <code>ul</code>. */
    Tag UL = Tag.UL;

    /** HTML5 tag for <code>var</code>. */
    Tag VAR = Tag.VAR;

    /** HTML5 tag for <code>video</code>. */
    Tag VIDEO = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "video";
        }
    };

    /** HTML5 tag for <code>wbr</code>. */
    Tag WBR = new Tag() {
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "wbr";
        }
    };
}
