package org.apache.maven.doxia.xsd;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import junit.framework.AssertionFailedError;

import org.apache.maven.doxia.parser.Parser;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.logging.Logger;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Abstract class to validate XML files.
 *
 * @author ltheussl
 *
 * @since 1.2
 */
public abstract class AbstractXmlValidator
        extends PlexusTestCase
{
    protected static final String EOL = System.getProperty( "line.separator" );

    /** XMLReader to validate xml file */
    private XMLReader xmlReader;

    /** HTML5 does not have a DTD or XSD, include option to disable validation */
    private boolean validate = true;

	/**
     * Filter fail message.
     *
     * @param message not null
     * @return <code>true</code> if the given message will fail the test.
     * @since 1.1.1
     */
    protected boolean isFailErrorMessage( String message )
    {
        return !( message.contains( "schema_reference.4: Failed to read schema document 'http://www.w3.org/2001/xml.xsd'" )
            || message.contains( "cvc-complex-type.4: Attribute 'alt' must appear on element 'img'." )
            || message.contains( "cvc-complex-type.2.4.a: Invalid content starting with element" )
            || message.contains( "cvc-complex-type.2.4.a: Invalid content was found starting with element" )
            || message.contains( "cvc-datatype-valid.1.2.1:" ) // Doxia allow space
            || message.contains( "cvc-attribute.3:" ) ); // Doxia allow space
    }

    @Override
    protected void tearDown()
            throws Exception
    {
        super.tearDown();

        xmlReader = null;
    }

    /**
     * Validate the test documents returned by {@link #getTestDocuments()} with DTD or XSD using xerces.
     *
     * @throws Exception if any
     *
     * @see #addNamespaces(String)
     * @see #getTestDocuments()
     */
    public void testValidateFiles()
        throws Exception
    {
        final Logger logger =
            ( (DefaultPlexusContainer) getContainer() ).getLoggerManager().getLoggerForComponent( Parser.ROLE );

        for ( Map.Entry<String, String> entry : getTestDocuments().entrySet() )
        {
            if ( logger.isDebugEnabled() )
            {
                logger.debug( "Validate '" + entry.getKey() + "'" );
            }

            List<ErrorMessage> errors = parseXML( entry.getValue() );

            for ( ErrorMessage error : errors )
            {
                if ( isFailErrorMessage( error.getMessage() ) )
                {
                    fail( entry.getKey() + EOL + error.toString() );
                }
                else
                {
                    if ( logger.isDebugEnabled() )
                    {
                        logger.debug( entry.getKey() + EOL + error.toString() );
                    }
                }
            }
        }
    }

    /**
     * @param content xml content not null
     * @return xml content with the wanted Doxia namespace
     */
    protected abstract String addNamespaces( String content );

    /**
     * @return a Map &lt; filePath, fileContent &gt; of files to validate.
     * @throws IOException if any
     */
    protected abstract Map<String,String> getTestDocuments()
            throws IOException;

    /**
     * Returns the EntityResolver that is used by the XMLReader for validation.
     *
     * @return an EntityResolver. Not null.
     */
    protected abstract EntityResolver getEntityResolver();

    /**
     * Returns whether the XMLReader should validate XML.
     * @return true if validation should be performed, false otherwise.
     */
    protected boolean isValidate() {
		return validate;
	}

    /**
     * Sets whether the XMLReader should validate XML.
     * @param validate true if validation should be performed, false otherwise.
     */
    protected void setValidate(boolean validate) {
		this.validate = validate;
	}

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    private XMLReader getXMLReader()
    {
        if ( xmlReader == null )
        {
            try
            {
                xmlReader = XMLReaderFactory.createXMLReader( "org.apache.xerces.parsers.SAXParser" );
                xmlReader.setFeature( "http://xml.org/sax/features/validation", validate );
                xmlReader.setFeature( "http://apache.org/xml/features/validation/schema", validate );
                xmlReader.setErrorHandler( new MessagesErrorHandler() );
                xmlReader.setEntityResolver( getEntityResolver() );
            }
            catch ( SAXNotRecognizedException e )
            {
                throw new AssertionFailedError( "SAXNotRecognizedException: " + e.getMessage() );
            }
            catch ( SAXNotSupportedException e )
            {
                throw new AssertionFailedError( "SAXNotSupportedException: " + e.getMessage() );
            }
            catch ( SAXException e )
            {
                throw new AssertionFailedError( "SAXException: " + e.getMessage() );
            }
        }

        ( (MessagesErrorHandler) xmlReader.getErrorHandler() ).clearMessages();

        return xmlReader;
    }

    /**
     * @param content
     * @return a list of ErrorMessage
     * @throws IOException is any
     * @throws SAXException if any
     */
    private List<ErrorMessage> parseXML( String content )
        throws IOException, SAXException
    {
        String xmlContent = addNamespaces( content );

        MessagesErrorHandler errorHandler = (MessagesErrorHandler) getXMLReader().getErrorHandler();

        getXMLReader().parse( new InputSource( new StringReader( xmlContent ) ) );

        return errorHandler.getMessages();
    }

    private static class ErrorMessage
        extends DefaultHandler
    {
        private final String level;
        private final String publicID;
        private final String systemID;
        private final int lineNumber;
        private final int columnNumber;
        private final String message;

        ErrorMessage( String level, String publicID, String systemID, int lineNumber, int columnNumber,
                             String message )
        {
            super();
            this.level = level;
            this.publicID = publicID;
            this.systemID = systemID;
            this.lineNumber = lineNumber;
            this.columnNumber = columnNumber;
            this.message = message;
        }

        /**
         * @return the level
         */
        protected String getLevel()
        {
            return level;
        }

        /**
         * @return the publicID
         */
        protected String getPublicID()
        {
            return publicID;
        }
        /**
         * @return the systemID
         */
        protected String getSystemID()
        {
            return systemID;
        }
        /**
         * @return the lineNumber
         */
        protected int getLineNumber()
        {
            return lineNumber;
        }
        /**
         * @return the columnNumber
         */
        protected int getColumnNumber()
        {
            return columnNumber;
        }
        /**
         * @return the message
         */
        protected String getMessage()
        {
            return message;
        }

        /** {@inheritDoc} */
        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder( 512 );

            sb.append( level ).append( EOL );
            sb.append( "  Public ID: " ).append( publicID ).append( EOL );
            sb.append( "  System ID: " ).append( systemID ).append( EOL );
            sb.append( "  Line number: " ).append( lineNumber ).append( EOL );
            sb.append( "  Column number: " ).append( columnNumber ).append( EOL );
            sb.append( "  Message: " ).append( message ).append( EOL );

            return sb.toString();
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + columnNumber;
            result = prime * result + ( ( level == null ) ? 0 : level.hashCode() );
            result = prime * result + lineNumber;
            result = prime * result + ( ( message == null ) ? 0 : message.hashCode() );
            result = prime * result + ( ( publicID == null ) ? 0 : publicID.hashCode() );
            result = prime * result + ( ( systemID == null ) ? 0 : systemID.hashCode() );
            return result;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
            {
                return true;
            }
            if ( obj == null )
            {
                return false;
            }
            if ( getClass() != obj.getClass() )
            {
                return false;
            }
            ErrorMessage other = (ErrorMessage) obj;
            if ( columnNumber != other.getColumnNumber() )
            {
                return false;
            }
            if ( level == null )
            {
                if ( other.getLevel() != null )
                {
                    return false;
                }
            }
            else if ( !level.equals( other.getLevel() ) )
            {
                return false;
            }
            if ( lineNumber != other.getLineNumber() )
            {
                return false;
            }
            if ( message == null )
            {
                if ( other.getMessage() != null )
                {
                    return false;
                }
            }
            else if ( !message.equals( other.getMessage() ) )
            {
                return false;
            }
            if ( publicID == null )
            {
                if ( other.getPublicID() != null )
                {
                    return false;
                }
            }
            else if ( !publicID.equals( other.getPublicID() ) )
            {
                return false;
            }
            if ( systemID == null )
            {
                if ( other.getSystemID() != null )
                {
                    return false;
                }
            }
            else if ( !systemID.equals( other.getSystemID() ) )
            {
                return false;
            }
            return true;
        }
    }

    private static class MessagesErrorHandler
        extends DefaultHandler
    {
        private final List<ErrorMessage> messages;

        MessagesErrorHandler()
        {
            messages = new ArrayList<>( 8 );
        }

        /** {@inheritDoc} */
        @Override
        public void warning( SAXParseException e )
            throws SAXException
        {
            addMessage( "Warning", e );
        }

        /** {@inheritDoc} */
        @Override
        public void error( SAXParseException e )
            throws SAXException
        {
            addMessage( "Error", e );
        }

        /** {@inheritDoc} */
        @Override
        public void fatalError( SAXParseException e )
            throws SAXException
        {
            addMessage( "Fatal error", e );
        }

        private void addMessage( String pre, SAXParseException e )
        {
            ErrorMessage error =
                new ErrorMessage( pre, e.getPublicId(), e.getSystemId(), e.getLineNumber(), e.getColumnNumber(),
                                  e.getMessage() );

            messages.add( error );
        }

        protected List<ErrorMessage> getMessages()
        {
            return Collections.unmodifiableList( messages );
        }

        protected void clearMessages()
        {
            messages.clear();
        }
    }
}
