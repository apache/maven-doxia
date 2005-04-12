/*
 * Copyright (c) 1999-2003 Pixware. 
 *
 * Author: Hussein Shafie
 *
 * This file is part of the Pixware APT tools.
 * For conditions of distribution and use, see the accompanying legal.txt file.
 */
package org.codehaus.doxia.module.apt;

import org.codehaus.doxia.parser.ParseException;

public class AptParseException
    extends ParseException
{
    public AptParseException( String message, AptSource source )
    {
        super( null, message, source.getName(), source.getLineNumber() );
    }

    public AptParseException( String message )
    {
        super( message );
    }

    public AptParseException( Exception e )
    {
        super( e );
    }
}
