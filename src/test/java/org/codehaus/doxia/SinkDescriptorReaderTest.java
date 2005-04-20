/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.doxia;

import org.codehaus.doxia.module.xhtml.SinkDescriptorReader;

import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.Iterator;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: SinkDescriptorReaderTest.java,v 1.1 2004/09/22 00:01:43 jvanzyl Exp $
 */
public class SinkDescriptorReaderTest
    extends TestCase
{
    public void testSinkDescriptorReader()
        throws Exception
    {
        String basedir = System.getProperty( "basedir" );

        FileReader reader = new FileReader( new File( basedir, "src/main/resources/codehaus.dst" ) );

        SinkDescriptorReader sdr = new SinkDescriptorReader();

        Map directives = sdr.read( reader );

        for ( Iterator i = directives.keySet().iterator(); i.hasNext(); )
        {
            Object key = i.next();

            System.out.println( key + " => " + directives.get( key ) );
        }

    }
}
