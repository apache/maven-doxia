package org.apache.maven.doxia.book.services.renderer;

import org.apache.maven.doxia.Doxia;
import org.apache.maven.doxia.book.context.BookContext;
import org.apache.maven.doxia.book.BookDoxiaException;
import org.apache.maven.doxia.book.model.BookModel;
import org.apache.maven.doxia.book.model.Chapter;
import org.apache.maven.doxia.book.model.Section;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

/**
 * @plexus.component role-hint="xhtml"
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class XHtmlBookRenderer
    extends AbstractLogEnabled
    implements BookRenderer
{
    /**
     * @plexus.requirement
     */
    private Doxia doxia;

    // ----------------------------------------------------------------------
    // BookRenderer Implementation
    // ----------------------------------------------------------------------

    public void renderBook( BookContext context )
        throws BookDoxiaException
    {
        BookModel book = context.getBook();

        if ( !context.getOutputDirectory().exists() )
        {
            if ( !context.getOutputDirectory().mkdirs() )
            {
                throw new BookDoxiaException( "Could not make directory: " + context.getOutputDirectory().getAbsolutePath() + "." );
            }
        }

        File bookFile = new File( context.getOutputDirectory(), book.getId() + ".xhtml" );

        FileWriter fileWriter;

        try
        {
            fileWriter = new FileWriter( bookFile );
        }
        catch ( IOException e )
        {
            throw new BookDoxiaException( "Error while opening file.", e );
        }

        PrettyPrintXMLWriter writer = new PrettyPrintXMLWriter( fileWriter );
        writer.writeText( "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"DTD/xhtml1-transitional.dtd\">" );
        writer.startElement( "html" );
        writer.addAttribute( "xmlns", "http://www.w3.org/1999/xhtml");
        writer.startElement( "body" );

        for ( Iterator it = book.getChapters().iterator(); it.hasNext(); )
        {
            Chapter chapter = (Chapter) it.next();

            renderChapter( writer, chapter, context );
        }

        writer.endElement(); // body
        writer.endElement(); // html

        try
        {
            fileWriter.close();
        }
        catch ( IOException e )
        {
            throw new BookDoxiaException( "Error while closing file.", e );
        }
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    private void renderChapter( PrettyPrintXMLWriter writer, Chapter chapter, BookContext context )
        throws BookDoxiaException
    {
        writer.startElement( "chapter" );

        for ( Iterator it = chapter.getSections().iterator(); it.hasNext(); )
        {
            Section section = (Section) it.next();

            renderSection( writer, section, context );
        }

        writer.endElement();
    }

    private void renderSection( PrettyPrintXMLWriter writer, Section section, BookContext context )
        throws BookDoxiaException
    {
        writer.startElement( "section" );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        BookContext.BookFile bookFile = (BookContext.BookFile) context.getFiles().get( section.getId() );

        if ( bookFile == null )
        {
            throw new BookDoxiaException( "No document that matches section with id=" + section.getId() + "." );
        }

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

//        Sink sink = new XhtmlSink( writer, new RenderingContext( context.getOutputDirectory(), bookFile.getFile().getAbsolutePath() ) );

//        try
//        {
//            doxia.parse( new FileReader( bookFile.getFile() ), bookFile.getParserId(), sink );
//        }
//        catch ( ParserNotFoundException e )
//        {
//            throw new BookDoxiaException( "Parser not found: " + bookFile.getParserId() + ".", e );
//        }
//        catch ( ParseException e )
//        {
//            throw new BookDoxiaException( "Error while parsing document: " + bookFile.getFile().getAbsolutePath() + ".", e );
//        }
//        catch ( FileNotFoundException e )
//        {
//            throw new BookDoxiaException( "Could not find document: " + bookFile.getFile().getAbsolutePath() + ".", e );
//        }

        writer.endElement();
    }
}
