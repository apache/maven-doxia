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
 * @version $Id$
 */
class FontMetrics
{
    boolean fixedPitch;

    short ascent;

    short descent;

    CharMetrics bounds;

    CharMetrics[] charMetrics;

    FontMetrics( boolean fixedPitch, int ascent, int descent, CharMetrics bounds, CharMetrics[] metrics )
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
        Class<?> classObject = Class.forName( className );
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
