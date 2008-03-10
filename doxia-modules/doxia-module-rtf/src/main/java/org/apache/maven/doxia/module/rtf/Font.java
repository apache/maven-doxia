package org.apache.maven.doxia.module.rtf;

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

/**
 * A basic font descriptor using standard PostScript font metrics to compute
 * text extents. All dimensions returned are in twips.
 *
 * @version $Id$
 */
class Font
{
    private int style;

    private int size;

    private FontMetrics metrics;

    Font( int style, int size /*pts*/ )
        throws Exception
    {
        this.style = style;
        this.size = size;
        metrics = FontMetrics.find( style );
    }

    int ascent()
    {
        return toTwips( metrics.ascent );
    }

    int descent()
    {
        return toTwips( metrics.descent );
    }

    TextExtents textExtents( String text )
    {
        int i, n;
        int width = 0;
        int ascent = 0;
        int descent = 0;

        for ( i = 0, n = text.length(); i < n; ++i )
        {
            char c = text.charAt( i );
            if ( c > 255 )
            {
                c = ' ';
            }
            FontMetrics.CharMetrics charMetrics = this.metrics.charMetrics[c];
            width += charMetrics.wx;
            if ( charMetrics.ury > ascent )
            {
                ascent = charMetrics.ury;
            }
            if ( charMetrics.lly < descent )
            {
                descent = charMetrics.lly;
            }
        }

        int height = ascent + Math.abs( descent );

        return new TextExtents( toTwips( width ), toTwips( height ), toTwips( ascent ) );
    }

    private int toTwips( int length )
    {
        return (int) Math.rint( (double) length * size / 50. );
    }

    static class TextExtents
    {

        int width;

        int height;

        int ascent;

        TextExtents( int width, int height, int ascent )
        {
            this.width = width;
            this.height = height;
            this.ascent = ascent;
        }

    }
}
