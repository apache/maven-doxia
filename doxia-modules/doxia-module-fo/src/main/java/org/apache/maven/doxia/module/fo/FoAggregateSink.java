package org.apache.maven.doxia.module.fo;

import java.io.File;

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
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Stack;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.docrenderer.DocumentRendererContext;
import org.apache.maven.doxia.document.DocumentCover;
import org.apache.maven.doxia.document.DocumentMeta;
import org.apache.maven.doxia.document.DocumentModel;
import org.apache.maven.doxia.document.DocumentTOC;
import org.apache.maven.doxia.document.DocumentTOCItem;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.util.DoxiaUtils;
import org.apache.maven.doxia.util.HtmlTools;

import org.codehaus.plexus.util.StringUtils;

/**
 * A Doxia Sink that produces an aggregated FO model. The usage is similar to the following:
 * <p/>
 * <pre>
 * FoAggregateSink sink = new FoAggregateSink( writer );
 * sink.setDocumentModel( documentModel );
 * sink.beginDocument();
 * sink.coverPage();
 * sink.toc();
 * ...
 * sink.endDocument();
 * </pre>
 * <p/>
 * <b>Note</b>: the documentModel object contains several
 * <a href="http://maven.apache.org/doxia/doxia/doxia-core/document.html">document metadata</a>, but only a few
 * of them are used in this sink (i.e. author, confidential, date and title), the others are ignored.
 *
 * @author ltheussl
 * @version $Id$
 * @since 1.1
 */
