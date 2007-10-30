package org.apache.maven.doxia.module.confluence.parser;

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

import org.apache.maven.doxia.sink.Sink;

public class FigureBlock
    implements Block
{

    private String location;

    private String caption;

    public FigureBlock( String location )
    {
        this.location = location;
    }

    public FigureBlock( String image, String caption )
    {
        this.location = image;
        this.caption = caption;
    }

    public void traverse( Sink sink )
    {
        sink.figure();
        sink.figureGraphics( location );

        if ( caption != null && caption.length() > 0 )
        {
            sink.figureCaption();
            new TextBlock( caption ).traverse( sink );
            sink.figureCaption_();
        }

        sink.figure_();
    }

}
