package org.apache.maven.doxia.site.decoration.inheritance;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import org.apache.maven.doxia.site.decoration.Body;
import org.apache.maven.doxia.site.decoration.DecorationModel;
import org.apache.maven.doxia.site.decoration.LinkItem;
import org.apache.maven.doxia.site.decoration.Menu;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manage inheritance of the decoration model.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @plexus.component role="org.apache.maven.doxia.site.decoration.inheritance.DecorationModelInheritanceAssembler"
 */
public class DefaultDecorationModelInheritanceAssembler
    implements DecorationModelInheritanceAssembler
{
    public void assembleModelInheritance( DecorationModel child, DecorationModel parent )
    {
        // TODO: align hrefs to the base URL of the project

        // cannot inherit from null parent.
        if ( parent != null )
        {
            if ( child.getBannerLeft() == null )
            {
                child.setBannerLeft( parent.getBannerLeft() );
            }

            if ( child.getBannerRight() == null )
            {
                child.setBannerRight( parent.getBannerRight() );
            }

            if ( child.getPublishDate() == null )
            {
                child.setPublishDate( parent.getPublishDate() );
            }

            child.setPoweredBy( mergeLinkItemLists( child.getPoweredBy(), parent.getPoweredBy() ) );

            assembleBodyInheritance( child, parent );

            assembleCustomInheritance( child, parent );
        }
    }

    private void assembleCustomInheritance( DecorationModel child, DecorationModel parent )
    {
        if ( child.getCustom() == null )
        {
            child.setCustom( parent.getCustom() );
        }
        else
        {
            child.setCustom( Xpp3Dom.mergeXpp3Dom( (Xpp3Dom) child.getCustom(), (Xpp3Dom) parent.getCustom() ) );
        }
    }

    private void assembleBodyInheritance( DecorationModel child, DecorationModel parent )
    {
        Body cBody = child.getBody();
        Body pBody = parent.getBody();

        if ( cBody == null )
        {
            child.setBody( pBody );
        }
        else if ( pBody != null )
        {
            if ( cBody.getHead() == null )
            {
                cBody.setHead( pBody.getHead() );
            }
            else
            {
                cBody.setHead( Xpp3Dom.mergeXpp3Dom( (Xpp3Dom) cBody.getHead(), (Xpp3Dom) pBody.getHead() ) );
            }

            cBody.setLinks( mergeLinkItemLists( cBody.getLinks(), pBody.getLinks() ) );
            cBody.setBreadcrumbs( mergeLinkItemLists( cBody.getBreadcrumbs(), pBody.getBreadcrumbs() ) );

            cBody.setMenus( mergeMenus( cBody.getMenus(), pBody.getMenus() ) );
        }
    }

    private List mergeMenus( List dominant, List recessive )
    {
        List menus = new ArrayList();

        for ( Iterator it = dominant.iterator(); it.hasNext(); )
        {
            Menu menu = (Menu) it.next();

            menus.add( menu );
        }

        for ( Iterator it = recessive.iterator(); it.hasNext(); )
        {
            Menu menu = (Menu) it.next();

            if ( "top".equals( menu.getInherit() ) )
            {
                menus.add( 0, menu );
            }
            else if ( "bottom".equals( menu.getInherit() ) )
            {
                menus.add( menu );
            }
        }

        return menus;
    }

    private List mergeLinkItemLists( List dominant, List recessive )
    {
        List items = new ArrayList();

        for ( Iterator it = dominant.iterator(); it.hasNext(); )
        {
            LinkItem item = (LinkItem) it.next();

            items.add( item );
        }

        for ( Iterator it = recessive.iterator(); it.hasNext(); )
        {
            LinkItem item = (LinkItem) it.next();

            if ( !items.contains( item ) )
            {
                items.add( item );
            }
        }

        return items;
    }
}
