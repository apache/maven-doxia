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

import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.util.ByLineSource;

public class ParagraphBlockParser
    implements BlockParser
{

    public boolean accept( String line, ByLineSource source )
    {
        return true;
    }

    public Block visit( String line, ByLineSource source )
        throws ParseException
    {

        StringBuffer text = new StringBuffer();

        do
        {

            if ( line.trim().length() == 0 )
            {
                break;
            }

            if ( text.length() == 0 )
            {
                text.append( line.trim() );
            }
            else
            {
                text.append( " " + line.trim() );
            }

        }
        // TODO: instead of just flying along we should probably test new lines
        // in the other parsers
        // to make sure there aren't things that should be handled by other
        // parsers. For example, right
        // now:
        // Blah blah blah blah
        // # one
        // # two
        //
        // Will not get processed correctly. This parser will try to deal with
        // it when it should be handled
        // by the list parser.
        while ( ( line = source.getNextLine() ) != null );

        ChildBlocksBuilder builder = new ChildBlocksBuilder();
        

        return new ParagraphBlock( builder.getBlocks( text.toString() ) );
    }
}
