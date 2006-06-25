package org.apache.maven.doxia.book;

import org.apache.maven.doxia.book.context.BookContext;
import org.apache.maven.doxia.book.context.TOCEntry;
import org.apache.maven.doxia.book.model.Book;
import org.apache.maven.doxia.book.model.io.xpp3.BookXpp3Reader;
import org.apache.maven.doxia.book.services.indexer.BookIndexer;
import org.apache.maven.doxia.book.services.renderer.BookRenderer;
import org.apache.maven.doxia.book.services.validation.BookValidator;
import org.apache.maven.doxia.book.services.validation.ValidationResult;
import org.apache.maven.doxia.site.module.SiteModule;
import org.apache.maven.doxia.site.module.manager.SiteModuleManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
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
    private BookValidator bookValidator;

    /**
     * @plexus.requirement
     */
    private BookIndexer bookIndexer;

    /**
     * @plexus.requirement role="org.apache.maven.doxia.book.services.renderer.BookRenderer"
     */
    private Map bookRenderers;

    /**
     * @plexus.requirement
     */
    private SiteModuleManager siteModuleManager;

    // ----------------------------------------------------------------------
    // BookDoxia Implementation
    // ----------------------------------------------------------------------

    public void renderBook( File bookDescriptor, String bookRendererId, List files, File outputDirectory )
        throws BookDoxiaException
    {
        Book book;

        try
        {
            book = new BookXpp3Reader().read( new FileReader( bookDescriptor ), true );
        }
        catch ( IOException e )
        {
            throw new BookDoxiaException( "Error while reading book descriptor.", e );
        }
        catch ( XmlPullParserException e )
        {
            throw new BookDoxiaException( "Error while reading book descriptor.", e );
        }

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        ValidationResult validationResult = bookValidator.validateBook( book );

        if ( !validationResult.isAllOk() )
        {
            throw new BookDoxiaException( "Could not validate the book model." );
        }

        // ----------------------------------------------------------------------
        // Create and initialize the context
        // ----------------------------------------------------------------------

        BookContext context = new BookContext();

        context.setBook( book );

        context.setOutputDirectory( outputDirectory );

        context.setRootTOCEntry( new TOCEntry() );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        Collection siteModules = siteModuleManager.getSiteModules();

        for ( Iterator it = siteModules.iterator(); it.hasNext(); )
        {
            SiteModule siteModule = (SiteModule) it.next();

            String extension = siteModule.getExtension();

            for ( Iterator j = files.iterator(); j.hasNext(); )
            {
                File file = (File) j.next();

                String name = file.getName();

                if ( name.endsWith( extension ) )
                {
                    name = name.substring( 0, name.length() - siteModule.getExtension().length() - 1 );

                    BookContext.BookFile bookFile = new BookContext.BookFile( file, siteModule.getParserId() );

                    context.getFiles().put( name, bookFile );
                }
            }
        }

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
