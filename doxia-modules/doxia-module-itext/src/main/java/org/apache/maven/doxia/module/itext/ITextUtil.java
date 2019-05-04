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

import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.xml.XmlToHtml;
import com.lowagie.text.xml.XmlToPdf;
import com.lowagie.text.xml.XmlToRtf;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Locale;

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
     * @return the page size
     * @see com.lowagie.text.PageSize
     */
    public static Rectangle getDefaultPageSize()
    {
        String defaultCountry = Locale.getDefault().getCountry();
        if ( defaultCountry != null
            && ( defaultCountry.equals( Locale.US.getCountry() )
                            || defaultCountry.equals( Locale.CANADA.getCountry() ) ) )
        {
            return PageSize.LETTER;
        }

        return PageSize.A4;
    }

    /**
     * Return a page size as String.
     *
     * @param rect a Rectangle defined in {@link PageSize}.
     * @return a page size as String or A4 if not found.
     * @see com.lowagie.text.PageSize
     */
    public static String getPageSize( Rectangle rect )
    {
        Field[] fields = PageSize.class.getFields();
        for ( Field currentField : fields )
        {
            try
            {
                if ( currentField.getType().equals( Rectangle.class ) )
                {
                    Rectangle fPageSize = (Rectangle) currentField.get( null );
                    if ( ( rect.width() == fPageSize.width() ) && ( rect.height() == fPageSize.height() ) )
                    {
                        return currentField.getName();
                    }
                }
            }
            catch ( Exception e )
            {
                // nop
            }
        }

        return "A4";
    }

    /**
     * Return <code>true</code> if the page size is supported by {@link PageSize} class, <code>false</code> otherwise.
     *
     * @param aPageSize a page size
     * @return <code>true</code> if the page size is supported, <code>false</code> otherwise
     * @see com.lowagie.text.PageSize
     */
    public static boolean isPageSizeSupported( String aPageSize )
    {
        Field[] fields = PageSize.class.getFields();
        for ( Field currentField : fields )
        {
            if ( ( currentField.getName().equalsIgnoreCase( aPageSize ) ) && ( currentField.getType().equals(
                    Rectangle.class ) ) )
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
     * @param is the <CODE>InputStream</CODE> from which the XML is read.
     * @param os the <CODE>OutputStream</CODE> to which the result as Pdf is written.
     * @see com.lowagie.text.xml.XmlToPdf
     */
    public static void writePdf( InputStream is, OutputStream os )
    {
        try
        {
            XmlToPdf x = new XmlToPdf();

            x.parse( is, os );
        }
        catch ( DocumentException e )
        {
            throw new RuntimeException( "DocumentException : " + e.getMessage(), e );
        }
    }

    /**
     * Parse an iText XML from the specified <CODE>InputStream</CODE>, writing an rtf document
     * specified <CODE>OutputStream</CODE>.
     *
     * @param is the <CODE>InputStream</CODE> from which the XML is read.
     * @param os the <CODE>OutputStream</CODE> to which the result as RTF is written.
     * @see com.lowagie.text.xml.XmlToRtf
     */
    public static void writeRtf( InputStream is, OutputStream os )
    {
        try
        {
            XmlToRtf x = new XmlToRtf();
            x.parse( is, os );
        }
        catch ( DocumentException e )
        {
            throw new RuntimeException( "DocumentException : " + e.getMessage(), e );
        }
    }

    /**
     * Parse an iText XML from the specified <CODE>InputStream</CODE>, writing an html document
     * specified <CODE>OutputStream</CODE>.
     *
     * @param is the <CODE>InputStream</CODE> from which the XML is read.
     * @param os the <CODE>OutputStream</CODE> to which the result as Html is written.
     * @see com.lowagie.text.xml.XmlToHtml
     */
    public static void writeHtml( InputStream is, OutputStream os )
    {
        try
        {
            XmlToHtml x = new XmlToHtml();
            x.parse( is, os );
        }
        catch ( DocumentException e )
        {
            throw new RuntimeException( "DocumentException : " + e.getMessage(), e );
        }
    }
}
