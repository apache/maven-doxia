package org.codehaus.doxia.module.fml.model;

public class Faq
{
    private String id;

    private String question;

    private String answer;

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getAnswer()
    {
        return answer;
    }

    public void setAnswer( String answer )
    {
        this.answer = answer;
    }

    public String getQuestion()
    {
        return question;
    }

    public void setQuestion( String question )
    {
        this.question = question;
    }
}
