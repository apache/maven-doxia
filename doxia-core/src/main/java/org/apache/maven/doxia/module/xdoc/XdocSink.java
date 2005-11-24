package org.apache.maven.doxia.module.xdoc;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.doxia.module.HtmlTools;
import org.apache.maven.doxia.module.apt.AptParser;
import org.apache.maven.doxia.sink.SinkAdapter;
import org.apache.maven.doxia.sink.StructureSink;
import org.apache.maven.doxia.util.LineBreaker;

import java.io.Writer;

/**
 * A doxia Sink which produces an xdoc document.
 *
 * @author <a href="mailto:james@jamestaylor.org">James Taylor</a>
 * @version $Id:XdocSink.java 348605 2005-11-24 12:02:44 +1100 (Thu, 24 Nov 2005) brett $
 * @componentx
 */
public class XdocSink
    extends SinkAdapter
{
    private LineBreaker out;

    private StringBuffer buffer = new StringBuffer();

    private boolean headFlag;

    private int itemFlag;

    private boolean boxedFlag;

    private boolean verbatimFlag;

    private int[] cellJustif;

    private int cellCount;

    private String section;

    public XdocSink( Writer out )
    {
        this.out = new LineBreaker( out );
    }

    protected void resetState()
    {
        headFlag = false;
        buffer = new StringBuffer();
        itemFlag = 0;
        boxedFlag = false;
        verbatimFlag = false;
        cellJustif = null;
        cellCount = 0;
    }

    public void head()
    {
        resetState();

        headFlag = true;

        markup( "<?xml version=\"1.0\" ?>\n" );

        markup( "<document>\n" );

        markup( "<properties>\n" );
    }

    public void head_()
    {
        headFlag = false;

        markup( "</properties>\n" );
    }

    public void title_()
    {
        if ( buffer.length() > 0 )
        {
            markup( "<title>" );
            content( buffer.toString() );
            markup( "</title>\n" );
            buffer = new StringBuffer();
        }
    }

    public void author_()
    {
        if ( buffer.length() > 0 )
        {
            markup( "<author>" );
            content( buffer.toString() );
            markup( "</author>\n" );
            buffer = new StringBuffer();
        }
    }

    public void date_()
    {
        if ( buffer.length() > 0 )
        {
            markup( "<date>" );
            content( buffer.toString() );
            markup( "</date>" );
            buffer = new StringBuffer();
        }
    }

    public void body()
    {
        markup( "<body>\n" );
    }

    public void body_()
    {
        markup( "</body>\n" );

        markup( "</document>\n" );

        out.flush();

        resetState();
    }

    public void section1()
    {
        section = "section";
    }

    public void section2()
    {
        section = "subsection";
    }

    public void section3()
    {
        section = "subsection";
    }

    public void section4()
    {
        section = "subsection";
    }

    public void section5()
    {
        section = "subsection";
    }

    public void sectionTitle()
    {
        markup( "<" + section + " name=\"" );
    }

    public void sectionTitle_()
    {
        markup( "\">" );
    }

    public void section1_()
    {
        markup( "</section>" );
    }

    public void section2_()
    {
        markup( "</subsection>" );
    }

    public void section3_()
    {
        markup( "</subsection>" );
    }

    public void section4_()
    {
        markup( "</subsection>" );
    }

    public void section5_()
    {
        markup( "</subsection>" );
    }

    public void list()
    {
        markup( "<ul>\n" );
    }

    public void list_()
    {
        markup( "</ul>" );
    }

    public void listItem()
    {
        markup( "<li>" );
        itemFlag++;
        // What follows is at least a paragraph.
    }

    public void listItem_()
    {
        markup( "</li>\n" );
    }

    public void numberedList( int numbering )
    {
        String style;
        switch ( numbering )
        {
            case NUMBERING_UPPER_ALPHA:
                style = "upper-alpha";
                break;
            case NUMBERING_LOWER_ALPHA:
                style = "lower-alpha";
                break;
            case NUMBERING_UPPER_ROMAN:
                style = "upper-roman";
                break;
            case NUMBERING_LOWER_ROMAN:
                style = "lower-roman";
                break;
            case NUMBERING_DECIMAL:
            default:
                style = "decimal";
        }
        markup( "<ol style=\"list-style-type: " + style + "\">\n" );
    }

    public void numberedList_()
    {
        markup( "</ol>" );
    }

    public void numberedListItem()
    {
        markup( "<li>" );
        itemFlag++;
        // What follows is at least a paragraph.
    }

    public void numberedListItem_()
    {
        markup( "</li>\n" );
    }

    public void definitionList()
    {
        markup( "<dl compact=\"compact\">\n" );
    }

    public void definitionList_()
    {
        markup( "</dl>" );
    }

    public void definedTerm()
    {
        markup( "<dt><b>" );
    }

    public void definedTerm_()
    {
        markup( "</b></dt>\n" );
    }

    public void definition()
    {
        markup( "<dd>" );
        itemFlag++;
        // What follows is at least a paragraph.
    }

    public void definition_()
    {
        markup( "</dd>\n" );
    }

    public void paragraph()
    {
        if ( itemFlag == 0 )
        {
            markup( "<p>" );
        }
    }

    public void paragraph_()
    {
        if ( itemFlag == 0 )
        {
            markup( "</p>" );
        }
        else
        {
            itemFlag--;
        }

    }

    public void verbatim( boolean boxed )
    {
        verbatimFlag = true;
        boxedFlag = boxed;
        if ( boxed )
        {
            markup( "<source>" );
        }
        else
        {
            markup( "<pre>" );
        }
    }

    public void verbatim_()
    {
        if ( boxedFlag )
        {
            markup( "</source>" );
        }
        else
        {
            markup( "</pre>" );
        }

        verbatimFlag = false;

        boxedFlag = false;
    }

    public void horizontalRule()
    {
        markup( "<hr />" );
    }

    public void table()
    {
        markup( "<table align=\"center\">\n" );
    }

    public void table_()
    {
        markup( "</table>" );
    }

    public void tableRows( int[] justification, boolean grid )

    {
        markup( "<table align=\"center\" border=\"" + ( grid ? 1 : 0 ) + "\">\n" );
        this.cellJustif = justification;
    }

    public void tableRows_()
    {
        markup( "</table>" );
    }

    public void tableRow()
    {
        markup( "<tr valign=\"top\">\n" );
        cellCount = 0;
    }

    public void tableRow_()
    {
        markup( "</tr>\n" );
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
                case AptParser.JUSTIFY_LEFT:
                    justif = "left";
                    break;
                case AptParser.JUSTIFY_RIGHT:
                    justif = "right";
                    break;
                case AptParser.JUSTIFY_CENTER:
                default:
                    justif = "center";
                    break;
            }
        }

        if ( justif != null )
        {
            markup( "<t" + ( headerRow ? 'h' : 'd' ) + " align=\"" + justif + "\">" );
        }
        else
        {
            markup( "<t" + ( headerRow ? 'h' : 'd' ) + ">" );
        }
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
        markup( "</t" + ( headerRow ? 'h' : 'd' ) + ">\n" );
        ++cellCount;
    }

    public void tableCaption()
    {
        markup( "<p><i>" );
    }

    public void tableCaption_()
    {
        markup( "</i></p>" );
    }

    public void anchor( String name )
    {
        if ( !headFlag )
        {
            String id = StructureSink.linkToKey( name );
            markup( "<a id=\"" + id + "\" name=\"" + id + "\">" );
        }
    }

    public void anchor_()
    {
        if ( !headFlag )
        {
            markup( "</a>" );
        }
    }

    public void link( String name )
    {
        if ( !headFlag )
        {
            markup( "<a href=\"" + name + "\">" );
        }
    }

    public void link_()
    {
        if ( !headFlag )
        {
            markup( "</a>" );
        }
    }

    public void italic()
    {
        if ( !headFlag )
        {
            markup( "<i>" );
        }
    }

    public void italic_()
    {
        if ( !headFlag )
        {
            markup( "</i>" );
        }
    }

    public void bold()
    {
        if ( !headFlag )
        {
            markup( "<b>" );
        }
    }

    public void bold_()
    {
        if ( !headFlag )
        {
            markup( "</b>" );
        }
    }

    public void monospaced()
    {
        if ( !headFlag )
        {
            markup( "<tt>" );
        }
    }

    public void monospaced_()
    {
        if ( !headFlag )
        {
            markup( "</tt>" );
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
            markup( "<br />" );
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
        out.write( text, true );
    }

    protected void content( String text )
    {
        out.write( escapeHTML( text ), false );
    }

    protected void verbatimContent( String text )
    {
        out.write( escapeHTML( text ), true );
    }

    public static String escapeHTML( String text )
    {
        return HtmlTools.escapeHTML( text );
    }

    public static String encodeURL( String text )
    {
        return HtmlTools.encodeURL( text );
    }

    public void flush()
    {
        out.flush();
    }

    public void close()
    {
        out.close();
    }
}
