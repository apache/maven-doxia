package org.apache.maven.doxia.module.xhtml;

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

import org.apache.maven.doxia.markup.HtmlMarkup;

/**
 * List of <code>Xhtml</code> markups.
 * <br/>
 * Xhtml uses all {@link javax.swing.text.html.HTML.Tag} and {@link javax.swing.text.html.HTML.Attribute}
 * as markups.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
@SuppressWarnings( "checkstyle:interfaceistype" )
public interface XhtmlMarkup
    extends HtmlMarkup
{
    /** XHTML namespace: "http://www.w3.org/1999/xhtml" */
    String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";

    /** XHTML 1.0 transitional public id: "-//W3C//DTD XHTML 1.0 Transitional//EN" */
    String XHTML_TRANSITIONAL_PUBLIC_ID = "-//W3C//DTD XHTML 1.0 Transitional//EN";

    /** XHTML 1.0 transitional system id: "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" */
    String XHTML_TRANSITIONAL_SYSTEM_ID = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd";
}
