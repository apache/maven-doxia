/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.maven.doxia.module.confluence.parser;

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
        boolean insideMonospace = false;
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
                            insideMonospace = true;
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

        System.out.println( "paragraph blocks.size() = " + blocks.size() );

        return new ParagraphBlock( blocks );
    }
}
