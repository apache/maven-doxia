package org.apache.maven.doxia.module.xhtml.decoration.model;/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author <a href="mailto:brett@codehaus.org">Brett Porter</a>
 * @version $Id$
 */
public class Image
{
    private String src;

    private String alt;

    private String title;

    private Link link;

    public Image()
    {
    }

    public String getSrc()
    {
        return src;
    }

    public void setSrc( String src )
    {
        this.src = src;
    }

    public Link getLink()
    {
        return link;
    }

    public void setLink( Link link )
    {
        this.link = link;
    }

    public String getAlt()
    {
        return alt;
    }

    public void setAlt( String alt )
    {
        this.alt = alt;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }
}
