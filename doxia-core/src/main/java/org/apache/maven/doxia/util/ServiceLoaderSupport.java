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
package org.apache.maven.doxia.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helpers for the {@code fromServiceLoader(ClassLoader)} factories of the Doxia managers, which build a manager
 * from {@code META-INF/services} entries instead of a Sisu or Plexus container.
 * <p>
 * This class supports those factories and is not meant to be used by other callers.
 *
 * @since 2.2.0
 */
public final class ServiceLoaderSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLoaderSupport.class);

    private ServiceLoaderSupport() {}

    /**
     * Loads all providers of the given service, skipping (with a warning) every provider that cannot be
     * loaded, for example because one of its dependencies is missing from the class loader.
     *
     * @param <T> the service type
     * @param service the service type
     * @param classLoader the class loader to look providers up in
     * @return the providers in discovery order, never {@code null}
     */
    public static <T> List<T> load(Class<T> service, ClassLoader classLoader) {
        List<T> result = new ArrayList<>();
        Iterator<T> iterator = ServiceLoader.load(service, classLoader).iterator();
        while (true) {
            try {
                if (!iterator.hasNext()) {
                    break;
                }
            } catch (ServiceConfigurationError e) {
                // the provider list itself cannot be read; retrying could loop forever
                LOGGER.warn("Cannot look up further {} providers: {}", service.getSimpleName(), e.getMessage(), e);
                break;
            }
            try {
                result.add(iterator.next());
            } catch (ServiceConfigurationError e) {
                LOGGER.warn("Skipping a {} that cannot be loaded: {}", service.getSimpleName(), e.getMessage(), e);
            }
        }
        return result;
    }

    /**
     * Loads all providers of the given service and keys them by the value of their {@code javax.inject.Named}
     * annotation, which is the key Sisu uses for the injected {@code Map<String, T>}. If two providers share
     * a key, the first one found wins.
     *
     * @param <T> the service type
     * @param service the service type
     * @param classLoader the class loader to look providers up in
     * @return the providers by name, in discovery order, never {@code null}
     * @throws IllegalStateException if a provider has no non-empty {@code @Named} value
     */
    public static <T> Map<String, T> loadNamed(Class<T> service, ClassLoader classLoader) {
        Map<String, T> result = new LinkedHashMap<>();
        for (T provider : load(service, classLoader)) {
            String name = namedValue(provider.getClass());
            if (result.containsKey(name)) {
                LOGGER.warn(
                        "Ignoring {}: {} is already registered as '{}'",
                        provider.getClass().getName(),
                        result.get(name).getClass().getName(),
                        name);
            } else {
                result.put(name, provider);
            }
        }
        return result;
    }

    /**
     * Returns the value of the {@code javax.inject.Named} annotation of a class. The annotation is read
     * reflectively because it is RUNTIME-retained but not otherwise needed by callers.
     *
     * @param type the implementation class
     * @return the non-empty name
     * @throws IllegalStateException if the class has no {@code @Named} or its value is empty
     */
    static String namedValue(Class<?> type) {
        for (Annotation annotation : type.getAnnotations()) {
            if ("javax.inject.Named".equals(annotation.annotationType().getName())) {
                try {
                    Method value = annotation.annotationType().getMethod("value");
                    String name = (String) value.invoke(annotation);
                    if (name != null && !name.isEmpty()) {
                        return name;
                    }
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException("Cannot read @Named of " + type.getName(), e);
                }
            }
        }
        throw new IllegalStateException(
                type.getName() + " has no @javax.inject.Named with a value, so it cannot be registered under an id");
    }
}
