package org.apache.maven.doxia.module.fo;

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
import java.io.Writer;
import java.util.Stack;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.sink.AbstractXmlSink;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.util.HtmlTools;

/**
 * A Doxia Sink that produces a FO model.
 *
 * @author ltheussl
 * @version $Id$
 * @since 1.0
 */
public class FoSink
    extends AbstractXmlSink
    implements FoMarkup
{
    /** For writing the result. */
    private final Writer out;

    /** Used to get the current position in numbered lists. */
    private final Stack listStack = new Stack();

    /** Used to get attributes for a given FO element. */
    private final FoConfiguration config;

    /** Counts the current section level. */
    private int section = 0;

    /** Counts the current subsection level. */
    private int subsection = 0;

    /** Counts the current subsubsection level. */
    private int subsubsection = 0;

    /** Drawing borders on table cells. */
    private boolean tableGrid;

    /** Alignment of table cells. */
    private int[] cellJustif;

    /** Current table cell. */
    private int cellCount;

    /** Verbatim flag. */
    private boolean verbatim;

    /**
     * Constructor.
     *
     * @param writer The writer for writing the result.
     */
    public FoSink( Writer writer )
    {
        this.out = writer;
        this.config = new FoConfiguration();

        setNameSpace( "fo" );
    }

    /**
     * Returns the configuration object of this sink.
     *
     * @return The configuration object of this sink.
     */
    public FoConfiguration getFoConfiguration()
    {
        return config;
    }

    // TODO add FOP compliance mode?

    /** {@inheritDoc} */
    public void head()
    {
        beginDocument();
        startPageSequence( "0", null, null );
    }

    /** {@inheritDoc} */
    public void head_()
    {
        newline();
    }

    /** {@inheritDoc} */
    public void title()
    {
        writeStartTag( BLOCK_TAG, "doc.header.title" );
    }

    /** {@inheritDoc} */
    public void title_()
    {
        writeEndTag( BLOCK_TAG );
    }

    /** {@inheritDoc} */
    public void author()
    {
        writeStartTag( BLOCK_TAG, "doc.header.author" );
    }

    /** {@inheritDoc} */
    public void author_()
    {
        writeEndTag( BLOCK_TAG );
    }

    /** {@inheritDoc} */
    public void date()
    {
        writeStartTag( BLOCK_TAG, "doc.header.date" );
    }

    /** {@inheritDoc} */
    public void date_()
    {
        writeEndTag( BLOCK_TAG );
    }

    /** {@inheritDoc} */
    public void body()
    {
        // noop
    }

    /** {@inheritDoc} */
    public void body_()
    {
        newline();
        writeEndTag( FLOW_TAG );
        writeEndTag( PAGE_SEQUENCE_TAG );
        endDocument();
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void sectionTitle()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void sectionTitle_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void section1()
    {
        section++;
        subsection = 0;
        subsubsection = 0;
        onSection();
    }

    /** {@inheritDoc} */
    public void sectionTitle1()
    {
        onSectionTitle( Sink.SECTION_LEVEL_1 );
    }

    /** {@inheritDoc} */
    public void sectionTitle1_()
    {
        onSectionTitle_();
    }

    /** {@inheritDoc} */
    public void section1_()
    {
        onSection_();
    }

    /** {@inheritDoc} */
    public void section2()
    {
        subsection++;
        subsubsection = 0;
        onSection();
    }

    /** {@inheritDoc} */
    public void sectionTitle2()
    {
        onSectionTitle( Sink.SECTION_LEVEL_2 );
    }

    /** {@inheritDoc} */
    public void sectionTitle2_()
    {
        onSectionTitle_();
    }

    /** {@inheritDoc} */
    public void section2_()
    {
        onSection_();
    }

    /** {@inheritDoc} */
    public void section3()
    {
        subsubsection++;
        onSection();
    }

    /** {@inheritDoc} */
    public void sectionTitle3()
    {
        onSectionTitle( Sink.SECTION_LEVEL_3 );
    }

    /** {@inheritDoc} */
    public void sectionTitle3_()
    {
        onSectionTitle_();
    }

    /** {@inheritDoc} */
    public void section3_()
    {
        onSection_();
    }

    /** {@inheritDoc} */
    public void section4()
    {
        onSection();
    }

    /** {@inheritDoc} */
    public void sectionTitle4()
    {
        onSectionTitle( Sink.SECTION_LEVEL_4 );
    }

    /** {@inheritDoc} */
    public void sectionTitle4_()
    {
        onSectionTitle_();
    }

    /** {@inheritDoc} */
    public void section4_()
    {
        onSection_();
    }

    /** {@inheritDoc} */
    public void section5()
    {
        onSection();
    }

    /** {@inheritDoc} */
    public void sectionTitle5()
    {
        onSectionTitle( Sink.SECTION_LEVEL_5 );
    }

    /** {@inheritDoc} */
    public void sectionTitle5_()
    {
        onSectionTitle_();
    }

    /** {@inheritDoc} */
    public void section5_()
    {
        onSection_();
    }

    /** Starts a section/subsection. */
    private void onSection()
    {
        newline();
        writeStartTag( BLOCK_TAG, "body.text" );
    }

    /**
     * Starts a section title.
     *
     * @param depth The section level.
     */
    private void onSectionTitle( int depth )
    {
        StringBuffer title = new StringBuffer( 16 );

        title.append( getChapterString() );

        newline();
        if ( depth == Sink.SECTION_LEVEL_1 )
        {
            writeStartTag( BLOCK_TAG, "body.h1" );
            title.append( section ).append( "   " );
        }
        else if ( depth == Sink.SECTION_LEVEL_2 )
        {
            writeStartTag( BLOCK_TAG, "body.h2" );
            title.append( section ).append( "." );
            title.append( subsection ).append( "   " );
        }
        else if ( depth == Sink.SECTION_LEVEL_3 )
        {
            writeStartTag( BLOCK_TAG, "body.h3" );
            title.append( section ).append( "." );
            title.append( subsection ).append( "." );
            title.append( subsubsection ).append( "   " );
        }
        else if ( depth == Sink.SECTION_LEVEL_4 )
        {
            writeStartTag( BLOCK_TAG, "body.h4" );
        }
        else
        {
            writeStartTag( BLOCK_TAG, "body.h5" );
        }

        write( title.toString() );
    }

    /** Ends a section title. */
    private void onSectionTitle_()
    {
        writeEndTag( BLOCK_TAG );
    }

    /** Ends a section/subsection. */
    private void onSection_()
    {
        writeEndTag( BLOCK_TAG );
    }

    /**
     * Resets the section counter to 0.
     * Only useful for overriding classes, like AggregateSink, the FoSink puts everything into one chapter.
     */
    protected void resetSectionCounter()
    {
        this.section = 0;
    }

    /**
     * Returns the current chapter number as a string.
     * By default does nothing, gets overridden by AggregateSink.
     *
     * @return an empty String.
     */
    protected String getChapterString()
    {
        return "";
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void list()
    {
        newline();
        writeStartTag( LIST_BLOCK_TAG, "list" );
    }

    /** {@inheritDoc} */
    public void list_()
    {
        writeEndTag( LIST_BLOCK_TAG );
    }

    /** {@inheritDoc} */
    public void listItem()
    {
        writeStartTag( LIST_ITEM_TAG, "list.item" );
        // TODO customize?
        writeln( "<fo:list-item-label><fo:block>&#8226;</fo:block></fo:list-item-label>" );
        writeStartTag( LIST_ITEM_BODY_TAG, "list.item" );
        writeStartTag( BLOCK_TAG, "" );
    }

    /** {@inheritDoc} */
    public void listItem_()
    {
        writeEndTag( BLOCK_TAG );
        writeEndTag( LIST_ITEM_BODY_TAG );
        writeEndTag( LIST_ITEM_TAG );
    }

    /** {@inheritDoc} */
    public void numberedList( int numbering )
    {
        listStack.push( new NumberedListItem( numbering ) );
        newline();
        writeStartTag( LIST_BLOCK_TAG, "list" );
    }

    /** {@inheritDoc} */
    public void numberedList_()
    {
        listStack.pop();
        writeEndTag( LIST_BLOCK_TAG );
    }

    /** {@inheritDoc} */
    public void numberedListItem()
    {
        NumberedListItem current = (NumberedListItem) listStack.peek();
        current.next();

        writeStartTag( LIST_ITEM_TAG, "list.item" );

        writeStartTag( LIST_ITEM_LABEL_TAG, "" );
        writeStartTag( BLOCK_TAG, "" );
        write( current.getListItemSymbol() );
        writeEndTag( BLOCK_TAG );
        writeEndTag( LIST_ITEM_LABEL_TAG );

        writeStartTag( LIST_ITEM_BODY_TAG, "list.item" );
        writeStartTag( BLOCK_TAG, "" );
    }

    /** {@inheritDoc} */
    public void numberedListItem_()
    {
        writeEndTag( BLOCK_TAG );
        writeEndTag( LIST_ITEM_BODY_TAG );
        writeEndTag( LIST_ITEM_TAG );
    }

    /** {@inheritDoc} */
    public void definitionList()
    {
        newline();
        writeStartTag( BLOCK_TAG, "dl" );
    }

    /** {@inheritDoc} */
    public void definitionList_()
    {
        writeEndTag( BLOCK_TAG );
    }

    /** {@inheritDoc} */
    public void definitionListItem()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void definitionListItem_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void definedTerm()
    {
        writeStartTag( BLOCK_TAG, "dt" );
    }

    /** {@inheritDoc} */
    public void definedTerm_()
    {
        writeEndTag( BLOCK_TAG );
    }

    /** {@inheritDoc} */
    public void definition()
    {
        newline();
        writeStartTag( BLOCK_TAG, "dd" );
    }

    /** {@inheritDoc} */
    public void definition_()
    {
        writeEndTag( BLOCK_TAG );
    }

    /** {@inheritDoc} */
    public void figure()
    {
        newline();
        writeStartTag( BLOCK_TAG, "figure.display" );
        write( "<fo:external-graphic"
            + config.getAttributeString( "figure.graphics" ) );
    }

    /** {@inheritDoc} */
    public void figure_()
    {
        writeEndTag( BLOCK_TAG );
    }

    /** {@inheritDoc} */
    public void figureGraphics( String s )
    {
        // TODO: figure out file extension.
        writeln( " src=\"" + s + ".png\"/>" );
    }

    /** {@inheritDoc} */
    public void figureCaption()
    {
        writeStartTag( BLOCK_TAG, "figure.caption" );
    }

    /** {@inheritDoc} */
    public void figureCaption_()
    {
        writeEndTag( BLOCK_TAG );
    }

    /** {@inheritDoc} */
    public void paragraph()
    {
        writeStartTag( BLOCK_TAG, "normal.paragraph" );
    }

    /** {@inheritDoc} */
    public void paragraph_()
    {
        writeEndTag( BLOCK_TAG );
    }

    /** {@inheritDoc} */
    public void verbatim( boolean boxed )
    {
        this.verbatim = true;
        if ( boxed )
        {
            writeStartTag( BLOCK_TAG, "body.source" );
        }
        else
        {
            writeStartTag( BLOCK_TAG, "body.pre" );
        }
    }

    /** {@inheritDoc} */
    public void verbatim_()
    {
        this.verbatim = false;
        writeEndTag( BLOCK_TAG );
    }

    /** {@inheritDoc} */
    public void horizontalRule()
    {
        newline();
        writeStartTag( BLOCK_TAG, "" );
        writeEmptyTag( LEADER_TAG, "body.rule" );
        writeEndTag( BLOCK_TAG );
    }

    /** {@inheritDoc} */
    public void pageBreak()
    {
        //writeln( "<fo:block break-before=\"page\"/>" );
        writeEmptyTag( BLOCK_TAG, "break-before", "page" );
        newline();
    }

    /** {@inheritDoc} */
    public void table()
    {
        newline();
        writeStartTag( BLOCK_TAG, "table.padding" );

        // <fo:table-and-caption> is XSL-FO 1.0 standard but not implemented in FOP 0.93
        //writeStartTag( TABLE_AND_CAPTION_TAG, "" );

        writeStartTag( TABLE_TAG, "table.layout" );
    }

    /** {@inheritDoc} */
    public void table_()
    {
        writeEndTag( TABLE_TAG );

        // <fo:table-and-caption> is XSL-FO 1.0 standard but not implemented in FOP 0.93
        //writeEndTag( TABLE_AND_CAPTION_TAG );

        writeEndTag( BLOCK_TAG );
    }

    /** {@inheritDoc} */
    public void tableRows( int[] justification, boolean grid )
    {
        this.tableGrid = grid;
        this.cellJustif = justification;

        // FOP hack to center the table, see
        // http://xmlgraphics.apache.org/fop/fo.html#fo-center-table-horizon
        writeln( "<fo:table-column column-width=\"proportional-column-width(1)\"/>" );

        // TODO: calculate width[i]
        for ( int i = 0;  i < cellJustif.length; i++ )
        {
            writeln( "<fo:table-column column-width=\"1in\"/>" );
        }

        writeln( "<fo:table-column column-width=\"proportional-column-width(1)\"/>" );
        writeStartTag( TABLE_BODY_TAG, "" );
    }

    /** {@inheritDoc} */
    public void tableRows_()
    {
        this.cellJustif = null;
        writeEndTag( TABLE_BODY_TAG );
    }

    /** {@inheritDoc} */
    public void tableRow()
    {
        // TODO spacer rows
        writeStartTag( TABLE_ROW_TAG, "table.body.row" );
        this.cellCount = 0;
    }

    /** {@inheritDoc} */
    public void tableRow_()
    {
        writeEndTag( TABLE_ROW_TAG );
    }

    /** {@inheritDoc} */
    public void tableCell()
    {
        tableCell( false );
    }

    /** {@inheritDoc} */
    public void tableCell( String width )
    {
        // TODO: fop can't handle cell width
        tableCell( false );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell()
    {
        // TODO: how to implement?
        tableCell( true );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell( String width )
    {
        // TODO: fop can't handle cell width
        tableCell( true );
    }

    /**
     * Writes a table cell.
     *
     * @param headerRow Currently not used.
     */
    private void tableCell( boolean headerRow )
    {
         String justif = null;
 
         if ( cellJustif != null )
         {
             switch ( cellJustif[cellCount] )
             {
                 case Parser.JUSTIFY_LEFT:
                     justif = "left";
                     break;
                 case Parser.JUSTIFY_RIGHT:
                     justif = "right";
                     break;
                 case Parser.JUSTIFY_CENTER:
                 default:
                     justif = "center";
                     break;
             }
         }
 
         if ( justif != null )
         {
            // the column-number is needed for the hack to center the table, see tableRows.
            write( "<fo:table-cell column-number=\"" + String.valueOf( cellCount + 2 ) + "\"" );
            if ( tableGrid )
            {
                write( " border-style=\"solid\" border-width=\"0.2mm\"" );
            }
            writeln( config.getAttributeString( "table.body.cell" ) + ">" );
         }
         else
         {
             writeStartTag( TABLE_CELL_TAG, "table.body.cell" );
         }
        writeln( "<fo:block text-align=\"" + justif + "\">" );
    }

    /** {@inheritDoc} */
    public void tableCell_()
    {
        writeEndTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        ++cellCount;
    }

    /** {@inheritDoc} */
    public void tableHeaderCell_()
    {
        tableCell_();
    }

    /** {@inheritDoc} */
    public void tableCaption()
    {
        // <fo:table-caption> is XSL-FO 1.0 standard but not implemented in FOP 0.93
        //writeStartTag( TABLE_CAPTION_TAG, "" );

        // TODO: how to implement this otherwise?
        // table-footer doesn't work because it has to be declared before table-body.
    }

    /** {@inheritDoc} */
    public void tableCaption_()
    {
        // <fo:table-caption> is XSL-FO 1.0 standard but not implemented in FOP 0.93
        //writeEndTag( TABLE_CAPTION_TAG );
    }

    /** {@inheritDoc} */
    public void anchor( String name )
    {
        String anchor = name;

        if ( anchor.startsWith( "#" ) )
        {
            anchor = "#" + HtmlTools.encodeId( anchor.substring( 1 ) );
        }
        else
        {
            anchor = "#" + HtmlTools.encodeId( anchor );
        }

        writeStartTag( INLINE_TAG, "id", anchor );
    }

    /** {@inheritDoc} */
    public void anchor_()
    {
        writeEndTag( INLINE_TAG );
    }

    /** {@inheritDoc} */
    public void link( String name )
    {
        if ( name.startsWith( "http" ) || name.startsWith( "mailto" )
            || name.startsWith( "ftp" ) )
        {
            writeStartTag( BASIC_LINK_TAG, "external-destination", HtmlTools.escapeHTML( name ) );
            writeStartTag( INLINE_TAG, "href.external" );
        }
        else
        {
            // treat everything else as internal, local (ie anchor is in the same source document)

            String anchor = name;

            if ( anchor.startsWith( "#" ) )
            {
                anchor = "#" + HtmlTools.encodeId( anchor.substring( 1 ) );
            }
            else
            {
                anchor = "#" + HtmlTools.encodeId( anchor );
            }

            writeStartTag( BASIC_LINK_TAG, "internal-destination", HtmlTools.escapeHTML( anchor ) );
            writeStartTag( INLINE_TAG, "href.internal" );
        }
    }

    /** {@inheritDoc} */
    public void link_()
    {
        writeEndTag( INLINE_TAG );
        writeEndTag( BASIC_LINK_TAG );
    }

    /** {@inheritDoc} */
    public void italic()
    {
        writeStartTag( INLINE_TAG, "italic" );
    }

    /** {@inheritDoc} */
    public void italic_()
    {
        writeEndTag( INLINE_TAG );
    }

    /** {@inheritDoc} */
    public void bold()
    {
        writeStartTag( INLINE_TAG, "bold" );
    }

    /** {@inheritDoc} */
    public void bold_()
    {
        writeEndTag( INLINE_TAG );
    }

    /** {@inheritDoc} */
    public void monospaced()
    {
        writeStartTag( INLINE_TAG, "monospace" );
    }

    /** {@inheritDoc} */
    public void monospaced_()
    {
        writeEndTag( INLINE_TAG );
    }

    /** {@inheritDoc} */
    public void lineBreak()
    {
        newline();
        writeEmptyTag( BLOCK_TAG, "" );
    }

    /** {@inheritDoc} */
    public void nonBreakingSpace()
    {
        write( "&#160;" );
    }

    /** {@inheritDoc} */
    public void text( String text )
    {
        content( text );
    }

    /** {@inheritDoc} */
    public void rawText( String text )
    {
        write( text );
    }

    /** {@inheritDoc} */
    public void flush()
    {
        try
        {
            out.flush();
        }
        catch ( IOException e )
        {
            // TODO: log
        }
    }

    /** {@inheritDoc} */
    public void close()
    {
        try
        {
            out.close();
        }
        catch ( IOException e )
        {
            // TODO: log
        }
    }

    /**
     * Writes the beginning of a FO document.
     */
    public void beginDocument()
    {
        writeln( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );

        writeStartTag( ROOT_TAG, "xmlns:" + getNameSpace(), "http://www.w3.org/1999/XSL/Format" );

        writeStartTag( LAYOUT_MASTER_SET_TAG, "" );

        writeStartTag( SIMPLE_PAGE_MASTER_TAG, "layout.master.set.cover-page" );
        writeEmptyTag( REGION_BODY_TAG, "layout.master.set.cover-page.region-body" );
        writeEndTag( SIMPLE_PAGE_MASTER_TAG );

        writeStartTag( SIMPLE_PAGE_MASTER_TAG, "layout.master.set.toc" );
        writeEmptyTag( REGION_BODY_TAG, "layout.master.set.toc.region-body" );
        writeEmptyTag( REGION_BEFORE_TAG, "layout.master.set.toc.region-before" );
        writeEmptyTag( REGION_AFTER_TAG, "layout.master.set.toc.region-after" );
        writeEndTag( SIMPLE_PAGE_MASTER_TAG );

        writeStartTag( SIMPLE_PAGE_MASTER_TAG, "layout.master.set.body" );
        writeEmptyTag( REGION_BODY_TAG, "layout.master.set.body.region-body" );
        writeEmptyTag( REGION_BEFORE_TAG, "layout.master.set.body.region-before" );
        writeEmptyTag( REGION_AFTER_TAG, "layout.master.set.body.region-after" );
        writeEndTag( SIMPLE_PAGE_MASTER_TAG );

        writeEndTag( LAYOUT_MASTER_SET_TAG );
    }

    /**
     * Writes the end of a FO document, flushes and closes the stream.
     */
    public void endDocument()
    {
        writeEndTag( ROOT_TAG );
        flush();
        close();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Writes a start tag, prepending EOL.
     *
     * @param tag The tag.
     * @param attributeId An id identifying the attribute set.
     */
    protected void writeStartTag( Tag tag, String attributeId )
    {
        newline();
        writeStartTag( tag, config.getAttributeSet( attributeId ) );
    }

    /**
     * Writes a start tag, prepending EOL.
     *
     * @param tag The tag.
     * @param id An id to add.
     * @param name The name (value) of the id.
     */
    protected void writeStartTag( Tag tag, String id, String name )
    {
        newline();
        MutableAttributeSet att = new SimpleAttributeSet();
        att.addAttribute( id, name );

        writeStartTag( tag, att );
    }

    /**
     * Writes a start tag, prepending EOL.
     *
     * @param tag The tag.
     * @param id An id to add.
     * @param name The name (value) of the id.
     * @param attributeId An id identifying the attribute set.
     */
    protected void writeStartTag( Tag tag, String id, String name, String attributeId )
    {
        MutableAttributeSet att = config.getAttributeSet( attributeId );

        // make sure we don't add it twice
        if ( att.isDefined( id ) )
        {
            att.removeAttribute( id );
        }

        att.addAttribute( id, name );

        newline();
        writeStartTag( tag, att );
    }

    /**
     * Writes an empty tag, prepending EOL.
     *
     * @param tag The tag.
     * @param id An id to add.
     * @param name The name (value) of the id.
     */
    protected void writeEmptyTag( Tag tag, String id, String name )
    {
        MutableAttributeSet att = new SimpleAttributeSet();
        att.addAttribute( id, name );

        newline();
        writeSimpleTag( tag, att );
    }

    /**
     * Writes a simple tag, appending EOL.
     *
     * @param tag The tag name.
     * @param attributeId An id identifying the attribute set.
     */
    protected void writeEmptyTag( Tag tag, String attributeId )
    {
        newline();
        writeSimpleTag( tag, config.getAttributeSet( attributeId ) );
    }

    /**
     * Writes a text, swallowing any exceptions.
     *
     * @param text The text to write.
     */
    protected void write( String text )
    {
        try
        {
            out.write( text );
        }
        catch ( IOException e )
        {
            // TODO: log
        }
    }

    /**
     * Writes a text, appending EOL.
     *
     * @param text The text to write.
     */
    protected void writeln( String text )
    {
        write( text );
        newline();
    }

    /**
     * Writes content, escaping special characters.
     *
     * @param text The text to write.
     */
    protected void content( String text )
    {
        write( escaped( text, verbatim ) );
    }

    /** Writes EOL. */
    protected void newline()
    {
        write( EOL );
    }

    /**
     * Escapes special characters so that the text can be included in a fo file.
     *
     * @param text The text to process.
     * @param verb In verbatim mode, white space and newlines are escaped.
     * @return The text with special characters escaped.
     */
    public static String escaped( String text, boolean verb )
    {
        int length = text.length();
        StringBuffer buffer = new StringBuffer( length );

        for ( int i = 0; i < length; ++i )
        {
            char c = text.charAt( i );
            switch ( c )
            {
                case ' ':
                    if ( verb )
                    {
                        buffer.append( "&#160;" );
                    }
                    else
                    {
                        buffer.append( c );
                    }
                    break;
                case '<':
                    buffer.append( "&lt;" );
                    break;
                case '>':
                    buffer.append( "&gt;" );
                    break;
                case '&':
                    buffer.append( "&amp;" );
                    break;
                case '\u00a9': // copyright
                    buffer.append( "&#169;" );
                    break;
                case '\n':
                    buffer.append( EOL );
                    if ( verb )
                    {
                        buffer.append( "<fo:block/>" + EOL );
                    }
                    break;
                default:
                    buffer.append( c );
            }
        }

        return buffer.toString();
    }

    /**
     * Starts a page sequence.
     *
     * @param initPageNumber The initial page number. Should be either "0" (for the first page) or "auto".
     * @param headerText The text to write in the header, if null, nothing is written.
     * @param footerText The text to write in the footer, if null, nothing is written.
     */
    protected void startPageSequence( String initPageNumber, String headerText, String footerText )
    {
        writeln( "<fo:page-sequence initial-page-number=\"" + initPageNumber + "\" master-reference=\"body\">" );
        regionBefore( headerText );
        regionAfter( footerText );
        writeln( "<fo:flow flow-name=\"xsl-region-body\">" );
        chapterHeading( null, true );
    }

    /**
     * Writes a 'xsl-region-before' block.
     *
     * @param headerText The text to write in the header, if null, nothing is written.
     */
    protected void regionBefore( String headerText )
    {
        // do nothing, overridden by AggregateSink
    }

    /**
     * Writes a 'xsl-region-after' block. By default does nothing, gets overridden by AggregateSink.
     *
     * @param footerText The text to write in the footer, if null, nothing is written.
     */
    protected void regionAfter( String footerText )
    {
        // do nothing, overridden by AggregateSink
    }

    /**
     * Writes a chapter heading. By default does nothing, gets overridden by AggregateSink.
     *
     * @param headerText The text to write in the header, if null, the current document title is written.
     * @param chapterNumber True if the chapter number should be written in front of the text.
     */
    protected void chapterHeading( String headerText, boolean chapterNumber )
    {
        // do nothing, overridden by AggregateSink
    }

}
