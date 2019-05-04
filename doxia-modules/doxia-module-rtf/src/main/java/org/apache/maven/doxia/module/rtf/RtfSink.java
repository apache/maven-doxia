package org.apache.maven.doxia.module.rtf;

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

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.AbstractTextSink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;

/**
 * <a href="http://en.wikipedia.org/wiki/Rich_Text_Format">RTF</a> Sink implementation.
 *
 * @version $Id$
 * @since 1.0
 */
public class RtfSink
    extends AbstractTextSink
{
    /** Paper width, 21 cm */
    public static final double DEFAULT_PAPER_WIDTH = 21.;   /*cm*/

    /** Paper height, 29.7 cm */
    public static final double DEFAULT_PAPER_HEIGHT = 29.7; /*cm*/

    /** Paper top margin, 2 cm */
    public static final double DEFAULT_TOP_MARGIN = 2.;    /*cm*/

    /** Paper bottom margin, 2 cm */
    public static final double DEFAULT_BOTTOM_MARGIN = 2.; /*cm*/

    /** Paper left margin, 2 cm */
    public static final double DEFAULT_LEFT_MARGIN = 2.;   /*cm*/

    /** Paper right margin, 2 cm */
    public static final double DEFAULT_RIGHT_MARGIN = 2.;  /*cm*/

    /** Font size, 10 pts */
    public static final int DEFAULT_FONT_SIZE = 10; /*pts*/

    /** Spacing, 10 pts */
    public static final int DEFAULT_SPACING = 10;   /*pts*/

    /** Resolution, 72 dpi */
    public static final int DEFAULT_RESOLUTION = 72; /*dpi*/

    /** Image format, bmp */
    public static final String DEFAULT_IMAGE_FORMAT = "bmp";

    /** Image type, palette */
    public static final String DEFAULT_IMAGE_TYPE = "palette";

    /** Data format, ascii */
    public static final String DEFAULT_DATA_FORMAT = "ascii";

    /** Codepage, 1252 */
    public static final int DEFAULT_CODE_PAGE = 1252;

    /** Constant <code>DEFAULT_CHAR_SET=0</code> */
    public static final int DEFAULT_CHAR_SET = 0;

    /** Constant <code>IMG_FORMAT_BMP="bmp"</code> */
    public static final String IMG_FORMAT_BMP = "bmp";

    /** Constant <code>IMG_FORMAT_WMF="wmf"</code> */
    public static final String IMG_FORMAT_WMF = "wmf";

    /** Constant <code>IMG_TYPE_PALETTE="palette"</code> */
    public static final String IMG_TYPE_PALETTE = "palette";

    /** Constant <code>IMG_TYPE_RGB="rgb"</code> */
    public static final String IMG_TYPE_RGB = "rgb";

    /** Constant <code>IMG_DATA_ASCII="ascii"</code> */
    public static final String IMG_DATA_ASCII = "ascii";

    /** Constant <code>IMG_DATA_RAW="raw"</code> */
    public static final String IMG_DATA_RAW = "raw";

    /** Constant <code>STYLE_ROMAN=0</code> */
    public static final int STYLE_ROMAN = 0;

    /** Constant <code>STYLE_ITALIC=1</code> */
    public static final int STYLE_ITALIC = 1;

    /** Constant <code>STYLE_BOLD=2</code> */
    public static final int STYLE_BOLD = 2;

    /** Constant <code>STYLE_TYPEWRITER=3</code> */
    public static final int STYLE_TYPEWRITER = 3;

    private static final int CONTEXT_UNDEFINED = 0;

    private static final int CONTEXT_VERBATIM = 1;

    private static final int CONTEXT_TABLE = 2;

    private static final int UNIT_MILLIMETER = 1;

    private static final int UNIT_CENTIMETER = 2;

    private static final int UNIT_INCH = 3;

    private static final int UNIT_PIXEL = 4;

    private static final int LIST_INDENT = 300; /*twips*/

    private static final String LIST_ITEM_HEADER = "-  ";

    private static final int DEFINITION_INDENT = 300; /*twips*/

    private static final int CELL_HORIZONTAL_PAD = 60; /*twips*/

    private static final int CELL_VERTICAL_PAD = 20;   /*twips*/

    private static final int BORDER_WIDTH = 15; /*twips*/

    private double paperWidth = DEFAULT_PAPER_WIDTH;

    private double paperHeight = DEFAULT_PAPER_HEIGHT;

    private double topMargin = DEFAULT_TOP_MARGIN;

    private double bottomMargin = DEFAULT_BOTTOM_MARGIN;

    private double leftMargin = DEFAULT_LEFT_MARGIN;

    private double rightMargin = DEFAULT_RIGHT_MARGIN;

    protected int fontSize = DEFAULT_FONT_SIZE;

    private int resolution = DEFAULT_RESOLUTION;

    private String imageFormat = DEFAULT_IMAGE_FORMAT;

    private String imageType = DEFAULT_IMAGE_TYPE;

    private String imageDataFormat = DEFAULT_DATA_FORMAT;

    private boolean imageCompression = true;

    private int codePage = DEFAULT_CODE_PAGE;

    private int charSet = DEFAULT_CHAR_SET;

    private final Hashtable fontTable;

    private Context context;

    private Paragraph paragraph;

    protected Indentation indentation;

    protected Space space;

    private int listItemIndent;

    private final Vector numbering;

    private final Vector itemNumber;

    private int style = STYLE_ROMAN;

    private int sectionLevel;

    private boolean emptyHeader;

    private StringBuilder verbatim;

    private boolean frame;

    private Table table;

    private Row row;

    private Cell cell;

    private Line line;

    protected PrintWriter writer;

    protected OutputStream stream; // for raw image data

    /** Keep track of the closing tags for inline events. */
    protected Stack<List<Integer>> inlineStack = new Stack<>();

    /** Map of warn messages with a String as key to describe the error type and a Set as value.
     * Using to reduce warn messages. */
    private Map warnMessages;

    // -----------------------------------------------------------------------

    /**
     * <p>Constructor for RtfSink.</p>
     *
     * @throws java.io.IOException if any
     */
    protected RtfSink()
        throws IOException
    {
        this( System.out );
    }

    /**
     * <p>Constructor for RtfSink.</p>
     *
     * @param output not null
     * @throws java.io.IOException if any
     */
    protected RtfSink( OutputStream output )
        throws IOException
    {
        this( output, null );
    }

    /**
     * <p>Constructor for RtfSink.</p>
     *
     * @param output not null
     * @param encoding a valid charset
     * @throws java.io.IOException if any
     */
    protected RtfSink( OutputStream output, String encoding )
        throws IOException
    {
        this.fontTable = new Hashtable();
        this.numbering = new Vector();
        this.itemNumber = new Vector();

        Writer w;
        this.stream = new BufferedOutputStream( output );
        // TODO: encoding should be consistent with codePage
        if ( encoding != null )
        {
            w = new OutputStreamWriter( stream, encoding );
        }
        else
        {
            w = new OutputStreamWriter( stream );
        }
        this.writer = new PrintWriter( new BufferedWriter( w ) );

        init();
    }

    /**
     * setPaperSize.
     *
     * @param width in cm.
     * @param height in cm.
     */
    public void setPaperSize( double width /*cm*/, double height /*cm*/ )
    {
        paperWidth = width;
        paperHeight = height;
    }

    /**
     * <p>Setter for the field <code>topMargin</code>.</p>
     *
     * @param margin margin.
     */
    public void setTopMargin( double margin )
    {
        topMargin = margin;
    }

    /**
     * <p>Setter for the field <code>bottomMargin</code>.</p>
     *
     * @param margin margin.
     */
    public void setBottomMargin( double margin )
    {
        bottomMargin = margin;
    }

    /**
     * <p>Setter for the field <code>leftMargin</code>.</p>
     *
     * @param margin margin
     */
    public void setLeftMargin( double margin )
    {
        leftMargin = margin;
    }

    /**
     * <p>Setter for the field <code>rightMargin</code>.</p>
     *
     * @param margin margin
     */
    public void setRightMargin( double margin )
    {
        rightMargin = margin;
    }

    /**
     * <p>Setter for the field <code>fontSize</code>.</p>
     *
     * @param size in pts
     */
    public void setFontSize( int size /*pts*/ )
    {
        fontSize = size;
    }

    /**
     * <p>setSpacing.</p>
     *
     * @param spacing in pts.
     */
    public void setSpacing( int spacing /*pts*/ )
    {
        space.set( 20 * spacing );
    }

    /**
     * <p>Setter for the field <code>resolution</code>.</p>
     *
     * @param resolution in dpi
     */
    public void setResolution( int resolution /*dpi*/ )
    {
        this.resolution = resolution;
    }

    /**
     * <p>Setter for the field <code>imageFormat</code>.</p>
     *
     * @param format
     */
    public void setImageFormat( String format )
    {
        imageFormat = format;
    }

    /**
     * <p>Setter for the field <code>imageType</code>.</p>
     *
     * @param type
     */
    public void setImageType( String type )
    {
        imageType = type;
    }

    /**
     * <p>Setter for the field <code>imageDataFormat</code>.</p>
     *
     * @param format
     */
    public void setImageDataFormat( String format )
    {
        imageDataFormat = format;
    }

    /**
     * <p>Setter for the field <code>imageCompression</code>.</p>
     *
     * @param compression
     */
    public void setImageCompression( boolean compression )
    {
        imageCompression = compression;
    }

    /**
     * <p>Setter for the field <code>codePage</code>.</p>
     *
     * @param cp
     */
    public void setCodePage( int cp )
    {
        codePage = cp;
    }

    /**
     * <p>Setter for the field <code>charSet</code>.</p>
     *
     * @param cs
     */
    public void setCharSet( int cs )
    {
        charSet = cs;
    }

    /** {@inheritDoc} */
    public void head()
    {
        init();

        writer.println( "{\\rtf1\\ansi\\ansicpg" + codePage + "\\deff0" );

        writer.println( "{\\fonttbl" );
        writer.println( "{\\f0\\froman\\fcharset" + charSet + " Times;}" );
        writer.println( "{\\f1\\fmodern\\fcharset" + charSet + " Courier;}" );
        writer.println( "}" );

        writer.println( "{\\stylesheet" );
        for ( int level = 1; level <= 5; ++level )
        {
            writer.print( "{\\s" + styleNumber( level ) );
            writer.print( "\\outlinelevel" + level );
            writer.print( " Section Title " + level );
            writer.println( ";}" );
        }
        writer.println( "}" );

        writer.println( "\\paperw" + toTwips( paperWidth, UNIT_CENTIMETER ) );
        writer.println( "\\paperh" + toTwips( paperHeight, UNIT_CENTIMETER ) );
        writer.println( "\\margl" + toTwips( leftMargin, UNIT_CENTIMETER ) );
        writer.println( "\\margr" + toTwips( rightMargin, UNIT_CENTIMETER ) );
        writer.println( "\\margt" + toTwips( topMargin, UNIT_CENTIMETER ) );
        writer.println( "\\margb" + toTwips( bottomMargin, UNIT_CENTIMETER ) );

        space.set( space.get() / 2 );
        space.setNext( 0 );

        emptyHeader = true;
    }

    /** {@inheritDoc} */
    public void head_()
    {
        space.restore();
        if ( emptyHeader )
        {
            space.setNext( 0 );
        }
        else
        {
            space.setNext( 2 * space.get() );
        }
    }

    /**
     * <p>toTwips.</p>
     *
     * @param length a double.
     * @param unit a int.
     * @return a int.
     */
    protected int toTwips( double length, int unit )
    {
        double points;

        switch ( unit )
        {
            case UNIT_MILLIMETER:
                points = ( length / 25.4 ) * 72.;
                break;
            case UNIT_CENTIMETER:
                points = ( length / 2.54 ) * 72.;
                break;
            case UNIT_INCH:
                points = length * 72.;
                break;
            case UNIT_PIXEL:
            default:
                points = ( length / resolution ) * 72.;
                break;
        }

        return (int) Math.rint( points * 20. );
    }

    /** {@inheritDoc} */
    public void title()
    {
        Paragraph p = new Paragraph( STYLE_BOLD, fontSize + 6 );
        p.justification = Sink.JUSTIFY_CENTER;
        beginParagraph( p );
        emptyHeader = false;
    }

    /** {@inheritDoc} */
    public void title_()
    {
        endParagraph();
    }

    /** {@inheritDoc} */
    public void author()
    {
        Paragraph p = new Paragraph( STYLE_ROMAN, fontSize + 2 );
        p.justification = Sink.JUSTIFY_CENTER;
        beginParagraph( p );
        emptyHeader = false;
    }

    /** {@inheritDoc} */
    public void author_()
    {
        endParagraph();
    }

    /** {@inheritDoc} */
    public void date()
    {
        Paragraph p = new Paragraph( STYLE_ROMAN, fontSize );
        p.justification = Sink.JUSTIFY_CENTER;
        beginParagraph( p );
        emptyHeader = false;
    }

    /** {@inheritDoc} */
    public void date_()
    {
        endParagraph();
    }

    /** {@inheritDoc} */
    public void body()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void body_()
    {
        writer.println( "}" );
        writer.flush();
    }

    /** {@inheritDoc} */
    public void section1()
    {
        sectionLevel = 1;
    }

    /** {@inheritDoc} */
    public void section1_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void section2()
    {
        sectionLevel = 2;
    }

    /** {@inheritDoc} */
    public void section2_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void section3()
    {
        sectionLevel = 3;
    }

    /** {@inheritDoc} */
    public void section3_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void section4()
    {
        sectionLevel = 4;
    }

    /** {@inheritDoc} */
    public void section4_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void section5()
    {
        sectionLevel = 5;
    }

    /** {@inheritDoc} */
    public void section5_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void sectionTitle()
    {
        int stl = STYLE_BOLD;
        int size = fontSize;

        switch ( sectionLevel )
        {
            case 1:
                size = fontSize + 6;
                break;
            case 2:
                size = fontSize + 4;
                break;
            case 3:
                size = fontSize + 2;
                break;
            case 4:
                break;
            case 5:
                stl = STYLE_ROMAN;
                break;
            default:
        }

        Paragraph p = new Paragraph( stl, size );
        p.style = styleNumber( sectionLevel );

        beginParagraph( p );
    }

    /** {@inheritDoc} */
    public void sectionTitle_()
    {
        endParagraph();
    }

    private int styleNumber( int level )
    {
        return level;
    }

    /** {@inheritDoc} */
    public void list()
    {
        indentation.add( LIST_INDENT );
        space.set( space.get() / 2 );
    }

    /** {@inheritDoc} */
    public void list_()
    {
        indentation.restore();
        space.restore();
    }

    /** {@inheritDoc} */
    public void listItem()
    {
        Paragraph p = new Paragraph();
        p.leftIndent = indentation.get() + listItemIndent;
        p.firstLineIndent = ( -listItemIndent );
        beginParagraph( p );

        beginStyle( STYLE_BOLD );
        writer.println( LIST_ITEM_HEADER );
        endStyle();

        indentation.add( listItemIndent );
        space.set( space.get() / 2 );
    }

    /** {@inheritDoc} */
    public void listItem_()
    {
        endParagraph();

        indentation.restore();
        space.restore();
    }

    /** {@inheritDoc} */
    public void numberedList( int numbering )
    {
        this.numbering.addElement( numbering );
        itemNumber.addElement( new Counter( 0 ) );

        indentation.add( LIST_INDENT );
        space.set( space.get() / 2 );
    }

    /** {@inheritDoc} */
    public void numberedList_()
    {
        numbering.removeElementAt( numbering.size() - 1 );
        itemNumber.removeElementAt( itemNumber.size() - 1 );

        indentation.restore();
        space.restore();
    }

    /** {@inheritDoc} */
    public void numberedListItem()
    {
        ( (Counter) itemNumber.lastElement() ).increment();

        int indent = 0;
        String header = getItemHeader();
        Font font = getFont( STYLE_TYPEWRITER, fontSize );
        if ( font != null )
        {
            indent = textWidth( header, font );
        }

        Paragraph p = new Paragraph();
        p.leftIndent = indentation.get() + indent;
        p.firstLineIndent = ( -indent );
        beginParagraph( p );

        beginStyle( STYLE_TYPEWRITER );
        writer.println( header );
        endStyle();

        indentation.add( indent );
        space.set( space.get() / 2 );
    }

    /** {@inheritDoc} */
    public void numberedListItem_()
    {
        endParagraph();

        indentation.restore();
        space.restore();
    }

    private String getItemHeader()
    {
        int nmb = (Integer) this.numbering.lastElement();
        int iNmb = ( (Counter) this.itemNumber.lastElement() ).get();
        StringBuilder buf = new StringBuilder();

        switch ( nmb )
        {
            case Sink.NUMBERING_DECIMAL:
            default:
                buf.append( iNmb );
                buf.append( ". " );
                while ( buf.length() < 4 )
                {
                    buf.append( ' ' );
                }
                break;

            case Sink.NUMBERING_LOWER_ALPHA:
                buf.append( AlphaNumerals.toString( iNmb, true ) );
                buf.append( ") " );
                break;

            case Sink.NUMBERING_UPPER_ALPHA:
                buf.append( AlphaNumerals.toString( iNmb, false ) );
                buf.append( ". " );
                break;

            case Sink.NUMBERING_LOWER_ROMAN:
                buf.append( RomanNumerals.toString( iNmb, true ) );
                buf.append( ") " );
                while ( buf.length() < 6 )
                {
                    buf.append( ' ' );
                }
                break;

            case Sink.NUMBERING_UPPER_ROMAN:
                buf.append( RomanNumerals.toString( iNmb, false ) );
                buf.append( ". " );
                while ( buf.length() < 6 )
                {
                    buf.append( ' ' );
                }
                break;
        }

        return buf.toString();
    }

    /** {@inheritDoc} */
    public void definitionList()
    {
        int next = space.getNext();

        indentation.add( LIST_INDENT );
        space.set( space.get() / 2 );
        space.setNext( next );
    }

    /** {@inheritDoc} */
    public void definitionList_()
    {
        indentation.restore();
        space.restore();
    }

    /** {@inheritDoc} */
    public void definitionListItem()
    {
        int next = space.getNext();
        space.set( space.get() / 2 );
        space.setNext( next );
    }

    /** {@inheritDoc} */
    public void definitionListItem_()
    {
        space.restore();
    }

    /** {@inheritDoc} */
    public void definedTerm()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void definedTerm_()
    {
        endParagraph();
    }

    /** {@inheritDoc} */
    public void definition()
    {
        int next = space.getNext();

        indentation.add( DEFINITION_INDENT );
        space.set( space.get() / 2 );
        space.setNext( next );
    }

    /** {@inheritDoc} */
    public void definition_()
    {
        endParagraph();

        indentation.restore();
        space.restore();
    }

    /** {@inheritDoc} */
    public void table()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void table_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void tableRows( int[] justification, boolean grid )

    {
        table = new Table( justification, grid );
        context.set( CONTEXT_TABLE );
    }

    /** {@inheritDoc} */
    public void tableRows_()
    {
        boolean bb = false;
        boolean br = false;

        int offset = ( pageWidth() - ( table.width() + indentation.get() ) ) / 2;
        int x0 = indentation.get() + offset;

        space.skip();

        for ( int i = 0; i < table.rows.size(); ++i )
        {
            Row r = (Row) table.rows.elementAt( i );

            writer.print( "\\trowd" );
            writer.print( "\\trleft" + x0 );
            writer.print( "\\trgaph" + CELL_HORIZONTAL_PAD );
            writer.println( "\\trrh" + r.height() );

            if ( table.grid )
            {
                if ( i == ( table.rows.size() - 1 ) )
                {
                    bb = true;
                }
                br = false;
            }

            for ( int j = 0, x = x0; j < table.numColumns; ++j )
            {
                if ( table.grid )
                {
                    if ( j == ( table.numColumns - 1 ) )
                    {
                        br = true;
                    }
                    setBorder( true, bb, true, br );
                    x += BORDER_WIDTH;
                }
                x += table.columnWidths[j];
                writer.println( "\\clvertalc\\cellx" + x );
            }

            for ( int j = 0; j < table.numColumns; ++j )
            {
                if ( j >= r.cells.size() )
                {
                    break;
                }
                Cell c = (Cell) r.cells.elementAt( j );

                writer.print( "\\pard\\intbl" );
                setJustification( table.justification[j] );
                writer.println( "\\plain\\f0\\fs" + ( 2 * fontSize ) );

                for ( int k = 0; k < c.lines.size(); ++k )
                {
                    if ( k > 0 )
                    {
                        writer.println( "\\line" );
                    }
                    Line l = (Line) c.lines.elementAt( k );

                    for ( int n = 0; n < l.items.size(); ++n )
                    {
                        Item item = (Item) l.items.elementAt( n );
                        writer.print( "{" );
                        setStyle( item.style );
                        writer.println( escape( item.text ) );
                        writer.println( "}" );
                    }
                }

                writer.println( "\\cell" );
            }

            writer.println( "\\row" );
        }

        context.restore();
    }

    private int pageWidth()
    {
        double width = paperWidth - ( leftMargin + rightMargin );
        return toTwips( width, UNIT_CENTIMETER );
    }

    private void setBorder( boolean bt, boolean bb, boolean bl, boolean br )
    {
        if ( bt )
        {
            writer.println( "\\clbrdrt\\brdrs\\brdrw" + BORDER_WIDTH );
        }
        if ( bb )
        {
            writer.println( "\\clbrdrb\\brdrs\\brdrw" + BORDER_WIDTH );
        }
        if ( bl )
        {
            writer.println( "\\clbrdrl\\brdrs\\brdrw" + BORDER_WIDTH );
        }
        if ( br )
        {
            writer.println( "\\clbrdrr\\brdrs\\brdrw" + BORDER_WIDTH );
        }
    }

    private void setJustification( int justification )
    {
        switch ( justification )
        {
            case Sink.JUSTIFY_LEFT:
            default:
                writer.println( "\\ql" );
                break;
            case Sink.JUSTIFY_CENTER:
                writer.println( "\\qc" );
                break;
            case Sink.JUSTIFY_RIGHT:
                writer.println( "\\qr" );
                break;
        }
    }

    private void setStyle( int style )
    {
        switch ( style )
        {
            case STYLE_ITALIC:
                writer.println( "\\i" );
                break;
            case STYLE_BOLD:
                writer.println( "\\b" );
                break;
            case STYLE_TYPEWRITER:
                writer.println( "\\f1" );
                break;
            default:
                break;
        }
    }

    /** {@inheritDoc} */
    public void tableRow()
    {
        row = new Row();
    }

    /** {@inheritDoc} */
    public void tableRow_()
    {
        table.add( row );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell()
    {
        tableCell();
    }

    /** {@inheritDoc} */
    public void tableHeaderCell_()
    {
        tableCell_();
    }

    /** {@inheritDoc} */
    public void tableCell()
    {
        cell = new Cell();
        line = new Line();
    }

    /** {@inheritDoc} */
    public void tableCell_()
    {
        cell.add( line );
        row.add( cell );
    }

    /** {@inheritDoc} */
    public void tableCaption()
    {
        Paragraph p = new Paragraph();
        p.justification = Sink.JUSTIFY_CENTER;
        p.spaceBefore /= 2;
        beginParagraph( p );
    }

    /** {@inheritDoc} */
    public void tableCaption_()
    {
        endParagraph();
    }

    /** {@inheritDoc} */
    public void paragraph()
    {
        if ( paragraph == null )
        {
            beginParagraph( new Paragraph() );
        }
    }

    /** {@inheritDoc} */
    public void paragraph_()
    {
        endParagraph();
    }

    private void beginParagraph( Paragraph p )
    {
        p.begin();
        this.paragraph = p;
        if ( style != STYLE_ROMAN )
        {
            beginStyle( style );
        }
    }

    private void endParagraph()
    {
        if ( paragraph != null )
        {
            if ( style != STYLE_ROMAN )
            {
                endStyle();
            }
            paragraph.end();
            paragraph = null;
        }
    }

    /** {@inheritDoc} */
    public void verbatim( boolean boxed )
    {
        verbatim = new StringBuilder();
        frame = boxed;

        context.set( CONTEXT_VERBATIM );
    }

    /** {@inheritDoc} */
    public void verbatim_()
    {
        String text = verbatim.toString();

        Paragraph p = new Paragraph();
        p.fontStyle = STYLE_TYPEWRITER;
        p.frame = frame;

        beginParagraph( p );

        StringTokenizer t = new StringTokenizer( text, EOL, true );
        while ( t.hasMoreTokens() )
        {
            String s = t.nextToken();
            if ( s.equals( EOL ) && t.hasMoreTokens() )
            {
                writer.println( "\\line" );
            }
            else
            {
                writer.println( escape( s ) );
            }
        }

        endParagraph();

        context.restore();
    }

    /** {@inheritDoc} */
    public void figure()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void figure_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void figureGraphics( String name )
    {
        Paragraph p = new Paragraph();
        p.justification = Sink.JUSTIFY_CENTER;
        beginParagraph( p );

        try
        {
            writeImage( name );
        }
        catch ( Exception e )
        {
            getLog().error( e.getMessage(), e );
        }

        endParagraph();
    }

    private void writeImage( String source )
        throws Exception
    {
        if ( !source.toLowerCase().endsWith( ".ppm" ) )
        {
            // TODO support more image types!
            String msg =
                "Unsupported image type for image file: '" + source + "'. Only PPM image type is "
                    + "currently supported.";
            logMessage( "unsupportedImage", msg );

            return;
        }

        int bytesPerLine;
        PBMReader ppm = new PBMReader( source );
        WMFWriter.Dib dib = new WMFWriter.Dib();
        WMFWriter wmf = new WMFWriter();

        int srcWidth = ppm.width();
        int srcHeight = ppm.height();

        dib.biWidth = srcWidth;
        dib.biHeight = srcHeight;
        dib.biXPelsPerMeter = (int) ( resolution * 100. / 2.54 );
        dib.biYPelsPerMeter = dib.biXPelsPerMeter;

        if ( imageType.equals( IMG_TYPE_RGB ) )
        {
            dib.biBitCount = 24;
            dib.biCompression = WMFWriter.Dib.BI_RGB; // no compression

            bytesPerLine = 4 * ( ( 3 * srcWidth + 3 ) / 4 );
            dib.bitmap = new byte[srcHeight * bytesPerLine];

            byte[] l = new byte[3 * srcWidth];
            for ( int i = ( srcHeight - 1 ); i >= 0; --i )
            {
                ppm.read( l, 0, l.length );
                for ( int j = 0, k = ( i * bytesPerLine ); j < l.length; j += 3 )
                {
                    // component order = BGR
                    dib.bitmap[k++] = l[j + 2];
                    dib.bitmap[k++] = l[j + 1];
                    dib.bitmap[k++] = l[j];
                }
            }
        }
        else
        {
            dib.biBitCount = 8;

            bytesPerLine = 4 * ( ( srcWidth + 3 ) / 4 );
            byte[] bitmap = new byte[srcHeight * bytesPerLine];

            Vector colors = new Vector( 256 );
            colors.addElement( Color.white );
            colors.addElement( Color.black );

            byte[] l = new byte[3 * srcWidth];
            for ( int i = ( srcHeight - 1 ); i >= 0; --i )
            {
                ppm.read( l, 0, l.length );
                for ( int j = 0, k = ( i * bytesPerLine ); j < l.length; )
                {
                    int r = (int) l[j++] & 0xff;
                    int g = (int) l[j++] & 0xff;
                    int b = (int) l[j++] & 0xff;
                    Color color = new Color( r, g, b );
                    int index = colors.indexOf( color );
                    if ( index < 0 )
                    {
                        if ( colors.size() < colors.capacity() )
                        {
                            colors.addElement( color );
                            index = colors.size() - 1;
                        }
                        else
                        {
                            index = 1;
                        }
                    }
                    bitmap[k++] = (byte) index;
                }
            }

            dib.biClrUsed = colors.size();
            dib.biClrImportant = dib.biClrUsed;
            dib.palette = new byte[4 * dib.biClrUsed];
            for ( int i = 0, j = 0; i < dib.biClrUsed; ++i, ++j )
            {
                Color color = (Color) colors.elementAt( i );
                dib.palette[j++] = (byte) color.getBlue();
                dib.palette[j++] = (byte) color.getGreen();
                dib.palette[j++] = (byte) color.getRed();
            }

            if ( imageCompression )
            {
                dib.biCompression = WMFWriter.Dib.BI_RLE8;
                dib.bitmap = new byte[bitmap.length + ( 2 * ( bitmap.length / 255 + 1 ) )];
                dib.biSizeImage = WMFWriter.Dib.rlEncode8( bitmap, 0, bitmap.length, dib.bitmap, 0 );
            }
            else
            {
                dib.biCompression = WMFWriter.Dib.BI_RGB;
                dib.bitmap = bitmap;
            }
        }

        if ( imageFormat.equals( IMG_FORMAT_WMF ) )
        {
            int[] parameters;
            WMFWriter.Record record;

            /*
             * See the libwmf library documentation
             * (http://www.wvware.com/wmf_doc_index.html)
             * for a description of WMF records.
             */

            // set mapping mode to MM_TEXT (logical unit = pixel)
            parameters = new int[1];
            parameters[0] = 1;
            record = new WMFWriter.Record( 0x0103, parameters );
            wmf.add( record );

            // set window origin and dimensions
            parameters = new int[2];
            record = new WMFWriter.Record( 0x020b, parameters );
            wmf.add( record );
            parameters = new int[2];
            parameters[0] = srcHeight;
            parameters[1] = srcWidth;
            record = new WMFWriter.Record( 0x020c, parameters );
            wmf.add( record );

            parameters = new int[WMFWriter.DibBitBltRecord.P_COUNT];
            // raster operation = SRCCOPY (0x00cc0020)
            parameters[WMFWriter.DibBitBltRecord.P_ROP_H] = 0x00cc;
            parameters[WMFWriter.DibBitBltRecord.P_ROP_L] = 0x0020;
            parameters[WMFWriter.DibBitBltRecord.P_WIDTH] = srcWidth;
            parameters[WMFWriter.DibBitBltRecord.P_HEIGHT] = srcHeight;
            record = new WMFWriter.DibBitBltRecord( parameters, dib );
            wmf.add( record );
        }

        if ( imageFormat.equals( IMG_FORMAT_WMF ) )
        {
            writer.print( "{\\pict\\wmetafile1" );
            writer.println( "\\picbmp\\picbpp" + dib.biBitCount );
        }
        else
        {
            writer.print( "{\\pict\\dibitmap0\\wbmplanes1" );
            writer.print( "\\wbmbitspixel" + dib.biBitCount );
            writer.println( "\\wbmwidthbytes" + bytesPerLine );
        }

        writer.print( "\\picw" + srcWidth );
        writer.print( "\\pich" + srcHeight );
        writer.print( "\\picwgoal" + toTwips( srcWidth, UNIT_PIXEL ) );
        writer.println( "\\pichgoal" + toTwips( srcHeight, UNIT_PIXEL ) );

        if ( imageFormat.equals( IMG_FORMAT_WMF ) )
        {
            if ( imageDataFormat.equals( IMG_DATA_RAW ) )
            {
                writer.print( "\\bin" + ( 2 * wmf.size() ) + " " );
                writer.flush();
                wmf.write( stream );
                stream.flush();
            }
            else
            {
                wmf.print( writer );
            }
        }
        else
        {
            if ( imageDataFormat.equals( IMG_DATA_RAW ) )
            {
                writer.print( "\\bin" + ( 2 * dib.size() ) + " " );
                writer.flush();
                dib.write( stream );
                stream.flush();
            }
            else
            {
                dib.print( writer );
            }
        }

        writer.println( "}" );
    }

    /** {@inheritDoc} */
    public void figureCaption()
    {
        Paragraph p = new Paragraph();
        p.justification = Sink.JUSTIFY_CENTER;
        p.spaceBefore /= 2;
        beginParagraph( p );
    }

    /** {@inheritDoc} */
    public void figureCaption_()
    {
        endParagraph();
    }

    /** {@inheritDoc} */
    public void horizontalRule()
    {
        writer.print( "\\pard\\li" + indentation.get() );

        int skip = space.getNext();
        if ( skip > 0 )
        {
            writer.print( "\\sb" + skip );
        }
        space.setNext( skip );

        writer.print( "\\brdrb\\brdrs\\brdrw" + BORDER_WIDTH );
        writer.println( "\\plain\\fs1\\par" );
    }

    /** {@inheritDoc} */
    public void pageBreak()
    {
        writer.println( "\\page" );
    }

    /** {@inheritDoc} */
    public void anchor( String name )
    {
        // nop
    }

    /** {@inheritDoc} */
    public void anchor_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void link( String name )
    {
        // nop
    }

    /** {@inheritDoc} */
    public void link_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void inline()
    {
        inline( null );
    }

    /** {@inheritDoc} */
    public void inline( SinkEventAttributes attributes )
    {
        List<Integer> tags = new ArrayList<>();

        if ( attributes != null )
        {

            if ( attributes.containsAttribute( SinkEventAttributes.SEMANTICS, "italic" ) )
            {
                tags.add( 0, this.style );
                beginStyle( STYLE_ITALIC );
            }

            if ( attributes.containsAttribute( SinkEventAttributes.SEMANTICS, "bold" ) )
            {
                tags.add( 0, this.style );
                beginStyle( STYLE_BOLD );
            }

            if ( attributes.containsAttribute( SinkEventAttributes.SEMANTICS, "code" ) )
            {
                tags.add( 0, this.style );
                beginStyle( STYLE_TYPEWRITER );
            }

        }

        inlineStack.push( tags );
    }

    /** {@inheritDoc} */
    public void inline_()
    {
        for ( Integer style: inlineStack.pop() )
        {
            endStyle();
            this.style = style;
        }
    }

    /** {@inheritDoc} */
    public void italic()
    {
        inline( SinkEventAttributeSet.Semantics.ITALIC );
    }

    /** {@inheritDoc} */
    public void italic_()
    {
        inline_();
    }

    /** {@inheritDoc} */
    public void bold()
    {
        inline( SinkEventAttributeSet.Semantics.BOLD );
    }

    /** {@inheritDoc} */
    public void bold_()
    {
        inline_();
    }

    /** {@inheritDoc} */
    public void monospaced()
    {
        inline( SinkEventAttributeSet.Semantics.CODE );
    }

    /** {@inheritDoc} */
    public void monospaced_()
    {
        inline_();
    }

    private void beginStyle( int style )
    {
        this.style = style;

        switch ( context.get() )
        {
            case CONTEXT_TABLE:
                break;
            default:
                if ( paragraph != null )
                {
                    switch ( style )
                    {
                        case STYLE_ITALIC:
                            writer.println( "{\\i" );
                            break;
                        case STYLE_BOLD:
                            writer.println( "{\\b" );
                            break;
                        case STYLE_TYPEWRITER:
                            writer.println( "{\\f1" );
                            break;
                        default:
                            writer.println( "{" );
                            break;
                    }
                }
                break;
        }
    }

    private void endStyle()
    {
        style = STYLE_ROMAN;

        switch ( context.get() )
        {
            case CONTEXT_TABLE:
                break;
            default:
                if ( paragraph != null )
                {
                    writer.println( "}" );
                }
                break;
        }
    }

    /** {@inheritDoc} */
    public void lineBreak()
    {
        switch ( context.get() )
        {
            case CONTEXT_TABLE:
                cell.add( line );
                line = new Line();
                break;
            default:
                writer.println( "\\line" );
                break;
        }
    }

    /** {@inheritDoc} */
    public void nonBreakingSpace()
    {
        switch ( context.get() )
        {
            case CONTEXT_TABLE:
                line.add( new Item( style, " " ) );
                break;
            default:
                writer.println( "\\~" );
                break;
        }
    }

    /** {@inheritDoc} */
    public void text( String text )
    {
        switch ( context.get() )
        {
            case CONTEXT_VERBATIM:
                verbatim.append( text );
                break;

            case CONTEXT_TABLE:
                StringTokenizer t = new StringTokenizer( text, EOL, true );
                while ( t.hasMoreTokens() )
                {
                    String token = t.nextToken();
                    if ( token.equals( EOL ) )
                    {
                        cell.add( line );
                        line = new Line();
                    }
                    else
                    {
                        line.add( new Item( style, normalize( token ) ) );
                    }
                }
                break;

            default:
                if ( paragraph == null )
                {
                    beginParagraph( new Paragraph() );
                }
                writer.println( escape( normalize( text ) ) );
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

    private static String normalize( String s )
    {
        int length = s.length();
        StringBuilder buffer = new StringBuilder( length );

        for ( int i = 0; i < length; ++i )
        {
            char c = s.charAt( i );

            if ( Character.isWhitespace( c ) )
            {
                if ( buffer.length() == 0 || buffer.charAt( buffer.length() - 1 ) != ' ' )
                {
                    buffer.append( ' ' );
                }
            }

            else
            {
                buffer.append( c );
            }
        }

        return buffer.toString();
    }

    private static String escape( String s )
    {
        int length = s.length();
        StringBuilder buffer = new StringBuilder( length );

        for ( int i = 0; i < length; ++i )
        {
            char c = s.charAt( i );
            switch ( c )
            {
                case '\\':
                    buffer.append( "\\\\" );
                    break;
                case '{':
                    buffer.append( "\\{" );
                    break;
                case '}':
                    buffer.append( "\\}" );
                    break;
                default:
                    buffer.append( c );
            }
        }

        return buffer.toString();
    }

    /**
     * <p>getFont.</p>
     *
     * @param style a int.
     * @param size a int.
     * @return a {@link org.apache.maven.doxia.module.rtf.Font} object.
     */
    protected Font getFont( int style, int size )
    {
        Font font = null;

        StringBuilder buf = new StringBuilder();
        buf.append( style );
        buf.append( size );
        String key = buf.toString();

        Object object = fontTable.get( key );
        if ( object == null )
        {
            try
            {
                font = new Font( style, size );
                fontTable.put( key, font );
            }
            catch ( Exception ignored )
            {
                if ( getLog().isDebugEnabled() )
                {
                    getLog().debug( ignored.getMessage(), ignored );
                }
            }
        }
        else
        {
            font = (Font) object;
        }

        return font;
    }

    private static int textWidth( String text, Font font )
    {
        int width = 0;
        StringTokenizer t = new StringTokenizer( text, EOL );

        while ( t.hasMoreTokens() )
        {
            int w = font.textExtents( t.nextToken() ).width;
            if ( w > width )
            {
                width = w;
            }
        }

        return width;
    }


    /** {@inheritDoc} */
    public void flush()
    {
        writer.flush();
    }

    /** {@inheritDoc} */
    public void close()
    {
        writer.close();

        if ( getLog().isWarnEnabled() && this.warnMessages != null )
        {
            for ( Object o1 : this.warnMessages.entrySet() )
            {
                Map.Entry entry = (Map.Entry) o1;

                Set set = (Set) entry.getValue();

                for ( Object o : set )
                {
                    String msg = (String) o;

                    getLog().warn( msg );
                }
            }

            this.warnMessages = null;
        }

        init();
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
        msg = "[RTF Sink] " + msg;
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( msg );

            return;
        }

        if ( warnMessages == null )
        {
            warnMessages = new HashMap();
        }

        Set set = (Set) warnMessages.get( key );
        if ( set == null )
        {
            set = new TreeSet();
        }
        set.add( msg );
        warnMessages.put( key, set );
    }

    /** {@inheritDoc} */
    protected void init()
    {
        super.init();

        this.fontTable.clear();
        this.context = new Context();
        this.paragraph = null;
        this.indentation = new Indentation( 0 );
        this.space = new Space( 20 * DEFAULT_SPACING );
        Font font = getFont( STYLE_BOLD, fontSize );
        if ( font != null )
        {
            this.listItemIndent = textWidth( LIST_ITEM_HEADER, font );
        }
        this.numbering.clear();
        this.itemNumber.clear();
        this.style = STYLE_ROMAN;
        this.sectionLevel = 0;
        this.emptyHeader = false;
        this.verbatim = null;
        this.frame = false;
        this.table = null;
        this.row = null;
        this.cell = null;
        this.line = null;
        this.warnMessages = null;
    }

    // -----------------------------------------------------------------------

    static class Counter
    {
        private int value;

        Counter( int value )
        {
            set( value );
        }

        void set( int value )
        {
            this.value = value;
        }

        int get()
        {
            return value;
        }

        void increment()
        {
            increment( 1 );
        }

        void increment( int value )
        {
            this.value += value;
        }
    }

    static class Context
    {
        private int context = CONTEXT_UNDEFINED;

        private Vector stack = new Vector();

        void set( int context )
        {
            stack.addElement( this.context );
            this.context = context;
        }

        void restore()
        {
            if ( !stack.isEmpty() )
            {
                context = (Integer) stack.lastElement();
                stack.removeElementAt( stack.size() - 1 );
            }
        }

        int get()
        {
            return context;
        }
    }

    class Paragraph
    {
        int style = 0;

        int justification = Sink.JUSTIFY_LEFT;

        int leftIndent = indentation.get();

        int rightIndent = 0;

        int firstLineIndent = 0;

        int spaceBefore = space.getNext();

        int spaceAfter = 0;

        boolean frame = false;

        int fontStyle = STYLE_ROMAN;

        int fontSize = RtfSink.this.fontSize;

        Paragraph()
        {
            // nop
        }

        Paragraph( int style, int size )
        {
            fontStyle = style;
            fontSize = size;
        }

        void begin()
        {
            writer.print( "\\pard" );
            if ( style > 0 )
            {
                writer.print( "\\s" + style );
            }
            switch ( justification )
            {
                case Sink.JUSTIFY_LEFT:
                default:
                    break;
                case Sink.JUSTIFY_CENTER:
                    writer.print( "\\qc" );
                    break;
                case Sink.JUSTIFY_RIGHT:
                    writer.print( "\\qr" );
                    break;
            }
            if ( leftIndent != 0 )
            {
                writer.print( "\\li" + leftIndent );
            }
            if ( rightIndent != 0 )
            {
                writer.print( "\\ri" + rightIndent );
            }
            if ( firstLineIndent != 0 )
            {
                writer.print( "\\fi" + firstLineIndent );
            }
            if ( spaceBefore != 0 )
            {
                writer.print( "\\sb" + spaceBefore );
            }
            if ( spaceAfter != 0 )
            {
                writer.print( "\\sa" + spaceAfter );
            }

            if ( frame )
            {
                writer.print( "\\box\\brdrs\\brdrw" + BORDER_WIDTH );
            }

            writer.print( "\\plain" );
            switch ( fontStyle )
            {
                case STYLE_ROMAN:
                default:
                    writer.print( "\\f0" );
                    break;
                case STYLE_ITALIC:
                    writer.print( "\\f0\\i" );
                    break;
                case STYLE_BOLD:
                    writer.print( "\\f0\\b" );
                    break;
                case STYLE_TYPEWRITER:
                    writer.print( "\\f1" );
                    break;
            }
            writer.println( "\\fs" + ( 2 * fontSize ) );
        }

        void end()
        {
            writer.println( "\\par" );
        }
    }

    class Space
    {
        private int space;

        private int next;

        private Vector stack = new Vector();

        Space( int space /*twips*/ )
        {
            this.space = space;
            next = space;
        }

        void set( int space /*twips*/ )
        {
            stack.addElement( this.space );
            this.space = space;
            next = space;
        }

        int get()
        {
            return space;
        }

        void restore()
        {
            if ( !stack.isEmpty() )
            {
                space = (Integer) stack.lastElement();
                stack.removeElementAt( stack.size() - 1 );
                next = space;
            }
        }

        void setNext( int space /*twips*/ )
        {
            next = space;
        }

        int getNext()
        {
            int nxt = this.next;
            this.next = space;
            return nxt;
        }

        void skip()
        {
            skip( getNext() );
        }

        void skip( int space /*twips*/ )
        {
            writer.print( "\\pard" );
            if ( ( space -= 10 ) > 0 )
            {
                writer.print( "\\sb" + space );
            }
            writer.println( "\\plain\\fs1\\par" );
        }
    }

    static class Indentation
    {
        private int indent;

        private Vector stack = new Vector();

        Indentation( int indent /*twips*/ )
        {
            this.indent = indent;
        }

        void set( int indent /*twips*/ )
        {
            stack.addElement( this.indent );
            this.indent = indent;
        }

        int get()
        {
            return indent;
        }

        void restore()
        {
            if ( !stack.isEmpty() )
            {
                indent = (Integer) stack.lastElement();
                stack.removeElementAt( stack.size() - 1 );
            }
        }

        void add( int indent /*twips*/ )
        {
            set( this.indent + indent );
        }
    }

    static class Table
    {
        int numColumns;

        int[] columnWidths;

        int[] justification;

        boolean grid;

        Vector rows;

        Table( int[] justification, boolean grid )
        {
            numColumns = justification.length;
            columnWidths = new int[numColumns];
            this.justification = justification;
            this.grid = grid;
            rows = new Vector();
        }

        void add( Row row )
        {
            rows.addElement( row );

            for ( int i = 0; i < numColumns; ++i )
            {
                if ( i >= row.cells.size() )
                {
                    break;
                }
                Cell cell = (Cell) row.cells.elementAt( i );
                int width = cell.boundingBox().width;
                if ( width > columnWidths[i] )
                {
                    columnWidths[i] = width;
                }
            }
        }

        int width()
        {
            int width = 0;
            for ( int i = 0; i < numColumns; ++i )
            {
                width += columnWidths[i];
            }
            if ( grid )
            {
                width += ( numColumns + 1 ) * BORDER_WIDTH;
            }
            return width;
        }
    }

    static class Row
    {
        Vector cells = new Vector();

        void add( Cell cell )
        {
            cells.addElement( cell );
        }

        int height()
        {
            int height = 0;
            int numCells = cells.size();
            for ( int i = 0; i < numCells; ++i )
            {
                Cell cell = (Cell) cells.elementAt( i );
                Box box = cell.boundingBox();
                if ( box.height > height )
                {
                    height = box.height;
                }
            }
            return height;
        }
    }

    class Cell
    {
        Vector lines = new Vector();

        void add( Line line )
        {
            lines.addElement( line );
        }

        Box boundingBox()
        {
            int width = 0;
            int height = 0;

            for ( int i = 0; i < lines.size(); ++i )
            {
                int w = 0;
                int h = 0;
                Line line = (Line) lines.elementAt( i );

                for ( int j = 0; j < line.items.size(); ++j )
                {
                    Item item = (Item) line.items.elementAt( j );
                    Font font = getFont( item.style, fontSize );
                    if ( font == null )
                    {
                        continue;
                    }
                    Font.TextExtents x = font.textExtents( item.text );
                    w += x.width;
                    if ( x.height > h )
                    {
                        h = x.height;
                    }
                }

                if ( w > width )
                {
                    width = w;
                }
                height += h;
            }

            width += ( 2 * CELL_HORIZONTAL_PAD );
            height += ( 2 * CELL_VERTICAL_PAD );

            // allow one more pixel for grid outline
            width += toTwips( 1., UNIT_PIXEL );

            return new Box( width, height );
        }
    }

    static class Line
    {
        Vector items = new Vector();

        void add( Item item )
        {
            items.addElement( item );
        }
    }

    static class Item
    {
        int style;

        String text;

        Item( int style, String text )
        {
            this.style = style;
            this.text = text;
        }
    }

    static class Box
    {
        int width;

        int height;

        Box( int width, int height )
        {
            this.width = width;
            this.height = height;
        }
    }
}
