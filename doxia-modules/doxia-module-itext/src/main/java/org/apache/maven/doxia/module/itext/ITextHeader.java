package org.apache.maven.doxia.module.itext;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

/**
 * Header object containing meta-informations.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class ITextHeader
{
    private String title;

    private StringBuffer authors;

    private Date date;

    /**
     * Default constructor
     */
    public ITextHeader()
    {
        // nop
    }

    /**
     * Add a title to the Document
     *
     * @param title1 the title.
     */
    public final void setTitle( String title1 )
    {
        this.title = title1;
    }

    /**
     * Get the title
     *
     * @return title as String
     */
    public String getTitle()
    {
        if ( this.title == null )
        {
            return "";
        }

        return this.title;
    }

    /**
     * Add a new author
     *
     * @param author the author.
     */
    public void addAuthor( String author )
    {
        if ( this.authors == null )
        {
            this.authors = new StringBuffer();
        }
        else
        {
            this.authors.append( ", " );
        }

        this.authors.append( author );
    }

    /**
     * Get the authors
     *
     * @return the authors as String
     */
    public String getAuthors()
    {
        if ( ( this.authors == null ) || ( this.authors.length() == 0 ) )
        {
            return System.getProperty( "user.name" );
        }

        return this.authors.toString();
    }

    /**
     * Add a date to the document
     *
     * @param date1 a date as String
     */
    public void setDate( String date1 )
    {
        try
        {
            this.date = new SimpleDateFormat().parse( date1 );
        }
        catch ( ParseException e )
        {
            this.date = new Date();
        }
    }

    /**
     * Get the date of the document
     *
     * @return the date as String
     */
    public String getDate()
    {
        if ( this.date == null )
        {
            return new Date( System.currentTimeMillis() ).toString();
        }

        return this.date.toString();
    }
}
