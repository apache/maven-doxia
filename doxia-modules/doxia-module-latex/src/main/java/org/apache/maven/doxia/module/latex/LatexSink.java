package org.apache.maven.doxia.module.latex;

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

import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.SinkAdapter;
import org.apache.maven.doxia.util.LineBreaker;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

public class LatexSink
    extends SinkAdapter
{
    private static final String EOL = System.getProperty( "line.separator" );

    /**
     * Flag that indicates if the document to be written is only a fragment.
     *
     * This implies that <code>\\begin{document}</code>, <code>\\ptitle{..}</code> will not be output.
     */
    private boolean fragmentDocument;

    private boolean ignoreText;

    private LineBreaker out;

    private String sinkCommands;

    private String preamble;

    private boolean titleFlag;

    private int numberedListNesting;

    private boolean verbatimFlag;

    private boolean boxFlag;

    private boolean figureFlag;

    private boolean tableFlag;

    private boolean gridFlag;

    private int[] cellJustif;

    private int cellCount;

    private boolean isTitle;

    private String title;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public LatexSink( Writer out )
        throws IOException
    {
        this( out, IOUtil.toString( getDefaultSinkCommands() ), IOUtil.toString( getDefaultPreamble() ) );
    }

    public LatexSink( Writer out, String sinkCommands, String preamble )
    {
        this.out = new LineBreaker( out );
        this.sinkCommands = sinkCommands;
        this.preamble = preamble;
    }

    public LatexSink( Writer out, String sinkCommands, String preamble, boolean fragmentDocument )
    {
        this.out = new LineBreaker( out );
        this.sinkCommands = sinkCommands;
        this.preamble = preamble;
        this.fragmentDocument = fragmentDocument;
    }

    // ----------------------------------------------------------------------
    // Overridables
    // ----------------------------------------------------------------------

    protected String getDocumentStart()
    {
        return "\\documentclass[a4paper]{article}" + EOL + EOL;
    }

    protected String getDocumentBegin()
    {
        return "\\begin{document}" + EOL + EOL;
    }

    protected String getDocumentEnd()
    {
        return "\\end{document}" + EOL;
    }

    // ----------------------------------------------------------------------
    // Sink Implementation
    // ----------------------------------------------------------------------

    public void head()
    {
        titleFlag = false;
        numberedListNesting = 0;
        verbatimFlag = false;
        boxFlag = false;
        figureFlag = false;
        tableFlag = false;
        gridFlag = false;
        cellJustif = null;
        cellCount = 0;

        if ( !fragmentDocument )
        {
            markup( sinkCommands );

            markup( getDocumentStart() );

            markup( preamble );

            markup( getDocumentBegin() );
        }
    }

    public void body()
    {
        if ( titleFlag )
        {
            if ( fragmentDocument  )
            {
                markup( "\\psection" );
            }
            else
            {
                titleFlag = false;
                markup( "\\pmaketitle" + EOL + EOL );
            }
        }
    }

    public void body_()
    {
        if ( !fragmentDocument )
        {
            markup( getDocumentEnd() );
        }

        out.flush();
    }

    // ----------------------------------------------------------------------
    // Section Title 1
    // ----------------------------------------------------------------------

    public void sectionTitle1()
    {
        isTitle = true;
    }

    public void sectionTitle1_()
    {
        isTitle = false;

        if ( StringUtils.isNotEmpty( title ) )
        {
            markup( "\\psectioni{" + title + "}" + EOL );

            title = null;
        }
    }

    // ----------------------------------------------------------------------
    // Section Title 2
    // ----------------------------------------------------------------------

    public void sectionTitle2()
    {
        isTitle = true;
    }

    public void sectionTitle2_()
    {
        isTitle = false;

        if ( StringUtils.isNotEmpty( title ) )
        {
            markup( "\\psectionii{" + title + "}" + EOL );

            title = null;
        }
    }

    // ----------------------------------------------------------------------
    // Section Title 3
    // ----------------------------------------------------------------------

    public void sectionTitle3()
    {
        isTitle = true;
    }

    public void sectionTitle3_()
    {
        isTitle = false;

        if ( StringUtils.isNotEmpty( title ) )
        {
            markup( "\\psectioniii{" + title + "}" + EOL );

            title = null;
        }
    }

    // ----------------------------------------------------------------------
    // Section Title 4
    // ----------------------------------------------------------------------

    public void sectionTitle4()
    {
        isTitle = true;
    }

    public void sectionTitle4_()
    {
        isTitle = false;

        if ( StringUtils.isNotEmpty( title ) )
        {
            markup( "\\psectioniv{" + title + "}" + EOL );

            title = null;
        }
    }

    // ----------------------------------------------------------------------
    // Section Title 5
    // ----------------------------------------------------------------------

    public void sectionTitle5()
    {
        isTitle = true;
    }

    public void sectionTitle5_()
    {
        isTitle = false;

        if ( StringUtils.isNotEmpty( title ) )
        {
            markup( "\\psectionv{" + title + "}" + EOL );

            title = null;
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void list()
    {
        markup( "\\begin{plist}" + EOL + EOL );
    }

    public void list_()
    {
        markup( "\\end{plist}" + EOL + EOL );
    }

    public void listItem()
    {
        markup( "\\item{} " );
    }

    public void numberedList( int numbering )
    {
        ++numberedListNesting;

        String counter;
        switch ( numberedListNesting )
        {
            case 1:
                counter = "enumi";
                break;
            case 2:
                counter = "enumii";
                break;
            case 3:
                counter = "enumiii";
                break;
            case 4:
            default:
                counter = "enumiv";
        }

        String style;
        switch ( numbering )
        {
            case NUMBERING_UPPER_ALPHA:
                style = "Alph";
                break;
            case NUMBERING_LOWER_ALPHA:
                style = "alph";
                break;
            case NUMBERING_UPPER_ROMAN:
                style = "Roman";
                break;
            case NUMBERING_LOWER_ROMAN:
                style = "roman";
                break;
            case NUMBERING_DECIMAL:
            default:
                style = "arabic";
        }

        markup( "\\begin{pnumberedlist}" + EOL );
        markup( "\\renewcommand{\\the" + counter + "}{\\" + style + "{" + counter + "}}" + EOL + EOL );
    }

    public void numberedList_()
    {
        markup( "\\end{pnumberedlist}" + EOL + EOL );
        --numberedListNesting;
    }

    public void numberedListItem()
    {
        markup( "\\item{} " );
    }

    public void definitionList()
    {
        markup( "\\begin{pdefinitionlist}" + EOL + EOL );
    }

    public void definitionList_()
    {
        markup( "\\end{pdefinitionlist}" + EOL + EOL );
    }

    public void figure()
    {
        figureFlag = true;
        markup( "\\begin{pfigure}" + EOL );
    }

    public void figure_()
    {
        markup( "\\end{pfigure}" + EOL + EOL );
        figureFlag = false;
    }

    public void table()
    {
        tableFlag = true;
        markup( "\\begin{ptable}" + EOL );
    }

    public void table_()
    {
        markup( "\\end{ptable}" + EOL + EOL );
        tableFlag = false;
    }

    public void tableRows( int[] justification, boolean grid )

    {
        StringBuffer justif = new StringBuffer();
        for ( int i = 0; i < justification.length; ++i )
        {
            if ( grid )
            {
                justif.append( '|' );
            }
            switch ( justification[i] )
            {
                case Parser.JUSTIFY_CENTER:
                    justif.append( 'c' );
                    break;
                case Parser.JUSTIFY_LEFT:
                    justif.append( 'l' );
                    break;
                case Parser.JUSTIFY_RIGHT:
                    justif.append( 'r' );
                    break;
            }
        }
        if ( grid )
        {
            justif.append( '|' );
        }

        markup( "\\begin{ptablerows}{" + justif.toString() + "}" + EOL );
        if ( grid )
        {
            markup( "\\hline" + EOL );
        }
        gridFlag = grid;
        cellJustif = justification;
    }

    public void tableRows_()
    {
        markup( "\\end{ptablerows}" + EOL );
        gridFlag = false;
        cellJustif = null;
    }

    public void tableRow()
    {
        cellCount = 0;
    }

    public void tableRow_()
    {
        markup( "\\\\" + EOL );
        if ( gridFlag || lastCellWasHeader )
        {
            markup( "\\hline" + EOL );
        }
        cellCount = 0;
        lastCellWasHeader = false;
    }

    public void title()
    {
        if ( !fragmentDocument )
        {
            titleFlag = true;
            markup( "\\ptitle{" );
        }
        else
        {
            ignoreText = true;
        }
    }

    public void title_()
    {
        if ( !fragmentDocument )
        {
            markup( "}" + EOL );
        }
        else
        {
            ignoreText = false;
        }
    }

    public void author()
    {
        if ( !fragmentDocument )
        {
            markup( "\\pauthor{" );
        }
        else
        {
            ignoreText = true;
        }
    }

    public void author_()
    {
        if ( !fragmentDocument )
        {
            markup( "}" + EOL );
        }
        else
        {
            ignoreText = false;
        }
    }

    public void date()
    {
        if ( !fragmentDocument )
        {
            markup( "\\pdate{" );
        }
        else
        {
            ignoreText = true;
        }
    }

    public void date_()
    {
        if ( !fragmentDocument )
        {
            markup( "}" + EOL );
        }
        else
        {
            ignoreText = false;
        }
    }

    public void sectionTitle_()
    {
        markup( "}" + EOL + EOL );
    }

    public void paragraph_()
    {
        markup( EOL + EOL );
    }

    public void verbatim( boolean boxed )
    {
        if ( boxed )
        {
            markup( "\\begin{pverbatimbox}" + EOL );
        }
        else
        {
            markup( "\\begin{pverbatim}" + EOL );
        }
        markup( "\\begin{verbatim}" + EOL );

        verbatimFlag = true;
        boxFlag = boxed;
    }

    public void verbatim_()
    {
        markup( EOL + "\\end{verbatim}" + EOL );
        if ( boxFlag )
        {
            markup( "\\end{pverbatimbox}" + EOL + EOL );
        }
        else
        {
            markup( "\\end{pverbatim}" + EOL + EOL );
        }

        verbatimFlag = false;
        boxFlag = false;
    }

    public void definedTerm()
    {
        markup( "\\item[\\mbox{" );
    }

    public void definedTerm_()
    {
        markup( "}] " );
    }

    public void figureCaption()
    {
        markup( "\\pfigurecaption{" );
    }

    public void figureCaption_()
    {
        markup( "}" + EOL );
    }

    public void tableCell()
    {
        tableCell( false );
    }
    
    public void tableCell_()
    {
        tableCell_( false );
    }

    public void tableHeaderCell()
    {
        tableCell( true );
    }
    
    public void tableHeaderCell_()
    {
        tableCell_( true );
    }
    
    private boolean lastCellWasHeader = false;
    
    public void tableCell( boolean header )
    {
        lastCellWasHeader = header;
        
        if ( cellCount > 0 )
        {
            markup( " &" + EOL );
        }

        char justif;
        switch ( cellJustif[cellCount] )
        {
            case Parser.JUSTIFY_LEFT:
                justif = 'l';
                break;
            case Parser.JUSTIFY_RIGHT:
                justif = 'r';
                break;
            case Parser.JUSTIFY_CENTER:
            default:
                justif = 'c';
                break;
        }
        markup( "\\begin{pcell}{" + justif + "}" );
    }

    public void tableCell_( boolean header )
    {
        markup( "\\end{pcell}" );
        ++cellCount;
    }

    public void tableCaption()
    {
        markup( "\\ptablecaption{" );
    }

    public void tableCaption_()
    {
        markup( "}" + EOL );
    }

    public void figureGraphics( String name )
    {
        markup( "\\pfiguregraphics{" + name + "}" + EOL );
    }

    public void horizontalRule()
    {
        markup( "\\phorizontalrule" + EOL + EOL );
    }

    public void pageBreak()
    {
        markup( "\\newpage" + EOL + EOL );
    }

    public void anchor( String name )
    {
        markup( "\\panchor{" );
    }

    public void anchor_()
    {
        markup( "}" );
    }

    public void link( String name )
    {
        markup( "\\plink{" );
    }

    public void link_()
    {
        markup( "}" );
    }

    public void italic()
    {
        markup( "\\pitalic{" );
    }

    public void italic_()
    {
        markup( "}" );
    }

    public void bold()
    {
        markup( "\\pbold{" );
    }

    public void bold_()
    {
        markup( "}" );
    }

    public void monospaced()
    {
        markup( "\\pmonospaced{" );
    }

    public void monospaced_()
    {
        markup( "}" );
    }

    public void lineBreak()
    {
        markup( ( figureFlag || tableFlag || titleFlag ) ? "\\\\" + EOL : "\\newline" + EOL );
    }

    public void nonBreakingSpace()
    {
        markup( "~" );
    }

    public void text( String text )
    {
        if ( ignoreText )
        {
            return;
        }
        if ( isTitle )
        {
            title = text;
        }
        else if ( verbatimFlag )
        {
            verbatimContent( text );
        }
        else
        {
            content( text );
        }
    }

    // -----------------------------------------------------------------------

    protected void markup( String text )
    {
        if ( text != null )
        {
            out.write( text, /*preserveSpace*/ true );
        }
    }

    protected void content( String text )
    {
        out.write( escaped( text ), /*preserveSpace*/ false );
    }

    protected void verbatimContent( String text )
    {
        out.write( text, /*preserveSpace*/ true );
    }

    // -----------------------------------------------------------------------

    protected static String escaped( String text )
    {
        int length = text.length();
        StringBuffer buffer = new StringBuffer( length );

        for ( int i = 0; i < length; ++i )
        {
            char c = text.charAt( i );
            switch ( c )
            {
                case '-':
                case '<':
                case '>':
                    buffer.append( "\\symbol{" ).append( (int) c ).append( "}" );
                    break;
                case '~':
                    buffer.append( "\\textasciitilde " );
                    break;
                case '^':
                    buffer.append( "\\textasciicircum " );
                    break;
                case '|':
                    buffer.append( "\\textbar " );
                    break;
                case '\\':
                    buffer.append( "\\textbackslash " );
                    break;
                case '$':
                    buffer.append( "\\$" );
                    break;
                case '&':
                    buffer.append( "\\&" );
                    break;
                case '%':
                    buffer.append( "\\%" );
                    break;
                case '#':
                    buffer.append( "\\#" );
                    break;
                case '{':
                    buffer.append( "\\{" );
                    break;
                case '}':
                    buffer.append( "\\}" );
                    break;
                case '_':
                    buffer.append( "\\_" );
                    break;
                default:
                    buffer.append( c );
            }
        }

        return buffer.toString();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void flush()
    {
        out.flush();
    }

    public void close()
    {
        out.close();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public static InputStream getDefaultSinkCommands()
        throws IOException
    {
        return LatexSink.class.getResource( "default_sink_commands.tex" ).openStream();
    }

    public static InputStream getDefaultPreamble()
        throws IOException
    {
        return LatexSink.class.getResource( "default_preamble.tex" ).openStream();
    }
}
