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

import org.apache.maven.doxia.site.module.AbstractSiteModule;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id:FmlSiteModule.java 348605 2005-11-24 12:02:44 +1100 (Thu, 24 Nov 2005) brett $
 * @plexus.component role="org.apache.maven.doxia.site.module.SiteModule" role-hint="fml"
 */
public class FmlSiteModule
    extends AbstractSiteModule
{

    public String getSourceDirectory()
    {
        return "fml";
    }

    public String getExtension()
    {
        return "fml";
    }

    public String getParserId()
    {
        return "fml";
    }

}
