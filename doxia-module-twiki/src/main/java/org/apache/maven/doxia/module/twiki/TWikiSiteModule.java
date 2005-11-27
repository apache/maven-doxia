/*
 *  Copyright 2005 Zauber <info /at/ zauber dot com dot ar>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.maven.doxia.module.twiki;

import org.apache.maven.doxia.site.module.SiteModule;

/**
 * {@link org.apache.maven.doxia.site.module.SiteModule} implementation for
 * the twiki file format.
 *
 * @author Juan F. Codagnone
 * @plexus.component role="org.apache.doxia.site.module.SiteModule"
 * role-hint="twiki"
 * @since Oct 31, 2005
 */
public final class TWikiSiteModule implements SiteModule
{

    /**
     * @see SiteModule#getSourceDirectory()
     */
    public String getSourceDirectory()
    {
        return "twiki";
    }

    /**
     * @see SiteModule#getExtension()
     */
    public String getExtension()
    {
        return "twiki";
    }

    /**
     * @see SiteModule#getParserId()
     */
    public String getParserId()
    {
        return "twiki";
    }

}
