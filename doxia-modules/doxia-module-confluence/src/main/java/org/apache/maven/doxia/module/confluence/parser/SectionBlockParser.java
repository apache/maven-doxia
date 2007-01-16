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

import org.apache.maven.doxia.module.common.ByLineSource;
import org.apache.maven.doxia.parser.ParseException;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class SectionBlockParser
    implements BlockParser
{
    public boolean accept( String line, ByLineSource source )
    {
        if ( line.startsWith( "h1." ) )
        {
            return true;
        }
        else if ( line.startsWith( "h2." ) )
        {
            return true;
        }
        else if ( line.startsWith( "h3." ) )
        {
            return true;
        }
        else if ( line.startsWith( "h4." ) )
        {
            return true;
        }
        else if ( line.startsWith( "h5." ) )
        {
            return true;
        }

        return false;
    }

    public Block visit( String line, ByLineSource source )
        throws ParseException
    {
        int level = Integer.parseInt( Character.toString( line.charAt( 1 ) ) );

        String title = line.substring( 3 );

        return new SectionBlock( title, level );
    }
}
