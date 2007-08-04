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

import org.apache.maven.doxia.markup.Markup;
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
        return AptMarkup.HEADER_START_MARKUP + AptMarkup.HEADER_START_MARKUP + AptMarkup.HEADER_START_MARKUP
            + AptMarkup.HEADER_START_MARKUP;
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
        return AptMarkup.SECTION_TITLE_START_MARKUP + title;
    }

    /** {@inheritDoc} */
    protected String getSection3Block( String title )
    {
        return StringUtils.repeat( String.valueOf( AptMarkup.SECTION_TITLE_START_MARKUP ), 2 ) + title;
    }

    /** {@inheritDoc} */
    protected String getSection4Block( String title )
    {
        return StringUtils.repeat( String.valueOf( AptMarkup.SECTION_TITLE_START_MARKUP ), 3 ) + title;
    }

    /** {@inheritDoc} */
    protected String getSection5Block( String title )
    {
        return StringUtils.repeat( String.valueOf( AptMarkup.SECTION_TITLE_START_MARKUP ), 4 ) + title;
    }

    /** {@inheritDoc} */
    protected String getListBlock( String item )
    {
        return Markup.SPACE + "" + AptMarkup.LIST_START_MARKUP + "" + Markup.SPACE + item
            + Markup.SPACE + "" + AptMarkup.LIST_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getNumberedListBlock( String item )
    {
        return Markup.SPACE + "" + Markup.LEFT_SQUARE_BRACKET + ""
            + Markup.LEFT_SQUARE_BRACKET + AptMarkup.NUMBERING_LOWER_ROMAN_CHAR + ""
            + Markup.RIGHT_SQUARE_BRACKET + "" + Markup.RIGHT_SQUARE_BRACKET
            + Markup.SPACE + item + Markup.SPACE + "" + AptMarkup.LIST_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getDefinitionListBlock( String definum, String definition )
    {
        return Markup.SPACE + "" + Markup.LEFT_SQUARE_BRACKET + definum
            + Markup.RIGHT_SQUARE_BRACKET + "" + definition;
    }

    /** {@inheritDoc} */
    protected String getFigureBlock( String source, String caption )
    {
        return Markup.LEFT_SQUARE_BRACKET + source + Markup.RIGHT_SQUARE_BRACKET
            + Markup.SPACE + caption;
    }

    /** {@inheritDoc} */
    protected String getTableBlock( String cell, String caption )
    {
        return AptMarkup.TABLE_ROW_START_MARKUP + AptMarkup.TABLE_COL_CENTERED_ALIGNED_MARKUP + cell
        + AptMarkup.TABLE_ROW_SEPARATOR_MARKUP + AptMarkup.TABLE_ROW_START_MARKUP + AptMarkup.TABLE_COL_CENTERED_ALIGNED_MARKUP  + caption;
    }

    /** {@inheritDoc} */
    protected String getParagraphBlock( String text )
    {
        return Markup.SPACE + text;
    }

    /** {@inheritDoc} */
    protected String getVerbatimBlock( String text )
    {
        return "\n" + AptMarkup.BOXED_VERBATIM_START_MARKUP + "\n" + text + "\n" + AptMarkup.BOXED_VERBATIM_START_MARKUP + "\n";
    }

    /** {@inheritDoc} */
    protected String getHorizontalRuleBlock()
    {
        return AptMarkup.HORIZONTAL_RULE_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getPageBreakBlock()
    {
        return AptMarkup.PAGE_BREAK_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getAnchorBlock( String anchor )
    {
        return AptMarkup.ANCHOR_START_MARKUP + anchor + AptMarkup.ANCHOR_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getLinkBlock( String link, String text )
    {
        return AptMarkup.LINK_START_1_MARKUP + link + AptMarkup.LINK_START_2_MARKUP + text + AptMarkup.LINK_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getItalicBlock( String text )
    {
        return AptMarkup.ITALIC_START_MARKUP + text + AptMarkup.ITALIC_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getBoldBlock( String text )
    {
        return AptMarkup.BOLD_START_MARKUP + text + AptMarkup.BOLD_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getMonospacedBlock( String text )
    {
        return AptMarkup.MONOSPACED_START_MARKUP + text + AptMarkup.MONOSPACED_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getLineBreakBlock()
    {
        return String.valueOf( AptMarkup.BACKSLASH );
    }

    /** {@inheritDoc} */
    protected String getNonBreakingSpaceBlock()
    {
        return AptMarkup.NON_BREAKING_SPACE_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getTextBlock( String text )
    {
        // "\\~, \\=, \\-, \\+, \\*, \\[, \\], \\<, \\>, \\{, \\}, \\\\"
        StringBuffer sb = new StringBuffer();
        sb.append( getSpecialCharacters( AptMarkup.COMMENT ) ).append( ", " );
        sb.append( getSpecialCharacters( Markup.EQUAL ) ).append( ", " );
        sb.append( getSpecialCharacters( Markup.MINUS ) ).append( ", " );
        sb.append( getSpecialCharacters( Markup.PLUS ) ).append( ", " );
        sb.append( getSpecialCharacters( Markup.STAR ) ).append( ", " );
        sb.append( getSpecialCharacters( Markup.LEFT_SQUARE_BRACKET ) ).append( ", " );
        sb.append( getSpecialCharacters( Markup.RIGHT_SQUARE_BRACKET ) ).append( ", " );
        sb.append( getSpecialCharacters( Markup.LESS_THAN ) ).append( ", " );
        sb.append( getSpecialCharacters( Markup.GREATER_THAN ) ).append( ", " );
        sb.append( getSpecialCharacters( Markup.LEFT_CURLY_BRACKET ) ).append( ", " );
        sb.append( getSpecialCharacters( Markup.RIGHT_CURLY_BRACKET ) ).append( ", " );
        sb.append( getSpecialCharacters( AptMarkup.BACKSLASH ) );

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
        return AptMarkup.BACKSLASH + "" + c;
    }
}
