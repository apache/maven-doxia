/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.maven.doxia.module.confluence.parser;

import org.apache.maven.doxia.sink.Sink;

import java.util.List;

public class ItalicBlock
    extends AbstractFatherBlock
{
    public ItalicBlock( List childBlocks )
    {
        super( childBlocks );
    }

    public final void before( final Sink sink )
    {
        sink.italic();
    }

    public final void after( final Sink sink )
    {
        sink.italic_();
    }
}
