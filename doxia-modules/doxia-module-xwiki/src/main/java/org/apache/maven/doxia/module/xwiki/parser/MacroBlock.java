package org.apache.maven.doxia.module.xwiki.parser;

import org.apache.maven.doxia.module.confluence.parser.AbstractFatherBlock;
import org.apache.maven.doxia.sink.Sink;

import java.util.List;
import java.util.Map;

public class MacroBlock
    extends AbstractFatherBlock
{
    private String name;

    private Map parameters;

    public MacroBlock( String name, Map parameters, List childBlocks )
    {
        super( childBlocks );
        this.name = name;
        this.parameters = parameters;
    }

    public void before( Sink sink )
    {

    }

    public void after( Sink sink )
    {

    }
}
