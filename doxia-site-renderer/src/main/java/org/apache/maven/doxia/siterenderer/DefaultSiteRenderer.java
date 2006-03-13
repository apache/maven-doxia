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
import org.apache.maven.doxia.site.decoration.DecorationModel;
import org.apache.maven.doxia.site.module.SiteModule;
import org.apache.maven.doxia.site.module.manager.SiteModuleManager;
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.PathTool;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.velocity.VelocityComponent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

    private static final String RESOURCE_DIR = "org/apache/maven/doxia/siterenderer/resources";

    private static final String DEFAULT_TEMPLATE = RESOURCE_DIR + "/default-site.vm";

    private static final String SKIN_TEMPLATE_LOCATION = "META-INF/maven/site.vm";

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
                renderModule( moduleBasedir, module.getExtension(), module.getParserId(), outputDirectory,
                              outputEncoding, context );
            }
        }

        copyResources( outputDirectory, context );
    }

    private void renderModule( File moduleBasedir, String moduleExtension, String moduleParserId, File outputDirectory,
                               String outputEncoding, SiteRenderingContext context )
        throws IOException, RendererException
    {
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

    public void render( File moduleBasedir, File outputDirectory, String module, String moduleExtension,
                        String moduleParserId, SiteRenderingContext context, String outputEncoding )
        throws RendererException, IOException
    {
        renderModule( moduleBasedir, moduleExtension, moduleParserId, outputDirectory, outputEncoding, context );

        copyResources( outputDirectory, context );
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

        context.put( "dateFormat", DateFormat.getDateInstance( DateFormat.DEFAULT, siteContext.getLocale() ) );

        String currentFileName = PathTool.calculateLink( renderingContext.getOutputName(), relativePath );
        currentFileName = currentFileName.replace( '\\', '/' );

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

        writeTemplate( writer, context, siteContext );
    }

    private void writeTemplate( Writer writer, Context context, SiteRenderingContext siteContext )
        throws RendererException
    {
        ClassLoader old = null;

        if ( siteContext.getTemplateClassLoader() != null )
        {
            // -------------------------------------------------------------------------
            // If no template classloader was set we'll just use the context classloader
            // -------------------------------------------------------------------------

            old = Thread.currentThread().getContextClassLoader();

            Thread.currentThread().setContextClassLoader( siteContext.getTemplateClassLoader() );
        }

        try
        {
            processTemplate( siteContext.getTemplateName(), context, writer );
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

    public SiteRenderingContext createContextForSkin( File skinFile, Map attributes, DecorationModel decoration,
                                                      Locale locale, String defaultWindowTitle,
                                                      File resourcesDirectory )
        throws IOException
    {
        SiteRenderingContext context = new SiteRenderingContext();

        // TODO: plexus-archiver, if it could do the excludes
        ZipFile zipFile = new ZipFile( skinFile );
        try
        {
            if ( zipFile.getEntry( SKIN_TEMPLATE_LOCATION ) != null )
            {
                context.setTemplateName( SKIN_TEMPLATE_LOCATION );
                context.setTemplateClassLoader( new URLClassLoader( new URL[]{skinFile.toURL()} ) );
            }
            else
            {
                context.setTemplateName( DEFAULT_TEMPLATE );
                context.setTemplateClassLoader( getClass().getClassLoader() );
                context.setUsingDefaultTemplate( true );
            }
        }
        finally
        {
            closeZipFile( zipFile );
        }

        context.setTemplateProperties( attributes );
        context.setLocale( locale );
        context.setDecoration( decoration );
        context.setDefaultWindowTitle( defaultWindowTitle );
        context.setSkinJarFile( skinFile );
        context.setResourcesDirectory( resourcesDirectory );

        return context;
    }

    public SiteRenderingContext createContextForTemplate( File templateFile, Map attributes, DecorationModel decoration,
                                                          Locale locale, String defaultWindowTitle, File skinFile,
                                                          File resourcesDirectory )
        throws MalformedURLException
    {
        SiteRenderingContext context = new SiteRenderingContext();

        context.setTemplateName( templateFile.getName() );
        context.setTemplateClassLoader( new URLClassLoader( new URL[]{templateFile.getParentFile().toURL()} ) );

        context.setTemplateProperties( attributes );
        context.setLocale( locale );
        context.setDecoration( decoration );
        context.setDefaultWindowTitle( defaultWindowTitle );
        context.setSkinJarFile( skinFile );
        context.setResourcesDirectory( resourcesDirectory );

        return context;
    }

    private void closeZipFile( ZipFile zipFile )
    {
        // TODO: move to plexus utils
        try
        {
            zipFile.close();
        }
        catch ( IOException e )
        {
            // ignore
        }
    }

    /**
     * Copy Resources
     *
     * @param outputDir the output directory
     * @throws java.io.IOException if any
     */
    public void copyResources( File outputDir, SiteRenderingContext siteContext )
        throws IOException
    {
        if ( siteContext.getSkinJarFile() != null )
        {
            // TODO: plexus-archiver, if it could do the excludes
            ZipFile file = new ZipFile( siteContext.getSkinJarFile() );
            try
            {
                for ( Enumeration e = file.entries(); e.hasMoreElements(); )
                {
                    ZipEntry entry = (ZipEntry) e.nextElement();

                    if ( !entry.getName().startsWith( "META-INF/" ) )
                    {
                        File destFile = new File( outputDir, entry.getName() );
                        if ( !entry.isDirectory() )
                        {
                            destFile.getParentFile().mkdirs();

                            FileOutputStream fos = new FileOutputStream( destFile );

                            try
                            {
                                IOUtil.copy( file.getInputStream( entry ), fos );
                            }
                            finally
                            {
                                IOUtil.close( fos );
                            }
                        }
                        else
                        {
                            destFile.mkdirs();
                        }
                    }
                }
            }
            finally
            {
                file.close();
            }
        }

        if ( siteContext.isUsingDefaultTemplate() )
        {
            InputStream resourceList =
                getClass().getClassLoader().getResourceAsStream( RESOURCE_DIR + "/resources.txt" );

            if ( resourceList != null )
            {
                LineNumberReader reader = new LineNumberReader( new InputStreamReader( resourceList ) );

                String line = reader.readLine();

                while ( line != null )
                {
                    InputStream is = getClass().getClassLoader().getResourceAsStream( RESOURCE_DIR + "/" + line );

                    if ( is == null )
                    {
                        throw new IOException( "The resource " + line + " doesn't exist." );
                    }

                    File outputFile = new File( outputDir, line );

                    if ( !outputFile.getParentFile().exists() )
                    {
                        outputFile.getParentFile().mkdirs();
                    }

                    FileOutputStream w = new FileOutputStream( outputFile );

                    IOUtil.copy( is, w );

                    IOUtil.close( is );

                    IOUtil.close( w );

                    line = reader.readLine();
                }
            }
        }

        File resourcesDirectory = siteContext.getResourcesDirectory();

        // Copy extra site resources
        // TODO: this should be accommodating locale
        // TODO: this should be automatically looking into ${siteDirectory}/resources
        if ( resourcesDirectory != null && resourcesDirectory.exists() )
        {
            copyDirectory( resourcesDirectory, outputDir );
        }

    }

    /**
     * Copy the directory
     *
     * @param source      source file to be copied
     * @param destination destination file
     * @throws java.io.IOException if any
     */
    protected void copyDirectory( File source, File destination )
        throws IOException
    {
        if ( source.exists() )
        {
            DirectoryScanner scanner = new DirectoryScanner();

            String[] includedResources = {"**/**"};

            scanner.setIncludes( includedResources );

            scanner.addDefaultExcludes();

            scanner.setBasedir( source );

            scanner.scan();

            List includedFiles = Arrays.asList( scanner.getIncludedFiles() );

            for ( Iterator j = includedFiles.iterator(); j.hasNext(); )
            {
                String name = (String) j.next();

                File sourceFile = new File( source, name );

                File destinationFile = new File( destination, name );

                FileUtils.copyFile( sourceFile, destinationFile );
            }
        }
    }

}
