/*
 * CopyrightPlugin (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.doxia;

import org.codehaus.doxia.module.latex.LatexSink;
import org.codehaus.doxia.sink.Sink;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: LatexSinkTest.java,v 1.2 2004/09/14 14:26:38 jvanzyl Exp $
 */
public class LatexSinkTest
    extends AbstractSinkTestCase
{
    protected String outputExtension()
    {
        return "tex";
    }

    protected Sink createSink() throws Exception
    {
        return new LatexSink( getTestWriter() );
    }
}
