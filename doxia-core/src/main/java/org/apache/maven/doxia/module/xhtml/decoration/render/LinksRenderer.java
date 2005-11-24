package org.apache.maven.doxia.module.xhtml.decoration.render;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.doxia.module.xhtml.decoration.model.Link;
import org.codehaus.plexus.util.xml.XMLWriter;

import java.util.List;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:LinksRenderer.java 348605 2005-11-24 12:02:44 +1100 (Thu, 24 Nov 2005) brett $
 */
public class LinksRenderer
    implements DecorationRenderer
{
    public void render( XMLWriter w, RenderingContext renderingContext )
    {
        List links = renderingContext.getDecorationModel().getLinks();

        for ( int i = 0; i < links.size(); i++ )
        {
            Link link = (Link) links.get( i );

            w.startElement( "a" );

            w.addAttribute( "href", link.getHref() );

            w.writeText( link.getText() );

            w.endElement();

            if ( i != links.size() - 1 )
            {
                w.writeText( " | " );
            }
        }
    }
}
