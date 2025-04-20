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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test AptUtils.
 *
 * @author ltheussl
 */
public class AptUtilsTest {
    /**
     * Test of isExternalLink method, of class AptUtils.
     */
    @Test
    public void testIsExternalLink() {
        String link = "http://maven.apache.org/";
        assertTrue(AptUtils.isExternalLink(link), "Should be an external link: " + link);

        link = "https://maven.apache.org/";
        assertTrue(AptUtils.isExternalLink(link), "Should be an external link: " + link);

        link = "HTTPS://MAVEN.APACHE.ORG/";
        assertTrue(AptUtils.isExternalLink(link), "Should be an external link: " + link);

        link = "ftp:/maven.apache.org/";
        assertTrue(AptUtils.isExternalLink(link), "Should be an external link: " + link);

        link = "mailto:maven@apache.org";
        assertTrue(AptUtils.isExternalLink(link), "Should be an external link: " + link);

        link = "file:/index.html";
        assertTrue(AptUtils.isExternalLink(link), "Should be an external link: " + link);

        link = "resource_type://domain:port/filepathname?query_string#anchor";
        assertTrue(AptUtils.isExternalLink(link), "Should be an external link: " + link);

        link = "index.html";
        assertFalse(AptUtils.isExternalLink(link), "Should NOT be an external link: " + link);

        link = "example.pdf";
        assertFalse(AptUtils.isExternalLink(link), "Should NOT be an external link: " + link);

        link = "./index.html";
        assertFalse(AptUtils.isExternalLink(link), "Should NOT be an external link: " + link);

        link = "../index.html";
        assertFalse(AptUtils.isExternalLink(link), "Should NOT be an external link: " + link);

        // Windows style separators "\" are not allowed

        link = "file:\\index.html";
        assertFalse(AptUtils.isExternalLink(link), "Should NOT be an external link: " + link);

        link = ".\\index.html";
        assertFalse(AptUtils.isExternalLink(link), "Should NOT be an external link: " + link);

        link = "..\\index.html";
        assertFalse(AptUtils.isExternalLink(link), "Should NOT be an external link: " + link);
    }

    /**
     * Test of isInternalLink method, of class AptUtils.
     */
    @Test
    public void testIsInternalLink() {
        String link = "index.html";
        assertTrue(AptUtils.isInternalLink(link), "Should be an internal link: " + link);
        link = "file:/index.html";
        assertFalse(AptUtils.isInternalLink(link), "Should NOT be an internal link: " + link);
        link = "./index.html";
        assertFalse(AptUtils.isInternalLink(link), "Should NOT be an internal link: " + link);
    }

    /**
     * Test of isLocalLink method, of class AptUtils.
     */
    @Test
    public void testIsLocalLink() {
        String link = "/index.html";
        assertTrue(AptUtils.isLocalLink(link), "Should be a local link: " + link);

        link = "./index.html";
        assertTrue(AptUtils.isLocalLink(link), "Should be a local link: " + link);

        link = "../index.html";
        assertTrue(AptUtils.isLocalLink(link), "Should be a local link: " + link);

        link = "index.html";
        assertFalse(AptUtils.isLocalLink(link), "Should NOT be a local link: " + link);

        link = ".\\index.html";
        assertFalse(AptUtils.isLocalLink(link), "Should NOT be a local link: " + link);

        link = "\\index.html";
        assertFalse(AptUtils.isLocalLink(link), "Should NOT be a local link: " + link);

        link = "..\\index.html";
        assertFalse(AptUtils.isLocalLink(link), "Should NOT be a local link: " + link);
    }
}
