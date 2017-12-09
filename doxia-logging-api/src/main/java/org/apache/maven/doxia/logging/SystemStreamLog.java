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
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Logger with "standard" output and error output stream. The log prefix is voluntarily in lower case.
 * <br>
 * Based on <code>org.apache.maven.plugin.logging.SystemStreamLog</code>.
 *
 * @author jdcasey
 * @author ltheussl
 * @version $Id$
 * @since 1.1
 */
public class SystemStreamLog
    implements Log
{
    private static final String EOL = System.getProperty( "line.separator" );

    private int currentLevel = LEVEL_INFO;

    /** {@inheritDoc} */
    public void setLogLevel( int level )
    {
        if ( level <= LEVEL_DEBUG )
        {
            currentLevel = LEVEL_DEBUG;
        }
        else if ( level <= LEVEL_INFO )
        {
            currentLevel = LEVEL_INFO;
        }
        else if ( level <= LEVEL_WARN )
        {
            currentLevel = LEVEL_WARN;
        }
        else if ( level <= LEVEL_ERROR )
        {
            currentLevel = LEVEL_ERROR;
        }
        else
        {
            currentLevel = LEVEL_DISABLED;
        }
    }

    /** {@inheritDoc} */
    public void debug( CharSequence content )
    {
        if ( isDebugEnabled() )
        {
            print( "debug", content );
        }
    }

    /** {@inheritDoc} */
    public void debug( CharSequence content, Throwable error )
    {
        if ( isDebugEnabled() )
        {
            print( "debug", content, error );
        }
    }

    /** {@inheritDoc} */
    public void debug( Throwable error )
    {
        if ( isDebugEnabled() )
        {
            print( "debug", error );
        }
    }

    /** {@inheritDoc} */
    public void info( CharSequence content )
    {
        if ( isInfoEnabled() )
        {
            print( "info", content );
        }
    }

    /** {@inheritDoc} */
    public void info( CharSequence content, Throwable error )
    {
        if ( isInfoEnabled() )
        {
            print( "info", content, error );
        }
    }

    /** {@inheritDoc} */
    public void info( Throwable error )
    {
        if ( isInfoEnabled() )
        {
            print( "info", error );
        }
    }

    /** {@inheritDoc} */
    public void warn( CharSequence content )
    {
        if ( isWarnEnabled() )
        {
            print( "warn", content );
        }
    }

    /** {@inheritDoc} */
    public void warn( CharSequence content, Throwable error )
    {
        if ( isWarnEnabled() )
        {
            print( "warn", content, error );
        }
    }

    /** {@inheritDoc} */
    public void warn( Throwable error )
    {
        if ( isWarnEnabled() )
        {
            print( "warn", error );
        }
    }

    /** {@inheritDoc} */
    public void error( CharSequence content )
    {
        if ( isErrorEnabled() )
        {
            System.err.println( "[error] " + content.toString() );
        }
    }

    /** {@inheritDoc} */
    public void error( CharSequence content, Throwable error )
    {
        if ( isErrorEnabled() )
        {
            StringWriter sWriter = new StringWriter();
            PrintWriter pWriter = new PrintWriter( sWriter );

            error.printStackTrace( pWriter );

            System.err.println( "[error] " + content.toString()
                + EOL + EOL + sWriter.toString() );
        }
    }

    /** {@inheritDoc} */
    public void error( Throwable error )
    {
        if ( isErrorEnabled() )
        {
            StringWriter sWriter = new StringWriter();
            PrintWriter pWriter = new PrintWriter( sWriter );

            error.printStackTrace( pWriter );

            System.err.println( "[error] " + sWriter.toString() );
        }
    }

    /** {@inheritDoc} */
    public boolean isDebugEnabled()
    {
        return ( currentLevel <= LEVEL_DEBUG );
    }

    /** {@inheritDoc} */
    public boolean isInfoEnabled()
    {
        return ( currentLevel <= LEVEL_INFO );
    }

    /** {@inheritDoc} */
    public boolean isWarnEnabled()
    {
        return ( currentLevel <= LEVEL_WARN );
    }

    /** {@inheritDoc} */
    public boolean isErrorEnabled()
    {
        return ( currentLevel <= LEVEL_ERROR );
    }

      //
     // private
    //

    private void print( String prefix, CharSequence content )
    {
        System.out.println( "[" + prefix + "] " + content.toString() );
    }

    private void print( String prefix, Throwable error )
    {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter( sWriter );

        error.printStackTrace( pWriter );

        System.out.println( "[" + prefix + "] " + sWriter.toString() );
    }

    private void print( String prefix, CharSequence content, Throwable error )
    {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter( sWriter );

        error.printStackTrace( pWriter );

        System.out.println( "[" + prefix + "] " + content.toString()
            + EOL + EOL + sWriter.toString() );
    }
}
