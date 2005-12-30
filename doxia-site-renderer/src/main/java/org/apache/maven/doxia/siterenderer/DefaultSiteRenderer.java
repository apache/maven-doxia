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

import org.apache.maven.doxia.Doxia;
import org.apache.maven.doxia.module.xhtml.decoration.render.RenderingContext;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.manager.ParserNotFoundException;
import org.apache.maven.doxia.site.module.SiteModule;
import org.apache.maven.doxia.site.module.manager.SiteModuleManager;
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.PathTool;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.velocity.VelocityComponent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id:DefaultSiteRenderer.java 348612 2005-11-24 12:54:19 +1100 (Thu, 24 Nov 2005) brett $
 * @plexus.component role="org.apache.maven.doxia.siterenderer.Renderer"
 */
public class DefaultSiteRenderer
    extends AbstractLogEnabled
    implements Renderer
{
    private static final String DEFAULT_OUTPUT_ENCODING = "UTF-8";

    // ----------------------------------------------------------------------
    // Requirements
    // ----------------------------------------------------------------------

    /**
     * @plexus.requirement
     */
    private VelocityComponent velocity;

    /**
     * @plexus.requirement
     */
    private SiteModuleManager siteModuleManager;

    /**
     * @plexus.requirement
     */
    private Doxia doxia;

    /**
     * @plexus.requirement
     */
    private I18N i18n;

    // ----------------------------------------------------------------------
    // Renderer implementation
    // ----------------------------------------------------------------------

    public void render( File siteDirectory, File outputDirectory, SiteRenderingContext context )
        throws RendererException, IOException
    {
        render( siteDirectory, outputDirectory, context, DEFAULT_OUTPUT_ENCODING );
    }

    public void render( File siteDirectory, File outputDirectory, SiteRenderingContext context, String outputEncoding )
        throws RendererException, IOException
    {
        for ( Iterator i = siteModuleManager.getSiteModules().iterator(); i.hasNext(); )
        {
            SiteModule module = (SiteModule) i.next();

            File moduleBasedir = new File( siteDirectory, module.getSourceDirectory() );

            if ( moduleBasedir.exists() )
            {
                List docs = FileUtils.getFileNames( moduleBasedir, "**/*." + module.getExtension(), null, false );

                for ( Iterator j = docs.iterator(); j.hasNext(); )
                {
                    String doc = (String) j.next();

                    String outputName = doc.substring( 0, doc.indexOf( "." ) + 1 ) + "html";

                    String fullPathDoc = new File( moduleBasedir, doc ).getPath();

                    SiteRendererSink sink = createSink( moduleBasedir, outputName );

                    try
                    {
                        FileReader reader = new FileReader( fullPathDoc );

                        doxia.parse( reader, module.getParserId(), sink );

                        File outputFile = new File( outputDirectory, outputName );

                        if ( !outputFile.getParentFile().exists() )
                        {
                            outputFile.getParentFile().mkdirs();
                        }

                        generateDocument( new OutputStreamWriter( new FileOutputStream( outputFile ), outputEncoding ),
                                          sink, context );
                    }
                    catch ( ParserNotFoundException e )
                    {
                        throw new RendererException(
                            "Error getting a parser for " + fullPathDoc + ": " + e.getMessage() );
                    }
                    catch ( ParseException e )
                    {
                        getLogger().error( "Error parsing " + fullPathDoc + ": " + e.getMessage(), e );
                    }
                    finally
                    {
                        sink.flush();

                        sink.close();
                    }
                }
            }
        }
    }

    // per module renderer
    public void render( File siteDirectory, File outputDirectory, String module, String moduleExtension,
                        String moduleParserId, SiteRenderingContext context, String outputEncoding )
        throws RendererException, IOException
    {
        List docs = FileUtils.getFileNames( siteDirectory, "**/*." + moduleExtension, null, false );

        for ( Iterator j = docs.iterator(); j.hasNext(); )
        {
            String doc = (String) j.next();

            String outputName = doc.substring( 0, doc.indexOf( "." ) + 1 ) + "html";

            String fullPathDoc = new File( siteDirectory, doc ).getPath();

            SiteRendererSink sink = createSink( siteDirectory, outputName );

            try
            {
                FileReader reader = new FileReader( fullPathDoc );

                doxia.parse( reader, moduleParserId, sink );

                File outputFile = new File( outputDirectory, outputName );

                if ( !outputFile.getParentFile().exists() )
                {
                    outputFile.getParentFile().mkdirs();
                }
                generateDocument( new OutputStreamWriter( new FileOutputStream( outputFile ), outputEncoding ), sink,
                                  context );
            }
            catch ( ParserNotFoundException e )
            {
                throw new RendererException( "Error getting a parser for " + fullPathDoc + ": " + e.getMessage() );
            }
            catch ( ParseException e )
            {
                getLogger().error( "Error parsing " + fullPathDoc + ": " + e.getMessage(), e );
            }
            finally
            {
                sink.flush();

                sink.close();
            }
        }
    }

    public void generateDocument( Writer writer, SiteRendererSink sink, SiteRenderingContext siteContext )
        throws RendererException
    {
        VelocityContext context = new VelocityContext();

        // ----------------------------------------------------------------------
        // Data objects
        // ----------------------------------------------------------------------

        RenderingContext renderingContext = sink.getRenderingContext();
        String relativePath = renderingContext.getRelativePath();
        context.put( "relativePath", relativePath );

        // Add infos from document
        context.put( "authors", sink.getAuthors() );

        String title = "";
        if ( siteContext.getDecoration().getName() != null )
        {
            title = siteContext.getDecoration().getName();
        }
        else if ( siteContext.getDefaultWindowTitle() != null )
        {
            title = siteContext.getDefaultWindowTitle();
        }

        if ( title.length() > 0 )
        {
            title += " - ";
        }
        title += sink.getTitle();

        context.put( "title", title );

        context.put( "bodyContent", sink.getBody() );

        context.put( "decoration", siteContext.getDecoration() );

        context.put( "currentDate", new Date() );

        context.put( "dateFormat", new SimpleDateFormat() );

        String currentFileName = PathTool.calculateLink( renderingContext.getOutputName(), relativePath );

        context.put( "currentFileName", currentFileName );

        context.put( "locale", siteContext.getLocale() );

        // Add user properties
        Map templateProperties = siteContext.getTemplateProperties();
        if ( templateProperties != null )
        {
            for ( Iterator i = templateProperties.keySet().iterator(); i.hasNext(); )
            {
                String key = (String) i.next();

                context.put( key, templateProperties.get( key ) );
            }
        }

        // ----------------------------------------------------------------------
        // Tools
        // ----------------------------------------------------------------------

        context.put( "PathTool", new PathTool() );

        context.put( "FileUtils", new FileUtils() );

        context.put( "StringUtils", new StringUtils() );

        context.put( "i18n", i18n );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        writeTemplate( siteContext.getTemplate(), writer, context, siteContext.getTemplateClassLoader() );
    }

    private void writeTemplate( String templateName, Writer writer, Context context, ClassLoader templateClassLoader )
        throws RendererException
    {
        ClassLoader old = null;

        if ( templateClassLoader != null )
        {
            // -------------------------------------------------------------------------
            // If no template classloader was set we'll just use the context classloader
            // -------------------------------------------------------------------------

            old = Thread.currentThread().getContextClassLoader();

            Thread.currentThread().setContextClassLoader( templateClassLoader );
        }

        try
        {
            processTemplate( templateName, context, writer );
        }
        finally
        {
            IOUtil.close( writer );

            if ( old != null )
            {
                Thread.currentThread().setContextClassLoader( old );
            }
        }
    }

    /**
     * @noinspection OverlyBroadCatchBlock,UnusedCatchParameter
     */
    private void processTemplate( String templateName, Context context, Writer writer )
        throws RendererException
    {
        Template template;

        try
        {
            template = velocity.getEngine().getTemplate( templateName );
        }
        catch ( Exception e )
        {
            throw new RendererException( "Could not find the template '" + templateName );
        }

        try
        {
            template.merge( context, writer );
        }
        catch ( Exception e )
        {
            throw new RendererException( "Error while generating code.", e );
        }
    }

    public SiteRendererSink createSink( File moduleBaseDir, String document )
    {
        return new SiteRendererSink( new RenderingContext( moduleBaseDir, document ) );
    }

}
