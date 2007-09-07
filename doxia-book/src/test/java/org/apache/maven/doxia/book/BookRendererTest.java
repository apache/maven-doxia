package org.apache.maven.doxia.book;

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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.apache.maven.doxia.book.model.BookModel;

import java.io.File;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class BookRendererTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        BookDoxia doxia = (BookDoxia) lookup( BookDoxia.ROLE );

        File book1 = getTestFile( "src/test/resources/book-1.xml" );

        List files = FileUtils.getFiles( getTestFile( "src/test/resources/book-1" ), "**/*.apt, **/*.xml", "" );

        BookModel book = doxia.loadBook( book1 );

        doxia.renderBook( book, "pdf", files, getTestFile( "target/test-output/itext" ) );
        doxia.renderBook( book, "xhtml", files, getTestFile( "target/test-output/xhtml" ) );
        doxia.renderBook( book, "xdoc", files, getTestFile( "target/test-output/xdoc" ) );
        doxia.renderBook( book, "latex", files, getTestFile( "target/test-output/latex" ) );
        doxia.renderBook( book, "doc-book", files, getTestFile( "target/test-output/doc-book" ) );
        doxia.renderBook( book, "rtf", files, getTestFile( "target/test-output/rtf" ) );
    }
}
