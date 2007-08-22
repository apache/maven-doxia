package org.apache.maven.doxia.module.docbook;

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

import org.apache.maven.doxia.markup.XmlMarkup;

/**
 * List of <code>Docbook</code> markups.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public interface DocbookMarkup
    extends XmlMarkup
{
    // ----------------------------------------------------------------------
    // Specific Docbook tags
    // ----------------------------------------------------------------------

    /** Docbook tag for <code>anchor</code> */
    Tag ANCHOR_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "anchor";
        }
    };

    /** Docbook tag for <code>article</code> */
    Tag ARTICLE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "article";
        }
    };
    
    /** Docbook tag for <code>articleinfo</code> */
    Tag ARTICLEINFO_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "articleinfo";
        }
    };

    /** Docbook tag for <code>book</code> */
    Tag BOOK_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "book";
        }
    };

    /** Docbook tag for <code>bookinfo</code> */
    Tag BOOKINFO_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "bookinfo";
        }
    };

    /** Docbook tag for <code>chapter</code> */
    Tag CHAPTER_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "chapter";
        }
    };

    /** Docbook tag for <code>colspec</code> */
    Tag COLSPEC_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "colspec";
        }
    };

    /** Docbook tag for <code>corpauthor</code> */
    Tag CORPAUTHOR_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "corpauthor";
        }
    };

    /** Docbook tag for <code>date</code> */
    Tag DATE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "date";
        }
    };

    /** Docbook tag for <code>email</code> */
    Tag EMAIL_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "email";
        }
    };

    /** Docbook tag for <code>entry</code> */
    Tag ENTRY_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "entry";
        }
    };

    /** Docbook tag for <code>figure</code> */
    Tag FIGURE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "figure";
        }
    };

    /** Docbook tag for <code>formalpara</code> */
    Tag FORMALPARA_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "formalpara";
        }
    };

    /** Docbook tag for <code>imagedata</code> */
    Tag IMAGEDATA_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "imagedata";
        }
    };

    /** Docbook tag for <code>imageobject</code> */
    Tag IMAGEOBJECT_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "imageobject";
        }
    };

    /** Docbook tag for <code>info</code> */
    Tag INFO_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "info";
        }
    };

    /** Docbook tag for <code>informalfigure</code> */
    Tag INFORMALFIGURE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "informalfigure";
        }
    };

    /** Docbook tag for <code>informaltable</code> */
    Tag INFORMALTABLE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "informaltable";
        }
    };

    /** Docbook tag for <code>itemizedlist</code> */
    Tag ITEMIZEDLIST_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "itemizedlist";
        }
    };

    /** Docbook tag for <code>link</code> */
    Tag LINK_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "link";
        }
    };

    /** Docbook tag for <code>listitem</code> */
    Tag LISTITEM_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "listitem";
        }
    };

    /** Docbook tag for <code>mediaobject</code> */
    Tag MEDIAOBJECT_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "mediaobject";
        }
    };

    /** Docbook tag for <code>orderedlist</code> */
    Tag ORDEREDLIST_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "orderedlist";
        }
    };

    /** Docbook tag for <code>para</code> */
    Tag PARA_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "para";
        }
    };

    /** Docbook tag for <code>programlisting</code> */
    Tag PROGRAMLISTING_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "programlisting";
        }
    };

    /** Docbook tag for <code>row</code> */
    Tag ROW_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "row";
        }
    };

    /** Docbook tag for <code>section</code> */
    Tag SECTION_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "section";
        }
    };

    /** Docbook tag for <code>simpara</code> */
    Tag SIMPARA_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "simpara";
        }
    };

    /** Docbook tag for <code>tbody</code> */
    Tag TBODY_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "tbody";
        }
    };

    /** Docbook tag for <code>term</code> */
    Tag TERM_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "term";
        }
    };

    /** Docbook tag for <code>tgroup</code> */
    Tag TGROUP_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "tgroup";
        }
    };

    /** Docbook tag for <code>thead</code> */
    Tag THEAD_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "thead";
        }
    };

    /** Docbook tag for <code>ulink</code> */
    Tag ULINK_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "ulink";
        }
    };

    /** Docbook tag for <code>url</code> */
    Tag URL_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "url";
        }
    };

    /** Docbook tag for <code>variablelist</code> */
    Tag VARIABLELIST_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "variablelist";
        }
    };

    /** Docbook tag for <code>varlistentry</code> */
    Tag VARLISTENTRY_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "varlistentry";
        }
    };

    /** Docbook tag for <code>xref</code> */
    Tag XREF_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "xref";
        }
    };

    // ----------------------------------------------------------------------
    // Specific Docbook attributes
    // ----------------------------------------------------------------------

    /** Docbook attribute <code>cols</code> used in <code>tgroup</code> tags */
    String COLS_ATTRIBUTE = "cols";

    /** Docbook attribute <code>colsep</code> used in <code>informaltable</code> tags */
    String COLSEP_ATTRIBUTE = "colsep";

    /** Docbook attribute <code>fileref</code> used in <code>orderedlist, imageobject</code> tags */
    String FILEREF_ATTRIBUTE = "fileref";

    /** Docbook attribute <code>format</code> used in <code>imagedata</code> tags */
    String FORMAT_ATTRIBUTE = "format";

    /** Docbook attribute <code>frame</code> used in <code>informaltable</code> tags */
    String FRAME_ATTRIBUTE = "frame";

    /** Docbook attribute <code>linkend</code> used in <code>link</code> tag */
    String LINKEND_ATTRIBUTE = "linkend";

    /** Docbook attribute <code>numeration</code> used in <code>orderedlist</code> tag */
    String NUMERATION_ATTRIBUTE = "numeration";

    /** Docbook attribute <code>rowsep</code> used in <code>informaltable</code> tags */
    String ROWSEP_ATTRIBUTE = "rowsep";

    /** Docbook attribute <code>url</code> used in <code>ulink</code> tags */
    String URL_ATTRIBUTE = "url";

    // ----------------------------------------------------------------------
    // Specific Docbook styles
    // ----------------------------------------------------------------------

    /** Docbook style <code>arabic</code> used in <code>numeration</code> attribute */
    String ARABIC_STYLE = "arabic";

    /** Docbook style <code>loweralpha</code> used in <code>numeration</code> attribute */
    String LOWERALPHA_STYLE = "loweralpha";

    /** Docbook style <code>lowerroman</code> used in <code>numeration</code> attribute */
    String LOWERROMAN_STYLE = "lowerroman";

    /** Docbook style <code>upperalpha</code> used in <code>numeration</code> attribute */
    String UPPERALPHA_STYLE = "upperalpha";

    /** Docbook style <code>upperroman</code> used in <code>numeration</code> attribute */
    String UPPERROMAN_STYLE = "upperroman";
}
