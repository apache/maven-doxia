package org.apache.maven.doxia.module.itext;

/*
 * Copyright 2004-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.awt.Color;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.maven.doxia.module.HtmlTools;
import org.apache.maven.doxia.module.SinkActionContext;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkAdapter;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;

import com.lowagie.text.BadElementException;
import com.lowagie.text.ElementTags;
import com.lowagie.text.Image;

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
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @plexus.component role="org.apache.maven.doxia.sink.Sink"
 * role-hint="pdf"
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

    /** The Header object */
    private ITextHeader header;

    /** The font object */
    private ITextFont font;

    private int numberDepth = 0;

    private int depth = 0;

    /**
     * Default constructor
     *
     * @param w a writer
     */
    public ITextSink( Writer w )
    {
        this.writer = w;

        this.actionContext = new SinkActionContext();
        this.font = new ITextFont();
        this.header = new ITextHeader();

        this.xmlWriter = new PrettyPrintXMLWriter( this.writer, "UTF-8", null );//, DOCTYPE );
    }

    /**
     * Get the current classLoader
     *
     * @return the current class loader
     */
    public ClassLoader getClassLoader()
    {
        return this.currentClassLoader;
    }

    /**
     * Set a new class loader
     *
     * @param cl
     */
    public void setClassLoader( ClassLoader cl )
    {
        this.currentClassLoader = cl;
    }

    // ----------------------------------------------------------------------
    // Document
    // ----------------------------------------------------------------------
    /**
     * @see org.apache.maven.doxia.sink.Sink#close()
     */
    public void close()
    {
        IOUtil.close( this.writer );
    }

    /**
     * @see org.apache.maven.doxia.sink.Sink#flush()
     */
    public void flush()
    {
        super.flush();
    }

    // ----------------------------------------------------------------------
    // Header
    // ----------------------------------------------------------------------
    /**
     * @see org.apache.maven.doxia.sink.Sink#head_()
     */
    public void head_()
    {
        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.Sink#head()
     */
    public void head()
    {
        this.actionContext.setAction( SinkActionContext.HEAD );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#author_()
     */
    public void author_()
    {
        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#author()
     */
    public void author()
    {
        this.actionContext.setAction( SinkActionContext.AUTHOR );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#date_()
     */
    public void date_()
    {
        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#date()
     */
    public void date()
    {
        this.actionContext.setAction( SinkActionContext.DATE );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#title_()
     */
    public void title_()
    {
        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#title()
     */
    public void title()
    {
        this.actionContext.setAction( SinkActionContext.TITLE );
    }

    // ----------------------------------------------------------------------
    // Body
    // ----------------------------------------------------------------------
    /**
     * @see org.apache.maven.doxia.sink.Sink#body_()
     */
    public void body_()
    {
        writeEndElement(); // ElementTags.CHAPTER

        writeEndElement(); // ElementTags.ITEXT

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.Sink#body()
     */
    public void body()
    {
        writeStartElement( ElementTags.ITEXT );
        writeAddAttribute( ElementTags.TITLE, this.header.getTitle() );
        writeAddAttribute( ElementTags.AUTHOR, this.header.getAuthors() );
        writeAddAttribute( ElementTags.CREATIONDATE, this.header.getDate() );
        writeAddAttribute( ElementTags.SUBJECT, this.header.getTitle() );
        writeAddAttribute( ElementTags.KEYWORDS, "" );
        writeAddAttribute( ElementTags.PRODUCER, "Generated with Doxia by " + System.getProperty( "user.name" ) );
        writeAddAttribute( ElementTags.PAGE_SIZE, ITextUtil.getPageSize( ITextUtil.getDefaultPageSize() ) );

        writeStartElement( ElementTags.CHAPTER );
        writeAddAttribute( ElementTags.NUMBERDEPTH, this.numberDepth );
        writeAddAttribute( ElementTags.DEPTH, this.depth );
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

        writeStartElement( ElementTags.CHUNK );
        writeAddAttribute( ElementTags.FONT, ITextFont.DEFAULT_FONT_NAME );
        writeAddAttribute( ElementTags.SIZE, ITextFont.getSectionFontSize( 0 ) );
        writeAddAttribute( ElementTags.STYLE, ITextFont.BOLD );
        writeAddAttribute( ElementTags.BLUE, ITextFont.DEFAULT_FONT_COLOR_BLUE );
        writeAddAttribute( ElementTags.GREEN, ITextFont.DEFAULT_FONT_COLOR_GREEN );
        writeAddAttribute( ElementTags.RED, ITextFont.DEFAULT_FONT_COLOR_RED );
        writeAddAttribute( ElementTags.LOCALDESTINATION, "top" );

        write( this.header.getTitle() );

        writeEndElement(); // ElementTags.CHUNK

        writeEndElement(); // ElementTags.TITLE

        this.actionContext.setAction( SinkActionContext.BODY );
    }

    // ----------------------------------------------------------------------
    // Sections
    // ----------------------------------------------------------------------
    /**
     * @see org.apache.maven.doxia.sink.Sink#sectionTitle()
     */
    public void sectionTitle()
    {
        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.Sink#sectionTitle_()
     */
    public void sectionTitle_()
    {
        this.actionContext.setAction( SinkActionContext.SECTION_TITLE );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#section1_()
     */
    public void section1_()
    {
        writeEndElement(); // ElementTags.SECTION

        this.numberDepth--;
        this.depth = 0;

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#section1()
     */
    public void section1()
    {
        this.numberDepth++;
        this.depth = 1;

        writeStartElement( ElementTags.SECTION );
        writeAddAttribute( ElementTags.NUMBERDEPTH, this.numberDepth );
        writeAddAttribute( ElementTags.DEPTH, this.depth );
        writeAddAttribute( ElementTags.INDENT, "0.0" );

        lineBreak();

        this.actionContext.setAction( SinkActionContext.SECTION_1 );
    }

    /**
     * @see org.apache.maven.doxia.sink.Sink#sectionTitle1_()
     */
    public void sectionTitle1_()
    {
        writeEndElement(); // ElementTags.TITLE

        this.font.setSize( ITextFont.DEFAULT_FONT_SIZE );
        bold_();

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.Sink#sectionTitle()
     */
    public void sectionTitle1()
    {
        this.font.setSize( ITextFont.getSectionFontSize( 1 ) );
        this.font.setColor( Color.BLACK );
        bold();

        writeStartElement( ElementTags.TITLE );
        writeAddAttribute( ElementTags.LEADING, DEFAULT_SECTION_TITLE_LEADING );
        writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

        this.actionContext.setAction( SinkActionContext.SECTION_TITLE_1 );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#section2_()
     */
    public void section2_()
    {
        writeEndElement(); // ElementTags.SECTION

        this.numberDepth--;
        this.depth = 0;

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#section2()
     */
    public void section2()
    {
        this.numberDepth++;
        this.depth = 1;

        writeStartElement( ElementTags.SECTION );
        writeAddAttribute( ElementTags.NUMBERDEPTH, this.numberDepth );
        writeAddAttribute( ElementTags.DEPTH, this.depth );
        writeAddAttribute( ElementTags.INDENT, "0.0" );

        lineBreak();

        this.actionContext.setAction( SinkActionContext.SECTION_2 );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#sectionTitle2_()
     */
    public void sectionTitle2_()
    {
        writeEndElement(); // ElementTags.TITLE

        this.font.setSize( ITextFont.DEFAULT_FONT_SIZE );
        bold_();

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#sectionTitle2()
     */
    public void sectionTitle2()
    {
        this.font.setSize( ITextFont.getSectionFontSize( 2 ) );
        this.font.setColor( Color.BLACK );
        bold();

        writeStartElement( ElementTags.TITLE );
        writeAddAttribute( ElementTags.LEADING, DEFAULT_SECTION_TITLE_LEADING );
        writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

        this.actionContext.setAction( SinkActionContext.SECTION_TITLE_2 );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#section3_()
     */
    public void section3_()
    {
        writeEndElement(); // ElementTags.SECTION

        this.numberDepth--;
        this.depth = 1;

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#section3()
     */
    public void section3()
    {
        this.numberDepth++;
        this.depth = 1;

        writeStartElement( ElementTags.SECTION );
        writeAddAttribute( ElementTags.NUMBERDEPTH, this.numberDepth );
        writeAddAttribute( ElementTags.DEPTH, this.depth );
        writeAddAttribute( ElementTags.INDENT, "0.0" );

        lineBreak();

        this.actionContext.setAction( SinkActionContext.SECTION_3 );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#sectionTitle3_()
     */
    public void sectionTitle3_()
    {
        writeEndElement(); // ElementTags.TITLE

        this.font.setSize( ITextFont.DEFAULT_FONT_SIZE );
        bold_();

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#sectionTitle3()
     */
    public void sectionTitle3()
    {
        this.font.setSize( ITextFont.getSectionFontSize( 3 ) );
        this.font.setColor( Color.BLACK );
        bold();

        writeStartElement( ElementTags.TITLE );
        writeAddAttribute( ElementTags.LEADING, DEFAULT_SECTION_TITLE_LEADING );
        writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

        this.actionContext.setAction( SinkActionContext.SECTION_TITLE_3 );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#section4_()
     */
    public void section4_()
    {
        writeEndElement(); // ElementTags.SECTION

        this.numberDepth--;
        this.depth = 1;

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#section4()
     */
    public void section4()
    {
        this.numberDepth++;
        this.depth = 1;

        writeStartElement( ElementTags.SECTION );
        writeAddAttribute( ElementTags.NUMBERDEPTH, this.numberDepth );
        writeAddAttribute( ElementTags.DEPTH, this.depth );
        writeAddAttribute( ElementTags.INDENT, "0.0" );

        lineBreak();

        this.actionContext.setAction( SinkActionContext.SECTION_4 );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#sectionTitle4_()
     */
    public void sectionTitle4_()
    {
        writeEndElement(); // ElementTags.TITLE

        this.font.setSize( ITextFont.DEFAULT_FONT_SIZE );
        bold_();

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#sectionTitle4()
     */
    public void sectionTitle4()
    {
        this.font.setSize( ITextFont.getSectionFontSize( 4 ) );
        this.font.setColor( Color.BLACK );
        bold();

        writeStartElement( ElementTags.TITLE );
        writeAddAttribute( ElementTags.LEADING, DEFAULT_SECTION_TITLE_LEADING );
        writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

        this.actionContext.setAction( SinkActionContext.SECTION_TITLE_4 );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#section5_()
     */
    public void section5_()
    {
        writeEndElement(); // ElementTags.SECTION

        this.numberDepth--;
        this.depth = 1;

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#section5()
     */
    public void section5()
    {
        this.numberDepth++;
        this.depth = 1;

        writeStartElement( ElementTags.SECTION );
        writeAddAttribute( ElementTags.NUMBERDEPTH, this.numberDepth );
        writeAddAttribute( ElementTags.DEPTH, this.depth );
        writeAddAttribute( ElementTags.INDENT, "0.0" );

        lineBreak();

        this.actionContext.setAction( SinkActionContext.SECTION_5 );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#sectionTitle5_()
     */
    public void sectionTitle5_()
    {
        writeEndElement(); // ElementTags.TITLE

        this.font.setSize( ITextFont.DEFAULT_FONT_SIZE );
        bold_();

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#sectionTitle5()
     */
    public void sectionTitle5()
    {
        this.font.setSize( ITextFont.getSectionFontSize( 5 ) );
        this.font.setColor( Color.BLACK );
        bold();

        writeStartElement( ElementTags.TITLE );
        writeAddAttribute( ElementTags.LEADING, DEFAULT_SECTION_TITLE_LEADING );
        writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

        this.actionContext.setAction( SinkActionContext.SECTION_TITLE_5 );
    }

    // ----------------------------------------------------------------------
    // Paragraph
    // ----------------------------------------------------------------------
    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#paragraph_()
     */
    public void paragraph_()
    {
        // Special case
        if ( ( this.actionContext.getCurrentAction() == SinkActionContext.LIST_ITEM )
            || ( this.actionContext.getCurrentAction() == SinkActionContext.NUMBERED_LIST_ITEM )
            || ( this.actionContext.getCurrentAction() == SinkActionContext.DEFINITION ) )
        {
            return;
        }

        writeEndElement(); // ElementTags.PARAGRAPH

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#paragraph()
     */
    public void paragraph()
    {
        // Special case
        if ( ( this.actionContext.getCurrentAction() == SinkActionContext.LIST_ITEM )
            || ( this.actionContext.getCurrentAction() == SinkActionContext.NUMBERED_LIST_ITEM )
            || ( this.actionContext.getCurrentAction() == SinkActionContext.DEFINITION ) )
        {
            return;
        }

        writeStartElement( ElementTags.PARAGRAPH );

        writeStartElement( ElementTags.NEWLINE );
        writeEndElement();

        this.actionContext.setAction( SinkActionContext.PARAGRAPH );
    }

    // ----------------------------------------------------------------------
    // Lists
    // ----------------------------------------------------------------------
    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#list_()
     */
    public void list_()
    {
        writeEndElement(); // ElementTags.LIST

        writeEndElement(); // ElementTags.CHUNK

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#list()
     */
    public void list()
    {
        writeStartElement( ElementTags.CHUNK );
        writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

        writeStartElement( ElementTags.LIST );
        writeAddAttribute( ElementTags.NUMBERED, Boolean.FALSE.toString() );
        writeAddAttribute( ElementTags.SYMBOLINDENT, "15" );

        this.actionContext.setAction( SinkActionContext.LIST );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#listItem_()
     */
    public void listItem_()
    {
        writeEndElement(); // ElementTags.LISTITEM

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#listItem()
     */
    public void listItem()
    {
        writeStartElement( ElementTags.LISTITEM );
        writeAddAttribute( ElementTags.INDENTATIONLEFT, "20.0" );

        this.actionContext.setAction( SinkActionContext.LIST_ITEM );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#numberedList_()
     */
    public void numberedList_()
    {
        writeEndElement(); // ElementTags.LIST

        writeEndElement(); // ElementTags.CHUNK

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#numberedList(int)
     */
    public void numberedList( int numbering )
    {
        writeStartElement( ElementTags.CHUNK );
        writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

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

        this.actionContext.setAction( SinkActionContext.NUMBERED_LIST );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#numberedListItem_()
     */
    public void numberedListItem_()
    {
        writeEndElement(); // ElementTags.LISTITEM

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#numberedListItem()
     */
    public void numberedListItem()
    {
        writeStartElement( ElementTags.LISTITEM );
        writeAddAttribute( ElementTags.INDENTATIONLEFT, "20" );

        this.actionContext.setAction( SinkActionContext.NUMBERED_LIST_ITEM );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#definitionList_()
     */
    public void definitionList_()
    {
        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#definitionList()
     */
    public void definitionList()
    {
        this.actionContext.setAction( SinkActionContext.DEFINITION_LIST );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#definedTerm_()
     */
    public void definedTerm_()
    {
        writeEndElement(); // ElementTags.CELL

        writeEndElement(); // ElementTags.ROW

        writeEndElement(); // ElementTags.TABLE

        writeEndElement(); // ElementTags.CHUNK

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#definedTerm()
     */
    public void definedTerm()
    {
        writeStartElement( ElementTags.CHUNK );
        writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

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

        this.actionContext.setAction( SinkActionContext.DEFINED_TERM );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#definition_()
     */
    public void definition_()
    {
        writeEndElement(); // ElementTags.CELL

        writeEndElement(); // ElementTags.ROW

        writeEndElement(); // ElementTags.TABLE

        writeEndElement(); // ElementTags.CHUNK

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#definition()
     */
    public void definition()
    {
        writeStartElement( ElementTags.CHUNK );
        writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

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

        this.actionContext.setAction( SinkActionContext.DEFINITION );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#definitionListItem_()
     */
    public void definitionListItem_()
    {
        // nop
        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#definitionListItem()
     */
    public void definitionListItem()
    {
        // nop

        this.actionContext.setAction( SinkActionContext.DEFINITION_LIST_ITEM );
    }

    // ----------------------------------------------------------------------
    // Tables
    // ----------------------------------------------------------------------
    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#table_()
     */
    public void table_()
    {
        writeEndElement(); // ElementTags.TABLE

        writeEndElement(); // ElementTags.CHUNK

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#table()
     */
    public void table()
    {
        writeStartElement( ElementTags.CHUNK );
        writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

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

        this.actionContext.setAction( SinkActionContext.TABLE );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#tableCaption_()
     */
    public void tableCaption_()
    {
        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#tableCaption()
     */
    public void tableCaption()
    {
        this.actionContext.setAction( SinkActionContext.TABLE_CAPTION );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#tableCell_()
     */
    public void tableCell_()
    {
        writeEndElement(); // ElementTags.CELL

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#tableCell()
     */
    public void tableCell()
    {
        writeStartElement( ElementTags.CELL );
        writeAddAttribute( ElementTags.LEFT, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.RIGHT, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.TOP, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.BOTTOM, Boolean.TRUE.toString() );
        writeAddAttribute( ElementTags.HORIZONTALALIGN, ElementTags.ALIGN_LEFT );

        this.actionContext.setAction( SinkActionContext.TABLE_CELL );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#tableCell(java.lang.String)
     */
    public void tableCell( String width )
    {
        this.actionContext.setAction( SinkActionContext.TABLE_CELL );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#tableHeaderCell_()
     */
    public void tableHeaderCell_()
    {
        writeEndElement(); // ElementTags.CELL

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#tableHeaderCell()
     */
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

        this.actionContext.setAction( SinkActionContext.TABLE_HEADER_CELL );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#tableHeaderCell(java.lang.String)
     */
    public void tableHeaderCell( String width )
    {
        this.actionContext.setAction( SinkActionContext.TABLE_HEADER_CELL );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#tableRow_()
     */
    public void tableRow_()
    {
        writeEndElement(); // ElementTags.ROW

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#tableRow()
     */
    public void tableRow()
    {
        writeStartElement( ElementTags.ROW );

        this.actionContext.setAction( SinkActionContext.TABLE_ROW );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#tableRows_()
     */
    public void tableRows_()
    {
        //writeEndElement(); // ElementTags.TABLE

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#tableRows(int[], boolean)
     */
    public void tableRows( int[] justification, boolean grid )
    {
        // ElementTags.TABLE
        writeAddAttribute( ElementTags.COLUMNS, justification.length );

        this.actionContext.setAction( SinkActionContext.TABLE_ROWS );
    }

    // ----------------------------------------------------------------------
    // Verbatim
    // ----------------------------------------------------------------------
    /**
     * @see org.apache.maven.doxia.sink.Sink#verbatim_()
     */
    public void verbatim_()
    {
        writeEndElement(); // ElementTags.CELL

        writeEndElement(); // ElementTags.ROW

        writeEndElement(); // ElementTags.TABLE

        writeEndElement(); // ElementTags.CHUNK

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.Sink#verbatim(boolean)
     */
    public void verbatim( boolean boxed )
    {
        // Always boxed
        writeStartElement( ElementTags.CHUNK );
        writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

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

        this.actionContext.setAction( SinkActionContext.VERBATIM );
    }

    // ----------------------------------------------------------------------
    // Figures
    // ----------------------------------------------------------------------
    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#figure_()
     */
    public void figure_()
    {
        writeEndElement(); // ElementTags.IMAGE

        writeEndElement(); // ElementTags.CHUNK

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#figure()
     */
    public void figure()
    {
        writeStartElement( ElementTags.CHUNK );
        writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

        writeStartElement( ElementTags.IMAGE );

        this.actionContext.setAction( SinkActionContext.FIGURE );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#figureCaption_()
     */
    public void figureCaption_()
    {
        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#figureCaption()
     */
    public void figureCaption()
    {
        this.actionContext.setAction( SinkActionContext.FIGURE_CAPTION );
    }

    /**
     * @see org.apache.maven.doxia.sink.Sink#figureGraphics(java.lang.String)
     */
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

        this.actionContext.setAction( SinkActionContext.FIGURE_GRAPHICS );
    }

    // ----------------------------------------------------------------------
    // Fonts
    // ----------------------------------------------------------------------
    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#bold_()
     */
    public void bold_()
    {
        this.font.removeBold();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#bold()
     */
    public void bold()
    {
        this.font.addBold();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#italic_()
     */
    public void italic_()
    {
        this.font.removeItalic();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#italic()
     */
    public void italic()
    {
        this.font.addItalic();
    }

    /**
     * @see org.apache.maven.doxia.sink.Sink#monospaced_()
     */
    public void monospaced_()
    {
        this.font.setMonoSpaced( false );
    }

    /**
     * @see org.apache.maven.doxia.sink.Sink#monospaced()
     */
    public void monospaced()
    {
        this.font.setMonoSpaced( true );
    }

    // ----------------------------------------------------------------------
    // Links
    // ----------------------------------------------------------------------
    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#link_()
     */
    public void link_()
    {
        writeEndElement(); // ElementTags.ANCHOR

        this.font.setColor( Color.BLACK );
        this.font.removeUnderlined();

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#link(java.lang.String)
     */
    public void link( String name )
    {
        this.font.setColor( Color.BLUE );
        this.font.addUnderlined();

        writeStartElement( ElementTags.ANCHOR );
        writeAddAttribute( ElementTags.REFERENCE, HtmlTools.escapeHTML( name ) );
        writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

        this.actionContext.setAction( SinkActionContext.LINK );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#anchor_()
     */
    public void anchor_()
    {
        writeEndElement(); // ElementTags.ANCHOR

        this.actionContext.release();
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#anchor(java.lang.String)
     */
    public void anchor( String name )
    {
        writeStartElement( ElementTags.ANCHOR );
        writeAddAttribute( ElementTags.NAME, name );

        this.actionContext.setAction( SinkActionContext.ANCHOR );
    }

    // ----------------------------------------------------------------------
    // Misc
    // ----------------------------------------------------------------------
    /**
     * @see org.apache.maven.doxia.sink.Sink#lineBreak()
     */
    public void lineBreak()
    {
        // Special case for the header
        if ( ( this.actionContext.getCurrentAction() == SinkActionContext.AUTHOR )
            || ( this.actionContext.getCurrentAction() == SinkActionContext.DATE )
            || ( this.actionContext.getCurrentAction() == SinkActionContext.TITLE ) )
        {
            return;
        }

        writeStartElement( ElementTags.NEWLINE );
        writeEndElement();
    }

    /**
     * @see org.apache.maven.doxia.sink.Sink#nonBreakingSpace()
     */
    public void nonBreakingSpace()
    {
        write( " " );
    }

    /**
     * @see org.apache.maven.doxia.sink.SinkAdapter#pageBreak()
     */
    public void pageBreak()
    {
        writeStartElement( ElementTags.NEWPAGE );
        writeEndElement();
    }

    /**
     * @see org.apache.maven.doxia.sink.Sink#horizontalRule()
     */
    public void horizontalRule()
    {
        writeStartElement( ElementTags.HORIZONTALRULE );
        writeEndElement();
    }

    // ----------------------------------------------------------------------
    // Text
    // ----------------------------------------------------------------------
    /**
     * @see org.apache.maven.doxia.sink.Sink#rawText(java.lang.String)
     */
    public void rawText( String text )
    {
        writeStartElement( ElementTags.CHUNK );
        writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
        writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
        writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
        writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
        writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
        writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

        write( text, false );

        writeEndElement(); // ElementTags.CHUNK
    }

    /**
     * @see org.apache.maven.doxia.sink.Sink#text(java.lang.String)
     */
    public void text( String text )
    {
        switch ( this.actionContext.getCurrentAction() )
        {
            case SinkActionContext.UNDEFINED:
                break;

            case SinkActionContext.HEAD:
                break;

            case SinkActionContext.AUTHOR:
                this.header.addAuthor( text );
                break;

            case SinkActionContext.DATE:
                this.header.setDate( text );
                break;

            case SinkActionContext.TITLE:
                this.header.setTitle( text );
                break;

            case SinkActionContext.SECTION_TITLE_1:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.SECTION_TITLE_2:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.SECTION_TITLE_3:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.SECTION_TITLE_4:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.SECTION_TITLE_5:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.LIST_ITEM:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.NUMBERED_LIST_ITEM:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.DEFINED_TERM:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.DEFINITION:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.TABLE:
                break;

            case SinkActionContext.TABLE_ROW:
                break;

            case SinkActionContext.TABLE_HEADER_CELL:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.TABLE_CELL:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

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
                        writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
                        writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
                        writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
                        writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
                        writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
                        writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

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
                writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

                write( text );

                writeEndElement(); // ElementTags.CHUNK
                break;

            case SinkActionContext.ANCHOR:
                writeStartElement( ElementTags.CHUNK );
                writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

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
                writeAddAttribute( ElementTags.FONT, this.font.getFontName() );
                writeAddAttribute( ElementTags.SIZE, this.font.getFontSize() );
                writeAddAttribute( ElementTags.STYLE, this.font.getFontStyle() );
                writeAddAttribute( ElementTags.BLUE, this.font.getFontColorBlue() );
                writeAddAttribute( ElementTags.GREEN, this.font.getFontColorGreen() );
                writeAddAttribute( ElementTags.RED, this.font.getFontColorRed() );

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
        this.xmlWriter.startElement( tag );
    }

    /**
     * Convenience method to write a key-value pair.
     *
     * @param key the name of an attribute
     * @param value the value of an attribute
     */
    private void writeAddAttribute( String key, String value )
    {
        this.xmlWriter.addAttribute( key, value );
    }

    /**
     * Convenience method to write a key-value pair.
     *
     * @param key the name of an attribute
     * @param value the value of an attribute
     */
    private void writeAddAttribute( String key, int value )
    {
        this.xmlWriter.addAttribute( key, String.valueOf( value ) );
    }

    /**
     * Convenience method to write an end element.
     */
    private void writeEndElement()
    {
        this.xmlWriter.endElement();
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
            this.xmlWriter.writeMarkup( aString );
        }
        else
        {
            this.xmlWriter.writeText( aString );
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
}