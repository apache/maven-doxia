package org.apache.maven.doxia.plugin;

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
     * @parameter
     */
    private List books;

    /**
     * @parameter expression="${basedir}"
     */
    private File basedir;

    /**
     * @parameter expression="${project.build.directory}/generated-site"
     */
    private File generatedDocs;

    /**
     * @component
     */
    private BookDoxia bookDoxia;

    private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

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
                throw new MojoFailureException( "Invalid configuration: The book is required to have a descriptor set." );
            }

            if ( StringUtils.isEmpty( book.getDirectory() ) )
            {
                throw new MojoFailureException( "Invalid configuration: The book is required to have a directory set." );
            }

            if ( book.getFormats() == null || book.getFormats().size() == 0 )
            {
                throw new MojoFailureException( "Invalid configuration: The book is required to have at least one format set." );
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
                throw new MojoExecutionException( "Error while looking for input files. " +
                    "Basedir=" + basedir.getAbsolutePath() + ", " +
                    "includes=" + includes + ", " +
                    "excludes=" + excludes, e );
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
                throw new MojoFailureException( "Invalid book descriptor: " + LINE_SEPARATOR +
                    formatResult( e.getValidationResult() ) );
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
                    throw new MojoExecutionException( "Error while generating book in format '" + format + "'.", e );
                }
            }
        }
    }

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
