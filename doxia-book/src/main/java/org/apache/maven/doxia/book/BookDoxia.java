package org.apache.maven.doxia.book;

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
import java.util.List;

/**
 * An interface to create books in different output formats from a book descriptor.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface BookDoxia
{
    /** The plexus lookup role. */
    String ROLE = BookDoxia.class.getName();

    /**
     * Load a BookModel from a descriptor file.
     *
     * @param bookDescriptor the book descriptor file.
     * @return BookModel
     * @throws BookDoxiaException if the model cannot be loaded. 
     */
    BookModel loadBook( File bookDescriptor )
        throws BookDoxiaException;

    /**
     * Creates a book from a BookModel.
     *
     * @param book the BookModel.
     * @param bookRendererId the id of the output format.
     * @param files a list of source files.
     * @param outputDirectory the output directory.
     * @throws BookDoxiaException if the model cannot be loaded.
     */
    void renderBook( BookModel book, String bookRendererId, List files, File outputDirectory )
        throws BookDoxiaException;
}
