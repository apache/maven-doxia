package org.apache.maven.doxia.module.docbook;

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

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.markup.HtmlMarkup;
import org.apache.maven.doxia.parser.AbstractXmlParser;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Parse a <a href="http://www.docbook.org/schemas/simplified"><code>Simplified DocBook</code></a> document
 * and emit events into the specified doxia Sink.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @since 1.0
 */
@Component( role = Parser.class, hint = "docbook" )
public class DocBookParser
    extends AbstractXmlParser
    implements DocbookMarkup, SimplifiedDocbookMarkup
{
    /**
     * Level counter for calculating the section level.
     */
    private int level;

    /**
     * Used to distinguish italic from bold.
     */
    private boolean isBold;

    private boolean inHead;

    private boolean ignore;

    private boolean simpleTag;

    private char trademark;

    /**
     * A selective stack of parent elements
     */
    private final Stack<String> parent = new Stack<>();

    /**
     * The list of DocBook elements that introduce a new level of hierarchy.
     */
    private static final Collection<String> HIER_ELEMENTS = new HashSet<>();

    /**
     * Simplified DocBook elements that are direct children of &lt;article&gt;
     * and that should be emitted into the Sink's head.
     */
    private static final Collection<String> META_ELEMENTS = new HashSet<>();

    /**
     * Simplified DocBook elements that occur within &lt;articleinfo&gt;
     * and that are currently recognized by the parser.
     */
    private static final Collection<String> ARTICLEINFO_ELEMENTS = new HashSet<>();

    /**
     * The list of DocBook elements that will be rendered verbatim
     */
    private static final Collection<String> VERBATIM_ELEMENTS = new HashSet<>();

    /**
     * The list of DocBook elements that will be rendered inline and bold
     */
    private static final Collection<String> BOLD_ELEMENTS = new HashSet<>();

    /**
     * The list of DocBook elements that will be rendered inline and italic
     */
    private static final Collection<String> ITALIC_ELEMENTS = new HashSet<>();

    /**
     * The list of DocBook elements that will be rendered inline and monospace
     */
    private static final Collection<String> MONOSPACE_ELEMENTS = new HashSet<>();

    /**
     * The list of DocBook elements that may be ignored, either because they don't
     * require any special processing or because they are not yet implemented.
     */
    private static final Collection<String> IGNORABLE_ELEMENTS = new HashSet<>();
    static
    {
        META_ELEMENTS.add( SimplifiedDocbookMarkup.ARTICLEINFO_TAG.toString() );
        META_ELEMENTS.add( SimplifiedDocbookMarkup.AUTHORBLURB_TAG.toString() );
        META_ELEMENTS.add( SimplifiedDocbookMarkup.SUBTITLE_TAG.toString() );
        META_ELEMENTS.add( SimplifiedDocbookMarkup.TITLE_TAG.toString() );
        META_ELEMENTS.add( SimplifiedDocbookMarkup.TITLEABBREV_TAG.toString() );

        ARTICLEINFO_ELEMENTS.add( SimplifiedDocbookMarkup.TITLE_TAG.toString() );
        ARTICLEINFO_ELEMENTS.add( SimplifiedDocbookMarkup.CORPAUTHOR_TAG.toString() );
        ARTICLEINFO_ELEMENTS.add( SimplifiedDocbookMarkup.DATE_TAG.toString() );

        HIER_ELEMENTS.add( SimplifiedDocbookMarkup.SECTION_TAG.toString() );
        HIER_ELEMENTS.add( SimplifiedDocbookMarkup.APPENDIX_TAG.toString() );
        HIER_ELEMENTS.add( SimplifiedDocbookMarkup.BIBLIOGRAPHY_TAG.toString() );
        HIER_ELEMENTS.add( SimplifiedDocbookMarkup.BIBLIODIV_TAG.toString() );

        VERBATIM_ELEMENTS.add( SimplifiedDocbookMarkup.PROGRAMLISTING_TAG.toString() );
        VERBATIM_ELEMENTS.add( SimplifiedDocbookMarkup.LITERALLAYOUT_TAG.toString() );

        BOLD_ELEMENTS.add( SimplifiedDocbookMarkup.COMMAND_TAG.toString() );
        BOLD_ELEMENTS.add( SimplifiedDocbookMarkup.USERINPUT_TAG.toString() );

        ITALIC_ELEMENTS.add( SimplifiedDocbookMarkup.REPLACEABLE_TAG.toString() );
        ITALIC_ELEMENTS.add( SimplifiedDocbookMarkup.SYSTEMITEM_TAG.toString() );
        ITALIC_ELEMENTS.add( SimplifiedDocbookMarkup.CITETITLE_TAG.toString() );
        ITALIC_ELEMENTS.add( SimplifiedDocbookMarkup.EMPHASIS_TAG.toString() );
        ITALIC_ELEMENTS.add( SimplifiedDocbookMarkup.ATTRIBUTION_TAG.toString() );
        ITALIC_ELEMENTS.add( SimplifiedDocbookMarkup.LINEANNOTATION_TAG.toString() );

        MONOSPACE_ELEMENTS.add( SimplifiedDocbookMarkup.COMPUTEROUTPUT_TAG.toString() );
        MONOSPACE_ELEMENTS.add( SimplifiedDocbookMarkup.REPLACEABLE_TAG.toString() );
        MONOSPACE_ELEMENTS.add( SimplifiedDocbookMarkup.LITERAL_TAG.toString() );
        MONOSPACE_ELEMENTS.add( SimplifiedDocbookMarkup.OPTION_TAG.toString() );
        MONOSPACE_ELEMENTS.add( SimplifiedDocbookMarkup.SYSTEMITEM_TAG.toString() );
        MONOSPACE_ELEMENTS.add( SimplifiedDocbookMarkup.USERINPUT_TAG.toString() );
        MONOSPACE_ELEMENTS.add( SimplifiedDocbookMarkup.FILENAME_TAG.toString() );

        IGNORABLE_ELEMENTS.add( SimplifiedDocbookMarkup.ABBREV_TAG.toString() );
        IGNORABLE_ELEMENTS.add( SimplifiedDocbookMarkup.ABSTRACT_TAG.toString() );
        IGNORABLE_ELEMENTS.add( SimplifiedDocbookMarkup.BIBLIOMIXED_TAG.toString() );
        IGNORABLE_ELEMENTS.add( SimplifiedDocbookMarkup.BIBLIOMSET_TAG.toString() );
        IGNORABLE_ELEMENTS.add( SimplifiedDocbookMarkup.COLSPEC_TAG.toString() );
        IGNORABLE_ELEMENTS.add( SimplifiedDocbookMarkup.EPIGRAPH_TAG.toString() );
        IGNORABLE_ELEMENTS.add( SimplifiedDocbookMarkup.EXAMPLE_TAG.toString() );
        IGNORABLE_ELEMENTS.add( SimplifiedDocbookMarkup.FOOTNOTEREF_TAG.toString() );
        IGNORABLE_ELEMENTS.add( SimplifiedDocbookMarkup.IMAGEOBJECT_TAG.toString() );
        IGNORABLE_ELEMENTS.add( SimplifiedDocbookMarkup.INLINEMEDIAOBJECT_TAG.toString() );
        IGNORABLE_ELEMENTS.add( SimplifiedDocbookMarkup.ISSUENUM_TAG.toString() );
        IGNORABLE_ELEMENTS.add( SimplifiedDocbookMarkup.PHRASE_TAG.toString() );
        IGNORABLE_ELEMENTS.add( SimplifiedDocbookMarkup.PUBDATE_TAG.toString() );
        IGNORABLE_ELEMENTS.add( SimplifiedDocbookMarkup.PUBLISHERNAME_TAG.toString() );
        IGNORABLE_ELEMENTS.add( SimplifiedDocbookMarkup.SPANSPEC_TAG.toString() );
        IGNORABLE_ELEMENTS.add( SimplifiedDocbookMarkup.TEXTOBJECT_TAG.toString() );
        IGNORABLE_ELEMENTS.add( SimplifiedDocbookMarkup.VOLUMENUM_TAG.toString() );
    }

    /** {@inheritDoc} */
    protected void init()
    {
        super.init();

        this.parent.clear();
        this.trademark = 0;
        this.level = 0;
        this.isBold = false;
        this.inHead = false;
        this.ignore = false;
        this.simpleTag = false;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    protected void handleStartTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        if ( inHead && !META_ELEMENTS.contains( parser.getName() )
                && isParent( SimplifiedDocbookMarkup.ARTICLE_TAG.toString() ) )
        {
            sink.head_();
            inHead = false;

            // assume any element that is not meta starts the body
            sink.body();
        }

        final SinkEventAttributeSet attribs = getAttributesFromParser( parser );
        simpleTag = parser.isEmptyElementTag();

        if ( parser.getName().equals( SimplifiedDocbookMarkup.ARTICLE_TAG.toString() ) )
        {
            handleArticleStart( sink, attribs );
        }
        else if ( isParent( SimplifiedDocbookMarkup.ARTICLEINFO_TAG.toString() ) )
        {
            handleArticleInfoStartTags( parser.getName(), sink, attribs );
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.ARTICLEINFO_TAG.toString() ) )
        {
            parent.push( SimplifiedDocbookMarkup.ARTICLEINFO_TAG.toString() );
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.FOOTNOTE_TAG.toString() )
                || parser.getName().equals( SimplifiedDocbookMarkup.SECTIONINFO_TAG.toString() )
                || parser.getName().equals( SimplifiedDocbookMarkup.VIDEOOBJECT_TAG.toString() )
                || parser.getName().equals( SimplifiedDocbookMarkup.AUDIOOBJECT_TAG.toString() ) )
        {
            parent.push( parser.getName() );
            ignore = true;
        }
        else if ( isParent( ( SimplifiedDocbookMarkup.FOOTNOTE_TAG.toString() ) )
                || isParent( SimplifiedDocbookMarkup.AUDIOOBJECT_TAG.toString() )
                || isParent( SimplifiedDocbookMarkup.VIDEOOBJECT_TAG.toString() )
                || isParent( SimplifiedDocbookMarkup.SECTIONINFO_TAG.toString() )
                || isParent( SimplifiedDocbookMarkup.ENTRYTBL_TAG.toString() ) )
        {
            return; // TODO: implement footnotes, entrytbl
        }
        else if ( HIER_ELEMENTS.contains( parser.getName() ) )
        {
            handleSectionElements( sink, parser.getName(), attribs );
        }
        else if ( listStartTags ( parser.getName(), sink, attribs ) )
        {
            return;
        }
        else if ( mediaStartTag( parser.getName(), sink, attribs ) )
        {
            return;
        }
        else if ( tableStartTags( parser.getName(), sink, attribs ) )
        {
            return;
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.PARA_TAG.toString() ) )
        {
            handleParaStart( sink, attribs );
        }
        else if ( styleStartTags( parser.getName(), sink, attribs ) )
        {
            return;
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.TITLE_TAG.toString() ) )
        {
            handleTitleStart( sink, attribs );
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.EMAIL_TAG.toString() ) )
        {
            handleEmailStart( parser, sink, attribs );
        }
        else if ( linkStartTag( parser.getName(), sink, attribs ) )
        {
            return;
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.QUOTE_TAG.toString() ) )
        {
            sink.text( "\"", null );
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.TRADEMARK_TAG.toString() ) )
        {
            trademark = '\u2122';
            final Object trade = attribs.getAttribute( "class" );

            if ( trade != null )
            {
                trademark = DocbookUtils.trademarkFromClass( trade.toString() );
            }
        }
        else
        {
            if ( !ignorable( parser.getName() ) )
            {
                if ( simpleTag )
                {
                    handleUnknown( parser, sink, HtmlMarkup.TAG_TYPE_SIMPLE );
                }
                else
                {
                    handleUnknown( parser, sink, HtmlMarkup.TAG_TYPE_START );
                }
            }
        }
    }

    /** {@inheritDoc} */
    protected void handleEndTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        if ( parser.getName().equals( SimplifiedDocbookMarkup.ARTICLE_TAG.toString() ) )
        {
            sink.body_();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.ARTICLEINFO_TAG.toString() ) )
        {
            parent.pop();
        }
        else if ( isParent( SimplifiedDocbookMarkup.ARTICLEINFO_TAG.toString() ) )
        {
             handleArticleInfoEndTags( parser.getName(), sink );
        }
        else if ( HIER_ELEMENTS.contains( parser.getName() ) )
        {
            sink.section_( level );

            //decrease the nesting level
            level--;
            parent.pop();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.FOOTNOTE_TAG.toString() )
                || parser.getName().equals( SimplifiedDocbookMarkup.AUDIOOBJECT_TAG.toString() )
                || parser.getName().equals( SimplifiedDocbookMarkup.VIDEOOBJECT_TAG.toString() )
                || parser.getName().equals( SimplifiedDocbookMarkup.SECTIONINFO_TAG.toString() )
                || parser.getName().equals( SimplifiedDocbookMarkup.ENTRYTBL_TAG.toString() ) )
        {
            parent.pop();
            ignore = false;
        }
        else if ( isParent( ( SimplifiedDocbookMarkup.FOOTNOTE_TAG.toString() ) )
                || isParent( SimplifiedDocbookMarkup.AUDIOOBJECT_TAG.toString() )
                || isParent( SimplifiedDocbookMarkup.VIDEOOBJECT_TAG.toString() )
                || isParent( SimplifiedDocbookMarkup.SECTIONINFO_TAG.toString() )
                || isParent( SimplifiedDocbookMarkup.ENTRYTBL_TAG.toString() ) )
        {
            return;
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.ITEMIZEDLIST_TAG.toString() ) )
        {
            sink.list_();
            parent.pop();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.ORDEREDLIST_TAG.toString() ) )
        {
            sink.numberedList_();
            parent.pop();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.LISTITEM_TAG.toString() ) )
        {
            parent.pop();

            if ( isParent( SimplifiedDocbookMarkup.VARIABLELIST_TAG.toString() ) )
            {
                sink.definition_();
            }
            else if ( isParent( SimplifiedDocbookMarkup.ORDEREDLIST_TAG.toString() ) )
            {
                sink.numberedListItem_();
            }
            else
            {
                sink.listItem_();
            }
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.VARIABLELIST_TAG.toString() ) )
        {
            sink.definitionList_();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.VARLISTENTRY_TAG.toString() ) )
        {
            sink.definitionListItem_();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.TERM_TAG.toString() ) )
        {
            sink.definedTerm_();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.MEDIAOBJECT_TAG.toString() ) )
        {
            sink.figure_();
            parent.pop();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.IMAGEOBJECT_TAG.toString() )
                || parser.getName().equals( SimplifiedDocbookMarkup.FIGURE_TAG.toString() )
                || parser.getName().equals( SimplifiedDocbookMarkup.THEAD_TAG.toString() )
                || parser.getName().equals( SimplifiedDocbookMarkup.TFOOT_TAG.toString() )
                || parser.getName().equals( SimplifiedDocbookMarkup.TBODY_TAG.toString() ) )
        {
            parent.pop();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.CAPTION_TAG.toString() ) )
        {
            handleCaptionEnd( sink );
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.TABLE_TAG.toString() )
            || parser.getName().equals( SimplifiedDocbookMarkup.INFORMALTABLE_TAG.toString() ) )
        {
            sink.table_();

            parent.pop();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.TR_TAG.toString() )
                || parser.getName().equals( SimplifiedDocbookMarkup.ROW_TAG.toString() ) )
        {
            sink.tableRow_();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.TGROUP_TAG.toString() ) )
        {
            sink.tableRows_();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.ENTRY_TAG.toString() )
                && isParent( SimplifiedDocbookMarkup.THEAD_TAG.toString() )
            || parser.getName().equals( TH_TAG.toString() ) )
        {
            sink.tableHeaderCell_();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.ENTRY_TAG.toString() ) )
        {
            sink.tableCell_();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.PARA_TAG.toString() ) )
        {
            handleParaEnd( sink );
        }
        else if ( VERBATIM_ELEMENTS.contains( parser.getName() ) )
        {
            sink.verbatim_();
        }
        else if ( BOLD_ELEMENTS.contains( parser.getName() )
            && MONOSPACE_ELEMENTS.contains( parser.getName() ) )
        {
            sink.monospaced_();
            sink.bold_();
        }
        else if ( ITALIC_ELEMENTS.contains( parser.getName() )
            && MONOSPACE_ELEMENTS.contains( parser.getName() ) )
        {
            sink.monospaced_();
            sink.italic_();
        }
        else if ( BOLD_ELEMENTS.contains( parser.getName() ) )
        {
            sink.bold_();
        }
        else if ( ITALIC_ELEMENTS.contains( parser.getName() ) )
        {
            if ( isBold )
            {
                sink.bold_();

                isBold = false;
            }
            else
            {
                sink.italic_();
            }
        }
        else if ( MONOSPACE_ELEMENTS.contains( parser.getName() ) )
        {
            sink.monospaced_();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.TITLE_TAG.toString() ) )
        {
            handleTitleEnd( sink );
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.ULINK_TAG.toString() )
                || parser.getName().equals( SimplifiedDocbookMarkup.LINK_TAG.toString() ) )
        {
            if ( isParent( parser.getName() ) )
            {
                parent.pop();
                sink.link_();
            }
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.QUOTE_TAG.toString() ) )
        {
            sink.text( "\"", null );
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.TRADEMARK_TAG.toString() ) )
        {
            sink.text( Character.toString( trademark ), null );
        }
        else if ( !simpleTag && !ignorable( parser.getName() ) )
        {
            handleUnknown( parser, sink, HtmlMarkup.TAG_TYPE_END );
        }
    }

    /** {@inheritDoc} */
    protected void handleComment( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        final String text = parser.getText();

        switch ( text.trim() )
        {
            case "PB":
                sink.pageBreak();
                break;
            case "HR":
                sink.horizontalRule();
                break;
            case "LB":
                sink.lineBreak();
                break;
            case "anchor_end":
                sink.anchor_();
                break;
            default:
                if ( isEmitComments() )
                {
                    sink.comment( text );
                }
                break;
        }
    }

    /** {@inheritDoc} */
    protected void handleCdsect( XmlPullParser parser, Sink sink )
            throws XmlPullParserException
    {
        if ( !ignore )
        {
            super.handleCdsect( parser, sink );
        }
    }

    /** {@inheritDoc} */
    protected void handleEntity( XmlPullParser parser, Sink sink )
            throws XmlPullParserException
    {
        if ( !ignore )
        {
            super.handleEntity( parser, sink );
        }
    }

    /** {@inheritDoc} */
    protected void handleText( XmlPullParser parser, Sink sink )
            throws XmlPullParserException
    {
        if ( !ignore )
        {
            super.handleText( parser, sink );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void handleArticleInfoStartTags( String name, Sink sink, SinkEventAttributeSet attribs )
    {
        if ( !ARTICLEINFO_ELEMENTS.contains( name ) )
        {
            ignore = true;
            return; // TODO: other meta data are ignored, implement!
        }

        if ( name.equals( SimplifiedDocbookMarkup.TITLE_TAG.toString() ) )
        {
            sink.title( attribs );
        }
        else if ( name.equals( SimplifiedDocbookMarkup.CORPAUTHOR_TAG.toString() ) )
        {
            sink.author( attribs );
        }
        else if ( name.equals( SimplifiedDocbookMarkup.DATE_TAG.toString() ) )
        {
            sink.date( attribs );
        }
    }

    private void handleArticleInfoEndTags( String name, Sink sink )
    {
        if ( !ARTICLEINFO_ELEMENTS.contains( name ) )
        {
            ignore = false;
            return; // TODO: other meta data are ignored, implement!
        }

        if ( name.equals( SimplifiedDocbookMarkup.TITLE_TAG.toString() ) )
        {
            sink.title_();
        }
        else if ( name.equals( SimplifiedDocbookMarkup.CORPAUTHOR_TAG.toString() ) )
        {
            sink.author_();
        }
        else if ( name.equals( SimplifiedDocbookMarkup.DATE_TAG.toString() ) )
        {
            sink.date_();
        }
    }

    private void handleCaptionStart( Sink sink, SinkEventAttributeSet attribs )
    {
        if ( isParent( SimplifiedDocbookMarkup.MEDIAOBJECT_TAG.toString() ) )
        {
            sink.figureCaption( attribs );
        }
        else if ( isParent( SimplifiedDocbookMarkup.INFORMALTABLE_TAG.toString() )
            || isParent( SimplifiedDocbookMarkup.TABLE_TAG.toString() ) )
        {
            sink.tableCaption( attribs );
        }

        parent.push( SimplifiedDocbookMarkup.CAPTION_TAG.toString() );
    }

    private void handleCaptionEnd( Sink sink )
    {
        parent.pop();

        if ( isParent( SimplifiedDocbookMarkup.MEDIAOBJECT_TAG.toString() ) )
        {
            sink.figureCaption_();
        }
        else if ( isParent( SimplifiedDocbookMarkup.INFORMALTABLE_TAG.toString() )
            || isParent( SimplifiedDocbookMarkup.TABLE_TAG.toString() ) )
        {
            sink.tableCaption_();
        }
    }

    private void handleEmailStart( XmlPullParser parser, Sink sink, SinkEventAttributeSet attribs )
            throws XmlPullParserException
    {
        try
        {
            final String mailto = parser.nextText();
            sink.link( "mailto:" + mailto, attribs );
            sink.monospaced();
            sink.text( mailto, null );
            sink.monospaced_();
            sink.link_();
        }
        catch ( IOException e )
        {
            throw new XmlPullParserException( "IOException: " + e.getMessage(), parser, e );
        }
    }

    private void handleFigureStart( Sink sink, SinkEventAttributeSet attribs )
    {
        sink.figure( attribs );
        parent.push( SimplifiedDocbookMarkup.MEDIAOBJECT_TAG.toString() );
    }

    private void handleArticleStart( Sink sink, SinkEventAttributeSet attribs )
    {
        sink.head( attribs );
        inHead = true;

        parent.push( SimplifiedDocbookMarkup.ARTICLE_TAG.toString() );
    }

    //If the element introduces a new level of hierarchy, raise the stack
    private void handleSectionElements( Sink sink, String name, SinkEventAttributeSet attribs )
    {
        //increase the nesting level
        level++;

        sink.section( level, attribs );

        parent.push( name );
    }

    private void handleAnchorStart( Sink sink, SinkEventAttributeSet attribs  )
    {
        final Object id = attribs.getAttribute( SimplifiedDocbookMarkup.ID_ATTRIBUTE );

        if ( id != null )
        {
            sink.anchor( id.toString(), attribs );
        }
    }

    private void handleImageDataStart( Sink sink, SinkEventAttributeSet attribs )
            throws XmlPullParserException
    {
        final Object fileref = attribs.getAttribute( SimplifiedDocbookMarkup.FILEREF_ATTRIBUTE );

        if ( fileref == null )
        {
            throw new XmlPullParserException( "Missing fileref attribute in imagedata!" );
        }

        sink.figureGraphics( fileref.toString(), attribs );
    }

    private void handleItemizedListStart( Sink sink, SinkEventAttributeSet attribs )
    {
        sink.list( attribs );
        //for itemizedlists in variablelists
        parent.push( SimplifiedDocbookMarkup.ITEMIZEDLIST_TAG.toString() );
    }

    private void handleLinkStart( Sink sink, SinkEventAttributeSet attribs )
            throws XmlPullParserException
    {
        final Object linkend = attribs.getAttribute( SimplifiedDocbookMarkup.LINKEND_ATTRIBUTE );

        if ( linkend == null )
        {
            throw new XmlPullParserException( "Missing linkend attribute in link!" );
        }

        parent.push( SimplifiedDocbookMarkup.LINK_TAG.toString() );
        sink.link( "#" + linkend.toString(), attribs );
    }

    private void handleListItemStart( Sink sink, SinkEventAttributeSet attribs )
    {
        if ( isParent( SimplifiedDocbookMarkup.VARIABLELIST_TAG.toString() ) )
        {
            sink.definition( attribs );
        }
        else if ( isParent( SimplifiedDocbookMarkup.ORDEREDLIST_TAG.toString() ) )
        {
            sink.numberedListItem( attribs );
        }
        else
        {
            sink.listItem( attribs );
        }

        parent.push( SimplifiedDocbookMarkup.LISTITEM_TAG.toString() );
    }

    private void handleOrderedListStart( Sink sink, SinkEventAttributeSet attribs )
    {
        //default enumeration style is decimal
        int numeration = Sink.NUMBERING_DECIMAL;

        final Object num = attribs.getAttribute( SimplifiedDocbookMarkup.NUMERATION_ATTRIBUTE );

        if ( num != null )
        {
            numeration = DocbookUtils.doxiaListNumbering( num.toString() );
        }

        sink.numberedList( numeration, attribs );
        parent.push( SimplifiedDocbookMarkup.ORDEREDLIST_TAG.toString() );
    }

    private void handleParaEnd( Sink sink )
    {
        if ( !isParent( SimplifiedDocbookMarkup.CAPTION_TAG.toString() )
                && ! isParent( SimplifiedDocbookMarkup.LISTITEM_TAG.toString() ) )
        {
            sink.paragraph_();
        }
    }

    private void handleParaStart( Sink sink, SinkEventAttributeSet attribs )
    {
        if ( !isParent( SimplifiedDocbookMarkup.CAPTION_TAG.toString() )
                && ! isParent( SimplifiedDocbookMarkup.LISTITEM_TAG.toString() ) )
        {
            sink.paragraph( attribs );
        }
    }

    private void handleTableStart( Sink sink, SinkEventAttributeSet attribs )
    {
        final Object frame = attribs.getAttribute( SimplifiedDocbookMarkup.FRAME_ATTRIBUTE );
        if ( frame != null )
        {
            attribs.addAttribute( SimplifiedDocbookMarkup.FRAME_ATTRIBUTE,
                    DocbookUtils.doxiaTableFrameAttribute( frame.toString() ) );
        }

        sink.table( attribs );

        parent.push( SimplifiedDocbookMarkup.TABLE_TAG.toString() );
    }

    private void handleTitleStart( Sink sink, SinkEventAttributeSet attribs )
    {
        if ( isParent( SimplifiedDocbookMarkup.TABLE_TAG.toString() )
                || isParent( SimplifiedDocbookMarkup.INFORMALTABLE_TAG.toString() ) )
        {
            sink.tableCaption( attribs );
        }
        else if ( isParent( SimplifiedDocbookMarkup.ARTICLE_TAG.toString() ) )
        {
            sink.title( attribs );
        }
        else if ( isParent( SimplifiedDocbookMarkup.SECTION_TAG.toString() ) )
        {
            sink.sectionTitle( level, attribs );
        }
        else
        {
            sink.bold();
        }
    }

    private void handleTitleEnd( Sink sink )
    {
        if ( isParent( SimplifiedDocbookMarkup.TABLE_TAG.toString() )
                || isParent( SimplifiedDocbookMarkup.INFORMALTABLE_TAG.toString() ) )
        {
            sink.tableCaption_();
        }
        else if ( isParent( SimplifiedDocbookMarkup.SECTION_TAG.toString() ) )
        {
            sink.sectionTitle_( level );
        }
        else if ( isParent( SimplifiedDocbookMarkup.ARTICLE_TAG.toString() ) )
        {
            sink.title_();
        }
        else
        {
            sink.bold_();
        }
    }

    private void handleUlinkStart( Sink sink, SinkEventAttributeSet attribs )
            throws XmlPullParserException
    {
        final Object url = attribs.getAttribute( SimplifiedDocbookMarkup.URL_ATTRIBUTE );

        if ( url == null )
        {
            throw new XmlPullParserException( "Missing url attribute in ulink!" );
        }

        parent.push( SimplifiedDocbookMarkup.ULINK_TAG.toString() );
        sink.link( url.toString(), attribs );
    }

    private void handleVariableListStart( Sink sink, SinkEventAttributeSet attribs )
    {
        sink.definitionList( attribs );
        parent.push( SimplifiedDocbookMarkup.VARIABLELIST_TAG.toString() );
    }

    private void handleXrefStart( Sink sink, SinkEventAttributeSet attribs )
            throws XmlPullParserException
    {
        final Object linkend = attribs.getAttribute( SimplifiedDocbookMarkup.LINKEND_ATTRIBUTE );

        if ( linkend == null )
        {
            throw new XmlPullParserException( "Missing linkend attribute in xref!" );
        }

        sink.link( "#" + linkend.toString(), attribs );
        sink.text( "Link" ); //TODO: determine text of link target
        sink.link_();
    }

    private boolean ignorable( String name )
    {
        return IGNORABLE_ELEMENTS.contains( name );
    }

    /**
     * Determines if the given element is a parent element.
     *
     * @param element the element to determine.
     * @return true if the given element is a parent element.
     */
    private boolean isParent( String element )
    {
        if ( parent.size() > 0 )
        {
            return parent.peek().equals( element );
        }

        return false;
    }

    private boolean linkStartTag( String name, Sink sink, SinkEventAttributeSet attribs )
            throws XmlPullParserException
    {
        if ( name.equals( SimplifiedDocbookMarkup.ULINK_TAG.toString() ) )
        {
            handleUlinkStart( sink, attribs );
        }
        else if ( name.equals( SimplifiedDocbookMarkup.LINK_TAG.toString() ) )
        {
            handleLinkStart( sink, attribs );
        }
        else if ( name.equals( SimplifiedDocbookMarkup.XREF_TAG.toString() ) )
        {
            handleXrefStart( sink, attribs );
        }
        else if ( name.equals( SimplifiedDocbookMarkup.ANCHOR_TAG.toString() ) )
        {
            handleAnchorStart( sink, attribs );
        }
        else
        {
            return false;
        }

        return true;
    }

    private boolean listStartTags( String name, Sink sink, SinkEventAttributeSet attribs )
    {
        if ( name.equals( SimplifiedDocbookMarkup.ITEMIZEDLIST_TAG.toString() ) )
        {
            handleItemizedListStart( sink, attribs );
        }
        else if ( name.equals( SimplifiedDocbookMarkup.ORDEREDLIST_TAG.toString() ) )
        {
            handleOrderedListStart( sink, attribs );
        }
        else if ( name.equals( SimplifiedDocbookMarkup.LISTITEM_TAG.toString() ) )
        {
            handleListItemStart( sink, attribs );
        }
        else if ( name.equals( SimplifiedDocbookMarkup.VARIABLELIST_TAG.toString() ) )
        {
            handleVariableListStart( sink, attribs );
        }
        else if ( name.equals( SimplifiedDocbookMarkup.VARLISTENTRY_TAG.toString() ) )
        {
            sink.definitionListItem( attribs );
        }
        else if ( name.equals( SimplifiedDocbookMarkup.TERM_TAG.toString() ) )
        {
            sink.definedTerm( attribs );
        }
        else
        {
            return false;
        }

        return true;
    }

    private boolean mediaStartTag( String name, Sink sink, SinkEventAttributeSet attribs )
            throws XmlPullParserException
    {
        if ( name.equals( SimplifiedDocbookMarkup.MEDIAOBJECT_TAG.toString() ) )
        {
            handleFigureStart( sink, attribs );
        }
        else if ( name.equals( SimplifiedDocbookMarkup.IMAGEOBJECT_TAG.toString() )
                || name.equals( SimplifiedDocbookMarkup.FIGURE_TAG.toString() ) )
        {
            parent.push( name );
        }
        else if ( name.equals( SimplifiedDocbookMarkup.IMAGEDATA_TAG.toString() ) )
        {
            handleImageDataStart( sink, attribs );
        }
        else if ( name.equals( SimplifiedDocbookMarkup.CAPTION_TAG.toString() ) )
        {
            handleCaptionStart( sink, attribs );
        }
        else
        {
            return false;
        }

        return true;
    }

    private boolean styleStartTags( String name, Sink sink, SinkEventAttributeSet attribs )
    {
        if ( VERBATIM_ELEMENTS.contains( name ) )
        {
            sink.verbatim( SinkEventAttributeSet.BOXED );
        }
        else if ( BOLD_ELEMENTS.contains( name ) && MONOSPACE_ELEMENTS.contains( name ) )
        {
            sink.bold();
            sink.monospaced();
        }
        else if ( ITALIC_ELEMENTS.contains( name ) && MONOSPACE_ELEMENTS.contains( name ) )
        {
            sink.italic();
            sink.monospaced();
        }
        else if ( BOLD_ELEMENTS.contains( name ) )
        {
            sink.bold();
        }
        else if ( ITALIC_ELEMENTS.contains( name ) && "bold".equals( attribs.getAttribute( "role" ) ) )
        {
            sink.bold();
            isBold = true;
        }
        else if ( ITALIC_ELEMENTS.contains( name ) )
        {
            sink.italic();
        }
        else if ( MONOSPACE_ELEMENTS.contains( name ) )
        {
            sink.monospaced();
        }
        else
        {
            return false;
        }

        return true;
    }

    private boolean tableStartTags( String name, Sink sink, SinkEventAttributeSet attribs )
    {
        if ( name.equals( SimplifiedDocbookMarkup.ENTRYTBL_TAG.toString() ) )
        {
            parent.push( name );
            ignore = true;
            // insert empty table cell instead
            sink.tableCell( (SinkEventAttributeSet) null );
            sink.tableCell_();
        }
        else if ( name.equals( SimplifiedDocbookMarkup.TABLE_TAG.toString() )
            || name.equals( SimplifiedDocbookMarkup.INFORMALTABLE_TAG.toString() ) )
        {
            handleTableStart( sink, attribs );
        }
        else if ( name.equals( SimplifiedDocbookMarkup.THEAD_TAG.toString() )
                || name.equals( SimplifiedDocbookMarkup.TFOOT_TAG.toString() )
                || name.equals( SimplifiedDocbookMarkup.TBODY_TAG.toString() ) )
        {
            parent.push( name );
        }
        else if ( name.equals( SimplifiedDocbookMarkup.TGROUP_TAG.toString() ) )
        {
            // this is required by the DTD
            final int cols = Integer.parseInt( (String) attribs.getAttribute( "cols" ) );
            int[] justification = new int[cols];
            int justif = Sink.JUSTIFY_LEFT;

            final Object align = attribs.getAttribute( SinkEventAttributeSet.ALIGN );

            if ( align != null )
            {
                final String al = align.toString();

                if ( "right".equals( al ) )
                {
                    justif = Sink.JUSTIFY_RIGHT;
                }
                else if ( "center".equals( al ) )
                {
                    justif = Sink.JUSTIFY_CENTER;
                }
            }

            for ( int i = 0; i < justification.length; i++ )
            {
                justification[i] = justif;
            }

            boolean grid = false;
            final Object rowsep = attribs.getAttribute( "rowsep" );

            if ( rowsep != null && Integer.parseInt( (String) rowsep ) == 1 )
            {
                grid = true;
            }

            final Object colsep = attribs.getAttribute( "colsep" );

            if ( colsep != null && Integer.parseInt( (String) colsep ) == 1 )
            {
                grid = true;
            }

            sink.tableRows( justification, grid );
        }
        else if ( name.equals( SimplifiedDocbookMarkup.TR_TAG.toString() )
                || name.equals( SimplifiedDocbookMarkup.ROW_TAG.toString() ) )
        {
            sink.tableRow( attribs );
        }
        else if ( name.equals( SimplifiedDocbookMarkup.ENTRY_TAG.toString() )
                && isParent( SimplifiedDocbookMarkup.THEAD_TAG.toString() )
                || name.equals( SimplifiedDocbookMarkup.TH_TAG.toString() ) )
        {
            sink.tableHeaderCell( attribs );
        }
        else if ( name.equals( SimplifiedDocbookMarkup.ENTRY_TAG.toString() ) )
        {
            sink.tableCell( attribs );
        }
        else
        {
            return false;
        }

        return true;
    }
}
