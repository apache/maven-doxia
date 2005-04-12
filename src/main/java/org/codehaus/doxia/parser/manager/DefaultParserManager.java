package org.codehaus.doxia.parser.manager;

import org.codehaus.doxia.parser.Parser;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

import java.util.Map;

/**
 * @plexus.component
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: DefaultParserManager.java,v 1.5 2004/11/02 05:00:40 jvanzyl Exp $
 */
public class DefaultParserManager
    implements ParserManager, Contextualizable
{
    private PlexusContainer container;

    private Map parsers;

    public Parser getParser( String id )
        throws ParserNotFoundException
    {
        Parser parser = null;

        try
        {
            parser = (Parser) container.lookup( Parser.ROLE, id );
        }
        catch ( ComponentLookupException e )
        {
            throw new ParserNotFoundException( "Cannot find site module id = " + id );
        }

        return parser;
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }
}
