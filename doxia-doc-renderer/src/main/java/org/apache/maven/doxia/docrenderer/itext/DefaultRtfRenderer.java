package org.apache.maven.doxia.docrenderer.itext;

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
import java.io.IOException;

import org.apache.maven.doxia.docrenderer.DocRendererException;
import org.apache.maven.doxia.module.itext.ITextUtil;

/**
 * RTF render with the <code>iText</code> framework
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @plexus.component role="org.apache.maven.doxia.docrenderer.RtfRenderer"
 */
public class DefaultRtfRenderer
    extends AbstractITextRender
    implements RtfRenderer
{
    /**
     * @see org.apache.maven.doxia.docrenderer.DocRenderer#getOutputExtension()
     */
    public String getOutputExtension()
    {
        return "rtf";
    }

    /**
     * @see org.apache.maven.doxia.docrenderer.itext.AbstractITextRender#generateOutput(java.io.File, java.io.File)
     */
    public void generateOutput( File iTextFile, File iTextOutput )
        throws DocRendererException, IOException
    {
        getLogger().debug( "Writing : " + iTextOutput );
        try
        {
            ITextUtil.writeRtf( new FileInputStream( iTextFile ), new FileOutputStream( iTextOutput ) );
        }
        catch ( RuntimeException e )
        {
            throw new DocRendererException( "Error writing RTF from " + iTextOutput + ": " + e.getMessage() );
        }
    }
}
