package org.apache.maven.doxia.siterenderer;

/*
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

import org.apache.maven.doxia.site.decoration.DecorationModel;

import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id:DefaultSiteRenderer.java 348612 2005-11-24 12:54:19 +1100 (Thu, 24 Nov 2005) brett $
 */
public class SiteRenderingContext
{
    private String template;

    private Map templateProperties;

    private ClassLoader templateClassLoader;

    private Locale locale = Locale.getDefault();

    private DecorationModel decoration;

    public String getTemplate()
    {
        return template;
    }

    public void setTemplate( String template )
    {
        this.template = template;
    }

    public Map getTemplateProperties()
    {
        return templateProperties;
    }

    public void setTemplateProperties( Map templateProperties )
    {
        this.templateProperties = templateProperties;
    }

    public ClassLoader getTemplateClassLoader()
    {
        return templateClassLoader;
    }

    public void setTemplateClassLoader( ClassLoader templateClassLoader )
    {
        this.templateClassLoader = templateClassLoader;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public void setLocale( Locale locale )
    {
        this.locale = locale;
    }

    public DecorationModel getDecoration()
    {
        return decoration;
    }

    public void setDecoration( DecorationModel decoration )
    {
        this.decoration = decoration;
    }
}
