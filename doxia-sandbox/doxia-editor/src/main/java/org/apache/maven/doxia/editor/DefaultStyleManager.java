package org.apache.maven.doxia.editor;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.apache.maven.doxia.editor.model.DoxiaAttribute;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultStyleManager
    extends AbstractLogEnabled
    implements StyleManager, Initializable
{
    private SimpleAttributeSet titleStyle = new SimpleAttributeSet();

    private SimpleAttributeSet authorStyle = new SimpleAttributeSet();

    private SimpleAttributeSet dateStyle = new SimpleAttributeSet();

    private SimpleAttributeSet section1Style = new SimpleAttributeSet();

    private SimpleAttributeSet section2Style = new SimpleAttributeSet();

    private SimpleAttributeSet section3Style = new SimpleAttributeSet();

    private SimpleAttributeSet section4Style = new SimpleAttributeSet();

    private SimpleAttributeSet section5Style = new SimpleAttributeSet();

    private SimpleAttributeSet textStyle = new SimpleAttributeSet();

    private SimpleAttributeSet paragraphSeparatorStyle = new SimpleAttributeSet();

    // ----------------------------------------------------------------------
    // StyleManager Implementation
    // ----------------------------------------------------------------------

    public SimpleAttributeSet getTitleStyle()
    {
        return titleStyle;
    }

    public SimpleAttributeSet getAuthorStyle()
    {
        return authorStyle;
    }

    public SimpleAttributeSet getDateStyle()
    {
        return dateStyle;
    }

    public SimpleAttributeSet getSection1Style()
    {
        return section1Style;
    }

    public SimpleAttributeSet getSection2Style()
    {
        return section2Style;
    }

    public SimpleAttributeSet getSection3Style()
    {
        return section3Style;
    }

    public SimpleAttributeSet getSection4Style()
    {
        return section4Style;
    }

    public SimpleAttributeSet getSection5Style()
    {
        return section5Style;
    }

    public SimpleAttributeSet getTextStyle()
    {
        return textStyle;
    }

    public SimpleAttributeSet getParagraphSeparatorStyle()
    {
        return paragraphSeparatorStyle;
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        StyleConstants.setBold( titleStyle, true );
        StyleConstants.setFontSize( titleStyle, 20 );
        titleStyle.addAttribute( DoxiaAttribute.TYPE, DoxiaAttribute.TITLE );

        StyleConstants.setFontSize( authorStyle, 20 );
        authorStyle.addAttribute( DoxiaAttribute.TYPE, DoxiaAttribute.AUTHOR );

        StyleConstants.setFontSize( dateStyle, 20 );
        dateStyle.addAttribute( DoxiaAttribute.TYPE, DoxiaAttribute.DATE );

        StyleConstants.setFontSize( section1Style, 18 );
        section1Style.addAttribute( DoxiaAttribute.TYPE, DoxiaAttribute.SECTION_1 );

        StyleConstants.setFontSize( section2Style, 16 );
        StyleConstants.setItalic( section2Style, true );
        section2Style.addAttribute( DoxiaAttribute.TYPE, DoxiaAttribute.SECTION_2 );

        StyleConstants.setFontSize( section3Style, 14 );
        section3Style.addAttribute( DoxiaAttribute.TYPE, DoxiaAttribute.SECTION_3 );

        StyleConstants.setFontSize( section4Style, 12 );
        StyleConstants.setBold( section4Style, true );
        section4Style.addAttribute( DoxiaAttribute.TYPE, DoxiaAttribute.SECTION_4 );

        StyleConstants.setFontSize( section5Style, 12 );
        StyleConstants.setItalic( section5Style, true );
        section5Style.addAttribute( DoxiaAttribute.TYPE, DoxiaAttribute.SECTION_5 );

        textStyle.addAttribute( DoxiaAttribute.TYPE, DoxiaAttribute.TEXT );

        paragraphSeparatorStyle.addAttribute( DoxiaAttribute.TYPE, DoxiaAttribute.PARAGRAPH_SEPARATOR );
    }
}
