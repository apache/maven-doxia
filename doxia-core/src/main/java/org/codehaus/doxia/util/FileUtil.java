/*
 * Copyright (c) 1998,1999,2000-2001 Pixware. 
 *
 * Author: Hussein Shafie
 *
 * This file is part of the Pixware Java utilities.
 * For conditions of distribution and use, see the accompanying legal.txt file.
 */
package org.codehaus.doxia.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

/**
 * A collection of utility functions (static methods) operating on files and
 * directories.
 */
public class FileUtil
{
    /**
     * Converts a <tt>file:</tt> URL to a File.
     *
     * @param url the URL to be converted
     * @return the result of the conversion or <code>null</code> if
     *         <code>url</code> is not a <tt>file:</tt> URL
     */
    public static File urlToFile( URL url )
    {
        if ( !url.getProtocol().equals( "file" ) )
            return null;

        String fileName = url.getFile();

        if ( File.separatorChar != '/' )
            fileName = fileName.replace( '/', File.separatorChar );

        return new File( fileName );
    }

    /**
     * Converts a <tt>file:</tt> URL name to a File.
     *
     * @param urlName the URL name to be converted
     * @return the result of the conversion or <code>null</code> if
     *         <code>urlName</code> is not a <tt>file:</tt> URL
     */
    public static File urlToFile( String urlName )
    {
        URL url;
        try
        {
            url = new URL( urlName );
        }
        catch ( MalformedURLException e )
        {
            //e.printStackTrace();
            url = null;
        }

        return ( url == null ) ? null : urlToFile( url );
    }

    /**
     * Converts a <tt>file:</tt> URL name to a file name.
     *
     * @param urlName the URL name to be converted
     * @return the result of the conversion or <code>null</code> if
     *         <code>urlName</code> is not a <tt>file:</tt> URL
     */
    public static String urlToFileName( String urlName )
    {
        File file = urlToFile( urlName );
        return ( file == null ) ? null : file.getAbsolutePath();
    }

    /**
     * Converts a File to a <tt>file:</tt> URL.
     * 
     * @param file the file to be converted
     * @return the result of the conversion
     */
    public static URL fileToURL( File file )
    {
        String fileName = file.getAbsolutePath();

        if ( File.separatorChar != '/' )
            fileName = fileName.replace( File.separatorChar, '/' );

        if ( !fileName.startsWith( "/" ) )
            fileName = "/" + fileName;

        if ( !fileName.endsWith( "/" ) && file.isDirectory() )
            fileName = fileName + "/";

        URL url;
        try
        {
            url = new URL( "file", "", -1, fileName );
        }
        catch ( MalformedURLException cannotHappen )
        {
            //e.printStackTrace();
            url = null;
        }
        return url;
    }

    /**
     * Converts a file name to a <tt>file:</tt> URL.
     * 
     * @param fileName the file name to be converted
     * @return the result of the conversion
     */
    public static URL fileToURL( String fileName )
    {
        return fileToURL( new File( fileName ) );
    }

    /**
     * Converts a file name to a <tt>file:</tt> URL name.
     * 
     * @param fileName the file name to be converted
     * @return the result of the conversion
     */
    public static String fileToURLName( String fileName )
    {
        URL url = fileToURL( fileName );
        return ( url == null ) ? null : url.toExternalForm();
    }

    // -----------------------------------------------------------------------

    /**
     * Returns the directory part in a file path name. The directory part is
     * everything before the last file path name separator (that is,
     * <tt>'\'</tt> for Windows).
     * <p>On Windows, <tt>'/'</tt> is used as an alternate file path name
     * separator.
     * <p>Examples:
     * <table border="1">
     * <tr>
     * <th bgcolor="#C0C0C0" align="center">Path
     * <th bgcolor="#C0C0C0" align="center">Result
     * <tr>
     * <td>util/FileUtil.java
     * <td>util
     * <tr>
     * <td>FileUtil.java
     * <td>. (dot)
     * </table>
     * 
     * @param fileName a file path name
     * @return the directory part
     */
    public static String fileDirName( String fileName )
    {
        char separ = File.separatorChar;
        int slash = fileName.lastIndexOf( separ );
        if ( slash < 0 && separ == '\\' )
        {
            separ = '/';
            // On Windows, '/' is an alternate fileName separator.
            slash = fileName.lastIndexOf( separ );
        }

        String name;
        if ( slash < 0 )
        {
            name = ".";
        }
        else if ( slash == 0 )
        {
            name = File.separator;
        }
        else
        {
            name = fileName.substring( 0, slash );
            if ( separ != File.separatorChar )
                name = name.replace( separ, File.separatorChar );
        }
        return name;
    }

