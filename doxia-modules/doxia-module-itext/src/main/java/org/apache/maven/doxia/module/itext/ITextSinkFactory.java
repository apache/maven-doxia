package org.apache.maven.doxia.module.itext;

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

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkFactory;
import org.codehaus.plexus.util.WriterFactory;

/**
 * IText implementation of the Sink factory.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 * @plexus.component role="org.apache.maven.doxia.sink.SinkFactory" role-hint="itext"
 */
public class ITextSinkFactory
    implements SinkFactory
{
    /** {@inheritDoc} */
    public Sink createSink( File outputDir, String outputName )
        throws IOException
    {
        if ( !outputDir.isDirectory() )
        {
            throw new IllegalArgumentException( "The dir '" + outputDir + "' is not a directory or not exist" );
        }

        Writer writer = WriterFactory.newXmlWriter( new File( outputDir, outputName ) );

        return new ITextSink( writer );
    }

    /**
     * @deprecated since 1.0, the encoding parameter has no effect, always use the UTF-8 encoding.
     *
     * {@inheritDoc}
     */
    public Sink createSink( File outputDir, String outputName, String encoding )
        throws IOException
    {
        return createSink( outputDir, outputName );
    }

    /** {@inheritDoc} */
    public Sink createSink( Writer writer )
    {
        return new ITextSink( writer );
    }
}
