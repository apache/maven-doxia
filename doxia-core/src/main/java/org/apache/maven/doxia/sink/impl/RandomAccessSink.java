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

import org.apache.maven.doxia.logging.Log;
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

    public RandomAccessSink( SinkFactory sinkFactory, OutputStream stream )
        throws IOException
    {
        this.sinkFactory = sinkFactory;
        this.coreOutputStream = stream;
        this.currentSink = sinkFactory.createSink( stream );
        this.coreSink = this.currentSink;
    }

    public RandomAccessSink( SinkFactory sinkFactory, OutputStream stream, String encoding )
        throws IOException
    {
        this.sinkFactory = sinkFactory;
        this.coreOutputStream = stream;
        this.encoding = encoding;
        this.currentSink = sinkFactory.createSink( stream, encoding );
        this.coreSink = this.currentSink;
    }

    public RandomAccessSink( SinkFactory sinkFactory, File outputDirectory, String outputName )
        throws IOException
    {
        this.sinkFactory = sinkFactory;
        this.coreOutputStream = new FileOutputStream( new File( outputDirectory, outputName ) );
        this.currentSink = sinkFactory.createSink( coreOutputStream );
        this.coreSink = this.currentSink;
    }

    public RandomAccessSink( SinkFactory sinkFactory, File outputDirectory, String outputName, String encoding )
        throws IOException
    {
        this.sinkFactory = sinkFactory;
        this.coreOutputStream = new FileOutputStream( new File( outputDirectory, outputName ) );
        this.encoding = encoding;
        this.currentSink = sinkFactory.createSink( coreOutputStream, encoding );
        this.coreSink = this.currentSink;
    }

    @Override
    public void address()
    {
        currentSink.address();
    }

    @Override
    public void address( SinkEventAttributes attributes )
    {
        currentSink.address( attributes );
    }

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

    @Override
    public void anchor( String name )
    {
        currentSink.anchor( name );
    }

    @Override
    public void anchor( String name, SinkEventAttributes attributes )
    {
        currentSink.anchor( name, attributes );
    }

    @Override
    public void anchor_()
    {
        currentSink.anchor_();
    }

    @Override
    public void article()
    {
        currentSink.article();
    }

    @Override
    public void article( SinkEventAttributes attributes )
    {
        currentSink.article( attributes );
    }

    @Override
    public void article_()
    {
        currentSink.article_();
    }

    @Override
    public void author()
    {
        currentSink.author();
    }

    @Override
    public void author( SinkEventAttributes attributes )
    {
        currentSink.author( attributes );
    }

    @Override
    public void author_()
    {
        currentSink.author_();
    }

    @Override
    public void blockquote()
    {
        currentSink.blockquote();
    }

    @Override
    public void blockquote( SinkEventAttributes attributes )
    {
        currentSink.blockquote( attributes );
    }

    @Override
    public void blockquote_()
    {
        currentSink.blockquote_();
    }

    @Override
    public void body()
    {
        currentSink.body();
    }

    @Override
    public void body( SinkEventAttributes attributes )
    {
        currentSink.body( attributes );
    }

    @Override
    public void body_()
    {
        currentSink.body_();
    }

    @Override
    public void bold()
    {
        currentSink.bold();
    }

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

    @Override
    public void comment( String comment )
    {
        currentSink.comment( comment );
    }

    @Override
    public void content()
    {
        currentSink.content();
    }

    @Override
    public void content( SinkEventAttributes attributes )
    {
        currentSink.content( attributes );
    }

    @Override
    public void content_()
    {
        currentSink.content_();
    }

    @Override
    public void data( String value )
    {
        currentSink.data( value );
    }

    @Override
    public void data( String value, SinkEventAttributes attributes )
    {
        currentSink.data( value, attributes );
    }

    @Override
    public void data_()
    {
        currentSink.data_();
    }

    @Override
    public void date()
    {
        currentSink.date();
    }

    @Override
    public void date( SinkEventAttributes attributes )
    {
        currentSink.date( attributes );
    }

    @Override
    public void date_()
    {
        currentSink.date_();
    }

    @Override
    public void definedTerm()
    {
        currentSink.definedTerm();
    }

    @Override
    public void definedTerm( SinkEventAttributes attributes )
    {
        currentSink.definedTerm( attributes );
    }

    @Override
    public void definedTerm_()
    {
        currentSink.definedTerm_();
    }

    @Override
    public void definition()
    {
        currentSink.definition();
    }

    @Override
    public void definition( SinkEventAttributes attributes )
    {
        currentSink.definition( attributes );
    }

    @Override
    public void definitionList()
    {
        currentSink.definitionList();
    }

    @Override
    public void definitionList( SinkEventAttributes attributes )
    {
        currentSink.definitionList( attributes );
    }

    @Override
    public void definitionListItem()
    {
        currentSink.definitionListItem();
    }

    @Override
    public void definitionListItem( SinkEventAttributes attributes )
    {
        currentSink.definitionListItem( attributes );
    }

    @Override
    public void definitionListItem_()
    {
        currentSink.definitionListItem_();
    }

    @Override
    public void definitionList_()
    {
        currentSink.definitionList_();
    }

    @Override
    public void definition_()
    {
        currentSink.definition_();
    }

    @Override
    public void division()
    {
        currentSink.division();
    }

    @Override
    public void division( SinkEventAttributes attributes )
    {
        currentSink.division( attributes );
    }

    @Override
    public void division_()
    {
        currentSink.division_();
    }

    @Override
    public void figure()
    {
        currentSink.figure();
    }

    @Override
    public void figure( SinkEventAttributes attributes )
    {
        currentSink.figure( attributes );
    }

    @Override
    public void figureCaption()
    {
        currentSink.figureCaption();
    }

    @Override
    public void figureCaption( SinkEventAttributes attributes )
    {
        currentSink.figureCaption( attributes );
    }

    @Override
    public void figureCaption_()
    {
        currentSink.figureCaption_();
    }

    @Override
    public void figureGraphics( String name )
    {
        currentSink.figureGraphics( name );
    }

    @Override
    public void figureGraphics( String src, SinkEventAttributes attributes )
    {
        currentSink.figureGraphics( src, attributes );
    }

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

    @Override
    public void footer()
    {
        currentSink.footer();
    }

    @Override
    public void footer( SinkEventAttributes attributes )
    {
        currentSink.footer( attributes );
    }

    @Override
    public void footer_()
    {
        currentSink.footer_();
    }

    @Override
    public void head()
    {
        currentSink.head();
    }

    @Override
    public void head( SinkEventAttributes attributes )
    {
        currentSink.head( attributes );
    }

    @Override
    public void head_()
    {
        currentSink.head_();
    }

    @Override
    public void header()
    {
        currentSink.header();
    }

    @Override
    public void header( SinkEventAttributes attributes )
    {
        currentSink.header( attributes );
    }

    @Override
    public void header_()
    {
        currentSink.header_();
    }

    @Override
    public void horizontalRule()
    {
        currentSink.horizontalRule();
    }

    @Override
    public void horizontalRule( SinkEventAttributes attributes )
    {
        currentSink.horizontalRule( attributes );
    }

    @Override
    public void inline()
    {
        currentSink.inline();
    }

    @Override
    public void inline( SinkEventAttributes attributes )
    {
        currentSink.inline( attributes );
    }

    @Override
    public void inline_()
    {
        currentSink.inline_();
    }

    @Override
    public void italic()
    {
        currentSink.italic();
    }

    @Override
    public void italic_()
    {
        currentSink.italic_();
    }

    @Override
    public void lineBreak()
    {
        currentSink.lineBreak();
    }

    @Override
    public void lineBreak( SinkEventAttributes attributes )
    {
        currentSink.lineBreak( attributes );
    }

    @Override
    public void lineBreakOpportunity()
    {
        currentSink.lineBreakOpportunity();
    }

    @Override
    public void lineBreakOpportunity( SinkEventAttributes attributes )
    {
        currentSink.lineBreakOpportunity( attributes );
    }

    @Override
    public void link( String name )
    {
        currentSink.link( name );
    }

    @Override
    public void link( String name, SinkEventAttributes attributes )
    {
        currentSink.link( name, attributes );
    }

    @Override
    public void link_()
    {
        currentSink.link_();
    }

    @Override
    public void list()
    {
        currentSink.list();
    }

    @Override
    public void list( SinkEventAttributes attributes )
    {
        currentSink.list( attributes );
    }

    @Override
    public void listItem()
    {
        currentSink.listItem();
    }

    @Override
    public void listItem( SinkEventAttributes attributes )
    {
        currentSink.listItem( attributes );
    }

    @Override
    public void listItem_()
    {
        currentSink.listItem_();
    }

    @Override
    public void list_()
    {
        currentSink.list_();
    }

    @Override
    public void monospaced()
    {
        currentSink.monospaced();
    }

    @Override
    public void monospaced_()
    {
        currentSink.monospaced_();
    }

    @Override
    public void navigation()
    {
        currentSink.navigation();
    }

    @Override
    public void navigation( SinkEventAttributes attributes )
    {
        currentSink.navigation( attributes );
    }

    @Override
    public void navigation_()
    {
        currentSink.navigation_();
    }

    @Override
    public void nonBreakingSpace()
    {
        currentSink.nonBreakingSpace();
    }

    @Override
    public void numberedList( int numbering )
    {
        currentSink.numberedList( numbering );
    }

    @Override
    public void numberedList( int numbering, SinkEventAttributes attributes )
    {
        currentSink.numberedList( numbering, attributes );
    }

    @Override
    public void numberedListItem()
    {
        currentSink.numberedListItem();
    }

    @Override
    public void numberedListItem( SinkEventAttributes attributes )
    {
        currentSink.numberedListItem( attributes );
    }

    @Override
    public void numberedListItem_()
    {
        currentSink.numberedListItem_();
    }

    @Override
    public void numberedList_()
    {
        currentSink.numberedList_();
    }

    @Override
    public void pageBreak()
    {
        currentSink.pageBreak();
    }

    @Override
    public void paragraph()
    {
        currentSink.paragraph();
    }

    @Override
    public void paragraph( SinkEventAttributes attributes )
    {
        currentSink.paragraph( attributes );
    }

    @Override
    public void paragraph_()
    {
        currentSink.paragraph_();
    }

    @Override
    public void rawText( String text )
    {
        currentSink.rawText( text );
    }

    @Override
    public void section( int level, SinkEventAttributes attributes )
    {
        currentSink.section( level, attributes );
    }

    @Override
    public void section1()
    {
        currentSink.section1();
    }

    @Override
    public void section1_()
    {
        currentSink.section1_();
    }

    @Override
    public void section2()
    {
        currentSink.section2();
    }

    @Override
    public void section2_()
    {
        currentSink.section2_();
    }

    @Override
    public void section3()
    {
        currentSink.section3();
    }

    @Override
    public void section3_()
    {
        currentSink.section3_();
    }

    @Override
    public void section4()
    {
        currentSink.section4();
    }

    @Override
    public void section4_()
    {
        currentSink.section4_();
    }

    @Override
    public void section5()
    {
        currentSink.section5();
    }

    @Override
    public void section5_()
    {
        currentSink.section5_();
    }

    @Override
    public void section6()
    {
        currentSink.section5();
    }

    @Override
    public void section6_()
    {
        currentSink.section5_();
    }

    @Override
    public void sectionTitle()
    {
        currentSink.sectionTitle();
    }

    @Override
    public void sectionTitle( int level, SinkEventAttributes attributes )
    {
        currentSink.sectionTitle( level, attributes );
    }

    @Override
    public void sectionTitle1()
    {
        currentSink.sectionTitle1();
    }

    @Override
    public void sectionTitle1_()
    {
        currentSink.sectionTitle1_();
    }

    @Override
    public void sectionTitle2()
    {
        currentSink.sectionTitle2();
    }

    @Override
    public void sectionTitle2_()
    {
        currentSink.sectionTitle2_();
    }

    @Override
    public void sectionTitle3()
    {
        currentSink.sectionTitle3();
    }

    @Override
    public void sectionTitle3_()
    {
        currentSink.sectionTitle3_();
    }

    @Override
    public void sectionTitle4()
    {
        currentSink.sectionTitle4();
    }

    @Override
    public void sectionTitle4_()
    {
        currentSink.sectionTitle4_();
    }

    @Override
    public void sectionTitle5()
    {
        currentSink.sectionTitle5();
    }

    @Override
    public void sectionTitle5_()
    {
        currentSink.sectionTitle5_();
    }

    @Override
    public void sectionTitle6()
    {
        currentSink.sectionTitle5();
    }

    @Override
    public void sectionTitle6_()
    {
        currentSink.sectionTitle5_();
    }

    @Override
    public void sectionTitle_()
    {
        currentSink.sectionTitle_();
    }

    @Override
    public void sectionTitle_( int level )
    {
        currentSink.sectionTitle_( level );
    }

    @Override
    public void section_( int level )
    {
        currentSink.section_( level );
    }

    @Override
    public void sidebar()
    {
        currentSink.sidebar();
    }

    @Override
    public void sidebar( SinkEventAttributes attributes )
    {
        currentSink.sidebar( attributes );
    }

    @Override
    public void sidebar_()
    {
        currentSink.sidebar_();
    }

    @Override
    public void table()
    {
        currentSink.table();
    }

    @Override
    public void table( SinkEventAttributes attributes )
    {
        currentSink.table( attributes );
    }

    @Override
    public void tableCaption()
    {
        currentSink.tableCaption();
    }

    @Override
    public void tableCaption( SinkEventAttributes attributes )
    {
        currentSink.tableCaption( attributes );
    }

    @Override
    public void tableCaption_()
    {
        currentSink.tableCaption_();
    }

    @Override
    public void tableCell()
    {
        currentSink.tableCell();
    }

    @Override
    public void tableCell( String width )
    {
        currentSink.tableCell( width );
    }

    @Override
    public void tableCell( SinkEventAttributes attributes )
    {
        currentSink.tableCell( attributes );
    }

    @Override
    public void tableCell_()
    {
        currentSink.tableCell_();
    }

    @Override
    public void tableHeaderCell()
    {
        currentSink.tableHeaderCell();
    }

    @Override
    public void tableHeaderCell( String width )
    {
        currentSink.tableHeaderCell( width );
    }

    @Override
    public void tableHeaderCell( SinkEventAttributes attributes )
    {
        currentSink.tableHeaderCell( attributes );
    }

    @Override
    public void tableHeaderCell_()
    {
        currentSink.tableHeaderCell_();
    }

    @Override
    public void tableRow()
    {
        currentSink.tableRow();
    }

    @Override
    public void tableRow( SinkEventAttributes attributes )
    {
        currentSink.tableRow( attributes );
    }

    @Override
    public void tableRow_()
    {
        currentSink.tableRow_();
    }

    @Override
    public void tableRows( int[] justification, boolean grid )
    {
        currentSink.tableRows( justification, grid );
    }

    @Override
    public void tableRows_()
    {
        currentSink.tableRows_();
    }

    @Override
    public void table_()
    {
        currentSink.table_();
    }

    @Override
    public void text( String text )
    {
        currentSink.text( text );
    }

    @Override
    public void text( String text, SinkEventAttributes attributes )
    {
        currentSink.text( text, attributes );
    }

    @Override
    public void time( String datetime )
    {
        currentSink.time( datetime );
    }

    @Override
    public void time( String datetime, SinkEventAttributes attributes )
    {
        currentSink.time( datetime, attributes );
    }

    @Override
    public void time_()
    {
        currentSink.time_();
    }

    @Override
    public void title()
    {
        currentSink.title();
    }

    @Override
    public void title( SinkEventAttributes attributes )
    {
        currentSink.title( attributes );
    }

    @Override
    public void title_()
    {
        currentSink.title_();
    }

    @Override
    public void unknown( String name, Object[] requiredParams, SinkEventAttributes attributes )
    {
        currentSink.unknown( name, requiredParams, attributes );
    }

    @Override
    public void verbatim( boolean boxed )
    {
        currentSink.verbatim( boxed );
    }

    @Override
    public void verbatim( SinkEventAttributes attributes )
    {
        currentSink.verbatim( attributes );
    }

    @Override
    public void verbatim_()
    {
        currentSink.verbatim_();
    }

    @Override
    public void enableLogging( Log log )
    {
        currentSink.enableLogging( log );
    }
}
