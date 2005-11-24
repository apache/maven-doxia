package org.apache.maven.doxia.module.docbook;

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

import org.apache.maven.doxia.site.module.AbstractSiteModule;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 * @plexus.component role="org.apache.maven.doxia.site.module.SiteModule"
 * role-hint="doc-book"
 */
public class DocBookSiteModule
    extends AbstractSiteModule
{
    public String getSourceDirectory()
    {
        return "docbook";
    }

    public String getExtension()
    {
        return "xml";
    }

    public String getParserId()
    {
        return "doc-book";
    }
}
