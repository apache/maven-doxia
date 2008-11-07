package org.apache.maven.doxia.parser;

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

import org.apache.maven.doxia.logging.Log;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Convenience class to beautify SAXParseException messages.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
class MessagesErrorHandler
    extends DefaultHandler
{
    /** The vm line separator */
    private static final String EOL = System.getProperty( "line.separator" );

    private static final int TYPE_UNKNOWN = 0;

    private static final int TYPE_WARNING = 1;

    private static final int TYPE_ERROR = 2;

    private static final int TYPE_FATAL = 3;

    private final Log log;

    public MessagesErrorHandler( Log log )
    {
        this.log = log;
    }

    /** {@inheritDoc} */
    public void warning( SAXParseException e )
        throws SAXException
    {
        processException( TYPE_WARNING, e );
    }

    /** {@inheritDoc} */
    public void error( SAXParseException e )
        throws SAXException
    {
        processException( TYPE_ERROR, e );
    }

    /** {@inheritDoc} */
    public void fatalError( SAXParseException e )
        throws SAXException
    {
        processException( TYPE_FATAL, e );
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    private void processException( int type, SAXParseException e )
        throws SAXException
    {
        StringBuffer message = new StringBuffer();

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
        message.append( "  Public ID: " + e.getPublicId() ).append( EOL );
        message.append( "  System ID: " + e.getSystemId() ).append( EOL );
        message.append( "  Line number: " + e.getLineNumber() ).append( EOL );
        message.append( "  Column number: " + e.getColumnNumber() ).append( EOL );
        message.append( "  Message: " + e.getMessage() ).append( EOL );

        switch ( type )
        {
            case TYPE_WARNING:
                if ( log.isWarnEnabled() )
                {
                    log.warn( message.toString() );
                }
                break;

            case TYPE_UNKNOWN:
            case TYPE_ERROR:
            case TYPE_FATAL:
            default:
                throw new SAXException( message.toString() );
        }
    }
}