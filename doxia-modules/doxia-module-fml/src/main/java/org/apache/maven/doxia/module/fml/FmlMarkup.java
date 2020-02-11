package org.apache.maven.doxia.module.fml;

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
 * List of <code>FML</code> markups.
 * <br>
 * FML uses several  {@link javax.swing.text.html.HTML.Tag} and {@link javax.swing.text.html.HTML.Attribute}
 * as markups and custom tags.
 *
 * @author ltheussl
 * @since 1.0
 */
@SuppressWarnings( "checkstyle:interfaceistype" )
public interface FmlMarkup
    extends HtmlMarkup
{
    /** FML namespace: "http://maven.apache.org/FML/1.0.1" */
    String FML_NAMESPACE = "http://maven.apache.org/FML/1.0.1";

    /** FML system id: "https://maven.apache.org/xsd/fml-1.0.1.xsd" */
    String FML_SYSTEM_ID = "https://maven.apache.org/xsd/fml-1.0.1.xsd";

    // ----------------------------------------------------------------------
    // Specific Fml tags
    // ----------------------------------------------------------------------

    /** Fml tag for <code>faqs</code> */
    Tag FAQS_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "faqs";
        }
    };

    /** Fml tag for <code>part</code> */
    Tag PART_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "part";
        }
    };

    /** Fml tag for <code>faq</code> */
    Tag FAQ_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "faq";
        }
    };

    /** Fml tag for <code>question</code> */
    Tag QUESTION_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "question";
        }
    };

    /** Fml tag for <code>answer</code> */
    Tag ANSWER_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "answer";
        }
    };

    /** Fml tag for <code>source</code> */
    Tag SOURCE_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "source";
        }
    };

    /**
     * Fml tag for <code>macro</code>
     * @since 1.1.1
     */
    Tag MACRO_TAG = new Tag()
    {
        /** {@inheritDoc} */
        public String toString()
        {
            return "macro";
        }
    };
}
