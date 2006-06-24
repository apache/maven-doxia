package org.apache.maven.doxia.module.itext;

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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.maven.doxia.module.apt.AptParser;
import org.apache.maven.doxia.module.xdoc.XdocParser;
import org.apache.maven.doxia.sink.Sink;
import org.codehaus.plexus.PlexusTestCase;

/**
 * <code>iText Sink</code> Test case.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class ITextSinkTestCase
    extends PlexusTestCase
{
    /**
     * Convenience method
     *
     * @param prefix
     * @param suffix
     * @return the input file
     */
    protected File getGeneratedFile( String prefix, String suffix )
    {
        File outputDirectory = new File( getBasedir(), "target/output" );
        if ( !outputDirectory.exists() )
        {
            outputDirectory.mkdirs();
        }

        return new File( outputDirectory, prefix + suffix );
    }

    /**
     * Create an <code>iTextSink</code> with a given classLoader (images dir)
     *
     * @param prefix
     * @param suffix
     * @return an iTextSink
     * @throws Exception if any
     */
    protected Sink createSink( String prefix, String suffix )
        throws Exception
    {
        ITextSink sink = new ITextSink( new FileWriter( getGeneratedFile( prefix, suffix ) ) );

        sink.setClassLoader( new URLClassLoader( new URL[] { ITextSinkTestCase.class.getResource( "/images/" ) } ) );

        return sink;
    }

    /**
     * @param path
     * @return a reader from an <code>apt</code> file.
     * @throws Exception if any
     */
    protected Reader getAptReader( String path )
        throws Exception
    {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( path );

        InputStreamReader reader = new InputStreamReader( is );

        return reader;
    }

    /**
     * @param path
     * @return a reader from an <code>xdoc</code> file.
     * @throws Exception if any
     */
    protected Reader getXdocReader( String path )
        throws Exception
    {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( path );

        InputStreamReader reader = new InputStreamReader( is );

        return reader;
    }

    public void testGeneratingPDFFromITextXml()
        throws Exception
    {
        File f = new File( getBasedir(), "src/test/resources/apt/itext.xml" );

        ITextUtil.writePdf( new FileInputStream( f ),
                            new FileOutputStream( getGeneratedFile( "test_itext_apt", ".pdf" ) ) );
    }

    /**
     * Generate a pdf and a rtf from an <code>apt</code> file
     *
     * @throws Exception if any
     */
    public void testApt()
        throws Exception
    {
        Sink sink = createSink( "test_apt", ".xml" );

        AptParser parser = new AptParser();
        parser.parse( getAptReader( "apt/test.apt" ), sink );

        sink.close();

        ITextUtil.writePdf( new FileInputStream( getGeneratedFile( "test_apt", ".xml" ) ),
                            new FileOutputStream( getGeneratedFile( "test_apt", ".pdf" ) ) );

        //ITextUtil.writeRtf( new FileInputStream( getGeneratedFile( "test_apt", ".xml" ) ),
          //                  new FileOutputStream( getGeneratedFile( "test_apt", ".rtf" ) ) );
    }

    /**
     * Generate a pdf and a rtf from an <code>apt</code> file
     *
     * @throws Exception if any
     */
    public void xtestApt2()
        throws Exception
    {
        Sink sink = createSink( "guide-ide-netbeans_apt", ".xml" );

        AptParser parser = new AptParser();
        parser.parse( getAptReader( "apt/guide-ide-netbeans.apt" ), sink );

        sink.close();

        ITextUtil.writePdf( new FileInputStream( getGeneratedFile( "guide-ide-netbeans_apt", ".xml" ) ),
                            new FileOutputStream( getGeneratedFile( "guide-ide-netbeans_apt", ".pdf" ) ) );

        ITextUtil.writeRtf( new FileInputStream( getGeneratedFile( "guide-ide-netbeans_apt", ".xml" ) ),
                            new FileOutputStream( getGeneratedFile( "guide-ide-netbeans_apt", ".rtf" ) ) );
    }

    /**
     * Generate a pdf and a rtf from an <code>xdoc</code> file
     *
     * @throws Exception if any
     */
    public void xtestXdoc()
        throws Exception
    {
        Sink sink = createSink( "test_xdoc", ".xml" );

        XdocParser parser = new XdocParser();
        parser.parse( getXdocReader( "xdoc/test.xml" ), sink );

        sink.close();

        ITextUtil.writePdf( new FileInputStream( getGeneratedFile( "test_xdoc", ".xml" ) ),
                            new FileOutputStream( getGeneratedFile( "test_xdoc", ".pdf" ) ) );

        ITextUtil.writeRtf( new FileInputStream( getGeneratedFile( "test_xdoc", ".xml" ) ),
                            new FileOutputStream( getGeneratedFile( "test_xdoc", ".rtf" ) ) );
    }
}
