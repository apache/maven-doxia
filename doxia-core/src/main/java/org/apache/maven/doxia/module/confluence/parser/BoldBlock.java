/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.maven.doxia.module.confluence.parser;

import org.apache.maven.doxia.sink.Sink;

import java.util.List;


public class BoldBlock extends AbstractFatherBlock
{
    public BoldBlock( List childBlocks )
    {
        super( childBlocks );
    }

    public final void before( final Sink sink )
    {
        sink.bold();
    }

    public final void after( final Sink sink )
    {
        sink.bold_();
    }
}