    /**
     * Returns the base name part in a file path name. The base name part is
     * everything after the last file name separator (that is <tt>'\'</tt> for
     * Windows).
     * <p>On Windows, <tt>'/'</tt> is used as an alternate file path name
     * separator.
     * <table border="1">
     * <tr>
     * <th bgcolor="#C0C0C0" align="center">Path
     * <th bgcolor="#C0C0C0" align="center">Result
     * <tr>
     * <td>util/FileUtil.java
     * <td>FileUtil.java
     * <tr>
     * <td>FileUtil.java
     * <td>FileUtil.java
     * </table>
     * 
     * @param fileName a file path name
     * @return the base name part
     */
    public static String fileBaseName( String fileName )
    {
        int slash = fileName.lastIndexOf( File.separatorChar );
        if ( slash < 0 && File.separatorChar == '\\' )
        // On Windows, '/' is an alternate fileName separator.
            slash = fileName.lastIndexOf( '/' );

        String name;
        if ( slash < 0 )
            name = fileName;
        else
            name = fileName.substring( slash + 1 );
        return name;
    }

    /**
     * Returns the file extension part in a file path name. The file extension
     * part is everything after the last dot.
     * <p>On Windows, <tt>'/'</tt> is used as an alternate file path name
     * separator.
     * <table border="1">
     * <tr>
     * <th bgcolor="#C0C0C0" align="center">Path
     * <th bgcolor="#C0C0C0" align="center">Result
     * <tr>
     * <td>util/FileUtil.java
     * <td>.java
     * <tr>
     * <td>makefile
     * <td>"" (empty string)
     * <tr>
     * <td>/home/hussein/.profile
     * <td>"" (empty string)
     * </table>
     *
     * @param fileName a file path name
     * @return the file extension part; does not include the dot
     *         <p>If the base name without its extension is empty, the path is
     *         considered not to have an extension part. This is the case of
     *         <tt>/home/hussein/.profile</tt> in the examples above.
     */
    public static String fileExtension( String fileName )
    {
        int slash = fileName.lastIndexOf( File.separatorChar );
        if ( slash < 0 && File.separatorChar == '\\' )
        // On Windows, '/' is an alternate fileName separator.
            slash = fileName.lastIndexOf( '/' );
        if ( slash < 0 )
            slash = 0;
        else
            ++slash;

        int dot = fileName.lastIndexOf( '.' );
        if ( dot <= slash )
        // '.profile' has no extension!
            return "";
        else
            return fileName.substring( dot + 1 );
    }

    /**
     * Returns a file path name without its file extension part. The file
     * extension part is everything after the last dot.
     * <p>On Windows, <tt>'/'</tt> is used as an alternate file path name
     * separator.
     * <table border="1">
     * <tr>
     * <th bgcolor="#C0C0C0" align="center">Path
     * <th bgcolor="#C0C0C0" align="center">Result
     * <tr>
     * <td>util/FileUtil.java
     * <td>util/FileUtil
     * <tr>
     * <td>makefile
     * <td>makefile
     * <tr>
     * <td>/home/hussein/.profile
     * <td>/home/hussein/.profile
     * </table>
     * 
     * @param fileName a file path name
     * @return the file path without extension part if any
     */
    public static String trimFileExtension( String fileName )
    {
        int slash = fileName.lastIndexOf( File.separatorChar );
        if ( slash < 0 && File.separatorChar == '\\' )
        // On Windows, '/' is an alternate fileName separator.
            slash = fileName.lastIndexOf( '/' );
        if ( slash < 0 )
            slash = 0;
        else
            ++slash;

        int dot = fileName.lastIndexOf( '.' );
        if ( dot <= slash )
        // '.profile' has no extension!
            return fileName;
        else
            return fileName.substring( 0, dot );
    }

    /**
     * A simple test for all functions dealing with file path names.
     */
    public static void main( String[] args )
    {
        for ( int i = 0; i < args.length; ++i )
        {
            String arg = args[i];
            System.out.println( "'" + fileDirName( arg ) + "'" );
            System.out.println( "'" + fileBaseName( arg ) + "'" );
            System.out.println( "'" + trimFileExtension( arg ) + "'" );
            System.out.println( "'" + fileExtension( arg ) + "'" );
            System.out.println( "'" + fileToURLName( arg ) + "'" );
            System.out.println( "---" );
        }
    }

