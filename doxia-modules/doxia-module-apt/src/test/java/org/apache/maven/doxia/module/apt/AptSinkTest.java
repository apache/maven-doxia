package org.apache.maven.doxia.module.apt;

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

import org.apache.maven.doxia.sink.AbstractSinkTest;
import org.apache.maven.doxia.sink.Sink;

public class AptSinkTest extends AbstractSinkTest
{
    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "apt";
    }

    /** {@inheritDoc} */
    protected Sink createSink( Writer writer )
    {
        return new AptSink( writer );
    }

    /** {@inheritDoc} */
    protected String getTitleBlock( String title )
    {
        return title;
    }

    /** {@inheritDoc} */
    protected String getAuthorBlock( String author )
    {
        return author;
    }

    /** {@inheritDoc} */
    protected String getDateBlock( String date )
    {
        return date;
    }

    /** {@inheritDoc} */
    protected String getHeadBlock()
    {
        return " ----- null ----- null ----- null -----";
    }

    /** {@inheritDoc} */
    protected String getBodyBlock()
    {
        return "";
    }

    /** {@inheritDoc} */
    protected String getSectionTitleBlock( String title )
    {
        return title;
    }

    /** {@inheritDoc} */
    protected String getSection1Block( String title )
    {
        return title;
    }

    /** {@inheritDoc} */
    protected String getSection2Block( String title )
    {
        return "*" + title;
    }

    /** {@inheritDoc} */
    protected String getSection3Block( String title )
    {
        return "**" + title;
    }

    /** {@inheritDoc} */
    protected String getSection4Block( String title )
    {
        return "***" + title;
    }

    /** {@inheritDoc} */
    protected String getSection5Block( String title )
    {
        return "****" + title;
    }

    /** {@inheritDoc} */
    protected String getListBlock( String item )
    {
        return " * " + item + " []";
    }

    /** {@inheritDoc} */
    protected String getNumberedListBlock( String item )
    {
        return " [[i]] " + item + " []";
    }

    /** {@inheritDoc} */
    protected String getDefinitionListBlock( String definum, String definition )
    {
        return " [" + definum + "]" + definition;
    }

    /** {@inheritDoc} */
    protected String getFigureBlock( String source, String caption )
    {
        return "[" + source + "] " + caption;
    }

    /** {@inheritDoc} */
    protected String getTableBlock( String cell, String caption )
    {
        return "*----*" + cell + "|*----*" + caption;
    }

    /** {@inheritDoc} */
    protected String getParagraphBlock( String text )
    {
        return " " + text;
    }

    /** {@inheritDoc} */
    protected String getVerbatimBlock( String text )
    {
        return "\n+------+\n" + text + "\n+------+\n";
    }

    /** {@inheritDoc} */
    protected String getHorizontalRuleBlock()
    {
        return "========";
    }

    /** {@inheritDoc} */
    protected String getPageBreakBlock()
    {
        return "\f";
    }

    /** {@inheritDoc} */
    protected String getAnchorBlock( String anchor )
    {
        return "{" + anchor + "}";
    }

    /** {@inheritDoc} */
    protected String getLinkBlock( String link, String text )
    {
        return "{{{" + link + "}" + text + "}}";
    }

    /** {@inheritDoc} */
    protected String getItalicBlock( String text )
    {
        return "<" + text + ">";
    }

    /** {@inheritDoc} */
    protected String getBoldBlock( String text )
    {
        return "<<" + text + ">>";
    }

    /** {@inheritDoc} */
    protected String getMonospacedBlock( String text )
    {
        return "<<<" + text + ">>>";
    }

    /** {@inheritDoc} */
    protected String getLineBreakBlock()
    {
        return "\\";
    }

    /** {@inheritDoc} */
    protected String getNonBreakingSpaceBlock()
    {
        return "\\ ";
    }

    /** {@inheritDoc} */
    protected String getTextBlock( String text )
    {
        // TODO: need to be able to retreive those from outside the sink
        return "\\~, \\=, \\-, \\+, \\*, \\[, \\], \\<, \\>, \\{, \\}, \\\\";
    }

    /** {@inheritDoc} */
    protected String getRawTextBlock( String text )
    {
        return text;
    }


}
