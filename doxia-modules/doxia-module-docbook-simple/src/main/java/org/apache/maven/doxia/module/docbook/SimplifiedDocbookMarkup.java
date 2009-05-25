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
 * List of <code>Simplified DocBook</code> markups.
 * Contains all 119 elements of the
 * <a href="http://www.docbook.org/schemas/sdocbook/elements.html">Simplified DocBook DTD v. 1.1</a>.
 *
 * @author ltheussl
 * @version $Id$
 * @since 1.1.1
 */
public interface SimplifiedDocbookMarkup
    extends XmlMarkup
{
    /** DocBook XML V1.1 XML public identifier: "-//OASIS//DTD Simplified DocBook XML V1.1//EN". */
    String DEFAULT_XML_PUBLIC_ID = "-//OASIS//DTD Simplified DocBook XML V1.1//EN";

    /** DocBook XML V1.1 XML system identifier: "http://www.oasis-open.org/docbook/xml/simple/1.1/sdocbook.dtd". */
    String DEFAULT_XML_SYSTEM_ID = "http://www.oasis-open.org/docbook/xml/simple/1.1/sdocbook.dtd";

    // ----------------------------------------------------------------------
    // 119 Simplified DocBook elements
    // ----------------------------------------------------------------------

    /** DocBook tag for <code>abbrev</code>. */
    Tag ABBREV_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "abbrev";
        }
    };

    /** DocBook tag for <code>abstract</code>. */
    Tag ABSTRACT_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "abstract";
        }
    };

    /** DocBook tag for <code>acronym</code>. */
    Tag ACRONYM_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "acronym";
        }
    };

    /** DocBook tag for <code>affiliation</code>. */
    Tag AFFILIATION_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "affiliation";
        }
    };

    /** DocBook tag for <code>anchor</code>. */
    Tag ANCHOR_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "anchor";
        }
    };

    /** DocBook tag for <code>appendix</code>. */
    Tag APPENDIX_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "appendix";
        }
    };

    /** DocBook tag for <code>article</code>. */
    Tag ARTICLE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "article";
        }
    };

    /** DocBook tag for <code>articleinfo</code>. */
    Tag ARTICLEINFO_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "articleinfo";
        }
    };

    /** DocBook tag for <code>attribution</code>. */
    Tag ATTRIBUTION_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "attribution";
        }
    };

    /** DocBook tag for <code>audiodata</code>. */
    Tag AUDIODATA_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "audiodata";
        }
    };

    /** DocBook tag for <code>audioobject</code>. */
    Tag AUDIOOBJECT_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "audioobject";
        }
    };

    /** DocBook tag for <code>author</code>. */
    Tag AUTHOR_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "author";
        }
    };

    /** DocBook tag for <code>authorblurb</code>. */
    Tag AUTHORBLURB_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "authorblurb";
        }
    };

    /** DocBook tag for <code>authorgroup</code>. */
    Tag AUTHORGROUP_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "authorgroup";
        }
    };

    /** DocBook tag for <code>authorinitials</code>. */
    Tag AUTHORINITIALS_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "authorinitials";
        }
    };

    /** DocBook tag for <code>bibliodiv</code>. */
    Tag BIBLIODIV_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "bibliodiv";
        }
    };

    /** DocBook tag for <code>bibliography</code>. */
    Tag BIBLIOGRAPHY_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "bibliography";
        }
    };

    /** DocBook tag for <code>bibliomisc</code>. */
    Tag BIBLIOMISC_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "bibliomisc";
        }
    };

    /** DocBook tag for <code>bibliomixed</code>. */
    Tag BIBLIOMIXED_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "bibliomixed";
        }
    };

    /** DocBook tag for <code>bibliomset</code>. */
    Tag BIBLIOMSET_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "bibliomset";
        }
    };

    /** DocBook tag for <code>blockquote</code>. */
    Tag BLOCKQUOTE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "blockquote";
        }
    };

    /** DocBook tag for <code>caption</code>. */
    Tag CAPTION_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "caption";
        }
    };

    /** DocBook tag for <code>citetitle</code>. */
    Tag CITETITLE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "citetitle";
        }
    };

    /** DocBook tag for <code>col</code>. */
    Tag COL_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "col";
        }
    };

    /** DocBook tag for <code>colgroup</code>. */
    Tag COLGROUP_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "colgroup";
        }
    };

    /** DocBook tag for <code>colspec</code>. */
    Tag COLSPEC_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "colspec";
        }
    };

    /** DocBook tag for <code>command</code>. */
    Tag COMMAND_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "command";
        }
    };

    /** DocBook tag for <code>computeroutput</code>. */
    Tag COMPUTEROUTPUT_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "computeroutput";
        }
    };

    /** DocBook tag for <code>copyright</code>. */
    Tag COPYRIGHT_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "copyright";
        }
    };

    /** DocBook tag for <code>corpauthor</code>. */
    Tag CORPAUTHOR_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "corpauthor";
        }
    };

    /** DocBook tag for <code>date</code>. */
    Tag DATE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "date";
        }
    };

    /** DocBook tag for <code>edition</code>. */
    Tag EDITION_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "edition";
        }
    };

    /** DocBook tag for <code>editor</code>. */
    Tag EDITOR_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "editor";
        }
    };

    /** DocBook tag for <code>email</code>. */
    Tag EMAIL_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "email";
        }
    };

    /** DocBook tag for <code>emphasis</code>. */
    Tag EMPHASIS_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "emphasis";
        }
    };

    /** DocBook tag for <code>entry</code>. */
    Tag ENTRY_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "entry";
        }
    };

    /** DocBook tag for <code>entrytbl</code>. */
    Tag ENTRYTBL_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "entrytbl";
        }
    };

    /** DocBook tag for <code>epigraph</code>. */
    Tag EPIGRAPH_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "epigraph";
        }
    };

    /** DocBook tag for <code>example</code>. */
    Tag EXAMPLE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "example";
        }
    };

    /** DocBook tag for <code>figure</code>. */
    Tag FIGURE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "figure";
        }
    };

    /** DocBook tag for <code>filename</code>. */
    Tag FILENAME_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "filename";
        }
    };

    /** DocBook tag for <code>firstname</code>. */
    Tag FIRSTNAME_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "firstname";
        }
    };

    /** DocBook tag for <code>footnote</code>. */
    Tag FOOTNOTE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "footnote";
        }
    };

    /** DocBook tag for <code>footnoteref</code>. */
    Tag FOOTNOTEREF_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "footnoteref";
        }
    };

    /** DocBook tag for <code>holder</code>. */
    Tag HOLDER_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "holder";
        }
    };

    /** DocBook tag for <code>honorific</code>. */
    Tag HONORIFIC_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "honorific";
        }
    };

    /** DocBook tag for <code>imagedata</code>. */
    Tag IMAGEDATA_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "imagedata";
        }
    };

    /** DocBook tag for <code>imageobject</code>. */
    Tag IMAGEOBJECT_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "imageobject";
        }
    };

    /** DocBook tag for <code>informaltable</code>. */
    Tag INFORMALTABLE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "informaltable";
        }
    };

    /** DocBook tag for <code>inlinemediaobject</code>. */
    Tag INLINEMEDIAOBJECT_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "inlinemediaobject";
        }
    };

    /** DocBook tag for <code>issuenum</code>. */
    Tag ISSUENUM_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "issuenum";
        }
    };

    /** DocBook tag for <code>itemizedlist</code>. */
    Tag ITEMIZEDLIST_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "itemizedlist";
        }
    };

    /** DocBook tag for <code>jobtitle</code>. */
    Tag JOBTITLE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "jobtitle";
        }
    };

    /** DocBook tag for <code>keyword</code>. */
    Tag KEYWORD_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "keyword";
        }
    };

    /** DocBook tag for <code>keywordset</code>. */
    Tag KEYWORDSET_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "keywordset";
        }
    };

    /** DocBook tag for <code>legalnotice</code>. */
    Tag LEGALNOTICE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "legalnotice";
        }
    };

    /** DocBook tag for <code>lineage</code>. */
    Tag LINEAGE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "lineage";
        }
    };

    /** DocBook tag for <code>lineannotation</code>. */
    Tag LINEANNOTATION_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "lineannotation";
        }
    };

    /** DocBook tag for <code>link</code>. */
    Tag LINK_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "link";
        }
    };

    /** DocBook tag for <code>listitem</code>. */
    Tag LISTITEM_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "listitem";
        }
    };

    /** DocBook tag for <code>literal</code>. */
    Tag LITERAL_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "literal";
        }
    };

    /** DocBook tag for <code>literallayout</code>. */
    Tag LITERALLAYOUT_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "literallayout";
        }
    };

    /** DocBook tag for <code>mediaobject</code>. */
    Tag MEDIAOBJECT_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "mediaobject";
        }
    };

    /** DocBook tag for <code>note</code>. */
    Tag NOTE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "note";
        }
    };

    /** DocBook tag for <code>objectinfo</code>. */
    Tag OBJECTINFO_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "objectinfo";
        }
    };

    /** DocBook tag for <code>option</code>. */
    Tag OPTION_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "option";
        }
    };

    /** DocBook tag for <code>orderedlist</code>. */
    Tag ORDEREDLIST_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "orderedlist";
        }
    };

    /** DocBook tag for <code>orgname</code>. */
    Tag ORGNAME_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "orgname";
        }
    };

    /** DocBook tag for <code>othercredit</code>. */
    Tag OTHERCREDIT_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "othercredit";
        }
    };

    /** DocBook tag for <code>othername</code>. */
    Tag OTHERNAME_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "othername";
        }
    };

    /** DocBook tag for <code>para</code>. */
    Tag PARA_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "para";
        }
    };

    /** DocBook tag for <code>phrase</code>. */
    Tag PHRASE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "phrase";
        }
    };

    /** DocBook tag for <code>programlisting</code>. */
    Tag PROGRAMLISTING_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "programlisting";
        }
    };

    /** DocBook tag for <code>pubdate</code>. */
    Tag PUBDATE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "pubdate";
        }
    };

    /** DocBook tag for <code>publishername</code>. */
    Tag PUBLISHERNAME_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "publishername";
        }
    };

    /** DocBook tag for <code>quote</code>. */
    Tag QUOTE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "quote";
        }
    };

    /** DocBook tag for <code>releaseinfo</code>. */
    Tag RELEASEINFO_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "releaseinfo";
        }
    };

    /** DocBook tag for <code>replaceable</code>. */
    Tag REPLACEABLE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "replaceable";
        }
    };

    /** DocBook tag for <code>revdescription</code>. */
    Tag REVDESCRIPTION_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "revdescription";
        }
    };

    /** DocBook tag for <code>revhistory</code>. */
    Tag REVHISTORY_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "revhistory";
        }
    };

    /** DocBook tag for <code>revision</code>. */
    Tag REVISION_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "revision";
        }
    };

    /** DocBook tag for <code>revnumber</code>. */
    Tag REVNUMBER_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "revnumber";
        }
    };

    /** DocBook tag for <code>revremark</code>. */
    Tag REVREMARK_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "revremark";
        }
    };

    /** DocBook tag for <code>row</code>. */
    Tag ROW_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "row";
        }
    };

    /** DocBook tag for <code>section</code>. */
    Tag SECTION_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "section";
        }
    };

    /** DocBook tag for <code>sectioninfo</code>. */
    Tag SECTIONINFO_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "sectioninfo";
        }
    };

    /** DocBook tag for <code>sidebar</code>. */
    Tag SIDEBAR_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "sidebar";
        }
    };

    /** DocBook tag for <code>spanspec</code>. */
    Tag SPANSPEC_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "spanspec";
        }
    };

    /** DocBook tag for <code>subject</code>. */
    Tag SUBJECT_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "subject";
        }
    };

    /** DocBook tag for <code>subjectset</code>. */
    Tag SUBJECTSET_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "subjectset";
        }
    };

    /** DocBook tag for <code>subjectterm</code>. */
    Tag SUBJECTTERM_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "subjectterm";
        }
    };

    /** DocBook tag for <code>subscript</code>. */
    Tag SUBSCRIPT_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "subscript";
        }
    };

    /** DocBook tag for <code>subtitle</code>. */
    Tag SUBTITLE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "subtitle";
        }
    };

    /** DocBook tag for <code>superscript</code>. */
    Tag SUPERSCRIPT_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "superscript";
        }
    };

    /** DocBook tag for <code>surname</code>. */
    Tag SURNAME_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "surname";
        }
    };

    /** DocBook tag for <code>systemitem</code>. */
    Tag SYSTEMITEM_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "systemitem";
        }
    };

    /** DocBook tag for <code>table</code>. */
    Tag TABLE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "table";
        }
    };

    /** DocBook tag for <code>tbody</code>. */
    Tag TBODY_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "tbody";
        }
    };

    /** DocBook tag for <code>td</code>. */
    Tag TD_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "td";
        }
    };

    /** DocBook tag for <code>term</code>. */
    Tag TERM_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "term";
        }
    };

    /** DocBook tag for <code>textdata</code>. */
    Tag TEXTDATA_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "textdata";
        }
    };

    /** DocBook tag for <code>textobject</code>. */
    Tag TEXTOBJECT_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "textobject";
        }
    };

    /** DocBook tag for <code>tfoot</code>. */
    Tag TFOOT_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "tfoot";
        }
    };

    /** DocBook tag for <code>tgroup</code>. */
    Tag TGROUP_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "tgroup";
        }
    };

    /** DocBook tag for <code>th</code>. */
    Tag TH_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "th";
        }
    };

    /** DocBook tag for <code>thead</code>. */
    Tag THEAD_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "thead";
        }
    };

    /** DocBook tag for <code>title</code>. */
    Tag TITLE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "title";
        }
    };

    /** DocBook tag for <code>titleabbrev</code>. */
    Tag TITLEABBREV_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "titleabbrev";
        }
    };

    /** DocBook tag for <code>tr</code>. */
    Tag TR_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "tr";
        }
    };

    /** DocBook tag for <code>trademark</code>. */
    Tag TRADEMARK_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "trademark";
        }
    };

    /** DocBook tag for <code>ulink</code>. */
    Tag ULINK_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "ulink";
        }
    };

    /** DocBook tag for <code>userinput</code>. */
    Tag USERINPUT_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "userinput";
        }
    };

    /** DocBook tag for <code>variablelist</code>. */
    Tag VARIABLELIST_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "variablelist";
        }
    };

    /** DocBook tag for <code>varlistentry</code>. */
    Tag VARLISTENTRY_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "varlistentry";
        }
    };

    /** DocBook tag for <code>videodata</code>. */
    Tag VIDEODATA_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "videodata";
        }
    };

    /** DocBook tag for <code>videoobject</code>. */
    Tag VIDEOOBJECT_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "videoobject";
        }
    };

    /** DocBook tag for <code>volumenum</code>. */
    Tag VOLUMENUM_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "volumenum";
        }
    };

    /** DocBook tag for <code>xref</code>. */
    Tag XREF_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "xref";
        }
    };

    /** DocBook tag for <code>year</code>. */
    Tag YEAR_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "year";
        }
    };

    // ----------------------------------------------------------------------
    // Common Docbook attributes
    // ----------------------------------------------------------------------

    /** Common DocBook attribute <code>id</code>. */
    String ID_ATTRIBUTE = "id";

    /** Common DocBook attribute <code>lang</code>. */
    String LANG_ATTRIBUTE = "lang";

    /** Common DocBook attribute <code>remap</code>. */
    String REMAP_ATTRIBUTE = "remap";

    /** Common DocBook attribute <code>revisionflag</code>. */
    String REVISIONFLAG_ATTRIBUTE = "revisionflag";

    // ----------------------------------------------------------------------
    // Specific Docbook attributes
    // ----------------------------------------------------------------------

    /** DocBook attribute <code>cols</code> used in <code>tgroup</code> tag. */
    String COLS_ATTRIBUTE = "cols";

    /** DocBook attribute <code>colsep</code> used in <code>informaltable</code> tag. */
    String COLSEP_ATTRIBUTE = "colsep";

    /** DocBook attribute <code>fileref</code> used in <code>orderedlist, imageobject</code> tag. */
    String FILEREF_ATTRIBUTE = "fileref";

    /** DocBook attribute <code>format</code> used in <code>imagedata</code> tag. */
    String FORMAT_ATTRIBUTE = "format";

    /** DocBook attribute <code>frame</code> used in <code>informaltable</code> tag. */
    String FRAME_ATTRIBUTE = "frame";

    /** DocBook attribute <code>linkend</code> used in <code>link</code> tag. */
    String LINKEND_ATTRIBUTE = "linkend";

    /** DocBook attribute <code>numeration</code> used in <code>orderedlist</code> tag. */
    String NUMERATION_ATTRIBUTE = "numeration";

    /** DocBook attribute <code>rowsep</code> used in <code>informaltable</code> tag. */
    String ROWSEP_ATTRIBUTE = "rowsep";

    /** DocBook attribute <code>url</code> used in <code>ulink</code> tag. */
    String URL_ATTRIBUTE = "url";

    // ----------------------------------------------------------------------
    // Specific Docbook styles
    // ----------------------------------------------------------------------

    /** Docbook style <code>arabic</code> used in <code>numeration</code> attribute. */
    String ARABIC_STYLE = "arabic";

    /** DocBook style <code>loweralpha</code> used in <code>numeration</code> attribute. */
    String LOWERALPHA_STYLE = "loweralpha";

    /** DocBook style <code>lowerroman</code> used in <code>numeration</code> attribute. */
    String LOWERROMAN_STYLE = "lowerroman";

    /** DocBook style <code>upperalpha</code> used in <code>numeration</code> attribute. */
    String UPPERALPHA_STYLE = "upperalpha";

    /** DocBook style <code>upperroman</code> used in <code>numeration</code> attribute. */
    String UPPERROMAN_STYLE = "upperroman";
}
