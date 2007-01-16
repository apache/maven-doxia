package org.apache.maven.doxia.index;

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

import java.util.Stack;

import org.apache.maven.doxia.module.HtmlTools;
import org.apache.maven.doxia.sink.SinkAdapter;

/**
 * A sink implementation for index
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class IndexingSink
    extends SinkAdapter
{
    private final static int TYPE_SECTION_1 = 1;

    private final static int TYPE_SECTION_2 = 2;

    private final static int TYPE_SECTION_3 = 3;

    private final static int TYPE_SECTION_4 = 4;

    private final static int TYPE_SECTION_5 = 5;

    private final static int TYPE_DEFINED_TERM = 6;

    private final static int TYPE_FIGURE = 7;

    private final static int TYPE_TABLE = 8;

    private final static int TITLE = 9;

    private int type;

    private String title;

    private Stack stack = new Stack();

    /**
     * Default constructor
     *
     * @param sectionEntry
     */
    public IndexingSink( IndexEntry sectionEntry )
    {
        stack.push( sectionEntry );
    }

    /**
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    // ----------------------------------------------------------------------
    // Sink Overrides
    // ----------------------------------------------------------------------

    public void title()
    {
        super.title();

        type = TITLE;
    }

    public void sectionTitle1()
    {
        type = TYPE_SECTION_1;
    }

    public void section1_()
    {
        pop();
    }

    public void sectionTitle2()
    {
        type = TYPE_SECTION_2;
    }

    public void section2_()
    {
        pop();
    }

    public void sectionTitle3()
    {
        type = TYPE_SECTION_3;
    }

    public void section3_()
    {
        pop();
    }

    public void sectionTitle4()
    {
        type = TYPE_SECTION_4;
    }

    public void section4_()
    {
        pop();
    }

    public void sectionTitle5()
    {
        type = TYPE_SECTION_5;
    }

    public void section5_()
    {
        pop();
    }

    //    public void definedTerm()
    //    {
    //        type = TYPE_DEFINED_TERM;
    //    }
    //
    //    public void figureCaption()
    //    {
    //        type = TYPE_FIGURE;
    //    }
    //
    //    public void tableCaption()
    //    {
    //        type = TYPE_TABLE;
    //    }

    public void text( String text )
    {
        IndexEntry entry;

        switch ( type )
        {
            case TITLE:
                this.title = text;
                break;
            case TYPE_SECTION_1:
            case TYPE_SECTION_2:
            case TYPE_SECTION_3:
            case TYPE_SECTION_4:
            case TYPE_SECTION_5:
                // -----------------------------------------------------------------------
                // Sanitize the id. The most important step is to remove any blanks
                // -----------------------------------------------------------------------

                String id = HtmlTools.encodeId( text );

                entry = new IndexEntry( peek(), id );

                entry.setTitle( text );

                push( entry );
                break;
            // Dunno how to handle these yet
            case TYPE_DEFINED_TERM:
            case TYPE_FIGURE:
            case TYPE_TABLE:
        }

        type = 0;
    }

    /**
     * Pushes an IndexEntry onto the top of this stack
     *
     * @param entry to put
     */
    public void push( IndexEntry entry )
    {
        stack.push( entry );
    }

    /**
     * Removes the IndexEntry at the top of this stack
     */
    public void pop()
    {
        stack.pop();
    }

    /**
     * @return Looks at the IndexEntry at the top of this stack
     */
    public IndexEntry peek()
    {
        return (IndexEntry) stack.peek();
    }
}
