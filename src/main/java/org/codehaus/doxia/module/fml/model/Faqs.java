package org.codehaus.doxia.module.fml.model;

import java.util.ArrayList;
import java.util.List;

public class Faqs
{
    private String title;

    private List parts;

    public List getParts()
    {
        return parts;
    }

    public void setParts( List parts )
    {
        this.parts = parts;
    }

    public void addPart( Part part )
    {
        if ( parts == null )
        {
            parts = new ArrayList();
        }

        parts.add( part );
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title)
    {
        this.title = title;
    }
}
