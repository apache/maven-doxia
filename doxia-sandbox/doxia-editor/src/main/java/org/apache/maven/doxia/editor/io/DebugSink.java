package org.apache.maven.doxia.editor.io;

import org.apache.maven.doxia.sink.Sink;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DebugSink
    implements InvocationHandler
{
    public Object invoke( Object proxy, Method method, Object[] args )
        throws Throwable
    {
        System.out.println( "element: " + method.getName() );

        return null;
    }

    public static Sink newInstance()
    {
        return (Sink) Proxy.newProxyInstance( DebugSink.class.getClassLoader(), new Class[]{Sink.class}, new DebugSink() );
    }
}
