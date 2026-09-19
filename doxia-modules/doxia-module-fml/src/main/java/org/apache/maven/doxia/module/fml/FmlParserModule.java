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
package org.apache.maven.doxia.module.fml;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.doxia.parser.module.AbstractParserModule;

/**
 * <p>FmlParserModule class.</p>
 *
 * @since 1.6
 * @deprecated FML is deprecated since 2.2.0 and scheduled for removal in the next major version.
 *     Convert FAQ pages to Markdown or XDoc, for example with doxia-converter; see
 *     https://github.com/apache/maven-doxia/issues/1101.
 */
@Deprecated
@Singleton
@Named("fml")
public class FmlParserModule extends AbstractParserModule {
    /**
     * Default constructor.
     */
    public FmlParserModule() {
        super("fml");
    }
}
