package org.apache.maven.doxia.module.confluence.parser;

import org.apache.maven.doxia.sink.Sink;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:$
 */
public class SectionBlock
    implements Block
{
    private String title;

    private int level;

    public SectionBlock( String title, int level )
    {
        this.title = title;
        this.level = level;
    }

    public void traverse( Sink sink )
    {
        if ( level == 1 )
        {
            sink.section1();
            sink.sectionTitle1();
        }
        else if ( level == 2 )
        {
            sink.section2();
            sink.sectionTitle2();
        }
        else if ( level == 3 )
        {
            sink.section3();
            sink.sectionTitle3();
        }
        else if ( level == 4 )
        {
            sink.section4();
            sink.sectionTitle4();
        }
        else if ( level == 5 )
        {
            sink.section5();
            sink.sectionTitle5();
        }

        sink.text( title );

        if ( level == 1 )
        {
            sink.section1_();
            sink.sectionTitle1_();
        }
        else if ( level == 2 )
        {
            sink.section2_();
            sink.sectionTitle2_();
        }
        else if ( level == 3 )
        {
            sink.section3_();
            sink.sectionTitle3_();

        }
        else if ( level == 4 )
        {
            sink.section4_();
            sink.sectionTitle4_();
        }
        else if ( level == 5 )
        {
            sink.section5_();
            sink.sectionTitle5_();
        }
    }
}
