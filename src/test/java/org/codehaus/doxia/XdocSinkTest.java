/*
 * CopyrightPlugin (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.doxia;

import org.codehaus.doxia.module.xdoc.XdocSink;
import org.codehaus.doxia.sink.Sink;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class XdocSinkTest
    extends AbstractSinkTestCase
{
    protected String outputExtension()
    {
        return "xml";
    }

    protected Sink createSink() throws Exception
    {
        return new XdocSink( getTestWriter() );
    }
}
