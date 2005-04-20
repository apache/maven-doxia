/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.doxia;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.doxia.module.xhtml.codehaus.CodehausXhtmlSink;
import org.codehaus.doxia.module.xhtml.decoration.model.MavenDecorationModel;
import org.codehaus.doxia.module.xhtml.decoration.model.MavenDecorationModelReader;
import org.codehaus.doxia.module.xhtml.decoration.render.RenderingContext;
import org.codehaus.doxia.plugin.maven.DependenciesRenderer;
import org.codehaus.doxia.sink.Sink;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: DepTest.java,v 1.3 2004/09/14 14:26:38 jvanzyl Exp $
 */
public class DepTest
    extends TestCase
{
    public void testApt() throws Exception
    {
        Writer writer = new FileWriter( "dependencies.html" );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        String basedir = System.getProperty( "basedir" );

        String siteXml = new File( basedir, "src/test/site/site.xml" ).getPath();

        String xdoc = "repository-upload.xml";

        MavenDecorationModelReader b = new MavenDecorationModelReader();

        MavenDecorationModel navigation = b.createNavigation( siteXml );

        RenderingContext renderingContext = new RenderingContext( basedir, new File( basedir, xdoc ).getPath(),
            navigation );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        MavenXpp3Reader reader = new MavenXpp3Reader();

        Model model = reader.read( new FileReader( "pom.xml" ) );

        Sink sink = new CodehausXhtmlSink( writer, renderingContext );

        DependenciesRenderer r = new DependenciesRenderer( sink, model );

        r.render();
    }
}
