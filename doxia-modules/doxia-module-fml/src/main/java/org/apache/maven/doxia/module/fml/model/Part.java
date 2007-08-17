package org.apache.maven.doxia.module.fml.model;

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

import java.util.ArrayList;
import java.util.List;

/** Encapsulates a model of a part of a FAQ. */
public class Part
{
    /** An id for the part. */
    private String id;

    /** The title of the part. */
    private String title;

    /** The list of FAQs. */
    private List faqs;

    /**
     * Adds the given Faq to the FAQs of this Part.
     *
     * @param faq the faq to add.
     */
    public void addFaq( Faq faq )
    {
        if ( faqs == null )
        {
            faqs = new ArrayList();
        }

        faqs.add( faq );
    }

    /**
     * Returns the faqs of this Part.
     *
     * @return the faqs.
     */
    public List getFaqs()
    {
        return faqs;
    }

    /**
     * Sets the list of FAQs of this Part.
     *
     * @param newFaqs the faqs to set.
     */
    public void setFaqs( List newFaqs )
    {
        this.faqs = newFaqs;
    }

    /**
     * Returns the id of this Part.
     *
     * @return the id.
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * Sets the id of this Part.
     *
     * @param newId the id to set.
     */
    public void setId( String newId )
    {
        this.id = newId;
    }

    /**
     * Returns the title of this Part.
     *
     * @return the title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the title of this Part.
     *
     * @param newTitle the title to set.
     */
    public void setTitle( String newTitle )
    {
        this.title = newTitle;
    }
}
