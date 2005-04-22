package org.codehaus.doxia.parser.manager;

import org.codehaus.doxia.parser.Parser;

import java.util.Map;

/**
 * @plexus.component
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: DefaultParserManager.java,v 1.5 2004/11/02 05:00:40 jvanzyl Exp $
 */
public class DefaultParserManager
    implements ParserManager
{
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
