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
package org.apache.maven.doxia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;

/**
 * Reads the Sisu index and the {@code META-INF/services} files from the test class path, so that tests can check
 * that both list the same implementations.
 */
public final class ServiceLoaderTestSupport {

    private ServiceLoaderTestSupport() {}

    /**
     * Returns the classes of the Sisu index that are assignable to the given role, minus the given exceptions.
     */
    public static Set<String> sisuIndex(Class<?> role, String... excluded) throws Exception {
        Set<String> result = new TreeSet<>();
        for (String name : lines("META-INF/sisu/javax.inject.Named")) {
            Class<?> type = Class.forName(name, false, role.getClassLoader());
            if (role.isAssignableFrom(type)) {
                result.add(name);
            }
        }
        for (String name : excluded) {
            result.remove(name);
        }
        return result;
    }

    /**
     * Returns the classes listed in the services files of the given role.
     */
    public static Set<String> servicesFile(Class<?> role) throws Exception {
        return new TreeSet<>(lines("META-INF/services/" + role.getName()));
    }

    private static Set<String> lines(String resource) throws IOException {
        Set<String> result = new TreeSet<>();
        Enumeration<URL> urls = ServiceLoaderTestSupport.class.getClassLoader().getResources(resource);
        while (urls.hasMoreElements()) {
            try (InputStream in = urls.nextElement().openStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        result.add(line);
                    }
                }
            }
        }
        return result;
    }
}
