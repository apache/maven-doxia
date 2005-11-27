/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.maven.doxia.module.confluence.parser;

import org.apache.maven.doxia.sink.Sink;


/**
 * Document objet model: we parse the document to a bunch of these.
 * <p/>
 * Implementators should implement equals() and hashCode() to ease testing
 *
 * @author Juan F. Codagnone
 * @since Nov 1, 2005
 */
public interface Block
{

    /**
     * Traverse the block
     *
     * @param sink the sink that travers
     */
    void traverse( final Sink sink );
}
