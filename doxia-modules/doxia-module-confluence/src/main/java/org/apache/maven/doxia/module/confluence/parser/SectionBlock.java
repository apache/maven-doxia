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

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class SectionBlock
    implements Block
{
    private String title;

    private int level;

    public SectionBlock( String title, int level )
    {
        this.title = title;
        this.level = level;
    }

    public void traverse( Sink sink )
    {
        if ( level == Sink.SECTION_LEVEL_1 )
        {
            sink.section1();
            sink.sectionTitle1();
        }
        else if ( level == Sink.SECTION_LEVEL_2 )
        {
            sink.section2();
            sink.sectionTitle2();
        }
        else if ( level == Sink.SECTION_LEVEL_3 )
        {
            sink.section3();
            sink.sectionTitle3();
        }
        else if ( level == Sink.SECTION_LEVEL_4 )
        {
            sink.section4();
            sink.sectionTitle4();
        }
        else if ( level == Sink.SECTION_LEVEL_5 )
        {
            sink.section5();
            sink.sectionTitle5();
        }

        sink.text( title );

        if ( level == Sink.SECTION_LEVEL_1 )
        {
            sink.sectionTitle1_();
            sink.section1_();
        }
        else if ( level == Sink.SECTION_LEVEL_2 )
        {
            sink.sectionTitle2_();
            sink.section2_();
        }
        else if ( level == Sink.SECTION_LEVEL_3 )
        {
            sink.sectionTitle3_();
            sink.section3_();

        }
        else if ( level == Sink.SECTION_LEVEL_4 )
        {
            sink.sectionTitle4_();
            sink.section4_();
        }
        else if ( level == Sink.SECTION_LEVEL_5 )
        {
            sink.sectionTitle5_();
            sink.section5_();
        }
    }
}
