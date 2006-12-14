package org.apache.maven.doxia.module.apt;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Stack;

import org.apache.maven.doxia.module.HtmlTools;
import org.apache.maven.doxia.sink.SinkAdapter;
import org.codehaus.plexus.util.StringUtils;

/**
 * APT Sink implementation.
 * @author eredmond
 */
public class AptSink extends SinkAdapter
{
    private static final String EOL = System.getProperty("line.separator");
    private StringBuffer buffer;
    private StringBuffer tableCaptionBuffer;
    private String author;
    private String title;
    private String date;
    private boolean tableCaptionFlag;
    private boolean headerFlag;
    private boolean bufferFlag;
    private boolean itemFlag;
    private boolean verbatimFlag;
    private boolean boxed;
    private boolean gridFlag;
    private int cellCount;
    private PrintWriter writer;
    private int cellJustif[];
    private String rowLine;
    private String listNestingIndent;
    private Stack listStyles;

    public AptSink( Writer writer )
    {
    	this.buffer = new StringBuffer();
        this.tableCaptionBuffer = new StringBuffer();
        this.writer = new PrintWriter(writer);
        this.listNestingIndent = "";
        this.listStyles = new Stack();
    }

    protected StringBuffer getBuffer()
    {
        return buffer;
    }

    protected void setHeadFlag(boolean headFlag)
    {
        this.headerFlag = headFlag;
    }

    protected void resetState()
    {
        headerFlag = false;
        resetBuffer();
        itemFlag = false;
        verbatimFlag = false;
        cellCount = 0;
    }

    protected void resetBuffer()
    {
        buffer = new StringBuffer();
    }

    protected void resetTableCaptionBuffer()
    {
    	tableCaptionBuffer = new StringBuffer();
    }

    public void head()
    {
    	resetState();
    	headerFlag = true;
    }

    public void head_()
    {
        headerFlag = false;

        write( " -----" + EOL );
        write( " " + title + EOL  );
        write( " -----" + EOL );
        write( " " + author + EOL  );
        write( " -----" + EOL );
        write( " " + date + EOL  );
        write( " -----" + EOL );
    }

    public void title_()
    {
        if(buffer.length() > 0)
        {
        	title = buffer.toString();
            resetBuffer();
        }
    }

    public void author_()
    {
        if(buffer.length() > 0)
        {
        	author = buffer.toString();
            resetBuffer();
        }
    }

    public void date_()
    {
        if(buffer.length() > 0)
        {
        	date = buffer.toString();
            resetBuffer();
        }
    }

    public void section1_()
    {
        write( EOL );
    }

    public void section2_()
    {
        write( EOL );
    }

    public void section3_()
    {
        write( EOL );
    }

    public void section4_()
    {
        write( EOL );
    }

    public void section5_()
    {
        write( EOL );
    }

    public void sectionTitle1()
    {
        write( EOL );
    }

    public void sectionTitle1_()
    {
        write( EOL + EOL );
    }

    public void sectionTitle2()
    {
        write( EOL + "*" );
    }

    public void sectionTitle2_()
    {
        write( EOL + EOL );
    }

    public void sectionTitle3()
    {
        write( EOL + "**" );
    }

    public void sectionTitle3_()
    {
        write( EOL + EOL );
    }

    public void sectionTitle4()
    {
        write( EOL + "***" );
    }

    public void sectionTitle4_()
    {
        write( EOL + EOL );
    }

    public void sectionTitle5()
    {
        write( EOL + "****" );
    }

    public void sectionTitle5_()
    {
        write( EOL + EOL );
    }

    public void list()
    {
    	listNestingIndent += " ";
    	listStyles.push( "*" );
    	write( EOL );
    }

    public void list_()
    {
    	if( listNestingIndent.length() <= 1 )
    		write( EOL + listNestingIndent + "[]" + EOL );
    	else
    		write( EOL );
        listNestingIndent = StringUtils.chomp( listNestingIndent, " " );
    	listStyles.pop();
    	itemFlag = false;
    }

    public void listItem()
    {
    	//if( !numberedList )
    	//write( EOL + listNestingIndent + "*" );
    	//else
    	numberedListItem();
        itemFlag = true;
    }

