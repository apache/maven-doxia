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


/**
 * {@link org.aopalliance.aop.Advice} implementation usefull to do things with
 * {@link org.apache.maven.doxia.sink.Sink}s.
 *
 * @author Juan F. Codagnone
 * @since Nov 6, 2005
 */
public abstract class AbstractSinkAdvice implements MethodBeforeAdvice
{

    /**
     * @see #before(Method, Object[], Object)
     */
    public abstract void doLeaf( final Method method, final Object [] args,
                                 final Object instance );

    /**
     * @see #before(Method, Object[], Object)
     */
    public abstract void doStart( final Method method, final Object [] args,
                                  final Object instance );

    /**
     * @see #before(Method, Object[], Object)
     */
    public abstract void doEnd( final Method method, final Object [] args,
                                final Object instance );

    /**
     * @see org.aopalliance.aop.Advice
     */
    public final void before( final Method method, final Object [] args,
                              final Object instance ) throws Throwable
    {
        String name = method.getName();

        if ( hasClosing( method ) )
        {
            doStart( method, args, instance );
        }
        else if ( name.endsWith( "_" ) )
        {
            doEnd( method, args, instance );
        }
        else
        {
            doLeaf( method, args, instance );
        }

    }

    /**
     * @param m method to test
     * @return <code>true</code> if the method has a closing tag
     */
    private static boolean hasClosing( final Method m )
    {
        boolean ret = false;

        if ( m.getName().endsWith( "-" ) )
        {
            ret = false;
        }
        else
        {
            final Method[] methods = m.getDeclaringClass().getMethods();
            for ( int i = 0; !ret && i < methods.length; i++ )
            {
                ret = methods[i].getName().equals( m.getName() + "_" );

            }
        }

        return ret;
    }
}
