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

import java.io.File;
import java.io.Writer;

import org.apache.maven.doxia.docrenderer.document.DocumentMeta;
import org.apache.maven.doxia.docrenderer.document.DocumentTOC;
import org.apache.maven.doxia.docrenderer.document.DocumentTOCItem;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.AbstractSinkTest;
import org.apache.maven.doxia.sink.SinkTestDocument;

/**
 * <code>FO Sink</code> Test case.
 */
public class FoSinkTest extends AbstractSinkTest
{
    private FoConfiguration config;

    // ----------------------------------------------------------------------
    // Specific test methods
    // ----------------------------------------------------------------------

    /**
     * Uses fop to generate a pdf from a test document.
     * @throws Exception If the conversion fails.
     */
    public void testConvertFO2PDF() throws Exception
    {
        String fileName = "test";
        // first create fo
        FoSink fosink = new FoSink( getTestWriter( fileName ) );
        SinkTestDocument.generate( fosink );
        fosink.close();

        // then generate PDF
        fo2pdf( fileName );
    }

    /**
     * Uses fop to generate an aggregated pdf from two test documents.
     * @throws Exception If the conversion fails.
     */
    public void testAggregateMode() throws Exception
    {
        FoAggregateSink fosink = new FoAggregateSink( getTestWriter( "aggregate" ) );

        fosink.beginDocument();

        fosink.coverPage( getMeta() );

        fosink.toc( getToc() );

        fosink.setDocumentName( "doc1" );
        fosink.setDocumentTitle( "Document 1" );
        SinkTestDocument.generate( fosink );

        // re-use the same source
        fosink.setDocumentName( "doc2" );
        fosink.setDocumentTitle( "Document 2" );
        SinkTestDocument.generate( fosink );

        fosink.endDocument();

        // then generate PDF
        fo2pdf( "aggregate" );
    }

    private DocumentMeta getMeta()
    {
        DocumentMeta meta = new DocumentMeta();
        meta.setAuthor( "The Apache Maven Project" );
        meta.setTitle( "Doxia FO Sink" );
        return meta;
    }

    private DocumentTOC getToc()
    {
        DocumentTOCItem item1 = new DocumentTOCItem();
        item1.setName( "First document" );
        item1.setRef( "doc1.apt" );

        DocumentTOCItem item2 = new DocumentTOCItem();
        item2.setName( "Second document" );
        item2.setRef( "doc2.xml" );

        DocumentTOC toc = new DocumentTOC();
        toc.setName( "What's in here" );
        toc.addItem( item1 );
        toc.addItem( item2 );

        return toc;
    }

