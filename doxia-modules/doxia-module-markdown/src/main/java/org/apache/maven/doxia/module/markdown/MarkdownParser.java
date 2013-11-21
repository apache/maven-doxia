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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.doxia.module.xhtml.XhtmlParser;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.IOUtil;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.HeaderNode;
import org.pegdown.ast.HtmlBlockNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.RootNode;
import org.pegdown.ast.SuperNode;
import org.pegdown.ast.TextNode;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of {@link org.apache.maven.doxia.parser.Parser} for Markdown documents.
 * <p/>
 * Defers parsing to the <a href="http://pegdown.org">PegDown library</a>.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 1.3
 */
@Component( role = Parser.class, hint = "markdown" )
public class MarkdownParser
    extends XhtmlParser
{

    /**
     * The role hint for the {@link MarkdownParser} Plexus component.
     */
    public static final String ROLE_HINT = "markdown";

    /**
     * The {@link PegDownProcessor} used to convert Pegdown documents to HTML.
     */
    protected static final PegDownProcessor PEGDOWN_PROCESSOR =
        new PegDownProcessor( Extensions.ALL & ~Extensions.HARDWRAPS, Long.MAX_VALUE );

    /**
     * Regex that identifies a multimarkdown-style metadata section at the start of the document
     */
    private static final String MULTI_MARKDOWN_METADATA_SECTION =
        "^(((?:[^\\s:][^:]*):(?:.*(?:\r?\n\\p{Blank}+[^\\s].*)*\r?\n))+)(?:\\s*\r?\n)";

    /**
     * Regex that captures the key and value of a multimarkdown-style metadata entry.
     */
    private static final String MULTI_MARKDOWN_METADATA_ENTRY =
        "([^\\s:][^:]*):(.*(?:\r?\n\\p{Blank}+[^\\s].*)*)\r?\n";

    /**
     * In order to ensure that we have minimal risk of false positives when slurping metadata sections, the
     * first key in the metadata section must be one of these standard keys or else the entire metadata section is
     * ignored.
     */
    private static final String[] STANDARD_METADATA_KEYS =
        { "title", "author", "date", "address", "affiliation", "copyright", "email", "keywords", "language", "phone",
            "subtitle" };


    /**
     * {@inheritDoc}
     */
    @Override
    public void parse( Reader source, Sink sink )
        throws ParseException
    {
        try
        {
            String text = IOUtil.toString( source );
            StringBuilder html = new StringBuilder( text.length() * 2 );
            html.append( "<html>" );
            html.append( "<head>" );
            Pattern metadataPattern = Pattern.compile( MULTI_MARKDOWN_METADATA_SECTION, Pattern.MULTILINE );
            Matcher metadataMatcher = metadataPattern.matcher( text );
            boolean haveTitle = false;
            if ( metadataMatcher.find() )
            {
                metadataPattern = Pattern.compile( MULTI_MARKDOWN_METADATA_ENTRY, Pattern.MULTILINE );
                Matcher lineMatcher = metadataPattern.matcher( metadataMatcher.group( 1 ) );
                boolean first = true;
                while ( lineMatcher.find() )
                {
                    String key = StringUtils.trimToEmpty( lineMatcher.group( 1 ) );
                    if ( first )
                    {
                        boolean found = false;
                        for ( String k : STANDARD_METADATA_KEYS )
                        {
                            if ( k.equalsIgnoreCase( key ) )
                            {
                                found = true;
                                break;
                            }
                        }
                        if ( !found )
                        {
                            break;
                        }
                        first = false;
                    }
                    String value = StringUtils.trimToEmpty( lineMatcher.group( 2 ) );
                    if ( "title".equalsIgnoreCase( key ) )
                    {
                        haveTitle = true;
                        html.append( "<title>" );
                        html.append( StringEscapeUtils.escapeXml( value ) );
                        html.append( "</title>" );
                    }
                    else if ( "author".equalsIgnoreCase( key ) )
                    {
                        html.append( "<meta name=\'author\' content=\'" );
                        html.append( StringEscapeUtils.escapeXml( value ) );
                        html.append( "\' />" );
                    }
                    else if ( "date".equalsIgnoreCase( key ) )
                    {
                        html.append( "<meta name=\'date\' content=\'" );
                        html.append( StringEscapeUtils.escapeXml( value ) );
                        html.append( "\' />" );
                    }
                    else
                    {
                        html.append( "<meta name=\'" );
                        html.append( StringEscapeUtils.escapeXml( key ) );
                        html.append( "\' content=\'" );
                        html.append( StringEscapeUtils.escapeXml( value ) );
                        html.append( "\' />" );
                    }
                }
                if ( !first )
                {
                    text = text.substring( metadataMatcher.end() );
                }
            }
            RootNode rootNode = PEGDOWN_PROCESSOR.parseMarkdown( text.toCharArray() );
            if ( !haveTitle && rootNode.getChildren().size() > 0 )
            {
                // use the first (non-comment) node only if it is a heading
                int i = 0;
                Node firstNode = null;
                while ( i < rootNode.getChildren().size() && isHtmlComment(
                    ( firstNode = rootNode.getChildren().get( i ) ) ) )
                {
                    i++;
                }
                if ( firstNode instanceof HeaderNode )
                {
                    html.append( "<title>" );
                    html.append( StringEscapeUtils.escapeXml( nodeText( firstNode ) ) );
                    html.append( "</title>" );
                }
            }
            html.append( "</head>" );
            html.append( "<body>" );
            html.append( new MarkdownToDoxiaHtmlSerializer().toHtml( rootNode ) );
            html.append( "</body>" );
            html.append( "</html>" );
            super.parse( new StringReader( html.toString() ), sink );
        }
        catch ( IOException e )
        {
            throw new ParseException( "Failed reading Markdown source document", e );
        }
    }

    public static boolean isHtmlComment( Node node ) {
        if (node instanceof HtmlBlockNode) {
            HtmlBlockNode blockNode = (HtmlBlockNode) node;
            return blockNode.getText().startsWith( "<!--" );
        }
        return false;
    }

    public static String nodeText( Node node )
    {
        StringBuilder builder = new StringBuilder();
        if ( node instanceof TextNode )
        {
            builder.append( TextNode.class.cast( node ).getText() );
        }
        else
        {
            for ( Node n : node.getChildren() )
            {
                if ( n instanceof TextNode )
                {
                    builder.append( TextNode.class.cast( n ).getText() );
                }
                else if ( n instanceof SuperNode )
                {
                    builder.append( nodeText( n ) );
                }
            }
        }
        return builder.toString();
    }

}
