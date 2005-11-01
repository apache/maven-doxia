package org.codehaus.plexus.siterenderer;

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

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.codehaus.doxia.Doxia;
import org.codehaus.doxia.module.xhtml.decoration.render.RenderingContext;
import org.codehaus.doxia.site.module.SiteModule;
import org.codehaus.doxia.site.module.manager.SiteModuleManager;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.siterenderer.sink.SiteRendererSink;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.PathTool;
import org.codehaus.plexus.util.StringInputStream;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.codehaus.plexus.velocity.VelocityComponent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @plexus.component
 *   role="org.codehaus.plexus.siterenderer.Renderer"
 * 
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
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
    // Fields
    // ----------------------------------------------------------------------

    private String currentDocument;

    private RenderingContext renderingContext;

    private ClassLoader templateClassLoader;

    private Xpp3Dom siteDescriptor;

    private Locale defaultLocale = Locale.ENGLISH;

    public void setTemplateClassLoader( ClassLoader templateClassLoader )
    {
        this.templateClassLoader = templateClassLoader;
    }

    // ----------------------------------------------------------------------
    // Renderer implementation
    // ----------------------------------------------------------------------

    /**
     * @see org.codehaus.plexus.siterenderer.Renderer#render(java.io.File, java.io.File, java.io.File, java.lang.String, java.util.Map)
     */
    public void render( File siteDirectory, File outputDirectory, File siteDescriptor, String templateName,
                       Map templateProperties )
        throws RendererException, IOException
    {
        render( siteDirectory, outputDirectory, siteDescriptor, templateName, templateProperties, defaultLocale );
    }

    /**
     * @see org.codehaus.plexus.siterenderer.Renderer#render(java.io.File, java.io.File, java.io.File, java.lang.String, java.util.Map, java.util.Locale)
     */
    public void render( File siteDirectory, File outputDirectory, File siteDescriptor, String templateName,
                       Map templateProperties, Locale locale )
        throws RendererException, IOException
    {
        render( siteDirectory, outputDirectory, new FileInputStream( siteDescriptor ), templateName,
                templateProperties, locale );
    }

    /**
     * @see org.codehaus.plexus.siterenderer.Renderer#render(java.io.File, java.io.File, java.lang.String, java.lang.String, Map)
     */
    public void render( File siteDirectory, File outputDirectory, String siteDescriptor, String templateName,
                       Map templateProperties )
        throws RendererException, IOException
    {
        render( siteDirectory, outputDirectory, siteDescriptor, templateName, templateProperties, defaultLocale );
    }

    /**
     * @see org.codehaus.plexus.siterenderer.Renderer#render(java.io.File, java.io.File, java.lang.String, java.lang.String, java.util.Map, java.util.Locale)
     */
    public void render( File siteDirectory, File outputDirectory, String siteDescriptor, String templateName,
                       Map templateProperties, Locale locale )
        throws RendererException, IOException
    {
        render( siteDirectory, outputDirectory, new StringInputStream( siteDescriptor ), templateName,
                templateProperties, locale );
    }

    /**
     * @see org.codehaus.plexus.siterenderer.Renderer#render(File, File, InputStream, String, Map)
     */
    public void render( File siteDirectory, File outputDirectory, InputStream siteDescriptor, String templateName,
                       Map templateProperties )
        throws RendererException, IOException
    {
        render( siteDirectory, outputDirectory, siteDescriptor, templateName, templateProperties, defaultLocale );
    }

    /**
     * @see org.codehaus.plexus.siterenderer.Renderer#render(java.io.File, java.io.File, java.io.InputStream, java.lang.String, java.util.Map, java.util.Locale)
     */
    public void render( File siteDirectory, File outputDirectory, InputStream siteDescriptor, String templateName,
                       Map templateProperties, Locale locale )
        throws RendererException, IOException
    {
        render( siteDirectory, outputDirectory, siteDescriptor, templateName, templateProperties, defaultLocale, DEFAULT_OUTPUT_ENCODING );
    }
    
    //per module
    public void render( File siteDirectory, File outputDirectory, String module, String moduleExtension, String moduleParserId, 
                    String siteDescriptor, String templateName, Map templateProperties, Locale locale, String outputEncoding )
         throws RendererException, IOException
    {
        render( siteDirectory, outputDirectory, module, moduleExtension, moduleParserId, new StringInputStream( siteDescriptor ),
                templateName, templateProperties, locale, outputEncoding);
    }

    /**
     * @see org.codehaus.plexus.siterenderer.Renderer#render(java.io.File, java.io.File, java.io.InputStream, java.lang.String, java.util.Map, java.util.Locale, java.lang.String)
     */
    public void render( File siteDirectory, File outputDirectory, InputStream siteDescriptor, String templateName,
                       Map templateProperties, Locale locale, String outputEncoding )
        throws RendererException, IOException
    {
        try
        {
            InputStreamReader xmlReader = new InputStreamReader( siteDescriptor );

            this.siteDescriptor = Xpp3DomBuilder.build( xmlReader );
        }
        catch ( XmlPullParserException e )
        {
            throw new RendererException( "Can't read site descriptor.", e );
        }

        for ( Iterator i = siteModuleManager.getSiteModules().iterator(); i.hasNext(); )
        {
            SiteModule module = (SiteModule) i.next();

            File moduleBasedir = new File( siteDirectory, module.getSourceDirectory() );

            if ( !moduleBasedir.exists() )
            {
                continue;
            }

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

                    
                    generateDocument( new OutputStreamWriter( new FileOutputStream( outputFile ), outputEncoding ), templateName, templateProperties, sink, locale );
                }
                catch ( Exception e )
                {
                    e.printStackTrace();

                    getLogger().error( "Error rendering " + fullPathDoc + ": " + e.getMessage(), e );
                }
                finally
                {
                    sink.flush();
                    sink.close();
                }
            }
        }
    }
    
    // per module renderer
    public void render( File siteDirectory, File outputDirectory, String module, String moduleExtension, String moduleParserId, 
                        InputStream siteDescriptor, String templateName, Map templateProperties, Locale locale, String outputEncoding )
        throws RendererException, IOException
    {
        try
        {
            InputStreamReader xmlReader = new InputStreamReader( siteDescriptor );

            this.siteDescriptor = Xpp3DomBuilder.build( xmlReader );
        }
        catch ( XmlPullParserException e )
        {
            throw new RendererException( "Can't read site descriptor.", e );
        }
        File moduleBasedir = siteDirectory;

        List docs = FileUtils.getFileNames( moduleBasedir, "**/*." + moduleExtension, null, false );

        for ( Iterator j = docs.iterator(); j.hasNext(); )
        {
            String doc = (String) j.next();

            String outputName = doc.substring( 0, doc.indexOf( "." ) + 1 ) + "html";

            String fullPathDoc = new File( moduleBasedir, doc ).getPath();

            SiteRendererSink sink = createSink( moduleBasedir, outputName );

            try
            {
                FileReader reader = new FileReader( fullPathDoc );

                doxia.parse( reader, moduleParserId, sink );

                File outputFile = new File( outputDirectory, outputName );

                if ( !outputFile.getParentFile().exists() )
                {
                    outputFile.getParentFile().mkdirs();
                }
                generateDocument( new OutputStreamWriter( new FileOutputStream( outputFile ), outputEncoding ), templateName, templateProperties, sink, locale );
            }
            catch ( Exception e )
            {
                e.printStackTrace();

                getLogger().error( "Error rendering " + fullPathDoc + ": " + e.getMessage(), e );
            }
            finally
            {
                sink.flush();
                sink.close();
            }
        }
    }    
    
    /**
     * @see org.codehaus.plexus.siterenderer.Renderer#generateDocument(java.io.Writer, java.lang.String, java.util.Map, org.codehaus.plexus.siterenderer.sink.SiteRendererSink)
     */
    public void generateDocument( Writer writer, String templateName, Map templateProperties, SiteRendererSink sink )
        throws RendererException
    {
        generateDocument( writer, templateName, templateProperties, sink, defaultLocale );
    }

    /**
     * @see org.codehaus.plexus.siterenderer.Renderer#generateDocument(java.io.Writer, java.lang.String, java.util.Map, org.codehaus.plexus.siterenderer.sink.SiteRendererSink, java.util.Locale)
     */
    public void generateDocument( Writer writer, String templateName, Map templateProperties, SiteRendererSink sink,
                                 Locale locale )
        throws RendererException
    {
        VelocityContext context = new VelocityContext();

        // ----------------------------------------------------------------------
        // Data objects
        // ----------------------------------------------------------------------

        context.put( "relativePath", renderingContext.getRelativePath() );

        // Add infos from document
        context.put( "authors", sink.getAuthors() );

        context.put( "title", sink.getTitle() );

        context.put( "bodyContent", sink.getBody() );

        // Add infos from siteDescriptor
        context.put( "siteDescriptor", siteDescriptor );

        context.put( "currentDate", new Date() );

        context.put( "currentFileName", PathTool.calculateLink( "/" + currentDocument, renderingContext
            .getRelativePath() ) );

        context.put( "locale", locale );

        // Add user properties
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

        writeTemplate( templateName, writer, context );
    }

    protected void writeTemplate( String templateName, Writer writer, Context context )
        throws RendererException
    {
        Template template = null;

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
            try
            {
                template = velocity.getEngine().getTemplate( templateName );
            }
            catch ( Exception e )
            {
                throw new RendererException( "Could not find the template '" + templateName + "' in "
                                             + Thread.currentThread().getContextClassLoader() );
            }
    
            try
            {
                template.merge( context, writer );
    
                writer.close();
            }
            catch ( Exception e )
            {
                throw new RendererException( "Error while generating code.", e );
            }
        }
        finally
        {
            if ( old != null )
            {
                Thread.currentThread().setContextClassLoader( old );
            }
        }
    }

    public SiteRendererSink createSink( File moduleBaseDir, String document, File siteDescriptor )
        throws RendererException, IOException
    {
        return createSink( moduleBaseDir, document, new FileInputStream( siteDescriptor ) );
    }

    public SiteRendererSink createSink( File moduleBaseDir, String document, String siteDescriptor )
        throws RendererException, IOException
    {
        return createSink( moduleBaseDir, document, new StringInputStream( siteDescriptor ) );
    }

    public SiteRendererSink createSink( File moduleBaseDir, String document, InputStream siteDescriptor )
        throws RendererException, IOException
    {
        try
        {
            InputStreamReader xmlReader = new InputStreamReader( siteDescriptor );

            this.siteDescriptor = Xpp3DomBuilder.build( xmlReader );
        }
        catch ( XmlPullParserException e )
        {
            throw new RendererException( "Can't read site descriptor.", e );
        }

        return createSink( moduleBaseDir, document );
    }

    private SiteRendererSink createSink( File moduleBaseDir, String document )
        throws RendererException
    {
        currentDocument = document;

        renderingContext = new RenderingContext( moduleBaseDir, document, null );

        return new SiteRendererSink( new StringWriter(), renderingContext );
    }
}
