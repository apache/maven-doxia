package org.apache.maven.doxia.module.confluence;

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

/**
 * Test the Confluence Sink
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @see ConfluenceSink
 */
public class ConfluenceSinkTest
    extends AbstractSinkTest
{
    /** {@inheritDoc} */
    protected Sink createSink( Writer writer )
    {
        return new ConfluenceSink( writer );
    }

    /** {@inheritDoc} */
    protected String getAnchorBlock( String anchor )
    {
        return ConfluenceMarkup.ANCHOR_START_MARKUP + anchor + ConfluenceMarkup.ANCHOR_END_MARKUP + anchor;
    }

    /** {@inheritDoc} */
    protected boolean isXmlSink()
    {
        return false;
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
        return ConfluenceMarkup.BOLD_START_MARKUP + text + ConfluenceMarkup.BOLD_END_MARKUP;
    }

    /** Not used.
     * {@inheritDoc} */
    protected String getDateBlock( String date )
    {
        return null;
    }

    /** Not used.
     * {@inheritDoc} */
    protected String getDefinitionListBlock( String definum, String definition )
    {
        return null;
    }

    /** {@inheritDoc} */
    protected String getFigureBlock( String source, String caption )
    {
        String figureBlock = EOL + ConfluenceMarkup.FIGURE_START_MARKUP + source + ConfluenceMarkup.FIGURE_END_MARKUP;
        if ( caption != null )
        {
            figureBlock += caption;
        }
        return figureBlock;
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
        return "";
    }

    /** {@inheritDoc} */
    protected String getItalicBlock( String text )
    {
        return ConfluenceMarkup.ITALIC_START_MARKUP + text + ConfluenceMarkup.ITALIC_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getLineBreakBlock()
    {
        return ConfluenceMarkup.LINE_BREAK_MARKUP + EOL;
    }

    /** {@inheritDoc} */
    protected String getLinkBlock( String link, String text )
    {
        return ConfluenceMarkup.LINK_START_MARKUP + text + ConfluenceMarkup.LINK_MIDDLE_MARKUP + link
            + ConfluenceMarkup.LINK_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getListBlock( String item )
    {
        return ConfluenceMarkup.LIST_ITEM_MARKUP + item + EOL;
    }

    /** {@inheritDoc} */
    protected String getMonospacedBlock( String text )
    {
        return ConfluenceMarkup.MONOSPACED_START_MARKUP + text + ConfluenceMarkup.MONOSPACED_END_MARKUP;
    }

    /** {@inheritDoc} */
    protected String getNonBreakingSpaceBlock()
    {
        return "";
    }

    /** {@inheritDoc} */
    protected String getNumberedListBlock( String item )
    {
        return ConfluenceMarkup.NUMBERING_MARKUP + " " + item + EOL;
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
        return "h1. " + title + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection2Block( String title )
    {
        return "h2. " + title + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection3Block( String title )
    {
        return "h3. " + title + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection4Block( String title )
    {
        return "h4. " + title + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getSection5Block( String title )
    {
        return "h5. " + title + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String getSectionTitleBlock( String title )
    {
        return title;
    }

    /** {@inheritDoc} */
    protected String getTableBlock( String cell, String caption )
    {
        return "| " + cell + " |" + EOL + "Table_caption" + EOL + EOL;
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
        return "{code|borderStyle=solid}" + EOL + text + "{code}" + EOL + EOL;
    }

    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "twiki";
    }
    
    // ----------------------------------------------------------------------
    // Confluence specific 
    // ----------------------------------------------------------------------

    public void testSuperScript()
    {
        verifyValignSup( "^ValignSup^" );
    }

    public void testSubScript()
    {
        verifyValignSub( "~ValignSub~" );
    }

    public void testStrikeStrough()
    {
        verifyDecorationLineThrough( "-DecorationLineThrough-" );
    }

    public void testUnderline()
    {
        verifyDecorationUnderline( "+DecorationUnderline+" );
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

    /** Not used.
     * {@inheritDoc} */
    public void testDefinitionList()
    {
        // nop
    }

    /** {@inheritDoc} */
    protected String getCommentBlock( String text )
    {
        return "";
    }
}
