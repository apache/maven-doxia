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
package org.apache.maven.doxia.module.apt;

import org.apache.maven.doxia.util.DoxiaUtils;

/**
 * A collection of utility methods for dealing with APT documents.
 *
 * @author ltheussl
 * @since 1.1
 */
public class AptUtils {

    /**
     * Checks if the given string corresponds to an external URI,
     * ie is not a link within the same document nor a link to another
     * document within the same site. This forwards to
     * {@link org.apache.maven.doxia.util.DoxiaUtils#isExternalLink(String)}.
     *
     * @param link The link to check.
     * @return True if DoxiaUtils.isExternalLink(link) returns true.
     * @see org.apache.maven.doxia.util.DoxiaUtils#isExternalLink(String)
     * @see #isInternalLink(String)
     * @see #isLocalLink(String)
     */
    public static boolean isExternalLink(String link) {
        return DoxiaUtils.isExternalLink(link);
    }

    /**
     * Checks if the given string corresponds to an internal link,
     * ie it is a link to an anchor within the same document.
     *
     * @param link The link to check.
     * @return True if link is neither an {@link #isExternalLink(String) external}
     * nor a {@link #isLocalLink(String) local} link.
     * @see org.apache.maven.doxia.util.DoxiaUtils#isInternalLink(String)
     * @see #isExternalLink(String)
     * @see #isLocalLink(String)
     */
    public static boolean isInternalLink(String link) {
        return (!isExternalLink(link) && !isLocalLink(link));
    }

    /**
     * Checks if the given string corresponds to a relative link to another document
     * within the same site.
     *
     * @param link The link to check.
     * @return True if the link starts with either "/", "./" or "../".
     * @see org.apache.maven.doxia.util.DoxiaUtils#isLocalLink(String)
     * @see #isExternalLink(String)
     * @see #isInternalLink(String)
     */
    public static boolean isLocalLink(String link) {
        return (link.startsWith("/") || link.startsWith("./") || link.startsWith("../"));
    }

    private AptUtils() {
        // utility class
    }
}
