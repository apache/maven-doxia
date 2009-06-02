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

import java.text.ParseException;

import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

import javax.swing.text.html.HTML.Attribute;

import org.apache.maven.doxia.sink.SinkAdapter;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.util.DoxiaUtils;

import org.codehaus.plexus.util.StringUtils;

/**
 * A Sink that collects meta-information emitted by a parser and stores it in a DocumentModel.
 *
 * <p>Use like:</p>
 *
 * <pre>
 * DocumentModelSink sink = new DocumentModelSink();
 * parser.parse( reader, sink );
 * DocumentModel model = sink.getModel();
 * </pre>
 *
 * <p>The sink only collects information from the <code>title()</code>, <code>author</code>
 * and <code>date</code> events, as well as meta-information emitted via <code>unknown()</code>,
 * all other events are ignored.</p>
 *
 * @author ltheussl
 * @version $Id$
 * @since 1.1.1.
 */

public class DocumentModelSink
        extends SinkAdapter
{
    private final DocumentModel model;

    private StringBuffer buffer;
    private DocumentAuthor author;

    /**
     * Create a DocumentModelSink.
     */
    public DocumentModelSink()
    {
        this.model = new DocumentModel();
        model.setMeta( new DocumentMeta() );
    }

    /**
     * Retrieve the DocumentModel created by this Sink.
     *
     * @return the DocumentModel.
     */
    public DocumentModel getModel()
    {
        return model;
    }

    /** Start recording a title. */
    public void title()
    {
        title( null );
    }

    /**
     * Start recording a title. Only text events within a title event are recorded.
     *
     * @param attributes ignored.
     */
    public void title( SinkEventAttributes attributes )
    {
        this.buffer = new StringBuffer();
    }

    /** End recording a title. */
    public void title_()
    {
        String title = buffer.toString();

        if ( StringUtils.isNotEmpty( title ) )
        {
            getModel().getMeta().setTitle( buffer.toString() );
        }

        this.buffer = null;
    }

    /** Start recording an author. */
    public void author()
    {
        author( null );
    }

    /**
     * Start recording an author. Only text events within an author event are recorded.
     *
     * @param attributes only email attribute is recognized.
     */
    public void author( SinkEventAttributes attributes )
    {
        this.buffer = new StringBuffer();
        this.author = new DocumentAuthor();

        if ( attributes != null )
        {
            for ( Enumeration e = attributes.getAttributeNames() ; e.hasMoreElements() ; )
            {
                String name = e.nextElement().toString();

                if ( name.equals( SinkEventAttributes.EMAIL ) )
                {
                    author.setEmail( attributes.getAttribute( name ).toString() );
                }
                else
                {
                    getLog().warn( "Ignoring unknown author attribute: " + name );
                }
            }
        }
    }

    /** End recording an author. */
    public void author_()
    {
        String auth = buffer.toString();

        if ( StringUtils.isNotEmpty( auth ) )
        {
            author.setName( buffer.toString() );
            model.getMeta().addAuthor( author );
        }

        this.author = null;
        this.buffer = null;
    }

    /** Start recording a date. */
    public void date()
    {
        date( null );
    }

    /**
     * Start recording a date. Only text events within a date event are recorded.
     *
     * @param attributes ignored.
     */
    public void date( SinkEventAttributes attributes )
    {
        this.buffer = new StringBuffer();
    }

    /** End recording a date. */
    public void date_()
    {
        String dat = buffer.toString();

        if ( StringUtils.isNotEmpty( dat ) )
        {
            try
            {
                Date date = DoxiaUtils.parseDate( buffer.toString() );
                model.getMeta().setDate( date );
            }
            catch ( ParseException ex )
            {
                getLog().warn( "Could not parse date: " + this.buffer.toString(), ex );
            }
        }

        this.buffer = null;
    }

    /**
     * Record a text.
     *
     * @param text the text to record.
     */
    public void text( String text )
    {
        text( text, null );
    }

    /**
     * Record a text.
     *
     * @param text the text to record.
     * @param attributes ignored.
     */
    public void text( String text, SinkEventAttributes attributes )
    {
        if ( this.buffer != null && StringUtils.isNotEmpty( text ) )
        {
            this.buffer.append( text );
        }
    }

    /**
     * Record a text.
     *
     * @param text the text to record.
     */
    public void rawText( String text )
    {
        if ( this.buffer != null && StringUtils.isNotEmpty( text ) )
        {
            this.buffer.append( text );
        }
    }

    /**
     * Record an unknown event. Only "meta" events are currently recognized.
     *
     * @param name the name of the event. If this is not "meta", the event is ignored.
     * @param requiredParams ignored.
     * @param attributes has to contain "name" and "content" attributes.
     */
    public void unknown( String name, Object[] requiredParams, SinkEventAttributes attributes )
    {
        if ( "meta".equals( name ) )
        {
            Object metaName = attributes.getAttribute( Attribute.NAME.toString() );
            Object metaContent = attributes.getAttribute( Attribute.CONTENT.toString() );

            if ( metaName == null || metaContent == null )
            {
                getLog().warn( "Missing name and/or content in meta, ignoring!" );
                return;
            }

            handleMeta( metaName.toString().toLowerCase( Locale.ENGLISH ),
                    metaContent.toString().toLowerCase( Locale.ENGLISH ) );
        }
    }

    private boolean handleMeta( String name, String content )
    {
        if ( "author".equals( name ) )
        {
            this.author( null );
            this.text( content );
            this.author_();
        }
        else if ( "date".equals( name ) )
        {
            this.date( null );
            this.text( content );
            this.date_();
        }
        else if ( "keywords".equals( name ) )
        {
            String[] keywords = StringUtils.split( content, "," );

            for ( int i = 0; i < keywords.length; i++ )
            {
                model.getMeta().addKeyWord( keywords[i].trim() );
            }
        }
        else if ( "description".equals( name ) )
        {
            model.getMeta().setDescription( content );
        }
        else if ( "generator".equals( name ) )
        {
            model.getMeta().setGenerator( content );
        }
        else if ( "language".equals( name ) || "lang".equals( name ) )
        {
            model.getMeta().setLanguage( content );
        }
        else if ( "creator".equals( name ) )
        {
            model.getMeta().setCreator( content );
        }
        else if ( "creation_date".equals( name ) )
        {
            try
            {
                model.getMeta().setCreationDate( DoxiaUtils.parseDate( content ) );
            }
            catch ( ParseException ex )
            {
                getLog().warn( "Could not parse date: " + content, ex );
            }
        }
        else if ( "date-creation-yyyymmdd".equals( name ) )
        {
            try
            {
                model.getMeta().setCreationDate( DoxiaUtils.parseDate( content ) );
            }
            catch ( ParseException ex )
            {
                getLog().warn( "Could not parse date: " + content, ex );
            }
        }
        else
        {
            getLog().warn( "Unknown meta: " + name );
        }

        return false;
    }
}
