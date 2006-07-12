package org.apache.maven.doxia.book;

import org.apache.maven.doxia.book.context.BookContext;
import org.apache.maven.doxia.book.model.BookModel;
import org.apache.maven.doxia.book.services.indexer.BookIndexer;
import org.apache.maven.doxia.book.services.io.BookIo;
import org.apache.maven.doxia.book.services.renderer.BookRenderer;
import org.apache.maven.doxia.book.services.validation.BookValidator;
import org.apache.maven.doxia.book.services.validation.ValidationResult;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @plexus.component
 */
public class DefaultBookDoxia
    extends AbstractLogEnabled
    implements BookDoxia
{
    /**
     * @plexus.requirement
     */
    private BookIo bookIo;

    /**
     * @plexus.requirement
     */
    private BookValidator bookValidator;

    /**
     * @plexus.requirement
     */
    private BookIndexer bookIndexer;

    /**
     * @plexus.requirement role="org.apache.maven.doxia.book.services.renderer.BookRenderer"
     */
    private Map bookRenderers;

    // ----------------------------------------------------------------------
    // BookDoxia Implementation
    // ----------------------------------------------------------------------

    public BookModel loadBook( File bookDescriptor )
        throws BookDoxiaException
    {
        return bookIo.readBook( bookDescriptor );
    }

    public void renderBook( BookModel book, String bookRendererId, List files, File outputDirectory )
        throws BookDoxiaException
    {
        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        ValidationResult validationResult = bookValidator.validateBook( book );

        if ( !validationResult.isAllOk() )
        {
            throw new InvalidBookDescriptorException( validationResult );
        }

        // ----------------------------------------------------------------------
        // Create and initialize the context
        // ----------------------------------------------------------------------

        BookContext context = new BookContext();

        context.setBook( book );

        context.setOutputDirectory( outputDirectory );

        // -----------------------------------------------------------------------
        //
        // -----------------------------------------------------------------------

        bookIo.loadFiles( context, files );

        // ----------------------------------------------------------------------
        // Generate indexes
        // ----------------------------------------------------------------------

        bookIndexer.indexBook( book, context );

        // ----------------------------------------------------------------------
        // Render the book
        // ----------------------------------------------------------------------

        BookRenderer bookRenderer = (BookRenderer) bookRenderers.get( bookRendererId );

        if ( bookRenderer == null )
        {
            throw new BookDoxiaException( "No such book rendered '" + bookRendererId + "'." );
        }

        bookRenderer.renderBook( context );
    }
}
