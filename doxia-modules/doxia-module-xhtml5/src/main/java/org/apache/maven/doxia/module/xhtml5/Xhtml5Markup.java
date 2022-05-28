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

import org.apache.maven.doxia.markup.HtmlMarkup;

/**
 * List of <code>Xhtml5</code> markups.
 * <br>
 * Xhtml5 uses all {@link javax.swing.text.html.HTML.Tag} and {@link javax.swing.text.html.HTML.Attribute}
 * as markups.
 */
@SuppressWarnings( "checkstyle:interfaceistype" )
public interface Xhtml5Markup
    extends HtmlMarkup
{
    /** XHTML5 namespace: "http://www.w3.org/1999/xhtml" */
    String XHTML5_NAMESPACE = "http://www.w3.org/1999/xhtml";
}
