package org.apache.maven.doxia.module.apt;

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
import java.io.Writer;
import java.util.Stack;

import org.apache.maven.doxia.util.HtmlTools;
import org.apache.maven.doxia.sink.AbstractTextSink;
import org.codehaus.plexus.util.StringUtils;

/**
 * APT Sink implementation.
 *
 * @author eredmond
 * @version $Id$
 * @since 1.0
 * @plexus.component role="org.apache.maven.doxia.sink.Sink" role-hint="apt"
 */
public class AptSink
    extends AbstractTextSink
    implements AptMarkup
{
    // ----------------------------------------------------------------------
    // Instance fields
    // ----------------------------------------------------------------------

    /**  A buffer that holds the current text. */
    private StringBuffer buffer;

    /**  A buffer that holds the table caption. */
    private StringBuffer tableCaptionBuffer;

    /**  author. */
    private String author;

    /**  title. */
    private String title;

    /**  date. */
    private String date;

    /**  tableCaptionFlag. */
    private boolean tableCaptionFlag;

    /**  headerFlag. */
    private boolean headerFlag;

    /**  bufferFlag. */
    private boolean bufferFlag;

    /**  itemFlag. */
    private boolean itemFlag;

    /**  verbatimFlag. */
    private boolean verbatimFlag;

    /**  boxed verbatim. */
    private boolean boxed;

    /**  gridFlag for tables. */
    private boolean gridFlag;

    /**  number of cells in a table. */
    private int cellCount;

    /**  The writer to use. */
    private PrintWriter writer;

    /**  justification of table cells. */
    private int cellJustif[];

    /**  a line of a row in a table. */
    private String rowLine;

    /**  listNestingIndent. */
    private String listNestingIndent;

    /**  listStyles. */
    private Stack listStyles;

    // ----------------------------------------------------------------------
    // Public protected methods
    // ----------------------------------------------------------------------

    /**
     * Constructor, initialize the variables.
     *
     * @param writer The writer to write the result.
     */
    public AptSink( Writer writer )
    {
        this.buffer = new StringBuffer();
        this.tableCaptionBuffer = new StringBuffer();
        this.writer = new PrintWriter( writer );
        this.listNestingIndent = "";
        this.listStyles = new Stack();
    }

    /**
     * Returns the buffer that holds the current text.
     *
     * @return A StringBuffer.
     */
    protected StringBuffer getBuffer()
    {
        return buffer;
    }

    /**
     * Used to determine whether we are in head mode.
     *
     * @param headFlag True for head mode.
     */
    protected void setHeadFlag( boolean headFlag )
    {
        this.headerFlag = headFlag;
    }

    /**
     * Reset all variables.
     */
    protected void resetState()
    {
        headerFlag = false;
        resetBuffer();
        itemFlag = false;
        verbatimFlag = false;
        cellCount = 0;
    }

    /**
     * Reset the StringBuffer.
     */
    protected void resetBuffer()
    {
        buffer = new StringBuffer();
    }

    /**
     * Reset the TableCaptionBuffer.
     */
    protected void resetTableCaptionBuffer()
    {
        tableCaptionBuffer = new StringBuffer();
    }

    /** {@inheritDoc} */
    public void head()
    {
        resetState();
        headerFlag = true;
    }

    /** {@inheritDoc} */
    public void head_()
    {
        headerFlag = false;

        write( HEADER_START_MARKUP + EOL );
        if ( title != null )
        {
            write( " " + title + EOL );
        }
        write( HEADER_START_MARKUP + EOL );
        if ( author != null )
        {
            write( " " + author + EOL );
        }
        write( HEADER_START_MARKUP + EOL );
        if ( date != null )
        {
            write( " " + date + EOL );
        }
        write( HEADER_START_MARKUP + EOL );
    }

    /** {@inheritDoc} */
    public void title_()
    {
        if ( buffer.length() > 0 )
        {
            title = buffer.toString();
            resetBuffer();
        }
    }

    /** {@inheritDoc} */
    public void author_()
    {
        if ( buffer.length() > 0 )
        {
            author = buffer.toString();
            resetBuffer();
        }
    }

    /** {@inheritDoc} */
    public void date_()
    {
        if ( buffer.length() > 0 )
        {
            date = buffer.toString();
            resetBuffer();
        }
    }

    /** {@inheritDoc} */
    public void section1_()
    {
        write( EOL );
    }

    /** {@inheritDoc} */
    public void section2_()
    {
        write( EOL );
    }

    /** {@inheritDoc} */
    public void section3_()
    {
        write( EOL );
    }

    /** {@inheritDoc} */
    public void section4_()
    {
        write( EOL );
    }

    /** {@inheritDoc} */
    public void section5_()
    {
        write( EOL );
    }

    /** {@inheritDoc} */
    public void sectionTitle1()
    {
        write( EOL );
    }

    /** {@inheritDoc} */
    public void sectionTitle1_()
    {
        write( EOL + EOL );
    }

    /** {@inheritDoc} */
    public void sectionTitle2()
    {
        write( EOL + SECTION_TITLE_START_MARKUP );
    }

    /** {@inheritDoc} */
    public void sectionTitle2_()
    {
        write( EOL + EOL );
    }

    /** {@inheritDoc} */
    public void sectionTitle3()
    {
        write( EOL + StringUtils.repeat( SECTION_TITLE_START_MARKUP, 2 ) );
    }

    /** {@inheritDoc} */
    public void sectionTitle3_()
    {
        write( EOL + EOL );
    }

    /** {@inheritDoc} */
    public void sectionTitle4()
    {
        write( EOL + StringUtils.repeat( SECTION_TITLE_START_MARKUP, 3 ) );
    }

    /** {@inheritDoc} */
    public void sectionTitle4_()
    {
        write( EOL + EOL );
    }

    /** {@inheritDoc} */
    public void sectionTitle5()
    {
        write( EOL + StringUtils.repeat( SECTION_TITLE_START_MARKUP, 4 ) );
    }

    /** {@inheritDoc} */
    public void sectionTitle5_()
    {
        write( EOL + EOL );
    }

    /** {@inheritDoc} */
    public void list()
    {
        listNestingIndent += " ";
        listStyles.push( LIST_START_MARKUP );
        write( EOL );
    }

    /** {@inheritDoc} */
    public void list_()
    {
        if ( listNestingIndent.length() <= 1 )
        {
            write( EOL + listNestingIndent + LIST_END_MARKUP + EOL );
        }
        else
        {
            write( EOL );
        }
        listNestingIndent = StringUtils.chomp( listNestingIndent, " " );
        listStyles.pop();
        itemFlag = false;
    }

    /** {@inheritDoc} */
    public void listItem()
    {
        //if ( !numberedList )
        //write( EOL + listNestingIndent + "*" );
        //else
        numberedListItem();
        itemFlag = true;
    }

    /** {@inheritDoc} */
    public void listItem_()
    {
        write( EOL );
    }

    /** {@inheritDoc} */
    public void numberedList( int numbering )
    {
        listNestingIndent += " ";
        write( EOL );

        String style;
        switch ( numbering )
        {
            case NUMBERING_UPPER_ALPHA:
                style = String.valueOf( NUMBERING_UPPER_ALPHA_CHAR );
                break;
            case NUMBERING_LOWER_ALPHA:
                style = String.valueOf( NUMBERING_LOWER_ALPHA_CHAR );
                break;
            case NUMBERING_UPPER_ROMAN:
                style = String.valueOf( NUMBERING_UPPER_ROMAN_CHAR );
                break;
            case NUMBERING_LOWER_ROMAN:
                style = String.valueOf( NUMBERING_LOWER_ROMAN_CHAR );
                break;
            case NUMBERING_DECIMAL:
            default:
                style = String.valueOf( NUMBERING );
        }

        listStyles.push( style );
    }

    /** {@inheritDoc} */
    public void numberedList_()
    {
        if ( listNestingIndent.length() <= 1 )
        {
            write( EOL + listNestingIndent + LIST_END_MARKUP + EOL );
        }
        else
        {
            write( EOL );
        }
        listNestingIndent = StringUtils.chomp( listNestingIndent, " " );
        listStyles.pop();
        itemFlag = false;
    }

    /** {@inheritDoc} */
    public void numberedListItem()
    {
        String style = (String) listStyles.peek();
        if ( style.equals( String.valueOf( STAR ) ) )
        {
            write( EOL + listNestingIndent + String.valueOf( STAR ) + String.valueOf( SPACE ) );
        }
        else
        {
            write( EOL + listNestingIndent + String.valueOf( LEFT_SQUARE_BRACKET )
                + String.valueOf( LEFT_SQUARE_BRACKET ) + style + String.valueOf( RIGHT_SQUARE_BRACKET )
                + String.valueOf( RIGHT_SQUARE_BRACKET ) + String.valueOf( SPACE ) );
        }
        itemFlag = true;
    }

    /** {@inheritDoc} */
    public void numberedListItem_()
    {
        write( EOL );
    }

    /** {@inheritDoc} */
    public void definitionList()
    {
        write( EOL );
    }

    /** {@inheritDoc} */
    public void definitionList_()
    {
        write( EOL );
        itemFlag = false;
    }

    /** {@inheritDoc} */
    public void definedTerm()
    {
        write( EOL + " [" );
    }

    /** {@inheritDoc} */
    public void definedTerm_()
    {
        write( "]" );
    }

    /** {@inheritDoc} */
    public void definition()
    {
        itemFlag = true;
    }

    /** {@inheritDoc} */
    public void definition_()
    {
        write( EOL );
    }

    /** {@inheritDoc} */
    public void pageBreak()
    {
        write( EOL + PAGE_BREAK + EOL );
    }

    /** {@inheritDoc} */
    public void paragraph()
    {
        if ( !itemFlag )
        {
            write( EOL + " " );
        }
    }

    /** {@inheritDoc} */
    public void paragraph_()
    {
        if ( itemFlag )
        {
            itemFlag = false;
        }
        else
        {
            write( EOL + EOL );
        }
    }

    /** {@inheritDoc} */
    public void verbatim( boolean boxed )
    {
        verbatimFlag = true;
        this.boxed = boxed;
        if ( boxed )
        {
            write( EOL + BOXED_VERBATIM_START_MARKUP + EOL );
        }
        else
        {
            write( EOL + NON_BOXED_VERBATIM_START_MARKUP + EOL );
        }
    }

    /** {@inheritDoc} */
    public void verbatim_()
    {
        if ( boxed )
        {
            write( EOL + BOXED_VERBATIM_END_MARKUP + EOL );
        }
        else
        {
            write( EOL + NON_BOXED_VERBATIM_END_MARKUP + EOL );
        }
        boxed = false;
        verbatimFlag = false;
    }

    /** {@inheritDoc} */
    public void horizontalRule()
    {
        write( EOL + HORIZONTAL_RULE_MARKUP + EOL );
    }

    /** {@inheritDoc} */
    public void table()
    {
        write( EOL );
    }

    /** {@inheritDoc} */
    public void table_()
    {
        if ( rowLine != null )
        {
            write( rowLine );
        }
        rowLine = null;

        if ( tableCaptionBuffer.length() > 0 )
        {
            text( tableCaptionBuffer.toString() + EOL );
        }

        resetTableCaptionBuffer();
    }

    /** {@inheritDoc} */
    public void tableRows( int justification[], boolean grid )
    {
        cellJustif = justification;
        gridFlag = grid;
    }

    /** {@inheritDoc} */
    public void tableRows_()
    {
        cellJustif = null;
        gridFlag = false;
    }

    /** {@inheritDoc} */
    public void tableRow()
    {
        bufferFlag = true;
        cellCount = 0;
    }

    /** {@inheritDoc} */
    public void tableRow_()
    {
        bufferFlag = false;

        // write out the header row first, then the data in the buffer
        buildRowLine();

        write( rowLine );

        // TODO: This will need to be more clever, for multi-line cells
        if ( gridFlag )
        {
            write( TABLE_ROW_SEPARATOR_MARKUP );
        }

        write( buffer.toString() );

        resetBuffer();

        write( EOL );

        // only reset cell count if this is the last row
        cellCount = 0;
    }

    /** Construct a table row. */
    private void buildRowLine()
    {
        StringBuffer rowLine = new StringBuffer();
        rowLine.append( TABLE_ROW_START_MARKUP );

        for ( int i = 0; i < cellCount; i++ )
        {
            if ( cellJustif != null )
            {
                switch ( cellJustif[i] )
                {
                case 1:
                    rowLine.append( TABLE_COL_LEFT_ALIGNED_MARKUP );
                    break;
                case 2:
                    rowLine.append( TABLE_COL_RIGHT_ALIGNED_MARKUP );
                    break;
                default:
                    rowLine.append( TABLE_COL_CENTERED_ALIGNED_MARKUP );
                }
            }
            else
            {
                rowLine.append( TABLE_COL_CENTERED_ALIGNED_MARKUP );
            }
        }
        rowLine.append( EOL );

        this.rowLine = rowLine.toString();
    }

    /** {@inheritDoc} */
    public void tableCell()
    {
        tableCell( false );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell()
    {
        tableCell( true );
    }

    /**
     * Starts a table cell.
     *
     * @param headerRow If this cell is part of a header row.
     */
    public void tableCell( boolean headerRow )
    {
        if ( headerRow )
        {
            buffer.append( TABLE_CELL_SEPARATOR_MARKUP );
        }
    }

    /** {@inheritDoc} */
    public void tableCell_()
    {
        tableCell_( false );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell_()
    {
        tableCell_( true );
    }

    /**
     * Ends a table cell.
     *
     * @param headerRow If this cell is part of a header row.
     */
    private void tableCell_( boolean headerRow )
    {
        buffer.append( TABLE_CELL_SEPARATOR_MARKUP );
        cellCount++;
    }

    /** {@inheritDoc} */
    public void tableCaption()
    {
        tableCaptionFlag = true;
    }

    /** {@inheritDoc} */
    public void tableCaption_()
    {
        tableCaptionFlag = false;
    }

    /** {@inheritDoc} */
    public void figureCaption_()
    {
        write( EOL );
    }

    /** {@inheritDoc} */
    public void figureGraphics( String name )
    {
        write( EOL + "[" + name + "] " );
    }

    /** {@inheritDoc} */
    public void anchor( String name )
    {
//        String id = HtmlTools.encodeId(name);
        write( ANCHOR_START_MARKUP );
    }

    /** {@inheritDoc} */
    public void anchor_()
    {
        write( ANCHOR_END_MARKUP );
    }

    /** {@inheritDoc} */
    public void link( String name )
    {
        if ( !headerFlag )
        {
            write( LINK_START_1_MARKUP );
            text( name );
            write( LINK_START_2_MARKUP );
        }
    }

    /** {@inheritDoc} */
    public void link_()
    {
        if ( !headerFlag )
        {
            write( LINK_END_MARKUP );
        }
    }

    /**
     * A link with a target.
     *
     * @param name The name of the link.
     * @param target The link target.
     */
    public void link( String name, String target )
    {
        if ( !headerFlag )
        {
            write( LINK_START_1_MARKUP );
            text( target );
            write( LINK_START_2_MARKUP );
            text( name );
        }
    }

    /** {@inheritDoc} */
    public void italic()
    {
        if ( !headerFlag )
        {
            write( ITALIC_START_MARKUP );
        }
    }

    /** {@inheritDoc} */
    public void italic_()
    {
        if ( !headerFlag )
        {
            write( ITALIC_END_MARKUP );
        }
    }

    /** {@inheritDoc} */
    public void bold()
    {
        if ( !headerFlag )
        {
            write( BOLD_START_MARKUP );
        }
    }

    /** {@inheritDoc} */
    public void bold_()
    {
        if ( !headerFlag )
        {
            write( BOLD_END_MARKUP );
        }
    }

    /** {@inheritDoc} */
    public void monospaced()
    {
        if ( !headerFlag )
        {
            write( MONOSPACED_START_MARKUP );
        }
    }

    /** {@inheritDoc} */
    public void monospaced_()
    {
        if ( !headerFlag )
        {
            write( MONOSPACED_END_MARKUP );
        }
    }

    /** {@inheritDoc} */
    public void lineBreak()
    {
        if ( headerFlag || bufferFlag )
        {
            buffer.append( EOL );
        }
        else
        {
            write( "\\" + EOL );
        }
    }

    /** {@inheritDoc} */
    public void nonBreakingSpace()
    {
        if ( headerFlag || bufferFlag )
        {
            buffer.append( NON_BREAKING_SPACE_MARKUP );
        }
        else
        {
            write( NON_BREAKING_SPACE_MARKUP );
        }
    }

    /** {@inheritDoc} */
    public void text( String text )
    {
        if ( tableCaptionFlag )
        {
            tableCaptionBuffer.append( text );
        }
        else if ( headerFlag || bufferFlag )
        {
            buffer.append( text );
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
        write( text );
    }

    /** {@inheritDoc} */
    public void comment( String comment )
    {
        rawText( EOL + "~~ " + comment );
    }


    /**
     * Write text to output.
     *
     * @param text The text to write.
     */
    protected void write( String text )
    {
        writer.write( text );
    }

    /**
     * Write Apt escaped text to output.
     *
     * @param text The text to write.
     */
    protected void content( String text )
    {
        write( escapeAPT( text ) );
    }

    /**
     * Write Apt escaped text to output.
     *
     * @param text The text to write.
     */
    protected void verbatimContent( String text )
    {
        write( escapeAPT( text ) );
    }

    /**
     * Forward to HtmlTools.encodeFragment( text ).
     *
     * @param text the String to fragment, may be null.
     * @return the fragmented text, null if null String input.
     * @see org.apache.maven.doxia.util.HtmlTools#encodeURL(String).
     */
    public static String encodeFragment( String text )
    {
        return HtmlTools.encodeFragment( text );
    }

    /**
     * Forward to HtmlTools.encodeURL( text ).
     *
     * @param text the String to encode, may be null.
     * @return the text encoded, null if null String input.
     * @see org.apache.maven.doxia.util.HtmlTools#encodeURL(String).
     */
    public static String encodeURL( String text )
    {
        return HtmlTools.encodeURL( text );
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

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * Escape special characters in a text in APT:
     *
     * <pre>
     * \~, \=, \-, \+, \*, \[, \], \<, \>, \{, \}, \\
     * </pre>
     *
     * @param text the String to escape, may be null
     * @return the text escaped, "" if null String input
     */
    private static String escapeAPT( String text )
    {
        if ( text == null )
        {
            return "";
        }

        int length = text.length();
        StringBuffer buffer = new StringBuffer( length );

        for ( int i = 0; i < length; ++i )
        {
            char c = text.charAt( i );
            switch ( c )
            { // 0080
                case '\\':
                case '~':
                case '=':
                case '-':
                case '+':
                case '*':
                case '[':
                case ']':
                case '<':
                case '>':
                case '{':
                case '}':
                    buffer.append( '\\' );
                    buffer.append( c );
                    break;
                default:
                    if ( c > 127 )
                    {
                        buffer.append( "\\u" );
                        String hex = Integer.toHexString( c );
                        if ( hex.length() == 2 )
                        {
                            buffer.append( "00" );
                        }
                        else if ( hex.length() == 3 )
                        {
                            buffer.append( "0" );
                        }
                        buffer.append( hex );
                    }
                    else
                    {
                        buffer.append( c );
                    }
            }
        }

        return buffer.toString();
    }
}
