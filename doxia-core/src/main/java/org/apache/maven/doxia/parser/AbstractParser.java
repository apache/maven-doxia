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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import java.util.Properties;

import org.apache.maven.doxia.logging.Log;
import org.apache.maven.doxia.logging.SystemStreamLog;
import org.apache.maven.doxia.macro.Macro;
import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.macro.MacroRequest;
import org.apache.maven.doxia.macro.manager.MacroManager;
import org.apache.maven.doxia.macro.manager.MacroNotFoundException;
import org.apache.maven.doxia.sink.Sink;
import org.codehaus.plexus.component.annotations.Requirement;

/**
 * An abstract base class that defines some convenience methods for parsers.
 * Provides a macro mechanism to give dynamic functionalities for the parsing.
 *
 * @author Jason van Zyl
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractParser
    implements Parser
{
    /** Indicates that a second parsing is required. */
    private boolean secondParsing = false;

    @Requirement
    private MacroManager macroManager;

    /** Log instance. */
    private Log logger;

    /**
     * Emit Doxia comment events when parsing comments?
     */
    private boolean emitComments = true;

    private static final String DOXIA_VERSION;

    static
    {
        final Properties props = new Properties();
        final InputStream is = AbstractParser.class.getResourceAsStream( "/build-info.properties" );

        if ( is == null )
        {
            props.setProperty( "version", "unknown" ); // should not happen
        }
        else
        {
            try
            {
                props.load( is );
            }
            catch ( IOException ex )
            {
                props.setProperty( "version", "unknown" ); // should not happen
            }
            finally
            {
                try
                {
                    is.close();
                }
                catch ( IOException ex )
                {
                    // oh well...
                }
            }
        }

        DOXIA_VERSION = props.getProperty( "version" );
    }

    /** {@inheritDoc} */
    public int getType()
    {
        return UNKNOWN_TYPE;
    }

    public void setEmitComments( boolean emitComments )
    {
        this.emitComments = emitComments;
    }

    public boolean isEmitComments()
    {
        return emitComments;
    }

    /**
     * Execute a macro on the given sink.
     *
     * @param macroId An id to lookup the macro.
     * @param request The corresponding MacroRequest.
     * @param sink The sink to receive the events.
     * @throws org.apache.maven.doxia.macro.MacroExecutionException if an error occurred during execution.
     * @throws org.apache.maven.doxia.macro.manager.MacroNotFoundException if the macro could not be found.
     */
    // Made public right now because of the structure of the APT parser and
    // all its inner classes.
    public void executeMacro( String macroId, MacroRequest request, Sink sink )
        throws MacroExecutionException, MacroNotFoundException
    {
        Macro macro = getMacroManager().getMacro( macroId );

        macro.enableLogging( getLog() );

        macro.execute( sink, request );
    }

    /**
     * Returns the current base directory.
     *
     * @return The base directory.
     *
     * @deprecated this does not work in multi-module builds, see DOXIA-373
     */
    protected File getBasedir()
    {
        // TODO: This is baaad, it should come in with the request.
        // (this is only used for macro requests, see AptParser)

        String basedir = System.getProperty( "basedir" );

        if ( basedir != null )
        {
            return new File( basedir );
        }

        return new File( new File( "" ).getAbsolutePath() );
    }

    /**
     * Convenience method to parse an arbitrary string and emit events into the given sink.
     *
     * @param string A string that provides the source input.
     * @param sink A sink that consumes the Doxia events.
     * @throws org.apache.maven.doxia.parser.ParseException if the string could not be parsed.
     * @since 1.1
     */
    public void parse( String string, Sink sink )
        throws ParseException
    {
        parse( new StringReader( string ), sink );
    }
    
    @Override
    public void parse( Reader source, Sink sink, String reference )
        throws ParseException
    {
        parse( source, sink );
    }

    /**
     * Set <code>secondParsing</code> to true, if we need a second parsing.
     *
     * @param second True for second parsing.
     */
    public void setSecondParsing( boolean second )
    {
        this.secondParsing = second;
    }

    /**
     * Indicates if we are currently parsing a second time.
     *
     * @return true if we are currently parsing a second time.
     * @since 1.1
     */
    protected boolean isSecondParsing()
    {
        return secondParsing;
    }

    /** {@inheritDoc} */
    public void enableLogging( Log log )
    {
        this.logger = log;
    }

    /**
     * Returns the current logger for this parser.
     * If no logger has been configured yet, a new SystemStreamLog is returned.
     *
     * @return Log
     * @since 1.1
     */
    protected Log getLog()
    {
        if ( logger == null )
        {
            logger = new SystemStreamLog();
        }

        return logger;
    }

    /**
     * Gets the current {@link MacroManager}.
     *
     * @return The current {@link MacroManager}.
     * @since 1.1
     */
    protected MacroManager getMacroManager()
    {
        return macroManager;
    }

    /**
     * Initialize the parser. This is called first by
     * {@link #parse(java.io.Reader, org.apache.maven.doxia.sink.Sink)} and can be used
     * to set the parser into a clear state so it can be re-used.
     *
     * @since 1.1.2
     */
    protected void init()
    {
        // nop
    }

    /**
     * The current Doxia version.
     *
     * @return the current Doxia version as a String.
     *
     * @since 1.2
     */
    protected static String doxiaVersion()
    {
        return DOXIA_VERSION;
    }
}
