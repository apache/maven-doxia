/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.maven.doxia.module.confluence.parser;

import org.apache.maven.doxia.sink.Sink;

import java.util.List;

/**
 * @author Juan F. Codagnone
 * @since Nov 1, 2005
 */
public class ParagraphBlock
    extends AbstractFatherBlock
{
    public ParagraphBlock( List blocks )
        throws IllegalArgumentException
    {
        super( blocks );
    }

    public  void before(  Sink sink )
    {
        sink.paragraph();
    }

    public  void after(  Sink sink )
    {
        sink.paragraph_();
    }
}
