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
package org.apache.maven.doxia.sink.impl;

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
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Objects;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkFactory;
import org.codehaus.plexus.util.WriterFactory;

/**
 * An abstract <code>SinkFactory</code> for Text markup syntax. <code>UTF-8</code> is used
 * when no encoding is specified.
 *
 * @author Hervé Boutemy
 * @author Benjamin Bentmann
 * @since 1.1
 */
public abstract class AbstractTextSinkFactory implements SinkFactory {
    /**
     * Create a text Sink for a given encoding.
     *
     * @param writer The writer for the sink output, never <code>null</code>.
     * @param encoding The character encoding used by the writer.
     * @return a Sink for text output in the given encoding.
     */
    protected abstract Sink createSink(Writer writer, String encoding);

    /** {@inheritDoc} */
    public Sink createSink(File outputDir, String outputName) throws IOException {
        return createSink(outputDir, outputName, WriterFactory.UTF_8);
    }

    /** {@inheritDoc} */
    public Sink createSink(File outputDir, String outputName, String encoding) throws IOException {
        Objects.requireNonNull(outputDir, "outputDir cannot be null");

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        } else {
            if (!outputDir.isDirectory()) {
                throw new IllegalArgumentException("The dir '" + outputDir + "' is not a directory.");
            }
        }

        Writer writer = WriterFactory.newWriter(new File(outputDir, outputName), encoding);

        return createSink(writer, encoding);
    }

    /** {@inheritDoc} */
    public Sink createSink(OutputStream out) throws IOException {
        return createSink(out, WriterFactory.UTF_8);
    }

    /** {@inheritDoc} */
    public Sink createSink(OutputStream out, String encoding) throws IOException {
        return createSink(new OutputStreamWriter(out, encoding), encoding);
    }
}
