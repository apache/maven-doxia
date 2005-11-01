/*
 * Copyright (c) 2001 Pixware. 
 *
 * Author: Jean-Yves Belmonte (john@codehaus.org)
 *
 * This file is part of the Pixware doxia package.
 * For conditions of use and distribution, see the attached legal.txt file.
 */

package org.codehaus.doxia.module.rtf;

/**
 * A basic font descriptor using standard PostScript font metrics to compute
 * text extents. All dimensions returned are in twips.
 */
public class Font
{

    private int style;
    private int size;
    private FontMetrics metrics;

    public Font( int style, int size /*pts*/ )
        throws Exception
    {
        this.style = style;
        this.size = size;
        metrics = FontMetrics.find( style );
    }

    public int ascent()
    {
        return toTwips( metrics.ascent );
    }

    public int descent()
    {
        return toTwips( metrics.descent );
    }

    public TextExtents textExtents( String text )
    {
        int i, n;
        int width = 0;
        int ascent = 0;
        int descent = 0;

        for ( i = 0, n = text.length(); i < n; ++i )
        {
            char c = text.charAt( i );
            if ( c > 255 ) c = ' ';
            FontMetrics.CharMetrics metrics = this.metrics.charMetrics[c];
            width += metrics.wx;
            if ( metrics.ury > ascent ) ascent = metrics.ury;
            if ( metrics.lly < descent ) descent = metrics.lly;
        }

        int height = ascent + Math.abs( descent );

        return new TextExtents( toTwips( width ), toTwips( height ),
                                toTwips( ascent ) );
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
