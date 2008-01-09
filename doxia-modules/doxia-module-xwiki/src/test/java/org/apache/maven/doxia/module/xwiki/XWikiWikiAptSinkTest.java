package org.apache.maven.doxia.module.xwiki;

import org.apache.maven.doxia.module.apt.AptParser;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.AbstractSinkTestCase;
import org.apache.maven.doxia.sink.Sink;

public class XWikiWikiAptSinkTest
    extends AbstractSinkTestCase
{
    protected String outputExtension()
    {
        return "apt.xwiki";
    }

    protected Parser createParser()
    {
        return new AptParser();
    }

    protected Sink createSink()
        throws Exception
    {
        return new XWikiWikiSink( getTestWriter() );
    }
}
