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

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.AbstractTextSink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.util.DoxiaUtils;
import org.apache.maven.doxia.util.LineBreaker;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

/**
 * Latex Sink implementation.
 * <br>
 * <b>Note</b>: The encoding used is UTF-8.
 *
 * @version $Id$
 * @since 1.0
 */
public class LatexSink
    extends AbstractTextSink
{
    /**
     * Flag that indicates if the document to be written is only a fragment.
     *
     * This implies that <code>\\begin{document}</code>, <code>\\title{..}</code> will not be output.
     */
    private final boolean fragmentDocument;

    private boolean ignoreText;

    private final LineBreaker out;

    private final String sinkCommands;

    private final String preamble;

    private boolean titleFlag;

    private int numberedListNesting;

    private boolean verbatimFlag;

    private boolean figureFlag;

    private boolean tableFlag;

    private boolean gridFlag;

    private int[] cellJustif;

    private int cellCount;

    private boolean isTitle;

    private String title;

    /** Keep track of the closing tags for inline events. */
    protected Stack<List<String>> inlineStack = new Stack<>();

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Constructor, initialize the Writer and the variables.
     *
     * @param out not null writer to write the result. <b>Should</b> be an UTF-8 Writer.
     * You could use <code>newWriter</code> methods from {@link org.codehaus.plexus.util.WriterFactory}.
     */
    protected LatexSink( Writer out )
    {
        this( out, null, null );
    }

    /**
     * Constructor, initialize the Writer and the variables.
     *
     * @param out not null writer to write the result. <b>Should</b> be an UTF-8 Writer.
     * You could use <code>newWriter</code> methods from {@link org.codehaus.plexus.util.WriterFactory}.
     * @param sinkCommands A String representation of commands that go before \documentclass.
     * @param preamble A String representation of commands that go between \documentclass and \begin{document}.
     */
    protected LatexSink( Writer out, String sinkCommands, String preamble )
    {
        this( out, sinkCommands, preamble, false );
    }

    /**
     * Constructor, initialize the Writer and the variables.
     *
     * @param out not null writer to write the result. <b>Should</b> be an UTF-8 Writer.
     * You could use <code>newWriter</code> methods from {@link org.codehaus.plexus.util.WriterFactory}.
     * @param sinkCommands A String representation of commands that go before \documentclass.
     * @param preamble A String representation of commands that go between \documentclass and \begin{document}.
     * @param fragmentDocument If this receives events that that are only part of a document.
     * Typically, headers are omitted if this is true.
     */
    protected LatexSink( Writer out, String sinkCommands, String preamble, boolean fragmentDocument )
    {
        this.out = new LineBreaker( out );

        if ( sinkCommands == null )
        {
            sinkCommands = defaultSinkCommands();
        }
        if ( preamble == null )
        {
            preamble = defaultPreamble();
        }

        this.sinkCommands = sinkCommands;
        this.preamble = preamble;
        this.fragmentDocument = fragmentDocument;

        init();
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
        head( null );
    }

    /** {@inheritDoc} */
    public void head( SinkEventAttributes attributes )
    {
        init();

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
        body( null );
    }

    /** {@inheritDoc} */
    public void body( SinkEventAttributes attributes )
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
        title( null );
    }

    /** {@inheritDoc} */
    public void title( SinkEventAttributes attributes )
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
        author( null );
    }

    /** {@inheritDoc} */
    public void author( SinkEventAttributes attributes )
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
        date( null );
    }

    /** {@inheritDoc} */
    public void date( SinkEventAttributes attributes )
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

    /** {@inheritDoc} */
    public void sectionTitle( int level, SinkEventAttributes attributes )
    {
        isTitle = true;
    }

    /** {@inheritDoc} */
    public void sectionTitle_( int level )
    {
        String command = "";
        switch ( level )
        {
            case SECTION_LEVEL_1:
                command = "section";
                break;
            case SECTION_LEVEL_2:
                command = "subsection";
                break;
            case SECTION_LEVEL_3:
                command = "subsubsection";
                break;
            case SECTION_LEVEL_4:
                command = "paragraph";
                break;
            case SECTION_LEVEL_5:
                command = "subparagraph";
                break;
            default:
                throw new IllegalArgumentException( "Not a section level: " + level );
        }

        isTitle = false;

        if ( StringUtils.isNotEmpty( title ) )
        {
            markup( EOL + "\\" + command + "{" + title + "}" + EOL );

            title = null;
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
        sectionTitle( SECTION_LEVEL_1, null );
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle1_()
    {
        sectionTitle_( SECTION_LEVEL_1 );
    }

    // ----------------------------------------------------------------------
    // Section Title 2
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void sectionTitle2()
    {
        sectionTitle( SECTION_LEVEL_2, null );
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle2_()
    {
        sectionTitle_( SECTION_LEVEL_2 );
    }

    // ----------------------------------------------------------------------
    // Section Title 3
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void sectionTitle3()
    {
        sectionTitle( SECTION_LEVEL_3, null );
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle3_()
    {
        sectionTitle_( SECTION_LEVEL_3 );
    }

    // ----------------------------------------------------------------------
    // Section Title 4
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void sectionTitle4()
    {
        sectionTitle( SECTION_LEVEL_4, null );
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle4_()
    {
        sectionTitle_( SECTION_LEVEL_4 );
    }

    // ----------------------------------------------------------------------
    // Section Title 5
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void sectionTitle5()
    {
        sectionTitle( SECTION_LEVEL_5, null );
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle5_()
    {
        sectionTitle_( SECTION_LEVEL_5 );
    }

    // ----------------------------------------------------------------------
    // List
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void list()
    {
        list( null );
    }

    /** {@inheritDoc} */
    public void list( SinkEventAttributes attributes )
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
        listItem( null );
    }

    /** {@inheritDoc} */
    public void listItem( SinkEventAttributes attributes )
    {
        markup( EOL + "\\item " );
    }

    /**
     * {@inheritDoc}
     */
    public void numberedList( int numbering )
    {
        numberedList( numbering, null );
    }

    /** {@inheritDoc} */
    public void numberedList( int numbering, SinkEventAttributes attributes )
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
        numberedListItem( null );
    }

    /** {@inheritDoc} */
    public void numberedListItem( SinkEventAttributes attributes )
    {
        markup( "\\item " );
    }

    /**
     * {@inheritDoc}
     */
    public void definitionList()
    {
        definitionList( null );
    }

    /** {@inheritDoc} */
    public void definitionList( SinkEventAttributes attributes )
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
        definedTerm( null );
    }

    /** {@inheritDoc} */
    public void definedTerm( SinkEventAttributes attributes )
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

    /** {@inheritDoc} */
    public void definitionListItem()
    {
        definitionListItem( null );
    }

    /** {@inheritDoc} */
    public void definitionListItem( SinkEventAttributes attributes )
    {
        // nop
    }

    /** {@inheritDoc} */
    public void definitionListItem_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void definition()
    {
        definition( null );
    }

    /** {@inheritDoc} */
    public void definition( SinkEventAttributes attributes )
    {
        // nop
    }

    /** {@inheritDoc} */
    public void definition_()
    {
        // nop
    }

    // ----------------------------------------------------------------------
    // Figure
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void figure()
    {
        figure( null );
    }

    /** {@inheritDoc} */
    public void figure( SinkEventAttributes attributes )
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
        figureGraphics( name, null );
    }

    /** {@inheritDoc} */
    public void figureGraphics( String src, SinkEventAttributes attributes )
    {
        if ( !src.toLowerCase( Locale.ENGLISH ).endsWith( ".eps" ) )
        {
            getLog().warn( "[Latex Sink] Found non-eps figure graphics!" );
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
        figureCaption( null );
    }

    /** {@inheritDoc} */
    public void figureCaption( SinkEventAttributes attributes )
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
        table( null );
    }

    /** {@inheritDoc} */
    public void table( SinkEventAttributes attributes )
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
        StringBuilder justif = new StringBuilder();
        for ( int i1 : justification )
        {
            if ( grid )
            {
                justif.append( '|' );
            }
            switch ( i1 )
            {
                case Sink.JUSTIFY_CENTER:
                    justif.append( 'c' );
                    break;
                case Sink.JUSTIFY_LEFT:
                    justif.append( 'l' );
                    break;
                case Sink.JUSTIFY_RIGHT:
                    justif.append( 'r' );
                    break;
                default:
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
        tableRow( null );
    }

    /** {@inheritDoc} */
    public void tableRow( SinkEventAttributes attributes )
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
        tableCell( (SinkEventAttributes) null );
    }

    /** {@inheritDoc} */
    public void tableCell( String width )
    {
        SinkEventAttributeSet att = new SinkEventAttributeSet();
        att.addAttribute( javax.swing.text.html.HTML.Attribute.WIDTH, width );

        tableCell( att );
    }

    /** {@inheritDoc} */
    public void tableCell( SinkEventAttributes attributes )
    {
        tableCell( false );
    }

    /**
     * {@inheritDoc}
     */
    public void tableCell_()
    {
        markup( "\\end{tabular}" );
        ++cellCount;
    }

    /**
     * {@inheritDoc}
     */
    public void tableHeaderCell()
    {
        tableCell( (SinkEventAttributes) null );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell( String width )
    {
        SinkEventAttributeSet att = new SinkEventAttributeSet();
        att.addAttribute( javax.swing.text.html.HTML.Attribute.WIDTH, width );

        tableHeaderCell( att );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell( SinkEventAttributes attributes )
    {
        tableCell( true );
    }

    /**
     * {@inheritDoc}
     */
    public void tableHeaderCell_()
    {
        tableCell_();
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
            case Sink.JUSTIFY_LEFT:
                justif = 'l';
                break;
            case Sink.JUSTIFY_RIGHT:
                justif = 'r';
                break;
            case Sink.JUSTIFY_CENTER:
            default:
                justif = 'c';
                break;
        }
        markup( "\\begin{tabular}[t]{" + justif + "}" );
    }

    /**
     * {@inheritDoc}
     */
    public void tableCaption()
    {
        tableCaption( null );
    }

    /** {@inheritDoc} */
    public void tableCaption( SinkEventAttributes attributes )
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
        paragraph( null );
    }

    /** {@inheritDoc} */
    public void paragraph( SinkEventAttributes attributes )
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
        verbatim( boxed ? SinkEventAttributeSet.BOXED : null );
    }

    /** {@inheritDoc} */
    public void verbatim( SinkEventAttributes attributes )
    {
        boolean boxed = false;

        if ( attributes != null && attributes.isDefined( SinkEventAttributes.DECORATION ) )
        {
            boxed = "boxed".equals(
                attributes.getAttribute( SinkEventAttributes.DECORATION ) );
        }

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
    }

    /**
     * {@inheritDoc}
     */
    public void verbatim_()
    {
        markup( EOL + "\\end{Verbatim}" + EOL );
        markup( "\\end{small}" + EOL );

        verbatimFlag = false;
    }

    /**
     * {@inheritDoc}
     */
    public void horizontalRule()
    {
        horizontalRule( null );
    }

    /** {@inheritDoc} */
    public void horizontalRule( SinkEventAttributes attributes )
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
        anchor( name, null );
    }

    /** {@inheritDoc} */
    public void anchor( String name, SinkEventAttributes attributes )
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
        link( name, null );
    }

    /** {@inheritDoc} */
    public void link( String name, SinkEventAttributes attributes )
    {
        // TODO: use \\url for simple links
        if ( DoxiaUtils.isExternalLink( name ) )
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
                markup( "\\textit{" );
                tags.add( 0, "}" );
            }

            if ( attributes.containsAttribute( SinkEventAttributes.SEMANTICS, "bold" ) )
            {
                markup( "\\textbf{" );
                tags.add( 0, "}" );
            }

            if ( attributes.containsAttribute( SinkEventAttributes.SEMANTICS, "code" ) )
            {
                markup( "\\texttt{\\small " );
                tags.add( 0, "}" );
            }

        }

        inlineStack.push( tags );
    }

    /** {@inheritDoc} */
    public void inline_()
    {
        for ( String tag: inlineStack.pop() )
        {
            markup( tag );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void italic()
    {
        inline( SinkEventAttributeSet.Semantics.ITALIC );
    }

    /**
     * {@inheritDoc}
     */
    public void italic_()
    {
        inline_();
    }

    /**
     * {@inheritDoc}
     */
    public void bold()
    {
        inline( SinkEventAttributeSet.Semantics.BOLD );
    }

    /**
     * {@inheritDoc}
     */
    public void bold_()
    {
        inline_();
    }

    /**
     * {@inheritDoc}
     */
    public void monospaced()
    {
        inline( SinkEventAttributeSet.Semantics.CODE );
    }

    /**
     * {@inheritDoc}
     */
    public void monospaced_()
    {
        inline_();
    }

    /**
     * {@inheritDoc}
     */
    public void lineBreak()
    {
        lineBreak( null );
    }

    /** {@inheritDoc} */
    public void lineBreak( SinkEventAttributes attributes )
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
        text( text, null );
    }

    /** {@inheritDoc} */
    public void text( String text, SinkEventAttributes attributes )
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

    /** {@inheritDoc} */
    public void rawText( String text )
    {
        verbatimContent( text );
    }

    /** {@inheritDoc} */
    public void comment( String comment )
    {
        rawText( EOL + "%" + comment );
    }

    /**
     * {@inheritDoc}
     *
     * Unkown events just log a warning message but are ignored otherwise.
     * @see org.apache.maven.doxia.sink.Sink#unknown(String,Object[],SinkEventAttributes)
     */
    public void unknown( String name, Object[] requiredParams, SinkEventAttributes attributes )
    {
        getLog().warn( "[Latex Sink] Unknown Sink event: '" + name + "', ignoring!" );
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
        StringBuilder buffer = new StringBuilder( length );

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

        init();
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
        try
        {
            return IOUtil.toString( getDefaultSinkCommands() );
        }
        catch ( IOException ioe )
        {
            // this should not happen
            getLog().warn( "Could not read default LaTeX commands, the generated LaTeX file will not compile!" );
            getLog().debug( ioe );

            return "";
        }
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

    /** {@inheritDoc} */
    protected void init()
    {
        super.init();

        this.ignoreText = false;
        this.titleFlag = false;
        this.numberedListNesting = 0;
        this.verbatimFlag = false;
        this.figureFlag = false;
        this.tableFlag = false;
        this.gridFlag = false;
        this.cellJustif = null;
        this.cellCount = 0;
        this.isTitle = false;
        this.title = null;
    }
}
