package org.apache.maven.doxia.parser;

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

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkAdapter;
import org.codehaus.plexus.PlexusTestCase;

import java.io.FileReader;
import java.io.Reader;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public abstract class AbstractParserTestCase
    extends PlexusTestCase
{
    protected abstract Parser getParser();

    protected abstract String getDocument();

    public void testParser()
        throws Exception
    {
        Sink sink = new SinkAdapter();

        Reader reader = new FileReader( getTestFile( getBasedir(), getDocument() ) );

        getParser().parse( reader, sink );
    }
}
