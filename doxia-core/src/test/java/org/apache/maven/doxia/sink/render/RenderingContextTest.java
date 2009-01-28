package org.apache.maven.doxia.sink.render;

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

import java.io.File;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * @since 20 oct. 07
 * @version $Id$
 */
public class RenderingContextTest
    extends PlexusTestCase
{

    public void testFileNameWithDot()
        throws Exception
    {
        File baseDir = new File( getBasedir() + File.separatorChar + "test" + File.separatorChar + "resources" );
        String docName = "file.with.dot.in.name.xml";

        RenderingContext renderingContext = new RenderingContext( baseDir, docName, "", "xml" );
        assertEquals( "file.with.dot.in.name.html", renderingContext.getOutputName() );
        assertEquals( ".", renderingContext.getRelativePath() );

        docName = "index.xml.vm";

        renderingContext = new RenderingContext( baseDir, docName, "", "xml" );
        assertEquals( "index.html", renderingContext.getOutputName() );
        assertEquals( ".", renderingContext.getRelativePath() );

        docName = "download.apt.vm";

        renderingContext = new RenderingContext( baseDir, docName, "", "apt" );
        assertEquals( "download.html", renderingContext.getOutputName() );
        assertEquals( ".", renderingContext.getRelativePath() );
    }

}
