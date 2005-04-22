package org.codehaus.doxia;

import org.codehaus.doxia.module.fml.FmlParser;
import org.codehaus.doxia.module.xhtml.decoration.render.RenderingContext;
import org.codehaus.doxia.module.xhtml.codehaus.CodehausXhtmlSink;
import org.codehaus.doxia.module.xhtml.decoration.model.DecorationModel;
import org.codehaus.doxia.module.xhtml.decoration.model.DecorationModelReader;
import org.codehaus.doxia.sink.Sink;
import junit.framework.TestCase;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Jason van Zyl</a>
 * @version $Id: XdocParserTest.java,v 1.4 2004/09/14 14:26:38 jvanzyl Exp $
 */
public class FmlParserTest
    extends TestCase
{
    public void testApt()
        throws Exception
    {
        String basedir = System.getProperty( "basedir" );

        String siteXml = new File( basedir, "src/test/site/site.xml").getPath();

        FmlParser fmlParser = new FmlParser();

        Reader reader = new FileReader( new File( basedir, "src/test/site/fml/faq.fml" ) );

        Writer writer = new FileWriter( "faq.html" );

        String xdoc = "repository-upload.xml";

        DecorationModelReader b = new DecorationModelReader();

        DecorationModel navigation = b.createNavigation( siteXml );

        RenderingContext renderingContext = new RenderingContext( basedir, 
                                                                  new File( basedir, xdoc ).getPath(),
                                                                  navigation );

        Sink sink = new CodehausXhtmlSink( writer, renderingContext );

        fmlParser.parse( reader, sink );
    }
}
