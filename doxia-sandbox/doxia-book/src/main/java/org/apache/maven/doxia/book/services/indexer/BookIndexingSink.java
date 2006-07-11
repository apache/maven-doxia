package org.apache.maven.doxia.book.services.indexer;

import org.apache.maven.doxia.sink.SinkAdapter;
import org.apache.maven.doxia.book.context.IndexEntry;

import java.util.List;
import java.util.Stack;

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
    private final static int TITLE = 9;

    private int type;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private String title;

    private Stack stack = new Stack();

    public BookIndexingSink( IndexEntry sectionEntry )
    {
        stack.push( sectionEntry );
    }

    public String getTitle()
    {
        return title;
    }

    // ----------------------------------------------------------------------
    // Sink Overrides
    // ----------------------------------------------------------------------

    public void title()
    {
        super.title();

        type = TITLE;
    }

    public void sectionTitle1()
    {
        type = TYPE_SECTION_1;
    }

    public void section1_()
    {
        pop();
    }

    public void sectionTitle2()
    {
        type = TYPE_SECTION_2;
    }

    public void section2_()
    {
        pop();
    }

    public void sectionTitle3()
    {
        type = TYPE_SECTION_3;
    }

    public void section3_()
    {
        pop();
    }

    public void sectionTitle4()
    {
        type = TYPE_SECTION_4;
    }

    public void section4_()
    {
        pop();
    }

    public void sectionTitle5()
    {
        type = TYPE_SECTION_5;
    }

    public void section5_()
    {
        pop();
    }

//    public void definedTerm()
//    {
//        type = TYPE_DEFINED_TERM;
//    }
//
//    public void figureCaption()
//    {
//        type = TYPE_FIGURE;
//    }
//
//    public void tableCaption()
//    {
//        type = TYPE_TABLE;
//    }

    public void text( String text )
    {
        IndexEntry entry;

        switch( type )
        {
            case TITLE:
                this.title = text;
                break;
            case TYPE_SECTION_1:
            case TYPE_SECTION_2:
            case TYPE_SECTION_3:
            case TYPE_SECTION_4:
            case TYPE_SECTION_5:
                // -----------------------------------------------------------------------
                // Sanitize the id. The most important step is to remove any blanks
                // -----------------------------------------------------------------------

                String id = text.toLowerCase().replace( ' ', '_' );

                // -----------------------------------------------------------------------
                //
                // -----------------------------------------------------------------------

                entry = new IndexEntry( peek(), id );

                entry.setTitle( text );

                push( entry );
                break;
            // Dunno how to handle these yet
            case TYPE_DEFINED_TERM:
            case TYPE_FIGURE:
            case TYPE_TABLE:
        }

        type = 0;
    }

    public void push( IndexEntry entry )
    {
//        System.out.println(  "push: " + entry.getId() );
        stack.push( entry );
    }

    public void pop()
    {
//        System.out.println(  "pop: " + peek().getId() );
        stack.pop();
    }

    public IndexEntry peek()
    {
        return (IndexEntry) stack.peek();
    }
}
