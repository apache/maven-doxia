package org.apache.maven.doxia.book;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class BookRendererTest
    extends PlexusTestCase
{
    public void testBasic()
        throws Exception
    {
        BookDoxia doxia = (BookDoxia) lookup( BookDoxia.ROLE );

        File book1 = getTestFile( "src/test/resources/book-1.xml" );

        File itextOutput = getTestFile( "target/book-1-itext" );
        File xhtmlOutput = getTestFile( "target/book-1-xhtml" );

        List files = FileUtils.getFiles( getTestFile( "src/test/resources/book-1" ), "**/*.apt, **/*.xml", "" );

        doxia.renderBook( book1, "itext", files, itextOutput );
//        doxia.renderBook( book1, "xhtml", files, xhtmlOutput );
    }
}