    // ----------------------------------------------------------------------
    // Abstract methods the individual SinkTests must provide
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "fo";
    }

    /** {@inheritDoc} */
    protected Sink createSink( Writer writer )
    {
        return new FoSink( writer );
    }

    /** {@inheritDoc} */
    protected String getTitleBlock( String title )
    {
        String attribs = getConfig().getAttributeString( "doc.header.title" );
        return "<fo:block" + attribs + ">" + title + "</fo:block>";
    }

    /** {@inheritDoc} */
    protected String getAuthorBlock( String author )
    {
        String attribs = getConfig().getAttributeString( "doc.header.author" );
        return "<fo:block" + attribs + ">" + author + "</fo:block>";
    }

    /** {@inheritDoc} */
    protected String getDateBlock( String date )
    {
        String attribs = getConfig().getAttributeString( "doc.header.date" );
        return "<fo:block" + attribs + ">" + date + "</fo:block>";
    }

    // TODO
    protected String getHeadBlock()
    {
        return "";
    }

    // TODO: remove
    public void testHead()
    {
        String expected = "";
        assertEquals( "Wrong head!", expected, getHeadBlock() );
    }

    /** {@inheritDoc} */
    protected String getBodyBlock()
    {
        return "</fo:flow></fo:page-sequence></fo:root>";
    }

    /** {@inheritDoc} */
    protected String getSectionTitleBlock( String title )
    {
        return title;
    }

    /** {@inheritDoc} */
    protected String getSection1Block( String title )
    {
        String attribs = getConfig().getAttributeString( "body.text" );
        String attrib2 = getConfig().getAttributeString( "body.h1" );
        return "<fo:block" + attribs + "><fo:block" + attrib2 + ">1   "
            + title + "</fo:block></fo:block>";
    }

    /** {@inheritDoc} */
    protected String getSection2Block( String title )
    {
        String attribs = getConfig().getAttributeString( "body.text" );
        String attrib2 = getConfig().getAttributeString( "body.h2" );
        return "<fo:block" + attribs + "><fo:block" + attrib2 + ">0.1   "
            + title + "</fo:block></fo:block>";
    }

    /** {@inheritDoc} */
    protected String getSection3Block( String title )
    {
        String attribs = getConfig().getAttributeString( "body.text" );
        String attrib2 = getConfig().getAttributeString( "body.h3" );
        return "<fo:block" + attribs + "><fo:block" + attrib2 + ">0.0.1   "
            + title + "</fo:block></fo:block>";
    }

    /** {@inheritDoc} */
    protected String getSection4Block( String title )
    {
        String attribs = getConfig().getAttributeString( "body.text" );
        String attrib2 = getConfig().getAttributeString( "body.h4" );
        return "<fo:block" + attribs + "><fo:block" + attrib2 + ">"
            + title + "</fo:block></fo:block>";
    }

    /** {@inheritDoc} */
    protected String getSection5Block( String title )
    {
        String attribs = getConfig().getAttributeString( "body.text" );
        String attrib2 = getConfig().getAttributeString( "body.h5" );
        return "<fo:block" + attribs + "><fo:block" + attrib2 + ">"
            + title + "</fo:block></fo:block>";
    }

    /** {@inheritDoc} */
    protected String getListBlock( String item )
    {
        String attribs = getConfig().getAttributeString( "list" );
        String itemAttribs = getConfig().getAttributeString( "list.item" );
        return "<fo:list-block" + attribs + "><fo:list-item" + itemAttribs
            + "><fo:list-item-label><fo:block>&#8226;</fo:block></fo:list-item-label><fo:list-item-body"
            + itemAttribs + "><fo:block>" + item
            + "</fo:block></fo:list-item-body></fo:list-item></fo:list-block>";
    }

    /** {@inheritDoc} */
    protected String getNumberedListBlock( String item )
    {
        String attribs = getConfig().getAttributeString( "list" );
        String itemAttribs = getConfig().getAttributeString( "list.item" );
        return "<fo:list-block" + attribs + "><fo:list-item" + itemAttribs
            + "><fo:list-item-label><fo:block>i</fo:block></fo:list-item-label>"
            + "<fo:list-item-body" + itemAttribs
            + "><fo:block>" + item + "</fo:block></fo:list-item-body>"
            + "</fo:list-item></fo:list-block>";
    }

    /** {@inheritDoc} */
    protected String getDefinitionListBlock( String definum, String definition )
    {
        String dlAtts = getConfig().getAttributeString( "dl" );
        String dtAtts = getConfig().getAttributeString( "dt" );
        String ddAtts = getConfig().getAttributeString( "dd" );
        return "<fo:block" + dlAtts + "><fo:block" + dtAtts + ">" + definum
        + "</fo:block><fo:block" + ddAtts + ">" + definition
        + "</fo:block></fo:block>";
    }

    /** {@inheritDoc} */
    protected String getFigureBlock( String source, String caption )
    {
        String dlAtts = getConfig().getAttributeString( "figure.display" );
        String dtAtts = getConfig().getAttributeString( "figure.graphics" );
        String ddAtts = getConfig().getAttributeString( "figure.caption" );
        return "<fo:block" + dlAtts + "><fo:external-graphic" + dtAtts
            + " src=\"" + source + ".png" + "\"/><fo:block" + ddAtts
            + ">" + caption + "</fo:block></fo:block>";
    }

    /** {@inheritDoc} */
    protected String getTableBlock( String cell, String caption )
    {
        String dlAtts = getConfig().getAttributeString( "table.padding" );
        String dtAtts = getConfig().getAttributeString( "table.layout" );
        String ddAtts = getConfig().getAttributeString( "table.body.row" );
        String deAtts = getConfig().getAttributeString( "table.body.cell" );
        return "<fo:block" + dlAtts + "><fo:table" + dtAtts + ">"
            + "<fo:table-column column-width=\"proportional-column-width(1)\"/>"
            + "<fo:table-column column-width=\"1in\"/>"
            + "<fo:table-column column-width=\"proportional-column-width(1)\"/>"
            + "<fo:table-body><fo:table-row" + ddAtts
            + "><fo:table-cell column-number=\"2\"" + deAtts
            + "><fo:block text-align=\"center\">" + cell
            + "</fo:block></fo:table-cell></fo:table-row></fo:table-body>"
            + caption + "</fo:table></fo:block>";
    }

    /** {@inheritDoc} */
    protected String getParagraphBlock( String text )
    {
        String attribs = getConfig().getAttributeString( "normal.paragraph" );
        return "<fo:block" + attribs + ">" + text + "</fo:block>";
    }

    /** {@inheritDoc} */
    protected String getVerbatimBlock( String text )
    {
        String attribs = getConfig().getAttributeString( "body.source" );
        return "<fo:block" + attribs + ">" + text + "</fo:block>";
    }

    /** {@inheritDoc} */
    protected String getHorizontalRuleBlock()
    {
        String attribs = getConfig().getAttributeString( "body.rule" );
        return "<fo:block><fo:leader" + attribs + " /></fo:block>";
    }

    /** {@inheritDoc} */
    protected String getPageBreakBlock()
    {
        return "<fo:block break-before=\"page\" />";
    }

    /** {@inheritDoc} */
    protected String getAnchorBlock( String anchor )
    {
        // assume that anchor doesn't start with #
        return "<fo:inline id=\"#" + anchor + "\">" + anchor + "</fo:inline>";
    }

    /** {@inheritDoc} */
    protected String getLinkBlock( String link, String text )
    {
        String attribs = getConfig().getAttributeString( "href.internal" );
        // assume that link doesn't start with #
        return "<fo:basic-link internal-destination=\"#" + link + "\"><fo:inline"
            + attribs + ">" + text + "</fo:inline></fo:basic-link>";
    }

    /** {@inheritDoc} */
    protected String getItalicBlock( String text )
    {
        String attribs = getConfig().getAttributeString( "italic" );
        return "<fo:inline" + attribs + ">" + text + "</fo:inline>";
    }

    /** {@inheritDoc} */
    protected String getBoldBlock( String text )
    {
        String attribs = getConfig().getAttributeString( "bold" );
        return "<fo:inline" + attribs + ">" + text + "</fo:inline>";
    }

    /** {@inheritDoc} */
    protected String getMonospacedBlock( String text )
    {
        String attribs = getConfig().getAttributeString( "monospace" );
        return "<fo:inline" + attribs + ">" + text + "</fo:inline>";
    }

    /** {@inheritDoc} */
    protected String getLineBreakBlock()
    {
        return "<fo:block />";
    }

    /** {@inheritDoc} */
    protected String getNonBreakingSpaceBlock()
    {
        return "&#160;";
    }

    /** {@inheritDoc} */
    protected String getTextBlock( String text )
    {
        return FoSink.escaped( text, false );
    }

    /** {@inheritDoc} */
    protected String getRawTextBlock( String text )
    {
        return text;
    }

    // ----------------------------------------------------------------------
    // Auxiliary methods 
    // ----------------------------------------------------------------------


    private void fo2pdf( String baseName ) throws Exception
    {
        //File outputDirectory = new File( getBasedirFile(), getOutputDir() );
        File outputDirectory = new File( getBasedir(), outputBaseDir() + getOutputDir() );
        File resourceDirectory = new File( getBasedirFile(), "target/test-classes" );
        File foFile = new File( outputDirectory, baseName + "." + outputExtension() );
        File pdfFile = new File( outputDirectory, baseName + ".pdf" );
        FoUtils.convertFO2PDF( foFile, pdfFile, resourceDirectory.getCanonicalPath() );
    }

    private FoConfiguration getConfig()
    {
        if ( config == null )
        {
            config = ((FoSink) getSink()).getFoConfiguration();
        }

        return config;
    }

}
