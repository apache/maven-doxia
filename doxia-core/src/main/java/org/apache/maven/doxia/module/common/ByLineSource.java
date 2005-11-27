package org.apache.maven.doxia.module.common;

import org.apache.maven.doxia.parser.ParseException;

/**
 * The token are the new lines :)
 *
 * @author Juan F. Codagnone
 * @since Nov 4, 2005
 */
public interface ByLineSource
{
    /**
     * @return the next line. <code>null</code> if we reached the end.
     * @throws ParseException on I/O error
     */
    String getNextLine() throws ParseException;

    /**
     * @return the name of the input. could be the filename for example
     */
    String getName();

    /**
     * @return the current line number
     */
    int getLineNumber();

    /**
     * @throws IllegalStateException if the ungetLine/unget is called more than
     *                               one time without calling getNextLine()
     */
    void ungetLine() throws IllegalStateException;


    /**
     * @param s some text to push back to the parser
     * @throws IllegalStateException if the ungetLine/unget is called more than
     *                               one time without calling getNextLine()
     */
    void unget( String s ) throws IllegalStateException;


    /**
     * close the source ...
     */
    void close();
}
