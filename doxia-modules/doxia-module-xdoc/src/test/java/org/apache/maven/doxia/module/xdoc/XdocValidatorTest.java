package org.apache.maven.doxia.module.xdoc;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.maven.doxia.xsd.AbstractXmlValidatorTest;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * Test XDOC files with namespace.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public class XdocValidatorTest
    extends AbstractXmlValidatorTest
{
    /** The xsd to use */
    private static final File XDOC_XSD = new File( getBasedir(), "/src/main/resources/xdoc-2.0.xsd" );

    /** {@inheritDoc} */
    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    /** {@inheritDoc} */
    protected void tearDown()
        throws Exception
    {
        super.tearDown();
    }

    /** {@inheritDoc} */
    protected List getScmRepositoryWrapper()
    {
        List l = new ArrayList();

        l.add( new ScmRepositoryWrapper( "scm:svn:http://svn.apache.org/repos/asf/maven/site/trunk/src/site/xdoc",
                                         new File( getBasedir(), "target/svn/maven-site/xdoc" ) ) );
        l.add( new ScmRepositoryWrapper( "scm:svn:http://svn.apache.org/repos/asf/maven/doxia/site/src/site/xdoc",
                                         new File( getBasedir(), "target/svn/doxia-site/xdoc" ) ) );

        l.add( new ScmRepositoryWrapper( "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-site-plugin/src/site/xdoc",
                                         new File( getBasedir(), "target/svn/maven-site-plugin/xdoc" ) ) );

        return l;
    }

    /** {@inheritDoc} */
    protected List getXmlFiles( ScmRepositoryWrapper wrapper )
        throws IOException
    {
        return FileUtils.getFiles( wrapper.getCheckoutDir(), "**/*.xml, **/*.xdoc",
                                   StringUtils.concatenate( FileUtils.getDefaultExcludes() ) );
    }

    /** {@inheritDoc} */
    protected String addNamespaces( String content )
    {
        boolean result =
            Pattern.matches( ".*(<document)(\\s)*(xmlns=\"http://maven.apache.org/XDOC/2.0\")"
                + "(\\s)*(xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\").*", content );

        if ( result )
        {
            return content;
        }

        String document =
            "<document xmlns=\"http://maven.apache.org/XDOC/2.0\" "
                + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "  xsi:schemaLocation=\"http://maven.apache.org/XDOC/2.0 " + XDOC_XSD.toURI() + "\">";

        return StringUtils.replace( content, "<document>", document );
    }
}
