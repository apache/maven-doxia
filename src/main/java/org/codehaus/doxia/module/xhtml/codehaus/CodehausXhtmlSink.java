package org.codehaus.doxia.module.xhtml.codehaus;

import org.codehaus.doxia.module.xhtml.decoration.model.Hyperlink;
import org.codehaus.doxia.module.xhtml.decoration.model.Link;
import org.codehaus.doxia.module.xhtml.decoration.model.Hyperlink;
import org.codehaus.doxia.module.xhtml.decoration.render.NavigationRenderer;
import org.codehaus.doxia.module.HTMLSink;
import org.codehaus.doxia.module.xhtml.AbstractXhtmlSink;
import org.codehaus.doxia.module.xhtml.decoration.render.NavigationRenderer;
import org.codehaus.doxia.module.xhtml.decoration.render.RenderingContext;
import org.codehaus.doxia.sink.StructureSink;
import org.codehaus.plexus.util.xml.XMLWriter;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.List;

// Note this is highly maven-site centric at the moment with the rendering
// context and the notion of a static site. I want this particular sink to
// funciton in the context of a wiki/blog so some refactoring will be
// necessary.

/**
 * A doxia sink which produces xhtml
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @plexus.component
 */
public class CodehausXhtmlSink
    extends AbstractXhtmlSink
{
    private StringBuffer buffer;

    private boolean headFlag;

    private boolean itemFlag;

    private boolean boxedFlag;

    private boolean verbatimFlag;

    private int cellCount;

    private boolean hasTitle;

    private int sectionLevel;

    private static String STYLESHEET = "/css/doxia.css";

    private XMLWriter w;

    private Writer writer;

    private RenderingContext renderingContext;

    public CodehausXhtmlSink( Writer writer, RenderingContext renderingContext )
    {
        this.writer = writer;

        w = new PrettyPrintXMLWriter( writer );

        this.renderingContext = renderingContext;
    }

    protected void resetState()
    {
        headFlag = false;

        buffer = new StringBuffer();

        itemFlag = false;

        boxedFlag = false;

        verbatimFlag = false;

        cellCount = 0;
    }

    public void head()
    {
        resetState();

        headFlag = true;

        try
        {
            writer.write( "<?xml version=\"1.0\">\n" );

            writer.write( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1-transitional.dtd\">\n" );
        }
        catch ( IOException e )
        {
        }

        w.startElement( "html" );

        w.startElement( "head" );
    }

    public void head_()
    {
        headFlag = false;

        w.startElement( "style" );

        w.addAttribute( "type", "text/css" );

        String css = renderingContext.getRelativePath() + STYLESHEET;

        w.writeText( "@import \"" + css + "\";" );

        w.endElement();

        w.startElement( "script" );

        w.addAttribute( "src", renderingContext.getRelativePath() + "/js/ie6.js" );

        w.addAttribute( "type", "text/javascript" );

        w.writeText( " " );

        w.endElement();

        // </head>
        w.endElement();
    }

    public void title_()
    {
        if ( buffer.length() > 0 )
        {
            w.startElement( "title" );

            w.writeText( buffer.toString() );

            w.endElement();

            buffer = new StringBuffer();

            hasTitle = true;
        }
    }

    public void author_()
    {
        if ( buffer.length() > 0 )
        {
            w.startElement( "meta" );

            w.addAttribute( "name", "author" );

            w.addAttribute( "content", buffer.toString() );

            w.endElement();

            buffer = new StringBuffer();
        }
    }

    public void date_()
    {
        if ( buffer.length() > 0 )
        {
            w.startElement( "meta" );

            w.addAttribute( "name", "author" );

            w.addAttribute( "date", buffer.toString() );

            buffer = new StringBuffer();
        }
    }

    // ----------------------------------------------------------------------
    // Decorations
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Header decoration which needs to be separated
    // ----------------------------------------------------------------------

    private void bannerLink( XMLWriter w, Hyperlink link )
    {
        w.startElement( "a" );

        w.addAttribute( "href", link.getHref() );

        if ( link.getName().startsWith( "http://" ) )
        {
            w.startElement( "img" );

            w.addAttribute( "src", link.getName() );

            w.endElement();
        }
        else
        {
            w.writeText( link.getName() );
        }

        w.endElement();
    }

    protected void header()
    {
        w.startElement( "div" );

        w.addAttribute( "id", "header" );

        w.startElement( "div" );

        w.addAttribute( "id", "banner" );

        w.startElement( "div" );

        w.addAttribute( "id", "bannerLeft" );

        w.addAttribute( "class", "bannerElement" );

        bannerLink( w, renderingContext.getMavenDecorationModel().getBannerLeft() );

        w.endElement();

        w.startElement( "div" );

        w.addAttribute( "id", "bannerRight" );

        w.addAttribute( "class", "bannerElement" );

        bannerLink( w, renderingContext.getMavenDecorationModel().getBannerRight() );

        w.endElement();

        w.endElement();

        w.startElement( "div" );

        w.addAttribute( "id", "navBar" );

        w.startElement( "div" );

        w.addAttribute( "id", "navBarLeft" );

        w.addAttribute( "class", "navBarElement" );

        w.writeText( "Last published: " + new Date() );

        w.endElement();

        w.startElement( "div" );

        w.addAttribute( "id", "navBarRight" );

        w.addAttribute( "class", "navBarElement" );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        List links = renderingContext.getMavenDecorationModel().getLinks();

        for ( int i = 0; i < links.size(); i++ )
        {
            Link link = (Link) links.get( i );

            w.startElement( "a" );

            w.addAttribute( "href", link.getHref() );

            w.writeText( link.getName() );

            w.endElement();

            if ( i != links.size() - 1 )
            {
                w.writeText( " | " );
            }
        }

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        w.endElement();

        w.endElement();

        w.endElement();
    }

    protected void leftColumn()
    {
        w.startElement( "div" );

        w.addAttribute( "class", "panel one" );

        NavigationRenderer r = new NavigationRenderer();

        r.render( w, renderingContext );

        w.endElement();
    }

    protected void rightColumn()
    {
        w.startElement( "div" );

        w.addAttribute( "class", "panel three" );

        w.writeText( " " );

        w.endElement();
    }

    protected void footer()
    {
        w.startElement( "div" );

        w.addAttribute( "id", "footer" );

        w.writeText( "footer" );

        w.endElement();
    }

    public void body()
    {
        w.startElement( "body" );

        //!!
        header();

        w.startElement( "div" );

        w.addAttribute( "class", "panelBox" );

        w.startElement( "div" );

        w.addAttribute( "class", "panelRow" );

        //!!
        leftColumn();

        w.startElement( "div" );

        w.addAttribute( "class", "panel two" );
    }

    public void body_()
    {
        // panel two
        w.endElement();

        //!!
        rightColumn();

        // panelRow
        w.endElement();

        // panelBox
        w.endElement();

        //!!
        footer();

        // body
        w.endElement();

        // html
        w.endElement();

        try
        {
            writer.close();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }

        resetState();
    }

    public void section1()
    {
        sectionLevel = 1;
    }

    public void section2()
    {
        sectionLevel = 2;
    }

    public void section3()
    {
        sectionLevel = 3;
    }

    public void section4()
    {
        sectionLevel = 4;
    }

    public void section5()
    {
        sectionLevel = 5;
    }

    public void sectionTitle()
    {
        w.startElement( "h" + sectionLevel );
    }

    public void sectionTitle_()
    {
        w.endElement();
    }

    public void list()
    {
        w.startElement( "ul" );
    }

    public void list_()
    {
        w.endElement();
    }

    public void listItem()
    {
        w.startElement( "li" );

        itemFlag = true;
        // What follows is at least a paragraph.
    }

    public void listItem_()
    {
        w.endElement();
    }

    public void numberedList( int numbering )
    {
        w.startElement( "ol" );
    }

    public void numberedList_()
    {
        w.endElement();
    }

    public void numberedListItem()
    {
        w.startElement( "li" );

        itemFlag = true;
        // What follows is at least a paragraph.
    }

    public void numberedListItem_()
    {
        w.endElement();
    }

    public void definitionList()
    {
        w.startElement( "dl" );
    }

    public void definitionList_()
    {
        w.endElement();
    }

    public void definedTerm()
    {
        w.startElement( "dt" );
    }

    public void definedTerm_()
    {
        w.endElement();
    }

    public void definition()
    {
        w.startElement( "dd" );

        itemFlag = true;
        // What follows is at least a paragraph.
    }

    public void definition_()
    {
        w.endElement();
    }

    public void paragraph()
    {
        if ( !itemFlag )
        {
            w.startElement( "p" );
        }
    }

    public void paragraph_()
    {
        if ( itemFlag )
        {
            itemFlag = false;
        }
        else
        {
            w.endElement();
        }
    }

    public void verbatim( boolean boxed )
    {
        verbatimFlag = true;

        boxedFlag = boxed;

        if ( boxed )
        {
            w.startElement( "div" );

            w.addAttribute( "class", "code" );
        }

        w.startElement( "pre" );
    }

    public void verbatim_()
    {
        w.endElement();

        if ( boxedFlag )
        {
            w.endElement();
        }

        verbatimFlag = false;

        boxedFlag = false;
    }

    public void horizontalRule()
    {
        w.startElement( "hr" );

        w.endElement();
    }

    public void table()
    {
        w.startElement( "table" );

        w.addAttribute( "class", "bodyTable" );
    }

    public void table_()
    {
        w.endElement();
    }

    public void tableRows( int[] justification, boolean grid )
    {
        w.startElement( "table" );

        w.addAttribute( "class", "bodyTable" );
    }

    public void tableRows_()
    {
        w.endElement();
    }

    public void tableRow()
    {
        w.startElement( "tr" );

        cellCount = 0;
    }

    public void tableRow_()
    {
        w.endElement();

        cellCount = 0;
    }

    public void tableCell()
    {
        tableCell( false );
    }

    public void tableHeaderCell()
    {
        tableCell( true );
    }

    public void tableCell( boolean headerRow )
    {
        w.startElement( "t" + ( headerRow ? 'h' : 'd' ) );
    }

    public void tableCell_()
    {
        tableCell_( false );
    }

    public void tableHeaderCell_()
    {
        tableCell_( true );
    }

    public void tableCell_( boolean headerRow )
    {
        w.endElement();

        ++cellCount;
    }

    public void tableCaption()
    {
        w.startElement( "blockquote" );
    }

    public void tableCaption_()
    {
        w.endElement();
    }

    public void anchor( String name )
    {
        if ( !headFlag )
        {
            String id = encodeFragment( name );

            w.startElement( "a" );

            w.addAttribute( "id", id );

            w.addAttribute( "name", id );
        }
    }

    public void anchor_()
    {
        if ( !headFlag )
        {
            w.endElement();
        }
    }

    public void link( String name )
    {
        if ( !headFlag )
        {
            w.startElement( "a" );

            w.addAttribute( "href", name );
        }
    }

    public void link_()
    {
        if ( !headFlag )
        {
            w.endElement();
        }
    }

    public void italic()
    {
        if ( !headFlag )
        {
            w.startElement( "i" );
        }
    }

    public void italic_()
    {
        if ( !headFlag )
        {
            w.endElement();
        }
    }

    public void bold()
    {
        if ( !headFlag )
        {
            w.startElement( "b" );
        }
    }

    public void bold_()
    {
        if ( !headFlag )
        {
            w.endElement();
        }
    }

    public void monospaced()
    {
        if ( !headFlag )
        {
            w.startElement( "tt" );
        }
    }

    public void monospaced_()
    {
        if ( !headFlag )
        {
            w.endElement();
        }
    }

    public void lineBreak()
    {
        if ( headFlag )
        {
            buffer.append( '\n' );
        }
        else
        {
            w.startElement( "br" );

            w.endElement();
        }
    }

    public void nonBreakingSpace()
    {
        if ( headFlag )
        {
            buffer.append( ' ' );
        }
        else
        {
            markup( "&#160;" );
        }
    }

    public void text( String text )
    {
        if ( headFlag )
        {
            buffer.append( text );
        }
        else
        {
            if ( verbatimFlag )
            {
                verbatimContent( text );
            }
            else
            {
                content( text );
            }
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected void markup( String text )

    {
        w.writeText( text );
    }

    protected void content( String text )

    {
        w.writeText( escapeHTML( text ) );
    }

    protected void verbatimContent( String text )

    {
        w.writeText( escapeHTML( text ) );
    }

    public static String escapeHTML( String text )
    {
        return HTMLSink.escapeHTML( text );
    }

    public static String encodeFragment( String text )
    {
        return encodeURL( StructureSink.linkToKey( text ) );
    }

    public static String encodeURL( String text )
    {
        return HTMLSink.encodeURL( text );
    }
}
