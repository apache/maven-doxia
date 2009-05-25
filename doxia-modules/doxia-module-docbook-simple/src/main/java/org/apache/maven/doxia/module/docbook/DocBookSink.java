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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;

import org.apache.maven.doxia.sink.AbstractXmlSink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.util.DoxiaUtils;
import org.apache.maven.doxia.util.HtmlTools;

import org.codehaus.plexus.util.FileUtils;

/**
 * <a href="http://www.oasis-open.org/docbook">Docbook</a> Sink implementation.
 * <br/>
 * It uses the Docbook v4.4 DTD <a href="http://www.oasis-open.org/docbook/sgml/4.4/docbookx.dtd">
 * http://www.oasis-open.org/docbook/sgml/4.4/docbookx.dtd</a>.
 *
 * @version $Id$
 * @since 1.0
 */
public class DocBookSink
    extends AbstractXmlSink
    implements DocbookMarkup, SimplifiedDocbookMarkup
{
    /** DocBook V4.4 SGML public id: "-//OASIS//DTD DocBook V4.4//EN"
     * @deprecated since 1.1, use {@link DocbookMarkup#DEFAULT_SGML_PUBLIC_ID} instead of. */
    public static final String DEFAULT_SGML_PUBLIC_ID = DocbookMarkup.DEFAULT_SGML_PUBLIC_ID;

    /** DocBook XML V4.4 XML public id: "-//OASIS//DTD DocBook XML V4.4//EN"
     * @deprecated since 1.1, use {@link DocbookMarkup#DEFAULT_XML_PUBLIC_ID} instead of. */
    public static final String DEFAULT_XML_PUBLIC_ID = DocbookMarkup.DEFAULT_XML_PUBLIC_ID;

    /** DocBook XML V4.4 XML system id: "http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd"
     * @deprecated since 1.1, use {@link DocbookMarkup#DEFAULT_XML_SYSTEM_ID} instead of. */
    public static final String DEFAULT_XML_SYSTEM_ID = DocbookMarkup.DEFAULT_XML_SYSTEM_ID;

    /** DocBook XML V4.4 SGML system id: "http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd"
     * @deprecated since 1.1, use {@link DocbookMarkup#DEFAULT_SGML_SYSTEM_ID} instead of. */
    public static final String DEFAULT_SGML_SYSTEM_ID = DocbookMarkup.DEFAULT_SGML_SYSTEM_ID;

    /** The output writer. */
    private PrintWriter out;

    /** xmlMode. */
    private boolean xmlMode = false;

    /** styleSheet. */
    private String styleSheet = null;

    /** language. */
    private String lang = null;

    /** publicId. */
    private String publicId = null;

    /** systemId. */
    private String systemId = null;

    /** italicBegin. */
    private String italicBeginTag;

    /** italicEnd. */
    private String italicEndTag;

    /** boldBegin. */
    private String boldBeginTag;

    /** boldEnd. */
    private String boldEndTag;

    /** monospacedBegin. */
    private String monospacedBeginTag;

    /** monospacedEnd. */
    private String monospacedEndTag;

    /** horizontalRule. */
    private String horizontalRuleElement;

    /** pageBreak. */
    private String pageBreakElement;

    /** lineBreak. */
    private String lineBreakElement;

    /** An image source file. */
    private String graphicsFileName;

    /** hasTitle. */
    private boolean hasTitle;

    /** authorDate. */
    private boolean authorDateFlag;

    /** verbatim. */
    private boolean verbatimFlag;

    /** externalLink. */
    private boolean externalLinkFlag;

    /** tableHasCaption. */
    private boolean tableHasCaption;

    /** Used for table rows. */
    private PrintWriter savedOut;

    /** tableRows. */
    private String tableRows;

    /** tableRows writer. */
    private StringWriter tableRowsWriter;

    /** tableHasGrid. */
    private boolean tableHasGrid;

    private boolean skip;

    private String encoding;

    /**
     * Constructor, initialize the Writer.
     *
     * @param writer not null writer to write the result. <b>Should</b> be an UTF-8 Writer.
     * You could use <code>newXmlWriter</code> methods from {@link org.codehaus.plexus.util.WriterFactory}.
     */
    protected DocBookSink( Writer writer )
    {
        this( writer, null );
    }

    /**
     * Constructor, initialize the Writer and tells which encoding is used.
     *
     * @param writer not null writer to write the result.
     * @param encoding the encoding used, that should be written to the generated HTML content
     * if not <code>null</code>.
     */
    protected DocBookSink( Writer writer, String encoding )
    {
        this.out = new PrintWriter( writer );
        this.encoding = encoding;

        setItalicElement( "<emphasis>" );
        setBoldElement( "<emphasis role=\"bold\">" );
        setMonospacedElement( "<literal>" );
        setHorizontalRuleElement( "<!-- HR -->" );
        setPageBreakElement( "<!-- PB -->" );
        setLineBreakElement( "<!-- LB -->" );
    }

    /**
     * Constructor, initialize the Writer and tells which encoding and languageId are used.
     *
     * @param writer not null writer to write the result.
     * @param encoding the encoding used, that should be written to the generated HTML content
     * if not <code>null</code>.
     * @param languageId language identifier for the root element as defined by
     * <a href="ftp://ftp.isi.edu/in-notes/bcp/bcp47.txt">IETF BCP 47</a>, Tags for the Identification of Languages;
     * in addition, the empty string may be specified.
     * @since 1.1
     */
    protected DocBookSink( Writer writer, String encoding, String languageId )
    {
        this( writer, encoding );

        this.lang = languageId;
    }

    /**
     * <p>escapeSGML</p>
     *
     * @param text The text to escape.
     * @param xmlMode xmlMode.
     * @return The escaped text.
     * @deprecated Use HtmlTools#escapeHTML(String,boolean).
     */
    public static final String escapeSGML( String text, boolean xmlMode )
    {
        return HtmlTools.escapeHTML( text, xmlMode );
    }

    /**
     * Sets the xml mode.
     *
     * @param mode the mode to set.
     * @deprecated xml mode is not used.
     */
    public void setXMLMode( boolean mode )
    {
        this.xmlMode = mode;
    }

    /**
     * Returns the current xmlMode.
     *
     * @return the current xmlMode.
     * @deprecated xml mode is not used.
     */
    public boolean isXMLMode()
    {
        return xmlMode;
    }

    /**
     * Sets the encoding. The encoding specified here must be consistent with then encoding
     * used in the Writer used by this DocBookSink instance.
     *
     * @param enc the encoding to set.
     */
    public void setEncoding( String enc )
    {
        encoding = enc;
    }

    /**
     * Returns the encoding.
     *
     * @return the encoding set (can be <code>null</code>).
     */
    public String getEncoding()
    {
        return encoding;
    }

    /**
     * Sets the styleSheet.
     *
     * @param sheet the styleSheet to set.
     */
    public void setStyleSheet( String sheet )
    {
        this.styleSheet = sheet;
    }

    /**
     * Returns the current styleSheet.
     *
     * @return the current styleSheet.
     */
    public String getStyleSheet()
    {
        return styleSheet;
    }

    /**
     * Sets the publicId.
     *
     * @param id the publicId to set.
     */
    public void setPublicId( String id )
    {
        this.publicId = id;
    }

    /**
     * Returns the current publicId.
     *
     * @return the current publicId.
     */
    public String getPublicId()
    {
        return publicId;
    }

    /**
     * Sets the systemId.
     *
     * @param id the systemId to set.
     */
    public void setSystemId( String id )
    {
        this.systemId = id;
    }

    /**
     * Returns the current systemId.
     *
     * @return the current systemId.
     */
    public String getSystemId()
    {
        return systemId;
    }

    /**
     * Sets the language.
     *
     * @param language the language to set.
     */
    public void setLanguage( String language )
    {
        this.lang = language;
    }

    /**
     * Returns the current language.
     *
     * @return the current language.
     */
    public String getLanguage()
    {
        return lang;
    }

    /**
     * Sets the current italicBeginTag and constructs the corresponding end tag from it.
     *
     * @param tag the tag to set. If tag is null, the empty string is used.
     */
    public void setItalicElement( String tag )
    {
        if ( tag == null )
        {
            tag = "";
        }
        this.italicBeginTag = tag;
        italicEndTag = makeEndTag( italicBeginTag );
    }

    /**
     * Constructs the corresponding end tag from the given begin tag.
     *
     * @param beginTag the begin tag to set. If null, the empty string is returned.
     * @return the corresponding end tag.
     */
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

    /**
     * Returns the current italicBeginTag.
     *
     * @return the current italicBeginTag. Defaults to "<emphasis>".
     */
    public String getItalicElement()
    {
        return italicBeginTag;
    }

    /**
     * Sets the current boldBeginTag and constructs the corresponding end tag from it.
     *
     * @param tag the tag to set. If tag is null, the empty string is used.
     */
    public void setBoldElement( String tag )
    {
        if ( tag == null )
        {
            tag = "";
        }
        this.boldBeginTag = tag;
        boldEndTag = makeEndTag( boldBeginTag );
    }

    /**
     * Returns the current boldBeginTag.
     *
     * @return the current boldBeginTag. Defaults to "<emphasis role=\"bold\">".
     */
    public String getBoldElement()
    {
        return boldBeginTag;
    }

    /**
     * Sets the current monospacedBeginTag and constructs the corresponding end tag from it.
     *
     * @param tag the tag to set. If tag is null, the empty string is used.
     */
    public void setMonospacedElement( String tag )
    {
        if ( tag == null )
        {
            tag = "";
        }
        this.monospacedBeginTag = tag;
        monospacedEndTag = makeEndTag( monospacedBeginTag );
    }

    /**
     * Returns the current monospacedBeginTag.
     *
     * @return the current monospacedBeginTag. Defaults to "<literal>>".
     */
    public String getMonospacedElement()
    {
        return monospacedBeginTag;
    }

    /**
     * Sets the current horizontalRuleElement.
     *
     * @param element the element to set.
     */
    public void setHorizontalRuleElement( String element )
    {
        this.horizontalRuleElement = element;
    }

    /**
     * Returns the current horizontalRuleElement.
     *
     * @return the current horizontalRuleElement. Defaults to "<!-- HR -->".
     */
    public String getHorizontalRuleElement()
    {
        return horizontalRuleElement;
    }

    /**
     * Sets the current pageBreakElement.
     *
     * @param element the element to set.
     */
    public void setPageBreakElement( String element )
    {
        this.pageBreakElement = element;
    }

    /**
     * Returns the current pageBreakElement.
     *
     * @return the current pageBreakElement. Defaults to "<!-- PB -->".
     */
    public String getPageBreakElement()
    {
        return pageBreakElement;
    }

    /**
     * Sets the current lineBreakElement.
     *
     * @param element the element to set.
     */
    public void setLineBreakElement( String element )
    {
        this.lineBreakElement = element;
    }

    /**
     * Returns the current lineBreakElement.
     *
     * @return the current lineBreakElement. Defaults to "<!-- LB -->".
     */
    public String getLineBreakElement()
    {
        return lineBreakElement;
    }

    /**
     * Reset all variables.
     */
    protected void resetState()
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
     * @see DocbookMarkup#DEFAULT_XML_PUBLIC_ID
     * @see DocbookMarkup#DEFAULT_SGML_PUBLIC_ID
     * @see DocbookMarkup#DEFAULT_XML_SYSTEM_ID
     * @see DocbookMarkup#ARTICLE_TAG
     */
    public void head()
    {
        resetState();

        MutableAttributeSet att = writeXmlHeader( "article" );

        writeStartTag( SimplifiedDocbookMarkup.ARTICLE_TAG, att );
    }

    /**
     * <p>writeXmlHeader</p>
     *
     * @param root not null
     * @return an attribute set
     * @see DocbookMarkup#DEFAULT_XML_PUBLIC_ID
     * @see DocbookMarkup#DEFAULT_SGML_PUBLIC_ID
     * @see DocbookMarkup#DEFAULT_XML_SYSTEM_ID
     * @see DocbookMarkup#ARTICLE_TAG
     * @since 1.1
     */
    protected MutableAttributeSet writeXmlHeader( String root )
    {
        markup( "<?xml version=\"1.0\"" );
        if ( encoding != null )
        {
            markup( " encoding=\"" + encoding + "\"" );
        }
        markup( "?>" );

        if ( styleSheet != null )
        {
            markup( "<?xml-stylesheet type=\"text/css\" href=\"" + styleSheet + "\" ?>" );
        }

        String pubId;
        markup( "<!DOCTYPE " + root + " PUBLIC" );

        if ( publicId == null )
        {
            pubId = SimplifiedDocbookMarkup.DEFAULT_XML_PUBLIC_ID;
        }
        else
        {
            pubId = publicId;
        }
        markup( " \"" + pubId + "\"" );
        String sysId = systemId;
        if ( sysId == null )
        {
                sysId = SimplifiedDocbookMarkup.DEFAULT_XML_SYSTEM_ID;
        }
        markup( " \"" + sysId + "\">" );

        MutableAttributeSet att = new SimpleAttributeSet();
        if ( lang != null )
        {
            att.addAttribute( LANG_ATTRIBUTE, lang );
        }
        return att;
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ARTICLEINFO_TAG
     */
    public void head_()
    {
        if ( hasTitle )
        {
            writeEndTag( SimplifiedDocbookMarkup.ARTICLEINFO_TAG );
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
        writeStartTag( SimplifiedDocbookMarkup.ARTICLEINFO_TAG );
        writeStartTag( SimplifiedDocbookMarkup.TITLE_TAG );
        hasTitle = true;
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void title_()
    {
        writeEndTag( SimplifiedDocbookMarkup.TITLE_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#CORPAUTHOR_TAG
     */
    public void author()
    {
        authorDateFlag = true;
        writeStartTag( SimplifiedDocbookMarkup.CORPAUTHOR_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#CORPAUTHOR_TAG
     */
    public void author_()
    {
        writeEndTag( SimplifiedDocbookMarkup.CORPAUTHOR_TAG );
        authorDateFlag = false;
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#DATE_TAG
     */
    public void date()
    {
        authorDateFlag = true;
        writeStartTag( SimplifiedDocbookMarkup.DATE_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#DATE_TAG
     */
    public void date_()
    {
        writeEndTag( SimplifiedDocbookMarkup.DATE_TAG );
        authorDateFlag = false;
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ARTICLE_TAG
     */
    public void body_()
    {
        writeEndTag( SimplifiedDocbookMarkup.ARTICLE_TAG );
        out.flush();
        resetState();
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#SECTION_TAG
     */
    public void section1()
    {
        writeStartTag( SimplifiedDocbookMarkup.SECTION_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#SECTION_TAG
     */
    public void section1_()
    {
        writeEndTag( SimplifiedDocbookMarkup.SECTION_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#SECTION_TAG
     */
    public void section2()
    {
        writeStartTag( SimplifiedDocbookMarkup.SECTION_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#SECTION_TAG
     */
    public void section2_()
    {
        writeEndTag( SimplifiedDocbookMarkup.SECTION_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#SECTION_TAG
     */
    public void section3()
    {
        writeStartTag( SimplifiedDocbookMarkup.SECTION_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#SECTION_TAG
     */
    public void section3_()
    {
        writeEndTag( SimplifiedDocbookMarkup.SECTION_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#SECTION_TAG
     */
    public void section4()
    {
        writeStartTag( SimplifiedDocbookMarkup.SECTION_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#SECTION_TAG
     */
    public void section4_()
    {
        writeEndTag( SimplifiedDocbookMarkup.SECTION_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#SECTION_TAG
     */
    public void section5()
    {
        writeStartTag( SimplifiedDocbookMarkup.SECTION_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#SECTION_TAG
     */
    public void section5_()
    {
        writeEndTag( SimplifiedDocbookMarkup.SECTION_TAG );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void sectionTitle()
    {
        writeStartTag( SimplifiedDocbookMarkup.TITLE_TAG );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void sectionTitle_()
    {
        writeEndTag( SimplifiedDocbookMarkup.TITLE_TAG );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void sectionTitle1()
    {
        writeStartTag( SimplifiedDocbookMarkup.TITLE_TAG );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void sectionTitle1_()
    {
        writeEndTag( SimplifiedDocbookMarkup.TITLE_TAG );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void sectionTitle2()
    {
        writeStartTag( SimplifiedDocbookMarkup.TITLE_TAG );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void sectionTitle2_()
    {
        writeEndTag( SimplifiedDocbookMarkup.TITLE_TAG );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void sectionTitle3()
    {
        writeStartTag( SimplifiedDocbookMarkup.TITLE_TAG );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void sectionTitle3_()
    {
        writeEndTag( SimplifiedDocbookMarkup.TITLE_TAG );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void sectionTitle4()
    {
        writeStartTag( SimplifiedDocbookMarkup.TITLE_TAG );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void sectionTitle4_()
    {
        writeEndTag( SimplifiedDocbookMarkup.TITLE_TAG );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void sectionTitle5()
    {
        writeStartTag( SimplifiedDocbookMarkup.TITLE_TAG );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void sectionTitle5_()
    {
        writeEndTag( SimplifiedDocbookMarkup.TITLE_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ITEMIZEDLIST_TAG
     */
    public void list()
    {
        writeStartTag( SimplifiedDocbookMarkup.ITEMIZEDLIST_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ITEMIZEDLIST_TAG
     */
    public void list_()
    {
        writeEndTag( SimplifiedDocbookMarkup.ITEMIZEDLIST_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#LISTITEM_TAG
     */
    public void listItem()
    {
        writeStartTag( SimplifiedDocbookMarkup.LISTITEM_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#LISTITEM_TAG
     */
    public void listItem_()
    {
        writeEndTag( SimplifiedDocbookMarkup.LISTITEM_TAG );
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
                numeration = SimplifiedDocbookMarkup.UPPERALPHA_STYLE;
                break;
            case NUMBERING_LOWER_ALPHA:
                numeration = SimplifiedDocbookMarkup.LOWERALPHA_STYLE;
                break;
            case NUMBERING_UPPER_ROMAN:
                numeration = SimplifiedDocbookMarkup.UPPERROMAN_STYLE;
                break;
            case NUMBERING_LOWER_ROMAN:
                numeration = SimplifiedDocbookMarkup.LOWERROMAN_STYLE;
                break;
            case NUMBERING_DECIMAL:
            default:
                numeration = SimplifiedDocbookMarkup.ARABIC_STYLE;
        }

        MutableAttributeSet att = new SimpleAttributeSet();
        att.addAttribute( SimplifiedDocbookMarkup.NUMERATION_ATTRIBUTE, numeration );

        writeStartTag( SimplifiedDocbookMarkup.ORDEREDLIST_TAG, att );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ORDEREDLIST_TAG
     */
    public void numberedList_()
    {
        writeEndTag( SimplifiedDocbookMarkup.ORDEREDLIST_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#LISTITEM_TAG
     */
    public void numberedListItem()
    {
        writeStartTag( SimplifiedDocbookMarkup.LISTITEM_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#LISTITEM_TAG
     */
    public void numberedListItem_()
    {
        writeEndTag( SimplifiedDocbookMarkup.LISTITEM_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#VARIABLELIST_TAG
     */
    public void definitionList()
    {
        writeStartTag( SimplifiedDocbookMarkup.VARIABLELIST_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#VARIABLELIST_TAG
     */
    public void definitionList_()
    {
        writeEndTag( SimplifiedDocbookMarkup.VARIABLELIST_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#VARLISTENTRY_TAG
     */
    public void definitionListItem()
    {
        writeStartTag( SimplifiedDocbookMarkup.VARLISTENTRY_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#VARLISTENTRY_TAG
     */
    public void definitionListItem_()
    {
        writeEndTag( SimplifiedDocbookMarkup.VARLISTENTRY_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#TERM_TAG
     */
    public void definedTerm()
    {
        writeStartTag( SimplifiedDocbookMarkup.TERM_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#TERM_TAG
     */
    public void definedTerm_()
    {
        writeEndTag( SimplifiedDocbookMarkup.TERM_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#LISTITEM_TAG
     */
    public void definition()
    {
        writeStartTag( SimplifiedDocbookMarkup.LISTITEM_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#LISTITEM_TAG
     */
    public void definition_()
    {
        writeEndTag( SimplifiedDocbookMarkup.LISTITEM_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#PARA_TAG
     */
    public void paragraph()
    {
        writeStartTag( SimplifiedDocbookMarkup.PARA_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#PARA_TAG
     */
    public void paragraph_()
    {
        writeEndTag( SimplifiedDocbookMarkup.PARA_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#PROGRAMLISTING_TAG
     */
    public void verbatim( boolean boxed )
    {
        verbatimFlag = true;
        writeStartTag( SimplifiedDocbookMarkup.PROGRAMLISTING_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#PROGRAMLISTING_TAG
     */
    public void verbatim_()
    {
        writeEndTag( SimplifiedDocbookMarkup.PROGRAMLISTING_TAG );
        verbatimFlag = false;
    }

    /** {@inheritDoc} */
    public void horizontalRule()
    {
        markup( horizontalRuleElement );
    }

    /** {@inheritDoc} */
    public void pageBreak()
    {
        markup( pageBreakElement );
    }

    /** {@inheritDoc} */
    public void figure_()
    {
        graphicElement();
    }

    /**
     * <p>graphicElement</p>
     *
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
            String format = FileUtils.extension( graphicsFileName ).toUpperCase( Locale.ENGLISH );
            if ( format.length() == 0 )
            {
                format = "JPEG";
            }

            writeStartTag( SimplifiedDocbookMarkup.MEDIAOBJECT_TAG );
            writeStartTag( SimplifiedDocbookMarkup.IMAGEOBJECT_TAG );

            MutableAttributeSet att = new SimpleAttributeSet();
            att.addAttribute( SimplifiedDocbookMarkup.FORMAT_ATTRIBUTE, format );
            att.addAttribute( SimplifiedDocbookMarkup.FILEREF_ATTRIBUTE,
                    HtmlTools.escapeHTML( graphicsFileName, true ) );

            writeSimpleTag( SimplifiedDocbookMarkup.IMAGEDATA_TAG, att );

            writeEndTag( SimplifiedDocbookMarkup.IMAGEOBJECT_TAG );
            writeEndTag( SimplifiedDocbookMarkup.MEDIAOBJECT_TAG );
            graphicsFileName = null;
        }
    }

    /** {@inheritDoc} */
    public void figureGraphics( String name )
    {
        // TODO: extension?
        graphicsFileName = name + ".jpeg";
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#FIGURE_TAG
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void figureCaption()
    {
        writeStartTag( SimplifiedDocbookMarkup.FIGURE_TAG );
        writeStartTag( SimplifiedDocbookMarkup.TITLE_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#FIGURE_TAG
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void figureCaption_()
    {
        writeEndTag( SimplifiedDocbookMarkup.TITLE_TAG );
        graphicElement();
        writeEndTag( SimplifiedDocbookMarkup.FIGURE_TAG );
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

            out.write( tableRows  );
            writeEndTag( TABLE_TAG );
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
            att.addAttribute( SimplifiedDocbookMarkup.FRAME_ATTRIBUTE, frame );
            att.addAttribute( SimplifiedDocbookMarkup.ROWSEP_ATTRIBUTE, String.valueOf( sep ) );
            att.addAttribute( SimplifiedDocbookMarkup.COLSEP_ATTRIBUTE, String.valueOf( sep ) );

            writeStartTag( SimplifiedDocbookMarkup.INFORMALTABLE_TAG, att );

            out.write( tableRows  );

            writeEndTag( SimplifiedDocbookMarkup.INFORMALTABLE_TAG );
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
        tableRowsWriter = new StringWriter();
        out = new PrintWriter( tableRowsWriter );

        MutableAttributeSet att = new SimpleAttributeSet();
        att.addAttribute( SimplifiedDocbookMarkup.COLS_ATTRIBUTE, String.valueOf( justification.length ) );

        writeStartTag( SimplifiedDocbookMarkup.TGROUP_TAG, att );

        for ( int i = 0; i < justification.length; ++i )
        {
            String justif;
            switch ( justification[i] )
            {
                case Sink.JUSTIFY_LEFT:
                    justif = "left";
                    break;
                case Sink.JUSTIFY_RIGHT:
                    justif = "right";
                    break;
                case Sink.JUSTIFY_CENTER:
                default:
                    justif = "center";
                    break;
            }

            att = new SimpleAttributeSet();
            att.addAttribute( "align", justif );

            writeSimpleTag( SimplifiedDocbookMarkup.COLSPEC_TAG, att );
        }

        writeStartTag( SimplifiedDocbookMarkup.TBODY_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#TGROUP_TAG
     * @see DocbookMarkup#TBODY_TAG
     */
    public void tableRows_()
    {
        writeEndTag( SimplifiedDocbookMarkup.TBODY_TAG );
        writeEndTag( SimplifiedDocbookMarkup.TGROUP_TAG );

        // Remember diverted output and restore original destination ---
        out.flush();
        if ( tableRowsWriter == null )
        {
            throw new IllegalArgumentException( "tableRows( int[] justification, boolean grid )"
                                                + " was not called before." );
        }

        tableRows = tableRowsWriter.toString();
        tableRowsWriter = null;
        out = savedOut;
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ROW_TAG
     */
    public void tableRow()
    {
        writeStartTag( SimplifiedDocbookMarkup.ROW_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ROW_TAG
     */
    public void tableRow_()
    {
        writeEndTag( SimplifiedDocbookMarkup.ROW_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ENTRY_TAG
     */
    public void tableCell()
    {
        writeStartTag( SimplifiedDocbookMarkup.ENTRY_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ENTRY_TAG
     */
    public void tableCell_()
    {
        writeEndTag( SimplifiedDocbookMarkup.ENTRY_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ENTRY_TAG
     */
    public void tableHeaderCell()
    {
        writeStartTag( SimplifiedDocbookMarkup.ENTRY_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ENTRY_TAG
     */
    public void tableHeaderCell_()
    {
        writeEndTag( SimplifiedDocbookMarkup.ENTRY_TAG );
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
        att.addAttribute( SimplifiedDocbookMarkup.FRAME_ATTRIBUTE, frame );
        att.addAttribute( SimplifiedDocbookMarkup.ROWSEP_ATTRIBUTE, String.valueOf( sep ) );
        att.addAttribute( SimplifiedDocbookMarkup.COLSEP_ATTRIBUTE, String.valueOf( sep ) );

        writeStartTag( TABLE_TAG, att );

        writeStartTag( SimplifiedDocbookMarkup.TITLE_TAG );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TITLE
     */
    public void tableCaption_()
    {
        writeEndTag( SimplifiedDocbookMarkup.TITLE_TAG );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ANCHOR_TAG
     */
    public void anchor( String name )
    {
        if ( name == null )
        {
            throw new NullPointerException( "Anchor name cannot be null!" );
        }

        if ( authorDateFlag )
        {
            return;
        }

        String id = name;

        if ( !DoxiaUtils.isValidId( id ) )
        {
            id = DoxiaUtils.encodeId( name, true );

            getLog().warn( "Modified invalid anchor name: " + name );
        }

        MutableAttributeSet att = new SimpleAttributeSet();
        att.addAttribute( ID_ATTRIBUTE, id );

        writeSimpleTag( SimplifiedDocbookMarkup.ANCHOR_TAG, att );
    }

    /**
     * {@inheritDoc}
     * @see DocbookMarkup#ANCHOR_TAG
     */
    public void anchor_()
    {
        // nop
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
        if ( name == null )
        {
            throw new NullPointerException( "Link name cannot be null!" );
        }

        if ( DoxiaUtils.isExternalLink( name ) )
        {
            externalLinkFlag = true;
            MutableAttributeSet att = new SimpleAttributeSet();
            att.addAttribute( SimplifiedDocbookMarkup.URL_ATTRIBUTE, HtmlTools.escapeHTML( name, true ) );

            writeStartTag( SimplifiedDocbookMarkup.ULINK_TAG, att );
        }
        else
        {
            MutableAttributeSet att = new SimpleAttributeSet();
            att.addAttribute( SimplifiedDocbookMarkup.LINKEND_ATTRIBUTE, HtmlTools.escapeHTML( name ) );

            writeStartTag( SimplifiedDocbookMarkup.LINK_TAG, att );
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
            writeEndTag( SimplifiedDocbookMarkup.ULINK_TAG );
            externalLinkFlag = false;
        }
        else
        {
            writeEndTag( SimplifiedDocbookMarkup.LINK_TAG );
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
        markup( lineBreakElement );
    }

    /** {@inheritDoc} */
    public void nonBreakingSpace()
    {
        markup( "&#x00A0;" );
        //markup( "&nbsp;" );
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

    /** {@inheritDoc} */
    public void comment( String comment )
    {
        StringBuffer buffer = new StringBuffer( comment.length() + 9 );

        buffer.append( LESS_THAN ).append( BANG ).append( MINUS ).append( MINUS ).append( SPACE );

        buffer.append( comment );

        buffer.append( SPACE ).append( MINUS ).append( MINUS ).append( GREATER_THAN );

        markup( buffer.toString() );
    }

    /**
     * {@inheritDoc}
     *
     * Unkown events just log a warning message but are ignored otherwise.
     * @see org.apache.maven.doxia.sink.Sink#unknown(String,Object[],SinkEventAttributes)
     */
    public void unknown( String name, Object[] requiredParams, SinkEventAttributes attributes )
    {
        getLog().warn( "Unknown Sink event in DocBookSink: " + name + ", ignoring!" );
    }

    // -----------------------------------------------------------------------

    /**
     * Write text to output, preserving white space.
     *
     * @param text The text to write.
     */
    protected void markup( String text )
    {
        if ( !skip )
        {
            out.write( text );
        }
    }

    /**
     * Write SGML escaped text to output, not preserving white space.
     *
     * @param text The text to write.
     */
    protected void content( String text )
    {
        if ( !skip )
        {
            out.write( HtmlTools.escapeHTML( text, true ) );
        }
    }

    /**
     * Write SGML escaped text to output, preserving white space.
     *
     * @param text The text to write.
     */
    protected void verbatimContent( String text )
    {
        if ( !skip )
        {
            out.write( HtmlTools.escapeHTML( text, true ) );
        }
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
        markup( unifyEOLs( text ) );
    }

    /**
     * <p>Setter for the field <code>skip</code>.</p>
     *
     * @param skip the skip to set.
     * @since 1.1
     */
    public void setSkip( boolean skip )
    {
        this.skip = skip;
    }
}
