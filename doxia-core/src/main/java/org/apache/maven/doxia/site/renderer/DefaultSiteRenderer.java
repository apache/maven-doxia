package org.apache.maven.doxia.site.renderer;

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
import org.apache.maven.doxia.module.xhtml.SinkDescriptorReader;
import org.apache.maven.doxia.module.xhtml.XhtmlSink;
import org.apache.maven.doxia.module.xhtml.decoration.model.DecorationModel;
import org.apache.maven.doxia.module.xhtml.decoration.model.DecorationModelReader;
import org.apache.maven.doxia.module.xhtml.decoration.render.RenderingContext;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.site.module.SiteModule;
import org.apache.maven.doxia.site.module.manager.SiteModuleManager;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @plexus.component role="org.apache.maven.doxia.site.renderer.SiteRenderer"
 */
public class DefaultSiteRenderer
    implements SiteRenderer
{
    /**
     * @plexus.requirement
     */
    private SiteModuleManager siteModuleManager;

    /**
     * @plexus.requirement
     */
    private Doxia doxia;

    private StringWriter writer = new StringWriter();

    public void render( String siteDirectory, String generatedSiteDirectory, String outputDirectory,
                        File resourcesDirectory )
        throws Exception
    {
        render( siteDirectory, generatedSiteDirectory, outputDirectory, null, "site.xml", resourcesDirectory );
    }

    public void render( String siteDirectory, String generatedSiteDirectory, String outputDirectory, String flavour,
                        File resourcesDirectory )
        throws Exception
    {
        render( siteDirectory, generatedSiteDirectory, outputDirectory, flavour, "site.xml", resourcesDirectory );
    }

    public void render( String siteDirectory, String generatedSiteDirectory, String outputDirectory, String flavour,
                        String siteDescriptorName, File resourcesDirectory )
        throws Exception
    {
        DecorationModelReader decorationModelReader = new DecorationModelReader();

        File siteDescriptor = new File( siteDirectory, siteDescriptorName );

        if ( !siteDescriptor.exists() )
        {
            throw new Exception( "The site descriptor is not present!" );
        }

        DecorationModel decorationModel = decorationModelReader.createNavigation( siteDescriptor.getPath() );

        render( siteDirectory, generatedSiteDirectory, outputDirectory, flavour, decorationModel, resourcesDirectory );
    }

    public void render( String siteDirectory, String generatedSiteDirectory, String outputDirectory, String flavour,
                        InputStream siteDescriptor, File resourcesDirectory )
        throws Exception
    {
        DecorationModelReader decorationModelReader = new DecorationModelReader();

        if ( siteDescriptor == null )
        {
            throw new Exception( "The site descriptor is not present!" );
        }

        DecorationModel decorationModel =
            decorationModelReader.createNavigation( new InputStreamReader( siteDescriptor ) );

        render( siteDirectory, generatedSiteDirectory, outputDirectory, flavour, decorationModel, resourcesDirectory );
    }

    private void render( String siteDirectory, String generatedSiteDirectory, String outputDirectory, String flavour,
                         DecorationModel decorationModel, File resourcesDirectory )
        throws Exception
    {
        String siteFlavour = flavour;

        // ----------------------------------------------------------------------
        // Define the flavour to use
        // ----------------------------------------------------------------------
        if ( siteFlavour == null )
        {
            if ( decorationModel.getFlavour() == null )
            {
                siteFlavour = "maven";
            }
            else
            {
                siteFlavour = decorationModel.getFlavour();
            }
        }

        // ----------------------------------------------------------------------
        // Generate the documentation for each active module for the static
        // documentation produced by users.
        // ----------------------------------------------------------------------

        for ( Iterator i = siteModuleManager.getSiteModules().iterator(); i.hasNext(); )
        {
            SiteModule module = (SiteModule) i.next();

            File moduleBasedir = new File( siteDirectory, module.getSourceDirectory() );

            if ( !moduleBasedir.exists() )
            {
                continue;
            }

            generateModuleDocumentation( siteFlavour, siteDirectory, module, moduleBasedir, decorationModel,
                                         outputDirectory );
        }

        // ----------------------------------------------------------------------
        // Generate the documentation for each active module for the generated
        // documentation produced by report generators, source tools or anything
        // else that can generate content.
        // ----------------------------------------------------------------------

        for ( Iterator i = siteModuleManager.getSiteModules().iterator(); i.hasNext(); )
        {
            SiteModule module = (SiteModule) i.next();

            File moduleBasedir = new File( generatedSiteDirectory, module.getSourceDirectory() );

            if ( !moduleBasedir.exists() )
            {
                continue;
            }

            generateModuleDocumentation( siteFlavour, siteDirectory, module, moduleBasedir, decorationModel,
                                         outputDirectory );
        }

        // ----------------------------------------------------------------------
        // Copy over the necessary resources
        // ----------------------------------------------------------------------

        copyResources( outputDirectory, siteFlavour );

        // TODO: we might want a full pattern set in the site descriptor, should this be outside of "render"?
        if ( resourcesDirectory.isDirectory() )
        {
            FileUtils.copyDirectoryStructure( resourcesDirectory, new File( outputDirectory ) );
        }
    }

    protected void generateModuleDocumentation( String flavour, String siteDirectory, SiteModule module,
                                                File moduleBasedir, DecorationModel decorationModel,
                                                String outputDirectory )
        throws Exception
    {
        List docs = FileUtils.getFileNames( moduleBasedir, "**/*." + module.getExtension(), null, false );

        for ( Iterator j = docs.iterator(); j.hasNext(); )
        {
            String doc = (String) j.next();

            String fullPathDoc = new File( moduleBasedir, doc ).getPath();

            Sink sink = createSink( moduleBasedir, doc, outputDirectory, decorationModel, flavour );

            try
            {
                FileReader reader = new FileReader( fullPathDoc );

                doxia.parse( reader, module.getParserId(), sink );
            }
            catch ( Exception e )
            {
                e.printStackTrace();

                System.out.println( "Error rendering " + fullPathDoc + ": " + e );
            }
            finally
            {
                sink.flush();
                sink.close();
            }
        }
    }

    private Map getDirectives( String flavour )
        throws Exception
    {
        InputStream is = getClass().getResourceAsStream( "/" + flavour + ".dst" );

        Reader r = new InputStreamReader( is );

        SinkDescriptorReader sdr = new SinkDescriptorReader();

        return sdr.read( r );
    }

    public XhtmlSink createSink( File moduleBasedir, String doc, String outputDirectory, File siteDescriptor,
                                 String flavour )
        throws Exception
    {
        DecorationModelReader decorationModelReader = new DecorationModelReader();

        if ( !siteDescriptor.exists() )
        {
            throw new Exception( "The site descriptor is not present!" );
        }

        DecorationModel decorationModel = decorationModelReader.createNavigation( siteDescriptor.getPath() );

        return createSink( moduleBasedir, doc, outputDirectory, decorationModel, flavour );
    }

    public XhtmlSink createSink( File moduleBasedir, String doc, String outputDirectory, InputStream siteDescriptor,
                                 String flavour )
        throws Exception
    {
        DecorationModelReader decorationModelReader = new DecorationModelReader();

        DecorationModel decorationModel =
            decorationModelReader.createNavigation( new InputStreamReader( siteDescriptor ) );

        return createSink( moduleBasedir, doc, outputDirectory, decorationModel, flavour );
    }

    private XhtmlSink createSink( File moduleBasedir, String doc, String outputDirectory,
                                  DecorationModel decorationModel, String flavour )
        throws Exception
    {
        String outputName = doc.substring( 0, doc.indexOf( "." ) + 1 ) + "html";

        File outputFile = new File( outputDirectory, outputName );

        if ( !outputFile.getParentFile().exists() )
        {
            outputFile.getParentFile().mkdirs();
        }

        Map directives = getDirectives( flavour );

        RenderingContext renderingContext = new RenderingContext( moduleBasedir, doc, decorationModel );

        return new XhtmlSink( new FileWriter( outputFile ), renderingContext, directives );
    }

    public void copyResources( String outputDirectory, String flavour )
        throws Exception
    {
        InputStream resourceList = getStream( flavour + "/resources.txt" );

        if ( resourceList != null )
        {
            LineNumberReader reader = new LineNumberReader( new InputStreamReader( resourceList ) );

            String line;

            while ( ( line = reader.readLine() ) != null )
            {
                InputStream is = getStream( flavour + "/" + line );

                if ( is == null )
                {
                    throw new IOException( "The resource " + line + " doesn't exists in " + flavour + " flavour." );
                }

                File outputFile = new File( outputDirectory, line );

                if ( !outputFile.getParentFile().exists() )
                {
                    outputFile.getParentFile().mkdirs();
                }

                FileOutputStream w = new FileOutputStream( outputFile );

                IOUtil.copy( is, w );

                IOUtil.close( is );

                IOUtil.close( w );
            }
        }
    }

    private InputStream getStream( String name )
        throws Exception
    {
        return DefaultSiteRenderer.class.getClassLoader().getResourceAsStream( name );
    }

}
