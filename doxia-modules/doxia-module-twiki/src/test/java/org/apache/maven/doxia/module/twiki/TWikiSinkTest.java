package org.apache.maven.doxia.module.twiki;

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
import org.apache.maven.doxia.util.HtmlTools;
import org.codehaus.plexus.util.StringUtils;

/**
 * Test the TWiki Sink
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @see TWikiSink
 */
public class TWikiSinkTest
    extends AbstractSinkTest
{
    /** {@inheritDoc} */
    protected Sink createSink( Writer writer )
    {
        return new TWikiSink( writer );
    }

    /** {@inheritDoc} */
    protected boolean isXmlSink()
    {
        return false;
    }

    /** {@inheritDoc} */
    protected String getAnchorBlock( String anchor )
    {
        return EOL + "#" + anchor + anchor;
    }

    /** Not used.
     * {@inheritDoc} */
    protected String getAuthorBlock( String author )
    {
        return null;
    }

    /** Not used.
     * {@inheritDoc} */
    protected String getBodyBlock()
    {
        return null;
    }

    /** {@inheritDoc} */
    protected String getBoldBlock( String text )
    {
        return TWikiMarkup.BOLD_START_MARKUP + text + TWikiMarkup.BOLD_END_MARKUP;
    }

    /** Not used.
     * {@inheritDoc} */
    protected String getDateBlock( String date )
    {
        return null;
    }

    /** {@inheritDoc} */
    protected String getDefinitionListBlock( String definum, String definition )
    {
        return TWikiMarkup.DEFINITION_LIST_ITEM_MARKUP + definum + TWikiMarkup.DEFINITION_LIST_DEFINITION_MARKUP
            + definition + EOL;
    }

    /** {@inheritDoc} */
    protected String getFigureBlock( String source, String caption )
    {
        return "<img src=\"" + source + "\" alt=\"" + caption + "\" />";
    }

    /** Not used.
     * {@inheritDoc} */
    protected String getHeadBlock()
    {
        return null;
    }

    /** {@inheritDoc} */
    protected String getHorizontalRuleBlock()
    {
        return TWikiMarkup.HORIZONTAL_RULE_MARKUP + EOL;
    }

    /** {@inheritDoc} */
    protected String getItalicBlock( String text )
    {
        return TWikiMarkup.ITALIC_START_MARKUP + text + TWikiMarkup.ITALIC_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getLineBreakBlock()
    {
        return "";
    }

    /** {@inheritDoc} */
    protected String getLinkBlock( String link, String text )
    {
        return TWikiMarkup.LINK_START_MARKUP + link + TWikiMarkup.LINK_MIDDLE_MARKUP + text
            + TWikiMarkup.LINK_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getListBlock( String item )
    {
        return TWikiMarkup.LIST_ITEM_MARKUP + item + EOL;
    }

    /** {@inheritDoc} */
    protected String getMonospacedBlock( String text )
    {
        return TWikiMarkup.MONOSPACED_START_MARKUP + text + TWikiMarkup.MONOSPACED_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getNonBreakingSpaceBlock()
    {
        return "";
    }

    /** {@inheritDoc} */
    protected String getNumberedListBlock( String item )
    {
        return TWikiMarkup.NUMBERING_LOWER_ROMAN_MARKUP + " " + item + EOL;
    }

    /** {@inheritDoc} */
    protected String getPageBreakBlock()
    {
        return "";
    }

    /** {@inheritDoc} */
    protected String getParagraphBlock( String text )
    {
        return text + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getRawTextBlock( String text )
    {
        return "";
    }

    /** {@inheritDoc} */
    protected String getSection1Block( String title )
    {
        return StringUtils.repeat( "-", 3 ) + StringUtils.repeat( "+", 1 ) + title + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection2Block( String title )
    {
        return StringUtils.repeat( "-", 3 ) + StringUtils.repeat( "+", 2 ) + title + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection3Block( String title )
    {
        return StringUtils.repeat( "-", 3 ) + StringUtils.repeat( "+", 3 ) + title + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection4Block( String title )
    {
        return StringUtils.repeat( "-", 3 ) + StringUtils.repeat( "+", 4 ) + title + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection5Block( String title )
    {
        return StringUtils.repeat( "-", 3 ) + StringUtils.repeat( "+", 5 ) + title + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getSectionTitleBlock( String title )
    {
        return title;
    }

    /** {@inheritDoc} */
    protected String getTableBlock( String cell, String caption )
    {
        return "| " + cell + " |" + EOL + "Table_caption";
    }

    /** {@inheritDoc} */
    protected String getTextBlock( String text )
    {
        return HtmlTools.escapeHTML( text );
    }

    /** Not used.
     * {@inheritDoc} */
    protected String getTitleBlock( String title )
    {
        return null;
    }

    /** {@inheritDoc} */
    protected String getVerbatimBlock( String text )
    {
        return "<div class=\"source\">" + EOL + "<verbatim>" + text + "</verbatim>" + EOL + "</div>" + EOL;
    }

    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "twiki";
    }

    // ----------------------------------------------------------------------
    // Override unused tests
    // ----------------------------------------------------------------------

    /** Not used.
     * {@inheritDoc} */
    public void testAuthor()
    {
        // nop
    }

    /** Not used.
     * {@inheritDoc} */
    public void testDate()
    {
        // nop
    }

    /** Not used.
     * {@inheritDoc} */
    public void testHead()
    {
        // nop
    }

    /** Not used.
     * {@inheritDoc} */
    public void testBody()
    {
        // nop
    }

    /** Not used.
     * {@inheritDoc} */
    public void testTitle()
    {
        // nop
    }
}
