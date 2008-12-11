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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import junit.framework.AssertionFailedError;

import org.apache.maven.doxia.parser.AbstractXmlParser;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.SelectorUtils;
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

    /** Simple cache mechanism to load test documents. */
    private static final Map CACHE_DOXIA_TEST_DOCUMENTS = new Hashtable();

    /** Maven resource in the doxia-test-docs-XXX.jar */
    private static final String MAVEN_RESOURCE_PATH = "META-INF/maven/org.apache.maven.doxia/doxia-test-docs/";

    /** XMLReader to validate xml file */
    private XMLReader xmlReader;

    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    /** {@inheritDoc} */
    protected void tearDown()
        throws Exception
    {
        super.tearDown();

        xmlReader = null;
    }

    /**
     * @return a non null patterns to includes specific test files.
     * @see AbstractXmlValidatorTest#getTestDocuments()
     */
    protected abstract String[] getIncludes();

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
        for ( Iterator it = getTestDocuments().entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) it.next();

            if ( getContainer().getLogger().isDebugEnabled() )
            {
                getContainer().getLogger().debug( "Validate '" + entry.getKey() + "'" );
            }

            List errors = parseXML( entry.getValue().toString() );

            for ( Iterator it2 = errors.iterator(); it2.hasNext(); )
            {
                String message = (String) it2.next();

                if ( message.length() != 0 )
                {
                    // Exclude some xhtml errors
                    if ( message
                                .indexOf( "schema_reference.4: Failed to read schema document 'http://www.w3.org/2001/xml.xsd'" ) == -1
                        && message
                                  .indexOf( "Message: cvc-complex-type.4: Attribute 'alt' must appear on element 'img'." ) == -1
                        && message
                                  .indexOf( "Message: cvc-complex-type.2.4.a: Invalid content starting with element" ) == -1
                        && message
                                  .indexOf( "Message: cvc-complex-type.2.4.a: Invalid content was found starting with element" ) == -1
                        && message.indexOf( "Message: cvc-datatype-valid.1.2.1:" ) == -1 // Doxia allow space
                        && message.indexOf( "Message: cvc-attribute.3:" ) == -1 ) // Doxia allow space
                    {
                        fail( entry.getKey() + EOL + message );
                    }
                    else
                    {
                        if ( getContainer().getLogger().isDebugEnabled() )
                        {
                            getContainer().getLogger().debug( entry.getKey() + EOL + message );
                        }
                    }
                }
            }
        }
    }

    // ----------------------------------------------------------------------
    // Private method
    // ----------------------------------------------------------------------

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

                xmlReader.setEntityResolver( new AbstractXmlParser.CachedFileEntityResolver() );
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

    private List parseXML( String xmlContent )
        throws IOException, SAXException
    {
        xmlContent = addNamespaces( xmlContent );

        MessagesErrorHandler errorHandler = (MessagesErrorHandler) getXMLReader().getErrorHandler();

        getXMLReader().parse( new InputSource( new StringReader( xmlContent ) ) );

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

    /**
     * @return a map of test resources filtered by patterns from {@link #getIncludes()}.
     * @throws IOException if any
     * @see #getIncludes()
     * @see #getAllTestDocuments()
     */
    protected Map getTestDocuments()
        throws IOException
    {
        if ( getIncludes() == null )
        {
            return Collections.EMPTY_MAP;
        }

        Map testDocs = getAllTestDocuments();
        Map ret = new Hashtable();
        ret.putAll( testDocs );
        for ( Iterator it = testDocs.keySet().iterator(); it.hasNext(); )
        {
            String key = it.next().toString();

            for (int i = 0; i < getIncludes().length; i++)
            {
                if ( !SelectorUtils.matchPath( getIncludes()[i], key.toLowerCase( Locale.ENGLISH ) ) )
                {
                    ret.remove( key );
                }
            }
        }

        return ret;
    }

    /**
     * Find test resources in the <code>doxia-test-docs-XXX.jar</code>
     *
     * @return a map of test resources defined as follow:
     * <ul>
     *   <li>key, the full url of test documents, i.e. <code>jar:file:/.../doxia-test-docs-XXX.jar!/path/to/resource</code></li>
     *   <li>value, the content for the resource defined by the key</li>
     * </ul>
     * @throws IOException if any
     */
    protected static Map getAllTestDocuments()
        throws IOException
    {
        if ( CACHE_DOXIA_TEST_DOCUMENTS != null && !CACHE_DOXIA_TEST_DOCUMENTS.isEmpty() )
        {
            return CACHE_DOXIA_TEST_DOCUMENTS;
        }

        URL testJar = AbstractXmlValidatorTest.class.getClassLoader().getResource( MAVEN_RESOURCE_PATH );
        if ( testJar == null )
        {
            throw new RuntimeException(
                                        "Could not find the Doxia test documents artefact i.e. doxia-test-docs-XXX.jar" );
        }

        JarURLConnection conn = (JarURLConnection) testJar.openConnection();
        JarFile jarFile = conn.getJarFile();
        for ( Enumeration e = jarFile.entries(); e.hasMoreElements(); )
        {
            JarEntry entry = (JarEntry) e.nextElement();

            if ( entry.getName().startsWith( "META-INF" ) )
            {
                continue;
            }
            if ( entry.isDirectory() )
            {
                continue;
            }

            InputStream in = null;
            try
            {
                in = AbstractXmlValidatorTest.class.getClassLoader().getResource( entry.getName() ).openStream();
                String content = IOUtil.toString( in );
                CACHE_DOXIA_TEST_DOCUMENTS.put( "jar:" + conn.getJarFileURL() + "!/" + entry.getName(), content );
            }
            finally
            {
                IOUtil.close( in );
            }
        }

        return CACHE_DOXIA_TEST_DOCUMENTS;
    }
}
