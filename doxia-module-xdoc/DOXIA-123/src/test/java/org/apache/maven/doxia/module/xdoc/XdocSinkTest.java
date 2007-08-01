package org.apache.maven.doxia.module.xdoc;

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

import org.apache.maven.doxia.module.xdoc.XdocSink;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.AbstractSinkTestCase;
import org.apache.maven.doxia.parser.Parser;

import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:XdocSinkTest.java 348605 2005-11-24 12:02:44 +1100 (Thu, 24 Nov 2005) brett $
 */
public class XdocSinkTest
    extends AbstractSinkTestCase
{
    protected String outputExtension()
    {
        return "xml";
    }

    protected Parser createParser()
    {
        return new XdocParser();
    }

    protected Sink createSink()
        throws Exception
    {
        return new XdocSink( getTestWriter() );
    }

    protected Reader getTestReader()
        throws Exception
    {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( "report.xml" );

        InputStreamReader reader = new InputStreamReader( is );

        return reader;
    }
}
