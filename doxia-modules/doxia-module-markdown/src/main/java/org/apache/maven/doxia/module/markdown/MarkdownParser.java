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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.HtmlCommentBlock;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.ast.util.TextCollectingVisitor;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.options.MutableDataSet;
import com.vladsch.flexmark.ext.escaped.character.EscapedCharacterExtension;
import com.vladsch.flexmark.ext.abbreviation.AbbreviationExtension;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.definition.DefinitionExtension;
import com.vladsch.flexmark.ext.typographic.TypographicExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.wikilink.WikiLinkExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;

import org.apache.maven.doxia.markup.HtmlMarkup;
import org.apache.maven.doxia.module.xhtml.XhtmlParser;
import org.apache.maven.doxia.parser.AbstractParser;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.util.HtmlTools;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Implementation of {@link org.apache.maven.doxia.parser.Parser} for Markdown documents.
 * </p>
 * <p>
 * Defers effective parsing to the <a href="https://github.com/vsch/flexmark-java">flexmark-java library</a>,
 * which generates HTML content then delegates parsing of this content to a slightly modified Doxia Xhtml parser.
 * (before 1.8, the <a href="http://pegdown.org">PegDown library</a> was used)
 * </p>
 *
 * @author Vladimir Schneider
 * @author Julien Nicoulaud
 * @since 1.3
 */
