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
import org.apache.maven.doxia.sink.AbstractTextSink;
import org.apache.maven.doxia.util.LineBreaker;
import org.apache.maven.doxia.util.StructureSinkUtils;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

/**
 * Latex Sink implementation.
 *
 * @since 1.0
 * @plexus.component role="org.apache.maven.doxia.sink.Sink" role-hint="tex"
 */
public class LatexSink
    extends AbstractTextSink
{
    /**
     * Flag that indicates if the document to be written is only a fragment.
     *
     * This implies that <code>\\begin{document}</code>, <code>\\title{..}</code> will not be output.
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

    /**
     * Constructor.
     *
     * @param out The writer to use.
     */
    public LatexSink( Writer out )
    {
        this.out = new LineBreaker( out );
        this.sinkCommands = defaultSinkCommands();
        this.preamble = defaultPreamble();
    }

    /**
     * Constructor.
     *
     * @param out The writer to use.
     * @param sinkCommands A String representation of commands that go before \documentclass.
     * @param preamble A String representation of commands that go between \documentclass and \begin{document}.
     */
    public LatexSink( Writer out, String sinkCommands, String preamble )
    {
        this( out, sinkCommands, preamble, false );
    }

    /**
     * Constructor.
     *
     * @param out The writer to use.
     * @param sinkCommands A String representation of commands that go before \documentclass.
     * @param preamble A String representation of commands that go between \documentclass and \begin{document}.
     * @param fragmentDocument If this receives events that that are only part of a document.
     * Typically, headers are omitted if this is true.     */
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

    /**
     * Returns a default \documentclass declaration.
     *
     * @return String.
     */
    protected String getDocumentStart()
    {
        return "\\documentclass[a4paper]{article}" + EOL + EOL;
    }

    /**
     * Returns a default \begin{document} declaration.
     *
     * @return String.
     */
    protected String getDocumentBegin()
    {
        return "\\begin{document}" + EOL + EOL;
    }

    /**
     * Returns a default \end{document} declaration.
     *
     * @return String.
     */
    protected String getDocumentEnd()
    {
        return "\\end{document}" + EOL;
    }

    // ----------------------------------------------------------------------
    // Sink Implementation
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public void body()
    {
        if ( titleFlag )
        {
            if ( fragmentDocument  )
            {
                markup( "\\section" );
            }
            else
            {
                titleFlag = false;
                markup( "\\maketitle" + EOL + EOL );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void body_()
    {
        if ( !fragmentDocument )
        {
            markup( getDocumentEnd() );
        }

        flush();
    }

    /**
     * {@inheritDoc}
     */
    public void title()
    {
        if ( !fragmentDocument )
        {
            titleFlag = true;
            markup( "\\title{" );
        }
        else
        {
            ignoreText = true;
        }
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public void author()
    {
        if ( !fragmentDocument )
        {
            markup( "\\author{" );
        }
        else
        {
            ignoreText = true;
        }
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public void date()
    {
        if ( !fragmentDocument )
        {
            markup( "\\date{" );
        }
        else
        {
            ignoreText = true;
        }
    }

    /**
     * {@inheritDoc}
     */
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

    // ----------------------------------------------------------------------
    // Section Title 1
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void sectionTitle1()
    {
        isTitle = true;
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle1_()
    {
        isTitle = false;

        if ( StringUtils.isNotEmpty( title ) )
        {
            markup( EOL + "\\section{" + title + "}" + EOL );

            title = null;
        }
    }

    // ----------------------------------------------------------------------
    // Section Title 2
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void sectionTitle2()
    {
        isTitle = true;
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle2_()
    {
        isTitle = false;

        if ( StringUtils.isNotEmpty( title ) )
        {
            markup( EOL + "\\subsection{" + title + "}" + EOL );

            title = null;
        }
    }

    // ----------------------------------------------------------------------
    // Section Title 3
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void sectionTitle3()
    {
        isTitle = true;
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle3_()
    {
        isTitle = false;

        if ( StringUtils.isNotEmpty( title ) )
        {
            markup( EOL + "\\subsubsection{" + title + "}" + EOL );

            title = null;
        }
    }

    // ----------------------------------------------------------------------
    // Section Title 4
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void sectionTitle4()
    {
        isTitle = true;
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle4_()
    {
        isTitle = false;

        if ( StringUtils.isNotEmpty( title ) )
        {
            markup( EOL + "\\paragraph{" + title + "}" + EOL );

            title = null;
        }
    }

    // ----------------------------------------------------------------------
    // Section Title 5
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void sectionTitle5()
    {
        isTitle = true;
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle5_()
    {
        isTitle = false;

        if ( StringUtils.isNotEmpty( title ) )
        {
            markup( EOL + "\\subparagraph{" + title + "}" + EOL );

            title = null;
        }
    }

    // ----------------------------------------------------------------------
    // List
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void list()
    {
        markup( EOL + "\\begin{itemize}" );
    }

    /**
     * {@inheritDoc}
     */
    public void list_()
    {
        markup( EOL + "\\end{itemize}" + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void listItem()
    {
        markup( EOL + "\\item " );
    }

    /**
     * {@inheritDoc}
     */
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

        markup( EOL + "\\begin{enumerate}" + EOL );
        markup( "\\renewcommand{\\the" + counter + "}{\\" + style + "{" + counter + "}}" + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void numberedList_()
    {
        markup( EOL + "\\end{enumerate}" + EOL );
        --numberedListNesting;
    }

    /**
     * {@inheritDoc}
     */
    public void numberedListItem()
    {
        markup( "\\item " );
    }

    /**
     * {@inheritDoc}
     */
    public void definitionList()
    {
        markup( EOL + "\\begin{description}" );
    }

    /**
     * {@inheritDoc}
     */
    public void definitionList_()
    {
        markup( EOL + "\\end{description}" + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void definedTerm()
    {
        markup( EOL + "\\item[\\mbox{" );
    }

    /**
     * {@inheritDoc}
     */
    public void definedTerm_()
    {
        markup( "}] " );
    }

    // ----------------------------------------------------------------------
    // Figure
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void figure()
    {
        figureFlag = true;
        markup( EOL + "\\begin{figure}[htb]" + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void figure_()
    {
        markup( "\\end{figure}" + EOL );
        figureFlag = false;
    }

    /**
     * {@inheritDoc}
     */
    public void figureGraphics( String name )
    {
        String src = name;

        if ( !src.endsWith( ".eps" ) )
        {
            getLog().warn( "[latex-sink] Found non-eps figure graphics!" );
        }

        markup( "\\begin{center}" + EOL );
        markup( "\\includegraphics{" + src + "}" + EOL );
        markup( "\\end{center}" + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void figureCaption()
    {
        markup( "\\caption{" );
    }

    /**
     * {@inheritDoc}
     */
    public void figureCaption_()
    {
        markup( "}" + EOL );
    }

    // ----------------------------------------------------------------------
    // Table
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void table()
    {
        tableFlag = true;
        markup( EOL + "\\begin{table}[htp]" + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void table_()
    {
        markup( "\\end{table}" + EOL );
        tableFlag = false;
    }

    /**
     * {@inheritDoc}
     */
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

        markup( "\\begin{center}" + EOL );
        markup( "\\begin{tabular}{" + justif.toString() + "}" + EOL );
        if ( grid )
        {
            markup( "\\hline" + EOL );
        }
        gridFlag = grid;
        cellJustif = justification;
    }

    /**
     * {@inheritDoc}
     */
    public void tableRows_()
    {
        markup( "\\end{tabular}" + EOL );
        markup( "\\end{center}" + EOL );

        gridFlag = false;
        cellJustif = null;
    }

    /**
     * {@inheritDoc}
     */
    public void tableRow()
    {
        cellCount = 0;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public void tableCell()
    {
        tableCell( false );
    }
    
    /**
     * {@inheritDoc}
     */
    public void tableCell_()
    {
        tableCell_( false );
    }

    /**
     * {@inheritDoc}
     */
    public void tableHeaderCell()
    {
        tableCell( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public void tableHeaderCell_()
    {
        tableCell_( true );
    }
    
    private boolean lastCellWasHeader = false;
    
    /**
     * Starts a table cell.
     *
     * @param header True if this is a header cell.
     */
    private void tableCell( boolean header )
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
        markup( "\\begin{tabular}[t]{" + justif + "}" );
    }

    /**
     * Ends a table cell.
     *
     * @param header True if this is a header cell.
     */
    private void tableCell_( boolean header )
    {
        markup( "\\end{tabular}" );
        ++cellCount;
    }

    /**
     * {@inheritDoc}
     */
    public void tableCaption()
    {
        markup( "\\caption{" );
    }

    /**
     * {@inheritDoc}
     */
    public void tableCaption_()
    {
        markup( "}" + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void paragraph()
    {
        markup( EOL + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void paragraph_()
    {
        markup( EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void verbatim( boolean boxed )
    {
        markup( EOL + "\\begin{small}" + EOL );

        if ( boxed )
        {
            markup( "\\begin{Verbatim}[frame=single]" + EOL );
        }
        else
        {
            markup( "\\begin{Verbatim}" + EOL );
        }

        verbatimFlag = true;
        boxFlag = boxed;
    }

    /**
     * {@inheritDoc}
     */
    public void verbatim_()
    {
        markup( EOL + "\\end{Verbatim}" + EOL );
        markup( "\\end{small}" + EOL );

        verbatimFlag = false;
        boxFlag = false;
    }

    /**
     * {@inheritDoc}
     */
    public void horizontalRule()
    {
        markup( EOL + "\\begin{center}\\rule[0.5ex]{\\linewidth}{1pt}\\end{center}" + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void pageBreak()
    {
        markup( EOL + "\\newpage" + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void anchor( String name )
    {
        markup( "\\hypertarget{" + name + "}{" );
    }

    /**
     * {@inheritDoc}
     */
    public void anchor_()
    {
        markup( "}" );
    }

    /**
     * {@inheritDoc}
     */
    public void link( String name )
    {
        // TODO: use \\url for simple links
        if ( StructureSinkUtils.isExternalLink( name ) )
        {
            markup( "\\href{" + name + "}{" );
        }
        else
        {
            markup( "\\hyperlink{" + name + "}{" );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void link_()
    {
        markup( "}" );
    }

    /**
     * {@inheritDoc}
     */
    public void italic()
    {
        markup( "\\textit{" );
    }

    /**
     * {@inheritDoc}
     */
    public void italic_()
    {
        markup( "}" );
    }

    /**
     * {@inheritDoc}
     */
    public void bold()
    {
        markup( "\\textbf{" );
    }

    /**
     * {@inheritDoc}
     */
    public void bold_()
    {
        markup( "}" );
    }

    /**
     * {@inheritDoc}
     */
    public void monospaced()
    {
        markup( "\\texttt{\\small " );
    }

    /**
     * {@inheritDoc}
     */
    public void monospaced_()
    {
        markup( "}" );
    }

    /**
     * {@inheritDoc}
     */
    public void lineBreak()
    {
        markup( ( figureFlag || tableFlag || titleFlag || verbatimFlag ) ? EOL : "\\newline" + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void nonBreakingSpace()
    {
        markup( "~" );
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * Writes the text, preserving whitespace.
     *
     * @param text the text to write.
     */
    protected void markup( String text )
    {
        if ( text != null )
        {
            out.write( text, /*preserveSpace*/ true );
        }
    }

    /**
     * Writes the text, without preserving whitespace.
     *
     * @param text the text to write.
     */
    protected void content( String text )
    {
        out.write( escaped( text ), /*preserveSpace*/ false );
    }

    /**
     * Writes the text, preserving whitespace.
     *
     * @param text the text to write.
     */
    protected void verbatimContent( String text )
    {
        out.write( text, /*preserveSpace*/ true );
    }

    // -----------------------------------------------------------------------

    /**
     * Escapes special characters.
     *
     * @param text The text to escape.
     * @return The text with special characters replaced.
     */
    public static String escaped( String text )
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

    /**
     * {@inheritDoc}
     */
    public void flush()
    {
        out.flush();
    }

    /**
     * {@inheritDoc}
     */
    public void close()
    {
        out.close();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Returns the default sink commands from a resource.
     *
     * @throws java.io.IOException if the resource file cannot be read.
     * @return InputStream
     */
    private static InputStream getDefaultSinkCommands()
        throws IOException
    {
        return LatexSink.class.getResource( "default_sink_commands.tex" ).openStream();
    }

    /**
     * Returns the default preamble from a resource.
     *
     * @return InputStream
     * @throws java.io.IOException if the resource file cannot be read.
     */
    private static InputStream getDefaultPreamble()
        throws IOException
    {
        return LatexSink.class.getResource( "default_preamble.tex" ).openStream();
    }

    /**
     * Returns the default sink commands.
     *
     * @return String.
     */
    protected String defaultSinkCommands()
    {
        String commands = "";

        try
        {
            commands = IOUtil.toString( getDefaultSinkCommands() );
        }
        catch ( IOException ioe )
        {
            // this should not happen
            getLog().warn( "Could not read default LaTeX commands, the generated LaTeX file will not compile!" );
            getLog().debug( ioe );

            return "";
        }

        return commands;
    }

    /**
     * Returns the default preamble.
     *
     * @return String.
     */
    protected String defaultPreamble()
    {
        try
        {
            return IOUtil.toString( getDefaultPreamble() );
        }
        catch ( IOException ioe )
        {
            // this should not happen
            getLog().warn( "Could not read default LaTeX preamble, the generated LaTeX file will not compile!" );
            getLog().debug( ioe );

            return "";
        }
    }

}
