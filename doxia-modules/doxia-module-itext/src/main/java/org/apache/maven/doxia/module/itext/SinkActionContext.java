package org.apache.maven.doxia.module.itext;

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

/**
 * <p>SinkActionContext class.</p>
 *
 * @author Jason van Zyl
 * @version $Id$
 */
public class SinkActionContext
{
    /** Constant <code>TITLE=0</code> */
    public static final int TITLE = 0;
    /** Constant <code>AUTHOR=1</code> */
    public static final int AUTHOR = 1;
    /** Constant <code>DATE=2</code> */
    public static final int DATE = 2;
    /** Constant <code>HEAD=3</code> */
    public static final int HEAD = 3;
    /** Constant <code>BODY=4</code> */
    public static final int BODY = 4;

    /** Constant <code>SECTION_TITLE=10</code> */
    public static final int SECTION_TITLE = 10;
    /** Constant <code>SECTION_TITLE_1=11</code> */
    public static final int SECTION_TITLE_1 = 11;
    /** Constant <code>SECTION_TITLE_2=12</code> */
    public static final int SECTION_TITLE_2 = 12;
    /** Constant <code>SECTION_TITLE_3=13</code> */
    public static final int SECTION_TITLE_3 = 13;
    /** Constant <code>SECTION_TITLE_4=14</code> */
    public static final int SECTION_TITLE_4 = 14;
    /** Constant <code>SECTION_TITLE_5=15</code> */
    public static final int SECTION_TITLE_5 = 15;

    /** Constant <code>SECTION_1=20</code> */
    public static final int SECTION_1 = 20;
    /** Constant <code>SECTION_2=21</code> */
    public static final int SECTION_2 = 21;
    /** Constant <code>SECTION_3=22</code> */
    public static final int SECTION_3 = 22;
    /** Constant <code>SECTION_4=23</code> */
    public static final int SECTION_4 = 23;
    /** Constant <code>SECTION_5=24</code> */
    public static final int SECTION_5 = 24;

    /** Constant <code>DEFINITION_LIST=30</code> */
    public static final int DEFINITION_LIST = 30;
    /** Constant <code>DEFINITION_LIST_ITEM=31</code> */
    public static final int DEFINITION_LIST_ITEM = 31;
    /** Constant <code>DEFINED_TERM=32</code> */
    public static final int DEFINED_TERM = 32;

    /** Constant <code>LIST_ITEM=40</code> */
    public static final int LIST_ITEM = 40;
    /** Constant <code>NUMBERED_LIST_ITEM=41</code> */
    public static final int NUMBERED_LIST_ITEM = 41;
    /** Constant <code>NUMBERED_LIST=42</code> */
    public static final int NUMBERED_LIST = 42;
    /** Constant <code>DEFINITION=43</code> */
    public static final int DEFINITION = 43;
    /** Constant <code>PARAGRAPH=44</code> */
    public static final int PARAGRAPH = 44;
    /** Constant <code>LIST=45</code> */
    public static final int LIST = 45;

    /** Constant <code>TABLE=50</code> */
    public static final int TABLE = 50;
    /** Constant <code>TABLE_CAPTION=51</code> */
    public static final int TABLE_CAPTION = 51;
    /** Constant <code>TABLE_CELL=52</code> */
    public static final int TABLE_CELL = 52;
    /** Constant <code>TABLE_HEADER_CELL=53</code> */
    public static final int TABLE_HEADER_CELL = 53;
    /** Constant <code>TABLE_ROW=54</code> */
    public static final int TABLE_ROW = 54;
    /** Constant <code>TABLE_ROWS=55</code> */
    public static final int TABLE_ROWS = 55;

    /** Constant <code>VERBATIM=60</code> */
    public static final int VERBATIM = 60;

    /** Constant <code>FIGURE=70</code> */
    public static final int FIGURE = 70;
    /** Constant <code>FIGURE_CAPTION=71</code> */
    public static final int FIGURE_CAPTION = 71;
    /** Constant <code>FIGURE_GRAPHICS=72</code> */
    public static final int FIGURE_GRAPHICS = 72;

    /** Constant <code>LINK=80</code> */
    public static final int LINK = 80;
    /** Constant <code>ANCHOR=81</code> */
    public static final int ANCHOR = 81;
    /** Constant <code>UNDEFINED=82</code> */
    public static final int UNDEFINED = 82;

    private Stack<Integer> stack = new Stack<>();

    private int currentAction;

    /**
     * <p>Getter for the field <code>currentAction</code>.</p>
     *
     * @return a int.
     */
    public int getCurrentAction()
    {
        //return currentAction;
        if ( stack.empty() )
        {
            return UNDEFINED;
        }
        else
        {
            return stack.peek();
        }
    }

    /**
     * release.
     */
    public void release()
    {
        //currentAction = -1;
        stack.pop();
    }

    /**
     * setAction.
     *
     * @param action a int.
     */
    public void setAction( int action )
    {
        //currentAction = action;

        stack.push( action );
    }
}
