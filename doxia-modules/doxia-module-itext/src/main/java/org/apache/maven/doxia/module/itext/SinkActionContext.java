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
 * @author Jason van Zyl
 */
public class SinkActionContext
{
    public static final int TITLE = 0;
    public static final int AUTHOR = 1;
    public static final int DATE = 2;
    public static final int HEAD = 3;
    public static final int BODY = 4;

    public static final int SECTION_TITLE = 10;
    public static final int SECTION_TITLE_1 = 11;
    public static final int SECTION_TITLE_2 = 12;
    public static final int SECTION_TITLE_3 = 13;
    public static final int SECTION_TITLE_4 = 14;
    public static final int SECTION_TITLE_5 = 15;

    public static final int SECTION_1 = 20;
    public static final int SECTION_2 = 21;
    public static final int SECTION_3 = 22;
    public static final int SECTION_4 = 23;
    public static final int SECTION_5 = 24;

    public static final int DEFINITION_LIST = 30;
    public static final int DEFINITION_LIST_ITEM = 31;
    public static final int DEFINED_TERM = 32;

    public static final int LIST_ITEM = 40;
    public static final int NUMBERED_LIST_ITEM = 41;
    public static final int NUMBERED_LIST = 42;
    public static final int DEFINITION = 43;
    public static final int PARAGRAPH = 44;
    public static final int LIST = 45;

    public static final int TABLE = 50;
    public static final int TABLE_CAPTION = 51;
    public static final int TABLE_CELL = 52;
    public static final int TABLE_HEADER_CELL = 53;
    public static final int TABLE_ROW = 54;
    public static final int TABLE_ROWS = 55;

    public static final int VERBATIM = 60;

    public static final int FIGURE = 70;
    public static final int FIGURE_CAPTION = 71;
    public static final int FIGURE_GRAPHICS = 72;

    public static final int LINK = 80;
    public static final int ANCHOR = 81;
    public static final int UNDEFINED = 82;

    private Stack stack = new Stack();

    private int currentAction;

    public int getCurrentAction()
    {
        //return currentAction;
        if ( stack.empty() )
        {
            return UNDEFINED;
        }
        else
        {
            return ( (Integer) stack.peek() ).intValue();
        }
    }

    public void release()
    {
        //currentAction = -1;
        stack.pop();
    }

    public void setAction( int action )
    {
        //currentAction = action;

        stack.push( new Integer( action ) );
    }
}
