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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkFactory;

/**
 * The RandomAccessSink provides the ability to create a {@link Sink} with hooks.
 * A page can be prepared by first creating its structure and specifying the positions of these hooks.
 * After specifying the structure, the page can be filled with content from one or more models.
 * These hooks can prevent you to have to loop over the model multiple times to build the page as desired.
 * @deprecated Use {@link BufferingSinkProxyFactory} instead which buffers on the (higher) Sink API level
 * which usually is less memory intense than buffering the output stream which is done by this class.
 * Also it doesn't require dynamically creating new sinks leveraging a {@link SinkFactory}.
 *
 * @author Robert Scholte
 * @since 1.3
 * @see BufferingSinkProxyFactory
 */
@Deprecated
public class RandomAccessSink extends SinkWrapper {
    private SinkFactory sinkFactory;

    private String encoding;

    private OutputStream coreOutputStream;

    private Sink coreSink;

    private List<Sink> sinks = new ArrayList<>();

    private List<ByteArrayOutputStream> outputStreams = new ArrayList<>();

    private Sink currentSink;

    /**
     * <p>Constructor for RandomAccessSink.</p>
     *
     * @param sinkFactory a {@link org.apache.maven.doxia.sink.SinkFactory} object.
     * @param stream a {@link java.io.OutputStream} object.
     * @throws java.io.IOException if any.
     */
    public RandomAccessSink(SinkFactory sinkFactory, OutputStream stream) throws IOException {
        super(sinkFactory.createSink(stream));
        this.sinkFactory = sinkFactory;
        this.coreOutputStream = stream;
        this.coreSink = getWrappedSink();
    }

    /**
     * <p>Constructor for RandomAccessSink.</p>
     *
     * @param sinkFactory a {@link org.apache.maven.doxia.sink.SinkFactory} object.
     * @param stream a {@link java.io.OutputStream} object.
     * @param encoding a {@link java.lang.String} object.
     * @throws java.io.IOException if any.
     */
    public RandomAccessSink(SinkFactory sinkFactory, OutputStream stream, String encoding) throws IOException {
        super(sinkFactory.createSink(stream, encoding));
        this.sinkFactory = sinkFactory;
        this.coreOutputStream = stream;
        this.encoding = encoding;
        this.coreSink = getWrappedSink();
    }

    /**
     * <p>Constructor for RandomAccessSink.</p>
     *
     * @param sinkFactory a {@link org.apache.maven.doxia.sink.SinkFactory} object.
     * @param outputDirectory a {@link java.io.File} object.
     * @param outputName a {@link java.lang.String} object.
     * @throws java.io.IOException if any.
     */
    public RandomAccessSink(SinkFactory sinkFactory, File outputDirectory, String outputName) throws IOException {
        super(null);
        this.sinkFactory = sinkFactory;
        this.coreOutputStream = new FileOutputStream(new File(outputDirectory, outputName));
        this.currentSink = sinkFactory.createSink(coreOutputStream);
        this.coreSink = this.currentSink;
        setWrappedSink(currentSink);
    }

    /**
     * <p>Constructor for RandomAccessSink.</p>
     *
     * @param sinkFactory a {@link org.apache.maven.doxia.sink.SinkFactory} object.
     * @param outputDirectory a {@link java.io.File} object.
     * @param outputName a {@link java.lang.String} object.
     * @param encoding a {@link java.lang.String} object.
     * @throws java.io.IOException if any.
     */
    public RandomAccessSink(SinkFactory sinkFactory, File outputDirectory, String outputName, String encoding)
            throws IOException {
        super(null);
        this.sinkFactory = sinkFactory;
        this.coreOutputStream = new FileOutputStream(new File(outputDirectory, outputName));
        this.encoding = encoding;
        this.currentSink = sinkFactory.createSink(coreOutputStream, encoding);
        this.coreSink = this.currentSink;
        setWrappedSink(currentSink);
    }

    /**
     * By calling this method a sink reference is added at the current position. You can write to both the new sink
     * reference and the original sink. After flushing all sinks will be flushed in the right order.
     *
     * @return a subsink reference you can write to
     */
    public Sink addSinkHook() {
        Sink subSink = null;
        try {
            ByteArrayOutputStream subOut = new ByteArrayOutputStream();
            ByteArrayOutputStream newOut = new ByteArrayOutputStream();

            outputStreams.add(subOut);
            outputStreams.add(newOut);

            if (encoding != null) {
                subSink = sinkFactory.createSink(subOut, encoding);
                currentSink = sinkFactory.createSink(newOut, encoding);
            } else {
                subSink = sinkFactory.createSink(subOut);
                currentSink = sinkFactory.createSink(newOut);
            }
            sinks.add(subSink);
            sinks.add(currentSink);
            setWrappedSink(currentSink);
        } catch (IOException e) {
            // IOException can only be caused by our own ByteArrayOutputStream
        }
        return subSink;
    }

    /**
     * Close all sinks
     */
    public void close() {
        for (Sink sink : sinks) {
            // sink is responsible for closing it's stream
            sink.close();
        }
        coreSink.close();
    }

    /**
     * Flush all sinks
     */
    public void flush() {
        for (int i = 0; i < sinks.size(); i++) {
            // first flush to get complete buffer
            // sink is responsible for flushing it's stream
            Sink sink = sinks.get(i);
            sink.flush();

            ByteArrayOutputStream stream = outputStreams.get(i);
            try {
                coreOutputStream.write(stream.toByteArray());
            } catch (IOException e) {
                // @todo
            }
        }
        coreSink.flush();
    }
}
