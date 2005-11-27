package org.apache.maven.doxia.module.confluence.parser.list;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.doxia.module.confluence.parser.AbstractFatherBlock;
import org.apache.maven.doxia.sink.Sink;

import java.util.List;

public class NumberedListBlock
    extends ListBlock
{
    public NumberedListBlock( final List childBlocks )
    {
        super( childBlocks );
    }

    public void before( Sink sink )
    {
        System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );

        sink.numberedList( Sink.NUMBERING_DECIMAL );
    }

    public void after( Sink sink )
    {
        sink.numberedList_();
    }
}
