package org.apache.maven.doxia.book.services.io;

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

import org.apache.maven.doxia.book.model.io.xpp3.BookModelXpp3Reader;
import org.apache.maven.doxia.book.model.BookModel;
import org.apache.maven.doxia.book.BookDoxiaException;
import org.apache.maven.doxia.book.context.BookContext;
import org.apache.maven.doxia.module.site.SiteModule;
import org.apache.maven.doxia.module.site.manager.SiteModuleManager;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;

/**
 * @plexus.component
 *
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultBookIo
    extends AbstractLogEnabled
    implements BookIo
{
    /**
     * @plexus.requirement
     */
    private SiteModuleManager siteModuleManager;

    // -----------------------------------------------------------------------
    // DefaultBookIo Implementation
    // -----------------------------------------------------------------------

    /** {@inheritDoc} */
    public BookModel readBook( File bookDescriptor )
        throws BookDoxiaException
    {
        try
        {
            return new BookModelXpp3Reader().read( new FileReader( bookDescriptor ), true );
        }
        catch ( IOException e )
        {
            throw new BookDoxiaException( "Error while reading book descriptor.", e );
        }
        catch ( XmlPullParserException e )
        {
            throw new BookDoxiaException( "Error while reading book descriptor.", e );
        }
    }

    /** {@inheritDoc} */
    public void loadFiles( BookContext context, List files )
    {
        // ----------------------------------------------------------------------
        // Find all the files, map the file names to ids
        // ----------------------------------------------------------------------

        Collection siteModules = siteModuleManager.getSiteModules();

        for ( Iterator it = siteModules.iterator(); it.hasNext(); )
        {
            SiteModule siteModule = (SiteModule) it.next();

            String extension = siteModule.getExtension();

            for ( Iterator j = files.iterator(); j.hasNext(); )
            {
                File file = (File) j.next();

                String name = file.getName();

                if ( name.endsWith( extension ) )
                {
                    name = name.substring( 0, name.length() - siteModule.getExtension().length() - 1 );

                    BookContext.BookFile bookFile = new BookContext.BookFile( file, siteModule.getParserId() );

                    context.getFiles().put( name, bookFile );
                }
            }
        }

        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Dumping document <-> id mapping:" );

            Map map = new TreeMap( context.getFiles() );

            for ( Iterator it = map.entrySet().iterator(); it.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) it.next();

                BookContext.BookFile file = (BookContext.BookFile) entry.getValue();

                getLogger().debug( " " + entry.getKey() + "=" + file.getFile() + ", parser: " + file.getParserId() );
            }
        }
    }
}
