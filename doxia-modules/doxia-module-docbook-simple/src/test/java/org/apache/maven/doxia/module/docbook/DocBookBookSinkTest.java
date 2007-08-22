package org.apache.maven.doxia.module.docbook;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.AbstractSinkTestCase;
import org.apache.maven.doxia.sink.Sink;

/**
 * Test the book path of the DockBook sink
 * @author eredmond
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
        return new DocBookSink( getTestWriter(), true );
    }

    protected Reader getTestReader() throws Exception
    {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( "book.xml" );

        InputStreamReader reader = new InputStreamReader( is );

        return reader;
    }
}
