package org.apache.maven.doxia.util;

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

/**
 * Base class for exceptions which are wrappers around other exceptions. This
 * type of exception keep a record of the <em>original</em> exception, that
 * is, the one which was used to actually report a problem.
 */
public abstract class WrappedException
    extends Exception
{
    /** The <em>original</em> exception. */
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

    /**
     * Returns the message of the original exception.
     *
     * @param e The Exception.
     * @return the original exception message, or <code>null</code>
     * if the root exception is null.
     */
    private static String makeMessage( Exception e )
    {
        Exception rootEx = findRootException( e );
        return ( rootEx == null ) ? null : rootEx.getMessage();
    }

    /**
     * Returns the original exception.
     *
     * @param e The Exception.
     * @return the original exception, or <code>null</code> if there is none.
     */
    private static Exception findRootException( Exception e )
    {
        Exception rootEx = null;

        while ( e != null )
        {
            rootEx = e;
            if ( e instanceof WrappedException )
            {
                e = ( (WrappedException) e ).getRootException();
            }
            else
            {
                break;
            }
        }

        return rootEx;
    }
}
