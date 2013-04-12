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
    protected boolean isXmlSink()
    {
        return false;
    }

    /** {@inheritDoc} */
    protected String getTitleBlock( String title )
    {
        return "\\title{" + LatexSink.escaped( title ) + "}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getAuthorBlock( String author )
    {
        return "\\author{" + LatexSink.escaped( author ) + "}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getDateBlock( String date )
    {
        return "\\date{" + LatexSink.escaped( date ) + "}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getHeadBlock()
    {
        return ( (LatexSink) getSink() ).defaultSinkCommands()
            + "\\documentclass[a4paper]{article}" + EOL + EOL
            + ( (LatexSink) getSink() ).defaultPreamble()
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
        return title;
    }

    /** {@inheritDoc} */
    protected String getSection1Block( String title )
    {
        return EOL + "\\section{" + title + "}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection2Block( String title )
    {
        return EOL + "\\subsection{" + title + "}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection3Block( String title )
    {
        return EOL + "\\subsubsection{" + title + "}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection4Block( String title )
    {
        return EOL + "\\paragraph{" + title + "}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection5Block( String title )
    {
        return EOL + "\\subparagraph{" + title + "}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getListBlock( String item )
    {
        return EOL + "\\begin{itemize}" + EOL + "\\item " + LatexSink.escaped( item ) + EOL + "\\end{itemize}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getNumberedListBlock( String item )
    {
        return EOL + "\\begin{enumerate}" + EOL + "\\renewcommand{\\theenumi}{\\roman{enumi}}" + EOL + "\\item "
            + LatexSink.escaped( item ) + EOL + "\\end{enumerate}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getDefinitionListBlock( String definum, String definition )
    {
        return EOL + "\\begin{description}" + EOL + "\\item[\\mbox{" + definum + "}] "
                + definition + EOL + "\\end{description}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getFigureBlock( String source, String caption )
    {
        String figureBlock = EOL + "\\begin{figure}[htb]" + EOL + "\\begin{center}" + EOL + "\\includegraphics{" + source + "}" + EOL
            + "\\end{center}" + EOL;
        if (caption != null )
        {
            figureBlock += "\\caption{Figure\\_caption}" + EOL;
        }
        figureBlock += "\\end{figure}" + EOL;
        return figureBlock;        
    }

    /** {@inheritDoc} */
    protected String getTableBlock( String cell, String caption )
    {
        return EOL + "\\begin{table}[htp]" + EOL + "\\begin{center}" + EOL + "\\begin{tabular}{c}" + EOL
            + "\\begin{tabular}[t]{c}cell\\end{tabular}\\\\" + EOL + "\\end{tabular}" + EOL
            + "\\end{center}" + EOL + "\\caption{Table\\_caption}" + EOL + "\\end{table}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getParagraphBlock( String text )
    {
        return  EOL + EOL + text + EOL;
    }

    /** {@inheritDoc} */
    protected String getVerbatimBlock( String text )
    {
        return EOL + "\\begin{small}" + EOL + "\\begin{Verbatim}[frame=single]" + EOL + text + EOL
            + "\\end{Verbatim}" + EOL + "\\end{small}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getHorizontalRuleBlock()
    {
        return EOL + "\\begin{center}\\rule[0.5ex]{\\linewidth}{1pt}\\end{center}" + EOL;
    }

    /** {@inheritDoc} */
    protected String getPageBreakBlock()
    {
        return EOL + "\\newpage" + EOL;
    }

    /** {@inheritDoc} */
    protected String getAnchorBlock( String anchor )
    {
        return "\\hypertarget{" + anchor + "}{" + anchor + "}";
    }

    /** {@inheritDoc} */
    protected String getLinkBlock( String link, String text )
    {
        return "\\hyperlink{" + link + "}{" + text + "}";
    }

    /** {@inheritDoc} */
    protected String getItalicBlock( String text )
    {
        return "\\textit{" + text + "}";
    }

    /** {@inheritDoc} */
    protected String getBoldBlock( String text )
    {
        return "\\textbf{" + text + "}";
    }

    /** {@inheritDoc} */
    protected String getMonospacedBlock( String text )
    {
        return "\\texttt{\\small " + text + "}";
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
        return "\\textasciitilde" + EOL + ",\\_=,\\_\\symbol{45},\\_+,\\_*,\\_[,\\_],"
                + "\\_\\symbol{60},\\_\\symbol{62},\\_\\{,\\_\\},\\_\\textbackslash";
    }

    /** {@inheritDoc} */
    protected String getRawTextBlock( String text )
    {
        return "~,_=,_-,_+,_*,_[,_],_<,_>,_{,_},_\\";
    }

    /** {@inheritDoc} */
    protected String getCommentBlock( String text )
    {
        return EOL + "% Simple comment with ----";
    }
}
