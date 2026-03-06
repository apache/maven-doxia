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
package org.apache.maven.doxia.module.markdown;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class BufferingStackWriter extends Writer {

    /**
     * A buffer stack that holds the output when the current context requires buffering.
     * The content of this buffer is supposed to be already escaped.
     */
    private final Queue<StringBuilder> bufferStack;

    private final Writer out;

    public BufferingStackWriter(Writer out) {
        this.out = out;
        this.bufferStack = Collections.asLifoQueue(new LinkedList<>());
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (bufferStack.isEmpty()) {
            out.write(cbuf, off, len);
        } else {
            bufferStack.element().append(cbuf, off, len);
        }
    }

    /**
     * Adds another buffer to the stack. The content of the current buffer is not affected, but the new content will
     * be written to the new buffer until it is polled.
     */
    public void addBuffer() {
        StringBuilder sb = new StringBuilder();
        bufferStack.add(sb);
    }

    /**
     * Retrieves the content of the current buffer without removing it from the stack.
     * Also writing to the StringBuffer returned by this method will affect the content of the current buffer.
     */
    public StringBuilder getCurrentBuffer() {
        return bufferStack.element();
    }

    /**
     * Retrieves the content of the current buffer without removing it from the stack.
     * In contrast to {@link #getCurrentBuffer()} the current buffer is cleared.
     */
    public String getAndClearCurrentBuffer() {
        String buffer = bufferStack.remove().toString();
        addBuffer();
        return buffer;
    }

    /**
     * Retrieves the content of the current buffer and removes it from the stack. Returns null if the stack is empty.
     * @return the content of the current buffer, or null if the stack is empty
     */
    public void removeBuffer() {
        bufferStack.remove();
    }

    @Override
    public void flush() throws IOException {
        // do nothing when there are buffers in the stack
        if (bufferStack.isEmpty()) {
            out.flush();
        }
    }

    @Override
    public void close() throws IOException {
        if (!bufferStack.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot close BufferingStackWriter while there are still buffers in the stack.");
        }
        out.close();
    }
}
