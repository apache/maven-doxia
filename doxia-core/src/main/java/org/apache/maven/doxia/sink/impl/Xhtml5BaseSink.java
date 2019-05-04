package org.apache.maven.doxia.sink.impl;

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
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.markup.HtmlMarkup;
import org.apache.maven.doxia.markup.Markup;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.util.DoxiaUtils;
import org.apache.maven.doxia.util.HtmlTools;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;

/**
 * Abstract base xhtml5 sink implementation.
 */
public class Xhtml5BaseSink
    extends AbstractXmlSink
    implements HtmlMarkup
{
    // ----------------------------------------------------------------------
    // Instance fields
    // ----------------------------------------------------------------------

    /** The PrintWriter to write the result. */
    private final PrintWriter writer;

    /** Used to collect text events mainly for the head events. */
    private StringBuffer textBuffer = new StringBuffer();

    /** An indication on if we're inside a head. */
    private boolean headFlag;

    /** Keep track of the main and div tags for content events. */
    protected Stack<Tag> contentStack = new Stack<>();

    /** Keep track of the closing tags for inline events. */
    protected Stack<List<Tag>> inlineStack = new Stack<>();

    /** An indication on if we're inside a paragraph flag. */
    private boolean paragraphFlag;

    /** An indication on if we're in verbatim mode. */
    private boolean verbatimFlag;

    /** Stack of alignment int[] of table cells. */
    private final LinkedList<int[]> cellJustifStack;

    /** Stack of justification of table cells. */
    private final LinkedList<Boolean> isCellJustifStack;

    /** Stack of current table cell. */
    private final LinkedList<Integer> cellCountStack;

    /** Used to style successive table rows differently. */
    private boolean evenTableRow = true;

    /** The stack of StringWriter to write the table result temporary, so we could play with the output DOXIA-177. */
    private final LinkedList<StringWriter> tableContentWriterStack;

    private final LinkedList<StringWriter> tableCaptionWriterStack;

    private final LinkedList<PrettyPrintXMLWriter> tableCaptionXMLWriterStack;

    /** The stack of table caption */
    private final LinkedList<String> tableCaptionStack;

    /** used to store attributes passed to table(). */
    protected MutableAttributeSet tableAttributes;

    /** Flag to know if {@link #tableRows(int[], boolean)} is called or not. It is mainly to be backward compatible
     * with some plugins (like checkstyle) which uses:
     * <pre>
     * sink.table();
     * sink.tableRow();
     * </pre>
     * instead of
     * <pre>
     * sink.table();
     * sink.tableRows( justify, true );
     * sink.tableRow();
     * </pre>
     * */
    protected boolean tableRows = false;

    /** Map of warn messages with a String as key to describe the error type and a Set as value.
     * Using to reduce warn messages. */
    private Map<String, Set<String>> warnMessages;

    // ----------------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------------

    /**
     * Constructor, initialize the PrintWriter.
     *
     * @param out The writer to write the result.
     */
    public Xhtml5BaseSink( Writer out )
    {
        this.writer = new PrintWriter( out );

        this.cellJustifStack = new LinkedList<>();
        this.isCellJustifStack = new LinkedList<>();
        this.cellCountStack = new LinkedList<>();
        this.tableContentWriterStack = new LinkedList<>();
        this.tableCaptionWriterStack = new LinkedList<>();
        this.tableCaptionXMLWriterStack = new LinkedList<>();
        this.tableCaptionStack = new LinkedList<>();

        init();
    }

    // ----------------------------------------------------------------------
    // Accessor methods
    // ----------------------------------------------------------------------

    /**
     * To use mainly when playing with the head events.
     *
     * @return the current buffer of text events.
     */
    protected StringBuffer getTextBuffer()
    {
        return this.textBuffer;
    }

    /**
     * <p>Setter for the field <code>headFlag</code>.</p>
     *
     * @param headFlag an header flag.
     */
    protected void setHeadFlag( boolean headFlag )
    {
        this.headFlag = headFlag;
    }

    /**
     * <p>isHeadFlag.</p>
     *
     * @return the current headFlag.
     */
    protected boolean isHeadFlag()
    {
        return this.headFlag ;
    }

    /**
     * <p>Setter for the field <code>verbatimFlag</code>.</p>
     *
     * @param verb a verbatim flag.
     */
    protected void setVerbatimFlag( boolean verb )
    {
        this.verbatimFlag = verb;
    }

    /**
     * <p>isVerbatimFlag.</p>
     *
     * @return the current verbatim flag.
     */
    protected boolean isVerbatimFlag()
    {
        return this.verbatimFlag ;
    }

    /**
     * <p>Setter for the field <code>cellJustif</code>.</p>
     *
     * @param justif the new cell justification array.
     */
    protected void setCellJustif( int[] justif )
    {
        this.cellJustifStack.addLast( justif );
        this.isCellJustifStack.addLast( Boolean.TRUE );
    }

    /**
     * <p>Getter for the field <code>cellJustif</code>.</p>
     *
     * @return the current cell justification array.
     */
    protected int[] getCellJustif()
    {
        return this.cellJustifStack.getLast();
    }

    /**
     * <p>Setter for the field <code>cellCount</code>.</p>
     *
     * @param count the new cell count.
     */
    protected void setCellCount( int count )
    {
        this.cellCountStack.addLast( count );
    }

    /**
     * <p>Getter for the field <code>cellCount</code>.</p>
     *
     * @return the current cell count.
     */
    protected int getCellCount()
    {
        return Integer.parseInt( this.cellCountStack.getLast().toString() );
    }

    /**
     * Reset all variables.
     *
     * @deprecated since 1.1.2, use {@link #init()} instead of.
     */
    protected void resetState()
    {
        init();
    }

    /** {@inheritDoc} */
    @Override
    protected void init()
    {
        super.init();

        resetTextBuffer();

        this.cellJustifStack.clear();
        this.isCellJustifStack.clear();
        this.cellCountStack.clear();
        this.tableContentWriterStack.clear();
        this.tableCaptionWriterStack.clear();
        this.tableCaptionXMLWriterStack.clear();
        this.tableCaptionStack.clear();
        this.inlineStack.clear();

        this.headFlag = false;
        this.paragraphFlag = false;
        this.verbatimFlag = false;

        this.evenTableRow = true;
        this.tableAttributes = null;
        this.tableRows = false;
        this.warnMessages = null;
    }

    /**
     * Reset the text buffer.
     */
    protected void resetTextBuffer()
    {
        this.textBuffer = new StringBuffer();
    }

    // ----------------------------------------------------------------------
    // Sections
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public void article()
    {
        article( null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void article( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_SECTION_ATTRIBUTES  );

        writeStartTag( HtmlMarkup.ARTICLE, atts );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void article_()
    {
        writeEndTag( HtmlMarkup.ARTICLE );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void navigation()
    {
        navigation( null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void navigation( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_SECTION_ATTRIBUTES  );

        writeStartTag( HtmlMarkup.NAV, atts );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void navigation_()
    {
        writeEndTag( HtmlMarkup.NAV );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sidebar()
    {
        sidebar( null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sidebar( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_SECTION_ATTRIBUTES  );

        writeStartTag( HtmlMarkup.ASIDE, atts );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sidebar_()
    {
        writeEndTag( HtmlMarkup.ASIDE );
    }

    /** {@inheritDoc} */
    @Override
    public void section( int level, SinkEventAttributes attributes )
    {
        onSection( level, attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle( int level, SinkEventAttributes attributes )
    {
        onSectionTitle( level, attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle_( int level )
    {
        onSectionTitle_( level );
    }

    /** {@inheritDoc} */
    @Override
    public void section_( int level )
    {
        onSection_( level );
    }

    /** {@inheritDoc} */
    @Override
    public void section1()
    {
        onSection( SECTION_LEVEL_1, null );
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle1()
    {
        onSectionTitle( SECTION_LEVEL_1, null );
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle1_()
    {
        onSectionTitle_( SECTION_LEVEL_1 );
    }

    /** {@inheritDoc} */
    @Override
    public void section1_()
    {
        onSection_( SECTION_LEVEL_1 );
    }

    /** {@inheritDoc} */
    @Override
    public void section2()
    {
        onSection( SECTION_LEVEL_2, null );
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle2()
    {
        onSectionTitle( SECTION_LEVEL_2, null );
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle2_()
    {
        onSectionTitle_( SECTION_LEVEL_2 );
    }

    /** {@inheritDoc} */
    @Override
    public void section2_()
    {
        onSection_( SECTION_LEVEL_2 );
    }

    /** {@inheritDoc} */
    @Override
    public void section3()
    {
        onSection( SECTION_LEVEL_3, null );
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle3()
    {
        onSectionTitle( SECTION_LEVEL_3, null );
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle3_()
    {
        onSectionTitle_( SECTION_LEVEL_3 );
    }

    /** {@inheritDoc} */
    @Override
    public void section3_()
    {
        onSection_( SECTION_LEVEL_3 );
    }

    /** {@inheritDoc} */
    @Override
    public void section4()
    {
        onSection( SECTION_LEVEL_4, null );
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle4()
    {
        onSectionTitle( SECTION_LEVEL_4, null );
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle4_()
    {
        onSectionTitle_( SECTION_LEVEL_4 );
    }

    /** {@inheritDoc} */
    @Override
    public void section4_()
    {
        onSection_( SECTION_LEVEL_4 );
    }

    /** {@inheritDoc} */
    @Override
    public void section5()
    {
        onSection( SECTION_LEVEL_5, null );
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle5()
    {
        onSectionTitle( SECTION_LEVEL_5, null );
    }

    /** {@inheritDoc} */
    @Override
    public void sectionTitle5_()
    {
        onSectionTitle_( SECTION_LEVEL_5 );
    }

    /** {@inheritDoc} */
    @Override
    public void section5_()
    {
        onSection_( SECTION_LEVEL_5 );
    }

    /**
     * Starts a section. The default class style is <code>section</code>.
     *
     * @param depth The level of the section.
     * @param attributes some attributes. May be null.
     * @see javax.swing.text.html.HTML.Tag#SECTION
     */
    protected void onSection( int depth, SinkEventAttributes attributes )
    {
        if ( depth >= SECTION_LEVEL_1 && depth <= SECTION_LEVEL_5 )
        {
            MutableAttributeSet att = new SinkEventAttributeSet();
            att.addAttributes( SinkUtils.filterAttributes(
                    attributes, SinkUtils.SINK_BASE_ATTRIBUTES  ) );

            writeStartTag( HtmlMarkup.SECTION, att );
        }
    }

    /**
     * Ends a section.
     *
     * @param depth The level of the section.
     * @see javax.swing.text.html.HTML.Tag#DIV
     */
    protected void onSection_( int depth )
    {
        if ( depth >= SECTION_LEVEL_1 && depth <= SECTION_LEVEL_5 )
        {
            writeEndTag( HtmlMarkup.SECTION );
        }
    }

    /**
     * Starts a section title.
     *
     * @param depth The level of the section title.
     * @param attributes some attributes. May be null.
     * @see javax.swing.text.html.HTML.Tag#H2
     * @see javax.swing.text.html.HTML.Tag#H3
     * @see javax.swing.text.html.HTML.Tag#H4
     * @see javax.swing.text.html.HTML.Tag#H5
     * @see javax.swing.text.html.HTML.Tag#H6
     */
    protected void onSectionTitle( int depth, SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_SECTION_ATTRIBUTES  );

        if ( depth == SECTION_LEVEL_1 )
        {
            writeStartTag( HtmlMarkup.H2, atts );
        }
        else if ( depth == SECTION_LEVEL_2 )
        {
            writeStartTag( HtmlMarkup.H3, atts );
        }
        else if ( depth == SECTION_LEVEL_3 )
        {
            writeStartTag( HtmlMarkup.H4, atts );
        }
        else if ( depth == SECTION_LEVEL_4 )
        {
            writeStartTag( HtmlMarkup.H5, atts );
        }
        else if ( depth == SECTION_LEVEL_5 )
        {
            writeStartTag( HtmlMarkup.H6, atts );
        }
    }

    /**
     * Ends a section title.
     *
     * @param depth The level of the section title.
     * @see javax.swing.text.html.HTML.Tag#H2
     * @see javax.swing.text.html.HTML.Tag#H3
     * @see javax.swing.text.html.HTML.Tag#H4
     * @see javax.swing.text.html.HTML.Tag#H5
     * @see javax.swing.text.html.HTML.Tag#H6
     */
    protected void onSectionTitle_( int depth )
    {
        if ( depth == SECTION_LEVEL_1 )
        {
            writeEndTag( HtmlMarkup.H2 );
        }
        else if ( depth == SECTION_LEVEL_2 )
        {
            writeEndTag( HtmlMarkup.H3 );
        }
        else if ( depth == SECTION_LEVEL_3 )
        {
            writeEndTag( HtmlMarkup.H4 );
        }
        else if ( depth == SECTION_LEVEL_4 )
        {
            writeEndTag( HtmlMarkup.H5 );
        }
        else if ( depth == SECTION_LEVEL_5 )
        {
            writeEndTag( HtmlMarkup.H6 );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void header()
    {
        header( null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void header( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_SECTION_ATTRIBUTES  );

        writeStartTag( HtmlMarkup.HEADER, atts );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void header_()
    {
        writeEndTag( HtmlMarkup.HEADER );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void content()
    {
        content( (SinkEventAttributes) null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void content( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_SECTION_ATTRIBUTES  );

        if ( contentStack.empty() )
        {
            writeStartTag( contentStack.push( HtmlMarkup.MAIN ), atts );
        }
        else
        {
            if ( atts == null )
            {
                atts = new SinkEventAttributeSet( 1 );
            }

            if ( !atts.isDefined( SinkEventAttributes.CLASS ) )
            {
                atts.addAttribute( SinkEventAttributes.CLASS, "content" );
            }

            writeStartTag( contentStack.push( HtmlMarkup.DIV ), atts );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void content_()
    {
        try
        {
            writeEndTag( contentStack.pop() );
        }
        catch ( EmptyStackException ese )
        {
            /* do nothing if the stack is empty */
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void footer()
    {
        footer( null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void footer( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_SECTION_ATTRIBUTES  );

        writeStartTag( HtmlMarkup.FOOTER, atts );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void footer_()
    {
        writeEndTag( HtmlMarkup.FOOTER );
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#UL
     */
    @Override
    public void list()
    {
        list( null );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#UL
     */
    @Override
    public void list( SinkEventAttributes attributes )
    {
        if ( paragraphFlag )
        {
            // The content of element type "p" must match
            // "(a|br|span|bdo|object|applet|img|map|iframe|tt|i|b|u|s|strike|big|small|font|basefont|em|strong|
            // dfn|code|q|samp|kbd|var|cite|abbr|acronym|sub|sup|input|select|textarea|label|button|ins|del|script)".
            paragraph_();
        }

        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_BASE_ATTRIBUTES  );

        writeStartTag( HtmlMarkup.UL, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#UL
     */
    @Override
    public void list_()
    {
        writeEndTag( HtmlMarkup.UL );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#LI
     */
    @Override
    public void listItem()
    {
        listItem( null );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#LI
     */
    @Override
    public void listItem( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_BASE_ATTRIBUTES  );

        writeStartTag( HtmlMarkup.LI, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#LI
     */
    @Override
    public void listItem_()
    {
        writeEndTag( HtmlMarkup.LI );
    }

    /**
     * The default list style depends on the numbering.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#OL
     */
    @Override
    public void numberedList( int numbering )
    {
        numberedList( numbering, null );
    }

    /**
     * The default list style depends on the numbering.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#OL
     */
    @Override
    public void numberedList( int numbering, SinkEventAttributes attributes )
    {
        if ( paragraphFlag )
        {
            // The content of element type "p" must match
            // "(a|br|span|bdo|object|applet|img|map|iframe|tt|i|b|u|s|strike|big|small|font|basefont|em|strong|
            // dfn|code|q|samp|kbd|var|cite|abbr|acronym|sub|sup|input|select|textarea|label|button|ins|del|script)".
            paragraph_();
        }

        String style;
        switch ( numbering )
        {
            case NUMBERING_UPPER_ALPHA:
                style = "upper-alpha";
                break;
            case NUMBERING_LOWER_ALPHA:
                style = "lower-alpha";
                break;
            case NUMBERING_UPPER_ROMAN:
                style = "upper-roman";
                break;
            case NUMBERING_LOWER_ROMAN:
                style = "lower-roman";
                break;
            case NUMBERING_DECIMAL:
            default:
                style = "decimal";
        }

        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_SECTION_ATTRIBUTES  );

        if ( atts == null )
        {
            atts = new SinkEventAttributeSet( 1 );
        }

        atts.addAttribute( Attribute.STYLE, "list-style-type: " + style );

        writeStartTag( HtmlMarkup.OL, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#OL
     */
    @Override
    public void numberedList_()
    {
        writeEndTag( HtmlMarkup.OL );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#LI
     */
    @Override
    public void numberedListItem()
    {
        numberedListItem( null );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#LI
     */
    @Override
    public void numberedListItem( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_BASE_ATTRIBUTES  );

        writeStartTag( HtmlMarkup.LI, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#LI
     */
    @Override
    public void numberedListItem_()
    {
        writeEndTag( HtmlMarkup.LI );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DL
     */
    @Override
    public void definitionList()
    {
        definitionList( null );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DL
     */
    @Override
    public void definitionList( SinkEventAttributes attributes )
    {
        if ( paragraphFlag )
        {
            // The content of element type "p" must match
            // "(a|br|span|bdo|object|applet|img|map|iframe|tt|i|b|u|s|strike|big|small|font|basefont|em|strong|
            // dfn|code|q|samp|kbd|var|cite|abbr|acronym|sub|sup|input|select|textarea|label|button|ins|del|script)".
            paragraph_();
        }

        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_BASE_ATTRIBUTES  );

        writeStartTag( HtmlMarkup.DL, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DL
     */
    @Override
    public void definitionList_()
    {
        writeEndTag( HtmlMarkup.DL );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DT
     */
    @Override
    public void definedTerm( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_BASE_ATTRIBUTES  );

        writeStartTag( HtmlMarkup.DT, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DT
     */
    @Override
    public void definedTerm()
    {
        definedTerm( null );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DT
     */
    @Override
    public void definedTerm_()
    {
        writeEndTag( HtmlMarkup.DT );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DD
     */
    @Override
    public void definition()
    {
        definition( null );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DD
     */
    @Override
    public void definition( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_BASE_ATTRIBUTES  );

        writeStartTag( HtmlMarkup.DD, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DD
     */
    @Override
    public void definition_()
    {
        writeEndTag( HtmlMarkup.DD );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void figure()
    {
        figure( null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void figure( SinkEventAttributes attributes )
    {
        writeStartTag( HtmlMarkup.FIGURE, attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void figure_()
    {
        writeEndTag( HtmlMarkup.FIGURE );
    }

    /** {@inheritDoc} */
    @Override
    public void figureGraphics( String name )
    {
        figureGraphics( name, null );
    }

    /** {@inheritDoc} */
    @Override
    public void figureGraphics( String src, SinkEventAttributes attributes )
    {
        MutableAttributeSet filtered = SinkUtils.filterAttributes( attributes, SinkUtils.SINK_IMG_ATTRIBUTES );
        if ( filtered != null )
        {
            filtered.removeAttribute( Attribute.SRC.toString() );
        }

        int count = ( attributes == null ? 1 : attributes.getAttributeCount() + 1 );

        MutableAttributeSet atts = new SinkEventAttributeSet( count );

        atts.addAttribute( Attribute.SRC, escapeHTML( src ) );
        atts.addAttributes( filtered );

        if ( atts.getAttribute( Attribute.ALT.toString() ) == null )
        {
            atts.addAttribute( Attribute.ALT.toString(), "" );
        }

        writeStartTag( HtmlMarkup.IMG, atts, true );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void figureCaption()
    {
        figureCaption( null );
    }

    /** {@inheritDoc} */
    @Override
    public void figureCaption( SinkEventAttributes attributes )
    {
        writeStartTag( HtmlMarkup.FIGCAPTION, attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void figureCaption_()
    {
        writeEndTag( HtmlMarkup.FIGCAPTION );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#P
     */
    @Override
    public void paragraph()
    {
        paragraph( null );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#P
     */
    @Override
    public void paragraph( SinkEventAttributes attributes )
    {
        paragraphFlag = true;

        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_SECTION_ATTRIBUTES  );

        writeStartTag( HtmlMarkup.P, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#P
     */
    @Override
    public void paragraph_()
    {
        if ( paragraphFlag )
        {
            writeEndTag( HtmlMarkup.P );
            paragraphFlag = false;
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DATA
     */
    @Override
    public void data( String value )
    {
        data( value, null );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DATA
     */
    @Override
    public void data( String value, SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_BASE_ATTRIBUTES  );

        MutableAttributeSet att = new SinkEventAttributeSet();
        if ( value != null )
        {
            att.addAttribute( Attribute.VALUE, value );
        }
        att.addAttributes( atts );

        writeStartTag( HtmlMarkup.DATA, att );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DATA
     */
    @Override
    public void data_()
    {
        writeEndTag( HtmlMarkup.DATA );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TIME
     */
    @Override
    public void time( String datetime )
    {
        time( datetime, null );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DATA
     */
    @Override
    public void time( String datetime, SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_BASE_ATTRIBUTES  );

        MutableAttributeSet att = new SinkEventAttributeSet();
        if ( datetime != null )
        {
            att.addAttribute( "datetime", datetime );
        }
        att.addAttributes( atts );

        writeStartTag( HtmlMarkup.TIME, att );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void time_()
    {
        writeEndTag( HtmlMarkup.TIME );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#ADDRESS
     */
    @Override
    public void address()
    {
        address( null );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#ADDRESS
     */
    @Override
    public void address( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_SECTION_ATTRIBUTES  );

        writeStartTag( HtmlMarkup.ADDRESS, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#ADDRESS
     */
    @Override
    public void address_()
    {
        writeEndTag( HtmlMarkup.ADDRESS );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#BLOCKQUOTE
     */
    @Override
    public void blockquote()
    {
        blockquote( null );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#BLOCKQUOTE
     */
    @Override
    public void blockquote( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_SECTION_ATTRIBUTES  );

        writeStartTag( HtmlMarkup.BLOCKQUOTE, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#BLOCKQUOTE
     */
    @Override
    public void blockquote_()
    {
        writeEndTag( HtmlMarkup.BLOCKQUOTE );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DIV
     */
    @Override
    public void division()
    {
        division( null );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DIV
     */
    @Override
    public void division( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_SECTION_ATTRIBUTES  );

        writeStartTag( HtmlMarkup.DIV, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DIV
     */
    @Override
    public void division_()
    {
        writeEndTag( HtmlMarkup.DIV );
    }

    /**
     * The default class style for boxed is <code>source</code>.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DIV
     * @see javax.swing.text.html.HTML.Tag#PRE
     */
    @Override
    public void verbatim( boolean boxed )
    {
        if ( boxed )
        {
            verbatim( SinkEventAttributeSet.BOXED );
        }
        else
        {
            verbatim( null );
        }
    }

    /**
     * The default class style for boxed is <code>source</code>.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DIV
     * @see javax.swing.text.html.HTML.Tag#PRE
     */
    @Override
    public void verbatim( SinkEventAttributes attributes )
    {
        if ( paragraphFlag )
        {
            // The content of element type "p" must match
            // "(a|br|span|bdo|object|applet|img|map|iframe|tt|i|b|u|s|strike|big|small|font|basefont|em|strong|
            // dfn|code|q|samp|kbd|var|cite|abbr|acronym|sub|sup|input|select|textarea|label|button|ins|del|script)".
            paragraph_();
        }

        verbatimFlag = true;

        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_VERBATIM_ATTRIBUTES  );

        if ( atts == null )
        {
            atts = new SinkEventAttributeSet();
        }

        boolean boxed = false;

        if ( atts.isDefined( SinkEventAttributes.DECORATION ) )
        {
            boxed =
                "boxed".equals( atts.getAttribute( SinkEventAttributes.DECORATION ).toString() );
        }

        SinkEventAttributes divAtts = null;

        if ( boxed )
        {
            divAtts = new SinkEventAttributeSet( Attribute.CLASS.toString(), "source" );
        }

        atts.removeAttribute( SinkEventAttributes.DECORATION );

        writeStartTag( HtmlMarkup.DIV, divAtts );
        writeStartTag( HtmlMarkup.PRE, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DIV
     * @see javax.swing.text.html.HTML.Tag#PRE
     */
    @Override
    public void verbatim_()
    {
        writeEndTag( HtmlMarkup.PRE );
        writeEndTag( HtmlMarkup.DIV );

        verbatimFlag = false;

    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#HR
     */
    @Override
    public void horizontalRule()
    {
        horizontalRule( null );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#HR
     */
    @Override
    public void horizontalRule( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_HR_ATTRIBUTES  );

        writeSimpleTag( HtmlMarkup.HR, atts );
    }

    /** {@inheritDoc} */
    @Override
    public void table()
    {
        // start table with tableRows
        table( null );
    }

    /** {@inheritDoc} */
    @Override
    public void table( SinkEventAttributes attributes )
    {
        this.tableContentWriterStack.addLast( new StringWriter() );
        this.tableRows = false;

        if ( paragraphFlag )
        {
            // The content of element type "p" must match
            // "(a|br|span|bdo|object|applet|img|map|iframe|tt|i|b|u|s|strike|big|small|font|basefont|em|strong|
            // dfn|code|q|samp|kbd|var|cite|abbr|acronym|sub|sup|input|select|textarea|label|button|ins|del|script)".
            paragraph_();
        }

        // start table with tableRows
        if ( attributes == null )
        {
            this.tableAttributes = new SinkEventAttributeSet( 0 );
        }
        else
        {
            this.tableAttributes = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_TABLE_ATTRIBUTES  );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TABLE
     */
    @Override
    public void table_()
    {
        this.tableRows = false;

        writeEndTag( HtmlMarkup.TABLE );

        if ( !this.cellCountStack.isEmpty() )
        {
            this.cellCountStack.removeLast().toString();
        }

        if ( this.tableContentWriterStack.isEmpty() )
        {
            if ( getLog().isWarnEnabled() )
            {
                getLog().warn( "No table content." );
            }
            return;
        }

        String tableContent = this.tableContentWriterStack.removeLast().toString();

        String tableCaption = null;
        if ( !this.tableCaptionStack.isEmpty() && this.tableCaptionStack.getLast() != null )
        {
            tableCaption = this.tableCaptionStack.removeLast();
        }

        if ( tableCaption != null )
        {
            // DOXIA-177
            StringBuilder sb = new StringBuilder();
            sb.append( tableContent, 0, tableContent.indexOf( Markup.GREATER_THAN ) + 1 );
            sb.append( tableCaption );
            sb.append( tableContent.substring( tableContent.indexOf( Markup.GREATER_THAN ) + 1 ) );

            write( sb.toString() );
        }
        else
        {
            write( tableContent );
        }
    }

    /**
     * The default class style is <code>bodyTable</code>.
     * The default align is <code>center</code>.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TABLE
     */
    @Override
    public void tableRows( int[] justification, boolean grid )
    {
        this.tableRows = true;

        setCellJustif( justification );

        if ( this.tableAttributes == null )
        {
            this.tableAttributes = new SinkEventAttributeSet( 0 );
        }

        MutableAttributeSet att = new SinkEventAttributeSet();
        if ( !this.tableAttributes.isDefined( Attribute.BORDER.toString() ) )
        {
            att.addAttribute( Attribute.BORDER, ( grid ? "1" : "0" ) );
        }

        if ( !this.tableAttributes.isDefined( Attribute.CLASS.toString() ) )
        {
            att.addAttribute( Attribute.CLASS, "bodyTable" );
        }

        att.addAttributes( this.tableAttributes );
        this.tableAttributes.removeAttributes( this.tableAttributes );

        writeStartTag( HtmlMarkup.TABLE, att );

        this.cellCountStack.addLast( 0 );
    }

    /** {@inheritDoc} */
    @Override
    public void tableRows_()
    {
        this.tableRows = false;
        if ( !this.cellJustifStack.isEmpty() )
        {
            this.cellJustifStack.removeLast();
        }
        if ( !this.isCellJustifStack.isEmpty() )
        {
            this.isCellJustifStack.removeLast();
        }

        this.evenTableRow = true;
    }

    /**
     * The default class style is <code>a</code> or <code>b</code> depending the row id.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TR
     */
    @Override
    public void tableRow()
    {
        // To be backward compatible
        if ( !this.tableRows )
        {
            tableRows( null, false );
        }
        tableRow( null );
    }

    /**
     * The default class style is <code>a</code> or <code>b</code> depending the row id.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TR
     */
    @Override
    public void tableRow( SinkEventAttributes attributes )
    {
        MutableAttributeSet att = new SinkEventAttributeSet();

        if ( evenTableRow )
        {
            att.addAttribute( Attribute.CLASS, "a" );
        }
        else
        {
            att.addAttribute( Attribute.CLASS, "b" );
        }

        att.addAttributes( SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_TR_ATTRIBUTES  ) );

        writeStartTag( HtmlMarkup.TR, att );

        evenTableRow = !evenTableRow;

        if ( !this.cellCountStack.isEmpty() )
        {
            this.cellCountStack.removeLast();
            this.cellCountStack.addLast( 0 );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TR
     */
    @Override
    public void tableRow_()
    {
        writeEndTag( HtmlMarkup.TR );
    }

    /** {@inheritDoc} */
    @Override
    public void tableCell()
    {
        tableCell( (SinkEventAttributeSet) null );
    }

    /** {@inheritDoc} */
    @Override
    public void tableHeaderCell()
    {
        tableHeaderCell( (SinkEventAttributeSet) null );
    }

    /** {@inheritDoc} */
    @Override
    public void tableCell( String width )
    {
        MutableAttributeSet att = new SinkEventAttributeSet();
        att.addAttribute( Attribute.WIDTH, width );

        tableCell( false, att );
    }

    /** {@inheritDoc} */
    @Override
    public void tableHeaderCell( String width )
    {
        MutableAttributeSet att = new SinkEventAttributeSet();
        att.addAttribute( Attribute.WIDTH, width );

        tableCell( true, att );
    }

    /** {@inheritDoc} */
    @Override
    public void tableCell( SinkEventAttributes attributes )
    {
        tableCell( false, attributes );
    }

    /** {@inheritDoc} */
    @Override
    public void tableHeaderCell( SinkEventAttributes attributes )
    {
        tableCell( true, attributes );
    }

    /**
     * @param headerRow true if it is an header row
     * @param attributes the cell attributes
     * @see javax.swing.text.html.HTML.Tag#TH
     * @see javax.swing.text.html.HTML.Tag#TD
     */
    private void tableCell( boolean headerRow, MutableAttributeSet attributes )
    {
        Tag t = ( headerRow ? HtmlMarkup.TH : HtmlMarkup.TD );

        if ( attributes == null )
        {
            writeStartTag( t, null );
        }
        else
        {
            writeStartTag( t,
                SinkUtils.filterAttributes( attributes, SinkUtils.SINK_TD_ATTRIBUTES ) );
        }
    }

    /** {@inheritDoc} */
    @Override
    public void tableCell_()
    {
        tableCell_( false );
    }

    /** {@inheritDoc} */
    @Override
    public void tableHeaderCell_()
    {
        tableCell_( true );
    }

    /**
     * Ends a table cell.
     *
     * @param headerRow true if it is an header row
     * @see javax.swing.text.html.HTML.Tag#TH
     * @see javax.swing.text.html.HTML.Tag#TD
     */
    private void tableCell_( boolean headerRow )
    {
        Tag t = ( headerRow ? HtmlMarkup.TH : HtmlMarkup.TD );

        writeEndTag( t );

        if ( !this.isCellJustifStack.isEmpty() && this.isCellJustifStack.getLast().equals( Boolean.TRUE )
            && !this.cellCountStack.isEmpty() )
        {
            int cellCount = Integer.parseInt( this.cellCountStack.removeLast().toString() );
            this.cellCountStack.addLast( ++cellCount );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#CAPTION
     */
    @Override
    public void tableCaption()
    {
        tableCaption( null );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#CAPTION
     */
    @Override
    public void tableCaption( SinkEventAttributes attributes )
    {
        StringWriter sw = new StringWriter();
        this.tableCaptionWriterStack.addLast( sw );
        this.tableCaptionXMLWriterStack.addLast( new PrettyPrintXMLWriter( sw ) );

        // TODO: tableCaption should be written before tableRows (DOXIA-177)
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_SECTION_ATTRIBUTES  );

        writeStartTag( HtmlMarkup.CAPTION, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#CAPTION
     */
    @Override
    public void tableCaption_()
    {
        writeEndTag( HtmlMarkup.CAPTION );

        if ( !this.tableCaptionXMLWriterStack.isEmpty() && this.tableCaptionXMLWriterStack.getLast() != null )
        {
            this.tableCaptionStack.addLast( this.tableCaptionWriterStack.removeLast().toString() );
            this.tableCaptionXMLWriterStack.removeLast();
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#A
     */
    @Override
    public void anchor( String name )
    {
        anchor( name, null );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#A
     */
    @Override
    public void anchor( String name, SinkEventAttributes attributes )
    {
        if ( name == null )
        {
            throw new NullPointerException( "Anchor name cannot be null!" );
        }

        if ( headFlag )
        {
            return;
        }

        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_BASE_ATTRIBUTES  );

        String id = name;

        if ( !DoxiaUtils.isValidId( id ) )
        {
            id = DoxiaUtils.encodeId( name, true );

            String msg = "Modified invalid anchor name: '" + name + "' to '" + id + "'";
            logMessage( "modifiedLink", msg );
        }

        MutableAttributeSet att = new SinkEventAttributeSet();
        att.addAttribute( Attribute.NAME, id );
        att.addAttributes( atts );

        writeStartTag( HtmlMarkup.A, att );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#A
     */
    @Override
    public void anchor_()
    {
        if ( !headFlag )
        {
            writeEndTag( HtmlMarkup.A );
        }
    }

    /** {@inheritDoc} */
    @Override
    public void link( String name )
    {
        link( name, null );
    }

    /** {@inheritDoc} */
    @Override
    public void link( String name, SinkEventAttributes attributes )
    {
        if ( attributes == null )
        {
            link( name, null, null );
        }
        else
        {
            String target = (String) attributes.getAttribute( Attribute.TARGET.toString() );
            MutableAttributeSet atts = SinkUtils.filterAttributes(
                    attributes, SinkUtils.SINK_LINK_ATTRIBUTES  );

            link( name, target, atts );
        }
    }

    /**
     * Adds a link with an optional target.
     * The default style class for external link is <code>externalLink</code>.
     *
     * @param href the link href.
     * @param target the link target, may be null.
     * @param attributes an AttributeSet, may be null.
     *      This is supposed to be filtered already.
     * @see javax.swing.text.html.HTML.Tag#A
     */
    private void link( String href, String target, MutableAttributeSet attributes )
    {
        if ( href == null )
        {
            throw new NullPointerException( "Link name cannot be null!" );
        }

        if ( headFlag )
        {
            return;
        }

        MutableAttributeSet att = new SinkEventAttributeSet();

        if ( DoxiaUtils.isExternalLink( href  ) )
        {
            att.addAttribute( Attribute.CLASS, "externalLink" );
        }

        att.addAttribute( Attribute.HREF, HtmlTools.escapeHTML( href  ) );

        if ( target != null )
        {
            att.addAttribute( Attribute.TARGET, target );
        }

        if ( attributes != null )
        {
            attributes.removeAttribute( Attribute.HREF.toString() );
            attributes.removeAttribute( Attribute.TARGET.toString() );
            att.addAttributes( attributes );
        }

        writeStartTag( HtmlMarkup.A, att );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#A
     */
    @Override
    public void link_()
    {
        if ( !headFlag )
        {
            writeEndTag( HtmlMarkup.A );
        }
    }

    /** {@inheritDoc} */
    @Override
    public void inline()
    {
        inline( null );
    }

    private void inlineSemantics( SinkEventAttributes attributes, String semantic,
            List<Tag> tags, Tag tag )
    {
        if ( attributes.containsAttribute( SinkEventAttributes.SEMANTICS, semantic ) )
        {
            writeStartTag( tag );
            tags.add( 0, tag );
        }
    }

    /** {@inheritDoc} */
    @Override
    public void inline( SinkEventAttributes attributes )
    {
        if ( !headFlag )
        {
            List<Tag> tags = new ArrayList<>();

            if ( attributes != null )
            {
                inlineSemantics( attributes, "emphasis", tags, HtmlMarkup.EM );
                inlineSemantics( attributes, "strong", tags, HtmlMarkup.STRONG );
                inlineSemantics( attributes, "small", tags, HtmlMarkup.SMALL );
                inlineSemantics( attributes, "line-through", tags, HtmlMarkup.S );
                inlineSemantics( attributes, "citation", tags, HtmlMarkup.CITE );
                inlineSemantics( attributes, "quote", tags, HtmlMarkup.Q );
                inlineSemantics( attributes, "definition", tags, HtmlMarkup.DFN );
                inlineSemantics( attributes, "abbreviation", tags, HtmlMarkup.ABBR );
                inlineSemantics( attributes, "italic", tags, HtmlMarkup.I );
                inlineSemantics( attributes, "bold", tags, HtmlMarkup.B );
                inlineSemantics( attributes, "code", tags, HtmlMarkup.CODE );
                inlineSemantics( attributes, "variable", tags, HtmlMarkup.VAR );
                inlineSemantics( attributes, "sample", tags, HtmlMarkup.SAMP );
                inlineSemantics( attributes, "keyboard", tags, HtmlMarkup.KBD );
                inlineSemantics( attributes, "superscript", tags, HtmlMarkup.SUP );
                inlineSemantics( attributes, "subscript", tags, HtmlMarkup.SUB );
                inlineSemantics( attributes, "annotation", tags, HtmlMarkup.U );
                inlineSemantics( attributes, "highlight", tags, HtmlMarkup.MARK );
                inlineSemantics( attributes, "ruby", tags, HtmlMarkup.RUBY );
                inlineSemantics( attributes, "rubyBase", tags, HtmlMarkup.RB );
                inlineSemantics( attributes, "rubyText", tags, HtmlMarkup.RT );
                inlineSemantics( attributes, "rubyTextContainer", tags, HtmlMarkup.RTC );
                inlineSemantics( attributes, "rubyParentheses", tags, HtmlMarkup.RP );
                inlineSemantics( attributes, "bidirectionalIsolation", tags, HtmlMarkup.BDI );
                inlineSemantics( attributes, "bidirectionalOverride", tags, HtmlMarkup.BDO );
                inlineSemantics( attributes, "phrase", tags, HtmlMarkup.SPAN );
                inlineSemantics( attributes, "insert", tags, HtmlMarkup.INS );
                inlineSemantics( attributes, "delete", tags, HtmlMarkup.DEL );
            }

            inlineStack.push( tags );
        }
    }

    /** {@inheritDoc} */
    @Override
    public void inline_()
    {
        if ( !headFlag )
        {
            for ( Tag tag: inlineStack.pop() )
            {
                writeEndTag( tag );
            }
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#I
     */
    @Override
    public void italic()
    {
        inline( SinkEventAttributeSet.Semantics.ITALIC );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#I
     */
    @Override
    public void italic_()
    {
        inline_();
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#B
     */
    @Override
    public void bold()
    {
        inline( SinkEventAttributeSet.Semantics.BOLD );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#B
     */
    @Override
    public void bold_()
    {
        inline_();
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#CODE
     */
    @Override
    public void monospaced()
    {
        inline( SinkEventAttributeSet.Semantics.CODE );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#CODE
     */
    @Override
    public void monospaced_()
    {
        inline_();
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#BR
     */
    @Override
    public void lineBreak()
    {
        lineBreak( null );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#BR
     */
    @Override
    public void lineBreak( SinkEventAttributes attributes )
    {
        if ( headFlag || isVerbatimFlag() )
        {
            getTextBuffer().append( EOL );
        }
        else
        {
            MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_BR_ATTRIBUTES  );

            writeSimpleTag( HtmlMarkup.BR, atts );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#WBR
     */
    @Override
    public void lineBreakOpportunity()
    {
        lineBreakOpportunity( null );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#WBR
     */
    @Override
    public void lineBreakOpportunity( SinkEventAttributes attributes )
    {
        if ( !headFlag && !isVerbatimFlag() )
        {
            MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_BR_ATTRIBUTES  );

            writeSimpleTag( HtmlMarkup.WBR, atts );
        }
    }

    /** {@inheritDoc} */
    @Override
    public void pageBreak()
    {
        comment( " PB " );
    }

    /** {@inheritDoc} */
    @Override
    public void nonBreakingSpace()
    {
        if ( headFlag )
        {
            getTextBuffer().append( ' ' );
        }
        else
        {
            write( "&#160;" );
        }
    }

    /** {@inheritDoc} */
    @Override
    public void text( String text )
    {
        if ( headFlag )
        {
            getTextBuffer().append( text );
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
    @Override
    public void text( String text, SinkEventAttributes attributes )
    {
        text( text );
    }

    /** {@inheritDoc} */
    @Override
    public void rawText( String text )
    {
        if ( headFlag )
        {
            getTextBuffer().append( text );
        }
        else
        {
            write( text );
        }
    }

    /** {@inheritDoc} */
    @Override
    public void comment( String comment )
    {
        if ( comment != null )
        {
            final String originalComment = comment;

            // http://www.w3.org/TR/2000/REC-xml-20001006#sec-comments
            while ( comment.contains( "--" ) )
            {
                comment = comment.replace( "--", "- -" );
            }

            if ( comment.endsWith( "-" ) )
            {
                comment += " ";
            }

            if ( !originalComment.equals( comment ) )
            {
                getLog().warn( "[Xhtml5 Sink] Modified invalid comment '" + originalComment
                        + "' to '" + comment + "'" );
            }

            final StringBuilder buffer = new StringBuilder( comment.length() + 7 );

            buffer.append( LESS_THAN ).append( BANG ).append( MINUS ).append( MINUS );
            buffer.append( comment );
            buffer.append( MINUS ).append( MINUS ).append( GREATER_THAN );

            write( buffer.toString() );
        }
    }

    /**
     * Add an unknown event.
     * This can be used to generate html tags for which no corresponding sink event exists.
     *
     * <p>
     * If {@link org.apache.maven.doxia.util.HtmlTools#getHtmlTag(String) HtmlTools.getHtmlTag( name )}
     * does not return null, the corresponding tag will be written.
     * </p>
     *
     * <p>For example, the div block</p>
     *
     * <pre>
     *  &lt;div class="detail" style="display:inline"&gt;text&lt;/div&gt;
     * </pre>
     *
     * <p>can be generated via the following event sequence:</p>
     *
     * <pre>
     *  SinkEventAttributeSet atts = new SinkEventAttributeSet();
     *  atts.addAttribute( SinkEventAttributes.CLASS, "detail" );
     *  atts.addAttribute( SinkEventAttributes.STYLE, "display:inline" );
     *  sink.unknown( "div", new Object[]{new Integer( HtmlMarkup.TAG_TYPE_START )}, atts );
     *  sink.text( "text" );
     *  sink.unknown( "div", new Object[]{new Integer( HtmlMarkup.TAG_TYPE_END )}, null );
     * </pre>
     *
     * @param name the name of the event. If this is not a valid xhtml tag name
     *      as defined in {@link org.apache.maven.doxia.markup.HtmlMarkup} then the event is ignored.
     * @param requiredParams If this is null or the first argument is not an Integer then the event is ignored.
     *      The first argument should indicate the type of the unknown event, its integer value should be one of
     *      {@link org.apache.maven.doxia.markup.HtmlMarkup#TAG_TYPE_START TAG_TYPE_START},
     *      {@link org.apache.maven.doxia.markup.HtmlMarkup#TAG_TYPE_END TAG_TYPE_END},
     *      {@link org.apache.maven.doxia.markup.HtmlMarkup#TAG_TYPE_SIMPLE TAG_TYPE_SIMPLE},
     *      {@link org.apache.maven.doxia.markup.HtmlMarkup#ENTITY_TYPE ENTITY_TYPE}, or
     *      {@link org.apache.maven.doxia.markup.HtmlMarkup#CDATA_TYPE CDATA_TYPE},
     *      otherwise the event will be ignored.
     * @param attributes a set of attributes for the event. May be null.
     *      The attributes will always be written, no validity check is performed.
     */
    @Override
    public void unknown( String name, Object[] requiredParams, SinkEventAttributes attributes )
    {
        if ( requiredParams == null || !( requiredParams[0] instanceof Integer ) )
        {
            String msg = "No type information for unknown event: '" + name + "', ignoring!";
            logMessage( "noTypeInfo", msg );

            return;
        }

        int tagType = (Integer) requiredParams[0];

        if ( tagType == ENTITY_TYPE )
        {
            rawText( name );

            return;
        }

        if ( tagType == CDATA_TYPE )
        {
            rawText( EOL + "//<![CDATA[" + requiredParams[1] + "]]>" + EOL );

            return;
        }

        Tag tag = HtmlTools.getHtmlTag( name );

        if ( tag == null )
        {
            String msg = "No HTML tag found for unknown event: '" + name + "', ignoring!";
            logMessage( "noHtmlTag", msg );
        }
        else
        {
            if ( tagType == TAG_TYPE_SIMPLE )
            {
                writeSimpleTag( tag, escapeAttributeValues( attributes ) );
            }
            else if ( tagType == TAG_TYPE_START )
            {
                writeStartTag( tag, escapeAttributeValues( attributes ) );
            }
            else if ( tagType == TAG_TYPE_END )
            {
                writeEndTag( tag );
            }
            else
            {
                String msg = "No type information for unknown event: '" + name + "', ignoring!";
                logMessage( "noTypeInfo", msg );
            }
        }
    }

    private SinkEventAttributes escapeAttributeValues( SinkEventAttributes attributes )
    {
        SinkEventAttributeSet set = new SinkEventAttributeSet( attributes.getAttributeCount() );

        Enumeration<?> names = attributes.getAttributeNames();

        while ( names.hasMoreElements() )
        {
            Object name = names.nextElement();

            set.addAttribute( name, escapeHTML( attributes.getAttribute( name ).toString() ) );
        }

        return set;
    }

    /** {@inheritDoc} */
    @Override
    public void flush()
    {
        writer.flush();
    }

    /** {@inheritDoc} */
    @Override
    public void close()
    {
        writer.close();

        if ( getLog().isWarnEnabled() && this.warnMessages != null )
        {
            for ( Map.Entry<String, Set<String>> entry : this.warnMessages.entrySet() )
            {
                for ( String msg : entry.getValue() )
                {
                    getLog().warn( msg );
                }
            }

            this.warnMessages = null;
        }

        init();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Write HTML escaped text to output.
     *
     * @param text The text to write.
     */
    protected void content( String text )
    {
        // small hack due to DOXIA-314
        String txt = escapeHTML( text );
        txt = StringUtils.replace( txt, "&amp;#", "&#" );
        write( txt );
    }

    /**
     * Write HTML escaped text to output.
     *
     * @param text The text to write.
     */
    protected void verbatimContent( String text )
    {
        write( escapeHTML( text ) );
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
        return HtmlTools.escapeHTML( text, false );
    }

    /**
     * Forward to HtmlTools.encodeURL( text ).
     *
     * @param text the String to encode, may be null.
     * @return the text encoded, null if null String input.
     * @see org.apache.maven.doxia.util.HtmlTools#encodeURL(String)
     */
    protected static String encodeURL( String text )
    {
        return HtmlTools.encodeURL( text );
    }

    /** {@inheritDoc} */
    protected void write( String text )
    {
        if ( !this.tableCaptionXMLWriterStack.isEmpty() && this.tableCaptionXMLWriterStack.getLast() != null )
        {
            this.tableCaptionXMLWriterStack.getLast().writeText( unifyEOLs( text ) );
        }
        else if ( !this.tableContentWriterStack.isEmpty() && this.tableContentWriterStack.getLast() != null )
        {
            this.tableContentWriterStack.getLast().write( unifyEOLs( text ) );
        }
        else
        {
            writer.write( unifyEOLs( text ) );
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void writeStartTag( Tag t, MutableAttributeSet att, boolean isSimpleTag )
    {
        if ( this.tableCaptionXMLWriterStack.isEmpty() )
        {
            super.writeStartTag ( t, att, isSimpleTag );
        }
        else
        {
            String tag = ( getNameSpace() != null ? getNameSpace() + ":" : "" ) + t.toString();
            this.tableCaptionXMLWriterStack.getLast().startElement( tag );

            if ( att != null )
            {
                Enumeration<?> names = att.getAttributeNames();
                while ( names.hasMoreElements() )
                {
                    Object key = names.nextElement();
                    Object value = att.getAttribute( key );

                    this.tableCaptionXMLWriterStack.getLast().addAttribute( key.toString(), value.toString() );
                }
            }

            if ( isSimpleTag )
            {
                this.tableCaptionXMLWriterStack.getLast().endElement();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void writeEndTag( Tag t )
    {
        if ( this.tableCaptionXMLWriterStack.isEmpty() )
        {
            super.writeEndTag( t );
        }
        else
        {
            this.tableCaptionXMLWriterStack.getLast().endElement();
        }
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
        final String mesg = "[XHTML5 Sink] " + msg;
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( mesg );

            return;
        }

        if ( warnMessages == null )
        {
            warnMessages = new HashMap<>();
        }

        Set<String> set = warnMessages.get( key );
        if ( set == null )
        {
            set = new TreeSet<>();
        }
        set.add( mesg );
        warnMessages.put( key, set );
    }
}
