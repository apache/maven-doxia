package org.codehaus.doxia.module.xhtml.decoration.render;

import org.codehaus.doxia.module.xhtml.decoration.model.Link;
import org.codehaus.doxia.module.xhtml.decoration.render.DecorationRenderer;
import org.codehaus.plexus.util.xml.XMLWriter;

import java.util.List;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
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
