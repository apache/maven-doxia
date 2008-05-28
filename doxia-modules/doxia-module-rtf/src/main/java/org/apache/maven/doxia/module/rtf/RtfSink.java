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

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkAdapter;
import org.apache.maven.doxia.sink.SinkEventAttributes;

/**
 * @version $Id$
 * @plexus.component role="org.apache.maven.doxia.sink.Sink" role-hint="rtf"
 */
public class RtfSink
    extends SinkAdapter
{

    public static final double DEFAULT_PAPER_WIDTH = 21.;   /*cm*/

    public static final double DEFAULT_PAPER_HEIGHT = 29.7; /*cm*/

    public static final double DEFAULT_TOP_MARGIN = 2.;    /*cm*/

    public static final double DEFAULT_BOTTOM_MARGIN = 2.; /*cm*/

    public static final double DEFAULT_LEFT_MARGIN = 2.;   /*cm*/

    public static final double DEFAULT_RIGHT_MARGIN = 2.;  /*cm*/

    public static final int DEFAULT_FONT_SIZE = 10; /*pts*/

    public static final int DEFAULT_SPACING = 10;   /*pts*/

    public static final int DEFAULT_RESOLUTION = 72; /*dpi*/

    public static final String DEFAULT_IMAGE_FORMAT = "bmp";

    public static final String DEFAULT_IMAGE_TYPE = "palette";

    public static final String DEFAULT_DATA_FORMAT = "ascii";

    public static final int DEFAULT_CODE_PAGE = 1252;

    public static final int DEFAULT_CHAR_SET = 0;

    public static final String IMG_FORMAT_BMP = "bmp";

    public static final String IMG_FORMAT_WMF = "wmf";

    public static final String IMG_TYPE_PALETTE = "palette";

    public static final String IMG_TYPE_RGB = "rgb";

    public static final String IMG_DATA_ASCII = "ascii";

    public static final String IMG_DATA_RAW = "raw";

    public static final int STYLE_ROMAN = 0;

    public static final int STYLE_ITALIC = 1;

    public static final int STYLE_BOLD = 2;

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

    private int fontSize = DEFAULT_FONT_SIZE;

    private int resolution = DEFAULT_RESOLUTION;

    private String imageFormat = DEFAULT_IMAGE_FORMAT;

    private String imageType = DEFAULT_IMAGE_TYPE;

    private String imageDataFormat = DEFAULT_DATA_FORMAT;

    private boolean imageCompression = true;

    private int codePage = DEFAULT_CODE_PAGE;

    private int charSet = DEFAULT_CHAR_SET;

    private Hashtable fontTable;

    private Context context;

    private Paragraph paragraph;

    private Indentation indentation;

    private Space space;

    private int listItemIndent;

    private Vector numbering;

    private Vector itemNumber;

    private int style = STYLE_ROMAN;

    private int sectionLevel;

    private boolean emptyHeader;

    private StringBuffer verbatim;

    boolean frame;

    private Table table;

    private Row row;

    private Cell cell;

    private Line line;

    protected PrintWriter writer;

    protected OutputStream stream; // for raw image data

    // -----------------------------------------------------------------------

    public RtfSink()
        throws IOException
    {
        this( System.out );
    }

    public RtfSink( OutputStream output )
        throws IOException
    {
        this( output, null );
    }

    public RtfSink( OutputStream output, String encoding )
        throws IOException
    {
        fontTable = new Hashtable();
        context = new Context();
        indentation = new Indentation( 0 );
        space = new Space( 20 * DEFAULT_SPACING );
        numbering = new Vector();
        itemNumber = new Vector();

        Font font = getFont( STYLE_BOLD, fontSize );
        if ( font != null )
        {
            listItemIndent = textWidth( LIST_ITEM_HEADER, font );
        }

        Writer w;
        stream = new BufferedOutputStream( output );
        if ( encoding != null )
        {
            w = new OutputStreamWriter( stream, encoding );
        }
        else
        {
            w = new OutputStreamWriter( stream );
        }
        writer = new PrintWriter( new BufferedWriter( w ) );
    }

    public void setPaperSize( double width /*cm*/, double height /*cm*/ )
    {
        paperWidth = width;
        paperHeight = height;
    }

    public void setTopMargin( double margin )
    {
        topMargin = margin;
    }

    public void setBottomMargin( double margin )
    {
        bottomMargin = margin;
    }

    public void setLeftMargin( double margin )
    {
        leftMargin = margin;
    }

    public void setRightMargin( double margin )
    {
        rightMargin = margin;
    }

    public void setFontSize( int size /*pts*/ )
    {
        fontSize = size;
    }

    public void setSpacing( int spacing /*pts*/ )
    {
        space.set( 20 * spacing );
    }

    public void setResolution( int resolution /*dpi*/ )
    {
        this.resolution = resolution;
    }

    public void setImageFormat( String format )
    {
        imageFormat = format;
    }

    public void setImageType( String type )
    {
        imageType = type;
    }

    public void setImageDataFormat( String format )
    {
        imageDataFormat = format;
    }

    public void setImageCompression( boolean compression )
    {
        imageCompression = compression;
    }

    public void setCodePage( int cp )
    {
        codePage = cp;
    }

    public void setCharSet( int cs )
    {
        charSet = cs;
    }

    public void head()
    {
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

    private int toTwips( double length, int unit )
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

    public void title()
    {
        Paragraph p = new Paragraph( STYLE_BOLD, fontSize + 6 );
        p.justification = Parser.JUSTIFY_CENTER;
        beginParagraph( p );
        emptyHeader = false;
    }

    public void title_()
    {
        endParagraph();
    }

    public void author()
    {
        Paragraph p = new Paragraph( STYLE_ROMAN, fontSize + 2 );
        p.justification = Parser.JUSTIFY_CENTER;
        beginParagraph( p );
        emptyHeader = false;
    }

    public void author_()
    {
        endParagraph();
    }

    public void date()
    {
        Paragraph p = new Paragraph( STYLE_ROMAN, fontSize );
        p.justification = Parser.JUSTIFY_CENTER;
        beginParagraph( p );
        emptyHeader = false;
    }

    public void date_()
    {
        endParagraph();
    }

    public void body()
    {
    }

    public void body_()
    {
        writer.println( "}" );
        writer.flush();
    }

    public void section1()
    {
        sectionLevel = 1;
    }

    public void section1_()
    {
    }

    public void section2()
    {
        sectionLevel = 2;
    }

    public void section2_()
    {
    }

    public void section3()
    {
        sectionLevel = 3;
    }

    public void section3_()
    {
    }

    public void section4()
    {
        sectionLevel = 4;
    }

    public void section4_()
    {
    }

    public void section5()
    {
        sectionLevel = 5;
    }

    public void section5_()
    {
    }

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
        }

        Paragraph p = new Paragraph( stl, size );
        p.style = styleNumber( sectionLevel );

        beginParagraph( p );
    }

    public void sectionTitle_()
    {
        endParagraph();
    }

    private int styleNumber( int level )
    {
        return level;
    }

    public void list()
    {
        indentation.add( LIST_INDENT );
        space.set( space.get() / 2 );
    }

    public void list_()
    {
        indentation.restore();
        space.restore();
    }

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

    public void listItem_()
    {
        endParagraph();

        indentation.restore();
        space.restore();
    }

    public void numberedList( int numbering )
    {
        this.numbering.addElement( new Integer( numbering ) );
        itemNumber.addElement( new Counter( 0 ) );

        indentation.add( LIST_INDENT );
        space.set( space.get() / 2 );
    }

    public void numberedList_()
    {
        numbering.removeElementAt( numbering.size() - 1 );
        itemNumber.removeElementAt( itemNumber.size() - 1 );

        indentation.restore();
        space.restore();
    }

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

    public void numberedListItem_()
    {
        endParagraph();

        indentation.restore();
        space.restore();
    }

    private String getItemHeader()
    {
        int nmb = ( (Integer) this.numbering.lastElement() ).intValue();
        int iNmb = ( (Counter) this.itemNumber.lastElement() ).get();
        StringBuffer buf = new StringBuffer();

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

    public void definitionList()
    {
        int next = space.getNext();

        indentation.add( LIST_INDENT );
        space.set( space.get() / 2 );
        space.setNext( next );
    }

    public void definitionList_()
    {
        indentation.restore();
        space.restore();
    }

    public void definitionListItem()
    {
        int next = space.getNext();
        space.set( space.get() / 2 );
        space.setNext( next );
    }

    public void definitionListItem_()
    {
        space.restore();
    }

    public void definedTerm()
    {
    }

    public void definedTerm_()
    {
        endParagraph();
    }

    public void definition()
    {
        int next = space.getNext();

        indentation.add( DEFINITION_INDENT );
        space.set( space.get() / 2 );
        space.setNext( next );
    }

    public void definition_()
    {
        endParagraph();

        indentation.restore();
        space.restore();
    }

    public void table()
    {
    }

    public void table_()
    {
    }

    public void tableRows( int[] justification, boolean grid )

    {
        table = new Table( justification, grid );
        context.set( CONTEXT_TABLE );
    }

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
            case Parser.JUSTIFY_LEFT:
            default:
                writer.println( "\\ql" );
                break;
            case Parser.JUSTIFY_CENTER:
                writer.println( "\\qc" );
                break;
            case Parser.JUSTIFY_RIGHT:
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

    public void tableRow()
    {
        row = new Row();
    }

    public void tableRow_()
    {
        table.add( row );
    }

    public void tableHeaderCell()
    {
        tableCell();
    }

    public void tableHeaderCell_()
    {
        tableCell_();
    }

    public void tableCell()
    {
        cell = new Cell();
        line = new Line();
    }

    public void tableCell_()
    {
        cell.add( line );
        row.add( cell );
    }

    public void tableCaption()
    {
        Paragraph p = new Paragraph();
        p.justification = Parser.JUSTIFY_CENTER;
        p.spaceBefore /= 2;
        beginParagraph( p );
    }

    public void tableCaption_()
    {
        endParagraph();
    }

    public void paragraph()
    {
        if ( paragraph == null )
        {
            beginParagraph( new Paragraph() );
        }
    }

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

    public void verbatim( boolean boxed )
    {
        verbatim = new StringBuffer();
        frame = boxed;

        context.set( CONTEXT_VERBATIM );
    }

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

    public void figure()
    {
    }

    public void figure_()
    {
    }

    public void figureGraphics( String name )
    {
        if ( !name.endsWith( ".ppm" ) )
        {
            getLog().warn( "Unsupported image type: " + name );
        }

        Paragraph p = new Paragraph();
        p.justification = Parser.JUSTIFY_CENTER;
        beginParagraph( p );

        try
        {
            writeImage( name );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            System.exit( 2 );
        }

        endParagraph();
    }

    private void writeImage( String source )
        throws Exception
    {
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

    public void figureCaption()
    {
        Paragraph p = new Paragraph();
        p.justification = Parser.JUSTIFY_CENTER;
        p.spaceBefore /= 2;
        beginParagraph( p );
    }

    public void figureCaption_()
    {
        endParagraph();
    }

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
    }

    /** {@inheritDoc} */
    public void anchor_()
    {
    }

    /** {@inheritDoc} */
    public void link( String name )
    {
    }

    /** {@inheritDoc} */
    public void link_()
    {
    }

    /** {@inheritDoc} */
    public void italic()
    {
        beginStyle( STYLE_ITALIC );
    }

    /** {@inheritDoc} */
    public void italic_()
    {
        endStyle();
    }

    /** {@inheritDoc} */
    public void bold()
    {
        beginStyle( STYLE_BOLD );
    }

    /** {@inheritDoc} */
    public void bold_()
    {
        endStyle();
    }

    /** {@inheritDoc} */
    public void monospaced()
    {
        beginStyle( STYLE_TYPEWRITER );
    }

    /** {@inheritDoc} */
    public void monospaced_()
    {
        endStyle();
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
     * Unkown events just log a warning message but are ignored otherwise.
     *
     * @param name The name of the event.
     * @param requiredParams not used.
     * @param attributes not used.
     * @see org.apache.maven.doxia.sink.Sink#unknown(String,Object[],SinkEventAttributes)
     */
    public void unknown( String name, Object[] requiredParams, SinkEventAttributes attributes )
    {
        getLog().warn( "Unknown Sink event in RtfSink: " + name + ", ignoring!" );
    }

    private static String normalize( String s )
    {
        int length = s.length();
        StringBuffer buffer = new StringBuffer( length );

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
        StringBuffer buffer = new StringBuffer( length );

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

    private Font getFont( int style, int size )
    {
        Font font = null;

        StringBuffer buf = new StringBuffer();
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

    // -----------------------------------------------------------------------

    private class Counter
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

    private class Context
    {

        private int context = CONTEXT_UNDEFINED;

        private Vector stack = new Vector();

        void set( int context )
        {
            stack.addElement( new Integer( this.context ) );
            this.context = context;
        }

        void restore()
        {
            if ( !stack.isEmpty() )
            {
                context = ( (Integer) stack.lastElement() ).intValue();
                stack.removeElementAt( stack.size() - 1 );
            }
        }

        int get()
        {
            return context;
        }

    }

    private class Paragraph
    {

        int style = 0;

        int justification = Parser.JUSTIFY_LEFT;

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
                case Parser.JUSTIFY_LEFT:
                default:
                    break;
                case Parser.JUSTIFY_CENTER:
                    writer.print( "\\qc" );
                    break;
                case Parser.JUSTIFY_RIGHT:
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

    private class Space
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
            stack.addElement( new Integer( this.space ) );
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
                space = ( (Integer) stack.lastElement() ).intValue();
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

    private class Indentation
    {

        private int indent;

        private Vector stack = new Vector();

        Indentation( int indent /*twips*/ )
        {
            this.indent = indent;
        }

        void set( int indent /*twips*/ )
        {
            stack.addElement( new Integer( this.indent ) );
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
                indent = ( (Integer) stack.lastElement() ).intValue();
                stack.removeElementAt( stack.size() - 1 );
            }
        }

        void add( int indent /*twips*/ )
        {
            set( this.indent + indent );
        }

    }

    private class Table
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

    private class Row
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

    private class Cell
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

    private class Line
    {

        Vector items = new Vector();

        void add( Item item )
        {
            items.addElement( item );
        }

    }

    private class Item
    {

        int style;

        String text;

        Item( int style, String text )
        {
            this.style = style;
            this.text = text;
        }

    }

    private class Box
    {

        int width;

        int height;

        Box( int width, int height )
        {
            this.width = width;
            this.height = height;
        }

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
    }
}