public class FoAggregateSink
    extends FoSink
{
    /**
     * No Table Of Content.
     *
     * @see #setDocumentModel(DocumentModel, int)
     */
    public static final int TOC_NONE = 0;

    /**
     * Table Of Content at the start of the document.
     *
     * @see #setDocumentModel(DocumentModel, int)
     */
    public static final int TOC_START = 1;

    /**
     * Table Of Content at the end of the document.
     *
     * @see #setDocumentModel(DocumentModel, int)
     */
    public static final int TOC_END = 2;

    // TODO: make configurable
    private static final String COVER_HEADER_HEIGHT = "0.5in";//"1.5in";

    /**
     * The document model to be used by this sink.
     */
    private DocumentModel docModel;

    /**
     * Counts the current chapter level.
     */
    private int chapter = 0;

    /**
     * Name of the source file of the current document, relative to the source root.
     */
    private String docName;

    /**
     * Title of the chapter, used in the page header.
     */
    private String docTitle = "";

    /**
     * Content in head is ignored in aggregated documents.
     */
    private boolean ignoreText;

    /**
     * Current position of the TOC, see {@link #TOC_POSITION}
     */
    private int tocPosition;

    /**
     * expected DocumentRendererContext
     */
    private Object context = null;
    
    /**
     * Writing pages netween cover-page and toc needs some changes to the standard-process.
     * 
     */
    private boolean writingPriorPage = false;
    
    /**
     * In order to write pages between cover-page and toc, it was neccessary to reset the page counter after these pages.
     */
    private boolean resetPageCounter = false;
    
    /**
     * Used to get the current position in the TOC.
     */
    private final Stack<NumberedListItem> tocStack = new Stack<NumberedListItem>();

    /**
     * During the cover-pages creation the value is set from the document-context and is used for defining the table of contents width afterwards.
     */
    private double availablePageWidthInInch = 0.0d;

    private double tocColWidthC1 = 0.22d;
    private double tocColWidthC2 = 0.38d;
    private double tocColWidthC3 = 0.58d;
    private double tocColWidthC4 = 0.76d;
    private double tocColWidthC5 = 0.94d;
    private double tocColWidthC6 = 1.12d;
    private double tocColWidthC7 = 1.85d;
    private double tocColWidthC8 = 0.10d;
    private double tocColWidthC9 = 0.30d;
    
    
	
    /**
     * Constructor.
     *
     * @param writer The writer for writing the result.
     */
    public FoAggregateSink( Writer writer )
    {
        super( writer );
    }

    public FoAggregateSink( Writer writer, Object context )  
    {
        this( writer );
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    public void head()
    {
        head( null );
    }

    /**
     * {@inheritDoc}
     */
    public void head( SinkEventAttributes attributes )
    {
        init();

        ignoreText = true;
    }

    /**
     * {@inheritDoc}
     */
    public void head_()
    {
        ignoreText = false;
        writeEOL();
    }

    /**
     * {@inheritDoc}
     */
    public void title()
    {
        title( null );
    }

    /**
     * {@inheritDoc}
     */
    public void title( SinkEventAttributes attributes )
    {
        // ignored
    }

    /**
     * {@inheritDoc}
     */
    public void title_()
    {
        // ignored
    }

    /**
     * {@inheritDoc}
     */
    public void author()
    {
        author( null );
    }

    /**
     * {@inheritDoc}
     */
    public void author( SinkEventAttributes attributes )
    {
        // ignored
    }

    /**
     * {@inheritDoc}
     */
    public void author_()
    {
        // ignored
    }

    /**
     * {@inheritDoc}
     */
    public void date()
    {
        date( null );
    }

    /**
     * {@inheritDoc}
     */
    public void date( SinkEventAttributes attributes )
    {
        // ignored
    }

    /**
     * {@inheritDoc}
     */
    public void date_()
    {
        // ignored
    }

    /**
     * {@inheritDoc}
     */
    public void body()
    {
        body( null );
    }

    /**
     * {@inheritDoc}
     */
    public void body( SinkEventAttributes attributes )
    {
    	chapter++;

        resetSectionCounter();

        if( !writingPriorPage )
        	startPageSequence( getChapterName(), getHeaderText(), getFooterText() );

        if ( docName == null )
        {
            getLog().warn( "No document root specified, local links will not be resolved correctly!" );
        }
        else
        {
            writeStartTag( BLOCK_TAG, "" );
        }

    }
    
    /**
     * {@inheritDoc}
     */
    public void body_()
    {
        writeEOL();
        writeEndTag( BLOCK_TAG );
        writeEndTag( FLOW_TAG );
        writeEndTag( PAGE_SEQUENCE_TAG );
        writeEOL();writeEOL();
        // reset document name
        docName = null;
    }

    /**
     * Sets the title of the current document. This is used as a chapter title in the page header.
     *
     * @param title the title of the current document.
     */
    public void setDocumentTitle( String title )
    {
        this.docTitle = title;

        if ( title == null )
        {
            this.docTitle = "";
        }
    }

    /**
     * Sets the name of the current source document, relative to the source root.
     * Used to resolve links to other source documents.
     *
     * @param name the name for the current document.
     */
    public void setDocumentName( String name )
    {
        this.docName = getIdName( name );
    }

    /**
     * Sets the DocumentModel to be used by this sink. The DocumentModel provides all the meta-information
     * required to render a document, eg settings for the cover page, table of contents, etc.
     * <br/>
     * By default, a TOC will be added at the beginning of the document.
     *
     * @param model the DocumentModel.
     * @see #setDocumentModel(DocumentModel, String)
     * @see #TOC_START
     */
    public void setDocumentModel( DocumentModel model )
    {
        setDocumentModel( model, TOC_START );
    }

    /**
     * Sets the DocumentModel to be used by this sink. The DocumentModel provides all the meta-information
     * required to render a document, eg settings for the cover page, table of contents, etc.
     *
     * @param model  the DocumentModel, could be null.
     * @param tocPos should be one of these values: {@link #TOC_NONE}, {@link #TOC_START} and {@link #TOC_END}.
     * @since 1.1.2
     */
    public void setDocumentModel( DocumentModel model, int tocPos )
    {
        this.docModel = model;
        if ( !( tocPos == TOC_NONE || tocPos == TOC_START || tocPos == TOC_END ) )
        {
            if ( getLog().isDebugEnabled() )
            {
                getLog().debug( "Unrecognized value for tocPosition: " + tocPos + ", using no toc." );
            }
            tocPos = TOC_NONE;
        }
        this.tocPosition = tocPos;

        if ( this.docModel != null && this.docModel.getToc() != null && this.tocPosition != TOC_NONE )
        {
            DocumentTOCItem tocItem = new DocumentTOCItem();
            tocItem.setName( this.docModel.getToc().getName() );
            tocItem.setRef( "./toc" );
            List<DocumentTOCItem> items = new LinkedList<DocumentTOCItem>();
            if ( this.tocPosition == TOC_START )
            {
                items.add( tocItem );
            }
            items.addAll( this.docModel.getToc().getItems() );
            if ( this.tocPosition == TOC_END )
            {
                items.add( tocItem );
            }

            this.docModel.getToc().setItems( items );
        }
    }

    /**
     * Translates the given name to a usable id.
     * Prepends "./" and strips any extension.
     *
     * @param name the name for the current document.
     * @return String
     */
    private String getIdName( String name )
    {
        if ( StringUtils.isEmpty( name ) )
        {
            getLog().warn( "Empty document reference, links will not be resolved correctly!" );
            return "";
        }

        String idName = name.replace( '\\', '/' );

        // prepend "./" and strip extension
        if ( !idName.startsWith( "./" ) )
        {
            idName = "./" + idName;
        }

        if ( idName.substring( 2 ).lastIndexOf( "." ) != -1 )
        {
            idName = idName.substring( 0, idName.lastIndexOf( "." ) );
        }

        while ( idName.indexOf( "//" ) != -1 )
        {
            idName = StringUtils.replace( idName, "//", "/" );
        }

        return idName;
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    /**
     * Looks in the current working-directory for the passed subpath.
     * @param subpath the subpath that is to be found
     * @return the absolute path of the subpath, if exists anywhere below the current position, otherwise null. If the passed subpath is null, null is returned.
     */
    private String findAbsolutePath(String subpath)
    {
    	if( subpath==null )return null;
    	
    	String res = new String(subpath);
    	
    	if( !subpath.contains("://") && !subpath.contains(":\\") && !subpath.startsWith("/") )
    	{
    		String tmpPath = StringUtils.replace(subpath, "\\", "/");
    		String[] levels = StringUtils.split(tmpPath, "/");
    		
    		File tmp = new File(".");
    		
    		if( levels!=null )
    		{
				File targetFile = findTargetFile(tmp, levels, levels[levels.length-1]);
				if( targetFile!=null)
				{
    				String absPath = targetFile.getAbsolutePath();
    				if( absPath!=null )
    				{
    					res = StringUtils.replace(absPath, "\\.\\", "/");
    					res = StringUtils.replace(res, "/./", "/");
    					res = StringUtils.replace(res, "\\", "/");
    				}
				}
    			
    		}
    		
    	}
    	
    	return res;
    }
    
    /**
     * Looks for a specific file, that is defined by subpath and filename, somewhere in the current directory or in the childrens paths
     * @param currentPosition current working directory
     * @param pathlevels the subpath if the searched file
     * @param targetFilename the filename of the searched file
     * @return the File-Object, if the searched file exists somewhere in the current working directory, 
     * its children or any subpath (passed subpath must be contained within the absolute path) from here. 
     * Returns null, if file does not exist.
     */
    private File findTargetFile(File currentPosition, String[] pathlevels, String targetFilename)
    {
    	File res = null;
    	
    	if( currentPosition!=null && targetFilename!= null )
    	{
    		File[] children = currentPosition.listFiles();
    		if( children!=null )
    		{
    			for( File f:children )
    			{
    				if( res==null && f!=null )
    				{
	    				if( f.getName().equals(targetFilename) )
	    					res = f;
	    				else if( f.isDirectory() )
	    					res = findTargetFile(f, pathlevels, targetFilename);

	    				if( res!=null && pathlevels!=null && pathlevels.length>1 )
	    				{
	    					File tmp = res;
	    					boolean match = true;
	    					for( int i=pathlevels.length-2; i>=0 && match && tmp!=null; i-- )
	    					{
	    						File tmpUp = tmp.getParentFile();

	    						if( tmpUp==null || !tmpUp.getName().equals(pathlevels[i]) )
	    						{
	    							match = false;
	    						}
	    						tmp = tmpUp;
	    					}
	    					if( !match ) res = null;
	    				}
    				}
    			}
    		}
    	}
    	
    	return res;
    }
    
    /**
     * {@inheritDoc}
     */
    public void figureGraphics( String name )
    {
        figureGraphics( name, null );
    }

    /**
     * {@inheritDoc}
     */
    public void figureGraphics( String src, SinkEventAttributes attributes )
    {
        String anchor = src;

        while ( anchor.startsWith( "./" ) )
        {
            anchor = anchor.substring( 2 );
        }

        if ( anchor.startsWith( "../" ) && docName != null )
        {
            anchor = resolveLinkRelativeToBase( anchor );
        }

        super.figureGraphics( anchor, attributes );
    }

    /**
     * {@inheritDoc}
     */
    public void anchor( String name )
    {
        anchor( name, null );
    }

    /**
     * {@inheritDoc}
     */
    public void anchor( String name, SinkEventAttributes attributes )
    {
        if ( name == null )
        {
            throw new NullPointerException( "Anchor name cannot be null!" );
        }

        String anchor = name;

        if ( !DoxiaUtils.isValidId( anchor ) )
        {
            anchor = DoxiaUtils.encodeId( name, true );

            String msg = "Modified invalid anchor name: '" + name + "' to '" + anchor + "'";
            logMessage( "modifiedLink", msg );
        }

        anchor = "#" + anchor;

        if ( docName != null )
        {
            anchor = docName + anchor;
        }

        writeStartTag( INLINE_TAG, "id", anchor );
    }

    /**
     * {@inheritDoc}
     */
    public void link( String name )
    {
        link( name, null );
    }

    /**
     * {@inheritDoc}
     */
    public void link( String name, SinkEventAttributes attributes )
    {
        if ( name == null )
        {
            throw new NullPointerException( "Link name cannot be null!" );
        }

        if ( DoxiaUtils.isExternalLink( name ) )
        {
            // external links
            writeStartTag( BASIC_LINK_TAG, "external-destination", HtmlTools.escapeHTML( name ) );
            writeStartTag( INLINE_TAG, "href.external" );
            return;
        }

        while ( name.indexOf( "//" ) != -1 )
        {
            name = StringUtils.replace( name, "//", "/" );
        }

        if ( DoxiaUtils.isInternalLink( name ) )
        {
            // internal link (ie anchor is in the same source document)
            String anchor = name.substring( 1 );

            if ( !DoxiaUtils.isValidId( anchor ) )
            {
                String tmp = anchor;
                anchor = DoxiaUtils.encodeId( anchor, true );

                String msg = "Modified invalid anchor name: '" + tmp + "' to '" + anchor + "'";
                logMessage( "modifiedLink", msg );
            }

            if ( docName != null )
            {
                anchor = docName + "#" + anchor;
            }

            writeStartTag( BASIC_LINK_TAG, "internal-destination", HtmlTools.escapeHTML( anchor ) );
            writeStartTag( INLINE_TAG, "href.internal" );
        }
        else if ( name.startsWith( "../" ) )
        {
            // local link (ie anchor is not in the same source document)

            if ( docName == null )
            {
                // can't resolve link without base, fop will issue a warning
                writeStartTag( BASIC_LINK_TAG, "internal-destination", HtmlTools.escapeHTML( name ) );
                writeStartTag( INLINE_TAG, "href.internal" );

                return;
            }

            String anchor = resolveLinkRelativeToBase( chopExtension( name ) );

            writeStartTag( BASIC_LINK_TAG, "internal-destination", HtmlTools.escapeHTML( anchor ) );
            writeStartTag( INLINE_TAG, "href.internal" );
        }
        else
        {
            // local link (ie anchor is not in the same source document)

            String anchor = name;

            if ( anchor.startsWith( "./" ) )
            {
                this.link( anchor.substring( 2 ) );
                return;
            }

            anchor = chopExtension( anchor );

            String base = docName.substring( 0, docName.lastIndexOf( "/" ) );
            anchor = base + "/" + anchor;

            writeStartTag( BASIC_LINK_TAG, "internal-destination", HtmlTools.escapeHTML( anchor ) );
            writeStartTag( INLINE_TAG, "href.internal" );
        }
    }

    // only call this if docName != null !!!
    private String resolveLinkRelativeToBase( String name )
    {
        String anchor = name;

        String base = docName.substring( 0, docName.lastIndexOf( "/" ) );

        if ( base.indexOf( "/" ) != -1 )
        {
            while ( anchor.startsWith( "../" ) )
            {
                base = base.substring( 0, base.lastIndexOf( "/" ) );

                anchor = anchor.substring( 3 );

                if ( base.lastIndexOf( "/" ) == -1 )
                {
                    while ( anchor.startsWith( "../" ) )
                    {
                        anchor = anchor.substring( 3 );
                    }
                    break;
                }
            }
        }

        return base + "/" + anchor;
    }

    private String chopExtension( String name )
    {
        String anchor = name;

        int dot = anchor.lastIndexOf( "." );

        if ( dot != -1 && dot != anchor.length() && anchor.charAt( dot + 1 ) != '/' )
        {
            int hash = anchor.indexOf( "#", dot );

            if ( hash != -1 )
            {
                int dot2 = anchor.indexOf( ".", hash );

                if ( dot2 != -1 )
                {
                    anchor =
                        anchor.substring( 0, dot ) + "#" + HtmlTools.encodeId( anchor.substring( hash + 1, dot2 ) );
                }
                else
                {
                    anchor = anchor.substring( 0, dot ) + "#" + HtmlTools.encodeId(
                        anchor.substring( hash + 1, anchor.length() ) );
                }
            }
            else
            {
                anchor = anchor.substring( 0, dot );
            }
        }

        return anchor;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p/>
     * Writes a start tag, prepending EOL.
     */
    protected void writeStartTag( Tag tag, String attributeId )
    {
        if ( !ignoreText )
        {
            super.writeStartTag( tag, attributeId );
        }
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Writes a start tag, prepending EOL.
     */
    protected void writeStartTag( Tag tag, String id, String name )
    {
        if ( !ignoreText )
        {
            super.writeStartTag( tag, id, name );
        }
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Writes an end tag, appending EOL.
     */
    protected void writeEndTag( Tag t )
    {
        if ( !ignoreText )
        {
            super.writeEndTag( t );
        }
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Writes a simple tag, appending EOL.
     */
    protected void writeEmptyTag( Tag tag, String attributeId )
    {
        if ( !ignoreText )
        {
            super.writeEmptyTag( tag, attributeId );
        }
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Writes a text, swallowing any exceptions.
     */
    protected void write( String text )
    {
        if ( !ignoreText )
        {
            super.write( text );
        }
    }

    
    /**
     * {@inheritDoc}
     * <p/>
     * Writes a text, appending EOL.
     */
    protected void writeln( String text )
    {
        if ( !ignoreText )
        {
            super.writeln( text );
        }
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Writes content, escaping special characters.
     */
    protected void content( String text )
    {
        if ( !ignoreText )
        {
            super.content( text );
        }
    }

    /**
     * Writes EOL.
     */
    protected void newline()
    {
        if ( !ignoreText )
        {
            writeEOL();
        }
    }

    /**
     * Starts a page sequence, depending on the current chapter.
     *
     * @param headerText The text to write in the header, if null, nothing is written.
     * @param footerText The text to write in the footer, if null, nothing is written.
     */
    protected void startPageSequence( String chapterName, String headerText, String footerText )
    {
        if ( chapter == 1 || resetPageCounter )
        {
            startPageSequence( "0", chapterName, headerText, footerText );
            resetPageCounter = false;
        }
        else
        {
            startPageSequence( "auto", chapterName, headerText, footerText );
        }
    }

    /**
     * Returns the text to write in the header of each page.
     *
     * @return String that contains the headers text, if defined in pom-file, otherwise empty string is returned
     */
    protected String getHeaderText()
    {
    	String res ="";
    	
        if ( context != null ) 
        {
        	
        	DocumentRendererContext drContext = getRendererContext();
        	if( drContext!=null)
        	{
        		Object tmp = drContext.get("pdfHeader");
        		if( tmp!=null )
        			res = tmp.toString();
            }
        	
            
        }
        
        return res;
    }
    
    
    protected String getChapterName(){
        return Integer.toString( chapter ) + "   " + docTitle;
    }

    /**
     * Returns the text to write in the footer of each page.
     *
     * @return String that contains the footers text, if defined in pom-file, otherwise empty string is returned
     */
    protected String getFooterText()
    {
    	String res ="";
    	if ( context != null ) 
        {
    		if ( context != null ) 
            {
            	
            	DocumentRendererContext drContext = getRendererContext();
            	if( drContext!=null)
            	{
            		Object tmp = drContext.get("pdfFooter");
            		if( tmp!=null )
            			res = tmp.toString();
                }
            	
            }
        }else
        {
	        int actualYear;
	        String add = " &#8226; " + getBundle( Locale.US ).getString( "footer.rights" );
	        String companyName = "";
	
	        if ( docModel != null && docModel.getMeta() != null && docModel.getMeta().isConfidential() )
	        {
	            add = add + " &#8226; " + getBundle( Locale.US ).getString( "footer.confidential" );
	        }
	
	        if ( docModel != null && docModel.getCover() != null && docModel.getCover().getCompanyName() != null )
	        {
	            companyName = docModel.getCover().getCompanyName();
	        }
	
	        if ( docModel != null && docModel.getMeta() != null && docModel.getMeta().getDate() != null )
	        {
	            Calendar date = Calendar.getInstance();
	            date.setTime( docModel.getMeta().getDate() );
	            actualYear = date.get( Calendar.YEAR );
	        }
	        else
	        {
	            actualYear = Calendar.getInstance().get( Calendar.YEAR );
	        }
	        
	        res = "&#169;" + actualYear + ", " + escaped( companyName, false ) + add;
        }
        return res;
    }

    /**
     * Reflection-method to get a properties value from the pom-file
     * @param propertyName the name of the property
     * @return the properties value as a String, if defined, otherwise an empty String is returned
     */
    public String getPomProperty(String propertyName)
    {
    	if ( context != null ) 
        {
            //developer commentary, this part uses reflection because the class DocumentRenderer
            //is not available in this classpath. 
            if ( context.getClass( ).getCanonicalName( ).
                    equals( "org.apache.maven.doxia.docrenderer.DocumentRendererContext" ) )
            {
                try 
                {
                    Method method = context.getClass( ).getMethod ( "get", String.class );
                    Method containsKey = context.getClass( ).getMethod ( "containsKey", Object.class );
                    boolean hasOverride = ( Boolean ) containsKey.invoke ( context, propertyName );
                    if ( hasOverride ) 
                    {
                        return escaped( ( String ) method.invoke ( context, propertyName ), false );
                    }
                } 
                catch ( NoSuchMethodException ex    ) 
                {
                    getLog().debug( "error trapped looking for footer override", ex );
                } 
                catch ( SecurityException ex ) 
                {
                    getLog().debug( "error trapped looking for footer override", ex );
                } 
                catch ( IllegalAccessException ex ) 
                {
                    getLog().debug( "error trapped looking for footer override", ex );
                } 
                catch ( IllegalArgumentException ex ) 
                {
                    getLog().debug( "error trapped looking for footer override", ex );
                } 
                catch ( InvocationTargetException ex ) 
                {
                    getLog().debug( "error trapped looking for footer override", ex );
                }
            }
        }
        
        return "";
    }
    
    /**
     * {@inheritDoc}
     * <p/>
     * Returns the current chapter number as a string.
     */
    protected String getChapterString()
    {
        return Integer.toString( chapter ) + ".";
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Writes a 'xsl-region-before' block.
     */
    @Override
    protected void regionBefore( String chapterName, String headerText )
    {
        writeStartTag( STATIC_CONTENT_TAG, "flow-name", "xsl-region-before" );
        writeln( "<fo:table table-layout=\"fixed\" width=\"100%\" >" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", "2.1666in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", "2.1666in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", "2.1666in" );
        writeStartTag( TABLE_BODY_TAG, "" );
        writeStartTag( TABLE_ROW_TAG, "" );
        writeStartTag( TABLE_CELL_TAG, "" );
        writeStartTag( BLOCK_TAG, "header.style" );
        
        //
        
        if ( chapterName != null )
        {
            write( chapterName );
        }

        writeEndTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        
        writeStartTag( TABLE_CELL_TAG, "" );
        writeStartTag( BLOCK_TAG, "header.style" );
        if ( headerText != null )
        {
            write( headerText );
        }
        writeEndTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );

        if( !writingPriorPage )
        {
	        writeStartTag( TABLE_CELL_TAG, "" );
	        writeStartTag( BLOCK_TAG, "page.number" );
	        writeEmptyTag( PAGE_NUMBER_TAG, "" );
	        writeEndTag( BLOCK_TAG );
	        writeEndTag( TABLE_CELL_TAG );
        }
        writeEndTag( TABLE_ROW_TAG );
        writeEndTag( TABLE_BODY_TAG );
        writeEndTag( TABLE_TAG );
        writeEndTag( STATIC_CONTENT_TAG );
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Writes a 'xsl-region-after' block.
     */
    protected void regionAfter( String footerText )
    {
        writeStartTag( STATIC_CONTENT_TAG, "flow-name", "xsl-region-after" );
        writeStartTag( BLOCK_TAG, "footer.style" );
       
        if ( footerText != null )
        {
            write( footerText );
        }
        writeEndTag( BLOCK_TAG );
        writeEndTag( STATIC_CONTENT_TAG );
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Writes a chapter heading.
     */
    protected void chapterHeading( String headerText, boolean chapterNumber )
    {
        if ( docName == null )
        {
            getLog().warn( "No document root specified, local links will not be resolved correctly!" );
            writeStartTag( BLOCK_TAG, "" );
        }
        else
        {
            writeStartTag( BLOCK_TAG, "id", docName );
        }

        writeStartTag( LIST_BLOCK_TAG, "" );
        writeStartTag( LIST_ITEM_TAG, "" );
        writeln( "<fo:list-item-label end-indent=\"6.375in\" start-indent=\"-1in\">" );
        writeStartTag( BLOCK_TAG, "outdented.number.style" );

        if ( chapterNumber )
        {
            writeStartTag( BLOCK_TAG, "chapter.title" );
            write( Integer.toString( chapter ) );
            writeEndTag( BLOCK_TAG );
        }

        writeEndTag( BLOCK_TAG );
        writeEndTag( LIST_ITEM_LABEL_TAG );
        writeln( "<fo:list-item-body end-indent=\"1in\" start-indent=\"0in\">" );
        writeStartTag( BLOCK_TAG, "chapter.title" );

        if ( headerText == null )
        {
            text( docTitle );
        }
        else
        {
            text( headerText );
        }

        writeEndTag( BLOCK_TAG );
        writeEndTag( LIST_ITEM_BODY_TAG );
        writeEndTag( LIST_ITEM_TAG );
        writeEndTag( LIST_BLOCK_TAG );
        writeEndTag( BLOCK_TAG );
        writeStartTag( BLOCK_TAG, "space-after.optimum", "0em" );
        writeEmptyTag( LEADER_TAG, "chapter.rule" );
        writeEndTag( BLOCK_TAG );
    }

    public void beginDocument()
    {
    	if ( docModel != null && docModel.getToc() != null)
    	generateMissingInternalReferences(docModel.getToc());
        super.beginDocument();
    }
    
    /**
     * Writes a table of contents. The DocumentModel has to contain a DocumentTOC for this to work.
     */
    public void toc()
    {
        if ( docModel == null || docModel.getToc() == null || docModel.getToc().getItems() == null
            || this.tocPosition == TOC_NONE )
        {
            return;
        }

        if( availablePageWidthInInch!=0.0d )
        	tocColWidthC7 = (availablePageWidthInInch-	tocColWidthC1-
        											tocColWidthC2-
        											tocColWidthC3-
        											tocColWidthC4-
        											tocColWidthC5-
        											tocColWidthC6-
        											tocColWidthC8-
        											tocColWidthC9);
        
        DocumentTOC toc = docModel.getToc();
        setDocTOC(toc);
        
        writeln( "<fo:page-sequence master-reference=\"toc\" initial-page-number=\"1\" format=\"i\">" );
        regionBefore( toc.getName(), getHeaderText() );
        regionAfter( getFooterText() );
        writeStartTag( FLOW_TAG, "flow-name", "xsl-region-body" );
        writeStartTag( BLOCK_TAG, "id", "./toc" );
        chapterHeading( toc.getName(), false );
        writeln( "<fo:table table-layout=\"fixed\" width=\"100%\" >" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", ""+tocColWidthC1+"in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", ""+tocColWidthC2+"in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", ""+tocColWidthC3+"in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", ""+tocColWidthC4+"in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", ""+tocColWidthC5+"in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", ""+tocColWidthC6+"in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", ""+tocColWidthC7+"in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", ""+tocColWidthC8+"in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", ""+tocColWidthC9+"in" );// TODO {$maxBodyWidth - 1.25}in
        
        writeStartTag( TABLE_BODY_TAG );

        writeTocItems( toc.getItems(), 1 );

        writeEndTag( TABLE_BODY_TAG );
        writeEndTag( TABLE_TAG );
        writeEndTag( BLOCK_TAG );
        writeEndTag( FLOW_TAG );
        writeEndTag( PAGE_SEQUENCE_TAG );
        writeEOL();writeEOL();
    }

    private void generateMissingInternalReferences(DocumentTOC toc)
    {
    	if( toc!=null )
    	{
    		String pathToItem = ".";
    		java.util.List<DocumentTOCItem> tocElements = toc.getItems();
    		
    		java.util.Vector<String> usedReferences = new java.util.Vector<String>();
    		
    		if( tocElements!=null )
				for( DocumentTOCItem child : tocElements )
				{
					fixReferenceIfNeeded(child, new String(pathToItem), usedReferences);
				}
    	}
    }
    
    /**
     * Originally the table of contents was limited to the entries in pdf.xml.
     * In this version the table of content is generated using the (sub-)chapter-structure of every toc-entry defined in the pdf.xml.
     * Therefore these toc-items don't have any external reference that could be transformed into an internal.
     * This function walkes through the TOC-Items to identify those without any reference and sets a new internal reference.
     * @param tocItem the root-TOC-Item (recursive call)
     * @param pathToItem the path through the TOC-Item-tree to the passed TOC-Item
     * @param usedReferences list of all chapter-references that are already used (to ensure no reference is used twice or more times)
     */
    private void fixReferenceIfNeeded( DocumentTOCItem tocItem, String pathToItem, java.util.Vector<String> usedReferences)
    {
    	if( tocItem!=null )
    	{
    		String tocName = tocItem.getName();
    		if( tocName!=null )
    		{
    			tocName = StringUtils.replace(tocName, ".", "_");
    			tocName = StringUtils.replace(tocName, "\\", "_");
    		}
    		String itemID = new String(pathToItem+"/"+tocName);

    		if( tocItem.getRef() ==null )
    		{
    			itemID = ensureReferenceIsValid(itemID, usedReferences);
    			tocItem.setRef(itemID);
    			usedReferences.addElement(itemID);
    		}
    		java.util.List<DocumentTOCItem> children = tocItem.getItems();
			if( children!=null )
				for( DocumentTOCItem child : children )
				{
					fixReferenceIfNeeded(child, itemID, usedReferences);
				}
    	}
    }
    
    /**
     * Tests if the passed reference is not defined previously.
     * @param reference the reference that is to be checked of previous definition 
     * @param usedReferences all references used before
     * @return the reference if it is unique, otherwise a new unique reference is returned
     */
    private String ensureReferenceIsValid(String reference, java.util.Vector<String> usedReferences)
    {
    	String res = reference;
    	
    	if( reference !=null && usedReferences!=null )
    	{
    		boolean invalidReference = false;
    		for( String tmp : usedReferences )
    			if( tmp!=null && tmp.equals(res) )
    				invalidReference = true;
    		
    		while(invalidReference)
    		{
    			res = res+"_";
    			invalidReference = false;
    			for( String tmp : usedReferences )
        			if( tmp!=null && tmp.equals(res) )
        				invalidReference = true;
    		}
    	}
    	
    	return res;
    }
    
    private void writeTocItems( List<DocumentTOCItem> tocItems, int level )
    {
        int maxTocLevel = 4;//default
        int numberOfColumns = 9;//to make 6 chapter-levels possible, 9 columns are needed
        double spaceBeforeAtAdditionalLine = 0.0;//in inch (defined in fo-styles.xslt)
        
        MutableAttributeSet attsIndentation = getFoConfiguration().getAttributeSet( "toc.hanging.indentation" );
        if( attsIndentation!=null )
        {
        	Object tmp = attsIndentation.getAttribute("indentation");
        	if( tmp!=null )
        	{
        		double indentation = inchFromString( tmp.toString());
        		if( !Double.isNaN(indentation) && !Double.isInfinite(indentation) && indentation>=0.0d )
        			spaceBeforeAtAdditionalLine = indentation;
        	}
        }
        
        DocumentRendererContext context = getRendererContext();
        if( context!=null )
        {
        	Object oMaxDepth = context.get("tocMaxDepthToPrint");
        	if( oMaxDepth!=null )
        	{
        		try {
        			maxTocLevel = Integer.parseInt(oMaxDepth.toString());
        		}
        		catch( NumberFormatException nfe) {
        			
        		}
        	}else
        	{
        	//if not defined take definition from pdf.xml
        		DocumentTOC tmpToc = getDocTOC();
        		if( tmpToc!=null )
        		{
        			int tmpTocLevel = tmpToc.getDepth();

        			if( tmpTocLevel>0 )
        				maxTocLevel=tmpTocLevel;
        		}
        	}
        }

		if( maxTocLevel == 0 )
		{
			writeStartTag( TABLE_ROW_TAG, "keep-with-next", "auto" );
			skipTableCells(1);
			writeEndTag( TABLE_ROW_TAG );
		}

        if ( level < 1 || level > maxTocLevel )
        {
            return;
        }

        tocStack.push( new NumberedListItem( NUMBERING_DECIMAL ) );

        for ( DocumentTOCItem tocItem : tocItems )
        {
        	if( tocItem!=null )
        	{
	            String ref = getIdName( tocItem.getRef() );
	            
	            MutableAttributeSet atts = getFoConfiguration().getAttributeSet( "toc.h" + level + ".style" );
	            String tocItemName = tocItem.getName();
	            
	            String[] tocItemNameSplitted = splitToFit(tocItemName, level, atts, spaceBeforeAtAdditionalLine);
	            
	            
	            for( int i=0; i<tocItemNameSplitted.length; i++ )
	            {
	            	if( i==0 )
	            	{
	            		writeStartTag( TABLE_ROW_TAG, "keep-with-next", "auto" );
	            		skipTableCells(level-1);
	            	}
	            	else
	            	{
	            		writeStartTag( TABLE_ROW_TAG, "keep-with-next", "always" );
	            		skipTableCells(level);
	            	}
		            
		            
	            	NumberedListItem current = tocStack.peek();
	                
	            	if( current!=null && i==0 )
	            	{
		            	writeStartTag( TABLE_CELL_TAG, "toc.cell" );
		                writeStartTag( BLOCK_TAG, "toc.number.style" );
		
		                
		                current.next();
		                if( level<5 )
		                	write( currentTocNumber() );
		            	
		            	writeEndTag( BLOCK_TAG );
		                writeEndTag( TABLE_CELL_TAG );
	            	}
		
	            	int iSpan = numberOfColumns - level -1;
		            String span = Integer.toString( iSpan );//5
		            
		            writeStartTag( TABLE_CELL_TAG, "number-columns-spanned", span, "toc.cell" );
		            
		            if( atts!=null )
		            {
		            	if( i!=0 )
		            	{
		            		atts.addAttribute( "text-align", "left" );
		            		atts.addAttribute( "text-align-last", "start");
		            		
		            		double tmpColWidth = tocColWidthC7-spaceBeforeAtAdditionalLine;
		            		if( iSpan>1 )tmpColWidth+=tocColWidthC6;
		            		if( iSpan>2 )tmpColWidth+=tocColWidthC5;
		            		if( iSpan>3 )tmpColWidth+=tocColWidthC4;
		            		if( iSpan>4 )tmpColWidth+=tocColWidthC3;
		            		if( iSpan>5 )tmpColWidth+=tocColWidthC2;

		            		writeln( "<fo:table table-layout=\"fixed\" width=\"100%\" >" );
		                    writeEmptyTag( TABLE_COLUMN_TAG, "column-width", ""+spaceBeforeAtAdditionalLine+"in" );
		                    writeEmptyTag( TABLE_COLUMN_TAG, "column-width", ""+tmpColWidth+"in" );
		                    
		                    writeStartTag( TABLE_BODY_TAG );
		                    writeStartTag( TABLE_ROW_TAG, "keep-with-next", "always" );
		                    skipTableCells(1);

		                    writeStartTag( TABLE_CELL_TAG );
		            	}else
		            	{
		            		atts.addAttribute( "text-align", "justify" );
		            		atts.addAttribute( "text-align-last", "justify");
		            	}
		            		
		            }else
		            {
		            	atts = new SimpleAttributeSet();
		            	atts.addAttribute( "text-align", "justify" );
	            		atts.addAttribute( "text-align-last", "justify");
		            }
		
		            writeStartTag( BLOCK_TAG, atts );
		            writeStartTag( BASIC_LINK_TAG, "internal-destination", ref );
		            text( tocItemNameSplitted[i] );
		            writeEndTag( BASIC_LINK_TAG );
		            if( i==0 )
		            	writeEmptyTag( LEADER_TAG, "toc.leader.style" );
	            	
	            	writeEndTag( BLOCK_TAG );
	            	
	            	if( i!=0 )
	            	{
	            		writeEndTag( TABLE_CELL_TAG );
	            		writeEndTag( TABLE_ROW_TAG );
	            		writeEndTag( TABLE_BODY_TAG );
	                    writeEndTag( TABLE_TAG );
	            	}
	            	
	            	writeEndTag( TABLE_CELL_TAG );
	            	writeStartTag( TABLE_CELL_TAG, "text-align", "right" );
	            	writeStartTag( BLOCK_TAG );
	            	
	            	if( i==0 )
	            	{
		            	writeStartTag( INLINE_TAG, "page.number" );
		            	writeEmptyTag( PAGE_NUMBER_CITATION_TAG, "ref-id", ref );
		            	writeEndTag( INLINE_TAG );
	            	}
		            writeEndTag( BLOCK_TAG );
		            writeEndTag( TABLE_CELL_TAG );
		            writeEndTag( TABLE_ROW_TAG );
	            }
	            
	            if ( tocItem.getItems() != null )
	            {
	                writeTocItems( tocItem.getItems(), level + 1 );
	            }

        	}
        }

        tocStack.pop();
    }

    /**
     * Sometimes chapter-titles are to long to fit into a single line in toc. If this is the case, this function splits it up in several strings so it can be printed in several lines. 
     * @param tocItemName the 
     * @param level
     * @param atts
     * @param spaceBeforeAtAdditionalLine
     * @return
     */
    private String[] splitToFit(String tocItemName, int level, MutableAttributeSet atts, double spaceBeforeAtAdditionalLine)
    {
    	String breakmark = "\u01DE";//"<#1#>";
    	String[] res = new String[] {tocItemName};
    	double spaceAvailableAtFirstLine = availablePageWidthInInch;
    	double spaceAvailableAtAdditionalLine = availablePageWidthInInch;
    	double offsetLine1 = 0.0d;
    	
    	switch( level )
    	{
	    	case 6:
	    		offsetLine1 	+= tocColWidthC5;
	    	case 5:
	    		offsetLine1 	+= tocColWidthC4;
	    	case 4:
	    		offsetLine1 	+= tocColWidthC3;
	    	case 3:
	    		offsetLine1 	+= tocColWidthC2;
	    	case 2:
	    		offsetLine1 	+= tocColWidthC1;
	    	case 1:
	    		break;
    		default:
    			break;   				
    	}
    	spaceAvailableAtFirstLine -= offsetLine1+tocColWidthC9;
    	spaceAvailableAtAdditionalLine -= offsetLine1+spaceBeforeAtAdditionalLine+tocColWidthC9;
    	
    	if( atts!= null )
    	{
    		double fontWidth = this.extractFontHeightInInch(atts, 2)*0.45d;
    		String words[] = StringUtils.split(tocItemName, " ");
    		
    		if( words!=null && words.length>0 )
    		{
    			StringBuffer tmp = new StringBuffer();
    			double availableSpaceInLine = spaceAvailableAtFirstLine;
    			boolean firstWordInLine = true;
    			
    			for( String word : words )
    			{
    				if( word!=null )
    				{
    					int letters = word.length();
    					if( !firstWordInLine )
    						letters++;
    					double wordLengthInInch = letters*fontWidth;

    					if( wordLengthInInch>availableSpaceInLine )
    					{
    						tmp.append(breakmark).append(" ");
    						availableSpaceInLine = spaceAvailableAtAdditionalLine;
    						firstWordInLine = true;
    					}	
    						
						if( !firstWordInLine )
							tmp.append(" ");
						else
						{
							firstWordInLine = false;
						}
						tmp.append(word);
						availableSpaceInLine -= wordLengthInInch;
    					
    				}
    			}
    			res = StringUtils.split( tmp.toString(), breakmark );
    			for( int i=0 ; i<res.length; i++ )
    				if( res[i]!=null)
    					res[i] = res[i].trim();
    			
    		}
    	}
    	
    	
    	return res;
    }
    
    /**
     * Skips the number of cells in the current table
     * @param numberOfCellsToSkip the number of table-cells to be skipped
     */
    private void skipTableCells(int numberOfCellsToSkip)
    {
    	for( int i=0; i<numberOfCellsToSkip; i++ )
    	{
	    	writeStartTag( TABLE_CELL_TAG );
	        writeSimpleTag( BLOCK_TAG );
	        writeEndTag( TABLE_CELL_TAG );
    	}
    }
    
    private String currentTocNumber()
    {
        StringBuilder ch = new StringBuilder( tocStack.get( 0 ).getListItemSymbol() );

        for ( int i = 1; i < tocStack.size(); i++ )
        {
            ch.append( tocStack.get( i ).getListItemSymbol() );
        }

        return ch.toString();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Writes a fo:bookmark-tree. The DocumentModel has to contain a DocumentTOC for this to work.
     */
    protected void pdfBookmarks()
    {
        if ( docModel == null || docModel.getToc() == null )
        {
            return;
        }

        writeStartTag( BOOKMARK_TREE_TAG );

        renderBookmarkItems( docModel.getToc().getItems() );

        writeEndTag( BOOKMARK_TREE_TAG );
    }

    private void renderBookmarkItems( List<DocumentTOCItem> items )
    {
        for ( DocumentTOCItem tocItem : items )
        {
            String ref = getIdName( tocItem.getRef() );

            writeStartTag( BOOKMARK_TAG, "internal-destination", ref );
            writeStartTag( BOOKMARK_TITLE_TAG );
            text( tocItem.getName() );
            writeEndTag( BOOKMARK_TITLE_TAG );

            if ( tocItem.getItems() != null )
            {
                renderBookmarkItems( tocItem.getItems() );
            }

            writeEndTag( BOOKMARK_TAG );
        }
    }

    /**
     * Document styles, defined in fo-styles.xslt, comtain the pageformat and margins in inches. The value is followed by the letters 'in'.
     * This function removes the measurement unit from the value and converts it into a double-value.
     * @param inch a floating-point-value as a string, followed by 'in' (e.g. "8.25in")
     * @return the requested value as double if valid, otherwise Double.NaN is returned
     */
    private Double inchFromString(String inch)
    {
    	Double res = null;
    	
    	if( inch!=null )
    	{
    		if(inch.endsWith("in"))
    		{
    			res = Double.parseDouble(inch.substring(0, inch.length()-2));
    			
    		}
    	}
    	
    	return res;
    }
    
    /**
     * Writes the executive summery, if it exists and is defined as first item in TOC
     * @Parameter exsumName the (localized) title of the executive summary
     */
    public void execSum(DocumentModel model, String exsumName)
    {
    	writeln( "<fo:page-sequence master-reference=\"body\" initial-page-number=\"0\" format=\"i\">" );
    	
        regionBefore( exsumName, getHeaderText() );
        
        regionAfter( getFooterText() );
        
        writeStartTag( FLOW_TAG, "flow-name", "xsl-region-body" );
        chapterHeading(null, false);
        
    }
    
    /**
     * Casts the Object that represents the DocumentRendererContext to an object of the class DocumentRendererContext.
     * @return the DocumentRendererContext, if exists and cast worked, otherwise null is returned
     */
    public DocumentRendererContext getRendererContext()
    {
    	DocumentRendererContext res = null;
    	
    	if( context!=null && context instanceof DocumentRendererContext )
        {
        	res = (DocumentRendererContext) context;
        }
    	
    	return res;
    }
    
    /**
     * Closes the executive summary page.
     */
    public void closeExecSum()
    {
        writeEndTag( PAGE_SEQUENCE_TAG );
        writeEOL();writeEOL();
    }
    
    /**
     * Writes a cover page. The DocumentModel has to contain a DocumentMeta for this to work.
     */
    public void coverPage()
    {
    	if ( this.docModel == null )
        {
            return;
        }

        DocumentCover cover = docModel.getCover();
        DocumentMeta meta = docModel.getMeta();

        if ( cover == null && meta == null )
        {
            return; // no information for cover page: ignore
        }

        String pageSize = "USLetter";
        String classification = "";
        if( meta!=null )
        {
        	pageSize = meta.getPageSize();
        }
        
        
    	DocumentRendererContext drContext = getRendererContext();
    	if( drContext!=null)
    	{
    		Object tmp = drContext.get("classification");
    		if( tmp!=null )
    			classification = tmp.toString();
        }
    	
        MutableAttributeSet attBase = getFoConfiguration().getAttributeSet( "layout.master.set.base" );

        double paperWidthInInch = 8.5;
        double paperHeightInInch = 11.00;
        
        //since margins are not defined in any available object but in the fo-styles.xslt these are hard coded at this point
        //for backup-reasons if the following extraction does not work
        double marginTopInInch = 0.625;
        double marginBottomInInch = 0.6;
        double marginLeftInInch = 1.0;
        double marginRightInInch = 1.0;
        
        if( pageSize!=null )
        {
	        if( pageSize.equalsIgnoreCase("A4") )
	        {
	        	paperWidthInInch = 8.26;
	        	paperHeightInInch= 11.69;
	        }else if( pageSize.equalsIgnoreCase("US" ) )
	        {
	        	paperWidthInInch = 8.5;
	        	paperHeightInInch= 14.00;
	        }
        }
        
        //more precise if possible
        if( attBase!=null )
        {
        	Double tmpWidth = inchFromString( attBase.getAttribute("page-width").toString());
        	Double tmpHeight= inchFromString( attBase.getAttribute("page-height").toString());
        	Double tmpBaseMarginTop = inchFromString( attBase.getAttribute("margin-top").toString());
        	Double tmpBaseMarginBottom = inchFromString( attBase.getAttribute("margin-bottom").toString());
        	Double tmpBaseMarginLeft = inchFromString( attBase.getAttribute("margin-left").toString());
        	Double tmpBaseMarginRight= inchFromString( attBase.getAttribute("margin-right").toString());
        	
        	if( tmpWidth!=null && tmpWidth>=0.0d )
        		paperWidthInInch = tmpWidth.doubleValue();
        	if( tmpHeight!=null && tmpHeight>=0.0d )
        		paperHeightInInch = tmpHeight.doubleValue();
        	if( tmpBaseMarginTop!=null && tmpBaseMarginTop>=0.0d )
        		marginTopInInch = tmpBaseMarginTop.doubleValue();
        	if( tmpBaseMarginBottom!=null && tmpBaseMarginBottom>=0.0d )
        		marginBottomInInch = tmpBaseMarginBottom.doubleValue();
        	if( tmpBaseMarginLeft!=null && tmpBaseMarginLeft>=0.0d )
        		marginLeftInInch = tmpBaseMarginLeft.doubleValue();
        	if( tmpBaseMarginRight!=null && tmpBaseMarginRight>=0.0d )
        		marginRightInInch = tmpBaseMarginRight.doubleValue();
        }
        
        double availableWidthInInch = paperWidthInInch-marginLeftInInch-marginRightInInch;
        double availableHeightInInch= paperHeightInInch-marginTopInInch-marginBottomInInch;
        availablePageWidthInInch = availableWidthInInch;
            	
            	
        writeln( "<fo:page-sequence master-reference=\"cover-page\">");
        writeCoverHead(cover, availableWidthInInch, classification);
        writeCoverFooter(classification);
        writeCoverBody(cover, availableWidthInInch, availableHeightInInch, meta);
        
        writeEndTag( PAGE_SEQUENCE_TAG );
        writeEOL();writeEOL();
    }
    
    /**
     * Writes the header to the cover-page. DocumentCover has to be defined for this work.
     * @param cover the DocumentCover
     * @param availableWidth the available width (overall width - margins (left and right))
     * @param classification the classificationstring that shall be written to the header (e.g. "UNCLASSIFIED")
     */
    private void writeCoverHead( DocumentCover cover, double availableWidth, String classification)
    {
    	if ( cover == null )
        {
            return;
        }

    	double columnWidth5Cols = availableWidth/5.0d;
        
        MutableAttributeSet att = getFoConfiguration().getAttributeSet( "cover.subtitle" );
        att.addAttribute("height", "2in");
        
        writeStartTag( STATIC_CONTENT_TAG, "flow-name", "xsl-region-before" );
        writeStartTag( BLOCK_TAG, "text-align", "center" );
        writeln( "<fo:table table-layout=\"fixed\" width=\"100%\" >" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", ""+columnWidth5Cols+"in");
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", ""+columnWidth5Cols+"in");
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", ""+columnWidth5Cols+"in");
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", ""+columnWidth5Cols+"in");
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", ""+columnWidth5Cols+"in");
        writeStartTag( TABLE_BODY_TAG );
        writeStartTag( TABLE_ROW_TAG );
        
        //classification
        writeStartTag( TABLE_CELL_TAG, "number-columns-spanned", "5");
        writeStartTag( BLOCK_TAG, "cover.classification");
        writeln( classification );
        writeEndTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );
        
        //spacing between classification and company-logo
        writeStartTag( TABLE_ROW_TAG );
        writeStartTag( TABLE_CELL_TAG, "height", "0.3in" );
        writeSimpleTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );
        
        writeEndTag( TABLE_BODY_TAG );
        writeEndTag( TABLE_TAG );
        writeEndTag( BLOCK_TAG );
        writeEndTag( STATIC_CONTENT_TAG );
    }
    
    /**
     * Writes the body of the cover (including logos, title, POC, distribution statement) to the coverpage
     * @TODO the vertical dimension is not optimized for very small sizes yet. Should be fixed if the intend is to support booklets etc.
     * @param cover the DocumentCover that contains cover-page metadata
     * @param availableWidthInInch  the available width (overall width - margins (left and right))
     * @param meta DocumentMeta (contains document title)
     */
    private void writeCoverBody( DocumentCover cover, double availableWidthInInch, double availableHeightInInch,DocumentMeta meta )
    {
    	if ( cover == null && meta == null )
        {
            return;
        }

    	String subtitle = null;
        String title = null;
        String type = null;
        String compLogo = null;
        String projLogo = null;
        String date = null;
        String poc = null;
        String distStatement = null;
        String effectiveDate = null;
        
        //for testing, supposed to get moved to resources-file
        distStatement = "";
        
        DocumentRendererContext drContext = getRendererContext();
        if( drContext!=null )
        {
        	Object tmp = drContext.get("distributionStatement");
        	if( tmp!=null )
        		distStatement = tmp.toString();
        	
        	tmp = drContext.get("coverDate");
        	if( tmp!=null )
        		effectiveDate = tmp.toString();
        }
        
        if ( cover == null )
        {
            // aleady checked that meta != null
            getLog().debug( "The DocumentCover is not defined, using the DocumentMeta title as cover title." );
            title = meta.getTitle();
            
        }
        else
        {
        	subtitle = cover.getCoverSubTitle();
            title = cover.getCoverTitle();
            type = cover.getCoverType();
            compLogo = (cover.getCompanyLogo());
            projLogo = (cover.getProjectLogo());
            String tmpAuthor =cover.getAuthor();
            if( tmpAuthor!=null && !tmpAuthor.trim().isEmpty())
            	poc = "POC: "+tmpAuthor;
            else poc = "";
            
            if ( cover.getCoverdate() == null )
            {
                cover.setCoverDate( new Date() );
                date = cover.getCoverdate();
                cover.setCoverDate( null );
            }
            else
            {
                date = cover.getCoverdate();
            }
        }
        
        if( effectiveDate!=null )
        {
        	if( effectiveDate.equalsIgnoreCase("auto") )
        		effectiveDate = date;
        	//otherwise there is a specific string that has to be printed on the cover-page as date
        }
        
        double leftColumnWidth = 0.0d;
        double rightColumnWidth= leftColumnWidth;
        double midColumnWidth = availableWidthInInch-leftColumnWidth-rightColumnWidth;
        
        double logoCol1Width = availableWidthInInch/5.0d*2.0d;
        double logoCol2Width = availableWidthInInch-logoCol1Width;
        
        
        boolean containsCLogo = StringUtils.isNotEmpty( compLogo );
        boolean containsPLogo = StringUtils.isNotEmpty( projLogo );
        boolean containsType = StringUtils.isNotEmpty( type );
        boolean containsPoc = StringUtils.isNotEmpty( poc );
        boolean containsDistStatement = StringUtils.isNotEmpty( distStatement );
        
        //calcultation of free space to be filled by spacers
        double compLogoHeight 	= 0.0d;
        double projLogoHeight 	= 0.0d;
        double titleHeight 		= 0.0d;
        double subtitleHeight 	= 0.0d;
        double typeHeight 		= 0.0d;
        double dateHeight 		= 0.0d;
        double pocHeight 		= 0.0d;
        double distStateHeight 	= 0.0d;
        
        if ( containsCLogo )
        {
        	compLogo = StringUtils.replace(compLogo, '\\', '/');
        	compLogo = findAbsolutePath(compLogo);
        	SinkEventAttributeSet atts = getGraphicsAttributes( compLogo );
            atts.addAttribute("content-width", ""+logoCol1Width+"in");
            compLogoHeight = calculateImageHeight(atts);
        }
        if ( containsPLogo )
        {
        	projLogo = StringUtils.replace(projLogo, '\\', '/');
        	projLogo = findAbsolutePath(projLogo);
        	SinkEventAttributeSet atts = getGraphicsAttributes( projLogo );
            atts.addAttribute("content-width", ""+midColumnWidth+"in");
            projLogoHeight = calculateImageHeight(atts);
        }
        
        FoConfiguration foConf = getFoConfiguration();
        MutableAttributeSet attTitle = null;
        MutableAttributeSet attSubtitle = null;
        MutableAttributeSet attDate = null;
        MutableAttributeSet attPOC = null;
        MutableAttributeSet attDistStat = null;
        
        if( foConf!=null )
        {
	        attTitle = foConf.getAttributeSet( "cover.title" );
	        attSubtitle = foConf.getAttributeSet( "cover.subtitle" );
	        attDate = foConf.getAttributeSet( "cover.date" );
	        attPOC = foConf.getAttributeSet( "cover.poc" );
	        attDistStat = foConf.getAttributeSet( "cover.distributionstatement" );
	        titleHeight = extractFontHeightInInch(attTitle, 2);
	        subtitleHeight = extractFontHeightInInch(attSubtitle, 2);
	        typeHeight = extractFontHeightInInch(attSubtitle, 2);
	        dateHeight = extractFontHeightInInch(attDate, 2);
	        pocHeight = extractFontHeightInInch(attPOC, 2);
	        distStateHeight = extractFontHeightInInch(attDistStat, 2);
        }
        
        
        //initial spacings in inch, to be (partly) scaled to fit the page
        double spcHdrCLogo = 0.675d;
        double spcCLogoPLogo = 0.5d;
        double spcPLogoTitle = 0.5d;
        double spcTitleSubT = 0.15d;
        double spcSubTType	 = 0.3d;
        double spcTypeDate	 = 0.5d;
        double spcDatePoc	 = 0.6d;
        double spcPocDist	 = 1.0d;
        double spcDistFooter = 1.0d;
        
        double adjustableSpacers = spcCLogoPLogo+spcPLogoTitle+spcTitleSubT;        
        double overAllHeightOfText = titleHeight+subtitleHeight;
        
        if ( containsType )
        {
        	adjustableSpacers+= spcSubTType;
        	overAllHeightOfText+= typeHeight;
        }
        if ( containsPoc )
        {
        	adjustableSpacers+= spcDatePoc;
        	overAllHeightOfText+= pocHeight;
        }
        if ( containsDistStatement )
        {
        	adjustableSpacers+= spcPocDist;
        	overAllHeightOfText+= distStateHeight*3;
        }
        if( effectiveDate!=null )
        {
        	adjustableSpacers += spcTypeDate;
        	overAllHeightOfText+= dateHeight;
        }
        
        double tmpAvailableHeight = availableHeightInInch-spcHdrCLogo-spcDistFooter-overAllHeightOfText-compLogoHeight-projLogoHeight;
        double spacersFactor = tmpAvailableHeight/adjustableSpacers;

        spcCLogoPLogo 	*= spacersFactor;
        spcPLogoTitle 	*= spacersFactor;
        spcTitleSubT 	*= spacersFactor;
        spcSubTType	 	*= spacersFactor;
        spcTypeDate	 	*= spacersFactor;
        spcDatePoc	 	*= spacersFactor;
        spcPocDist	 	*= spacersFactor;
        
        //starting to build up document
        writeStartTag( FLOW_TAG, "flow-name", "xsl-region-body" );
        writeStartTag( BLOCK_TAG, "text-align", "center" );
        writeln( "<fo:table table-layout=\"fixed\" width=\"100%\" >" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", ""+leftColumnWidth+"in");
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", ""+midColumnWidth+"in");
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", ""+rightColumnWidth+"in");
        writeStartTag( TABLE_BODY_TAG );
        
        //spacing between header and company-logo
        writeStartTag( TABLE_ROW_TAG );
        writeStartTag( TABLE_CELL_TAG, "height", ""+spcHdrCLogo+"in" );
        writeSimpleTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );
        
        //company logo
        writeStartTag( TABLE_ROW_TAG );
        writeStartTag( TABLE_CELL_TAG, "number-columns-spanned", "3" );
        writeStartTag( BLOCK_TAG );
        writeln( "<fo:table table-layout=\"fixed\" width=\"100%\" >" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", ""+logoCol1Width+"in");//"2.083in" );
        writeEmptyTag( TABLE_COLUMN_TAG, "column-width", ""+logoCol2Width+"in");//"2.083in" );
        writeStartTag( TABLE_BODY_TAG );
        writeStartTag( TABLE_ROW_TAG );
        writeStartTag( TABLE_CELL_TAG, "height", ""+compLogoHeight+"in" );
        
        if ( StringUtils.isNotEmpty( compLogo ) )
        {
            SinkEventAttributeSet atts = getGraphicsAttributes( compLogo );
            atts.removeAttribute("height");
            atts.removeAttribute("width");
            atts.addAttribute("width", logoCol1Width+"in");
            atts.addAttribute("height", compLogoHeight+"in");
            atts.addAttribute( "text-align", "left" );
            atts.addAttribute( "vertical-align", "top" );
            
            writeStartTag( BLOCK_TAG, atts );
            figureGraphics( compLogo, atts );
            writeEndTag( BLOCK_TAG );
        }else
        {
        	writeSimpleTag( BLOCK_TAG );
        }
        
        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );
        writeEndTag( TABLE_BODY_TAG );
        writeEndTag( TABLE_TAG );
        writeEndTag( BLOCK_TAG );
        
        writeSimpleTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );
        
        //spacing between company-logo and project-logo
        writeStartTag( TABLE_ROW_TAG );
        writeStartTag( TABLE_CELL_TAG, "height", ""+spcCLogoPLogo+"in" );
        writeSimpleTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );
        
        writeStartTag( TABLE_ROW_TAG );
        //move logo to middle column
        writeStartTag( TABLE_CELL_TAG );
        writeSimpleTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        
        //project-logo
        if ( StringUtils.isNotEmpty( projLogo ) )
        {
        	writeStartTag( TABLE_CELL_TAG, "height", ""+projLogoHeight+"in"  );
            SinkEventAttributes sea = getGraphicsAttributes( projLogo );
            if(sea!=null )
            {
				sea.addAttribute("width", ""+midColumnWidth+"in");
				sea.addAttribute("content-width", ""+midColumnWidth+"in");
				sea.addAttribute("height", projLogoHeight+"in");
				writeStartTag( BLOCK_TAG );//, sea );
	            figureGraphics( projLogo, sea );
            }
            writeEndTag( BLOCK_TAG );
            writeEndTag( TABLE_CELL_TAG );
        }else
        {
          writeStartTag( TABLE_CELL_TAG );
          writeSimpleTag( BLOCK_TAG );
          writeEndTag( TABLE_CELL_TAG );
        }
        
        writeEndTag( TABLE_ROW_TAG );
        
        //spacing between project-logo and title
        writeStartTag( TABLE_ROW_TAG );
        writeStartTag( TABLE_CELL_TAG, "height", ""+spcPLogoTitle+"in");
        writeSimpleTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );
        
        //title
        writeStartTag( TABLE_ROW_TAG );
        writeStartTag( TABLE_CELL_TAG );
        writeSimpleTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        
        writeStartTag( TABLE_CELL_TAG );
        writeStartTag( BLOCK_TAG, attTitle );
        text( title == null ? "" : title );
        writeEndTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );

        //spacing between title and subtitle
        writeStartTag( TABLE_ROW_TAG );
        writeStartTag( TABLE_CELL_TAG, "height", ""+spcTitleSubT+"in" );
        writeSimpleTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );
        
        //subtitle
        writeStartTag( TABLE_ROW_TAG );
        writeStartTag( TABLE_CELL_TAG );
        writeSimpleTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        
        writeStartTag( TABLE_CELL_TAG );
        writeStartTag( BLOCK_TAG, attSubtitle );
        text( subtitle == null ? "" : subtitle );
        writeEndTag( BLOCK_TAG );
        writeEndTag( TABLE_CELL_TAG );
        writeEndTag( TABLE_ROW_TAG );

        if( containsType )
        {
	        //spacing between subtitle and type
	        writeStartTag( TABLE_ROW_TAG );
	        writeStartTag( TABLE_CELL_TAG, "height", ""+spcSubTType+"in" );
	        writeSimpleTag( BLOCK_TAG );
	        writeEndTag( TABLE_CELL_TAG );
	        writeEndTag( TABLE_ROW_TAG );
	        
	        //type
	        writeStartTag( TABLE_ROW_TAG );
	        writeStartTag( TABLE_CELL_TAG );
	        writeSimpleTag( BLOCK_TAG );
	        writeEndTag( TABLE_CELL_TAG );
        
	        writeStartTag( TABLE_CELL_TAG );
	        writeStartTag( BLOCK_TAG, attSubtitle );
	        text( type == null ? "" : type );
	        writeEndTag( BLOCK_TAG );
	        writeEndTag( TABLE_CELL_TAG );
	        writeEndTag( TABLE_ROW_TAG );
        }
        
        if( effectiveDate!=null )
        {
	        //spacing between type and date
	        writeStartTag( TABLE_ROW_TAG );
	        writeStartTag( TABLE_CELL_TAG, "height", ""+spcTypeDate+"in" );
	        writeSimpleTag( BLOCK_TAG );
	        writeEndTag( TABLE_CELL_TAG );
	        writeEndTag( TABLE_ROW_TAG );
	        
	        //date
	        writeStartTag( TABLE_ROW_TAG );
	        writeStartTag( TABLE_CELL_TAG );
	        writeSimpleTag( BLOCK_TAG );
	        writeEndTag( TABLE_CELL_TAG );
	        writeStartTag( TABLE_CELL_TAG );
	        writeStartTag( BLOCK_TAG, attDate );
	        text( date == null ? "" : effectiveDate );
	        writeEndTag( BLOCK_TAG );
	        writeEndTag( TABLE_CELL_TAG );
	        writeEndTag( TABLE_ROW_TAG );
        }
        
        //spacing between date and POC
        if( containsPoc )
        {
	        writeStartTag( TABLE_ROW_TAG );
	        writeStartTag( TABLE_CELL_TAG, "height", ""+spcDatePoc+"in" );
	        writeSimpleTag( BLOCK_TAG );
	        writeEndTag( TABLE_CELL_TAG );
	        writeEndTag( TABLE_ROW_TAG );
	        
	        //poc
	        writeStartTag( TABLE_ROW_TAG );
	        writeStartTag( TABLE_CELL_TAG, "number-columns-spanned", "3" );
	        writeStartTag( BLOCK_TAG, attPOC );
	        text( poc == null ? "poc missing" : poc );
	        writeEndTag( BLOCK_TAG );
	        writeEndTag( TABLE_CELL_TAG );
	        writeEndTag( TABLE_ROW_TAG );
        }
        
        if( containsDistStatement )
        {
	        //spacing between poc and distribution statement
	        writeStartTag( TABLE_ROW_TAG );
	        writeStartTag( TABLE_CELL_TAG, "height", ""+spcPocDist+"in" );
	        writeSimpleTag( BLOCK_TAG );
	        writeEndTag( TABLE_CELL_TAG );
	        writeEndTag( TABLE_ROW_TAG );
	
	        //distribution statement
	        writeStartTag( TABLE_ROW_TAG );
	        writeStartTag( TABLE_CELL_TAG, "number-columns-spanned", "3" );
	        writeStartTag( BLOCK_TAG, attDistStat );
	        text( distStatement == null ? "no distribution statment set" : distStatement );
	        writeEndTag( BLOCK_TAG );
	        writeEndTag( TABLE_CELL_TAG );
	        writeEndTag( TABLE_ROW_TAG );
        }
        
        //end so that the footer can be written
        writeEndTag( TABLE_BODY_TAG );
        writeEndTag( TABLE_TAG );
        writeEndTag( BLOCK_TAG );
        writeEndTag( FLOW_TAG );
    }
    
    /**
     * Estimates the font height in inches.
     * @param mas MultipleAttributeSet that represents the font
     * @param spacing additional amount if dots to seperate one line from another
     * @return the inch-value of the height which is consumed by the a line of the font
     */
    private double extractFontHeightInInch( MutableAttributeSet mas, int spacing)
    {
    	double res = 0.0d;
    	
    	if( mas!=null )
    	{
    		Object oFtSize = mas.getAttribute("font-size");
    		if( oFtSize!=null )
    		{
    			String strFtSize = oFtSize.toString();
    			if( strFtSize.endsWith("pt") )
    			{
    				try
    				{
    					
	    				double pxHeight = Double.parseDouble(strFtSize.substring(0, strFtSize.length()-2));

	    				if( spacing>0 )
	    					pxHeight+=spacing;
	    				if( pxHeight>0 )
	    					res = pxHeight/72.0d; //72 dotsPerInch
    				}catch(NumberFormatException nfe)
    				{
    					res = spacing/72.0d;
    				}
    			}
    		}
    	}
    	
    	return res;
    }
    
    /**
     * Calculates the height of an image in inch that is characterized by the passed SinkEventAttributeSet.
     * @param seas the SinkEventAttributeSet that characterizes the image. "width", "height" and "content-width" 
     * have to be defined in this object. "width" and "height" specify the size in pixels/dots, "content-width" is scaled in inches
     * @return the image height in inches if input-data was like expected. Otherwise 0.0d
     */
    private double calculateImageHeight(SinkEventAttributeSet seas)
    {
    	double res =0.0d;
    	
    	if( seas!=null )
    	{
    		Object strPxWidth = seas.getAttribute("width");
    		Object strPxHeight= seas.getAttribute("height");
    		Object strContWidth = seas.getAttribute("content-width");
    		if( strPxWidth!=null && strPxHeight!=null && strContWidth!=null )
    		{
    			int pxWidth = Integer.parseInt(strPxWidth.toString());
    			int pxHeight = Integer.parseInt(strPxHeight.toString());
    			double contWidth = inchFromString(strContWidth.toString());

    			res = calculateImageHeight(pxWidth, pxHeight, contWidth);
    		}
    	}
    	
    	return res;
    }
    
    /**
     * Calculates the height of an image in inch that is characterized by the passed parameters.
     * @param pixelsWidth images width in pixels/dots
     * @param pixelsHeight images height in pixels/dots 
     * @param contentWidth images width in inches
     * @return the image height in inches if input-data was like expected. Otherwise 0.0d
     */
    private double calculateImageHeight(int pixelsWidth, int pixelsHeight, double contentWidth)
    {
    	double res = 0.0d;
    	
    	if( pixelsWidth > 0 && pixelsHeight>0 &&contentWidth>0 )
    	{
    		res=contentWidth/pixelsWidth*pixelsHeight;
    	}
    	
    	return res;
    }
    
    /**
     * Writes the footer to the cover-page.
     * @param classification the classification that is to be written to the cover-pages footer
     */
    private void writeCoverFooter( String classification )
    {
    	writeStartTag( STATIC_CONTENT_TAG, "flow-name", "xsl-region-after" );
        writeStartTag( BLOCK_TAG, "cover.classification");
       
        if ( classification != null )
        {
            write( classification );
        }
        writeEndTag( BLOCK_TAG );
        writeEndTag( STATIC_CONTENT_TAG );
    }
    
    private ResourceBundle getBundle( Locale locale )
    {
        return ResourceBundle.getBundle( "doxia-fo", locale, this.getClass().getClassLoader() );
    }

    private SinkEventAttributeSet getGraphicsAttributes( String logo )
    {
        MutableAttributeSet atts = null;

        try
        {
            atts = DoxiaUtils.getImageAttributes( logo );
        }
        catch ( IOException e )
        {
            getLog().debug( e );
        }

        if ( atts == null )
        {
            return new SinkEventAttributeSet( new String[]{ SinkEventAttributes.HEIGHT, COVER_HEADER_HEIGHT } );
        }

        // FOP dpi: 72
        // Max width : 3.125 inch, table cell size, see #coverPage()
        final int maxWidth = 225; // 3.125 * 72

        if ( Integer.parseInt( atts.getAttribute( SinkEventAttributes.WIDTH ).toString() ) > maxWidth )
        {
            atts.addAttribute( "content-width", "3.125in" );
        }

        return new SinkEventAttributeSet( atts );
    }

    /**
     * (De-)Activates the mode of writing pages between document-cover and table of contents. (needed to write the executive summary)
     * @param skip
     */
    public void activatePriorPageWriting(boolean skip)
    {
    	writingPriorPage = skip;
    }
    
    public void resetPageCounter()
    {
    	resetPageCounter = true;
    }
    
    public void setChapter(int chapterNumber)
    {
    	chapter = chapterNumber;
    }
}
