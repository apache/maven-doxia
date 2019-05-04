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

    private boolean insideLinethrough = false;

    private boolean insideUnderline = false;

    private boolean insideSub = false;

    private boolean insideSup = false;

    private List<Block> blocks = new ArrayList<>();

    private StringBuilder text = new StringBuilder();

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
    public List<Block> getBlocks()
    {
        List<Block> specialBlocks = new ArrayList<>();

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
                        text = new StringBuilder();
                    }
                    else if ( insideMonospaced )
                    {
                        text.append( c );
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
                        text = new StringBuilder();
                    }
                    else if ( insideLink || insideMonospaced )
                    {
                        text.append( c );
                    }
                    else
                    {
                        text = addTextBlockIfNecessary( blocks, specialBlocks, text );
                        insideItalic = true;
                    }

                    break;
                case '-':
                    if ( insideLinethrough )
                    {
                        insideLinethrough = false;
                        blocks.add( new LinethroughBlock( text.toString() ) );
                        text = new StringBuilder();
                    }
                    else if ( insideLink || insideMonospaced )
                    {
                        text.append( c );    
                    }
                    else
                    {
                        text = addTextBlockIfNecessary( blocks, specialBlocks, text );
                        insideLinethrough = true;                            
                    }
                    break;
                case '+':
                    if ( insideUnderline )
                    {
                        insideUnderline = false;
                        blocks.add( new UnderlineBlock( text.toString() ) );
                        text = new StringBuilder();
                    }
                    else if ( insideLink || insideMonospaced )
                    {
                        text.append( c );    
                    }
                    else
                    {
                        text = addTextBlockIfNecessary( blocks, specialBlocks, text );
                        insideUnderline = true;                            
                    }
                    break;
                case '~':
                    if ( insideSub )
                    {
                        insideSub = false;
                        blocks.add( new SubBlock( text.toString() ) );
                        text = new StringBuilder();
                    }
                    else if ( insideLink || insideMonospaced )
                    {
                        text.append( c );    
                    }
                    else
                    {
                        text = addTextBlockIfNecessary( blocks, specialBlocks, text );
                        insideSub = true;                            
                    }
                    break;
                case '^':
                    if ( insideSup )
                    {
                        insideSup = false;
                        blocks.add( new SupBlock( text.toString() ) );
                        text = new StringBuilder();
                    }
                    else if ( insideLink || insideMonospaced )
                    {
                        text.append( c );    
                    }
                    else
                    {
                        text = addTextBlockIfNecessary( blocks, specialBlocks, text );
                        insideSup = true;                            
                    }
                    break;
                case '[':
                    if ( insideMonospaced )
                    {
                        text.append( c );
                    }
                    else
                    {
                        insideLink = true;
                        text = addTextBlockIfNecessary( blocks, specialBlocks, text );
                    }
                    break;
                case ']':
                    if ( insideLink )
                    {
                        boolean addHTMLSuffix = false;
                        String link = text.toString();

                        if ( !link.endsWith( ".html" ) )
                        {
                            if ( !link.contains( "http" ) )
                            {
                                // relative path: see DOXIA-298
                                addHTMLSuffix = true;
                            }
                        }
                        if ( link.contains( "|" ) )
                        {
                            String[] pieces = StringUtils.split( text.toString(), "|" );
                            
                            if ( pieces[1].startsWith( "^" ) )
                            {
                                // use the "file attachment" ^ syntax to force verbatim link: needed to allow actually
                                // linking to some non-html resources
                                pieces[1] = pieces[1].substring( 1 ); // now just get rid of the lead ^
                                addHTMLSuffix = false; // force verbatim link to support attaching files/resources (not
                                                       // just .html files)
                            }

                            if ( addHTMLSuffix )
                            {
                                if ( !pieces[1].contains( "#" ) )
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
                            else if ( link.startsWith( "^" ) )
                            {
                                link = link.substring( 1 );  // chop off the lead ^ from link and from value
                                value = link;
                                addHTMLSuffix = false; // force verbatim link to support attaching files/resources (not
                                                       // just .html files)
                            }

                            if ( addHTMLSuffix )
                            {
                                if ( !link.contains( "#" ) )
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

                        text = new StringBuilder();
                        insideLink = false;
                    }
                    else if ( insideMonospaced )
                    {
                        text.append( c );
                    }

                    break;
                case '{':
                    if ( insideMonospaced )
                    {
                        text.append( c );
                    }
                    else
                    {
                        text = addTextBlockIfNecessary( blocks, specialBlocks, text );

                        if ( nextChar( input, i ) == '{' ) // it's monospaced
                        {
                            i++;
                            insideMonospaced = true;
                        }
                    }
                    // else it's a confluence macro...

                    break;
                case '}':
                    if ( nextChar( input, i ) == '}' )
                    {
                        i++;
                        insideMonospaced = false;
                        specialBlocks = getList( new MonospaceBlock( getChildren( text, specialBlocks ) ),
                                                 specialBlocks );
                        text = new StringBuilder();
                    }
                    else if ( insideMonospaced )
                    {
                        text.append( c );
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
                        text = new StringBuilder();
                    }

                    break;
                case '\\':
                    if ( insideMonospaced )
                    {
                        text.append( c );
                    }
                    else if ( nextChar( input, i ) == '\\' )
                    {
                        i++;
                        text = addTextBlockIfNecessary( blocks, specialBlocks, text );
                        blocks.add( new LinebreakBlock() );
                    }
                    else
                    {
                        // DOXIA-467 single trailing backward slash, double is considered linebreak
                        if ( i == input.length() - 1 )
                        {
                            text.append( '\\' );
                        }
                        else
                        {
                            text.append( input.charAt( ++i ) );
                        }
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

    private List<Block> getList( Block block, List<Block> currentBlocks )
    {
        List<Block> list = new ArrayList<>();

        if ( insideBold || insideItalic || insideMonospaced )
        {
            list.addAll( currentBlocks );
        }

        list.add( block );

        return list;
    }

    private List<Block> getChildren( StringBuilder buffer, List<Block> currentBlocks )
    {
        String txt = buffer.toString().trim();

        if ( currentBlocks.isEmpty() && StringUtils.isEmpty( txt ) )
        {
            return new ArrayList<>();
        }

        ArrayList<Block> list = new ArrayList<>();

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

    private static char nextChar( String input, int i )
    {
        return input.length() > i + 1 ? input.charAt( i + 1 ) : '\0';
    }

    private StringBuilder addTextBlockIfNecessary( List<Block> blcks, List<Block> specialBlocks, StringBuilder txt )
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

        return new StringBuilder();
    }

}
