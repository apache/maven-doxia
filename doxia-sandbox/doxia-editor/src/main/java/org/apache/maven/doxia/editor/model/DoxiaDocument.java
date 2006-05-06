package org.apache.maven.doxia.editor.model;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DoxiaDocument
    extends Element
{
    private Text title;

    private Text author;

    private Text date;

    public Text getTitle()
    {
        return title;
    }

    public void setTitle( Text title )
    {
        this.title = title;
    }

    public Text getAuthor()
    {
        return author;
    }

    public void setAuthor( Text author )
    {
        this.author = author;
    }

    public Text getDate()
    {
        return date;
    }

    public void setDate( Text date )
    {
        this.date = date;
    }
}
