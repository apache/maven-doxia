package org.apache.maven.doxia.editor.io;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.editor.model.DoxiaDocument;
import org.apache.maven.doxia.editor.model.Section;
import org.apache.maven.doxia.editor.model.Paragraph;
import org.apache.maven.doxia.editor.model.Text;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class EditorSink
    implements Sink
{
    private DoxiaDocument document;

    private Section section1;

    private Section section2;

    private Section section3;

    private Section section4;

    private Section section5;

    private Section currentSection;

    private Paragraph paragraph;

    private Text text;

    private boolean italic;

    private boolean bold;

    private boolean monospaced;

    // ----------------------------------------------------------------------
    // Sink Implementation
    // ----------------------------------------------------------------------

    public void head()
    {
    }

    public void head_()
    {
    }

    public void body()
    {
    }

    public void body_()
    {
    }

    public void section1()
    {
        currentSection = section1 = new Section();
    }

    public void section1_()
    {
        section1.setDepth( 1 );
        getDocument().getChildren().add( section1 );
    }

    public void section2()
    {
        currentSection = section2 = new Section();
    }

    public void section2_()
    {
        section2.setDepth( 2 );
        section1.getChildren().add( section2 );
    }

    public void section3()
    {
        currentSection = section3 = new Section();
    }

    public void section3_()
    {
        section3.setDepth( 3 );
        section2.getChildren().add( section3 );
    }

    public void section4()
    {
        currentSection = section4 = new Section();
    }

    public void section4_()
    {
        section4.setDepth( 4 );
        section3.getChildren().add( section4 );
    }

    public void section5()
    {
        currentSection = section5 = new Section();
    }

    public void section5_()
    {
        section5.setDepth( 5 );
        section4.getChildren().add( section5 );
    }

    public void list()
    {
    }

    public void list_()
    {
    }

    public void listItem()
    {
    }

    public void listItem_()
    {
    }

    public void numberedList( int numbering )
    {
    }

    public void numberedList_()
    {
    }

    public void numberedListItem()
    {
    }

    public void numberedListItem_()
    {
    }

    public void definitionList()
    {
    }

    public void definitionList_()
    {
    }

    public void definitionListItem()
    {
    }

    public void definitionListItem_()
    {
    }

    public void definition()
    {
    }

    public void definition_()
    {
    }

    public void figure()
    {
    }

    public void figure_()
    {
    }

    public void table()
    {
    }

    public void table_()
    {
    }

    public void tableRows( int[] justification, boolean grid )
    {
    }

    public void tableRows_()
    {
    }

    public void tableRow()
    {
    }

    public void tableRow_()
    {
    }

    public void title()
    {
    }

    public void title_()
    {
        getDocument().setTitle( text );
    }

    public void author()
    {
    }

    public void author_()
    {
        getDocument().setAuthor( text );
    }

    public void date()
    {
    }

    public void date_()
    {
        getDocument().setDate( text );
    }

    public void sectionTitle()
    {
    }

    public void sectionTitle_()
    {
    }

    public void sectionTitle1()
    {
    }

    public void sectionTitle1_()
    {
        section1.setTitle( text );
    }

    public void sectionTitle2()
    {
    }

    public void sectionTitle2_()
    {
        section2.setTitle( text );
    }

    public void sectionTitle3()
    {
    }

    public void sectionTitle3_()
    {
        section3.setTitle( text );
    }

    public void sectionTitle4()
    {
    }

    public void sectionTitle4_()
    {
        section4.setTitle( text );
    }

    public void sectionTitle5()
    {
    }

    public void sectionTitle5_()
    {
        section5.setTitle( text );
    }

    public void paragraph()
    {
        paragraph = new Paragraph();
    }

    public void paragraph_()
    {
        currentSection.getChildren().add( paragraph );
        paragraph = null;
    }

    public void verbatim( boolean boxed )
    {
    }

    public void verbatim_()
    {
    }

    public void definedTerm()
    {
    }

    public void definedTerm_()
    {
    }

    public void figureCaption()
    {
    }

    public void figureCaption_()
    {
    }

    public void tableCell()
    {
    }

    public void tableCell( String width )
    {
    }

    public void tableCell_()
    {
    }

    public void tableHeaderCell()
    {
    }

    public void tableHeaderCell( String width )
    {
    }

    public void tableHeaderCell_()
    {
    }

    public void tableCaption()
    {
    }

    public void tableCaption_()
    {
    }

    public void figureGraphics( String name )
    {
    }

    public void horizontalRule()
    {
    }

    public void pageBreak()
    {
    }

    public void anchor( String name )
    {
    }

    public void anchor_()
    {
    }

    public void link( String name )
    {
    }

    public void link_()
    {
    }

    public void italic()
    {
        italic = true;
    }

    public void italic_()
    {
        italic = false;
    }

    public void bold()
    {
        bold = true;
    }

    public void bold_()
    {
        bold = false;
    }

    public void monospaced()
    {
        monospaced = true;
    }

    public void monospaced_()
    {
        monospaced = false;
    }

    public void lineBreak()
    {
    }

    public void nonBreakingSpace()
    {
    }

    public void text( String text )
    {
        this.text = new Text( text, bold, italic, monospaced );

        if ( paragraph != null )
        {
            paragraph.getChildren().add( this.text );
        }
    }

    public void rawText( String text )
    {
    }

    public void flush()
    {
    }

    public void close()
    {
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public DoxiaDocument getDocument()
    {
        if ( document == null )
        {
            document = new DoxiaDocument();
        }

        return document;
    }
}
