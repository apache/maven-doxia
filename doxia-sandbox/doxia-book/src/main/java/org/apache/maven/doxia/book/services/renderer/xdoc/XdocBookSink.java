package org.apache.maven.doxia.book.services.renderer.xdoc;

import org.apache.maven.doxia.module.xdoc.XdocSink;
import org.apache.maven.doxia.book.context.IndexEntry;

import java.io.Writer;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class XdocBookSink
    extends XdocSink
{
    private IndexEntry indexEntry;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public XdocBookSink( Writer out, IndexEntry indexEntry )
    {
        super( out );

        this.indexEntry = indexEntry;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void head()
    {
        resetState();

        headFlag = true;

        markup( "<?xml version=\"1.0\" ?>" + EOL );

        markup( "<document>" + EOL );

        markup( "<properties>" + EOL );

    }

    public void head_()
    {
        headFlag = false;

        markup( "</properties>" + EOL );
    }

    public void author_()
    {
        if ( buffer.length() > 0 )
        {
            markup( "<author>" );
            content( buffer.toString() );
            markup( "</author>" + EOL );
            buffer = new StringBuffer();
        }
    }

    public void date_()
    {
    }

    public void body()
    {
        markup( "<body>" + EOL );

        markup( "<table width=\"100%\" align=\"center\">" + EOL );
        markup( "<tr>" + EOL );

        // -----------------------------------------------------------------------
        // Prev
        // -----------------------------------------------------------------------

        IndexEntry prevEntry = indexEntry.getPrevEntry();

        String string;

        if ( prevEntry != null )
        {
            string = "Previous: <a href='" + prevEntry.getId() + ".html'>" + prevEntry.getTitle() + "</a>";
        }
        else
        {
            string = "&lt;first section&gt;";
        }

        markup( "<td><div align='left'>" + string + "</div></td>" + EOL );

        // -----------------------------------------------------------------------
        // Parent
        // -----------------------------------------------------------------------

        IndexEntry parent = indexEntry.getParent();

        markup( "<td><div align='center'>Up: <a href='" + parent.getId() + ".html'>" + parent.getTitle() + "</a></div></td>" + EOL );

        // -----------------------------------------------------------------------
        //
        // -----------------------------------------------------------------------

        IndexEntry nextEntry = indexEntry.getNextEntry();

        if ( nextEntry != null )
        {
            string = "Next: <a href='" + nextEntry.getId() + ".html'>" + nextEntry.getTitle() + "</a>";
        }
        else
        {
            string = "&lt;last section&gt;";
        }

        markup( "<td><div align='right'>" + string + "</div></td>" + EOL );

        // -----------------------------------------------------------------------
        //
        // -----------------------------------------------------------------------

        markup( "</tr>" + EOL );
        markup( "</table>" + EOL );
    }

    public void body_()
    {
        markup( "</body>" + EOL );

        markup( "</document>" + EOL );

        out.flush();

        resetState();
    }

    public void title_()
    {
        if ( buffer.length() > 0 )
        {
            markup( "<title>" );
            content( "Book " + buffer.toString() );
            markup( "</title>" + EOL );
            buffer = new StringBuffer();
        }
    }
}
