package org.codehaus.doxia.sink.manager;

import org.codehaus.doxia.sink.Sink;

import java.util.Map;
import java.util.Collection;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: DefaultSinkManager.java,v 1.4 2004/11/02 05:00:40 jvanzyl Exp $
 * 
 * @plexus.component
 *   role="org.codehaus.doxia.sink.manager.SinkManager"
 */
public class DefaultSinkManager
    implements SinkManager
{
    /**
     * @plexus.requirement
     *   role="org.codehaus.doxia.sink.Sink"
     */
    private Map sinks;

    public Collection getSinks()
    {
        return sinks.values();
    }

    public Sink getSink( String id )
        throws SinkNotFoundException
    {
        Sink sink = (Sink) sinks.get( id );

        if ( sink == null )
        {
            throw new SinkNotFoundException( "Cannot find sink with id = " + id );
        }

        return sink;
    }
}
