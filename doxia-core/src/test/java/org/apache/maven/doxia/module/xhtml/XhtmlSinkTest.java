package org.apache.maven.doxia.module.xhtml;

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

import org.apache.maven.doxia.module.xhtml.decoration.render.RenderingContext;
import org.apache.maven.doxia.sink.AbstractSinkTestCase;
import org.apache.maven.doxia.sink.Sink;

import java.io.File;
import java.io.FileReader;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:XhtmlSinkTest.java 348605 2005-11-24 12:02:44 +1100 (Thu, 24 Nov 2005) brett $
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
        String apt = "test.apt";

        RenderingContext renderingContext =
            new RenderingContext( getBasedirFile(), new File( getBasedirFile(), apt ).getPath(), "apt" );

        FileReader reader = new FileReader( new File( getBasedirFile(), "src/test/resources/codehaus.dst" ) );

        SinkDescriptorReader sdr = new SinkDescriptorReader();

        Map directives = sdr.read( reader );

        return new XhtmlSink( getTestWriter(), renderingContext, directives );
    }

    // END SNIPPET: foo
}
