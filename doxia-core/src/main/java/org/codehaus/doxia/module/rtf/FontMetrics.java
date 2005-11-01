/*
 * Copyright (c) 2001 Pixware. 
 *
 * Author: Jean-Yves Belmonte (john@codehaus.org)
 *
 * This file is part of the Pixware doxia package.
 * For conditions of use and distribution, see the attached legal.txt file.
 */

package org.codehaus.doxia.module.rtf;

class FontMetrics
{

    boolean fixedPitch;
    short ascent;
    short descent;
    CharMetrics bounds;
    CharMetrics[] charMetrics;

    FontMetrics( boolean fixedPitch, int ascent, int descent,
                 CharMetrics bounds, CharMetrics[] metrics )
    {
        this.fixedPitch = fixedPitch;
        this.ascent = (short) ascent;
        this.descent = (short) descent;
        this.bounds = bounds;
        this.charMetrics = metrics;
    }

    static FontMetrics find( int style )
        throws Exception
    {
        String s = FontMetrics.class.getName();
        String packageName = s.substring( 0, s.lastIndexOf( '.' ) );

        StringBuffer buf = new StringBuffer( packageName + "." );

        switch ( style )
        {
            case RtfSink.STYLE_ROMAN:
            default:
                buf.append( "Serif" );
                break;
            case RtfSink.STYLE_ITALIC:
                buf.append( "SerifItalic" );
                break;
            case RtfSink.STYLE_BOLD:
                buf.append( "SerifBold" );
                break;
            case RtfSink.STYLE_TYPEWRITER:
                buf.append( "Monospace" );
                break;
        }

        String className = buf.toString();
        Class classObject = Class.forName( className );
        return (FontMetrics) classObject.newInstance();
    }

    static class CharMetrics
    {

        short wx;
        short wy;
        short llx;
        short lly;
        short urx;
        short ury;

        CharMetrics( int wx, int wy, int llx, int lly, int urx, int ury )
        {
            this.wx = (short) wx;
            this.wy = (short) wy;
            this.llx = (short) llx;
            this.lly = (short) lly;
            this.urx = (short) urx;
            this.ury = (short) ury;
        }

    }

}
