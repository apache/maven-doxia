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
import java.io.IOException;
import java.io.OutputStream;

/**
 * A factory that creates a <code>Sink</code> object.
 *
 * @author <a href="kenney@apache.org">Kenney Westerhof</a>
 * @version $Id$
 * @since 1.0-alpha-9
 */
public interface SinkFactory
{
    /** The Plexus SinkFactory Role. */
    String ROLE = SinkFactory.class.getName();

    /**
     * Create a <code>Sink</code> into a file.
     *
     * @param outputDir the not-null output dir.
     * @param outputName the not-null output name.
     * @return a <code>Sink</code> instance with a file as output.
     * @throws IOException if any
     */
    Sink createSink( File outputDir, String outputName )
        throws IOException;

    /**
     * Create a <code>Sink</code> into a file using a specified encoding.
     *
     * @param outputDir the not-null output dir.
     * @param outputName the not-null output name.
     * @param encoding the output encoding.
     * @return a <code>Sink</code> instance with a file as output and using specified encoding.
     * @throws IOException if any
     * @see #createSink(File, String)
     * @since 1.0-beta-1
     */
    Sink createSink( File outputDir, String outputName, String encoding )
        throws IOException;

    /**
     * Create a <code>Sink</code> into an OutputStream.
     *
     * @param out not null OutputStream to write the result.
     * @return a <code>Sink</code> instance.
     * @since 1.0-beta-1
     */
    Sink createSink( OutputStream out )
        throws IOException;

    /**
     * Create a <code>Sink</code> into an OutputStream using a specified encoding.
     *
     * @param out not null OutputStream to write the result.
     * @param encoding the output encoding.
     * @return a <code>Sink</code> instance using specified encoding.
     * @since 1.0-beta-1
     */
    Sink createSink( OutputStream out, String encoding )
        throws IOException;
}
