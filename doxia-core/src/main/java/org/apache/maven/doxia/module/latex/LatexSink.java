package org.apache.maven.doxia.module.latex;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
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
        markup( "\\begin{document}\n\n" );
    }

    public void body()
    {
        if ( titleFlag )
        {
            titleFlag = false;
            markup( "\\pmaketitle\n\n" );
        }
    }

    public void body_()
    {
        markup( "\\end{document}\n\n" );
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
        markup( "\\begin{plist}\n\n" );
    }

    public void list_()
    {
        markup( "\\end{plist}\n\n" );
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

        markup( "\\begin{pnumberedlist}\n" );
        markup( "\\renewcommand{\\the" + counter + "}{\\" + style + "{" + counter + "}}\n\n" );
    }

    public void numberedList_()
    {
        markup( "\\end{pnumberedlist}\n\n" );
        --numberedListNesting;
    }

    public void numberedListItem()
    {
        markup( "\\item{} " );
    }

    public void definitionList()
    {
        markup( "\\begin{pdefinitionlist}\n\n" );
    }

    public void definitionList_()
    {
        markup( "\\end{pdefinitionlist}\n\n" );
    }

    public void figure()
    {
        figureFlag = true;
        markup( "\\begin{pfigure}\n" );
    }

    public void figure_()
    {
        markup( "\\end{pfigure}\n\n" );
        figureFlag = false;
    }

    public void table()
    {
        tableFlag = true;
        markup( "\\begin{ptable}\n" );
    }

    public void table_()
    {
        markup( "\\end{ptable}\n\n" );
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

        markup( "\\begin{ptablerows}{" + justif.toString() + "}\n" );
        if ( grid )
        {
            markup( "\\hline\n" );
        }
        gridFlag = grid;
        cellJustif = justification;
    }

    public void tableRows_()
    {
        markup( "\\end{ptablerows}\n" );
        gridFlag = false;
        cellJustif = null;
    }

    public void tableRow()
    {
        cellCount = 0;
    }

    public void tableRow_()
    {
        markup( "\\\\\n" );
        if ( gridFlag )
        {
            markup( "\\hline\n" );
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
        markup( "}\n" );
    }

    public void author()
    {
        markup( "\\pauthor{" );
    }

    public void author_()
    {
        markup( "}\n" );
    }

    public void date()
    {
        markup( "\\pdate{" );
    }

    public void date_()
    {
        markup( "}\n" );
    }

    public void sectionTitle_()
    {
        markup( "}\n\n" );
    }

    public void paragraph_()
    {
        markup( "\n\n" );
    }

    public void verbatim( boolean boxed )
    {
        if ( boxed )
        {
            markup( "\\begin{pverbatimbox}\n" );
        }
        else
        {
            markup( "\\begin{pverbatim}\n" );
        }
        markup( "\\begin{verbatim}\n" );

        verbatimFlag = true;
        boxFlag = boxed;
    }

    public void verbatim_()
    {
        markup( "\n\\end{verbatim}\n" );
        if ( boxFlag )
        {
            markup( "\\end{pverbatimbox}\n\n" );
        }
        else
        {
            markup( "\\end{pverbatim}\n\n" );
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
        markup( "}\n" );
    }

    public void tableCell()
    {
        if ( cellCount > 0 )
        {
            markup( " &\n" );
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
        markup( "}\n" );
    }

    public void figureGraphics( String name )
    {
        markup( "\\pfiguregraphics{" + name + "}\n" );
    }

    public void horizontalRule()
    {
        markup( "\\phorizontalrule\n\n" );
    }

    public void pageBreak()
    {
        markup( "\\newpage\n\n" );
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
        markup( ( figureFlag || tableFlag || titleFlag ) ? "\\\\\n" : "\\newline\n" );
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

    private static final String defaultPreamble = "\\newcommand{\\ptitle}[1]{\\title{#1}}\n" +
        "\\newcommand{\\pauthor}[1]{\\author{#1}}\n" + "\\newcommand{\\pdate}[1]{\\date{#1}}\n" +
        "\\newcommand{\\pmaketitle}{\\maketitle}\n" + "\\newcommand{\\psectioni}[1]{\\section{#1}}\n" +
        "\\newcommand{\\psectionii}[1]{\\subsection{#1}}\n" + "\\newcommand{\\psectioniii}[1]{\\subsubsection{#1}}\n" +
        "\\newcommand{\\psectioniv}[1]{\\paragraph{#1}}\n" + "\\newcommand{\\psectionv}[1]{\\subparagraph{#1}}\n" +
        "\\newenvironment{plist}{\\begin{itemize}}{\\end{itemize}}\n" +
        "\\newenvironment{pnumberedlist}{\\begin{enumerate}}{\\end{enumerate}}\n" +
        "\\newcommand{\\pdef}[1]{\\textbf{#1}\\hfill}\n" + "\\newenvironment{pdefinitionlist}\n" +
        "{\\begin{list}{}{\\settowidth{\\labelwidth}{\\textbf{999.}}\n" +
        "                \\setlength{\\leftmargin}{\\labelwidth}\n" +
        "                \\addtolength{\\leftmargin}{\\labelsep}\n" +
        "                \\renewcommand{\\makelabel}{\\pdef}}}\n" + "{\\end{list}}\n" +
        "\\newenvironment{pfigure}{\\begin{center}}{\\end{center}}\n" +
        "\\newcommand{\\pfiguregraphics}[1]{\\includegraphics{#1.eps}}\n" +
        "\\newcommand{\\pfigurecaption}[1]{\\\\ \\vspace{\\pparskipamount}\n" +
        "                                \\textit{#1}}\n" +
        "\\newenvironment{ptable}{\\begin{center}}{\\end{center}}\n" +
        "\\newenvironment{ptablerows}[1]{\\begin{tabular}{#1}}{\\end{tabular}}\n" +
        "\\newenvironment{pcell}[1]{\\begin{tabular}[t]{#1}}{\\end{tabular}}\n" +
        "\\newcommand{\\ptablecaption}[1]{\\\\ \\vspace{\\pparskipamount}\n" +
        "                               \\textit{#1}}\n" +
        "\\newenvironment{pverbatim}{\\begin{small}}{\\end{small}}\n" + "\\newsavebox{\\pbox}\n" +
        "\\newenvironment{pverbatimbox}\n" + "{\\begin{lrbox}{\\pbox}\\begin{minipage}{\\linewidth}\\begin{small}}\n" +
        "{\\end{small}\\end{minipage}\\end{lrbox}\\fbox{\\usebox{\\pbox}}}\n" +
        "\\newcommand{\\phorizontalrule}{\\begin{center}\n" +
        "                              \\rule[0.5ex]{\\linewidth}{1pt}\n" +
        "                              \\end{center}}\n" +
        "\\newcommand{\\panchor}[1]{\\textcolor{panchorcolor}{#1}}\n" +
        "\\newcommand{\\plink}[1]{\\textcolor{plinkcolor}{#1}}\n" + "\\newcommand{\\pitalic}[1]{\\textit{#1}}\n" +
        "\\newcommand{\\pbold}[1]{\\textbf{#1}}\n" + "\\newcommand{\\pmonospaced}[1]{\\texttt{\\small #1}}\n\n" +
        "\\documentclass[a4paper]{article}\n" + "\\usepackage{a4wide}\n" + "\\usepackage{color}\n" +
        "\\usepackage{graphics}\n" + "\\usepackage{times}\n" + "\\usepackage[latin1]{inputenc}\n" +
        "\\usepackage[T1]{fontenc}\n\n" + "\\pagestyle{plain}\n\n" + "\\definecolor{plinkcolor}{rgb}{0,0,0.54}\n" +
        "\\definecolor{panchorcolor}{rgb}{0.54,0,0}\n\n" + "\\newlength{\\pparskipamount}\n" +
        "\\setlength{\\pparskipamount}{1ex}\n" + "\\setlength{\\parindent}{0pt}\n" +
        "\\setlength{\\parskip}{\\pparskipamount}\n\n";

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
