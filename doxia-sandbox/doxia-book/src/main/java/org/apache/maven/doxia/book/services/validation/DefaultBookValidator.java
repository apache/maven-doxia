package org.apache.maven.doxia.book.services.validation;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.StringUtils;
import org.apache.maven.doxia.book.model.Book;

/**
 * @plexus.component
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultBookValidator
    extends AbstractLogEnabled
    implements BookValidator, Initializable
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

        return result;
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
    }
}
