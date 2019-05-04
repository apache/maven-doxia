package org.apache.maven.doxia.module.markdown;

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

import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.options.DataKey;
import com.vladsch.flexmark.util.options.MutableDataHolder;

/**
 * Implements flexmark-java extension to render fenced code and indented code using doxia format
 */
class FlexmarkDoxiaExtension implements HtmlRenderer.HtmlRendererExtension
{
    public static final DataKey<String> INPUT_FILE_EXTENSION = new DataKey<>( "INPUT_FILE_EXTENSION", "md" );

    @Override
    public void rendererOptions( final MutableDataHolder options )
    {

    }

    @Override
    public void extend( HtmlRenderer.Builder rendererBuilder, String rendererType )
    {
        rendererBuilder.nodeRendererFactory( new FlexmarkDoxiaNodeRenderer.Factory() );
    }

    public static Extension create()
    {
        return new FlexmarkDoxiaExtension();
    }
}
