/*
 * CopyrightPlugin (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.doxia.plugin.maven;

import org.codehaus.doxia.sink.Sink;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: AbstractRenderer.java,v 1.3 2004/09/15 15:07:56 jvanzyl Exp $
 */
public abstract class AbstractRenderer
{
    protected Sink sink;

    public AbstractRenderer( Sink sink )
    {
        this.sink = sink;
    }

    public void render()
    {
        sink.head();

        sink.title();

        sink.text( getTitle() );

        sink.title_();

        sink.head_();

        sink.body();

        renderBody();

        sink.body_();
    }

    protected void startTable()
    {
        sink.table();
    }

    protected void endTable()
    {
        sink.table_();
    }

    protected void section( String name )
    {
        sink.section1();

        sink.sectionTitle();

        sink.text( name );

        sink.sectionTitle_();

        sink.section1_();
    }

    protected void tableHeaderCell( String text )
    {
        sink.tableHeaderCell();

        sink.text( text );

        sink.tableHeaderCell_();
    }

    protected void tableCell( String text )
    {
        sink.tableCell();

        sink.text( text );

        sink.tableCell_();
    }

    protected void tableRow( String[] content )
    {
        sink.tableRow();

        for ( int i = 0; i < content.length; i++ )
        {
            tableCell( content[i] );
        }

        sink.tableRow_();
    }

    protected void tableHeader( String[] content )
    {
        sink.tableRow();

        for ( int i = 0; i < content.length; i++ )
        {
            tableHeaderCell( content[i] );
        }

        sink.tableRow_();
    }

    protected abstract String getTitle();

    protected abstract void renderBody();
}
