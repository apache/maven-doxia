package org.apache.maven.doxia.module.twiki.parser;

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

import org.apache.maven.doxia.sink.Sink;

/**
 * Represents an anchor
 *
 * @author Juan F. Codagnone
 */
class AnchorBlock
    implements Block
{
    /**
     * anchor name
     */
    private final String name;

    /**
     * Creates the AnchorBlock.
     *
     * @param name name of the anchor, not null.
     */
    AnchorBlock( final String name )
    {
        if ( name == null )
        {
            throw new IllegalArgumentException( "argument can't be null" );
        }
        this.name = name;
    }

    /** {@inheritDoc} */
    public final void traverse( final Sink sink )
    {
        sink.anchor( name );
        sink.anchor_();
    }

    /** {@inheritDoc} */
    public final boolean equals( final Object obj )
    {
        boolean ret = false;

        if ( obj == this )
        {
            ret = true;
        }
        else if ( obj instanceof AnchorBlock )
        {
            final AnchorBlock a = (AnchorBlock) obj;

            ret = name.equals( a.name );
        }

        return ret;
    }

    /**
     * {@inheritDoc}
     *
     * @return a int.
     */
    public final int hashCode()
    {
        return name.hashCode();
    }

    /**
     * {@inheritDoc}
     *
     * @return a {@link java.lang.String} object.
     */
    public String toString()
    {
        return name;
    }
}
