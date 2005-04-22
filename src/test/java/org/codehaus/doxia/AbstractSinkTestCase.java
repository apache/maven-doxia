/*
 * CopyrightPlugin (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.doxia;

import org.codehaus.doxia.module.apt.AptParser;
import org.codehaus.doxia.sink.Sink;
import org.codehaus.plexus.PlexusTestCase;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: AbstractSinkTestCase.java,v 1.4 2004/09/22 00:01:43 jvanzyl Exp $
 */
public abstract class AbstractSinkTestCase
    extends PlexusTestCase
{
    protected Writer testWriter;

    // ----------------------------------------------------------------------
    // Test case
    // ----------------------------------------------------------------------

    public void testApt()
        throws Exception
    {
        AptParser parser = new AptParser();

        parser.parse( getTestReader(), createSink() );
    }

    // ----------------------------------------------------------------------
    // Abstract methods the individual SinkTests must provide
    // ----------------------------------------------------------------------

    protected abstract String outputExtension();

    protected abstract Sink createSink()
        throws Exception;

    // ----------------------------------------------------------------------
    // Methods for creating the test reader and writer
    // ----------------------------------------------------------------------

    protected Writer getTestWriter()
        throws Exception
    {
        if ( testWriter == null )
        {
            File outputDirectory = new File( basedir, "target/output" );

            if ( !outputDirectory.exists() )
            {
                outputDirectory.mkdirs();
            }

            testWriter = new FileWriter( new File( outputDirectory, "test." + outputExtension() ) );
        }

        return testWriter;
    }

    protected Reader getTestReader()
        throws Exception
    {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( "test/test.apt" );

        InputStreamReader reader = new InputStreamReader( is );

        return reader;
    }

    // ----------------------------------------------------------------------
    // Utility methods
    // ----------------------------------------------------------------------

    public File getBasedirFile()
    {
        return new File( getBasedir() );
    }
}
