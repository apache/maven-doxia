package org.codehaus.doxia.module.xhtml;

import org.codehaus.doxia.parser.Parser;
import org.codehaus.doxia.parser.ParseException;
import org.codehaus.doxia.sink.Sink;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;

import java.io.Reader;
import java.util.Stack;

/**
 * Parse an xdoc document and emit events into the specified doxia
 * Sink.
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class XhtmlParser
    implements Parser
{
    /**
     * This stack is needed to keep track of the different link and anchor-types
     * which utilize the same element
     */
    private Stack linktypes = new Stack();
    /**
     * This stack is needed to keep track of the section nesting. Each time
     * a lower section heading is encounted, this stack raises, each time a
     * higher section heading is encountered, this stack lowers.
     */
    private Stack sections = new Stack();
    /**
     * Indicates the last a-tag denoted a link
     */
    private static final String LINK = "link";
    /**
     * Indicates the last a-tag denoted an anchor
     */
    private static final String ANCHOR = "anchor";
    
    public void parse( Reader reader, Sink sink )
        throws ParseException
    {
        try
        {
            XmlPullParser parser = new MXParser();

            parser.setInput( reader );

            parseXdoc( parser, sink );
        }
        catch ( Exception ex )
        {
            throw new ParseException( "Error parsing the model.", ex );
        }
    }

    public void parseXdoc( XmlPullParser parser, Sink sink )
        throws Exception
    {
        int eventType = parser.getEventType();

        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
                if ( parser.getName().equals( "title" ) )
                {
                    sink.title();
                }
                /*
                 * The ADDRESS element may be used by authors to supply contact information 
                 * for a document or a major part of a document such as a form. This element
                 *  often appears at the beginning or end of a document.
                 */
                else if ( parser.getName().equals( "address" ) )
                {
                    sink.author();
                }
                else if ( parser.getName().equals( "body" ) )
                {
                    sink.body();
                }
                else if ( parser.getName().equals("h1")||
                    parser.getName().equals("h2")||
                    parser.getName().equals("h3")||
                    parser.getName().equals("h4")||
                    parser.getName().equals("h5") )
                {
                    this.closeSubordinatedSections(parser.getName(), sink);
                    this.startSection(this.sections.size(), sink);
                    this.startSectionTitle(this.sections.size(), sink);
                    this.sections.push(parser.getName());
                    
                }
                else if ( parser.getName().equals( "p" ) )
                {
                    sink.paragraph();
                }
                /*
                 * The PRE element tells visual user agents that the enclosed text is 
                 * "preformatted". When handling preformatted text, visual user agents:
                 * - May leave white space intact.
                 * - May render text with a fixed-pitch font.
                 * - May disable automatic word wrap.
                 * - Must not disable bidirectional processing.
                 * Non-visual user agents are not required to respect extra white space
                 * in the content of a PRE element.  
                 */
                else if ( parser.getName().equals( "pre" ) )
                {
                    sink.verbatim( true );
                }
                else if ( (parser.getName().equals( "code" ))
                    ||(parser.getName().equals( "samp" ))
                    ||(parser.getName().equals( "tt" )))
                {
                    sink.monospaced();
                }
                else if ( parser.getName().equals( "ul" ) )
                {
                    sink.list();
                }
                else if ( parser.getName().equals( "ol" ) )
                {
                    sink.numberedList( Sink.NUMBERING_DECIMAL );
                }
                else if ( parser.getName().equals( "li" ) )
                {
                    sink.listItem();
                }
                else if ( parser.getName().equals( "head" ) )
                {
                    sink.head();
                }
                else if ( (parser.getName().equals( "b" ))||(parser.getName().equals( "strong" )) )
                {
                    sink.bold();
                }
                else if ( (parser.getName().equals( "i" ))||(parser.getName().equals( "em" )) )
                {
                    sink.italic();
                }
                else if ( parser.getName().equals( "a" ) )
                {
                    String href = parser.getAttributeValue( null, "href" );
                    String name = parser.getAttributeValue( null, "name" );
                    String id = parser.getAttributeValue( null, "id" );
                    if (href!=null)
                    {
                        sink.link(href);
                        this.linktypes.push(XhtmlParser.LINK);
                    } else if (name!=null)
                    {
                        sink.anchor(name);
                        this.linktypes.push(XhtmlParser.ANCHOR);
                    } else if (id!=null)
                    {
                        sink.anchor(id);
                        this.linktypes.push(XhtmlParser.ANCHOR);
                    }
                } 
                else if ( parser.getName().equals("br") )
                {
                    sink.pageBreak();
                }
                else if ( parser.getName().equals("hr") )
                {
                    sink.horizontalRule();
                }
                else if ( parser.getName().equals("img") )
                {
                    sink.figure();
                    String src = parser.getAttributeValue( null, "src" );
                    String title = parser.getAttributeValue( null, "title" );
                    String alt = parser.getAttributeValue( null, "alt" );
                    if (src!=null)
                    {
                        sink.figureGraphics(src);
                    }
                    if (title!=null)
                    {
                        sink.figureCaption();
                        sink.text(title);
                        sink.figureCaption_();
                    }
                    else if (alt!=null)
                    {
                        sink.figureCaption();
                        sink.text(alt);
                        sink.figureCaption_();
                    }
                    sink.figure_();
                }
                // ----------------------------------------------------------------------
                // Tables
                // ----------------------------------------------------------------------


                else if ( parser.getName().equals( "table" ) )
                {
                    sink.table();
                }
                else if ( parser.getName().equals( "tr" ) )
                {
                    sink.tableRow();
                }
                else if ( parser.getName().equals( "th" ) )
                {
                    sink.tableCell();
                }
                else if ( parser.getName().equals( "td" ) )
                {
                    sink.tableCell();
                }

            }
            else if ( eventType == XmlPullParser.END_TAG )
            {
                if ( parser.getName().equals( "title" ) )
                {
                    sink.title_();
                }
                else if ( parser.getName().equals( "address" ) )
                {
                    sink.author_();
                }
                else if ( parser.getName().equals( "body" ) )
                {
                    //close all sections that are still open
                    closeSubordinatedSections("h0", sink);
                    sink.body_();
                }
                else if ( parser.getName().equals("h1")||
                    parser.getName().equals("h2")||
                    parser.getName().equals("h3")||
                    parser.getName().equals("h4")||
                    parser.getName().equals("h5") )
                {
                    this.closeSectionTitle(this.sections.size() - 1, sink);
                }
                else if ( parser.getName().equals( "p" ) )
                {
                    sink.paragraph_();
                }
                else if ( parser.getName().equals( "pre" ) )
                {
                    sink.verbatim_();
                }
                else if ( (parser.getName().equals( "code" ))
                    ||(parser.getName().equals( "samp" ))
                    ||(parser.getName().equals( "tt" )))
                {
                    sink.monospaced_();
                }
                else if ( parser.getName().equals( "ul" ) )
                {
                    sink.list_();
                }
                else if ( parser.getName().equals( "ol" ) )
                {
                    sink.numberedList_();
                }
                else if ( parser.getName().equals( "li" ) )
                {
                    sink.listItem_();
                }
                else if ( parser.getName().equals( "head" ) )
                {
                    sink.head_();
                }
                else if ( (parser.getName().equals( "b" ))||(parser.getName().equals( "strong" )) )
                {
                    sink.bold_();
                }
                else if ( (parser.getName().equals( "i" ))||(parser.getName().equals( "em" )) )
                {
                    sink.italic_();
                }
                else if ( parser.getName().equals( "a" ) )
                {
                    String linktype = (String) this.linktypes.pop();
                    //the equals operation is ok here, because we always use the class constant
                    if (linktype==XhtmlParser.LINK)
                    {
                        sink.link_();
                    }
                    else
                    {
                        sink.anchor_();
                    }
                }
                // ----------------------------------------------------------------------
                // Tables
                // ----------------------------------------------------------------------


                else if ( parser.getName().equals( "table" ) )
                {
                    sink.table_();
                }
                else if ( parser.getName().equals( "tr" ) )
                {
                    sink.tableRow_();
                }
                else if ( parser.getName().equals( "th" ) )
                {
                    sink.tableCell_();
                }
                else if ( parser.getName().equals( "td" ) )
                {
                    sink.tableCell_();
                }

            }
            else if ( eventType == XmlPullParser.TEXT )
            {
                sink.text( parser.getText() );
            }

            eventType = parser.next();
        }
    }
    
    private void closeSubordinatedSections(String level, Sink sink) {
        if (this.sections.size()>0) {
            String heading = (String) this.sections.peek();
            int otherlevel = Integer.parseInt(heading.substring(1));
            int mylevel = Integer.parseInt(level.substring(1));
            if (otherlevel>=mylevel)
            {
                closeSection(this.sections.size(), sink);
                closeSubordinatedSections(level, sink);
            }
        }
    }

    /**
     * Close a section of the specified level.
     * @param level level of the section to close
     * @param sink the sink to write to
     */
    private void closeSection( int level, Sink sink )
    {
        this.sections.pop();
        switch (level) {
            case 1:sink.section1_();break;
            case 2:sink.section2_();break;
            case 3:sink.section3_();break;
            case 4:sink.section4_();break;
            case 5:sink.section5_();break;
        }
    }
    /**
     * Starts a new section of the specified level
     * @param level level of the new section
     * @param sink the sink to write to
     */
    private void startSection( int level, Sink sink  )
    {
        switch (level) {
            case 0:sink.section1();break;
            case 1:sink.section2();break;
            case 2:sink.section3();break;
            case 3:sink.section4();break;
            case 4:sink.section5();break;
        }
    }
    /**
     * Closes the title of a section
     * @param level level of the section
     * @param sink the sink to write to
     */
    private void closeSectionTitle( int level, Sink sink )
    {
        switch (level) {
            case 0:sink.sectionTitle1_();break;
            case 1:sink.sectionTitle2_();break;
            case 2:sink.sectionTitle3_();break;
            case 3:sink.sectionTitle4_();break;
            case 4:sink.sectionTitle5_();break;
        }
    }
    /**
     * Starts the title of a new section
     * @param level level of the new section
     * @param sink the sink to write to
     */
    private void startSectionTitle( int level, Sink sink  )
    {
        switch (level) {
            case 0:sink.sectionTitle1();break;
            case 1:sink.sectionTitle2();break;
            case 2:sink.sectionTitle3();break;
            case 3:sink.sectionTitle4();break;
            case 4:sink.sectionTitle5();break;
        }
    }
}
