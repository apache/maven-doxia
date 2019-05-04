package org.apache.maven.doxia.module.twiki.parser;

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

import java.lang.reflect.Method;
import java.util.Collections;

import org.apache.maven.doxia.sink.Sink;

/**
 * Block that represents a section
 *
 * @author Juan F. Codagnone
 * @version $Id$
 */
public class SectionBlock
    extends AbstractFatherBlock
{
    /** {@inheritDoc} */
    private final String title;

    /** {@inheritDoc} */
    private final int level;

    /**
     * Creates the SectionBlock.
     *
     * @param title  the section title, cannot be {@code null}
     * @param level  the section level: 0 &lt; level &lt; 6
     * @param blocks child blocks, cannot be {@code null}
     */
    public SectionBlock( final String title, final int level, final Block[] blocks )
    {
        super( blocks );
        final int maxLevel = 5;
        if ( title == null )
        {
            throw new IllegalArgumentException( "title cant be null" );
        }
        else if ( level < 1 || level > maxLevel )
        {
            throw new IllegalArgumentException( "invalid level: " + level );
        }

        this.title = title;
        this.level = level;
    }

    /** {@inheritDoc} */
    final void before( final Sink sink )
    {
        sectionStart( sink );
        sectionTitle( sink );
        sink.text( title );
        sectionTitle_( sink );

    }

    /** {@inheritDoc} */
    final void after( final Sink sink )
    {
        sectionEnd( sink );
    }

    /**
     * call to sink.section<Level>()
     *
     * @param sink sink
     */
    private void sectionStart( final Sink sink )
    {
        invokeVoidVoid( sink, "section" + level );
    }

    /**
     * call to sink.section<Level>_()
     *
     * @param sink sink
     */
    private void sectionEnd( final Sink sink )
    {
        invokeVoidVoid( sink, "section" + level + "_" );
    }

    /**
     * Let you call sink's methods that returns <code>null</code> and have
     * no parameters.
     *
     * @param sink the Sink
     * @param name the name of the method to call
     */
    private void invokeVoidVoid( final Sink sink, final String name )
    {
        try
        {
            final Method m = sink.getClass().getMethod( name );
            m.invoke( sink, Collections.EMPTY_LIST.toArray() );
        }
        catch ( Exception e )
        {
            // FIXME
            throw new IllegalArgumentException( "invoking sink's " + name + " method: " + e.getMessage() );
        }
    }

    /**
     * Returns the level.
     *
     * @return <code>int</code> with the level.
     */
    public final int getLevel()
    {
        return level;
    }

    /**
     * Returns the title.
     *
     * @return <code>String</code> with the title.
     */
    public final String getTitle()
    {
        return title;
    }

    /** {@inheritDoc} */
    public final String toString()
    {
        final StringBuilder sb = new StringBuilder();

        sb.append( "Section  {title: '" );
        sb.append( getTitle() );
        sb.append( "' level: " );
        sb.append( getLevel() );
        sb.append( "}: [" );
        for ( int i = 0; i < getBlocks().length; i++ )
        {
            final Block block = getBlocks()[i];

            sb.append( block.toString() );
            sb.append( ", " );
        }
        sb.append( "]" );
        return sb.toString();
    }

    /** @param sink */
    private void sectionTitle( final Sink sink )
    {
        invokeVoidVoid( sink, "sectionTitle" + level );
    }

    /** @param sink */
    private void sectionTitle_( final Sink sink )
    {
        invokeVoidVoid( sink, "sectionTitle" + level + "_" );
    }
}
