package org.apache.maven.doxia.editor.model;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class Section
    extends Element
{
    private Text title;

    private int depth;

    public Section()
    {
    }

    public Section( Text title, int depth )
    {
        this.title = title;
        this.depth = depth;
    }

    public Text getTitle()
    {
        return title;
    }

    public void setTitle( Text title )
    {
        this.title = title;
    }

    public int getDepth()
    {
        return depth;
    }

    public void setDepth( int depth )
    {
        this.depth = depth;
    }
}
