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
 * Represent a WikiWord
 *
 * @author Juan F. Codagnone
 */
class WikiWordBlock
    implements Block
{
    /**
     * the wiki word
     */
    private final String wikiword;

    /**
     * content to show in the wiki word link
     */
    private final Block content;

    /**
     * Resolves WikiWord links
     */
    private final WikiWordLinkResolver wikiWordLinkResolver;

    /**
     * @see WikiWordBlock(String, String)
     * @param aWikiword the wikiWord
     * @param resolver responsible of resolving the link to the wikiWord
     */
    WikiWordBlock( final String aWikiword, final WikiWordLinkResolver resolver )
    {
        this( aWikiword, aWikiword, resolver );
    }

    /**
     * Creates the WikiWordBlock.
     *
     * @param aWikiword the wiki word
     * @param aText text to show in the wiki link
     * @param resolver responsible of resolving the link to the wikiWord
     * @throws IllegalArgumentException if the wikiword is <code>null</code>
     * @deprecated
     */
    WikiWordBlock( final String aWikiword, final String aText, final WikiWordLinkResolver resolver )
    {
        this( aWikiword, new TextBlock( aText ), resolver );
    }

    /**
     * Creates the WikiWordBlock.
     *
     * @param aWikiword the wiki word
     * @param content content to show in the wiki link
     * @param resolver responsible of resolving the link to the wikiWord
     * @throws IllegalArgumentException if the wikiword is <code>null</code>
     */
    WikiWordBlock( final String aWikiword, final Block content, final WikiWordLinkResolver resolver )
    {
        if ( aWikiword == null || content == null || resolver == null )
        {
            throw new IllegalArgumentException( "arguments can't be null" );
        }
        this.wikiword = aWikiword;
        this.content = content;
        this.wikiWordLinkResolver = resolver;
    }

    /** {@inheritDoc} */
    public final void traverse( final Sink sink )
    {
        sink.link( wikiWordLinkResolver.resolveLink( wikiword ) );
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
        else if ( obj instanceof WikiWordBlock )
        {
            final WikiWordBlock w = (WikiWordBlock) obj;
            ret = wikiword.equals( w.wikiword ) && content.equals( w.content );
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
        final int magic1 = 17;
        final int magic2 = 37;

        return magic1 + magic2 * wikiword.hashCode() + magic2 * content.hashCode();
    }
}
