package org.apache.maven.doxia.module.xwiki;

import junit.framework.TestCase;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkTestDocument;

import java.io.StringWriter;
import java.io.Writer;

public class XWikiWikiSink2Test
    extends TestCase
{
    // TODO: Need to decide if this test is better than XWikiWikiSinkTest.
    public void testSink()
    {
        Writer writer = new StringWriter();
        Sink sink = new XWikiWikiSink( writer );
        SinkTestDocument.generate( sink );
        String result = writer.toString();

        assertEquals( "xxx", result );
    }
}
