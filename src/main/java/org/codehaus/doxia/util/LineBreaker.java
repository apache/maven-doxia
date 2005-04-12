/*
 * Copyright (c) 1999,2000 Pixware. 
 *
 * Author: Hussein Shafie
 *
 * This file is part of the Pixware APT tools.
 * For conditions of distribution and use, see the accompanying legal.txt file.
 */
package org.codehaus.doxia.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class LineBreaker
{
    public static final int DEFAULT_MAX_LINE_LENGTH = 78;

    private Writer destination;
    private BufferedWriter writer;
    private int maxLineLength;
    private int lineLength = 0;
    private StringBuffer word = new StringBuffer( 1024 );

    public LineBreaker( Writer out )
    {
        this( out, DEFAULT_MAX_LINE_LENGTH );
    }

    public LineBreaker( Writer out, int maxLineLength )
    {
        if ( maxLineLength <= 0 )
            throw new IllegalArgumentException( "maxLineLength<=0" );

        destination = out;
        this.maxLineLength = maxLineLength;
        writer = new BufferedWriter( out );
    }

    public Writer getDestination()
    {
        return destination;
    }

    public void write( String text ) throws IOException
    {
        write( text, /*preserveSpace*/ false );
    }

    public void write( String text, boolean preserveSpace )
    {
        int length = text.length();

        try
        {
            for ( int i = 0; i < length; ++i )
            {
                char c = text.charAt( i );
                switch ( c )
                {
                    case ' ':
                        if ( preserveSpace )
                            word.append( c );
                        else
                            writeWord();
                        break;

                    case '\n':
                        writeWord();
                        writer.write( '\n' );
                        lineLength = 0;
                        break;

                    default:
                        word.append( c );
                }

            }
        }
        catch ( Exception e )
        {

        }
    }

    public void flush()
    {
        try
        {
            writeWord();
            writer.flush();
        }
        catch ( IOException e )
        {
        }
    }

    private void writeWord() throws IOException
    {
        int length = word.length();
        if ( length > 0 )
        {
            if ( lineLength > 0 )
            {
                if ( lineLength + 1 + length > maxLineLength )
                {
                    writer.write( '\n' );
                    lineLength = 0;
                }
                else
                {
                    writer.write( ' ' );
                    ++lineLength;
                }
            }

            writer.write( word.toString() );
            word.setLength( 0 );

            lineLength += length;
        }
    }
}
