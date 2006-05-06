package org.apache.maven.doxia.editor;

import javax.swing.text.SimpleAttributeSet;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface StyleManager
{
    String ROLE = StyleManager.class.getName();

    SimpleAttributeSet getTitleStyle();

    SimpleAttributeSet getAuthorStyle();

    SimpleAttributeSet getDateStyle();

    SimpleAttributeSet getSection1Style();

    SimpleAttributeSet getSection2Style();

    SimpleAttributeSet getSection3Style();

    SimpleAttributeSet getSection4Style();

    SimpleAttributeSet getSection5Style();

    SimpleAttributeSet getTextStyle();

    SimpleAttributeSet getParagraphSeparatorStyle();
}
