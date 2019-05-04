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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.maven.doxia.parser.AbstractXmlParser;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.SelectorUtils;
import org.codehaus.plexus.util.xml.XmlUtil;

import org.xml.sax.EntityResolver;

/**
 * Abstract class to validate XML files with DTD or XSD mainly for Doxia namespaces.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractXmlValidatorTest
    extends AbstractXmlValidator
{

    /** Simple cache mechanism to load test documents. */
    private static final Map<String,String> CACHE_DOXIA_TEST_DOCUMENTS = new Hashtable<>();

    /** Maven resource in the doxia-test-docs-XXX.jar */
    private static final String MAVEN_RESOURCE_PATH = "META-INF/maven/org.apache.maven.doxia/doxia-test-docs/";

    // ----------------------------------------------------------------------
    // Protected methods
    // ----------------------------------------------------------------------

    /**
     * @return a non null patterns to includes specific test files.
     * @see AbstractXmlValidatorTest#getTestDocuments()
     */
    protected abstract String[] getIncludes();

    /**
     * @return a map of test resources filtered by patterns from {@link #getIncludes()}.
     * @throws IOException if any
     * @see #getIncludes()
     * @see #getAllTestDocuments()
     */
    protected Map<String, String> getTestDocuments()
        throws IOException
    {
        if ( getIncludes() == null )
        {
            return Collections.emptyMap();
        }

        Map<String,String> testDocs = getAllTestDocuments();
        Map<String, String> ret = new Hashtable<>( testDocs );
        for ( String key : testDocs.keySet() )
        {
            for ( int i = 0; i < getIncludes().length; i++ )
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
     * Returns the EntityResolver that is used by the XMLReader for validation.
     * By default a {@link AbstractXmlParser.CachedFileEntityResolver} is used,
     * but implementations should override this for performance reasons.
     *
     * @return an EntityResolver. Not null.
     *
     * @since 1.2
     */
    protected EntityResolver getEntityResolver()
    {
        return new AbstractXmlParser.CachedFileEntityResolver();
    }

    /**
     * Find test resources in the <code>doxia-test-docs-XXX.jar</code> or in an IDE project.
     *
     * @return a map of test resources defined as follow:
     * <ul>
     *   <li>key, the full url of test documents,
     *      i.e. <code>jar:file:/.../doxia-test-docs-XXX.jar!/path/to/resource</code></li>
     *   <li>value, the content for the resource defined by the key</li>
     * </ul>
     * @throws IOException if any
     */
    protected static Map<String,String> getAllTestDocuments()
        throws IOException
    {
        if ( CACHE_DOXIA_TEST_DOCUMENTS != null && !CACHE_DOXIA_TEST_DOCUMENTS.isEmpty() )
        {
            return Collections.unmodifiableMap( CACHE_DOXIA_TEST_DOCUMENTS );
        }

        URL testJar = AbstractXmlValidatorTest.class.getClassLoader().getResource( MAVEN_RESOURCE_PATH );
        if ( testJar == null )
        {
            // maybe in an IDE project
            testJar = AbstractXmlValidatorTest.class.getClassLoader().getResource( "doxia-site" );

            if ( testJar == null )
            {
                throw new RuntimeException(
                        "Could not find the Doxia test documents artefact i.e. doxia-test-docs-XXX.jar" );
            }
        }

        if ( testJar.toString().startsWith( "jar" ))
            {
            JarURLConnection conn = (JarURLConnection) testJar.openConnection();
            JarFile jarFile = conn.getJarFile();
            for ( Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements(); )
            {
                JarEntry entry = e.nextElement();

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
                    String content = IOUtil.toString( in, "UTF-8" );
                    CACHE_DOXIA_TEST_DOCUMENTS.put( "jar:" + conn.getJarFileURL() + "!/" + entry.getName(), content );
                }
                finally
                {
                    IOUtil.close( in );
                }
            }
        }
        else
        {
            // IDE projects
            File testDocsDir = FileUtils.toFile( testJar ).getParentFile();

            List<File> files = FileUtils.getFiles( testDocsDir, "**/*.*", FileUtils.getDefaultExcludesAsString(), true );
            for ( File file1 : files )
            {
                File file = new File( file1.toString() );

                if ( file.getAbsolutePath().contains( "META-INF" ) )
                {
                    continue;
                }

                Reader reader = null;
                if ( XmlUtil.isXml( file ) )
                {
                    reader = ReaderFactory.newXmlReader( file );
                }
                else
                {
                    reader = ReaderFactory.newReader( file, "UTF-8" );
                }

                String content = IOUtil.toString( reader );
                CACHE_DOXIA_TEST_DOCUMENTS.put( file.toURI().toString(), content );
            }
        }

        return Collections.unmodifiableMap( CACHE_DOXIA_TEST_DOCUMENTS );
    }
}
