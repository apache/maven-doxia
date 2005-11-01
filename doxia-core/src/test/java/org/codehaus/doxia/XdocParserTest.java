package org.codehaus.doxia;

import org.codehaus.doxia.module.xdoc.XdocParser;
import org.codehaus.doxia.parser.Parser;
import org.codehaus.doxia.parser.AbstractParserTestCase;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class XdocParserTest
    extends AbstractParserTestCase
{
    protected Parser getParser()
    {
        return new XdocParser();
    }

    protected String getDocument()
    {
        return "src/test/site/xdoc/report.xml";
    }
}
