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

import org.apache.maven.doxia.book.model.BookModel;
import org.apache.maven.doxia.book.BookDoxiaException;
import org.apache.maven.doxia.book.context.BookContext;

import java.io.File;
import java.util.List;

/**
 * Common book-related IO methods.
 *
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface BookIo
{
    /** The plexus lookup role. */
    String ROLE = BookIo.class.getName();

    /**
     * Read a BookModel from a descriptor file.
     *
     * @param bookDescriptor the book descriptor file.
     * @return BookModel
     * @throws org.apache.maven.doxia.book.BookDoxiaException if the model cannot be read.
     */
    BookModel readBook( File bookDescriptor )
        throws BookDoxiaException;

    /**
     * Loads files in a given context.
     *
     * @param context the BookContext.
     * @param files a list of files.
     */
    void loadFiles( BookContext context, List<File> files );
}