    public void listItem_()
    {
    	write( EOL );
    }

    public void numberedList( int numbering )
    {
    	listNestingIndent += " ";
        write( EOL );

        String style;
        switch ( numbering )
        {
            case NUMBERING_UPPER_ALPHA:
                style = "A";
                break;
            case NUMBERING_LOWER_ALPHA:
                style = "a";
                break;
            case NUMBERING_UPPER_ROMAN:
                style = "I";
                break;
            case NUMBERING_LOWER_ROMAN:
                style = "i";
                break;
            case NUMBERING_DECIMAL:
            default:
                style = "1";
        }

        listStyles.push( style );
    }

    public void numberedList_()
    {
    	if( listNestingIndent.length() <= 1 )
    		write( EOL + listNestingIndent + "[]" + EOL );
    	else
    		write( EOL );
    	listNestingIndent = StringUtils.chomp( listNestingIndent, " " );
    	listStyles.pop();
        itemFlag = false;
    }

    public void numberedListItem()
    {
    	String style = (String)listStyles.peek();
    	if( style == "*" )
        	write( EOL + listNestingIndent + "* " );
    	else
    		write( EOL + listNestingIndent + "[[" + style + "]] " );
        itemFlag = true;
    }

    public void numberedListItem_()
    {
        write( EOL );
    }

    public void definitionList()
    {
        write( EOL );
    }

    public void definitionList_()
    {
        write( EOL );
        itemFlag = false;
    }

    public void definedTerm()
    {
        write( EOL + " [" );
    }

    public void definedTerm_()
    {
        write("]");
    }

    public void definition()
    {
        itemFlag = true;
    }

    public void definition_()
    {
        write( EOL );
    }

    public void pageBreak()
    {
    	// TODO: APT parse defect... pagebreak is never used.
    	write( EOL + "^L" + EOL );
    }

    public void paragraph()
    {
        if(!itemFlag)
            write( EOL + " ");
    }

    public void paragraph_()
    {
        if(itemFlag)
            itemFlag = false;
        else
            write( EOL + EOL );
    }

    public void verbatim( boolean boxed )
    {
        verbatimFlag = true;
        this.boxed = boxed;
        if( boxed )
        	write("\n+------+\n");
        else
        	write("\n------\n");
    }

    public void verbatim_()
    {
        if( boxed )
        	write("\n+------+\n");
        else
        	write("\n------\n");
        boxed = false;
        verbatimFlag = false;
    }

    public void horizontalRule()
    {
        write(EOL + "========" + EOL );
    }

    public void table()
    {
    	write( EOL );
    }

    public void table_()
    {
    	if( rowLine != null )
    		write( rowLine );
    	rowLine = null;

    	if( tableCaptionBuffer.length() > 0 )
    		text( tableCaptionBuffer.toString() + EOL );

    	resetTableCaptionBuffer();
    }

    public void tableRows(int justification[], boolean grid)
    {
        cellJustif = justification;
        gridFlag = grid;
    }

    public void tableRows_()
    {
        cellJustif = null;
        gridFlag = false;
    }

    public void tableRow()
    {
    	bufferFlag = true;
    	cellCount = 0;
    }

    public void tableRow_()
    {
    	bufferFlag = false;

    	// write out the header row first, then the data in the buffer
    	buildRowLine();

    	write( rowLine );

    	// TODO: This will need to be more clever, for multi-line cells
    	if( gridFlag )
    		write( "|" );

		write( buffer.toString() );

    	resetBuffer();

    	write( EOL );

    	// only reset cell count if this is the last row
    	cellCount = 0;
    }

    private void buildRowLine()
    {
    	StringBuffer rowLine = new StringBuffer();
    	rowLine.append( "*--" );

    	for(int i = 0; i < cellCount; i++)
    	{
    		if(cellJustif != null)
    			switch(cellJustif[i])
    			{
    			case 1:
    				rowLine.append( "--+" );
    				break;
    			case 2:
    				rowLine.append( "--:" );
    				break;
    			default:
    				rowLine.append( "--*" );
    			}
    		else
    			rowLine.append( "--*" );
    	}
    	rowLine.append( EOL );

    	this.rowLine = rowLine.toString();
    }

