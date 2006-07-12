package org.apache.maven.doxia.book.services.validation;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.StringUtils;
import org.apache.maven.doxia.book.model.BookModel;
import org.apache.maven.doxia.book.model.Chapter;

import java.util.Iterator;

/**
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
