package org.apache.maven.doxia.macro;

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

import java.util.Map;
import org.apache.maven.doxia.sink.Sink;
import org.codehaus.plexus.component.annotations.Component;

/**
 * Server-Side Include directive, to insert a SSI into the output.
 * Required parameter is <code>function</code> to define SSI function, then
 * additional parameters are completely free.
 * @since 1.7
 */
@Component( role = Macro.class, hint = "ssi" )
public class SsiMacro
    extends AbstractMacro
{
    private static final String PARAM_FUNCTION = "function";

    private boolean isInternalParameter( String name )
    {
        return PARAM_FUNCTION.equals( name ) || MacroRequest.isInternalParameter( name );
    }
    /** {@inheritDoc} */
    public void execute( Sink sink, MacroRequest request )
        throws MacroExecutionException
    {
        String function = (String) request.getParameter( PARAM_FUNCTION );

        required( PARAM_FUNCTION, function );

        StringBuilder buff = new StringBuilder();
        buff.append( '#' );
        buff.append( function );

        for ( Map.Entry<String, Object> entry : request.getParameters().entrySet() )
        {
            if ( !isInternalParameter( entry.getKey() ) )
            {
                buff.append( ' ' );
                buff.append( entry.getKey() );
                buff.append( "=\"" );
                buff.append( entry.getValue() );
                buff.append( '"' );
            }
        }

        buff.append( ' ' );
        sink.comment( buff.toString() );
    }
}
