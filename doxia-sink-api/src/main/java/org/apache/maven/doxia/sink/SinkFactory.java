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
import java.io.Writer;

/**
 * A factory that creates a <code>Sink</code> object.
 *
 * @author <a href="kenney@apache.org">Kenney Westerhof</a>
 * @version $Id$
 * @since 1.0
 */
public interface SinkFactory
{
    /**
     * The Plexus SinkFactory Role.
     */
    String ROLE = SinkFactory.class.getName();

    /**
     * @param outputDir the not-null output dir.
     * @param outputName the not-null output name.
     * @return a <code>Sink</code> instance with a file as output and using UTF-8 as encoding.
     * @throws IOException if any
     */
    Sink createSink( File outputDir, String outputName )
        throws IOException;

    /**
     * @param outputDir the not-null output dir.
     * @param outputName the not-null output name.
     * @param encoding the output encoding.
     * @return a <code>Sink</code> instance with a file as output.
     * @throws IOException if any
     */
    Sink createSink( File outputDir, String outputName, String encoding )
        throws IOException;

    /**
     * @param writer a not-null writer.
     * @return a <code>Sink</code> instance.
     */
    Sink createSink( Writer writer );
}
