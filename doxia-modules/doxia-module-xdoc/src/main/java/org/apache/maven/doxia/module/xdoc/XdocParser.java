package org.apache.maven.doxia.module.xdoc;

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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.html.HTML.Attribute;

import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.macro.MacroRequest;
import org.apache.maven.doxia.macro.manager.MacroNotFoundException;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.parser.XhtmlBaseParser;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.util.HtmlTools;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Parse an xdoc model and emit events into the specified doxia Sink.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @since 1.0
 */
@Component( role = Parser.class, hint = "xdoc" )
public class XdocParser
    extends XhtmlBaseParser
    implements XdocMarkup
{
    /**
     * The source content of the input reader. Used to pass into macros.
     */
    private String sourceContent;

    /**
     * Empty elements don't write a closing tag.
     */
    private boolean isEmptyElement;

    /**
     * A macro name.
     */
    private String macroName;

    /**
     * The macro parameters.
     */
    private Map<String, Object> macroParameters = new HashMap<>();

    /**
     * Indicates that we're inside &lt;properties&gt; or &lt;head&gt;.
     */
    private boolean inHead;

    /**
     * Indicates that &lt;title&gt; was called from &lt;properties&gt; or &lt;head&gt;.
     */
    private boolean hasTitle;

    /**
     * {@inheritDoc}
     */
    public void parse( Reader source, Sink sink )
        throws ParseException
    {
        this.sourceContent = null;

        try
        {
            StringWriter contentWriter = new StringWriter();
            IOUtil.copy( source, contentWriter );
            sourceContent = contentWriter.toString();
        }
        catch ( IOException ex )
        {
            throw new ParseException( "Error reading the input source: " + ex.getMessage(), ex );
        }
        finally
        {
            IOUtil.close( source );
        }

        // leave this at default (false) until everything is properly implemented, see DOXIA-226
        //setIgnorableWhitespace( true );

        try
        {
            super.parse( new StringReader( sourceContent ), sink );
        }
        finally
        {
            this.sourceContent = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void handleStartTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        isEmptyElement = parser.isEmptyElementTag();

        SinkEventAttributeSet attribs = getAttributesFromParser( parser );

        if ( parser.getName().equals( DOCUMENT_TAG.toString() ) )
        {
            //Do nothing
            return;
        }
        else if ( parser.getName().equals( HEAD.toString() ) )
        {
            if ( !inHead ) // we might be in head from a <properties> already
            {
                this.inHead = true;

                sink.head( attribs );
            }
        }
        else if ( parser.getName().equals( TITLE.toString() ) )
        {
            if ( hasTitle )
            {
                getLog().warn( "<title> was already defined in <properties>, ignored <title> in <head>." );

                try
                {
                    parser.nextText(); // ignore next text event
                }
                catch ( IOException ex )
                {
                    throw new XmlPullParserException( "Failed to parse text", parser, ex );
                }
            }
            else
            {
                sink.title( attribs );
            }
        }
        else if ( parser.getName().equals( AUTHOR_TAG.toString() ) )
        {
            sink.author( attribs );
        }
        else if ( parser.getName().equals( DATE_TAG.toString() ) )
        {
            sink.date( attribs );
        }
        else if ( parser.getName().equals( META.toString() ) )
        {
            handleMetaStart( parser, sink, attribs );
        }
        else if ( parser.getName().equals( BODY.toString() ) )
        {
            if ( inHead )
            {
                sink.head_();
                this.inHead = false;
            }

            sink.body( attribs );
        }
        else if ( parser.getName().equals( SECTION_TAG.toString() ) )
        {
            handleSectionStart( Sink.SECTION_LEVEL_1, sink, attribs, parser );
        }
        else if ( parser.getName().equals( SUBSECTION_TAG.toString() ) )
        {
            handleSectionStart( Sink.SECTION_LEVEL_2, sink, attribs, parser );
        }
        else if ( parser.getName().equals( SOURCE_TAG.toString() ) )
        {
            verbatim();

            attribs.addAttributes( SinkEventAttributeSet.BOXED );

            sink.verbatim( attribs );
        }
        else if ( parser.getName().equals( PROPERTIES_TAG.toString() ) )
        {
            if ( !inHead ) // we might be in head from a <head> already
            {
                this.inHead = true;

                sink.head( attribs );
            }
        }

        // ----------------------------------------------------------------------
        // Macro
        // ----------------------------------------------------------------------

        else if ( parser.getName().equals( MACRO_TAG.toString() ) )
        {
            handleMacroStart( parser );
        }
        else if ( parser.getName().equals( PARAM.toString() ) )
        {
            handleParamStart( parser, sink );
        }
        else if ( !baseStartTag( parser, sink ) )
        {
            if ( isEmptyElement )
            {
                handleUnknown( parser, sink, TAG_TYPE_SIMPLE );
            }
            else
            {
                handleUnknown( parser, sink, TAG_TYPE_START );
            }

            if ( getLog().isDebugEnabled() )
            {
                String position = "[" + parser.getLineNumber() + ":" + parser.getColumnNumber() + "]";
                String tag = "<" + parser.getName() + ">";

                getLog().debug( "Unrecognized xdoc tag: " + tag + " at " + position );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void handleEndTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        if ( parser.getName().equals( DOCUMENT_TAG.toString() ) )
        {
            //Do nothing
            return;
        }
        else if ( parser.getName().equals( HEAD.toString() ) )
        {
            //Do nothing, head is closed with BODY start.
        }
        else if ( parser.getName().equals( BODY.toString() ) )
        {
            consecutiveSections( 0, sink );

            sink.body_();
        }
        else if ( parser.getName().equals( TITLE.toString() ) )
        {
            if ( !hasTitle )
            {
                sink.title_();
                this.hasTitle = true;
            }
        }
        else if ( parser.getName().equals( AUTHOR_TAG.toString() ) )
        {
            sink.author_();
        }
        else if ( parser.getName().equals( DATE_TAG.toString() ) )
        {
            sink.date_();
        }
        else if ( parser.getName().equals( SOURCE_TAG.toString() ) )
        {
            verbatim_();

            sink.verbatim_();
        }
        else if ( parser.getName().equals( PROPERTIES_TAG.toString() ) )
        {
            //Do nothing, head is closed with BODY start.
        }
        else if ( parser.getName().equals( MACRO_TAG.toString() ) )
        {
            handleMacroEnd( sink );
        }
        else if ( parser.getName().equals( PARAM.toString() ) )
        {
            if ( !StringUtils.isNotEmpty( macroName ) )
            {
                handleUnknown( parser, sink, TAG_TYPE_END );
            }
        }
        else if ( parser.getName().equals( SECTION_TAG.toString() ) )
        {
            consecutiveSections( 0, sink );

            sink.section1_();
        }
        else if ( parser.getName().equals( SUBSECTION_TAG.toString() ) )
        {
            consecutiveSections( Sink.SECTION_LEVEL_1, sink );
        }
        else if ( !baseEndTag( parser, sink ) )
        {
            if ( !isEmptyElement )
            {
                handleUnknown( parser, sink, TAG_TYPE_END );
            }
        }

        isEmptyElement = false;
    }

    /**
     * {@inheritDoc}
     */
    protected void consecutiveSections( int newLevel, Sink sink )
    {
        closeOpenSections( newLevel, sink );
        openMissingSections( newLevel, sink );

        setSectionLevel( newLevel );
    }

    /**
     * {@inheritDoc}
     */
    protected void init()
    {
        super.init();

        this.isEmptyElement = false;
        this.macroName = null;
        this.macroParameters = null;
        this.inHead = false;
        this.hasTitle = false;
    }

    /**
     * Close open h4, h5, h6 sections.
     */
    private void closeOpenSections( int newLevel, Sink sink )
    {
        while ( getSectionLevel() >= newLevel )
        {
            if ( getSectionLevel() == Sink.SECTION_LEVEL_5 )
            {
                sink.section5_();
            }
            else if ( getSectionLevel() == Sink.SECTION_LEVEL_4 )
            {
                sink.section4_();
            }
            else if ( getSectionLevel() == Sink.SECTION_LEVEL_3 )
            {
                sink.section3_();
            }
            else if ( getSectionLevel() == Sink.SECTION_LEVEL_2 )
            {
                sink.section2_();
            }

            setSectionLevel( getSectionLevel() - 1 );
        }
    }

    private void handleMacroEnd( Sink sink )
        throws MacroExecutionException
    {
        if ( !isSecondParsing() && StringUtils.isNotEmpty( macroName ) )
        {
            MacroRequest request =
                new MacroRequest( sourceContent, new XdocParser(), macroParameters, getBasedir() );

            try
            {
                executeMacro( macroName, request, sink );
            }
            catch ( MacroNotFoundException me )
            {
                throw new MacroExecutionException( "Macro not found: " + macroName, me );
            }
        }

        // Reinit macro
        macroName = null;
        macroParameters = null;
    }

    private void handleMacroStart( XmlPullParser parser )
        throws MacroExecutionException
    {
        if ( !isSecondParsing() )
        {
            macroName = parser.getAttributeValue( null, Attribute.NAME.toString() );

            if ( macroParameters == null )
            {
                macroParameters = new HashMap<>();
            }

            if ( StringUtils.isEmpty( macroName ) )
            {
                throw new MacroExecutionException(
                    "The '" + Attribute.NAME.toString() + "' attribute for the '" + MACRO_TAG.toString()
                        + "' tag is required." );
            }
        }
    }

    private void handleMetaStart( XmlPullParser parser, Sink sink, SinkEventAttributeSet attribs )
    {
        String name = parser.getAttributeValue( null, Attribute.NAME.toString() );
        String content = parser.getAttributeValue( null, Attribute.CONTENT.toString() );

        if ( "author".equals( name ) )
        {
            sink.author( null );
            sink.text( content );
            sink.author_();
        }
        else if ( "date".equals( name ) )
        {
            sink.date( null );
            sink.text( content );
            sink.date_();
        }
        else
        {
            sink.unknown( "meta", new Object[]{ TAG_TYPE_SIMPLE }, attribs );
        }
    }

    private void handleParamStart( XmlPullParser parser, Sink sink )
        throws MacroExecutionException
    {
        if ( !isSecondParsing() )
        {
            if ( StringUtils.isNotEmpty( macroName ) )
            {
                String paramName = parser.getAttributeValue( null, Attribute.NAME.toString() );
                String paramValue = parser.getAttributeValue( null, Attribute.VALUE.toString() );

                if ( StringUtils.isEmpty( paramName ) || StringUtils.isEmpty( paramValue ) )
                {
                    throw new MacroExecutionException(
                        "'" + Attribute.NAME.toString() + "' and '" + Attribute.VALUE.toString()
                            + "' attributes for the '" + PARAM.toString() + "' tag are required inside the '"
                            + MACRO_TAG.toString() + "' tag." );
                }

                macroParameters.put( paramName, paramValue );
            }
            else
            {
                // param tag from non-macro object, see MSITE-288
                handleUnknown( parser, sink, TAG_TYPE_START );
            }
        }
    }

    private void handleSectionStart( int level, Sink sink, SinkEventAttributeSet attribs, XmlPullParser parser )
    {
        consecutiveSections( level, sink );

        Object id = attribs.getAttribute( Attribute.ID.toString() );

        if ( id != null )
        {
            sink.anchor( id.toString() );
            sink.anchor_();
        }

        sink.section( level, attribs );
        sink.sectionTitle( level, null );
        sink.text( HtmlTools.unescapeHTML( parser.getAttributeValue( null, Attribute.NAME.toString() ) ) );
        sink.sectionTitle_( level );
    }

    /**
     * Open missing h4, h5, h6 sections.
     */
    private void openMissingSections( int newLevel, Sink sink )
    {
        while ( getSectionLevel() < newLevel - 1 )
        {
            setSectionLevel( getSectionLevel() + 1 );

            if ( getSectionLevel() == Sink.SECTION_LEVEL_5 )
            {
                sink.section5();
            }
            else if ( getSectionLevel() == Sink.SECTION_LEVEL_4 )
            {
                sink.section4();
            }
            else if ( getSectionLevel() == Sink.SECTION_LEVEL_3 )
            {
                sink.section3();
            }
            else if ( getSectionLevel() == Sink.SECTION_LEVEL_2 )
            {
                sink.section2();
            }
        }
    }
}
