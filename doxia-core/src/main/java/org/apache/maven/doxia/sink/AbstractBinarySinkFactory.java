package org.apache.maven.doxia.sink;

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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.codehaus.plexus.util.WriterFactory;

/**
 * An abstract <code>SinkFactory</code> for binary output.
 *
 * @author <a href="mailto:hboutemy@apache.org">Hervé Boutemy</a>
 * @version $Id$
 * @since 1.1
 */
public abstract class AbstractBinarySinkFactory
    implements SinkFactory
{
    /** {@inheritDoc} */
    public Sink createSink( File outputDir, String outputName )
        throws IOException
    {
        return createSink( outputDir, outputName, WriterFactory.UTF_8 );
    }

    /** {@inheritDoc} */
    public Sink createSink( File outputDir, String outputName, String encoding )
        throws IOException
    {
        if ( outputDir == null )
        {
            throw new IllegalArgumentException( "outputDir cannot be null." );
        }

        if ( !outputDir.exists() )
        {
            outputDir.mkdirs();
        }
        else
        {
            if ( !outputDir.isDirectory() )
            {
                throw new IllegalArgumentException( "The dir '" + outputDir + "' is not a directory." );
            }
        }

        OutputStream out = new FileOutputStream( new File( outputDir, outputName ) );

        return createSink( out, encoding );
    }

    /** {@inheritDoc} */
    public Sink createSink( OutputStream out )
        throws IOException
    {
        return createSink( out, WriterFactory.UTF_8 );
    }
}
