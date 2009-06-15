package org.apache.maven.doxia.module.docbook;

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

import junit.framework.TestCase;

import org.apache.maven.doxia.sink.Sink;

/**
 * Test DocbookUtils.
 *
 * @author ltheussl
 * @version $Id$
 */
public class DocbookUtilsTest
        extends TestCase
{
    /**
     * Test of doxiaTableFrameAttribute method, of class DocbookUtils.
     */
    public void testDoxiaTableFrameAttribute()
    {
        assertEquals( "box", DocbookUtils.doxiaTableFrameAttribute( "all" ) );
        assertEquals( "below", DocbookUtils.doxiaTableFrameAttribute( "bottom" ) );
        assertEquals( "void", DocbookUtils.doxiaTableFrameAttribute( "none" ) );
        assertEquals( "vsides", DocbookUtils.doxiaTableFrameAttribute( "sides" ) );
        assertEquals( "above", DocbookUtils.doxiaTableFrameAttribute( "top" ) );
        assertEquals( "hsides", DocbookUtils.doxiaTableFrameAttribute( "topbot" ) );

        try
        {
            DocbookUtils.doxiaTableFrameAttribute( "" );
            fail();
        }
        catch ( IllegalArgumentException e )
        {
            assertNotNull( e );
        }
    }

    /**
     * Test of doxiaListNumbering method, of class DocbookUtils.
     */
    public void testDoxiaListNumbering()
    {
        assertEquals( Sink.NUMBERING_LOWER_ALPHA,
                DocbookUtils.doxiaListNumbering( SimplifiedDocbookMarkup.LOWERALPHA_STYLE ) );
        assertEquals( Sink.NUMBERING_LOWER_ROMAN,
                DocbookUtils.doxiaListNumbering( SimplifiedDocbookMarkup.LOWERROMAN_STYLE ) );
        assertEquals( Sink.NUMBERING_UPPER_ALPHA,
                DocbookUtils.doxiaListNumbering( SimplifiedDocbookMarkup.UPPERALPHA_STYLE ) );
        assertEquals( Sink.NUMBERING_UPPER_ROMAN,
                DocbookUtils.doxiaListNumbering( SimplifiedDocbookMarkup.UPPERROMAN_STYLE ) );
        assertEquals( Sink.NUMBERING_DECIMAL,
                DocbookUtils.doxiaListNumbering( SimplifiedDocbookMarkup.ARABIC_STYLE ) );

        try
        {
            DocbookUtils.doxiaListNumbering( "" );
            fail();
        }
        catch ( IllegalArgumentException e )
        {
            assertNotNull( e );
        }
    }

    /**
     * Test of docbookListNumbering method, of class DocbookUtils.
     */
    public void testDocbookListNumbering()
    {
        assertEquals( SimplifiedDocbookMarkup.UPPERALPHA_STYLE,
                DocbookUtils.docbookListNumbering( Sink.NUMBERING_UPPER_ALPHA ) );
        assertEquals( SimplifiedDocbookMarkup.LOWERALPHA_STYLE,
                DocbookUtils.docbookListNumbering( Sink.NUMBERING_LOWER_ALPHA ) );
        assertEquals( SimplifiedDocbookMarkup.UPPERROMAN_STYLE,
                DocbookUtils.docbookListNumbering( Sink.NUMBERING_UPPER_ROMAN ) );
        assertEquals( SimplifiedDocbookMarkup.LOWERROMAN_STYLE,
                DocbookUtils.docbookListNumbering( Sink.NUMBERING_LOWER_ROMAN ) );
        assertEquals( SimplifiedDocbookMarkup.ARABIC_STYLE,
                DocbookUtils.docbookListNumbering( Sink.NUMBERING_DECIMAL ) );

        try
        {
            DocbookUtils.docbookListNumbering( -1 );
            fail();
        }
        catch ( IllegalArgumentException e )
        {
            assertNotNull( e );
        }
    }
        /**
     * Test of trademarkFromClass method, of class DocbookUtils.
     */
    public void testTrademarkFromClass()
    {
        assertEquals( '\u00AE', DocbookUtils.trademarkFromClass( "registered" ) );
        assertEquals( '\u00A9', DocbookUtils.trademarkFromClass( "copyright" ) );
        assertEquals( '\u2120', DocbookUtils.trademarkFromClass( "service" ) );
        assertEquals( '\u2122', DocbookUtils.trademarkFromClass( "trade" ) );

        try
        {
            DocbookUtils.trademarkFromClass( "" );
            fail();
        }
        catch ( IllegalArgumentException e )
        {
            assertNotNull( e );
        }
    }
}
