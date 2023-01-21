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

import java.util.Map;

import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;

/**
 * Abstract base class to execute <code>Macro</code>.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @since 1.0
 */
public abstract class AbstractMacro implements Macro {
    /**
     * Convert the Map of macro parameters to an AttributeSet.
     * No check of validity is done, all parameters are added.
     *
     * @param parameters the macro parameters.
     * @return a SinkEventAttributeSet containing the same parameters,
     *  or null if parameters is null.
     * @since 1.1.1.
     */
    protected static SinkEventAttributes getAttributesFromMap(Map<?, ?> parameters) {
        if (parameters == null) {
            return null;
        }

        final int count = parameters.size();

        if (count <= 0) {
            return null;
        }

        final SinkEventAttributeSet atts = new SinkEventAttributeSet(count);

        for (Map.Entry<?, ?> entry : parameters.entrySet()) {
            atts.addAttribute(entry.getKey(), entry.getValue());
        }

        return atts;
    }
}
