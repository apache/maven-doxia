package org.apache.maven.doxia.module.itext;

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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Writer;

import java.net.URL;
import java.net.URLClassLoader;

import org.apache.maven.doxia.AbstractModuleTest;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkTestDocument;

/**
 * <code>iText Sink</code> Test case.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class ITextSinkTestCase
    extends AbstractModuleTest
{
    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "xml";
    }

    /** {@inheritDoc} */
    protected String getOutputDir()
    {
        return "sink/";
    }

    /**
     * Convenience method
     *
     * @param prefix
     * @param suffix
     * @return the input file
     */
    protected File getGeneratedFile( String prefix, String suffix )
    {
        File outputDirectory = new File( getBasedir(), outputBaseDir() + getOutputDir() );
        if ( !outputDirectory.exists() )
        {
            outputDirectory.mkdirs();
        }

        return new File( outputDirectory, prefix + "." + suffix );
    }

    protected Sink createSink( Writer writer )
    {
        ITextSink sink = new ITextSink( writer );

        sink.setClassLoader( new URLClassLoader(
            new URL[] { ITextSinkTestCase.class.getResource( "/images/" ) } ) );

        return sink;
    }

    /**
     * Test PDF generation
     *
     * @throws Exception
     */
    public void testGeneratingPDFFromITextXml()
        throws Exception
    {
        File f = new File( getBasedir(), "src/test/resources/itext/itext.xml" );

        ITextUtil.writePdf( new FileInputStream( f ),
                            new FileOutputStream( getGeneratedFile( "test_itext", "pdf" ) ) );
    }

    /**
     * Generate a pdf and a rtf from the standart test model.
     *
     * @throws Exception if any
     */
    public void testModel()
        throws Exception
    {
        Sink sink = createSink( getXmlTestWriter( "test_model", "xml" ) );

        SinkTestDocument.generate( sink );

        sink.close();

        ITextUtil.writePdf( new FileInputStream( getGeneratedFile( "test_model", "xml" ) ),
                            new FileOutputStream( getGeneratedFile( "test_model", "pdf" ) ) );
        ITextUtil.writeRtf( new FileInputStream( getGeneratedFile( "test_model", "xml" ) ),
                            new FileOutputStream( getGeneratedFile( "test_model", "rtf" ) ) );
    }
}
