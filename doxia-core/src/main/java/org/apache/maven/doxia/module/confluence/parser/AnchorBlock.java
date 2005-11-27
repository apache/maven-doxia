/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.maven.doxia.module.confluence.parser;

import org.apache.maven.doxia.sink.Sink;

public class AnchorBlock
    implements Block
{
    private final String name;

    public AnchorBlock( final String name ) throws IllegalArgumentException
    {
        if ( name == null )
        {
            throw new IllegalArgumentException( "argument can't be null" );
        }
        this.name = name;
    }

    public final void traverse( final Sink sink )
    {
        sink.anchor( name );
        sink.anchor_();
    }
}
