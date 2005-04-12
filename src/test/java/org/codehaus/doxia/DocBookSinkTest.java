/*
 * CopyrightPlugin (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.doxia;

import junit.framework.TestCase;
import org.codehaus.doxia.module.apt.AptParser;
import org.codehaus.doxia.module.apt.AptReaderSource;
import org.codehaus.doxia.module.latex.LatexSink;
import org.codehaus.doxia.module.rtf.RtfSink;
import org.codehaus.doxia.module.xdoc.XdocSink;
import org.codehaus.doxia.module.docbook.DocBookSink;
import org.codehaus.doxia.sink.Sink;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.io.FileOutputStream;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: DocBookSinkTest.java,v 1.3 2004/09/15 01:04:04 jvanzyl Exp $
 */
public class DocBookSinkTest
    extends AbstractSinkTestCase
{
    protected String outputExtension()
    {
        return "docbook";
    }

    protected Sink createSink()
        throws Exception
    {
        return new DocBookSink( getTestWriter() );
    }
}
