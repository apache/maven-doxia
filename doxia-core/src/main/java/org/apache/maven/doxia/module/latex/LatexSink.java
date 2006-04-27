package org.apache.maven.doxia.module.latex;

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

import org.apache.maven.doxia.module.apt.AptParser;
import org.apache.maven.doxia.sink.SinkAdapter;
import org.apache.maven.doxia.util.LineBreaker;

import java.io.Writer;

/**
 * @componentx
 */
public class LatexSink
    extends SinkAdapter
{
    private static final String EOL = System.getProperty( "line.separator" );

    private LineBreaker out;

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

    // -----------------------------------------------------------------------

    public LatexSink( Writer out )
    {
        this( out, defaultPreamble );
    }

    public LatexSink( Writer out, String preamble )
    {
        this.out = new LineBreaker( out );
        setPreamble( preamble );
    }

    public void setPreamble( String preamble )
    {
        this.preamble = preamble;
    }

    public String getPreamble()
    {
        return preamble;
    }

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

        markup( preamble );
        markup( "\\begin{document}" + EOL + EOL );
    }

    public void body()
    {
        if ( titleFlag )
        {
            titleFlag = false;
            markup( "\\pmaketitle" + EOL + EOL );
        }
    }

    public void body_()
    {
        markup( "\\end{document}" + EOL + EOL );
        out.flush();
    }

    public void section1()
    {
        markup( "\\psectioni{" );
    }

    public void section2()
    {
        markup( "\\psectionii{" );
    }

    public void section3()
    {
        markup( "\\psectioniii{" );
    }

    public void section4()
    {
        markup( "\\psectioniv{" );
    }

    public void section5()
    {
        markup( "\\psectionv{" );
    }

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
                case AptParser.JUSTIFY_CENTER:
                    justif.append( 'c' );
                    break;
                case AptParser.JUSTIFY_LEFT:
                    justif.append( 'l' );
                    break;
                case AptParser.JUSTIFY_RIGHT:
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
        if ( gridFlag )
        {
            markup( "\\hline" + EOL );
        }
        cellCount = 0;
    }

    public void title()
    {
        titleFlag = true;
        markup( "\\ptitle{" );
    }

    public void title_()
    {
        markup( "}" + EOL );
    }

    public void author()
    {
        markup( "\\pauthor{" );
    }

    public void author_()
    {
        markup( "}" + EOL );
    }

    public void date()
    {
        markup( "\\pdate{" );
    }

    public void date_()
    {
        markup( "}" + EOL );
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
        if ( cellCount > 0 )
        {
            markup( " &" + EOL );
        }

        char justif;
        switch ( cellJustif[cellCount] )
        {
            case AptParser.JUSTIFY_LEFT:
                justif = 'l';
                break;
            case AptParser.JUSTIFY_RIGHT:
                justif = 'r';
                break;
            case AptParser.JUSTIFY_CENTER:
            default:
                justif = 'c';
                break;
        }
        markup( "\\begin{pcell}{" + justif + "}" );
    }

    public void tableCell_()
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
        if ( verbatimFlag )
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
        out.write( text, /*preserveSpace*/ true );
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
                    buffer.append( "\\symbol{" + ( (int) c ) + "}" );
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

    // -----------------------------------------------------------------------

    private static final String defaultPreamble = "\\newcommand{\\ptitle}[1]{\\title{#1}}" + EOL +
        "\\newcommand{\\pauthor}[1]{\\author{#1}}" + EOL +
        "\\newcommand{\\pdate}[1]{\\date{#1}}" + EOL +
        "\\newcommand{\\pmaketitle}{\\maketitle}" + EOL +
        "\\newcommand{\\psectioni}[1]{\\section{#1}}" + EOL +
        "\\newcommand{\\psectionii}[1]{\\subsection{#1}}" + EOL +
        "\\newcommand{\\psectioniii}[1]{\\subsubsection{#1}}" + EOL +
        "\\newcommand{\\psectioniv}[1]{\\paragraph{#1}}" + EOL +
        "\\newcommand{\\psectionv}[1]{\\subparagraph{#1}}" + EOL +
        "\\newenvironment{plist}{\\begin{itemize}}{\\end{itemize}}" + EOL +
        "\\newenvironment{pnumberedlist}{\\begin{enumerate}}{\\end{enumerate}}" + EOL +
        "\\newcommand{\\pdef}[1]{\\textbf{#1}\\hfill}" + EOL +
        "\\newenvironment{pdefinitionlist}" + EOL +
        "{\\begin{list}{}{\\settowidth{\\labelwidth}{\\textbf{999.}}" + EOL +
        "                \\setlength{\\leftmargin}{\\labelwidth}" + EOL +
        "                \\addtolength{\\leftmargin}{\\labelsep}" + EOL +
        "                \\renewcommand{\\makelabel}{\\pdef}}}" + EOL + "{\\end{list}}" + EOL +
        "\\newenvironment{pfigure}{\\begin{center}}{\\end{center}}" + EOL +
        "\\newcommand{\\pfiguregraphics}[1]{\\includegraphics{#1.eps}}" + EOL +
        "\\newcommand{\\pfigurecaption}[1]{\\\\ \\vspace{\\pparskipamount}" + EOL +
        "                                \\textit{#1}}" + EOL +
        "\\newenvironment{ptable}{\\begin{center}}{\\end{center}}" + EOL +
        "\\newenvironment{ptablerows}[1]{\\begin{tabular}{#1}}{\\end{tabular}}" + EOL +
        "\\newenvironment{pcell}[1]{\\begin{tabular}[t]{#1}}{\\end{tabular}}" + EOL +
        "\\newcommand{\\ptablecaption}[1]{\\\\ \\vspace{\\pparskipamount}" + EOL +
        "                               \\textit{#1}}" + EOL +
        "\\newenvironment{pverbatim}{\\begin{small}}{\\end{small}}" + EOL + "\\newsavebox{\\pbox}" + EOL +
        "\\newenvironment{pverbatimbox}" + EOL + "{\\begin{lrbox}{\\pbox}\\begin{minipage}{\\linewidth}\\begin{small}}" + EOL +
        "{\\end{small}\\end{minipage}\\end{lrbox}\\fbox{\\usebox{\\pbox}}}" + EOL +
        "\\newcommand{\\phorizontalrule}{\\begin{center}" + EOL +
        "                              \\rule[0.5ex]{\\linewidth}{1pt}" + EOL +
        "                              \\end{center}}" + EOL +
        "\\newcommand{\\panchor}[1]{\\textcolor{panchorcolor}{#1}}" + EOL +
        "\\newcommand{\\plink}[1]{\\textcolor{plinkcolor}{#1}}" + EOL + "\\newcommand{\\pitalic}[1]{\\textit{#1}}" + EOL +
        "\\newcommand{\\pbold}[1]{\\textbf{#1}}" + EOL +
        "\\newcommand{\\pmonospaced}[1]{\\texttt{\\small #1}}" + EOL + EOL +
        "\\documentclass[a4paper]{article}" + EOL + "\\usepackage{a4wide}" + EOL +
        "\\usepackage{color}" + EOL +
        "\\usepackage{graphics}" + EOL +
        "\\usepackage{times}" + EOL +
        "\\usepackage[latin1]{inputenc}" + EOL +
        "\\usepackage[T1]{fontenc}" + EOL + EOL +
        "\\pagestyle{plain}" + EOL + EOL +
        "\\definecolor{plinkcolor}{rgb}{0,0,0.54}" + EOL +
        "\\definecolor{panchorcolor}{rgb}{0.54,0,0}" + EOL + EOL +
        "\\newlength{\\pparskipamount}" + EOL +
        "\\setlength{\\pparskipamount}{1ex}" + EOL +
        "\\setlength{\\parindent}{0pt}" + EOL +
        "\\setlength{\\parskip}{\\pparskipamount}" + EOL + EOL;

    // -----------------------------------------------------------------------

    public void flush()
    {
        out.flush();
    }

    public void close()
    {
        out.close();
    }
}
