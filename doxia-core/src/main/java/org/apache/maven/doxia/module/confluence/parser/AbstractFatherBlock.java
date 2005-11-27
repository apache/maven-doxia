/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.maven.doxia.module.confluence.parser;

import java.util.Arrays;
import java.util.List;
import java.util.Iterator;

import org.apache.maven.doxia.sink.Sink;


public abstract class AbstractFatherBlock implements Block
{
    private final List blocks;

    public abstract void before( Sink sink );

    public abstract void after( Sink sink );

    public AbstractFatherBlock( final List childBlocks )
    {
        if ( childBlocks == null )
        {
            throw new IllegalArgumentException( "argument can't be null" );
        }

        this.blocks = childBlocks;
    }

    public final void traverse( final Sink sink )
    {
        before( sink );

        for ( Iterator i = blocks.iterator(); i.hasNext(); )
        {
            Block block = (Block) i.next();

            System.out.println( "block = " + block );

            block.traverse( sink );
        }

        after( sink );
    }

    public final List getBlocks()
    {
        return blocks;
    }
}
