package org.apache.maven.doxia.module.docbook;

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

import org.apache.maven.doxia.parser.AbstractParserTestCase;
import org.apache.maven.doxia.parser.Parser;

/**
 * @author <a href="mailto:lars@trieloff.net">Lars Trieloff</a>
 * @version $Id:DocBookParserTest.java 348605 2005-11-24 01:02:44Z brett $
 */
public class DocBookParserTest
    extends AbstractParserTestCase
{
    protected Parser getParser()
    {
        return new DocBookParser();
    }

    protected String getDocument()
    {
        return "src/test/site/docbook/guide.xml";
    }
}
