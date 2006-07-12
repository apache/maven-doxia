package org.apache.maven.doxia.book;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.apache.maven.doxia.book.model.BookModel;

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

        List files = FileUtils.getFiles( getTestFile( "src/test/resources/book-1" ), "**/*.apt, **/*.xml", "" );

        BookModel book = doxia.loadBook( book1 );

//        doxia.renderBook( "itext", files, getTestFile( "target/book-1-itext" ) );
//        doxia.renderBook( "xhtml", files, getTestFile( "target/book-1-xhtml" ) );
        doxia.renderBook( book, "xdoc", files, getTestFile( "target/book-1-xdoc" ) );
    }
}
