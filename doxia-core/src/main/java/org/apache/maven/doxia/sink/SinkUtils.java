package org.apache.maven.doxia.sink;

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


import java.util.Enumeration;
import java.util.Arrays;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;

import org.apache.maven.doxia.markup.Markup;

/**
 * Collection of common utility methods for sinks.
 *
 * @author ltheussl
 * @version $Id$
 * @since 1.0-beta-1
 */
public class SinkUtils
{

    /** Do not instantiate. */
    private SinkUtils()
    {
    }

    /**
     * The set of base attributes.
     */
    public static final String[] SINK_BASE_ATTRIBUTES =
    {
        SinkEventAttributes.CLASS, SinkEventAttributes.ID, SinkEventAttributes.LANG,
        SinkEventAttributes.STYLE, SinkEventAttributes.TITLE
    };

    /**
     * The attributes that are supported for the br tag.
     */
    public static final String[] SINK_BR_ATTRIBUTES =
    {
        SinkEventAttributes.CLASS, SinkEventAttributes.ID,
        SinkEventAttributes.STYLE, SinkEventAttributes.TITLE
    };

    /**
     * The attributes that are supported for the &lt;img&gt; tag.
     */
    public static final String[] SINK_IMG_ATTRIBUTES;

    /**
     * The attributes that are supported for the section tags, like &lt;p&gt;, &lt;h2&gt;, &lt;div&gt;.
     */
    public static final String[] SINK_SECTION_ATTRIBUTES;

    /**
     * The attributes that are supported for the &lt;div&gt; and &lt;pre&gt; tags.
     */
    public static final String[] SINK_VERBATIM_ATTRIBUTES;

    /**
     * The attributes that are supported for the &lt;hr&gt; tag.
     */
    public static final String[] SINK_HR_ATTRIBUTES;

    /**
     * The attributes that are supported for the &lt;a&gt; tag.
     */
    public static final String[] SINK_LINK_ATTRIBUTES;

    /**
     * The attributes that are supported for the &lt;table&gt; tag.
     */
    public static final String[] SINK_TABLE_ATTRIBUTES;

    /**
     * The attributes that are supported for the &lt;td&gt; and &lt;th&gt; tags.
     */
    public static final String[] SINK_TD_ATTRIBUTES;

    /**
     * The attributes that are supported for the &lt;tr&gt; tag.
     */
    public static final String[] SINK_TR_ATTRIBUTES;

    private static final String[] IMG_ATTRIBUTES =
    {
        SinkEventAttributes.ALIGN, SinkEventAttributes.ALT, SinkEventAttributes.BORDER,
        SinkEventAttributes.HEIGHT, SinkEventAttributes.HSPACE, SinkEventAttributes.ISMAP,
        SinkEventAttributes.SRC, SinkEventAttributes.USEMAP, SinkEventAttributes.VSPACE,
        SinkEventAttributes.WIDTH
    };

    private static final String[] HR_ATTRIBUTES =
    {
        SinkEventAttributes.ALIGN, SinkEventAttributes.NOSHADE, SinkEventAttributes.SIZE,
        SinkEventAttributes.WIDTH
    };

    private static final String[] LINK_ATTRIBUTES =
    {
        SinkEventAttributes.CHARSET, SinkEventAttributes.COORDS, SinkEventAttributes.HREF,
        SinkEventAttributes.HREFLANG, SinkEventAttributes.REL, SinkEventAttributes.REV,
        SinkEventAttributes.SHAPE, SinkEventAttributes.TARGET, SinkEventAttributes.TYPE
    };

    private static final String[] TABLE_ATTRIBUTES =
    {
        SinkEventAttributes.ALIGN, SinkEventAttributes.BGCOLOR, SinkEventAttributes.BORDER,
        SinkEventAttributes.CELLPADDING, SinkEventAttributes.CELLSPACING, SinkEventAttributes.FRAME,
        SinkEventAttributes.RULES, SinkEventAttributes.SUMMARY, SinkEventAttributes.WIDTH
    };

    private static final String[] TABLE_CELL_ATTRIBUTES =
    {
        SinkEventAttributes.ABBRV, SinkEventAttributes.ALIGN, SinkEventAttributes.AXIS,
        SinkEventAttributes.BGCOLOR, SinkEventAttributes.COLSPAN, SinkEventAttributes.HEADERS,
        SinkEventAttributes.HEIGHT, SinkEventAttributes.NOWRAP, SinkEventAttributes.ROWSPAN,
        SinkEventAttributes.SCOPE, SinkEventAttributes.VALIGN, SinkEventAttributes.WIDTH
    };

