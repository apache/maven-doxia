package org.apache.maven.doxia.sink.impl;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.SinkFactory;

/**
 * The RandomAccessSink provides the ability to create a {@link Sink} with hooks.
 * A page can be prepared by first creating its structure and specifying the positions of these hooks.
 * After specifying the structure, the page can be filled with content from one or more models.
 * These hooks can prevent you to have to loop over the model multiple times to build the page as desired.
 *
 * @author Robert Scholte
 * @since 1.3
 */
public class RandomAccessSink
    implements Sink
{
    private SinkFactory sinkFactory;

    private String encoding;

    private OutputStream coreOutputStream;

    private Sink coreSink;

    private List<Sink> sinks = new ArrayList<>();

    private List<ByteArrayOutputStream> outputStreams = new ArrayList<>();

    private Sink currentSink;

    /**
     * <p>Constructor for RandomAccessSink.</p>
     *
     * @param sinkFactory a {@link org.apache.maven.doxia.sink.SinkFactory} object.
     * @param stream a {@link java.io.OutputStream} object.
     * @throws java.io.IOException if any.
     */
    public RandomAccessSink( SinkFactory sinkFactory, OutputStream stream )
        throws IOException
    {
        this.sinkFactory = sinkFactory;
        this.coreOutputStream = stream;
        this.currentSink = sinkFactory.createSink( stream );
        this.coreSink = this.currentSink;
    }

    /**
     * <p>Constructor for RandomAccessSink.</p>
     *
     * @param sinkFactory a {@link org.apache.maven.doxia.sink.SinkFactory} object.
     * @param stream a {@link java.io.OutputStream} object.
     * @param encoding a {@link java.lang.String} object.
     * @throws java.io.IOException if any.
     */
    public RandomAccessSink( SinkFactory sinkFactory, OutputStream stream, String encoding )
        throws IOException
    {
        this.sinkFactory = sinkFactory;
        this.coreOutputStream = stream;
        this.encoding = encoding;
        this.currentSink = sinkFactory.createSink( stream, encoding );
        this.coreSink = this.currentSink;
    }

    /**
     * <p>Constructor for RandomAccessSink.</p>
     *
     * @param sinkFactory a {@link org.apache.maven.doxia.sink.SinkFactory} object.
     * @param outputDirectory a {@link java.io.File} object.
     * @param outputName a {@link java.lang.String} object.
     * @throws java.io.IOException if any.
     */
    public RandomAccessSink( SinkFactory sinkFactory, File outputDirectory, String outputName )
        throws IOException
    {
        this.sinkFactory = sinkFactory;
        this.coreOutputStream = new FileOutputStream( new File( outputDirectory, outputName ) );
        this.currentSink = sinkFactory.createSink( coreOutputStream );
        this.coreSink = this.currentSink;
    }

    /**
     * <p>Constructor for RandomAccessSink.</p>
     *
     * @param sinkFactory a {@link org.apache.maven.doxia.sink.SinkFactory} object.
     * @param outputDirectory a {@link java.io.File} object.
     * @param outputName a {@link java.lang.String} object.
     * @param encoding a {@link java.lang.String} object.
     * @throws java.io.IOException if any.
     */
    public RandomAccessSink( SinkFactory sinkFactory, File outputDirectory, String outputName, String encoding )
        throws IOException
    {
        this.sinkFactory = sinkFactory;
        this.coreOutputStream = new FileOutputStream( new File( outputDirectory, outputName ) );
        this.encoding = encoding;
        this.currentSink = sinkFactory.createSink( coreOutputStream, encoding );
        this.coreSink = this.currentSink;
    }

    /** {@inheritDoc} */
    @Override
    public void address()
    {
        currentSink.address();
    }

    /** {@inheritDoc} */
    @Override
    public void address( SinkEventAttributes attributes )
    {
        currentSink.address( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void address_()
    {
        currentSink.address_();
    }

    /**
     * By calling this method a sink reference is added at the current position. You can write to both the new sink
     * reference and the original sink. After flushing all sinks will be flushed in the right order.
     *
     * @return a subsink reference you can write to
     */
    public Sink addSinkHook()
    {
        Sink subSink = null;
        try
        {
            ByteArrayOutputStream subOut = new ByteArrayOutputStream();
            ByteArrayOutputStream newOut = new ByteArrayOutputStream();

            outputStreams.add( subOut );
            outputStreams.add( newOut );

            if ( encoding != null )
            {
                subSink = sinkFactory.createSink( subOut, encoding );
                currentSink = sinkFactory.createSink( newOut, encoding );
            }
            else
            {
                subSink = sinkFactory.createSink( subOut );
                currentSink = sinkFactory.createSink( newOut );
            }
            sinks.add( subSink );
            sinks.add( currentSink );
        }
        catch ( IOException e )
        {
            // IOException can only be caused by our own ByteArrayOutputStream
        }
        return subSink;
    }

    /** {@inheritDoc} */
    @Override
    public void anchor( String name )
    {
        currentSink.anchor( name );
    }

    /** {@inheritDoc} */
    @Override
    public void anchor( String name, SinkEventAttributes attributes )
    {
        currentSink.anchor( name, attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void anchor_()
    {
        currentSink.anchor_();
    }

    /** {@inheritDoc} */
    @Override
    public void article()
    {
        currentSink.article();
    }

    /** {@inheritDoc} */
    @Override
    public void article( SinkEventAttributes attributes )
    {
        currentSink.article( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void article_()
    {
        currentSink.article_();
    }

    /** {@inheritDoc} */
    @Override
    public void author()
    {
        currentSink.author();
    }

    /** {@inheritDoc} */
    @Override
    public void author( SinkEventAttributes attributes )
    {
        currentSink.author( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void author_()
    {
        currentSink.author_();
    }

    /** {@inheritDoc} */
    @Override
    public void blockquote()
    {
        currentSink.blockquote();
    }

    /** {@inheritDoc} */
    @Override
    public void blockquote( SinkEventAttributes attributes )
    {
        currentSink.blockquote( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void blockquote_()
    {
        currentSink.blockquote_();
    }

    /** {@inheritDoc} */
    @Override
    public void body()
    {
        currentSink.body();
    }

    /** {@inheritDoc} */
    @Override
    public void body( SinkEventAttributes attributes )
    {
        currentSink.body( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void body_()
    {
        currentSink.body_();
    }

    /** {@inheritDoc} */
    @Override
    public void bold()
    {
        currentSink.bold();
    }

    /** {@inheritDoc} */
    @Override
    public void bold_()
    {
        currentSink.bold_();
    }

    /**
     * Close all sinks
     */
    public void close()
    {
        for ( Sink sink  : sinks )
        {
            // sink is responsible for closing it's stream
            sink.close();
        }
        coreSink.close();
    }

    /** {@inheritDoc} */
    @Override
    public void comment( String comment )
    {
        currentSink.comment( comment );
    }

    /** {@inheritDoc} */
    @Override
    public void content()
    {
        currentSink.content();
    }

    /** {@inheritDoc} */
    @Override
    public void content( SinkEventAttributes attributes )
    {
        currentSink.content( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void content_()
    {
        currentSink.content_();
    }

    /** {@inheritDoc} */
    @Override
    public void data( String value )
    {
        currentSink.data( value );
    }

    /** {@inheritDoc} */
    @Override
    public void data( String value, SinkEventAttributes attributes )
    {
        currentSink.data( value, attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void data_()
    {
        currentSink.data_();
    }

    /** {@inheritDoc} */
    @Override
    public void date()
    {
        currentSink.date();
    }

    /** {@inheritDoc} */
    @Override
    public void date( SinkEventAttributes attributes )
    {
        currentSink.date( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void date_()
    {
        currentSink.date_();
    }

    /** {@inheritDoc} */
    @Override
    public void definedTerm()
    {
        currentSink.definedTerm();
    }

    /** {@inheritDoc} */
    @Override
    public void definedTerm( SinkEventAttributes attributes )
    {
        currentSink.definedTerm( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void definedTerm_()
    {
        currentSink.definedTerm_();
    }

    /** {@inheritDoc} */
    @Override
    public void definition()
    {
        currentSink.definition();
    }

    /** {@inheritDoc} */
    @Override
    public void definition( SinkEventAttributes attributes )
    {
        currentSink.definition( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void definitionList()
    {
        currentSink.definitionList();
    }

    /** {@inheritDoc} */
    @Override
    public void definitionList( SinkEventAttributes attributes )
    {
        currentSink.definitionList( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void definitionListItem()
    {
        currentSink.definitionListItem();
    }

    /** {@inheritDoc} */
    @Override
    public void definitionListItem( SinkEventAttributes attributes )
    {
        currentSink.definitionListItem( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void definitionListItem_()
    {
        currentSink.definitionListItem_();
    }

    /** {@inheritDoc} */
    @Override
    public void definitionList_()
    {
        currentSink.definitionList_();
    }

    /** {@inheritDoc} */
    @Override
    public void definition_()
    {
        currentSink.definition_();
    }

    /** {@inheritDoc} */
    @Override
    public void division()
    {
        currentSink.division();
    }

    /** {@inheritDoc} */
    @Override
    public void division( SinkEventAttributes attributes )
    {
        currentSink.division( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void division_()
    {
        currentSink.division_();
    }

    /** {@inheritDoc} */
    @Override
    public void figure()
    {
        currentSink.figure();
    }

    /** {@inheritDoc} */
    @Override
    public void figure( SinkEventAttributes attributes )
    {
        currentSink.figure( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void figureCaption()
    {
        currentSink.figureCaption();
    }

    /** {@inheritDoc} */
    @Override
    public void figureCaption( SinkEventAttributes attributes )
    {
        currentSink.figureCaption( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void figureCaption_()
    {
        currentSink.figureCaption_();
    }

    /** {@inheritDoc} */
    @Override
    public void figureGraphics( String name )
    {
        currentSink.figureGraphics( name );
    }

    /** {@inheritDoc} */
    @Override
    public void figureGraphics( String src, SinkEventAttributes attributes )
    {
        currentSink.figureGraphics( src, attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void figure_()
    {
        currentSink.figure_();
    }

    /**
     * Flush all sinks
     */
    public void flush()
    {
        for ( int i = 0; i < sinks.size(); i++ )
        {
            // first flush to get complete buffer
            // sink is responsible for flushing it's stream
            Sink sink = sinks.get( i );
            sink.flush();

            ByteArrayOutputStream stream = outputStreams.get( i );
            try
            {
                coreOutputStream.write( stream.toByteArray() );
            }
            catch ( IOException e )
            {
                // @todo
            }
        }
        coreSink.flush();
    }

    /** {@inheritDoc} */
    @Override
    public void footer()
    {
        currentSink.footer();
    }

    /** {@inheritDoc} */
    @Override
    public void footer( SinkEventAttributes attributes )
    {
        currentSink.footer( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void footer_()
    {
        currentSink.footer_();
    }

    /** {@inheritDoc} */
    @Override
    public void head()
    {
        currentSink.head();
    }

    /** {@inheritDoc} */
    @Override
    public void head( SinkEventAttributes attributes )
    {
        currentSink.head( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void head_()
    {
        currentSink.head_();
    }

    /** {@inheritDoc} */
    @Override
    public void header()
    {
        currentSink.header();
    }

    /** {@inheritDoc} */
    @Override
    public void header( SinkEventAttributes attributes )
    {
        currentSink.header( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void header_()
    {
        currentSink.header_();
    }

    /** {@inheritDoc} */
    @Override
    public void horizontalRule()
    {
        currentSink.horizontalRule();
    }

    /** {@inheritDoc} */
    @Override
    public void horizontalRule( SinkEventAttributes attributes )
    {
        currentSink.horizontalRule( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void inline()
    {
        currentSink.inline();
    }

    /** {@inheritDoc} */
    @Override
    public void inline( SinkEventAttributes attributes )
    {
        currentSink.inline( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void inline_()
    {
        currentSink.inline_();
    }

    /** {@inheritDoc} */
    @Override
    public void italic()
    {
        currentSink.italic();
    }

    /** {@inheritDoc} */
    @Override
    public void italic_()
    {
        currentSink.italic_();
    }

    /** {@inheritDoc} */
    @Override
    public void lineBreak()
    {
        currentSink.lineBreak();
    }

    /** {@inheritDoc} */
    @Override
    public void lineBreak( SinkEventAttributes attributes )
    {
        currentSink.lineBreak( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void lineBreakOpportunity()
    {
        currentSink.lineBreakOpportunity();
    }

    /** {@inheritDoc} */
    @Override
    public void lineBreakOpportunity( SinkEventAttributes attributes )
    {
        currentSink.lineBreakOpportunity( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void link( String name )
    {
        currentSink.link( name );
    }

    /** {@inheritDoc} */
    @Override
    public void link( String name, SinkEventAttributes attributes )
    {
        currentSink.link( name, attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void link_()
    {
        currentSink.link_();
    }

    /** {@inheritDoc} */
    @Override
    public void list()
    {
        currentSink.list();
    }

    /** {@inheritDoc} */
    @Override
    public void list( SinkEventAttributes attributes )
    {
        currentSink.list( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void listItem()
    {
        currentSink.listItem();
    }

    /** {@inheritDoc} */
    @Override
    public void listItem( SinkEventAttributes attributes )
    {
        currentSink.listItem( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void listItem_()
    {
        currentSink.listItem_();
    }

    /** {@inheritDoc} */
    @Override
    public void list_()
    {
        currentSink.list_();
    }

    /** {@inheritDoc} */
    @Override
    public void monospaced()
    {
        currentSink.monospaced();
    }

    /** {@inheritDoc} */
    @Override
    public void monospaced_()
    {
        currentSink.monospaced_();
    }

    /** {@inheritDoc} */
    @Override
    public void navigation()
    {
        currentSink.navigation();
    }

    /** {@inheritDoc} */
    @Override
    public void navigation( SinkEventAttributes attributes )
    {
        currentSink.navigation( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void navigation_()
    {
        currentSink.navigation_();
    }

    /** {@inheritDoc} */
    @Override
    public void nonBreakingSpace()
    {
        currentSink.nonBreakingSpace();
    }

    /** {@inheritDoc} */
    @Override
    public void numberedList( int numbering )
    {
        currentSink.numberedList( numbering );
    }

    /** {@inheritDoc} */
    @Override
    public void numberedList( int numbering, SinkEventAttributes attributes )
    {
        currentSink.numberedList( numbering, attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void numberedListItem()
    {
        currentSink.numberedListItem();
    }

    /** {@inheritDoc} */
    @Override
    public void numberedListItem( SinkEventAttributes attributes )
    {
        currentSink.numberedListItem( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void numberedListItem_()
    {
        currentSink.numberedListItem_();
    }

    /** {@inheritDoc} */
    @Override
    public void numberedList_()
    {
        currentSink.numberedList_();
    }

    /** {@inheritDoc} */
    @Override
    public void pageBreak()
    {
        currentSink.pageBreak();
    }

    /** {@inheritDoc} */
    @Override
    public void paragraph()
    {
        currentSink.paragraph();
    }

    /** {@inheritDoc} */
    @Override
    public void paragraph( SinkEventAttributes attributes )
    {
        currentSink.paragraph( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void paragraph_()
    {
        currentSink.paragraph_();
    }

    /** {@inheritDoc} */
    @Override
    public void rawText( String text )
    {
        currentSink.rawText( text );
    }

    /** {@inheritDoc} */
    @Override
    public void section( int level, SinkEventAttributes attributes )
    {
        currentSink.section( level, attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void section1()
    {
        currentSink.section1();
    }

    /** {@inheritDoc} */
    @Override
    public void section1_()
    {
        currentSink.section1_();
    }

    /** {@inheritDoc} */
    @Override
    public void section2()
    {
        currentSink.section2();
    }

    /** {@inheritDoc} */
    @Override
    public void section2_()
    {
        currentSink.section2_();
    }

    /** {@inheritDoc} */
    @Override
    public void section3()
    {
        currentSink.section3();
    }

    /** {@inheritDoc} */
    @Override
    public void section3_()
    {
        currentSink.section3_();
    }

    /** {@inheritDoc} */
    @Override
    public void section4()
    {
        currentSink.section4();
    }

    /** {@inheritDoc} */
    @Override
    public void section4_()
    {
        currentSink.section4_();
    }

    /** {@inheritDoc} */
    @Override
    public void section5()
    {
        currentSink.section5();
    }

    /** {@inheritDoc} */
    @Override
    public void section5_()
    {
        currentSink.section5_();
    }

    /** {@inheritDoc} */
    @Override
    public void section6()
    {
        currentSink.section5();
    }

    /** {@inheritDoc} */
    @Override
    public void section6_()
    {
        currentSink.section5_();
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle()
    {
        currentSink.sectionTitle();
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle( int level, SinkEventAttributes attributes )
    {
        currentSink.sectionTitle( level, attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle1()
    {
        currentSink.sectionTitle1();
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle1_()
    {
        currentSink.sectionTitle1_();
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle2()
    {
        currentSink.sectionTitle2();
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle2_()
    {
        currentSink.sectionTitle2_();
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle3()
    {
        currentSink.sectionTitle3();
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle3_()
    {
        currentSink.sectionTitle3_();
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle4()
    {
        currentSink.sectionTitle4();
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle4_()
    {
        currentSink.sectionTitle4_();
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle5()
    {
        currentSink.sectionTitle5();
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle5_()
    {
        currentSink.sectionTitle5_();
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle6()
    {
        currentSink.sectionTitle5();
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle6_()
    {
        currentSink.sectionTitle5_();
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle_()
    {
        currentSink.sectionTitle_();
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle_( int level )
    {
        currentSink.sectionTitle_( level );
    }

    /** {@inheritDoc} */
    @Override
    public void section_( int level )
    {
        currentSink.section_( level );
    }

    /** {@inheritDoc} */
    @Override
    public void sidebar()
    {
        currentSink.sidebar();
    }

    /** {@inheritDoc} */
    @Override
    public void sidebar( SinkEventAttributes attributes )
    {
        currentSink.sidebar( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void sidebar_()
    {
        currentSink.sidebar_();
    }

    /** {@inheritDoc} */
    @Override
    public void table()
    {
        currentSink.table();
    }

    /** {@inheritDoc} */
    @Override
    public void table( SinkEventAttributes attributes )
    {
        currentSink.table( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void tableCaption()
    {
        currentSink.tableCaption();
    }

    /** {@inheritDoc} */
    @Override
    public void tableCaption( SinkEventAttributes attributes )
    {
        currentSink.tableCaption( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void tableCaption_()
    {
        currentSink.tableCaption_();
    }

    /** {@inheritDoc} */
    @Override
    public void tableCell()
    {
        currentSink.tableCell();
    }

    /** {@inheritDoc} */
    @Override
    public void tableCell( SinkEventAttributes attributes )
    {
        currentSink.tableCell( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void tableCell_()
    {
        currentSink.tableCell_();
    }

    /** {@inheritDoc} */
    @Override
    public void tableHeaderCell()
    {
        currentSink.tableHeaderCell();
    }

    /** {@inheritDoc} */
    @Override
    public void tableHeaderCell( SinkEventAttributes attributes )
    {
        currentSink.tableHeaderCell( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void tableHeaderCell_()
    {
        currentSink.tableHeaderCell_();
    }

    /** {@inheritDoc} */
    @Override
    public void tableRow()
    {
        currentSink.tableRow();
    }

    /** {@inheritDoc} */
    @Override
    public void tableRow( SinkEventAttributes attributes )
    {
        currentSink.tableRow( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void tableRow_()
    {
        currentSink.tableRow_();
    }

    /** {@inheritDoc} */
    @Override
    public void tableRows( int[] justification, boolean grid )
    {
        currentSink.tableRows( justification, grid );
    }

    /** {@inheritDoc} */
    @Override
    public void tableRows_()
    {
        currentSink.tableRows_();
    }

    /** {@inheritDoc} */
    @Override
    public void table_()
    {
        currentSink.table_();
    }

    /** {@inheritDoc} */
    @Override
    public void text( String text )
    {
        currentSink.text( text );
    }

    /** {@inheritDoc} */
    @Override
    public void text( String text, SinkEventAttributes attributes )
    {
        currentSink.text( text, attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void time( String datetime )
    {
        currentSink.time( datetime );
    }

    /** {@inheritDoc} */
    @Override
    public void time( String datetime, SinkEventAttributes attributes )
    {
        currentSink.time( datetime, attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void time_()
    {
        currentSink.time_();
    }

    /** {@inheritDoc} */
    @Override
    public void title()
    {
        currentSink.title();
    }

    /** {@inheritDoc} */
    @Override
    public void title( SinkEventAttributes attributes )
    {
        currentSink.title( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void title_()
    {
        currentSink.title_();
    }

    /** {@inheritDoc} */
    @Override
    public void unknown( String name, Object[] requiredParams, SinkEventAttributes attributes )
    {
        currentSink.unknown( name, requiredParams, attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void verbatim( SinkEventAttributes attributes )
    {
        currentSink.verbatim( attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void verbatim_()
    {
        currentSink.verbatim_();
    }
}
