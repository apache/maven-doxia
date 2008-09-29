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

import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.parser.AbstractXmlParser;
import org.apache.maven.doxia.sink.Sink;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

/**
 * Parse a <a href="http://www.docbook.org/schemas/simplified"><code>Simplified DocBook</code></a> document
 * and emit events into the specified doxia Sink.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @since 1.0
 * @plexus.component role="org.apache.maven.doxia.parser.Parser" role-hint="docbook"
 */
public class DocBookParser
    extends AbstractXmlParser
    implements DocbookMarkup
{
    /**
     * Level counter for calculating the section level.
     */
    private int level = -1;

    /**
     * Used to distinguish italic from bold.
     */
    private boolean isBold;

    /**
     * A selective stack of parent elements
     */
    private Stack parent = new Stack();

    /**
     * The list of elements the parse has been unable to handle
     */
    private Collection failedElements = new HashSet();

    /**
     * The list of DocBook elements that introduce a new level of
     * hierarchy.
     */
    private static final Collection HIER_ELEMENTS = new HashSet();

    /**
     * The list of DocBook elements that will be rendered verbatim
     */
    private static final Collection VERBATIM_ELEMENTS = new HashSet();

    /**
     * The list of DocBook elements that will be rendered inline and bold
     */
    private static final Collection BOLD_ELEMENTS = new HashSet();

    /**
     * The list of DocBook elements that will be rendered inline and italic
     */
    private static final Collection ITALIC_ELEMENTS = new HashSet();

    /**
     * The list of DocBook elements that will be rendered inline and monospace
     */
    private static final Collection MONOSPACE_ELEMENTS = new HashSet();

    static
    {
        DocBookParser.HIER_ELEMENTS.add( "set" );
        DocBookParser.HIER_ELEMENTS.add( "book" );
        DocBookParser.HIER_ELEMENTS.add( "part" );
        DocBookParser.HIER_ELEMENTS.add( "chapter" );
        DocBookParser.HIER_ELEMENTS.add( "section" );
        DocBookParser.HIER_ELEMENTS.add( "sect1" );
        DocBookParser.HIER_ELEMENTS.add( "sect2" );
        DocBookParser.HIER_ELEMENTS.add( "sect3" );
        DocBookParser.HIER_ELEMENTS.add( "sect4" );
        DocBookParser.HIER_ELEMENTS.add( "sect5" );
        DocBookParser.HIER_ELEMENTS.add( "article" );
        DocBookParser.HIER_ELEMENTS.add( "preface" );
        DocBookParser.HIER_ELEMENTS.add( "partintro" );
        DocBookParser.HIER_ELEMENTS.add( "appendix" );
        DocBookParser.HIER_ELEMENTS.add( "bibliography" );
        DocBookParser.HIER_ELEMENTS.add( "reference" );
        DocBookParser.HIER_ELEMENTS.add( "bibliography" );
        DocBookParser.HIER_ELEMENTS.add( "bibliodiv" );
        DocBookParser.HIER_ELEMENTS.add( "glossary" );
        DocBookParser.HIER_ELEMENTS.add( "refentry" );
        DocBookParser.HIER_ELEMENTS.add( "refnamediv" );
        DocBookParser.HIER_ELEMENTS.add( "refsection" );
        DocBookParser.HIER_ELEMENTS.add( "refsect1" );
        DocBookParser.HIER_ELEMENTS.add( "refsect2" );
        DocBookParser.HIER_ELEMENTS.add( "refsect3" );

        DocBookParser.VERBATIM_ELEMENTS.add( "programlisting" );
        DocBookParser.VERBATIM_ELEMENTS.add( "screen" );
        DocBookParser.VERBATIM_ELEMENTS.add( "literallayout" );
        DocBookParser.VERBATIM_ELEMENTS.add( "synopsis" );

        DocBookParser.BOLD_ELEMENTS.add( "command" );
        DocBookParser.BOLD_ELEMENTS.add( "keycap" );
        DocBookParser.BOLD_ELEMENTS.add( "shortcut" );
        DocBookParser.BOLD_ELEMENTS.add( "userinput" );

        DocBookParser.ITALIC_ELEMENTS.add( "parameter" );
        DocBookParser.ITALIC_ELEMENTS.add( "replaceable" );
        DocBookParser.ITALIC_ELEMENTS.add( "medialabel" );
        DocBookParser.ITALIC_ELEMENTS.add( "structfield" );
        DocBookParser.ITALIC_ELEMENTS.add( "systemitem" );
        DocBookParser.ITALIC_ELEMENTS.add( "citetitle" );
        DocBookParser.ITALIC_ELEMENTS.add( "emphasis" );
        DocBookParser.ITALIC_ELEMENTS.add( "foreignphrase" );
        DocBookParser.ITALIC_ELEMENTS.add( "wordasword" );

        DocBookParser.MONOSPACE_ELEMENTS.add( "classname" );
        DocBookParser.MONOSPACE_ELEMENTS.add( "exceptionname" );
        DocBookParser.MONOSPACE_ELEMENTS.add( "interfacename" );
        DocBookParser.MONOSPACE_ELEMENTS.add( "methodname" );
        DocBookParser.MONOSPACE_ELEMENTS.add( "computeroutput" );
        DocBookParser.MONOSPACE_ELEMENTS.add( "constant" );
        DocBookParser.MONOSPACE_ELEMENTS.add( "envar" );
        DocBookParser.MONOSPACE_ELEMENTS.add( "function" );
        DocBookParser.MONOSPACE_ELEMENTS.add( "parameter" );
        DocBookParser.MONOSPACE_ELEMENTS.add( "replaceable" );
        DocBookParser.MONOSPACE_ELEMENTS.add( "literal" );
        DocBookParser.MONOSPACE_ELEMENTS.add( "code" );
        DocBookParser.MONOSPACE_ELEMENTS.add( "option" );
        DocBookParser.MONOSPACE_ELEMENTS.add( "prompt" );
        DocBookParser.MONOSPACE_ELEMENTS.add( "structfield" );
        DocBookParser.MONOSPACE_ELEMENTS.add( "systemitem" );
        DocBookParser.MONOSPACE_ELEMENTS.add( "structfield" );
        DocBookParser.MONOSPACE_ELEMENTS.add( "userinput" );
        DocBookParser.MONOSPACE_ELEMENTS.add( "varname" );
        DocBookParser.MONOSPACE_ELEMENTS.add( "sgmltag" );
        DocBookParser.MONOSPACE_ELEMENTS.add( "tag" ); //DocBook 5
        DocBookParser.MONOSPACE_ELEMENTS.add( "uri" );
        DocBookParser.MONOSPACE_ELEMENTS.add( "filename" );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

//    public void parseDocBook( XmlPullParser parser, Sink sink )
//        throws XmlPullParserException, IOException
//    {
//
//        // TODO: This should go through some monitor
//        if ( !failedElements.isEmpty() )
//        {
//            System.out.println( "Doxia was unable to handle following elements" );
//            for ( Iterator i = failedElements.iterator(); i.hasNext(); )
//            {
//                System.out.print( i.next().toString() + " " );
//            }
//
//            System.out.println();
//        }
//    }

    /** {@inheritDoc} */
    protected void handleStartTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        String id = getAttributeValue( parser, Attribute.ID.toString() );
        //catch link targets
        if ( id != null )
        {
            sink.anchor( id );
            sink.anchor_();
        }

        //If the element introduces a new level of hierarchy, raise the stack
        if ( HIER_ELEMENTS.contains( parser.getName() ) )
        {
            //increase the nesting level
            level++;
            //if this is the root element, handle it as body
            if ( level == 0 )
            {
                sink.body();
            }
            else if ( level == Sink.SECTION_LEVEL_1 )
            {
                sink.section1();
            }
            else if ( level == Sink.SECTION_LEVEL_2 )
            {
                sink.section2();
            }
            else if ( level == Sink.SECTION_LEVEL_3 )
            {
                sink.section3();
            }
            else if ( level == Sink.SECTION_LEVEL_4 )
            {
                sink.section4();
            }
            else if ( level == Sink.SECTION_LEVEL_5 )
            {
                sink.section5();
            }
        }
        //Match all *info-Elements for metainformation, but only consider the root element
        else if ( ( parser.getName().endsWith( INFO_TAG.toString() ) ) && ( level == 0 ) )
        {
            sink.head();
            parent.push( parser.getName() );
        }
        //handle lists
        else if ( parser.getName().equals( ITEMIZEDLIST_TAG.toString() ) )
        {
            sink.list();
            //for itemizedlists in variablelists
            parent.push( parser.getName() );
        }
        else if ( parser.getName().equals( ORDEREDLIST_TAG.toString() ) )
        {
            //default enumeration style is decimal
            int numeration = Sink.NUMBERING_DECIMAL;
            String style = getAttributeValue( parser, NUMERATION_ATTRIBUTE );
            if ( style.equals( ARABIC_STYLE ) )
            {
                numeration = Sink.NUMBERING_DECIMAL;
            }
            else if ( style.equals( LOWERALPHA_STYLE ) )
            {
                numeration = Sink.NUMBERING_LOWER_ALPHA;
            }
            else if ( style.equals( LOWERROMAN_STYLE ) )
            {
                numeration = Sink.NUMBERING_LOWER_ROMAN;
            }
            else if ( style.equals( UPPERALPHA_STYLE ) )
            {
                numeration = Sink.NUMBERING_UPPER_ALPHA;
            }
            else if ( style.equals( UPPERROMAN_STYLE ) )
            {
                numeration = Sink.NUMBERING_UPPER_ROMAN;
            }
            sink.numberedList( numeration );
            parent.push( parser.getName() );
        }
        else if ( parser.getName().equals( LISTITEM_TAG.toString() ) )
        {
            if ( isParent( VARIABLELIST_TAG.toString() ) )
            {
                sink.definition();
            }
            else if ( isParent( ORDEREDLIST_TAG.toString() ) )
            {
                sink.numberedListItem();
            }
            else
            {
                sink.listItem();
            }
        }
        else if ( parser.getName().equals( VARIABLELIST_TAG.toString() ) )
        {
            sink.definitionList();
            parent.push( parser.getName() );
        }
        else if ( parser.getName().equals( VARLISTENTRY_TAG.toString() ) )
        {
            sink.definitionListItem();
        }
        else if ( parser.getName().equals( TERM_TAG.toString() ) )
        {
            sink.definedTerm();
        }
        //handle figures
        else if ( parser.getName().equals( FIGURE_TAG.toString() )
            || parser.getName().equals( INFORMALFIGURE_TAG.toString() ) )
        {
            sink.figure();
            parent.push( parser.getName() );
        }
        else if ( parser.getName().equals( IMAGEOBJECT_TAG.toString() ) )
        {
            String fileref = getAttributeValue( parser, FILEREF_ATTRIBUTE );
            if ( fileref != null )
            {
                sink.figureGraphics( fileref );
                parent.push( parser.getName() );
            }
        }
        else if ( parser.getName().equals( Tag.CAPTION.toString() ) && isParent( FIGURE_TAG.toString() ) )
        {
            sink.figureCaption();
        }
        else if ( parser.getName().equals( Tag.TABLE.toString() )
            || parser.getName().equals( INFORMALTABLE_TAG.toString() ) )
        {
            sink.table();
            //TODO handle tgroups
            parent.push( parser.getName() );
        }
        else if ( parser.getName().equals( THEAD_TAG.toString() ) )
        {
            parent.push( parser.getName() );
        }
        else if ( parser.getName().equals( Tag.TR.toString() ) || parser.getName().equals( ROW_TAG.toString() ) )
        {
            sink.tableRow();
        }
        else if ( parser.getName().equals( ENTRY_TAG.toString() ) && isParent( THEAD_TAG.toString() )
            || parser.getName().equals( Tag.TH.toString() ) )
        {
            sink.tableHeaderCell();
        }
        else if ( parser.getName().equals( ENTRY_TAG.toString() ) )
        {
            sink.tableCell();
        }
        else
        if ( parser.getName().equals( Tag.CAPTION.toString() )
            && ( isParent( INFORMALTABLE_TAG.toString() ) || isParent( Tag.TABLE.toString() ) ) )
        {
            sink.tableCaption();
        }
        else if ( ( parser.getName().equals( PARA_TAG.toString() )
            || parser.getName().equals( SIMPARA_TAG.toString() ) )
            && !isParent( FORMALPARA_TAG.toString() ) )
        {
            sink.paragraph();
        }
        else if ( parser.getName().equals( FORMALPARA_TAG.toString() ) )
        {
            parent.push( parser.getName() );
            sink.paragraph();
        }
        else if ( parser.getName().equals( Tag.TITLE.toString() ) && isParent( FORMALPARA_TAG.toString() ) )
        {
            sink.bold();
        }
        else if ( DocBookParser.VERBATIM_ELEMENTS.contains( parser.getName() ) )
        {
            sink.verbatim( true );
        }

        else if ( DocBookParser.BOLD_ELEMENTS.contains( parser.getName() )
            && DocBookParser.MONOSPACE_ELEMENTS.contains( parser.getName() ) )
        {
            sink.bold();
            sink.monospaced();
        }
        else if ( DocBookParser.ITALIC_ELEMENTS.contains( parser.getName() )
            && DocBookParser.MONOSPACE_ELEMENTS.contains( parser.getName() ) )
        {
            sink.italic();
            sink.monospaced();
        }
        else if ( DocBookParser.BOLD_ELEMENTS.contains( parser.getName() ) )
        {
            sink.bold();
        }
        else if ( DocBookParser.ITALIC_ELEMENTS.contains( parser.getName() )
            && "bold".equals( parser.getAttributeValue( null, "role" ) ) )
        {
            sink.bold();

            isBold = true;
        }
        else if ( DocBookParser.ITALIC_ELEMENTS.contains( parser.getName() ) )
        {
            sink.italic();
        }
        else if ( DocBookParser.MONOSPACE_ELEMENTS.contains( parser.getName() ) )
        {
            sink.monospaced();
        }

        else if ( parser.getName().equals( Tag.TITLE.toString() ) )
        {
            if ( parser.getName().equals( FIGURE_TAG.toString() )
                || parser.getName().equals( INFORMALFIGURE_TAG.toString() ) )
            {
                sink.figureCaption();
            }
            else if ( parser.getName().equals( Tag.TABLE.toString() )
                || parser.getName().equals( INFORMALTABLE_TAG.toString() ) )
            {
                sink.tableCaption();
            }
            else if ( level == 0 )
            {
                sink.title();
            }
            else if ( level == Sink.SECTION_LEVEL_1 )
            {
                sink.sectionTitle1();
            }
            else if ( level == Sink.SECTION_LEVEL_2 )
            {
                sink.sectionTitle2();
            }
            else if ( level == Sink.SECTION_LEVEL_3 )
            {
                sink.sectionTitle3();
            }
            else if ( level == Sink.SECTION_LEVEL_4 )
            {
                sink.sectionTitle4();
            }
            else if ( level == Sink.SECTION_LEVEL_5 )
            {
                sink.sectionTitle5();
            }
        }
        else if ( parser.getName().equals( CORPAUTHOR_TAG.toString() ) )
        {
            sink.author();
        }
        else if ( parser.getName().equals( DATE_TAG.toString() ) )
        {
            sink.date();
        }
        else if ( parser.getName().equals( ULINK_TAG.toString() ) )
        {
            String url = getAttributeValue( parser, URL_TAG.toString() );
            if ( url != null )
            {
                parent.push( parser.getName() );
                sink.link( url );
            }
        }
        else if ( parser.getName().equals( EMAIL_TAG.toString() ) )
        {
            String mailto;
            try
            {
                mailto = parser.nextText();
            }
            catch ( IOException e )
            {
                throw new XmlPullParserException( "IOException: " + e.getMessage(), parser, e );
            }
            sink.link( "mailto:" + mailto );
            sink.link_();
        }
        else if ( parser.getName().equals( LINK_TAG.toString() ) )
        {
            String linkend = getAttributeValue( parser, LINKEND_ATTRIBUTE );
            if ( linkend != null )
            {
                parent.push( parser.getName() );
                sink.link( "#" + linkend );
            }
        }
        else if ( parser.getName().equals( XREF_TAG.toString() ) )
        {
            String linkend = getAttributeValue( parser, LINKEND_ATTRIBUTE );
            if ( linkend != null )
            {
                sink.link( "#" + linkend );
                sink.text( "Link" ); //TODO: determine text of link target
                sink.link_();
            }
        }
        else
        {
            failedElements.add( parser.getName() );
        }
    }

    /** {@inheritDoc} */
    protected void handleEndTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {

        //If the element introduces a new level of hierarchy, lower the stack
        if ( HIER_ELEMENTS.contains( parser.getName() ) )
        {
            //if this is the root element, handle it as body
            if ( level == 0 )
            {
                sink.body_();
            }
            else if ( level == Sink.SECTION_LEVEL_1 )
            {
                sink.section1_();
            }
            else if ( level == Sink.SECTION_LEVEL_2 )
            {
                sink.section2_();
            }
            else if ( level == Sink.SECTION_LEVEL_3 )
            {
                sink.section3_();
            }
            else if ( level == Sink.SECTION_LEVEL_4 )
            {
                sink.section4_();
            }
            else if ( level == Sink.SECTION_LEVEL_5 )
            {
                sink.section5_();
            }
            //decrease the nesting level
            level--;
        }
        //Match all *info-Elements for metainformation, but only consider the root element
        else if ( parser.getName().endsWith( INFO_TAG.toString() ) && level == 0 )
        {
            sink.head_();
            parent.pop();
        }
        //handle lists
        else if ( parser.getName().equals( ITEMIZEDLIST_TAG.toString() ) )
        {
            sink.list_();
            parent.pop();
        }
        else if ( parser.getName().equals( ORDEREDLIST_TAG.toString() ) )
        {
            sink.numberedList_();
            parent.pop();
        }
        else if ( parser.getName().equals( LISTITEM_TAG.toString() ) )
        {
            if ( isParent( VARIABLELIST_TAG.toString() ) )
            {
                sink.definition_();
            }
            else if ( isParent( ORDEREDLIST_TAG.toString() ) )
            {
                sink.numberedListItem_();
            }
            else
            {
                sink.listItem_();
            }
        }
        else if ( parser.getName().equals( VARIABLELIST_TAG.toString() ) )
        {
            sink.definitionList_();
        }
        else if ( parser.getName().equals( VARLISTENTRY_TAG.toString() ) )
        {
            sink.definitionListItem_();
        }
        else if ( parser.getName().equals( TERM_TAG.toString() ) )
        {
            sink.definedTerm_();
        }
        //handle figures
        else if ( parser.getName().equals( FIGURE_TAG.toString() )
            || parser.getName().equals( INFORMALFIGURE_TAG.toString() ) )
        {
            sink.figure_();
            parent.pop();
        }
        else if ( parser.getName().equals( Tag.CAPTION.toString() ) && isParent( FIGURE_TAG.toString() ) )
        {
            sink.figureCaption_();
        }
        else if ( parser.getName().equals( Tag.TABLE.toString() )
            || parser.getName().equals( INFORMALTABLE_TAG.toString() ) )
        {
            sink.table_();
            //TODO handle tgroups
            parent.pop();
        }
        else if ( parser.getName().equals( THEAD_TAG.toString() ) )
        {
            parent.pop();
        }
        else if ( parser.getName().equals( Tag.TR.toString() ) || parser.getName().equals( ROW_TAG.toString() ) )
        {
            sink.tableRow_();
        }
        else if ( parser.getName().equals( ENTRY_TAG.toString() ) && isParent( THEAD_TAG.toString() )
            || parser.getName().equals( Tag.TH.toString() ) )
        {
            sink.tableHeaderCell_();
        }
        else if ( parser.getName().equals( ENTRY_TAG.toString() ) )
        {
            sink.tableCell_();
        }
        else
        if ( parser.getName().equals( Tag.CAPTION.toString() )
            && ( isParent( INFORMALTABLE_TAG.toString() ) || isParent( Tag.TABLE.toString() ) ) )
        {
            sink.tableCaption_();
        }
        else if ( ( parser.getName().equals( PARA_TAG.toString() )
            || parser.getName().equals( SIMPARA_TAG.toString() ) )
            && !isParent( FORMALPARA_TAG.toString() ) )
        {
            sink.paragraph_();
        }
        else if ( parser.getName().equals( FORMALPARA_TAG.toString() ) )
        {
            parent.pop();
            sink.paragraph_();
        }
        else if ( parser.getName().equals( Tag.TITLE.toString() ) && isParent( FORMALPARA_TAG.toString() ) )
        {
            sink.text( ". " ); //Inline Running head
            sink.bold_();
        }
        else if ( DocBookParser.VERBATIM_ELEMENTS.contains( parser.getName() ) )
        {
            sink.verbatim_();
        }
        else if ( DocBookParser.BOLD_ELEMENTS.contains( parser.getName() )
            && DocBookParser.MONOSPACE_ELEMENTS.contains( parser.getName() ) )
        {
            sink.bold_();
            sink.monospaced_();
        }
        else if ( DocBookParser.ITALIC_ELEMENTS.contains( parser.getName() )
            && DocBookParser.MONOSPACE_ELEMENTS.contains( parser.getName() ) )
        {
            sink.italic_();
            sink.monospaced_();
        }
        else if ( DocBookParser.BOLD_ELEMENTS.contains( parser.getName() ) )
        {
            sink.bold_();
        }
        else if ( DocBookParser.ITALIC_ELEMENTS.contains( parser.getName() ) )
        {
            if ( isBold )
            {
                sink.bold_();

                isBold = false;
            }
            else
            {
                sink.italic_();
            }
        }
        else if ( DocBookParser.MONOSPACE_ELEMENTS.contains( parser.getName() ) )
        {
            sink.monospaced_();
        }

        else if ( parser.getName().equals( Tag.TITLE.toString() ) )
        {
            if ( parser.getName().equals( FIGURE_TAG.toString() )
                || parser.getName().equals( INFORMALFIGURE_TAG.toString() ) )
            {
                sink.figureCaption_();
            }
            else if ( parser.getName().equals( Tag.TABLE.toString() )
                || parser.getName().equals( INFORMALTABLE_TAG.toString() ) )
            {
                sink.tableCaption_();
            }
            else if ( level == 0 )
            {
                sink.title_();
            }
            else if ( level == Sink.SECTION_LEVEL_1 )
            {
                sink.sectionTitle1_();
            }
            else if ( level == Sink.SECTION_LEVEL_2 )
            {
                sink.sectionTitle2_();
            }
            else if ( level == Sink.SECTION_LEVEL_3 )
            {
                sink.sectionTitle3_();
            }
            else if ( level == Sink.SECTION_LEVEL_4 )
            {
                sink.sectionTitle4_();
            }
            else if ( level == Sink.SECTION_LEVEL_5 )
            {
                sink.sectionTitle5_();
            }
        }
        else if ( parser.getName().equals( CORPAUTHOR_TAG.toString() ) )
        {
            sink.author_();
        }
        else if ( parser.getName().equals( DATE_TAG.toString() ) )
        {
            sink.date_();
        }
        else if ( parser.getName().equals( ULINK_TAG.toString() ) || parser.getName().equals( LINK_TAG.toString() ) )
        {
            if ( isParent( parser.getName() ) )
            {
                parent.pop();
                sink.link_();
            }
        }
    }

    /** {@inheritDoc} */
    protected void handleText( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = parser.getText();

        /*
         * NOTE: Don't do any whitespace trimming here. Whitespace normalization has already been performed by the
         * parser so any whitespace that makes it here is significant.
         */
        if ( StringUtils.isNotEmpty( text ) )
        {
            // Emit separate text events for different lines, e.g. the input
            // "\nLine1\n\nLine2\n\n" should deliver the event sequence "\n", "Line1\n", "\n", "Line2\n", "\n".
            // In other words, the concatenation of the text events must deliver the input sequence.
            // (according to section 2.11 of the XML spec, parsers must normalize line breaks to "\n")
            String[] lines = text.split( "\n", -1 );

            for ( int i = 0; i < lines.length - 1; i++ )
            {
                sink.text( lines[i] + EOL );
            }

            if ( lines[lines.length - 1].length() > 0 )
            {
                sink.text( lines[lines.length - 1] );
            }
        }
    }

    /** {@inheritDoc} */
    protected void handleCdsect( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = parser.getText();

        sink.rawText( text );
    }

    /** {@inheritDoc} */
    protected void handleComment( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = parser.getText();

        if ( "PB".equals( text.trim() ) )
        {
            sink.pageBreak();
        }
        else if ( "HR".equals( text.trim() ) )
        {
            sink.horizontalRule();
        }
        else if ( "LB".equals( text.trim() ) )
        {
            sink.lineBreak();
        }
        else
        {
            sink.comment( text.trim() );
        }
    }

    /** {@inheritDoc} */
    protected void handleEntity( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = parser.getText();

        int[] holder = new int[] {0, 0};
        char[] chars = parser.getTextCharacters( holder );
        String textChars = String.valueOf( chars, holder[0], holder[1] );

        if ( "#x00A0".equals( textChars ) )
        {
            sink.nonBreakingSpace();
        }
        else
        {
            sink.text( text );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Returns the value of the given attribute.
     *
     * @param parser the parser to scan.
     * @param name the attribute name.
     * @return the attribute value.
     */
    private String getAttributeValue( XmlPullParser parser, String name )
    {
        for ( int i = 0; i < parser.getAttributeCount(); i++ )
        {
            if ( parser.getAttributeName( i ).equals( name ) )
            {
                return parser.getAttributeValue( i );
            }
        }

        return null;
    }

    /**
     * Determines if the given element is a parent element.
     *
     * @param element the element to determine.
     * @return true if the given element is a parent element.
     */
    private boolean isParent( String element )
    {
        if ( parent.size() > 0 )
        {
            return parent.peek().equals( element );
        }

        return false;
    }
}
