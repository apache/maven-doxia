/*
 * CopyrightPlugin (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.doxia;

import org.codehaus.doxia.module.docbook.DocBookSink;
import org.codehaus.doxia.sink.Sink;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DocBookSinkTest
    extends AbstractSinkTestCase
{
    protected String outputExtension()
    {
        return "docbook";
    }

    protected Sink createSink() throws Exception
    {
        return new DocBookSink( getTestWriter() );
    }
}
