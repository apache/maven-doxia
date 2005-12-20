package org.apache.maven.doxia;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import junit.framework.TestCase;
import org.apache.maven.doxia.module.xhtml.SinkDescriptorReader;

import java.io.File;
import java.io.FileReader;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:SinkDescriptorReaderTest.java 348605 2005-11-24 12:02:44 +1100 (Thu, 24 Nov 2005) brett $
 */
public class SinkDescriptorReaderTest
    extends TestCase
{
    public void testSinkDescriptorReader()
        throws Exception
    {
        String basedir = System.getProperty( "basedir" );

        FileReader reader = new FileReader( new File( basedir, "src/test/resources/codehaus.dst" ) );

        SinkDescriptorReader sdr = new SinkDescriptorReader();

        Map directives = sdr.read( reader );

//        for ( Iterator i = directives.keySet().iterator(); i.hasNext(); )
//        {
//            Object key = i.next();
//
//            System.out.println( key + " => " + directives.get( key ) );
//        }
    }
}
