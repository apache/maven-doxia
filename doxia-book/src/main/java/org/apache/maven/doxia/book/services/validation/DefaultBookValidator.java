package org.apache.maven.doxia.book.services.validation;

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

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.StringUtils;
import org.apache.maven.doxia.book.model.BookModel;
import org.apache.maven.doxia.book.model.Chapter;

import java.util.Iterator;

/**
 * Default implementation of BookValidator.
 *
 * @plexus.component
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultBookValidator
    extends AbstractLogEnabled
    implements BookValidator
{
    // ----------------------------------------------------------------------
    // BookValidator Implementation
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    public ValidationResult validateBook( BookModel book )
    {
        ValidationResult result = new ValidationResult();

        if ( StringUtils.isEmpty( book.getId() ) )
        {
            result.getErrors().add( "Book is missing id." );
        }

        if ( StringUtils.isEmpty( book.getTitle() ) )
        {
            result.getErrors().add( "Book is missing title." );
        }

        if ( book.getChapters().size() == 0 )
        {
            result.getErrors().add( "The book must have at least one chaper" );
        }
        else
        {
            for ( Iterator it = book.getChapters().iterator(); it.hasNext(); )
            {
                Chapter chapter = (Chapter) it.next();

                validateChapter( result, chapter );

                // TODO: Validate the chapter id
            }
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    /**
     * Validate a Chapter.
     *
     * @param result the ValidationResult to receive the results.
     * @param chapter the chapter to validate.
     */
    private void validateChapter( ValidationResult result, Chapter chapter )
    {
        if ( StringUtils.isEmpty( chapter.getId() ) )
        {
            result.getErrors().add( "Each chapter has to have an id." );

            return;
        }

        if ( StringUtils.isEmpty( chapter.getTitle() ) )
        {
            result.getErrors().add( "Missing title. Chapter id: " + chapter.getId() );
        }

        if ( chapter.getSections().size() == 0 )
        {
            result.getErrors().add( "Chapter doesn't have any sections. Chapter id: " + chapter.getId() );
        }
    }
}
