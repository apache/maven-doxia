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

/** Encapsulates a model of a collection of FAQs. */
public class Faqs
{
    /** Whether to create toplinks. */
    private boolean toplink = true;

    /** The FAQ title. */
    private String title = "FAQ";

     /** The FAQ parts. */
    private List parts;

    /**
     * Returns the parts of this FAQ.
     *
     * @return the parts.
     */
    public List getParts()
    {
        return parts;
    }

    /**
     * Sets the parts of this FAQ.
     *
     * @param newParts the parts to set.
     */
    public void setParts( List newParts )
    {
        this.parts = newParts;
    }

    /**
     * Adds the given Part to the parts of this FAQ.
     *
     * @param part the part to add.
     */
    public void addPart( Part part )
    {
        if ( parts == null )
        {
            parts = new ArrayList();
        }

        parts.add( part );
    }

    /**
     * Returns the title of this FAQ. Defaults to "FAQ".
     *
     * @return the title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the title of this FAQ.
     *
     * @param newTitle the title to set.
     */
    public void setTitle( String newTitle )
    {
        this.title = newTitle;
    }

    /**
     * Sets the toplink of this FAQ.
     *
     * @param newToplink the toplink to set.
     */
    public void setToplink( boolean newToplink )
    {
        this.toplink = newToplink;
    }

    /**
     * Whether to create toplinks. Defaults to true.
     *
     * @return True if toplinks are created.
     */
    public boolean isToplink()
    {
        return toplink;
    }
}
