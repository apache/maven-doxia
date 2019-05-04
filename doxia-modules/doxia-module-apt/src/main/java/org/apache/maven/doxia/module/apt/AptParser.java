package org.apache.maven.doxia.module.apt;

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
import org.apache.maven.doxia.macro.MacroRequest;
import org.apache.maven.doxia.macro.manager.MacroNotFoundException;
import org.apache.maven.doxia.parser.AbstractTextParser;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.SinkAdapter;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.util.DoxiaUtils;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * The APT parser.
 * <br>
 * Based on the <a href="http://www.xmlmind.com/aptconvert.html">APTconvert</a> project.
 *
 * @version $Id$
 * @since 1.0
 */
@Component( role = Parser.class, hint = "apt" )
public class AptParser
    extends AbstractTextParser
    implements AptMarkup
{
    /** Title event id */
    private static final int TITLE = 0;

    /** Section 1 event id */
    private static final int SECTION1 = 1;

    /** Section 2 event id */
    private static final int SECTION2 = 2;

    /** Section 3 event id */
    private static final int SECTION3 = 3;

    /** Section 4 event id */
    private static final int SECTION4 = 4;

    /** Section 5 event id */
    private static final int SECTION5 = 5;

    /** Paragraph event id */
    private static final int PARAGRAPH = 6;

    /** Verbatim event id */
    private static final int VERBATIM = 7;

    /** Figure event id */
    private static final int FIGURE = 8;

    /** Table event id */
    private static final int TABLE = 9;

    /** List event id */
    private static final int LIST_ITEM = 10;

    /** Numbered list event id */
    private static final int NUMBERED_LIST_ITEM = 11;

    /** Definition list event id */
    private static final int DEFINITION_LIST_ITEM = 12;

    /** Horizontal rule event id */
    private static final int HORIZONTAL_RULE = 13;

    /** Page break event id */
    private static final int PG_BREAK = 14;

    /** List break event id */
    private static final int LIST_BREAK = 15;

    /** Macro event id */
    private static final int MACRO = 16;

    /** Comment event id. */
    private static final int COMMENT_BLOCK = 17;

    /** String representations of event ids */
    private static final String[] TYPE_NAMES = {
        "TITLE",
        "SECTION1",
        "SECTION2",
        "SECTION3",
        "SECTION4",
        "SECTION5",
        "PARAGRAPH",
        "VERBATIM",
        "FIGURE",
        "TABLE",
        "LIST_ITEM",
        "NUMBERED_LIST_ITEM",
        "DEFINITION_LIST_ITEM",
        "HORIZONTAL_RULE",
        "PG_BREAK",
        "LIST_BREAK",
        "MACRO",
        "COMMENT_BLOCK" };

    /** An array of 85 spaces. */
    protected static final char[] SPACES;

    /** Default tab width. */
    public static final int TAB_WIDTH = 8;

    // ----------------------------------------------------------------------
    // Instance fields
    // ----------------------------------------------------------------------

    /** the AptSource. */
    private AptSource source;

    /** a block of AptSource. */
    private Block block;

    /** blockFileName. */
    private String blockFileName;

    /** blockLineNumber. */
    private int blockLineNumber;

    /** sourceContent. */
    protected String sourceContent;

    /** the sink to receive the events. */
    protected Sink sink;

    /** a line of AptSource. */
    protected String line;

    /** Map of warn messages with a String as key to describe the error type and a Set as value.
     * Using to reduce warn messages. */
    protected Map<String, Set<String>> warnMessages;

    private static final int NUMBER_OF_SPACES = 85;

    static
    {
        SPACES = new char[NUMBER_OF_SPACES];

        for ( int i = 0; i < NUMBER_OF_SPACES; i++ )
        {
            SPACES[i] = ' ';
        }
    }

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    @Override
    public void parse( Reader source, Sink sink )
        throws ParseException
    {
        parse( source, sink, "" );
    }
    
    @Override
    public void parse( Reader source, Sink sink, String reference )
        throws ParseException
    {
        init();

        try
        {
            StringWriter contentWriter = new StringWriter();
            IOUtil.copy( source, contentWriter );
            sourceContent = contentWriter.toString();
        }
        catch ( IOException e )
        {
            throw new AptParseException( "IOException: " + e.getMessage(), e );
        }

        try
        {
            this.source = new AptReaderSource( new StringReader( sourceContent ), reference );

            this.sink = sink;
            sink.enableLogging( getLog() );

            blockFileName = null;

            blockLineNumber = -1;

            // Lookahead line.
            nextLine();

            // Lookahead block.
            nextBlock( /*first*/true );

            // traverse comments
            while ( ( block != null ) && ( block.getType() == COMMENT_BLOCK ) )
            {
                block.traverse();
                nextBlock( /*first*/true );
            }

            traverseHead();

            traverseBody();
        }
        catch ( AptParseException ape )
        {
            // TODO handle column number
            throw new AptParseException( ape.getMessage(), ape, getSourceName(), getSourceLineNumber(), -1 );
        }
        finally
        {
            logWarnings();

            setSecondParsing( false );
            init();
        }
    }

    /**
     * Returns the name of the Apt source document.
     *
     * @return the source name.
     */
    public String getSourceName()
    {
        // Use this rather than source.getName() to report errors.
        return blockFileName;
    }

    /**
     * Returns the current line number of the Apt source document.
     *
     * @return the line number.
     */
    public int getSourceLineNumber()
    {
        // Use this rather than source.getLineNumber() to report errors.
        return blockLineNumber;
    }

    // ----------------------------------------------------------------------
    // Protected methods
    // ----------------------------------------------------------------------

    /**
     * Parse the next line of the Apt source document.
     *
     * @throws org.apache.maven.doxia.module.apt.AptParseException if something goes wrong.
     */
    protected void nextLine()
        throws AptParseException
    {
        line = source.getNextLine();
    }

    /**
     * Parse the given text.
     *
     * @param text the text to parse.
     * @param begin offset.
     * @param end offset.
     * @param sink the sink to receive the events.
     * @throws org.apache.maven.doxia.module.apt.AptParseException if something goes wrong.
     */
    protected void doTraverseText( String text, int begin, int end, Sink sink )
        throws AptParseException
    {
        boolean anchor = false;
        boolean link = false;
        boolean italic = false;
        boolean bold = false;
        boolean monospaced = false;
        StringBuilder buffer = new StringBuilder( end - begin );

        for ( int i = begin; i < end; ++i )
        {
            char c = text.charAt( i );
            switch ( c )
            {
                case BACKSLASH:
                    if ( i + 1 < end )
                    {
                        char escaped = text.charAt( i + 1 );
                        switch ( escaped )
                        {
                            case SPACE:
                                ++i;
                                flushTraversed( buffer, sink );
                                sink.nonBreakingSpace();
                                break;
                            case '\r':
                            case '\n':
                                ++i;
                                // Skip white space which may follow a line break.
                                while ( i + 1 < end && Character.isWhitespace( text.charAt( i + 1 ) ) )
                                {
                                    ++i;
                                }
                                flushTraversed( buffer, sink );
                                sink.lineBreak();
                                break;
                            case BACKSLASH:
                            case PIPE:
                            case COMMENT:
                            case EQUAL:
                            case MINUS:
                            case PLUS:
                            case STAR:
                            case LEFT_SQUARE_BRACKET:
                            case RIGHT_SQUARE_BRACKET:
                            case LESS_THAN:
                            case GREATER_THAN:
                            case LEFT_CURLY_BRACKET:
                            case RIGHT_CURLY_BRACKET:
                                ++i;
                                buffer.append( escaped );
                                break;
                            case 'x':
                                if ( i + 3 < end && isHexChar( text.charAt( i + 2 ) )
                                    && isHexChar( text.charAt( i + 3 ) ) )
                                {
                                    int value = '?';
                                    try
                                    {
                                        value = Integer.parseInt( text.substring( i + 2, i + 4 ), 16 );
                                    }
                                    catch ( NumberFormatException e )
                                    {
                                        if ( getLog().isDebugEnabled() )
                                        {
                                            getLog().debug( "Not a number: " + text.substring( i + 2, i + 4 ) );
                                        }
                                    }

                                    i += 3;
                                    buffer.append( (char) value );
                                }
                                else
                                {
                                    buffer.append( BACKSLASH );
                                }
                                break;
                            case 'u':
                                if ( i + 5 < end && isHexChar( text.charAt( i + 2 ) )
                                    && isHexChar( text.charAt( i + 3 ) ) && isHexChar( text.charAt( i + 4 ) )
                                    && isHexChar( text.charAt( i + 5 ) ) )
                                {
                                    int value = '?';
                                    try
                                    {
                                        value = Integer.parseInt( text.substring( i + 2, i + 6 ), 16 );
                                    }
                                    catch ( NumberFormatException e )
                                    {
                                        if ( getLog().isDebugEnabled() )
                                        {
                                            getLog().debug( "Not a number: " + text.substring( i + 2, i + 6 ) );
                                        }
                                    }

                                    i += 5;
                                    buffer.append( (char) value );
                                }
                                else
                                {
                                    buffer.append( BACKSLASH );
                                }
                                break;
                            default:
                                if ( isOctalChar( escaped ) )
                                {
                                    int octalChars = 1;
                                    if ( isOctalChar( charAt( text, end, i + 2 ) ) )
                                    {
                                        ++octalChars;
                                        if ( isOctalChar( charAt( text, end, i + 3 ) ) )
                                        {
                                            ++octalChars;
                                        }
                                    }
                                    int value = '?';
                                    try
                                    {
                                        value = Integer.parseInt( text.substring( i + 1, i + 1 + octalChars ), 8 );
                                    }
                                    catch ( NumberFormatException e )
                                    {
                                        if ( getLog().isDebugEnabled() )
                                        {
                                            getLog().debug(
                                                            "Not a number: "
                                                                + text.substring( i + 1, i + 1 + octalChars ) );
                                        }
                                    }

                                    i += octalChars;
                                    buffer.append( (char) value );
                                }
                                else
                                {
                                    buffer.append( BACKSLASH );
                                }
                        }
                    }
                    else
                    {
                        buffer.append( BACKSLASH );
                    }
                    break;

                case LEFT_CURLY_BRACKET: /*}*/
                    if ( !anchor && !link )
                    {
                        if ( i + 1 < end && text.charAt( i + 1 ) == LEFT_CURLY_BRACKET /*}*/ )
                        {
                            ++i;
                            link = true;
                            flushTraversed( buffer, sink );

                            String linkAnchor = null;

                            if ( i + 1 < end && text.charAt( i + 1 ) == LEFT_CURLY_BRACKET /*}*/ )
                            {
                                ++i;
                                StringBuilder buf = new StringBuilder();
                                i = skipTraversedLinkAnchor( text, i + 1, end, buf );
                                linkAnchor = buf.toString();
                            }

                            if ( linkAnchor == null )
                            {
                                linkAnchor = getTraversedLink( text, i + 1, end );
                            }

                            if ( AptUtils.isInternalLink( linkAnchor ) )
                            {
                                linkAnchor = "#" + linkAnchor;
                            }

                            int hashIndex = linkAnchor.indexOf( "#" );

                            if ( hashIndex != -1 && !AptUtils.isExternalLink( linkAnchor ) )
                            {
                                String hash = linkAnchor.substring( hashIndex + 1 );

                                if ( hash.endsWith( ".html" ) && !hash.startsWith( "./" ) )
                                {
                                    String msg = "Ambiguous link: '" + hash
                                            + "'. If this is a local link, prepend \"./\"!";
                                    logMessage( "ambiguousLink", msg );
                                }

                                // link##anchor means literal
                                if ( hash.startsWith( "#" ) )
                                {
                                    linkAnchor = linkAnchor.substring( 0, hashIndex ) + hash;
                                }
                                else if ( !DoxiaUtils.isValidId( hash ) )
                                {
                                    linkAnchor =
                                        linkAnchor.substring( 0, hashIndex ) + "#"
                                            + DoxiaUtils.encodeId( hash, true );

                                    String msg = "Modified invalid link: '" + hash + "' to '" + linkAnchor + "'";
                                    logMessage( "modifiedLink", msg );
                                }
                            }

                            sink.link( linkAnchor );
                        }
                        else
                        {
                            anchor = true;
                            flushTraversed( buffer, sink );

                            String linkAnchor = getTraversedAnchor( text, i + 1, end );

                            linkAnchor = AptUtils.encodeAnchor( linkAnchor );

                            sink.anchor( linkAnchor );
                        }
                    }
                    else
                    {
                        buffer.append( c );
                    }
                    break;

                case /*{*/RIGHT_CURLY_BRACKET:
                    if ( link && i + 1 < end && text.charAt( i + 1 ) == /*{*/RIGHT_CURLY_BRACKET )
                    {
                        ++i;
                        link = false;
                        flushTraversed( buffer, sink );
                        sink.link_();
                    }
                    else if ( anchor )
                    {
                        anchor = false;
                        flushTraversed( buffer, sink );
                        sink.anchor_();
                    }
                    else
                    {
                        buffer.append( c );
                    }
                    break;

                case LESS_THAN:
                    if ( !italic && !bold && !monospaced )
                    {
                        if ( i + 1 < end && text.charAt( i + 1 ) == LESS_THAN )
                        {
                            if ( i + 2 < end && text.charAt( i + 2 ) == LESS_THAN )
                            {
                                i += 2;
                                monospaced = true;
                                flushTraversed( buffer, sink );
                                sink.monospaced();
                            }
                            else
                            {
                                ++i;
                                bold = true;
                                flushTraversed( buffer, sink );
                                sink.bold();
                            }
                        }
                        else
                        {
                            italic = true;
                            flushTraversed( buffer, sink );
                            sink.italic();
                        }
                    }
                    else
                    {
                        buffer.append( c );
                    }
                    break;

                case GREATER_THAN:
                    if ( monospaced && i + 2 < end && text.charAt( i + 1 ) == GREATER_THAN
                        && text.charAt( i + 2 ) == GREATER_THAN )
                    {
                        i += 2;
                        monospaced = false;
                        flushTraversed( buffer, sink );
                        sink.monospaced_();
                    }
                    else if ( bold && i + 1 < end && text.charAt( i + 1 ) == GREATER_THAN )
                    {
                        ++i;
                        bold = false;
                        flushTraversed( buffer, sink );
                        sink.bold_();
                    }
                    else if ( italic )
                    {
                        italic = false;
                        flushTraversed( buffer, sink );
                        sink.italic_();
                    }
                    else
                    {
                        buffer.append( c );
                    }
                    break;

                default:
                    if ( Character.isWhitespace( c ) )
                    {
                        buffer.append( SPACE );

                        // Skip to the last char of a sequence of white spaces.
                        while ( i + 1 < end && Character.isWhitespace( text.charAt( i + 1 ) ) )
                        {
                            ++i;
                        }
                    }
                    else
                    {
                        buffer.append( c );
                    }
            }
        }

        if ( monospaced )
        {
            throw new AptParseException( "missing '" + MONOSPACED_END_MARKUP + "'" );
        }
        if ( bold )
        {
            throw new AptParseException( "missing '" + BOLD_END_MARKUP + "'" );
        }
        if ( italic )
        {
            throw new AptParseException( "missing '" + ITALIC_END_MARKUP + "'" );
        }
        if ( link )
        {
            throw new AptParseException( "missing '" + LINK_END_MARKUP + "'" );
        }
        if ( anchor )
        {
            throw new AptParseException( "missing '" + ANCHOR_END_MARKUP + "'" );
        }

        flushTraversed( buffer, sink );
    }

    // -----------------------------------------------------------------------

    /**
     * Returns the character at position i of the given string.
     *
     * @param string the string.
     * @param length length.
     * @param i offset.
     * @return the character, or '\0' if i &gt; length.
     */
    protected static char charAt( String string, int length, int i )
    {
        return ( i < length ) ? string.charAt( i ) : '\0';
    }

    /**
     * Skip spaces.
     *
     * @param string string.
     * @param length length.
     * @param i offset.
     * @return int.
     */
    protected static int skipSpace( String string, int length, int i )
    {
        loop: for ( ; i < length; ++i )
        {
            switch ( string.charAt( i ) )
            {
                case SPACE:
                case TAB:
                    break;
                default:
                    break loop;
            }
        }
        return i;
    }

    /**
     * Replace part of a string.
     *
     * @param string the string
     * @param oldSub the substring to replace
     * @param newSub the replacement string
     * @return String
     */
    protected static String replaceAll( String string, String oldSub, String newSub )
    {
        StringBuilder replaced = new StringBuilder();
        int oldSubLength = oldSub.length();
        int begin, end;

        begin = 0;
        while ( ( end = string.indexOf( oldSub, begin ) ) >= 0 )
        {
            if ( end > begin )
            {
                replaced.append( string.substring( begin, end ) );
            }
            replaced.append( newSub );
            begin = end + oldSubLength;
        }
        if ( begin < string.length() )
        {
            replaced.append( string.substring( begin ) );
        }

        return replaced.toString();
    }

    /** {@inheritDoc} */
    protected void init()
    {
        super.init();

        this.sourceContent = null;
        this.sink = null;
        this.source = null;
        this.block = null;
        this.blockFileName = null;
        this.blockLineNumber = 0;
        this.line = null;
        this.warnMessages = null;
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * Parse the head of the Apt source document.
     *
     * @throws AptParseException if something goes wrong.
     */
    private void traverseHead()
        throws AptParseException
    {
        sink.head();

        if ( block != null && block.getType() == TITLE )
        {
            block.traverse();
            nextBlock();
        }

        sink.head_();
    }

    /**
     * Parse the body of the Apt source document.
     *
     * @throws AptParseException if something goes wrong.
     */
    private void traverseBody()
        throws AptParseException
    {
        sink.body();

        if ( block != null )
        {
            traverseSectionBlocks();
        }

        while ( block != null )
        {
            traverseSection( 0 );
        }

        sink.body_();
    }

    /**
     * Parse a section of the Apt source document.
     *
     * @param level The section level.
     * @throws AptParseException if something goes wrong.
     */
    private void traverseSection( int level )
        throws AptParseException
    {
        if ( block == null )
        {
            return;
        }

        int type = SECTION1 + level;

        expectedBlock( type );

        switch ( level )
        {
            case 0:
                sink.section1();
                break;
            case 1:
                sink.section2();
                break;
            case 2:
                sink.section3();
                break;
            case 3:
                sink.section4();
                break;
            case 4:
                sink.section5();
                break;
            default:
                break;
        }

        block.traverse();

        nextBlock();

        traverseSectionBlocks();

        while ( block != null )
        {
            if ( block.getType() <= type )
            {
                break;
            }

            traverseSection( level + 1 );
        }

        switch ( level )
        {
            case 0:
                sink.section1_();
                break;
            case 1:
                sink.section2_();
                break;
            case 2:
                sink.section3_();
                break;
            case 3:
                sink.section4_();
                break;
            case 4:
                sink.section5_();
                break;
            default:
                break;
        }
    }

    /**
     * Parse the section blocks of the Apt source document.
     *
     * @throws AptParseException if something goes wrong.
     */
    private void traverseSectionBlocks()
        throws AptParseException
    {
        loop: while ( block != null )
        {
            switch ( block.getType() )
            {
                case PARAGRAPH:
                case VERBATIM:
                case FIGURE:
                case TABLE:
                case HORIZONTAL_RULE:
                case PG_BREAK:
                case MACRO:
                case COMMENT_BLOCK:
                    block.traverse();
                    nextBlock();
                    break;

                case LIST_ITEM:
                    traverseList();
                    break;

                case NUMBERED_LIST_ITEM:
                    traverseNumberedList();
                    break;

                case DEFINITION_LIST_ITEM:
                    traverseDefinitionList();
                    break;

                case LIST_BREAK:
                    // May be this is a list break which has not been indented
                    // very precisely.
                    nextBlock();
                    break;

                default:
                    // A section block which starts a new section.
                    break loop;
            }
        }
    }

    /**
     * Parse a list of the Apt source document.
     *
     * @throws AptParseException if something goes wrong.
     */
    private void traverseList()
        throws AptParseException
    {
        if ( block == null )
        {
            return;
        }

        expectedBlock( LIST_ITEM );

        int listIndent = block.getIndent();

        sink.list();

        sink.listItem();

        block.traverse();

        nextBlock();

        loop: while ( block != null )
        {
            int blockIndent = block.getIndent();

            switch ( block.getType() )
            {
                case PARAGRAPH:
                    if ( blockIndent < listIndent )
                    {
                        break loop;
                    }
                    /*FALLTHROUGH*/
                case VERBATIM:
                case MACRO:
                case FIGURE:
                case TABLE:
                case HORIZONTAL_RULE:
                case PG_BREAK:
                    block.traverse();
                    nextBlock();
                    break;

                case LIST_ITEM:
                    if ( blockIndent < listIndent )
                    {
                        break loop;
                    }

                    if ( blockIndent > listIndent )
                    {
                        traverseList();
                    }
                    else
                    {
                        sink.listItem_();
                        sink.listItem();
                        block.traverse();
                        nextBlock();
                    }
                    break;

                case NUMBERED_LIST_ITEM:
                    if ( blockIndent < listIndent )
                    {
                        break loop;
                    }

                    traverseNumberedList();
                    break;

                case DEFINITION_LIST_ITEM:
                    if ( blockIndent < listIndent )
                    {
                        break loop;
                    }

                    traverseDefinitionList();
                    break;

                case LIST_BREAK:
                    if ( blockIndent >= listIndent )
                    {
                        nextBlock();
                    }
                    /*FALLTHROUGH*/
                default:
                    // A block which ends the list.
                    break loop;
            }
        }

        sink.listItem_();
        sink.list_();
    }

    /**
     * Parse a numbered list of the Apt source document.
     *
     * @throws AptParseException if something goes wrong.
     */
    private void traverseNumberedList()
        throws AptParseException
    {
        if ( block == null )
        {
            return;
        }
        expectedBlock( NUMBERED_LIST_ITEM );
        int listIndent = block.getIndent();

        sink.numberedList( ( (NumberedListItem) block ).getNumbering() );
        sink.numberedListItem();
        block.traverse();
        nextBlock();

        loop: while ( block != null )
        {
            int blockIndent = block.getIndent();

            switch ( block.getType() )
            {
                case PARAGRAPH:
                    if ( blockIndent < listIndent )
                    {
                        break loop;
                    }
                    /*FALLTHROUGH*/
                case VERBATIM:
                case FIGURE:
                case TABLE:
                case HORIZONTAL_RULE:
                case PG_BREAK:
                    block.traverse();
                    nextBlock();
                    break;

                case LIST_ITEM:
                    if ( blockIndent < listIndent )
                    {
                        break loop;
                    }

                    traverseList();
                    break;

                case NUMBERED_LIST_ITEM:
                    if ( blockIndent < listIndent )
                    {
                        break loop;
                    }

                    if ( blockIndent > listIndent )
                    {
                        traverseNumberedList();
                    }
                    else
                    {
                        sink.numberedListItem_();
                        sink.numberedListItem();
                        block.traverse();
                        nextBlock();
                    }
                    break;

                case DEFINITION_LIST_ITEM:
                    if ( blockIndent < listIndent )
                    {
                        break loop;
                    }

                    traverseDefinitionList();
                    break;

                case LIST_BREAK:
                    if ( blockIndent >= listIndent )
                    {
                        nextBlock();
                    }
                    /*FALLTHROUGH*/
                default:
                    // A block which ends the list.
                    break loop;
            }
        }

        sink.numberedListItem_();
        sink.numberedList_();
    }

    /**
     * Parse a definition list of the Apt source document.
     *
     * @throws AptParseException if something goes wrong.
     */
    private void traverseDefinitionList()
        throws AptParseException
    {
        if ( block == null )
        {
            return;
        }
        expectedBlock( DEFINITION_LIST_ITEM );
        int listIndent = block.getIndent();

        sink.definitionList();
        sink.definitionListItem();
        block.traverse();
        nextBlock();

        loop: while ( block != null )
        {
            int blockIndent = block.getIndent();

            switch ( block.getType() )
            {
                case PARAGRAPH:
                    if ( blockIndent < listIndent )
                    {
                        break loop;
                    }
                    /*FALLTHROUGH*/
                case VERBATIM:
                case FIGURE:
                case TABLE:
                case HORIZONTAL_RULE:
                case PG_BREAK:
                    block.traverse();
                    nextBlock();
                    break;

                case LIST_ITEM:
                    if ( blockIndent < listIndent )
                    {
                        break loop;
                    }

                    traverseList();
                    break;

                case NUMBERED_LIST_ITEM:
                    if ( blockIndent < listIndent )
                    {
                        break loop;
                    }

                    traverseNumberedList();
                    break;

                case DEFINITION_LIST_ITEM:
                    if ( blockIndent < listIndent )
                    {
                        break loop;
                    }

                    if ( blockIndent > listIndent )
                    {
                        traverseDefinitionList();
                    }
                    else
                    {
                        sink.definition_();
                        sink.definitionListItem_();
                        sink.definitionListItem();
                        block.traverse();
                        nextBlock();
                    }
                    break;

                case LIST_BREAK:
                    if ( blockIndent >= listIndent )
                    {
                        nextBlock();
                    }
                    /*FALLTHROUGH*/
                default:
                    // A block which ends the list.
                    break loop;
            }
        }

        sink.definition_();
        sink.definitionListItem_();
        sink.definitionList_();
    }

    /**
     * Parse the next block of the Apt source document.
     *
     * @throws AptParseException if something goes wrong.
     */
    private void nextBlock()
        throws AptParseException
    {
        nextBlock( /*first*/false );
    }

    /**
     * Parse the next block of the Apt source document.
     *
     * @param firstBlock True if this is the first block of the Apt source document.
     * @throws AptParseException if something goes wrong.
     */
    private void nextBlock( boolean firstBlock )
        throws AptParseException
    {
        // Skip open lines.
        int length, indent, i;

        skipLoop: for ( ;; )
        {
            if ( line == null )
            {
                block = null;
                return;
            }

            length = line.length();
            indent = 0;
            for ( i = 0; i < length; ++i )
            {
                switch ( line.charAt( i ) )
                {
                    case SPACE:
                        ++indent;
                        break;
                    case TAB:
                        indent += 8;
                        break;
                    default:
                        break skipLoop;
                }
            }

            if ( i == length )
            {
                nextLine();
            }
        }

        blockFileName = source.getName();
        blockLineNumber = source.getLineNumber();
        block = null;
        switch ( line.charAt( i ) )
        {
            case STAR:
                if ( indent == 0 )
                {
                    if ( charAt( line, length, i + 1 ) == MINUS && charAt( line, length, i + 2 ) == MINUS )
                    {
                        block = new Table( indent, line );
                    }
                    else if ( charAt( line, length, i + 1 ) == STAR )
                    {
                        if ( charAt( line, length, i + 2 ) == STAR )
                        {
                            if ( charAt( line, length, i + 3 ) == STAR )
                            {
                                block = new Section5( indent, line );
                            }
                            else
                            {
                                block = new Section4( indent, line );
                            }
                        }
                        else
                        {
                            block = new Section3( indent, line );
                        }
                    }
                    else
                    {
                        block = new Section2( indent, line );
                    }
                }
                else
                {
                    block = new ListItem( indent, line );
                }
                break;
            case LEFT_SQUARE_BRACKET:
                if ( charAt( line, length, i + 1 ) == RIGHT_SQUARE_BRACKET )
                {
                    block = new ListBreak( indent, line );
                }
                else
                {
                    if ( indent == 0 )
                    {
                        block = new Figure( indent, line );
                    }
                    else
                    {
                        if ( charAt( line, length, i + 1 ) == LEFT_SQUARE_BRACKET )
                        {
                            int numbering;

                            switch ( charAt( line, length, i + 2 ) )
                            {
                                case NUMBERING_LOWER_ALPHA_CHAR:
                                    numbering = Sink.NUMBERING_LOWER_ALPHA;
                                    break;
                                case NUMBERING_UPPER_ALPHA_CHAR:
                                    numbering = Sink.NUMBERING_UPPER_ALPHA;
                                    break;
                                case NUMBERING_LOWER_ROMAN_CHAR:
                                    numbering = Sink.NUMBERING_LOWER_ROMAN;
                                    break;
                                case NUMBERING_UPPER_ROMAN_CHAR:
                                    numbering = Sink.NUMBERING_UPPER_ROMAN;
                                    break;
                                case NUMBERING:
                                default:
                                    // The first item establishes the numbering
                                    // scheme for the whole list.
                                    numbering = Sink.NUMBERING_DECIMAL;
                            }

                            block = new NumberedListItem( indent, line, numbering );
                        }
                        else
                        {
                            block = new DefinitionListItem( indent, line );
                        }
                    }
                }
                break;
            case MINUS:
                if ( charAt( line, length, i + 1 ) == MINUS && charAt( line, length, i + 2 ) == MINUS )
                {
                    if ( indent == 0 )
                    {
                        block = new Verbatim( indent, line );
                    }
                    else
                    {
                        if ( firstBlock )
                        {
                            block = new Title( indent, line );
                        }
                    }
                }
                break;
            case PLUS:
                if ( indent == 0 && charAt( line, length, i + 1 ) == MINUS && charAt( line, length, i + 2 ) == MINUS )
                {
                    block = new Verbatim( indent, line );
                }
                break;
            case EQUAL:
                if ( indent == 0 && charAt( line, length, i + 1 ) == EQUAL && charAt( line, length, i + 2 ) == EQUAL )
                {
                    block = new HorizontalRule( indent, line );
                }
                break;
            case PAGE_BREAK:
                if ( indent == 0 )
                {
                    block = new PageBreak( indent, line );
                }
                break;
            case PERCENT:
                if ( indent == 0 && charAt( line, length, i + 1 ) == LEFT_CURLY_BRACKET )
                {
                    block = new MacroBlock( indent, line );
                }
                break;
            case COMMENT:
                if ( charAt( line, length, i + 1 ) == COMMENT )
                {
                    block = new Comment( line.substring( i + 2 ) );
                }
                break;
            default:
                break;
        }

        if ( block == null )
        {
            if ( indent == 0 )
            {
                block = new Section1( indent, line );
            }
            else
            {
                block = new Paragraph( indent, line );
            }
        }
    }

    /**
     * Checks that the current block is of the expected type.
     *
     * @param type the expected type.
     * @throws AptParseException if something goes wrong.
     */
    private void expectedBlock( int type )
        throws AptParseException
    {
        int blockType = block.getType();

        if ( blockType != type )
        {
            throw new AptParseException( "expected " + TYPE_NAMES[type] + ", found " + TYPE_NAMES[blockType] );
        }
    }

    // -----------------------------------------------------------------------

    /**
     * Determine if c is an octal character.
     *
     * @param c the character.
     * @return boolean
     */
    private static boolean isOctalChar( char c )
    {
        return ( c >= '0' && c <= '7' );
    }

    /**
     * Determine if c is an hex character.
     *
     * @param c the character.
     * @return boolean
     */
    private static boolean isHexChar( char c )
    {
        return ( ( c >= '0' && c <= '9' ) || ( c >= 'a' && c <= 'f' ) || ( c >= 'A' && c <= 'F' ) );
    }

    /**
     * Emits the text so far parsed into the given sink.
     *
     * @param buffer A StringBuilder that contains the text to be flushed.
     * @param sink The sink to receive the text.
     */
    private static void flushTraversed( StringBuilder buffer, Sink sink )
    {
        if ( buffer.length() > 0 )
        {
            sink.text( buffer.toString() );
            buffer.setLength( 0 );
        }
    }

    /**
     * Parse the given text.
     *
     * @param text the text to parse.
     * @param begin offset.
     * @param end offset.
     * @param linkAnchor a StringBuilder.
     * @return int
     * @throws AptParseException if something goes wrong.
     */
    private static int skipTraversedLinkAnchor( String text, int begin, int end, StringBuilder linkAnchor )
        throws AptParseException
    {
        int i;
        loop: for ( i = begin; i < end; ++i )
        {
            char c = text.charAt( i );
            switch ( c )
            {
                case RIGHT_CURLY_BRACKET:
                    break loop;
                case BACKSLASH:
                    if ( i + 1 < end )
                    {
                        ++i;
                        linkAnchor.append( text.charAt( i ) );
                    }
                    else
                    {
                        linkAnchor.append( BACKSLASH );
                    }
                    break;
                default:
                    linkAnchor.append( c );
            }
        }
        if ( i == end )
        {
            throw new AptParseException( "missing '" + RIGHT_CURLY_BRACKET + "'" );
        }

        return i;
    }

    /**
     * Parse the given text.
     *
     * @param text the text to parse.
     * @param begin offset.
     * @param end offset.
     * @return String
     * @throws AptParseException if something goes wrong.
     */
    private String getTraversedLink( String text, int begin, int end )
        throws AptParseException
    {
        char previous2 = LEFT_CURLY_BRACKET;
        char previous = LEFT_CURLY_BRACKET;
        int i;

        for ( i = begin; i < end; ++i )
        {
            char c = text.charAt( i );
            if ( c == RIGHT_CURLY_BRACKET && previous == RIGHT_CURLY_BRACKET && previous2 != BACKSLASH )
            {
                break;
            }

            previous2 = previous;
            previous = c;
        }
        if ( i == end )
        {
            throw new AptParseException( "missing '" + LEFT_CURLY_BRACKET + LEFT_CURLY_BRACKET + "'" );
        }

        return doGetTraversedLink( text, begin, i - 1 );
    }

    /**
     * Parse the given text.
     *
     * @param text the text to parse.
     * @param begin offset.
     * @param end offset.
     * @return String
     * @throws AptParseException if something goes wrong.
     */
    private String getTraversedAnchor( String text, int begin, int end )
        throws AptParseException
    {
        char previous = LEFT_CURLY_BRACKET;
        int i;

        for ( i = begin; i < end; ++i )
        {
            char c = text.charAt( i );
            if ( c == RIGHT_CURLY_BRACKET && previous != BACKSLASH )
            {
                break;
            }

            previous = c;
        }
        if ( i == end )
        {
            throw new AptParseException( "missing '" + RIGHT_CURLY_BRACKET + "'" );
        }

        return doGetTraversedLink( text, begin, i );
    }

    /**
     * Parse the given text.
     *
     * @param text the text to parse.
     * @param begin offset.
     * @param end offset.
     * @return String
     * @throws AptParseException if something goes wrong.
     */
    private String doGetTraversedLink( String text, int begin, int end )
        throws AptParseException
    {
        final StringBuilder buffer = new StringBuilder( end - begin );

        Sink linkSink = new SinkAdapter()
        {
            /** {@inheritDoc} */
            public void lineBreak()
            {
                buffer.append( SPACE );
            }

            /** {@inheritDoc} */
            public void nonBreakingSpace()
            {
                buffer.append( SPACE );
            }

            /** {@inheritDoc} */
            public void text( String text )
            {
                buffer.append( text );
            }
        };
        doTraverseText( text, begin, end, linkSink );

        return buffer.toString().trim();
    }

    /**
     * If debug mode is enabled, log the <code>msg</code> as is, otherwise add unique msg in <code>warnMessages</code>.
     *
     * @param key not null
     * @param msg not null
     * @see #parse(Reader, Sink)
     * @since 1.1.1
     */
    private void logMessage( String key, String msg )
    {
        msg = "[APT Parser] " + msg;
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( msg );

            return;
        }

        if ( warnMessages == null )
        {
            warnMessages = new HashMap<>();
        }

        Set<String> set = warnMessages.get( key );
        if ( set == null )
        {
            set = new TreeSet<>();
        }
        set.add( msg );
        warnMessages.put( key, set );
    }

    /**
     * @since 1.1.2
     */
    private void logWarnings()
    {
        if ( getLog().isWarnEnabled() && this.warnMessages != null && !isSecondParsing() )
        {
            for ( Map.Entry<String, Set<String>> entry : this.warnMessages.entrySet() )
            {
                for ( String msg : entry.getValue() )
                {
                    getLog().warn( msg );
                }
            }

            this.warnMessages = null;
        }
    }

    // -----------------------------------------------------------------------

    /** A block of an apt source document. */
    private abstract class Block
    {
        /** type. */
        protected int type;

        /** indent. */
        protected int indent;

        /** text. */
        protected String text;

        /** textLength. */
        protected int textLength;

        /**
         * Constructor.
         *
         * @param type the block type.
         * @param indent indent.
         * @throws AptParseException AptParseException
         */
        Block( int type, int indent )
            throws AptParseException
        {
            this( type, indent, null );
        }

        /**
         * Constructor.
         *
         * @param type type.
         * @param indent indent.
         * @param firstLine the first line.
         * @throws AptParseException AptParseException
         */
        Block( int type, int indent, String firstLine )
            throws AptParseException
        {
            this.type = type;
            this.indent = indent;

            // Skip first line ---
            AptParser.this.nextLine();

            if ( firstLine == null )
            {
                text = null;
                textLength = 0;
            }
            else
            {
                // Read block ---
                StringBuilder buffer = new StringBuilder( firstLine );

                while ( AptParser.this.line != null )
                {
                    String l = AptParser.this.line;
                    int length = l.length();
                    int i = 0;

                    i = skipSpace( l, length, i );
                    if ( i == length )
                    {
                        // Stop after open line and skip it.
                        AptParser.this.nextLine();
                        break;
                    }
                    else if ( ( AptParser.charAt( l, length, i ) == COMMENT
                            && AptParser.charAt( l, length, i + 1 ) == COMMENT )
                            || type == COMMENT_BLOCK )
                    {
                        // parse comments as separate blocks line by line
                        break;
                    }

                    buffer.append( EOL );
                    buffer.append( l );

                    AptParser.this.nextLine();
                }

                text = buffer.toString();
                textLength = text.length();
            }
        }

        /**
         * Return the block type.
         *
         * @return int
         */
        public final int getType()
        {
            return type;
        }

        /**
         * Return the block indent.
         *
         * @return int
         */
        public final int getIndent()
        {
            return indent;
        }

        /**
         * Parse the block.
         *
         * @throws AptParseException if something goes wrong.
         */
        public abstract void traverse()
            throws AptParseException;

        /**
         * Traverse the text.
         *
         * @param begin offset.
         * @throws AptParseException if something goes wrong.
         */
        protected void traverseText( int begin )
            throws AptParseException
        {
            traverseText( begin, text.length() );
        }

        /**
         * Traverse the text.
         *
         * @param begin offset.
         * @param end offset.
         * @throws AptParseException if something goes wrong.
         */
        protected void traverseText( int begin, int end )
            throws AptParseException
        {
            AptParser.this.doTraverseText( text, begin, end, AptParser.this.sink );
        }

        /**
         * Skip spaces.
         *
         * @return int.
         */
        protected int skipLeadingBullets()
        {
            int i = skipSpaceFrom( 0 );
            for ( ; i < textLength; ++i )
            {
                if ( text.charAt( i ) != STAR )
                {
                    break;
                }
            }
            return skipSpaceFrom( i );
        }

        /**
         * Skip brackets.
         *
         * @param i offset.
         * @return int.
         * @throws AptParseException if something goes wrong.
         */
        protected int skipFromLeftToRightBracket( int i )
            throws AptParseException
        {
            char previous = LEFT_SQUARE_BRACKET;
            for ( ++i; i < textLength; ++i )
            {
                char c = text.charAt( i );
                if ( c == RIGHT_SQUARE_BRACKET && previous != BACKSLASH )
                {
                    break;
                }
                previous = c;
            }
            if ( i == textLength )
            {
                throw new AptParseException( "missing '" + RIGHT_SQUARE_BRACKET + "'" );
            }

            return i;
        }

        /**
         * Skip spaces.
         *
         * @param i offset.
         * @return int.
         */
        protected final int skipSpaceFrom( int i )
        {
            return AptParser.skipSpace( text, textLength, i );
        }
    }

    /** A ListBreak Block. */
    private class ListBreak
        extends AptParser.Block
    {
        /**
         * Constructor.
         *
         * @param indent indent.
         * @param firstLine the first line.
         * @throws AptParseException AptParseException
         */
        ListBreak( int indent, String firstLine )
            throws AptParseException
        {
            super( AptParser.LIST_BREAK, indent, firstLine );
        }

        /** {@inheritDoc} */
        public void traverse()
            throws AptParseException
        {
            throw new AptParseException( "internal error: traversing list break" );
        }
    }

    /** A Title Block. */
    private class Title
        extends Block
    {
        /**
         * Constructor.
         *
         * @param indent indent.
         * @param firstLine the first line.
         * @throws AptParseException AptParseException
         */
        Title( int indent, String firstLine )
            throws AptParseException
        {
            super( TITLE, indent, firstLine );
        }

        /** {@inheritDoc} */
        public void traverse()
            throws AptParseException
        {
            StringTokenizer lines = new StringTokenizer( text, EOL );
            int separator = -1;
            boolean firstLine = true;
            boolean title = false;
            boolean author = false;
            boolean date = false;

            loop: while ( lines.hasMoreTokens() )
            {
                String line = lines.nextToken().trim();
                int lineLength = line.length();

                if ( AptParser.charAt( line, lineLength, 0 ) == MINUS
                    && AptParser.charAt( line, lineLength, 1 ) == MINUS
                    && AptParser.charAt( line, lineLength, 2 ) == MINUS )
                {
                    switch ( separator )
                    {
                        case 0:
                            if ( title )
                            {
                                AptParser.this.sink.title_();
                            }
                            else
                            {
                                throw new AptParseException( "missing title" );
                            }
                            break;
                        case 1:
                            if ( author )
                            {
                                AptParser.this.sink.author_();
                            }
                            break;
                        case 2:
                            // Note that an extra decorative line is allowed
                            // at the end of the author.
                            break loop;
                        default:
                            break;
                    }

                    ++separator;
                    firstLine = true;
                }
                else
                {
                    if ( firstLine )
                    {
                        firstLine = false;
                        switch ( separator )
                        {
                            case 0:
                                title = true;
                                AptParser.this.sink.title();
                                break;
                            case 1:
                                author = true;
                                AptParser.this.sink.author();
                                break;
                            case 2:
                                date = true;
                                AptParser.this.sink.date();
                                break;
                            default:
                                break;
                        }
                    }
                    else
                    {
                        // An implicit lineBreak separates title lines.
                        AptParser.this.sink.lineBreak();
                    }

                    AptParser.this.doTraverseText( line, 0, lineLength, AptParser.this.sink );
                }
            }

            switch ( separator )
            {
                case 0:
                    if ( title )
                    {
                        AptParser.this.sink.title_();
                    }
                    else
                    {
                        throw new AptParseException( "missing title" );
                    }
                    break;
                case 1:
                    if ( author )
                    {
                        AptParser.this.sink.author_();
                    }
                    break;
                case 2:
                    if ( date )
                    {
                        AptParser.this.sink.date_();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /** A Section Block. */
    private abstract class Section
        extends Block
    {
        /**
         * Constructor.
         *
         * @param type type.
         * @param indent indent.
         * @param firstLine the first line.
         * @throws AptParseException AptParseException
         */
        Section( int type, int indent, String firstLine )
            throws AptParseException
        {
            super( type, indent, firstLine );
        }

        /** {@inheritDoc} */
        public void traverse()
            throws AptParseException
        {
            Title();
            traverseText( skipLeadingBullets() );
            Title_();
        }

        /** Start a title. */
        public abstract void Title();

        /** End a title. */
        public abstract void Title_();
    }

    /** A Section1 Block. */
    private class Section1
        extends Section
    {
        /**
         * Constructor.
         *
         * @param indent indent.
         * @param firstLine the first line.
         * @throws AptParseException AptParseException
         */
        Section1( int indent, String firstLine )
            throws AptParseException
        {
            super( SECTION1, indent, firstLine );
        }

        /** {@inheritDoc} */
        public void Title()
        {
            AptParser.this.sink.sectionTitle1();
        }

        /** {@inheritDoc} */
        public void Title_()
        {
            AptParser.this.sink.sectionTitle1_();
        }
    }

    /** A Section2 Block. */
    private class Section2
        extends Section
    {
        /**
         * Constructor.
         *
         * @param indent indent.
         * @param firstLine the first line.
         * @throws AptParseException AptParseException
         */
        Section2( int indent, String firstLine )
            throws AptParseException
        {
            super( SECTION2, indent, firstLine );
        }

        /** {@inheritDoc} */
        public void Title()
        {
            AptParser.this.sink.sectionTitle2();
        }

        /** {@inheritDoc} */
        public void Title_()
        {
            AptParser.this.sink.sectionTitle2_();
        }
    }

    /** A Section3 Block. */
    public class Section3
        extends Section
    {
        /**
         * Constructor.
         *
         * @param indent indent.
         * @param firstLine the first line.
         * @throws AptParseException AptParseException
         */
        Section3( int indent, String firstLine )
            throws AptParseException
        {
            super( SECTION3, indent, firstLine );
        }

        /** {@inheritDoc} */
        public void Title()
        {
            AptParser.this.sink.sectionTitle3();
        }

        /** {@inheritDoc} */
        public void Title_()
        {
            AptParser.this.sink.sectionTitle3_();
        }
    }

    /** A Section4 Block. */
    private class Section4
        extends Section
    {
        /**
         * Constructor.
         *
         * @param indent indent.
         * @param firstLine the first line.
         * @throws AptParseException AptParseException
         */
        Section4( int indent, String firstLine )
            throws AptParseException
        {
            super( SECTION4, indent, firstLine );
        }

        /** {@inheritDoc} */
        public void Title()
        {
            AptParser.this.sink.sectionTitle4();
        }

        /** {@inheritDoc} */
        public void Title_()
        {
            AptParser.this.sink.sectionTitle4_();
        }
    }

    /** A Section5 Block. */
    private class Section5
        extends Section
    {
        /**
         * Constructor.
         *
         * @param indent indent.
         * @param firstLine the first line.
         * @throws AptParseException AptParseException
         */
        Section5( int indent, String firstLine )
            throws AptParseException
        {
            super( SECTION5, indent, firstLine );
        }

        /** {@inheritDoc} */
        public void Title()
        {
            AptParser.this.sink.sectionTitle5();
        }

        /** {@inheritDoc} */
        public void Title_()
        {
            AptParser.this.sink.sectionTitle5_();
        }
    }

    /** A Paragraph Block. */
    private class Paragraph
        extends Block
    {
        /**
         * Constructor.
         *
         * @param indent indent.
         * @param firstLine the first line.
         * @throws AptParseException AptParseException
         */
        Paragraph( int indent, String firstLine )
            throws AptParseException
        {
            super( PARAGRAPH, indent, firstLine );
        }

        /** {@inheritDoc} */
        public void traverse()
            throws AptParseException
        {
            AptParser.this.sink.paragraph();
            traverseText( skipSpaceFrom( 0 ) );
            AptParser.this.sink.paragraph_();
        }
    }

    /** A Comment Block. */
    private class Comment
        extends Block
    {
        /**
         * Constructor.
         *
         * @param line the comment line.
         * @throws AptParseException AptParseException
         */
        Comment( String line )
            throws AptParseException
        {
            super( COMMENT_BLOCK, 0, line );
        }

        /** {@inheritDoc} */
        public void traverse()
            throws AptParseException
        {
            if ( isEmitComments() )
            {
                AptParser.this.sink.comment( text );
            }
        }
    }

    /** A Verbatim Block. */
    private class Verbatim
        extends Block
    {
        /** boxed. */
        private boolean boxed;

        /**
         * Constructor.
         *
         * @param indent indent.
         * @param firstLine the first line.
         * @throws AptParseException AptParseException
         */
        Verbatim( int indent, String firstLine )
            throws AptParseException
        {
            super( VERBATIM, indent, null );

            // Read block (first line already skipped) ---

            StringBuilder buffer = new StringBuilder();
            char firstChar = firstLine.charAt( 0 );
            boxed = ( firstChar == PLUS );

            while ( AptParser.this.line != null )
            {
                String l = AptParser.this.line;
                int length = l.length();

                if ( AptParser.charAt( l, length, 0 ) == firstChar && AptParser.charAt( l, length, 1 ) == MINUS
                    && AptParser.charAt( l, length, 2 ) == MINUS )
                {
                    AptParser.this.nextLine();

                    break;
                }

                // Expand tabs ---

                int prevColumn, column;

                column = 0;

                for ( int i = 0; i < length; ++i )
                {
                    char c = l.charAt( i );

                    if ( c == TAB )
                    {
                        prevColumn = column;

                        column = ( ( column + 1 + TAB_WIDTH - 1 ) / TAB_WIDTH ) * TAB_WIDTH;

                        buffer.append( SPACES, 0, column - prevColumn );
                    }
                    else
                    {
                        ++column;
                        buffer.append( c );
                    }
                }
                buffer.append( EOL );

                AptParser.this.nextLine();
            }

            // The last '\n' is mandatory before the "---" delimeter but is
            // not part of the verbatim text.
            textLength = buffer.length();

            if ( textLength > 0 )
            {
                --textLength;

                buffer.setLength( textLength );
            }

            text = buffer.toString();
        }

        /** {@inheritDoc} */
        public void traverse()
            throws AptParseException
        {
            AptParser.this.sink.verbatim( boxed ? SinkEventAttributeSet.BOXED : null );
            AptParser.this.sink.text( text );
            AptParser.this.sink.verbatim_();
        }
    }

    /** A Figure Block. */
    private class Figure
        extends Block
    {
        /**
         * Constructor.
         *
         * @param indent indent.
         * @param firstLine the first line.
         * @throws AptParseException AptParseException
         */
        Figure( int indent, String firstLine )
            throws AptParseException
        {
            super( FIGURE, indent, firstLine );
        }

        /** {@inheritDoc} */
        public void traverse()
            throws AptParseException
        {
            AptParser.this.sink.figure();

            int i = skipFromLeftToRightBracket( 0 );
            AptParser.this.sink.figureGraphics( text.substring( 1, i ) );

            i = skipSpaceFrom( i + 1 );
            if ( i < textLength )
            {
                AptParser.this.sink.figureCaption();
                traverseText( i );
                AptParser.this.sink.figureCaption_();
            }

            AptParser.this.sink.figure_();
        }
    }

    /** A Table Block. */
    private class Table
        extends Block
    {
        /**
         * Constructor.
         *
         * @param indent indent.
         * @param firstLine the first line.
         * @throws AptParseException AptParseException
         */
        Table( int indent, String firstLine )
            throws AptParseException
        {
            super( TABLE, indent, firstLine );
        }

        /** {@inheritDoc} */
        public void traverse()
            throws AptParseException
        {
            int captionIndex = -1;
            int nextLineIndex = 0;
            int init = 2;
            int[] justification = null;
            int rows = 0;
            int columns = 0;
            StringBuilder[] cells = null;
            boolean[] headers = null;
            boolean grid;

            AptParser.this.sink.table();

            while ( nextLineIndex < textLength )
            {
                int i = text.indexOf( "*--", nextLineIndex );
                if ( i < 0 )
                {
                    captionIndex = nextLineIndex;
                    break;
                }

                String line;
                i = text.indexOf( '\n', nextLineIndex );
                if ( i < 0 )
                {
                    line = text.substring( nextLineIndex );
                    nextLineIndex = textLength;
                }
                else
                {
                    line = text.substring( nextLineIndex, i );
                    nextLineIndex = i + 1;
                }
                int lineLength = line.length();

                if ( line.indexOf( "*--" ) == 0 )
                {
                    if ( init == 2 )
                    {
                        init = 1;
                        justification = parseJustification( line, lineLength );
                        columns = justification.length;
                        cells = new StringBuilder[columns];
                        headers = new boolean[columns];
                        for ( i = 0; i < columns; ++i )
                        {
                            cells[i] = new StringBuilder();
                            headers[i] = false;
                        }
                    }
                    else
                    {
                        if ( traverseRow( cells, headers, justification ) )
                        {
                            ++rows;
                        }
                        justification = parseJustification( line, lineLength );
                    }
                }
                else
                {
                    if ( init == 1 )
                    {
                        init = 0;
                        grid = ( AptParser.charAt( line, lineLength, 0 ) == PIPE );
                        AptParser.this.sink.tableRows( justification, grid );
                    }

                    line = replaceAll( line, "\\|", "\\u007C" );

                    StringTokenizer cellLines = new StringTokenizer( line, "|", true );

                    i = 0;
                    boolean processedGrid = false;
                    while ( cellLines.hasMoreTokens() )
                    {
                        String cellLine = cellLines.nextToken();
                        if ( "|".equals( cellLine ) )
                        {
                            if ( processedGrid )
                            {
                                headers[i] = true;
                            }
                            else
                            {
                                processedGrid = true;
                                headers[i] = false;
                            }
                            continue;
                        }
                        processedGrid = false;
                        cellLine = replaceAll( cellLine, "\\", "\\u00A0" ); // linebreak
                        // Escaped special characters: \~, \=, \-, \+, \*, \[, \], \<, \>, \{, \}, \\.
                        cellLine = replaceAll( cellLine, "\\u00A0~", "\\~" );
                        cellLine = replaceAll( cellLine, "\\u00A0=", "\\=" );
                        cellLine = replaceAll( cellLine, "\\u00A0-", "\\-" );
                        cellLine = replaceAll( cellLine, "\\u00A0+", "\\+" );
                        cellLine = replaceAll( cellLine, "\\u00A0*", "\\*" );
                        cellLine = replaceAll( cellLine, "\\u00A0[", "\\[" );
                        cellLine = replaceAll( cellLine, "\\u00A0]", "\\]" );
                        cellLine = replaceAll( cellLine, "\\u00A0<", "\\<" );
                        cellLine = replaceAll( cellLine, "\\u00A0>", "\\>" );
                        cellLine = replaceAll( cellLine, "\\u00A0{", "\\{" );
                        cellLine = replaceAll( cellLine, "\\u00A0}", "\\}" );
                        cellLine = replaceAll( cellLine, "\\u00A0u", "\\u" );
                        cellLine = replaceAll( cellLine, "\\u00A0\\u00A0", "\\\\" );
                        cellLine = cellLine.trim();

                        StringBuilder cell = cells[i];
                        if ( cellLine.length() > 0 )
                        {
                            // line break in table cells
                            if ( cell.toString().trim().endsWith( "\\u00A0" ) )
                            {
                                cell.append( "\\\n" );
                            }
                            else
                            {
                                if ( cell.length() != 0 )
                                {
                                    // Always add a space for multi line tables cells
                                    cell.append( " " );
                                }
                            }

                            cell.append( cellLine );
                        }

                        ++i;
                        if ( i == columns )
                        {
                            break;
                        }
                    }
                }
            }
            if ( rows == 0 )
            {
                throw new AptParseException( "no table rows" );
            }
            AptParser.this.sink.tableRows_();

            if ( captionIndex >= 0 )
            {
                AptParser.this.sink.tableCaption();
                AptParser.this.doTraverseText( text, captionIndex, textLength, AptParser.this.sink );
                AptParser.this.sink.tableCaption_();
            }

            AptParser.this.sink.table_();
        }

        /**
         * Parse a table justification line.
         *
         * @param jline the justification line.
         * @param lineLength the length of the line. Must be > 2.
         * @return int[]
         * @throws AptParseException if something goes wrong.
         */
        private int[] parseJustification( String jline, int lineLength )
            throws AptParseException
        {
            int columns = 0;

            for ( int i = 2 /*Skip '*--'*/; i < lineLength; ++i )
            {
                switch ( jline.charAt( i ) )
                {
                    case STAR:
                    case PLUS:
                    case COLON:
                        ++columns;
                        break;
                    default:
                        break;
                }
            }

            if ( columns == 0 )
            {
                throw new AptParseException( "no columns specified" );
            }

            int[] justification = new int[columns];
            columns = 0;
            for ( int i = 2; i < lineLength; ++i )
            {
                switch ( jline.charAt( i ) )
                {
                    case STAR:
                        justification[columns++] = Sink.JUSTIFY_CENTER;
                        break;
                    case PLUS:
                        justification[columns++] = Sink.JUSTIFY_LEFT;
                        break;
                    case COLON:
                        justification[columns++] = Sink.JUSTIFY_RIGHT;
                        break;
                    default:
                        break;
                }
            }

            return justification;
        }

        /**
         * Traverse a table row.
         *
         * @param cells The table cells.
         * @param headers true for header cells.
         * @param justification the justification for each cell.
         * @return boolean
         * @throws AptParseException if something goes wrong.
         */
        private boolean traverseRow( StringBuilder[] cells, boolean[] headers, int[] justification )
            throws AptParseException
        {
            // Skip empty row (a decorative line).
            boolean traversed = false;
            for ( StringBuilder cell1 : cells )
            {
                if ( cell1.length() > 0 )
                {
                    traversed = true;
                    break;
                }
            }

            if ( traversed )
            {
                AptParser.this.sink.tableRow();
                for ( int i = 0; i < cells.length; ++i )
                {
                    StringBuilder cell = cells[i];

                    SinkEventAttributes justif;
                    switch ( justification[i] )
                    {
                        case Sink.JUSTIFY_CENTER:
                            justif = SinkEventAttributeSet.CENTER;
                            break;
                        case Sink.JUSTIFY_LEFT:
                            justif = SinkEventAttributeSet.LEFT;
                            break;
                        case Sink.JUSTIFY_RIGHT:
                            justif = SinkEventAttributeSet.RIGHT;
                            break;
                        default:
                            justif = SinkEventAttributeSet.LEFT;
                            break;
                    }
                    SinkEventAttributeSet event = new SinkEventAttributeSet();
                    event.addAttributes( justif );

                    if ( headers[i] )
                    {
                        AptParser.this.sink.tableHeaderCell( event );
                    }
                    else
                    {
                        AptParser.this.sink.tableCell( event );
                    }
                    if ( cell.length() > 0 )
                    {
                        AptParser.this.doTraverseText( cell.toString(), 0, cell.length(), AptParser.this.sink );
                        cell.setLength( 0 );
                    }
                    if ( headers[i] )
                    {
                        AptParser.this.sink.tableHeaderCell_();
                        // DOXIA-404: reset header for next row
                        headers[i] = false;
                    }
                    else
                    {
                        AptParser.this.sink.tableCell_();
                    }
                }
                AptParser.this.sink.tableRow_();
            }

            return traversed;
        }
    }

    /** A ListItem Block. */
    private class ListItem
        extends Block
    {
        /**
         * Constructor.
         *
         * @param indent indent.
         * @param firstLine the first line.
         * @throws AptParseException AptParseException
         */
        ListItem( int indent, String firstLine )
            throws AptParseException
        {
            super( LIST_ITEM, indent, firstLine );
        }

        /** {@inheritDoc} */
        public void traverse()
            throws AptParseException
        {
            traverseText( skipLeadingBullets() );
        }
    }

    /** A NumberedListItem Block. */
    private class NumberedListItem
        extends Block
    {
        /** numbering. */
        private int numbering;

        /**
         * Constructor.
         *
         * @param indent indent.
         * @param firstLine the first line.
         * @param number numbering.
         * @throws AptParseException AptParseException
         */
        NumberedListItem( int indent, String firstLine, int number )
            throws AptParseException
        {
            super( NUMBERED_LIST_ITEM, indent, firstLine );
            this.numbering = number;
        }

        /**
         * getNumbering.
         *
         * @return int
         */
        public int getNumbering()
        {
            return numbering;
        }

        /** {@inheritDoc} */
        public void traverse()
            throws AptParseException
        {
            traverseText( skipItemNumber() );
        }

        /**
         * skipItemNumber.
         *
         * @return int
         * @throws AptParseException AptParseException
         */
        private int skipItemNumber()
            throws AptParseException
        {
            int i = skipSpaceFrom( 0 );

            char prevChar = SPACE;
            for ( ; i < textLength; ++i )
            {
                char c = text.charAt( i );
                if ( c == RIGHT_SQUARE_BRACKET && prevChar == RIGHT_SQUARE_BRACKET )
                {
                    break;
                }
                prevChar = c;
            }

            if ( i == textLength )
            {
                throw new AptParseException( "missing '" + RIGHT_SQUARE_BRACKET + RIGHT_SQUARE_BRACKET + "'" );
            }

            return skipSpaceFrom( i + 1 );
        }
    }

    /** A DefinitionListItem Block. */
    private class DefinitionListItem
        extends Block
    {
        /**
         * Constructor.
         *
         * @param indent indent.
         * @param firstLine the first line.
         * @throws AptParseException AptParseException
         */
        DefinitionListItem( int indent, String firstLine )
            throws AptParseException
        {
            super( DEFINITION_LIST_ITEM, indent, firstLine );
        }

        /** {@inheritDoc} */
        public void traverse()
            throws AptParseException
        {
            int i = skipSpaceFrom( 0 );
            int j = skipFromLeftToRightBracket( i );

            AptParser.this.sink.definedTerm();
            traverseText( i + 1, j );
            AptParser.this.sink.definedTerm_();

            j = skipSpaceFrom( j + 1 );
            if ( j == textLength )
            {
                // TODO: this doesn't handle the case of a dd in a paragraph
                //throw new AptParseException( "no definition" );
            }

            AptParser.this.sink.definition();
            traverseText( j );
        }
    }

    /** A HorizontalRule Block. */
    private class HorizontalRule
        extends Block
    {
        /**
         * Constructor.
         *
         * @param indent indent.
         * @param firstLine the first line.
         * @throws AptParseException AptParseException
         */
        HorizontalRule( int indent, String firstLine )
            throws AptParseException
        {
            super( HORIZONTAL_RULE, indent, firstLine );
        }

        /** {@inheritDoc} */
        public void traverse()
            throws AptParseException
        {
            AptParser.this.sink.horizontalRule();
        }
    }

    /** A PageBreak Block. */
    private class PageBreak
        extends Block
    {
        /**
         * Constructor.
         *
         * @param indent indent.
         * @param firstLine the first line.
         * @throws AptParseException AptParseException
         */
        PageBreak( int indent, String firstLine )
            throws AptParseException
        {
            super( PG_BREAK, indent, firstLine );
        }

        /** {@inheritDoc} */
        public void traverse()
            throws AptParseException
        {
            AptParser.this.sink.pageBreak();
        }
    }

    /** A MacroBlock Block. */
    private class MacroBlock
        extends Block
    {
        /**
         * Constructor.
         *
         * @param indent indent.
         * @param firstLine the first line.
         * @throws AptParseException AptParseException
         */
        MacroBlock( int indent, String firstLine )
            throws AptParseException
        {
            super( MACRO, indent );

            text = firstLine;
        }

        /** {@inheritDoc} */
        public void traverse()
            throws AptParseException
        {
            if ( isSecondParsing() )
            {
                return;
            }

            final int start = text.indexOf( '{' );
            final int end = text.indexOf( '}' );

            String s = text.substring( start + 1, end );

            s = escapeForMacro( s );

            String[] params = StringUtils.split( s, "|" );

            String macroId = params[0];

            Map<String, Object> parameters = new HashMap<>();

            for ( int i = 1; i < params.length; i++ )
            {
                String[] param = StringUtils.split( params[i], "=" );

                if ( param.length == 1 )
                {
                    throw new AptParseException( "Missing 'key=value' pair for macro parameter: " + params[i] );
                }

                String key = unescapeForMacro( param[0] );
                String value = unescapeForMacro( param[1] );

                parameters.put( key, value );
            }

            // getBasedir() does not work in multi-module builds, see DOXIA-373
            // the basedir should be injected from here, see DOXIA-224
            MacroRequest request = new MacroRequest( sourceContent, new AptParser(), parameters, getBasedir() );
            try
            {
                AptParser.this.executeMacro( macroId, request, sink );
            }
            catch ( MacroExecutionException e )
            {
                throw new AptParseException( "Unable to execute macro in the APT document", e );
            }
            catch ( MacroNotFoundException e )
            {
                throw new AptParseException( "Unable to find macro used in the APT document", e );
            }
        }

        /**
         * escapeForMacro
         *
         * @param s String
         * @return String
         */
        private String escapeForMacro( String s )
        {
            if ( s == null || s.length() < 1 )
            {
                return s;
            }

            String result = s;

            // use some outrageously out-of-place chars for text
            // (these are device control one/two in unicode)
            result = StringUtils.replace( result, "\\=", "\u0011" );
            result = StringUtils.replace( result, "\\|", "\u0012" );

            return result;
        }

        /**
         * unescapeForMacro
         *
         * @param s String
         * @return String
         */
        private String unescapeForMacro( String s )
        {
            if ( s == null || s.length() < 1 )
            {
                return s;
            }

            String result = s;

            result = StringUtils.replace( result, "\u0011", "=" );
            result = StringUtils.replace( result, "\u0012", "|" );

            return result;
        }
    }
}
