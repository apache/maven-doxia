package org.apache.maven.doxia.editor.io;

import org.apache.maven.doxia.sink.Sink;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PipelineSink
    implements InvocationHandler
{
    private List pipeline;

    public PipelineSink( List pipeline )
    {
        this.pipeline = pipeline;
    }

    public void addSink( Sink sink )
    {
        pipeline.add( sink );
    }

    public Object invoke( Object proxy, Method method, Object[] args )
        throws Throwable
    {
        for ( Iterator it = pipeline.iterator(); it.hasNext(); )
        {
            Sink sink = (Sink) it.next();

            method.invoke( sink, args );
        }

        return null;
    }

    public static Sink newInstance( List pipeline )
    {
        return (Sink) Proxy.newProxyInstance( PipelineSink.class.getClassLoader(),
                                              new Class[]{Sink.class},
                                              new PipelineSink( pipeline ) );
    }
}
