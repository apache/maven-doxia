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

import org.apache.maven.doxia.sink.Sink;

/**
 * Utility methods for Doxia Docbook Parser and Sink.
 *
 * @author ltheussl
 * @since 1.1.1
 */
public final class DocbookUtils
{
    /**
     * Translate a given Docbook table frame attribute value to a valid
     * Doxia table frame attribute value.
     *
     * <p>The input has to be one of <code>"all"</code>, <code>"bottom"</code>,
     * <code>"none"</code>, <code>"sides"</code>, <code>"top"</code> or <code>"topbot"</code>,
     * otherwise an IllegalArgumentException is thrown.</p>
     *
     * <p>The corresponding output values are <code>"box"</code>, <code>"below"</code>,
     * <code>"void"</code>, <code>"vsides"</code>, <code>"above"</code> and <code>"hsides"</code>.</p>
     *
     * @param frame a valid docbook table frame attribute as specified above,
     * otherwise an IllegalArgumentException is thrown.
     * @return a valid Doxia table frame attribute as specified above.
     */
    public static String doxiaTableFrameAttribute( final String frame )
    {
        String fr = frame;

        if ( "all".equals( fr ) )
        {
            fr = "box";
        }
        else if ( "bottom".equals( fr ) )
        {
            fr = "below";
        }
        else if ( "none".equals( fr ) )
        {
            fr = "void";
        }
        else if ( "sides".equals( fr ) )
        {
            fr = "vsides";
        }
        else if ( "top".equals( fr ) )
        {
            fr = "above";
        }
        else if ( "topbot".equals( fr ) )
        {
            fr = "hsides";
        }
        else
        {
            throw new IllegalArgumentException( "Not a valid frame attribute: " + fr );
        }

        return fr;
    }

    /**
     * Convert a docbook ordered-list numbering style to a doxia numbering style.
     *
     * <p>The input has to be one of the style constants defined in {@link SimplifiedDocbookMarkup},
     * otherwise an IllegalArgumentException is thrown.</p>
     *
     * <p>The output is one of the numbering constants defined in {@link Sink}.</p>
     * @param style a docbook ordered-list numbering style.
     * @return a doxia numbering style.
     */
    public static int doxiaListNumbering( final String style )
    {
        if ( SimplifiedDocbookMarkup.LOWERALPHA_STYLE.equals( style ) )
        {
            return Sink.NUMBERING_LOWER_ALPHA;
        }
        else if ( SimplifiedDocbookMarkup.LOWERROMAN_STYLE.equals( style ) )
        {
            return Sink.NUMBERING_LOWER_ROMAN;
        }
        else if ( SimplifiedDocbookMarkup.UPPERALPHA_STYLE.equals( style ) )
        {
            return Sink.NUMBERING_UPPER_ALPHA;
        }
        else if ( SimplifiedDocbookMarkup.UPPERROMAN_STYLE.equals( style ) )
        {
            return Sink.NUMBERING_UPPER_ROMAN;
        }
        else if ( SimplifiedDocbookMarkup.ARABIC_STYLE.equals( style ) )
        {
            return Sink.NUMBERING_DECIMAL;
        }
        else
        {
            throw new IllegalArgumentException( "Not a valid numbering style: " + style );
        }
    }

    /**
     * Convert a doxia numbering style to a docbook ordered-list numbering style.
     *
     * <p>The input has to be one of the numbering constants defined in {@link Sink},
     * otherwise an IllegalArgumentException is thrown.</p>
     *
     * <p>The output is one of the style constants defined in {@link SimplifiedDocbookMarkup}.</p>
     *
     * @param numbering a doxia numbering style.
     * @return a docbook ordered-list numbering style.
     */
    public static String docbookListNumbering( final int numbering )
    {
        switch ( numbering )
        {
            case Sink.NUMBERING_UPPER_ALPHA:
                return SimplifiedDocbookMarkup.UPPERALPHA_STYLE;
            case Sink.NUMBERING_LOWER_ALPHA:
                return SimplifiedDocbookMarkup.LOWERALPHA_STYLE;
            case Sink.NUMBERING_UPPER_ROMAN:
                return SimplifiedDocbookMarkup.UPPERROMAN_STYLE;
            case Sink.NUMBERING_LOWER_ROMAN:
                return SimplifiedDocbookMarkup.LOWERROMAN_STYLE;
            case Sink.NUMBERING_DECIMAL:
                return SimplifiedDocbookMarkup.ARABIC_STYLE;
            default:
                throw new IllegalArgumentException( "Not a valid numbering: " + numbering );
        }
    }

    /**
     * Get a trademark character from a class attribute.
     *
     * <p>The input String has to be one of <code>"registered"</code>, <code>"copyright"</code>,
     * <code>"service"</code> or <code>"trade"</code> otherwise an IllegalArgumentException is thrown.</p>
     *
     * <p>The corresponding output is <code>'\u00AE'</code>, <code>'\u00A9'</code>,
     * <code>'\u2120'</code> or <code>'\u2122'</code>.</p>
     *
     * @param trade a valid class atribute for the docbook <code>&lt;trademark&gt;</code> tag.
     * @return the corresponding unicode character.
     */
    public static char trademarkFromClass( final String trade )
    {
        if ( "registered".equals( trade ) )
        {
            return '\u00AE';
        }
        else if ( "copyright".equals( trade ) )
        {
            return '\u00A9';
        }
        else if ( "service".equals( trade ) )
        {
            return '\u2120';
        }
        else if ( "trade".equals( trade ) )
        {
            return '\u2122';
        }
        else
        {
            throw new IllegalArgumentException( "Not a trademark class: " + trade );
        }
    }

    private DocbookUtils()
    {
        // utility class
    }
}