    // -----------------------------------------------------------------------

    /**
     * Deletes a file or an empty directory.
     *
     * @param fileName the name of the file or empty directory to be deleted
     * @return <code>true</code> if the file or directory has been
     *         successfully deleted; <code>false</code> otherwise
     */
    public static boolean removeFile( String fileName )
    {
        return removeFile( fileName, /*force*/ false );
    }

    /**
     * Deletes a file or a directory, possibly emptying the directory before
     * deleting it.
     *
     * @param fileName the name of the file or directory to be deleted
     * @param force    if <code>true</code> and the file to be deleted is a
     *                 non-empty directory, empty it before attempting to delete it; if
     *                 <code>false</code>, do not empty directories
     * @return <code>true</code> if the file or directory has been
     *         successfully deleted; <code>false</code> otherwise
     */
    public static boolean removeFile( String fileName, boolean force )
    {
        return removeFile( new File( fileName ), force );
    }

    /**
     * Deletes a file or a directory, possibly emptying the directory before
     * deleting it.
     *
     * @param file  the file or directory to be deleted
     * @param force if <code>true</code> and the file to be deleted is a
     *              non-empty directory, empty it before attempting to delete it; if
     *              <code>false</code>, do not empty directories
     * @return <code>true</code> if the file or directory has been
     *         successfully deleted; <code>false</code> otherwise
     */
    public static boolean removeFile( File file, boolean force )
    {
        if ( file.isDirectory() && force )
            emptyDirectory( file );

        return file.delete();
    }

    /**
     * Recursively deletes all the entries of a directory.
     * 
     * @param dirName the name of the directory to be emptied
     */
    public static void emptyDirectory( String dirName )
    {
        emptyDirectory( new File( dirName ) );
    }

    /**
     * Recursively deletes all the entries of a directory.
     * 
     * @param dir the directory to be emptied
     */
    public static void emptyDirectory( File dir )
    {
        String[] children = dir.list();

        for ( int i = 0; i < children.length; ++i )
        {
            File child = new File( dir, children[i] );

            if ( child.isDirectory() )
                removeFile( child, /*force*/ true );
            else
                child.delete();
        }
    }

    /**
     * Copy a file.
     *
     * @param srcFileName the name of the copied file
     * @param dstFileName the name of the copy
     * @throws IOException if there is an IO problem
     */
    public static void copyFile( String srcFileName, String dstFileName )
        throws IOException
    {
        FileInputStream src = new FileInputStream( srcFileName );
        FileOutputStream dst = new FileOutputStream( dstFileName );

        byte[] bytes = new byte[8192];
        int count;

        while ( ( count = src.read( bytes ) ) != -1 )
            dst.write( bytes, 0, count );

        src.close();
        dst.flush();
        dst.close();
    }

    // -----------------------------------------------------------------------

    /**
     * Loads the content of a text file.
     *
     * @param fileName the name of the text file
     * @return the loaded String
     * @throws IOException if there is an IO problem
     */
    public static String loadString( String fileName )
        throws FileNotFoundException, IOException
    {
        return loadString( new FileInputStream( fileName ) );
    }

    /**
     * Loads the content of an URL containing text.
     *
     * @param url the URL of the text resource
     * @return the loaded String
     * @throws IOException if there is an IO problem
     */
    public static String loadString( URL url ) throws IOException
    {
        URLConnection connection = url.openConnection();
        connection.setDefaultUseCaches( false );
        connection.setUseCaches( false );
        connection.setIfModifiedSince( 0 );
        return loadString( connection.getInputStream() );
    }

    /**
     * Loads the content of an InputStream returning text.
     *
     * @param stream the text source
     * @return the loaded String
     * @throws IOException if there is an IO problem
     */
    public static String loadString( InputStream stream ) throws IOException
    {
        InputStreamReader in = new InputStreamReader( stream );

        char[] chars = new char[8192];
        StringBuffer buffer = new StringBuffer( chars.length );
        int count;

        try
        {
            while ( ( count = in.read( chars, 0, chars.length ) ) != -1 )
            {
                if ( count > 0 )
                    buffer.append( chars, 0, count );
            }
        }
        finally
        {
            in.close();
        }

        return buffer.toString();
    }

    /**
     * Saves some text to a file.
     *
     * @param string   the text to be saved
     * @param fileName the name of the file
     * @throws IOException if there is an IO problem
     */
    public static void saveString( String string, String fileName )
        throws IOException
    {
        saveString( string, new FileOutputStream( fileName ) );
    }

