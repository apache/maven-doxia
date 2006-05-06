package org.apache.maven.doxia.editor.model;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class Text
    extends LeafElement
{
    private String text;

    private boolean bold;

    private boolean italic;

    private boolean monospaced;

    public Text( String text, boolean bold, boolean italic, boolean monospaced )
    {
        this.text = text;
        this.bold = bold;
        this.italic = italic;
        this.monospaced = monospaced;
    }

    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        this.text = text;
    }

    public boolean isBold()
    {
        return bold;
    }

    public void setBold( boolean bold )
    {
        this.bold = bold;
    }

    public boolean isItalic()
    {
        return italic;
    }

    public void setItalic( boolean italic )
    {
        this.italic = italic;
    }

    public boolean isMonospaced()
    {
        return monospaced;
    }

    public void setMonospaced( boolean monospaced )
    {
        this.monospaced = monospaced;
    }
}
