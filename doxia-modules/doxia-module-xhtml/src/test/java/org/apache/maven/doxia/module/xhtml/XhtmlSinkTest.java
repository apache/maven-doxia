package org.apache.maven.doxia.module.xhtml;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.doxia.module.xhtml.RenderingContext;
import org.apache.maven.doxia.sink.AbstractSinkTestCase;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.parser.Parser;

import java.io.File;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * @author Jason van Zyl
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

    protected Parser createParser()
    {
        return new XhtmlParser();
    }

    protected Sink createSink()
        throws Exception
    {
        String apt = "test.apt";

        RenderingContext renderingContext =
            new RenderingContext( getBasedirFile(), new File( getBasedirFile(), apt ).getPath(), "apt" );

        //PLXAPI: This horrible fake map is being used because someone neutered the directives approach in the
        // site renderer so that it half worked. Put it back and make it work properly.

        return new XhtmlSink( getTestWriter(), renderingContext, new FakeMap() );
    }

    protected Reader getTestReader()
        throws Exception
    {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( "fun.html" );

        InputStreamReader reader = new InputStreamReader( is );

        return reader;
    }

    // END SNIPPET: foo

    class FakeMap
        extends HashMap
    {
        public Object get( Object key )
        {
            return "fake";
        }
    }
}
