package org.apache.maven.doxia.editor.io;

import org.apache.maven.doxia.editor.DoxiaEditorException;
import org.apache.maven.doxia.editor.model.DoxiaDocument;

import javax.swing.text.Document;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface DoxiaDocumentBuilder
{
    String ROLE = DoxiaDocumentBuilder.class.getName();

    void loadDocument( DoxiaDocument doxiaDocument, Document document )
        throws DoxiaEditorException;
}
