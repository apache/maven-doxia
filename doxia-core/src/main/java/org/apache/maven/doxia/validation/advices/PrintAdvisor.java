/*
 *  Copyright 2005 Juan F. Codagnone <juam at users dot sourceforge dot net>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.maven.doxia.validation.advices;

import java.io.PrintStream;
import java.lang.reflect.Method;


/**
 * Advisor that prints the hierarchy of a model as is beeing generated
 *
 * @author Juan F. Codagnone
 * @since Nov 6, 2005
 */
public class PrintAdvisor extends AbstractSinkAdvice
{
    /**
     * indentation level
     */
    private String indent = "";
    /**
     * output stream
     */
    private PrintStream printStream;

    /**
     * indent increment
     */
    private static final String INDENT_STEP = "   ";
    /**
     * indent increment lenght
     */
    private static final int INDENT_STEP_LEN = INDENT_STEP.length();

    /**
     * Creates the PrintAdvisor.
     *
     * @param printStream stream printer
     */
    public PrintAdvisor( final PrintStream printStream )
    {
        if ( printStream == null )
        {
            throw new IllegalArgumentException( "argument cant be null" );
        }
        this.printStream = printStream;
    }

    /**
     * @see AbstractSinkAdvice#doLeaf(Method, Object[], Object)
     */
    public final void doLeaf( final Method method, final Object [] args,
                              final Object instance )
    {
        printStream.println( indent + method.getName() );
    }

    /**
     * @see AbstractSinkAdvice#doStart(Method, Object[], Object)
     */
    public final void doStart( final Method method, final Object [] args,
                               final Object instance )
    {
        printStream.println( indent + method.getName() );
        indent += INDENT_STEP;
    }

    /**
     * @see AbstractSinkAdvice#doEnd(Method, Object[], Object)
     */
    public final void doEnd( final Method method, final Object [] args,
                             final Object instance )
    {
        indent = indent.substring( INDENT_STEP_LEN );
        printStream.println( indent + method.getName() );
    }
}
