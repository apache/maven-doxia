package org.apache.maven.doxia.module.xdoc;

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
import java.io.StringReader;
import java.io.StringWriter;

import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;
import org.codehaus.plexus.util.xml.XmlUtil;

/**
 * A Doxia Sink which produces an xdoc document.
 *
 * @author juan <a href="mailto:james@jamestaylor.org">James Taylor</a>
 * @author Juan F. Codagnone  (replaced println with XmlWriterXdocSink)
 * @version $Id$
 * @deprecated Since 1.1, this sink is not more supported. If you are looking for a <code>Sink</code> which produces
 * pretty formatted XML, you could use the {@link XdocSink#XdocSink(java.io.Writer)} as usual and reformat the
 * <code>Sink</code> content produced with {@link XmlUtil#prettyFormat(java.io.Reader, java.io.Writer)}.
 */
public class XmlWriterXdocSink
    extends XdocSink
{
    /** Writer used by Xdoc */
    private StringWriter xdocWriter;

    private XMLWriter xmlWriter;

    private XmlWriterXdocSink( StringWriter out, String encoding )
    {
        super( out, encoding );
        this.xdocWriter = out;
        this.xmlWriter = new PrettyPrintXMLWriter( out );
    }

    /**
     * @param out the wanted XML Writer.
     * @deprecated since 1.1
     */
    public XmlWriterXdocSink( XMLWriter out )
    {
        this( new StringWriter(), "UTF-8" );
        this.xmlWriter = out;
    }

    /** {@inheritDoc} */
    public void close()
    {
        super.close();

        String xdocContent = xdocWriter.toString();
        if ( getLog().isDebugEnabled() )
        {
            getLog().error( "Xdoc content: " + xdocContent );
        }
        StringWriter formattedContent = new StringWriter();
        try
        {
            XmlUtil.prettyFormat( new StringReader( xdocContent ), formattedContent );
        }
        catch ( IOException e )
        {
            if ( getLog().isDebugEnabled() )
            {
                getLog().error( "IOException: " + e.getMessage(), e );
            }
            formattedContent = new StringWriter();
            formattedContent.write( xdocContent );
        }
        xmlWriter.writeMarkup( formattedContent.toString() );
    }
}
