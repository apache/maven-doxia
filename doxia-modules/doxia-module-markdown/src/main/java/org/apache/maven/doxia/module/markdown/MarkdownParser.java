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
import org.pegdown.ast.RootNode;

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
        "^((?:[^\\s:][^:]*):(?:.*(?:\r?\n\\s[^\\s].*)*\r?\n))+(?:\\s*\r?\n)";

    /**
     * Regex that captures the key and value of a multimarkdown-style metadata entry.
     */
    private static final String MULTI_MARKDOWN_METADATA_ENTRY = "([^\\s:][^:]*):(.*(?:\r?\n\\s[^\\s].*)*)\r?\n";


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
            if ( metadataMatcher.find() )
            {
                metadataPattern = Pattern.compile( MULTI_MARKDOWN_METADATA_ENTRY, Pattern.MULTILINE );
                for ( int i = 1; i <= metadataMatcher.groupCount(); i++ )
                {
                    String line = metadataMatcher.group( i );
                    Matcher lineMatcher = metadataPattern.matcher( line );
                    if ( lineMatcher.matches() )
                    {
                        String key = StringUtils.trimToEmpty( lineMatcher.group( 1 ) );
                        String value = StringUtils.trimToEmpty( lineMatcher.group( 2 ) );
                        if ( "title".equalsIgnoreCase( key ) )
                        {
                            html.append( "<title>" );
                            html.append( StringEscapeUtils.escapeXml( value ) );
                            html.append( "</title>" );
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
                }
                text = text.substring( metadataMatcher.end() );
            }
            html.append( "</head>" );
            html.append( "<body>" );
            RootNode rootNode = PEGDOWN_PROCESSOR.parseMarkdown( text.toCharArray() );
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
}
