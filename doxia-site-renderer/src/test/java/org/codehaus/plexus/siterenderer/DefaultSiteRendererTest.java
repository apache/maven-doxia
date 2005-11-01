package org.codehaus.plexus.siterenderer;

import org.codehaus.plexus.PlexusTestCase;

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

/**
 * @author <a href="mailto:evenisse@codehaus.org>Emmanuel Venisse</a>
 * @version $Id$
 */
public class DefaultSiteRendererTest
    extends PlexusTestCase
{
    private Renderer renderer;

    public DefaultSiteRendererTest()
    {
        super();
    }

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
    {
        try
        {
            renderer.render( getTestFile( "src/test/site" ), getTestFile( "target/output" ),
                             getTestFile( "src/test/site/site.xml" ), "maven-site.vm", null );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }
}
