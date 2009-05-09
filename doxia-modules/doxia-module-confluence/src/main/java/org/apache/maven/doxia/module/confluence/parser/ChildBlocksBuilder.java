package org.apache.maven.doxia.module.confluence.parser;

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

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;

/**
 * Re-usable builder that can be used to generate paragraph and list item text from a string containing all the content
 * and wiki formatting. This class is intentionally stateful, but cheap to create, so create one as needed and keep it
 * on the stack to preserve stateless behaviour in the caller.
 *
 * @author Dave Syer
 * @version $Id$
 * @since 1.1
 */
public class ChildBlocksBuilder
{
    private boolean insideBold = false;

    private boolean insideItalic = false;

    private boolean insideLink = false;

    private List blocks = new ArrayList();

    private StringBuffer text = new StringBuffer();

    private String input;

    private boolean insideMonospaced;

    /**
     * <p>Constructor for ChildBlocksBuilder.</p>
     *
     * @param input the input.
     */
    public ChildBlocksBuilder( String input )
    {
        this.input = input;
    }

    /**
     * Utility method to convert marked up content into blocks for rendering.
     *
     * @return a list of Blocks that can be used to render it
     */
    public List getBlocks()
    {
        List specialBlocks = new ArrayList();

        for ( int i = 0; i < input.length(); i++ )
        {
            char c = input.charAt( i );

            switch ( c )
            {
                case '*':
                    if ( insideBold )
                    {
                        insideBold = false;
                        specialBlocks = getList( new BoldBlock( getChildren( text, specialBlocks ) ), specialBlocks );
                        text = new StringBuffer();
                    }
                    else
                    {
                        text = addTextBlockIfNecessary( blocks, specialBlocks, text );
                        insideBold = true;
                    }

                    break;
                case '_':
                    if ( insideItalic )
                    {
                        insideItalic = false;
                        specialBlocks = getList( new ItalicBlock( getChildren( text, specialBlocks ) ), specialBlocks );
                        text = new StringBuffer();
                    }
                    else
                    {
                        text = addTextBlockIfNecessary( blocks, specialBlocks, text );
                        insideItalic = true;
                    }

                    break;
                case '[':
                    insideLink = true;
                    text = addTextBlockIfNecessary( blocks, specialBlocks, text );
                    break;
                case ']':
                    if ( insideLink )
                    {
                        boolean addHTMLSuffix = false;
                        String link = text.toString();

                        if ( !link.endsWith( ".html" ) )
                        {
                            if ( link.indexOf( "http" ) < 0 )
                            {
                                addHTMLSuffix = true;
                            }
                        }
                        if ( link.indexOf( "|" ) > 0 )
                        {
                            String[] pieces = StringUtils.split( text.toString(), "|" );

                            if ( addHTMLSuffix )
                            {
                                if ( pieces[1].indexOf( "#" ) < 0 )
                                {
                                    pieces[1] = pieces[1].concat( ".html" );
                                }
                                else
                                {
                                    if ( !pieces[1].startsWith( "#" ) )
                                    {
                                        String[] temp = pieces[1].split( "#" );
                                        pieces[1] = temp[0] + ".html#" + temp[1];
                                    }
                                }
                            }

                            blocks.add( new LinkBlock( pieces[1], pieces[0] ) );
                        }
                        else
                        {
                            String value = link;

                            if ( link.startsWith( "#" ) )
                            {
                                value = link.substring( 1 );
                            }

                            if ( addHTMLSuffix )
                            {
                                if ( link.indexOf( "#" ) < 0 )
                                {
                                    link = link.concat( ".html" );
                                }
                                else
                                {
                                    if ( !link.startsWith( "#" ) )
                                    {
                                        String[] temp = link.split( "#" );
                                        link = temp[0] + ".html#" + temp[1];
                                    }
                                }
                            }

                            blocks.add( new LinkBlock( link, value ) );
                        }

                        text = new StringBuffer();
                        insideLink = false;
                    }

                    break;
                case '{':

                    text = addTextBlockIfNecessary( blocks, specialBlocks, text );

                    if ( charAt( input, i ) == '{' ) // it's monospaced
                    {
                        i++;
                        insideMonospaced = true;
                    }
                    // else it's a confluence macro...

                    break;
                case '}':

                    // System.out.println( "line = " + line );

                    if ( charAt( input, i ) == '}' )
                    {
                        i++;
                        insideMonospaced = false;
                        specialBlocks = getList( new MonospaceBlock( getChildren( text, specialBlocks ) ),
                                                 specialBlocks );
                        text = new StringBuffer();
                    }
                    else
                    {
                        String name = text.toString();
                        if ( name.startsWith( "anchor:" ) )
                        {
                            blocks.add( new AnchorBlock( name.substring( "anchor:".length() ) ) );
                        }
                        else
                        {
                            blocks.add( new TextBlock( "{" + name + "}" ) );
                        }
                        text = new StringBuffer();
                    }

                    break;
                case '\\':

                    // System.out.println( "line = " + line );

                    if ( charAt( input, i ) == '\\' )
                    {
                        i++;
                        text = addTextBlockIfNecessary( blocks, specialBlocks, text );
                        blocks.add( new LinebreakBlock() );
                    }
                    else
                    {
                        i++;
                        text.append( input.charAt( i ) );
                    }

                    break;
                default:
                    text.append( c );
            }

            if ( !specialBlocks.isEmpty() )
            {
                if ( !insideItalic && !insideBold && !insideMonospaced )
                {
                    blocks.addAll( specialBlocks );
                    specialBlocks.clear();
                }
            }

        }

        if ( text.length() > 0 )
        {
            blocks.add( new TextBlock( text.toString() ) );
        }

        return blocks;
    }

    private List getList( Block block, List currentBlocks )
    {
        List list = new ArrayList();

        if ( insideBold || insideItalic || insideMonospaced )
        {
            list.addAll( currentBlocks );
        }

        list.add( block );

        return list;
    }

    private List getChildren( StringBuffer buffer, List currentBlocks )
    {
        String txt = buffer.toString().trim();

        if ( currentBlocks.isEmpty() && StringUtils.isEmpty( txt ) )
        {
            return new ArrayList();
        }

        ArrayList list = new ArrayList();

        if ( !insideBold && !insideItalic && !insideMonospaced )
        {
            list.addAll( currentBlocks );
        }

        if ( StringUtils.isEmpty( txt ) )
        {
            return list;
        }

        list.add( new TextBlock( txt ) );

        return list;
    }

    private static char charAt( String input, int i )
    {
        return input.length() > i + 1 ? input.charAt( i + 1 ) : '\0';
    }

    private StringBuffer addTextBlockIfNecessary( List blcks, List specialBlocks, StringBuffer txt )
    {
        if ( txt.length() == 0 )
        {
            return txt;
        }

        TextBlock textBlock = new TextBlock( txt.toString() );

        if ( !insideBold && !insideItalic && !insideMonospaced )
        {
            blcks.add( textBlock );
        }
        else
        {
            specialBlocks.add( textBlock );
        }

        return new StringBuffer();
    }

}
