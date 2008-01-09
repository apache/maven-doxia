package org.apache.maven.doxia.module.xwiki.parser;

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

import org.apache.maven.doxia.module.confluence.parser.Block;
import org.apache.maven.doxia.module.confluence.parser.BoldBlock;
import org.apache.maven.doxia.module.confluence.parser.ItalicBlock;
import org.apache.maven.doxia.module.confluence.parser.LinebreakBlock;
import org.apache.maven.doxia.module.confluence.parser.LinkBlock;
import org.apache.maven.doxia.module.confluence.parser.TextBlock;
import org.apache.maven.doxia.parser.ParseException;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Re-usable builder that can be used to generate paragraph and list item text from a string containing all the content
 * and wiki formatting. This class is intentionally stateful, but cheap to create, so create one as needed and keep it
 * on the stack to preserve stateless behaviour in the caller.
 *
 * @author Dave Syer
 */
public class ChildBlocksBuilder
{
    private MacroParser macroParser = new MacroParser();

    private boolean insideBold = false;

    private boolean insideItalic = false;

    private boolean insideLink = false;

    private List blocks = new ArrayList();

    private StringBuffer text = new StringBuffer();

    private String input;

    private boolean insideMonospaced;

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
        throws ParseException
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
                case '~':
                    if ( charAt( input, i ) == '~' )
                    {
                        i++;
                        if ( insideItalic )
                        {
                            insideItalic = false;
                            specialBlocks =
                                getList( new ItalicBlock( getChildren( text, specialBlocks ) ), specialBlocks );
                            text = new StringBuffer();
                        }
                        else
                        {
                            text = addTextBlockIfNecessary( blocks, specialBlocks, text );
                            insideItalic = true;
                        }
                    }
                    else
                    {
                        text.append( c );
                    }
                    break;
                case '[':
                    insideLink = true;
                    text = addTextBlockIfNecessary( blocks, specialBlocks, text );
                    break;
                case ']':
                    if ( insideLink )
                    {
                        // We're not fully parsing the XWiki link format here. We're only extracting the alias
                        // and link since the Doxia Sink only supports those 2 attributes. In practice XWiki
                        // links can have many more items: virtual wiki name, space name, page name, query string,
                        // anchor, interwiki links and target. These are parsed in the XWiki Sink.

                        String link = text.toString();

                        if ( link.indexOf( "|" ) > 0 )
                        {
                            parseLinkSeparator( link, "|" );
                        }
                        else if ( link.indexOf( ">" ) > 0 )
                        {
                            parseLinkSeparator( link, ">" );
                        }
                        else
                        {
                            blocks.add( new LinkBlock( link, "" ) );
                        }

                        text = new StringBuffer();
                        insideLink = false;
                    }

                    break;
                case '{':
                    text = addTextBlockIfNecessary( blocks, specialBlocks, text );
                    i = macroParser.parse( input, i + 1, blocks );
                    break;
                case '\\':

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

    private void parseLinkSeparator( String link, String separator )
    {
        String[] pieces = StringUtils.split( text.toString(), separator );

        // If the right side of the separator starts with underscore then it's a target, so do nothing.
        if ( pieces[1].startsWith( "_" ) )
        {
            blocks.add( new LinkBlock( link, "" ) );
        }
        else
        {
            blocks.add( new LinkBlock( pieces[1], pieces[0] ) );
        }
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
        String text = buffer.toString().trim();

        if ( currentBlocks.isEmpty() && StringUtils.isEmpty( text ) )
        {
            return new ArrayList();
        }

        ArrayList list = new ArrayList();

        if ( !insideBold && !insideItalic && !insideMonospaced )
        {
            list.addAll( currentBlocks );
        }

        if ( StringUtils.isEmpty( text ) )
        {
            return list;
        }

        list.add( new TextBlock( text ) );

        return list;
    }

    private static char charAt( String input, int i )
    {
        return input.length() > i + 1 ? input.charAt( i + 1 ) : '\0';
    }

    private StringBuffer addTextBlockIfNecessary( List blocks, List specialBlocks, StringBuffer text )
    {
        if ( text.length() == 0 )
        {
            return text;
        }

        TextBlock textBlock = new TextBlock( text.toString() );

        if ( !insideBold && !insideItalic && !insideMonospaced )
        {
            blocks.add( textBlock );
        }
        else
        {
            specialBlocks.add( textBlock );
        }

        return new StringBuffer();
    }

}