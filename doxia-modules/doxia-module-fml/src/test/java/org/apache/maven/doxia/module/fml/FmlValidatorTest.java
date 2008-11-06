package org.apache.maven.doxia.module.fml;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.doxia.xsd.AbstractXmlValidatorTest;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * Test FML files with namespace.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public class FmlValidatorTest
    extends AbstractXmlValidatorTest
{
    /** The xsd to use */
    private static final File FML_XSD = new File( getBasedir(), "/src/main/resources/fml-1.0.xsd" );

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

        l.add( new ScmRepositoryWrapper( "scm:svn:http://svn.apache.org/repos/asf/maven/site/trunk/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-site/fml" ) ) );
        l.add( new ScmRepositoryWrapper( "scm:svn:http://svn.apache.org/repos/asf/maven/doxia/site/src/site/fml",
                                         new File( getBasedir(), "target/svn/doxia-site/fml" ) ) );

        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-ant-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-ant-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-antrun-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-antrun-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-assembly-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-assembly-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-changelog-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-changelog-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-changes-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-changes-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-checkstyle-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-checkstyle-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-clean-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-clean-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-compiler-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-compiler-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-dependency-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-dependency-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-deploy-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-deploy-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-doap-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-doap-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-docck-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-docck-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-ear-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-ear-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-ejb-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-ejb-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-gpg-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-gpg-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-help-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-help-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-idea-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-idea-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-install-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-install-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-invoker-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-invoker-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-jar-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-jar-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-javadoc-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-javadoc-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-one-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-one-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-patch-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-patch-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-pmd-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-pmd-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-project-info-reports-plugin/src/site/fml",
                                         new File( getBasedir(),
                                                   "target/svn/maven-project-info-reports-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-rar-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-rar-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-remote-resources-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-remote-resources-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-repository-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-repository-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-ressources-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-ressources-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-site-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-site-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-source-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-source-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-stage-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-stage-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-stage-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-stage-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-verifier-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-verifier-plugin/fml" ) ) );
        l
         .add( new ScmRepositoryWrapper(
                                         "scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-war-plugin/src/site/fml",
                                         new File( getBasedir(), "target/svn/maven-war-plugin/fml" ) ) );

        return l;
    }

    /** {@inheritDoc} */
    protected List getXmlFiles( ScmRepositoryWrapper wrapper )
        throws IOException
    {
        return FileUtils.getFiles( wrapper.getCheckoutDir(), "**/*.fml",
                                   StringUtils.concatenate( FileUtils.getDefaultExcludes() ) );
    }

    /** {@inheritDoc} */
    protected String addNamespaces( String content )
    {
        Pattern pattern = Pattern.compile( ".*<([A-Za-z][A-Za-z0-9:_.-]*)([^>]*)>.*" );
        Matcher matcher = pattern.matcher( content );
        if ( matcher.find() )
        {
            String root = matcher.group( 1 );
            String value = matcher.group( 2 );

            if ( value.indexOf( FML_XSD.getName() ) == -1 )
            {
                String faqs =
                    "<" + root + " xmlns=\"http://maven.apache.org/FML/1.0\""
                        + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + "  xsi:schemaLocation=\"http://maven.apache.org/FML/1.0 " + FML_XSD.toURI() + "\" ";

                return StringUtils.replace( content, "<" + root, faqs );
            }
        }

        return content;
    }
}
