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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.doxia.markup.XmlMarkup;
import org.apache.maven.doxia.xsd.AbstractXmlValidatorTest;
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
    protected String[] getIncludes()
    {
        return new String[] { "**/*.xml", "**/xdoc/*" };
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

            if ( !value.contains( XDOC_XSD.getName() ) )
            {
                String faqs =
                    "<" + root + " xmlns=\"http://maven.apache.org/XDOC/2.0\""
                        + "  xmlns:xsi=\"" + XmlMarkup.XML_NAMESPACE + "\""
                        + "  xsi:schemaLocation=\"http://maven.apache.org/XDOC/2.0 " + XDOC_XSD.toURI() + "\" ";

                return StringUtils.replace( content, "<" + root, faqs );
            }
        }

        return content;
    }

    @Override
    public void testValidateFiles()
        throws Exception
    {
        // TODO: super.testValidateFiles() only validates files from doxia-test-docs, what's the point?
    }
}
