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
import org.apache.maven.doxia.tools.SiteTool;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A Mojo to create books in different output formats.
 *
 * @goal render-books
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @since 1.0
 */
public class DoxiaRenderBooksMojo
    extends AbstractMojo
{
    /** System EOL. */
    private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );

    // ----------------------------------------------------------------------
    // Mojo components
    // ----------------------------------------------------------------------

    /**
     * BookDoxia component
     *
     * @component
     */
    private BookDoxia bookDoxia;

    /**
     * SiteTool.
     *
     * @component
     */
    protected SiteTool siteTool;

    // ----------------------------------------------------------------------
    // Mojo parameters
    // ----------------------------------------------------------------------

    /**
     * A list of books.
     *
     * @parameter
     * @required
     */
    private List<Book> books;

    /**
     * Base directory of the project.
     *
     * @parameter default-value="${basedir}"
     */
    private File basedir;

    /**
     * Directory containing the generated project docs.
     *
     * @parameter default-value="${project.build.directory}/generated-site"
     */
    private File generatedDocs;

    /**
     * A comma separated list of locales supported by Maven. The first valid token will be the default Locale
     * for this instance of the Java Virtual Machine.
     *
     * @parameter default-value="${locales}"
     */
    protected String locales;

    /**
     * Specifies the input encoding.
     *
     * @parameter expression="${encoding}" default-value="${project.build.sourceEncoding}"
     */
    private String inputEncoding;

    /**
     * Specifies the output encoding.
     *
     * @parameter expression="${outputEncoding}" default-value="${project.reporting.outputEncoding}"
     */
    private String outputEncoding;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * Executes the Mojo.
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        for ( Book book : books )
        {
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

            String includes;
            if ( book.getIncludes() != null )
            {
                includes = StringUtils.join( book.getIncludes().iterator(), "," );
            }
            else
            {
                includes = "**/*";
            }

            String excludes = "";

            if ( book.getExcludes() != null )
            {
                excludes = StringUtils.join( book.getExcludes().iterator(), "," );
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

            List<File> files;

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

            List<Locale> localesList = siteTool.getAvailableLocales( locales );

            // Default is first in the list
            Locale defaultLocale = localesList.get( 0 );
            Locale.setDefault( defaultLocale );

            for ( Locale locale : localesList )
            {
                for ( Format format : book.getFormats() )
                {
                    File outputDirectory = new File( generatedDocs, format.getId() );
                    File directory = new File( outputDirectory + "/" + locale.toString(), bookModel.getId() );

                    if ( locale.equals( defaultLocale ) )
                    {
                        directory = new File( outputDirectory, bookModel.getId() );
                    }

                    try
                    {
                        bookDoxia.renderBook( bookModel, format.getId(), files, directory, locale,
                                              getInputEncoding(), getOutputEncoding() );
                    }
                    catch ( BookDoxiaException e )
                    {
                        throw new MojoExecutionException( "Error while generating book in format '"
                            + format.getId() + "'.", e );
                    }
                }
            }
        }
    }

    /**
     * Gets the input files encoding.
     *
     * @return The input files encoding, never <code>null</code>.
     * @since 1.1
     */
    protected String getInputEncoding()
    {
        return ( inputEncoding == null ) ? ReaderFactory.ISO_8859_1 : inputEncoding;
    }

    /**
     * Gets the effective reporting output files encoding.
     *
     * @return The effective reporting output file encoding, never <code>null</code>.
     * @since 1.1
     */
    protected String getOutputEncoding()
    {
        return ( outputEncoding == null ) ? ReaderFactory.UTF_8 : outputEncoding;
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

            for ( String error : result.getErrors() )
            {
                buffer.append( LINE_SEPARATOR ).append( " " ).append( error );
            }
        }

        if ( result.getWarnings().size() > 0 )
        {
            buffer.append( "Validation warnings:" );

            for ( String error : result.getWarnings() )
            {
                buffer.append( LINE_SEPARATOR ).append( " " ).append( error );
            }
        }

        return buffer.toString();
    }
}
