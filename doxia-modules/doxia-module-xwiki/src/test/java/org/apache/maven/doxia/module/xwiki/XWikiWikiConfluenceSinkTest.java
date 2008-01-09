package org.apache.maven.doxia.module.xwiki;

import org.apache.maven.doxia.module.confluence.ConfluenceParser;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.AbstractSinkTestCase;
import org.apache.maven.doxia.sink.Sink;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class XWikiWikiConfluenceSinkTest
    extends AbstractSinkTestCase
{
    protected String outputExtension()
    {
        return "confluence.xwiki";
    }

    protected Parser createParser()
    {
        return new ConfluenceParser();
    }

    protected Sink createSink()
        throws Exception
    {
        return new XWikiWikiSink( getTestWriter() );
    }

    protected Reader getTestReader()
        throws Exception
    {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( "test.confluence" );

        InputStreamReader reader = new InputStreamReader( is );

        return reader;
    }
}
