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
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.util.EntityUtils;

import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.markup.XmlMarkup;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.util.HtmlTools;
import org.apache.maven.doxia.util.XmlValidator;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
     * <br>
     * see <a href="http://www.w3.org/TR/REC-xml/#NT-EntityDecl">http://www.w3.org/TR/REC-xml/#NT-EntityDecl</a>.
     */
    private static final Pattern PATTERN_ENTITY_1 =
        Pattern.compile( ENTITY_START + "(\\s)+([^>|^\\s]+)(\\s)+\"(\\s)*(&[a-zA-Z]{2,6};)(\\s)*\"(\\s)*>" );

    /**
     * Entity pattern for Unicode entity, i.e. &#38;#38;
     * "<!ENTITY(\\s)+([^>|^\\s]+)(\\s)+\"(\\s)*(&(#x?[0-9a-fA-F]{1,5};)*)(\\s)*\"(\\s)*>"
     * <br>
     * see <a href="http://www.w3.org/TR/REC-xml/#NT-EntityDecl">http://www.w3.org/TR/REC-xml/#NT-EntityDecl</a>.
     */
    private static final Pattern PATTERN_ENTITY_2 =
        Pattern.compile( ENTITY_START + "(\\s)+([^>|^\\s]+)(\\s)+\"(\\s)*(&(#x?[0-9a-fA-F]{1,5};)*)(\\s)*\"(\\s)*>" );

    private boolean ignorableWhitespace;

    private boolean collapsibleWhitespace;

    private boolean trimmableWhitespace;

    private Map<String, String> entities;

    private boolean validate = false;

    /** {@inheritDoc} */
    public void parse( Reader source, Sink sink )
        throws ParseException
    {
        init();

        Reader src = source;

        // 1 first parsing if validation is required
        if ( isValidate() )
        {
            String content;
            try
            {
                content = IOUtil.toString( new BufferedReader( src ) );
            }
            catch ( IOException e )
            {
                throw new ParseException( "Error reading the model: " + e.getMessage(), e );
            }

            new XmlValidator( getLog() ).validate( content );

            src = new StringReader( content );
        }

        // 2 second parsing to process
        try
        {
            XmlPullParser parser = new MXParser();

            parser.setInput( src );
            
            // allow parser initialization, e.g. for additional entities in XHTML
            // Note: do it after input is set, otherwise values are reset
            initXmlParser( parser );

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

        setSecondParsing( false );
        init();
    }
    
    /**
     * Initializes the parser with custom entities or other options.
     *
     * @param parser A parser, not null.
     * @throws org.codehaus.plexus.util.xml.pull.XmlPullParserException if there's a problem initializing the parser
     */
    protected void initXmlParser( XmlPullParser parser )
        throws XmlPullParserException
    {
        // nop
    }

    /**
     * {@inheritDoc}
     *
     * Convenience method to parse an arbitrary string and emit any xml events into the given sink.
     */
    @Override
    public void parse( String string, Sink sink )
        throws ParseException
    {
        super.parse( string, sink );
    }

    /** {@inheritDoc} */
    @Override
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
                    if ( text.trim().length() != 0 )
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

                for ( byte[] res : CachedFileEntityResolver.ENTITY_CACHE.values() )
                {
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
        if ( isEmitComments() )
        {
            sink.comment( getText( parser ) );
        }
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
     * Handles an unknown event.
     *
     * <p>This is a default implementation, all events are emitted as unknown
     * events into the specified sink.</p>
     *
     * @param parser the parser to get the event from.
     * @param sink the sink to receive the event.
     * @param type the tag event type. This should be one of HtmlMarkup.TAG_TYPE_SIMPLE,
     * HtmlMarkup.TAG_TYPE_START, HtmlMarkup.TAG_TYPE_END or HtmlMarkup.ENTITY_TYPE.
     * It will be passed as the first argument of the required parameters to the Sink
     * {@link
     * org.apache.maven.doxia.sink.Sink#unknown(String, Object[], org.apache.maven.doxia.sink.SinkEventAttributes)}
     * method.
     */
    protected void handleUnknown( XmlPullParser parser, Sink sink, int type )
    {
        Object[] required = new Object[] { type };

        SinkEventAttributeSet attribs = getAttributesFromParser( parser );

        sink.unknown( parser.getName(), required, attribs );
    }

    /**
     * <p>isIgnorableWhitespace.</p>
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
     * Specify that whitespace will be ignored. I.e.:
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
     * <p>isCollapsibleWhitespace.</p>
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
     * Specify that text will be collapsed. I.e.:
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
     * <p>isTrimmableWhitespace.</p>
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
     * Specify that text will be collapsed. I.e.:
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
     * <p>getText.</p>
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
            StringBuilder newText = new StringBuilder();
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
     * Return the defined entities in a local doctype. I.e.:
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
    protected Map<String, String> getLocalEntities()
    {
        if ( entities == null )
        {
            entities = new LinkedHashMap<>();
        }

        return entities;
    }

    /**
     * <p>isValidate.</p>
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
     * Add an entity given by <code>entityName</code> and <code>entityValue</code> to {@link #entities}.
     * <br>
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
            int start = text.indexOf( '[' );
            int end = text.lastIndexOf( ']' );
            if ( start != -1 && end != -1 )
            {
                addDTDEntities( parser, text.substring( start + 1, end ) );
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
            final String txt = StringUtils.replace( text, ENTITY_START, "\n" + ENTITY_START );
            try ( BufferedReader reader = new BufferedReader( new StringReader( txt ) ) )
            {
                String line;
                String tmpLine = "";
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
        protected static final Map<String, byte[]> ENTITY_CACHE = new Hashtable<>();

        /** {@inheritDoc} */
        public InputSource resolveEntity( String publicId, String systemId )
            throws SAXException, IOException
        {
            byte[] res = ENTITY_CACHE.get( systemId );
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
                        String resource = "/" + systemName;
                        URL url = getClass().getResource( resource );
                        if ( url != null )
                        {
                            res = toByteArray( url );
                        }
                        else
                        {
                            throw new SAXException( "Could not find the SYSTEM entity: " + systemId
                            + " because '" + resource + "' is not available of the classpath." );
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
         * If url is not an http/https urls, call {@link IOUtil#toByteArray(java.io.InputStream)} to get the url
         * content.
         * Otherwise, use HttpClient to get the http content.
         * Wrap all internal exceptions to throw SAXException.
         *
         * @param url not null
         * @return return an array of byte
         * @throws SAXException if any
         */
        private static byte[] toByteArray( URL url )
            throws SAXException
        {
            if ( !( url.getProtocol().equalsIgnoreCase( "http" ) || url.getProtocol().equalsIgnoreCase( "https" ) ) )
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

            // it is an HTTP url, using HttpClient...
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet method = new HttpGet( url.toString() );
            // Set a user-agent that doesn't contain the word "java", otherwise it will be blocked by the W3C
            // The default user-agent is "Apache-HttpClient/4.0.2 (java 1.5)"
            method.setHeader( "user-agent", "Apache-Doxia/" + doxiaVersion() );

            HttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler( 3, false );
            client.setHttpRequestRetryHandler( retryHandler );

            HttpEntity entity = null;
            try
            {
                HttpResponse response = client.execute( method );
                int statusCode = response.getStatusLine().getStatusCode();
                if ( statusCode != HttpStatus.SC_OK )
                {
                    throw new IOException( "The status code when accessing the URL '" + url.toString() + "' was "
                        + statusCode + ", which is not allowed. The server gave this reason for the failure '"
                        + response.getStatusLine().getReasonPhrase() + "'." );
                }

                entity = response.getEntity();
                return EntityUtils.toByteArray( entity );
            }
            catch ( ClientProtocolException e )
            {
                throw new SAXException( "ClientProtocolException: Fatal protocol violation: " + e.getMessage(), e );
            }
            catch ( IOException e )
            {
                throw new SAXException( "IOException: Fatal transport error: " + e.getMessage(), e );
            }
            finally
            {
                if ( entity != null )
                {
                    try
                    {
                        entity.consumeContent();
                    }
                    catch ( IOException e )
                    {
                        // Ignore
                    }
                }
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
}
