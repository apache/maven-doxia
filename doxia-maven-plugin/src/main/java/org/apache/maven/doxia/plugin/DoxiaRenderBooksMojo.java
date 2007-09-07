package org.apache.maven.doxia.plugin;

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

import org.apache.maven.doxia.book.BookDoxia;
import org.apache.maven.doxia.book.BookDoxiaException;
import org.apache.maven.doxia.book.InvalidBookDescriptorException;
import org.apache.maven.doxia.book.model.BookModel;
import org.apache.maven.doxia.book.services.validation.ValidationResult;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * A Mojo to create books in different output formats.
 *
 * @goal render-books
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DoxiaRenderBooksMojo
    extends AbstractMojo
{
    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * A list of books.
     *
     * @parameter
     */
    private List books;

    /**
     * Base directory of the project.
     *
     * @parameter expression="${basedir}"
     */
    private File basedir;

    /**
     * Directory containing the generated project docs.
     *
     * @parameter expression="${project.build.directory}/generated-site"
     */
    private File generatedDocs;

    /**
     * BookDoxia component
     *
     * @component
     */
    private BookDoxia bookDoxia;

    /** System EOL. */
    private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Executes the Mojo.
     *
     * @throws MojoExecutionException if an exception occurs during the execution of the plugin.
     * @throws MojoFailureException if there are configuration errors.
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        for ( Iterator it = books.iterator(); it.hasNext(); )
        {
            Book book = (Book) it.next();

            // ----------------------------------------------------------------------
            // Validate
            // ----------------------------------------------------------------------

            if ( StringUtils.isEmpty( book.getDescriptor() ) )
            {
                throw new MojoFailureException( "Invalid configuration: "
                    + "The book is required to have a descriptor set." );
            }

            if ( StringUtils.isEmpty( book.getDirectory() ) )
            {
                throw new MojoFailureException( "Invalid configuration: "
                    + "The book is required to have a directory set." );
            }

            if ( book.getFormats() == null || book.getFormats().size() == 0 )
            {
                throw new MojoFailureException( "Invalid configuration: "
                    + "The book is required to have at least one format set." );
            }

            // ----------------------------------------------------------------------
            //
            // ----------------------------------------------------------------------

            File descriptor = new File( basedir, book.getDescriptor() );

            String includes = "";

            if ( book.getIncludes() != null )
            {
                for ( Iterator j = book.getIncludes().iterator(); j.hasNext(); )
                {
                    String include = (String) j.next();

                    includes += include + ",";
                }
            }
            else
            {
                includes = "**/*";
            }

            String excludes = "";

            if ( book.getExcludes() != null )
            {
                for ( Iterator j = book.getExcludes().iterator(); j.hasNext(); )
                {
                    String exclude = (String) j.next();

                    excludes += exclude + ",";
                }
            }

            // ----------------------------------------------------------------------
            // Find all the files to pass to the renderer.
            // ----------------------------------------------------------------------

            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "Locating files to include in the book:" );
                getLog().debug( "Basedir: " + basedir );
                getLog().debug( "Includes: " + includes );
                getLog().debug( "Excludes: " + excludes );
            }

            List files;

            try
            {
                files = FileUtils.getFiles( new File( basedir, book.getDirectory() ), includes, excludes );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Error while looking for input files. " + "Basedir="
                    + basedir.getAbsolutePath() + ", " + "includes=" + includes + ", " + "excludes=" + excludes, e );
            }

            // -----------------------------------------------------------------------
            // Load the model
            // -----------------------------------------------------------------------

            BookModel bookModel;

            try
            {
                bookModel = bookDoxia.loadBook( descriptor );
            }
            catch ( InvalidBookDescriptorException e )
            {
                throw new MojoFailureException( "Invalid book descriptor: " + LINE_SEPARATOR
                    + formatResult( e.getValidationResult() ) );
            }
            catch ( BookDoxiaException e )
            {
                throw new MojoExecutionException( "Error while loading the book descriptor", e );
            }

            // -----------------------------------------------------------------------
            // Render the book in all the formats
            // -----------------------------------------------------------------------

            for ( Iterator j = book.getFormats().iterator(); j.hasNext(); )
            {
                Format format = (Format) j.next();

                File outputDirectory = new File( generatedDocs, format.getId() );
                File directory = new File( outputDirectory, bookModel.getId() );

                try
                {
                    bookDoxia.renderBook( bookModel, format.getId(), files, directory );
                }
                catch ( BookDoxiaException e )
                {
                    throw new MojoExecutionException(
                                                      "Error while generating book in format '" + format.getId() + "'.",
                                                      e );
                }
            }
        }
    }

    /**
     * Returns a formatted message of a ValidationResult.
     *
     * @param result the ValidationResult to format.
     * @return the formatted result.
     */
    private String formatResult( ValidationResult result )
    {
        StringBuffer buffer = new StringBuffer();

        if ( result.getErrors().size() > 0 )
        {
            buffer.append( "Validation errors:" );

            for ( Iterator it = result.getErrors().iterator(); it.hasNext(); )
            {
                String error = (String) it.next();

                buffer.append( LINE_SEPARATOR ).append( " " ).append( error );
            }
        }

        if ( result.getWarnings().size() > 0 )
        {
            buffer.append( "Validation warnings:" );

            for ( Iterator it = result.getWarnings().iterator(); it.hasNext(); )
            {
                String error = (String) it.next();

                buffer.append( LINE_SEPARATOR ).append( " " ).append( error );
            }
        }

        return buffer.toString();
    }
}
