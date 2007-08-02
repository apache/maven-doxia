package org.apache.maven.doxia.module.apt;

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

import java.io.Writer;

import org.apache.maven.doxia.sink.AbstractSinkTest;
import org.apache.maven.doxia.sink.Sink;
import org.codehaus.plexus.util.StringUtils;

/**
 * Test the <code>AptSink</code> class
 *
 * @see AptSink
 */
public class AptSinkTest extends AbstractSinkTest
{
    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "apt";
    }

    /** {@inheritDoc} */
    protected Sink createSink( Writer writer )
    {
        return new AptSink( writer );
    }

    /** {@inheritDoc} */
    protected String getTitleBlock( String title )
    {
        return title;
    }

    /** {@inheritDoc} */
    protected String getAuthorBlock( String author )
    {
        return author;
    }

    /** {@inheritDoc} */
    protected String getDateBlock( String date )
    {
        return date;
    }

    /** {@inheritDoc} */
    protected String getHeadBlock()
    {
        return AptMarkup.HEADER_START + AptMarkup.HEADER_START + AptMarkup.HEADER_START + AptMarkup.HEADER_START;
    }

    /** {@inheritDoc} */
    protected String getBodyBlock()
    {
        return "";
    }

    /** {@inheritDoc} */
    protected String getSectionTitleBlock( String title )
    {
        return title;
    }

    /** {@inheritDoc} */
    protected String getSection1Block( String title )
    {
        return title;
    }

    /** {@inheritDoc} */
    protected String getSection2Block( String title )
    {
        return AptMarkup.SECTION_TITLE_START + title;
    }

    /** {@inheritDoc} */
    protected String getSection3Block( String title )
    {
        return StringUtils.repeat( String.valueOf( AptMarkup.SECTION_TITLE_START ), 2 ) + title;
    }

    /** {@inheritDoc} */
    protected String getSection4Block( String title )
    {
        return StringUtils.repeat( String.valueOf( AptMarkup.SECTION_TITLE_START ), 3 ) + title;
    }

    /** {@inheritDoc} */
    protected String getSection5Block( String title )
    {
        return StringUtils.repeat( String.valueOf( AptMarkup.SECTION_TITLE_START ), 4 ) + title;
    }

    /** {@inheritDoc} */
    protected String getListBlock( String item )
    {
        return AptMarkup.SPACE_MARKUP + "" + AptMarkup.LIST_START + "" + AptMarkup.SPACE_MARKUP + item
            + AptMarkup.SPACE_MARKUP + "" + AptMarkup.LIST_END;
    }

    /** {@inheritDoc} */
    protected String getNumberedListBlock( String item )
    {
        return AptMarkup.SPACE_MARKUP + "" + AptMarkup.LEFT_SQUARE_BRACKET_MARKUP + ""
            + AptMarkup.LEFT_SQUARE_BRACKET_MARKUP + AptMarkup.NUMBERING_LOWER_ROMAN_MARKUP + ""
            + AptMarkup.RIGHT_SQUARE_BRACKET_MARKUP + "" + AptMarkup.RIGHT_SQUARE_BRACKET_MARKUP
            + AptMarkup.SPACE_MARKUP + item + AptMarkup.SPACE_MARKUP + "" + AptMarkup.LIST_END;
    }

    /** {@inheritDoc} */
    protected String getDefinitionListBlock( String definum, String definition )
    {
        return AptMarkup.SPACE_MARKUP + "" + AptMarkup.LEFT_SQUARE_BRACKET_MARKUP + definum
            + AptMarkup.RIGHT_SQUARE_BRACKET_MARKUP + "" + definition;
    }

    /** {@inheritDoc} */
    protected String getFigureBlock( String source, String caption )
    {
        return AptMarkup.LEFT_SQUARE_BRACKET_MARKUP + source + AptMarkup.RIGHT_SQUARE_BRACKET_MARKUP
            + AptMarkup.SPACE_MARKUP + caption;
    }

    /** {@inheritDoc} */
    protected String getTableBlock( String cell, String caption )
    {
        return AptMarkup.TABLE_ROW_START + AptMarkup.TABLE_COL_CENTERED_ALIGNED + cell
        + AptMarkup.TABLE_ROW_SEPARATOR + AptMarkup.TABLE_ROW_START + AptMarkup.TABLE_COL_CENTERED_ALIGNED  + caption;
    }

    /** {@inheritDoc} */
    protected String getParagraphBlock( String text )
    {
        return AptMarkup.SPACE_MARKUP + text;
    }

    /** {@inheritDoc} */
    protected String getVerbatimBlock( String text )
    {
        return "\n" + AptMarkup.BOXED_VERBATIM_START + "\n" + text + "\n" + AptMarkup.BOXED_VERBATIM_START + "\n";
    }

    /** {@inheritDoc} */
    protected String getHorizontalRuleBlock()
    {
        return AptMarkup.HORIZONTAL_RULE;
    }

    /** {@inheritDoc} */
    protected String getPageBreakBlock()
    {
        return AptMarkup.PAGE_BREAK;
    }

    /** {@inheritDoc} */
    protected String getAnchorBlock( String anchor )
    {
        return AptMarkup.ANCHOR_START + anchor + AptMarkup.ANCHOR_END;
    }

    /** {@inheritDoc} */
    protected String getLinkBlock( String link, String text )
    {
        return AptMarkup.LINK_START_1 + link + AptMarkup.LINK_START_2 + text + AptMarkup.LINK_END;
    }

    /** {@inheritDoc} */
    protected String getItalicBlock( String text )
    {
        return AptMarkup.ITALIC_START + text + AptMarkup.ITALIC_END;
    }

    /** {@inheritDoc} */
    protected String getBoldBlock( String text )
    {
        return AptMarkup.BOLD_START + text + AptMarkup.BOLD_END;
    }

    /** {@inheritDoc} */
    protected String getMonospacedBlock( String text )
    {
        return AptMarkup.MONOSPACED_START + text + AptMarkup.MONOSPACED_END;
    }

    /** {@inheritDoc} */
    protected String getLineBreakBlock()
    {
        return AptMarkup.BACKSLASH_MARKUP + "";
    }

    /** {@inheritDoc} */
    protected String getNonBreakingSpaceBlock()
    {
        return AptMarkup.NON_BREAKING_SPACE;
    }

    /** {@inheritDoc} */
    protected String getTextBlock( String text )
    {
        // "\\~, \\=, \\-, \\+, \\*, \\[, \\], \\<, \\>, \\{, \\}, \\\\"
        StringBuffer sb = new StringBuffer();
        sb.append( getSpecialCharacters( AptMarkup.COMMENT_MARKUP ) ).append( ", " );
        sb.append( getSpecialCharacters( AptMarkup.EQUAL_MARKUP ) ).append( ", " );
        sb.append( getSpecialCharacters( AptMarkup.MINUS_MARKUP ) ).append( ", " );
        sb.append( getSpecialCharacters( AptMarkup.PLUS_MARKUP ) ).append( ", " );
        sb.append( getSpecialCharacters( AptMarkup.STAR_MARKUP ) ).append( ", " );
        sb.append( getSpecialCharacters( AptMarkup.LEFT_SQUARE_BRACKET_MARKUP ) ).append( ", " );
        sb.append( getSpecialCharacters( AptMarkup.RIGHT_SQUARE_BRACKET_MARKUP ) ).append( ", " );
        sb.append( getSpecialCharacters( AptMarkup.LESS_THAN_MARKUP ) ).append( ", " );
        sb.append( getSpecialCharacters( AptMarkup.GREATER_THAN_MARKUP ) ).append( ", " );
        sb.append( getSpecialCharacters( AptMarkup.LEFT_CURLY_BRACKET_MARKUP ) ).append( ", " );
        sb.append( getSpecialCharacters( AptMarkup.RIGHT_CURLY_BRACKET_MARKUP ) ).append( ", " );
        sb.append( getSpecialCharacters( AptMarkup.BACKSLASH_MARKUP ) );

        return sb.toString();
    }

    /** {@inheritDoc} */
    protected String getRawTextBlock( String text )
    {
        return text;
    }

    /**
     * Add a backslash for a special markup character
     *
     * @param c
     * @return the character with a backslash before
     */
    private static String getSpecialCharacters( char c )
    {
        return AptMarkup.BACKSLASH_MARKUP + "" + c;
    }
}
