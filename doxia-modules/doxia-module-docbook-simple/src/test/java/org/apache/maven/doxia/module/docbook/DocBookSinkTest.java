package org.apache.maven.doxia.module.docbook;

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

import org.apache.maven.doxia.sink.AbstractSinkTest;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.parser.Parser;

import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DocBookSinkTest extends AbstractSinkTest
{
    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "docbook";
    }

    /** {@inheritDoc} */
    protected Sink createSink( Writer writer )
    {
        return new DocBookSink( writer );
    }

    /** {@inheritDoc} */
    protected String getTitleBlock( String title )
    {
        return "<articleinfo><title>" + title + "</title>";
    }

    /** {@inheritDoc} */
    protected String getAuthorBlock( String author )
    {
        return "<corpauthor>" + author + "</corpauthor>";
    }

    /** {@inheritDoc} */
    protected String getDateBlock( String date )
    {
        return "<date>" + date + "</date>";
    }

    /** {@inheritDoc} */
    protected String getHeadBlock()
    {
        return "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook V4.1//EN\"><article>";
    }

    /** {@inheritDoc} */
    protected String getBodyBlock()
    {
        return "</article>";
    }

    /** {@inheritDoc} */
    protected String getSectionTitleBlock( String title )
    {
        return "<title>" + title + "</title>";
    }

    /** {@inheritDoc} */
    protected String getSection1Block( String title )
    {
        return "<section>" + title + "</section>";
    }

    /** {@inheritDoc} */
    protected String getSection2Block( String title )
    {
        return "<section>" + title + "</section>";
    }

    /** {@inheritDoc} */
    protected String getSection3Block( String title )
    {
        return "<section>" + title + "</section>";
    }

    /** {@inheritDoc} */
    protected String getSection4Block( String title )
    {
        return "<section>" + title + "</section>";
    }

    /** {@inheritDoc} */
    protected String getSection5Block( String title )
    {
        return "<section>" + title + "</section>";
    }

    /** {@inheritDoc} */
    protected String getListBlock( String item )
    {
        return "<itemizedlist><listitem>" + item  + "</listitem></itemizedlist>";
    }

    /** {@inheritDoc} */
    protected String getNumberedListBlock( String item )
    {
        return "<orderedlist numeration=\"lowerroman\"><listitem>"
            + item  + "</listitem></orderedlist>";
    }

    /** {@inheritDoc} */
    protected String getDefinitionListBlock( String definum, String definition )
    {
        return "<variablelist><varlistentry><term>" + definum
            + "</term><listitem>" + definition
            + "</listitem></varlistentry></variablelist>";
    }

    /** {@inheritDoc} */
    protected String getFigureBlock( String source, String caption )
    {
        // TODO: fix source
        return "<figure><title>" + caption + "</title><mediaobject><imageobject><imagedata fileref=\"figure.jpeg\" format=\"JPEG\"></imagedata></imageobject></mediaobject></figure>";
    }

    /** {@inheritDoc} */
    protected String getTableBlock( String cell, String caption )
    {
        return "<table frame=\"none\" rowsep=\"0\" colsep=\"0\"><title>" + caption
            + "</title><tgroup cols=\"1\"><colspec align=\"center\"></colspec><tbody><row><entry>"
            + cell  + "</entry></row></tbody></tgroup></table>";
    }

    /** {@inheritDoc} */
    protected String getParagraphBlock( String text )
    {
        return "<para>" + text + "</para>";
    }

    /** {@inheritDoc} */
    protected String getVerbatimBlock( String text )
    {
        return "<programlisting>" + text + "</programlisting>";
    }

    /** {@inheritDoc} */
    protected String getHorizontalRuleBlock()
    {
        return "<!-- HR -->";
    }

    /** {@inheritDoc} */
    protected String getPageBreakBlock()
    {
        return "<!-- PB -->";
    }

    /** {@inheritDoc} */
    protected String getAnchorBlock( String anchor )
    {
        // TODO: fix id
        return "<anchor id=\"a.anchor\">" + anchor + "</anchor>";
    }

    /** {@inheritDoc} */
    protected String getLinkBlock( String link, String text )
    {
        // TODO: fix link
        return "<link linkend=\"a.link\">" + text + "</link>";
    }

    /** {@inheritDoc} */
    protected String getItalicBlock( String text )
    {
        return "<emphasis>" + text + "</emphasis>";
    }

    /** {@inheritDoc} */
    protected String getBoldBlock( String text )
    {
        return "<emphasis role=\"bold\">" + text + "</emphasis>";
    }

    /** {@inheritDoc} */
    protected String getMonospacedBlock( String text )
    {
        return "<literal>" + text + "</literal>";
    }

    /** {@inheritDoc} */
    protected String getLineBreakBlock()
    {
        return "<!-- LB -->";
    }

    /** {@inheritDoc} */
    protected String getNonBreakingSpaceBlock()
    {
        return "&#x00A0;";
    }

    /** {@inheritDoc} */
    protected String getTextBlock( String text )
    {
        // TODO: retreive those from the sink
        return "~, =, -, +, *, [, ], &lt;, &gt;, {, }, \\";
    }

    /** {@inheritDoc} */
    protected String getRawTextBlock( String text )
    {
        // TODO
        return "";
    }


}
