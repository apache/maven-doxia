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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
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
        String flavour = "maven";

        DecorationModelReader decorationModelReader = new DecorationModelReader();

        File siteDescriptor = new File( siteDirectory, "site.xml" );

        if ( !siteDescriptor.exists() )
        {
            throw new Exception( "The site descriptor is not present!" );
        }

        DecorationModel decorationModel = decorationModelReader.createNavigation( siteDescriptor.getPath() );

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

            generateModuleDocumentation( flavour, module, moduleBasedir, decorationModel, outputDirectory );
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

            generateModuleDocumentation( flavour, module, moduleBasedir, decorationModel, outputDirectory );
        }

        // ----------------------------------------------------------------------
        // Copy over the necessary resources
        // ----------------------------------------------------------------------

        // set default flavour in site.xml but allow it to be overridden

        copyResources( outputDirectory, flavour );

        FileUtils.copyDirectory( new File( siteDirectory, "images" ), new File( outputDirectory, "images" ) );
    }

    protected void generateModuleDocumentation( String flavour,
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

            String outputName = doc.substring( 0, doc.indexOf( "." ) + 1 ) + "html";

            File outputFile = new File( outputDirectory, outputName );

            if ( !outputFile.getParentFile().exists() )
            {
                outputFile.getParentFile().mkdirs();
            }

            InputStream is = getClass().getResourceAsStream( "/" + flavour + ".dst" );

            Reader r = new InputStreamReader( is );

            SinkDescriptorReader sdr = new SinkDescriptorReader();

            Map directives = sdr.read( r );

            RenderingContext renderingContext =
                new RenderingContext( moduleBasedir.getPath(), doc, decorationModel );

            String relativePathToBasedir = PathTool.getRelativePath( moduleBasedir.getAbsolutePath(), fullPathDoc );

            XhtmlSink sink = new XhtmlSink( new FileWriter( outputFile ), renderingContext, directives, relativePathToBasedir );

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

    private void copyResources( String outputDirectory, String flavour )
        throws Exception
    {
        InputStream resourceList = getStream( flavour + "/resources.txt" );

        LineNumberReader reader = new LineNumberReader( new InputStreamReader( resourceList ) );

        String line;

        while ( ( line = reader.readLine() ) != null )
        {
            InputStream is = getStream( flavour + "/" + line );

            if ( is == null )
            {
                continue;
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
}
