package org.apache.maven.doxia;

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

import org.apache.maven.doxia.module.xhtml.SinkDescriptorReader;
import org.apache.maven.doxia.module.xhtml.XhtmlSink;
import org.apache.maven.doxia.module.xhtml.decoration.model.DecorationModel;
import org.apache.maven.doxia.module.xhtml.decoration.model.DecorationModelReader;
import org.apache.maven.doxia.module.xhtml.decoration.render.RenderingContext;
import org.apache.maven.doxia.sink.Sink;

import java.io.File;
import java.io.FileReader;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class XhtmlSinkTest
    extends AbstractSinkTestCase
{
    protected String outputExtension()
    {
        return "xhtml";
    }

    // START SNIPPET: foo

    protected Sink createSink()
        throws Exception
    {
        String xdoc = "test.apt";

        DecorationModelReader b = new DecorationModelReader();

        DecorationModel navigation = b.createNavigation( "site.xml" );

        RenderingContext renderingContext =
            new RenderingContext( getBasedirFile(), new File( getBasedirFile(), xdoc ).getPath(), navigation );

        FileReader reader = new FileReader( new File( getBasedirFile(), "src/main/resources/codehaus.dst" ) );

        SinkDescriptorReader sdr = new SinkDescriptorReader();

        Map directives = sdr.read( reader );

        XhtmlSink sink = new XhtmlSink( getTestWriter(), renderingContext, directives );

        return sink;
    }

    // END SNIPPET: foo
}
