package org.apache.maven.doxia.book.services.indexer;

import org.apache.maven.doxia.sink.SinkAdapter;
import org.apache.maven.doxia.book.context.BookContext;
import org.apache.maven.doxia.book.context.TOCEntry;
import org.apache.maven.doxia.book.BookDoxiaException;

import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class BookIndexingSink
    extends SinkAdapter
{
    private final static int TYPE_SECTION_1 = 1;
    private final static int TYPE_SECTION_2 = 2;
    private final static int TYPE_SECTION_3 = 3;
    private final static int TYPE_SECTION_4 = 4;
    private final static int TYPE_SECTION_5 = 5;
    private final static int TYPE_DEFINED_TERM = 6;
    private final static int TYPE_FIGURE = 7;
    private final static int TYPE_TABLE = 8;

    private int type;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private BookContext bookContext;

    public BookIndexingSink( BookContext bookContext )
    {
        this.bookContext = bookContext;
    }

    // ----------------------------------------------------------------------
    // Sink Overrides
    // ----------------------------------------------------------------------

    public void sectionTitle1_()
    {
        type = TYPE_SECTION_1;
    }

    public void sectionTitle2_()
    {
        type = TYPE_SECTION_2;
    }

    public void sectionTitle3_()
    {
        type = TYPE_SECTION_3;
    }

    public void sectionTitle4_()
    {
        type = TYPE_SECTION_4;
    }

    public void sectionTitle5_()
    {
        type = TYPE_SECTION_5;
    }

    public void definedTerm_()
    {
        type = TYPE_DEFINED_TERM;
    }

    public void figureCaption_()
    {
        type = TYPE_FIGURE;
    }

    public void tableCaption_()
    {
        type = TYPE_TABLE;
    }

    public void text( String text )
    {
        if ( bookContext == null )
        {
            throw new RuntimeException( new BookDoxiaException( "The book context has to be set first." ) );
        }

        TOCEntry entry = new TOCEntry();
        entry.setTitle( text );

        switch( type )
        {
            case TYPE_SECTION_1: addSection( 0, entry ); break;
            case TYPE_SECTION_2: addSection( 1, entry ); break;
            case TYPE_SECTION_3: addSection( 2, entry ); break;
            case TYPE_SECTION_4: addSection( 3, entry ); break;
            case TYPE_SECTION_5: addSection( 4, entry ); break;
            case TYPE_DEFINED_TERM:
            case TYPE_FIGURE:
            case TYPE_TABLE:
        }
    }

    private void addSection( int depth, TOCEntry entry )
    {
        TOCEntry parent = bookContext.getRootTOCEntry();

        for ( int i = 0; i < depth; i++ )
        {
            List entries = parent.getChildEntries();

            parent = (TOCEntry) entries.get( entries.size() - 1 );
        }

        parent.getChildEntries().add( entry );
    }
}
