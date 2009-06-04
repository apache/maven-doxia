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
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributeSet;

import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Parse a <a href="http://www.docbook.org/schemas/simplified"><code>Simplified DocBook</code></a> document
 * and emit events into the specified doxia Sink.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @since 1.0
 * @plexus.component role="org.apache.maven.doxia.parser.Parser" role-hint="docbook"
 */
public class DocBookParser
    extends AbstractXmlParser
    implements DocbookMarkup, SimplifiedDocbookMarkup
{
    /**
     * Level counter for calculating the section level.
     */
    private int level = -1;

    /**
     * Used to distinguish italic from bold.
     */
    private boolean isBold;

    /**
     * A selective stack of parent elements
     */
    private Stack parent = new Stack();

    /**
     * The list of DocBook elements that introduce a new level of hierarchy.
     */
    private static final Collection HIER_ELEMENTS = new HashSet();

    /**
     * The list of DocBook elements that will be rendered verbatim
     */
    private static final Collection VERBATIM_ELEMENTS = new HashSet();

    /**
     * The list of DocBook elements that will be rendered inline and bold
     */
    private static final Collection BOLD_ELEMENTS = new HashSet();

    /**
     * The list of DocBook elements that will be rendered inline and italic
     */
    private static final Collection ITALIC_ELEMENTS = new HashSet();

    /**
     * The list of DocBook elements that will be rendered inline and monospace
     */
    private static final Collection MONOSPACE_ELEMENTS = new HashSet();

    static
    {
        HIER_ELEMENTS.add( SimplifiedDocbookMarkup.SECTION_TAG.toString() );
        HIER_ELEMENTS.add( SimplifiedDocbookMarkup.ARTICLE_TAG.toString() );
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

        MONOSPACE_ELEMENTS.add( SimplifiedDocbookMarkup.COMPUTEROUTPUT_TAG.toString() );
        MONOSPACE_ELEMENTS.add( SimplifiedDocbookMarkup.REPLACEABLE_TAG.toString() );
        MONOSPACE_ELEMENTS.add( SimplifiedDocbookMarkup.LITERAL_TAG.toString() );
        MONOSPACE_ELEMENTS.add( SimplifiedDocbookMarkup.OPTION_TAG.toString() );
        MONOSPACE_ELEMENTS.add( SimplifiedDocbookMarkup.SYSTEMITEM_TAG.toString() );
        MONOSPACE_ELEMENTS.add( SimplifiedDocbookMarkup.USERINPUT_TAG.toString() );
        MONOSPACE_ELEMENTS.add( SimplifiedDocbookMarkup.FILENAME_TAG.toString() );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    protected void handleStartTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        handleIdAnchor( getAttributeValue( parser, ID_ATTRIBUTE ), sink );

        if ( HIER_ELEMENTS.contains( parser.getName() ) )
        {
            handleHierarchyElements( sink );
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.ITEMIZEDLIST_TAG.toString() ) )
        {
            handleItemizedListStart( sink, parser);
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.ORDEREDLIST_TAG.toString() ) )
        {
            handleOrderedListStart( parser, sink );
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.LISTITEM_TAG.toString() ) )
        {
            handleListItemStart( sink );
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.VARIABLELIST_TAG.toString() ) )
        {
            handleVariableListStart( sink, parser);
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.VARLISTENTRY_TAG.toString() ) )
        {
            sink.definitionListItem();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.TERM_TAG.toString() ) )
        {
            sink.definedTerm();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.FIGURE_TAG.toString() ) )
        {
            handleFigureStart( sink, parser);
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.IMAGEOBJECT_TAG.toString() ) )
        {
            handleImageObjectStart( parser, sink );
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.CAPTION_TAG.toString() ) )
        {
            handleCaptionStart(sink);
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.TABLE_TAG.toString() )
            || parser.getName().equals( SimplifiedDocbookMarkup.INFORMALTABLE_TAG.toString() ) )
        {
            handleTableStart( sink, parser);
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.THEAD_TAG.toString() ) )
        {
            parent.push( parser.getName() );
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.TR_TAG.toString() )
                || parser.getName().equals( SimplifiedDocbookMarkup.ROW_TAG.toString() ) )
        {
            sink.tableRow();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.ENTRY_TAG.toString() )
                && isParent( SimplifiedDocbookMarkup.THEAD_TAG.toString() )
                || parser.getName().equals( SimplifiedDocbookMarkup.TH_TAG.toString() ) )
        {
            sink.tableHeaderCell();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.ENTRY_TAG.toString() ) )
        {
            sink.tableCell();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.PARA_TAG.toString() ) )
        {
            sink.paragraph();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.TITLE_TAG.toString() ) )
        {
            sink.bold();
        }
        else if ( VERBATIM_ELEMENTS.contains( parser.getName() ) )
        {
            sink.verbatim( SinkEventAttributeSet.BOXED );
        }
        else if ( BOLD_ELEMENTS.contains( parser.getName() ) && MONOSPACE_ELEMENTS.contains( parser.getName() ) )
        {
            sink.bold();
            sink.monospaced();
        }
        else if ( ITALIC_ELEMENTS.contains( parser.getName() ) && MONOSPACE_ELEMENTS.contains( parser.getName() ) )
        {
            sink.italic();
            sink.monospaced();
        }
        else if ( BOLD_ELEMENTS.contains( parser.getName() ) )
        {
            sink.bold();
        }
        else if ( ITALIC_ELEMENTS.contains( parser.getName() )
            && "bold".equals( parser.getAttributeValue( null, "role" ) ) )
        {
            sink.bold();
            isBold = true;
        }
        else if ( ITALIC_ELEMENTS.contains( parser.getName() ) )
        {
            sink.italic();
        }
        else if ( MONOSPACE_ELEMENTS.contains( parser.getName() ) )
        {
            sink.monospaced();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.TITLE_TAG.toString() ) )
        {
            handleTitleStart( parser, sink );
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.CORPAUTHOR_TAG.toString() ) )
        {
            sink.author();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.DATE_TAG.toString() ) )
        {
            sink.date();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.ULINK_TAG.toString() ) )
        {
            handleUlinkStart( parser, sink );
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.EMAIL_TAG.toString() ) )
        {
            handleEmailStart(parser, sink);
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.LINK_TAG.toString() ) )
        {
            handleLinkStart( parser, sink );
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.XREF_TAG.toString() ) )
        {
            handleXrefStart( parser, sink );
        }
        else
        {
            handleUnknown( parser, sink, HtmlMarkup.TAG_TYPE_START );
        }
    }

    /** {@inheritDoc} */
    protected void handleEndTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {

        //If the element introduces a new level of hierarchy, lower the stack
        if ( HIER_ELEMENTS.contains( parser.getName() ) )
        {
            //if this is the root element, handle it as body
            if ( level == 0 )
            {
                sink.body_();
            }
            else
            {
                sink.section_( level );
            }
            //decrease the nesting level
            level--;
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
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.FIGURE_TAG.toString() ) )
        {
            sink.figure_();
            parent.pop();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.CAPTION_TAG.toString() ) )
        {
            handleCaptionEnd(sink);
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.TABLE_TAG.toString() )
            || parser.getName().equals( SimplifiedDocbookMarkup.INFORMALTABLE_TAG.toString() ) )
        {
            sink.table_();
            //TODO handle tgroups
            parent.pop();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.THEAD_TAG.toString() ) )
        {
            parent.pop();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.TR_TAG.toString() )
                || parser.getName().equals( SimplifiedDocbookMarkup.ROW_TAG.toString() ) )
        {
            sink.tableRow_();
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
            sink.paragraph_();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.TITLE_TAG.toString() ) )
        {
            sink.text( ". " ); //Inline Running head
            sink.bold_();
        }
        else if ( VERBATIM_ELEMENTS.contains( parser.getName() ) )
        {
            sink.verbatim_();
        }
        else if ( BOLD_ELEMENTS.contains( parser.getName() )
            && MONOSPACE_ELEMENTS.contains( parser.getName() ) )
        {
            sink.bold_();
            sink.monospaced_();
        }
        else if ( ITALIC_ELEMENTS.contains( parser.getName() )
            && MONOSPACE_ELEMENTS.contains( parser.getName() ) )
        {
            sink.italic_();
            sink.monospaced_();
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
            if ( parser.getName().equals( SimplifiedDocbookMarkup.FIGURE_TAG.toString() ) )
            {
                sink.figureCaption_();
            }
            else if ( parser.getName().equals( SimplifiedDocbookMarkup.TABLE_TAG.toString() )
                || parser.getName().equals( SimplifiedDocbookMarkup.INFORMALTABLE_TAG.toString() ) )
            {
                sink.tableCaption_();
            }
            else if ( level == 0 )
            {
                sink.title_();
            }
            else
            {
                sink.sectionTitle_( level );
            }
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.CORPAUTHOR_TAG.toString() ) )
        {
            sink.author_();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.DATE_TAG.toString() ) )
        {
            sink.date_();
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
    }

    /** {@inheritDoc} */
    protected void handleComment( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = parser.getText();

        if ( "PB".equals( text.trim() ) )
        {
            sink.pageBreak();
        }
        else if ( "HR".equals( text.trim() ) )
        {
            sink.horizontalRule();
        }
        else if ( "LB".equals( text.trim() ) )
        {
            sink.lineBreak();
        }
        else
        {
            sink.comment( text.trim() );
        }
    }

    /** {@inheritDoc} */
    public boolean isValidate()
    {
        // TODO always false due to "Error validating the model: http://www.oasis-open.org/docbook/sgml/4.4/ent/isoamsa.ent"
        return false;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Returns the value of the given attribute.
     *
     * @param parser the parser to scan.
     * @param name the attribute name.
     * @return the attribute value.
     */
    private String getAttributeValue( XmlPullParser parser, String name )
    {
        for ( int i = 0; i < parser.getAttributeCount(); i++ )
        {
            if ( parser.getAttributeName( i ).equals( name ) )
            {
                return parser.getAttributeValue( i );
            }
        }

        return null;
    }

    private void handleCaptionStart( Sink sink )
    {
        if ( isParent( SimplifiedDocbookMarkup.FIGURE_TAG.toString() ) )
        {
            sink.figureCaption();
        }
        else if ( isParent( SimplifiedDocbookMarkup.INFORMALTABLE_TAG.toString() )
            || isParent( SimplifiedDocbookMarkup.TABLE_TAG.toString() ) )
        {
            sink.tableCaption();
        }
    }

    private void handleCaptionEnd( Sink sink )
    {
        if ( isParent( SimplifiedDocbookMarkup.FIGURE_TAG.toString() ) )
        {
            sink.figureCaption_();
        }
        else if ( isParent( SimplifiedDocbookMarkup.INFORMALTABLE_TAG.toString() )
            || isParent( SimplifiedDocbookMarkup.TABLE_TAG.toString() ) )
        {
            sink.tableCaption_();
        }
    }

    private void handleEmailStart( XmlPullParser parser, Sink sink )
            throws XmlPullParserException
    {
        String mailto;
        try
        {
            mailto = parser.nextText();
        } catch ( IOException e )
        {
            throw new XmlPullParserException( "IOException: " + e.getMessage(), parser, e );
        }
        sink.link( "mailto:" + mailto );
        sink.link_();
    }

    private void handleFigureStart( Sink sink, XmlPullParser parser )
    {
        sink.figure();
        parent.push( parser.getName() );
    }

    //If the element introduces a new level of hierarchy, raise the stack
    private void handleHierarchyElements( Sink sink )
    {
        //increase the nesting level
        level++;

        //if this is the root element, handle it as body
        if ( level == 0 )
        {
            sink.body();
        }
        else
        {
            sink.section( level, null );
        }
    }

    private void handleIdAnchor( String id, Sink sink )
    {
        //catch link targets
        if ( id != null )
        {
            sink.anchor( id );
            sink.anchor_();
        }
    }

    private void handleImageObjectStart( XmlPullParser parser, Sink sink )
    {
        String fileref = getAttributeValue( parser, "fileref" );

        if ( fileref != null )
        {
            sink.figureGraphics( fileref );
            parent.push( parser.getName() );
        }
    }

    private void handleItemizedListStart( Sink sink, XmlPullParser parser )
    {
        sink.list();
        //for itemizedlists in variablelists
        parent.push( parser.getName() );
    }

    private void handleLinkStart( XmlPullParser parser, Sink sink )
    {
        String linkend = getAttributeValue( parser,
                SimplifiedDocbookMarkup.LINKEND_ATTRIBUTE );
        if ( linkend != null )
        {
            parent.push( parser.getName() );
            sink.link( "#" + linkend );
        }
    }

    private void handleListItemStart( Sink sink )
    {
        if ( isParent( SimplifiedDocbookMarkup.VARIABLELIST_TAG.toString() ) )
        {
            sink.definition();
        }
        else if ( isParent( SimplifiedDocbookMarkup.ORDEREDLIST_TAG.toString() ) )
        {
            sink.numberedListItem();
        }
        else
        {
            sink.listItem();
        }
    }

    private void handleOrderedListStart( XmlPullParser parser, Sink sink )
    {
        //default enumeration style is decimal
        int numeration = Sink.NUMBERING_DECIMAL;

        String style = getAttributeValue( parser, SimplifiedDocbookMarkup.NUMERATION_ATTRIBUTE );

        if ( style.equals( SimplifiedDocbookMarkup.ARABIC_STYLE ) )
        {
            numeration = Sink.NUMBERING_DECIMAL;
        }
        else if ( style.equals( SimplifiedDocbookMarkup.LOWERALPHA_STYLE ) )
        {
            numeration = Sink.NUMBERING_LOWER_ALPHA;
        }
        else if ( style.equals( SimplifiedDocbookMarkup.LOWERROMAN_STYLE ) )
        {
            numeration = Sink.NUMBERING_LOWER_ROMAN;
        }
        else if ( style.equals( SimplifiedDocbookMarkup.UPPERALPHA_STYLE ) )
        {
            numeration = Sink.NUMBERING_UPPER_ALPHA;
        }
        else if ( style.equals( SimplifiedDocbookMarkup.UPPERROMAN_STYLE ) )
        {
            numeration = Sink.NUMBERING_UPPER_ROMAN;
        }

        sink.numberedList( numeration );
        parent.push( parser.getName() );
    }

    private void handleTableStart( Sink sink, XmlPullParser parser )
    {
        sink.table();
        //TODO handle tgroups
        parent.push( parser.getName() );
    }

    private void handleTitleStart( XmlPullParser parser, Sink sink )
    {
        if ( parser.getName().equals( SimplifiedDocbookMarkup.FIGURE_TAG.toString() ) )
        {
            sink.figureCaption();
        }
        else if ( parser.getName().equals( SimplifiedDocbookMarkup.TABLE_TAG.toString() )
                || parser.getName().equals( SimplifiedDocbookMarkup.INFORMALTABLE_TAG.toString() ) )
        {
            sink.tableCaption();
        }
        else if ( level == 0 )
        {
            sink.title();
        }
        else
        {
            sink.sectionTitle( level, null );
        }
    }

    private void handleUlinkStart( XmlPullParser parser, Sink sink )
    {
        String url = getAttributeValue( parser,
                SimplifiedDocbookMarkup.URL_ATTRIBUTE );
        if ( url != null )
        {
            parent.push( parser.getName() );
            sink.link( url );
        }
    }

    private void handleVariableListStart( Sink sink, XmlPullParser parser )
    {
        sink.definitionList();
        parent.push( parser.getName() );
    }

    private void handleXrefStart( XmlPullParser parser, Sink sink )
    {
        String linkend = getAttributeValue( parser,
                SimplifiedDocbookMarkup.LINKEND_ATTRIBUTE );
        if ( linkend != null )
        {
            sink.link( "#" + linkend );
            sink.text( "Link" ); //TODO: determine text of link target
            sink.link_();
        }
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
}
