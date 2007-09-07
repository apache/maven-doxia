package org.apache.maven.doxia.book.services.renderer.latex;

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

import org.apache.maven.doxia.module.latex.LatexSink;

import java.io.Writer;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class LatexBookSink
    extends LatexSink
{
    /** text. */
    private String text;

    /** title. */
    private String title;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Construct a new LatexBookSink which is a LatexSink with the given writer,
     * null sinkCommands, null preamble and fragmentDocument = true.
     *
     * @param out the writer for the sink.
     */
    public LatexBookSink( Writer out )
    {
        super( out, null, null, true );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    protected String getDocumentStart()
    {
        return "";
//        return "\\documentclass{book}";
    }

    /** {@inheritDoc} */
    protected String getDocumentBegin()
    {
        return null;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void text( String newText )
    {
        this.text = newText;

        super.text( text );
    }

    /** {@inheritDoc} */
    public void title_()
    {
        super.title_();

        this.title = text;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Return the title.
     *
     * @return String.
     */
    public String getTitle()
    {
        return title;
    }
}
