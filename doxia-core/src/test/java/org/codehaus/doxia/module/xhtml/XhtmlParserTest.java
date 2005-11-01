package org.codehaus.doxia.module.xhtml;

import java.io.FileReader;
import java.io.Reader;

import org.codehaus.doxia.WellformednessCheckingSink;
import org.codehaus.doxia.parser.AbstractParserTestCase;
import org.codehaus.doxia.parser.Parser;

/**
 * @author <a href="mailto:lars@trieloff.net">Lars Trieloff</a>
 * @version $Id: SinkDescriptorReaderTest.java 131 2005-10-25 05:16:26Z trygvis $
 */
public class XhtmlParserTest
    extends AbstractParserTestCase
{
    protected Parser getParser()
    {
        return new XhtmlParser();
    }

    protected String getDocument()
    {
        return "src/test/site/xhtml/fun.html";
    }
    
    public void testParser()
        throws Exception
    {
        //use the new wellformedness checking sink.
        WellformednessCheckingSink sink = new WellformednessCheckingSink();
    
        Reader reader = new FileReader( getTestFile( getBasedir(), getDocument() ) );
    
        getParser().parse( reader, sink );
        
        assertTrue("Input not wellformed, offending element: " + sink.getOffender() , sink.isWellformed());
    }
}
