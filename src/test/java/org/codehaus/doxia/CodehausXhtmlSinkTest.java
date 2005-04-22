/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.doxia;

import org.codehaus.doxia.module.xhtml.decoration.model.DecorationModel;
import org.codehaus.doxia.module.xhtml.decoration.model.DecorationModelReader;
import org.codehaus.doxia.module.xhtml.decoration.render.RenderingContext;
import org.codehaus.doxia.module.xhtml.codehaus.CodehausXhtmlSink;
import org.codehaus.doxia.sink.Sink;

import java.io.File;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: CodehausXhtmlSinkTest.java,v 1.1 2004/09/22 00:01:43 jvanzyl Exp $
 */
public class CodehausXhtmlSinkTest
    extends AbstractSinkTestCase
{
    protected String outputExtension()
    {
        return "html";
    }

    // START SNIPPET: foo
    
    protected Sink createSink()
        throws Exception
    {
        String xdoc = "test.apt";

        String siteXml = new File( basedir, "src/test/site/site.xml").getPath();

        DecorationModelReader b = new DecorationModelReader();

        DecorationModel navigation = b.createNavigation( siteXml );

        RenderingContext renderingContext = new RenderingContext( basedir,
                                                                  new File( basedir, xdoc ).getPath(),
                                                                  navigation );

        CodehausXhtmlSink sink = new CodehausXhtmlSink( getTestWriter(), renderingContext );

        return sink;
    }
    
    // END SNIPPET: foo
}
