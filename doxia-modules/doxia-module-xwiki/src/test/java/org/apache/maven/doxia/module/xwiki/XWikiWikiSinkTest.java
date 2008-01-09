package org.apache.maven.doxia.module.xwiki;

import org.apache.maven.doxia.sink.AbstractSinkTest;
import org.apache.maven.doxia.sink.Sink;

import java.io.Writer;

public class XWikiWikiSinkTest
    extends AbstractSinkTest
{
    protected Sink createSink( Writer writer )
    {
        return new XWikiWikiSink( writer );
    }

    protected String getTitleBlock( String title )
    {
        return "";
    }

    protected String getAuthorBlock( String author )
    {
        return "";
    }

    protected String getDateBlock( String date )
    {
        return "";
    }

    protected String getHeadBlock()
    {
        return "";
    }

    protected String getBodyBlock()
    {
        return "";
    }

    protected String getSectionTitleBlock( String title )
    {
        return "";
    }

    protected String getSection1Block( String title )
    {
        return "1 Title1";
    }

    protected String getSection2Block( String title )
    {
        return "1.1 Title2";
    }

    protected String getSection3Block( String title )
    {
        return "1.1.1 Title3";
    }

    protected String getSection4Block( String title )
    {
        return "1.1.1.1 Title4";
    }

    protected String getSection5Block( String title )
    {
        return "1.1.1.1.1 Title5";
    }

    protected String getListBlock( String item )
    {
        return "* list item";
    }

    protected String getNumberedListBlock( String item )
    {
        return "i numbered list item";
    }

    protected String getDefinitionListBlock( String definum, String definition )
    {
        return "<dl><dt>definum</dt><dd>definition</dd></dl>";
    }

    protected String getFigureBlock( String source, String caption )
    {
        return "{image:figure|alt=Figure caption}";
    }

    protected String getTableBlock( String cell, String caption )
    {
        return "{table}cell|{table}";
    }

    protected String getParagraphBlock( String text )
    {
        return "Text";
    }

    protected String getVerbatimBlock( String text )
    {
        return "{code:none}Text{code}";
    }

    protected String getHorizontalRuleBlock()
    {
        return "----";
    }

    protected String getPageBreakBlock()
    {
        return "";
    }

    protected String getAnchorBlock( String anchor )
    {
        return "<a name=\"Anchor\"/>";
    }

    protected String getLinkBlock( String link, String text )
    {
        return "[Text>Link]";
    }

    protected String getItalicBlock( String text )
    {
        return "~~Italic~~";
    }

    protected String getBoldBlock( String text )
    {
        return "*Bold*";
    }

    protected String getMonospacedBlock( String text )
    {
        return "<tt>Monospaced</tt>";
    }

    protected String getLineBreakBlock()
    {
        return "\\\\";
    }

    protected String getNonBreakingSpaceBlock()
    {
        return "&nbsp;";
    }

    protected String getTextBlock( String text )
    {
        return "~, =, -, +, *, [, ], <, >, {, }, \\";
    }

    protected String getRawTextBlock( String text )
    {
        return "~, =, -, +, *, [, ], <, >, {, }, \\";
    }

    protected String outputExtension()
    {
        return "xwiki";
    }
}
