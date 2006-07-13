package org.apache.maven.doxia.macro;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Warp an exception that occurs during the execution of a Doxia macro.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 */
public class MacroExecutionException
    extends Exception
{
    public MacroExecutionException( String message )
    {
        super( message );
    }

    public MacroExecutionException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