    /**
     * Saves some text to an OutputStream.
     *
     * @param string the text to be saved
     * @param stream the text sink
     * @throws IOException if there is an IO problem
     */
    public static void saveString( String string, OutputStream stream )
        throws IOException
    {
        OutputStreamWriter out = new OutputStreamWriter( stream );

        out.write( string, 0, string.length() );
        out.flush();

        out.close();
    }

    // -----------------------------------------------------------------------

    /**
     * Loads the content of a binary file.
     *
     * @param fileName the name of the binary file
     * @return the loaded bytes
     * @throws IOException if there is an IO problem
     */
    public static byte[] loadBytes( String fileName ) throws IOException
    {
        return loadBytes( new FileInputStream( fileName ) );
    }

    /**
     * Loads the content of an URL containing binary data.
     *
     * @param url the URL of the binary data
     * @return the loaded bytes
     * @throws IOException if there is an IO problem
     */
    public static byte[] loadBytes( URL url ) throws IOException
    {
        URLConnection connection = url.openConnection();
        connection.setDefaultUseCaches( false );
        connection.setUseCaches( false );
        connection.setIfModifiedSince( 0 );
        return loadBytes( connection.getInputStream() );
    }

    /**
     * Loads the content of an InputStream returning binary data.
     *
     * @param source the binary data source
     * @return the loaded bytes
     * @throws IOException if there is an IO problem
     */
    public static byte[] loadBytes( InputStream source ) throws IOException
    {
        BufferedInputStream in = new BufferedInputStream( source );
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] bytes = new byte[8192];
        int count;

        while ( ( count = in.read( bytes ) ) >= 0 )
            out.write( bytes, 0, count );

        return out.toByteArray();
    }

    // -----------------------------------------------------------------------

    /**
     * Tests if a file has been compressed using gzip.
     *
     * @param fileName the name of the file to be tested
     * @return <code>true</code> if the file has been gzip-ed,
     *         <code>false</code> otherwise
     * @throws IOException if there is an IO problem
     */
    public static boolean isGzipped( String fileName ) throws IOException
    {
        InputStream in = new FileInputStream( fileName );
        int magic1 = in.read();
        int magic2 = in.read();
        in.close();
        return ( magic1 == 0037 && magic2 == 0213 );
    }

    /**
     * Loads the content of a text file compressed using gzip.
     *
     * @param fileName the name of the gzip-ed file; the encoding of the text
     *                 before compression is assumed to be the default encoding of the
     *                 platform
     * @return the loaded String
     * @throws IOException if there is an IO problem
     * @see #defaultEncoding
     */
    public static String loadGzippedString( String fileName )
        throws IOException
    {
        return loadGzippedString( new FileInputStream( fileName ), null );
    }

    /**
     * Loads the content of an URL containing text compressed using gzip.
     *
     * @param url the URL of the gzip-ed data; the encoding of the text before
     *            compression is assumed to be the default encoding of the platform
     * @return the loaded String
     * @throws IOException if there is an IO problem
     * @see #defaultEncoding
     */
    public static String loadGzippedString( URL url ) throws IOException
    {
        return loadGzippedString( url.openStream(), null );
    }

    /**
     * Loads the content of an InputStream returning text compressed using
     * gzip.
     *
     * @param source   the gzip-ed data source
     * @param encoding the encoding of the text before compression
     * @return the loaded String
     * @throws IOException if there is an IO problem
     */
    public static String loadGzippedString( InputStream source,
                                            String encoding )
        throws IOException
    {
        if ( encoding == null )
            encoding = defaultEncoding();

        Reader in = new InputStreamReader( new GZIPInputStream( source ),
                                           encoding );
        char[] chars = new char[8192];
        StringBuffer buffer = new StringBuffer( chars.length );
        int count;

        try
        {
            while ( ( count = in.read( chars, 0, chars.length ) ) != -1 )
            {
                if ( count > 0 )
                    buffer.append( chars, 0, count );
            }
        }
        finally
        {
            in.close();
        }

        return buffer.toString();
    }

    // -----------------------------------------------------------------------

    private static String platformDefaultEncoding;

    static
    {
        platformDefaultEncoding =
            ( new OutputStreamWriter( System.out ) ).getEncoding();
    }

    /**
     * Returns the default character encoding for this platform.
     * 
     * @return default character encoding for this platform
     */
    public static String defaultEncoding()
    {
        return platformDefaultEncoding;
    }
}
