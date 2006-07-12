package org.apache.maven.doxia.book.services.renderer;

import org.apache.maven.doxia.book.context.BookContext;
import org.apache.maven.doxia.book.BookDoxiaException;
import org.apache.maven.doxia.book.model.BookModel;
import org.apache.maven.doxia.book.model.Chapter;
import org.apache.maven.doxia.book.model.Section;
import org.apache.maven.doxia.module.itext.ITextSink;
import org.apache.maven.doxia.module.itext.ITextUtil;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.Doxia;
import org.apache.maven.doxia.editor.io.PipelineSink;
import org.apache.maven.doxia.parser.manager.ParserNotFoundException;
import org.apache.maven.doxia.parser.ParseException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.text.DateFormat;

/**
 * @plexus.component role-hint="itext"
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ITextBookRenderer
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

        File bookFile = new File( context.getOutputDirectory(), book.getId() + ".xml" );

        FileWriter fileWriter;

        try
        {
            fileWriter = new FileWriter( bookFile );
        }
        catch ( IOException e )
        {
            throw new BookDoxiaException( "Error while opening file.", e );
        }

        // ----------------------------------------------------------------------
        // Create the XML File
        // ----------------------------------------------------------------------

        PrettyPrintXMLWriter writer = new PrettyPrintXMLWriter( fileWriter, "UTF-8", null);
        writer.startElement( "itext" );
        writer.addAttribute( "creationdate", DateFormat.getDateTimeInstance().format( new Date() ) );
        writer.addAttribute( "producer", "Doxia iText" );

//        writer.startElement( "paragraph" );
//        writer.addAttribute( "leading", "18.0" );
//        writer.addAttribute( "font", "unknown" );
//        writer.addAttribute( "align", "Default" );
//        writer.writeText( "Please visit my" + System.getProperty( "line.separator" ) );
//
//        writer.startElement( "anchor" );
//        writer.addAttribute( "leading", "18.0" );
//        writer.addAttribute( "font", "Helvetica" );
//        writer.addAttribute( "size", "12.0" );
//        writer.addAttribute( "fontstyle", "normal, underline" );
//        writer.addAttribute( "red", "0" );
//        writer.addAttribute( "green", "0" );
//        writer.addAttribute( "blue", "255" );
//        writer.addAttribute( "name", "top" );
//        writer.addAttribute( "reference", "http://www.lowagie.com/iText/" );
//
//        writer.startElement( "chunk" );
//        writer.addAttribute( "font", "Helvetica" );
//        writer.addAttribute( "size", "12.0" );
//        writer.addAttribute( "fontstyle", "normal, underline" );
//        writer.addAttribute( "red", "0" );
//        writer.addAttribute( "green", "0" );
//        writer.addAttribute( "blue", "255" );
//        writer.writeText( "website (external reference)" );
//        writer.endElement();
//
//        writer.endElement(); // anchor
//
//        writer.endElement(); // paragraph

        // TODO: Write out TOC

        for ( Iterator it = book.getChapters().iterator(); it.hasNext(); )
        {
            Chapter chapter = (Chapter) it.next();

            renderChapter( writer, chapter, context );
        }

        writer.endElement(); // itext

        try
        {
            fileWriter.close();
        }
        catch ( IOException e )
        {
            throw new BookDoxiaException( "Error while closing file.", e );
        }

        // ----------------------------------------------------------------------
        // Render the XML to PDF
        // ----------------------------------------------------------------------

        File pdfFile = new File( context.getOutputDirectory(), book.getId() + ".pdf" );

        try
        {
            ITextUtil.writePdf( new FileInputStream( bookFile ), new FileOutputStream( pdfFile ) );
        }
        catch ( FileNotFoundException e )
        {
            throw new BookDoxiaException( "Error while rendering file to PDF", e );
        }
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    private void renderChapter( PrettyPrintXMLWriter writer, Chapter chapter, BookContext context )
        throws BookDoxiaException
    {
        writer.startElement( "chapter" );
        writer.addAttribute( "numberdepth", "1" );
        writer.addAttribute( "depth", "1" );
        writer.addAttribute( "indent", "1" );

        startTitle( writer, "36.0", "Helvetica", "24.0", "normal", "255", "0", "0");
        chunk( writer, chapter.getTitle(), "Helvetica", "24.0", "normal","255", "0", "0" );
        writer.endElement(); // title

//        writer.startElement( "sectioncontent" );
        for ( Iterator it = chapter.getSections().iterator(); it.hasNext(); )
        {
            Section section = (Section) it.next();

            renderSection( writer, section, context );
        }
//        writer.endElement(); // sectioncontent

        writer.endElement(); // chapter
    }

    private void renderSection( PrettyPrintXMLWriter writer, Section section, BookContext context )
        throws BookDoxiaException
    {
//        writer.startElement( "section" );

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

        Sink itextSink = new ITextSink( writer );

        List pipeline = new ArrayList();
//        pipeline.add( DebugSink.newInstance() );
        pipeline.add( itextSink );
        Sink sink = PipelineSink.newInstance( pipeline );

        try
        {
            doxia.parse( new FileReader( bookFile.getFile() ), bookFile.getParserId(), sink );
        }
        catch ( ParserNotFoundException e )
        {
            throw new BookDoxiaException( "Parser not found: " + bookFile.getParserId() + ".", e );
        }
        catch ( ParseException e )
        {
            throw new BookDoxiaException( "Error while parsing document: " + bookFile.getFile().getAbsolutePath() + ".", e );
        }
        catch ( FileNotFoundException e )
        {
            throw new BookDoxiaException( "Could not find document: " + bookFile.getFile().getAbsolutePath() + ".", e );
        }

//        writer.endElement(); // section
    }

    private void startTitle( PrettyPrintXMLWriter writer, String leading, String font, String size, String fontstyle,
                             String red, String green, String blue )
    {
        writer.startElement( "title" );
        writer.addAttribute( "leading", leading );
        writer.addAttribute( "font", font );
        writer.addAttribute( "size", size );
        writer.addAttribute( "fontstyle", fontstyle );
        writer.addAttribute( "red", red );
        writer.addAttribute( "green", green );
        writer.addAttribute( "blue", blue );
    }

    private void chunk( PrettyPrintXMLWriter writer, String title, String font, String size, String fontstyle,
                        String red, String green, String blue )
    {
        writer.startElement( "chunk" );
        writer.addAttribute( "font", font );
        writer.addAttribute( "size", size );
        writer.addAttribute( "fontstyle", fontstyle );
        writer.addAttribute( "red", red );
        writer.addAttribute( "green", green );
        writer.addAttribute( "blue", blue );
        if ( StringUtils.isNotEmpty( title ) )
        {
            writer.writeText( title );
        }
        else
        {
            writer.writeText( "<Missing title>" );
        }
        writer.endElement(); // chunk
    }
}
