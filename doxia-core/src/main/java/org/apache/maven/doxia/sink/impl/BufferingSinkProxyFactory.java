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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.maven.doxia.sink.Sink;

/**
 * Buffers all method calls on the proxied Sink until its {@link Sink#flush()} is called.
 */
public class BufferingSinkProxyFactory implements SinkWrapperFactory {

    private static final class MethodWithArguments {
        private final Method method;
        private final Object[] args;

        MethodWithArguments(Method method, Object[] args) {
            super();
            this.method = method;
            this.args = args;
        }

        void invoke(Object object) {
            try {
                method.invoke(object, args);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new IllegalStateException("Could not call buffered method " + method, e);
            }
        }
    }

    public interface BufferingSink extends Sink {
        // just a marker interface
        Sink getBufferedSink();
    }

    private static final class BufferingSinkProxy implements InvocationHandler {
        private final Queue<MethodWithArguments> bufferedInvocations;
        private final Sink delegate;
        private static final Method FLUSH_METHOD;
        private static final Method GET_BUFFERED_SINK_METHOD;

        static {
            try {
                FLUSH_METHOD = Sink.class.getMethod("flush");
                GET_BUFFERED_SINK_METHOD = BufferingSink.class.getMethod("getBufferedSink");
            } catch (NoSuchMethodException | SecurityException e) {
                throw new IllegalStateException("Could not find flush method in Sink!", e);
            }
        }

        BufferingSinkProxy(Sink delegate) {
            bufferedInvocations = new LinkedList<>();
            this.delegate = delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.equals(FLUSH_METHOD)) {
                bufferedInvocations.forEach(i -> i.invoke(delegate));
                bufferedInvocations.clear();
            } else if (method.equals(GET_BUFFERED_SINK_METHOD)) {
                return delegate;
            } else {
                bufferedInvocations.add(new MethodWithArguments(method, args));
            }
            return null;
        }
    }

    @Override
    public Sink createWrapper(Sink delegate) {
        BufferingSinkProxy proxy = new BufferingSinkProxy(delegate);
        return (Sink) Proxy.newProxyInstance(
                delegate.getClass().getClassLoader(), new Class<?>[] {BufferingSink.class}, proxy);
    }

    public static BufferingSink castAsBufferingSink(Sink sink) {
        if (sink instanceof BufferingSink) {
            return (BufferingSink) sink;
        } else {
            throw new IllegalArgumentException("The given sink is not a BufferingSink but a " + sink.getClass());
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
