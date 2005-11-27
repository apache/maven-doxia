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

    public  void before(  Sink sink )
    {
        sink.tableRow();
    }

    public  void after(  Sink sink )
    {
        sink.tableRow_();
    }

}
