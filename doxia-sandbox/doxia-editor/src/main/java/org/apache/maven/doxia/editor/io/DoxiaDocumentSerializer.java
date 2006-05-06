package org.apache.maven.doxia.editor.io;

import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.parser.manager.ParserNotFoundException;

import javax.swing.text.Document;
import java.io.IOException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface DoxiaDocumentSerializer
{
    String ROLE = DoxiaDocumentSerializer.class.getName();

    void serialize( Document document, String sinkId )
        throws ParserNotFoundException, ParseException, IOException;
}
