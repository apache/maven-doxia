package org.apache.maven.doxia.book.services.renderer.docbook;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.maven.doxia.module.docbook.DocBookParser;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.AbstractSinkTestCase;
import org.apache.maven.doxia.sink.Sink;

/**
 * Test the book path of the DockBook sink
 * @author Dave Syer
 */
public class DocBookBookSinkTest extends AbstractSinkTestCase
{
    protected String outputExtension()
    {
        return "docbook";
    }

    protected Parser createParser()
    {
        return new DocBookParser();
    }

    protected Sink createSink() throws Exception
    {
        return new DocBookBookSink( getTestWriter() );
    }

    protected Reader getTestReader() throws Exception
    {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( "book-1.xml" );

        InputStreamReader reader = new InputStreamReader( is );

        return reader;
    }
}
