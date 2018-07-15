package org.apache.maven.doxia.logging;

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

import org.codehaus.plexus.logging.Logger;

/**
 * Wrap a Plexus logger into a Doxia logger.
 * Based on org.apache.maven.plugin.logging.Log.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @since 1.1
 * @deprecated use slf4j
 */
public class PlexusLoggerWrapper
    implements Log
{
    private final Logger logger;

    /**
     * <p>Constructor for PlexusLoggerWrapper.</p>
     *
     * @param logger the Plexus logger to wrap.
     */
    public PlexusLoggerWrapper( Logger logger )
    {
        this.logger = logger;
    }

    /** {@inheritDoc} */
    public void setLogLevel( int level )
    {
        if ( level <= LEVEL_DEBUG )
        {
            logger.setThreshold( Logger.LEVEL_DEBUG );
        }
        else if ( level <= LEVEL_INFO )
        {
            logger.setThreshold( Logger.LEVEL_INFO );
        }
        else if ( level <= LEVEL_WARN )
        {
            logger.setThreshold( Logger.LEVEL_WARN );
        }
        else if ( level <= LEVEL_ERROR )
        {
            logger.setThreshold( Logger.LEVEL_ERROR );
        }
        else
        {
            logger.setThreshold( Logger.LEVEL_DISABLED );
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param content a {@link java.lang.CharSequence} object.
     */
    public void debug( CharSequence content )
    {
        logger.debug( toString( content ) );
    }

    /** {@inheritDoc} */
    public void debug( CharSequence content, Throwable error )
    {
        logger.debug( toString( content ), error );
    }

    /** {@inheritDoc} */
    public void debug( Throwable error )
    {
        logger.debug( "", error );
    }

    /** {@inheritDoc} */
    public void info( CharSequence content )
    {
        logger.info( toString( content ) );
    }

    /** {@inheritDoc} */
    public void info( CharSequence content, Throwable error )
    {
        logger.info( toString( content ), error );
    }

    /**
     * {@inheritDoc}
     *
     * @param error a {@link java.lang.Throwable} object.
     */
    public void info( Throwable error )
    {
        logger.info( "", error );
    }

    /**
     * {@inheritDoc}
     *
     * @param content a {@link java.lang.CharSequence} object.
     */
    public void warn( CharSequence content )
    {
        logger.warn( toString( content ) );
    }

    /** {@inheritDoc} */
    public void warn( CharSequence content, Throwable error )
    {
        logger.warn( toString( content ), error );
    }

    /** {@inheritDoc} */
    public void warn( Throwable error )
    {
        logger.warn( "", error );
    }

    /** {@inheritDoc} */
    public void error( CharSequence content )
    {
        logger.error( toString( content ) );
    }

    /** {@inheritDoc} */
    public void error( CharSequence content, Throwable error )
    {
        logger.error( toString( content ), error );
    }

    /**
     * {@inheritDoc}
     *
     * @param error a {@link java.lang.Throwable} object.
     */
    public void error( Throwable error )
    {
        logger.error( "", error );
    }

    /**
     * {@inheritDoc}
     *
     * @return a boolean.
     */
    public boolean isDebugEnabled()
    {
        return logger.isDebugEnabled();
    }

    /**
     * {@inheritDoc}
     *
     * @return a boolean.
     */
    public boolean isInfoEnabled()
    {
        return logger.isInfoEnabled();
    }

    /**
     * {@inheritDoc}
     *
     * @return a boolean.
     */
    public boolean isWarnEnabled()
    {
        return logger.isWarnEnabled();
    }

    /**
     * {@inheritDoc}
     *
     * @return a boolean.
     */
    public boolean isErrorEnabled()
    {
        return logger.isErrorEnabled();
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    private String toString( CharSequence content )
    {
        if ( content == null )
        {
            return "";
        }

        return content.toString();
    }
}
