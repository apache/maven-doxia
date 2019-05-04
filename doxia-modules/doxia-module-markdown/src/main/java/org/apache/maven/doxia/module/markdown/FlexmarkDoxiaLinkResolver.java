package org.apache.maven.doxia.module.markdown;

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

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vladsch.flexmark.ext.wikilink.internal.WikiLinkLinkResolver;
import com.vladsch.flexmark.html.IndependentLinkResolverFactory;
import com.vladsch.flexmark.html.LinkResolver;
import com.vladsch.flexmark.html.LinkResolverFactory;
import com.vladsch.flexmark.html.renderer.LinkResolverContext;
import com.vladsch.flexmark.html.renderer.LinkStatus;
import com.vladsch.flexmark.html.renderer.LinkType;
import com.vladsch.flexmark.html.renderer.ResolvedLink;
import com.vladsch.flexmark.util.ast.Node;

/**
 * The FlexmarkDoxiaLinkResolver rewrites the md, markdown links to html.
 *
 * Sample links it rewrites:
 * - doc.md -> doc.html
 * - doc.markdown -> doc.html
 * - doc.md#anchor -> doc.html#anchor
 * - doc.markdown#anchor -> doc.html#anchor
 * - :doc.md -> :doc.html
 * - :doc.markdown -> :doc.html
 *
 * Sample links it leaves untouched:
 * - http://doc.md
 * - https://doc.markdown
 * - doc.md.badformat
 * - doc.md#bad#format
 * - doc.md#bad.format
 */
public class FlexmarkDoxiaLinkResolver implements LinkResolver
{
    final Pattern pattern;

    public FlexmarkDoxiaLinkResolver( LinkResolverContext context )
    {
        this.pattern = Pattern.compile(
                            "^(?![^:]+:)([^\\.]+).(?:"
                          + MarkdownParserModule.FILE_EXTENSION
                          + "|"
                          + MarkdownParserModule.ALTERNATE_FILE_EXTENSION
                          + ")(#[^#\\.]*){0,1}$"
                        );
    }

    @Override
    public ResolvedLink resolveLink( Node node, LinkResolverContext context, ResolvedLink link )
    {
        if ( link.getLinkType() == LinkType.LINK )
        {
            Matcher matcher = this.pattern.matcher( link.getUrl() );
            if ( matcher.matches() )
            {
                return link.withStatus( LinkStatus.VALID ).withUrl( matcher.replaceAll( "$1.html$2" ) );
            }
        }

        return link;
    }

    /**
     * Factory that creates FlexmarkDoxiaLinkResolver objects.
     */
    public static class Factory extends IndependentLinkResolverFactory
    {
        @Override
        public Set<Class<? extends LinkResolverFactory>> getBeforeDependents()
        {
            Set<Class<? extends LinkResolverFactory>> set = new HashSet<>();
            set.add( WikiLinkLinkResolver.Factory.class );
            return set;
        }

        @Override
            public LinkResolver create( LinkResolverContext context )
        {
            return new FlexmarkDoxiaLinkResolver( context );
        }
    }
}
