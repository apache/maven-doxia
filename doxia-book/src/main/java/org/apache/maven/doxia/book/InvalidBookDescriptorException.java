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

import org.apache.maven.doxia.book.services.validation.ValidationResult;

/**
 * Indicates that the book descriptor file could not be parsed correctly.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class InvalidBookDescriptorException
    extends BookDoxiaException
{

    /** serialVersionUID */
    private static final long serialVersionUID = -5706648416915909753L;

    /** ValidationResult. */
    private ValidationResult validationResult;

    /**
     * Construct a new InvalidBookDescriptorException and stores the given ValidationResult.
     *
     * @param validationResult The ValidationResult to store.
     */
    public InvalidBookDescriptorException( ValidationResult validationResult )
    {
        super( "Invalid book descriptor." );

        this.validationResult = validationResult;
    }

    /**
     * Return the ValidationResult.
     *
     * @return the ValidationResult associated with this Exception.
     */
    public ValidationResult getValidationResult()
    {
        return validationResult;
    }
}
