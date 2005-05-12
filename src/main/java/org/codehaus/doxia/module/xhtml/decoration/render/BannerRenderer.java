package org.codehaus.doxia.module.xhtml.decoration.render;

import org.codehaus.doxia.module.xhtml.decoration.model.Image;
import org.codehaus.doxia.module.xhtml.decoration.model.Link;
import org.codehaus.doxia.module.xhtml.decoration.model.Banner;
import org.codehaus.plexus.util.xml.XMLWriter;

/**
 * @author <a href="mailto:brett@codehaus.org">Brett Porter</a>
 * @version $Id$
 */
public class BannerRenderer
    implements DecorationRenderer
{
    private final String id;

    public BannerRenderer( String id )
    {
        this.id = id;
    }

    public void render( XMLWriter w, RenderingContext renderingContext )
    {
        Banner banner;

        if ( id.equals( "bannerLeft" ) )
        {
            banner = renderingContext.getDecorationModel().getBannerLeft();
        }
        else
        {
            banner = renderingContext.getDecorationModel().getBannerRight();
        }

        if ( banner != null )
        {
            Link link = banner.getLink();

            if ( link != null )
            {
                w.startElement( "a" );

                w.addAttribute( "href", link.getHref() );

                w.addAttribute( "id", id );
            }
            else
            {
                w.startElement( "span" );

                w.addAttribute( "id", id );
            }

            Image image = banner.getImage();
            if ( image != null )
            {
                w.startElement( "img" );

                w.addAttribute( "src", image.getSrc() );

                if ( image.getAlt() != null )
                {
                    w.addAttribute( "alt", image.getAlt() );
                }

                if ( image.getTitle() != null )
                {
                    w.addAttribute( "title", image.getTitle() );
                }

                w.endElement();
            }
            else
            {
                w.writeText( banner.getName() );
            }

            w.endElement();
        }
    }
}
