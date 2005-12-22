package org.apache.maven.doxia.module.xhtml.decoration.render;

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

import org.codehaus.plexus.util.PathTool;

import java.io.File;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:RenderingContext.java 348605 2005-11-24 12:02:44 +1100 (Thu, 24 Nov 2005) brett $
 */
public class RenderingContext
{
    private File basedir;

    private String relativePath;

    private String outputName;

    public RenderingContext( File basedir, String document )
    {
        this.basedir = basedir;

        // For site comparisons we'll prepend a "/"
        this.outputName = "/" + document.substring( 0, document.lastIndexOf( "." ) + 1 ) + "html";

        relativePath = PathTool.getRelativePath( basedir.getPath(), new File( basedir, document ).getPath() );
    }

    public File getBasedir()
    {
        return basedir;
    }

    public String getRelativePath()
    {
        return relativePath;
    }

    public String getOutputName()
    {
        return outputName;
    }

}
