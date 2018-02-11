package org.apache.maven.doxia.module.docbook;

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
import java.util.Locale;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.AbstractSinkTest;
import org.apache.maven.doxia.sink.impl.SinkUtils;
import org.apache.maven.doxia.util.DoxiaUtils;
import org.codehaus.plexus.util.FileUtils;

import static org.apache.maven.doxia.util.HtmlTools.escapeHTML;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DocBookSinkTest extends AbstractSinkTest
{
    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "docbook";
    }

    /** {@inheritDoc} */
    protected Sink createSink( Writer writer )
    {
        return new DocBookSink( writer, "UTF-8" );
    }

    /** {@inheritDoc} */
    protected boolean isXmlSink()
    {
        return true;
    }

    /** {@inheritDoc} */
    protected String getTitleBlock( String title )
    {
        return "<articleinfo><title>" + title + "</title>";
    }

    /** {@inheritDoc} */
    protected String getAuthorBlock( String author )
    {
        return "<corpauthor>" + author + "</corpauthor>";
    }

    /** {@inheritDoc} */
    protected String getDateBlock( String date )
    {
        return "<date>" + date + "</date>";
    }

    /** {@inheritDoc} */
    protected String getHeadBlock()
    {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE article PUBLIC \""
                + SimplifiedDocbookMarkup.DEFAULT_XML_PUBLIC_ID + "\" "
                + "\"" + SimplifiedDocbookMarkup.DEFAULT_XML_SYSTEM_ID + "\"><article>";
    }

    /** {@inheritDoc} */
    protected String getBodyBlock()
    {
        return "</article>";
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
        return "<sidebar></sidebar>";
    }

    /** {@inheritDoc} */
    protected String getSectionTitleBlock( String title )
    {
        return "<title>" + title + "</title>";
    }

    /** {@inheritDoc} */
    protected String getSection1Block( String title )
    {
        return "<section><sectioninfo><title>" + title + "</title></sectioninfo></section>";
    }

    /** {@inheritDoc} */
    protected String getSection2Block( String title )
    {
        return "<section><sectioninfo><title>" + title + "</title></sectioninfo></section>";
    }

    /** {@inheritDoc} */
    protected String getSection3Block( String title )
    {
        return "<section><sectioninfo><title>" + title + "</title></sectioninfo></section>";
    }

    /** {@inheritDoc} */
    protected String getSection4Block( String title )
    {
        return "<section><sectioninfo><title>" + title + "</title></sectioninfo></section>";
    }

    /** {@inheritDoc} */
    protected String getSection5Block( String title )
    {
        return "<section><sectioninfo><title>" + title + "</title></sectioninfo></section>";
    }

    /** {@inheritDoc} */
    protected String getHeaderBlock()
    {
        return "<sectioninfo></sectioninfo>";
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
        return "<itemizedlist><listitem><para>" + item  + "</para></listitem>" + "</itemizedlist>";
    }

    /** {@inheritDoc} */
    protected String getNumberedListBlock( String item )
    {
        return "<orderedlist numeration=\"lowerroman\"><listitem><para>"
            + item  + "</para></listitem>" + "</orderedlist>";
    }

    /** {@inheritDoc} */
    protected String getDefinitionListBlock( String definum, String definition )
    {
        return "<variablelist><varlistentry><term>" + definum
            + "</term>" + "<listitem><para>" + definition
            + "</para></listitem>" + "</varlistentry>" + "</variablelist>";
    }

    /** {@inheritDoc} */
    protected String getFigureBlock( String source, String caption )
    {
        String format = FileUtils.extension( source ).toUpperCase( Locale.ENGLISH );
        String figureBlock = "<mediaobject><imageobject>"
                + "<imagedata fileref=\"" + escapeHTML( source ) + "\" format=\"" + escapeHTML( format ) + "\" />"
                + "</imageobject>";
        if ( caption != null )
        {
            figureBlock += "<caption><para>" + caption + "</para></caption>";
        }
        figureBlock += "</mediaobject>";
        
        return figureBlock;
    }

    /** {@inheritDoc} */
    protected String getTableBlock( String cell, String caption )
    {
        // Using the same set ordering than the JVM
        MutableAttributeSet att = new SimpleAttributeSet();
        att.addAttribute( "frame", "none" );
        att.addAttribute( "rowsep", "0" );
        att.addAttribute( "colsep", "0" );

        return "<table" + SinkUtils.getAttributeString( att ) + "><title>" + caption
            + "</title>" + "<tgroup cols=\"1\"><colspec align=\"center\" />" + "<tbody><row><entry>"
            + cell  + "</entry>" + "</row>" + "</tbody></tgroup>" + "</table>";
    }

    /** {@inheritDoc} */
    protected String getParagraphBlock( String text )
    {
        return "<para>" + text + "</para>";
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
        return text;
    }

    /** {@inheritDoc} */
    protected String getBlockquoteBlock( String text )
    {
        return text;
    }

    /** {@inheritDoc} */
    protected String getDivisionBlock( String text )
    {
        return text;
    }

    /** {@inheritDoc} */
    protected String getVerbatimBlock( String text )
    {
        return "<programlisting>" + text + "</programlisting>";
    }

    /** {@inheritDoc} */
    protected String getHorizontalRuleBlock()
    {
        return "<!-- HR -->";
    }

    /** {@inheritDoc} */
    protected String getPageBreakBlock()
    {
        return "<!-- PB -->";
    }

    /** {@inheritDoc} */
    protected String getAnchorBlock( String anchor )
    {
        return "<anchor id=\"" + anchor + "\" />" + anchor + "<!-- anchor_end -->";
    }

    /** {@inheritDoc} */
    protected String getLinkBlock( String link, String text )
    {
        String linkend = DoxiaUtils.isInternalLink( link ) ? link.substring( 1 ) : link;
        return "<link linkend=\"" + linkend + "\">" + text + "</link>";
    }

    /** {@inheritDoc} */
    protected String getInlineBlock( String text )
    {
        return text;
    }

    /** {@inheritDoc} */
    protected String getInlineItalicBlock( String text )
    {
        return "<emphasis>" + text + "</emphasis>";
    }

    /** {@inheritDoc} */
    protected String getInlineBoldBlock( String text )
    {
        return "<emphasis role=\"bold\">" + text + "</emphasis>";
    }

    /** {@inheritDoc} */
    protected String getInlineCodeBlock( String text )
    {
        return "<literal>" + text + "</literal>";
    }

    /** {@inheritDoc} */
    protected String getItalicBlock( String text )
    {
        return "<emphasis>" + text + "</emphasis>";
    }

    /** {@inheritDoc} */
    protected String getBoldBlock( String text )
    {
        return "<emphasis role=\"bold\">" + text + "</emphasis>";
    }

    /** {@inheritDoc} */
    protected String getMonospacedBlock( String text )
    {
        return "<literal>" + text + "</literal>";
    }

    /** {@inheritDoc} */
    protected String getLineBreakBlock()
    {
        return "<!-- LB -->";
    }

    /** {@inheritDoc} */
    protected String getLineBreakOpportunityBlock()
    {
        return "";
    }

    /** {@inheritDoc} */
    protected String getNonBreakingSpaceBlock()
    {
        return "&#x00A0;";
    }

    /** {@inheritDoc} */
    protected String getTextBlock( String text )
    {
        // TODO: retreive those from the sink
        return "~,_=,_-,_+,_*,_[,_],_&lt;,_&gt;,_{,_},_\\";
    }

    /** {@inheritDoc} */
    protected String getRawTextBlock( String text )
    {
        // TODO
        return "";
    }

    /** {@inheritDoc} */
    protected String getCommentBlock( String text )
    {
        return "<!--" + toXmlComment( text ) + "-->";
    }
}
