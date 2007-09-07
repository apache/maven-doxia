package org.apache.maven.doxia.book;

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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.maven.doxia.book.model.BookModel;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.tools.cli.AbstractCli;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.util.List;

/**
 * Invoke BookDoxia from the command line.
 *
 * @author Jason van Zyl
 */
public class BookDoxiaCli
    extends AbstractCli
{
    /**
     * Execute the BookDoxia command line interface.
     *
     * @param args command line options.
     * @throws Exception if something goes wrong.
     */
    public static void main( String[] args )
        throws Exception
    {
        new BookDoxiaCli().execute( args );
    }

    /** {@inheritDoc} */
    public String getPomPropertiesPath()
    {
        return "META-INF/maven/org.apache.maven.doxia/doxia-book/pom.properties";
    }

    /** {@inheritDoc} */
    public Options buildCliOptions( Options options )
    {
        options.addOption( OptionBuilder.withLongOpt( "book-xml" ).hasArg().withDescription(
            "book xml file." )
            .create( 'b' ) );

        options.addOption( OptionBuilder.withLongOpt( "content" ).hasArg().withDescription(
            "book content" )
            .create( 'c' ) );

        options.addOption( OptionBuilder.withLongOpt( "output" ).hasArg().withDescription(
            "output directory" )
            .create( 'o' ) );

        return options;
    }

    /** {@inheritDoc} */
    public void invokePlexusComponent( CommandLine cli,
                                       PlexusContainer plexus )
        throws Exception
    {
        BookDoxia doxia = (BookDoxia) plexus.lookup( BookDoxia.ROLE );

        String bookXml = cli.getOptionValue( 'b' );

        String content = cli.getOptionValue( 'c' );

        String output = cli.getOptionValue( 'o' );

        File book1 = new File( bookXml );

        List files = FileUtils.getFiles( new File( content ), "**/*.apt, **/*.xml", "" );

        BookModel book = doxia.loadBook( book1 );

        doxia.renderBook( book, "xdoc", files, new File( output ) );
    }

}
