package org.apache.maven.doxia.editor.action;

import org.apache.maven.doxia.Doxia;
import org.apache.maven.doxia.editor.io.DebugSink;
import org.apache.maven.doxia.editor.io.EditorSink;
import org.apache.maven.doxia.editor.io.PipelineSink;
import org.apache.maven.doxia.editor.model.DoxiaDocument;

import java.awt.event.ActionEvent;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class OpenDocumentAction
    extends AbstractDoxiaAction
{
    public void doAction( ActionEvent event )
        throws Exception
    {
        // ----------------------------------------------------------------------
        // Load the model
        // ----------------------------------------------------------------------

        Doxia doxia = getApplication().getDoxia();

        EditorSink editorSink = new EditorSink();

        List pipeline = new ArrayList();
        pipeline.add( DebugSink.newInstance() );
        pipeline.add( editorSink );

        doxia.parse( new FileReader( "src/test/apt/test.apt" ), "apt", PipelineSink.newInstance( pipeline ) );

        DoxiaDocument document = editorSink.getDocument();

        getApplication().getDoxiaDocumentBuilder().loadDocument( document, getApplication().getEditorWindow().getDocument() );

        getApplication().getEditorWindow().setTitle( document.getTitle().getText() );
    }
}
