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
 * Block that represents a link.
 *
 * @author Juan F. Codagnone
 * @version $Id$
 */
class LinkBlock
    implements Block
{
    /**
     * link reference
     */
    private final String reference;

    /**
     * link text
     */
    private final Block content;

    /**
     * Creates the LinkBlock.
     *
     * @param reference reference anchor
     * @param text text to display
     * @deprecated
     */
    LinkBlock( final String reference, final String text )
    {
        this( reference, new TextBlock( text ) );
    }

    /**
     * Creates the LinkBlock.
     *
     * @param reference reference anchor
     * @param content block with the displayed content
     * @throws IllegalArgumentException if any argument is <code>null</code>
     */
    LinkBlock( final String reference, final Block content )
        throws IllegalArgumentException
    {
        if ( reference == null || content == null )
        {
            throw new IllegalArgumentException( "arguments can't be null" );
        }
        this.reference = reference;
        this.content = content;
    }

    /** {@inheritDoc} */
    public final void traverse( final Sink sink )
    {
        sink.link( reference );
        content.traverse( sink );
        sink.link_();

    }

    /** {@inheritDoc} */
    public final boolean equals( final Object obj )
    {
        boolean ret = false;

        if ( obj == this )
        {
            ret = true;
        }
        else if ( obj instanceof LinkBlock )
        {
            final LinkBlock l = (LinkBlock) obj;
            ret = reference.equals( l.reference ) && content.equals( l.content );
        }

        return ret;
    }

    /** {@inheritDoc} */
    public final int hashCode()
    {
        final int magic1 = 17;
        final int magic2 = 37;

        return magic1 + magic2 * reference.hashCode() + magic2 * content.hashCode();
    }
}
