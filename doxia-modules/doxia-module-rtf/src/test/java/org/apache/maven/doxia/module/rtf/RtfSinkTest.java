package org.apache.maven.doxia.module.rtf;

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

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.AbstractSinkTestCase;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.module.apt.AptParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class RtfSinkTest
    extends AbstractSinkTestCase
{
    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "rtf";
    }

    /** {@inheritDoc} */
    protected Parser createParser()
    {
        return new AptParser();
    }

    /** {@inheritDoc} */
    protected boolean isXmlSink()
    {
        return false;
    }

    /** {@inheritDoc} */
    protected Sink createSink()
        throws Exception
    {
        File outputFile = new File( getBasedirFile(), "target/output/test.rtf" );
        outputFile.getParentFile().mkdirs();
        return new RtfSink( new FileOutputStream( outputFile ) );
    }

    /** {@inheritDoc} */
    protected Reader getTestReader()
        throws Exception
    {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( "test.apt" );

        InputStreamReader reader = new InputStreamReader( is );

        return reader;
    }
}
