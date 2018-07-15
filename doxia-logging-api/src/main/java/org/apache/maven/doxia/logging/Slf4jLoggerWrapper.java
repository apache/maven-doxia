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

import org.slf4j.Logger;

/**
 * Wrap a Slf4j logger into a Doxia logger.
 *
 * @since 1.7
 * @deprecated use directly slf4j
 */
@Deprecated
public class Slf4jLoggerWrapper implements Log
{

    private Logger logger;

    public Slf4jLoggerWrapper( Logger log )
    {
        logger = log;
    }

    public void setLogLevel( int level )
    {
        // ignore - managed outside
    }

    public void debug( CharSequence content )
    {
        if ( logger.isDebugEnabled() )
            logger.debug( toString( content ) );
    }

    public void debug( CharSequence content, Throwable error )
    {
        if ( logger.isDebugEnabled() )
            logger.debug( toString( content ), error );
    }

    public void debug( Throwable error )
    {
        logger.debug( "", error );
    }

    public void info( CharSequence content )
    {
        if ( logger.isInfoEnabled() )
            logger.info( toString( content ) );
    }

    public void info( CharSequence content, Throwable error )
    {
        if ( logger.isInfoEnabled() )
            logger.info( toString( content ), error );
    }

    public void info( Throwable error )
    {
        logger.info( "", error );
    }

    public void warn( CharSequence content )
    {
        if ( logger.isWarnEnabled() )
            logger.warn( toString( content ) );
    }

    public void warn( CharSequence content, Throwable error )
    {
        if ( logger.isWarnEnabled() )
            logger.warn( toString( content ), error );
    }

    public void warn( Throwable error )
    {
        logger.warn( "", error );
    }

    public void error( CharSequence content )
    {
        if ( logger.isErrorEnabled() )
            logger.error( toString( content ) );
    }

    public void error( CharSequence content, Throwable error )
    {
        if ( logger.isErrorEnabled() )
            logger.error( toString( content ), error );
    }

    public void error( Throwable error )
    {
        logger.error( "", error );
    }

    public boolean isDebugEnabled()
    {
        return logger.isDebugEnabled();
    }

    public boolean isInfoEnabled()
    {
        return logger.isInfoEnabled();
    }

    public boolean isWarnEnabled()
    {
        return logger.isWarnEnabled();
    }

    public boolean isErrorEnabled()
    {
        return logger.isErrorEnabled();
    }

    private String toString( CharSequence content )
    {
        if ( content == null )
        {
            return "";
        }

        return content.toString();
    }
}
