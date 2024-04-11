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
import java.util.List;

import org.apache.maven.doxia.sink.Sink;

/**
 * A proxy for a Sink which captures all event/method names called on it.
 */
public class EventCapturingSinkProxy implements InvocationHandler {

    private final Sink sink;
    private final List<String> capturedEventNames;

    /**
     *
     * @param sink
     * @param capturedEventNames the list to receive the captured event/method names
     * @return a new, proxied sink
     */
    public static Sink newInstance(Sink sink, List<String> capturedEventNames) {
        return (Sink) java.lang.reflect.Proxy.newProxyInstance(
                sink.getClass().getClassLoader(),
                new Class<?>[] {Sink.class},
                new EventCapturingSinkProxy(sink, capturedEventNames));
    }

    private EventCapturingSinkProxy(Sink sink, List<String> capturedEventNames) {
        this.sink = sink;
        this.capturedEventNames = capturedEventNames;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        try {
            capturedEventNames.add(method.getName());
            result = method.invoke(sink, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
        return result;
    }
}