@Singleton
@Named( MarkdownParser.ROLE_HINT )
public class MarkdownParser
    extends AbstractParser
{

    /**
     * The role hint for the {@link MarkdownParser} Plexus component.
     */
    public static final String ROLE_HINT = "markdown";

    /**
     * Regex that identifies a multimarkdown-style metadata section at the start of the document
     *
     * In order to ensure that we have minimal risk of false positives when slurping metadata sections, the
     * first key in the metadata section must be one of these standard keys or else the entire metadata section is
     * ignored.
     */
    private static final Pattern METADATA_SECTION_PATTERN = Pattern.compile(
            "\\A^\\s*"
            + "(?:title|author|date|address|affiliation|copyright|email|keywords|language|phone|subtitle)"
            + "[ \\t]*:[ \\t]*[^\\r\\n]*[ \\t]*$[\\r\\n]+"
            + "(?:^[ \\t]*[^:\\r\\n]+[ \\t]*:[ \\t]*[^\\r\\n]*[ \\t]*$[\\r\\n]+)*",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE );

    /**
     * Regex that captures the key and value of a multimarkdown-style metadata entry.
     */
    private static final Pattern METADATA_ENTRY_PATTERN = Pattern.compile(
            "^[ \\t]*([^:\\r\\n]+?)[ \\t]*:[ \\t]*([^\\r\\n]*)[ \\t]*$",
            Pattern.MULTILINE );

    /**
     * <p>getType.</p>
     *
     * @return a int.
     */
    @Override
    public int getType()
    {
        return TXT_TYPE;
    }

    /**
     * The parser of the HTML produced by Flexmark, that we will
     * use to convert this HTML to Sink events
     */
    @Inject
    private MarkdownHtmlParser parser;

    /**
     * Flexmark's Markdown parser (one static instance fits all)
     */
    private static final com.vladsch.flexmark.parser.Parser FLEXMARK_PARSER;

    /**
     * Flexmark's HTML renderer (its output will be re-parsed and converted to Sink events)
     */
    private static final HtmlRenderer FLEXMARK_HTML_RENDERER;

    // Initialize the Flexmark parser and renderer, once and for all
    static
    {
        MutableDataSet flexmarkOptions = new MutableDataSet();

        // Enable the extensions that we used to have in Pegdown
        flexmarkOptions.set( com.vladsch.flexmark.parser.Parser.EXTENSIONS, Arrays.asList(
                EscapedCharacterExtension.create(),
                AbbreviationExtension.create(),
                AutolinkExtension.create(),
                DefinitionExtension.create(),
                TypographicExtension.create(),
                TablesExtension.create(),
                WikiLinkExtension.create(),
                StrikethroughExtension.create()
        ) );

        // Disable wrong apostrophe replacement
        flexmarkOptions.set( TypographicExtension.SINGLE_QUOTE_UNMATCHED, "&apos;" );

        // Additional options on the HTML rendering
        flexmarkOptions.set( HtmlRenderer.HTML_BLOCK_OPEN_TAG_EOL, false );
        flexmarkOptions.set( HtmlRenderer.HTML_BLOCK_CLOSE_TAG_EOL, false );
        flexmarkOptions.set( HtmlRenderer.MAX_TRAILING_BLANK_LINES, -1 );

        // Build the Markdown parser
        FLEXMARK_PARSER = com.vladsch.flexmark.parser.Parser.builder( flexmarkOptions ).build();

        // Build the HTML renderer
        FLEXMARK_HTML_RENDERER = HtmlRenderer.builder( flexmarkOptions )
                .linkResolverFactory( new FlexmarkDoxiaLinkResolver.Factory() )
                .build();

    }

    /** {@inheritDoc} */
    @Override
    public void parse( Reader source, Sink sink, String reference )
        throws ParseException
    {
        try
        {
            // Markdown to HTML (using flexmark-java library)
            String html = toHtml( source );

            // then HTML to Sink API
            parser.parse( html, sink );
        }
        catch ( IOException e )
        {
            throw new ParseException( "Failed reading Markdown source document", e );
        }
    }

    /**
     * uses flexmark-java library to parse content and generate HTML output.
     *
     * @param source the Markdown source
     * @return HTML content generated by flexmark-java
     * @throws IOException passed through
     */
    String toHtml( Reader source )
        throws IOException
    {
        // Read the source
        String text = IOUtil.toString( source );

        // Now, build the HTML document
        StringBuilder html = new StringBuilder( 1000 );
        html.append( "<html>" );
        html.append( "<head>" );

        // First, we interpret the "metadata" section of the document and add the corresponding HTML headers
        Matcher metadataMatcher = METADATA_SECTION_PATTERN.matcher( text );
        boolean haveTitle = false;
        if ( metadataMatcher.find() )
        {
            Matcher entryMatcher = METADATA_ENTRY_PATTERN.matcher( metadataMatcher.group( 0 ) );
            while ( entryMatcher.find() )
            {
                String key = entryMatcher.group( 1 );
                String value = entryMatcher.group( 2 );
                if ( "title".equalsIgnoreCase( key ) )
                {
                    haveTitle = true;
                    html.append( "<title>" );
                    html.append( HtmlTools.escapeHTML( value, false ) );
                    html.append( "</title>" );
                }
                else
                {
                    html.append( "<meta name='" );
                    html.append( HtmlTools.escapeHTML( key ) );
                    html.append( "' content='" );
                    html.append( HtmlTools.escapeHTML( value ) );
                    html.append( "' />" );
                }
            }

            // Trim the metadata from the source
            text = text.substring( metadataMatcher.end( 0 ) );

        }

        // Now is the time to parse the Markdown document
        // (after we've trimmed out the metadatas, and before we check for its headings)
        Node documentRoot = FLEXMARK_PARSER.parse( text );

        // Special trick: if there is no title specified as a metadata in the header, we will use the first
        // heading as the document title
        if ( !haveTitle && documentRoot.hasChildren() )
        {
            // Skip the comment nodes
            Node firstNode = documentRoot.getFirstChild();
            while ( firstNode != null && firstNode instanceof HtmlCommentBlock )
            {
                firstNode = firstNode.getNext();
            }

            // If this first non-comment node is a heading, we use it as the document title
            if ( firstNode != null && firstNode instanceof Heading )
            {
                html.append( "<title>" );
                TextCollectingVisitor collectingVisitor = new TextCollectingVisitor();
                String headingText = collectingVisitor.collectAndGetText( firstNode );
                html.append( HtmlTools.escapeHTML( headingText, false ) );
                html.append( "</title>" );
            }
        }
        html.append( "</head>" );
        html.append( "<body>" );

        // Convert our Markdown document to HTML and append it to our HTML
        FLEXMARK_HTML_RENDERER.render( documentRoot, html );

        html.append( "</body>" );
        html.append( "</html>" );

        return html.toString();
    }

    /**
     * Internal parser for HTML generated by the Markdown library.
     *
     * 2 special things:
     * <ul>
     * <li> DIV elements are translated as Unknown Sink events
     * <li> PRE elements are all considered as boxed
     * </ul>
     * PRE elements need to be "boxed" because the XhtmlSink will surround the
     * corresponding verbatim() Sink event with a DIV element with class="source",
     * which is how most Maven Skin (incl. Fluido) recognize a block of code, which
     * needs to be highlighted accordingly.
     */
    @Named
    public static class MarkdownHtmlParser
        extends XhtmlParser
    {
        public MarkdownHtmlParser()
        {
            super();
        }

        @Override
        protected void init()
        {
            super.init();
            super.boxed = true;
        }

        @Override
        protected boolean baseEndTag( XmlPullParser parser, Sink sink )
        {
            boolean visited = super.baseEndTag( parser, sink );
            if ( !visited )
            {
                if ( parser.getName().equals( HtmlMarkup.DIV.toString() ) )
                {
                    handleUnknown( parser, sink, TAG_TYPE_END );
                    visited = true;
                }
            }
            return visited;
        }

        @Override
        protected boolean baseStartTag( XmlPullParser parser, Sink sink )
        {
            boolean visited = super.baseStartTag( parser, sink );
            if ( !visited )
            {
                if ( parser.getName().equals( HtmlMarkup.DIV.toString() ) )
                {
                    handleUnknown( parser, sink, TAG_TYPE_START );
                    super.boxed = true;
                    visited = true;
                }
            }
            return visited;
        }
    }
}
