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

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ast.IndentedCodeBlock;
import com.vladsch.flexmark.html.CustomNodeRenderer;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.options.DataHolder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The node renderer that renders all the core nodes (comes last in the order of node renderers).
 */
@SuppressWarnings( "WeakerAccess" )
class MarkdownDoxiaNodeRenderer implements NodeRenderer
{
    public MarkdownDoxiaNodeRenderer( DataHolder options )
    {
    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers()
    {
        //noinspection unchecked
        return new HashSet<NodeRenderingHandler<?>>( Arrays.asList(
                new NodeRenderingHandler<IndentedCodeBlock>( IndentedCodeBlock.class,
                        new CustomNodeRenderer<IndentedCodeBlock>()
                        {
                            @Override
                            public void render( IndentedCodeBlock node, NodeRendererContext context, HtmlWriter html )
                            {
                                MarkdownDoxiaNodeRenderer.this.render( node, context, html );
                            }
                        } ),
                new NodeRenderingHandler<FencedCodeBlock>( FencedCodeBlock.class,
                        new CustomNodeRenderer<FencedCodeBlock>()
                        {
                            @Override
                            public void render( FencedCodeBlock node, NodeRendererContext context, HtmlWriter html )
                            {
                                MarkdownDoxiaNodeRenderer.this.render( node, context, html );
                            }
                        } )
        ) );
    }

    private void render( IndentedCodeBlock node, NodeRendererContext context, HtmlWriter html )
    {
        html.line();
        html.attr( "class", "source" ).tag( "div" );
        html.srcPosWithEOL( node.getChars() ).withAttr().tag( "pre" ).openPre();

        String noLanguageClass = context.getHtmlOptions().noLanguageClass.trim();
        if ( !noLanguageClass.isEmpty() )
        {
            html.attr( "class", noLanguageClass );
        }

        //html.srcPosWithEOL(node.getContentChars()).withAttr(CoreNodeRenderer.CODE_CONTENT).tag("code");
        String s = node.getContentChars().trimTailBlankLines().normalizeEndWithEOL();
        while ( !s.isEmpty() && s.charAt( 0 ) == '\n' )
        {
            html.raw( "<br/>" );
            s = s.substring( 1 );
        }
        html.text( s );

        //html.tag("/code");
        html.tag( "/pre" ).closePre();
        html.tag( "/div" );
        html.line();
    }

    private void render( FencedCodeBlock node, NodeRendererContext context, HtmlWriter html )
    {
        html.line();
        html.attr( "class", "source" ).tag( "div" );
        html.srcPosWithTrailingEOL( node.getChars() ).withAttr().tag( "pre" ).openPre();

        //BasedSequence info = node.getInfo();
        //if (info.isNotNull() && !info.isBlank()) {
        //    int space = info.indexOf(' ');
        //    BasedSequence language;
        //    if (space == -1) {
        //        language = info;
        //    } else {
        //        language = info.subSequence(0, space);
        //    }
        //    html.attr("class", context.getHtmlOptions().languageClassPrefix + language.unescape());
        //} else  {
        //    String noLanguageClass = context.getHtmlOptions().noLanguageClass.trim();
        //    if (!noLanguageClass.isEmpty()) {
        //        html.attr("class", noLanguageClass);
        //    }
        //}

        //html.srcPosWithEOL(node.getContentChars()).withAttr(CoreNodeRenderer.CODE_CONTENT).tag("code");
        String s = node.getContentChars().normalizeEOL();
        while ( !s.isEmpty() && s.charAt( 0 ) == '\n' )
        {
            html.raw( "<br/>" );
            s = s.substring( 1 );
        }
        html.text( s );

        //html.tag("/code");
        html.tag( "/pre" ).closePre();
        html.tag( "/div" );
        html.line();
    }

    /**
     * Factory for doxia node renderer
     */
    public static class Factory implements NodeRendererFactory
    {
        @Override
        public NodeRenderer create( final DataHolder options )
        {
            return new MarkdownDoxiaNodeRenderer( options );
        }
    }
}
