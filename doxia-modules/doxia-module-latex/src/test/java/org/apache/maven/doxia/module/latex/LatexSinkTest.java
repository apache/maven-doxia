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
        return "\\ptitle{" + LatexSink.escaped( title ) + "}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getAuthorBlock( String author )
    {
        return "\\pauthor{" + LatexSink.escaped( author ) + "}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getDateBlock( String date )
    {
        return "\\pdate{" + LatexSink.escaped( date ) + "}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getHeadBlock()
    {
        return ((LatexSink) getSink()).defaultSinkCommands()
            + "\\documentclass[a4paper]{article}" + EOL + EOL
            + ((LatexSink) getSink()).defaultPreamble()
            + "\\begin{document}" + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getBodyBlock()
    {
        return "\\end{document}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getSectionTitleBlock( String title )
    {
        // TODO: closing bracket?
        return title + "}" + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection1Block( String title )
    {
        return "\\psectioni{" + title + "}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection2Block( String title )
    {
        return "\\psectionii{" + title + "}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection3Block( String title )
    {
        return "\\psectioniii{" + title + "}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection4Block( String title )
    {
        return "\\psectioniv{" + title + "}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection5Block( String title )
    {
        return "\\psectionv{" + title + "}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getListBlock( String item )
    {
        return "\\begin{plist}" + EOL + EOL + "\\item{} " + LatexSink.escaped( item ) + "\\end{plist}" + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getNumberedListBlock( String item )
    {
        return "\\begin{pnumberedlist}" + EOL + "\\renewcommand{\\theenumi}{\\roman{enumi}}" + EOL + EOL + "\\item{} "
            + LatexSink.escaped( item ) + "\\end{pnumberedlist}" + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getDefinitionListBlock( String definum, String definition )
    {
        return "\\begin{pdefinitionlist}" + EOL + EOL + "\\item[\\mbox{" + definum + "}] " + definition + "\\end{pdefinitionlist}" + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getFigureBlock( String source, String caption )
    {
        return "\\begin{pfigure}" + EOL + "\\pfiguregraphics{" + source + "}" + EOL + "\\pfigurecaption{"
            + LatexSink.escaped( caption ) + "}" + EOL + "\\end{pfigure}" + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getTableBlock( String cell, String caption )
    {
        // TODO: something's wrong
        return "\\begin{ptable}" + EOL + "\\begin{ptablerows}{c}" + EOL + "\\begin{pcell}{c}cell\\end{pcell}\\\\"
            + EOL + "\\end{ptablerows}" + EOL + "\\ptablecaption{" + LatexSink.escaped( caption ) + "}"
            + EOL + "\\end{ptable}" + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getParagraphBlock( String text )
    {
        return text + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getVerbatimBlock( String text )
    {
        return "\\begin{pverbatimbox}" + EOL + "\\begin{verbatim}" + EOL + text + EOL + "\\end{verbatim}" + EOL + "\\end{pverbatimbox}" + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getHorizontalRuleBlock()
    {
        return "\\phorizontalrule" + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getPageBreakBlock()
    {
        return "\\newpage" + EOL + EOL;
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
        return "\\newline" + EOL;
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
        return "\\textasciitilde" + EOL + ",\\_=,\\_\\symbol{45},\\_+,\\_*,\\_[,\\_],\\_\\symbol{60},\\_\\symbol{62},\\_\\{,\\_\\},\\_\\textbackslash";
    }

    /** {@inheritDoc} */
    protected String getRawTextBlock( String text )
    {
        // TODO: not implemented
        return "";
    }
}
