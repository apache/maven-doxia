package org.apache.maven.doxia.util;

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

/**
 * General Doxia utility methods. The methods in this class should not assume
 * any specific Doxia module or document format.
 *
 * @author ltheussl
 * @since 1.0-beta-1
 * @version $Id$
 */
public class DoxiaUtils {

    /**
     * Checks if the given string corresponds to an internal link,
     * ie it is a link to an anchor within the same document.
     *
     * @param link The link to check.
     * @return True if the link starts with "#".
     */
    public static boolean isInternalLink( String link )
    {
        return link.startsWith( "#" );
    }

    /**
     * Checks if the given string corresponds to an external URI,
     * ie is not a link within the same document nor a relative link
     * to another document (a local link).
     *
     * @param link The link to check.
     * @return True if the link (ignoring case) starts with either of the
     * following: "http:/", "https:/", "ftp:/", "mailto:", "file:/".
     * Note that Windows style separators "\" are not allowed
     * for URIs, see  http://www.ietf.org/rfc/rfc2396.txt , section 2.4.3.
     */
    public static boolean isExternalLink( String link )
    {
        String text = link.toLowerCase();

        return ( text.indexOf( "http:/" ) == 0 || text.indexOf( "https:/" ) == 0
            || text.indexOf( "ftp:/" ) == 0 || text.indexOf( "mailto:" ) == 0
            || text.indexOf( "file:/" ) == 0 );
    }

    /**
     * Checks if the given string corresponds to a relative link to another document.
     *
     * @param link The link to check.
     * @return True if the link is neither an external nor an internal link.
     */
    public static boolean isLocalLink( String link )
    {
        return ( !isExternalLink( link ) && !isInternalLink( link ) );
    }

    private DoxiaUtils() {
        // utility class
    }

}
