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
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

/**
 * Parse a <code>Docbook</code> document and emit events into the specified doxia
 * Sink.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @since 1.0
 * @plexus.component role-hint="doc-book"
 */
public class DocBookParser
    extends AbstractXmlParser
    implements DocbookMarkup
{
    /**
     * Level counter for calculating the section level.
     */
    private int level = 0;

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
    private static final Collection hierElements = new HashSet();

    /**
     * The list of DocBook elements that will be rendered verbatim
     */
    private static final Collection verbatimElements = new HashSet();

    /**
     * The list of DocBook elements that will be rendered inline and bold
     */
    private static final Collection boldElements = new HashSet();

    /**
     * The list of DocBook elements that will be rendered inline and italic
     */
    private static final Collection italicElements = new HashSet();

    /**
     * The list of DocBook elements that will be rendered inline and monospace
     */
    private static final Collection monospaceElements = new HashSet();

    static
    {
        DocBookParser.hierElements.add( "set" );
        DocBookParser.hierElements.add( "book" );
        DocBookParser.hierElements.add( "part" );
        DocBookParser.hierElements.add( "chapter" );
        DocBookParser.hierElements.add( "section" );
        DocBookParser.hierElements.add( "sect1" );
        DocBookParser.hierElements.add( "sect2" );
        DocBookParser.hierElements.add( "sect3" );
        DocBookParser.hierElements.add( "sect4" );
        DocBookParser.hierElements.add( "sect5" );
        DocBookParser.hierElements.add( "article" );
        DocBookParser.hierElements.add( "preface" );
        DocBookParser.hierElements.add( "partintro" );
        DocBookParser.hierElements.add( "appendix" );
        DocBookParser.hierElements.add( "bibliography" );
        DocBookParser.hierElements.add( "reference" );
        DocBookParser.hierElements.add( "bibliography" );
        DocBookParser.hierElements.add( "bibliodiv" );
        DocBookParser.hierElements.add( "glossary" );
        DocBookParser.hierElements.add( "refentry" );
        DocBookParser.hierElements.add( "refnamediv" );
        DocBookParser.hierElements.add( "refsection" );
        DocBookParser.hierElements.add( "refsect1" );
        DocBookParser.hierElements.add( "refsect2" );
        DocBookParser.hierElements.add( "refsect3" );

        DocBookParser.verbatimElements.add( "programlisting" );
        DocBookParser.verbatimElements.add( "screen" );
        DocBookParser.verbatimElements.add( "literallayout" );
        DocBookParser.verbatimElements.add( "synopsis" );

        DocBookParser.boldElements.add( "command" );
        DocBookParser.boldElements.add( "keycap" );
        DocBookParser.boldElements.add( "shortcut" );
        DocBookParser.boldElements.add( "userinput" );

        DocBookParser.italicElements.add( "parameter" );
        DocBookParser.italicElements.add( "replaceable" );
        DocBookParser.italicElements.add( "medialabel" );
        DocBookParser.italicElements.add( "structfield" );
        DocBookParser.italicElements.add( "systemitem" );
        DocBookParser.italicElements.add( "citetitle" );
        DocBookParser.italicElements.add( "emphasis" );
        DocBookParser.italicElements.add( "foreignphrase" );
        DocBookParser.italicElements.add( "wordasword" );

        DocBookParser.monospaceElements.add( "classname" );
        DocBookParser.monospaceElements.add( "exceptionname" );
        DocBookParser.monospaceElements.add( "interfacename" );
        DocBookParser.monospaceElements.add( "methodname" );
        DocBookParser.monospaceElements.add( "computeroutput" );
        DocBookParser.monospaceElements.add( "constant" );
        DocBookParser.monospaceElements.add( "envar" );
        DocBookParser.monospaceElements.add( "function" );
        DocBookParser.monospaceElements.add( "parameter" );
        DocBookParser.monospaceElements.add( "replaceable" );
        DocBookParser.monospaceElements.add( "literal" );
        DocBookParser.monospaceElements.add( "code" );
        DocBookParser.monospaceElements.add( "option" );
        DocBookParser.monospaceElements.add( "prompt" );
        DocBookParser.monospaceElements.add( "structfield" );
        DocBookParser.monospaceElements.add( "systemitem" );
        DocBookParser.monospaceElements.add( "structfield" );
        DocBookParser.monospaceElements.add( "userinput" );
        DocBookParser.monospaceElements.add( "varname" );
        DocBookParser.monospaceElements.add( "sgmltag" );
        DocBookParser.monospaceElements.add( "tag" );//DocBook 5
        DocBookParser.monospaceElements.add( "uri" );
        DocBookParser.monospaceElements.add( "filename" );
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
        }

        //If the element introduces a new level of hierarchy, raise the stack
        if ( hierElements.contains( parser.getName() ) )
        {
            //increase the nesting level
            level++;
            //if this is the root element, handle it as body
            if ( level == 1 )
            {
                sink.body();
            }
            else if ( level == 2 )
            {
                sink.section1();
            }
            else if ( level == 3 )
            {
                sink.section2();
            }
            else if ( level == 4 )
            {
                sink.section3();
            }
            else if ( level == 5 )
            {
                sink.section4();
            }
            else if ( level == 6 )
            {
                sink.section5();
            }
        }
        //Match all *info-Elements for metainformation, but only consider the root element
        else if ( ( parser.getName().endsWith( INFO_TAG.toString() ) ) && ( level == 1 ) )
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
        else if ( ( parser.getName().equals( PARA_TAG.toString() ) || parser.getName().equals( SIMPARA_TAG.toString() ) )
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
        else if ( DocBookParser.verbatimElements.contains( parser.getName() ) )
        {
            sink.verbatim( true );
        }

        else if ( DocBookParser.boldElements.contains( parser.getName() ) &&
            DocBookParser.monospaceElements.contains( parser.getName() ) )
        {
            sink.bold();
            sink.monospaced();
        }
        else if ( DocBookParser.italicElements.contains( parser.getName() ) &&
            DocBookParser.monospaceElements.contains( parser.getName() ) )
        {
            sink.italic();
            sink.monospaced();
        }
        else if ( DocBookParser.boldElements.contains( parser.getName() ) )
        {
            sink.bold();
        }
        else if ( DocBookParser.italicElements.contains( parser.getName() ) )
        {
            sink.italic();
        }
        else if ( DocBookParser.monospaceElements.contains( parser.getName() ) )
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
            else if ( level == 1 )
            {
                sink.title();
            }
            else if ( level == 2 )
            {
                sink.sectionTitle1();
            }
            else if ( level == 3 )
            {
                sink.sectionTitle2();
            }
            else if ( level == 4 )
            {
                sink.sectionTitle3();
            }
            else if ( level == 5 )
            {
                sink.sectionTitle4();
            }
            else if ( level == 6 )
            {
                sink.sectionTitle5();
            }
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
                sink.text( "Link" );//TODO: determine text of link target
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
        if ( hierElements.contains( parser.getName() ) )
        {
            //increase the nesting level
            level--;
            //if this is the root element, handle it as body
            if ( level == 1 )
            {
                sink.body_();
            }
            else if ( level == 2 )
            {
                sink.section1_();
            }
            else if ( level == 3 )
            {
                sink.section2_();
            }
            else if ( level == 4 )
            {
                sink.section3_();
            }
            else if ( level == 5 )
            {
                sink.section4_();
            }
            else if ( level == 6 )
            {
                sink.section5_();
            }
        }
        //Match all *info-Elements for metainformation, but only consider the root element
        else if ( parser.getName().endsWith( INFO_TAG.toString() ) && level == 1 )
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
        else if ( ( parser.getName().equals( PARA_TAG.toString() ) || parser.getName().equals( SIMPARA_TAG.toString() ) )
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
            sink.text( ". " );//Inline Running head
            sink.bold_();
        }
        else if ( DocBookParser.verbatimElements.contains( parser.getName() ) )
        {
            sink.verbatim_();
        }
        else if ( DocBookParser.boldElements.contains( parser.getName() ) &&
            DocBookParser.monospaceElements.contains( parser.getName() ) )
        {
            sink.bold_();
            sink.monospaced_();
        }
        else if ( DocBookParser.italicElements.contains( parser.getName() ) &&
            DocBookParser.monospaceElements.contains( parser.getName() ) )
        {
            sink.italic_();
            sink.monospaced_();
        }
        else if ( DocBookParser.boldElements.contains( parser.getName() ) )
        {
            sink.bold_();
        }
        else if ( DocBookParser.italicElements.contains( parser.getName() ) )
        {
            sink.italic_();
        }
        else if ( DocBookParser.monospaceElements.contains( parser.getName() ) )
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
            else if ( level == 1 )
            {
                sink.title_();
            }
            else if ( level == 2 )
            {
                sink.sectionTitle1_();
            }
            else if ( level == 3 )
            {
                sink.sectionTitle2_();
            }
            else if ( level == 4 )
            {
                sink.sectionTitle3_();
            }
            else if ( level == 5 )
            {
                sink.sectionTitle4_();
            }
            else if ( level == 6 )
            {
                sink.sectionTitle5_();
            }
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
        sink.text( parser.getText() );
    }


    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

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

    private boolean isParent( String element )
    {
        if ( parent.size() > 0 )
        {
            return parent.peek().equals( element );
        }

        return false;
    }
}
