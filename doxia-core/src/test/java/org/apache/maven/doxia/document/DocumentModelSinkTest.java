package org.apache.maven.doxia.document;

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

import java.util.Iterator;
import java.util.List;

import org.apache.maven.doxia.sink.SinkEventAttributeSet;

import org.codehaus.plexus.PlexusTestCase;

/**
 * Test DocumentModelSink.
 *
 * @author ltheussl
 * @version $Id$
 * @since 1.1.1
 */
public class DocumentModelSinkTest
        extends PlexusTestCase
{
    /**
     * Test of title method, of class DocumentModelSink.
     */
    public void testTitle()
    {
        final DocumentModelSink sink = new DocumentModelSink();

        sink.title();
        sink.rawText( "" );
        sink.title_();

        assertNull( sink.getModel().getMeta().getTitle() );

        sink.title();
        sink.rawText( "Title" );
        sink.title_();

        assertEquals( "Title", sink.getModel().getMeta().getTitle() );
    }

    /**
     * Test of author method, of class DocumentModelSink.
     */
    public void testAuthor()
    {
        final DocumentModelSink sink = new DocumentModelSink();
        final SinkEventAttributeSet email =
                new SinkEventAttributeSet( new String[] {"email", "yo@com", "hobby", "breathing"} );

        sink.author();
        sink.text( "Author" );
        sink.text( "" );
        sink.author_();

        sink.author();
        sink.text( "" );
        sink.author_();

        sink.author( email );
        sink.text( "Author with email" );
        sink.rawText( "" );
        sink.author_();

        assertEquals( "Author, Author with email", sink.getModel().getMeta().getAllAuthorNames() );

        final List authors = sink.getModel().getMeta().getAuthors();
        assertEquals( 2, authors.size() );

        for ( final Iterator it = authors.iterator(); it.hasNext(); )
        {
            final DocumentAuthor author = (DocumentAuthor) it.next();
            final String name = author.getName();
            assertTrue( "Author".equals( name ) || "Author with email".equals( name ) );

            if ( "Author with email".equals( name ) )
            {
                assertEquals( "yo@com", author.getEmail() );
            }
        }
    }

    /**
     * Test of date method, of class DocumentModelSink.
     */
    public void testDate()
    {
        final DocumentModelSink sink = new DocumentModelSink();

        sink.date();
        sink.text( "" );
        sink.date_();

        assertNull( sink.getModel().getMeta().getDate() );

        sink.date();
        sink.text( "heute" );
        sink.date_();

        assertNull( sink.getModel().getMeta().getDate() );

        sink.date();
        sink.text( "1973-02-27" );
        sink.date_();

        final long feb27 = 99615600000L;
        assertEquals( feb27, sink.getModel().getMeta().getDate().getTime() );
    }

    /**
     * Test of unknown method, of class DocumentModelSink.
     */
    public void testUnknown()
    {
        final String id = "meta";
        final String name = "name";
        final String content = "content";

        final SinkEventAttributeSet meta =
                new SinkEventAttributeSet( new String[] {name, "generator", content, "me"} );

        final DocumentModelSink sink = new DocumentModelSink();

        sink.unknown( id, null, meta );
        assertEquals( "me", sink.getModel().getMeta().getGenerator() );

        meta.addAttribute( name, "lang" );
        meta.addAttribute( content, "en-us" );
        sink.unknown( id, null, meta );
        assertEquals( "en-us", sink.getModel().getMeta().getLanguage() );

        meta.addAttribute( name, "language" );
        meta.addAttribute( content, "de-at" );
        sink.unknown( id, null, meta );
        assertEquals( "de-at", sink.getModel().getMeta().getLanguage() );

        meta.addAttribute( name, "creator" );
        meta.addAttribute( content, "yo" );
        sink.unknown( id, null, meta );
        assertEquals( "yo", sink.getModel().getMeta().getCreator() );

        meta.addAttribute( name, "creation_date" );
        meta.addAttribute( content, "today" );
        sink.unknown( id, null, meta );
        assertNotNull( sink.getModel().getMeta().getCreationDate() );

        sink.getModel().getMeta().setCreationDate( null );
        meta.addAttribute( name, "date-creation-yyyymmdd" );
        meta.addAttribute( content, "20000101" );
        sink.unknown( id, null, meta );
        assertNotNull( sink.getModel().getMeta().getCreationDate() );

        meta.addAttribute( name, "description" );
        meta.addAttribute( content, "hot air" );
        sink.unknown( id, null, meta );
        assertEquals( "hot air", sink.getModel().getMeta().getDescription() );

        meta.addAttribute( name, "keywords" );
        meta.addAttribute( content, "a, b, c" );
        sink.unknown( id, null, meta );
        assertEquals( 3, sink.getModel().getMeta().getKeyWords().size() );
        assertEquals( "a, b, c", sink.getModel().getMeta().getAllKeyWords() );

        meta.addAttribute( name, "date" );
        meta.addAttribute( content, "today" );
        sink.unknown( id, null, meta );
        assertNotNull( sink.getModel().getMeta().getDate() );

        meta.addAttribute( name, "author" );
        meta.addAttribute( content, "me" );
        sink.unknown( id, null, meta );
        assertEquals( "me", sink.getModel().getMeta().getAllAuthorNames() );

        meta.addAttribute( name, "unknownmeta" );
        meta.addAttribute( content, "unknowncontent" );
        sink.unknown( id, null, meta );
        // unknown meta should log a warning
    }
}
