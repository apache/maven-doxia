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

import java.util.Iterator;

import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.docrenderer.document.DocumentMeta;
import org.apache.maven.doxia.docrenderer.document.DocumentTOC;
import org.apache.maven.doxia.docrenderer.document.DocumentTOCItem;
import org.apache.maven.doxia.util.HtmlTools;

/**
 * A Doxia Sink that produces an aggregated FO model.
 *
 * @author ltheussl
 * @version $Id$
 * @since 1.0
 */
public class FoAggregateSink extends FoSink
{

    /** Counts the current chapter level. */
    private int chapter = 0;

    /** Name of the source file of the current document, relative to the source root. */
    private String docName;

    /** Title of the chapter, used in the page header. */
    private String docTitle = "";

    /** Content in head is ignored in aggregated documents. */
    private boolean ignoreText;

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
        newline();
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
            // TODO: log.warn( "No document root specified, local links will not be resolved correctly!" )
        }
        else
        {
            writeStartTag( BLOCK_TAG, "id", docName );
        }

    }

    /** {@inheritDoc} */
    public void body_()
    {
        newline();
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
     * Translates the given name to a usable id.
     * Prepends "./" and strips any extension.
     *
     * @param name the name for the current document.
     * @return String
     */
    private String getIdName( String name )
    {
        String idName = name;

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
    public void anchor( String name )
    {
        String anchor = name;

        if ( anchor.startsWith( "#" ) )
        {
            anchor = "#" + HtmlTools.encodeId( anchor.substring( 1 ) );
        }
        else
        {
            anchor = "#" + HtmlTools.encodeId( anchor );
        }

        if ( docName != null )
        {
            anchor = docName + anchor;
        }

        writeStartTag( INLINE_TAG, "id", anchor );
    }


    /** {@inheritDoc} */
    public void link( String name )
    {
        if ( name.startsWith( "http" ) || name.startsWith( "mailto" )
            || name.startsWith( "ftp" ) )
        {
            // external links
            writeStartTag( BASIC_LINK_TAG, "external-destination", HtmlTools.escapeHTML( name ) );
            writeStartTag( INLINE_TAG, "href.external" );
        }
        else if ( name.startsWith( "./" ) )
        {
            // internal, non-local link (ie anchor is not in the same source document)
            // and link destination source document is in the same directory

            String anchor = name;

            int dot = anchor.indexOf( ".", 2 );

            if ( dot != -1 )
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

            writeStartTag( BASIC_LINK_TAG, "internal-destination", HtmlTools.escapeHTML( anchor ) );
            writeStartTag( INLINE_TAG, "href.internal" );
        }
        else if ( name.startsWith( "../" ) )
        {
            // internal, non-local link (ie anchor is not in the same source document)
            // and link destination source document is in a different directory

            String anchor = name;

            if ( docName == null )
            {
                // can't resolve link without base, fop will issue a warning
                writeStartTag( BASIC_LINK_TAG, "internal-destination", HtmlTools.escapeHTML( anchor ) );
                writeStartTag( INLINE_TAG, "href.internal" );

                return;
            }

            String base = docName.substring( 0, docName.lastIndexOf( "/" ) );

            while ( anchor.startsWith( "../" ) )
            {
                base = base.substring( 0, base.lastIndexOf( "/" ) );

                anchor = anchor.substring( 3 );
            }

            // call again with resolved link
            link( base + "/" + anchor );
        }
        else
        {
            // internal, local link (ie anchor is in the same source document)

            String anchor = name;

            if ( anchor.startsWith( "#" ) )
            {
                anchor = "#" + HtmlTools.encodeId( anchor.substring( 1 ) );
            }
            else
            {
                anchor = "#" + HtmlTools.encodeId( anchor );
            }

            if ( docName != null )
            {
                anchor = docName + anchor;
            }

            writeStartTag( BASIC_LINK_TAG, "internal-destination", HtmlTools.escapeHTML( anchor ) );
            writeStartTag( INLINE_TAG, "href.internal" );
        }
    }


    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Writes a start tag, prepending EOL.
     *
     * @param tag The tag.
     * @param attributeId An id identifying the attribute set.
     */
    protected void writeStartTag( Tag tag, String attributeId )
    {
        if ( !ignoreText )
        {
            super.writeStartTag( tag, attributeId );
        }
    }

    /**
     * Writes a start tag, prepending EOL.
     *
     * @param tag The tag.
     * @param id An id to add.
     * @param name The name (value) of the id.
     */
    protected void writeStartTag( Tag tag, String id, String name )
    {
        if ( !ignoreText )
        {
            super.writeStartTag( tag, id, name );
        }
    }

    /**
     * Writes an end tag, appending EOL.
     *
     * @param tag The tag.
     */
    protected void writeEndTag( Tag tag )
    {
        if ( !ignoreText )
        {
            super.writeEndTag( tag );
        }
    }

    /**
     * Writes a simple tag, appending EOL.
     *
     * @param tag The tag.
     * @param attributeId An id identifying the attribute set.
     */
    protected void writeEmptyTag( Tag tag, String attributeId )
    {
        if ( !ignoreText )
        {
            super.writeEmptyTag( tag, attributeId );
        }
    }

    /**
     * Writes a text, swallowing any exceptions.
     *
     * @param text The text to write.
     */
    protected void write( String text )
    {
        if ( !ignoreText )
        {
            super.write( text );
        }
    }

    /**
     * Writes a text, appending EOL.
     *
     * @param text The text to write.
     */
    protected void writeln( String text )
    {
        if ( !ignoreText )
        {
            super.writeln( text );
        }
    }

    /**
     * Writes content, escaping special characters.
     *
     * @param text The text to write.
     */
    protected void content( String text )
    {
        if ( !ignoreText )
        {
            super.content( text );
        }
    }

    /** Writes EOL. */
    protected void newline()
    {
        if ( !ignoreText )
        {
            super.newline();
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
        // TODO: year and company have to come from DocumentMeta
        return "&#169;2007 The Apache Software Foundation &#8226; ALL RIGHTS RESERVED";
    }

    /**
     * Returns the current chapter number as a string.
     *
     * @return String
     */
    protected String getChapterString()
    {
        return Integer.toString( chapter ) + ".";
    }

    /**
     * Writes a 'xsl-region-before' block.
     *
     * @param headerText The text to write in the header, if null, nothing is written.
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
     * Writes a 'xsl-region-after' block.
     *
     * @param footerText The text to write in the footer, if null, nothing is written.
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
     * Writes a chapter heading.
     *
     * @param headerText The text to write in the header, if null, the current document title is written.
     * @param chapterNumber True if the chapter number should be written in front of the text.
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
     * Writes a table of contents.
     *
     * @param toc The DocumentTOC object that contains all information for the table of contents.
     */
    public void toc( DocumentTOC toc )
    {
        writeln( "<fo:page-sequence master-reference=\"toc\" initial-page-number=\"1\" format=\"i\">" );
        regionBefore( toc.getName() );
        regionAfter( getFooterText() );
        writeStartTag( FLOW_TAG, "flow-name", "xsl-region-body" );
        chapterHeading( toc.getName(), false );
        writeln( "<fo:table table-layout=\"fixed\" width=\"100%\" >" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", "0.45in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", "0.4in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", "0.4in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", "5in" ); // TODO
        writeStartTag( TABLE_BODY_TAG, "" );

        int count = 0;

        for ( Iterator k = toc.getItems().iterator(); k.hasNext(); )
        {
            DocumentTOCItem tocItem = (DocumentTOCItem) k.next();
            count++;

            String ref = getIdName( tocItem.getRef() );

            writeStartTag( TABLE_ROW_TAG, "keep-with-next", "always" );
            writeStartTag( TABLE_CELL_TAG, "toc.cell" );
            writeStartTag( BLOCK_TAG, "toc.number.style" );
            write( Integer.toString( count ) );
            writeEndTag( BLOCK_TAG );
            writeEndTag( TABLE_CELL_TAG );
            writeStartTag( TABLE_CELL_TAG, "number-columns-spanned", "3", "toc.cell" );
            // TODO: writeStartTag( BLOCK_TAG, "text-align-last", "justify", "toc.h1.style" );
            writeStartTag( BLOCK_TAG, "toc.h1.style" );
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
        }

        writeEndTag( TABLE_BODY_TAG );
        writeEndTag( TABLE_TAG );
        writeEndTag( FLOW_TAG );
        writeEndTag( PAGE_SEQUENCE_TAG );


    }

    /**
     * Writes a cover page.
     *
     * @param meta The DocumentMeta object that contains all information for the cover page.
     */
    public void coverPage( DocumentMeta meta )
    {
        String title = meta.getTitle();
        String author = meta.getAuthor();

        // TODO: remove hard-coded settings

        writeStartTag( PAGE_SEQUENCE_TAG, "master-reference", "cover-page" );
        writeStartTag( FLOW_TAG, "flow-name", "xsl-region-body" );
        writeStartTag( BLOCK_TAG, "text-align", "center" );
        //writeStartTag( TABLE_TAG, "table-layout", "fixed" );
        writeln( "<fo:table table-layout=\"fixed\" width=\"100%\" >" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", "3.125in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", "3.125in" );
        writeStartTag( TABLE_BODY_TAG, "" );

        writeStartTag( TABLE_ROW_TAG, "height", "1.5in" );
        writeStartTag( TABLE_CELL_TAG, "" );
        // TODO: companyLogo
        writeEmptyTag( BLOCK_TAG, "" );
        writeEndTag( TABLE_CELL_TAG );
        writeStartTag( TABLE_CELL_TAG, "" );
        // TODO: projectLogo
        writeEmptyTag( BLOCK_TAG, "" );
        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );

        writeln( "<fo:table-row keep-with-previous=\"always\" height=\"0.014in\">" );
        writeStartTag( TABLE_CELL_TAG, "number-columns-spanned", "2" );
        writeStartTag( BLOCK_TAG, "line-height", "0.014in" );
        writeEmptyTag( LEADER_TAG, "chapter.rule" );
        writeEndTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );

        writeStartTag( TABLE_ROW_TAG, "height", "7.447in" );
        writeStartTag( TABLE_CELL_TAG, "number-columns-spanned", "2" );
        //writeStartTag( TABLE_TAG, "table-layout", "fixed" );
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
        write( title );
        // TODO: version
        writeEndTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );

        writeStartTag( TABLE_ROW_TAG, "" );
        writeStartTag( TABLE_CELL_TAG, "" );
        writeEmptyTag( BLOCK_TAG, "" );
        writeEndTag( TABLE_CELL_TAG );


        writeStartTag( TABLE_CELL_TAG, "number-columns-spanned", "2", "cover.border.left.bottom" );
        writeStartTag( BLOCK_TAG, "cover.subtitle" );
        // TODO: sub title (cover type)
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

        writeStartTag( TABLE_ROW_TAG, "height", "0.3in" );
        writeStartTag( TABLE_CELL_TAG, "" );
        writeStartTag( BLOCK_TAG, "height", "0.3in", "cover.subtitle" );
        write( author );
        writeEndTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );

        writeStartTag( TABLE_CELL_TAG, "" );
        writeStartTag( BLOCK_TAG, "height", "0.3in", "cover.subtitle" );
        // TODO: date
        writeEndTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );

        writeEndTag( TABLE_ROW_TAG );
        writeEndTag( TABLE_BODY_TAG );
        writeEndTag( TABLE_TAG );
        writeEndTag( BLOCK_TAG );
        writeEndTag( FLOW_TAG );
        writeEndTag( PAGE_SEQUENCE_TAG );
    }

}