    static
    {
        SINK_IMG_ATTRIBUTES = join( SINK_BASE_ATTRIBUTES, IMG_ATTRIBUTES  );
        SINK_SECTION_ATTRIBUTES =
                join( SINK_BASE_ATTRIBUTES, new String[] {SinkEventAttributes.ALIGN}  );
        SINK_VERBATIM_ATTRIBUTES =
                join( SINK_BASE_ATTRIBUTES,
                new String[] {SinkEventAttributes.ALIGN, SinkEventAttributes.BOXED, SinkEventAttributes.WIDTH}  );
        SINK_HR_ATTRIBUTES = join( SINK_BASE_ATTRIBUTES, HR_ATTRIBUTES  );
        SINK_LINK_ATTRIBUTES = join( SINK_BASE_ATTRIBUTES, LINK_ATTRIBUTES  );
        SINK_TABLE_ATTRIBUTES = join( SINK_BASE_ATTRIBUTES, TABLE_ATTRIBUTES  );
        SINK_TR_ATTRIBUTES =
                join( SINK_BASE_ATTRIBUTES,
                new String[] {SinkEventAttributes.ALIGN, SinkEventAttributes.BGCOLOR, SinkEventAttributes.VALIGN}  );
        SINK_TD_ATTRIBUTES = join( SINK_BASE_ATTRIBUTES, TABLE_CELL_ATTRIBUTES  );
    }

    private static String[] join( String[] a, String[] b )
    {
        String[] temp = new String[a.length + b.length];
        System.arraycopy( a, 0, temp, 0, a.length );
        System.arraycopy( b, 0, temp, a.length, b.length);

        Arrays.sort( temp ); // necessary for binary searches in filterAttributes()

        return temp;
    }

    /**
     * Utility method to get an AttributeSet as a String.
     * The resulting String is in the form ' name1="value1" name2="value2" ...',
     * ie it can be appended directly to an xml start tag. Attribute values that are itself
     * AttributeSets are ignored, all other keys and values are written as Strings.
     *
     * @param att The AttributeSet. May be null, in which case an empty String is returned.
     * @return the AttributeSet as a String in a form that can be appended to an xml start tag.
     */
    public static String getAttributeString( AttributeSet att )
    {
        if ( att == null )
        {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        Enumeration names = att.getAttributeNames();

        while ( names.hasMoreElements() )
        {
            Object key = names.nextElement();
            Object value = att.getAttribute( key );

            // AttributeSets are ignored
            if ( !(value instanceof AttributeSet) )
            {
                sb.append( Markup.SPACE ).append( key.toString() ).append( Markup.EQUAL )
                    .append( Markup.QUOTE ).append( value.toString() ).append( Markup.QUOTE );
            }
        }

        return sb.toString();
    }

    /**
     * Filters the given AttributeSet.
     * Removes all attributes whose name (key) is not contained in the sorted array valids.
     *
     * @param attributes The AttributeSet to filter. The String values of Attribute names
     * are compared to the elements of the valids array.
     * @param valids a sorted array of attribute names that are to be kept in the resulting AttributeSet.
     *      <b>Note:</b> a binary search is employed, so the array has to be sorted for correct results.
     * @return A filtered MutableAttributeSet object. Returns null if the input AttributeSet is null.
     *      If the array of valids is either null or empty, an empty AttributeSet is returned.
     */
    public static MutableAttributeSet filterAttributes( AttributeSet attributes, String[] valids )
    {
        if ( attributes == null )
        {
            return null;
        }

        if ( valids == null || valids.length == 0 )
        {
            return new SinkEventAttributeSet( 0 );
        }

        MutableAttributeSet atts = new SinkEventAttributeSet( attributes.getAttributeCount() );

        Enumeration names = attributes.getAttributeNames();

        while ( names.hasMoreElements() )
        {
            String key = names.nextElement().toString();

            if ( Arrays.binarySearch( valids, key ) >= 0 )
            {
                atts.addAttribute( key, attributes.getAttribute( key ) );
            }
        }

        return atts;
    }
}
