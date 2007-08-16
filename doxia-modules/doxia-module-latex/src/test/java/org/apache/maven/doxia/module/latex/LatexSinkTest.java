package org.apache.maven.doxia.module.latex;

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

import org.apache.maven.doxia.module.latex.LatexSink;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.AbstractSinkTest;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class LatexSinkTest
    extends AbstractSinkTest
{
    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "tex";
    }

    /** {@inheritDoc} */
    protected Sink createSink( Writer writer )
    {
        return new LatexSink( writer );
    }

    /** {@inheritDoc} */
    protected String getTitleBlock( String title )
    {
        return "\\ptitle{" + title + "}";
    }

    /** {@inheritDoc} */
    protected String getAuthorBlock( String author )
    {
        return "\\pauthor{" + author + "}";
    }

    /** {@inheritDoc} */
    protected String getDateBlock( String date )
    {
        return "\\pdate{" + date + "}";
    }

    /** {@inheritDoc} */
    protected String getHeadBlock()
    {
        return LatexSink.defaultSinkCommands()
            + "\\documentclass[a4paper]{article}"
            + LatexSink.defaultPreamble()
            + "\\begin{document}";
    }

    /** {@inheritDoc} */
    protected String getBodyBlock()
    {
        return "\\end{document}";
    }

    /** {@inheritDoc} */
    protected String getSectionTitleBlock( String title )
    {
        // TODO: closing bracket?
        return title + "}";
    }

    /** {@inheritDoc} */
    protected String getSection1Block( String title )
    {
        return "\\psectioni{" + title + "}";
    }

    /** {@inheritDoc} */
    protected String getSection2Block( String title )
    {
        return "\\psectionii{" + title + "}";
    }

    /** {@inheritDoc} */
    protected String getSection3Block( String title )
    {
        return "\\psectioniii{" + title + "}";
    }

    /** {@inheritDoc} */
    protected String getSection4Block( String title )
    {
        return "\\psectioniv{" + title + "}";
    }

    /** {@inheritDoc} */
    protected String getSection5Block( String title )
    {
        return "\\psectionv{" + title + "}";
    }

    /** {@inheritDoc} */
    protected String getListBlock( String item )
    {
        return "\\begin{plist}\\item{} " + item + "\\end{plist}";
    }

    /** {@inheritDoc} */
    protected String getNumberedListBlock( String item )
    {
        return "\\begin{pnumberedlist}\\renewcommand{\\theenumi}{\\roman{enumi}}\\item{} " + item + "\\end{pnumberedlist}";
    }

    /** {@inheritDoc} */
    protected String getDefinitionListBlock( String definum, String definition )
    {
        return "\\begin{pdefinitionlist}\\item[\\mbox{" + definum + "}] " + definition + "\\end{pdefinitionlist}";
    }

    /** {@inheritDoc} */
    protected String getFigureBlock( String source, String caption )
    {
        return "\\begin{pfigure}\\pfiguregraphics{" + source + "}\\pfigurecaption{" + caption + "}\\end{pfigure}";
    }

    /** {@inheritDoc} */
    protected String getTableBlock( String cell, String caption )
    {
        // TODO: something's wrong
        return "\\begin{ptable}\\begin{ptablerows}{c}\\begin{pcell}{c}cell\\end{pcell}\\\\\\end{ptablerows}\\ptablecaption{Table caption}\\end{ptable}";
    }

    /** {@inheritDoc} */
    protected String getParagraphBlock( String text )
    {
        return text;
    }

    /** {@inheritDoc} */
    protected String getVerbatimBlock( String text )
    {
        return "\\begin{pverbatimbox}\\begin{verbatim}" + text + "\\end{verbatim}\\end{pverbatimbox}";
    }

    /** {@inheritDoc} */
    protected String getHorizontalRuleBlock()
    {
        return "\\phorizontalrule";
    }

    /** {@inheritDoc} */
    protected String getPageBreakBlock()
    {
        return "\\newpage";
    }

    /** {@inheritDoc} */
    protected String getAnchorBlock( String anchor )
    {
        return "\\panchor{" + anchor + "}";
    }

    /** {@inheritDoc} */
    protected String getLinkBlock( String link, String text )
    {
        return "\\plink{" + text + "}";
    }

    /** {@inheritDoc} */
    protected String getItalicBlock( String text )
    {
        return "\\pitalic{" + text + "}";
    }

    /** {@inheritDoc} */
    protected String getBoldBlock( String text )
    {
        return "\\pbold{" + text + "}";
    }

    /** {@inheritDoc} */
    protected String getMonospacedBlock( String text )
    {
        return "\\pmonospaced{" + text + "}";
    }

    /** {@inheritDoc} */
    protected String getLineBreakBlock()
    {
        return "\\newline";
    }

    /** {@inheritDoc} */
    protected String getNonBreakingSpaceBlock()
    {
        return "~";
    }

    /** {@inheritDoc} */
    protected String getTextBlock( String text )
    {
        // TODO: how to retrieve those outside the sink?
        return "\\textasciitilde , =, \\symbol{45}, +, *, [, ], \\symbol{60}, \\symbol{62}, \\{,\\}, \\textbackslash";
    }

    /** {@inheritDoc} */
    protected String getRawTextBlock( String text )
    {
        // TODO: not implemented
        return "";
    }
}
