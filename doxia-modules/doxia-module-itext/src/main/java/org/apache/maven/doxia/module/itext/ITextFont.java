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

import java.awt.Color;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.markup.MarkupTags;
import com.lowagie.text.pdf.BaseFont;

/**
 * <code>iText</code> wrapper object for font.
 *
 * @see com.lowagie.text.Font
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class ITextFont
{
    /** A normal font style */
    public static final String NORMAL = MarkupTags.CSS_VALUE_NORMAL;

    /** A bold font style */
    public static final String BOLD = MarkupTags.CSS_VALUE_BOLD;

    /** A italic font style */
    public static final String ITALIC = MarkupTags.CSS_VALUE_ITALIC;

    /** An underline font style */
    public static final String UNDERLINE = MarkupTags.CSS_VALUE_UNDERLINE;

    /** A default font name */
    public static final String DEFAULT_FONT_NAME = FontFactory.HELVETICA;

    /** A default font size */
    public static final float DEFAULT_FONT_SIZE = 12;

    /** A default font style */
    public static final String DEFAULT_FONT_STYLE = NORMAL;

    /** A default Black color definition */
    public static final int DEFAULT_FONT_COLOR_RED = Color.BLACK.getRed();

    /** A default Black color definition */
    public static final int DEFAULT_FONT_COLOR_GREEN = Color.BLACK.getGreen();

    /** A default Black color definition */
    public static final int DEFAULT_FONT_COLOR_BLUE = Color.BLACK.getBlue();

    private static final int SECTION_FONT_SIZE_0 = 24;
    private static final int SECTION_FONT_SIZE_1 = 22;
    private static final int SECTION_FONT_SIZE_2 = 20;
    private static final int SECTION_FONT_SIZE_3 = 18;
    private static final int SECTION_FONT_SIZE_4 = 16;
    private static final int SECTION_FONT_SIZE_DEFAULT = 14;

    private boolean monoSpaced = false;

    private float currentSize = 12;

    private int currentStyle = Font.NORMAL;

    private Color currentColor = Color.BLACK;

    /**
     * Default constructor
     */
    public ITextFont()
    {
        // nop
    }

    /**
     * Add bold style to the current style
     */
    public void addBold()
    {
        this.currentStyle += Font.BOLD;
    }

    /**
     * Remove bold style to the current style
     */
    public void removeBold()
    {
        this.currentStyle -= Font.BOLD;
        if ( this.currentStyle < 0 )
        {
            this.currentStyle = Font.NORMAL;
        }
    }

    /**
     * Add italic style to the current style
     */
    public void addItalic()
    {
        this.currentStyle += Font.ITALIC;
    }

    /**
     * Remove italic style to the current style
     */
    public void removeItalic()
    {
        this.currentStyle -= Font.ITALIC;
        if ( this.currentStyle < 0 )
        {
            this.currentStyle = Font.NORMAL;
        }
    }

    /**
     * Add italic style to the current style
     */
    public void addUnderlined()
    {
        this.currentStyle += Font.UNDERLINE;
    }

    /**
     * Remove italic style to the current style
     */
    public void removeUnderlined()
    {
        this.currentStyle -= Font.UNDERLINE;
        if ( this.currentStyle < 0 )
        {
            this.currentStyle = Font.NORMAL;
        }
    }

    /**
     * Add monospaced style to the current style
     *
     * @param monoSpaced true for monospaced style
     */
    public void setMonoSpaced( boolean monoSpaced )
    {
        this.monoSpaced = monoSpaced;
    }

    /**
     * Set a new font color
     *
     * @param color a new color
     */
    public void setColor( Color color )
    {
        this.currentColor = color;
    }

    /**
     * Set a new font color
     *
     * @param size a new size
     */
    public void setSize( float size )
    {
        this.currentSize = size;
    }

    /**
     * Return the font name
     *
     * @return the font name
     */
    public String getFontName()
    {
        Font font = getCurrentFont();

        return font.getFamilyname();
    }

    /**
     * Return the font style
     *
     * @return the font style
     */
    public String getFontStyle()
    {
        Font font = getCurrentFont();
        StringBuilder sb = new StringBuilder();

        if ( font.isBold() )
        {
            sb.append( BOLD );
        }

        if ( font.isItalic() )
        {
            if ( sb.length() == 0 )
            {
                sb.append( ITALIC );
            }
            else
            {
                sb.append( "," );
                sb.append( ITALIC );
            }
        }

        if ( font.isUnderlined() )
        {
            if ( sb.length() == 0 )
            {
                sb.append( UNDERLINE );
            }
            else
            {
                sb.append( "," );
                sb.append( UNDERLINE );
            }
        }

        if ( sb.length() == 0 )
        {
            return NORMAL;
        }

        return sb.toString();
    }

    /**
     * Return the font name
     *
     * @return the font name
     */
    public String getFontSize()
    {
        Font font = getCurrentFont();

        return String.valueOf( font.getCalculatedSize() );
    }

    /**
     * Return the font color blue
     *
     * @return the font color blue
     */
    public String getFontColorBlue()
    {
        Font font = getCurrentFont();

        return String.valueOf( font.color().getBlue() );
    }

    /**
     * Return the font color green
     *
     * @return the font color green
     */
    public String getFontColorGreen()
    {
        Font font = getCurrentFont();

        return String.valueOf( font.color().getGreen() );
    }

    /**
     * Return the font color red
     *
     * @return the font color red
     */
    public String getFontColorRed()
    {
        Font font = getCurrentFont();

        return String.valueOf( font.color().getRed() );
    }

    /**
     * Get a section font size depending the section number.
     * <dl>
     * <dt>0</dt>
     * <dd>Chapter: font size = 24</dd>
     * <dt>1</dt>
     * <dd>Section 1: font size = 22</dd>
     * <dt>2</dt>
     * <dd>Section 2: font size = 20</dd>
     * <dt>3</dt>
     * <dd>Section 3: font size = 18</dd>
     * <dt>4</dt>
     * <dd>Section 4: font size = 16</dd>
     * <dt>5 ot otherwise</dt>
     * <dd>Section 5: font size = 14</dd>
     * </dl>
     *
     * @param sectionNumber a section number
     * @return a font size.
     */
    public static int getSectionFontSize( int sectionNumber )
    {
        switch ( sectionNumber )
        {
            case 0:
                return SECTION_FONT_SIZE_0;

            case 1:
                return SECTION_FONT_SIZE_1;

            case 2:
                return SECTION_FONT_SIZE_2;

            case 3:
                return SECTION_FONT_SIZE_3;

            case 4:
                return SECTION_FONT_SIZE_4;

            case 5:
            default:
                return SECTION_FONT_SIZE_DEFAULT;
        }
    }

    /**
     * Convenience method to get a defined MonoSpaced font depending the wanted style and size.
     *
     * @param style the font style.
     * @param size the font size.
     * @param color the font color.
     * @return a font the font.
     */
    public static Font getMonoSpacedFont( int style, float size, Color color )
    {
        try
        {
            return new Font( BaseFont.createFont( BaseFont.COURIER, BaseFont.CP1252, false ), size, style, color );
        }
        catch ( Exception e )
        {
            throw new ExceptionConverter( e );
        }
    }

    /**
     * Convenience method to get a defined font depending the wanted style and size.
     *
     * @param style the font style.
     * @param size the font size.
     * @param color the font color.
     * @return a font the font.
     */
    public static Font getFont( int style, float size, Color color )
    {
        Font font = new Font();
        font.setFamily( DEFAULT_FONT_NAME );
        font.setStyle( style );
        font.setSize( size );
        font.setColor( color );
        return font;
    }

    /**
     * Convenience method to return the current font
     *
     * @return the current font
     */
    private Font getCurrentFont()
    {
        if ( this.monoSpaced )
        {
            return getMonoSpacedFont( this.currentStyle, this.currentSize, this.currentColor );
        }

        return getFont( this.currentStyle, this.currentSize, this.currentColor );
    }
}
