package org.apache.maven.doxia.module.rtf;

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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * <a href="http://en.wikipedia.org/wiki/Portable_pixmap">PBM</a> images reader.
 *
 * @version $Id$
 */
class PBMReader
{
    static final int TYPE_PBM = 1;

    static final int TYPE_PGM = 2;

    static final int TYPE_PPM = 3;

    private static final String BAD_FILE_FORMAT = "bad file format";

    private static final String UNSUPPORTED_TYPE = "unsupported file type";

    private static final String UNSUPPORTED_FORMAT = "unsupported data format";

    private static final String UNSUPPORTED_DEPTH = "unsupported color depth";

    protected int type;

    protected boolean binary;

    protected int width;

    protected int height;

    protected int maxValue;

    private int bytesPerLine;

    private InputStream stream;

    PBMReader( String fileName )
        throws Exception
    {
        HeaderReader header = new HeaderReader();

        int length = header.read( fileName );

        if ( type != TYPE_PPM )
        {
            throw new Exception( UNSUPPORTED_TYPE );
        }

        if ( !binary )
        {
            throw new Exception( UNSUPPORTED_FORMAT );
        }

        if ( maxValue > 255 )
        {
            throw new Exception( UNSUPPORTED_DEPTH );
        }

        switch ( type )
        {
            case TYPE_PBM:
                bytesPerLine = ( width + 7 ) / 8;
                break;
            case TYPE_PGM:
                bytesPerLine = width;
                break;
            case TYPE_PPM:
                bytesPerLine = 3 * width;
                break;
            default:
        }

        stream = new BufferedInputStream( new FileInputStream( fileName ) );

        skip( length );
    }

    int type()
    {
        return type;
    }

    int width()
    {
        return width;
    }

    int height()
    {
        return height;
    }

    int maxValue()
    {
        return maxValue;
    }

    int bytesPerLine()
    {
        return bytesPerLine;
    }

    long skip( long count )
        throws IOException
    {
        long skipped = stream.skip( count );

        if ( skipped < count )
        {
            byte[] b = new byte[512];
            while ( skipped < count )
            {
                int len = (int) Math.min( b.length, ( count - skipped ) );
                int n = stream.read( b, 0, len );
                if ( n < 0 )
                {
                    break; // end of file
                }
                skipped += n;
            }
        }

        return skipped;
    }

    int read( byte[] b, int off, int len )
        throws IOException
    {
        int count = 0;
        while ( count < len )
        {
            int n = stream.read( b, off + count, len - count );
            if ( n < 0 )
            {
                break; // end of file
            }
            count += n;
        }
        return count;
    }

    // -----------------------------------------------------------------------

    class HeaderReader
    {

        private Reader reader;

        private int offset;

        int read( String fileName )
            throws Exception
        {
            String field;

            reader = new BufferedReader( new InputStreamReader(
                    new FileInputStream( fileName ), StandardCharsets.US_ASCII ) );
            offset = 0;

            field = getField();
            if ( field.length() != 2 || field.charAt( 0 ) != 'P' )
            {
                reader.close();
                throw new Exception( BAD_FILE_FORMAT );
            }
            switch ( field.charAt( 1 ) )
            {
                case '1':
                case '4':
                    type = TYPE_PBM;
                    break;
                case '2':
                case '5':
                    type = TYPE_PGM;
                    break;
                case '3':
                case '6':
                    type = TYPE_PPM;
                    break;
                default:
                    reader.close();
                    throw new Exception( BAD_FILE_FORMAT );
            }
            if ( field.charAt( 1 ) > '3' )
            {
                binary = true;
            }
            else
            {
                binary = false;
            }

            try
            {
                width = Integer.parseInt( getField() );
                height = Integer.parseInt( getField() );
                if ( type == TYPE_PBM )
                {
                    maxValue = 1;
                }
                else
                {
                    maxValue = Integer.parseInt( getField() );
                }
            }
            catch ( NumberFormatException e )
            {
                reader.close();
                throw new Exception( BAD_FILE_FORMAT );
            }

            reader.close();

            return offset;
        }

        private String getField()
            throws IOException
        {
            char c;
            StringBuilder field = new StringBuilder();

            try
            {
                do
                {
                    while ( ( c = getChar() ) == '#' )
                    {
                        skipComment();
                    }
                }
                while ( Character.isWhitespace( c ) );

                field.append( c );

                while ( !Character.isWhitespace( c = getChar() ) )
                {
                    if ( c == '#' )
                    {
                        skipComment();
                        break;
                    }
                    field.append( c );
                }
            }
            catch ( EOFException ignore )
            {
                // nop
            }

            return field.toString();
        }

        private char getChar()
            throws IOException, EOFException
        {
            int c = reader.read();
            if ( c < 0 )
            {
                throw new EOFException();
            }
            offset += 1;
            return (char) c;
        }

        private void skipComment()
            throws IOException
        {
            try
            {
                while ( getChar() != '\n' )
                {
                    // nop
                }
            }
            catch ( EOFException ignore )
            {
                // nop
            }
        }
    }
}
