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
    private IndexEntry firstEntry;

    /**
     * Default constructor.
     *
     * @param out the Writer.
     * @param firstEntry the first IndexEntry.
     * @param i18n I18N.
     */
    public IndexXdocBookSink( Writer out, IndexEntry firstEntry, I18N i18n )
    {
        super( out, i18n );

        this.firstEntry = firstEntry;
    }

    /**
     * @see org.apache.maven.doxia.book.services.renderer.xdoc.AbstractXdocBookSink#navigationPanel()
     */
    protected void navigationPanel()
    {
        markup( "<!--Navigation Panel-->" + EOL );

        markup( "<table width=\"100%\" align=\"center\">" + EOL );
        markup( "<tr>" + EOL );

        // -----------------------------------------------------------------------
        // Next
        // -----------------------------------------------------------------------

        if ( firstEntry != null )
        {
            markup( "<td><div align='right'>" );

            markup( getString( "next" ) + ": <a href='" + firstEntry.getId() + ".html'>" );
            content( firstEntry.getTitle() );
            markup( "</a>" );

            markup( "</div></td>" + EOL );
        }

        markup( "</tr>" + EOL );
        markup( "</table>" + EOL );

        markup( "<!--End of Navigation Panel-->" + EOL );
    }
}
