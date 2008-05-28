package org.apache.maven.doxia.markup;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License" ); you may not use this file except in compliance
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
 *   This should contain all valid XHTML 1.0 tags, comprising the tags in
 *   {@link javax.swing.text.html.HTML.Tag} plus several others.
 * </p>
 *
 * @author ltheussl
 * @version $Id:$
 * @since 1.0
 */
public interface HtmlMarkup
    extends XmlMarkup
{

    /** A simple HTML tag. Eg <code>&lt;br/&gt;</code>. */
    int TAG_TYPE_SIMPLE = 1;

    /** A start HTML tag. Eg <code>&lt;p&gt;</code>. */
    int TAG_TYPE_START = 2;

    /** An end HTML tag. Eg <code>&lt;/p&gt;</code>. */
    int TAG_TYPE_END = 3;

    // ----------------------------------------------------------------------
    // All XHTML 1.0 tags
    // ----------------------------------------------------------------------

    /** Xhtml tag for <code>a</code>. */
    Tag A = Tag.A;

    /** Xhtml tag for <code>abbr</code>. */
    Tag ABBR = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "abbr";
        }
    };

    /** Xhtml tag for <code>acronym</code>. */
    Tag ACRONYM = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "acronym";
        }
    };

    /** Xhtml tag for <code>address</code>. */
    Tag ADDRESS = Tag.ADDRESS;

    /** Xhtml tag for <code>applet</code>. */
    Tag APPLET = Tag.APPLET;

    /** Xhtml tag for <code>area</code>. */
    Tag AREA = Tag.AREA;

    /** Xhtml tag for <code>b</code>. */
    Tag B = Tag.B;

    /** Xhtml tag for <code>base</code>. */
    Tag BASE = Tag.BASE;

    /** Xhtml tag for <code>basefont</code>. */
    Tag BASEFONT = Tag.BASEFONT;

    /** Xhtml tag for <code>bdo</code>. */
    Tag BDO = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "bdo";
        }
    };

    /** Xhtml tag for <code>big</code>. */
    Tag BIG = Tag.BIG;

    /** Xhtml tag for <code>blockquote</code>. */
    Tag BLOCKQUOTE = Tag.BLOCKQUOTE;

    /** Xhtml tag for <code>body</code>. */
    Tag BODY = Tag.BODY;

    /** Xhtml tag for <code>br</code>. */
    Tag BR = Tag.BR;

    /** Xhtml tag for <code>button</code>. */
    Tag BUTTON = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "button";
        }
    };

    /** Xhtml tag for <code>caption</code>. */
    Tag CAPTION = Tag.CAPTION;

    /** Xhtml tag for <code>center</code>. */
    Tag CENTER = Tag.CENTER;

    /** Xhtml tag for <code>cite</code>. */
    Tag CITE = Tag.CITE;

    /** Xhtml tag for <code>code</code>. */
    Tag CODE = Tag.CODE;

    /** Xhtml tag for <code>col</code>. */
    Tag COL = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "col";
        }
    };

    /** Xhtml tag for <code>colgroup</code>. */
    Tag COLGROUP = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "colgroup";
        }
    };

    /** Xhtml tag for <code>dd</code>. */
    Tag DD = Tag.DD;

    /** Xhtml tag for <code>del</code>. */
    Tag DEL = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "del";
        }
    };

    /** Xhtml tag for <code>dfn</code>. */
    Tag DFN = Tag.DFN;

    /** Xhtml tag for <code>dir</code>. */
    Tag DIR = Tag.DIR;

    /** Xhtml tag for <code>div</code>. */
    Tag DIV = Tag.DIV;

    /** Xhtml tag for <code>dl</code>. */
    Tag DL = Tag.DL;

    /** Xhtml tag for <code>dt</code>. */
    Tag DT = Tag.DT;

    /** Xhtml tag for <code>em</code>. */
    Tag EM = Tag.EM;

    /** Xhtml tag for <code>fieldset</code>. */
    Tag FIELDSET = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "fieldset";
        }
    };

    /** Xhtml tag for <code>font</code>. */
    Tag FONT = Tag.FONT;

    /** Xhtml tag for <code>form</code>. */
    Tag FORM = Tag.FORM;

    /** Xhtml tag for <code>frame</code>. */
    Tag FRAME = Tag.FRAME;

    /** Xhtml tag for <code>frameset</code>. */
    Tag FRAMESET = Tag.FRAMESET;

    /** Xhtml tag for <code>h1</code>. */
    Tag H1 = Tag.H1;

    /** Xhtml tag for <code>h2</code>. */
    Tag H2 = Tag.H2 ;

    /** Xhtml tag for <code>h3</code>. */
    Tag H3 = Tag.H3;

    /** Xhtml tag for <code>h4</code>. */
    Tag H4 = Tag.H4;

    /** Xhtml tag for <code>h5</code>. */
    Tag H5 = Tag.H5;

    /** Xhtml tag for <code>h6</code>. */
    Tag H6 = Tag.H6;

    /** Xhtml tag for <code>head</code>. */
    Tag HEAD = Tag.HEAD;

    /** Xhtml tag for <code>hr</code>. */
    Tag HR = Tag.HR;

    /** Xhtml tag for <code>html</code>. */
    Tag HTML = Tag.HTML;

    /** Xhtml tag for <code>i</code>. */
    Tag I = Tag.I;

    /** Xhtml tag for <code>iframe</code>. */
    Tag IFRAME = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "iframe";
        }
    };

    /** Xhtml tag for <code>img</code>. */
    Tag IMG = Tag.IMG;

    /** Xhtml tag for <code>input</code>. */
    Tag INPUT = Tag.INPUT;

    /** Xhtml tag for <code>ins</code>. */
    Tag INS = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "ins";
        }
    };

    /** Xhtml tag for <code>isindex</code>. */
    Tag ISINDEX = Tag.ISINDEX;

    /** Xhtml tag for <code>kbd</code>. */
    Tag KBD = Tag.KBD;

    /** Xhtml tag for <code>label</code>. */
    Tag LABEL = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "label";
        }
    };

    /** Xhtml tag for <code>legend</code>. */
    Tag LEGEND = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "legend";
        }
    };

    /** Xhtml tag for <code>li</code>. */
    Tag LI = Tag.LI;

    /** Xhtml tag for <code>link</code>. */
    Tag LINK = Tag.LINK;

    /** Xhtml tag for <code>map</code>. */
    Tag MAP = Tag.MAP;

    /** Xhtml tag for <code>menu</code>. */
    Tag MENU = Tag.MENU;

    /** Xhtml tag for <code>meta</code>. */
    Tag META = Tag.META;

    /** Xhtml tag for <code>noframes</code>. */
    Tag NOFRAMES = Tag.NOFRAMES;

    /** Xhtml tag for <code>noscript</code>. */
    Tag NOSCRIPT = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "noscript";
        }
    };

    /** Xhtml tag for <code>object</code>. */
    Tag OBJECT = Tag.OBJECT;

    /** Xhtml tag for <code>ol</code>. */
    Tag OL = Tag.OL;

    /** Xhtml tag for <code>optgroup</code>. */
    Tag OPTGROUP = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "optgroup";
        }
    };

    /** Xhtml tag for <code>option</code>. */
    Tag OPTION = Tag.OPTION;

    /** Xhtml tag for <code>p</code>. */
    Tag P = Tag.P;

    /** Xhtml tag for <code>param</code>. */
    Tag PARAM = Tag.PARAM;

    /** Xhtml tag for <code>pre</code>. */
    Tag PRE = Tag.PRE;

    /** Xhtml tag for <code>q</code>. */
    Tag Q = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "q";
        }
    };

    /** Xhtml tag for <code>s</code>. */
    Tag S = Tag.S;

    /** Xhtml tag for <code>samp</code>. */
    Tag SAMP = Tag.SAMP;

    /** Xhtml tag for <code>script</code>. */
    Tag SCRIPT = Tag.SCRIPT;

    /** Xhtml tag for <code>select</code>. */
    Tag SELECT = Tag.SELECT;

    /** Xhtml tag for <code>small</code>. */
    Tag SMALL = Tag.SMALL;

    /** Xhtml tag for <code>span</code>. */
    Tag SPAN = Tag.SPAN;

    /** Xhtml tag for <code>strike</code>. */
    Tag STRIKE = Tag.STRIKE;

    /** Xhtml tag for <code>strong</code>. */
    Tag STRONG = Tag.STRONG;

    /** Xhtml tag for <code>style</code>. */
    Tag STYLE = Tag.STYLE;

    /** Xhtml tag for <code>sub</code>. */
    Tag SUB = Tag.SUB;

    /** Xhtml tag for <code>sup</code>. */
    Tag SUP = Tag.SUP;

    /** Xhtml tag for <code>table</code>. */
    Tag TABLE = Tag.TABLE;

    /** Xhtml tag for <code>tbody</code>. */
    Tag TBODY = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "tbody";
        }
    };

    /** Xhtml tag for <code>td</code>. */
    Tag TD = Tag.TD;

    /** Xhtml tag for <code>textarea</code>. */
    Tag TEXTAREA = Tag.TEXTAREA;

    /** Xhtml tag for <code>tfoot</code>. */
    Tag TFOOT = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "tfoot";
        }
    };

    /** Xhtml tag for <code>th</code>. */
    Tag TH = Tag.TH;

    /** Xhtml tag for <code>thead</code>. */
    Tag THEAD = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "thead";
        }
    };

    /** Xhtml tag for <code>title</code>. */
    Tag TITLE = Tag.TITLE;

    /** Xhtml tag for <code>tr</code>. */
    Tag TR = Tag.TR;

    /** Xhtml tag for <code>tt</code>. */
    Tag TT = Tag.TT;

    /** Xhtml tag for <code>u</code>. */
    Tag U = Tag.U;

    /** Xhtml tag for <code>ul</code>. */
    Tag UL = Tag.UL;

    /** Xhtml tag for <code>var</code>. */
    Tag VAR = Tag.VAR ;
}
