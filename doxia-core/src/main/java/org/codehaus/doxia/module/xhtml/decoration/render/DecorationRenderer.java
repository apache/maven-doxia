/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.doxia.module.xhtml.decoration.render;

import org.codehaus.plexus.util.xml.XMLWriter;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface DecorationRenderer
{
    void render( XMLWriter writer, RenderingContext renderingContext );
}
