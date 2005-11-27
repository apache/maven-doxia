/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.maven.doxia.module.confluence;

import org.apache.maven.doxia.module.apt.AptSource;
import org.apache.maven.doxia.module.apt.AptParseException;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

public class ConfluenceReaderSource
    implements ConfluenceSource
{
    private LineNumberReader reader;

    private int lineNumber;

    public ConfluenceReaderSource( Reader in )
    {
        reader = new LineNumberReader( in );

        lineNumber = -1;
    }

    public String getNextLine()
        throws ConfluenceParseException
    {
        if ( reader == null )
        {
            return null;
        }

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
            throw new ConfluenceParseException( e );
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
