package org.apache.maven.doxia.module.docbook;

/*
 * Copyright 2004-2006 The Apache Software Foundation.
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

import org.apache.maven.doxia.module.apt.AptParser;
import org.apache.maven.doxia.sink.SinkAdapter;
import org.apache.maven.doxia.sink.StructureSink;
import org.apache.maven.doxia.util.FileUtil;
import org.apache.maven.doxia.util.LineBreaker;

import java.io.StringWriter;
import java.io.Writer;
import java.util.StringTokenizer;

public class DocBookSink
    extends SinkAdapter
{
    private static final String EOL = System.getProperty( "line.separator" );

    public static final String DEFAULT_SGML_PUBLIC_ID = "-//OASIS//DTD DocBook V4.1//EN";

    public static final String DEFAULT_XML_PUBLIC_ID = "-//OASIS//DTD DocBook XML V4.1.2//EN";

    public static final String DEFAULT_XML_SYSTEM_ID = "http://www.oasis-open.org/docbook/xml/4.0/docbookx.dtd";

    private LineBreaker out;

    private boolean xmlMode = false;

    private String encoding = null;

    private String styleSheet = null;

    private String lang = null;

    private String publicId = null;

    private String systemId = null;

    private String italicBeginTag;

    private String italicEndTag;

    private String boldBeginTag;

    private String boldEndTag;

    private String monospacedBeginTag;

    private String monospacedEndTag;

    private String horizontalRuleElement;

    private String pageBreakElement;

    private String lineBreakElement;

    protected String graphicsFileName;

    private boolean hasTitle;

    private boolean authorDateFlag;

    private boolean verbatimFlag;

    private boolean externalLinkFlag;

    private boolean tableHasCaption;

    private LineBreaker savedOut;

    private String tableRows;

    private boolean tableHasGrid;

    // -----------------------------------------------------------------------

    public DocBookSink( Writer out )
    {
        this.out = new LineBreaker( out );
        setItalicElement( "<emphasis>" );
        setBoldElement( "<emphasis role=\"bold\">" );
        setMonospacedElement( "<literal>" );
        setHorizontalRuleElement( "<!-- HR -->" );
        setPageBreakElement( "<!-- PB -->" );
        setLineBreakElement( "<!-- LB -->" );
    }

    public static final String escapeSGML( String text, boolean xmlMode )
    {
        int length = text.length();
        StringBuffer buffer = new StringBuffer( length );

        for ( int i = 0; i < length; ++i )
        {
            char c = text.charAt( i );
            switch ( c )
            {
                case '<':
                    buffer.append( "&lt;" );
                    break;
                case '>':
                    buffer.append( "&gt;" );
                    break;
                case '&':
                    buffer.append( "&amp;" );
                    break;
                case '"':
                    buffer.append( "&quot;" );
                    break;
                default:
                    if ( xmlMode )
                    {
                        buffer.append( c );
                    }
                    else
                    {
                        if ( c <= 0x7E )
                        {
                            // ASCII.
                            buffer.append( c );
                        }
                        else
                        {
                            buffer.append( "&#" );
                            buffer.append( (int) c );
                            buffer.append( ';' );
                        }
                    }
            }
        }

        return buffer.toString();
    }

    public void setXMLMode( boolean xmlMode )
    {
        this.xmlMode = xmlMode;
    }

    public boolean isXMLMode()
    {
        return xmlMode;
    }

    public void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void setStyleSheet( String styleSheet )
    {
        this.styleSheet = styleSheet;
    }

    public String getStyleSheet()
    {
        return styleSheet;
    }

    public void setPublicId( String publicId )
    {
        this.publicId = publicId;
    }

    public String getPublicId()
    {
        return publicId;
    }

    public void setSystemId( String systemId )
    {
        this.systemId = systemId;
    }

    public String getSystemId()
    {
        return systemId;
    }

    public void setLanguage( String lang )
    {
        this.lang = lang;
    }

    public String getLanguage()
    {
        return lang;
    }

    public void setItalicElement( String italicBeginTag )
    {
        if ( italicBeginTag == null )
        {
            italicBeginTag = "";
        }
        this.italicBeginTag = italicBeginTag;
        italicEndTag = makeEndTag( italicBeginTag );
    }

    private String makeEndTag( String beginTag )
    {
        int length = beginTag.length();
        if ( length == 0 )
        {
            return "";
        }

        if ( beginTag.charAt( 0 ) != '<' || beginTag.charAt( length - 1 ) != '>' )
        {
            throw new IllegalArgumentException( "'" + beginTag + "', not a tag" );
        }

        StringTokenizer tokens = new StringTokenizer( beginTag, "<> \t\n\r\f" );
        if ( !tokens.hasMoreTokens() )
        {
            throw new IllegalArgumentException( "'" + beginTag + "', invalid tag" );
        }

        return "</" + tokens.nextToken() + ">";
    }

    public String getItalicElement()
    {
        return italicBeginTag;
    }

    public void setBoldElement( String boldBeginTag )
    {
        if ( boldBeginTag == null )
        {
            boldBeginTag = "";
        }
        this.boldBeginTag = boldBeginTag;
        boldEndTag = makeEndTag( boldBeginTag );
    }

    public String getBoldElement()
    {
        return boldBeginTag;
    }

    public void setMonospacedElement( String monospacedBeginTag )
    {
        if ( monospacedBeginTag == null )
        {
            monospacedBeginTag = "";
        }
        this.monospacedBeginTag = monospacedBeginTag;
        monospacedEndTag = makeEndTag( monospacedBeginTag );
    }

    public String getMonospacedElement()
    {
        return monospacedBeginTag;
    }

    public void setHorizontalRuleElement( String horizontalRuleElement )
    {
        this.horizontalRuleElement = horizontalRuleElement;
    }

    public String getHorizontalRuleElement()
    {
        return horizontalRuleElement;
    }

    public void setPageBreakElement( String pageBreakElement )
    {
        this.pageBreakElement = pageBreakElement;
    }

    public String getPageBreakElement()
    {
        return pageBreakElement;
    }

    public void setLineBreakElement( String lineBreakElement )
    {
        this.lineBreakElement = lineBreakElement;
    }

    public String getLineBreakElement()
    {
        return lineBreakElement;
    }

    private void resetState()
    {
        hasTitle = false;
        authorDateFlag = false;
        verbatimFlag = false;
        externalLinkFlag = false;
        graphicsFileName = null;
        tableHasCaption = false;
        savedOut = null;
        tableRows = null;
        tableHasGrid = false;
    }

    public void head()
    {
        resetState();

        if ( xmlMode )
        {
            markup( "<?xml version=\"1.0\"" );
            if ( encoding != null )
            {
                markup( " encoding=\"" + encoding + "\"" );
            }
            markup( " ?>\n" );

            if ( styleSheet != null )
            {
                markup( "<?xml-stylesheet type=\"text/css\"" + EOL + "href=\"" + styleSheet + "\" ?>" + EOL );
            }
        }

        String pubId;
        markup( "<!DOCTYPE article PUBLIC" );
        if ( publicId == null )
        {
            if ( xmlMode )
            {
                pubId = DEFAULT_XML_PUBLIC_ID;
            }
            else
            {
                pubId = DEFAULT_SGML_PUBLIC_ID;
            }
        }
        else
        {
            pubId = publicId;
        }
        markup( " \"" + pubId + "\"" );
        String sysId = systemId;
        if ( sysId == null && xmlMode )
        {
            sysId = DEFAULT_XML_SYSTEM_ID;
        }
        if ( sysId == null )
        {
            markup( ">" + EOL );
        }
        else
        {
            markup( EOL + "\"" + sysId + "\">" + EOL );
        }

        markup( "<article" );
        if ( lang != null )
        {
            markup( " lang=\"" + lang + "\"" );
        }
        markup( ">" + EOL );
    }

    public void head_()
    {
        if ( hasTitle )
        {
            markup( "</articleinfo>" + EOL );
            hasTitle = false;
        }
    }

    public void title()
    {
        markup( "<articleinfo>" + EOL );

        hasTitle = true;
        markup( "<title>" );
    }

    public void title_()
    {
        markup( "</title>" + EOL );
    }

    public void author()
    {
        authorDateFlag = true;
        markup( "<corpauthor>" );
    }

    public void author_()
    {
        markup( "</corpauthor>" + EOL );
        authorDateFlag = false;
    }

    public void date()
    {
        authorDateFlag = true;
        markup( "<date>" );
    }

    public void date_()
    {
        markup( "</date>" + EOL );
        authorDateFlag = false;
    }

    public void body_()
    {
        markup( "</article>" + EOL );
        out.flush();
        resetState();
    }

    public void section1()
    {
        markup( "<section>" + EOL );
    }

    public void section1_()
    {
        markup( "</section>" + EOL );
    }

    public void section2()
    {
        markup( "<section>" + EOL );
    }

    public void section2_()
    {
        markup( "</section>" + EOL );
    }

    public void section3()
    {
        markup( "<section>" + EOL );
    }

    public void section3_()
    {
        markup( "</section>" + EOL );
    }

    public void section4()
    {
        markup( "<section>" + EOL );
    }

    public void section4_()
    {
        markup( "</section>" + EOL );
    }

    public void section5()
    {
        markup( "<section>" + EOL );
    }

    public void section5_()
    {
        markup( "</section>" + EOL );
    }

    public void sectionTitle()
    {
        markup( "<title>" );
    }

    public void sectionTitle_()
    {
        markup( "</title>" + EOL );
    }

    public void list()
    {
        markup( "<itemizedlist>" + EOL );
    }

    public void list_()
    {
        markup( "</itemizedlist>" + EOL );
    }

    public void listItem()
    {
        markup( "<listitem>" + EOL );
    }

    public void listItem_()
    {
        markup( "</listitem>" + EOL );
    }

    public void numberedList( int numbering )
    {
        String numeration;
        switch ( numbering )
        {
            case NUMBERING_UPPER_ALPHA:
                numeration = "upperalpha";
                break;
            case NUMBERING_LOWER_ALPHA:
                numeration = "loweralpha";
                break;
            case NUMBERING_UPPER_ROMAN:
                numeration = "upperroman";
                break;
            case NUMBERING_LOWER_ROMAN:
                numeration = "lowerroman";
                break;
            case NUMBERING_DECIMAL:
            default:
                numeration = "arabic";
        }
        markup( "<orderedlist numeration=\"" + numeration + "\">" + EOL );
    }

    public void numberedList_()
    {
        markup( "</orderedlist>" + EOL );
    }

    public void numberedListItem()
    {
        markup( "<listitem>" + EOL );
    }

    public void numberedListItem_()
    {
        markup( "</listitem>" + EOL );
    }

    public void definitionList()
    {
        markup( "<variablelist>" + EOL );
    }

    public void definitionList_()
    {
        markup( "</variablelist>" + EOL );
    }

    public void definitionListItem()
    {
        markup( "<varlistentry>" + EOL );
    }

    public void definitionListItem_()
    {
        markup( "</varlistentry>" + EOL );
    }

    public void definedTerm()
    {
        markup( "<term>" );
    }

    public void definedTerm_()
    {
        markup( "</term>" + EOL );
    }

    public void definition()
    {
        markup( "<listitem>" + EOL );
    }

    public void definition_()
    {
        markup( "</listitem>"+ EOL);
    }

    public void paragraph()
    {
        markup( "<para>" );
    }

    public void paragraph_()
    {
        markup( "</para>"  + EOL );
    }

    public void verbatim( boolean boxed )
    {
        verbatimFlag = true;
        markup( "<programlisting>" );
    }

    public void verbatim_()
    {
        markup( "</programlisting>" + EOL );
        verbatimFlag = false;
    }

    public void horizontalRule()
    {
        markup( horizontalRuleElement + EOL );
    }

    public void pageBreak()
    {
        markup( pageBreakElement + EOL );
    }

    public void figure_()
    {
        graphicElement();
    }

    protected void graphicElement()
    {
        if ( graphicsFileName != null )
        {
            String format = FileUtil.fileExtension( graphicsFileName ).toUpperCase();
            if ( format.length() == 0 )
            {
                format = "JPEG";
            }

            markup( "<mediaobject>" + EOL + "<imageobject>" + EOL );
            markup(
                "<imagedata format=\"" + format + "\"" + EOL + "fileref=\"" + escapeSGML( graphicsFileName, xmlMode ) + '\"' );
            if ( xmlMode )
            {
                markup( "/>" + EOL );
            }
            else
            {
                markup( ">" + EOL );
            }
            markup( "</imageobject>" + EOL + "</mediaobject>" + EOL );
            graphicsFileName = null;
        }
    }

    public void figureGraphics( String name )
    {
        graphicsFileName = name + ".jpeg";
    }

    public void figureCaption()
    {
        markup( "<figure>" + EOL );
        markup( "<title>" );
    }

    public void figureCaption_()
    {
        markup( "</title>" + EOL );
        graphicElement();
        markup( "</figure>" + EOL );
    }

    public void table()
    {
        tableHasCaption = false;
    }

    public void table_()
    {
        if ( tableHasCaption )
        {
            tableHasCaption = false;
            // Formal table+title already written to original destination ---

            out.write( tableRows, /*preserveSpace*/ true );
            markup( "</table>" + EOL );
        }
        else
        {
            String frame;
            int sep;
            if ( tableHasGrid )
            {
                frame = "all";
                sep = 1;
            }
            else
            {
                frame = "none";
                sep = 0;
            }

            markup( "<informaltable frame=\"" + frame + "\" rowsep=\"" + sep + "\" colsep=\"" + sep + "\">" + EOL );
            out.write( tableRows, /*preserveSpace*/ true );
            markup( "</informaltable>" + EOL );
        }

        tableRows = null;
        tableHasGrid = false;
    }

    public void tableRows( int[] justification, boolean grid )

    {
        tableHasGrid = grid;

        // Divert output to a string ---
        out.flush();
        savedOut = out;
        out = new LineBreaker( new StringWriter() );

        markup( "<tgroup cols=\"" + justification.length + "\">" + EOL );
        for ( int i = 0; i < justification.length; ++i )
        {
            String justif;
            switch ( justification[i] )
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
            markup( "<colspec align=\"" + justif + "\"" );
            if ( xmlMode )
            {
                markup( "/>" + EOL );
            }
            else
            {
                markup( ">" + EOL );
            }
        }

        markup( "<tbody>" + EOL );
    }

    public void tableRows_()
    {
        markup( "</tbody>" + EOL );
        markup( "</tgroup>" + EOL );

        // Remember diverted output and restore original destination ---
        out.flush();
        tableRows = ( (StringWriter) out.getDestination() ).toString();
        out = savedOut;
    }

    public void tableRow()
    {
        markup( "<row>" + EOL );
    }

    public void tableRow_()
    {
        markup( "</row>" + EOL );
    }

    public void tableCell()
    {
        markup( "<entry><para>" );
    }

    public void tableCell_()
    {
        markup( "</para></entry>" + EOL );
    }

    public void tableCaption()
    {
        tableHasCaption = true;

        String frame;
        int sep;
        if ( tableHasGrid )
        {
            frame = "all";
            sep = 1;
        }
        else
        {
            frame = "none";
            sep = 0;
        }

        markup( "<table frame=\"" + frame + "\" rowsep=\"" + sep + "\" colsep=\"" + sep + "\">" + EOL );
        markup( "<title>" );
    }

    public void tableCaption_()
    {
        markup( "</title>" + EOL );
    }

    public void anchor( String name )
    {
        if ( !authorDateFlag )
        {
            // First char of ID cannot be a digit.
            markup( "<anchor id=\"a." + StructureSink.linkToKey( name ) + "\"" );
            if ( xmlMode )
            {
                markup( "/>" );
            }
            else
            {
                markup( ">" );
            }
        }
    }

    public void link( String name )
    {
        if ( StructureSink.isExternalLink( name ) )
        {
            externalLinkFlag = true;
            markup( "<ulink url=\"" + escapeSGML( name, xmlMode ) + "\">" );
        }
        else
        {
            // First char of ID cannot be a digit.
            markup( "<link linkend=\"a." + StructureSink.linkToKey( name ) + "\">" );
        }
    }

    public void link_()
    {
        if ( externalLinkFlag )
        {
            markup( "</ulink>" );
            externalLinkFlag = false;
        }
        else
        {
            markup( "</link>" );
        }
    }

    public void italic()
    {
        markup( italicBeginTag );
    }

    public void italic_()
    {
        markup( italicEndTag );
    }

    public void bold()
    {
        markup( boldBeginTag );
    }

    public void bold_()
    {
        markup( boldEndTag );
    }

    public void monospaced()
    {
        if ( !authorDateFlag )
        {
            markup( monospacedBeginTag );
        }
    }

    public void monospaced_()
    {
        if ( !authorDateFlag )
        {
            markup( monospacedEndTag );
        }
    }

    public void lineBreak()
    {
        markup( lineBreakElement + EOL );
    }

    public void nonBreakingSpace()
    {
        //markup("&#x00A0;");
        markup( "&nbsp;" );
    }

    public void text( String text )
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

    // -----------------------------------------------------------------------

    protected void markup( String text )
    {
        out.write( text, /*preserveSpace*/ true );
    }

    protected void content( String text )
    {
        out.write( escapeSGML( text, xmlMode ), /*preserveSpace*/ false );
    }

    protected void verbatimContent( String text )
    {
        out.write( escapeSGML( text, xmlMode ), /*preserveSpace*/ true );
    }

    // -----------------------------------------------------------------------

    public void flush()
    {
        out.flush();
    }

    public void close()
    {
        out.close();
    }
}
