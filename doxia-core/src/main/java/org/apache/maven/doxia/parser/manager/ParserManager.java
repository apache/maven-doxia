package org.apache.maven.doxia.parser.manager;

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

import org.apache.maven.doxia.parser.Parser;

/**
 * Handles parser lookups.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @since 1.0
 */
public interface ParserManager
{

    /**
     * Returns the parser that corresponds to the given id.
     *
     * @param id The identifier.
     * @return The corresponding parser.
     * @throws org.apache.maven.doxia.parser.manager.ParserNotFoundException if no parser could be found
     * for the given id.
     */
    Parser getParser( String id )
        throws ParserNotFoundException;
}
