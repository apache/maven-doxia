package org.apache.maven.doxia.editor.action;

import org.apache.maven.doxia.editor.io.DoxiaDocumentSerializer;

import java.awt.event.ActionEvent;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class SaveDocumentAction
    extends AbstractDoxiaAction
{
    public void doAction( ActionEvent e )
        throws Exception
    {
        DoxiaDocumentSerializer serializer = (DoxiaDocumentSerializer) getApplication().lookup( DoxiaDocumentSerializer.ROLE );

        serializer.serialize( getApplication().getEditorWindow().getDocument(), "xdoc" );
    }
}
