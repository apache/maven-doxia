package org.apache.maven.doxia.editor.io;

import org.apache.maven.doxia.Doxia;
import org.apache.maven.doxia.editor.Application;
import org.apache.maven.doxia.module.xdoc.XdocSink;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.manager.ParserNotFoundException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import javax.swing.text.Document;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultDoxiaDocumentSerializer
    extends AbstractLogEnabled
    implements DoxiaDocumentSerializer
{
    private Application application;

    private Doxia doxia;

    // ----------------------------------------------------------------------
    // DoxiaDocumentSerializer Implementation
    // ----------------------------------------------------------------------

    public void serialize( Document document, String sinkId )
        throws ParserNotFoundException, ParseException, IOException
    {
        XdocSink sink = new XdocSink( new FileWriter( "/tmp/document.xdoc" ) );

        DocumentParser.document.set( application.getEditorWindow().getDocument() );

        List list = new ArrayList();
        list.add( DebugSink.newInstance() );
//        list.add( new WellformednessCheckingSink() );
        list.add( sink );

        try
        {
            doxia.parse( null, "doxia-document", PipelineSink.newInstance( list ) );
        }
        finally
        {
            DocumentParser.document.set( null );
        }
    }
}
