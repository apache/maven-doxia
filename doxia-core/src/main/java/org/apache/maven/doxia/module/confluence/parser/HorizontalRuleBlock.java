/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.maven.doxia.module.confluence.parser;

import org.apache.maven.doxia.sink.Sink;

public class HorizontalRuleBlock 
    implements Block
{
    public final void traverse( final Sink sink )
    {
        sink.horizontalRule();
    }
}
