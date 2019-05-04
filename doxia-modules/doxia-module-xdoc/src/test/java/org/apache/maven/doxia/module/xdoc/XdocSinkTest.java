package org.apache.maven.doxia.module.xdoc;

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

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.AbstractSinkTest;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.util.HtmlTools;

import java.io.StringWriter;
import java.io.Writer;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @since 1.0
 */
public class XdocSinkTest
    extends AbstractSinkTest
{
    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "xml";
    }

    /** {@inheritDoc} */
    protected Sink createSink( Writer writer )
    {
        return new XdocSink( writer, "UTF-8" );
    }

    /** {@inheritDoc} */
    protected boolean isXmlSink()
    {
        return true;
    }

    /** {@inheritDoc} */
    protected String getTitleBlock( String title )
    {
        return "<title>" + title + "</title>";
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
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<document xmlns=\"http://maven.apache.org/XDOC/2.0\" "
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xsi:schemaLocation=\"http://maven.apache.org/XDOC/2.0 http://maven.apache.org/xsd/xdoc-2.0.xsd\">"
            + "<properties></properties>";
    }

    /** {@inheritDoc} */
    protected String getBodyBlock()
    {
        return "<body></body></document>";
    }

    /** {@inheritDoc} */
    protected String getArticleBlock()
    {
        return "";
    }

    /** {@inheritDoc} */
    protected String getNavigationBlock()
    {
        return "";
    }

    /** {@inheritDoc} */
    protected String getSidebarBlock()
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
        return "<section name=\"" + title + "\"></section>";
    }

    /** {@inheritDoc} */
    protected String getSection2Block( String title )
    {
        return "<subsection name=\"" + title + "\"></subsection>";
    }

    /** {@inheritDoc} */
    protected String getSection3Block( String title )
    {
        return "<h4>" + title + "</h4>";
    }

    /** {@inheritDoc} */
    protected String getSection4Block( String title )
    {
        return "<h5>" + title + "</h5>";
    }

    /** {@inheritDoc} */
    protected String getSection5Block( String title )
    {
        return "<h6>" + title + "</h6>";
    }

    /** {@inheritDoc} */
    protected String getHeaderBlock()
    {
        return "";
    }

    /** {@inheritDoc} */
    protected String getContentBlock()
    {
        return "";
    }

    /** {@inheritDoc} */
    protected String getFooterBlock()
    {
        return "";
    }

    /** {@inheritDoc} */
    protected String getListBlock( String item )
    {
        return "<ul>\n<li>" + item + "</li></ul>";
    }

    /** {@inheritDoc} */
    protected String getNumberedListBlock( String item )
    {
        return "<ol style=\"list-style-type: lower-roman\">\n<li>" + item + "</li></ol>";
    }

    /** {@inheritDoc} */
    protected String getDefinitionListBlock( String definum, String definition )
    {
        return "<dl>\n<dt>" + definum + "</dt>\n<dd>" + definition + "</dd></dl>";
    }

    /** {@inheritDoc} */
    protected String getFigureBlock( String source, String caption )
    {
        String figureBlock = "<img src=\"" + source + "\"";
        if ( caption != null )
        {
            figureBlock += " alt=\"" + caption + "\"";
        }
        else //@todo fix DOXIA-361
        {
            figureBlock += " alt=\"\"";
        }
        figureBlock += " />";
        return figureBlock;
    }

    /** {@inheritDoc} */
    protected String getTableBlock( String cell, String caption )
    {
        return "<table border=\"0\"><caption>" + caption
                + "</caption>\n<tr valign=\"top\">\n<td>" + cell + "</td></tr></table>";
    }

    /** {@inheritDoc} */
    protected String getParagraphBlock( String text )
    {
        return "<p>" + text + "</p>";
    }

    /** {@inheritDoc} */
    protected String getDataBlock( String value, String text )
    {
        return text;
    }

    /** {@inheritDoc} */
    protected String getTimeBlock( String datetime, String text )
    {
        return text;
    }

    /** {@inheritDoc} */
    protected String getAddressBlock( String text )
    {
        return "<address>" + text + "</address>";
    }

    /** {@inheritDoc} */
    protected String getBlockquoteBlock( String text )
    {
        return "<blockquote>" + text + "</blockquote>";
    }

    /** {@inheritDoc} */
    protected String getDivisionBlock( String text )
    {
        return "<div>" + text + "</div>";
    }

    /** {@inheritDoc} */
    protected String getVerbatimBlock( String text )
    {
        return "<source>" + text + "</source>";
    }

    /** {@inheritDoc} */
    protected String getHorizontalRuleBlock()
    {
        return "<hr />";
    }

    /** {@inheritDoc} */
    protected String getPageBreakBlock()
    {
        return "<!-- PB -->";
    }

    /** {@inheritDoc} */
    protected String getAnchorBlock( String anchor )
    {
        return "<a name=\"" + anchor + "\">" + anchor + "</a>";
    }

    /** {@inheritDoc} */
    protected String getLinkBlock( String link, String text )
    {
        return "<a href=\"" + link + "\">" + text + "</a>";
    }

    /** {@inheritDoc} */
    protected String getInlineBlock( String text )
    {
        return text;
    }

    /** {@inheritDoc} */
    protected String getInlineItalicBlock( String text )
    {
        return "<i>" + text + "</i>";
    }

    /** {@inheritDoc} */
    protected String getInlineBoldBlock( String text )
    {
        return "<b>" + text + "</b>";
    }

    /** {@inheritDoc} */
    protected String getInlineCodeBlock( String text )
    {
        return "<code>" + text + "</code>";
    }

    /** {@inheritDoc} */
    protected String getItalicBlock( String text )
    {
        return "<i>" + text + "</i>";
    }

    /** {@inheritDoc} */
    protected String getBoldBlock( String text )
    {
        return "<b>" + text + "</b>";
    }

    /** {@inheritDoc} */
    protected String getMonospacedBlock( String text )
    {
        return "<tt>" + text + "</tt>";
    }

    /** {@inheritDoc} */
    protected String getLineBreakBlock()
    {
        return "<br />";
    }

    /** {@inheritDoc} */
    protected String getLineBreakOpportunityBlock()
    {
        return "";
    }

    /** {@inheritDoc} */
    protected String getNonBreakingSpaceBlock()
    {
        return "&#160;";
    }

    /** {@inheritDoc} */
    protected String getTextBlock( String text )
    {
        // TODO: need to be able to retreive those from outside the sink
        return HtmlTools.escapeHTML( text );
    }

    /** {@inheritDoc} */
    protected String getRawTextBlock( String text )
    {
        return "~,_=,_-,_+,_*,_[,_],_<,_>,_{,_},_\\";
    }

    /**
     * Test verbatim.
     */
    public void testBoxedVerbatim()
    {
        Writer writer =  new StringWriter();
        XdocSink sink = null;

        try
        {
            sink = new XdocSink( writer );

            sink.verbatim( null );
            sink.verbatim_();
            sink.verbatim( SinkEventAttributeSet.BOXED );
            sink.verbatim_();
            sink.verbatim( new SinkEventAttributeSet( SinkEventAttributeSet.WIDTH, "20%" ) );
            sink.verbatim_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<pre></pre><source></source>\n<pre width=\"20%\"></pre>", writer.toString() );
    }

    /**
     * Test link.
     */
    public void testLinkWithTarget()
    {
        Writer writer =  new StringWriter();
        XdocSink sink = null;

        try
        {
            sink = new XdocSink( writer );

            sink.link( "name", (String) null );
            sink.link_();
            sink.link( "name", "nirvana" );
            sink.link_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<a href=\"name\"></a><a href=\"name\" target=\"nirvana\"></a>", writer.toString() );
    }

    /** {@inheritDoc} */
    protected String getCommentBlock( String text )
    {
        return "<!--" + toXmlComment( text ) + "-->";
    }
}
