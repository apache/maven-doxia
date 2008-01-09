package org.apache.maven.doxia.module.xwiki;

import org.apache.maven.doxia.sink.AbstractTextSink;
import org.codehaus.plexus.util.StringUtils;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Stack;

/**
 * Doxia Sink for <a href="http://xwiki.org">XWiki</a>.
 *
 * @author Vincent Massol
 */
public class XWikiWikiSink
    extends AbstractTextSink
    implements XWikiMarkup
{
    /**
     * The writer to use.
     */
    private PrintWriter writer;

    /**
     * listNestingIndent.
     */
    private int listNestingIndent = 0;

    /**
     * listStyles.
     */
    private Stack listStyles;

    private boolean shouldIgnore = false;

    private boolean shouldAddEOL = false;

    private String linkName;

    private boolean isInList = false;

    /**
     * Constructor, initialize the variables.
     *
     * @param writer The writer to write the result.
     */
    public XWikiWikiSink( Writer writer )
    {
        this.writer = new PrintWriter( writer );
        this.listStyles = new Stack();
    }

    /**
     * {@inheritDoc}
     */
    public void head()
    {
        // Ignore since there's no notion of head in XWiki markup
        this.shouldIgnore = true;
    }

    /**
     * {@inheritDoc}
     */
    public void head_()
    {
        // Reset ignore so that it's not carried forward for other events
        this.shouldIgnore = false;
    }

    /**
     * {@inheritDoc}
     */
    public void title()
    {
        // Ignore since there's no notion of title in XWiki markup
        this.shouldIgnore = true;
    }

    /**
     * {@inheritDoc}
     */
    public void title_()
    {
        // Reset ignore so that it's not carried forward for other events
        this.shouldIgnore = false;
    }

    /**
     * {@inheritDoc}
     */
    public void author()
    {
        // Ignore since there's no notion of author in XWiki markup
        this.shouldIgnore = true;
    }

    /**
     * {@inheritDoc}
     */
    public void author_()
    {
        // Reset ignore so that it's not carried forward for other events
        this.shouldIgnore = false;
    }

    /**
     * {@inheritDoc}
     */
    public void date()
    {
        // Ignore since there's no notion of date in XWiki markup
        this.shouldIgnore = true;
    }

    /**
     * {@inheritDoc}
     */
    public void date_()
    {
        // Reset ignore so that it's not carried forward for other events
        this.shouldIgnore = false;
    }

    /**
     * {@inheritDoc}
     */
    public void body()
    {
        // Ignore since there's no notion of body in XWiki markup
    }

    /**
     * {@inheritDoc}
     */
    public void body_()
    {
        // Ignore since there's no notion of body in XWiki markup
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle()
    {
        // Ignore since there's no notion of global section title in XWiki markup
        this.shouldIgnore = true;
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle_()
    {
        // Reset ignore so that it's not carried forward for other events
        this.shouldIgnore = false;
    }

    /**
     * {@inheritDoc}
     */
    public void section1()
    {
        // Ignore, do the output in sectionTitle1()
    }

    /**
     * {@inheritDoc}
     */
    public void section1_()
    {
        // Ignore, do the output in sectionTitle1()
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle1()
    {
        write( "1 " );
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle1_()
    {
        write( EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void section2()
    {
        // Ignore, do the output in sectionTitle2()
    }

    /**
     * {@inheritDoc}
     */
    public void section2_()
    {
        // Ignore, do the output in sectionTitle2()
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle2()
    {
        write( "1.1 " );
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle2_()
    {
        write( EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void section3()
    {
        // Ignore, do the output in sectionTitle3()
    }

    /**
     * {@inheritDoc}
     */
    public void section3_()
    {
        // Ignore, do the output in sectionTitle3()
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle3()
    {
        write( "1.1.1 " );
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle3_()
    {
        write( EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void section4()
    {
        // Ignore, do the output in sectionTitle4()
    }

    /**
     * {@inheritDoc}
     */
    public void section4_()
    {
        // Ignore, do the output in sectionTitle4()
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle4()
    {
        write( "1.1.1.1 " );
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle4_()
    {
        write( EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void section5()
    {
        // Ignore, do the output in sectionTitle5()
    }

    /**
     * {@inheritDoc}
     */
    public void section5_()
    {
        // Ignore, do the output in sectionTitle5()
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle5()
    {
        write( "1.1.1.1.1 " );
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle5_()
    {
        write( EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void list()
    {
        isInList = true;
        listNestingIndent++;
        listStyles.push( LIST_START_MARKUP );

        if ( shouldAddEOL )
        {
            write( EOL );
            shouldAddEOL = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void list_()
    {
        listNestingIndent--;
        listStyles.pop();
        isInList = false;
    }

    /**
     * {@inheritDoc}
     */
    public void listItem()
    {
        numberedListItem();
    }

    /**
     * {@inheritDoc}
     */
    public void listItem_()
    {
        if ( shouldAddEOL )
        {
            write( EOL );
            shouldAddEOL = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void numberedList( int numbering )
    {
        listNestingIndent++;

        String style;
        switch ( numbering )
        {
            case NUMBERING_UPPER_ALPHA:
                style = String.valueOf( NUMBERING_UPPER_ALPHA_CHAR );
                break;
            case NUMBERING_LOWER_ALPHA:
                style = String.valueOf( NUMBERING_LOWER_ALPHA_CHAR );
                break;
            case NUMBERING_UPPER_ROMAN:
                style = String.valueOf( NUMBERING_UPPER_ROMAN_CHAR );
                break;
            case NUMBERING_LOWER_ROMAN:
                style = String.valueOf( NUMBERING_LOWER_ROMAN_CHAR );
                break;
            case NUMBERING_DECIMAL:
            default:
                style = String.valueOf( NUMBERING );
        }

        listStyles.push( style );

        if ( shouldAddEOL )
        {
            write( EOL );
            shouldAddEOL = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void numberedList_()
    {
        listNestingIndent--;
        listStyles.pop();
    }

    /**
     * {@inheritDoc}
     */
    public void numberedListItem()
    {
        String style = (String) listStyles.peek();
        write( StringUtils.repeat( style, listNestingIndent ) );
        if ( style.equals( String.valueOf( NUMBERING ) ) )
        {
            // Add a .
            write( "." );
        }
        write( " " );
        shouldAddEOL = true;
    }

    /**
     * {@inheritDoc}
     */
    public void numberedListItem_()
    {
        if ( shouldAddEOL )
        {
            write( EOL );
            shouldAddEOL = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void definitionList()
    {
        write( "<dl>" + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void definitionList_()
    {
        write( "</dl>" + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void definitionListItem()
    {
    }

    /**
     * {@inheritDoc}
     */
    public void definitionListItem_()
    {
    }

    /**
     * {@inheritDoc}
     */
    public void definition()
    {
        write( "<dd>" );
    }

    /**
     * {@inheritDoc}
     */
    public void definition_()
    {
        write( "</dd>" + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void definedTerm()
    {
        write( "<dt>" );
    }

    /**
     * {@inheritDoc}
     */
    public void definedTerm_()
    {
        write( "</dt>" + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void figure()
    {
    }

    /**
     * {@inheritDoc}
     */
    public void figure_()
    {
        write( "}" + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void figureCaption()
    {
        write( "|alt=" );
    }

    /**
     * {@inheritDoc}
     */
    public void figureCaption_()
    {
    }

    /**
     * {@inheritDoc}
     */
    public void figureGraphics( String name )
    {
        write( "{image:" + name );
    }

    /**
     * {@inheritDoc}
     */
    public void table()
    {
        write( "{table}" + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void table_()
    {
        write( "{table}" + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void tableRows( int[] justification, boolean grid )
    {
    }

    /**
     * {@inheritDoc}
     */
    public void tableRows_()
    {
    }

    /**
     * {@inheritDoc}
     */
    public void tableRow()
    {
    }

    /**
     * {@inheritDoc}
     */
    public void tableRow_()
    {
        write( EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void tableCell()
    {
    }

    /**
     * {@inheritDoc}
     */
    public void tableCell( String width )
    {
    }

    /**
     * {@inheritDoc}
     */
    public void tableCell_()
    {
        write( "|" );
    }

    /**
     * {@inheritDoc}
     */
    public void tableHeaderCell()
    {
        tableCell();
    }

    /**
     * {@inheritDoc}
     */
    public void tableHeaderCell( String width )
    {
        tableHeaderCell();
    }

    /**
     * {@inheritDoc}
     */
    public void tableHeaderCell_()
    {
        tableCell_();
    }

    /**
     * {@inheritDoc}
     */
    public void tableCaption()
    {
        // Ignoring the table caption since I'm not sure what it is and what to do with it yet.
        this.shouldIgnore = true;
    }

    /**
     * {@inheritDoc}
     */
    public void tableCaption_()
    {
        this.shouldIgnore = false;
    }

    /**
     * {@inheritDoc}
     */
    public void paragraph()
    {
        if ( isInList )
        {
            write( "\\\\" + EOL );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void paragraph_()
    {
        if ( !isInList )
        {
            write( EOL + EOL );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void verbatim( boolean boxed )
    {
        write( "{code:none}" + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void verbatim_()
    {
        write( EOL + "{code}" + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void horizontalRule()
    {
        write( "----" + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void pageBreak()
    {
    }

    /**
     * {@inheritDoc}
     */
    public void anchor( String name )
    {
        write( "<a name=\"" );
    }

    /**
     * {@inheritDoc}
     */
    public void anchor_()
    {
        write( "\"/>" + EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void link( String name )
    {
        linkName = name;
        write( "[" );
    }

    /**
     * {@inheritDoc}
     */
    public void link_()
    {
        if ( linkName != null )
        {
            write( ">" + linkName + "]" );
            linkName = null;
        }
        else
        {
            write( "]" );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void italic()
    {
        write( "~~" );
    }

    /**
     * {@inheritDoc}
     */
    public void italic_()
    {
        write( "~~" );
    }

    /**
     * {@inheritDoc}
     */
    public void bold()
    {
        write( "*" );
    }

    /**
     * {@inheritDoc}
     */
    public void bold_()
    {
        write( "*" );
    }

    /**
     * {@inheritDoc}
     */
    public void monospaced()
    {
        write( "<tt>" );
    }

    /**
     * {@inheritDoc}
     */
    public void monospaced_()
    {
        write( "</tt>" );
    }

    /**
     * {@inheritDoc}
     */
    public void lineBreak()
    {
        write( EOL );
    }

    /**
     * {@inheritDoc}
     */
    public void nonBreakingSpace()
    {
        write( "&nbsp;" );
    }

    /**
     * {@inheritDoc}
     */
    public void text( String text )
    {
        if ( !shouldIgnore )
        {
            write( text );
        }

        if ( ( linkName != null ) && ( linkName.equals( text ) ) )
        {
            linkName = null;
        }
    }

    public void rawText( String text )
    {
        write( text );
    }

    public void comment( String comment )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * {@inheritDoc}
     */
    public void flush()
    {
        writer.flush();
    }

    /**
     * {@inheritDoc}
     */
    public void close()
    {
        writer.close();
    }

    /**
     * Write text to output.
     *
     * @param text The text to write.
     */
    protected void write( String text )
    {
        this.writer.write( text );
    }

}
