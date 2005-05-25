package org.codehaus.doxia.parser;

import org.codehaus.doxia.sink.Sink;

import java.io.Reader;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: Parser.java,v 1.4 2004/09/15 19:32:42 jvanzyl Exp $
 */
public interface Parser
{
    String ROLE = Parser.class.getName();

    int JUSTIFY_CENTER = 0;
    int JUSTIFY_LEFT = 1;
    int JUSTIFY_RIGHT = 2;

    void parse( Reader source, Sink sink )
        throws ParseException;
}
