package org.apache.maven.doxia.module.xdoc;

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

import java.io.Writer;

import org.apache.maven.doxia.util.HtmlTools;
import org.apache.maven.doxia.sink.SinkAdapter;
import org.apache.maven.doxia.util.LineBreaker;
import org.apache.maven.doxia.parser.Parser;

/**
 * A doxia Sink which produces an xdoc model.
 *
 * @author <a href="mailto:james@jamestaylor.org">James Taylor</a>
 * @version $Id:XdocSink.java 348605 2005-11-24 12:02:44 +1100 (Thu, 24 Nov 2005) brett $
 */
public class XdocSink
    extends SinkAdapter
{
    protected static final String EOL = System.getProperty( "line.separator" );

    protected LineBreaker out;

    protected StringBuffer buffer = new StringBuffer();

    protected boolean headFlag;

    /**
     * An indication on if we're inside a title.
     *
     * This will prevent the styling of titles.
     */
    protected boolean titleFlag;

    private boolean itemFlag;

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
        itemFlag = false;
        boxedFlag = false;
        verbatimFlag = false;
        cellJustif = null;
        cellCount = 0;
    }

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

    public void title_()
    {
        if ( buffer.length() > 0 )
        {
            markup( "<title>" );
            content( buffer.toString() );
            markup( "</title>" + EOL );
            buffer = new StringBuffer();
        }
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
        markup( "<body>" + EOL );
    }

    public void body_()
    {
        markup( "</body>" + EOL );

        markup( "</document>" + EOL );

        out.flush();

        resetState();
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    public void section1()
    {
        onSection( 1 );
    }

    public void sectionTitle1()
    {
        onSectionTitle( 1 );
    }

    public void sectionTitle1_()
    {
        onSectionTitle_( 1 );
    }

    public void section1_()
    {
        onSection_( 1 );
    }

    public void section2()
    {
        onSection( 2 );
    }

    public void sectionTitle2()
    {
        onSectionTitle( 2 );
    }

    public void sectionTitle2_()
    {
        onSectionTitle_( 2 );
    }

    public void section2_()
    {
        onSection_( 2 );
    }

    public void section3()
    {
        onSection( 3 );
    }

    public void sectionTitle3()
    {
        onSectionTitle( 3 );
    }

    public void sectionTitle3_()
    {
        onSectionTitle_( 3 );
    }

    public void section3_()
    {
        onSection_( 3 );
    }

    public void section4()
    {
        onSection( 4 );
    }

    public void sectionTitle4()
    {
        onSectionTitle( 4 );
    }

    public void sectionTitle4_()
    {
        onSectionTitle_( 4 );
    }

    public void section4_()
    {
        onSection_( 4 );
    }

    public void section5()
    {
        onSection( 5 );
    }

    public void sectionTitle5()
    {
        onSectionTitle( 5 );
    }

    public void sectionTitle5_()
    {
        onSectionTitle_( 5 );
    }

    public void section5_()
    {
        onSection_( 5 );
    }

    private void onSection( int depth )
    {
        if ( depth == 1 )
        {
            markup( "<section name=\"" );
        }
        else
        {
            markup( "<subsection name=\"" );
        }
    }

    private void onSectionTitle( int depth )
    {
        titleFlag = true;
    }

    private void onSectionTitle_( int depth )
    {
        markup( "\">" );

        titleFlag = false;
    }

    private void onSection_( int depth )
    {
        if ( depth == 1 )
        {
            markup( "</section>" );
        }
        else
        {
            markup( "</subsection>" );
        }
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    public void list()
    {
        markup( "<ul>" + EOL );
    }

    public void list_()
    {
        markup( "</ul>" );
        itemFlag = false;
    }

    public void listItem()
    {
        markup( "<li>" );
        itemFlag = true;
        // What follows is at least a paragraph.
    }

    public void listItem_()
    {
        markup( "</li>" + EOL );
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
        markup( "<ol style=\"list-style-type: " + style + "\">" + EOL );
    }

    public void numberedList_()
    {
        markup( "</ol>" );
        itemFlag = false;
    }

    public void numberedListItem()
    {
        markup( "<li>" );
        itemFlag = true;
        // What follows is at least a paragraph.
    }

    public void numberedListItem_()
    {
        markup( "</li>" + EOL );
    }

    public void definitionList()
    {
        markup( "<dl compact=\"compact\">" + EOL );
    }

    public void definitionList_()
    {
        markup( "</dl>" );
        itemFlag = false;
    }

    public void definedTerm()
    {
        markup( "<dt><b>" );
    }

    public void definedTerm_()
    {
        markup( "</b></dt>" + EOL );
    }

    public void definition()
    {
        markup( "<dd>" );
        itemFlag = true;
        // What follows is at least a paragraph.
    }

    public void definition_()
    {
        markup( "</dd>" + EOL );
    }

    public void figure()
    {
        markup( "<img" );
    }

    public void figure_()
    {
        markup( " />" );
    }

    public void figureGraphics(String s)
    {
        markup( " src=\"" + s + "\"" );
    }

    public void figureCaption()
    {
        markup( " alt=\"" );
    }

    public void figureCaption_()
    {
        markup( "\"" );
    }

    public void paragraph()
    {
        if ( !itemFlag )
        {
            markup( "<p>" );
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
            markup( "</p>" );
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
        markup( "<table align=\"center\">" + EOL );
    }

    public void table_()
    {
        markup( "</table>" );
    }

    public void tableRows( int[] justification, boolean grid )

    {
        markup( "<table align=\"center\" border=\"" + ( grid ? 1 : 0 ) + "\">" + EOL );
        this.cellJustif = justification;
    }

    public void tableRows_()
    {
        markup( "</table>" );
    }

    public void tableRow()
    {
        markup( "<tr valign=\"top\">" + EOL );
        cellCount = 0;
    }

    public void tableRow_()
    {
        markup( "</tr>" + EOL );
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
        markup( "</t" + ( headerRow ? 'h' : 'd' ) + ">" + EOL );
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
        if ( !headFlag && !titleFlag )
        {
            String id = HtmlTools.encodeId( name );
            markup( "<a id=\"" + id + "\" name=\"" + id + "\">" );
        }
    }

    public void anchor_()
    {
        if ( !headFlag && !titleFlag )
        {
            markup( "</a>" );
        }
    }

    public void link( String name )
    {
        if ( !headFlag && !titleFlag )
        {
            markup( "<a href=\"" + name + "\">" );
        }
    }

    public void link_()
    {
        if ( !headFlag && !titleFlag )
        {
            markup( "</a>" );
        }
    }

    public void italic()
    {
        if ( !headFlag && !titleFlag )
        {
            markup( "<i>" );
        }
    }

    public void italic_()
    {
        if ( !headFlag && !titleFlag )
        {
            markup( "</i>" );
        }
    }

    public void bold()
    {
        if ( !headFlag && !titleFlag )
        {
            markup( "<b>" );
        }
    }

    public void bold_()
    {
        if ( !headFlag && !titleFlag )
        {
            markup( "</b>" );
        }
    }

    public void monospaced()
    {
        if ( !headFlag && !titleFlag )
        {
            markup( "<tt>" );
        }
    }

    public void monospaced_()
    {
        if ( !headFlag && !titleFlag )
        {
            markup( "</tt>" );
        }
    }

    public void lineBreak()
    {
        if ( headFlag || titleFlag )
        {
            buffer.append( EOL );
        }
        else
        {
            markup( "<br />" );
        }
    }

    public void nonBreakingSpace()
    {
        if ( headFlag || titleFlag )
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
        else if ( verbatimFlag )
        {
            verbatimContent( text );
        }
        else
        {
            content( text );
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
