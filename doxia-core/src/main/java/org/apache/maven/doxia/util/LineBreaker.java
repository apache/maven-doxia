package org.apache.maven.doxia.util;

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

import org.codehaus.plexus.util.IOUtil;

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
        {
            throw new IllegalArgumentException( "maxLineLength<=0" );
        }

        destination = out;
        this.maxLineLength = maxLineLength;
        writer = new BufferedWriter( out );
    }

    public Writer getDestination()
    {
        return destination;
    }

    public void write( String text )
        throws IOException
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
                        {
                            word.append( c );
                        }
                        else
                        {
                            writeWord();
                        }
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

    private void writeWord()
        throws IOException
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

    public void close()
    {
        IOUtil.close( writer );
    }
}
