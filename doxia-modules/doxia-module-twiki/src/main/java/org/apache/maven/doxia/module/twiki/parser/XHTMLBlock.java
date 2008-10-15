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
 * An XHTML Block
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
class XHTMLBlock
    implements Block
{
    private final String tag;

    /**
     * Creates the XHTMLBlock.
     *
     * @param tag the tag, eg: &lt;pre&gt;
     * @throws IllegalArgumentException if the arguments are <code>null</code>
     */
    XHTMLBlock( final String tag )
        throws IllegalArgumentException
    {
        if ( tag == null )
        {
            throw new IllegalArgumentException( "argument can't be null" );
        }
        this.tag = tag;
    }

    /** {@inheritDoc}*/
    public final void traverse( final Sink sink )
    {
        if ( tag.trim().length() == 0 )
        {
            return;
        }

        sink.rawText( tag );
    }

    /** {@inheritDoc}*/
    public final boolean equals( final Object obj )
    {
        boolean ret = false;

        if ( obj == this )
        {
            ret = true;
        }
        else if ( obj instanceof XHTMLBlock )
        {
            final XHTMLBlock a = (XHTMLBlock) obj;

            ret = tag.equals( a.tag );
        }

        return ret;
    }

    /** {@inheritDoc}*/
    public final int hashCode()
    {
        return tag.hashCode();
    }
}
