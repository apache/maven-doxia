package org.apache.maven.doxia.xsd;

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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.manager.ScmManager;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Abstract class to validate XML files with Doxia namespaces.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractXmlValidatorTest
    extends PlexusTestCase
{
    /** The vm line separator */
    protected static final String EOL = System.getProperty( "line.separator" );

    /** XMLReader to validate xml file */
    private XMLReader xmlReader;

    /** The scm manager */
    private ScmManager scmManager;

    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        for ( Iterator it = getScmRepositoryWrapper().iterator(); it.hasNext(); )
        {
            ScmRepositoryWrapper wrapper = (ScmRepositoryWrapper) it.next();

            if ( !wrapper.getCheckoutDir().exists() )
            {
                wrapper.getCheckoutDir().mkdirs();
                getScmManager().checkOut( getScmManager().makeScmRepository( wrapper.getScmRepository() ),
                                          new ScmFileSet( wrapper.getCheckoutDir() ) );
            }
        }
    }

    /** {@inheritDoc} */
    protected void tearDown()
        throws Exception
    {
        super.tearDown();

        scmManager = null;

        xmlReader = null;
    }

    /**
     * @return non null list of ScmRepositoryWrapper
     */
    protected abstract List getScmRepositoryWrapper();

    /**
     * @param wrapper not null
     * @return a list of files in a given wrapper to be validated
     * @throws IOException if any
     */
    protected abstract List getXmlFiles( ScmRepositoryWrapper wrapper )
        throws IOException;

    /**
     * @param content xml content not null
     * @return xml content with the wanted Doxia namespace
     */
    protected abstract String addNamespaces( String content );

    /**
     * Test xml files with namespace.
     *
     * @throws Exception if any
     */
    public void testXmlFilesWithDoxiaNamespaces()
        throws Exception
    {
        for ( Iterator it = getScmRepositoryWrapper().iterator(); it.hasNext(); )
        {
            ScmRepositoryWrapper wrapper = (ScmRepositoryWrapper) it.next();

            for ( Iterator it2 = getXmlFiles( wrapper ).iterator(); it2.hasNext(); )
            {
                File f = (File) it2.next();

                List errors = parseXML( f );

                for ( Iterator it3 = errors.iterator(); it3.hasNext(); )
                {
                    String message = (String) it3.next();

                    if ( message.length() != 0 )
                    {
                        // Exclude some xhtml errors
                        if ( message.indexOf( "schema_reference.4: Failed to read schema document 'http://www.w3.org/2001/xml.xsd'" ) == -1
                            && message.indexOf( "Message: cvc-complex-type.4: Attribute 'alt' must appear on element 'img'." ) == -1
                            && message.indexOf( "Message: cvc-complex-type.2.4.a: Invalid content starting with element" ) == -1
                            && message.indexOf( "Message: cvc-complex-type.2.4.a: Invalid content was found starting with element" ) == -1
                            && message.indexOf( "Message: cvc-datatype-valid.1.2.1:" ) == -1  // Doxia allow space
                            && message.indexOf( "Message: cvc-attribute.3:" ) == -1 ) // Doxia allow space
                        {
                            fail( f.getAbsolutePath() + EOL + message );
                        }
                        else
                        {
                            if ( getContainer().getLogger().isDebugEnabled() )
                            {
                                getContainer().getLogger().debug( f.getAbsolutePath() + EOL + message );
                            }
                        }
                    }
                }
            }
        }
    }

    // ----------------------------------------------------------------------
    // Private method
    // ----------------------------------------------------------------------

    private ScmManager getScmManager()
        throws Exception
    {
        if ( scmManager == null )
        {
            scmManager = (ScmManager) lookup( ScmManager.ROLE );
        }

        return scmManager;
    }

    private XMLReader getXMLReader()
    {
        if ( xmlReader == null )
        {
            try
            {
                xmlReader = XMLReaderFactory.createXMLReader( "org.apache.xerces.parsers.SAXParser" );
                xmlReader.setFeature( "http://xml.org/sax/features/validation", true );
                xmlReader.setFeature( "http://apache.org/xml/features/validation/schema", true );
                MessagesErrorHandler errorHandler = new MessagesErrorHandler();
                xmlReader.setErrorHandler( errorHandler );
            }
            catch ( SAXNotRecognizedException e )
            {
                throw new AssertionFailedError( "SAXNotRecognizedException: " + e.getMessage() );
            }
            catch ( SAXNotSupportedException e )
            {
                throw new AssertionFailedError( "SAXNotSupportedException: " + e.getMessage() );
            }
            catch ( SAXException e )
            {
                throw new AssertionFailedError( "SAXException: " + e.getMessage() );
            }
        }

        return xmlReader;
    }

    private List parseXML( File f )
        throws IOException, SAXException
    {
        Reader reader = ReaderFactory.newXmlReader( f );
        String content = IOUtil.toString( reader );
        content = addNamespaces( content );

        MessagesErrorHandler errorHandler = (MessagesErrorHandler) getXMLReader().getErrorHandler();

        getXMLReader().parse( new InputSource( new ByteArrayInputStream( content.getBytes() ) ) );

        return errorHandler.getMessages();
    }

    private static class MessagesErrorHandler
        extends DefaultHandler
    {
        private final List messages;

        public MessagesErrorHandler()
        {
            messages = new ArrayList();
        }

        /** {@inheritDoc} */
        public void warning( SAXParseException e )
            throws SAXException
        {
            addMessage( "Warning:", e );
        }

        /** {@inheritDoc} */
        public void error( SAXParseException e )
            throws SAXException
        {
            addMessage( "Error:", e );
        }

        /** {@inheritDoc} */
        public void fatalError( SAXParseException e )
            throws SAXException
        {
            addMessage( "Fatal error:", e );
        }

        private void addMessage( String pre, SAXParseException e )
        {
            StringBuffer message = new StringBuffer();

            message.append( pre ).append( EOL );
            message.append( "  Public ID: " + e.getPublicId() ).append( EOL );
            message.append( "  System ID: " + e.getSystemId() ).append( EOL );
            message.append( "  Line number: " + e.getLineNumber() ).append( EOL );
            message.append( "  Column number: " + e.getColumnNumber() ).append( EOL );
            message.append( "  Message: " + e.getMessage() ).append( EOL );

            messages.add( message.toString() );
        }

        protected List getMessages()
        {
            return messages;
        }
    }

    protected static class ScmRepositoryWrapper
    {
        private final String scmRepository;

        private final File checkoutDir;

        public ScmRepositoryWrapper( String scmRepository, File checkoutDir )
        {
            this.scmRepository = scmRepository;
            this.checkoutDir = checkoutDir;
        }

        /**
         * @return the scmRepository
         */
        public String getScmRepository()
        {
            return scmRepository;
        }

        /**
         * @return the checkoutDir
         */
        public File getCheckoutDir()
        {
            return checkoutDir;
        }
    }
}
