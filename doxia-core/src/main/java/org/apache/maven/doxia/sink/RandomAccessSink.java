package org.apache.maven.doxia.sink;

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

    private List<Sink> sinks = new ArrayList<Sink>();

    private List<ByteArrayOutputStream> outputStreams = new ArrayList<ByteArrayOutputStream>();

    private Sink currentSink;

    public RandomAccessSink( SinkFactory sinkFactory, OutputStream stream )
        throws IOException
    {
        this.sinkFactory = sinkFactory;
        this.coreOutputStream = stream;
        this.coreSink = this.currentSink = sinkFactory.createSink( stream );
    }

    public RandomAccessSink( SinkFactory sinkFactory, OutputStream stream, String encoding )
        throws IOException
    {
        this.sinkFactory = sinkFactory;
        this.coreOutputStream = stream;
        this.encoding = encoding;
        this.coreSink = this.currentSink = sinkFactory.createSink( stream, encoding );
    }

    public RandomAccessSink( SinkFactory sinkFactory, File outputDirectory, String outputName )
        throws IOException
    {
        this.sinkFactory = sinkFactory;
        this.coreOutputStream = new FileOutputStream( new File( outputDirectory, outputName ) );
        this.coreSink = this.currentSink = sinkFactory.createSink( coreOutputStream );
    }

    public RandomAccessSink( SinkFactory sinkFactory, File outputDirectory, String outputName, String encoding )
        throws IOException
    {
        this.sinkFactory = sinkFactory;
        this.coreOutputStream = new FileOutputStream( new File( outputDirectory, outputName ) );
        this.encoding = encoding;
        this.coreSink = this.currentSink = sinkFactory.createSink( coreOutputStream, encoding );
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
    public void anchor( String name )
    {
        currentSink.anchor( name );
    }

    /** {@inheritDoc} */
    public void anchor( String name, SinkEventAttributes attributes )
    {
        currentSink.anchor( name, attributes );
    }

    /** {@inheritDoc} */
    public void anchor_()
    {
        currentSink.anchor_();
    }

    /** {@inheritDoc} */
    public void author()
    {
        currentSink.author();
    }

    /** {@inheritDoc} */
    public void author( SinkEventAttributes attributes )
    {
        currentSink.author( attributes );
    }

    /** {@inheritDoc} */
    public void author_()
    {
        currentSink.author_();
    }

    /** {@inheritDoc} */
    public void body()
    {
        currentSink.body();
    }

    /** {@inheritDoc} */
    public void body( SinkEventAttributes attributes )
    {
        currentSink.body( attributes );
    }

    /** {@inheritDoc} */
    public void body_()
    {
        currentSink.body_();
    }

    /** {@inheritDoc} */
    public void bold()
    {
        currentSink.bold();
    }

    /** {@inheritDoc} */
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
    public void comment( String comment )
    {
        currentSink.comment( comment );
    }

    /** {@inheritDoc} */
    public void date()
    {
        currentSink.date();
    }

    /** {@inheritDoc} */
    public void date( SinkEventAttributes attributes )
    {
        currentSink.date( attributes );
    }

    /** {@inheritDoc} */
    public void date_()
    {
        currentSink.date_();
    }

    /** {@inheritDoc} */
    public void definedTerm()
    {
        currentSink.definedTerm();
    }

    /** {@inheritDoc} */
    public void definedTerm( SinkEventAttributes attributes )
    {
        currentSink.definedTerm( attributes );
    }

    /** {@inheritDoc} */
    public void definedTerm_()
    {
        currentSink.definedTerm_();
    }

    /** {@inheritDoc} */
    public void definition()
    {
        currentSink.definition();
    }

    /** {@inheritDoc} */
    public void definition( SinkEventAttributes attributes )
    {
        currentSink.definition( attributes );
    }

    /** {@inheritDoc} */
    public void definitionList()
    {
        currentSink.definitionList();
    }

    /** {@inheritDoc} */
    public void definitionList( SinkEventAttributes attributes )
    {
        currentSink.definitionList( attributes );
    }

    /** {@inheritDoc} */
    public void definitionListItem()
    {
        currentSink.definitionListItem();
    }

    /** {@inheritDoc} */
    public void definitionListItem( SinkEventAttributes attributes )
    {
        currentSink.definitionListItem( attributes );
    }

    /** {@inheritDoc} */
    public void definitionListItem_()
    {
        currentSink.definitionListItem_();
    }

    /** {@inheritDoc} */
    public void definitionList_()
    {
        currentSink.definitionList_();
    }

    /** {@inheritDoc} */
    public void definition_()
    {
        currentSink.definition_();
    }

    /** {@inheritDoc} */
    public void figure()
    {
        currentSink.figure();
    }

    /** {@inheritDoc} */
    public void figure( SinkEventAttributes attributes )
    {
        currentSink.figure( attributes );
    }

    /** {@inheritDoc} */
    public void figureCaption()
    {
        currentSink.figureCaption();
    }

    /** {@inheritDoc} */
    public void figureCaption( SinkEventAttributes attributes )
    {
        currentSink.figureCaption( attributes );
    }

    /** {@inheritDoc} */
    public void figureCaption_()
    {
        currentSink.figureCaption_();
    }

    /** {@inheritDoc} */
    public void figureGraphics( String name )
    {
        currentSink.figureGraphics( name );
    }

    /** {@inheritDoc} */
    public void figureGraphics( String src, SinkEventAttributes attributes )
    {
        currentSink.figureGraphics( src, attributes );
    }

    /** {@inheritDoc} */
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
    public void head()
    {
        currentSink.head();
    }

    /** {@inheritDoc} */
    public void head( SinkEventAttributes attributes )
    {
        currentSink.head( attributes );
    }

    /** {@inheritDoc} */
    public void head_()
    {
        currentSink.head_();
    }

    /** {@inheritDoc} */
    public void horizontalRule()
    {
        currentSink.horizontalRule();
    }

    /** {@inheritDoc} */
    public void horizontalRule( SinkEventAttributes attributes )
    {
        currentSink.horizontalRule( attributes );
    }

    /** {@inheritDoc} */
    public void italic()
    {
        currentSink.italic();
    }

    /** {@inheritDoc} */
    public void italic_()
    {
        currentSink.italic_();
    }

    /** {@inheritDoc} */
    public void lineBreak()
    {
        currentSink.lineBreak();
    }

    /** {@inheritDoc} */
    public void lineBreak( SinkEventAttributes attributes )
    {
        currentSink.lineBreak( attributes );
    }

    /** {@inheritDoc} */
    public void link( String name )
    {
        currentSink.link( name );
    }

    /** {@inheritDoc} */
    public void link( String name, SinkEventAttributes attributes )
    {
        currentSink.link( name, attributes );
    }

    /** {@inheritDoc} */
    public void link_()
    {
        currentSink.link_();
    }

    /** {@inheritDoc} */
    public void list()
    {
        currentSink.list();
    }

    /** {@inheritDoc} */
    public void list( SinkEventAttributes attributes )
    {
        currentSink.list( attributes );
    }

    /** {@inheritDoc} */
    public void listItem()
    {
        currentSink.listItem();
    }

    /** {@inheritDoc} */
    public void listItem( SinkEventAttributes attributes )
    {
        currentSink.listItem( attributes );
    }

    /** {@inheritDoc} */
    public void listItem_()
    {
        currentSink.listItem_();
    }

    /** {@inheritDoc} */
    public void list_()
    {
        currentSink.list_();
    }

    /** {@inheritDoc} */
    public void monospaced()
    {
        currentSink.monospaced();
    }

    /** {@inheritDoc} */
    public void monospaced_()
    {
        currentSink.monospaced_();
    }

    /** {@inheritDoc} */
    public void nonBreakingSpace()
    {
        currentSink.nonBreakingSpace();
    }

    /** {@inheritDoc} */
    public void numberedList( int numbering )
    {
        currentSink.numberedList( numbering );
    }

    /** {@inheritDoc} */
    public void numberedList( int numbering, SinkEventAttributes attributes )
    {
        currentSink.numberedList( numbering, attributes );
    }

    /** {@inheritDoc} */
    public void numberedListItem()
    {
        currentSink.numberedListItem();
    }

    /** {@inheritDoc} */
    public void numberedListItem( SinkEventAttributes attributes )
    {
        currentSink.numberedListItem( attributes );
    }

    /** {@inheritDoc} */
    public void numberedListItem_()
    {
        currentSink.numberedListItem_();
    }

    /** {@inheritDoc} */
    public void numberedList_()
    {
        currentSink.numberedList_();
    }

    /** {@inheritDoc} */
    public void pageBreak()
    {
        currentSink.pageBreak();
    }

    /** {@inheritDoc} */
    public void paragraph()
    {
        currentSink.paragraph();
    }

    /** {@inheritDoc} */
    public void paragraph( SinkEventAttributes attributes )
    {
        currentSink.paragraph( attributes );
    }

    /** {@inheritDoc} */
    public void paragraph_()
    {
        currentSink.paragraph_();
    }

    /** {@inheritDoc} */
    public void rawText( String text )
    {
        currentSink.rawText( text );
    }

    /** {@inheritDoc} */
    public void section( int level, SinkEventAttributes attributes )
    {
        currentSink.section( level, attributes );
    }

    /** {@inheritDoc} */
    public void section1()
    {
        currentSink.section1();
    }

    /** {@inheritDoc} */
    public void section1_()
    {
        currentSink.section1_();
    }

    /** {@inheritDoc} */
    public void section2()
    {
        currentSink.section2();
    }

    /** {@inheritDoc} */
    public void section2_()
    {
        currentSink.section2_();
    }

    /** {@inheritDoc} */
    public void section3()
    {
        currentSink.section3();
    }

    /** {@inheritDoc} */
    public void section3_()
    {
        currentSink.section3_();
    }

    /** {@inheritDoc} */
    public void section4()
    {
        currentSink.section4();
    }

    /** {@inheritDoc} */
    public void section4_()
    {
        currentSink.section4_();
    }

    /** {@inheritDoc} */
    public void section5()
    {
        currentSink.section5();
    }

    /** {@inheritDoc} */
    public void section5_()
    {
        currentSink.section5_();
    }

    /** {@inheritDoc} */
    public void sectionTitle()
    {
        currentSink.sectionTitle();
    }

    /** {@inheritDoc} */
    public void sectionTitle( int level, SinkEventAttributes attributes )
    {
        currentSink.sectionTitle( level, attributes );
    }

    /** {@inheritDoc} */
    public void sectionTitle1()
    {
        currentSink.sectionTitle1();
    }

    /** {@inheritDoc} */
    public void sectionTitle1_()
    {
        currentSink.sectionTitle1_();
    }

    /** {@inheritDoc} */
    public void sectionTitle2()
    {
        currentSink.sectionTitle2();
    }

    /** {@inheritDoc} */
    public void sectionTitle2_()
    {
        currentSink.sectionTitle2_();
    }

    /** {@inheritDoc} */
    public void sectionTitle3()
    {
        currentSink.sectionTitle3();
    }

    /** {@inheritDoc} */
    public void sectionTitle3_()
    {
        currentSink.sectionTitle3_();
    }

    /** {@inheritDoc} */
    public void sectionTitle4()
    {
        currentSink.sectionTitle4();
    }

    /** {@inheritDoc} */
    public void sectionTitle4_()
    {
        currentSink.sectionTitle4_();
    }

    /** {@inheritDoc} */
    public void sectionTitle5()
    {
        currentSink.sectionTitle5();
    }

    /** {@inheritDoc} */
    public void sectionTitle5_()
    {
        currentSink.sectionTitle5_();
    }

    /** {@inheritDoc} */
    public void sectionTitle_()
    {
        currentSink.sectionTitle_();
    }

    /** {@inheritDoc} */
    public void sectionTitle_( int level )
    {
        currentSink.sectionTitle_( level );
    }

    /** {@inheritDoc} */
    public void section_( int level )
    {
        currentSink.section_( level );
    }

    /** {@inheritDoc} */
    public void table()
    {
        currentSink.table();
    }

    /** {@inheritDoc} */
    public void table( SinkEventAttributes attributes )
    {
        currentSink.table( attributes );
    }

    /** {@inheritDoc} */
    public void tableCaption()
    {
        currentSink.tableCaption();
    }

    /** {@inheritDoc} */
    public void tableCaption( SinkEventAttributes attributes )
    {
        currentSink.tableCaption( attributes );
    }

    /** {@inheritDoc} */
    public void tableCaption_()
    {
        currentSink.tableCaption_();
    }

    /** {@inheritDoc} */
    public void tableCell()
    {
        currentSink.tableCell();
    }

    /** {@inheritDoc} */
    public void tableCell( String width )
    {
        currentSink.tableCell( width );
    }

    /** {@inheritDoc} */
    public void tableCell( SinkEventAttributes attributes )
    {
        currentSink.tableCell( attributes );
    }

    /** {@inheritDoc} */
    public void tableCell_()
    {
        currentSink.tableCell_();
    }

    /** {@inheritDoc} */
    public void tableHeaderCell()
    {
        currentSink.tableHeaderCell();
    }

    /** {@inheritDoc} */
    public void tableHeaderCell( String width )
    {
        currentSink.tableHeaderCell( width );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell( SinkEventAttributes attributes )
    {
        currentSink.tableHeaderCell( attributes );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell_()
    {
        currentSink.tableHeaderCell_();
    }

    /** {@inheritDoc} */
    public void tableRow()
    {
        currentSink.tableRow();
    }

    /** {@inheritDoc} */
    public void tableRow( SinkEventAttributes attributes )
    {
        currentSink.tableRow( attributes );
    }

    /** {@inheritDoc} */
    public void tableRow_()
    {
        currentSink.tableRow_();
    }

    /** {@inheritDoc} */
    public void tableRows( int[] justification, boolean grid )
    {
        currentSink.tableRows( justification, grid );
    }

    /** {@inheritDoc} */
    public void tableRows_()
    {
        currentSink.tableRows_();
    }

    /** {@inheritDoc} */
    public void table_()
    {
        currentSink.table_();
    }

    /** {@inheritDoc} */
    public void text( String text )
    {
        currentSink.text( text );
    }

    /** {@inheritDoc} */
    public void text( String text, SinkEventAttributes attributes )
    {
        currentSink.text( text, attributes );
    }

    /** {@inheritDoc} */
    public void title()
    {
        currentSink.title();
    }

    /** {@inheritDoc} */
    public void title( SinkEventAttributes attributes )
    {
        currentSink.title( attributes );
    }

    /** {@inheritDoc} */
    public void title_()
    {
        currentSink.title_();
    }

    /** {@inheritDoc} */
    public void unknown( String name, Object[] requiredParams, SinkEventAttributes attributes )
    {
        currentSink.unknown( name, requiredParams, attributes );
    }

    /** {@inheritDoc} */
    public void verbatim( boolean boxed )
    {
        currentSink.verbatim( boxed );
    }

    /** {@inheritDoc} */
    public void verbatim( SinkEventAttributes attributes )
    {
        currentSink.verbatim( attributes );
    }

    /** {@inheritDoc} */
    public void verbatim_()
    {
        currentSink.verbatim_();
    }

    /** {@inheritDoc} */
    public void enableLogging( Log log )
    {
        currentSink.enableLogging( log );
    }
}