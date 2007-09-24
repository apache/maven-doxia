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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

/**
 * <code>FO Sink</code> utilities.
 *
 * @author ltheussl
 * @version $Id$
 * @since 1.0
 */
public class FoUtils
{

    /**
     * Converts an FO file to a PDF file using FOP.
     *
     * @param fo the FO file.
     * @param pdf the target PDF file.
     * @param resourceDir The base directory for relative path resolution.
     * If null, defaults to the parent directory of fo.
     * @throws TransformerException In case of a conversion problem.
     */
    public static void convertFO2PDF( File fo, File pdf, String resourceDir )
        throws TransformerException
    {

        FopFactory fopFactory = FopFactory.newInstance();

        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

        foUserAgent.setBaseURL( getBaseURL( fo, resourceDir ) );

        OutputStream out = null;

        try
        {
            try
            {
                out = new BufferedOutputStream( new FileOutputStream( pdf ) );
            }
            catch ( IOException e )
            {
                throw new TransformerException( e );
            }

            Fop fop = fopFactory.newFop( MimeConstants.MIME_PDF, foUserAgent, out );

            Source src = new StreamSource( fo );

            Result res = new SAXResult( fop.getDefaultHandler() );

            Transformer transformer = null;

            try
            {
                // identity transformer
                transformer = TransformerFactory.newInstance().newTransformer();
            }
            catch ( TransformerConfigurationException e )
            {
                throw new TransformerException( e );
            }

                transformer.transform( src, res );
        }
        catch ( FOPException e )
        {
            throw new TransformerException( e );
        }
        finally
        {
            try
            {
                out.close();
            }
            catch ( IOException e )
            {
                // TODO: log
            }
        }
    }

    /**
     * Returns a base URL to be used by the FOUserAgent.
     *
     * @param fo the FO file.
     * @param resourceDir the resource directory.
     * @return String.
     */
    private static String getBaseURL( File fo, String resourceDir )
    {
        String url = null;

        if ( resourceDir == null )
        {
            url = "file:///" + fo.getParent() + "/";
        }
        else
        {
            url = "file:///" + resourceDir + "/";
        }

        return url;
    }

}
