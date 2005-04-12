package org.codehaus.doxia.parser.manager;

import org.codehaus.doxia.parser.Parser;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: ParserManager.java,v 1.2 2004/09/15 17:14:29 jvanzyl Exp $
 */
public interface ParserManager
{
    String ROLE = ParserManager.class.getName();

    Parser getParser( String id )
        throws ParserNotFoundException;
}
