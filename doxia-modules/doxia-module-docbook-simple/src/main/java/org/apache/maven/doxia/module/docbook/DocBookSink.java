package org.apache.maven.doxia.module.docbook;

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

import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.AbstractXmlSink;
import org.apache.maven.doxia.sink.StructureSink;
import org.apache.maven.doxia.util.HtmlTools;
import org.apache.maven.doxia.util.LineBreaker;
import org.codehaus.plexus.util.FileUtils;

import java.io.StringWriter;
import java.io.Writer;
import java.util.StringTokenizer;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

/**
 * A doxia Sink which produces a <code>Docbook</code> model.
 *
 * @version $Id$
 * @since 1.0
 */
public class DocBookSink
    extends AbstractXmlSink
    implements DocbookMarkup
{
    /** DocBook V4.1 SGML public id: "-//OASIS//DTD DocBook V4.1//EN" */
    public static final String DEFAULT_SGML_PUBLIC_ID = "-//OASIS//DTD DocBook V4.1//EN";

    /** DocBook XML V4.1.2 XML public id: "-//OASIS//DTD DocBook XML V4.1.2//EN" */
    public static final String DEFAULT_XML_PUBLIC_ID = "-//OASIS//DTD DocBook XML V4.1.2//EN";

    /** DocBook XML V4.0 XML system id: "http://www.oasis-open.org/docbook/xml/4.0/docbookx.dtd" */
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

    /**
     * @param out the default writer
     */
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

    /**
     * TODO Try to replace by {@link HtmlTools#escapeHTML(String)}
     *
     * @param text
     * @param xmlMode
     * @return
     */
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

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     * @see #DEFAULT_XML_PUBLIC_ID
     * @see #DEFAULT_SGML_PUBLIC_ID
     * @see #DEFAULT_XML_SYSTEM_ID
     * @see DocbookMarkup#ARTICLE_TAG
     */
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

        MutableAttributeSet att = new SimpleAttributeSet();
        if ( lang != null )
        {
            att.addAttribute( Attribute.LANG, lang );
        }

        writeStartTag( ARTICLE_TAG, att );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ARTICLEINFO_TAG
     */
    public void head_()
    {
        if ( hasTitle )
        {
            writeEndTag( ARTICLEINFO_TAG );
            hasTitle = false;
        }
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ARTICLEINFO_TAG
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void title()
    {
        writeStartTag( ARTICLEINFO_TAG );

        hasTitle = true;
        writeStartTag( Tag.TITLE );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void title_()
    {
        writeEndTag( Tag.TITLE );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#CORPAUTHOR_TAG
     */
    public void author()
    {
        authorDateFlag = true;
        writeStartTag( CORPAUTHOR_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#CORPAUTHOR_TAG
     */
    public void author_()
    {
        writeEndTag( CORPAUTHOR_TAG );
        authorDateFlag = false;
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#DATE_TAG
     */
    public void date()
    {
        authorDateFlag = true;
        writeStartTag( DATE_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#DATE_TAG
     */
    public void date_()
    {
        writeEndTag( DATE_TAG );
        authorDateFlag = false;
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ARTICLE_TAG
     */
    public void body_()
    {
        writeEndTag( ARTICLE_TAG );
        out.flush();
        resetState();
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#SECTION_TAG
     */
    public void section1()
    {
        writeStartTag( SECTION_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#SECTION_TAG
     */
    public void section1_()
    {
        writeEndTag( SECTION_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#SECTION_TAG
     */
    public void section2()
    {
        writeStartTag( SECTION_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#SECTION_TAG
     */
    public void section2_()
    {
        writeEndTag( SECTION_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#SECTION_TAG
     */
    public void section3()
    {
        writeStartTag( SECTION_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#SECTION_TAG
     */
    public void section3_()
    {
        writeEndTag( SECTION_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#SECTION_TAG
     */
    public void section4()
    {
        writeStartTag( SECTION_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#SECTION_TAG
     */
    public void section4_()
    {
        writeEndTag( SECTION_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#SECTION_TAG
     */
    public void section5()
    {
        writeStartTag( SECTION_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#SECTION_TAG
     */
    public void section5_()
    {
        writeEndTag( SECTION_TAG );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void sectionTitle()
    {
        writeStartTag( Tag.TITLE );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void sectionTitle_()
    {
        writeEndTag( Tag.TITLE );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ITEMIZEDLIST_TAG
     */
    public void list()
    {
        writeStartTag( ITEMIZEDLIST_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ITEMIZEDLIST_TAG
     */
    public void list_()
    {
        writeEndTag( ITEMIZEDLIST_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#LISTITEM_TAG
     */
    public void listItem()
    {
        writeStartTag( LISTITEM_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#LISTITEM_TAG
     */
    public void listItem_()
    {
        writeEndTag( LISTITEM_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ORDEREDLIST_TAG
     * @see DocbookMarkup#NUMERATION_ATTRIBUTE
     */
    public void numberedList( int numbering )
    {
        String numeration;
        switch ( numbering )
        {
            case NUMBERING_UPPER_ALPHA:
                numeration = UPPERALPHA_STYLE;
                break;
            case NUMBERING_LOWER_ALPHA:
                numeration = LOWERALPHA_STYLE;
                break;
            case NUMBERING_UPPER_ROMAN:
                numeration = UPPERROMAN_STYLE;
                break;
            case NUMBERING_LOWER_ROMAN:
                numeration = LOWERROMAN_STYLE;
                break;
            case NUMBERING_DECIMAL:
            default:
                numeration = ARABIC_STYLE;
        }

        MutableAttributeSet att = new SimpleAttributeSet();
        att.addAttribute( NUMERATION_ATTRIBUTE, numeration );

        writeStartTag( ORDEREDLIST_TAG, att );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ORDEREDLIST_TAG
     */
    public void numberedList_()
    {
        writeEndTag( ORDEREDLIST_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#LISTITEM_TAG
     */
    public void numberedListItem()
    {
        writeStartTag( LISTITEM_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#LISTITEM_TAG
     */
    public void numberedListItem_()
    {
        writeEndTag( LISTITEM_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#VARIABLELIST_TAG
     */
    public void definitionList()
    {
        writeStartTag( VARIABLELIST_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#VARIABLELIST_TAG
     */
    public void definitionList_()
    {
        writeEndTag( VARIABLELIST_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#VARLISTENTRY_TAG
     */
    public void definitionListItem()
    {
        writeStartTag( VARLISTENTRY_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#VARLISTENTRY_TAG
     */
    public void definitionListItem_()
    {
        writeEndTag( VARLISTENTRY_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#TERM_TAG
     */
    public void definedTerm()
    {
        writeStartTag( TERM_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#TERM_TAG
     */
    public void definedTerm_()
    {
        writeEndTag( TERM_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#LISTITEM_TAG
     */
    public void definition()
    {
        writeStartTag( LISTITEM_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#LISTITEM_TAG
     */
    public void definition_()
    {
        writeEndTag( LISTITEM_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#PARA_TAG
     */
    public void paragraph()
    {
        writeStartTag( PARA_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#PARA_TAG
     */
    public void paragraph_()
    {
        writeEndTag( PARA_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#PROGRAMLISTING_TAG
     */
    public void verbatim( boolean boxed )
    {
        verbatimFlag = true;
        writeStartTag( PROGRAMLISTING_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#PROGRAMLISTING_TAG
     */
    public void verbatim_()
    {
        writeEndTag( PROGRAMLISTING_TAG );
        verbatimFlag = false;
    }

    /** {@inheritDoc} */
    public void horizontalRule()
    {
        markup( horizontalRuleElement + EOL );
    }

    /** {@inheritDoc} */
    public void pageBreak()
    {
        markup( pageBreakElement + EOL );
    }

    /** {@inheritDoc} */
    public void figure_()
    {
        graphicElement();
    }

    /**
     * @see DocbookMarkup#MEDIAOBJECT_TAG
     * @see DocbookMarkup#IMAGEOBJECT_TAG
     * @see DocbookMarkup#IMAGEDATA_TAG
     * @see DocbookMarkup#FORMAT_ATTRIBUTE
     * @see DocbookMarkup#FILEREF_ATTRIBUTE
     */
    protected void graphicElement()
    {
        if ( graphicsFileName != null )
        {
            String format = FileUtils.extension( graphicsFileName ).toUpperCase();
            if ( format.length() == 0 )
            {
                format = "JPEG";
            }

            writeStartTag( MEDIAOBJECT_TAG );
            writeStartTag( IMAGEOBJECT_TAG );

            MutableAttributeSet att = new SimpleAttributeSet();
            att.addAttribute( FORMAT_ATTRIBUTE, format );
            att.addAttribute( FILEREF_ATTRIBUTE, escapeSGML( graphicsFileName, xmlMode ) );

            if ( xmlMode )
            {
                writeSimpleTag( IMAGEDATA_TAG, att );
            }
            else
            {
                writeStartTag( IMAGEDATA_TAG, att );
            }

            writeEndTag( IMAGEOBJECT_TAG );
            writeEndTag( MEDIAOBJECT_TAG );
            graphicsFileName = null;
        }
    }

    /** {@inheritDoc} */
    public void figureGraphics( String name )
    {
        graphicsFileName = name + ".jpeg";
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#FIGURE_TAG
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void figureCaption()
    {
        writeStartTag( FIGURE_TAG );
        writeStartTag( Tag.TITLE );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#FIGURE_TAG
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void figureCaption_()
    {
        writeEndTag( Tag.TITLE );
        graphicElement();
        writeEndTag( FIGURE_TAG );
    }

    /** {@inheritDoc} */
    public void table()
    {
        tableHasCaption = false;
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#INFORMALTABLE_TAG
     * @see DocbookMarkup#FRAME_ATTRIBUTE
     * @see DocbookMarkup#ROWSEP_ATTRIBUTE
     * @see DocbookMarkup#COLSEP_ATTRIBUTE
     * @see javax.swing.text.html.HTML.Tag#TABLE
     */
    public void table_()
    {
        if ( tableHasCaption )
        {
            tableHasCaption = false;
            // Formal table+title already written to original destination ---

            out.write( tableRows, /*preserveSpace*/ true );
            writeEndTag( Tag.TABLE );
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

            MutableAttributeSet att = new SimpleAttributeSet();
            att.addAttribute( FRAME_ATTRIBUTE, frame );
            att.addAttribute( ROWSEP_ATTRIBUTE, String.valueOf( sep ) );
            att.addAttribute( COLSEP_ATTRIBUTE, String.valueOf( sep )  );

            writeStartTag( INFORMALTABLE_TAG, att );

            out.write( tableRows, /*preserveSpace*/ true );

            writeEndTag( INFORMALTABLE_TAG );
        }

        tableRows = null;
        tableHasGrid = false;
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#TGROUP_TAG
     * @see DocbookMarkup#COLS_ATTRIBUTE
     * @see DocbookMarkup#COLSPEC_TAG
     */
    public void tableRows( int[] justification, boolean grid )
    {
        tableHasGrid = grid;

        // Divert output to a string ---
        out.flush();
        savedOut = out;
        out = new LineBreaker( new StringWriter() );

        MutableAttributeSet att = new SimpleAttributeSet();
        att.addAttribute( COLS_ATTRIBUTE, String.valueOf( justification.length ) );

        writeStartTag( TGROUP_TAG, att );

        for ( int i = 0; i < justification.length; ++i )
        {
            String justif;
            switch ( justification[i] )
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

            att = new SimpleAttributeSet();
            att.addAttribute( Attribute.ALIGN.toString(), justif );

            if ( xmlMode )
            {
                writeSimpleTag( COLSPEC_TAG, att );
            }
            else
            {
                writeStartTag( COLSPEC_TAG, att );
            }
        }

        writeStartTag( TBODY_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#TGROUP_TAG
     * @see DocbookMarkup#TBODY_TAG
     */
    public void tableRows_()
    {
        writeEndTag( TBODY_TAG );
        writeEndTag( TGROUP_TAG );

        // Remember diverted output and restore original destination ---
        out.flush();
        tableRows = ( (StringWriter) out.getDestination() ).toString();
        out = savedOut;
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ROW_TAG
     */
    public void tableRow()
    {
        writeStartTag( ROW_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ROW_TAG
     */
    public void tableRow_()
    {
        writeEndTag( ROW_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ENTRY_TAG
     * @see DocbookMarkup#PARA_TAG
     */
    public void tableCell()
    {
        writeStartTag( ENTRY_TAG );
        writeStartTag( PARA_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ENTRY_TAG
     * @see DocbookMarkup#PARA_TAG
     */
    public void tableCell_()
    {
        writeEndTag( PARA_TAG );
        writeEndTag( ENTRY_TAG );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TABLE
     * @see DocbookMarkup#FRAME_ATTRIBUTE
     * @see DocbookMarkup#ROWSEP_ATTRIBUTE
     * @see DocbookMarkup#COLSEP_ATTRIBUTE
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
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

        MutableAttributeSet att = new SimpleAttributeSet();
        att.addAttribute( FRAME_ATTRIBUTE, frame );
        att.addAttribute( ROWSEP_ATTRIBUTE, String.valueOf( sep ) );
        att.addAttribute( COLSEP_ATTRIBUTE, String.valueOf( sep ) );

        writeStartTag( Tag.TABLE, att );

        writeStartTag( Tag.TITLE );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void tableCaption_()
    {
        writeEndTag( Tag.TITLE );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ANCHOR_TAG
     */
    public void anchor( String name )
    {
        if ( !authorDateFlag )
        {
            // First char of ID cannot be a digit.
            MutableAttributeSet att = new SimpleAttributeSet();
            att.addAttribute( Attribute.ID, "a." + StructureSink.linkToKey( name ) );

            if ( xmlMode )
            {
                writeSimpleTag( ANCHOR_TAG, att );
            }
            else
            {
                writeStartTag( ANCHOR_TAG, att );
            }
        }
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ULINK_TAG
     * @see DocbookMarkup#URL_ATTRIBUTE
     * @see DocbookMarkup#LINK_TAG
     * @see DocbookMarkup#LINKEND_ATTRIBUTE
     */
    public void link( String name )
    {
        if ( StructureSink.isExternalLink( name ) )
        {
            externalLinkFlag = true;
            MutableAttributeSet att = new SimpleAttributeSet();
            att.addAttribute( URL_ATTRIBUTE, escapeSGML( name, xmlMode ) );

            writeStartTag( ULINK_TAG, att );
        }
        else
        {
            // First char of ID cannot be a digit.
            MutableAttributeSet att = new SimpleAttributeSet();
            att.addAttribute( LINKEND_ATTRIBUTE, "a." + StructureSink.linkToKey( name ) );

            writeStartTag( LINK_TAG, att );
        }
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ULINK_TAG
     * @see DocbookMarkup#LINK_TAG
     */
    public void link_()
    {
        if ( externalLinkFlag )
        {
            writeEndTag( ULINK_TAG );
            externalLinkFlag = false;
        }
        else
        {
            writeEndTag( LINK_TAG );
        }
    }

    /** {@inheritDoc} */
    public void italic()
    {
        markup( italicBeginTag );
    }

    /** {@inheritDoc} */
    public void italic_()
    {
        markup( italicEndTag );
    }

    /** {@inheritDoc} */
    public void bold()
    {
        markup( boldBeginTag );
    }

    /** {@inheritDoc} */
    public void bold_()
    {
        markup( boldEndTag );
    }

    /** {@inheritDoc} */
    public void monospaced()
    {
        if ( !authorDateFlag )
        {
            markup( monospacedBeginTag );
        }
    }

    /** {@inheritDoc} */
    public void monospaced_()
    {
        if ( !authorDateFlag )
        {
            markup( monospacedEndTag );
        }
    }

    /** {@inheritDoc} */
    public void lineBreak()
    {
        markup( lineBreakElement + EOL );
    }

    /** {@inheritDoc} */
    public void nonBreakingSpace()
    {
        //markup("&#x00A0;");
        markup( "&nbsp;" );
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public void flush()
    {
        out.flush();
    }

    /** {@inheritDoc} */
    public void close()
    {
        out.close();
    }

    /** {@inheritDoc} */
    protected void write( String text )
    {
        markup( text );
    }
}
