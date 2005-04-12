package org.codehaus.doxia.parser;

import org.codehaus.doxia.sink.Sink;
import org.codehaus.doxia.macro.manager.MacroManager;

import java.io.Reader;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: Parser.java,v 1.4 2004/09/15 19:32:42 jvanzyl Exp $
 */
public interface Parser
{
    String ROLE = Parser.class.getName();

    void parse( Reader source, Sink sink )
        throws ParseException;
}
