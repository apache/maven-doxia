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

import java.io.StringReader;
import java.util.Iterator;

import org.apache.maven.doxia.index.IndexEntry;
import org.apache.maven.doxia.index.IndexingSink;
import org.apache.maven.doxia.macro.AbstractMacro;
import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.macro.MacroRequest;
import org.apache.maven.doxia.module.HtmlTools;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.codehaus.plexus.util.StringUtils;

/**
 * Macro to display a <code>Table Of Content</code> in a given <code>Sink</code>. The input for this macro are:
 * <dl>
 * <dt>section</dt>
 * <dd>Display the specificated section number or all sections if 0 (in this case, other paramaters are ignored).<br/>
 * Positive int, not mandatory, 0 by default.</dd>
 * <dt>fromDepth</dt>
 * <dd>Display the depth starting for the given section number.<br/>
 * Positive int, not mandatory, 0 by default.</dd>
 * <dt>toDepth</dt>
 * <dd>Display the depth ending for the given section number.<br/>
 * Positive int, not mandatory, 5 by default.</dd>
 * </dl>
 * For instance, in an APT file, you could write:
 * <dl>
 * <dt>%{toc|section=2|fromDepth=2|toDepth=2}</dt>
 * <dd>Display a TOC for the section number 2 in the document, from the subsection depth 1 to the subsection depth 2</dd>
 * <dt>%{toc}</dt>
 * <dd>display a TOC with all section and subsections (similar to %{toc|section=0} )</dd>
 * </dl>
 * Moreover, you need to write APT link for section to allow anchor, for instance:
 * <pre>
 * * {SubSection 1}
 * </pre>
 *
 * Similary, in an XDOC file, you could write:
 * <pre>
 * &lt;macro name="toc" section="1" fromDepth="1" toDepth="2"/&gt;
 * </pre>
 *
 * @plexus.component role="org.apache.maven.doxia.macro.Macro"
 * role-hint="toc"
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class TocMacro
    extends AbstractMacro
{
    private int section;

    private int fromDepth;

    private int toDepth;

    /**
     * @see org.apache.maven.doxia.macro.Macro#execute(org.apache.maven.doxia.sink.Sink, org.apache.maven.doxia.macro.MacroRequest)
     */
    public void execute( Sink sink, MacroRequest request )
        throws MacroExecutionException
    {
        String source = (String) request.getParameter( "sourceContent" );
        Parser parser = (Parser) request.getParameter( "parser" );

        section = getInt( request, "section", 0 );
        if ( section != 0 )
        {
            fromDepth = getInt( request, "fromDepth", 0 );
            toDepth = getInt( request, "toDepth", 5 );
        }
        else
        {
            fromDepth = 0;
            toDepth = 5;
        }
        IndexEntry index = new IndexEntry( "index" );
        IndexingSink tocSink = new IndexingSink( index );

        try
        {
            parser.parse( new StringReader( source ), tocSink );

            if ( index.getChildEntries().size() > 0 )
            {
                if ( ( fromDepth < section ) || ( section == 0 ) )
                {
                    sink.list();
                }
                int i = 1;
                for ( Iterator it = index.getChildEntries().iterator(); it.hasNext(); )
                {
                    IndexEntry sectionIndex = (IndexEntry) it.next();
                    if ( ( i == section ) || ( section == 0 ) )
                    {
                        writeSubSectionN( sink, sectionIndex, i );
                    }
                    i++;
                }
                if ( ( fromDepth < section ) || ( section == 0 ) )
                {
                    sink.list_();
                }
            }
        }
        catch ( ParseException e )
        {
            throw new MacroExecutionException( "ParseException: " + e.getMessage(), e );
        }
    }

    /**
     * @param sink
     * @param sectionIndex
     * @param n
     */
    private void writeSubSectionN( Sink sink, IndexEntry sectionIndex, int n )
    {
        if ( fromDepth < n )
        {
            sink.listItem();
            sink.link( "#" + HtmlTools.encodeId( sectionIndex.getId() ) );
            sink.text( sectionIndex.getTitle() );
            sink.link_();
        }

        if ( toDepth >= n )
        {
            if ( sectionIndex.getChildEntries().size() > 0 )
            {
                if ( fromDepth < ( n + 1 ) )
                {
                    sink.list();
                }
                for ( Iterator it = sectionIndex.getChildEntries().iterator(); it.hasNext(); )
                {
                    IndexEntry subsectionIndex = (IndexEntry) it.next();
                    if ( n == 5 )
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
                if ( fromDepth < ( n + 1 ) )
                {
                    sink.list_();
                }
            }
        }

        if ( fromDepth < n )
        {
            sink.listItem_();
        }
    }

    /**
     * @param request
     * @param parameter
     * @param defaultValue
     * @return the int value of an parameter in the request.
     * @throws MacroExecutionException
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
