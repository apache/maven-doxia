package org.apache.maven.doxia.module.confluence.parser;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.apache.maven.doxia.module.common.ByLineSource;
import org.apache.maven.doxia.parser.ParseException;
import org.codehaus.plexus.util.StringUtils;

public class ParagraphBlockParser
    implements BlockParser
{
    public boolean accept( String line )
    {
        return true;
    }

    public Block visit( String line, ByLineSource source )
        throws ParseException
    {
        boolean insideBold = false;
        boolean insideItalic = false;
        boolean insideLink = false;

        List blocks = new ArrayList();

        StringBuffer text = new StringBuffer();

        do
        {
            if ( line.trim().length() == 0 )
            {
                break;
            }

            for ( int i = 0; i < line.length(); i++ )
            {
                char c = line.charAt( i );

                switch ( c )
                {
                    case '*':
                        if ( insideBold )
                        {
                            TextBlock tb = new TextBlock( text.toString() );
                            blocks.add( new BoldBlock( Arrays.asList( new Block[]{tb} ) ) );
                            text = new StringBuffer();
                        }
                        else
                        {
                            blocks.add( new TextBlock( text.toString() ) );
                            text = new StringBuffer();
                            insideBold = true;
                        }

                        break;
                    case '_':
                        if ( insideItalic )
                        {
                            TextBlock tb = new TextBlock( text.toString() );
                            blocks.add( new ItalicBlock( Arrays.asList( new Block[]{tb} ) ) );
                            text = new StringBuffer();
                        }
                        else
                        {
                            blocks.add( new TextBlock( text.toString() ) );
                            text = new StringBuffer();
                            insideItalic = true;
                        }

                        break;
                    case '[':
                        insideLink = true;
                        blocks.add( new TextBlock( text.toString() ) );
                        text = new StringBuffer();
                        break;
                    case ']':
                        if ( insideLink )
                        {
                            String link = text.toString();

                            if ( link.indexOf( "|" ) > 0 )
                            {
                                String[] pieces = StringUtils.split( text.toString(), "|" );
                                blocks.add( new LinkBlock( pieces[1], pieces[0] ) );
                            }
                            else
                            {
                                blocks.add( new LinkBlock( link, link ) );
                            }

                            text = new StringBuffer();
                        }

                        break;
                    case '{':

                        if ( line.charAt( i + 1 ) == '{' )
                        {
                            i++;
                            blocks.add( new TextBlock( text.toString() ) );
                            text = new StringBuffer();
                        }
                        else
                        {
                            text.append( c );
                        }

                        break;
                    case '}':

                        if ( line.charAt( i + 1 ) == '}' )
                        {
                            i++;
                            TextBlock tb = new TextBlock( text.toString() );
                            blocks.add( new MonospaceBlock( Arrays.asList( new Block[]{tb} ) ) );
                            text = new StringBuffer();
                        }
                        else
                        {
                            text.append( c );
                        }

                        break;
                    default:
                        text.append( c );
                }
            }
        }
        while ( ( line = source.getNextLine() ) != null );

        if ( text.length() > 0 )
        {
            blocks.add( new TextBlock( text.toString() ) );
        }

        return new ParagraphBlock( blocks );
    }
}
