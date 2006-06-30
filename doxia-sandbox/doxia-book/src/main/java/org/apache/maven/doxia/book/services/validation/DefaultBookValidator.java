package org.apache.maven.doxia.book.services.validation;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.StringUtils;
import org.apache.maven.doxia.book.model.Book;
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

    public ValidationResult validateBook( Book book )
    {
        ValidationResult result = new ValidationResult();

        if ( StringUtils.isEmpty( book.getId() ) )
        {
            result.getErrors().add( "Book is missing id." );
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

        if ( chapter.getSections().size() == 0 )
        {
            result.getErrors().add( "Each chapter has to contain at least one section. Chapter id: " + chapter.getId() );
        }
    }
}
