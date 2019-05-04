package org.apache.maven.doxia.util;

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

import java.io.IOException;
import java.io.StringReader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;

import org.apache.maven.doxia.logging.Log;
import org.apache.maven.doxia.markup.XmlMarkup;
import org.apache.maven.doxia.parser.AbstractXmlParser.CachedFileEntityResolver;
import org.apache.maven.doxia.parser.ParseException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A class to validate xml documents.
 *
 * @version $Id$
 * @since 1.1.3
 */
public class XmlValidator
{
    /**
     * Doctype pattern i.e. ".*<!DOCTYPE([^>]*)>.*"
     * see <a href="http://www.w3.org/TR/REC-xml/#NT-doctypedecl">http://www.w3.org/TR/REC-xml/#NT-doctypedecl</a>.
     */
    private static final Pattern PATTERN_DOCTYPE = Pattern.compile( ".*" + XmlMarkup.DOCTYPE_START + "([^>]*)>.*" );

    /** Tag pattern as defined in http://www.w3.org/TR/REC-xml/#NT-Name */
    private static final Pattern PATTERN_TAG = Pattern.compile( ".*<([A-Za-z][A-Za-z0-9:_.-]*)([^>]*)>.*" );

    /** lazy xmlReader to validate xml content*/
    private XMLReader xmlReader;

    private Log logger;

    /**
     * Constructor.
     *
     * @param log a logger, not null.
     */
    public XmlValidator( Log log )
    {
        this.logger = log;
    }

    /**
     * Validate an XML content with SAX.
     *
     * @param content a not null xml content
     * @throws ParseException if any.
     */
    public void validate( String content )
        throws ParseException
    {
        try
        {
            // 1 if there's a doctype
            boolean hasDoctype = false;
            Matcher matcher = PATTERN_DOCTYPE.matcher( content );
            if ( matcher.find() )
            {
                hasDoctype = true;
            }

            // 2 check for an xmlns instance
            boolean hasXsd = false;
            matcher = PATTERN_TAG.matcher( content );
            if ( matcher.find() )
            {
                String value = matcher.group( 2 );

                if ( value.contains( XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI ) )
                {
                    hasXsd = true;
                }
            }

            // 3 validate content
            getLog().debug( "Validating the content..." );
            getXmlReader( hasXsd && hasDoctype ).parse( new InputSource( new StringReader( content ) ) );
        }
        catch ( IOException | SAXException e )
        {
            throw new ParseException( "Error validating the model: " + e.getMessage(), e );
        }
    }

    /**
     * @param hasDtdAndXsd to flag the <code>ErrorHandler</code>.
     * @return an xmlReader instance.
     * @throws SAXException if any
     */
    private XMLReader getXmlReader( boolean hasDtdAndXsd )
        throws SAXException
    {
        if ( xmlReader == null )
        {
            MessagesErrorHandler errorHandler = new MessagesErrorHandler( getLog() );

            xmlReader = XMLReaderFactory.createXMLReader();
            xmlReader.setFeature( "http://xml.org/sax/features/validation", true );
            xmlReader.setFeature( "http://apache.org/xml/features/validation/schema", true );
            xmlReader.setErrorHandler( errorHandler );
            xmlReader.setEntityResolver( new CachedFileEntityResolver() );
        }

        ( (MessagesErrorHandler) xmlReader.getErrorHandler() ).setHasDtdAndXsd( hasDtdAndXsd );

        return xmlReader;
    }

    private Log getLog()
    {
        return logger;
    }

    /**
     * Convenience class to beautify <code>SAXParseException</code> messages.
     */
    private static class MessagesErrorHandler
        extends DefaultHandler
    {
        private static final int TYPE_UNKNOWN = 0;

        private static final int TYPE_WARNING = 1;

        private static final int TYPE_ERROR = 2;

        private static final int TYPE_FATAL = 3;

        private static final String EOL = XmlMarkup.EOL;

        /** @see org/apache/xerces/impl/msg/XMLMessages.properties#MSG_ELEMENT_NOT_DECLARED */
        private static final Pattern ELEMENT_TYPE_PATTERN =
            Pattern.compile( "Element type \".*\" must be declared.", Pattern.DOTALL );

        private final Log log;

        private boolean hasDtdAndXsd;

        private MessagesErrorHandler( Log log )
        {
            this.log = log;
        }

        /**
         * @param hasDtdAndXsd the hasDtdAndXsd to set
         */
        protected void setHasDtdAndXsd( boolean hasDtdAndXsd )
        {
            this.hasDtdAndXsd = hasDtdAndXsd;
        }

        /** {@inheritDoc} */
        @Override
        public void warning( SAXParseException e )
            throws SAXException
        {
            processException( TYPE_WARNING, e );
        }

        /** {@inheritDoc} */
        @Override
        public void error( SAXParseException e )
            throws SAXException
        {
            // Workaround for Xerces complaints when an XML with XSD needs also a <!DOCTYPE []> to specify entities
            // like &nbsp;
            // See http://xsd.stylusstudio.com/2001Nov/post08021.htm
            if ( !hasDtdAndXsd )
            {
                processException( TYPE_ERROR, e );
                return;
            }

            Matcher m = ELEMENT_TYPE_PATTERN.matcher( e.getMessage() );
            if ( !m.find() )
            {
                processException( TYPE_ERROR, e );
            }
        }

        /** {@inheritDoc} */
        @Override
        public void fatalError( SAXParseException e )
            throws SAXException
        {
            processException( TYPE_FATAL, e );
        }

        private void processException( int type, SAXParseException e )
            throws SAXException
        {
            StringBuilder message = new StringBuilder();

            switch ( type )
            {
                case TYPE_WARNING:
                    message.append( "Warning:" );
                    break;

                case TYPE_ERROR:
                    message.append( "Error:" );
                    break;

                case TYPE_FATAL:
                    message.append( "Fatal error:" );
                    break;

                case TYPE_UNKNOWN:
                default:
                    message.append( "Unknown:" );
                    break;
            }

            message.append( EOL );
            message.append( "  Public ID: " ).append( e.getPublicId() ).append( EOL );
            message.append( "  System ID: " ).append( e.getSystemId() ).append( EOL );
            message.append( "  Line number: " ).append( e.getLineNumber() ).append( EOL );
            message.append( "  Column number: " ).append( e.getColumnNumber() ).append( EOL );
            message.append( "  Message: " ).append( e.getMessage() ).append( EOL );

            final String logMessage = message.toString();

            switch ( type )
            {
                case TYPE_WARNING:
                    log.warn( logMessage );
                    break;

                case TYPE_UNKNOWN:
                case TYPE_ERROR:
                case TYPE_FATAL:
                default:
                    throw new SAXException( logMessage );
            }
        }
    }
}
