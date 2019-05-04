package org.apache.maven.doxia.sink.impl;

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

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;

import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.impl.SinkUtils;

import junit.framework.TestCase;

/**
 *
 * @author ltheussl
 */
public class SinkUtilsTest
        extends TestCase
{

    /**
     * Test of getAttributeString method, of class SinkUtils.
     */
    public void testGetAttributeString()
    {
        assertEquals( "", SinkUtils.getAttributeString( null ) );

        AttributeSet att = new SinkEventAttributeSet( SinkEventAttributeSet.BOXED );
        String expResult = " decoration=\"boxed\"";
        String result = SinkUtils.getAttributeString( att );
        assertEquals( expResult, result );

        SinkEventAttributes at = new SinkEventAttributeSet( SinkEventAttributeSet.BOLD );
        at.addAttributes( att );
        expResult = " style=\"bold\" decoration=\"boxed\"";
        result = SinkUtils.getAttributeString( at );
        assertEquals( expResult, result );

        att = new SinkEventAttributeSet( "color", "red", "margin-left", "20px" );

        at = new SinkEventAttributeSet();
        at.addAttribute( SinkEventAttributeSet.STYLE, att );
        expResult = " style=\"color: red; margin-left: 20px\"";
        result = SinkUtils.getAttributeString( at );
        assertEquals( expResult, result );
    }

    /**
     * Test of filterAttributes method, of class SinkUtils.
     */
    public void testFilterAttributes()
    {
        assertNull( SinkUtils.filterAttributes( null, null ) );

        AttributeSet attributes = new SinkEventAttributeSet( 1 );
        String[] valids = null;

        MutableAttributeSet result = SinkUtils.filterAttributes( attributes, valids );
        assertEquals( 0, result.getAttributeCount() );

        valids = new String[] {};
        result = SinkUtils.filterAttributes( attributes, valids );
        assertEquals( 0, result.getAttributeCount() );

        result = SinkUtils.filterAttributes( SinkEventAttributeSet.BOLD, SinkUtils.SINK_BASE_ATTRIBUTES );
        assertEquals( 1, result.getAttributeCount() );

        result = SinkUtils.filterAttributes( SinkEventAttributeSet.CENTER, SinkUtils.SINK_BASE_ATTRIBUTES );
        assertEquals( 0, result.getAttributeCount() );
    }
}
