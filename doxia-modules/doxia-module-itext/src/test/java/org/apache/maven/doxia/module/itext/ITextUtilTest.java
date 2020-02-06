package org.apache.maven.doxia.module.itext;

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

import java.util.Locale;

import com.lowagie.text.PageSize;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class ITextUtilTest
{
    @Test
    public void testGetDefaultPageSize()
    {
        Locale oldLocale = Locale.getDefault();

        try
        {
            Locale.setDefault( Locale.US );
            assertEquals( PageSize.LETTER, ITextUtil.getDefaultPageSize() );

            Locale.setDefault( Locale.CANADA );
            assertEquals( PageSize.LETTER, ITextUtil.getDefaultPageSize() );

            Locale.setDefault( Locale.FRANCE );
            assertEquals( PageSize.A4, ITextUtil.getDefaultPageSize() );
        }
        finally
        {
            Locale.setDefault( oldLocale );
        }
    }

    @Test
    public void testGetPageSize()
        throws Exception
    {
        assertEquals( "A0", ITextUtil.getPageSize( PageSize.A0 ) );
        assertEquals( "A1", ITextUtil.getPageSize( PageSize.A1 ) );
        assertEquals( "A2", ITextUtil.getPageSize( PageSize.A2 ) );
        assertEquals( "A3", ITextUtil.getPageSize( PageSize.A3 ) );
        assertEquals( "A4", ITextUtil.getPageSize( PageSize.A4 ) );
        assertEquals( "A5", ITextUtil.getPageSize( PageSize.A5 ) );
        assertEquals( "A6", ITextUtil.getPageSize( PageSize.A6 ) );
        assertEquals( "A7", ITextUtil.getPageSize( PageSize.A7 ) );
        assertEquals( "A8", ITextUtil.getPageSize( PageSize.A8 ) );
        assertEquals( "A9", ITextUtil.getPageSize( PageSize.A9 ) );
        assertEquals( "A10", ITextUtil.getPageSize( PageSize.A10 ) );
        assertEquals( "LETTER", ITextUtil.getPageSize( PageSize.LETTER ) );
        assertEquals( "LEGAL", ITextUtil.getPageSize( PageSize.LEGAL ) );
    }

    @Test
    public void testIsPageSupported()
    {
        assertTrue( ITextUtil.isPageSizeSupported( "A0" ) );
        assertTrue( ITextUtil.isPageSizeSupported( "A1" ) );
        assertTrue( ITextUtil.isPageSizeSupported( "A2" ) );
        assertTrue( ITextUtil.isPageSizeSupported( "A3" ) );
        assertTrue( ITextUtil.isPageSizeSupported( "A4" ) );
        assertTrue( ITextUtil.isPageSizeSupported( "A5" ) );
        assertTrue( ITextUtil.isPageSizeSupported( "A6" ) );
        assertTrue( ITextUtil.isPageSizeSupported( "A7" ) );
        assertTrue( ITextUtil.isPageSizeSupported( "A8" ) );
        assertTrue( ITextUtil.isPageSizeSupported( "A9" ) );
        assertTrue( ITextUtil.isPageSizeSupported( "A10" ) );
        assertTrue( ITextUtil.isPageSizeSupported( "LETTER" ) );
        assertTrue( ITextUtil.isPageSizeSupported( "letter" ) );
        assertTrue( ITextUtil.isPageSizeSupported( "LEGAL" ) );
        assertTrue( ITextUtil.isPageSizeSupported( "legal" ) );
    }
}
