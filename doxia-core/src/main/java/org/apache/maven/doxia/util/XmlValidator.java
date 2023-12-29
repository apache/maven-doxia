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
package org.apache.maven.doxia.util;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.doxia.markup.XmlMarkup;
import org.apache.maven.doxia.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A class to validate xml documents.
 *
 * @since 1.1.3
 */
public class XmlValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlValidator.class);

    /** lazy xmlReader to validate xml content*/
    private XMLReader xmlReader;

    private boolean validate = true;
    private DefaultHandler defaultHandler;
    private EntityResolver entityResolver;

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public DefaultHandler getDefaultHandler() {
        return defaultHandler;
    }

    public void setDefaultHandler(DefaultHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    /**
     * Validate an XML content with SAX.
     *
     * @param content a not null xml content
     * @throws ParseException if any.
     */
    public void validate(String content) throws ParseException {
        try {
            getXmlReader().parse(new InputSource(new StringReader(content)));
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new ParseException("Error validating the model", e);
        }
    }

    /**
     * @return an xmlReader instance.
     * @throws SAXException if any
     * @throws ParserConfigurationException
     */
    public XMLReader getXmlReader() throws SAXException, ParserConfigurationException {
        if (xmlReader == null) {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setNamespaceAware(true);
            SAXParser parser = parserFactory.newSAXParser();
            // If both DTD and XSD are provided, force XSD
            parser.setProperty(
                    "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
            // Always force language-neutral exception messages for MessagesErrorHandler
            parser.setProperty("http://apache.org/xml/properties/locale", Locale.ROOT);
            xmlReader = parser.getXMLReader();
            xmlReader.setFeature("http://xml.org/sax/features/validation", isValidate());
            xmlReader.setFeature("http://apache.org/xml/features/validation/dynamic", isValidate());
            xmlReader.setFeature("http://apache.org/xml/features/validation/schema", isValidate());
            xmlReader.setErrorHandler(getDefaultHandler());
            xmlReader.setEntityResolver(getEntityResolver());
        }

        return xmlReader;
    }

    /**
     * Convenience class to beautify <code>SAXParseException</code> messages.
     */
    public static class MessagesErrorHandler extends DefaultHandler {
        private static final int TYPE_UNKNOWN = 0;

        private static final int TYPE_WARNING = 1;

        private static final int TYPE_ERROR = 2;

        private static final int TYPE_FATAL = 3;

        private static final String EOL = XmlMarkup.EOL;

        /** @see org/apache/xerces/impl/msg/XMLMessages.properties#MSG_ELEMENT_NOT_DECLARED */
        private static final Pattern ELEMENT_TYPE_PATTERN =
                Pattern.compile("Element type \".*\" must be declared.", Pattern.DOTALL);

        /** {@inheritDoc} */
        @Override
        public void warning(SAXParseException e) throws SAXException {
            processException(TYPE_WARNING, e);
        }

        /** {@inheritDoc} */
        @Override
        public void error(SAXParseException e) throws SAXException {
            Matcher m = ELEMENT_TYPE_PATTERN.matcher(e.getMessage());
            if (!m.find()) {
                processException(TYPE_ERROR, e);
            }
        }

        /** {@inheritDoc} */
        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            processException(TYPE_FATAL, e);
        }

        private void processException(int type, SAXParseException e) throws SAXException {
            StringBuilder message = new StringBuilder();

            switch (type) {
                case TYPE_WARNING:
                    message.append("Warning:");
                    break;

                case TYPE_ERROR:
                    message.append("Error:");
                    break;

                case TYPE_FATAL:
                    message.append("Fatal error:");
                    break;

                case TYPE_UNKNOWN:
                default:
                    message.append("Unknown:");
                    break;
            }

            message.append(EOL);
            message.append("  Public ID: ").append(e.getPublicId()).append(EOL);
            message.append("  System ID: ").append(e.getSystemId()).append(EOL);
            message.append("  Line number: ").append(e.getLineNumber()).append(EOL);
            message.append("  Column number: ").append(e.getColumnNumber()).append(EOL);
            message.append("  Message: ").append(e.getMessage()).append(EOL);

            final String logMessage = message.toString();

            switch (type) {
                case TYPE_WARNING:
                    LOGGER.warn(logMessage);
                    break;

                case TYPE_UNKNOWN:
                case TYPE_ERROR:
                case TYPE_FATAL:
                default:
                    throw new SAXException(logMessage);
            }
        }
    }
}
