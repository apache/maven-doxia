package org.apache.maven.doxia.module;

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

    private int currentAction;

    public int getCurrentAction()
    {
        return currentAction;
    }

    public void release()
    {
        currentAction = -1;
    }

    public void setAction( int action )
    {
        currentAction = action;
    }
}
