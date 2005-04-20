package org.codehaus.doxia;

import org.codehaus.doxia.module.xhtml.decoration.model.MavenDecorationModelReader;
import org.codehaus.doxia.module.xhtml.decoration.model.MavenDecorationModel;
import org.codehaus.doxia.module.xhtml.decoration.render.RenderingContext;
import org.codehaus.doxia.module.xhtml.SinkDescriptorReader;
import org.codehaus.doxia.module.xhtml.XhtmlSink;
import org.codehaus.doxia.sink.Sink;

import java.io.File;
import java.io.FileReader;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: XhtmlSinkTest.java,v 1.5 2004/09/22 00:01:43 jvanzyl Exp $
 */
public class XhtmlSinkTest
    extends AbstractSinkTestCase
{
    protected String outputExtension()
    {
        return "xhtml";
    }

    // START SNIPPET: foo

    protected Sink createSink() throws Exception
    {
        String xdoc = "test.apt";

        MavenDecorationModelReader b = new MavenDecorationModelReader();

        MavenDecorationModel navigation = b.createNavigation( "site.xml" );

        RenderingContext renderingContext = new RenderingContext( basedir, new File( basedir, xdoc ).getPath(),
            navigation );

        String basedir = System.getProperty( "basedir" );

        FileReader reader = new FileReader( new File( basedir, "src/main/resources/codehaus.dst" ) );

        SinkDescriptorReader sdr = new SinkDescriptorReader();

        Map directives = sdr.read( reader );

        XhtmlSink sink = new XhtmlSink( getTestWriter(), renderingContext, directives, null );

        return sink;
    }

    // END SNIPPET: foo
}
