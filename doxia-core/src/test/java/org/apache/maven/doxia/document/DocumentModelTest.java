package org.apache.maven.doxia.document;

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
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import org.apache.maven.doxia.document.io.xpp3.DocumentXpp3Reader;
import org.apache.maven.doxia.document.io.xpp3.DocumentXpp3Writer;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.WriterFactory;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Test DocumentModel.
 *
 * @author ltheussl
 */
public class DocumentModelTest
    extends PlexusTestCase
{
    /** ISO 8601 date format, i.e. <code>yyyy-MM-dd</code> **/
    private static final java.text.DateFormat ISO_8601_FORMAT =
            new java.text.SimpleDateFormat( "yyyy-MM-dd", java.util.Locale.ENGLISH );

    /**
     * Test DocumentModel.
     *
     * @throws Exception if any.
     */
    public void testDocumentModel()
            throws Exception
    {
        DocumentModel model = getModel();
        verifyModel( model );

        DocumentModel copy = writeAndRecover( model );
        verifyModel( copy );
        assertTrue( copy.equals( model ) );
    }

    private DocumentModel getModel()
    {
        DocumentModel model = new DocumentModel();

        model.setOutputName( "outputName" );
        model.setModelEncoding( "ISO-8859-1" );
        model.setCover( getDocumentCover() );
        model.setToc( getDocumentToc() );
        model.setMeta( getDocumentMeta() );

        return model ;
    }

    private void verifyModel( DocumentModel model )
    {
        assertNotNull( model );
        assertTrue( model.equals( model ) );
        assertTrue ( model.hashCode() != 0 );
        assertTrue( model.toString().length() > 0 );

        assertEquals( "outputName", model.getOutputName() );
        assertEquals( "ISO-8859-1", model.getModelEncoding() );
        verifyDocumentCover( model.getCover() );
        verifyDocumentTOC( model.getToc() );
        verifyDocumentMeta( model.getMeta() );
    }

    private DocumentAuthor getAuthor( int i )
    {
        DocumentAuthor author = new DocumentAuthor();

        author.setCity( "city" + i );
        author.setCompanyName( "companyName" + i );
        author.setCountry( "country" + i );
        author.setEmail( "email" + i );
        author.setFaxNumber( "faxNumber" + i );
        author.setName( "name" + i );
        author.setFirstName( "firstName" + i );
        author.setInitials( "initials" + i );
        author.setLastName( "lastName" + i );
        author.setPhoneNumber( "phoneNumber" + i );
        author.setPosition( "position" + i );
        author.setPostalCode( "postalCode" + i );
        author.setState( "state" + i );
        author.setStreet( "street" + i );
        author.setTitle( "title" + i );

        return author;
    }

    private void verifyAuthor( DocumentAuthor documentAuthor, int i )
    {
        assertEquals( "city" + i, documentAuthor.getCity() );
        assertEquals( "companyName" + i, documentAuthor.getCompanyName() );
        assertEquals( "country" + i, documentAuthor.getCountry() );
        assertEquals( "email" + i, documentAuthor.getEmail() );
        assertEquals( "faxNumber" + i, documentAuthor.getFaxNumber() );
        assertEquals( "name" + i, documentAuthor.getName() );
        assertEquals( "firstName" + i, documentAuthor.getFirstName() );
        assertEquals( "initials" + i, documentAuthor.getInitials() );
        assertEquals( "lastName" + i, documentAuthor.getLastName() );
        assertEquals( "phoneNumber" + i, documentAuthor.getPhoneNumber() );
        assertEquals( "position" + i, documentAuthor.getPosition() );
        assertEquals( "postalCode" + i, documentAuthor.getPostalCode() );
        assertEquals( "state" + i, documentAuthor.getState() );
        assertEquals( "street" + i, documentAuthor.getStreet() );
        assertEquals( "title" + i, documentAuthor.getTitle() );
    }

    private DocumentCover getDocumentCover()
    {
        DocumentCover cover = new DocumentCover();
        cover.addAuthor( getAuthor( 1 ) );
        cover.setAuthor( "Author" );
        cover.setCompanyLogo( "companyLogo" );
        cover.setCompanyName( "companyName" );
        cover.setCoverDate( new Date( 0L ) );
        cover.setCoverSubTitle( "coverSubTitle" );
        cover.setCoverTitle( "coverTitle" );
        cover.setCoverType( "coverType" );
        cover.setCoverVersion( "coverVersion" );
        cover.setProjectLogo( "projectLogo" );
        cover.setProjectName( "projectName" );

        return cover;
    }

    private void verifyDocumentCover( DocumentCover cover )
    {
        List<DocumentAuthor> authors = cover.getAuthors();
        assertEquals( 1, authors.size() );
        verifyAuthor( authors.get( 0 ), 1 );

        assertEquals( "Author", cover.getAuthor() );
        assertEquals( "companyLogo", cover.getCompanyLogo() );
        assertEquals( "companyName", cover.getCompanyName() );
        assertEquals( 0L, cover.getCoverDate().getTime() );
        // the actual String depends on the timezone you're in
        assertEquals( ISO_8601_FORMAT.format( new Date( 0L ) ), cover.getCoverdate() );
        assertEquals( "coverSubTitle", cover.getCoverSubTitle() );
        assertEquals( "coverTitle", cover.getCoverTitle() );
        assertEquals( "coverType", cover.getCoverType() );
        assertEquals( "coverVersion", cover.getCoverVersion() );
        assertEquals( "projectLogo", cover.getProjectLogo() );
        assertEquals( "projectName", cover.getProjectName() );
    }

    private DocumentStatistic getDocumentStatistic()
    {
        DocumentStatistic statistic = new DocumentStatistic();

        statistic.setCharacterCount( 2L );
        statistic.setDrawCount( 3L );
        statistic.setFrameCount( 4L );
        statistic.setImageCount( 5L );
        statistic.setNonWhitespaceCharacterCount( 6L );
        statistic.setObjectCount( 1L );
        statistic.setOleObjectCount( 8L );
        statistic.setPageCount( 7L );
        statistic.setParagraphCount( 5L );
        statistic.setRowCount( 6L );
        statistic.setSentenceCount( 3L );
        statistic.setSyllableCount( 10L );
        statistic.setTableCount( 2L );
        statistic.setWordCount( 11L );

        return statistic;
    }

    private void verifyDocumentStatistic( DocumentStatistic documentStatistic )
    {
        assertEquals( 2L, documentStatistic.getCharacterCount() );
        assertEquals( 3L, documentStatistic.getDrawCount() );
        assertEquals( 4L, documentStatistic.getFrameCount() );
        assertEquals( 5L, documentStatistic.getImageCount() );
        assertEquals( 6L, documentStatistic.getNonWhitespaceCharacterCount() );
        assertEquals( 1L, documentStatistic.getObjectCount() );
        assertEquals( 8L, documentStatistic.getOleObjectCount() );
        assertEquals( 7L, documentStatistic.getPageCount() );
        assertEquals( 5L, documentStatistic.getParagraphCount() );
        assertEquals( 6L, documentStatistic.getRowCount() );
        assertEquals( 3L, documentStatistic.getSentenceCount() );
        assertEquals( 10L, documentStatistic.getSyllableCount() );
        assertEquals( 2L, documentStatistic.getTableCount() );
        assertEquals( 11L, documentStatistic.getWordCount() );
    }

    private DocumentHyperlinkBehaviour getDocumentHyperlinkBehaviour()
    {
        DocumentHyperlinkBehaviour hylink = new DocumentHyperlinkBehaviour();

        hylink.setTargetFrame( "targetFrame" );

        return hylink;
    }

    private void verifyDocumentHyperlinkBehaviour( DocumentHyperlinkBehaviour hyperlinkBehaviour )
    {
        assertEquals( "targetFrame", hyperlinkBehaviour.getTargetFrame() );
    }

    private DocumentMeta getDocumentMeta()
    {
        DocumentMeta meta = new DocumentMeta();

        meta.setAuthor( "author" );
        meta.addAuthor( getAuthor( 2 ) );
        meta.setConfidential( true );
        meta.setCreationDate( new Date( 1L ) );
        meta.setCreator( "creator" );
        meta.setDate( new Date( 2L ) );
        meta.setDescription( "description" );
        meta.setDocumentStatistic( getDocumentStatistic() );
        meta.setDraft( true );
        meta.setEditingCycles( 15L );
        meta.setEditingDuration( 3L );
        meta.setGenerator( "generator" );
        meta.setHyperlinkBehaviour( getDocumentHyperlinkBehaviour() );
        meta.setInitialCreator( "initialCreator" );
        meta.addKeyWord( "keyword1" );
        meta.addKeyWord( "keyword2" );
        meta.setLanguage( "language" );
        meta.setPageSize( "pageSize" );
        meta.setPrintDate( new Date( 4L ) );
        meta.setPrintedBy( "printedBy" );
        meta.setSubject( "subject" );
        meta.setTemplate( getDocumentTemplate() );
        meta.setTitle( "title" );

        return meta;
    }

    private void verifyDocumentMeta( DocumentMeta meta )
    {
        assertEquals( "author", meta.getAuthor() );
        List<DocumentAuthor> authors = meta.getAuthors();
        assertEquals( 1, authors.size() );
        verifyAuthor( authors.get( 0 ), 2 );

        assertTrue( meta.isConfidential() );
        assertEquals( 1L, meta.getCreationDate().getTime() );
        assertEquals( "creator", meta.getCreator() );
        assertEquals( 2L, meta.getDate().getTime() );
        assertEquals( "description", meta.getDescription() );
        verifyDocumentStatistic( meta.getDocumentStatistic() );
        assertTrue( meta.isDraft() );
        assertEquals( 15L, meta.getEditingCycles() );
        assertEquals( 3L, meta.getEditingDuration() );
        assertEquals( "generator", meta.getGenerator() );
        verifyDocumentHyperlinkBehaviour( meta.getHyperlinkBehaviour() );
        assertEquals( "initialCreator", meta.getInitialCreator() );
        assertEquals( "keyword1, keyword2", meta.getAllKeyWords() );
        assertEquals( "language", meta.getLanguage() );
        assertEquals( "pageSize", meta.getPageSize() );
        assertEquals( 4L, meta.getPrintDate().getTime() );
        assertEquals( "printedBy", meta.getPrintedBy() );
        assertEquals( "subject", meta.getSubject() );
        verifyDocumentTemplate( meta.getTemplate() );
        assertEquals( "title", meta.getTitle() );
    }

    private DocumentTemplate getDocumentTemplate()
    {
        DocumentTemplate template = new DocumentTemplate();

        template.setDate( new Date( 3L ) );
        template.setHref( "href" );
        template.setTitle( "title" );

        return template;
    }

    private void verifyDocumentTemplate( DocumentTemplate template )
    {
        assertEquals( 3L, template.getDate().getTime() );
        assertEquals( "href", template.getHref() );
        assertEquals( "title", template.getTitle() );
    }

    private DocumentTOC getDocumentToc()
    {
        DocumentTOCItem item1 = new DocumentTOCItem();
        item1.setName( "First document" );
        item1.setRef( "doc1.apt" );

        DocumentTOCItem item2 = new DocumentTOCItem();
        item2.setName( "Second document" );
        item2.setRef( "doc2.xml" );

        DocumentTOC toc = new DocumentTOC();
        toc.setName( "name" );
        toc.addItem( item1 );
        toc.addItem( item2 );

        return toc;
    }

    private void verifyDocumentTOC( DocumentTOC toc )
    {
        assertEquals( "name", toc.getName() );
    }

    private DocumentModel writeAndRecover( DocumentModel model )
            throws IOException
    {
        File dir = getTestFile( "target/test-output/xpp3/" );

        if ( !dir.exists() )
        {
            dir.mkdirs();
        }

        File testFile = getTestFile( dir.getAbsolutePath(), "testModel.xml" );
        Writer w = null;

        try
        {
            w = WriterFactory.newXmlWriter( testFile );
            new DocumentXpp3Writer().write( w, model );
        }
        finally
        {
            IOUtil.close( w );
        }

        DocumentModel documentModel;

        try ( Reader reader = ReaderFactory.newXmlReader( testFile ) )
        {
            documentModel = new DocumentXpp3Reader().read( reader );
        }
        catch ( XmlPullParserException e )
        {
            throw new IOException( "Error parsing document descriptor", e );
        }

        return documentModel;
    }
}