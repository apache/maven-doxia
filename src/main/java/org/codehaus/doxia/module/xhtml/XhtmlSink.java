package org.codehaus.doxia.module.xhtml;

import org.codehaus.doxia.module.HtmlTools;
import org.codehaus.doxia.module.xhtml.decoration.render.BannerRenderer;
import org.codehaus.doxia.module.xhtml.decoration.render.LinksRenderer;
import org.codehaus.doxia.module.xhtml.decoration.render.NavigationRenderer;
import org.codehaus.doxia.module.xhtml.decoration.render.RenderingContext;
import org.codehaus.doxia.parser.Parser;
import org.codehaus.doxia.sink.StructureSink;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Note this is highly maven-site centric at the moment with the rendering
// context and the notion of a static site. I want this particular sink to
// funciton in the context of a wiki/blog so some refactoring will be

/**
 * A doxia sink which produces xhtml
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 */
public class XhtmlSink
    extends AbstractXhtmlSink
{
    private StringBuffer buffer = new StringBuffer();

    private boolean headFlag;

    private boolean itemFlag;

    private boolean boxedFlag;

    private boolean verbatimFlag;

    private int cellCount;

    private boolean hasTitle;

    private int sectionLevel;

    private PrintWriter writer;

    private StringsMap directives;

    private RenderingContext renderingContext;

    private int[] cellJustif;

    public XhtmlSink( Writer writer, RenderingContext renderingContext, Map directives )
    {
        this.writer = new PrintWriter( writer );

        this.directives = new StringsMap( directives );

        this.renderingContext = renderingContext;
    }

    protected StringBuffer getBuffer()
    {
        return buffer;
    }

    protected void setHeadFlag( boolean headFlag )
    {
        this.headFlag = headFlag;
    }

    protected void resetState()
    {
        headFlag = false;

        resetBuffer();

        itemFlag = false;

        boxedFlag = false;

        verbatimFlag = false;

        cellCount = 0;
    }

    protected void resetBuffer()
    {
        buffer = new StringBuffer();
    }

    public void head()
    {
        directive( "head()" );

        resetState();

        headFlag = true;
    }

    public void head_()
    {
        headFlag = false;

        directive( "head_()" );
    }

    public void title()
    {
        write( "<title>" );
    }

    public void title_()
    {
        write( buffer.toString() );

        write( "</title>" );

        resetBuffer();

        hasTitle = true;
    }

    public void author_()
    {
        if ( buffer.length() > 0 )
        {
            write( "<meta name=\"author\" content=\"" );
            write( buffer.toString() );
            write( "\" />\n" );
            resetBuffer();
        }
    }

    public void date_()
    {
        if ( buffer.length() > 0 )
        {
            write( "<meta name=\"date\" content=\"" );
            write( buffer.toString() );
            write( "\" />\n" );
            resetBuffer();
        }
    }

    public void body()
    {
        String body = directiveValue( "body()" );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        NavigationRenderer r = new NavigationRenderer();

        StringWriter sw = new StringWriter();

        XMLWriter w = new PrettyPrintXMLWriter( sw );

        r.render( w, renderingContext );

        Map map = new HashMap();

        map.put( "mainMenu", sw.toString() );

        sw = new StringWriter();

        w = new PrettyPrintXMLWriter( sw );

        LinksRenderer lr = new LinksRenderer();

        lr.render( w, renderingContext );

        map.put( "links", sw.toString() );

        sw = new StringWriter();

        w = new PrettyPrintXMLWriter( sw );

        BannerRenderer br = new BannerRenderer( "bannerLeft" );

        br.render( w, renderingContext );

        map.put( "bannerLeft", sw.toString() );

        sw = new StringWriter();

        w = new PrettyPrintXMLWriter( sw );

        br = new BannerRenderer( "bannerRight" );

        br.render( w, renderingContext );

        map.put( "bannerRight", sw.toString() );

        map.put( "navBarLeft", "Last Published: " + new Date() );

        map.put( "rightColumn", " " );

        body = StringUtils.interpolate( body, map );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        write( body );
    }

    public void body_()
    {
        String body = directiveValue( "body_()" );

        Map map = new HashMap();

        map.put( "rightColumn", " " );

        map.put( "footer", "Powered by Doxia 2004 (c)" );

        body = StringUtils.interpolate( body, map );

        write( body );

        resetState();
    }

    // ----------------------------------------------------------------------
    // Sections
    // ----------------------------------------------------------------------

    public void section1()
    {
        write( "<div class=\"section\">" );
    }

    public void section2()
    {
        write( "<div class=\"section\">" );
    }

    public void section3()
    {
        write( "<div class=\"section\">" );
    }

    public void section4()
    {
        write( "<div class=\"section\">" );
    }

    public void section5()
    {
        write( "<div class=\"section\">" );
    }

    public void section1_()
    {
        write( "</div>" );
    }

    public void section2_()
    {
        write( "</div>" );
    }

    public void section3_()
    {
        write( "</div>" );
    }

    public void section4_()
    {
        write( "</div>" );
    }

    public void section5_()
    {
        write( "</div>" );
    }

    public void sectionTitle1()
    {
        write( "<h2>" );
    }

    public void sectionTitle1_()
    {
        write( "</h2>" );
    }

    public void sectionTitle2()
    {
        write( "<h3>" );
    }

    public void sectionTitle2_()
    {
        write( "</h3>" );
    }

    public void sectionTitle3()
    {
        write( "<h4>" );
    }

    public void sectionTitle3_()
    {
        write( "</h4>" );
    }

    public void sectionTitle4()
    {
        write( "<h5>" );
    }

    public void sectionTitle4_()
    {
        write( "</h5>" );
    }

    public void sectionTitle5()
    {
        write( "<h6>" );
    }

    public void sectionTitle5_()
    {
        write( "</h6>" );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void list()
    {
        write( "<ul>" );
    }

    public void list_()
    {
        write( "</ul>" );
    }

    public void listItem()
    {
        write( "<li>" );

        itemFlag = true;
        // What follows is at least a paragraph.
    }

    public void listItem_()
    {
        write( "</li>" );
    }

    public void numberedList( int numbering )
    {
        write( "<ol>" );
    }

    public void numberedList_()
    {
        write( "</ol>" );
    }

    public void numberedListItem()
    {
        write( "<li>" );

        itemFlag = true;
        // What follows is at least a paragraph.
    }

    public void numberedListItem_()
    {
        write( "</li>" );
    }

    public void definitionList()
    {
        write( "<dl>" );
    }

    public void definitionList_()
    {
        write( "</dl>" );
    }

    public void definedTerm()
    {
        write( "<dt>" );
    }

    public void definedTerm_()
    {
        write( "</dt>" );
    }

    public void definition()
    {
        write( "<dd>" );

        itemFlag = true;
        // What follows is at least a paragraph.
    }

    public void definition_()
    {
        write( "</dd>" );
    }

    public void paragraph()
    {
        if ( !itemFlag )
        {
            write( "<p>" );
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
            write( "</p>" );
        }
    }

    public void verbatim( boolean boxed )
    {
        verbatimFlag = true;

        boxedFlag = boxed;

        write( "<div class=\"source\"><pre>" );
    }

    public void verbatim_()
    {
        write( "</pre></div>" );

        verbatimFlag = false;

        boxedFlag = false;
    }

    public void horizontalRule()
    {
        write( "<hr />" );
    }

    public void table()
    {
        write( "<table class=\"bodyTable\">" );
    }

    public void table_()
    {
        write( "</table>");
    }

    public void tableRows( int[] justification, boolean grid )
    {
        write( "<tbody>" );

        cellJustif = justification;
    }

    public void tableRows_()
    {
        write( "</tbody>" );

        cellJustif = null;
    }

    private int rowMarker = 0;

    //TODO: could probably make this more flexible but really i would just like a standard xhtml structure.
    public void tableRow()
    {
        if ( rowMarker == 0 )
        {
            write( "<tr class=\"a\">" );

            rowMarker = 1;
        }
        else
        {
            write( "<tr class=\"b\">" );

            rowMarker = 0;
        }

        cellCount = 0;
    }

    public void tableRow_()
    {
        write( "</tr>" );

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
    	String justif = null;

        if ( cellJustif != null )
        {
        	switch ( cellJustif[cellCount] )
        	{
            	case Parser.JUSTIFY_LEFT:
            	    justif = "left";
            	    break;
            	case Parser.JUSTIFY_RIGHT:
            	    justif = "right";
            	    break;
            	case Parser.JUSTIFY_CENTER:
            	    justif = "center";
            	    break;
        	}
        }

        if ( headerRow )
        {
            write( "<th" );
        }
        else
        {
            write( "<td" );
        }

        if ( justif != null )
        {
            write( " align=\"" + justif + "\"" );
        }

        write( ">" );
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
        if ( headerRow )
        {
            write( "</th>" );
        }
        else
        {
            write( "</td>" );
        }

        ++cellCount;
    }

    public void tableCaption()
    {
        write( "<caption>" );
    }

    public void tableCaption_()
    {
        write( "</caption>" );
    }

    /**
     * @see org.codehaus.doxia.sink.SinkAdapter#figure()
     */
    public void figure()
    {
        write( "<img" );
    }

    /**
     * @see org.codehaus.doxia.sink.SinkAdapter#figure_()
     */
    public void figure_()
    {
        write( " />" );
    }


    /**
     * @see org.codehaus.doxia.sink.SinkAdapter#figureCaption()
     */
    public void figureCaption()
    {
        write( " alt=\"" );
    }

    /**
     * @see org.codehaus.doxia.sink.SinkAdapter#figureCaption_()
     */
    public void figureCaption_()
    {
        write( "\"" );
    }

    /**
     * @see org.codehaus.doxia.sink.SinkAdapter#figureGraphics(java.lang.String)
     */
    public void figureGraphics( String name )
    {
        write( " src=\"" + name + "\"" );
    }

    public void anchor( String name )
    {
        if ( !headFlag )
        {
            write( "<a name=\"" + name + "\">" );
        }
    }

    public void anchor_()
    {
        if ( !headFlag )
        {
            write( "</a>" );
        }
    }

    public void link( String name )
    {
        if ( !headFlag )
        {
            write( "<a href=\"" + name + "\">" );
        }
    }

    public void link( String name, String target )
    {
        if ( !headFlag )
        {
            write( "<a href=\"" + name + "\" target=\"" + target + "\">" );
        }
    }

    public void link_()
    {
        if ( !headFlag )
        {
            write( "</a>" );
        }
    }

    public void italic()
    {
        if ( !headFlag )
        {
            write( "<i>" );
        }
    }

    public void italic_()
    {
        if ( !headFlag )
        {
            write( "</i>" );
        }
    }

    public void bold()
    {
        if ( !headFlag )
        {
            write( "<b>" );
        }
    }

    public void bold_()
    {
        if ( !headFlag )
        {
            write( "</b>" );
        }
    }

    public void monospaced()
    {
        if ( !headFlag )
        {
            write( "<tt>" );
        }
    }

    public void monospaced_()
    {
        if ( !headFlag )
        {
            write( "</tt>" );
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
            write( "<br />" );
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
            write( "&#160;" );
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

    public void rawText( String text )
    {
        write( text );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected void write( String text )
    {
        String relativePathToBasedir = renderingContext.getRelativePath();

        if ( relativePathToBasedir != null )
        {
            text = StringUtils.replace( text, "$relativePath", relativePathToBasedir );
        }
        else
        {
            text = StringUtils.replace( text, "$relativePath", "." );
        }

        writer.write( text );
    }

    protected String directiveValue( String key )
    {
        return directives.get( key );
    }

    protected void directive( String key )
    {
        write( directives.get( key ) );
    }

    protected void content( String text )
    {
        write( escapeHTML( text ) );
    }

    protected void verbatimContent( String text )

    {
        write( escapeHTML( text ) );
    }

    public static String escapeHTML( String text )
    {
        return HtmlTools.escapeHTML( text );
    }

    public static String encodeFragment( String text )
    {
        return encodeURL( StructureSink.linkToKey( text ) );
    }

    public static String encodeURL( String text )
    {
        return HtmlTools.encodeURL( text );
    }

    public void flush()
    {
        writer.flush();
    }

    public void close()
    {
        writer.close();
    }
}
