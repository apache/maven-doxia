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

import java.lang.reflect.Method;
import java.util.Stack;


/**
 * Advisor that checks for incorrect closing
 *
 * @author Juan F. Codagnone
 * @since Nov 6, 2005
 */
public class HangingElementAdvice extends AbstractSinkAdvice
{
    /**
     * stack used to keep state
     */
    private Stack/*<String>*/ stack = new Stack/*<String>*/();

    /**
     * @see AbstractSinkAdvice#doLeaf(Method, Object[], Object)
     */
    public final void doLeaf( final Method method, final Object [] args,
                              final Object instance )
    {
        // nothing to do
    }

    /**
     * @see AbstractSinkAdvice#doStart(Method, Object[], Object)
     */
    public final void doStart( final Method method, final Object [] args,
                               final Object instance )
    {
        stack.push( method.getName() );
    }

    /**
     * @see AbstractSinkAdvice#doEnd(Method, Object[], Object)
     */
    public void doEnd( final Method method, final Object [] args,
                       final Object instance )
    {
        if ( stack.size() == 0 )
        {
            throw new IllegalStateException(
                "there are more closings that openings" );
        }
        String name = (String) stack.pop();
        if ( !( name + "_" ).equals( method.getName() ) )
        {
            throw new IllegalStateException( "we are closing: "
                + name + " with " + method.getName() );
        }
    }
}
