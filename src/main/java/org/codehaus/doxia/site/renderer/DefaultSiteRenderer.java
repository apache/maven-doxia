/*
 * CopyrightPlugin (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.doxia.site.renderer;

import org.codehaus.doxia.Doxia;
import org.codehaus.doxia.module.xhtml.SinkDescriptorReader;
import org.codehaus.doxia.module.xhtml.XhtmlSink;
import org.codehaus.doxia.module.xhtml.decoration.model.DecorationModel;
import org.codehaus.doxia.module.xhtml.decoration.model.DecorationModelReader;
import org.codehaus.doxia.module.xhtml.decoration.render.RenderingContext;
import org.codehaus.doxia.site.module.SiteModule;
import org.codehaus.doxia.site.module.manager.SiteModuleManager;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.PathTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: DefaultSiteRenderer.java,v 1.6 2004/11/02 05:00:40 jvanzyl Exp $
 * @plexus.component
 */
public class DefaultSiteRenderer
    implements SiteRenderer
{
    private SiteModuleManager siteModuleManager;

    private Doxia doxia;

    public void render( String siteDirectory, String generatedSiteDirectory, String outputDirectory )
        throws Exception
    {
        render( siteDirectory, generatedSiteDirectory, outputDirectory, null, "site.xml" );
    }

    public void render( String siteDirectory, String generatedSiteDirectory, String outputDirectory, String flavour )
        throws Exception
    {
        render( siteDirectory, generatedSiteDirectory, outputDirectory, flavour, "site.xml" );
    }

    public void render( String siteDirectory, String generatedSiteDirectory, String outputDirectory,
                        String flavour, String siteDescriptorName )
        throws Exception
    {
        DecorationModelReader decorationModelReader = new DecorationModelReader();

        File siteDescriptor = new File( siteDirectory, siteDescriptorName );

        if ( !siteDescriptor.exists() )
        {
            throw new Exception( "The site descriptor is not present!" );
        }

        DecorationModel decorationModel = decorationModelReader.createNavigation( siteDescriptor.getPath() );

        render( siteDirectory, generatedSiteDirectory, outputDirectory, flavour, decorationModel );
    }

    public void render( String siteDirectory, String generatedSiteDirectory, String outputDirectory,
                        String flavour, InputStream siteDescriptor )
        throws Exception
    {
        DecorationModelReader decorationModelReader = new DecorationModelReader();

        if ( siteDescriptor == null )
        {
            throw new Exception( "The site descriptor is not present!" );
        }

        DecorationModel decorationModel = decorationModelReader.createNavigation( new InputStreamReader( siteDescriptor ) );

        render( siteDirectory, generatedSiteDirectory, outputDirectory, flavour, decorationModel );
    }

    private void render( String siteDirectory, String generatedSiteDirectory, String outputDirectory,
                         String flavour, DecorationModel decorationModel )
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

            generateModuleDocumentation( siteFlavour, siteDirectory, module, moduleBasedir, decorationModel, outputDirectory );
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

            generateModuleDocumentation( siteFlavour, siteDirectory, module, moduleBasedir, decorationModel, outputDirectory );
        }

        // ----------------------------------------------------------------------
        // Copy over the necessary resources
        // ----------------------------------------------------------------------

        copyResources( outputDirectory, siteFlavour, siteDirectory );

        FileUtils.copyDirectory( new File( siteDirectory, "css" ), new File( outputDirectory, "css" ) );

        FileUtils.copyDirectory( new File( siteDirectory, "images" ), new File( outputDirectory, "images" ) );
    }

    protected void generateModuleDocumentation( String flavour,
                                                String siteDirectory,
                                                SiteModule module,
                                                File moduleBasedir,
                                                DecorationModel decorationModel,
                                                String outputDirectory )
        throws Exception
    {
        List docs = FileUtils.getFileNames( moduleBasedir, "**/*." + module.getExtension(), null, false );

        for ( Iterator j = docs.iterator(); j.hasNext(); )
        {
            String doc = (String) j.next();

            String fullPathDoc = new File( moduleBasedir, doc ).getPath();

            XhtmlSink sink = createSink( moduleBasedir, siteDirectory, doc, outputDirectory, decorationModel, flavour );

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
        }
    }

    private Map getDirectives( String siteDirectory, String flavour )
        throws Exception
    {
        InputStream is = getClass().getResourceAsStream( "/" + flavour + ".dst" );

        if ( is == null )
        {
            File flavourTemplate = new File( getFlavourDirectory( siteDirectory ), flavour + ".dst" );

            if ( ! flavourTemplate.exists() )
            {
                throw new IOException( "The flavour " + flavourTemplate.getAbsolutePath() + " doesn't exists.");
            }

            is = new FileInputStream( flavourTemplate );
        }

        Reader r = new InputStreamReader( is );

        SinkDescriptorReader sdr = new SinkDescriptorReader();

        return sdr.read( r );
    }

    public XhtmlSink createSink( File moduleBasedir, String siteDirectory, String doc, String outputDirectory,
                                 String siteDescriptorName, String flavour )
        throws Exception
    {
        DecorationModelReader decorationModelReader = new DecorationModelReader();

        File siteDescriptor = new File( siteDirectory, siteDescriptorName );

        if ( !siteDescriptor.exists() )
        {
            throw new Exception( "The site descriptor is not present!" );
        }

        DecorationModel decorationModel = decorationModelReader.createNavigation( siteDescriptor.getPath() );

        return createSink( moduleBasedir, siteDirectory, doc, outputDirectory, decorationModel, flavour );
    }

    public XhtmlSink createSink( File moduleBasedir, String siteDirectory, String doc, String outputDirectory,
                                 InputStream siteDescriptor, String flavour )
        throws Exception
    {
        DecorationModelReader decorationModelReader = new DecorationModelReader();

        DecorationModel decorationModel = decorationModelReader.createNavigation( new InputStreamReader( siteDescriptor ) );


        return createSink( moduleBasedir, siteDirectory, doc, outputDirectory, decorationModel, flavour );
    }

    private XhtmlSink createSink( File moduleBasedir, String siteDirectory, String doc, String outputDirectory,
                                 DecorationModel decorationModel, String flavour )
        throws Exception
    {
        String fullPathDoc = new File( moduleBasedir, doc ).getPath();

        String outputName = doc.substring( 0, doc.indexOf( "." ) + 1 ) + "html";

        File outputFile = new File( outputDirectory, outputName );

        if ( !outputFile.getParentFile().exists() )
        {
            outputFile.getParentFile().mkdirs();
        }

        Map directives = getDirectives( siteDirectory, flavour );

        RenderingContext renderingContext =
            new RenderingContext( moduleBasedir, doc, decorationModel );

        return new XhtmlSink( new FileWriter( outputFile ), renderingContext, directives );
    }

    private void copyResources( String outputDirectory, String flavour, String siteDirectory )
        throws Exception
    {
        boolean isProjectResources = false;

        InputStream resourceList = getStream( flavour + "/resources.txt" );

        if ( resourceList == null )
        {
            File flavourResources = new File( getFlavourResourcesDirectory( siteDirectory, flavour ), "resources.txt" );

            if ( ! flavourResources.exists() )
            {
                throw new IOException( "The flavour resources file" + flavourResources.getAbsolutePath() + " doesn't exists.");
            }

            resourceList = new FileInputStream( flavourResources );

            isProjectResources = true;
        }

        LineNumberReader reader = new LineNumberReader( new InputStreamReader( resourceList ) );

        String line;

        while ( ( line = reader.readLine() ) != null )
        {
            InputStream is = null;

            if ( isProjectResources )
            {
                File resourceLine = new File( getFlavourResourcesDirectory( siteDirectory, flavour ), line );

                is = new FileInputStream( resourceLine );
            }
            else
            {
                is = getStream( flavour + "/" + line );
            }

            if ( is == null )
            {
                throw new IOException( "The resource " + line + " doesn't exists in " + flavour + " flavour.");
            }

            File outputFile = new File( outputDirectory, line );

            if ( !outputFile.getParentFile().exists() )
            {
                outputFile.getParentFile().mkdirs();
            }

            FileOutputStream w = new FileOutputStream( outputFile );

            copy( is, w );
        }
    }

    private void copy( InputStream input, OutputStream output )
        throws Exception
    {
        byte[] buffer = new byte[1024];

        int n;

        while ( -1 != ( n = input.read( buffer ) ) )
        {
            output.write( buffer, 0, n );
        }

        shutdownStream( input );

        shutdownStream( output );
    }

    protected void shutdownStream( InputStream input )
    {
        if ( input != null )
        {
            try
            {
                input.close();
            }
            catch ( Exception e )
            {
            }
        }
    }

    protected void shutdownStream( OutputStream output )
    {
        if ( output != null )
        {
            try
            {
                output.close();
            }
            catch ( Exception e )
            {
            }
        }
    }

    private InputStream getStream( String name )
        throws Exception
    {
        return DefaultSiteRenderer.class.getClassLoader().getResourceAsStream( name );
    }
    
    private String getFlavourDirectory( String siteDirectory )
    {
        return siteDirectory + "/flavour";
    }
    
    private String getFlavourResourcesDirectory( String siteDirectory, String flavour )
    {
        return getFlavourDirectory( siteDirectory ) + "/" + flavour;
    }
}
