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
import org.apache.maven.doxia.site.decoration.io.xpp3.DecorationXpp3Reader;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileReader;
import java.io.IOException;

/**
 * @author <a href="mailto:evenisse@codehaus.org>Emmanuel Venisse</a>
 * @version $Id:DefaultSiteRendererTest.java 348612 2005-11-24 12:54:19 +1100 (Thu, 24 Nov 2005) brett $
 */
public class DefaultSiteRendererTest
    extends PlexusTestCase
{
    private Renderer renderer;

    /**
     * @see org.codehaus.plexus.PlexusTestCase#setUp()
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();
        renderer = (Renderer) lookup( Renderer.ROLE );
    }

    /**
     * @see org.codehaus.plexus.PlexusTestCase#tearDown()
     */
    protected void tearDown()
        throws Exception
    {
        release( renderer );
        super.tearDown();
    }

    public void testRender()
        throws RendererException, IOException, XmlPullParserException
    {
        DecorationModel decoration =
            new DecorationXpp3Reader().read( new FileReader( getTestFile( "src/test/site/site.xml" ) ) );
        SiteRenderingContext context = new SiteRenderingContext();
        context.setTemplateName( "default-site.vm" );
        context.setTemplateClassLoader( getClassLoader() );
        context.setDecoration( decoration );
        renderer.render( getTestFile( "src/test/site" ), getTestFile( "target/output" ), context );
    }
}
