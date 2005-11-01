package org.codehaus.doxia.module.docbook;

import org.codehaus.doxia.parser.ParseException;
import org.codehaus.doxia.parser.Parser;
import org.codehaus.doxia.sink.Sink;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

/**
 * Parse a DocBook document and emit events into the specified doxia
 * Sink.
 *
 * @plexus.component
 *   role="org.codehaus.doxia.parser.Parser"
 *   role-hint="doc-book"
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DocBookParser
    implements Parser
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
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void parse( Reader reader, Sink sink )
        throws ParseException
    {
        try
        {
            XmlPullParser parser = new MXParser();

            parser.setInput( reader );

            parseDocBook( parser, sink );
        }
        catch ( XmlPullParserException e )
        {
            throw new ParseException( "Error parsing the model.", e );
        }
        catch ( IOException e )
        {
            throw new ParseException( "Error parsing the model.", e );
        }
    }

    public void parseDocBook( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, IOException
    {
        int eventType = parser.getEventType();

        while ( eventType != XmlPullParser.END_DOCUMENT )
        {

            if ( eventType == XmlPullParser.START_TAG )
            {
                String id = getAttributeValue( parser, "id" );
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
                else if ( ( parser.getName().endsWith( "info" ) ) && ( level == 1 ) )
                {
                    sink.head();
                    parent.push( parser.getName() );
                }
                //handle lists
                else if ( parser.getName().equals( "itemizedlist" ) )
                {
                    sink.list();
                    //for itemizedlists in variablelists
                    parent.push( parser.getName() );
                }
                else if ( parser.getName().equals( "orderedlist" ) )
                {
                    //default enumeration style is decimal
                    int numeration = Sink.NUMBERING_DECIMAL;
                    String style = getAttributeValue( parser, "numeration" );
                    if ( style.equals( "arabic" ) )
                    {
                        numeration = Sink.NUMBERING_DECIMAL;
                    }
                    else if ( style.equals( "loweralpha" ) )
                    {
                        numeration = Sink.NUMBERING_LOWER_ALPHA;
                    }
                    else if ( style.equals( "lowerroman" ) )
                    {
                        numeration = Sink.NUMBERING_LOWER_ROMAN;
                    }
                    else if ( style.equals( "upperalpha" ) )
                    {
                        numeration = Sink.NUMBERING_UPPER_ALPHA;
                    }
                    else if ( style.equals( "upperroman" ) )
                    {
                        numeration = Sink.NUMBERING_UPPER_ROMAN;
                    }
                    sink.numberedList( numeration );
                    parent.push( parser.getName() );
                }
                else if ( parser.getName().equals( "listitem" ) )
                {
                    if ( isParent( "variablelist" ) )
                    {
                        sink.definition();
                    }
                    else
                    {
                        sink.listItem();
                    }
                }
                else if ( parser.getName().equals( "variablelist" ) )
                {
                    sink.definitionList();
                    parent.push( parser.getName() );
                }
                else if ( parser.getName().equals( "varlistentry" ) )
                {
                    sink.definitionListItem();
                }
                else if ( parser.getName().equals( "term" ) )
                {
                    sink.definedTerm();
                }
                //handle figures
                else if ( parser.getName().equals( "figure" ) || parser.getName().equals( "informalfigure" ) )
                {
                    sink.figure();
                    parent.push( parser.getName() );
                }
                else if ( parser.getName().equals( "imageobject" ) )
                {
                    String fileref = getAttributeValue( parser, "fileref" );
                    if ( fileref != null )
                    {
                        sink.figureGraphics( fileref );
                        parent.push( parser.getName() );
                    }
                }
                else if ( parser.getName().equals( "caption" ) && isParent( "figure" ) )
                {
                    sink.figureCaption();
                }
                else if ( parser.getName().equals( "table" ) || parser.getName().equals( "informaltable" ) )
                {
                    sink.table();
                    //TODO handle tgroups
                    parent.push( parser.getName() );
                }
                else if ( parser.getName().equals( "thead" ) )
                {
                    parent.push( parser.getName() );
                }
                else if ( parser.getName().equals( "tr" ) || parser.getName().equals( "row" ) )
                {
                    sink.tableRow();
                }
                else if ( parser.getName().equals( "entry" ) &&
                          isParent( "thead" ) ||
                          parser.getName().equals( "th" ) )
                {
                    sink.tableHeaderCell();
                }
                else if ( parser.getName().equals( "entry" ) )
                {
                    sink.tableCell();
                }
                else if ( parser.getName().equals( "caption" ) && ( isParent( "informaltable" ) ||
                          isParent( "table" ) ) )
                {
                    sink.tableCaption();
                }

                else if ( ( parser.getName().equals( "para" ) || parser.getName().equals( "simpara" ) ) &&
                          !isParent( "formalpara" ) )
                {
                    sink.paragraph();
                }
                else if ( parser.getName().equals( "formalpara" ) )
                {
                    parent.push( parser.getName() );
                    sink.paragraph();
                }
                else if ( parser.getName().equals( "title" ) && isParent( "formalpara" ) )
                {
                    sink.bold();
                }
                else if ( DocBookParser.verbatimElements.contains( parser.getName() ) )
                {
                    sink.verbatim( true );
                }

                else if ( DocBookParser.boldElements.contains(
                    parser.getName() ) && DocBookParser.monospaceElements.contains( parser.getName() ) )
                {
                    sink.bold();
                    sink.monospaced();
                }
                else if ( DocBookParser.italicElements.contains(
                    parser.getName() ) && DocBookParser.monospaceElements.contains( parser.getName() ) )
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

                else if ( parser.getName().equals( "title" ) )
                {
                    if ( parser.getName().equals( "figure" ) || parser.getName().equals( "informalfigure" ) )
                    {
                        sink.figureCaption();
                    }
                    else if ( parser.getName().equals( "table" ) || parser.getName().equals( "informaltable" ) )
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
                else if ( parser.getName().equals( "ulink" ) )
                {
                    String url = getAttributeValue( parser, "url" );
                    if ( url != null )
                    {
                        parent.push( parser.getName() );
                        sink.link( url );
                    }
                }
                else if ( parser.getName().equals( "email" ) )
                {
                    sink.link( "mailto:" + parser.nextText() );
                    sink.link_();
                }
                else if ( parser.getName().equals( "link" ) )
                {
                    String linkend = getAttributeValue( parser, "linkend" );
                    if ( linkend != null )
                    {
                        parent.push( parser.getName() );
                        sink.link( "#" + linkend );
                    }
                }
                else if ( parser.getName().equals( "xref" ) )
                {
                    String linkend = getAttributeValue( parser, "linkend" );
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
            else if ( eventType == XmlPullParser.END_TAG )
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
                else if ( parser.getName().endsWith( "info" ) && level == 1 )
                {
                    sink.head_();
                    parent.pop();
                }
                //handle lists
                else if ( parser.getName().equals( "itemizedlist" ) )
                {
                    sink.list_();
                    parent.pop();
                }
                else if ( parser.getName().equals( "orderedlist" ) )
                {
                    sink.numberedList_();
                    parent.pop();
                }
                else if ( parser.getName().equals( "listitem" ) )
                {
                    if ( isParent( "variablelist" ) )
                    {
                        sink.definition_();
                    }
                    else
                    {
                        sink.listItem_();
                    }
                }
                else if ( parser.getName().equals( "variablelist" ) )
                {
                    sink.definitionList_();
                }
                else if ( parser.getName().equals( "varlistentry" ) )
                {
                    sink.definitionListItem_();
                }
                else if ( parser.getName().equals( "term" ) )
                {
                    sink.definedTerm_();
                }
                //handle figures
                else if ( parser.getName().equals( "figure" ) || parser.getName().equals( "informalfigure" ) )
                {
                    sink.figure_();
                    parent.pop();
                }
                else if ( parser.getName().equals( "caption" ) && isParent( "figure" ) )
                {
                    sink.figureCaption_();
                }
                else if ( parser.getName().equals( "table" ) || parser.getName().equals( "informaltable" ) )
                {
                    sink.table_();
                    //TODO handle tgroups
                    parent.pop();
                }
                else if ( parser.getName().equals( "thead" ) )
                {
                    parent.pop();
                }
                else if ( parser.getName().equals( "tr" ) || parser.getName().equals( "row" ) )
                {
                    sink.tableRow_();
                }
                else if ( parser.getName().equals( "entry" ) &&  isParent( "thead" ) ||
                          parser.getName().equals( "th" ) )
                {
                    sink.tableHeaderCell_();
                }
                else if ( parser.getName().equals( "entry" ) )
                {
                    sink.tableCell_();
                }
                else if ( parser.getName().equals( "caption" ) &&
                        ( isParent( "informaltable" ) || isParent( "table" ) ) )
                {
                    sink.tableCaption_();
                }
                else if ( ( parser.getName().equals( "para" ) || parser.getName().equals( "simpara" ) ) &&
                          !isParent( "formalpara" ) )
                {
                    sink.paragraph_();
                }
                else if ( parser.getName().equals( "formalpara" ) )
                {
                    parent.pop();
                    sink.paragraph_();
                }
                else if ( parser.getName().equals( "title" ) && isParent( "formalpara" ) )
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

                else if ( parser.getName().equals( "title" ) )
                {
                    if ( parser.getName().equals( "figure" ) || parser.getName().equals( "informalfigure" ) )
                    {
                        sink.figureCaption_();
                    }
                    else if ( parser.getName().equals( "table" ) || parser.getName().equals( "informaltable" ) )
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
                else if ( parser.getName().equals( "ulink" ) || parser.getName().equals( "link" ) )
                {
                    if ( isParent( parser.getName() ) )
                    {
                        parent.pop();
                        sink.link_();
                    }
                }
            }
            else if ( eventType == XmlPullParser.TEXT )
            {
                sink.text( parser.getText() );
            }

            eventType = parser.next();
        }

        // TODO: This should go through some monitor
        if ( !failedElements.isEmpty() )
        {
            System.out.println( "Doxia was unable to handle following elements" );
            for ( Iterator i = failedElements.iterator(); i.hasNext(); )
            {
                System.out.print( i.next().toString() + " " );
            }

            System.out.println();
        }
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
