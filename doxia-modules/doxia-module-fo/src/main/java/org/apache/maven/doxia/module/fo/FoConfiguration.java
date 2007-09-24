package org.apache.maven.doxia.module.fo;

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

import java.util.List;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import org.apache.maven.doxia.sink.AbstractXmlSink;

/**
 * A utility class to construct FO configuration parameters.
 *
 * @author ltheussl
 * @version $Id$
 * @since 1.0
 */
public class FoConfiguration
{
    /** Holds the single attributes. */
    private MutableAttributeSet attributeSet;

    /** The configuration instance. */
    private final XMLConfiguration config;

    /** The list of attribute sets. */
    private final List sets;

    // TODO: add constructor to override default configuration

    /**
     * Constructor.
     */
    public FoConfiguration()
    {
        this.config = new XMLConfiguration();

        // necessary because some attributes contain commas:
        config.setDelimiterParsingDisabled( true );

        try
        {
            config.load( getClass().getResourceAsStream( "/fo-styles.xslt" ) );
        }
        catch ( ConfigurationException cex )
        {
            throw new RuntimeException( cex ); // TODO
        }

        this.sets = config.getList( "xsl:attribute-set[@name]" );
        reset();
    }

    /**
     * Builds a list of attributes.
     *
     * @param attributeId A unique id to identify the set of attributes.
     * This should correspond to the name of an attribute-set
     * defined in the configuration file.
     * @return A string that contains a list of attributes with
     * the values configured for the current builder. Returns the
     * empty string if attributeId is null or if attributeId
     * is not a valid identifier.
     */
    public String getAttributeString( String attributeId )
    {
        if ( attributeId == null )
        {
            return "";
        }

        reset();
        addAttributes( attributeId );

        return AbstractXmlSink.getAttributeString( attributeSet );
    }

    /**
     * Builds a set of attributes.
     *
     * @param attributeId A unique id to identify the set of attributes.
     * This should correspond to the name of an attribute-set
     * defined in the configuration file.
     * @return A MutableAttributeSet that contains the attributes with
     * the values configured for the current builder. Returns null
     * if attributeId is null or empty, or if attributeId is not a valid identifier.
     */
    public MutableAttributeSet getAttributeSet( String attributeId )
    {
        if ( attributeId == null || attributeId.length() == 0 )
        {
            return null;
        }

        reset();
        addAttributes( attributeId );

        if ( attributeSet.getAttributeCount() == 0 )
        {
            return null;
        }

        return attributeSet;
    }

    /**
     * Adds an attribute to the current StringBuffer.
     *
     * @param attributeId A unique id to identify the set of attributes.
     * This should correspond to the name of an attribute-set
     * defined in the configuration file.
     */
    private void addAttributes( String attributeId )
    {
        int index = sets.indexOf( attributeId );
        String keybase = "xsl:attribute-set(" + String.valueOf( index ) + ")";

        Object prop = config.getProperty( keybase + ".xsl:attribute" );
        if ( prop instanceof List )
        {
            List values = (List) prop;
            List keys = config.getList( keybase + ".xsl:attribute[@name]" );
            for ( int i = 0; i < values.size(); i++ )
            {
                attributeSet.addAttribute( keys.get( i ), values.get( i ) );
            }
        }
        else if ( prop instanceof String )
        {
            String value = config.getString( keybase + ".xsl:attribute" );
            String key = config.getString( keybase + ".xsl:attribute[@name]" );
            attributeSet.addAttribute( key, value );
        }

        String extend = config.getString( keybase + "[@use-attribute-sets]" );
        if ( extend != null )
        {
            addAttributes( extend );
        }
    }

    /**
     * (Re-)initialize the AttributeSet.
     */
    private void reset()
    {
        this.attributeSet = new SimpleAttributeSet();
    }

}
