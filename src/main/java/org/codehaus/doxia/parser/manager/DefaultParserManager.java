package org.codehaus.doxia.parser.manager;

import org.codehaus.doxia.parser.Parser;

import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * 
 * @plexus.component
 *   role="org.codehaus.doxia.parser.manager.ParserManager"
 */
public class DefaultParserManager
    implements ParserManager
{
    /**
     * @plexus.requirement
     *   role="org.codehaus.doxia.parser.Parser"
     */
    private Map parsers;

    public Parser getParser( String id )
        throws ParserNotFoundException
    {
        Parser parser = (Parser) parsers.get( id );

        if ( parser == null )
        {
            throw new ParserNotFoundException( "Cannot find parser with id = " + id );
        }

        return parser;
    }
}
