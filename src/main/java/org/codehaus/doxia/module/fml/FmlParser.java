package org.codehaus.doxia.module.fml;

import org.codehaus.doxia.module.fml.model.Faq;
import org.codehaus.doxia.module.fml.model.Faqs;
import org.codehaus.doxia.module.fml.model.Part;
import org.codehaus.doxia.parser.Parser;
import org.codehaus.doxia.parser.ParseException;
import org.codehaus.doxia.sink.Sink;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.StringUtils;

import java.io.Reader;
import java.util.Iterator;

/**
 * Parse a fml document and emit events into the specified doxia Sink.
 * 
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 * 
 * @plexus.component
 *   role="org.codehaus.doxia.parser.Parser"
 *   role-hint="fml"
 */
public class FmlParser
    implements Parser
{
    public void parse( Reader reader, Sink sink )
        throws ParseException
    {
        Faqs faqs;
        try
        {
            XmlPullParser parser = new MXParser();

            parser.setInput( reader );

            faqs = parseFml( parser, sink );
        }
        catch ( Exception ex )
        {
            throw new ParseException( "Error parsing the model.", ex );
        }

        createSink( faqs, sink );
    }

    /**
     * @param parser
     * @param sink
     * @throws Exception
     */
    public Faqs parseFml( XmlPullParser parser, Sink sink ) throws Exception
    {
        Faqs faqs = new Faqs();

        Part currentPart = null;

        Faq currentFaq = null;

        boolean inFaq = false;

        boolean inPart = false;

        boolean inQuestion = false;

        boolean inAnswer = false;

        StringBuffer buffer = null;

        int eventType = parser.getEventType();

        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
                if ( parser.getName().equals( "faqs" ) )
                {
                    String title = parser.getAttributeValue( null, "title" );
                    if ( title != null )
                    {
                        faqs.setTitle( title );
                    }
                }
                else if ( parser.getName().equals( "part" ) )
                {
                    inPart = true;
                    currentPart = new Part();
                    currentPart.setId( parser.getAttributeValue( null, "id" ) );
                }
                else if ( parser.getName().equals( "title" ) )
                {
                    currentPart.setTitle( parser.nextText().trim() );
                }
                else if ( parser.getName().equals( "faq" ) )
                {
                    inFaq = true;
                    currentFaq = new Faq();
                    currentFaq.setId( parser.getAttributeValue( null, "id" ) );
                }
                if ( parser.getName().equals( "question" ) )
                {
                    buffer = new StringBuffer();
                    inQuestion = true;
                }
                else if ( parser.getName().equals( "answer" ) )
                {
                    buffer = new StringBuffer();
                    inAnswer = true;
                }
                else if ( inQuestion || inAnswer )
                {
                    buffer.append( "<" );

                    buffer.append( parser.getName() );

                    int count = parser.getAttributeCount();

                    for ( int i = 0; i < count; i++ )
                    {
                        buffer.append( " " );

                        buffer.append( parser.getAttributeName( i ) );

                        buffer.append( "=" );

                        buffer.append( "\"" );

                        buffer.append( parser.getAttributeValue( i ) );

                        buffer.append( "\"" );
                    }

                    buffer.append( ">" );
                }
            }
            else if ( eventType == XmlPullParser.END_TAG )
            {
                if ( parser.getName().equals( "faqs" ) )
                {
                    // Do nothing
                }
                else if ( parser.getName().equals( "part" ) )
                {
                    faqs.addPart( currentPart );

                    currentPart = null;

                    inPart = false;
                }
                else if ( parser.getName().equals( "faq" ) )
                {
                    currentPart.addFaq( currentFaq );

                    currentFaq = null;

                    inFaq = false;
                }
                if ( parser.getName().equals( "question" ) )
                {
                    currentFaq.setQuestion( buffer.toString() );

                    inQuestion = false;
                }
                else if ( parser.getName().equals( "answer" ) )
                {
                    currentFaq.setAnswer( buffer.toString() );

                    inAnswer = false;
                }
                else if ( inQuestion || inAnswer )
                {
                    buffer.append( "</" );

                    buffer.append( parser.getName() );

                    buffer.append( ">" );
                }
            }
            else if ( eventType == XmlPullParser.TEXT )
            {
                if ( buffer != null && parser.getText() != null )
                {
                    buffer.append( parser.getText() );
                }
            }

            eventType = parser.next();
        }

        return faqs;
    }

    private void createSink( Faqs faqs, Sink sink )
    {
        sink.head();
        sink.title();
        sink.text( faqs.getTitle() );
        sink.title_();
        sink.head_();

        sink.body();
        sink.section1();
        sink.sectionTitle1();
        sink.text( faqs.getTitle() );
        sink.sectionTitle1_();

        // Write summary
        for ( Iterator partIterator = faqs.getParts().iterator(); partIterator.hasNext(); )
        {
            Part part = (Part) partIterator.next();
            if ( StringUtils.isNotEmpty( part.getTitle() ) )
            {
                sink.paragraph();
                sink.bold();
                sink.text( part.getTitle() );
                sink.bold_();
                sink.paragraph_();
            }

            sink.numberedList( Sink.NUMBERING_DECIMAL );
            for ( Iterator faqIterator = part.getFaqs().iterator(); faqIterator.hasNext(); )
            {
                Faq faq = (Faq) faqIterator.next();
                sink.numberedListItem();
                sink.link( "#" + faq.getId() );
                sink.text( faq.getQuestion() );
                sink.link_();
                sink.numberedListItem_();
            }
            sink.numberedList_();
        }
        sink.section1_();

        // Write content
        for ( Iterator partIterator = faqs.getParts().iterator(); partIterator.hasNext(); )
        {
            Part part = (Part) partIterator.next();
            sink.section1();
            if ( StringUtils.isNotEmpty( part.getTitle() ) )
            {
                sink.sectionTitle1();
                sink.text( part.getTitle() );
                sink.sectionTitle1_();
            }

            sink.definitionList();
            for ( Iterator faqIterator = part.getFaqs().iterator(); faqIterator.hasNext(); )
            {
                Faq faq = (Faq) faqIterator.next();
                sink.definedTerm();
                sink.anchor( faq.getId() );
                sink.rawText( faq.getQuestion() );
                sink.anchor_();
                sink.definedTerm_();
                sink.definition();
                sink.paragraph();
                writeAnswer( sink, faq.getAnswer() );
                sink.paragraph_();
                if ( faqIterator.hasNext() )
                {
                    sink.horizontalRule();
                }
                sink.definition_();
            }
            sink.definitionList_();
            sink.section1_();
        }

        sink.body_();
    }

    private void writeAnswer( Sink sink, String answer )
    {
        int startSource = answer.indexOf( "<source>" );
        int endSource = answer.lastIndexOf( "</source>" );
        if ( startSource != -1 )
        {
            sink.rawText( answer.substring( 0, startSource ) );
            sink.verbatim( true );
            sink.text( answer.substring( startSource + "<source>".length(), endSource ) );
            sink.verbatim_();
            sink.rawText( answer.substring( endSource + "</source>".length() ) );
        }
        else
        {
            sink.rawText( answer );
        }
    }
}
