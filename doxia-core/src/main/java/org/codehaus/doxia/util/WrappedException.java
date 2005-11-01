/*
 * Copyright (c) 1999,2000-2001 Pixware. 
 *
 * Author: Hussein Shafie
 *
 * This file is part of the Pixware Java utilities.
 * For conditions of distribution and use, see the accompanying legal.txt file.
 */
package org.codehaus.doxia.util;

/**
 * Base class for exceptions which are wrappers around other exceptions. This
 * type of exception keep a record of the <em>original</em> exception, that
 * is, the one which was used to actually report a problem.
 */
public abstract class WrappedException extends Exception
{
    private Exception rootException;

    /**
     * Constructs an exception with no original exception and no detail
     * message.
     */
    public WrappedException()
    {
        this( null, null );
    }

    /**
     * Constructs an exception with a detail message but no original
     * exception.
     * 
     * @param message the detail message
     */
    public WrappedException( String message )
    {
        this( null, message );
    }

    /**
     * Constucts an exception with an original exception but no detail
     * message.
     * 
     * @param e the original exception
     */
    public WrappedException( Exception e )
    {
        this( e, null );
    }

    /**
     * Constucts an exception with both an original exception and an detail
     * message.
     *
     * @param e       the original exception
     * @param message the detail message; if null, the detail message of the
     *                original exception if any is used instead (it is this message which is
     *                returned by {@link java.lang.Throwable#getMessage})
     */
    public WrappedException( Exception e, String message )
    {
        super( ( message == null ) ? makeMessage( e ) : message );
        rootException = e;
    }

    /**
     * Returns the original exception.
     * 
     * @return the original exception if any or <code>null</code> otherwise
     */
    public Exception getRootException()
    {
        return rootException;
    }

    private static String makeMessage( Exception e )
    {
        Exception rootEx = findRootException( e );
        return ( rootEx == null ) ? null : rootEx.getMessage();
    }

    private static Exception findRootException( Exception e )
    {
        Exception rootEx = null;

        while ( e != null )
        {
            rootEx = e;
            if ( e instanceof WrappedException )
                e = ( (WrappedException) e ).getRootException();
            else
                break;
        }

        return rootEx;
    }
}
