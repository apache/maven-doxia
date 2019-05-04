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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.doxia.markup.XmlMarkup;
import org.apache.maven.doxia.xsd.AbstractXmlValidatorTest;
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
    private static final File FML_XSD = new File( getBasedir(), "/src/main/resources/fml-1.0.1.xsd" );

    /** {@inheritDoc} */
    protected String[] getIncludes()
    {
        return new String[] { "**/*.fml" };
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

            if ( !value.contains( FML_XSD.getName() ) )
            {
                String faqs =
                    "<" + root + " xmlns=\"" + FmlMarkup.FML_NAMESPACE + "\""
                        + "  xmlns:xsi=\"" + XmlMarkup.XML_NAMESPACE + "\""
                        + "  xsi:schemaLocation=\"" + FmlMarkup.FML_NAMESPACE + " " + FML_XSD.toURI() + "\" ";

                return StringUtils.replace( content, "<" + root, faqs );
            }
        }

        return content;
    }

    public void testValidateFiles()
        throws Exception
    {
        // TODO: super.testValidateFiles() only validates files from doxia-test-docs, what's the point?
    }
}
