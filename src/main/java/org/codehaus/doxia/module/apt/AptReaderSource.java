/*
 * Copyright (c) 1999,2000 Pixware. 
 *
 * Author: Hussein Shafie
 *
 * This file is part of the Pixware APT tools.
 * For conditions of distribution and use, see the accompanying legal.txt file.
 */
package org.codehaus.doxia.module.apt;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

public class AptReaderSource implements AptSource
{
    private LineNumberReader reader;

    private int lineNumber;

    public AptReaderSource( Reader in )
    {
        reader = new LineNumberReader( in );

        lineNumber = -1;
    }

    public String getNextLine() throws AptParseException
    {
        if ( reader == null )
            return null;

        String line;

        try
        {
            line = reader.readLine();
            if ( line == null )
            {
                reader.close();
                reader = null;
            }
            else
            {
                lineNumber = reader.getLineNumber();
            }
        }
        catch ( IOException e )
        {
            throw new AptParseException( e );
        }

        return line;
    }

    public String getName()
    {
        return "";
    }

    public int getLineNumber()
    {
        return lineNumber;
    }

    public void close()
    {
        if ( reader != null )
        {
            try
            {
                reader.close();
            }
            catch ( IOException ignored )
            {
            }
        }
        reader = null;
    }
}
