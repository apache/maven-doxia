package org.apache.maven.doxia.macro.toc;

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

import javax.inject.Named;
import javax.inject.Singleton;

import java.io.StringReader;

import org.apache.maven.doxia.index.IndexEntry;
import org.apache.maven.doxia.index.IndexingSink;
import org.apache.maven.doxia.macro.AbstractMacro;
import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.macro.MacroRequest;
import org.apache.maven.doxia.util.HtmlTools;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;

import org.codehaus.plexus.util.StringUtils;

/**
 * Macro to display a <code>Table Of Content</code> in a given <code>Sink</code>.
 * The input parameters for this macro are:
 * <dl>
 * <dt>section</dt>
 * <dd>Display a TOC for the specified section only, or all sections if 0.<br>
 * Positive int, not mandatory, 0 by default.</dd>
 * <dt>fromDepth</dt>
 * <dd>Minimal depth of entries to display in the TOC.
 * Sections are depth 1, sub-sections depth 2, etc.<br>
 * Positive int, not mandatory, 0 by default.</dd>
 * <dt>toDepth</dt>
 * <dd>Maximum depth of entries to display in the TOC.<br>
 * Positive int, not mandatory, 5 by default.</dd>
 * </dl>
 * For instance, in an APT file, you could write:
 * <dl>
 * <dt>%{toc|section=2|fromDepth=2|toDepth=3}</dt>
 * <dd>Display a TOC for the second section in the document, including all
 * subsections (depth 2) and  sub-subsections (depth 3).</dd>
 * <dt>%{toc}</dt>
 * <dd>display a TOC with all section and subsections
 * (similar to %{toc|section=0} )</dd>
 * </dl>
 * Moreover, you need to write APT link for section to allow anchor,
 * for instance:
 * <pre>
 * * {SubSection 1}
 * </pre>
 *
 * Similarly, in an XDOC file, you could write:
 * <pre>
 * &lt;macro name="toc"&gt;
 *   &lt;param name="section" value="1" /&gt;
 *   &lt;param name="fromDepth" value="1" /&gt;
 *   &lt;param name="toDepth" value="2" /&gt;
 * &lt;/macro&gt;
 * </pre>
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 */
@Singleton
@Named( "toc" )
public class TocMacro
    extends AbstractMacro
{
    /** The section to display. */
    private int section;

    /** Start depth. */
    private int fromDepth;

    /** End depth. */
    private int toDepth;

    /** The default end depth. */
    private static final int DEFAULT_DEPTH = 5;

    /** {@inheritDoc} */
    public void execute( Sink sink, MacroRequest request )
        throws MacroExecutionException
    {
        String source = request.getSourceContent();
        Parser parser = request.getParser();

        section = getInt( request, "section", 0 );
        fromDepth = getInt( request, "fromDepth", 0 );
        toDepth = getInt( request, "toDepth", DEFAULT_DEPTH );

        if ( fromDepth > toDepth )
        {
            return;
        }

        IndexEntry index = new IndexEntry( "index" );
        IndexingSink tocSink = new IndexingSink( index );

        try
        {
            parser.parse( new StringReader( source ), tocSink );
        }
        catch ( ParseException e )
        {
            throw new MacroExecutionException( e );
        }

        if ( index.getChildEntries().size() > 0 )
        {
            sink.list( getAttributesFromMap( request.getParameters() ) );

            int i = 1;

            for ( IndexEntry sectionIndex : index.getChildEntries() )
            {
                if ( ( i == section ) || ( section == 0 ) )
                {
                    writeSubSectionN( sink, sectionIndex, 1 );
                }

                i++;
            }

            sink.list_();
        }
    }

    /**
     * @param sink The sink to write to.
     * @param sectionIndex The section index.
     * @param n The toc depth.
     */
    private void writeSubSectionN( Sink sink, IndexEntry sectionIndex, int n )
    {
        if ( fromDepth <= n )
        {
            sink.listItem();
            sink.link( "#" + HtmlTools.encodeId( sectionIndex.getId() ) );
            sink.text( sectionIndex.getTitle() );
            sink.link_();
        }

        if ( toDepth > n )
        {
            if ( sectionIndex.getChildEntries().size() > 0 )
            {
                if ( fromDepth <= n )
                {
                    sink.list();
                }

                for ( IndexEntry subsectionIndex : sectionIndex.getChildEntries() )
                {
                    if ( n == toDepth - 1 )
                    {
                        sink.listItem();
                        sink.link( "#" + HtmlTools.encodeId( subsectionIndex.getId() ) );
                        sink.text( subsectionIndex.getTitle() );
                        sink.link_();
                        sink.listItem_();
                    }
                    else
                    {
                        writeSubSectionN( sink, subsectionIndex, n + 1 );
                    }
                }

                if ( fromDepth <= n )
                {
                    sink.list_();
                }
            }
        }

        if ( fromDepth <= n )
        {
            sink.listItem_();
        }
    }

    /**
     * @param request The MacroRequest.
     * @param parameter The parameter.
     * @param defaultValue the default value.
     * @return the int value of a parameter in the request.
     * @throws MacroExecutionException if something goes wrong.
     */
    private static int getInt( MacroRequest request, String parameter, int defaultValue )
        throws MacroExecutionException
    {
        String value = (String) request.getParameter( parameter );

        if ( StringUtils.isEmpty( value ) )
        {
            return defaultValue;
        }

        int i;

        try
        {
            i = Integer.parseInt( value );
        }
        catch ( NumberFormatException e )
        {
            return defaultValue;
        }

        if ( i < 0 )
        {
            throw new MacroExecutionException( "The " + parameter + "=" + i + " should be positive." );
        }

        return i;
    }
}
