package org.apache.maven.doxia.module.apt;

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

import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.macro.MacroRequest;
import org.apache.maven.doxia.macro.manager.MacroNotFoundException;
import org.apache.maven.doxia.parser.AbstractParser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkAdapter;
import org.codehaus.plexus.util.StringUtils;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @plexus.component role="org.apache.maven.doxia.parser.Parser" role-hint="apt"
 */
public class AptParser
    extends AbstractParser
{
    private static final String EOL = System.getProperty( "line.separator" );

    private static final int TITLE = 0;

    private static final int SECTION1 = 1;

    private static final int SECTION2 = 2;

    private static final int SECTION3 = 3;

    private static final int SECTION4 = 4;

    private static final int SECTION5 = 5;

    private static final int PARAGRAPH = 6;

    private static final int VERBATIM = 7;

    private static final int FIGURE = 8;

    private static final int TABLE = 9;

    private static final int LIST_ITEM = 10;

    private static final int NUMBERED_LIST_ITEM = 11;

    private static final int DEFINITION_LIST_ITEM = 12;

    private static final int HORIZONTAL_RULE = 13;

    private static final int PAGE_BREAK = 14;

    private static final int LIST_BREAK = 15;

    private static final int MACRO = 16;

    private static final String typeNames[] = {"TITLE", "SECTION1", "SECTION2", "SECTION3", "SECTION4", "SECTION5",
        "PARAGRAPH", "VERBATIM", "FIGURE", "TABLE", "LIST_ITEM", "NUMBERED_LIST_ITEM", "DEFINITION_LIST_ITEM",
        "HORIZONTAL_RULE", "PAGE_BREAK", "LIST_BREAK", "MACRO"};

    private static final char spaces[] = {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
        ' ', ' ', ' ', ' '};

    public static final int TAB_WIDTH = 8;

    // -----------------------------------------------------------------------

    private AptSource source;

    private Sink sink;

    private String line;

    private Block block;

    private String blockFileName;

    private int blockLineNumber;

    // -----------------------------------------------------------------------

    public void parse( Reader source, Sink sink )
        throws AptParseException
    {
        try
        {
            this.source = new AptReaderSource( source );

            this.sink = sink;

            blockFileName = null;

            blockLineNumber = -1;

            // Lookahead line.
            nextLine();

            // Lookahead block.
            nextBlock( /*first*/ true );

            traverseHead();

            traverseBody();

            this.source = null;

            this.sink = null;
        }
        catch ( AptParseException ape )
        {
            throw new AptParseException( ape.getMessage(), getSourceName(), getSourceLineNumber(), ape );
        }
    }

    public String getSourceName()
    {
        // Use this rather than source.getName() to report errors.
        return blockFileName;
    }

    public int getSourceLineNumber()
    {
        // Use this rather than source.getLineNumber() to report errors.
        return blockLineNumber;
    }

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
        }
    }

    private void traverseSectionBlocks()
        throws AptParseException
    {
        loop:
        while ( block != null )
        {
            switch ( block.getType() )
            {
                case PARAGRAPH:
                case VERBATIM:
                case FIGURE:
                case TABLE:
                case HORIZONTAL_RULE:
                case PAGE_BREAK:
                case MACRO:
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

        loop:
        while ( block != null )
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
                case PAGE_BREAK:
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

        loop:
        while ( block != null )
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
                case PAGE_BREAK:
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

        loop:
        while ( block != null )
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
                case PAGE_BREAK:
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

    private void nextLine()
        throws AptParseException
    {
        line = source.getNextLine();
    }

    private void nextBlock()
        throws AptParseException
    {
        nextBlock( /*first*/ false );
    }

    private void nextBlock( boolean firstBlock )
        throws AptParseException
    {
        // Skip open and comment lines.
        int length, indent, i;

        skipLoop:
        for ( ; ; )
        {
            if ( line == null )
            {
                block = null;
                return;
            }

            length = line.length();
            indent = 0;
            lineLoop:
            for ( i = 0; i < length; ++i )
            {
                switch ( line.charAt( i ) )
                {
                    case ' ':
                        ++indent;
                        break;
                    case '\t':
                        indent += 8;
                        break;
                    case '~':
                        if ( charAt( line, length, i + 1 ) == '~' )
                        {
                            // Comment.
                            i = length;
                            break lineLoop;
                        }
                        else
                        {
                            break skipLoop;
                        }
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
            case '*':
                if ( indent == 0 )
                {
                    if ( charAt( line, length, i + 1 ) == '-' && charAt( line, length, i + 2 ) == '-' )
                    {
                        block = new Table( indent, line );
                    }
                    else if ( charAt( line, length, i + 1 ) == '*' )
                    {
                        if ( charAt( line, length, i + 2 ) == '*' )
                        {
                            if ( charAt( line, length, i + 3 ) == '*' )
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
            case '[':
                if ( charAt( line, length, i + 1 ) == ']' )
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
                        if ( charAt( line, length, i + 1 ) == '[' )
                        {
                            int numbering;

                            switch ( charAt( line, length, i + 2 ) )
                            {
                                case 'a':
                                    numbering = Sink.NUMBERING_LOWER_ALPHA;
                                    break;
                                case 'A':
                                    numbering = Sink.NUMBERING_UPPER_ALPHA;
                                    break;
                                case 'i':
                                    numbering = Sink.NUMBERING_LOWER_ROMAN;
                                    break;
                                case 'I':
                                    numbering = Sink.NUMBERING_UPPER_ROMAN;
                                    break;
                                case '1':
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
            case '-':
                if ( charAt( line, length, i + 1 ) == '-' && charAt( line, length, i + 2 ) == '-' )
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
            case '+':
                if ( indent == 0 && charAt( line, length, i + 1 ) == '-' && charAt( line, length, i + 2 ) == '-' )
                {
                    block = new Verbatim( indent, line );
                }
                break;
            case '=':
                if ( indent == 0 && charAt( line, length, i + 1 ) == '=' && charAt( line, length, i + 2 ) == '=' )
                {
                    block = new HorizontalRule( indent, line );
                }
                break;
            case '\f':
                if ( indent == 0 )
                {
                    block = new PageBreak( indent, line );
                }
                break;
            case '%':
                if ( indent == 0 && charAt( line, length, i + 1 ) == '{' )
                {
                    block = new MacroBlock( indent, line );
                }
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

    private void expectedBlock( int type )
        throws AptParseException
    {
        int blockType = block.getType();

        if ( blockType != type )
        {
            throw new AptParseException(
                "expected " + typeNames[type] + ", found " + typeNames[blockType] );
        }
    }

    // -----------------------------------------------------------------------

    private static boolean isOctalChar( char c )
    {
        return ( c >= '0' && c <= '7' );
    }

    private static boolean isHexChar( char c )
    {
        return ( ( c >= '0' && c <= '9' ) || ( c >= 'a' && c <= 'f' ) || ( c >= 'A' && c <= 'F' ) );
    }

    private static char charAt( String string, int length, int i )
    {
        return ( i < length ) ? string.charAt( i ) : '\0';
    }

    private static int skipSpace( String string, int length, int i )
    {
        loop:
        for ( ; i < length; ++i )
        {
            switch ( string.charAt( i ) )
            {
                case ' ':
                case '\t':
                    break;
                default:
                    break loop;
            }
        }
        return i;
    }

    private static void doTraverseText( String text, int begin, int end, Sink sink )
        throws AptParseException
    {
        boolean anchor = false;
        boolean link = false;
        boolean italic = false;
        boolean bold = false;
        boolean monospaced = false;
        StringBuffer buffer = new StringBuffer( end - begin );

        for ( int i = begin; i < end; ++i )
        {
            char c = text.charAt( i );
            switch ( c )
            {
                case '\\':
                    if ( i + 1 < end )
                    {
                        char escaped = text.charAt( i + 1 );
                        switch ( escaped )
                        {
                            case ' ':
                                ++i;
                                flushTraversed( buffer, sink );
                                sink.nonBreakingSpace();
                                break;
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
                            case '\\':
                            case '|':
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
                                ++i;
                                buffer.append( escaped );
                                break;
                            case 'x':
                                if ( i + 3 < end && isHexChar( text.charAt( i + 2 ) ) &&
                                    isHexChar( text.charAt( i + 3 ) ) )
                                {
                                    int value = '?';
                                    try
                                    {
                                        value = Integer.parseInt( text.substring( i + 2, i + 4 ), 16 );
                                    }
                                    catch ( NumberFormatException e )
                                    {
                                    }

                                    i += 3;
                                    buffer.append( (char) value );
                                }
                                else
                                {
                                    buffer.append( '\\' );
                                }
                                break;
                            case 'u':
                                if ( i + 5 < end && isHexChar( text.charAt( i + 2 ) ) &&
                                    isHexChar( text.charAt( i + 3 ) ) && isHexChar( text.charAt( i + 4 ) ) &&
                                    isHexChar( text.charAt( i + 5 ) ) )
                                {
                                    int value = '?';
                                    try
                                    {
                                        value = Integer.parseInt( text.substring( i + 2, i + 6 ), 16 );
                                    }
                                    catch ( NumberFormatException e )
                                    {
                                    }

                                    i += 5;
                                    buffer.append( (char) value );
                                }
                                else
                                {
                                    buffer.append( '\\' );
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
                                    }

                                    i += octalChars;
                                    buffer.append( (char) value );
                                }
                                else
                                {
                                    buffer.append( '\\' );
                                }
                        }
                    }
                    else
                    {
                        buffer.append( '\\' );
                    }
                    break;

                case '{': /*}*/
                    if ( !anchor && !link )
                    {
                        if ( i + 1 < end && text.charAt( i + 1 ) == '{' /*}*/ )
                        {
                            ++i;
                            link = true;
                            flushTraversed( buffer, sink );

                            String linkAnchor = null;

                            if ( i + 1 < end && text.charAt( i + 1 ) == '{' /*}*/ )
                            {
                                ++i;
                                StringBuffer buf = new StringBuffer();
                                i = skipTraversedLinkAnchor( text, i + 1, end, buf );
                                linkAnchor = buf.toString();
                            }

                            if ( linkAnchor == null )
                            {
                                linkAnchor = getTraversedLink( text, i + 1, end );
                            }

                            sink.link( linkAnchor );
                        }
                        else
                        {
                            anchor = true;
                            flushTraversed( buffer, sink );
                            sink.anchor( getTraversedAnchor( text, i + 1, end ) );
                        }
                    }
                    else
                    {
                        buffer.append( c );
                    }
                    break;

                case /*{*/ '}':
                    if ( link && i + 1 < end && text.charAt( i + 1 ) == /*{*/ '}' )
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

                case '<':
                    if ( !italic && !bold && !monospaced )
                    {
                        if ( i + 1 < end && text.charAt( i + 1 ) == '<' )
                        {
                            if ( i + 2 < end && text.charAt( i + 2 ) == '<' )
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

                case '>':
                    if ( monospaced && i + 2 < end && text.charAt( i + 1 ) == '>' && text.charAt( i + 2 ) == '>' )
                    {
                        i += 2;
                        monospaced = false;
                        flushTraversed( buffer, sink );
                        sink.monospaced_();
                    }
                    else if ( bold && i + 1 < end && text.charAt( i + 1 ) == '>' )
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
                        buffer.append( ' ' );

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
            throw new AptParseException( "missing '>>>'" );
        }
        if ( bold )
        {
            throw new AptParseException( "missing '>>'" );
        }
        if ( italic )
        {
            throw new AptParseException( "missing '>'" );
        }
        if ( link )
        {
            throw new AptParseException( "missing '}}'" );
        }
        if ( anchor )
        {
            throw new AptParseException( "missing '}'" );
        }

        flushTraversed( buffer, sink );
    }

    private static void flushTraversed( StringBuffer buffer, Sink sink )
    {
        if ( buffer.length() > 0 )
        {
            sink.text( buffer.toString() );
            buffer.setLength( 0 );
        }
    }

    private static int skipTraversedLinkAnchor( String text, int begin, int end, StringBuffer linkAnchor )
        throws AptParseException
    {
        int i;
        loop:
        for ( i = begin; i < end; ++i )
        {
            char c = text.charAt( i );
            switch ( c )
            {
                case '}':
                    break loop;
                case '\\':
                    if ( i + 1 < end )
                    {
                        ++i;
                        linkAnchor.append( text.charAt( i ) );
                    }
                    else
                    {
                        linkAnchor.append( '\\' );
                    }
                    break;
                default:
                    linkAnchor.append( c );
            }
        }
        if ( i == end )
        {
            throw new AptParseException( "missing '}'" );
        }

        return i;
    }

    private static String getTraversedLink( String text, int begin, int end )
        throws AptParseException
    {
        char previous2 = '{';
        char previous = '{';
        int i;

        for ( i = begin; i < end; ++i )
        {
            char c = text.charAt( i );
            if ( c == '}' && previous == '}' && previous2 != '\\' )
            {
                break;
            }

            previous2 = previous;
            previous = c;
        }
        if ( i == end )
        {
            throw new AptParseException( "missing '}}'" );
        }

        return doGetTraversedLink( text, begin, i - 1 );
    }

    private static String getTraversedAnchor( String text, int begin, int end )
        throws AptParseException
    {
        char previous = '{';
        int i;

        for ( i = begin; i < end; ++i )
        {
            char c = text.charAt( i );
            if ( c == '}' && previous != '\\' )
            {
                break;
            }

            previous = c;
        }
        if ( i == end )
        {
            throw new AptParseException( "missing '}'" );
        }

        return doGetTraversedLink( text, begin, i );
    }

    private static String doGetTraversedLink( String text, int begin, int end )
        throws AptParseException
    {
        final StringBuffer buffer = new StringBuffer( end - begin );

        Sink sink = new SinkAdapter()
        {
            public void lineBreak()
            {
                buffer.append( ' ' );
            }

            public void nonBreakingSpace()
            {
                buffer.append( ' ' );
            }

            public void text( String text )
            {
                buffer.append( text );
            }
        };
        doTraverseText( text, begin, end, sink );

        return buffer.toString().trim();
    }

    // -----------------------------------------------------------------------

    private abstract class Block
    {
        protected int type;

        protected int indent;

        protected String text;

        protected int textLength;

        public Block( int type, int indent )
            throws AptParseException
        {
            this( type, indent, null );
        }

        public Block( int type, int indent, String firstLine )
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
                StringBuffer buffer = new StringBuffer( firstLine );

                while ( AptParser.this.line != null )
                {
                    String l = AptParser.this.line;
                    int length = l.length();
                    int i = 0;

                    i = skipSpace( l, length, i );
                    if ( i == length ||
                        ( AptParser.charAt( l, length, i ) == '~' && AptParser.charAt( l, length, i + 1 ) == '~' ) )
                    {
                        // Stop after open or comment line and skip it.
                        // (A comment line is considered to be an open line.)
                        AptParser.this.nextLine();
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

        public final int getType()
        {
            return type;
        }

        public final int getIndent()
        {
            return indent;
        }

        public abstract void traverse()
            throws AptParseException;

        protected void traverseText( int begin )
            throws AptParseException
        {
            traverseText( begin, text.length() );
        }

        protected void traverseText( int begin, int end )
            throws AptParseException
        {
            AptParser.doTraverseText( text, begin, end, AptParser.this.sink );
        }

        protected int skipLeadingBullets()
        {
            int i = skipSpaceFrom( 0 );
            for ( ; i < textLength; ++i )
            {
                if ( text.charAt( i ) != '*' )
                {
                    break;
                }
            }
            return skipSpaceFrom( i );
        }

        protected int skipFromLeftToRightBracket( int i )
            throws AptParseException
        {
            char previous = '[';
            for ( ++i; i < textLength; ++i )
            {
                char c = text.charAt( i );
                if ( c == ']' && previous != '\\' )
                {
                    break;
                }
                previous = c;
            }
            if ( i == textLength )
            {
                throw new AptParseException( "missing ']'" );
            }

            return i;
        }

        protected final int skipSpaceFrom( int i )
        {
            return AptParser.skipSpace( text, textLength, i );
        }
    }

    private class ListBreak
        extends AptParser.Block
    {
        public ListBreak( int indent, String firstLine )
            throws AptParseException
        {
            super( AptParser.LIST_BREAK, indent );
        }

        public void traverse()
            throws AptParseException
        {
            throw new AptParseException( "internal error: traversing list break" );
        }
    }

    private class Title
        extends Block
    {
        public Title( int indent, String firstLine )
            throws AptParseException
        {
            super( TITLE, indent, firstLine );
        }

        public void traverse()
            throws AptParseException
        {
            StringTokenizer lines = new StringTokenizer( text, EOL );
            int separator = -1;
            boolean firstLine = true;
            boolean title = false;
            boolean author = false;
            boolean date = false;

            loop:
            while ( lines.hasMoreTokens() )
            {
                String line = lines.nextToken().trim();
                int lineLength = line.length();

                if ( AptParser.charAt( line, lineLength, 0 ) == '-' && AptParser.charAt( line, lineLength, 1 ) == '-' &&
                    AptParser.charAt( line, lineLength, 2 ) == '-' )
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
                        }
                    }
                    else
                    {
                        // An implicit lineBreak separates title lines.
                        AptParser.this.sink.lineBreak();
                    }

                    AptParser.doTraverseText( line, 0, lineLength, AptParser.this.sink );
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
            }
        }
    }

    private class Section
        extends Block
    {
        public Section( int type, int indent, String firstLine )
            throws AptParseException
        {
            super( type, indent, firstLine );
        }

        public void traverse()
            throws AptParseException
        {
            Title();
            traverseText( skipLeadingBullets() );
            Title_();
        }

        public void Title()
        {
            AptParser.this.sink.sectionTitle();
        }

        public void Title_()
        {
            AptParser.this.sink.sectionTitle_();
        }
    }

    private class Section1
        extends Section
    {
        public Section1( int indent, String firstLine )
            throws AptParseException
        {
            super( SECTION1, indent, firstLine );
        }

        public void Title()
        {
            AptParser.this.sink.sectionTitle1();
        }

        public void Title_()
        {
            AptParser.this.sink.sectionTitle1_();
        }
    }

    private class Section2
        extends Section
    {
        public Section2( int indent, String firstLine )
            throws AptParseException
        {
            super( SECTION2, indent, firstLine );
        }

        public void Title()
        {
            AptParser.this.sink.sectionTitle2();
        }

        public void Title_()
        {
            AptParser.this.sink.sectionTitle2_();
        }
    }

    private class Section3
        extends Section
    {
        public Section3( int indent, String firstLine )
            throws AptParseException
        {
            super( SECTION3, indent, firstLine );
        }

        public void Title()
        {
            AptParser.this.sink.sectionTitle3();
        }

        public void Title_()
        {
            AptParser.this.sink.sectionTitle3_();
        }
    }

    private class Section4
        extends Section
    {
        public Section4( int indent, String firstLine )
            throws AptParseException
        {
            super( SECTION4, indent, firstLine );
        }

        public void Title()
        {
            AptParser.this.sink.sectionTitle4();
        }

        public void Title_()
        {
            AptParser.this.sink.sectionTitle4_();
        }
    }

    private class Section5
        extends Section
    {
        public Section5( int indent, String firstLine )
            throws AptParseException
        {
            super( SECTION5, indent, firstLine );
        }

        public void Title()
        {
            AptParser.this.sink.sectionTitle5();
        }

        public void Title_()
        {
            AptParser.this.sink.sectionTitle5_();
        }
    }

    private class Paragraph
        extends Block
    {
        public Paragraph( int indent, String firstLine )
            throws AptParseException
        {
            super( PARAGRAPH, indent, firstLine );
        }

        public void traverse()
            throws AptParseException
        {
            AptParser.this.sink.paragraph();
            traverseText( skipSpaceFrom( 0 ) );
            AptParser.this.sink.paragraph_();
        }
    }

    private class Verbatim
        extends Block
    {
        private boolean boxed;

        public Verbatim( int indent, String firstLine )
            throws AptParseException
        {
            super( VERBATIM, indent, null );

            // Read block (first line already skipped) ---

            StringBuffer buffer = new StringBuffer();
            char firstChar = firstLine.charAt( 0 );
            boxed = ( firstChar == '+' );

            while ( AptParser.this.line != null )
            {
                String l = AptParser.this.line;
                int length = l.length();

                if ( AptParser.charAt( l, length, 0 ) == firstChar && AptParser.charAt( l, length, 1 ) == '-' &&
                    AptParser.charAt( l, length, 2 ) == '-' )
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

                    if ( c == '\t' )
                    {
                        prevColumn = column;

                        column = ( ( column + 1 + TAB_WIDTH - 1 ) / TAB_WIDTH ) * TAB_WIDTH;

                        buffer.append( spaces, 0, column - prevColumn );
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

        public void traverse()
            throws AptParseException
        {
            AptParser.this.sink.verbatim( boxed );
            AptParser.this.sink.text( text );
            AptParser.this.sink.verbatim_();
        }
    }

    private class Figure
        extends Block
    {
        public Figure( int indent, String firstLine )
            throws AptParseException
        {
            super( FIGURE, indent, firstLine );
        }

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

    private class Table
        extends Block
    {
        public Table( int indent, String firstLine )
            throws AptParseException
        {
            super( TABLE, indent, firstLine );
        }

        public void traverse()
            throws AptParseException
        {
            int captionIndex = -1;
            int nextLineIndex = 0;
            int init = 2;
            int[] justification = null;
            int rows = 0;
            int columns = 0;
            StringBuffer[] cells = null;
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
                        cells = new StringBuffer[columns];
                        headers = new boolean[columns];
                        for ( i = 0; i < columns; ++i )
                        {
                            cells[i] = new StringBuffer();
                            headers[i] = false;
                        }
                    }
                    else
                    {
                        if ( traverseRow( cells, headers ) )
                        {
                            ++rows;
                        }
                    }
                }
                else
                {
                    if ( init == 1 )
                    {
                        init = 0;
                        grid = ( AptParser.charAt( line, lineLength, 0 ) == '|' );
                        AptParser.this.sink.tableRows( justification, grid );
                    }

                    line = replaceAll( line, "\\|", "\\174" );

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
                        cellLine = replaceAll( cellLine, "\\ ", "\\240" );
                        cellLine = cellLine.trim();

                        StringBuffer cell = cells[i];

                        if ( cellLine.length() > 0 )
                        {
                            if ( cell.length() > 0 )
                            // Implicit lineBreak if multi-line cell.
                            {
                                cell.append( "\\\n" );
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
                AptParser.doTraverseText( text, captionIndex, textLength, AptParser.this.sink );
                AptParser.this.sink.tableCaption_();
            }

            AptParser.this.sink.table_();
        }

        private int[] parseJustification( String line, int lineLength )
            throws AptParseException
        {
            int columns = 0;

            for ( int i = 2 /*Skip '*--'*/; i < lineLength; ++i )
            {
                switch ( line.charAt( i ) )
                {
                    case '*':
                    case '+':
                    case ':':
                        ++columns;
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
                switch ( line.charAt( i ) )
                {
                    case '*':
                        justification[columns++] = JUSTIFY_CENTER;
                        break;
                    case '+':
                        justification[columns++] = JUSTIFY_LEFT;
                        break;
                    case ':':
                        justification[columns++] = JUSTIFY_RIGHT;
                        break;
                }
            }

            return justification;
        }

        private boolean traverseRow( StringBuffer[] cells, boolean[] headers )
            throws AptParseException
        {
            // Skip empty row (a decorative line).
            boolean traversed = false;
            for ( int i = 0; i < cells.length; ++i )
            {
                if ( cells[i].length() > 0 )
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
                    StringBuffer cell = cells[i];
                    if ( headers[i] )
                    {
                        AptParser.this.sink.tableHeaderCell();
                    }
                    else
                    {
                        AptParser.this.sink.tableCell();
                    }
                    if ( cell.length() > 0 )
                    {
                        AptParser.doTraverseText( cell.toString(), 0, cell.length(), AptParser.this.sink );
                        cell.setLength( 0 );
                    }
                    if ( headers[i] )
                    {
                        AptParser.this.sink.tableHeaderCell_();
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

    private class ListItem
        extends Block
    {
        public ListItem( int indent, String firstLine )
            throws AptParseException
        {
            super( LIST_ITEM, indent, firstLine );
        }

        public void traverse()
            throws AptParseException
        {
            AptParser.this.sink.paragraph();
            traverseText( skipLeadingBullets() );
            AptParser.this.sink.paragraph_();
        }
    }

    private class NumberedListItem
        extends Block
    {
        private int numbering;

        public NumberedListItem( int indent, String firstLine, int numbering )
            throws AptParseException
        {
            super( NUMBERED_LIST_ITEM, indent, firstLine );
            this.numbering = numbering;
        }

        public int getNumbering()
        {
            return numbering;
        }

        public void traverse()
            throws AptParseException
        {
            AptParser.this.sink.paragraph();
            traverseText( skipItemNumber() );
            AptParser.this.sink.paragraph_();
        }

        private int skipItemNumber()
            throws AptParseException
        {
            int i = skipSpaceFrom( 0 );

            char prevChar = ' ';
            for ( ; i < textLength; ++i )
            {
                char c = text.charAt( i );
                if ( c == ']' && prevChar == ']' )
                {
                    break;
                }
                prevChar = c;
            }

            if ( i == textLength )
            {
                throw new AptParseException( "missing ']]'" );
            }

            return skipSpaceFrom( i + 1 );
        }
    }

    private class DefinitionListItem
        extends Block
    {
        public DefinitionListItem( int indent, String firstLine )
            throws AptParseException
        {
            super( DEFINITION_LIST_ITEM, indent, firstLine );
        }

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
                throw new AptParseException( "no definition" );
            }

            AptParser.this.sink.definition();
            AptParser.this.sink.paragraph();
            traverseText( j );
            AptParser.this.sink.paragraph_();
        }
    }

    private class HorizontalRule
        extends Block
    {
        public HorizontalRule( int indent, String firstLine )
            throws AptParseException
        {
            super( HORIZONTAL_RULE, indent );
        }

        public void traverse()
            throws AptParseException
        {
            AptParser.this.sink.horizontalRule();
        }
    }

    private class PageBreak
        extends Block
    {
        public PageBreak( int indent, String firstLine )
            throws AptParseException
        {
            super( PAGE_BREAK, indent );
        }

        public void traverse()
            throws AptParseException
        {
            AptParser.this.sink.pageBreak();
        }
    }

    private class MacroBlock
        extends Block
    {
        public MacroBlock( int indent, String firstLine )
            throws AptParseException
        {
            super( MACRO, indent );

            text = firstLine;
        }

        public void traverse()
            throws AptParseException
        {
            String s = text;

            s = s.substring( 2, s.length() - 1 );

            String[] params = StringUtils.split( s, "|" );

            String macroId = params[0];

            Map parameters = new HashMap();

            for ( int i = 1; i < params.length; i++ )
            {
                String[] param = StringUtils.split( params[i], "=" );

                parameters.put( param[0], param[1] );
            }

            MacroRequest request = new MacroRequest( parameters, getBasedir() );

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
    }

    // -----------------------------------------------------------------------

    private static String replaceAll( String string, String oldSub, String newSub )
    {
        StringBuffer replaced = new StringBuffer();
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
}
