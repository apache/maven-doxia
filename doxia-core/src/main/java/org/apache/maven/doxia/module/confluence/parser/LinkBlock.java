/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.maven.doxia.module.confluence.parser;

import org.apache.maven.doxia.sink.Sink;


public class LinkBlock
    implements Block
{
    private final String reference;

    private final String text;

    public LinkBlock( final String reference, final String text )
        throws IllegalArgumentException
    {
        if ( reference == null || text == null )
        {
            throw new IllegalArgumentException( "arguments can't be null" );
        }
        this.reference = reference;
        this.text = text;
    }

    public final void traverse( final Sink sink )
    {
        sink.link( reference );
        sink.text( text );
        sink.link_();
    }
}
