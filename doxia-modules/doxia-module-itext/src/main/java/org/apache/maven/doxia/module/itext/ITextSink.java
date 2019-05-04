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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.AbstractXmlSink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.util.DoxiaUtils;
import org.apache.maven.doxia.util.HtmlTools;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;

/**
 * <p>A doxia Sink which produces an XML Front End document for <code>iText</code> framework.</p>
 * Known limitations:
 * <ul>
 * <li>Roman lists are not supported.</li>
 * <li>Horizontal rule is not supported with 1.3.
 * See <a href="http://www.mail-archive.com/itext-questions@lists.sourceforge.net/msg10323.html">
 * http://www.mail-archive.com/itext-questions@lists.sourceforge.net/msg10323.html</a></li>
 * <li>iText has some problems with <code>ElementTags.TABLE</code> and <code>ElementTags.TABLEFITSPAGE</code>.
 * See <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=786427&group_id=15255&atid=115255">
 * SourceForce Tracker</a>.</li>
 * <li>Images could be on another page and next text on the last one.</li>
 * </ul>
 *
 * @see <a href="http://www.lowagie.com/iText/tutorial/ch07.html">http://www.lowagie.com/iText/tutorial/ch07.html</a>
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class ITextSink
    extends AbstractXmlSink
{
    /** This is the place where the iText DTD is located. IMPORTANT: this DTD is not uptodate! */
    public static final String DTD = "http://itext.sourceforge.net/itext.dtd";

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
    private final XMLWriter xmlWriter;

    private boolean writeStart;

    /** The Header object */
    private ITextHeader header;

    /** The font object */
    private ITextFont font;

    private int numberDepth = 1;

    private int depth = 0;

    private StringWriter tableCaptionWriter = null;

    private XMLWriter tableCaptionXMLWriter = null;

    /** Flag to know if an anchor is defined or not. Used as workaround for iText which needs a defined local
     * destination. */
    private boolean anchorDefined = false;

    /** Flag to know if an figure event is called. */
    private boolean figureDefined = false;

    /** Keep track of the closing tags for inline events. */
    protected Stack<List<String>> inlineStack = new Stack<>();

    /** Map of warn messages with a String as key to describe the error type and a Set as value.
     * Using to reduce warn messages. */
    private Map<String, Set<String>> warnMessages;

    /**
     * <p>Constructor for ITextSink.</p>
     *
     * @param writer the writer.
     */
    protected ITextSink( Writer writer )
    {
        this( writer, "UTF-8" );
    }

    /**
     * <p>Constructor for ITextSink.</p>
     *
     * @param writer the writer.
     * @param encoding the encoding.
     * @since 1.1
     */
    protected ITextSink( Writer writer, String encoding )
    {
        // No doctype since itext doctype is not up to date!
        this( new PrettyPrintXMLWriter( writer, encoding, null ) );

        this.writer = writer;
        this.writeStart = true;
    }

    /**
     * <p>Constructor for ITextSink.</p>
     *
     * @param xmlWriter a pretty-printing xml writer.
     */
    protected ITextSink( PrettyPrintXMLWriter xmlWriter )
    {
        this.xmlWriter = xmlWriter;

        this.writeStart = false;

        init();
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
     * @param cl the class loader.
     */
    public void setClassLoader( ClassLoader cl )
    {
        currentClassLoader = cl;
    }

    // ----------------------------------------------------------------------
    // Document
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void close()
    {
        IOUtil.close( writer );

        init();
    }

    /** {@inheritDoc} */
    public void flush()
    {
        if ( getLog().isWarnEnabled() && this.warnMessages != null )
        {
            for ( Map.Entry<String, Set<String>> entry : this.warnMessages.entrySet() )
            {
                for ( String msg : entry.getValue() )
                {
                    getLog().warn( msg );
                }
            }
        }

        this.warnMessages = null;
    }

    // ----------------------------------------------------------------------
    // Header
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void head_()
    {
        actionContext.release();
    }

    /** {@inheritDoc} */
    public void head()
    {
        //init(); // why? this causes DOXIA-413

        actionContext.setAction( SinkActionContext.HEAD );
    }

    /** {@inheritDoc} */
    public void author_()
    {
        actionContext.release();
    }

    /** {@inheritDoc} */
    public void author()
    {
        actionContext.setAction( SinkActionContext.AUTHOR );
    }

    /** {@inheritDoc} */
    public void date_()
    {
        actionContext.release();
    }

    /** {@inheritDoc} */
    public void date()
    {
        actionContext.setAction( SinkActionContext.DATE );
    }

    /** {@inheritDoc} */
    public void title_()
    {
        actionContext.release();
    }

    /** {@inheritDoc} */
    public void title()
    {
        actionContext.setAction( SinkActionContext.TITLE );
    }

    // ----------------------------------------------------------------------
    // Body
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void body_()
    {
        if ( writeStart )
        {
            writeEndElement(); // ElementTags.CHAPTER

            writeEndElement(); // ElementTags.ITEXT
        }

        actionContext.release();
    }

    /** {@inheritDoc} */
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

//            startChunk( ITextFont.DEFAULT_FONT_NAME, ITextFont.getSectionFontSize( 0 ),
//                    ITextFont.BOLD, ITextFont.DEFAULT_FONT_COLOR_BLUE, ITextFont.DEFAULT_FONT_COLOR_GREEN,
//                    ITextFont.DEFAULT_FONT_COLOR_RED, "top" );

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

    /** {@inheritDoc} */
    public void sectionTitle()
    {
        actionContext.release();
    }

    /** {@inheritDoc} */
    public void sectionTitle_()
    {
        actionContext.setAction( SinkActionContext.SECTION_TITLE );
    }

    /** {@inheritDoc} */
    public void section1_()
    {
        writeEndElement(); // ElementTags.SECTION

        numberDepth--;
        depth = 0;

        actionContext.release();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void sectionTitle1_()
    {
        writeEndElement(); // ElementTags.TITLE

        font.setSize( ITextFont.DEFAULT_FONT_SIZE );
        bold_();

        actionContext.release();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void section2_()
    {
        writeEndElement(); // ElementTags.SECTION

        numberDepth--;
        depth = 0;

        actionContext.release();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void sectionTitle2_()
    {
        writeEndElement(); // ElementTags.TITLE

        font.setSize( ITextFont.DEFAULT_FONT_SIZE );
        bold_();

        actionContext.release();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void section3_()
    {
        writeEndElement(); // ElementTags.SECTION

        numberDepth--;
        depth = 1;

        actionContext.release();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void sectionTitle3_()
    {
        writeEndElement(); // ElementTags.TITLE

        font.setSize( ITextFont.DEFAULT_FONT_SIZE );
        bold_();

        actionContext.release();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void section4_()
    {
        writeEndElement(); // ElementTags.SECTION

        numberDepth--;
        depth = 1;

        actionContext.release();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void sectionTitle4_()
    {
        writeEndElement(); // ElementTags.TITLE

        font.setSize( ITextFont.DEFAULT_FONT_SIZE );
        bold_();

        actionContext.release();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void section5_()
    {
        writeEndElement(); // ElementTags.SECTION

        numberDepth--;
        depth = 1;

        actionContext.release();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void sectionTitle5_()
    {
        writeEndElement(); // ElementTags.TITLE

        font.setSize( ITextFont.DEFAULT_FONT_SIZE );
        bold_();

        actionContext.release();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void list_()
    {
        writeEndElement(); // ElementTags.LIST

        writeEndElement(); // ElementTags.CHUNK

        actionContext.release();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void listItem_()
    {
        writeEndElement(); // ElementTags.LISTITEM

        actionContext.release();
    }

    /** {@inheritDoc} */
    public void listItem()
    {
        writeStartElement( ElementTags.LISTITEM );
        writeAddAttribute( ElementTags.INDENTATIONLEFT, "20.0" );

        actionContext.setAction( SinkActionContext.LIST_ITEM );
    }

    /** {@inheritDoc} */
    public void numberedList_()
    {
        writeEndElement(); // ElementTags.LIST

        writeEndElement(); // ElementTags.CHUNK

        actionContext.release();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void numberedListItem_()
    {
        writeEndElement(); // ElementTags.LISTITEM

        actionContext.release();
    }

    /** {@inheritDoc} */
    public void numberedListItem()
    {
        writeStartElement( ElementTags.LISTITEM );
        writeAddAttribute( ElementTags.INDENTATIONLEFT, "20" );

        actionContext.setAction( SinkActionContext.NUMBERED_LIST_ITEM );
    }

    /** {@inheritDoc} */
    public void definitionList_()
    {
        actionContext.release();
    }

    /** {@inheritDoc} */
    public void definitionList()
    {
        lineBreak();

        actionContext.setAction( SinkActionContext.DEFINITION_LIST );
    }

    /** {@inheritDoc} */
    public void definedTerm_()
    {
        font.setSize( ITextFont.DEFAULT_FONT_SIZE );
        bold_();

        writeEndElement(); // ElementTags.CHUNK

        actionContext.release();

        lineBreak();
    }

    /** {@inheritDoc} */
    public void definedTerm()
    {
        font.setSize( ITextFont.DEFAULT_FONT_SIZE + 2 );
        bold();

        writeStartElement( ElementTags.CHUNK );
        writeAddAttribute( ElementTags.FONT, font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

        actionContext.setAction( SinkActionContext.DEFINED_TERM );
    }

    /** {@inheritDoc} */
    public void definition_()
    {
        writeEndElement(); // ElementTags.CHUNK

        actionContext.release();

        lineBreak();
    }

    /** {@inheritDoc} */
    public void definition()
    {
        writeStartElement( ElementTags.CHUNK );
        writeAddAttribute( ElementTags.FONT, font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, font.getFontColorRed() );


        writeStartElement( ElementTags.CHUNK );
        writeAddAttribute( ElementTags.FONT, font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

        // We need to add a non break space first to display empty string
        write( "\u00A0" + StringUtils.repeat( " ", 16 ), false, false );

        writeEndElement(); // ElementTags.CHUNK

        actionContext.setAction( SinkActionContext.DEFINITION );
    }

    /** {@inheritDoc} */
    public void definitionListItem_()
    {
        actionContext.release();
    }

    /** {@inheritDoc} */
    public void definitionListItem()
    {
        actionContext.setAction( SinkActionContext.DEFINITION_LIST_ITEM );
    }

    // ----------------------------------------------------------------------
    //  Tables
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void table_()
    {
        if ( tableCaptionXMLWriter != null )
        {
            tableCaptionXMLWriter = null;

            writeEndElement(); // ElementTags.TABLE

            writeEndElement(); // ElementTags.CHUNK

            writeStartElement( ElementTags.PARAGRAPH );
            writeAddAttribute( ElementTags.ALIGN, ElementTags.ALIGN_CENTER );

            write( tableCaptionWriter.toString(), true );

            writeEndElement(); // ElementTags.PARAGRAPH

            tableCaptionWriter = null;
        }
        else
        {
            writeEndElement(); // ElementTags.TABLE

            writeEndElement(); // ElementTags.CHUNK
        }
        actionContext.release();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void tableCaption_()
    {
        actionContext.release();
    }

    /** {@inheritDoc} */
    public void tableCaption()
    {
        tableCaptionWriter = new StringWriter();
        tableCaptionXMLWriter = new PrettyPrintXMLWriter( tableCaptionWriter );
        actionContext.setAction( SinkActionContext.TABLE_CAPTION );
    }

    /** {@inheritDoc} */
    public void tableCell_()
    {
        writeEndElement(); // ElementTags.CELL

        actionContext.release();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void tableCell( String width )
    {
        actionContext.setAction( SinkActionContext.TABLE_CELL );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell_()
    {
        writeEndElement(); // ElementTags.CELL

        actionContext.release();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void tableHeaderCell( String width )
    {
        actionContext.setAction( SinkActionContext.TABLE_HEADER_CELL );
    }

    /** {@inheritDoc} */
    public void tableRow_()
    {
        writeEndElement(); // ElementTags.ROW

        actionContext.release();
    }

    /** {@inheritDoc} */
    public void tableRow()
    {
        writeStartElement( ElementTags.ROW );

        actionContext.setAction( SinkActionContext.TABLE_ROW );
    }

    /** {@inheritDoc} */
    public void tableRows_()
    {
        //writeEndElement(); // ElementTags.TABLE

        actionContext.release();
    }

    /** {@inheritDoc} */
    public void tableRows( int[] justification, boolean grid )
    {
        // ElementTags.TABLE
        writeAddAttribute( ElementTags.COLUMNS, justification.length );

        actionContext.setAction( SinkActionContext.TABLE_ROWS );
    }

    // ----------------------------------------------------------------------
    // Verbatim
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void verbatim_()
    {
        writeEndElement(); // ElementTags.CELL

        writeEndElement(); // ElementTags.ROW

        writeEndElement(); // ElementTags.TABLE

        writeEndElement(); // ElementTags.CHUNK

        actionContext.release();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void figure_()
    {
        writeEndElement(); // ElementTags.IMAGE

        writeEndElement(); // ElementTags.CHUNK

        actionContext.release();

        figureDefined = false;
    }

    /** {@inheritDoc} */
    public void figure()
    {
        figureDefined = true;

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

    /** {@inheritDoc} */
    public void figureCaption_()
    {
        actionContext.release();
    }

    /** {@inheritDoc} */
    public void figureCaption()
    {
        actionContext.setAction( SinkActionContext.FIGURE_CAPTION );
    }

    /**
     * If the <code>name</code> is a relative link, the internal link will used a System property
     * <code>itext.basedir</code>, or the class loader.
     * {@inheritDoc}
     */
    public void figureGraphics( String name )
    {
        String urlName = null;
        File nameFile = null;
        if ( ( name.toLowerCase( Locale.ENGLISH ).startsWith( "http://" ) )
            || ( name.toLowerCase( Locale.ENGLISH ).startsWith( "https://" ) ) )
        {
            urlName = name;
        }
        else
        {
            if ( System.getProperty( "itext.basedir" ) != null )
            {
                try
                {
                    nameFile = new File( System.getProperty( "itext.basedir" ), name );
                    urlName = nameFile.toURI().toURL().toString();
                }
                catch ( MalformedURLException e )
                {
                    getLog().error( "MalformedURLException: " + e.getMessage(), e );
                }
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
        }

        if ( urlName == null )
        {
            String msg =
                "No image '" + name
                    + "' found in the class loader. Try to call setClassLoader(ClassLoader) before.";
            logMessage( "imageNotFound", msg );

            return;
        }

        if ( nameFile != null && !nameFile.exists() )
        {
            String msg = "No image '" + nameFile + "' found in your system, check the path.";
            logMessage( "imageNotFound", msg );

            return;
        }

        boolean figureCalled = figureDefined;
        if ( !figureCalled )
        {
            figure();
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
            getLog().error( "BadElementException: " + e.getMessage(), e );
        }
        catch ( MalformedURLException e )
        {
            getLog().error( "MalformedURLException: " + e.getMessage(), e );
        }
        catch ( IOException e )
        {
            getLog().error( "IOException: " + e.getMessage(), e );
        }

        writeAddAttribute( ElementTags.URL, urlName );
        writeAddAttribute( ElementTags.ALIGN, ElementTags.ALIGN_MIDDLE );
        writeAddAttribute( ElementTags.PLAINWIDTH, String.valueOf( width ) );
        writeAddAttribute( ElementTags.PLAINHEIGHT, String.valueOf( height ) );

        actionContext.setAction( SinkActionContext.FIGURE_GRAPHICS );

        if ( !figureCalled )
        {
            figure_();
        }
    }

    // ----------------------------------------------------------------------
    // Fonts
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void inline()
    {
        inline( null );
    }

    /** {@inheritDoc} */
    public void inline( SinkEventAttributes attributes )
    {
        List<String> tags = new ArrayList<>();

        if ( attributes != null )
        {

            if ( attributes.containsAttribute( SinkEventAttributes.SEMANTICS, "italic" ) )
            {
                font.addItalic();
                tags.add( 0, "italic" );
            }

            if ( attributes.containsAttribute( SinkEventAttributes.SEMANTICS, "bold" ) )
            {
                font.addBold();
                tags.add( 0, "bold" );
            }

            if ( attributes.containsAttribute( SinkEventAttributes.SEMANTICS, "code" ) )
            {
                font.setMonoSpaced( true );
                tags.add( 0, "code" );
            }

        }

        inlineStack.push( tags );
    }

    /** {@inheritDoc} */
    public void inline_()
    {
        for ( String tag: inlineStack.pop() )
        {
            if ( "italic".equals( tag ) )
            {
                font.removeItalic();
            }
            else if ( "bold".equals( tag ) )
            {
                font.removeBold();
            }
            else if ( "code".equals( tag ) )
            {
                font.setMonoSpaced( false );
            }
        }
    }

    /** {@inheritDoc} */
    public void bold_()
    {
        inline_();
    }

    /** {@inheritDoc} */
    public void bold()
    {
        inline( SinkEventAttributeSet.Semantics.BOLD );
    }

    /** {@inheritDoc} */
    public void italic_()
    {
        inline_();
    }

    /** {@inheritDoc} */
    public void italic()
    {
        inline( SinkEventAttributeSet.Semantics.ITALIC );
    }

    /** {@inheritDoc} */
    public void monospaced_()
    {
        inline_();
    }

    /** {@inheritDoc} */
    public void monospaced()
    {
        inline( SinkEventAttributeSet.Semantics.CODE );
    }

    // ----------------------------------------------------------------------
    // Links
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void link_()
    {
        writeEndElement(); // ElementTags.ANCHOR

        font.setColor( Color.BLACK );
        font.removeUnderlined();

        actionContext.release();
    }

    /** {@inheritDoc} */
    public void link( String name )
    {
        if ( name == null )
        {
            throw new NullPointerException( "Link name cannot be null!" );
        }

        font.setColor( Color.BLUE );
        font.addUnderlined();

        writeStartElement( ElementTags.ANCHOR );
        if ( StringUtils.isNotEmpty( name ) && name.startsWith( "#" ) && StringUtils.isNotEmpty( header.getTitle() ) )
        {
            name = "#" + DoxiaUtils.encodeId( header.getTitle(), true ) + "_" + name.substring( 1 );
        }
        writeAddAttribute( ElementTags.REFERENCE, HtmlTools.escapeHTML( name ) );
        writeAddAttribute( ElementTags.FONT, font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, font.getFontColorRed() );

        actionContext.setAction( SinkActionContext.LINK );
    }

    /** {@inheritDoc} */
    public void anchor_()
    {
        if ( !anchorDefined )
        {
            // itext needs a defined local destination, we put an invisible text
            writeAddAttribute( ElementTags.BLUE, "255" );
            writeAddAttribute( ElementTags.GREEN, "255" );
            writeAddAttribute( ElementTags.RED, "255" );

            write( "_" );
        }

        anchorDefined = false;

        writeEndElement(); // ElementTags.ANCHOR

        actionContext.release();
    }

    /** {@inheritDoc} */
    public void anchor( String name )
    {
        if ( name == null )
        {
            throw new NullPointerException( "Anchor name cannot be null!" );
        }

        if ( StringUtils.isNotEmpty( header.getTitle() ) )
        {
            name = header.getTitle() + "_" + name;
        }
        String id = name;

        if ( !DoxiaUtils.isValidId( id ) )
        {
            id = DoxiaUtils.encodeId( name, true );

            String msg = "Modified invalid link: '" + name + "' to '" + id + "'";
            logMessage( "modifiedLink", msg );
        }

        writeStartElement( ElementTags.ANCHOR );
        writeAddAttribute( ElementTags.NAME, id );
        writeAddAttribute( ElementTags.FONT, font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, font.getFontStyle() );

        actionContext.setAction( SinkActionContext.ANCHOR );
    }

    // ----------------------------------------------------------------------
    // Misc
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void nonBreakingSpace()
    {
        write( " " );
    }

    /** {@inheritDoc} */
    public void pageBreak()
    {
        writeStartElement( ElementTags.NEWPAGE );
        writeEndElement();
    }

    /** {@inheritDoc} */
    public void horizontalRule()
    {
        writeStartElement( ElementTags.PARAGRAPH );
        writeAddAttribute( ElementTags.BLUE, "255" );
        writeAddAttribute( ElementTags.GREEN, "255" );
        writeAddAttribute( ElementTags.RED, "255" );
        write( "_" );
        writeEndElement();

        writeStartElement( ElementTags.PARAGRAPH );
        writeStartElement( ElementTags.HORIZONTALRULE );
        writeEndElement();
        writeEndElement();
    }

    // ----------------------------------------------------------------------
    // Text
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void text( String text )
    {
        if ( StringUtils.isEmpty( text ) )
        {
            return;
        }

        switch ( actionContext.getCurrentAction() )
        {
            case SinkActionContext.AUTHOR:
                header.addAuthor( text );
                break;

            case SinkActionContext.DATE:
                header.setDate( text );
                break;

            case SinkActionContext.TITLE:
                header.setTitle( text );
                break;

            case SinkActionContext.TABLE_CAPTION:
                this.tableCaptionXMLWriter.writeText( text );
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

            case SinkActionContext.FIGURE_CAPTION:
                writeAddAttribute( ElementTags.ALT, text );
                break;

            case SinkActionContext.SECTION_TITLE:
            case SinkActionContext.SECTION_1:
            case SinkActionContext.SECTION_2:
            case SinkActionContext.SECTION_3:
            case SinkActionContext.SECTION_4:
            case SinkActionContext.SECTION_5:
            case SinkActionContext.FIGURE:
            case SinkActionContext.FIGURE_GRAPHICS:
            case SinkActionContext.TABLE_ROW:
            case SinkActionContext.TABLE:
            case SinkActionContext.HEAD:
            case SinkActionContext.UNDEFINED:
                break;

            case SinkActionContext.ANCHOR:
                anchorDefined = true;
            case SinkActionContext.PARAGRAPH:
            case SinkActionContext.LINK:
            case SinkActionContext.TABLE_CELL:
            case SinkActionContext.TABLE_HEADER_CELL:
            case SinkActionContext.DEFINITION:
            case SinkActionContext.DEFINED_TERM:
            case SinkActionContext.NUMBERED_LIST_ITEM:
            case SinkActionContext.LIST_ITEM:
            case SinkActionContext.SECTION_TITLE_5:
            case SinkActionContext.SECTION_TITLE_4:
            case SinkActionContext.SECTION_TITLE_3:
            case SinkActionContext.SECTION_TITLE_2:
            case SinkActionContext.SECTION_TITLE_1:
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
     * {@inheritDoc}
     *
     * Unkown events just log a warning message but are ignored otherwise.
     * @see org.apache.maven.doxia.sink.Sink#unknown(String,Object[],SinkEventAttributes)
     */
    public void unknown( String name, Object[] requiredParams, SinkEventAttributes attributes )
    {
        String msg = "Unknown Sink event: '" + name + "', ignoring!";
        logMessage( "unknownEvent", msg );
    }

    /** {@inheritDoc} */
    protected void init()
    {
        super.init();

        this.actionContext = new SinkActionContext();
        this.font = new ITextFont();
        this.header = new ITextHeader();

        this.numberDepth = 1;
        this.depth = 0;
        this.tableCaptionWriter = null;
        this.tableCaptionXMLWriter = null;
        this.anchorDefined = false;
        this.figureDefined = false;
        this.warnMessages = null;
    }

    /**
     * Convenience method to write a starting element.
     *
     * @param tag the name of the tag
     */
    private void writeStartElement( String tag )
    {
        if ( tableCaptionXMLWriter == null )
        {
            xmlWriter.startElement( tag );
        }
        else
        {
            tableCaptionXMLWriter.startElement( tag );
        }
    }

    /**
     * Convenience method to write a key-value pair.
     *
     * @param key the name of an attribute
     * @param value the value of an attribute
     */
    private void writeAddAttribute( String key, String value )
    {
        if ( tableCaptionXMLWriter == null )
        {
            xmlWriter.addAttribute( key, value );
        }
        else
        {
            tableCaptionXMLWriter.addAttribute( key, value );
        }
    }

    /**
     * Convenience method to write a key-value pair.
     *
     * @param key the name of an attribute
     * @param value the value of an attribute
     */
    private void writeAddAttribute( String key, int value )
    {
        if ( tableCaptionXMLWriter == null )
        {
            xmlWriter.addAttribute( key, String.valueOf( value ) );
        }
        else
        {
            tableCaptionXMLWriter.addAttribute( key, String.valueOf( value ) );
        }
    }

    /**
     * Convenience method to write an end element.
     */
    private void writeEndElement()
    {
        if ( tableCaptionXMLWriter == null )
        {
            xmlWriter.endElement();
        }
        else
        {
            tableCaptionXMLWriter.endElement();
        }
    }

    /**
     * Convenience method to write a String
     *
     * @param aString
     */
    protected void write( String aString )
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
            StringBuilder sb = new StringBuilder();
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
            if ( tableCaptionXMLWriter == null )
            {
                xmlWriter.writeMarkup( aString );
            }
            else
            {
                tableCaptionXMLWriter.writeMarkup( aString );
            }
        }
        else
        {
            if ( tableCaptionXMLWriter == null )
            {
                xmlWriter.writeText( aString );
            }
            else
            {
                tableCaptionXMLWriter.writeText( aString );
            }
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
        StringBuilder sb = new StringBuilder();
        for ( String string : strings )
        {
            if ( string.trim().length() != 0 )
            {
                sb.append( string.trim() );
                sb.append( " " );
            }
        }

        return sb.toString().trim();
    }

    private void startChunk( String fontName, int fontSize, String fontStyle, int fontColorBlue, int fontColorGreen,
                             int fontColorRed, String localDestination )
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

    /**
     * If debug mode is enabled, log the <code>msg</code> as is, otherwise add unique msg in <code>warnMessages</code>.
     *
     * @param key not null
     * @param msg not null
     * @see #close()
     * @since 1.1.1
     */
    private void logMessage( String key, String msg )
    {
        msg = "[iText Sink] " + msg;
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( msg );

            return;
        }

        if ( warnMessages == null )
        {
            warnMessages = new HashMap<>();
        }

        Set<String> set = warnMessages.get( key );
        if ( set == null )
        {
            set = new TreeSet<>();
        }
        set.add( msg );
        warnMessages.put( key, set );
    }
}
