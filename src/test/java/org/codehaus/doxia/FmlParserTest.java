package org.codehaus.doxia;

import org.codehaus.doxia.module.fml.FmlParser;
import org.codehaus.doxia.parser.Parser;
import org.codehaus.doxia.parser.AbstractParserTestCase;
/**
 * @author <a href="mailto:evenisse@codehaus.org">Jason van Zyl</a>
 * @version $Id$
 */
public class FmlParserTest
    extends AbstractParserTestCase
{
    protected Parser getParser()
    {
        return new FmlParser();
    }

    protected String getDocument()
    {
        return "src/test/site/fml/faq.fml";
    }
}
