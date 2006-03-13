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
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:evenisse@codehaus.org>Emmanuel Venisse</a>
 * @version $Id:Renderer.java 348612 2005-11-24 12:54:19 +1100 (Thu, 24 Nov 2005) brett $
 */
public interface Renderer
{
    String ROLE = Renderer.class.getName();

    void render( File siteDirectory, File outputDirectory, SiteRenderingContext context )
        throws RendererException, IOException;

    void render( File siteDirectory, File outputDirectory, SiteRenderingContext context, String outputEncoding )
        throws RendererException, IOException;

    void render( File moduleBasedir, File outputDirectory, String module, String moduleExtension, String moduleParserId,
                 SiteRenderingContext context, String outputEncoding )
        throws RendererException, IOException;

    void generateDocument( Writer writer, SiteRendererSink sink, SiteRenderingContext siteContext )
        throws RendererException;

    SiteRendererSink createSink( File moduleBaseDir, String document )
        throws RendererException, IOException;

    SiteRenderingContext createContextForSkin( File skinFile, Map attributes, DecorationModel decoration, Locale locale,
                                               String defaultWindowTitle, File resourcesDirectory )
        throws IOException;

    SiteRenderingContext createContextForTemplate( File templateFile, Map attributes, DecorationModel decoration,
                                                   Locale locale, String defaultWindowTitle, File skinFile,
                                                   File resourcesDirectory )
        throws MalformedURLException;

    void copyResources( File outputDirectory, SiteRenderingContext siteContext )
        throws IOException;
}
