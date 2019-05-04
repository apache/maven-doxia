package org.apache.maven.doxia.module.twiki;

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

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.AbstractTextSink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.impl.SinkUtils;
import org.apache.maven.doxia.util.HtmlTools;
import org.codehaus.plexus.util.StringUtils;

/**
 * TWiki Sink implementation.
 * <br>
 * <b>Note</b>: The encoding used is UTF-8.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public class TWikiSink
    extends AbstractTextSink
    implements TWikiMarkup
{
    /**  The writer to use. */
    private final PrintWriter out;

    /**  The writer to use. */
    private StringWriter writer;

    /** An indication on if we're in bold mode. */
    private boolean boldFlag;

    /** An indication on if we're in head mode. */
    private boolean headFlag;

    private int levelList = 0;

    /**  listStyles. */
    private final Stack<String> listStyles;

    /** Keep track of the nested bold flag. */
    protected Stack<Boolean> boldStack = new Stack<>();

    /** Keep track of the closing tags for inline events. */
    protected Stack<List<String>> inlineStack = new Stack<>();

    /**
     * Constructor, initialize the Writer and the variables.
     *
     * @param writer not null writer to write the result. <b>Should</b> be an UTF-8 Writer.
     * You could use <code>newWriter</code> methods from {@link org.codehaus.plexus.util.WriterFactory}.
     */
    protected TWikiSink( Writer writer )
    {
        this.out = new PrintWriter( writer );
        this.listStyles = new Stack<>();

        init();
    }

    /** {@inheritDoc} */
    public void anchor( String name )
    {
        write( EOL );
        write( ANCHOR_MARKUP + name );
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

    /**
     * Not used.
     * {@inheritDoc}
     */
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
     * Not used.
     * {@inheritDoc}
     */
    public void definedTerm_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void definition()
    {
        write( DEFINITION_LIST_DEFINITION_MARKUP );
    }

    /** {@inheritDoc} */
    public void definition( SinkEventAttributes attributes )
    {
        definition();
    }

    /** {@inheritDoc} */
    public void definition_()
    {
        writeEOL();
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void definitionList()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void definitionList( SinkEventAttributes attributes )
    {
        definitionList();
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void definitionList_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void definitionListItem()
    {
        write( DEFINITION_LIST_ITEM_MARKUP );
    }

    /** {@inheritDoc} */
    public void definitionListItem( SinkEventAttributes attributes )
    {
        definitionListItem();
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void definitionListItem_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void figure()
    {
        write( LESS_THAN + Tag.IMG.toString() + SPACE );
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
        write( SLASH + String.valueOf( GREATER_THAN ) );
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void figureCaption()
    {
        write( Attribute.ALT.toString() + EQUAL + QUOTE );
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
        write( QUOTE + String.valueOf( SPACE ) );
    }

    /** {@inheritDoc} */
    public void figureGraphics( String name )
    {
        write( Attribute.SRC.toString() + EQUAL + QUOTE + name + QUOTE + SPACE );
    }

    /** {@inheritDoc} */
    public void figureGraphics( String src, SinkEventAttributes attributes )
    {
        figureGraphics( src );
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

    /** {@inheritDoc} */
    public void horizontalRule()
    {
        writeEOL( true );
        write( HORIZONTAL_RULE_MARKUP );
        writeEOL( true );
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
        List<String> tags = new ArrayList<>();

        boldStack.push( boldFlag );

        if ( attributes != null )
        {

            if ( attributes.containsAttribute( SinkEventAttributes.SEMANTICS, "bold" ) )
            {
                boldFlag = true;
                write( BOLD_START_MARKUP );
                tags.add( 0, BOLD_END_MARKUP );
            }

            if ( attributes.containsAttribute( SinkEventAttributes.SEMANTICS, "italic" ) )
            {
                if ( boldFlag )
                {
                    String tmp = writer.toString();
                    writer = new StringWriter();
                    writer.write( tmp.substring( 0, tmp.length() - 1 ) );
                    write( BOLD_ITALIC_START_MARKUP );
                    tags.add( 0, BOLD_ITALIC_END_MARKUP );
                }
                else
                {
                    write( ITALIC_START_MARKUP );
                    tags.add( ITALIC_END_MARKUP );
                }
            }

            if ( attributes.containsAttribute( SinkEventAttributes.SEMANTICS, "code" ) )
            {
                if ( boldFlag )
                {
                    String tmp = writer.toString();
                    writer = new StringWriter();
                    writer.write( tmp.substring( 0, tmp.length() - 1 ) );
                    write( BOLD_MONOSPACED_START_MARKUP );
                    tags.add( 0, BOLD_MONOSPACED_END_MARKUP );
                }
                else
                {
                    write( MONOSPACED_START_MARKUP );
                    tags.add( 0, MONOSPACED_END_MARKUP );
                }
            }

        }

        inlineStack.push( tags );
    }

    /** {@inheritDoc} */
    public void inline_()
    {
        for ( String tag: inlineStack.pop() )
        {
            write( tag );
        }
        this.boldFlag = boldStack.pop();
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

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void lineBreak()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void lineBreak( SinkEventAttributes attributes )
    {
        lineBreak();
    }

    /** {@inheritDoc} */
    public void link( String name )
    {
        write( LINK_START_MARKUP + name + LINK_MIDDLE_MARKUP );
    }

    /** {@inheritDoc} */
    public void link( String name, SinkEventAttributes attributes )
    {
        link( name );
    }

    /** {@inheritDoc} */
    public void link_()
    {
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
    }

    /** {@inheritDoc} */
    public void listItem()
    {
        String indent = StringUtils.repeat( THREE_SPACES_MARKUP, levelList );
        write( indent + LIST_ITEM_MARKUP );
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
        inline( SinkEventAttributeSet.Semantics.CODE );
    }

    /** {@inheritDoc} */
    public void monospaced_()
    {
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
        levelList++;

        String style;
        switch ( numbering )
        {
            case NUMBERING_UPPER_ALPHA:
                style = NUMBERING_UPPER_ALPHA_MARKUP;
                break;
            case NUMBERING_LOWER_ALPHA:
                style = NUMBERING_LOWER_ALPHA_MARKUP;
                break;
            case NUMBERING_UPPER_ROMAN:
                style = NUMBERING_UPPER_ROMAN_MARKUP;
                break;
            case NUMBERING_LOWER_ROMAN:
                style = NUMBERING_LOWER_ROMAN_MARKUP;
                break;
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
        listStyles.pop();
    }

    /** {@inheritDoc} */
    public void numberedListItem()
    {
        writeEOL( true );
        String style = listStyles.peek();
        String indent = StringUtils.repeat( THREE_SPACES_MARKUP, levelList );
        write( indent + style + SPACE );
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
            write( StringUtils.repeat( "-", 3 ) + StringUtils.repeat( "+", level ) );
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

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void table()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void table( SinkEventAttributes attributes )
    {
        table();
    }

    /**
     * Not used.
     * {@inheritDoc}
     */
    public void table_()
    {
        // nop
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
        write( TABLE_CELL_MARKUP );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell()
    {
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

        content( text );
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
                writeStartTag( Tag.U );
            }
            if ( attributes.containsAttribute( SinkEventAttributes.DECORATION, "line-through" ) )
            {
                writeStartTag( Tag.S );
            }
            if ( attributes.containsAttribute( SinkEventAttributes.VALIGN, "sub" ) )
            {
                writeStartTag( Tag.SUB );
            }
            if ( attributes.containsAttribute( SinkEventAttributes.VALIGN, "sup" ) )
            {
                writeStartTag( Tag.SUP );
            }

            text( text );

            if ( attributes.containsAttribute( SinkEventAttributes.VALIGN, "sup" ) )
            {
                writeEndTag( Tag.SUP );
            }
            if ( attributes.containsAttribute( SinkEventAttributes.VALIGN, "sub" ) )
            {
                writeEndTag( Tag.SUB );
            }
            if ( attributes.containsAttribute( SinkEventAttributes.DECORATION, "line-through" ) )
            {
                writeEndTag( Tag.S );
            }
            if ( attributes.containsAttribute( SinkEventAttributes.DECORATION, "underline" ) )
            {
                writeEndTag( Tag.U );
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
        SinkEventAttributeSet att = new SinkEventAttributeSet();

        if ( boxed )
        {
            att.addAttribute( SinkEventAttributes.DECORATION, "boxed" );
        }

        verbatim( att );
    }

    /** {@inheritDoc} */
    public void verbatim( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes( attributes, SinkUtils.SINK_VERBATIM_ATTRIBUTES );

        if ( atts == null )
        {
            atts = new SinkEventAttributeSet();
        }

        boolean boxed = false;

        if ( atts.isDefined( SinkEventAttributes.DECORATION ) )
        {
            boxed = "boxed".equals( atts.getAttribute( SinkEventAttributes.DECORATION ).toString() );
        }

        if ( boxed )
        {
            atts.addAttribute( Attribute.CLASS, "source" );
        }

        atts.removeAttribute( SinkEventAttributes.DECORATION );

        String width = (String) atts.getAttribute( Attribute.WIDTH.toString() );
        atts.removeAttribute( Attribute.WIDTH.toString() );

        writeStartTag( Tag.DIV, atts );
        writeEOL( true );

        if ( width != null )
        {
            atts.addAttribute( Attribute.WIDTH.toString(), width );
        }

        atts.removeAttribute( Attribute.ALIGN.toString() );
        atts.removeAttribute( Attribute.CLASS.toString() );

        writeStartTag( VERBATIM_TAG, atts );
    }

    /** {@inheritDoc} */
    public void verbatim_()
    {
        writeEndTag( VERBATIM_TAG );
        writeEOL( true );
        writeEndTag( Tag.DIV );
        writeEOL( true );
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    private void write( String text )
    {
        writer.write( unifyEOLs( text ) );
    }

    /**
     * Starts a Tag. For instance:
     * <pre>
     * &lt;tag&gt;
     * </pre>
     * <br>
     * <b>Note</b>: Copy from {@link AbstractXmlSink#writeStartTag(javax.swing.text.html.HTML.Tag)}
     *
     * @param t a non null tag
     * @see #writeStartTag(javax.swing.text.html.HTML.Tag)
     */
    private void writeStartTag( Tag t )
    {
        writeStartTag( t, null );
    }

    /**
     * Starts a Tag with attributes. For instance:
     * <pre>
     * &lt;tag attName="attValue"&gt;
     * </pre>
     * <br>
     * <b>Note</b>: Copy from {@link AbstractXmlSink#writeStartTag(javax.swing.text.html.HTML.Tag,
     *      javax.swing.text.MutableAttributeSet)}
     *
     * @param t a non null tag
     * @param att a set of attributes
     * @see #writeStartTag(javax.swing.text.html.HTML.Tag, javax.swing.text.MutableAttributeSet, boolean)
     */
    private void writeStartTag( Tag t, MutableAttributeSet att )
    {
        writeStartTag( t, att, false );
    }

    /**
     * Starts a Tag with attributes. For instance:
     * <pre>
     * &lt;tag attName="attValue"&gt;
     * </pre>
     * <br>
     * <b>Note</b>: Copy from {@link AbstractXmlSink#writeStartTag(javax.swing.text.html.HTML.Tag,
     *      javax.swing.text.MutableAttributeSet, boolean)}
     *
     * @param t a non null tag
     * @param att a set of attributes
     * @param isSimpleTag boolean to write as a simple tag
     */
    private void writeStartTag( Tag t, MutableAttributeSet att, boolean isSimpleTag )
    {
        if ( t == null )
        {
            throw new IllegalArgumentException( "A tag is required" );
        }

        StringBuilder sb = new StringBuilder();
        sb.append( LESS_THAN );

        sb.append( t.toString() );

        sb.append( SinkUtils.getAttributeString( att ) );

        if ( isSimpleTag )
        {
            sb.append( SPACE ).append( SLASH );
        }

        sb.append( GREATER_THAN );

        write( sb.toString() );
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
     * Ends a Tag without writing an EOL. For instance: <pre>&lt;/tag&gt;</pre>.
     * <br>
     * <b>Note</b>: Copy from {@link AbstractXmlSink#writeEndTag(javax.swing.text.html.HTML.Tag)}
     *
     * @param t a tag.
     */
    private void writeEndTag( Tag t )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( LESS_THAN );
        sb.append( SLASH );

        sb.append( t.toString() );
        sb.append( GREATER_THAN );

        write( sb.toString() );
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

    /** {@inheritDoc} */
    protected void init()
    {
        super.init();

        this.writer = new StringWriter();
        this.headFlag = false;
        this.levelList = 0;
        this.listStyles.clear();
        this.boldFlag = false;
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
