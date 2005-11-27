package org.apache.maven.doxia.module.confluence;

import org.apache.maven.doxia.module.common.ByLineReaderSource;
import org.apache.maven.doxia.module.common.ByLineSource;
import org.apache.maven.doxia.module.confluence.parser.Block;
import org.apache.maven.doxia.module.confluence.parser.BlockParser;
import org.apache.maven.doxia.module.confluence.parser.SectionBlockParser;
import org.apache.maven.doxia.module.confluence.parser.ParagraphBlockParser;
import org.apache.maven.doxia.module.confluence.parser.VerbatimBlockParser;
import org.apache.maven.doxia.module.confluence.parser.HorizontalRuleBlockParser;
import org.apache.maven.doxia.parser.AbstractParser;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @plexus.component role="org.apache.maven.doxia.parser.Parser"
 * role-hint="confluence"
 */
public class ConfluenceParser
    extends AbstractParser
{
    private BlockParser[] parsers;

    public ConfluenceParser()
    {
        BlockParser headingParser = new SectionBlockParser();
        BlockParser verbatimParser = new VerbatimBlockParser();
        BlockParser horizontalRuleParser = new HorizontalRuleBlockParser();
        BlockParser paragraphParser = new ParagraphBlockParser();

        parsers = new BlockParser[]
            {
                headingParser,
                verbatimParser,
                horizontalRuleParser,
                paragraphParser
            };
    }

    public List parse( ByLineSource source )
        throws ParseException
    {
        List blocks = new ArrayList();

        String line;

        while ( ( line = source.getNextLine() ) != null )
        {
            boolean accepted = false;

            for ( int i = 0; i < parsers.length; i++ )
            {
                BlockParser parser = parsers[i];

                if ( line.trim().length() == 0 )
                {
                    continue;
                }

                if ( parser.accept( line ) )
                {
                    System.out.println( "------------------------------------------------------------" );
                    System.out.println( "line = " + line );
                    System.out.println( "line accepted by: " + parser );
                    System.out.println( "------------------------------------------------------------" );

                    accepted = true;

                    blocks.add( parser.visit( line, source ) );

                    break;
                }
            }

            /*
            if ( !accepted )
            {
                throw new ParseException( "don't  know how to handle line: " + source.getLineNumber() + ": " + line );
            }
            */
        }

        return blocks;
    }

    public final synchronized void parse( Reader reader, Sink sink )
        throws ParseException
    {
        List blocks;

        ByLineSource source = new ByLineReaderSource( reader );

        try
        {
            blocks = parse( source );
        }
        catch ( final ParseException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            throw new ParseException( e, source.getName(), source.getLineNumber() );
        }

        sink.head();

        sink.head_();

        sink.body();

        for ( Iterator i = blocks.iterator(); i.hasNext(); )
        {
            Block block = (Block) i.next();

            block.traverse( sink );
        }

        sink.body_();
    }
}
