package org.apache.maven.doxia.docrenderer.itext;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.maven.doxia.Doxia;
import org.apache.maven.doxia.docrenderer.DocRenderer;
import org.apache.maven.doxia.docrenderer.DocRendererException;
import org.apache.maven.doxia.docrenderer.document.DocumentModel;
import org.apache.maven.doxia.docrenderer.document.DocumentTOCItem;
import org.apache.maven.doxia.docrenderer.document.io.xpp3.DocumentXpp3Reader;
import org.apache.maven.doxia.module.itext.ITextSink;
import org.apache.maven.doxia.module.itext.ITextUtil;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.manager.ParserNotFoundException;
import org.apache.maven.doxia.site.module.SiteModule;
import org.apache.maven.doxia.site.module.manager.SiteModuleManager;
import org.apache.xml.utils.DefaultErrorHandler;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.lowagie.text.ElementTags;

/**
 * Abstract <code>document</code> render with the <code>iText</code> framework
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public abstract class AbstractITextRender
    extends AbstractLogEnabled
    implements DocRenderer
{
    private static String XSLT_RESOURCE = "org/apache/maven/doxia/docrenderer/itext/xslt/TOC.xslt";

    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

    /**
     * @plexus.requirement
     */
    protected SiteModuleManager siteModuleManager;

    /**
     * @plexus.requirement
     */
    protected Doxia doxia;

    static
    {
        TRANSFORMER_FACTORY.setErrorListener( new DefaultErrorHandler() );
    }

    /**
     * @see org.apache.maven.doxia.docrenderer.DocRenderer#render(java.io.File, java.io.File)
     */
    public void render( File siteDirectory, File outputDirectory )
        throws DocRendererException, IOException
    {
        for ( Iterator i = siteModuleManager.getSiteModules().iterator(); i.hasNext(); )
        {
            SiteModule module = (SiteModule) i.next();

            File moduleBasedir = new File( siteDirectory, module.getSourceDirectory() );

            if ( moduleBasedir.exists() )
            {
                List docs = FileUtils.getFileNames( moduleBasedir, "**/*." + module.getExtension(), null, false );

                for ( Iterator j = docs.iterator(); j.hasNext(); )
                {
                    String doc = (String) j.next();
                    String fullPathDoc = new File( moduleBasedir, doc ).getPath();

                    String outputITextName = doc.substring( 0, doc.indexOf( "." ) + 1 ) + "xml";
                    File outputITextFile = new File( outputDirectory, outputITextName );
                    if ( !outputITextFile.getParentFile().exists() )
                    {
                        outputITextFile.getParentFile().mkdirs();
                    }
                    String iTextOutputName = doc.substring( 0, doc.indexOf( "." ) + 1 ) + getOutputExtension();
                    File iTextOutputFile = new File( outputDirectory, iTextOutputName );
                    if ( !iTextOutputFile.getParentFile().exists() )
                    {
                        iTextOutputFile.getParentFile().mkdirs();
                    }

                    parse( fullPathDoc, module, outputITextFile );

                    generateOutput( outputITextFile, iTextOutputFile );
                }
            }
        }
    }

    /**
     * @see org.apache.maven.doxia.docrenderer.DocRenderer#render(java.io.File, java.io.File, java.io.File)
     */
    public void render( File siteDirectory, File outputDirectory, File documentDescriptor )
        throws DocRendererException, IOException
    {
        if ( ( documentDescriptor == null ) || ( !documentDescriptor.exists() ) )
        {
            getLogger().info( "No documentDescriptor is found. Generate all documents." );
            render( siteDirectory, outputDirectory );
            return;
        }

        DocumentModel documentModel;
        try
        {
            documentModel = new DocumentXpp3Reader().read( new FileReader( documentDescriptor ) );
        }
        catch ( XmlPullParserException e )
        {
            throw new DocRendererException( "Error parsing document descriptor", e );
        }
        catch ( IOException e )
        {
            throw new DocRendererException( "Error reading document descriptor", e );
        }

        if ( documentModel.getOutputName() == null )
        {
            getLogger().info( "No outputName is defined in the document descriptor. Using 'generated_itext'" );
            documentModel.setOutputName( "generated_itext" );
        }

        if ( ( documentModel.getToc() == null ) || ( documentModel.getToc().getItems() == null ) )
        {
            getLogger().info( "No TOC is defined in the document descriptor. Merging all documents." );
        }

        List iTextFiles = new LinkedList();
        for ( Iterator i = siteModuleManager.getSiteModules().iterator(); i.hasNext(); )
        {
            SiteModule module = (SiteModule) i.next();

            File moduleBasedir = new File( siteDirectory, module.getSourceDirectory() );

            if ( moduleBasedir.exists() )
            {
                List docs = FileUtils.getFileNames( moduleBasedir, "**/*." + module.getExtension(), null, false );

                for ( Iterator j = docs.iterator(); j.hasNext(); )
                {
                    String doc = (String) j.next();
                    String fullPathDoc = new File( moduleBasedir, doc ).getPath();

                    String outputITextName = doc.substring( 0, doc.lastIndexOf( "." ) + 1 ) + "xml";
                    File outputITextFile = new File( outputDirectory, outputITextName );

                    if ( ( documentModel.getToc() == null ) || ( documentModel.getToc().getItems() == null ) )
                    {
                        iTextFiles.add( outputITextFile );

                        if ( !outputITextFile.getParentFile().exists() )
                        {
                            outputITextFile.getParentFile().mkdirs();
                        }

                        parse( fullPathDoc, module, outputITextFile );
                    }
                    else
                    {
                        for ( Iterator k = documentModel.getToc().getItems().iterator(); k.hasNext(); )
                        {
                            DocumentTOCItem tocItem = (DocumentTOCItem) k.next();

                            if ( tocItem.getRef() == null )
                            {
                                getLogger().info( "No ref defined for an tocItem in the document descriptor." );
                                continue;
                            }

                            String outTmp = StringUtils.replace( outputITextFile.getAbsolutePath(), "\\", "/" );
                            outTmp = outTmp.substring( 0, outTmp.lastIndexOf( "." ) );

                            String outRef = StringUtils.replace( tocItem.getRef(), "\\", "/" );
                            if (outRef.lastIndexOf( "." )!= -1)
                            {
                                outRef = outRef.substring( 0, outRef.lastIndexOf( "." ) );
                            }
                            else
                            {
                                outRef = outRef.substring( 0, outRef.length() );
                            }

                            if ( outTmp.indexOf( outRef ) != -1 )
                            {
                                iTextFiles.add( outputITextFile );

                                if ( !outputITextFile.getParentFile().exists() )
                                {
                                    outputITextFile.getParentFile().mkdirs();
                                }

                                parse( fullPathDoc, module, outputITextFile );
                            }
                        }
                    }
                }
            }
        }

        File iTextFile = new File( outputDirectory, documentModel.getOutputName() + ".xml" );
        File iTextOutput = new File( outputDirectory, documentModel.getOutputName() + "." + getOutputExtension() );
        Document document = generateDocument( iTextFiles );
        transform( documentModel, document, iTextFile );
        generateOutput( iTextFile, iTextOutput );
    }

    /**
     * Generate an ouput file with the iText framework
     *
     * @param iTextFile
     * @param iTextOutput
     * @throws DocRendererException if any
     * @throws IOException if any
     */
    public abstract void generateOutput( File iTextFile, File iTextOutput )
        throws DocRendererException, IOException;

    /**
     * Parse a sink
     *
     * @param fullPathDoc
     * @param module
     * @param outputITextFile
     * @throws DocRendererException
     * @throws IOException
     */
    private void parse( String fullPathDoc, SiteModule module, File outputITextFile )
        throws DocRendererException, IOException
    {
        ITextSink sink = new ITextSink( new FileWriter( outputITextFile ) );

        sink.setClassLoader( new URLClassLoader( new URL[] { outputITextFile.getParentFile().toURL() } ) );
        try
        {
            FileReader reader = new FileReader( fullPathDoc );

            doxia.parse( reader, module.getParserId(), sink );
        }
        catch ( ParserNotFoundException e )
        {
            throw new DocRendererException( "Error getting a parser for " + fullPathDoc + ": " + e.getMessage() );
        }
        catch ( ParseException e )
        {
            getLogger().error( "Error parsing " + fullPathDoc + ": " + e.getMessage(), e );
        }
        finally
        {
            sink.flush();

            sink.close();
        }
    }

    /**
     * Merge all iTextFiles to a single one
     *
     * @param iTextFiles
     * @return a document
     * @throws DocRendererException if any
     * @throws IOException if any
     */
    private Document generateDocument( List iTextFiles )
        throws DocRendererException, IOException
    {
        Document document;
        try
        {
            document = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder().newDocument();
        }
        catch ( ParserConfigurationException e )
        {
            throw new DocRendererException( "Error building document :" + e.getMessage() );
        }
        document.appendChild( document.createElement( ElementTags.ITEXT ) ); // Used only to set a root

        for ( int i = 0; i < iTextFiles.size(); i++ )
        {
            File iTextFile = (File) iTextFiles.get( i );

            Document iTextDocument;
            try
            {
                iTextDocument = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder().parse( iTextFile );
            }
            catch ( SAXException e )
            {
                throw new DocRendererException( "SAX Error : " + e.getMessage() );
            }
            catch ( ParserConfigurationException e )
            {
                throw new DocRendererException( "Error parsing configuration : " + e.getMessage() );
            }

            // Only one chapter per doc
            Node chapter = iTextDocument.getElementsByTagName( ElementTags.CHAPTER ).item( 0 );
            try
            {
                document.getDocumentElement().appendChild( document.importNode( chapter, true ) );
            }
            catch ( DOMException e )
            {
                throw new DocRendererException( "Error appending chapter for " + iTextFile + " : " + e.getMessage() );
            }
        }

        return document;
    }

    /**
     * Init the transformer object
     *
     * @return an instanced transformer object
     * @throws DocRendererException if any
     */
    private Transformer initTransformer()
        throws DocRendererException
    {
        try
        {
            Transformer transformer = TRANSFORMER_FACTORY.newTransformer( new StreamSource( DefaultPdfRenderer.class
                .getResourceAsStream( "/" + XSLT_RESOURCE ) ) );
            transformer.setErrorListener( TRANSFORMER_FACTORY.getErrorListener() );

            transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "false" );
            transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
            transformer.setOutputProperty( OutputKeys.METHOD, "xml" );
            transformer.setOutputProperty( OutputKeys.ENCODING, "UTF-8" );

            return transformer;
        }
        catch ( TransformerConfigurationException e )
        {
            throw new DocRendererException( "Error configuring Transformer for " + XSLT_RESOURCE + ": "
                + e.getMessage() );
        }
        catch ( IllegalArgumentException e )
        {
            throw new DocRendererException( "Error configuring Transformer for " + XSLT_RESOURCE + ": "
                + e.getMessage() );
        }
    }

    /**
     * Add transformer parameters
     *
     * @param transformer
     * @param documentModel
     */
    private void addTransformerParameters( Transformer transformer, DocumentModel documentModel )
    {
        transformer.setParameter( "title", documentModel.getMeta().getTitle() );
        transformer.setParameter( "author", documentModel.getMeta().getAuthor() );
        transformer.setParameter( "creationdate", new Date().toString() );
        transformer.setParameter( "subject", documentModel.getMeta().getSubject() );
        transformer.setParameter( "keywords", documentModel.getMeta().getKeywords() );
        transformer.setParameter( "producer", "Generated with Doxia by " + System.getProperty( "user.name" ) );
        if ( ITextUtil.isPageSizeSupported( documentModel.getMeta().getTitle() ) )
        {
            transformer.setParameter( "pagesize", documentModel.getMeta().getPageSize() );
        }
        else
        {
            transformer.setParameter( "pagesize", "A4" );
        }

        transformer.setParameter( "frontPageHeader", "" );
        transformer.setParameter( "frontPageTitle", documentModel.getMeta().getTitle() );
        transformer.setParameter( "frontPageFooter", "Generated date " + new Date().toString() );
    }

    /**
     * Transform a document to an iTextFile
     *
     * @param documentModel
     * @param document
     * @param iTextFile
     * @throws DocRendererException
     */
    private void transform( DocumentModel documentModel, Document document, File iTextFile )
        throws DocRendererException
    {
        Transformer transformer = initTransformer();

        addTransformerParameters( transformer, documentModel );

        try
        {
            transformer.transform( new DOMSource( document ), new StreamResult( iTextFile ) );
        }
        catch ( TransformerException e )
        {
            throw new DocRendererException( "Error transformer Document from " + document + ": " + e.getMessage() );
        }
    }
}
