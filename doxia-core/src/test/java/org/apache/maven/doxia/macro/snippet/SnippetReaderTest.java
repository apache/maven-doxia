package org.apache.maven.doxia.macro.snippet;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SnippetReaderTest
{
    @Test
    public void testIsDemarcator()
    {
        String snippetId = "first";
        String what = "START";
        assertTrue( SnippetReader.isDemarcator( snippetId, what, "SNIPPET START first" ) );
        assertTrue( SnippetReader.isDemarcator( snippetId, what, "SNIPPET start first" ) );
        assertTrue( SnippetReader.isDemarcator( snippetId, what, "snippet START first" ) );
        assertTrue( SnippetReader.isDemarcator( snippetId, what, "snippet start first" ) );
        assertTrue( SnippetReader.isDemarcator( snippetId, what, "<!-- START SNIPPET: first -->" ) );
        
        assertFalse( SnippetReader.isDemarcator( snippetId, what, "SNIPPET START First" ) );
        assertFalse( SnippetReader.isDemarcator( snippetId, what, "SNIPPET START FIRST" ) );
        assertFalse( SnippetReader.isDemarcator( snippetId, what, "SNIPPET START first_id" ) );
        assertFalse( SnippetReader.isDemarcator( snippetId, what, "SNIPPET START id_first" ) );
        
    }
}
