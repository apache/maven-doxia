package org.apache.maven.doxia.module.fo;

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

import java.io.Writer;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.document.DocumentCover;
import org.apache.maven.doxia.document.DocumentMeta;
import org.apache.maven.doxia.document.DocumentModel;
import org.apache.maven.doxia.document.DocumentTOC;
import org.apache.maven.doxia.document.DocumentTOCItem;
import org.apache.maven.doxia.sink.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.util.DoxiaUtils;
import org.apache.maven.doxia.util.HtmlTools;

import org.codehaus.plexus.util.StringUtils;

/**
 * A Doxia Sink that produces an aggregated FO model.
 *
 * @author ltheussl
 * @version $Id$
 * @since 1.1
 */
public class FoAggregateSink extends FoSink
{
    /** The document model to be used by this sink. */
    private DocumentModel docModel;

    /** Counts the current chapter level. */
    private int chapter = 0;

    /** Name of the source file of the current document, relative to the source root. */
    private String docName;

    /** Title of the chapter, used in the page header. */
    private String docTitle = "";

    /** Content in head is ignored in aggregated documents. */
    private boolean ignoreText;

    /** Used to get the current position in the TOC. */
    private final Stack tocStack = new Stack();

    /**
     * Constructor.
     *
     * @param writer The writer for writing the result.
     */
    public FoAggregateSink( Writer writer )
    {
        super( writer );
    }

    /** {@inheritDoc} */
    public void head()
    {
        ignoreText = true;
    }

    /** {@inheritDoc} */
    public void head_()
    {
        ignoreText = false;
        writeEOL();
    }

    /** {@inheritDoc} */
    public void title()
    {
        // ignored
    }

    /** {@inheritDoc} */
    public void title_()
    {
        // ignored
    }

    /** {@inheritDoc} */
    public void author()
    {
        // ignored
    }

    /** {@inheritDoc} */
    public void author_()
    {
        // ignored
    }

    /** {@inheritDoc} */
    public void date()
    {
        // ignored
    }

    /** {@inheritDoc} */
    public void date_()
    {
        // ignored
    }

    /** {@inheritDoc} */
    public void body()
    {
        chapter++;

        resetSectionCounter();

        startPageSequence( getHeaderText(), getFooterText() );

        if ( docName == null )
        {
            getLog().warn( "No document root specified, local links will not be resolved correctly!" );
        }
        else
        {
            writeStartTag( BLOCK_TAG, "id", docName );
        }

    }

    /** {@inheritDoc} */
    public void body_()
    {
        writeEOL();
        writeEndTag( BLOCK_TAG );
        writeEndTag( FLOW_TAG );
        writeEndTag( PAGE_SEQUENCE_TAG );

        // reset document name
        docName = null;
    }

    /**
     * Sets the title of the current document. This is used as a chapter title in the page header.
     *
     * @param title the title of the current document.
     */
    public void setDocumentTitle( String title )
    {
        this.docTitle = title;

        if ( title == null )
        {
            this.docTitle = "";
        }
    }


    /**
     * Sets the name of the current source document, relative to the source root.
     * Used to resolve links to other source documents.
     *
     * @param name the name for the current document.
     */
    public void setDocumentName( String name )
    {
        this.docName = getIdName( name );
    }

    /**
     * Sets the DocumentModel to be used by this sink. The DocumentModel provides all the meta-information
     * required to render a document, eg settings for the cover page, table of contents, etc.
     *
     * @param model the DocumentModel.
     */
    public void setDocumentModel( DocumentModel model )
    {
        this.docModel = model;
    }

