package org.apache.maven.doxia.editor.io;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.apache.maven.doxia.editor.model.DoxiaDocument;
import org.apache.maven.doxia.editor.model.Element;
import org.apache.maven.doxia.editor.model.Section;
import org.apache.maven.doxia.editor.model.Text;
import org.apache.maven.doxia.editor.model.Paragraph;
import org.apache.maven.doxia.editor.StyleManager;
import org.apache.maven.doxia.editor.DoxiaEditorException;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import java.util.Iterator;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultDoxiaDocumentBuilder
    extends AbstractLogEnabled
    implements DoxiaDocumentBuilder
{
    private final static String newLine = System.getProperty( "line.separator" );

    private StyleManager styleManager;

    // ----------------------------------------------------------------------
    // DoxiaDocumentBuilder Implementation
    // ----------------------------------------------------------------------

    public void loadDocument( DoxiaDocument doxiaDocument, Document document )
        throws DoxiaEditorException
    {
        try
        {
            if ( doxiaDocument.getTitle() != null )
            {
                document.insertString( document.getLength(), doxiaDocument.getTitle().getText() + newLine,
                                       styleManager.getTitleStyle() );
            }

            if ( doxiaDocument.getAuthor() != null )
            {
                document.insertString( document.getLength(), "Author: " + doxiaDocument.getAuthor().getText() + newLine,
                                       styleManager.getAuthorStyle() );
            }

            if ( doxiaDocument.getDate() != null )
            {
                document.insertString( document.getLength(), "Date: " + doxiaDocument.getDate().getText() + newLine,
                                       styleManager.getDateStyle() );
            }

            insertChildren( document, doxiaDocument );
        }
        catch ( BadLocationException e )
        {
            throw new DoxiaEditorException( "Error while loading document", e );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void insertChildren( Document document, Element element )
        throws BadLocationException
    {
        for ( Iterator it = element.getChildren().iterator(); it.hasNext(); )
        {
            Object o = it.next();

            insertElement( document, o );
        }
    }

    private void insertElement( Document document, Object o )
        throws BadLocationException
    {
        boolean insertChildren;

        if ( o instanceof Section )
        {
            insertChildren = insertSection( document, (Section) o );
        }
        else if ( o instanceof Text )
        {
            insertChildren = insertText( document, (Text) o, true );
        }
        else if ( o instanceof Paragraph )
        {
            insertChildren = insertParagraph( document, (Paragraph) o );
        }
        else
        {
            // This would be bad.
            System.err.println( "Unknown type: " + o.getClass().getName() );
            insertChildren = false;
        }

        if ( insertChildren && o instanceof Element )
        {
            insertChildren( document, (Element) o );
        }
    }

    private boolean insertSection( Document document, Section section )
        throws BadLocationException
    {
        AttributeSet style;

        switch ( section.getDepth() )
        {
            case 1:
                style = styleManager.getSection1Style();
                break;
            case 2:
                style = styleManager.getSection2Style();
                break;
            case 3:
                style = styleManager.getSection3Style();
                break;
            case 4:
                style = styleManager.getSection4Style();
                break;
            case 5:
                style = styleManager.getSection5Style();
                break;
            default:
                throw new RuntimeException( "Unknown section depth: " + section.getDepth() );
        }
        System.out.println(
            "Inserting section, start: " + document.getLength() + ", size: " + section.getTitle().getText().length() );

        document.insertString( document.getLength(), section.getTitle().getText() + newLine, style );

        return true;
    }

    private boolean insertText( Document document, Text text, boolean insertNewline )
        throws BadLocationException
    {
        // TODO: This has to come from the style manager and has to be singletons

        MutableAttributeSet attributeSet = (MutableAttributeSet) styleManager.getTextStyle().clone();

        if ( text.isBold() )
        {
            StyleConstants.setBold( attributeSet, true );
        }

        if ( text.isItalic() )
        {
            StyleConstants.setItalic( attributeSet, true );
        }

        if ( text.isMonospaced() )
        {
            // TODO:
        }

        System.out.println( "Inserting text, start: " + document.getLength() + ", size: " + text.getText().length() );
        document.insertString( document.getLength(), text.getText(), attributeSet );

        if ( insertNewline )
        {
            document.insertString( document.getLength(), newLine, attributeSet );
        }

        return true;
    }

    private boolean insertParagraph( Document document, Paragraph paragraph )
        throws BadLocationException
    {
        for ( int i = 0; i < paragraph.getChildren().size(); i++ )
        {
            Object o = paragraph.getChildren().get( i );

            insertText( document, (Text) o, false );
        }

        System.out.println( "Inserting paragraph, start: " + document.getLength() + ", size: 2" );
        document.insertString( document.getLength(), newLine + newLine, styleManager.getParagraphSeparatorStyle() );

        return false;
    }
}
