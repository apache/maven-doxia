package org.apache.maven.doxia.book.services.renderer;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.doxia.book.BookDoxiaException;
import org.apache.maven.doxia.book.services.renderer.latex.LatexBookSink;
import org.apache.maven.doxia.book.context.BookContext;
import org.apache.maven.doxia.book.model.BookModel;
import org.apache.maven.doxia.book.model.Chapter;
import org.apache.maven.doxia.book.model.Section;
import org.apache.maven.doxia.parser.manager.ParserNotFoundException;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.Doxia;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.WriterFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.HashMap;

/**
 * <p>LatexBookRenderer class.</p>
 *
 * @plexus.component role-hint="latex"
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class LatexBookRenderer
    implements BookRenderer
{
    /**
     * @plexus.requirement
     */
    private Doxia doxia;

    // ----------------------------------------------------------------------
    // BookRenderer Implementatino
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void renderBook( BookContext context )
        throws BookDoxiaException
    {
        BookModel book = context.getBook();

        if ( !context.getOutputDirectory().exists() )
        {
            if ( !context.getOutputDirectory().mkdirs() )
            {
                throw new BookDoxiaException(
                    "Could not make directory: " + context.getOutputDirectory().getAbsolutePath() + "." );
            }
        }

        File bookFile = new File( context.getOutputDirectory(), book.getId() + ".tex" );

        FileWriter fileWriter = null;

        try
        {
            fileWriter = new FileWriter( bookFile );

            PrintWriter writer = new PrintWriter( fileWriter );

            writeBook( book, context, writer );
        }
        catch ( IOException e )
        {
            throw new BookDoxiaException( "Error while opening file.", e );
        }
        finally
        {
            IOUtil.close( fileWriter );
        }
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    /** SectionInfo: id and title. */
    static class SectionInfo
    {
        /** id. */
        String id;

        /** title. */
        String title;
    }

    /**
     * Write a book.
     *
     * @param book the BookModel to write.
     * @param context the BookContext.
     * @param writer the writer to use.
     * @throws IOException if any.
     * @throws BookDoxiaException if the section cannot be written.
     */
    private void writeBook( BookModel book, BookContext context, PrintWriter writer )
        throws IOException, BookDoxiaException
    {
        // ----------------------------------------------------------------------
        // Process all the section documents and collect their names
        // ----------------------------------------------------------------------

        Map<String, SectionInfo> sectionInfos = new HashMap<String, SectionInfo>();

        for ( Chapter chapter : book.getChapters() )
        {
            for ( Section section : chapter.getSections() )
            {
                SectionInfo info = writeSection( section, context );

                sectionInfos.put( info.id, info );
            }
        }

        // ----------------------------------------------------------------------
        // Write the main .tex file
        // ----------------------------------------------------------------------

        writer.println( "\\documentclass{book}" );
        writer.println( "\\title{" + book.getTitle() + "}" );

        if ( StringUtils.isNotEmpty( book.getAuthor() ) )
        {
            writer.println( "\\author{" + book.getAuthor() + "}" );
        }

        if ( StringUtils.isNotEmpty( book.getDate() ) )
        {
            writer.println( "\\date{" + book.getDate() + "}" );
        }

        LatexBookSink sink = new LatexBookSink( writer );
        sink.defaultBookPreamble();

        writer.println( "\\begin{document}" );
        writer.println( "\\maketitle" );
        writer.println( "\\tableofcontents" );
//        writer.println( "\\listoffigures" );

        for ( Chapter chapter : book.getChapters() )
        {
            writer.println( "\\chapter{" + chapter.getTitle() + "}" );

            for ( Section section : chapter.getSections() )
            {
                SectionInfo info = sectionInfos.get( section.getId() );

                writer.println( "\\input{" + info.id + "}" );
            }
        }

        writer.println( "\\end{document}" );
    }

    /**
     * Write a section.
     *
     * @param section the Section to write.
     * @param context the BookContext.
     * @return SectionInfo
     * @throws IOException if any.
     * @throws BookDoxiaException if the section cannot be written.
     */
    private SectionInfo writeSection( Section section, BookContext context )
        throws IOException, BookDoxiaException
    {
        File file = new File( context.getOutputDirectory(), ( section.getId() + ".tex" ) );

        Writer writer = WriterFactory.newWriter( file, context.getOutputEncoding() );

        LatexBookSink sink = new LatexBookSink( writer );

        BookContext.BookFile bookFile = (BookContext.BookFile) context.getFiles().get( section.getId() );

        if ( bookFile == null )
        {
            throw new BookDoxiaException( "No document that matches section with id="
                        + section.getId() + "." );
        }

        Reader reader = null;
        try
        {
            reader = ReaderFactory.newReader( bookFile.getFile(), context.getInputEncoding() );
            doxia.parse( reader, bookFile.getParserId(), sink );
        }
        catch ( ParserNotFoundException e )
        {
            throw new BookDoxiaException( "Parser not found: "
                        + bookFile.getParserId() + ".", e );
        }
        catch ( ParseException e )
        {
            throw new BookDoxiaException( "Error while parsing document: "
                        + bookFile.getFile().getAbsolutePath() + ".", e );
        }
        catch ( FileNotFoundException e )
        {
            throw new BookDoxiaException( "Could not find document: "
                        + bookFile.getFile().getAbsolutePath() + ".", e );
        }
        finally
        {
            sink.flush();
            sink.close();

            IOUtil.close( reader );
            IOUtil.close( writer );
        }

        SectionInfo info = new SectionInfo();
        info.id = section.getId();
        info.title = sink.getTitle();

        return info;
    }
}
