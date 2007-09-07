package org.apache.maven.doxia.book.context;

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

import org.apache.maven.doxia.book.model.BookModel;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

/**
 * Context to render a book.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class BookContext
{
    /** The BookModel of this context. */
    private BookModel book;

    /** The files. */
    private Map files;

    /** The output directory. */
    private File outputDirectory;

    /** The BookIndex of this context. */
    private BookIndex index;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /** Represents a BookFile. */
    public static class BookFile
    {
        /** The file. */
        private File file;

        /** The id of the parser. */
        private String parserId;

        /**
         * Constructor.
         *
         * @param file the file.
         * @param parserId the parser id.
         */
        public BookFile( File file, String parserId )
        {
            this.file = file;
            this.parserId = parserId;
        }

        /**
         * Return the file of this BookFile.
         *
         * @return File.
         */
        public File getFile()
        {
            return file;
        }

        /**
         * Return the parserId of this BookFile.
         *
         * @return String.
         */
        public String getParserId()
        {
            return parserId;
        }
    }

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    /**
     * Return the BookModel of this BookContext.
     *
     * @return BookModel.
     */
    public BookModel getBook()
    {
        return book;
    }

    /**
     * Set the BookModel of this BookContext.
     *
     * @param book the BookModel.
     */
    public void setBook( BookModel book )
    {
        this.book = book;
    }

    /**
     * Return the files of this BookContext.
     *
     * @return Map. A new HashMap is constructed if the current Map is null.
     */
    public Map getFiles()
    {
        if ( files == null )
        {
            files = new HashMap();
        }

        return files;
    }

    /**
     * Set the files of this BookContext.
     *
     * @param files the Map of files.
     */
    public void setFiles( Map files )
    {
        this.files = files;
    }

    /**
     * Return the outputDirectory of this BookContext.
     *
     * @return File.
     */
    public File getOutputDirectory()
    {
        return outputDirectory;
    }

    /**
     * Set the outputDirectory of this BookContext.
     *
     * @param outputDirectory the output directory.
     */
    public void setOutputDirectory( File outputDirectory )
    {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Return the index of this BookContext.
     *
     * @return BookIndex.
     */
    public BookIndex getIndex()
    {
        return index;
    }

    /**
     * Set the index of this BookContext.
     *
     * @param index the index to set.
     */
    public void setIndex( BookIndex index )
    {
        this.index = index;
    }
}
