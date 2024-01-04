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
package org.apache.maven.doxia.macro;

import java.io.File;
import java.util.Map;

import org.apache.maven.doxia.parser.AbstractParser;
import org.apache.maven.doxia.parser.Parser;

/**
 * <p>MacroRequest class.</p>
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @since 1.0
 */
public class MacroRequest {
    private static final String PARAM_SOURCE_CONTENT = "sourceContent";
    private static final String PARAM_PARSER = "parser";

    /** The current base directory. */
    private File basedir;

    /** A map of parameters. */
    private Map<String, Object> parameters;

    /**
     * <p>Constructor for MacroRequest.</p>
     *
     * @param sourceContent a {@link java.lang.String} object.
     * @param parser a new {@link org.apache.maven.doxia.parser.AbstractParser} object acting as secondary parser.
     * @param param a {@link java.util.Map} object.
     * @param basedir a {@link java.io.File} object.
     */
    public MacroRequest(String sourceContent, AbstractParser parser, Map<String, Object> param, File basedir) {
        this.parameters = param;
        this.basedir = basedir;
        param.put(PARAM_SOURCE_CONTENT, sourceContent);
        parser.setSecondParsing(true);
        param.put(PARAM_PARSER, parser);
    }

    /**
     * Returns the current base directory.
     *
     * @return The base dir.
     */
    public File getBasedir() {
        return basedir;
    }

    /**
     * Sets the current base directory.
     *
     * @param base The current base directory.
     */
    public void setBasedir(File base) {
        this.basedir = base;
    }

    /**
     * Returns the map of parameters.
     *
     * @return The map of parameters.
     */
    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     * Returns on object from the map of parameters
     * that corresponds to the given key.
     *
     * @param key The key to lookup the object.
     * @return The value object.
     */
    public Object getParameter(String key) {
        return parameters.get(key);
    }

    /**
     * <p>getSourceContent.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSourceContent() {
        return (String) getParameter(PARAM_SOURCE_CONTENT);
    }

    /**
     * <p>getParser.</p>
     *
     * @return a {@link org.apache.maven.doxia.parser.Parser} object. This is a new secondary parser.
     */
    public Parser getParser() {
        return (Parser) getParameter(PARAM_PARSER);
    }

    /**
     * <p>isInternalParameter.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean isInternalParameter(String name) {
        return PARAM_PARSER.equals(name) || PARAM_SOURCE_CONTENT.equals(name);
    }
}
