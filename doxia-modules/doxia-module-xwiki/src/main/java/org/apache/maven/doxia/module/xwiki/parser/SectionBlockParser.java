package org.apache.maven.doxia.module.xwiki.parser;

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

import org.apache.maven.doxia.module.confluence.parser.Block;
import org.apache.maven.doxia.module.confluence.parser.BlockParser;
import org.apache.maven.doxia.module.confluence.parser.SectionBlock;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.util.ByLineSource;

/**
 * @author Vincent Massol
 * @version $Id$
 */
public class SectionBlockParser
    implements BlockParser
{
    public boolean accept( String line, ByLineSource source )
    {
        return line.trim().startsWith( "1" );
    }

    public Block visit( String line, ByLineSource source )
        throws ParseException
    {
        int level;
        String title;

        String trimmedLine = line.trim();

        if ( trimmedLine.startsWith( "1.1.1.1.1" ) )
        {
            level = 5;
            title = trimmedLine.substring( 9 ).trim();
        }
        else if ( trimmedLine.startsWith( "1.1.1.1" ) )
        {
            level = 4;
            title = trimmedLine.substring( 7 ).trim();
        }
        else if ( trimmedLine.startsWith( "1.1.1" ) )
        {
            level = 3;
            title = trimmedLine.substring( 5 ).trim();
        }
        else if ( trimmedLine.startsWith( "1.1" ) )
        {
            level = 2;
            title = trimmedLine.substring( 3 ).trim();
        }
        else
        {
            level = 1;
            title = trimmedLine.substring( 1 ).trim();
        }

        return new SectionBlock( title, level );
    }
}