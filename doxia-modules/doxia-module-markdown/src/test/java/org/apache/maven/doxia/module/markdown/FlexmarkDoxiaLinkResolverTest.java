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

import com.vladsch.flexmark.html.renderer.LinkStatus;
import com.vladsch.flexmark.html.renderer.LinkType;
import com.vladsch.flexmark.html.renderer.ResolvedLink;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class FlexmarkDoxiaLinkResolverTest
{
    @Test
    public void testResolveLink()
    {
        FlexmarkDoxiaLinkResolver flexmarkDoxiaLinkResolver = new FlexmarkDoxiaLinkResolver( null );

        checkLinkRewritten( flexmarkDoxiaLinkResolver, "doc.md", "doc.html" );
        checkLinkRewritten( flexmarkDoxiaLinkResolver, "doc.markdown", "doc.html" );

        checkLinkRewritten( flexmarkDoxiaLinkResolver, "doc.md#anchor", "doc.html#anchor" );
        checkLinkRewritten( flexmarkDoxiaLinkResolver, "doc.markdown#anchor", "doc.html#anchor" );

        checkLinkRewritten( flexmarkDoxiaLinkResolver, "./doc.md#anchor", "./doc.html#anchor" );
        checkLinkRewritten( flexmarkDoxiaLinkResolver, "./doc.markdown#anchor", "./doc.html#anchor" );
        checkLinkRewritten( flexmarkDoxiaLinkResolver, "./dir/doc.md#anchor", "./dir/doc.html#anchor" );
        checkLinkRewritten( flexmarkDoxiaLinkResolver, "./dir/doc.markdown#anchor", "./dir/doc.html#anchor" );

        checkLinkRewritten( flexmarkDoxiaLinkResolver, "../doc.md#anchor", "../doc.html#anchor" );
        checkLinkRewritten( flexmarkDoxiaLinkResolver, "../doc.markdown#anchor", "../doc.html#anchor" );
        checkLinkRewritten( flexmarkDoxiaLinkResolver, "../dir/doc.md#anchor", "../dir/doc.html#anchor" );
        checkLinkRewritten( flexmarkDoxiaLinkResolver, "../dir/doc.markdown#anchor", "../dir/doc.html#anchor" );

        checkLinkRewritten( flexmarkDoxiaLinkResolver, "./../doc.md#anchor", "./../doc.html#anchor" );
        checkLinkRewritten( flexmarkDoxiaLinkResolver, "./../doc.markdown#anchor", "./../doc.html#anchor" );
        checkLinkRewritten( flexmarkDoxiaLinkResolver, "./../dir/doc.md#anchor", "./../dir/doc.html#anchor" );
        checkLinkRewritten( flexmarkDoxiaLinkResolver, "./../dir/doc.markdown#anchor", "./../dir/doc.html#anchor" );

        checkLinkRewritten( flexmarkDoxiaLinkResolver, "../../doc.md#anchor", "../../doc.html#anchor" );
        checkLinkRewritten( flexmarkDoxiaLinkResolver, "../../doc.markdown#anchor", "../../doc.html#anchor" );
        checkLinkRewritten( flexmarkDoxiaLinkResolver, "../../dir/doc.md#anchor", "../../dir/doc.html#anchor" );
        checkLinkRewritten( flexmarkDoxiaLinkResolver, "../../dir/doc.markdown#anchor", "../../dir/doc.html#anchor" );

        // these edge cases are still allowed
        checkLinkRewritten( flexmarkDoxiaLinkResolver, ":doc.md", ":doc.html" );
        checkLinkRewritten( flexmarkDoxiaLinkResolver, ":doc.markdown", ":doc.html" );

        checkLinkRewritten( flexmarkDoxiaLinkResolver, ":doc.md#anchor", ":doc.html#anchor" );
        checkLinkRewritten( flexmarkDoxiaLinkResolver, ":doc.markdown#anchor", ":doc.html#anchor" );

        checkLinkLeftUnchanged( flexmarkDoxiaLinkResolver, "doc.md", LinkType.LINK_REF );
        checkLinkLeftUnchanged( flexmarkDoxiaLinkResolver, "doc.md", LinkType.IMAGE );
        checkLinkLeftUnchanged( flexmarkDoxiaLinkResolver, "doc.md", LinkType.IMAGE_REF );
        checkLinkLeftUnchanged( flexmarkDoxiaLinkResolver, "doc.markdown", LinkType.LINK_REF );
        checkLinkLeftUnchanged( flexmarkDoxiaLinkResolver, "doc.markdown", LinkType.IMAGE );
        checkLinkLeftUnchanged( flexmarkDoxiaLinkResolver, "doc.markdown", LinkType.IMAGE_REF );

        checkLinkLeftUnchanged( flexmarkDoxiaLinkResolver, "http://doc.md", LinkType.LINK );
        checkLinkLeftUnchanged( flexmarkDoxiaLinkResolver, "https://doc.md", LinkType.LINK );
        checkLinkLeftUnchanged( flexmarkDoxiaLinkResolver, "ftp://doc.md", LinkType.LINK );
        checkLinkLeftUnchanged( flexmarkDoxiaLinkResolver, "http://doc.markdown", LinkType.LINK );
        checkLinkLeftUnchanged( flexmarkDoxiaLinkResolver, "https://doc.markdown", LinkType.LINK );
        checkLinkLeftUnchanged( flexmarkDoxiaLinkResolver, "ftp://doc.markdown", LinkType.LINK );

        checkLinkLeftUnchanged( flexmarkDoxiaLinkResolver, "docs.md.badformat", LinkType.LINK );
        checkLinkLeftUnchanged( flexmarkDoxiaLinkResolver, "docs.markdown.badformat", LinkType.LINK );

        checkLinkLeftUnchanged( flexmarkDoxiaLinkResolver, "docs.md#bad#format", LinkType.LINK );
        checkLinkLeftUnchanged( flexmarkDoxiaLinkResolver, "docs.md#bad.format", LinkType.LINK );
        checkLinkLeftUnchanged( flexmarkDoxiaLinkResolver, "docs.md.bad#format", LinkType.LINK );
        checkLinkLeftUnchanged( flexmarkDoxiaLinkResolver, "docs.markdown#bad#format", LinkType.LINK );
        checkLinkLeftUnchanged( flexmarkDoxiaLinkResolver, "docs.markdown#bad.format", LinkType.LINK );
        checkLinkLeftUnchanged( flexmarkDoxiaLinkResolver, "docs.markdown.bad#format", LinkType.LINK );

        checkLinkLeftUnchanged( flexmarkDoxiaLinkResolver, ".../badpath/docs.md", LinkType.LINK );
    }

    private static void checkLinkLeftUnchanged( FlexmarkDoxiaLinkResolver flexmarkDoxiaLinkResolver, String url,
                                                LinkType linkType )
    {
        ResolvedLink originalLink = new ResolvedLink( linkType, url );
        ResolvedLink rewrittenLink = flexmarkDoxiaLinkResolver.resolveLink( null, null, originalLink );

        assertEquals( originalLink.getLinkType(), rewrittenLink.getLinkType() );
        assertEquals( originalLink.getUrl(), rewrittenLink.getUrl() );
        assertEquals( originalLink.getStatus(), rewrittenLink.getStatus() );
    }

    private static void checkLinkRewritten( FlexmarkDoxiaLinkResolver flexmarkDoxiaLinkResolver, String originalUrl,
                                            String expectedUrl )
    {
        ResolvedLink originalLink = new ResolvedLink( LinkType.LINK, originalUrl );
        ResolvedLink rewrittenLink = flexmarkDoxiaLinkResolver.resolveLink( null, null, originalLink );

        assertEquals( originalLink.getLinkType(), rewrittenLink.getLinkType() );
        assertEquals( expectedUrl, rewrittenLink.getUrl() );
        assertEquals( LinkStatus.VALID, rewrittenLink.getStatus() );
    }
}
