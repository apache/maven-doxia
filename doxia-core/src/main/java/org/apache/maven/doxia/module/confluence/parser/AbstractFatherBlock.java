package org.apache.maven.doxia.module.confluence.parser;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Arrays;
import java.util.List;
import java.util.Iterator;

import org.apache.maven.doxia.sink.Sink;

public abstract class AbstractFatherBlock
    implements Block
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

            block.traverse( sink );
        }

        after( sink );
    }

    public final List getBlocks()
    {
        return blocks;
    }
}
