/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.doxia;

import junit.framework.TestCase;
import org.codehaus.doxia.module.xhtml.decoration.model.MavenDecorationModel;
import org.codehaus.doxia.module.xhtml.decoration.model.MavenDecorationModelReader;
import org.codehaus.doxia.module.xhtml.decoration.render.RenderingContext;
import org.codehaus.doxia.module.xhtml.SinkDescriptorReader;
import org.codehaus.doxia.module.xhtml.codehaus.CodehausXhtmlSink;
import org.codehaus.doxia.module.xhtml.SinkDescriptorReader;
import org.codehaus.doxia.plugin.maven.DependenciesRenderer;
import org.codehaus.doxia.sink.Sink;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.Model;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.io.FileReader;
import java.util.Map;
import java.util.Iterator;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: SinkDescriptorReaderTest.java,v 1.1 2004/09/22 00:01:43 jvanzyl Exp $
 */
public class SinkDescriptorReaderTest
    extends TestCase
{
    public void testSinkDescriptorReader()
        throws Exception
    {
        String basedir = System.getProperty( "basedir" );

        FileReader reader = new FileReader( new File( basedir, "src/main/resources/codehaus.dst" ) );

        SinkDescriptorReader sdr = new SinkDescriptorReader();

        Map directives = sdr.read( reader );

        for ( Iterator i = directives.keySet().iterator(); i.hasNext(); )
        {
            Object key = i.next();

            System.out.println( key + " => " + directives.get( key ) );
        }

    }
}
