package org.apache.maven.doxia.editor;

import org.apache.maven.doxia.editor.model.DoxiaAttribute;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class EditorDocumentListener
    implements DocumentListener
{
    // ----------------------------------------------------------------------
    // DocumentListener Implementation
    // ----------------------------------------------------------------------

    public void changedUpdate( DocumentEvent e )
    {
//        System.out.println( "EditorDocumentListener.changedUpdate" );
//
//        System.out.println( "e.getOffset() = " + e.getOffset() );
//        System.out.println( "e.getLength() = " + e.getLength() );
//        System.out.println( "e.getType() = " + e.getType() );
//
//        Element element = ( (StyledDocument) e.getDocument() ).getCharacterElement( e.getOffset() );
//
//        Object type = element.getAttributes().getAttribute( DoxiaAttribute.TYPE );
//
//        System.out.println( "type = " + type );
    }

    public void insertUpdate( DocumentEvent e )
    {
//        System.out.println( "EditorDocumentListener.insertUpdate" );
//
//        System.out.println( "e.getOffset() = " + e.getOffset() );
//        System.out.println( "e.getLength() = " + e.getLength() );
//        System.out.println( "e.getType() = " + e.getType() );
    }

    public void removeUpdate( DocumentEvent e )
    {
//        System.out.println( "EditorDocumentListener.removeUpdate" );
//
//        System.out.println( "e.getOffset() = " + e.getOffset() );
//        System.out.println( "e.getLength() = " + e.getLength() );
//        System.out.println( "e.getType() = " + e.getType() );
    }
}
