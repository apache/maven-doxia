package org.apache.maven.doxia.book;

import org.apache.maven.doxia.book.services.validation.ValidationResult;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class InvalidBookDescriptorException
    extends BookDoxiaException
{
    private ValidationResult validationResult;

    public InvalidBookDescriptorException( ValidationResult validationResult )
    {
        super( "Invalid book descriptor." );

        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult()
    {
        return validationResult;
    }
}
