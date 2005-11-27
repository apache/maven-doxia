/*
 * Originaly from org.apache.doxia.module.apt.AptReaderSource. It was modified
 * to get unget support 
 */
package org.apache.maven.doxia.module.common;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

import org.apache.maven.doxia.module.apt.AptParseException;

/**
 * {@link org.apache.maven.doxia.module.common.ByLineSource} default implementation
 */
public class ByLineReaderSource implements ByLineSource
{
    /**
     * reader
     */
    private LineNumberReader reader;
    /**
     * current line number
     */
    private int lineNumber;

    /**
     * holds the last line returned by getNextLine()
     */
    private String lastLine;

    /**
     * <code>true</code> if ungetLine() was called and no getNextLine() was
     * called
     */
    private boolean ungetted = false;

    /**
     * Creates the ByLineReaderSource.
     *
     * @param in real source :)
     */
    public ByLineReaderSource( final Reader in )
    {
        reader = new LineNumberReader( in );

        lineNumber = -1;
    }

    /**
     * @see ByLineSource#getNextLine()
     */
    public final String getNextLine() throws AptParseException
    {
        if ( reader == null )
        {
            return null;
        }

        if ( ungetted )
        {
            ungetted = false;
            return lastLine;
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
            throw new AptParseException( e );
        }

        lastLine = line;

        return line;
    }

    /**
     * @see ByLineSource#getName()
     */
    public final String getName()
    {
        return "";
    }

    /**
     * @see ByLineSource#getLineNumber()
     */
    public final int getLineNumber()
    {
        return lineNumber;
    }

    /**
     * @see ByLineSource#close()
     */
    public final void close()
    {
        if ( reader != null )
        {
            try
            {
                reader.close();
            }
            catch ( IOException ignored )
            {
                // ignore
            }
        }
        reader = null;
    }

    /**
     * @see ByLineSource#ungetLine()
     */
    public final void ungetLine() throws IllegalStateException
    {
        if ( ungetted )
        {
            throw new IllegalStateException(
                "we support only one level of ungetLine()" );
        }
        ungetted = true;
    }

    /**
     * @see ByLineSource#unget(String)
     */
    public final void unget( final String s ) throws IllegalStateException
    {
        if ( s == null )
        {
            throw new IllegalArgumentException( "argument can't be null" );
        }
        if ( s.length() == 0 )
        {
            // dont do anything
        }
        else
        {
            ungetLine();
            lastLine = s;
        }
    }
}