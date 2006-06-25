package org.apache.maven.doxia.book.context;

import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class TOCEntry
{
    private String sectionId;

    private String title;

    private List childEntries;

    public String getSectionId()
    {
        return sectionId;
    }

    public void setSectionId( String sectionId )
    {
        this.sectionId = sectionId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public List getChildEntries()
    {
        if ( childEntries == null )
        {
            childEntries = new ArrayList();
        }

        return childEntries;
    }

    public void setChildEntries( List childEntries )
    {
        this.childEntries = childEntries;
    }
}
