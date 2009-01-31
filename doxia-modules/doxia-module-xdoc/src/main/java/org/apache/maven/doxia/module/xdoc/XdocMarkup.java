package org.apache.maven.doxia.module.xdoc;

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

import org.apache.maven.doxia.markup.HtmlMarkup;

/**
 * List of <code>Xdoc</code> markups.
 * <br/>
 * Xdoc uses several  {@link javax.swing.text.html.HTML.Tag} and {@link javax.swing.text.html.HTML.Attribute}
 * as markups and custom tags.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public interface XdocMarkup
    extends HtmlMarkup
{
    /** XDOC namespace: "http://maven.apache.org/XDOC/2.0" */
    String XDOC_NAMESPACE = "http://maven.apache.org/XDOC/2.0";

    /** XDOC system id: "http://maven.apache.org/xsd/xdoc-2.0.xsd" */
    String XDOC_SYSTEM_ID = "http://maven.apache.org/xsd/xdoc-2.0.xsd";

    // ----------------------------------------------------------------------
    // Specific Xdoc tags
    // ----------------------------------------------------------------------

    /** Xdoc tag for <code>author</code> */
    Tag AUTHOR_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "author";
        }
    };

    /** Xdoc tag for <code>date</code> */
    Tag DATE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "date";
        }
    };

    /** Xdoc tag for <code>document</code> */
    Tag DOCUMENT_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "document";
        }
    };

    /** Xdoc tag for <code>macro</code> */
    Tag MACRO_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "macro";
        }
    };

    /** Xdoc tag for <code>properties</code> */
    Tag PROPERTIES_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "properties";
        }
    };

    /** Xdoc tag for <code>section</code> */
    Tag SECTION_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "section";
        }
    };

    /** Xdoc tag for <code>source</code> */
    Tag SOURCE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "source";
        }
    };

    /** Xdoc tag for <code>subsection</code> */
    Tag SUBSECTION_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "subsection";
        }
    };
}
