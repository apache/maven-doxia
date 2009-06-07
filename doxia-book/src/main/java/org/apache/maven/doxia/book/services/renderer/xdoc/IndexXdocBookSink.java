package org.apache.maven.doxia.book.services.renderer.xdoc;

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
import java.util.Locale;

import org.apache.maven.doxia.index.IndexEntry;
import org.codehaus.plexus.i18n.I18N;

/**
 * A <code>XdocSink</code> implementation for index book.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class IndexXdocBookSink
    extends AbstractXdocBookSink
{
    /** the first IndexEntry. */
    private final IndexEntry firstEntry;

    /**
     * Default constructor.
     *
     * @param out the Writer.
     * @param firstEntry the first IndexEntry.
     * @param i18n I18N.
     * @param locale wanted locale.
     */
    public IndexXdocBookSink( Writer out, IndexEntry firstEntry, I18N i18n, Locale locale )
    {
        super( out, i18n, locale );

        this.firstEntry = firstEntry;
    }

    /** {@inheritDoc} */
    protected void navigationPanel()
    {
        write( "<!--Navigation Panel-->" );

        write( "<table width=\"100%\" align=\"center\">" );
        write( "<tr>" );

        // -----------------------------------------------------------------------
        // Next
        // -----------------------------------------------------------------------

        if ( firstEntry != null )
        {
            write( "<td><div align=\"right\">" );

            write( getString( "next" ) + ": <a href=\"" + firstEntry.getId() + ".html\">" );
            content( firstEntry.getTitle() );
            write( "</a>" );

            write( "</div></td>" );
        }

        write( "</tr>" );
        write( "</table>" );

        write( "<!--End of Navigation Panel-->" );
    }
}
