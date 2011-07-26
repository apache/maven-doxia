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

import org.apache.maven.doxia.sink.Sink;
import org.codehaus.plexus.util.StringUtils;

/**
 * Macro for embedding Flash (SWF) within Maven documentation.
 *
 * @plexus.component role="org.apache.maven.doxia.macro.Macro"
 * role-hint="swf"
 *
 * @author <a href="mailto:steve.motola@gmail.com">Steve Motola</a>
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class SwfMacro
    extends AbstractMacro
{
    /** {@inheritDoc} */
    public void execute( Sink sink, MacroRequest request )
        throws MacroExecutionException
    {
        // parameter defaults
        String src = "";
        String id = "swf";
        String width = "400";
        String height = "400";
        String quality = "high";
        String menu = "false";
        String loop = "0";
        String play = "true";
        String version = "9,0,45,0";
        String allowScript = "sameDomain";

        // assign parameters
        for ( String key : request.getParameters().keySet() )
        {
            String str = (String) request.getParameter( key );
            if ( key.equals( "src" ) )
            {
                if ( StringUtils.isNotEmpty( str ) )
                {
                    src = str;
                }
            }
            else if ( key.equals( "id" ) )
            {
                if ( StringUtils.isNotEmpty( str ) )
                {
                    id = str;
                }
            }
            else if ( key.equals( "width" ) )
            {
                if ( StringUtils.isNotEmpty( str ) )
                {
                    width = str;
                }
            }
            else if ( key.equals( "height" ) )
            {
                if ( StringUtils.isNotEmpty( str ) )
                {
                    height = str;
                }
            }
            else if ( key.equals( "quality" ) )
            {
                if ( StringUtils.isNotEmpty( str ) )
                {
                    quality = str;
                }
            }
            else if ( key.equals( "menu" ) )
            {
                if ( StringUtils.isNotEmpty( str ) )
                {
                    menu = str;
                }
            }
            else if ( key.equals( "loop" ) )
            {
                if ( StringUtils.isNotEmpty( str ) )
                {
                    loop = str;
                }
            }
            else if ( key.equals( "play" ) )
            {
                if ( StringUtils.isNotEmpty( str ) )
                {
                    play = str;
                }
            }
            else if ( key.equals( "version" ) )
            {
                // enable version shorthand
                // TODO: put in other shorthand versions
                if ( str.equals( "6" ) )
                {
                    version = "6,0,29,0";
                }
                else
                {
                    if ( str.equals( "9" ) )
                    {
                        version = "9,0,45,0";
                    }
                    else
                    {
                        if ( StringUtils.isNotEmpty( str ) )
                        {
                            version = str;
                        }
                    }
                }
            }
            else if ( key.equals( "allowScript" ) )
            {
                if ( StringUtils.isNotEmpty( str ) )
                {
                    allowScript = str;
                }
            }
        }

        StringBuffer content = new StringBuffer();
        content.append( "<center>" ).append( EOL );
        content.append( "<object classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" " )
            .append( "codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=" )
            .append( version ).append( "\" width=\"" ).append( width ).append( "\" height=\"" ).append( height )
            .append( "\" id=\"" ).append( id ).append( "\">" ).append( EOL );
        content.append( "<param name=\"movie\" value=\"" ).append( src ).append( "\" />" ).append( EOL );
        content.append( "<param name=\"quality\" value=\"" ).append( quality ).append( "\" />" ).append( EOL );
        content.append( "<param name=\"menu\" value=\"" ).append( menu ).append( "\" />" ).append( EOL );
        content.append( "<param name=\"loop\" value=\"" ).append( loop ).append( "\" />" ).append( EOL );
        content.append( "<param name=\"play\" value=\"" ).append( play ).append( "\" />" ).append( EOL );
        content.append( "<param name=\"allowScriptAccess\" value=\"" )
            .append( allowScript ).append( "\" />" ).append( EOL );
        content.append( "<embed src=\"" ).append( src ).append( "\" width=\"" ).append( width ).append( "\" height=\"" )
            .append( height ).append( "\" loop=\"" ).append( loop ).append( "\" play=\"" ).append( play )
            .append( "\" quality=\"" ).append( quality ).append( "\" allowScriptAccess=\"" ).append( allowScript )
            .append( "\" " ).append( "pluginspage=\"http://www.macromedia.com/go/getflashplayer\" " )
            .append( "type=\"application/x-shockwave-flash\" menu=\"" ).append( menu ).append( "\">" ).append( EOL );
        content.append( "</embed>" ).append( EOL );
        content.append( "</object>" ).append( EOL );
        content.append( "</center>" ).append( EOL );

        sink.rawText( content.toString() );
    }
}