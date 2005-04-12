/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.util;

import junit.framework.TestCase;

import java.io.Writer;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:$
 */
public class InterpolationFilterWriterTest
    extends TestCase
{
    public void testInterpolatedValueAtTheBeginningOfTheInput()
        throws Exception
    {
        StringWriter sw = new StringWriter();

        Writer w = new InterpolationFilterWriter( sw, createMap() );

        w.write( "@name@ is my name." );

        assertEquals( "jason is my name.", sw.toString() );
    }

    public void testInterpolatedValueInTheMiddleOfTheInput()
        throws Exception
    {
        StringWriter sw = new StringWriter();

        Writer w = new InterpolationFilterWriter( sw, createMap() );

        w.write( "My name is @name@ the lazy." );

        assertEquals( "My name is jason the lazy.", sw.toString() );
    }

    public void testInterpolatedValueAtTheEndOfTheInput()
        throws Exception
    {
        StringWriter sw = new StringWriter();

        Writer w = new InterpolationFilterWriter( sw, createMap() );

        w.write( "My name is @name@" );

        assertEquals( "My name is jason", sw.toString() );
    }

    public void testInterpolatedValueAtTheBeginningOfTheInputUsingMultiCharacterBeginToken()
        throws Exception
    {
        StringWriter sw = new StringWriter();

        Writer w = new InterpolationFilterWriter( sw, createMap(), "${", "}" );

        w.write( "${name} is my name." );

        assertEquals( "jason is my name.", sw.toString() );
    }

    public void testInterpolatedValueInTheMiddleOfTheInputUsingMultiCharacterBeginToken()
        throws Exception
    {
        StringWriter sw = new StringWriter();

        Writer w = new InterpolationFilterWriter( sw, createMap(), "${", "}"  );

        w.write( "My name is ${name} the lazy." );

        assertEquals( "My name is jason the lazy.", sw.toString() );
    }

    public void testInterpolatedValueAtTheEndOfTheInputUsingMultiCharacterBeginToken()
        throws Exception
    {
        StringWriter sw = new StringWriter();

        Writer w = new InterpolationFilterWriter( sw, createMap(), "${", "}"  );

        w.write( "My name is ${name}" );

        assertEquals( "My name is jason", sw.toString() );
    }

    public void testInterpolatedValueAtTheBeginningOfTheInputUsingMultiCharacterEndToken()
        throws Exception
    {
        StringWriter sw = new StringWriter();

        Writer w = new InterpolationFilterWriter( sw, createMap(), "{", "}}" );

        w.write( "{name}} is my name." );

        assertEquals( "jason is my name.", sw.toString() );
    }

    public void testInterpolatedValueInTheMiddleOfTheInputUsingMultiCharacterEndToken()
        throws Exception
    {
        StringWriter sw = new StringWriter();

        Writer w = new InterpolationFilterWriter( sw, createMap(), "{", "}}"  );

        w.write( "My name is {name}} the lazy." );

        assertEquals( "My name is jason the lazy.", sw.toString() );
    }

    public void testInterpolatedValueAtTheEndOfTheInputUsingMultiCharacterEndToken()
        throws Exception
    {
        StringWriter sw = new StringWriter();

        Writer w = new InterpolationFilterWriter( sw, createMap(), "{", "}}"  );

        w.write( "My name is {name}}" );

        assertEquals( "My name is jason", sw.toString() );
    }

    public void testInterpolatedValueAtTheBeginningOfTheInputUsingMultiCharacterTokens()
        throws Exception
    {
        StringWriter sw = new StringWriter();

        Writer w = new InterpolationFilterWriter( sw, createMap(), "{{", "}}" );

        w.write( "{{name}} is my name." );

        assertEquals( "jason is my name.", sw.toString() );
    }

    public void testInterpolatedValueInTheMiddleOfTheInputUsingMultiCharacterTokens()
        throws Exception
    {
        StringWriter sw = new StringWriter();

        Writer w = new InterpolationFilterWriter( sw, createMap(), "{{", "}}"  );

        w.write( "My name is {{name}} the lazy." );

        assertEquals( "My name is jason the lazy.", sw.toString() );
    }

    public void testInterpolatedValueAtTheEndOfTheInputUsingMultiCharacterTokens()
        throws Exception
    {
        StringWriter sw = new StringWriter();

        Writer w = new InterpolationFilterWriter( sw, createMap(), "{{", "}}"  );

        w.write( "My name is {{name}}" );

        assertEquals( "My name is jason", sw.toString() );
    }


    protected Map createMap()
    {
        Map m = new HashMap();

        m.put( "name", "jason" );

        return m;
    }
}
