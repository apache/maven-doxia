package org.apache.maven.doxia.module.itext;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;

import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.xml.XmlToHtml;
import com.lowagie.text.xml.XmlToPdf;
import com.lowagie.text.xml.XmlToRtf;

/**
 * A set of util methods for the <code>iText</code> framework
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class ITextUtil
{
    /**
     * Set the default page size for the document depending the user's country.
     * TODO Maybe more generic?
     *
     * @see com.lowagie.text.PageSize
     *
     * @return the page size
     */
    public static Rectangle getDefaultPageSize()
    {
        String defaultCountry = Locale.getDefault().getCountry();
        if ( defaultCountry != null
            && ( defaultCountry.equals( Locale.US.getCountry() ) || defaultCountry.equals( Locale.CANADA.getCountry() ) ) )
        {
            return PageSize.LETTER;
        }

        return PageSize.A4;
    }

    /**
     * Return a page size as String.
     *
     * @see com.lowagie.text.PageSize
     *
     * @param rect a Rectangle
     * @return a page size as String
     */
    public static String getPageSize( Rectangle rect )
    {
        if ( ( rect.width() == PageSize.LETTER.width() ) && ( rect.height() == PageSize.LETTER.height() ) )
        {
            return "LETTER";
        }

        return "A4";
    }

    /**
     * Return true if the page size is supported by <code>PageSize</code> class, false otherwise
     *
     * @see com.lowagie.text.PageSize
     *
     * @param aPageSize a page size
     * @return true if the page size is supported, false otherwise
     */
    public static boolean isPageSizeSupported( String aPageSize )
    {
        Field[] sizes = PageSize.class.getDeclaredFields();
        for ( int i = 0; i < sizes.length; i++ )
        {
            Field currentField = sizes[i];
            if ( ( currentField.getName().equalsIgnoreCase( aPageSize ) )
                && ( Modifier.isStatic( currentField.getModifiers() ) )
                && ( currentField.getType().equals( Rectangle.class ) ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Parse an iText XML from the specified <CODE>InputStream</CODE>, writing an Pdf document
     * specified <CODE>OutputStream</CODE>.
     *
     * @see com.lowagie.text.xml.XmlToPdf
     *
     * @param is the <CODE>InputStream</CODE> from which the XML is read.
     * @param os the <CODE>OutputStream</CODE> to which the result as Pdf is written.
     * @throws RuntimeException if any
     */
    public static void writePdf( InputStream is, OutputStream os )
        throws RuntimeException
    {
        try
        {
            XmlToPdf x = new XmlToPdf();
            x.parse( is, os );
        }
        catch ( DocumentException e )
        {
            throw new RuntimeException( "DocumentException : " + e.getMessage() );
        }
    }

    /**
     * Parse an iText XML from the specified <CODE>InputStream</CODE>, writing an rtf document
     * specified <CODE>OutputStream</CODE>.
     *
     * @see com.lowagie.text.xml.XmlToRtf
     *
     * @param is the <CODE>InputStream</CODE> from which the XML is read.
     * @param os the <CODE>OutputStream</CODE> to which the result as RTF is written.
     * @throws RuntimeException if any
     */
    public static void writeRtf( InputStream is, OutputStream os )
        throws RuntimeException
    {
        try
        {
            XmlToRtf x = new XmlToRtf();
            x.parse( is, os );
        }
        catch ( DocumentException e )
        {
            throw new RuntimeException( "DocumentException : " + e.getMessage() );
        }
    }

    /**
     * Parse an iText XML from the specified <CODE>InputStream</CODE>, writing an html document
     * specified <CODE>OutputStream</CODE>.
     *
     * @see com.lowagie.text.xml.XmlToHtml
     *
     * @param is the <CODE>InputStream</CODE> from which the XML is read.
     * @param os the <CODE>OutputStream</CODE> to which the result as Html is written.
     * @throws RuntimeException if any
     */
    public static void writeHtml( InputStream is, OutputStream os )
        throws RuntimeException
    {
        try
        {
            XmlToHtml x = new XmlToHtml();
            x.parse( is, os );
        }
        catch ( DocumentException e )
        {
            throw new RuntimeException( "DocumentException : " + e.getMessage() );
        }
    }
}
