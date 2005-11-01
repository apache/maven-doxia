package org.codehaus.doxia.module.docbook;

import org.codehaus.doxia.parser.AbstractParserTestCase;
import org.codehaus.doxia.parser.Parser;

/**
 * @author <a href="mailto:lars@trieloff.net">Lars Trieloff</a>
 * @version $Id$
 */
public class DocBookParserTest
    extends AbstractParserTestCase
{
    protected Parser getParser()
    {
        return new DocBookParser();
    }

    protected String getDocument()
    {
        return "src/test/site/docbook/guide.xml";
    }
}
