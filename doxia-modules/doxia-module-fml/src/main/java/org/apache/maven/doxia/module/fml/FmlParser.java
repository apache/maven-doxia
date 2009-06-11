package org.apache.maven.doxia.module.fml;

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

import java.io.Reader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.text.html.HTML.Attribute;

import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.module.fml.model.Faq;
import org.apache.maven.doxia.module.fml.model.Faqs;
import org.apache.maven.doxia.module.fml.model.Part;
import org.apache.maven.doxia.parser.AbstractXmlParser;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributeSet;
import org.apache.maven.doxia.util.DoxiaUtils;
import org.apache.maven.doxia.util.HtmlTools;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Parse a fml model and emit events into the specified doxia Sink.
 *
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @author ltheussl
 * @version $Id$
 * @since 1.0
 * @plexus.component role="org.apache.maven.doxia.parser.Parser" role-hint="fml"
 */
public class FmlParser
    extends AbstractXmlParser
    implements FmlMarkup
{
    /** Collect a faqs model. */
    private Faqs faqs;

    /** Collect a part. */
    private Part currentPart;

    /** Collect a single faq. */
    private Faq currentFaq;

    /** Used to collect text events. */
    private StringBuffer buffer;

    /** Map of warn messages with a String as key to describe the error type and a Set as value.
     * Using to reduce warn messages. */
    private Map warnMessages;

    /** {@inheritDoc} */
    public void parse( Reader source, Sink sink )
        throws ParseException
    {
        this.faqs = new Faqs();

        // this populates faqs
        super.parse( source, sink );

        writeFaqs( sink );

        logWarnings();
    }

    /** {@inheritDoc} */
    protected void handleStartTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        if ( parser.getName().equals( FAQS_TAG.toString() ) )
        {
            String title = parser.getAttributeValue( null, "title" );

            if ( title != null )
            {
                faqs.setTitle( title );
            }

            String toplink = parser.getAttributeValue( null, "toplink" );

            if ( toplink != null )
            {
                if ( toplink.equalsIgnoreCase( "true" ) )
                {
                    faqs.setToplink( true );
                }
                else
                {
                    faqs.setToplink( false );
                }
            }
        }
        else if ( parser.getName().equals( PART_TAG.toString() ) )
        {
            currentPart = new Part();

            currentPart.setId( parser.getAttributeValue( null, Attribute.ID.toString() ) );

            if ( currentPart.getId() == null )
            {
                throw new XmlPullParserException( "id attribute required for <part> at: ("
                    + parser.getLineNumber() + ":" + parser.getColumnNumber() + ")" );
            }
            else if ( !DoxiaUtils.isValidId( currentPart.getId() ) )
            {
                String linkAnchor = DoxiaUtils.encodeId( currentPart.getId(), true );

                String msg = "Modified invalid link: '" + currentPart.getId() + "' to '" + linkAnchor + "'";
                logMessage( "modifiedLink", msg );

                currentPart.setId( linkAnchor );
            }
        }
        else if ( parser.getName().equals( TITLE.toString() ) )
        {
            buffer = new StringBuffer();

            buffer.append( String.valueOf( LESS_THAN ) ).append( parser.getName() )
                .append( String.valueOf( GREATER_THAN ) );
        }
        else if ( parser.getName().equals( FAQ_TAG.toString() ) )
        {
            currentFaq = new Faq();

            currentFaq.setId( parser.getAttributeValue( null, Attribute.ID.toString() ) );

            if ( currentFaq.getId() == null )
            {
                throw new XmlPullParserException( "id attribute required for <faq> at: ("
                    + parser.getLineNumber() + ":" + parser.getColumnNumber() + ")" );
            }
            else if ( !DoxiaUtils.isValidId( currentFaq.getId() ) )
            {
                String linkAnchor = DoxiaUtils.encodeId( currentFaq.getId(), true );

                String msg = "Modified invalid link: '" + currentFaq.getId() + "' to '" + linkAnchor + "'";
                logMessage( "modifiedLink", msg );

                currentFaq.setId( linkAnchor );
            }
        }
        else if ( parser.getName().equals( QUESTION_TAG.toString() ) )
        {
            buffer = new StringBuffer();

            buffer.append( String.valueOf( LESS_THAN ) ).append( parser.getName() )
                .append( String.valueOf( GREATER_THAN ) );
        }
        else if ( parser.getName().equals( ANSWER_TAG.toString() ) )
        {
            buffer = new StringBuffer();

            buffer.append( String.valueOf( LESS_THAN ) ).append( parser.getName() )
                .append( String.valueOf( GREATER_THAN ) );

        }
        else if ( buffer != null )
        {
            buffer.append( String.valueOf( LESS_THAN ) ).append( parser.getName() );

            int count = parser.getAttributeCount();

            for ( int i = 0; i < count; i++ )
            {
                buffer.append( String.valueOf( SPACE ) ).append( parser.getAttributeName( i ) );

                buffer.append( String.valueOf( EQUAL ) ).append( String.valueOf( QUOTE ) );

                // TODO: why are attribute values HTML-encoded?
                buffer.append( HtmlTools.escapeHTML( parser.getAttributeValue( i ) ) );

                buffer.append( String.valueOf( QUOTE ) );
            }

            buffer.append( String.valueOf( GREATER_THAN ) );
        }
    }

    /** {@inheritDoc} */
    protected void handleEndTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        if ( parser.getName().equals( FAQS_TAG.toString() ) )
        {
            // Do nothing
            return;
        }
        else if ( parser.getName().equals( PART_TAG.toString() ) )
        {
            faqs.addPart( currentPart );

            currentPart = null;
        }
        else if ( parser.getName().equals( FAQ_TAG.toString() ) )
        {
            if ( currentPart == null )
            {
                throw new XmlPullParserException( "Missing <part>  at: ("
                    + parser.getLineNumber() + ":" + parser.getColumnNumber() + ")" );
            }

            currentPart.addFaq( currentFaq );

            currentFaq = null;
        }
        else if ( parser.getName().equals( QUESTION_TAG.toString() ) )
        {
            if ( currentFaq == null )
            {
                throw new XmlPullParserException( "Missing <faq> at: ("
                    + parser.getLineNumber() + ":" + parser.getColumnNumber() + ")" );
            }

            buffer.append( String.valueOf( LESS_THAN ) ).append( String.valueOf( SLASH ) )
                .append( parser.getName() ).append( String.valueOf( GREATER_THAN ) );

            currentFaq.setQuestion( buffer.toString() );

            buffer = null;
        }
        else if ( parser.getName().equals( ANSWER_TAG.toString() ) )
        {
            if ( currentFaq == null )
            {
                throw new XmlPullParserException( "Missing <faq> at: ("
                    + parser.getLineNumber() + ":" + parser.getColumnNumber() + ")" );
            }

            buffer.append( String.valueOf( LESS_THAN ) ).append( String.valueOf( SLASH ) )
                .append( parser.getName() ).append( String.valueOf( GREATER_THAN ) );

            currentFaq.setAnswer( buffer.toString() );

            buffer = null;
        }
        else if ( parser.getName().equals( TITLE.toString() ) )
        {
            if ( currentPart == null )
            {
                throw new XmlPullParserException( "Missing <part> at: ("
                    + parser.getLineNumber() + ":" + parser.getColumnNumber() + ")" );
            }

            buffer.append( String.valueOf( LESS_THAN ) ).append( String.valueOf( SLASH ) )
                .append( parser.getName() ).append( String.valueOf( GREATER_THAN ) );

            currentPart.setTitle( buffer.toString() );

            buffer = null;
        }
        else if ( buffer != null )
        {
            if ( buffer.length() > 0 && buffer.charAt( buffer.length() - 1 ) == SPACE )
            {
                buffer.deleteCharAt( buffer.length() - 1 );
            }

            buffer.append( String.valueOf( LESS_THAN ) ).append( String.valueOf( SLASH ) )
                .append( parser.getName() ).append( String.valueOf( GREATER_THAN ) );
        }
    }

    /** {@inheritDoc} */
    protected void handleText( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        if ( buffer != null )
        {
            buffer.append( parser.getText() );
        }
        // only significant text content in fml files is in <question>, <answer> or <title>
    }

    /** {@inheritDoc} */
    protected void handleCdsect( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String cdSection = parser.getText();

        if ( buffer != null )
        {
            buffer.append( LESS_THAN ).append( BANG ).append( LEFT_SQUARE_BRACKET ).append( CDATA )
                    .append( LEFT_SQUARE_BRACKET ).append( cdSection ).append( RIGHT_SQUARE_BRACKET )
                    .append( RIGHT_SQUARE_BRACKET ).append( GREATER_THAN );
        }
        else
        {
            sink.text( cdSection );
        }
    }

    /** {@inheritDoc} */
    protected void handleComment( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String comment = parser.getText();

        if ( buffer != null )
        {
            buffer.append( LESS_THAN ).append( BANG ).append( MINUS ).append( MINUS )
                    .append( comment ).append( MINUS ).append( MINUS ).append( GREATER_THAN );
        }
        else
        {
            sink.comment( comment.trim() );
        }
    }

    /** {@inheritDoc} */
    protected void handleEntity( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        if ( buffer != null )
        {
            if ( parser.getText() != null )
            {
                String text = parser.getText();

                // parser.getText() returns the entity replacement text
                // (&lt; -> <), need to re-escape them
                if ( text.length() == 1 )
                {
                    text = HtmlTools.escapeHTML( text );
                }

                buffer.append( text );
            }
        }
        else
        {
            super.handleEntity( parser, sink );
        }
    }

    /**
     * Writes the faqs to the specified sink.
     *
     * @param faqs The faqs to emit.
     * @param sink The sink to consume the event.
     * @throws ParseException if something goes wrong.
     */
    private void writeFaqs( Sink sink )
        throws ParseException
    {
        FmlContentParser xdocParser = new FmlContentParser();
        xdocParser.enableLogging( getLog() );

        sink.head();
        sink.title();
        sink.text( faqs.getTitle() );
        sink.title_();
        sink.head_();

        sink.body();
        sink.section1();
        sink.sectionTitle1();
        sink.anchor( "top" );
        sink.text( faqs.getTitle() );
        sink.anchor_();
        sink.sectionTitle1_();

        // ----------------------------------------------------------------------
        // Write summary
        // ----------------------------------------------------------------------

        for ( Iterator partIterator = faqs.getParts().iterator(); partIterator.hasNext(); )
        {
            Part part = (Part) partIterator.next();

            if ( StringUtils.isNotEmpty( part.getTitle() ) )
            {
                sink.paragraph();
                sink.bold();
                xdocParser.parse( part.getTitle(), sink );
                sink.bold_();
                sink.paragraph_();
            }

            sink.numberedList( Sink.NUMBERING_DECIMAL );

            for ( Iterator faqIterator = part.getFaqs().iterator(); faqIterator.hasNext(); )
            {
                Faq faq = (Faq) faqIterator.next();
                sink.numberedListItem();
                sink.link( "#" + faq.getId() );

                if ( StringUtils.isNotEmpty( faq.getQuestion() ) )
                {
                    xdocParser.parse( faq.getQuestion(), sink );
                }
                else
                {
                    throw new ParseException( "Missing <question> for FAQ '" + faq.getId() + "'" );
                }

                sink.link_();
                sink.numberedListItem_();
            }

            sink.numberedList_();
        }

        sink.section1_();

        // ----------------------------------------------------------------------
        // Write content
        // ----------------------------------------------------------------------

        for ( Iterator partIterator = faqs.getParts().iterator(); partIterator.hasNext(); )
        {
            Part part = (Part) partIterator.next();

            if ( StringUtils.isNotEmpty( part.getTitle() ) )
            {
                sink.section1();

                sink.sectionTitle1();
                xdocParser.parse( part.getTitle(), sink );
                sink.sectionTitle1_();
            }

            sink.definitionList();

            for ( Iterator faqIterator = part.getFaqs().iterator(); faqIterator.hasNext(); )
            {
                Faq faq = (Faq) faqIterator.next();

                sink.definedTerm();
                sink.anchor( faq.getId() );

                if ( StringUtils.isNotEmpty( faq.getQuestion() ) )
                {
                    xdocParser.parse( faq.getQuestion(), sink );
                }
                else
                {
                    throw new ParseException( "Missing <question> for FAQ '" + faq.getId() + "'" );
                }

                sink.anchor_();
                sink.definedTerm_();

                sink.definition();

                if ( StringUtils.isNotEmpty( faq.getAnswer() ) )
                {
                    xdocParser.parse( faq.getAnswer(), sink );
                }
                else
                {
                    throw new ParseException( "Missing <answer> for FAQ '" + faq.getId() + "'" );
                }

                if ( faqs.isToplink() )
                {
                    writeTopLink( sink );
                }

                if ( faqIterator.hasNext() )
                {
                    sink.horizontalRule();
                }

                sink.definition_();
            }

            sink.definitionList_();

            if ( StringUtils.isNotEmpty( part.getTitle() ) )
            {
                sink.section1_();
            }
        }

        sink.body_();
    }

    /**
     * Writes a toplink element.
     *
     * @param sink The sink to consume the event.
     */
    private void writeTopLink( Sink sink )
    {
        SinkEventAttributeSet atts = new SinkEventAttributeSet();
        atts.addAttribute( SinkEventAttributeSet.ALIGN, "right" );
        sink.paragraph( atts );
        sink.link( "#top" );
        sink.text( "[top]" );
        sink.link_();
        sink.paragraph_();
    }

    /**
     * If debug mode is enabled, log the <code>msg</code> as is, otherwise add unique msg in <code>warnMessages</code>.
     *
     * @param key not null
     * @param msg not null
     * @see #parse(Reader, Sink)
     * @since 1.1.1
     */
    private void logMessage( String key, String msg )
    {
        msg = "[FML Parser] " + msg;
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( msg );

            return;
        }

        if ( warnMessages == null )
        {
            warnMessages = new HashMap();
        }

        Set set = (Set) warnMessages.get( key );
        if ( set == null )
        {
            set = new TreeSet();
        }
        set.add( msg );
        warnMessages.put( key, set );
    }

    private void logWarnings()
    {
        if ( getLog().isWarnEnabled() && this.warnMessages != null && !isSecondParsing() )
        {
            for ( Iterator it = this.warnMessages.entrySet().iterator(); it.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) it.next();
                Set set = (Set) entry.getValue();
                for ( Iterator it2 = set.iterator(); it2.hasNext(); )
                {
                    String msg = (String) it2.next();
                    getLog().warn( msg );
                }
            }
            this.warnMessages = null;
        }
    }
}
