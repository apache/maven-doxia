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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;

import org.apache.maven.doxia.logging.Log;
import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.markup.XmlMarkup;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributeSet;
import org.apache.maven.doxia.util.HtmlTools;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
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
    /**
     * Entity pattern for HTML entity, i.e. &#38;nbsp;
     * "<!ENTITY(\\s)+([^>|^\\s]+)(\\s)+\"(\\s)*(&[a-zA-Z]{2,6};)(\\s)*\"(\\s)*>
     * <br/>
     * see <a href="http://www.w3.org/TR/REC-xml/#NT-EntityDecl">http://www.w3.org/TR/REC-xml/#NT-EntityDecl</a>.
     */
    private static final Pattern PATTERN_ENTITY_1 =
        Pattern.compile( ENTITY_START + "(\\s)+([^>|^\\s]+)(\\s)+\"(\\s)*(&[a-zA-Z]{2,6};)(\\s)*\"(\\s)*>" );

    /**
     * Entity pattern for Unicode entity, i.e. &#38;#38;
     * "<!ENTITY(\\s)+([^>|^\\s]+)(\\s)+\"(\\s)*(&(#x?[0-9a-fA-F]{1,5};)*)(\\s)*\"(\\s)*>"
     * <br/>
     * see <a href="http://www.w3.org/TR/REC-xml/#NT-EntityDecl">http://www.w3.org/TR/REC-xml/#NT-EntityDecl</a>.
     */
    private static final Pattern PATTERN_ENTITY_2 =
        Pattern.compile( ENTITY_START + "(\\s)+([^>|^\\s]+)(\\s)+\"(\\s)*(&(#x?[0-9a-fA-F]{1,5};)*)(\\s)*\"(\\s)*>" );

    /**
     * Doctype pattern i.e. ".*<!DOCTYPE([^>]*)>.*"
     * see <a href="http://www.w3.org/TR/REC-xml/#NT-doctypedecl">http://www.w3.org/TR/REC-xml/#NT-doctypedecl</a>.
     */
    private static final Pattern PATTERN_DOCTYPE = Pattern.compile( ".*" + DOCTYPE_START + "([^>]*)>.*" );

    /** Tag pattern as defined in http://www.w3.org/TR/REC-xml/#NT-Name */
    private static final Pattern PATTERN_TAG = Pattern.compile( ".*<([A-Za-z][A-Za-z0-9:_.-]*)([^>]*)>.*" );

    private boolean ignorableWhitespace;

    private boolean collapsibleWhitespace;

    private boolean trimmableWhitespace;

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
            XmlPullParser parser = new DoxiaMXParser();

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
     * {@inheritDoc}
     *
     * Convenience method to parse an arbitrary string and emit any xml events into the given sink.
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
     * @since 1.1
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
                addLocalEntities( parser, parser.getText() );

                for ( Iterator it = CachedFileEntityResolver.ENTITY_CACHE.values().iterator(); it.hasNext(); )
                {
                    byte[] res = (byte[]) it.next();

                    addDTDEntities( parser, new String( res ) );
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
     * <p>This is a default implementation, if the parser points to a non-empty text element,
     * it is emitted as a text event into the specified sink.</p>
     *
     * @param parser A parser, not null.
     * @param sink the sink to receive the events. Not null.
     * @throws org.codehaus.plexus.util.xml.pull.XmlPullParserException if there's a problem parsing the model
     */
    protected void handleText( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = getText( parser );

        /*
         * NOTE: Don't do any whitespace trimming here. Whitespace normalization has already been performed by the
         * parser so any whitespace that makes it here is significant.
         */
        if ( StringUtils.isNotEmpty( text ) )
        {
            sink.text( text );
        }
    }

    /**
     * Handles CDATA sections.
     *
     * <p>This is a default implementation, all data are emitted as text
     * events into the specified sink.</p>
     *
     * @param parser A parser, not null.
     * @param sink the sink to receive the events. Not null.
     * @throws org.codehaus.plexus.util.xml.pull.XmlPullParserException if there's a problem parsing the model
     */
    protected void handleCdsect( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        sink.text( getText( parser ) );
    }

    /**
     * Handles comments.
     *
     * <p>This is a default implementation, all data are emitted as comment
     * events into the specified sink.</p>
     *
     * @param parser A parser, not null.
     * @param sink the sink to receive the events. Not null.
     * @throws org.codehaus.plexus.util.xml.pull.XmlPullParserException if there's a problem parsing the model
     */
    protected void handleComment( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        sink.comment( getText( parser ).trim() );
    }

    /**
     * Handles entities.
     *
     * <p>This is a default implementation, all entities are resolved and emitted as text
     * events into the specified sink, except:</p>
     * <ul>
     * <li>the entities with names <code>#160</code>, <code>nbsp</code> and <code>#x00A0</code>
     * are emitted as <code>nonBreakingSpace()</code> events.</li>
     * </ul>
     *
     * @param parser A parser, not null.
     * @param sink the sink to receive the events. Not null.
     * @throws org.codehaus.plexus.util.xml.pull.XmlPullParserException if there's a problem parsing the model
     */
    protected void handleEntity( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = getText( parser );

        String name = parser.getName();

        if ( "#160".equals( name ) || "nbsp".equals( name ) || "#x00A0".equals( name ) )
        {
            sink.nonBreakingSpace();
        }
        else
        {
            String unescaped = HtmlTools.unescapeHTML( text );

            sink.text( unescaped );
        }
    }

    /**
     * Handles an unkown event.
     *
     * <p>This is a default implementation, all events are emitted as unknown
     * events into the specified sink.</p>
     *
     * @param parser the parser to get the event from.
     * @param sink the sink to receive the event.
     * @param type the tag event type. This should be one of HtmlMarkup.TAG_TYPE_SIMPLE,
     * HtmlMarkup.TAG_TYPE_START, HtmlMarkup.TAG_TYPE_END or HtmlMarkup.ENTITY_TYPE.
     * It will be passed as the first argument of the required parameters to the Sink
     * {@link org.apache.maven.doxia.sink.Sink#unknown(String, Object[], SinkEventAttributes)}
     * method.
     */
    protected void handleUnknown( XmlPullParser parser, Sink sink, int type )
    {
        Object[] required = new Object[] { new Integer( type ) };

        SinkEventAttributeSet attribs = getAttributesFromParser( parser );

        sink.unknown( parser.getName(), required, attribs );
    }

    /**
     * <p>isIgnorableWhitespace</p>
     *
     * @return <code>true</code> if whitespace will be ignored, <code>false</code> otherwise.
     * @see #setIgnorableWhitespace(boolean)
     * @since 1.1
     */
    protected boolean isIgnorableWhitespace()
    {
        return ignorableWhitespace;
    }

    /**
     * Specify that whitespace will be ignore i.e.:
     * <pre>&lt;tr&gt; &lt;td/&gt; &lt;/tr&gt;</pre>
     * is equivalent to
     * <pre>&lt;tr&gt;&lt;td/&gt;&lt;/tr&gt;</pre>
     *
     * @param ignorable <code>true</code> to ignore whitespace, <code>false</code> otherwise.
     * @since 1.1
     */
    protected void setIgnorableWhitespace( boolean ignorable )
    {
        this.ignorableWhitespace = ignorable;
    }

    /**
     * <p>isCollapsibleWhitespace</p>
     *
     * @return <code>true</code> if text will collapse, <code>false</code> otherwise.
     * @see #setCollapsibleWhitespace(boolean)
     * @since 1.1
     */
    protected boolean isCollapsibleWhitespace()
    {
        return collapsibleWhitespace;
    }

    /**
     * Specify that text will be collapse i.e.:
     * <pre>Text   Text</pre>
     * is equivalent to
     * <pre>Text Text</pre>
     *
     * @param collapsible <code>true</code> to allow collapsible text, <code>false</code> otherwise.
     * @since 1.1
     */
    protected void setCollapsibleWhitespace( boolean collapsible )
    {
        this.collapsibleWhitespace = collapsible;
    }

    /**
     * <p>isTrimmableWhitespace</p>
     *
     * @return <code>true</code> if text will be trim, <code>false</code> otherwise.
     * @see #setTrimmableWhitespace(boolean)
     * @since 1.1
     */
    protected boolean isTrimmableWhitespace()
    {
        return trimmableWhitespace;
    }

    /**
     * Specify that text will be collapse i.e.:
     * <pre>&lt;p&gt; Text &lt;/p&gt;</pre>
     * is equivalent to
     * <pre>&lt;p&gt;Text&lt;/p&gt;</pre>
     *
     * @param trimmable <code>true</code> to allow trimmable text, <code>false</code> otherwise.
     * @since 1.1
     */
    protected void setTrimmableWhitespace( boolean trimmable )
    {
        this.trimmableWhitespace = trimmable;
    }

    /**
     * <p>getText</p>
     *
     * @param parser A parser, not null.
     * @return the {@link XmlPullParser#getText()} taking care of trimmable or collapsible configuration.
     * @see XmlPullParser#getText()
     * @see #isCollapsibleWhitespace()
     * @see #isTrimmableWhitespace()
     * @since 1.1
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
     * @since 1.1
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
     * <p>isValidate</p>
     *
     * @return <code>true</code> if XML content will be validate, <code>false</code> otherwise.
     * @since 1.1
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
     * @since 1.1
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

            // 2 check for an xmlns instance
            boolean hasXsd = false;
            matcher = PATTERN_TAG.matcher( content );
            if ( matcher.find() )
            {
                String value = matcher.group( 2 );

                if ( value.indexOf( XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI ) != -1 )
                {
                    hasXsd = true;
                }
            }

            // 3 validate content if doctype or xsd
            if ( hasDoctype || hasXsd )
            {
                if ( getLog().isDebugEnabled() )
                {
                    getLog().debug( "Validating the content..." );
                }
                getXmlReader( hasXsd && hasDoctype ).parse( new InputSource( new StringReader( content ) ) );
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
     * @param hasDtdAndXsd to flag the <code>ErrorHandler</code>.
     * @return an xmlReader instance.
     * @throws SAXException if any
     */
    private XMLReader getXmlReader( boolean hasDtdAndXsd )
        throws SAXException
    {
        if ( xmlReader == null )
        {
            MessagesErrorHandler errorHandler = new MessagesErrorHandler( getLog() );

            xmlReader = XMLReaderFactory.createXMLReader( "org.apache.xerces.parsers.SAXParser" );
            xmlReader.setFeature( "http://xml.org/sax/features/validation", true );
            xmlReader.setFeature( "http://apache.org/xml/features/validation/schema", true );
            xmlReader.setErrorHandler( errorHandler );
            xmlReader.setEntityResolver( new CachedFileEntityResolver() );
        }

        ( (MessagesErrorHandler) xmlReader.getErrorHandler() ).setHasDtdAndXsd( hasDtdAndXsd );

        return xmlReader;
    }

    /**
     * Add an entity given by <code>entityName</code> and <code>entityValue</code> to {@link #entities}.
     * <br/>
     * By default, we exclude the default XML entities: &#38;amp;, &#38;lt;, &#38;gt;, &#38;quot; and &#38;apos;.
     *
     * @param parser not null
     * @param entityName not null
     * @param entityValue not null
     * @throws XmlPullParserException if any
     * @see {@link XmlPullParser#defineEntityReplacementText(String, String)}
     */
    private void addEntity( XmlPullParser parser, String entityName, String entityValue )
        throws XmlPullParserException
    {
        if ( entityName.endsWith( "amp" ) || entityName.endsWith( "lt" ) || entityName.endsWith( "gt" )
            || entityName.endsWith( "quot" ) || entityName.endsWith( "apos" ) )
        {
            return;
        }

        parser.defineEntityReplacementText( entityName, entityValue );
        getLocalEntities().put( entityName, entityValue );
    }

    /**
     * Handle entities defined in a local doctype as the following:
     * <pre>
     * &lt;!DOCTYPE foo [
     *   &lt;!ENTITY bar "&#38;#x160;"&gt;
     *   &lt;!ENTITY bar1 "&#38;#x161;"&gt;
     * ]&gt;
     * </pre>
     *
     * @param parser not null
     * @param text not null
     * @throws XmlPullParserException if any
     */
    private void addLocalEntities( XmlPullParser parser, String text )
        throws XmlPullParserException
    {
        int entitiesCount = StringUtils.countMatches( text, ENTITY_START );
        if ( entitiesCount > 0 )
        {
            // text should be foo [...]
            int start = text.indexOf( "[" );
            int end = text.lastIndexOf( "]" );
            if ( start != -1 && end != -1 )
            {
                text = text.substring( start + 1, end );
                addDTDEntities( parser, text );
            }
        }
    }

    /**
     * Handle entities defined in external doctypes as the following:
     * <pre>
     * &lt;!DOCTYPE foo [
     *   &lt;!-- These are the entity sets for ISO Latin 1 characters for the XHTML --&gt;
     *   &lt;!ENTITY % HTMLlat1 PUBLIC "-//W3C//ENTITIES Latin 1 for XHTML//EN"
     *          "http://www.w3.org/TR/xhtml1/DTD/xhtml-lat1.ent"&gt;
     *   %HTMLlat1;
     * ]&gt;
     * </pre>
     *
     * @param parser not null
     * @param text not null
     * @throws XmlPullParserException if any
     */
    private void addDTDEntities( XmlPullParser parser, String text )
        throws XmlPullParserException
    {
        int entitiesCount = StringUtils.countMatches( text, ENTITY_START );
        if ( entitiesCount > 0 )
        {
            text = StringUtils.replace( text, ENTITY_START, "\n" + ENTITY_START );
            BufferedReader reader = new BufferedReader( new StringReader( text ) );
            String line;
            String tmpLine = "";
            try
            {
                Matcher matcher;
                while ( ( line = reader.readLine() ) != null )
                {
                    tmpLine += "\n" + line;
                    matcher = PATTERN_ENTITY_1.matcher( tmpLine );
                    if ( matcher.find() && matcher.groupCount() == 7 )
                    {
                        String entityName = matcher.group( 2 );
                        String entityValue = matcher.group( 5 );

                        addEntity( parser, entityName, entityValue );
                        tmpLine = "";
                    }
                    else
                    {
                        matcher = PATTERN_ENTITY_2.matcher( tmpLine );
                        if ( matcher.find() && matcher.groupCount() == 8 )
                        {
                            String entityName = matcher.group( 2 );
                            String entityValue = matcher.group( 5 );

                            addEntity( parser, entityName, entityValue );
                            tmpLine = "";
                        }
                    }
                }
            }
            catch ( IOException e )
            {
                // nop
            }
            finally
            {
                IOUtil.close( reader );
            }
        }
    }

    /**
     * Convenience class to beautify <code>SAXParseException</code> messages.
     */
    static class MessagesErrorHandler
        extends DefaultHandler
    {
        private static final int TYPE_UNKNOWN = 0;

        private static final int TYPE_WARNING = 1;

        private static final int TYPE_ERROR = 2;

        private static final int TYPE_FATAL = 3;

        /** @see org/apache/xerces/impl/msg/XMLMessages.properties#MSG_ELEMENT_NOT_DECLARED */
        private static final Pattern ELEMENT_TYPE_PATTERN =
            Pattern.compile( "Element type \".*\" must be declared.", Pattern.DOTALL );

        private final Log log;

        private boolean hasDtdAndXsd;

        public MessagesErrorHandler( Log log )
        {
            this.log = log;
        }

        /**
         * @param hasDtdAndXsd the hasDtdAndXsd to set
         */
        protected void setHasDtdAndXsd( boolean hasDtdAndXsd )
        {
            this.hasDtdAndXsd = hasDtdAndXsd;
        }

        /** {@inheritDoc} */
        public void warning( SAXParseException e )
            throws SAXException
        {
            processException( TYPE_WARNING, e );
        }

        /** {@inheritDoc} */
        public void error( SAXParseException e )
            throws SAXException
        {
            // Workaround for Xerces complaints when an XML with XSD needs also a <!DOCTYPE []> to specify entities
            // like &nbsp;
            // See http://xsd.stylusstudio.com/2001Nov/post08021.htm
            if ( !hasDtdAndXsd )
            {
                processException( TYPE_ERROR, e );
                return;
            }

            Matcher m = ELEMENT_TYPE_PATTERN.matcher( e.getMessage() );
            if ( !m.find() )
            {
                processException( TYPE_ERROR, e );
            }
        }

        /** {@inheritDoc} */
        public void fatalError( SAXParseException e )
            throws SAXException
        {
            processException( TYPE_FATAL, e );
        }

        private void processException( int type, SAXParseException e )
            throws SAXException
        {
            StringBuffer message = new StringBuffer();

            switch ( type )
            {
                case TYPE_WARNING:
                    message.append( "Warning:" );
                    break;

                case TYPE_ERROR:
                    message.append( "Error:" );
                    break;

                case TYPE_FATAL:
                    message.append( "Fatal error:" );
                    break;

                case TYPE_UNKNOWN:
                default:
                    message.append( "Unknown:" );
                    break;
            }

            message.append( EOL );
            message.append( "  Public ID: " + e.getPublicId() ).append( EOL );
            message.append( "  System ID: " + e.getSystemId() ).append( EOL );
            message.append( "  Line number: " + e.getLineNumber() ).append( EOL );
            message.append( "  Column number: " + e.getColumnNumber() ).append( EOL );
            message.append( "  Message: " + e.getMessage() ).append( EOL );

            switch ( type )
            {
                case TYPE_WARNING:
                    if ( log.isWarnEnabled() )
                    {
                        log.warn( message.toString() );
                    }
                    break;

                case TYPE_UNKNOWN:
                case TYPE_ERROR:
                case TYPE_FATAL:
                default:
                    throw new SAXException( message.toString() );
            }
        }
    }

    /**
     * Implementation of the callback mechanism <code>EntityResolver</code>.
     * Using a mechanism of cached files in temp dir to improve performance when using the <code>XMLReader</code>.
     */
    public static class CachedFileEntityResolver
        implements EntityResolver
    {
        /** Map with systemId as key and the content of systemId as byte[]. */
        protected static final Map ENTITY_CACHE = new Hashtable();

        /** {@inheritDoc} */
        public InputSource resolveEntity( String publicId, String systemId )
            throws SAXException, IOException
        {
            byte[] res = (byte[]) ENTITY_CACHE.get( systemId );
            // already cached?
            if ( res == null )
            {
                String systemName = FileUtils.getFile( systemId ).getName();
                File temp = new File( System.getProperty( "java.io.tmpdir" ), systemName );
                // maybe already as a temp file?
                if ( !temp.exists() )
                {
                    // is systemId a file or an url?
                    if ( systemId.toLowerCase( Locale.ENGLISH ).startsWith( "file" ) )
                    {
                        // Doxia XSDs are included in the jars, so try to find the resource systemName from
                        // the classpath...
                        URL url = getClass().getResource( "/" + systemName );
                        if ( url != null )
                        {
                            res = toByteArray( url );
                        }
                        else
                        {
                            throw new SAXException( "Could not find the SYSTEM entity: " + systemId );
                        }
                    }
                    else
                    {
                        res = toByteArray( new URL( systemId ) );
                    }

                    // write systemId as temp file
                    copy( res, temp );
                }
                else
                {
                    // TODO How to refresh Doxia XSDs from temp dir?
                    res = toByteArray( temp.toURI().toURL() );
                }

                ENTITY_CACHE.put( systemId, res );
            }

            InputSource is = new InputSource( new ByteArrayInputStream( res ) );
            is.setPublicId( publicId );
            is.setSystemId( systemId );

            return is;
        }

        /**
         * Wrap {@link IOUtil#toByteArray(java.io.InputStream)} to throw SAXException.
         *
         * @param url not null
         * @return return an array of byte
         * @throws SAXException if any
         * @see {@link IOUtil#toByteArray(java.io.InputStream)}
         */
        private static byte[] toByteArray( URL url )
            throws SAXException
        {
            InputStream is = null;
            try
            {
                is = url.openStream();
                if ( is == null )
                {
                    throw new SAXException( "Cannot open stream from the url: " + url.toString() );
                }
                return IOUtil.toByteArray( is );
            }
            catch ( IOException e )
            {
                throw new SAXException( "IOException: " + e.getMessage(), e );
            }
            finally
            {
                IOUtil.close( is );
            }
        }

        /**
         * Wrap {@link IOUtil#copy(byte[], OutputStream)} to throw SAXException.
         *
         * @param res not null array of byte
         * @param f the file where to write the bytes
         * @throws SAXException if any
         * @see {@link IOUtil#copy(byte[], OutputStream)}
         */
        private void copy( byte[] res, File f )
            throws SAXException
        {
            if ( f.isDirectory() )
            {
                throw new SAXException( "'" + f.getAbsolutePath() + "' is a directory, can not write it." );
            }

            OutputStream os = null;
            try
            {
                os = new FileOutputStream( f );
                IOUtil.copy( res, os );
            }
            catch ( IOException e )
            {
                throw new SAXException( "IOException: " + e.getMessage(), e );
            }
            finally
            {
                IOUtil.close( os );
            }
        }
    }

    /**
     * Custom MXParser to fix PLXUTILS-109 and PLXUTILS-110
     */
    private static class DoxiaMXParser
        extends MXParser
    {
        /** {@inheritDoc} */
        // Fix PLXUTILS-109
        protected char[] parseEntityRef()
            throws XmlPullParserException, IOException
        {
            // entity reference http://www.w3.org/TR/2000/REC-xml-20001006#NT-Reference
            // [67] Reference ::= EntityRef | CharRef

            // ASSUMPTION just after &
            entityRefName = null;
            posStart = pos;
            char ch = more();
            if ( ch == '#' )
            {
                return numericEntity( ch );
            }
            else
            {
                return namedEntity( ch );
            }
        }

        /** {@inheritDoc} */
        // Fix PLXUTILS-110
        public void defineEntityReplacementText( String entityName,
                                                String replacementText )
            throws XmlPullParserException
        {
            //      throw new XmlPullParserException("not allowed");

            if ( !replacementText.startsWith( "&#" ) && this.entityName != null && replacementText.length() > 1 )
            {
                String tmp = replacementText.substring( 1, replacementText.length() - 1 );
                for ( int i = 0; i < this.entityName.length; i++ )
                {
                    if ( this.entityName[i] != null && this.entityName[i].equals( tmp ) )
                    {
                        replacementText = this.entityReplacement[i];
                    }
                }
            }

            //protected char[] entityReplacement[];
            ensureEntityCapacity();

            // this is to make sure that if interning works we will take advantage of it ...
            this.entityName[entityEnd] = newString( entityName.toCharArray(), 0, entityName.length() );
            entityNameBuf[entityEnd] = entityName.toCharArray();

            entityReplacement[entityEnd] = replacementText;
            entityReplacementBuf[entityEnd] = replacementText.toCharArray();
            if ( !allStringsInterned )
            {
                entityNameHash[ entityEnd ] =
                    fastHash( entityNameBuf[entityEnd], 0, entityNameBuf[entityEnd].length );
            }
            ++entityEnd;
            //TODO disallow < or & in entity replacement text (or ]]>???)
            // TOOD keepEntityNormalizedForAttributeValue cached as well ...
        }

        private char[] namedEntity( char ch )
                throws IOException, XmlPullParserException
        {
            // [68] EntityRef ::= '&' Name ';'
            // scan anem until ;
            if ( !isNameStartChar( ch ) )
            {
                throw new XmlPullParserException( "entity reference names can not start with character '"
                        + printable( ch ) + "'", this, null );
            }
            while ( true )
            {
                ch = more();
                if ( ch == ';' )
                {
                    break;
                }
                if ( !isNameChar( ch ) )
                {
                    throw new XmlPullParserException( "entity reference name can not contain character "
                            + printable( ch ) + "'", this, null );
                }
            }
            posEnd = pos - 1;
            // determine what name maps to
            final int len = posEnd - posStart;
            if ( len == 2 && buf[posStart] == 'l' && buf[posStart + 1] == 't' )
            {
                if ( tokenize )
                {
                    text = "<";
                }
                charRefOneCharBuf[0] = '<';
                return charRefOneCharBuf;
                // if(paramPC || isParserTokenizing) {
                // if(pcEnd >= pc.length) ensurePC();
                // pc[pcEnd++] = '<';
                // }
            }
            else if ( len == 3 && buf[posStart] == 'a' && buf[posStart + 1] == 'm' && buf[posStart + 2] == 'p' )
            {
                if ( tokenize )
                {
                    text = "&";
                }
                charRefOneCharBuf[0] = '&';
                return charRefOneCharBuf;
            }
            else if ( len == 2 && buf[posStart] == 'g' && buf[posStart + 1] == 't' )
            {
                if ( tokenize )
                {
                    text = ">";
                }
                charRefOneCharBuf[0] = '>';
                return charRefOneCharBuf;
            }
            else if ( len == 4 && buf[posStart] == 'a' && buf[posStart + 1] == 'p'
                    && buf[posStart + 2] == 'o' && buf[posStart + 3] == 's' )
            {
                if ( tokenize )
                {
                    text = "'";
                }
                charRefOneCharBuf[0] = '\'';
                return charRefOneCharBuf;
            }
            else if ( len == 4 && buf[posStart] == 'q' && buf[posStart + 1] == 'u'
                    && buf[posStart + 2] == 'o' && buf[posStart + 3] == 't' )
            {
                if ( tokenize )
                {
                    text = "\"";
                }
                charRefOneCharBuf[0] = '"';
                return charRefOneCharBuf;
            }
            else
            {
                final char[] result = lookuEntityReplacement( len );
                if ( result != null )
                {
                    return result;
                }
            }
            if ( tokenize )
            {
                text = null;
            }
            return null;
        }

        private char[] numericEntity( char ch )
                throws IOException, XmlPullParserException
        {
            // parse character reference
            char charRef = 0;
            ch = more();
            StringBuffer sb = new StringBuffer();
            if ( ch == 'x' )
            {
                // encoded in hex
                while ( true )
                {
                    ch = more();
                    if ( ch >= '0' && ch <= '9' )
                    {
                        sb.append( ch );
                        charRef = (char) ( charRef * 16 + ( ch - '0' ) );
                    }
                    else if ( ch >= 'a' && ch <= 'f' )
                    {
                        sb.append( ch );
                        charRef = (char) ( charRef * 16 + ( ch - ( 'a' - 10 ) ) );
                    }
                    else if ( ch >= 'A' && ch <= 'F' )
                    {
                        sb.append( ch );
                        charRef = (char) ( charRef * 16 + ( ch - ( 'A' - 10 ) ) );
                    }
                    else if ( ch == ';' )
                    {
                        break;
                    }
                    else
                    {
                        throw new XmlPullParserException( "character reference (with hex value) may not contain "
                                + printable( ch ), this, null );
                    }
                }
            }
            else
            {
                // encoded in decimal
                while ( true )
                {
                    if ( ch >= '0' && ch <= '9' )
                    {
                        charRef = (char) ( charRef * 10 + ( ch - '0' ) );
                    }
                    else if ( ch == ';' )
                    {
                        break;
                    }
                    else
                    {
                        throw new XmlPullParserException( "character reference (with decimal value) may not contain "
                                + printable( ch ), this, null );
                    }
                    ch = more();
                }
            }
            posEnd = pos - 1;
            if ( sb.length() > 0 )
            {
                char[] tmp = HtmlTools.toChars( Integer.parseInt( sb.toString(), 16 ) );
                charRefOneCharBuf = tmp;
                if ( tokenize )
                {
                    text = newString( charRefOneCharBuf, 0, charRefOneCharBuf.length );
                }
                return charRefOneCharBuf;
            }
            charRefOneCharBuf[0] = charRef;
            if ( tokenize )
            {
                text = newString( charRefOneCharBuf, 0, 1 );
            }
            return charRefOneCharBuf;
        }
    }
}
