package org.apache.maven.doxia.parser;

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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;

import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.markup.XmlMarkup;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributeSet;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * An abstract class that defines some convenience methods for <code>XML</code> parsers.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractXmlParser
    extends AbstractParser
    implements XmlMarkup
{
    /** Entity pattern for HTML entity, i.e. &#38;nbsp; see http://www.w3.org/TR/REC-xml/#NT-EntityDecl */
    private static final Pattern PATTERN_ENTITY_1 =
        Pattern.compile( "<!ENTITY(\\s)+([^>|^\\s]+)(\\s)+\"(\\s)*(&[a-zA-Z]{2,6};)(\\s)*\"(\\s)*>" );

    /** Entity pattern for Unicode entity, i.e. &#38;#38; see http://www.w3.org/TR/REC-xml/#NT-EntityDecl */
    private static final Pattern PATTERN_ENTITY_2 =
        Pattern.compile( "<!ENTITY(\\s)+([^>|^\\s]+)(\\s)+\"(\\s)*(&#x?[0-9a-fA-F]{1,4};)(\\s)*\"(\\s)*>" );

    /** Doctype pattern as defined in http://www.w3.org/TR/REC-xml/#NT-doctypedecl */
    private static final Pattern PATTERN_DOCTYPE = Pattern.compile( ".*<!DOCTYPE([^>]*)>.*" );

    /** Tag pattern as defined in http://www.w3.org/TR/REC-xml/#NT-Name */
    private static final Pattern PATTERN_TAG = Pattern.compile( ".*<([A-Za-z][A-Za-z0-9:_.-]*)([^>]*)>.*" );

    private boolean ignorable;

    private boolean collapsible;

    private boolean trimmable;

    private Map entities;

    private boolean validate = true;

    /** lazy xmlReader to validate xml content*/
    private XMLReader xmlReader;

    /** {@inheritDoc} */
    public void parse( Reader source, Sink sink )
        throws ParseException
    {
        // 1 first parsing if validation is required
        if ( isValidate() )
        {
            String content;
            try
            {
                content = IOUtil.toString( new BufferedReader( source ) );
            }
            catch ( IOException e )
            {
                throw new ParseException( "Error reading the model: " + e.getMessage(), e );
            }

            validate( content );

            source = new StringReader( content );
        }

        // 2 second parsing to process
        try
        {
            XmlPullParser parser = new MXParser();

            parser.setInput( source );

            sink.enableLogging( getLog() );

            parseXml( parser, sink );
        }
        catch ( XmlPullParserException ex )
        {
            throw new ParseException( "Error parsing the model: " + ex.getMessage(), ex, ex.getLineNumber(),
                                      ex.getColumnNumber() );
        }
        catch ( MacroExecutionException ex )
        {
            throw new ParseException( "Macro execution failed: " + ex.getMessage(), ex );
        }
    }

    /**
     * Convenience method to parse an arbitrary string and emit any xml events into the given sink.
     *
     * @param string A string that provides the source input. The string has to be completely
     * enclosed inside one xml root element, otherwise a ParseException is thrown.
     * @param sink A sink that consumes the Doxia events.
     * @throws ParseException if the string does not represent a well-formed xml snippet.
     */
    public void parse( String string, Sink sink )
        throws ParseException
    {
        super.parse( string, sink );
    }

    /** {@inheritDoc} */
    public final int getType()
    {
        return XML_TYPE;
    }

    /**
     * Converts the attributes of the current start tag of the given parser to a SinkEventAttributeSet.
     *
     * @param parser A parser, not null.
     * @return a SinkEventAttributeSet or null if the current parser event is not a start tag.
     */
    protected SinkEventAttributeSet getAttributesFromParser( XmlPullParser parser )
    {
        int count = parser.getAttributeCount();

        if ( count < 0 )
        {
            return null;
        }

        SinkEventAttributeSet atts = new SinkEventAttributeSet( count );

        for ( int i = 0; i < count; i++ )
        {
            atts.addAttribute( parser.getAttributeName( i ), parser.getAttributeValue( i ) );
        }

        return atts;
    }

    /**
     * Parse the model from the XmlPullParser into the given sink.
     *
     * @param parser A parser, not null.
     * @param sink the sink to receive the events.
     * @throws org.codehaus.plexus.util.xml.pull.XmlPullParserException if there's a problem parsing the model
     * @throws org.apache.maven.doxia.macro.MacroExecutionException if there's a problem executing a macro
     */
    private void parseXml( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        int eventType = parser.getEventType();

        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
                handleStartTag( parser, sink );
            }
            else if ( eventType == XmlPullParser.END_TAG )
            {
                handleEndTag( parser, sink );
            }
            else if ( eventType == XmlPullParser.TEXT )
            {
                String text = getText( parser );

                if ( isIgnorableWhitespace() )
                {
                    if ( !text.trim().equals( "" ) )
                    {
                        handleText( parser, sink );
                    }
                }
                else
                {
                    handleText( parser, sink );
                }
            }
            else if ( eventType == XmlPullParser.CDSECT )
            {
                handleCdsect( parser, sink );
            }
            else if ( eventType == XmlPullParser.COMMENT )
            {
                handleComment( parser, sink );
            }
            else if ( eventType == XmlPullParser.ENTITY_REF )
            {
                handleEntity( parser, sink );
            }
            else if ( eventType == XmlPullParser.IGNORABLE_WHITESPACE )
            {
                // nop
            }
            else if ( eventType == XmlPullParser.PROCESSING_INSTRUCTION )
            {
                // nop
            }
            else if ( eventType == XmlPullParser.DOCDECL )
            {
                String text = parser.getText();
                int entitiesCount = StringUtils.countMatches( text, "<!ENTITY" );
                // entities defined in a local doctype
                if ( entitiesCount > 0 )
                {
                    int start = text.indexOf( "<" );
                    int end = text.lastIndexOf( ">" );
                    if ( start != -1 && end != -1 )
                    {
                        text = text.substring( start, end + 1 );
                        for ( int i = 0; i < entitiesCount; i++ )
                        {
                            String tmp = text.substring( text.indexOf( "<" ), text.indexOf( ">" ) + 1 );
                            Matcher matcher = PATTERN_ENTITY_1.matcher( tmp );
                            if ( matcher.find() && matcher.groupCount() == 7 )
                            {
                                String entityName = matcher.group( 2 );
                                String entityValue = matcher.group( 5 );

                                parser.defineEntityReplacementText( entityName, entityValue );
                                getLocalEntities().put( entityName, entityValue );
                            }
                            else
                            {
                                matcher = PATTERN_ENTITY_2.matcher( text );
                                if ( matcher.find() && matcher.groupCount() == 7 )
                                {
                                    String entityName = matcher.group( 2 );
                                    String entityValue = matcher.group( 5 );

                                    parser.defineEntityReplacementText( entityName, entityValue );
                                    getLocalEntities().put( entityName, entityValue );
                                }
                            }
                            text = StringUtils.replace( text, tmp, "" ).trim();
                        }
                    }
                }
            }

            try
            {
                eventType = parser.nextToken();
            }
            catch ( IOException io )
            {
                throw new XmlPullParserException( "IOException: " + io.getMessage(), parser, io );
            }
        }
    }

    /**
     * Goes through the possible start tags.
     *
     * @param parser A parser, not null.
     * @param sink the sink to receive the events.
     * @throws org.codehaus.plexus.util.xml.pull.XmlPullParserException if there's a problem parsing the model
     * @throws org.apache.maven.doxia.macro.MacroExecutionException if there's a problem executing a macro
     */
    protected abstract void handleStartTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException;

    /**
     * Goes through the possible end tags.
     *
     * @param parser A parser, not null.
     * @param sink the sink to receive the events.
     * @throws org.codehaus.plexus.util.xml.pull.XmlPullParserException if there's a problem parsing the model
     * @throws org.apache.maven.doxia.macro.MacroExecutionException if there's a problem executing a macro
     */
    protected abstract void handleEndTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException;

    /**
     * Handles text events.
     *
     * @param parser A parser, not null.
     * @param sink the sink to receive the events.
     * @throws org.codehaus.plexus.util.xml.pull.XmlPullParserException if there's a problem parsing the model
     */
    protected abstract void handleText( XmlPullParser parser, Sink sink )
        throws XmlPullParserException;

    /**
     * Handles CDATA sections.
     *
     * @param parser A parser, not null.
     * @param sink the sink to receive the events.
     * @throws org.codehaus.plexus.util.xml.pull.XmlPullParserException if there's a problem parsing the model
     */
    protected abstract void handleCdsect( XmlPullParser parser, Sink sink )
        throws XmlPullParserException;

    /**
     * Handles comments.
     *
     * @param parser A parser, not null.
     * @param sink the sink to receive the events.
     * @throws org.codehaus.plexus.util.xml.pull.XmlPullParserException if there's a problem parsing the model
     */
    protected abstract void handleComment( XmlPullParser parser, Sink sink )
        throws XmlPullParserException;

    /**
     * Handles entities.
     *
     * @param parser A parser, not null.
     * @param sink the sink to receive the events.
     * @throws org.codehaus.plexus.util.xml.pull.XmlPullParserException if there's a problem parsing the model
     */
    protected abstract void handleEntity( XmlPullParser parser, Sink sink )
        throws XmlPullParserException;

    /**
     * @return <code>true</code> if whitespace will be ignored, <code>false</code> otherwise.
     * @see #setIgnorableWhitespace(boolean)
     */
    protected boolean isIgnorableWhitespace()
    {
        return ignorable;
    }

    /**
     * Specify that whitespace will be ignore i.e.:
     * <pre>&lt;tr&gt; &lt;td/&gt; &lt;/tr&gt;</pre>
     * is equivalent to
     * <pre>&lt;tr&gt;&lt;td/&gt;&lt;/tr&gt;</pre>
     *
     * @param ignorable <code>true</code> to ignore whitespace, <code>false</code> otherwise.
     */
    protected void setIgnorableWhitespace( boolean ignorable )
    {
        this.ignorable = ignorable;
    }

    /**
     * @return <code>true</code> if text will collapse, <code>false</code> otherwise.
     * @see #setCollapsibleWhitespace(boolean)
     */
    protected boolean isCollapsibleWhitespace()
    {
        return collapsible;
    }

    /**
     * Specify that text will be collapse i.e.:
     * <pre>Text   Text</pre>
     * is equivalent to
     * <pre>Text Text</pre>
     *
     * @param collapsible <code>true</code> to allow collapsible text, <code>false</code> otherwise.
     */
    protected void setCollapsibleWhitespace( boolean collapsible )
    {
        this.collapsible = collapsible;
    }

    /**
     * @return <code>true</code> if text will be trim, <code>false</code> otherwise.
     * @see #setTrimmableWhitespace(boolean)
     */
    protected boolean isTrimmableWhitespace()
    {
        return trimmable;
    }

    /**
     * Specify that text will be collapse i.e.:
     * <pre>&lt;p&gt; Text &lt;/p&gt;</pre>
     * is equivalent to
     * <pre>&lt;p&gt;Text&lt;/p&gt;</pre>
     *
     * @param trimmable <code>true</code> to allow trimmable text, <code>false</code> otherwise.
     */
    protected void setTrimmableWhitespace( boolean trimmable )
    {
        this.trimmable = trimmable;
    }

    /**
     * @param parser A parser, not null.
     * @return the {@link XmlPullParser#getText()} taking care of trimmable or collapsible configuration.
     * @see XmlPullParser#getText()
     * @see #isCollapsibleWhitespace()
     * @see #isTrimmableWhitespace()
     */
    protected String getText( XmlPullParser parser )
    {
        String text = parser.getText();

        if ( isTrimmableWhitespace() )
        {
            text = text.trim();
        }

        if ( isCollapsibleWhitespace() )
        {
            StringBuffer newText = new StringBuffer();
            String[] elts = StringUtils.split( text, " \r\n" );
            for ( int i = 0; i < elts.length; i++ )
            {
                newText.append( elts[i] );
                if ( ( i + 1 ) < elts.length )
                {
                    newText.append( " " );
                }
            }
            text = newText.toString();
        }

        return text;
    }

    /**
     * Return the defined entities in a local doctype, i.e.:
     * <pre>
     * &lt;!DOCTYPE foo [
     *   &lt;!ENTITY bar "&#38;#x160;"&gt;
     *   &lt;!ENTITY bar1 "&#38;#x161;"&gt;
     * ]&gt;
     * </pre>
     *
     * @return a map of the defined entities in a local doctype.
     */
    protected Map getLocalEntities()
    {
        if ( entities == null )
        {
            entities = new LinkedHashMap();
        }

        return entities;
    }

    /**
     * @return <code>true</code> if XML content will be validate, <code>false</code> otherwise.
     */
    public boolean isValidate()
    {
        return validate;
    }

    /**
     * Specify a flag to validate or not the XML content.
     *
     * @param validate the validate to set
     * @see #parse(Reader, Sink)
     */
    public void setValidate( boolean validate )
    {
        this.validate = validate;
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * Validate an XML content with SAX.
     *
     * @param content a not null xml content
     * @throws ParseException if any.
     */
    private void validate( String content )
        throws ParseException
    {
        try
        {
            // 1 if there's a doctype
            boolean hasDoctype = false;
            Matcher matcher = PATTERN_DOCTYPE.matcher( content );
            if ( matcher.find() )
            {
                hasDoctype = true;
            }

            // 2 if no doctype, check for an xmlns instance
            boolean hasXsd = false;
            if ( !hasDoctype )
            {
                matcher = PATTERN_TAG.matcher( content );
                if ( matcher.find() )
                {
                    String value = matcher.group( 2 );

                    if ( value.indexOf( XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI ) != -1 )
                    {
                        hasXsd = true;
                    }
                }
            }

            // 3 validate content if doctype or xsd
            if ( hasDoctype || hasXsd )
            {
                if ( getLog().isDebugEnabled() )
                {
                    getLog().debug( "Validating the content..." );
                }
                getXmlReader().parse( new InputSource( new ByteArrayInputStream( content.getBytes() ) ) );
            }
        }
        catch ( IOException e )
        {
            throw new ParseException( "Error validating the model: " + e.getMessage(), e );
        }
        catch ( SAXNotRecognizedException e )
        {
            throw new ParseException( "Error validating the model: " + e.getMessage(), e );
        }
        catch ( SAXNotSupportedException e )
        {
            throw new ParseException( "Error validating the model: " + e.getMessage(), e );
        }
        catch ( SAXException e )
        {
            throw new ParseException( "Error validating the model: " + e.getMessage(), e );
        }
    }

    /**
     * @return an xmlReader instance.
     * @throws SAXException if any
     */
    private XMLReader getXmlReader()
        throws SAXException
    {
        if ( xmlReader == null )
        {
            MessagesErrorHandler errorHandler = new MessagesErrorHandler( getLog() );

            xmlReader = XMLReaderFactory.createXMLReader( "org.apache.xerces.parsers.SAXParser" );
            xmlReader.setFeature( "http://xml.org/sax/features/validation", true );
            xmlReader.setFeature( "http://apache.org/xml/features/validation/schema", true );
            xmlReader.setErrorHandler( errorHandler );
        }

        return xmlReader;
    }
}
