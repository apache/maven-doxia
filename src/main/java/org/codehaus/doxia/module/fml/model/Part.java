package org.codehaus.doxia.module.fml.model;

import java.util.ArrayList;
import java.util.List;

public class Part
{
    private String id;

    private String title;

    private List faqs;

    public void addFaq( Faq faq )
    {
        if ( faqs == null )
        {
            faqs = new ArrayList();
        }

        faqs.add( faq );
    }

    public List getFaqs()
    {
        return faqs;
    }

    public void setFaqs( List faqs )
    {
        this.faqs = faqs;
    }

    public String getId()
    {
        return this.id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }
}
