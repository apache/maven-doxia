package org.apache.maven.doxia.module.xhtml5;

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

import java.io.Writer;
import java.util.Locale;

import org.apache.maven.doxia.module.xhtml5.Xhtml5Sink;
import org.apache.maven.doxia.sink.Sink;

public class Xhtml5SinkWithLanguageIdTest
    extends Xhtml5SinkTest
{
    /** {@inheritDoc} */
    protected Sink createSink( Writer writer )
    {
        return new Xhtml5Sink( writer, "UTF-8", Locale.US.getLanguage() );
    }

    /** {@inheritDoc} */
    protected String getHeadBlock()
    {
        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
            + "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">"
            + "<head><title></title>"
            + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/></head>";
    }
}
