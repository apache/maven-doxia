package org.apache.maven.doxia.module.itext;

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

import com.lowagie.text.BadElementException;
import com.lowagie.text.ElementTags;
import com.lowagie.text.Image;
import org.apache.maven.doxia.util.HtmlTools;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkAdapter;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;

import java.awt.*;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>A doxia Sink which produces an XML Front End document for <code>iText</code> framework.</p>
 * Known limitations:
 * <ul>
 * <li>Roman lists are not supported.</li>
 * <li>Horizontal rule is not supported with 1.3.
 * See <a href="http://www.mail-archive.com/itext-questions@lists.sourceforge.net/msg10323.html">http://www.mail-archive.com/itext-questions@lists.sourceforge.net/msg10323.html</a></li>
 * <li>iText has some problems with <code>ElementTags.TABLE</code> and <code>ElementTags.TABLEFITSPAGE</code>.
 * See http://sourceforge.net/tracker/index.php?func=detail&aid=786427&group_id=15255&atid=115255.</li>
 * <li>Images could be on another page and next text on the last one.</li>
 * </ul>
 *
 * @see <a href="http://www.lowagie.com/iText/tutorial/ch07.html">http://www.lowagie.com/iText/tutorial/ch07.html</a>
 *
 * @plexus.component role="org.apache.maven.doxia.sink.Sink" role-hint="pdf"
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public final class ITextSink
    extends SinkAdapter
{
    /** This is the place where the iText DTD is located. IMPORTANT: this DTD is not uptodate! */
    public final static String DTD = "http://itext.sourceforge.net/itext.dtd";

    /** This is the reference to the DTD. */
    public static final String DOCTYPE = "ITEXT SYSTEM \"" + DTD + "\"";

    /** This is the default leading for chapter title */
    public static final String DEFAULT_CHAPTER_TITLE_LEADING = "36.0";

    /** This is the default leading for section title */
    public static final String DEFAULT_SECTION_TITLE_LEADING = "24.0";

    /** The ClassLoader used */
    private ClassLoader currentClassLoader;

    /** The action context */
    private SinkActionContext actionContext;

    /** The Writer used */
    private Writer writer;

    /** The XML Writer used */
    private XMLWriter xmlWriter;

    private boolean writeStart;

    /** The Header object */
    private ITextHeader header;

    /** The font object */
    private ITextFont font;

    private int numberDepth = 0;

    private int depth = 0;

    public ITextSink( Writer writer )
    {
        this.writer = writer;

        actionContext = new SinkActionContext();
        font = new ITextFont();
        header = new ITextHeader();

        xmlWriter = new PrettyPrintXMLWriter( this.writer, "UTF-8", null );//, DOCTYPE );
        writeStart = true;
    }

    public ITextSink( PrettyPrintXMLWriter xmlWriter )
    {
        this.xmlWriter = xmlWriter;

        actionContext = new SinkActionContext();
        font = new ITextFont();
        header = new ITextHeader();

        writeStart = false;
    }

    /**
     * Get the current classLoader
     *
     * @return the current class loader
     */
    public ClassLoader getClassLoader()
    {
        return currentClassLoader;
    }

    /**
     * Set a new class loader
     *
     * @param cl
     */
    public void setClassLoader( ClassLoader cl )
    {
        currentClassLoader = cl;
    }

    // ----------------------------------------------------------------------
    // Document
    // ----------------------------------------------------------------------

    public void close()
    {
        IOUtil.close( writer );
    }

    public void flush()
    {
        super.flush();
    }

    // ----------------------------------------------------------------------
    // Header
    // ----------------------------------------------------------------------

    public void head_()
    {
        actionContext.release();
    }

    public void head()
    {
        actionContext.setAction( SinkActionContext.HEAD );
    }

    public void author_()
    {
        actionContext.release();
    }

    public void author()
    {
        actionContext.setAction( SinkActionContext.AUTHOR );
    }

    public void date_()
    {
        actionContext.release();
    }

    public void date()
    {
        actionContext.setAction( SinkActionContext.DATE );
    }

    public void title_()
    {
        actionContext.release();
    }

    public void title()
    {
        actionContext.setAction( SinkActionContext.TITLE );
    }

    // ----------------------------------------------------------------------
    // Body
    // ----------------------------------------------------------------------

    public void body_()
    {
        if ( writeStart )
        {
            writeEndElement(); // ElementTags.CHAPTER

            writeEndElement(); // ElementTags.ITEXT
        }

        actionContext.release();
    }

    public void body()
    {
        if ( writeStart )
        {
            writeStartElement( ElementTags.ITEXT );
            writeAddAttribute( ElementTags.TITLE, header.getTitle() );
            writeAddAttribute( ElementTags.AUTHOR, header.getAuthors() );
            writeAddAttribute( ElementTags.CREATIONDATE, header.getDate() );
            writeAddAttribute( ElementTags.SUBJECT, header.getTitle() );
            writeAddAttribute( ElementTags.KEYWORDS, "" );
            writeAddAttribute( ElementTags.PRODUCER, "Generated with Doxia by " + System.getProperty( "user.name" ) );
            writeAddAttribute( ElementTags.PAGE_SIZE, ITextUtil.getPageSize( ITextUtil.getDefaultPageSize() ) );

            writeStartElement( ElementTags.CHAPTER );
            writeAddAttribute( ElementTags.NUMBERDEPTH, numberDepth );
            writeAddAttribute( ElementTags.DEPTH, depth );
            writeAddAttribute( ElementTags.INDENT, "0.0" );

            writeStartElement( ElementTags.TITLE );
            writeAddAttribute( ElementTags.LEADING, DEFAULT_CHAPTER_TITLE_LEADING );
            writeAddAttribute( ElementTags.FONT, ITextFont.DEFAULT_FONT_NAME );
            writeAddAttribute( ElementTags.SIZE, ITextFont.getSectionFontSize( 0 ) );
            writeAddAttribute( ElementTags.STYLE, ITextFont.BOLD );
            writeAddAttribute( ElementTags.BLUE, ITextFont.DEFAULT_FONT_COLOR_BLUE );
            writeAddAttribute( ElementTags.GREEN, ITextFont.DEFAULT_FONT_COLOR_GREEN );
            writeAddAttribute( ElementTags.RED, ITextFont.DEFAULT_FONT_COLOR_RED );
            writeAddAttribute( ElementTags.ALIGN, ElementTags.ALIGN_CENTER );

//            startChunk( ITextFont.DEFAULT_FONT_NAME, ITextFont.getSectionFontSize( 0 ), ITextFont.BOLD, ITextFont.DEFAULT_FONT_COLOR_BLUE, ITextFont.DEFAULT_FONT_COLOR_GREEN, ITextFont.DEFAULT_FONT_COLOR_RED, "top" );

            writeStartElement( ElementTags.CHUNK );
            writeAddAttribute( ElementTags.FONT, ITextFont.DEFAULT_FONT_NAME );
            writeAddAttribute( ElementTags.SIZE, ITextFont.getSectionFontSize( 0 ) );
            writeAddAttribute( ElementTags.STYLE, ITextFont.BOLD );
            writeAddAttribute( ElementTags.BLUE, ITextFont.DEFAULT_FONT_COLOR_BLUE );
            writeAddAttribute( ElementTags.GREEN, ITextFont.DEFAULT_FONT_COLOR_GREEN );
            writeAddAttribute( ElementTags.RED, ITextFont.DEFAULT_FONT_COLOR_RED );
//            writeAddAttribute( ElementTags.LOCALDESTINATION, "top" );

            write( header.getTitle() );

            writeEndElement(); // ElementTags.CHUNK

            writeEndElement(); // ElementTags.TITLE
        }

        actionContext.setAction( SinkActionContext.BODY );
    }

    // ----------------------------------------------------------------------
    // Sections
    // ----------------------------------------------------------------------

    public void sectionTitle()
    {
        actionContext.release();
    }

    public void sectionTitle_()
    {
        actionContext.setAction( SinkActionContext.SECTION_TITLE );
    }

    public void section1_()
    {
        writeEndElement(); // ElementTags.SECTION

        numberDepth--;
        depth = 0;

        actionContext.release();
    }

    public void section1()
    {
        numberDepth++;
        depth = 1;

        writeStartElement( ElementTags.SECTION );
        writeAddAttribute( ElementTags.NUMBERDEPTH, numberDepth );
        writeAddAttribute( ElementTags.DEPTH, depth );
        writeAddAttribute( ElementTags.INDENT, "0.0" );

        lineBreak();

        actionContext.setAction( SinkActionContext.SECTION_1 );
    }

    public void sectionTitle1_()
    {
        writeEndElement(); // ElementTags.TITLE

        font.setSize( ITextFont.DEFAULT_FONT_SIZE );
        bold_();

        actionContext.release();
    }

    public void sectionTitle1()
    {
        font.setSize( ITextFont.getSectionFontSize( 1 ) );
        font.setColor( Color.BLACK );
        bold();

        writeStartElement( ElementTags.TITLE );
        writeAddAttribute( ElementTags.LEADING, DEFAULT_SECTION_TITLE_LEADING );
        writeAddAttribute( ElementTags.FONT, font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, font.getFontColorRed() );
//        writeAddAttribute( ElementTags.LOCALDESTINATION, "top" ); // trygve

        actionContext.setAction( SinkActionContext.SECTION_TITLE_1 );
    }

    public void section2_()
    {
        writeEndElement(); // ElementTags.SECTION

        numberDepth--;
        depth = 0;

        actionContext.release();
    }

    public void section2()
    {
        numberDepth++;
        depth = 1;

        writeStartElement( ElementTags.SECTION );
        writeAddAttribute( ElementTags.NUMBERDEPTH, numberDepth );
        writeAddAttribute( ElementTags.DEPTH, depth );
        writeAddAttribute( ElementTags.INDENT, "0.0" );

        lineBreak();

        actionContext.setAction( SinkActionContext.SECTION_2 );
    }

    public void sectionTitle2_()
    {
        writeEndElement(); // ElementTags.TITLE

        font.setSize( ITextFont.DEFAULT_FONT_SIZE );
        bold_();

        actionContext.release();
    }

    public void sectionTitle2()
    {
        font.setSize( ITextFont.getSectionFontSize( 2 ) );
        font.setColor( Color.BLACK );
        bold();

        writeStartElement( ElementTags.TITLE );
        writeAddAttribute( ElementTags.LEADING, DEFAULT_SECTION_TITLE_LEADING );
        writeAddAttribute( ElementTags.FONT, font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, font.getFontColorRed() );
//        writeAddAttribute( ElementTags.LOCALDESTINATION, "top" ); // trygve

        actionContext.setAction( SinkActionContext.SECTION_TITLE_2 );
    }

    public void section3_()
    {
        writeEndElement(); // ElementTags.SECTION

        numberDepth--;
        depth = 1;

        actionContext.release();
    }

    public void section3()
    {
        numberDepth++;
        depth = 1;

        writeStartElement( ElementTags.SECTION );
        writeAddAttribute( ElementTags.NUMBERDEPTH, numberDepth );
        writeAddAttribute( ElementTags.DEPTH, depth );
        writeAddAttribute( ElementTags.INDENT, "0.0" );

        lineBreak();

        actionContext.setAction( SinkActionContext.SECTION_3 );
    }

    public void sectionTitle3_()
    {
        writeEndElement(); // ElementTags.TITLE

        font.setSize( ITextFont.DEFAULT_FONT_SIZE );
        bold_();

        actionContext.release();
    }

    public void sectionTitle3()
    {
        font.setSize( ITextFont.getSectionFontSize( 3 ) );
        font.setColor( Color.BLACK );
        bold();

        writeStartElement( ElementTags.TITLE );
        writeAddAttribute( ElementTags.LEADING, DEFAULT_SECTION_TITLE_LEADING );
        writeAddAttribute( ElementTags.FONT, font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, font.getFontColorRed() );
//        writeAddAttribute( ElementTags.LOCALDESTINATION, "top" ); // trygve

        actionContext.setAction( SinkActionContext.SECTION_TITLE_3 );
    }

    public void section4_()
    {
        writeEndElement(); // ElementTags.SECTION

        numberDepth--;
        depth = 1;

        actionContext.release();
    }

    public void section4()
    {
        numberDepth++;
        depth = 1;

        writeStartElement( ElementTags.SECTION );
        writeAddAttribute( ElementTags.NUMBERDEPTH, numberDepth );
        writeAddAttribute( ElementTags.DEPTH, depth );
        writeAddAttribute( ElementTags.INDENT, "0.0" );

        lineBreak();

        actionContext.setAction( SinkActionContext.SECTION_4 );
    }

    public void sectionTitle4_()
    {
        writeEndElement(); // ElementTags.TITLE

        font.setSize( ITextFont.DEFAULT_FONT_SIZE );
        bold_();

        actionContext.release();
    }

    public void sectionTitle4()
    {
        font.setSize( ITextFont.getSectionFontSize( 4 ) );
        font.setColor( Color.BLACK );
        bold();

        writeStartElement( ElementTags.TITLE );
        writeAddAttribute( ElementTags.LEADING, DEFAULT_SECTION_TITLE_LEADING );
        writeAddAttribute( ElementTags.FONT, font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, font.getFontColorRed() );
//        writeAddAttribute( ElementTags.LOCALDESTINATION, "top" ); // trygve

        actionContext.setAction( SinkActionContext.SECTION_TITLE_4 );
    }

    public void section5_()
    {
        writeEndElement(); // ElementTags.SECTION

        numberDepth--;
        depth = 1;

        actionContext.release();
    }

    public void section5()
    {
        numberDepth++;
        depth = 1;

        writeStartElement( ElementTags.SECTION );
        writeAddAttribute( ElementTags.NUMBERDEPTH, numberDepth );
        writeAddAttribute( ElementTags.DEPTH, depth );
        writeAddAttribute( ElementTags.INDENT, "0.0" );

        lineBreak();

        actionContext.setAction( SinkActionContext.SECTION_5 );
    }

    public void sectionTitle5_()
    {
        writeEndElement(); // ElementTags.TITLE

        font.setSize( ITextFont.DEFAULT_FONT_SIZE );
        bold_();

        actionContext.release();
    }

    public void sectionTitle5()
    {
        font.setSize( ITextFont.getSectionFontSize( 5 ) );
        font.setColor( Color.BLACK );
        bold();

        writeStartElement( ElementTags.TITLE );
        writeAddAttribute( ElementTags.LEADING, DEFAULT_SECTION_TITLE_LEADING );
        writeAddAttribute( ElementTags.FONT, font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, font.getFontColorRed() );
//        writeAddAttribute( ElementTags.LOCALDESTINATION, "top" ); // trygve

        actionContext.setAction( SinkActionContext.SECTION_TITLE_5 );
    }

    // ----------------------------------------------------------------------
    // Paragraph
    // ----------------------------------------------------------------------

    public void paragraph_()
    {
        // Special case
        if ( ( actionContext.getCurrentAction() == SinkActionContext.LIST_ITEM )
            || ( actionContext.getCurrentAction() == SinkActionContext.NUMBERED_LIST_ITEM )
            || ( actionContext.getCurrentAction() == SinkActionContext.DEFINITION ) )
        {
            return;
        }

        writeEndElement(); // ElementTags.PARAGRAPH

        actionContext.release();
    }

    public void paragraph()
    {
        // Special case
        if ( ( actionContext.getCurrentAction() == SinkActionContext.LIST_ITEM )
            || ( actionContext.getCurrentAction() == SinkActionContext.NUMBERED_LIST_ITEM )
            || ( actionContext.getCurrentAction() == SinkActionContext.DEFINITION ) )
        {
            return;
        }

        writeStartElement( ElementTags.PARAGRAPH );
        writeStartElement( ElementTags.NEWLINE );
        writeEndElement();

        actionContext.setAction( SinkActionContext.PARAGRAPH );
    }

    // ----------------------------------------------------------------------
    // Lists
    // ----------------------------------------------------------------------

    public void list_()
    {
        writeEndElement(); // ElementTags.LIST

        writeEndElement(); // ElementTags.CHUNK

        actionContext.release();
    }

    public void list()
    {
        writeStartElement( ElementTags.CHUNK );
        writeAddAttribute( ElementTags.FONT, font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

        writeStartElement( ElementTags.LIST );
        writeAddAttribute( ElementTags.NUMBERED, Boolean.FALSE.toString() );
        writeAddAttribute( ElementTags.SYMBOLINDENT, "15" );

        actionContext.setAction( SinkActionContext.LIST );
    }

    public void listItem_()
    {
        writeEndElement(); // ElementTags.LISTITEM

        actionContext.release();
    }

    public void listItem()
    {
        writeStartElement( ElementTags.LISTITEM );
        writeAddAttribute( ElementTags.INDENTATIONLEFT, "20.0" );

        actionContext.setAction( SinkActionContext.LIST_ITEM );
    }

    public void numberedList_()
    {
        writeEndElement(); // ElementTags.LIST

        writeEndElement(); // ElementTags.CHUNK

        actionContext.release();
    }

    public void numberedList( int numbering )
    {
        writeStartElement( ElementTags.CHUNK );
        writeAddAttribute( ElementTags.FONT, font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

        writeStartElement( ElementTags.LIST );
        writeAddAttribute( ElementTags.NUMBERED, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.SYMBOLINDENT, "20" );

        switch ( numbering )
        {
            case Sink.NUMBERING_UPPER_ALPHA:
                writeAddAttribute( ElementTags.LETTERED, Boolean.TRUE.toString() );
                writeAddAttribute( ElementTags.FIRST, 'A' );
                break;

            case Sink.NUMBERING_LOWER_ALPHA:
                writeAddAttribute( ElementTags.LETTERED, Boolean.TRUE.toString() );
                writeAddAttribute( ElementTags.FIRST, 'a' );
                break;

            // TODO Doesn't work
            case Sink.NUMBERING_UPPER_ROMAN:
                writeAddAttribute( ElementTags.LETTERED, Boolean.TRUE.toString() );
                writeAddAttribute( ElementTags.FIRST, 'I' );
                break;

            case Sink.NUMBERING_LOWER_ROMAN:
                writeAddAttribute( ElementTags.LETTERED, Boolean.TRUE.toString() );
                writeAddAttribute( ElementTags.FIRST, 'i' );
                break;

            case Sink.NUMBERING_DECIMAL:
            default:
                writeAddAttribute( ElementTags.LETTERED, Boolean.FALSE.toString() );
        }

        actionContext.setAction( SinkActionContext.NUMBERED_LIST );
    }

    public void numberedListItem_()
    {
        writeEndElement(); // ElementTags.LISTITEM

        actionContext.release();
    }

    public void numberedListItem()
    {
        writeStartElement( ElementTags.LISTITEM );
        writeAddAttribute( ElementTags.INDENTATIONLEFT, "20" );

        actionContext.setAction( SinkActionContext.NUMBERED_LIST_ITEM );
    }

    public void definitionList_()
    {
        actionContext.release();
    }

    public void definitionList()
    {
        actionContext.setAction( SinkActionContext.DEFINITION_LIST );
    }

    public void definedTerm_()
    {
        writeEndElement(); // ElementTags.CELL

        writeEndElement(); // ElementTags.ROW

        writeEndElement(); // ElementTags.TABLE

        writeEndElement(); // ElementTags.CHUNK

        actionContext.release();
    }

    public void definedTerm()
    {
        writeStartElement( ElementTags.CHUNK );
        writeAddAttribute( ElementTags.FONT, font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

        writeStartElement( ElementTags.TABLE );
        writeAddAttribute( ElementTags.COLUMNS, "1" );
        writeAddAttribute( ElementTags.LEFT, Boolean.FALSE.toString() );
        writeAddAttribute( ElementTags.RIGHT, Boolean.FALSE.toString() );
        writeAddAttribute( ElementTags.TOP, Boolean.FALSE.toString() );
        writeAddAttribute( ElementTags.BOTTOM, Boolean.FALSE.toString() );
        writeAddAttribute( ElementTags.ALIGN, ElementTags.ALIGN_CENTER );
        writeAddAttribute( ElementTags.WIDTH, "100%" );
        //        writeAddAttribute( ElementTags.TABLEFITSPAGE, Boolean.TRUE.toString() );
        //        writeAddAttribute( ElementTags.CELLSFITPAGE, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.CELLPADDING, "0" );

        writeStartElement( ElementTags.ROW );

        writeStartElement( ElementTags.CELL );
        writeAddAttribute( ElementTags.LEFT, Boolean.FALSE.toString() );
        writeAddAttribute( ElementTags.RIGHT, Boolean.FALSE.toString() );
        writeAddAttribute( ElementTags.TOP, Boolean.FALSE.toString() );
        writeAddAttribute( ElementTags.BOTTOM, Boolean.FALSE.toString() );

        actionContext.setAction( SinkActionContext.DEFINED_TERM );
    }

    public void definition_()
    {
        writeEndElement(); // ElementTags.CELL

        writeEndElement(); // ElementTags.ROW

        writeEndElement(); // ElementTags.TABLE

        writeEndElement(); // ElementTags.CHUNK

        actionContext.release();
    }

    public void definition()
    {
        writeStartElement( ElementTags.CHUNK );
        writeAddAttribute( ElementTags.FONT, font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

        writeStartElement( ElementTags.TABLE );
        writeAddAttribute( ElementTags.COLUMNS, "2" );
        writeAddAttribute( ElementTags.LEFT, Boolean.FALSE.toString() );
        writeAddAttribute( ElementTags.RIGHT, Boolean.FALSE.toString() );
        writeAddAttribute( ElementTags.TOP, Boolean.FALSE.toString() );
        writeAddAttribute( ElementTags.BOTTOM, Boolean.FALSE.toString() );
        writeAddAttribute( ElementTags.ALIGN, ElementTags.ALIGN_CENTER );
        writeAddAttribute( ElementTags.WIDTH, "100%" );
        writeAddAttribute( ElementTags.WIDTHS, "20.0;80.0" );
        //        writeAddAttribute( ElementTags.TABLEFITSPAGE, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.CELLSFITPAGE, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.CELLPADDING, "5" );

        writeStartElement( ElementTags.ROW );

        writeStartElement( ElementTags.CELL );
        writeAddAttribute( ElementTags.LEFT, Boolean.FALSE.toString() );
        writeAddAttribute( ElementTags.RIGHT, Boolean.FALSE.toString() );
        writeAddAttribute( ElementTags.TOP, Boolean.FALSE.toString() );
        writeAddAttribute( ElementTags.BOTTOM, Boolean.FALSE.toString() );

        write( "" );

        writeEndElement(); // ElementTags.CELL

        writeStartElement( ElementTags.CELL );
        writeAddAttribute( ElementTags.LEFT, Boolean.FALSE.toString() );
        writeAddAttribute( ElementTags.RIGHT, Boolean.FALSE.toString() );
        writeAddAttribute( ElementTags.TOP, Boolean.FALSE.toString() );
        writeAddAttribute( ElementTags.BOTTOM, Boolean.FALSE.toString() );

        actionContext.setAction( SinkActionContext.DEFINITION );
    }

    public void definitionListItem_()
    {
        // nop
        actionContext.release();
    }

    public void definitionListItem()
    {
        // nop

        actionContext.setAction( SinkActionContext.DEFINITION_LIST_ITEM );
    }

    // ----------------------------------------------------------------------
    //  Tables
    // ----------------------------------------------------------------------

    public void table_()
    {
        writeEndElement(); // ElementTags.TABLE

        writeEndElement(); // ElementTags.CHUNK

        actionContext.release();
    }

    public void table()
    {
        writeStartElement( ElementTags.CHUNK );
        writeAddAttribute( ElementTags.FONT, font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

        writeStartElement( ElementTags.TABLE );
        writeAddAttribute( ElementTags.LEFT, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.RIGHT, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.TOP, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.BOTTOM, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.ALIGN, ElementTags.ALIGN_CENTER );
        writeAddAttribute( ElementTags.WIDTH, "100.0%" );
        writeAddAttribute( ElementTags.TABLEFITSPAGE, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.CELLSFITPAGE, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.CELLPADDING, "10" );
        //writeAddAttribute( ElementTags.COLUMNS, "2" );

        actionContext.setAction( SinkActionContext.TABLE );
    }

    public void tableCaption_()
    {
        actionContext.release();
    }

    public void tableCaption()
    {
        actionContext.setAction( SinkActionContext.TABLE_CAPTION );
    }

    public void tableCell_()
    {
        writeEndElement(); // ElementTags.CELL

        actionContext.release();
    }

    public void tableCell()
    {
        writeStartElement( ElementTags.CELL );
        writeAddAttribute( ElementTags.LEFT, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.RIGHT, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.TOP, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.BOTTOM, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.HORIZONTALALIGN, ElementTags.ALIGN_LEFT );

        actionContext.setAction( SinkActionContext.TABLE_CELL );
    }

    public void tableCell( String width )
    {
        actionContext.setAction( SinkActionContext.TABLE_CELL );
    }

    public void tableHeaderCell_()
    {
        writeEndElement(); // ElementTags.CELL

        actionContext.release();
    }

    public void tableHeaderCell()
    {
        writeStartElement( ElementTags.CELL );
        writeAddAttribute( ElementTags.LEFT, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.RIGHT, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.TOP, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.BOTTOM, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.HEADER, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.BGRED, Color.GRAY.getRed() );
        writeAddAttribute( ElementTags.BGBLUE, Color.GRAY.getBlue() );
        writeAddAttribute( ElementTags.BGGREEN, Color.GRAY.getGreen() );
        writeAddAttribute( ElementTags.HORIZONTALALIGN, ElementTags.ALIGN_CENTER );

        actionContext.setAction( SinkActionContext.TABLE_HEADER_CELL );
    }

    public void tableHeaderCell( String width )
    {
        actionContext.setAction( SinkActionContext.TABLE_HEADER_CELL );
    }

    public void tableRow_()
    {
        writeEndElement(); // ElementTags.ROW

        actionContext.release();
    }

    public void tableRow()
    {
        writeStartElement( ElementTags.ROW );

        actionContext.setAction( SinkActionContext.TABLE_ROW );
    }

    public void tableRows_()
    {
        //writeEndElement(); // ElementTags.TABLE

        actionContext.release();
    }

    public void tableRows( int[] justification, boolean grid )
    {
        // ElementTags.TABLE
        writeAddAttribute( ElementTags.COLUMNS, justification.length );

        actionContext.setAction( SinkActionContext.TABLE_ROWS );
    }

    // ----------------------------------------------------------------------
    // Verbatim
    // ----------------------------------------------------------------------

    public void verbatim_()
    {
        writeEndElement(); // ElementTags.CELL

        writeEndElement(); // ElementTags.ROW

        writeEndElement(); // ElementTags.TABLE

        writeEndElement(); // ElementTags.CHUNK

        actionContext.release();
    }

    public void verbatim( boolean boxed )
    {
        // Always boxed
        writeStartElement( ElementTags.CHUNK );
        writeAddAttribute( ElementTags.FONT, font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

        writeStartElement( ElementTags.TABLE );
        writeAddAttribute( ElementTags.COLUMNS, "1" );
        writeAddAttribute( ElementTags.LEFT, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.RIGHT, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.TOP, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.BOTTOM, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.ALIGN, ElementTags.ALIGN_CENTER );
        writeAddAttribute( ElementTags.TABLEFITSPAGE, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.CELLSFITPAGE, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.CELLPADDING, "10" );
        writeAddAttribute( ElementTags.WIDTH, "100.0%" );

        writeStartElement( ElementTags.ROW );

        writeStartElement( ElementTags.CELL );
        writeAddAttribute( ElementTags.LEFT, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.RIGHT, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.TOP, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.BOTTOM, Boolean.TRUE.toString() );

        actionContext.setAction( SinkActionContext.VERBATIM );
    }

    // ----------------------------------------------------------------------
    // Figures
    // ----------------------------------------------------------------------

    public void figure_()
    {
        writeEndElement(); // ElementTags.IMAGE

        writeEndElement(); // ElementTags.CHUNK

        actionContext.release();
    }

    public void figure()
    {
        writeStartElement( ElementTags.CHUNK );
        writeAddAttribute( ElementTags.FONT, font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

        writeStartElement( ElementTags.IMAGE );

        actionContext.setAction( SinkActionContext.FIGURE );
    }

    public void figureCaption_()
    {
        actionContext.release();
    }

    public void figureCaption()
    {
        actionContext.setAction( SinkActionContext.FIGURE_CAPTION );
    }

    public void figureGraphics( String name )
    {
        String urlName = null;
        if ( ( name.toLowerCase().startsWith( "http://" ) ) || ( name.toLowerCase().startsWith( "https://" ) ) )
        {
            urlName = name;
        }
        else
        {
            if ( getClassLoader() != null )
            {
                if ( getClassLoader().getResource( name ) != null )
                {
                    urlName = getClassLoader().getResource( name ).toString();
                }
            }
            else
            {
                if ( ITextSink.class.getClassLoader().getResource( name ) != null )
                {
                    urlName = ITextSink.class.getClassLoader().getResource( name ).toString();
                }
            }
        }

        if ( urlName == null )
        {
            //TODO
            System.err.println( "The image " + name + " not found in the class loader. Try to call setClassLoader(ClassLoader) before." );
            return;
        }

        float width = 0;
        float height = 0;
        try
        {
            Image image = Image.getInstance( new URL( urlName ) );
            image.scaleToFit( ITextUtil.getDefaultPageSize().width() / 2, ITextUtil.getDefaultPageSize().height() / 2 );
            width = image.plainWidth();
            height = image.plainHeight();
        }
        catch ( BadElementException e )
        {
            // nop
        }
        catch ( MalformedURLException e )
        {
            // nop
        }
        catch ( IOException e )
        {
            // nop
        }

        writeAddAttribute( ElementTags.URL, urlName );
        writeAddAttribute( ElementTags.ALIGN, ElementTags.ALIGN_MIDDLE );
        writeAddAttribute( ElementTags.PLAINWIDTH, String.valueOf( width ) );
        writeAddAttribute( ElementTags.PLAINHEIGHT, String.valueOf( height ) );

        actionContext.setAction( SinkActionContext.FIGURE_GRAPHICS );
    }

    // ----------------------------------------------------------------------
    // Fonts
    // ----------------------------------------------------------------------

    public void bold_()
    {
        font.removeBold();
    }

    public void bold()
    {
        font.addBold();
    }

    public void italic_()
    {
        font.removeItalic();
    }

    public void italic()
    {
        font.addItalic();
    }

    public void monospaced_()
    {
        font.setMonoSpaced( false );
    }

    public void monospaced()
    {
        font.setMonoSpaced( true );
    }

    // ----------------------------------------------------------------------
    // Links
    // ----------------------------------------------------------------------

    public void link_()
    {
        writeEndElement(); // ElementTags.ANCHOR

        font.setColor( Color.BLACK );
        font.removeUnderlined();

        actionContext.release();
    }

    public void link( String name )
    {
        font.setColor( Color.BLUE );
        font.addUnderlined();

        writeStartElement( ElementTags.ANCHOR );
        writeAddAttribute( ElementTags.REFERENCE, HtmlTools.escapeHTML( name ) );
        writeAddAttribute( ElementTags.FONT, font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

        actionContext.setAction( SinkActionContext.LINK );
    }

    public void anchor_()
    {
        writeEndElement(); // ElementTags.ANCHOR

        actionContext.release();
    }

    public void anchor( String name )
    {
        writeStartElement( ElementTags.ANCHOR );
        writeAddAttribute( ElementTags.NAME, name );

        actionContext.setAction( SinkActionContext.ANCHOR );
    }

    // ----------------------------------------------------------------------
    // Misc
    // ----------------------------------------------------------------------

    public void lineBreak()
    {
        // Special case for the header
        if ( ( actionContext.getCurrentAction() == SinkActionContext.AUTHOR )
            || ( actionContext.getCurrentAction() == SinkActionContext.DATE )
            || ( actionContext.getCurrentAction() == SinkActionContext.TITLE ) )
        {
            return;
        }

        writeStartElement( ElementTags.NEWLINE );
        writeEndElement();
    }

    public void nonBreakingSpace()
    {
        write( " " );
    }

    public void pageBreak()
    {
        writeStartElement( ElementTags.NEWPAGE );
        writeEndElement();
    }

    public void horizontalRule()
    {
        writeStartElement( ElementTags.HORIZONTALRULE );
        writeEndElement();
    }

    // ----------------------------------------------------------------------
    // Text
    // ----------------------------------------------------------------------

    public void rawText( String text )
    {
        writeStartElement( ElementTags.CHUNK );
        writeAddAttribute( ElementTags.FONT, font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

        write( text, false );

        writeEndElement(); // ElementTags.CHUNK
    }

    public void text( String text )
    {
        switch ( actionContext.getCurrentAction() )
        {
            case SinkActionContext.UNDEFINED:
                break;

            case SinkActionContext.HEAD:
                break;

            case SinkActionContext.AUTHOR:
                header.addAuthor( text );
                break;

            case SinkActionContext.DATE:
                header.setDate( text );
                break;

            case SinkActionContext.TITLE:
                header.setTitle( text );
                break;

            case SinkActionContext.SECTION_TITLE_1:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.SECTION_TITLE_2:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.SECTION_TITLE_3:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.SECTION_TITLE_4:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.SECTION_TITLE_5:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.LIST_ITEM:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.NUMBERED_LIST_ITEM:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.DEFINED_TERM:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.DEFINITION:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.TABLE:
                break;

            case SinkActionContext.TABLE_ROW:
                break;

            case SinkActionContext.TABLE_HEADER_CELL:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.TABLE_CELL:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.TABLE_CAPTION:
                writeStartElement( ElementTags.PARAGRAPH );
                writeAddAttribute( ElementTags.ALIGN, ElementTags.ALIGN_CENTER );

                write( text );

                writeEndElement(); // ElementTags.PARAGRAPH
                break;

            case SinkActionContext.VERBATIM:
                // Used to preserve indentation and formating
                LineNumberReader lnr = new LineNumberReader( new StringReader( text ) );
                String line;
                try
                {
                    while ( ( line = lnr.readLine() ) != null )
                    {
                        writeStartElement( ElementTags.CHUNK );
                        writeAddAttribute( ElementTags.FONT, font.getFontName() );
                        writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
                        writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
                        writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
                        writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
                        writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

                        write( "<![CDATA[", true );
                        // Special case
                        line = StringUtils.replace( line, "<![CDATA[", "< ![CDATA[" );
                        line = StringUtils.replace( line, "]]>", "]] >" );
                        write( line, true, false );
                        write( "]]>", true );

                        writeEndElement();
                        lineBreak();
                    }
                }
                catch ( IOException e )
                {
                    throw new RuntimeException( "IOException: ", e );
                }
                break;

            case SinkActionContext.FIGURE:
            case SinkActionContext.FIGURE_GRAPHICS:
                break;

            case SinkActionContext.FIGURE_CAPTION:
                writeAddAttribute( ElementTags.ALT, text );
                break;

            case SinkActionContext.LINK:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.ANCHOR:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.SECTION_TITLE:
            case SinkActionContext.SECTION_1:
            case SinkActionContext.SECTION_2:
            case SinkActionContext.SECTION_3:
            case SinkActionContext.SECTION_4:
            case SinkActionContext.SECTION_5:
                break;

            case SinkActionContext.PARAGRAPH:
            default:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
        }
    }

    /**
     * Convenience method to write a starting element.
     *
     * @param tag the name of the tag
     */
    private void writeStartElement( String tag )
    {
        xmlWriter.startElement( tag );
    }

    /**
     * Convenience method to write a key-value pair.
     *
     * @param key the name of an attribute
     * @param value the value of an attribute
     */
    private void writeAddAttribute( String key, String value )
    {
        xmlWriter.addAttribute( key, value );
    }

    /**
     * Convenience method to write a key-value pair.
     *
     * @param key the name of an attribute
     * @param value the value of an attribute
     */
    private void writeAddAttribute( String key, int value )
    {
        xmlWriter.addAttribute( key, String.valueOf( value ) );
    }

    /**
     * Convenience method to write an end element.
     */
    private void writeEndElement()
    {
        xmlWriter.endElement();
    }

    /**
     * Convenience method to write a String
     *
     * @param aString
     */
    private void write( String aString )
    {
        write( aString, false );
    }

    /**
     * Convenience method to write a String depending the escapeHtml flag
     *
     * @param aString
     * @param escapeHtml
     */
    private void write( String aString, boolean escapeHtml )
    {
        write( aString, escapeHtml, true );
    }

    /**
     * Convenience method to write a String depending the escapeHtml flag
     *
     * @param aString
     * @param escapeHtml
     * @param trim
     */
    private void write( String aString, boolean escapeHtml, boolean trim )
    {
        if ( aString == null )
        {
            return;
        }

        if ( trim )
        {
            aString = StringUtils.replace( aString, "\n", "" );

            LineNumberReader lnr = new LineNumberReader( new StringReader( aString ) );
            StringBuffer sb = new StringBuffer();
            String line;
            try
            {
                while ( ( line = lnr.readLine() ) != null )
                {
                    sb.append( beautifyPhrase( line.trim() ) );
                    sb.append( " " );
                }

                aString = sb.toString();
            }
            catch ( IOException e )
            {
                // nop
            }
            if ( aString.trim().length() == 0 )
            {
                return;
            }
        }
        if ( escapeHtml )
        {
            xmlWriter.writeMarkup( aString );
        }
        else
        {
            xmlWriter.writeText( aString );
        }
    }

    /**
     * Convenience method to return a beautify phrase, i.e. one space between words.
     *
     * @param aString
     * @return a String with only one space between words
     */
    private static String beautifyPhrase( String aString )
    {
        String[] strings = StringUtils.split( aString, " " );
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < strings.length; i++ )
        {
            if ( strings[i].trim().length() != 0 )
            {
                sb.append( strings[i].trim() );
                sb.append( " " );
            }
        }

        return sb.toString().trim();
    }

    private void startChunk( String fontName, int fontSize, String fontStyle, int fontColorBlue, int fontColorGreen, int fontColorRed, String localDestination )
    {
        writeStartElement( ElementTags.CHUNK );
        writeAddAttribute( ElementTags.FONT, fontName );
        writeAddAttribute( ElementTags.SIZE, fontSize );
        writeAddAttribute( ElementTags.STYLE, fontStyle );
        writeAddAttribute( ElementTags.BLUE, fontColorBlue );
        writeAddAttribute( ElementTags.GREEN, fontColorGreen );
        writeAddAttribute( ElementTags.RED, fontColorRed );
//        writeAddAttribute( ElementTags.LOCALDESTINATION, localDestination );
    }
}
