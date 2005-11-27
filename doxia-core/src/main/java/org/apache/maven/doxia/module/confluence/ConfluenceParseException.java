/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.maven.doxia.module.confluence;

import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.module.apt.AptSource;

public class ConfluenceParseException
    extends ParseException
{
    public ConfluenceParseException( String message, ConfluenceSource source )
    {
        super( null, message, source.getName(), source.getLineNumber() );
    }

    public ConfluenceParseException( String message )
    {
        super( message );
    }

    public ConfluenceParseException( Exception e )
    {
        super( e );
    }
}
