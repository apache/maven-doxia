package org.apache.maven.doxia.book.services.validation;

import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ValidationResult
{
    private boolean allOk;

    private List errors;

    private List warnings;

    public boolean isAllOk()
    {
        return getErrors().size() == 0 && getWarnings().size() == 0;
    }

    public List getErrors()
    {
        if ( errors == null )
        {
            errors = new ArrayList();
        }

        return errors;
    }

    public List getWarnings()
    {
        if ( warnings == null )
        {
            warnings = new ArrayList();
        }

        return warnings;
    }
}
