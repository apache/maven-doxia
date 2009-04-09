package org.apache.maven.doxia.module.confluence.parser;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Iterator;
import java.util.List;

import org.apache.maven.doxia.sink.Sink;

/**
 * <p>Abstract AbstractFatherBlock class.</p>
 *
 * @version $Id$
 */
public abstract class AbstractFatherBlock
    implements Block
{
    private  List blocks;

    /**
     * <p>before</p>
     *
     * @param sink the Sink to receive events.
     */
    public abstract void before( Sink sink );

    /**
     * <p>after</p>
     *
     * @param sink the Sink to receive events.
     */
    public abstract void after( Sink sink );

    /**
     * <p>Constructor for AbstractFatherBlock.</p>
     *
     * @param childBlocks the child blocks.
     */
    public AbstractFatherBlock(  List childBlocks )
    {
        if ( childBlocks == null )
        {
            throw new IllegalArgumentException( "argument can't be null" );
        }

        this.blocks = childBlocks;
    }

    /** {@inheritDoc} */
    public  void traverse(  Sink sink )
    {
        before( sink );

        for ( Iterator i = blocks.iterator(); i.hasNext(); )
        {
            Block block = (Block) i.next();

            block.traverse( sink );
        }

        after( sink );
    }

    /**
     * <p>Getter for the field <code>blocks</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public  List getBlocks()
    {
        return blocks;
    }
}
