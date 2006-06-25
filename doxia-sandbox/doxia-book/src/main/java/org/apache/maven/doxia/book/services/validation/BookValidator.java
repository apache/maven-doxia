package org.apache.maven.doxia.book.services.validation;

import org.apache.maven.doxia.book.model.Book;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface BookValidator
{
    String ROLE = BookValidator.class.getName();

    ValidationResult validateBook( Book book );
}
