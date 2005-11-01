package org.codehaus.doxia.parser.manager;

import org.codehaus.doxia.parser.Parser;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface ParserManager
{
    String ROLE = ParserManager.class.getName();

    Parser getParser( String id )
        throws ParserNotFoundException;
}
