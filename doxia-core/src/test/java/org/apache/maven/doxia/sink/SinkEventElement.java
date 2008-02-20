package org.apache.maven.doxia.sink;

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
 * A single sink event, used for testing purposes in order to check
 * the order and effect of some parser events.
 *
 * @author ltheussl
 * @version $Id$
 * @since 1.0-beta-1
 */
public class SinkEventElement
{
    /** The name of the sink event, ie the sink method name. */
    private final String methodName;

    /** The array of arguments to the sink method. */
    private final Object[] args;

    /**
     * A SinkEventElement is characterized by the method name and associated array of arguments.
     *
     * @param name The name of the sink event, ie the sink method name.
     * @param arguments The array of arguments to the sink method.
     *      For a no-arg element this may be null or an empty array.
     */
    public SinkEventElement( String name, Object[] arguments )
    {
        if ( name == null )
        {
            throw new NullPointerException( "Element name can't be null!" );
        }

        this.methodName = name;
        this.args = arguments;
    }

    /**
     * Return the name of the this event.
     *
     * @return The name of the sink event.
     */
    public String getName()
    {
        return this.methodName;
    }

    /**
     * Return the array of arguments to the sink method.
     *
     * @return the array of arguments to the sink method.
     */
    public Object[] getArgs()
    {
        return this.args;
    }

}
