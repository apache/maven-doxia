package org.apache.maven.doxia.macro.manager;

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

import org.apache.maven.doxia.macro.Macro;

import org.codehaus.plexus.PlexusTestCase;

/** @author Jason van Zyl */
public class MacroManagerTest
    extends PlexusTestCase
{
    /**
     * Test MacroManager.
     *
     * @throws java.lang.Exception if any.
     */
    public void testMacroManager()
        throws Exception
    {
        MacroManager mm = lookup( MacroManager.ROLE );

        assertNotNull( mm );

        Macro macro = mm.getMacro( "snippet" );
        assertNotNull( macro );

        try
        {
            macro = mm.getMacro( "weirdId" );
            fail( "should not exist!" );
        }
        catch ( MacroNotFoundException macroNotFoundException )
        {
            assertNotNull( macroNotFoundException );
        }
    }
}
