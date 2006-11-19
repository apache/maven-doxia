package org.apache.maven.doxia.book;

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
 * @author Jason van Zyl
 */
public class BookDoxiaCli
    extends AbstractCli
{
    public static void main( String[] args )
        throws Exception
    {
        new BookDoxiaCli().execute( args );
    }

    public String getPomPropertiesPath()
    {
        return "META-INF/maven/org.apache.maven.doxia/doxia-book/pom.properties";
    }

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
