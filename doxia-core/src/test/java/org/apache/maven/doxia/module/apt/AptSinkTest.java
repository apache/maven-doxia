package org.apache.maven.doxia.module.apt;

import org.apache.maven.doxia.sink.AbstractSinkTestCase;
import org.apache.maven.doxia.sink.Sink;

public class AptSinkTest
	extends AbstractSinkTestCase
{
    protected String outputExtension()
    {
        return "apt";
    }

    public void testApt()
	    throws Exception
	{
	    Sink sink = createSink();

	    new AptParser().parse( getTestReader(), createSink() );

	    sink.flush();
	}

    protected Sink createSink()
        throws Exception
    {
        return new AptSink( getTestWriter() );
    }
}
