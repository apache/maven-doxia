package org.apache.maven.doxia.editor.io;

import org.apache.maven.doxia.editor.model.DoxiaAttribute;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import java.io.Reader;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DocumentParser
    implements Parser
{
    public static ThreadLocal document = new ThreadLocal();

    private boolean inHead;

    private boolean startedBody;

    public void parse( Reader source, Sink sink )
        throws ParseException
    {
        Document document = (Document) DocumentParser.document.get();

        System.out.println( "document.getLength() = " + document.getLength() );

        try
        {
            inHead = true;

            sink.head();
            dump( document.getDefaultRootElement(), sink );
            sink.body_();
        }
        catch ( BadLocationException e )
        {
            System.out.println( "e.offsetRequested() = " + e.offsetRequested() );
            throw new ParseException( "Error while parsing document.", e );
        }
    }

    private void dump( Element element, Sink sink )
        throws BadLocationException
    {
        if ( !inHead && !startedBody )
        {
            sink.head_();
            sink.body();

            startedBody = true;
        }

        AttributeSet attributes = element.getAttributes();
        Object type = attributes.getAttribute( DoxiaAttribute.TYPE );

        if ( type != null )
        {
            System.out.println( "type: " + type );

            if ( type == DoxiaAttribute.TITLE )
            {
                sink.title();
                sink.text( getText( element ) );
                sink.title_();
            }
            else if ( type == DoxiaAttribute.AUTHOR )
            {
                sink.author();
                sink.text( getText( element ) );
                sink.author_();
            }
            else if ( type == DoxiaAttribute.DATE )
            {
                sink.date();
                sink.text( getText( element ) );
                sink.date_();
            }
            else if ( type == DoxiaAttribute.SECTION_1 )
            {
                sink.section1();
                sink.text( getText( element ) );
                sink.section1_();
                inHead = false;
            }
            else if ( type == DoxiaAttribute.SECTION_2 )
            {
                sink.section2();
                sink.text( getText( element ) );
                sink.section2_();
                inHead = false;
            }
            else if ( type == DoxiaAttribute.SECTION_3 )
            {
                sink.section3();
                sink.text( getText( element ) );
                sink.section3_();
                inHead = false;
            }
            else if ( type == DoxiaAttribute.SECTION_4 )
            {
                sink.section4();
                sink.text( getText( element ) );
                sink.section4_();
                inHead = false;
            }
            else if ( type == DoxiaAttribute.SECTION_5 )
            {
                sink.section5();
                sink.text( getText( element ) );
                sink.section5_();
                inHead = false;
            }
            else if ( type == DoxiaAttribute.PARAGRAPH_SEPARATOR )
            {
                sink.paragraph_();
                sink.paragraph();
                inHead = false;
            }
            else if ( type == DoxiaAttribute.TEXT )
            {
                sink.text( getText( element ) );
                inHead = false;
            }
        }

        for ( int i = 0; i < element.getElementCount(); i++ )
        {
            dump( element.getElement( i ), sink );
        }
    }

    private String getText( Element element )
        throws BadLocationException
    {
        int startOffset = element.getStartOffset();
        int endOffset = element.getEndOffset();

        return element.getDocument().getText( startOffset, endOffset - startOffset ).trim();
    }
}
