package org.codehaus.doxia.module.xdoc;

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

import org.apache.maven.doxia.util.HtmlTools;                                                                                  
import org.apache.maven.doxia.sink.SinkAdapter;                                                                                
import org.apache.maven.doxia.sink.StructureSink;
import org.codehaus.plexus.util.xml.XMLWriter;
import org.apache.maven.doxia.parser.Parser;                                                                                   

/**
 * A doxia Sink which produces an xdoc document.
 *
 * @author juan <a href="mailto:james@jamestaylor.org">James Taylor</a>
 * @author Juan F. Codagnone  (replaced println with XmlWriterXdocSink)
 * @version $Id$
 * @componentx
 */
public class XmlWriterXdocSink
    extends SinkAdapter
{
    private final XMLWriter writer;

    private StringBuffer buffer = new StringBuffer();

    private boolean headFlag;
    //private boolean itemFlag;
    private boolean verbatimFlag;
    private int[] cellJustif;
    private int cellCount;
    private int itemFlag;
    private boolean sectionTitleFlag;

    public XmlWriterXdocSink( XMLWriter writer)
    {
        if(writer == null) {
            throw new IllegalArgumentException("argument can't be null");
        }
        this.writer = writer;
        
    }

    protected void resetState()
    {
        headFlag = false;
        buffer = new StringBuffer();
        itemFlag = 0;
        verbatimFlag = false;
        cellJustif = null;
        cellCount = 0;
        sectionTitleFlag = false;
    }

    public void head()
    {
        resetState();

        headFlag = true;

        writer.startElement("document");
        writer.startElement("properties");
    }

    public void head_()
    {
        headFlag = false;
        
        writer.endElement(); // properties    
    }

    public void title_()
    {
        if ( buffer.length() > 0 )
        {
            writer.startElement("title");
            content( buffer.toString() );
            writer.endElement(); // title
            buffer = new StringBuffer();
        }
    }

    public void author_()
    {
        if ( buffer.length() > 0 )
        {
            writer.startElement("author");
            content( buffer.toString() );
            writer.endElement(); // author
            buffer = new StringBuffer();
        }
    }

    public void date_()
    {
        if ( buffer.length() > 0 )
        {
            writer.startElement("date");
            content( buffer.toString() );
            writer.endElement();
            buffer = new StringBuffer();
        }
    }

    public void body()
    {
        writer.startElement("body");
    }

    public void body_()
    {
        writer.endElement(); // body

        writer.endElement(); // document

        resetState();
    }

    public void section1()
    {
        writer.startElement("section");
    }

    public void section2()
    {
        writer.startElement("subsection");
    }

    public void section3()
    {
        writer.startElement("subsection");
    }

    public void section4()
    {
        writer.startElement("subsection");
    }

    public void section5()
    {
        writer.startElement("subsection");
    }

    public void sectionTitle()
    {
        sectionTitleFlag = true;
        buffer = new StringBuffer();
    }

    public void sectionTitle_()
    {
        sectionTitleFlag = false;
        writer.addAttribute("name", buffer.toString());
    }

    public void section1_()
    {
        writer.endElement();
    }

    public void section2_()
    {
        writer.endElement();
    }

    public void section3_()
    {
        writer.endElement();
    }

    public void section4_()
    {
        writer.endElement();
    }

    public void section5_()
    {
        writer.endElement();
    }

    public void list()
    {
        writer.startElement("ul");
    }

    public void list_()
    {
        writer.endElement();
    }

    public void listItem()
    {
        writer.startElement("li");
        itemFlag++;
        // What follows is at least a paragraph.
    }

    public void listItem_()
    {
        writer.endElement();
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
        writer.startElement("ol");
        writer.addAttribute("style", "list-style-type: " + style);
    }

    public void numberedList_()
    {
        writer.endElement();
    }

    public void numberedListItem()
    {
        writer.startElement("li");
        itemFlag++;
        // What follows is at least a paragraph.
    }

    public void numberedListItem_()
    {
        writer.endElement();
    }

    public void definitionList()
    {
        writer.startElement("dl");
        writer.addAttribute("compact", "compact");
    }

    public void definitionList_()
    {
        writer.endElement();
    }

    public void definedTerm()
    {
        writer.startElement("dt");
        writer.startElement("b");
    }

    public void definedTerm_()
    {
        writer.endElement();
        writer.endElement();
    }

    public void definition()
    {
        writer.startElement("dd");
        itemFlag++;
        // What follows is at least a paragraph.
    }

    public void definition_()
    {
        writer.endElement();
    }

    public void paragraph()
    {
        if ( itemFlag == 0 )
            writer.startElement("p");
    }

    public void paragraph_()
    {
        if ( itemFlag == 0)
            writer.endElement();
        else
            itemFlag--;
    }

    public void verbatim( boolean boxed )
    {
        verbatimFlag = true;
        if ( boxed )
        {
            writer.startElement("source" );
        }
        else
        {
            writer.startElement("pre");
        }
    }

    public void verbatim_()
    {
        writer.endElement();

        verbatimFlag = false;
    }

    public void horizontalRule()
    {
        writer.startElement("hr");
        writer.endElement();
    }

    public void table()
    {
        writer.startElement("table");
        writer.addAttribute("align", "center");
    }

    public void table_()
    {
        writer.endElement();
    }

    public void tableRows( int[] justification, boolean grid )

    {
        writer.startElement("table");
        writer.addAttribute("align", "center");
        writer.addAttribute("border", String.valueOf(grid ? 1 : 0) );
        this.cellJustif = justification;
    }

    public void tableRows_()
    {
        writer.endElement();
    }

    public void tableRow()
    {
        writer.startElement("tr");
        writer.addAttribute("valign", "top");
        cellCount = 0;
    }

    public void tableRow_()
    {
        writer.endElement();
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

        
        writer.startElement("t" + ( headerRow ? 'h' : 'd' ));
        if ( justif != null )
            writer.addAttribute("align", justif);
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
        writer.endElement();
        ++cellCount;
    }

    public void tableCaption()
    {
        writer.startElement("p");
        writer.startElement("i");
    }

    public void tableCaption_()
    {
        writer.endElement();
        writer.endElement();
    }

    public void anchor( String name )
    {
        if ( !headFlag )
        {
            String id = StructureSink.linkToKey( name );
            writer.startElement("a");
            writer.addAttribute("id", id);
            writer.addAttribute("name", id);
        }
    }

    public void anchor_()
    {
        if ( !headFlag )
            writer.endElement();
    }

    public void link( String name )
    {
        if ( !headFlag )
        {
            writer.startElement("a");
            writer.addAttribute("href", name);
        }
    }

    public void link_()
    {
        if ( !headFlag )
            writer.endElement();
    }

    public void italic()
    {
        if ( !headFlag )
            writer.startElement("i");
    }

    public void italic_()
    {
        if ( !headFlag )
            writer.endElement();
    }

    public void bold()
    {
        if ( !headFlag )
            writer.startElement("b");
    }

    public void bold_()
    {
        if ( !headFlag )
            writer.endElement();
    }

    public void monospaced()
    {
        if ( !headFlag )
            writer.startElement("tt");
    }

    public void monospaced_()
    {
        if ( !headFlag )
            writer.endElement();
    }

    public void lineBreak()
    {
        if ( headFlag )
        {
            buffer.append( '\n' );
        }
        else
        {
            writer.startElement("br");
            writer.endElement();
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
            writer.writeText( "&#160;" );
        }
    }

    public void text( String text )
    {
        if ( headFlag )
        {
            buffer.append( text );
        }
        else if ( sectionTitleFlag ) 
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


    protected void content( String text )
    {
        writer.writeText(escapeHTML( text ));
    }

    protected void verbatimContent( String text )
    {
        writer.writeText( text.replace(" ", "&nbsp;"));
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
    }

    public void close()
    {
    }
}
