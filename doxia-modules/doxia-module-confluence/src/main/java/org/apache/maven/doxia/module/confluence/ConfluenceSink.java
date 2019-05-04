package org.apache.maven.doxia.module.confluence;

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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.text.html.HTML.Attribute;

import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.AbstractTextSink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.util.HtmlTools;
import org.codehaus.plexus.util.StringUtils;

/**
 * Confluence Sink implementation.
 * <br>
 * <b>Note</b>: The encoding used is UTF-8.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public class ConfluenceSink
    extends AbstractTextSink
    implements ConfluenceMarkup
{
    /**  The writer to use. */
    private final PrintWriter out;

    /**  The writer to use. */
    private StringWriter writer;

    /** An indication on if we're in head mode. */
    private boolean headFlag;

    private int levelList = 0;

    /**  listStyles. */
    private final Stack<String> listStyles;

    /** An indication on if we're in monospaced mode. */
    private boolean monospacedFlag;

    /** Keep track of the closing tags for inline events. */
    protected Stack<List<String>> inlineStack = new Stack<>();

    /** An indication on if we're in verbatim box mode. */
    private boolean verbatimBoxedFlag;

    /** An indication on if we're in table mode. */
    private boolean tableFlag;

    /** An indication on if we're in table header mode. */
    private boolean tableHeaderFlag;

    /** The link name. */
    private String linkName;

    /**
     * Constructor, initialize the Writer and the variables.
     *
     * @param writer not null writer to write the result. <b>Should</b> be an UTF-8 Writer.
     * You could use <code>newWriter</code> methods from {@link org.codehaus.plexus.util.WriterFactory}.
     */
    protected ConfluenceSink( Writer writer )
    {
        this.out = new PrintWriter( writer );
        this.listStyles = new Stack<>();

        init();
    }

    /** {@inheritDoc} */
    public void anchor( String name )
    {
        write( ANCHOR_START_MARKUP + name + ANCHOR_END_MARKUP );
    }

    /** {@inheritDoc} */
    public void anchor( String name, SinkEventAttributes attributes )
    {
        anchor( name );
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void anchor_()
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void author()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void author( SinkEventAttributes attributes )
    {
        author();
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void author_()
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void body()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void body( SinkEventAttributes attributes )
    {
        body();
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void body_()
    {
        // nop
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

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void comment( String comment )
    {
        // nop
    }

    /** {@inheritDoc} */
    public void close()
    {
        out.write( writer.toString() );
        out.close();

        init();
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void date()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void date( SinkEventAttributes attributes )
    {
        date();
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void date_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void definedTerm()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void definedTerm( SinkEventAttributes attributes )
    {
        definedTerm();
    }

    /**
     * {@inheritDoc}
     */
    public void definedTerm_()
    {
        writeEOL( true );
    }

    /**
     * {@inheritDoc}
     */
    public void definition()
    {
        writer.write( CITATION_START_MARKUP );
    }

    /**
     * {@inheritDoc}
     */
    public void definition( SinkEventAttributes attributes )
    {
        definition();
    }

    /**
     * {@inheritDoc}
     */
    public void definition_()
    {
        writer.write( CITATION_END_MARKUP );
        writeEOL( true );
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void definitionList()
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void definitionList( SinkEventAttributes attributes )
    {
        // nop
    }

    /**
     * {@inheritDoc}
     */
    public void definitionList_()
    {
        writeEOL();
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void definitionListItem()
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void definitionListItem( SinkEventAttributes attributes )
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void definitionListItem_()
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void figure()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void figure( SinkEventAttributes attributes )
    {
        figure();
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void figure_()
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void figureCaption()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void figureCaption( SinkEventAttributes attributes )
    {
        figureCaption();
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void figureCaption_()
    {
        // nop;
    }

    /** {@inheritDoc} */
    public void figureGraphics( String name )
    {
        writeEOL();
        write( FIGURE_START_MARKUP + name + FIGURE_END_MARKUP );
    }

    /** {@inheritDoc} */
    public void figureGraphics( String src, SinkEventAttributes attributes )
    {
        figureGraphics( src );
        if ( attributes != null && attributes.getAttribute( Attribute.ALT.toString() ) != null )
        {
            write( attributes.getAttribute( Attribute.ALT.toString() ).toString() );
            writeEOL( true );
        }
    }

    /** {@inheritDoc} */
    public void flush()
    {
        close();
        writer.flush();
    }

    /** {@inheritDoc} */
    public void head()
    {
        init();

        headFlag = true;
    }

    /** {@inheritDoc} */
    public void head( SinkEventAttributes attributes )
    {
        head();
    }

    /** {@inheritDoc} */
    public void head_()
    {
        headFlag = false;
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void horizontalRule()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void horizontalRule( SinkEventAttributes attributes )
    {
        horizontalRule();
    }

    /** {@inheritDoc} */
    public void inline()
    {
        inline( null );
    }

    /** {@inheritDoc} */
    public void inline( SinkEventAttributes attributes )
    {
        if ( !headFlag )
        {
            List<String> tags = new ArrayList<>();

            if ( attributes != null )
            {

                if ( attributes.containsAttribute( SinkEventAttributes.SEMANTICS, "italic" ) )
                {
                    write( ITALIC_START_MARKUP );
                    tags.add( 0, ITALIC_END_MARKUP );
                }

                if ( attributes.containsAttribute( SinkEventAttributes.SEMANTICS, "bold" ) )
                {
                    write( BOLD_START_MARKUP );
                    tags.add( 0, BOLD_END_MARKUP );
                }

                if ( attributes.containsAttribute( SinkEventAttributes.SEMANTICS, "code" ) )
                {
                    write( MONOSPACED_START_MARKUP );
                    tags.add( 0, MONOSPACED_END_MARKUP );
                }

            }

            inlineStack.push( tags );
        }
    }

    /** {@inheritDoc} */
    public void inline_()
    {
        if ( !headFlag )
        {
            for ( String tag: inlineStack.pop() )
            {
                write( tag );
            }
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
    public void lineBreak()
    {
        write( LINE_BREAK_MARKUP );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void lineBreak( SinkEventAttributes attributes )
    {
        lineBreak();
    }

    /** {@inheritDoc} */
    public void link( String name )
    {
        linkName = name;
    }

    /** {@inheritDoc} */
    public void link( String name, SinkEventAttributes attributes )
    {
        link( name );
    }

    /** {@inheritDoc} */
    public void link_()
    {
        linkName = null;
        write( LINK_END_MARKUP );
    }

    /** {@inheritDoc} */
    public void list()
    {
        if ( !writer.toString().endsWith( EOL + EOL ) )
        {
            writeEOL( true );
        }

        levelList++;
    }

    /** {@inheritDoc} */
    public void list( SinkEventAttributes attributes )
    {
        list();
    }

    /** {@inheritDoc} */
    public void list_()
    {
        levelList--;
        if ( levelList == 0 )
        {
            writeEOL( true );
            writeEOL();
        }
    }

    /** {@inheritDoc} */
    public void listItem()
    {
        write( StringUtils.repeat( "*", levelList ) + " " );
    }

    /** {@inheritDoc} */
    public void listItem( SinkEventAttributes attributes )
    {
        listItem();
    }

    /** {@inheritDoc} */
    public void listItem_()
    {
        writeEOL( true );
    }

    /** {@inheritDoc} */
    public void monospaced()
    {
        monospacedFlag = true;
        inline( SinkEventAttributeSet.Semantics.CODE );
    }

    /** {@inheritDoc} */
    public void monospaced_()
    {
        monospacedFlag = false;
        inline_();
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void nonBreakingSpace()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void numberedList( int numbering )
    {
        if ( !writer.toString().endsWith( EOL + EOL ) )
        {
            writeEOL( true );
        }
        levelList++;

        String style;
        switch ( numbering )
        {
            case NUMBERING_UPPER_ALPHA:
            case NUMBERING_LOWER_ALPHA:
            case NUMBERING_UPPER_ROMAN:
            case NUMBERING_LOWER_ROMAN:
            case NUMBERING_DECIMAL:
            default:
                style = NUMBERING_MARKUP;
        }

        listStyles.push( style );
    }

    /** {@inheritDoc} */
    public void numberedList( int numbering, SinkEventAttributes attributes )
    {
        numberedList( numbering );
    }

    /** {@inheritDoc} */
    public void numberedList_()
    {
        levelList--;
        if ( levelList == 0 )
        {
            writeEOL( true );
            writeEOL();
        }
        listStyles.pop();
    }

    /** {@inheritDoc} */
    public void numberedListItem()
    {
        writeEOL( true );
        String style = listStyles.peek();
        // We currently only handle one type of numbering style for Confluence,
        // so we can just repeat the latest numbering markup for each level.
        // If we ever decide to handle multiple different numbering styles, we'd
        // need to traverse the entire listStyles stack and use the correct
        // numbering style for each level.
        write( StringUtils.repeat( style, levelList ) + SPACE );
    }

    /** {@inheritDoc} */
    public void numberedListItem( SinkEventAttributes attributes )
    {
        numberedListItem();
    }

    /** {@inheritDoc} */
    public void numberedListItem_()
    {
        writeEOL( true );
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void pageBreak()
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void paragraph()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void paragraph( SinkEventAttributes attributes )
    {
        paragraph();
    }

    /** {@inheritDoc} */
    public void paragraph_()
    {
        writeEOL( true );
        writeEOL();
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void rawText( String text )
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void section( int level, SinkEventAttributes attributes )
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void section1()
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void section1_()
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void section2()
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void section2_()
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void section3()
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void section3_()
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void section4()
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void section4_()
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void section5()
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void section5_()
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void section_( int level )
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void sectionTitle()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void sectionTitle( int level, SinkEventAttributes attributes )
    {
        if ( level > 0 && level < 6 )
        {
            write( "h" + level + ". " );
        }
    }

    /** {@inheritDoc} */
    public void sectionTitle1()
    {
        sectionTitle( 1, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle1_()
    {
        sectionTitle_( 1 );
    }

    /** {@inheritDoc} */
    public void sectionTitle2()
    {
        sectionTitle( 2, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle2_()
    {
        sectionTitle_( 2 );
    }

    /** {@inheritDoc} */
    public void sectionTitle3()
    {
        sectionTitle( 3, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle3_()
    {
        sectionTitle_( 3 );
    }

    /** {@inheritDoc} */
    public void sectionTitle4()
    {
        sectionTitle( 4, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle4_()
    {
        sectionTitle_( 4 );
    }

    /** {@inheritDoc} */
    public void sectionTitle5()
    {
        sectionTitle( 5, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle5_()
    {
        sectionTitle_( 5 );
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void sectionTitle_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void sectionTitle_( int level )
    {
        writeEOL( true );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void table()
    {
        // nop
        tableFlag = true;
        writeEOL( true );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void table( SinkEventAttributes attributes )
    {
        table();
    }

    /** {@inheritDoc} */
    public void table_()
    {
        tableFlag = false;
        writeEOL( true );
        writeEOL();
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void tableCaption()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void tableCaption( SinkEventAttributes attributes )
    {
        tableCaption();
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void tableCaption_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void tableCell()
    {
        write( " " );
    }

    /** {@inheritDoc} */
    public void tableCell( SinkEventAttributes attributes )
    {
        tableCell();
    }

    /** {@inheritDoc} */
    public void tableCell( String width )
    {
        tableCell();
    }

    /** {@inheritDoc} */
    public void tableCell_()
    {
        write( " " );
        write( TABLE_CELL_MARKUP );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell()
    {
        tableHeaderFlag = true;
        write( TABLE_CELL_HEADER_START_MARKUP );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell( SinkEventAttributes attributes )
    {
        tableHeaderCell();
    }

    /** {@inheritDoc} */
    public void tableHeaderCell( String width )
    {
        tableHeaderCell();
    }

    /** {@inheritDoc} */
    public void tableHeaderCell_()
    {
        write( TABLE_CELL_HEADER_END_MARKUP );
    }

    /** {@inheritDoc} */
    public void tableRow()
    {
        write( TABLE_ROW_MARKUP );
    }

    /** {@inheritDoc} */
    public void tableRow( SinkEventAttributes attributes )
    {
        tableRow();
    }

    /** {@inheritDoc} */
    public void tableRow_()
    {
        if ( tableHeaderFlag )
        {
            tableHeaderFlag = false;
            write( TABLE_ROW_MARKUP );
        }
        writeEOL( true );
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void tableRows( int[] justification, boolean grid )
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void tableRows_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void text( String text )
    {
        if ( headFlag )
        {
            return;
        }

        if ( linkName != null )
        {
            write( LINK_START_MARKUP );
        }

        if ( tableFlag )
        {
            // Remove line breaks, because it interferes with the table syntax
            String strippedText = StringUtils.replace( text, "\n", "" );
            // Trim if only whitespace, to handle ignorable whitespace from xdoc documents
            if ( StringUtils.isWhitespace( strippedText ) )
            {
                strippedText = StringUtils.trim( strippedText );
            }
            content( strippedText );
        }
        else if ( monospacedFlag )
        {
            content( text, true );
        }
        else
        {
            content( text );
        }

        if ( linkName != null )
        {
            write( LINK_MIDDLE_MARKUP + linkName );
        }
    }

    /** {@inheritDoc} */
    public void text( String text, SinkEventAttributes attributes )
    {
        if ( attributes == null )
        {
            text( text );
        }
        else
        {
            if ( attributes.containsAttribute( SinkEventAttributes.DECORATION, "underline" ) )
            {
                write( UNDERLINED_START_MARKUP );
            }
            else if ( attributes.containsAttribute( SinkEventAttributes.DECORATION, "line-through" ) )
            {
                write( STRIKETHROUGH_START_MARKUP );
            }
            if ( attributes.containsAttribute( SinkEventAttributes.VALIGN, "sub" ) )
            {
                write( SUBSCRIPT_START_MARKUP );
            }
            else if ( attributes.containsAttribute( SinkEventAttributes.VALIGN, "sup" ) )
            {
                write( SUPERSCRIPT_START_MARKUP );
            }

            text( text );

            if ( attributes.containsAttribute( SinkEventAttributes.VALIGN, "sup" ) )
            {
                write( SUPERSCRIPT_END_MARKUP );
            }
            else if ( attributes.containsAttribute( SinkEventAttributes.VALIGN, "sub" ) )
            {
                write( SUBSCRIPT_END_MARKUP );
            }
            if ( attributes.containsAttribute( SinkEventAttributes.DECORATION, "line-through" ) )
            {
                write( STRIKETHROUGH_END_MARKUP );
            }
            else if ( attributes.containsAttribute( SinkEventAttributes.DECORATION, "underline" ) )
            {
                write( UNDERLINED_END_MARKUP );
            }
        }
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void title()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void title( SinkEventAttributes attributes )
    {
        title();
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void title_()
    {
        // nop
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void unknown( String name, Object[] requiredParams, SinkEventAttributes attributes )
    {
        // nop
    }

    /** {@inheritDoc} */
    public void verbatim( boolean boxed )
    {
        if ( boxed )
        {
            verbatimBoxedFlag = true;
        }

        if ( verbatimBoxedFlag )
        {
            write( "{code:borderStyle=solid}" );
        }
        else
        {
            write( "{noformat}" );
        }
        writeEOL( true );
    }

    /** {@inheritDoc} */
    public void verbatim_()
    {
        if ( verbatimBoxedFlag )
        {
            write( "{code}" );
        }
        else
        {
            write( "{noformat}" );
        }

        writeEOL( true );
        writeEOL();
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    private void write( String text )
    {
        writer.write( unifyEOLs( text ) );
    }

    /**
     * Writes a system EOL.
     */
    private void writeEOL()
    {
        write( EOL );
    }

    /**
     * Writes a system EOL, with or without trim.
     */
    private void writeEOL( boolean trim )
    {
        if ( !trim )
        {
            writeEOL();
            return;
        }

        String tmp = writer.toString().trim();
        writer = new StringWriter();
        writer.write( tmp );
        write( EOL );
    }

    /**
     * Write HTML escaped text to output.
     *
     * @param text The text to write.
     */
    protected void content( String text )
    {
        write( escapeHTML( text ) );
    }

    /**
     * Write HTML, and optionally Confluence, escaped text to output.
     *
     * @param text The text to write.
     */
    protected void content( String text, boolean escapeConfluence )
    {
        if ( escapeConfluence )
        {
            write( escapeConfluence( escapeHTML( text ) ) );
        }
        else
        {
            content( text );
        }
    }

    /** {@inheritDoc} */
    protected void init()
    {
        super.init();

        this.writer = new StringWriter();
        this.monospacedFlag = false;
        this.headFlag = false;
        this.levelList = 0;
        this.listStyles.clear();
        this.verbatimBoxedFlag = false;
        this.tableHeaderFlag = false;
        this.linkName = null;
    }

    /**
     * Escape characters that have special meaning in Confluence.
     *
     * @param text the String to escape, may be null
     * @return the text escaped, "" if null String input
     */
    protected static String escapeConfluence( String text )
    {
        if ( text == null )
        {
            return "";
        }
        else
        {
            int length = text.length();
            StringBuilder buffer = new StringBuilder( length );

            for ( int i = 0; i < length; ++i )
            {
                char c = text.charAt( i );
                switch ( c )
                {
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
    }

    /**
     * Forward to HtmlTools.escapeHTML( text ).
     *
     * @param text the String to escape, may be null
     * @return the text escaped, "" if null String input
     * @see org.apache.maven.doxia.util.HtmlTools#escapeHTML(String)
     */
    protected static String escapeHTML( String text )
    {
        return HtmlTools.escapeHTML( text );
    }
}
