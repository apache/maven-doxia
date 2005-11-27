package org.apache.maven.doxia.module.confluence.parser.table;

import org.apache.maven.doxia.module.confluence.parser.AbstractFatherBlock;
import org.apache.maven.doxia.sink.Sink;

import java.util.List;


public class TableRowBlock
    extends AbstractFatherBlock
{
    public TableRowBlock( List childBlocks )
    {
        super( childBlocks );
    }

    public final void before( final Sink sink )
    {
        sink.tableRow();
    }

    public final void after( final Sink sink )
    {
        sink.tableRow_();
    }

}
