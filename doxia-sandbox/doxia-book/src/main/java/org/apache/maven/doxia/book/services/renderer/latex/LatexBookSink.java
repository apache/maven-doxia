package org.apache.maven.doxia.book.services.renderer.latex;

import org.apache.maven.doxia.module.latex.LatexSink;

import java.io.Writer;
import java.io.IOException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class LatexBookSink
    extends LatexSink
{
    private String text;

    private String title;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public LatexBookSink( Writer out )
    {
        super( out, null, null, true );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected String getDocumentStart()
    {
        return "";
//        return "\\documentclass{book}";
    }

    protected String getDocumentBegin()
    {
        return null;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void text( String text )
    {
        this.text = text;

        super.text( text );
    }

    public void title_()
    {
        super.title_();

        this.title = text;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public String getTitle()
    {
        return title;
    }
}
