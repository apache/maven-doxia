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
 * List of <code>DocBook</code> markups.
 * TODO: only use <a href="http://www.docbook.org/schemas/sdocbook/elements.html">Simplified DocBook elements</a>,
 * remove full DocBook-only ones.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 * @deprecated This interface is incomplete and will be removed. Use {@link SimplifiedDocbookMarkup} instead.
 */
@SuppressWarnings( "checkstyle:interfaceistype" )
public interface DocbookMarkup
    extends XmlMarkup
{
    /** DocBook V4.4 SGML public id: "-//OASIS//DTD DocBook V4.4//EN" */
    String DEFAULT_SGML_PUBLIC_ID = "-//OASIS//DTD DocBook V4.4//EN";

    /** DocBook XML V4.4 XML public id: "-//OASIS//DTD DocBook XML V4.4//EN" */
    String DEFAULT_XML_PUBLIC_ID = "-//OASIS//DTD DocBook V4.4//EN";

    /** DocBook XML V4.4 XML system id: "http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd" */
    String DEFAULT_XML_SYSTEM_ID = "http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd";

    /** DocBook XML V4.4 SGML system id: "http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd" */
    String DEFAULT_SGML_SYSTEM_ID = "http://www.oasis-open.org/docbook/sgml/4.4/docbookx.dtd";

    // ----------------------------------------------------------------------
    // Specific DocBook tags
    // ----------------------------------------------------------------------

    /** DocBook tag for <code>anchor</code> */
    Tag ANCHOR_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "anchor";
        }
    };

    /** DocBook tag for <code>article</code> */
    Tag ARTICLE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "article";
        }
    };

    /** DocBook tag for <code>articleinfo</code> */
    Tag ARTICLEINFO_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "articleinfo";
        }
    };

    /** DocBook tag for <code>book</code> */
    Tag BOOK_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "book";
        }
    };

    /** DocBook tag for <code>bookinfo</code> */
    Tag BOOKINFO_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "bookinfo";
        }
    };

    /** DocBook tag for <code>chapter</code> */
    Tag CHAPTER_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "chapter";
        }
    };

    /** DocBook tag for <code>colspec</code> */
    Tag COLSPEC_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "colspec";
        }
    };

    /** DocBook tag for <code>corpauthor</code> */
    Tag CORPAUTHOR_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "corpauthor";
        }
    };

    /** DocBook tag for <code>date</code> */
    Tag DATE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "date";
        }
    };

    /** DocBook tag for <code>email</code> */
    Tag EMAIL_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "email";
        }
    };

    /** DocBook tag for <code>entry</code> */
    Tag ENTRY_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "entry";
        }
    };

    /** DocBook tag for <code>figure</code> */
    Tag FIGURE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "figure";
        }
    };

    /** DocBook tag for <code>formalpara</code> */
    Tag FORMALPARA_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "formalpara";
        }
    };

    /** DocBook tag for <code>imagedata</code> */
    Tag IMAGEDATA_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "imagedata";
        }
    };

    /** DocBook tag for <code>imageobject</code> */
    Tag IMAGEOBJECT_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "imageobject";
        }
    };

    /** DocBook tag for <code>info</code> */
    Tag INFO_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "info";
        }
    };

    /** DocBook tag for <code>informalfigure</code> */
    Tag INFORMALFIGURE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "informalfigure";
        }
    };

    /** DocBook tag for <code>informaltable</code> */
    Tag INFORMALTABLE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "informaltable";
        }
    };

    /** DocBook tag for <code>itemizedlist</code> */
    Tag ITEMIZEDLIST_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "itemizedlist";
        }
    };

    /** DocBook tag for <code>link</code> */
    Tag LINK_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "link";
        }
    };

    /** DocBook tag for <code>listitem</code> */
    Tag LISTITEM_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "listitem";
        }
    };

    /** DocBook tag for <code>mediaobject</code> */
    Tag MEDIAOBJECT_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "mediaobject";
        }
    };

    /** DocBook tag for <code>orderedlist</code> */
    Tag ORDEREDLIST_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "orderedlist";
        }
    };

    /** DocBook tag for <code>para</code> */
    Tag PARA_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "para";
        }
    };

    /** DocBook tag for <code>programlisting</code> */
    Tag PROGRAMLISTING_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "programlisting";
        }
    };

    /** DocBook tag for <code>row</code> */
    Tag ROW_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "row";
        }
    };

    /** DocBook tag for <code>section</code> */
    Tag SECTION_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "section";
        }
    };

    /** DocBook tag for <code>simpara</code> */
    Tag SIMPARA_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "simpara";
        }
    };

    /** DocBook tag for <code>tbody</code> */
    Tag TBODY_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "tbody";
        }
    };

    /** DocBook tag for <code>term</code> */
    Tag TERM_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "term";
        }
    };

    /** DocBook tag for <code>tgroup</code> */
    Tag TGROUP_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "tgroup";
        }
    };

    /** DocBook tag for <code>thead</code> */
    Tag THEAD_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "thead";
        }
    };

    /** DocBook tag for <code>ulink</code> */
    Tag ULINK_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "ulink";
        }
    };

    /** DocBook tag for <code>url</code> */
    Tag URL_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "url";
        }
    };

    /** DocBook tag for <code>variablelist</code> */
    Tag VARIABLELIST_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "variablelist";
        }
    };

    /** DocBook tag for <code>varlistentry</code> */
    Tag VARLISTENTRY_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "varlistentry";
        }
    };

    /** DocBook tag for <code>xref</code> */
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

    /** DocBook attribute <code>cols</code> used in <code>tgroup</code> tags */
    String COLS_ATTRIBUTE = "cols";

    /** DocBook attribute <code>colsep</code> used in <code>informaltable</code> tags */
    String COLSEP_ATTRIBUTE = "colsep";

    /** DocBook attribute <code>fileref</code> used in <code>orderedlist, imageobject</code> tags */
    String FILEREF_ATTRIBUTE = "fileref";

    /** DocBook attribute <code>format</code> used in <code>imagedata</code> tags */
    String FORMAT_ATTRIBUTE = "format";

    /** DocBook attribute <code>frame</code> used in <code>informaltable</code> tags */
    String FRAME_ATTRIBUTE = "frame";

    /** DocBook attribute <code>linkend</code> used in <code>link</code> tag */
    String LINKEND_ATTRIBUTE = "linkend";

    /** DocBook attribute <code>numeration</code> used in <code>orderedlist</code> tag */
    String NUMERATION_ATTRIBUTE = "numeration";

    /** DocBook attribute <code>rowsep</code> used in <code>informaltable</code> tags */
    String ROWSEP_ATTRIBUTE = "rowsep";

    /** DocBook attribute <code>url</code> used in <code>ulink</code> tags */
    String URL_ATTRIBUTE = "url";

    // ----------------------------------------------------------------------
    // Specific Docbook styles
    // ----------------------------------------------------------------------

    /** Docbook style <code>arabic</code> used in <code>numeration</code> attribute */
    String ARABIC_STYLE = "arabic";

    /** DocBook style <code>loweralpha</code> used in <code>numeration</code> attribute */
    String LOWERALPHA_STYLE = "loweralpha";

    /** DocBook style <code>lowerroman</code> used in <code>numeration</code> attribute */
    String LOWERROMAN_STYLE = "lowerroman";

    /** DocBook style <code>upperalpha</code> used in <code>numeration</code> attribute */
    String UPPERALPHA_STYLE = "upperalpha";

    /** DocBook style <code>upperroman</code> used in <code>numeration</code> attribute */
    String UPPERROMAN_STYLE = "upperroman";
}
