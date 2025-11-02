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
package org.apache.maven.doxia.module.xdoc;

import java.io.Writer;

import org.apache.maven.doxia.module.AbstractIdentityTest;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.util.DoxiaStringUtils;
import org.junit.jupiter.api.BeforeEach;

/**
 * Check that piping a full model through an XdocParser and an XdocSink
 * leaves the model unchanged.
 */
class XdocIdentityTest extends AbstractIdentityTest {

    @BeforeEach
    protected void setUp() throws Exception {
        assertIdentity(true);
    }

    protected Sink createSink(Writer writer) {
        XdocSink sink = new XdocSink(writer);
        sink.setInsertNewline(false);
        return sink;
    }

    protected Parser createParser() {
        return new XdocParser();
    }

    protected String getExpected() {
        // DOXIA-177
        String expected = super.getExpected();

        String startCaption = "begin:tableCaption";
        String endCaption = "end:tableCaption";

        int iStartCaption = expected.indexOf(startCaption);
        int iEndCaption = expected.indexOf(endCaption) + endCaption.length();

        String captionTag = expected.substring(iStartCaption, iEndCaption) + EOL + EOL + EOL;
        expected = DoxiaStringUtils.replace(expected, captionTag, "");

        int iStartTableRows =
                expected.substring(0, iStartCaption).lastIndexOf("begin:tableRows") + "begin:tableRows".length();

        StringBuilder text = new StringBuilder();
        text.append(expected, 0, iStartTableRows);
        text.append(EOL).append(EOL).append(EOL);
        text.append(captionTag.subSequence(0, captionTag.indexOf("end:tableCaption") + "end:tableCaption".length()));
        text.append(expected.substring(iStartTableRows));

        return text.toString();
    }
}
