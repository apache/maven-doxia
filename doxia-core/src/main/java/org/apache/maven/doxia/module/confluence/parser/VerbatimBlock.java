/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.maven.doxia.module.confluence.parser;

import org.apache.maven.doxia.sink.Sink;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class VerbatimBlock
    implements Block
{
    private String text;

    public VerbatimBlock( String text )
    {
        this.text = text;
    }

    public void traverse( Sink sink )
    {
        sink.verbatim( true );

        sink.text( text );

        sink.verbatim_();
    }
}
