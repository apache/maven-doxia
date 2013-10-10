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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.maven.doxia.module.xhtml.XhtmlParser;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.IOUtil;

import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.RootNode;

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
     * {@inheritDoc}
     */
    @Override
    public void parse( Reader source, Sink sink )
        throws ParseException
    {
        try
        {
            RootNode rootNode = PEGDOWN_PROCESSOR.parseMarkdown( IOUtil.toString( source ).toCharArray() );
            String markdownAsHtml = new MarkdownToDoxiaHtmlSerializer().toHtml( rootNode );
            super.parse( new StringReader( "<html><body>" + markdownAsHtml + "</body></html>" ), sink );
        }
        catch ( IOException e )
        {
            throw new ParseException( "Failed reading Markdown source document", e );
        }
    }
}
