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

import java.io.Writer;

import org.apache.maven.doxia.sink.Sink;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XmlUtil;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.1
 */
public class XmlWriterXdocSinkTest
    extends XdocSinkTest
{
    private static final String DEFAULT_INDENT = StringUtils.repeat( " ", XmlUtil.DEFAULT_INDENTATION_SIZE );

    /** {@inheritDoc} */
    protected Sink createSink( Writer writer )
    {
        return new XmlWriterXdocSink( new PrettyPrintXMLWriter( writer ) );
    }

    /** {@inheritDoc} */
    protected String getSection1Block( String title )
    {
        return "<section name=\"" + title + "\"/>";
    }

    /** {@inheritDoc} */
    protected String getSection2Block( String title )
    {
        return "<subsection name=\"" + title + "\"/>";
    }

    /** {@inheritDoc} */
    protected String getListBlock( String item )
    {
        return "<ul>" + XmlUtil.DEFAULT_LINE_SEPARATOR + DEFAULT_INDENT + "<li>" + item + "</li>"
            + XmlUtil.DEFAULT_LINE_SEPARATOR + "</ul>";
    }

    /** {@inheritDoc} */
    protected String getNumberedListBlock( String item )
    {
        return "<ol style=\"list-style-type: lower-roman\">" + XmlUtil.DEFAULT_LINE_SEPARATOR + DEFAULT_INDENT
            + "<li>" + item + "</li>" + XmlUtil.DEFAULT_LINE_SEPARATOR + "</ol>";
    }

    /** {@inheritDoc} */
    protected String getDefinitionListBlock( String definum, String definition )
    {
        return "<dl>" + XmlUtil.DEFAULT_LINE_SEPARATOR + DEFAULT_INDENT + "<dt>" + definum + "</dt>"
            + XmlUtil.DEFAULT_LINE_SEPARATOR + DEFAULT_INDENT + "<dd>" + definition + "</dd>"
            + XmlUtil.DEFAULT_LINE_SEPARATOR + "</dl>";
    }

    /** {@inheritDoc} */
    protected String getFigureBlock( String source, String caption )
    {
        return "<img src=\"" + source + "\" alt=\"" + caption + "\"/>";
    }

    /** {@inheritDoc} */
    protected String getTableBlock( String cell, String caption )
    {
        return "<table border=\"0\">" + XmlUtil.DEFAULT_LINE_SEPARATOR + DEFAULT_INDENT
            + "<caption>" + caption + "</caption>" + XmlUtil.DEFAULT_LINE_SEPARATOR + DEFAULT_INDENT
            + "<tr valign=\"top\">" + XmlUtil.DEFAULT_LINE_SEPARATOR + DEFAULT_INDENT + DEFAULT_INDENT
            + "<td>" + cell + "</td>" + XmlUtil.DEFAULT_LINE_SEPARATOR + DEFAULT_INDENT + "</tr>"
            + XmlUtil.DEFAULT_LINE_SEPARATOR + "</table>";
    }

    /** {@inheritDoc} */
    protected String getHorizontalRuleBlock()
    {
        return "<hr/>";
    }

    /** {@inheritDoc} */
    protected String getLineBreakBlock()
    {
        return "<br/>";
    }
}
