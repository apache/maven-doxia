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

import org.apache.maven.doxia.module.xhtml.decoration.model.Banner;
import org.apache.maven.doxia.module.xhtml.decoration.model.Image;
import org.apache.maven.doxia.module.xhtml.decoration.model.Link;
import org.codehaus.plexus.util.xml.XMLWriter;

/**
 * @author <a href="mailto:brett@codehaus.org">Brett Porter</a>
 * @version $Id:BannerRenderer.java 348605 2005-11-24 12:02:44 +1100 (Thu, 24 Nov 2005) brett $
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
                else
                {
                    w.addAttribute( "alt", "" );
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