    public void tableCell_()
    {
        tableCell_(false);
    }

    public void tableHeaderCell_()
    {
        tableCell_(true);
    }

    public void tableCell_(boolean headerRow)
    {
        buffer.append("|");
        cellCount++;
    }

    public void tableCaption()
    {
    	tableCaptionFlag = true;
    }

    public void tableCaption_()
    {
    	tableCaptionFlag = false;
    }

    public void figureCaption_()
    {
        write( EOL );
    }

    public void figureGraphics(String name)
    {
        write( EOL + "[" + name + "] ");
    }

    public void anchor(String name)
    {
//    	String id = HtmlTools.encodeId(name);
    	write("{");
    }

    public void anchor_()
    {
    	write("}");
    }

    public void link(String name)
    {
        if(!headerFlag)
        {
        	write("{{{");
        	text( name );
        	write( "}" );
        }
    }

    public void link_()
    {
        if(!headerFlag)
        {
        	write( "}}" );
        }
    }

    public void link(String name, String target)
    {
        if(!headerFlag)
        {
        	write("{{{");
        	text( target );
        	write( "}" );
        	text( name );
        }
    }

    public void italic()
    {
        if(!headerFlag)
        	write("<");
    }

    public void italic_()
    {
        if(!headerFlag)
        	write(">");
    }

    public void bold()
    {
        if(!headerFlag)
        	write("<<");
    }

    public void bold_()
    {
        if(!headerFlag)
        	write(">>");
    }

    public void monospaced()
    {
        if(!headerFlag)
        	write("<<<");
    }

    public void monospaced_()
    {
        if(!headerFlag)
        	write(">>>");
    }

    public void lineBreak()
    {
        if(headerFlag || bufferFlag)
            buffer.append(EOL);
        else
            write( "\\" + EOL);
    }

    public void nonBreakingSpace()
    {
        if(headerFlag || bufferFlag)
            buffer.append("\\ ");
        else
            write( "\\ " );
    }

    public void text( String text )
    {
    	if( tableCaptionFlag )
    		tableCaptionBuffer.append( text );
    	else if( headerFlag || bufferFlag )
            buffer.append(text);
        else if( verbatimFlag )
            verbatimContent(text);
        else
            content(text);
    }

    public void rawText(String text)
    {
        write(text);
    }

    protected void write(String text)
    {
    	writer.write(text);
    }

    protected void content(String text)
    {
        write(escapeAPT(text));
    }

    protected void verbatimContent(String text)
    {
        write(escapeAPT(text));
    }

    public static String encodeFragment(String text)
    {
        return HtmlTools.encodeFragment(text);
    }

    public static String encodeURL(String text)
    {
        return HtmlTools.encodeURL(text);
    }

    public void flush()
    {
        writer.flush();
    }

    public void close()
    {
        writer.close();
    }

    /**
     * Escape special characters in a text in APT:
     *
     * <pre>
     * \~, \=, \-, \+, \*, \[, \], \<, \>, \{, \}, \\
     * </pre>
     *
     * @param text the String to escape, may be null
     * @return the text escaped, "" if null String input
     */
    private static String escapeAPT( String text )
    {
        if ( text == null )
        {
            return "";
        }

        int length = text.length();
        StringBuffer buffer = new StringBuffer( length );

        for ( int i = 0; i < length; ++i )
        {
            char c = text.charAt( i );
            switch ( c )
            { // 0080
                case '\\':
                case '~':
                case '=':
                case '-':
                case '+':
                case '*':
                case '[':
                case ']':
                case '<':
                case '>':
                case '{':
                case '}':
                    buffer.append( '\\' );
                    buffer.append( c );
                    break;
                default:
                	if( c > 127 )
                	{
                		buffer.append( "\\u" );
                		String hex = Integer.toHexString( c );
                		if( hex.length() == 2 )
                    		buffer.append( "00" );
                		else if( hex.length() == 3 )
                    		buffer.append( "0" );
                		buffer.append( hex );
                	}
                	else
                		buffer.append( c );
            }
        }

        return buffer.toString();
    }
}