    /**
     * Translates the given name to a usable id.
     * Prepends "./" and strips any extension.
     *
     * @param name the name for the current document.
     * @return String
     */
    private String getIdName( String name )
    {
        if ( StringUtils.isEmpty( name ) )
        {
            getLog().warn( "Empty document reference, links will not be resolved correctly!" );
            return "";
        }

        String idName = name.replace( '\\', '/' );

        // prepend "./" and strip extension
        if ( !idName.startsWith( "./" ) )
        {
            idName = "./" + idName;
        }

        if ( idName.indexOf( ".", 2 ) != -1 )
        {
            idName = idName.substring( 0, idName.indexOf( ".", 2 ) );
        }

        return idName;
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void figureGraphics( String name )
    {
        figureGraphics( name, null );
    }

    /** {@inheritDoc} */
    public void figureGraphics( String src, SinkEventAttributes attributes )
    {
        String anchor = src;

        while ( anchor.startsWith( "./" ) )
        {
            anchor = anchor.substring( 2 );
        }

        if ( anchor.startsWith( "../" ) && docName != null )
        {
            anchor = resolveLinkRelativeToBase( anchor );
        }

        super.figureGraphics( anchor, attributes );
    }



    /** {@inheritDoc} */
    public void anchor( String name )
    {
        if ( name == null )
        {
            throw new NullPointerException( "Anchor name cannot be null!" );
        }

        String anchor = name;

        if ( !DoxiaUtils.isValidId( anchor ) )
        {
            anchor = DoxiaUtils.encodeId( name, true );

            getLog().warn( "[FO Sink] Modified invalid anchor name: " + name );
        }

        anchor = "#" + anchor;

        if ( docName != null )
        {
            anchor = docName + anchor;
        }

        writeStartTag( INLINE_TAG, "id", anchor );
    }


    /** {@inheritDoc} */
    public void link( String name )
    {
        if ( name == null )
        {
            throw new NullPointerException( "Link name cannot be null!" );
        }

        if ( DoxiaUtils.isExternalLink( name ) )
        {
            // external links
            writeStartTag( BASIC_LINK_TAG, "external-destination", HtmlTools.escapeHTML( name ) );
            writeStartTag( INLINE_TAG, "href.external" );
        }
        else if ( DoxiaUtils.isInternalLink( name ) )
        {
            // internal link (ie anchor is in the same source document)

            String anchor = name.substring( 1 );

            if ( !DoxiaUtils.isValidId( anchor ) )
            {
                anchor = DoxiaUtils.encodeId( anchor, true );

                getLog().warn( "[FO Sink] Modified invalid link name: " + name );
            }

            if ( docName != null )
            {
                anchor = docName + "#" + anchor;
            }

            writeStartTag( BASIC_LINK_TAG, "internal-destination", HtmlTools.escapeHTML( anchor ) );
            writeStartTag( INLINE_TAG, "href.internal" );
        }
        else if ( name.startsWith( "../" ) )
        {
            // local link (ie anchor is not in the same source document)

            if ( docName == null )
            {
                // can't resolve link without base, fop will issue a warning
                writeStartTag( BASIC_LINK_TAG, "internal-destination", HtmlTools.escapeHTML( name ) );
                writeStartTag( INLINE_TAG, "href.internal" );

                return;
            }

            String anchor = resolveLinkRelativeToBase( chopExtension( name ) );

            writeStartTag( BASIC_LINK_TAG, "internal-destination", HtmlTools.escapeHTML( anchor ) );
            writeStartTag( INLINE_TAG, "href.internal" );
        }
        else
        {
            // local link (ie anchor is not in the same source document)

            String anchor = name;

            if ( anchor.startsWith( "./" ) )
            {
                this.link( anchor.substring( 2 ) );
                return;
            }

            anchor = chopExtension ( anchor );

            String base = docName.substring( 0, docName.lastIndexOf( "/" ) );
            anchor = base + "/" + anchor;

            writeStartTag( BASIC_LINK_TAG, "internal-destination", HtmlTools.escapeHTML( anchor ) );
            writeStartTag( INLINE_TAG, "href.internal" );
        }
    }

    // only call this if docName != null !!!
    private String resolveLinkRelativeToBase( String name )
    {
        String anchor = name;

        String base = docName.substring( 0, docName.lastIndexOf( "/" ) );

        if ( base.indexOf( "/" ) != -1 )
        {
            while ( anchor.startsWith( "../" ) )
            {
                base = base.substring( 0, base.lastIndexOf( "/" ) );

                anchor = anchor.substring( 3 );
            }
        }

        return base + "/" + anchor;
    }

    private  String chopExtension( String name )
    {
        String anchor = name;

        int dot = anchor.lastIndexOf( "." );

        if ( dot != -1 && dot != anchor.length() && anchor.charAt( dot + 1 ) != '/' )
        {
            int hash = anchor.indexOf( "#", dot );

            if ( hash != -1 )
            {
                int dot2 = anchor.indexOf( ".", hash );

                if ( dot2 != -1 )
                {
                    anchor = anchor.substring( 0, dot ) + "#"
                        + HtmlTools.encodeId( anchor.substring( hash + 1, dot2 ) );
                }
                else
                {
                    anchor = anchor.substring( 0, dot ) + "#"
                        + HtmlTools.encodeId( anchor.substring( hash + 1, anchor.length() ) );
                }
            }
            else
            {
                anchor = anchor.substring( 0, dot );
            }
        }

        return anchor;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * Writes a start tag, prepending EOL.
     */
    protected void writeStartTag( Tag tag, String attributeId )
    {
        if ( !ignoreText )
        {
            super.writeStartTag( tag, attributeId );
        }
    }

    /**
     * {@inheritDoc}
     *
     * Writes a start tag, prepending EOL.
     */
    protected void writeStartTag( Tag tag, String id, String name )
    {
        if ( !ignoreText )
        {
            super.writeStartTag( tag, id, name );
        }
    }

    /**
     * {@inheritDoc}
     *
     * Writes an end tag, appending EOL.
     */
    protected void writeEndTag( Tag t )
    {
        if ( !ignoreText )
        {
            super.writeEndTag( t );
        }
    }

    /**
     * {@inheritDoc}
     *
     * Writes a simple tag, appending EOL.
     */
    protected void writeEmptyTag( Tag tag, String attributeId )
    {
        if ( !ignoreText )
        {
            super.writeEmptyTag( tag, attributeId );
        }
    }

    /**
     * {@inheritDoc}
     *
     * Writes a text, swallowing any exceptions.
     */
    protected void write( String text )
    {
        if ( !ignoreText )
        {
            super.write( text );
        }
    }

    /**
     * {@inheritDoc}
     *
     * Writes a text, appending EOL.
     */
    protected void writeln( String text )
    {
        if ( !ignoreText )
        {
            super.writeln( text );
        }
    }

    /**
     * {@inheritDoc}
     *
     * Writes content, escaping special characters.
     */
    protected void content( String text )
    {
        if ( !ignoreText )
        {
            super.content( text );
        }
    }

    /**
     * Writes EOL.
     */
    protected void newline()
    {
        if ( !ignoreText )
        {
            writeEOL();
        }
    }

    /**
     * Starts a page sequence, depending on the current chapter.
     *
     * @param headerText The text to write in the header, if null, nothing is written.
     * @param footerText The text to write in the footer, if null, nothing is written.
     */
    protected void startPageSequence( String headerText, String footerText )
    {
        if ( chapter == 1 )
        {
            startPageSequence( "0", headerText, footerText );
        }
        else
        {
            startPageSequence( "auto", headerText, footerText );
        }
    }

    /**
     * Returns the text to write in the header of each page.
     *
     * @return String
     */
    protected String getHeaderText()
    {
        return Integer.toString( chapter ) + "   " + docTitle;
    }

    /**
     * Returns the text to write in the footer of each page.
     *
     * @return String
     */
    protected String getFooterText()
    {
        int actualYear;
        String add = " &#8226; ALL RIGHTS RESERVED.";
        String companyName = "";

        if ( docModel != null && docModel.getMeta() != null && docModel.getMeta().isConfidential() )
        {
            add = add + " &#8226; PROPRIETARY AND CONFIDENTIAL";
        }

        if ( docModel != null && docModel.getCover() != null && docModel.getCover().getCompanyName() != null )
        {
            companyName = docModel.getCover().getCompanyName();
        }

        if ( docModel != null && docModel.getMeta() != null && docModel.getMeta().getDate() != null )
        {
            Calendar date = Calendar.getInstance();
            date.setTime( docModel.getMeta().getDate() );
            actualYear = date.get( Calendar.YEAR );
        }
        else
        {
            actualYear = Calendar.getInstance().get( Calendar.YEAR );
        }

        return "&#169;" + actualYear + ", " + companyName + add;
    }

    /**
     * {@inheritDoc}
     *
     * Returns the current chapter number as a string.
     */
    protected String getChapterString()
    {
        return Integer.toString( chapter ) + ".";
    }

    /**
     * {@inheritDoc}
     *
     * Writes a 'xsl-region-before' block.
     */
    protected void regionBefore( String headerText )
    {
        writeStartTag( STATIC_CONTENT_TAG, "flow-name", "xsl-region-before" );
        writeln( "<fo:table table-layout=\"fixed\" width=\"100%\" >" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", "5.625in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", "0.625in" );
        writeStartTag( TABLE_BODY_TAG, "" );
        writeStartTag( TABLE_ROW_TAG, "" );
        writeStartTag( TABLE_CELL_TAG, "" );
        writeStartTag( BLOCK_TAG, "header.style" );

        if ( headerText != null )
        {
            write( headerText );
        }

        writeEndTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        writeStartTag( TABLE_CELL_TAG, "" );
        writeStartTag( BLOCK_TAG, "page.number" );
        writeEmptyTag( PAGE_NUMBER_TAG, "" );
        writeEndTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );
        writeEndTag( TABLE_BODY_TAG );
        writeEndTag( TABLE_TAG );
        writeEndTag( STATIC_CONTENT_TAG );
    }

    /**
     * {@inheritDoc}
     *
     * Writes a 'xsl-region-after' block.
     */
    protected void regionAfter( String footerText )
    {
        writeStartTag( STATIC_CONTENT_TAG, "flow-name", "xsl-region-after" );
        writeStartTag( BLOCK_TAG, "footer.style" );

        if ( footerText != null )
        {
            write( footerText );
        }

        writeEndTag( BLOCK_TAG );
        writeEndTag( STATIC_CONTENT_TAG );
    }

    /**
     * {@inheritDoc}
     *
     * Writes a chapter heading.
     */
    protected void chapterHeading( String headerText, boolean chapterNumber )
    {
        writeStartTag( BLOCK_TAG, "" );
        writeStartTag( LIST_BLOCK_TAG, "" );
        writeStartTag( LIST_ITEM_TAG, "" );
        writeln( "<fo:list-item-label end-indent=\"6.375in\" start-indent=\"-1in\">" );
        writeStartTag( BLOCK_TAG, "outdented.number.style" );

        if ( chapterNumber )
        {
            write( Integer.toString( chapter ) );
        }

        writeEndTag( BLOCK_TAG );
        writeEndTag( LIST_ITEM_LABEL_TAG );
        writeln( "<fo:list-item-body end-indent=\"1in\" start-indent=\"0in\">" );
        writeStartTag( BLOCK_TAG, "chapter.title" );

        if ( headerText == null )
        {
            write( docTitle );
        }
        else
        {
            write( headerText );
        }

        writeEndTag( BLOCK_TAG );
        writeEndTag( LIST_ITEM_BODY_TAG );
        writeEndTag( LIST_ITEM_TAG );
        writeEndTag( LIST_BLOCK_TAG );
        writeEndTag( BLOCK_TAG );
        writeStartTag( BLOCK_TAG, "space-after.optimum", "0em" );
        writeEmptyTag( LEADER_TAG, "chapter.rule" );
        writeEndTag( BLOCK_TAG );
    }

    /**
     * Writes a table of contents. The DocumentModel has to contain a DocumentTOC for this to work.
     */
    public void toc()
    {
        if ( docModel == null || docModel.getToc() == null || docModel.getToc().getItems() == null )
        {
            return;
        }

        DocumentTOC toc = docModel.getToc();

        writeln( "<fo:page-sequence master-reference=\"toc\" initial-page-number=\"1\" format=\"i\">" );
        regionBefore( toc.getName() );
        regionAfter( getFooterText() );
        writeStartTag( FLOW_TAG, "flow-name", "xsl-region-body" );
        chapterHeading( toc.getName(), false );
        writeln( "<fo:table table-layout=\"fixed\" width=\"100%\" >" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", "0.45in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", "0.4in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", "0.4in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", "5in" ); // TODO {$maxBodyWidth - 1.25}in
        writeStartTag( TABLE_BODY_TAG, "" );

        writeTocItems( toc.getItems(), 1 );

        writeEndTag( TABLE_BODY_TAG );
        writeEndTag( TABLE_TAG );
        writeEndTag( FLOW_TAG );
        writeEndTag( PAGE_SEQUENCE_TAG );
    }

    private void writeTocItems( List tocItems, int level )
    {
        final int maxTocLevel = 4;

        if ( level < 1 || level > maxTocLevel )
        {
            return;
        }

        tocStack.push( new NumberedListItem( NUMBERING_DECIMAL ) );

        for ( Iterator k = tocItems.iterator(); k.hasNext(); )
        {
            DocumentTOCItem tocItem = (DocumentTOCItem) k.next();

            String ref = getIdName( tocItem.getRef() );

            writeStartTag( TABLE_ROW_TAG, "keep-with-next", "auto" );

            if ( level > 2 )
            {
                for ( int i = 0; i < level - 2; i++ )
                {
                    writeStartTag( TABLE_CELL_TAG );
                    writeEmptyTag( BLOCK_TAG, "" );
                    writeEndTag( TABLE_CELL_TAG );
                }
            }

            writeStartTag( TABLE_CELL_TAG, "toc.cell" );
            writeStartTag( BLOCK_TAG, "toc.number.style" );

            NumberedListItem current = (NumberedListItem) tocStack.peek();
            current.next();
            write( currentTocNumber() );

            writeEndTag( BLOCK_TAG );
            writeEndTag( TABLE_CELL_TAG );

            String span = "3";

            if ( level > 2 )
            {
                span = Integer.toString( 5 - level );
            }

            writeStartTag( TABLE_CELL_TAG, "number-columns-spanned", span, "toc.cell" );
            MutableAttributeSet atts = getFoConfiguration().getAttributeSet( "toc.h" + level + ".style" );
            atts.addAttribute( "text-align-last", "justify" );
            writeStartTag( BLOCK_TAG, atts );
            writeStartTag( BASIC_LINK_TAG, "internal-destination", ref );
            write( tocItem.getName() );
            writeEndTag( BASIC_LINK_TAG );
            writeEmptyTag( LEADER_TAG, "toc.leader.style" );
            writeStartTag( INLINE_TAG, "page.number" );
            writeEmptyTag( PAGE_NUMBER_CITATION_TAG, "ref-id", ref );
            writeEndTag( INLINE_TAG );
            writeEndTag( BLOCK_TAG );
            writeEndTag( TABLE_CELL_TAG );
            writeEndTag( TABLE_ROW_TAG );

            if ( tocItem.getItems() != null )
            {
                writeTocItems( tocItem.getItems(), level + 1 );
            }
        }

        tocStack.pop();
    }

    private String currentTocNumber()
    {
        String ch = ( (NumberedListItem) tocStack.get( 0 ) ).getListItemSymbol();

        for ( int i = 1; i < tocStack.size(); i++ )
        {
            ch = ch + "." + ( (NumberedListItem) tocStack.get( i ) ).getListItemSymbol();
        }

        return ch;
    }

    /**
     * {@inheritDoc}
     *
     * Writes a fo:bookmark-tree. The DocumentModel has to contain a DocumentTOC for this to work.
     */
    protected void pdfBookmarks()
    {
        if ( docModel == null || docModel.getToc() == null )
        {
            return;
        }

        writeStartTag( BOOKMARK_TREE_TAG, "" );

        renderBookmarkItems( docModel.getToc().getItems() );

        writeEndTag( BOOKMARK_TREE_TAG );
    }

    private void renderBookmarkItems( List items )
    {
        for ( Iterator k = items.iterator(); k.hasNext(); )
        {
            DocumentTOCItem tocItem = (DocumentTOCItem) k.next();

            String ref = getIdName( tocItem.getRef() );

            writeStartTag( BOOKMARK_TAG, "internal-destination", ref );
            writeStartTag( BOOKMARK_TITLE_TAG, "" );
            write( tocItem.getName() );
            writeEndTag( BOOKMARK_TITLE_TAG );

            if ( tocItem.getItems() != null )
            {
                renderBookmarkItems( tocItem.getItems() );
            }

            writeEndTag( BOOKMARK_TAG );
        }
    }

    /**
     * Writes a cover page. The DocumentModel has to contain a DocumentMeta for this to work.
     */
    public void coverPage()
    {
        if ( this.docModel == null )
        {
            return;
        }

        DocumentCover cover = docModel.getCover();
        DocumentMeta meta = docModel.getMeta();

        if ( cover == null && meta == null )
        {
            return; // no information for cover page: ignore
        }

        String title = null;
        String subtitle = null;
        String version = null;
        String type = null;
        String date = null;
        // TODO: implement
        //String author = null;
        //String projName = null;
        String projLogo = null;
        String compName = null;
        String compLogo = null;

        if ( cover == null )
        {
            // aleady checked that meta != null
            title = meta.getTitle();
            compName = meta.getAuthor();
        }
        else
        {
            title = cover.getCoverTitle();
            subtitle = cover.getCoverSubTitle();
            version = cover.getCoverVersion();
            type = cover.getCoverType();
            date = cover.getCoverDate();
            //author = cover.getAuthor();
            //projName = cover.getProjectName();
            projLogo = cover.getProjectLogo();
            compName = cover.getCompanyName();
            compLogo = cover.getCompanyLogo();
        }

        // TODO: remove hard-coded settings

        writeStartTag( PAGE_SEQUENCE_TAG, "master-reference", "cover-page" );
        writeStartTag( FLOW_TAG, "flow-name", "xsl-region-body" );
        writeStartTag( BLOCK_TAG, "text-align", "center" );
        writeln( "<fo:table table-layout=\"fixed\" width=\"100%\" >" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", "3.125in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", "3.125in" );
        writeStartTag( TABLE_BODY_TAG, "" );

        writeCoverHead( compLogo, projLogo );
        writeCoverBody( title, version, subtitle, type );
        writeCoverFooter( compName, date );

        writeEndTag( TABLE_BODY_TAG );
        writeEndTag( TABLE_TAG );
        writeEndTag( BLOCK_TAG );
        writeEndTag( FLOW_TAG );
        writeEndTag( PAGE_SEQUENCE_TAG );
    }

    private void writeCoverBody( String title, String version, String subtitle, String type )
    {
        writeln( "<fo:table-row keep-with-previous=\"always\" height=\"0.014in\">" );
        writeStartTag( TABLE_CELL_TAG, "number-columns-spanned", "2" );
        writeStartTag( BLOCK_TAG, "line-height", "0.014in" );
        writeEmptyTag( LEADER_TAG, "chapter.rule" );
        writeEndTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );

        writeStartTag( TABLE_ROW_TAG, "height", "7.447in" );
        writeStartTag( TABLE_CELL_TAG, "number-columns-spanned", "2" );
        writeln( "<fo:table table-layout=\"fixed\" width=\"100%\" >" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", "2.083in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", "2.083in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", "2.083in" );

        writeStartTag( TABLE_BODY_TAG, "" );

        writeStartTag( TABLE_ROW_TAG, "" );
        writeStartTag( TABLE_CELL_TAG, "number-columns-spanned", "3" );
        writeEmptyTag( BLOCK_TAG, "" );
        writeEmptyTag( BLOCK_TAG, "space-before", "3.2235in" );
        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );

        writeStartTag( TABLE_ROW_TAG, "" );
        writeStartTag( TABLE_CELL_TAG, "" );
        writeEmptyTag( BLOCK_TAG, "space-after", "0.5in" );
        writeEndTag( TABLE_CELL_TAG );

        writeStartTag( TABLE_CELL_TAG, "number-columns-spanned", "2", "cover.border.left" );
        writeStartTag( BLOCK_TAG, "cover.title" );
        write( title == null ? "" : title );
        write( version == null ? "" : " v. " + version );
        writeEndTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );

        writeStartTag( TABLE_ROW_TAG, "" );
        writeStartTag( TABLE_CELL_TAG, "" );
        writeEmptyTag( BLOCK_TAG, "" );
        writeEndTag( TABLE_CELL_TAG );


        writeStartTag( TABLE_CELL_TAG, "number-columns-spanned", "2", "cover.border.left.bottom" );
        writeStartTag( BLOCK_TAG, "cover.subtitle" );
        write( subtitle == null ? "" : subtitle );
        writeEndTag( BLOCK_TAG );
        writeStartTag( BLOCK_TAG, "cover.subtitle" );
        write( type == null ? "" : type );
        writeEndTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );

        writeEndTag( TABLE_BODY_TAG );
        writeEndTag( TABLE_TAG );

        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );

        writeStartTag( TABLE_ROW_TAG, "height", "0.014in" );
        writeStartTag( TABLE_CELL_TAG, "number-columns-spanned", "2" );
        writeln( "<fo:block space-after=\"0.2in\" line-height=\"0.014in\">" );
        writeEmptyTag( LEADER_TAG, "chapter.rule" );
        writeEndTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );

        writeStartTag( TABLE_ROW_TAG, "" );
        writeStartTag( TABLE_CELL_TAG, "number-columns-spanned", "2" );
        writeEmptyTag( BLOCK_TAG, "" );
        writeEmptyTag( BLOCK_TAG, "space-before", "0.2in" );
        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );
    }

    private void writeCoverFooter( String compName, String date )
    {
        writeStartTag( TABLE_ROW_TAG, "height", "0.3in" );

        writeStartTag( TABLE_CELL_TAG, "" );
        MutableAttributeSet att = getFoConfiguration().getAttributeSet( "cover.subtitle" );
        att.addAttribute( "height", "0.3in" );
        att.addAttribute( "text-align", "left" );
        writeStartTag( BLOCK_TAG, att );
        write( compName == null ? "" : compName );
        writeEndTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );

        writeStartTag( TABLE_CELL_TAG, "" );
        att = getFoConfiguration().getAttributeSet( "cover.subtitle" );
        att.addAttribute( "height", "0.3in" );
        att.addAttribute( "text-align", "right" );
        writeStartTag( BLOCK_TAG, att );
        write( date == null ? Calendar.getInstance().get( Calendar.YEAR ) + "" : date );
        writeEndTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );

        writeEndTag( TABLE_ROW_TAG );
    }

    private void writeCoverHead( String compLogo, String projLogo )
    {
        writeStartTag( TABLE_ROW_TAG, "height", "1.5in" );
        writeStartTag( TABLE_CELL_TAG, "" );

        if ( compLogo != null )
        {
            SinkEventAttributeSet atts = new SinkEventAttributeSet();
            atts.addAttribute( "text-align", "left" );
            atts.addAttribute( "vertical-align", "top" );
            writeStartTag( BLOCK_TAG, atts );
            atts = new SinkEventAttributeSet();
            atts.addAttribute( SinkEventAttributes.HEIGHT, "1.5in" );
            figureGraphics( compLogo, atts );
            writeEndTag( BLOCK_TAG );
        }

        writeEmptyTag( BLOCK_TAG, "" );
        writeEndTag( TABLE_CELL_TAG );
        writeStartTag( TABLE_CELL_TAG, "" );

        if ( projLogo != null )
        {
            SinkEventAttributeSet atts = new SinkEventAttributeSet();
            atts.addAttribute( "text-align", "right" );
            atts.addAttribute( "vertical-align", "top" );
            writeStartTag( BLOCK_TAG, atts );
            atts = new SinkEventAttributeSet();
            atts.addAttribute( SinkEventAttributes.HEIGHT, "1.5in" );
            figureGraphics( projLogo, atts );
            writeEndTag( BLOCK_TAG );
        }

        writeEmptyTag( BLOCK_TAG, "" );
        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );
    }

}
