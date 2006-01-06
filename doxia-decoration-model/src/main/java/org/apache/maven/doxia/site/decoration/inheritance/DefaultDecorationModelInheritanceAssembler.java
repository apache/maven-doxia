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

import org.apache.maven.doxia.site.decoration.Banner;
import org.apache.maven.doxia.site.decoration.Body;
import org.apache.maven.doxia.site.decoration.DecorationModel;
import org.apache.maven.doxia.site.decoration.LinkItem;
import org.apache.maven.doxia.site.decoration.Logo;
import org.apache.maven.doxia.site.decoration.Menu;
import org.apache.maven.doxia.site.decoration.MenuItem;
import org.codehaus.plexus.util.PathTool;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Manage inheritance of the decoration model.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @plexus.component role="org.apache.maven.doxia.site.decoration.inheritance.DecorationModelInheritanceAssembler"
 */
public class DefaultDecorationModelInheritanceAssembler
    implements DecorationModelInheritanceAssembler
{
    public void assembleModelInheritance( DecorationModel child, DecorationModel parent, String childBaseUrl,
                                          String parentBaseUrl )
    {
        String prefix = getParentPrefix( parentBaseUrl, childBaseUrl );

        if ( !prefix.endsWith( "/" ) )
        {
            prefix += "/";
        }

        // cannot inherit from null parent.
        if ( parent != null )
        {
            if ( child.getBannerLeft() == null )
            {
                child.setBannerLeft( parent.getBannerLeft() );

                resolveBannerPaths( child.getBannerLeft(), prefix, parentBaseUrl );
            }

            if ( child.getBannerRight() == null )
            {
                child.setBannerRight( parent.getBannerRight() );

                resolveBannerPaths( child.getBannerRight(), prefix, parentBaseUrl );
            }

            if ( child.getPublishDate() == null )
            {
                child.setPublishDate( parent.getPublishDate() );
            }

            if ( child.getSkin() == null )
            {
                child.setSkin( parent.getSkin() );
            }

            child.setPoweredBy(
                mergePoweredByLists( child.getPoweredBy(), parent.getPoweredBy(), prefix, parentBaseUrl ) );

            assembleBodyInheritance( child, parent, prefix, parentBaseUrl );

            assembleCustomInheritance( child, parent );
        }
    }

    public void resolvePaths( DecorationModel decoration, String baseUrl )
    {
        String prefix = ".";

        if ( decoration.getBannerLeft() != null )
        {
            resolveBannerPaths( decoration.getBannerLeft(), prefix, baseUrl );
        }

        if ( decoration.getBannerRight() != null )
        {
            resolveBannerPaths( decoration.getBannerRight(), prefix, baseUrl );
        }

        for ( Iterator i = decoration.getPoweredBy().iterator(); i.hasNext(); )
        {
            Logo logo = (Logo) i.next();

            resolveLogoPaths( logo, prefix, baseUrl );
        }

        if ( decoration.getBody() != null )
        {
            for ( Iterator i = decoration.getBody().getLinks().iterator(); i.hasNext(); )
            {
                LinkItem linkItem = (LinkItem) i.next();

                resolveLinkItemPaths( linkItem, prefix, baseUrl );
            }

            for ( Iterator i = decoration.getBody().getBreadcrumbs().iterator(); i.hasNext(); )
            {
                LinkItem linkItem = (LinkItem) i.next();

                resolveLinkItemPaths( linkItem, prefix, baseUrl );
            }

            for ( Iterator i = decoration.getBody().getMenus().iterator(); i.hasNext(); )
            {
                Menu menu = (Menu) i.next();

                resolveMenuPaths( menu.getItems(), prefix, baseUrl );
            }
        }
    }

    private void resolveBannerPaths( Banner banner, String prefix, String baseUrl )
    {
        if ( banner != null )
        {
            banner.setHref( resolvePath( banner.getHref(), prefix, baseUrl ) );
            banner.setSrc( resolvePath( banner.getSrc(), prefix, baseUrl ) );
        }
    }

    private String resolvePath( String href, String prefix, String baseUrl )
    {
        String relativePath = getParentPrefix( href, baseUrl );

        if ( relativePath.startsWith( "/" ) )
        {
            relativePath = relativePath.substring( 1 );
        }
        return PathTool.calculateLink( relativePath, prefix );
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

    private void assembleBodyInheritance( DecorationModel child, DecorationModel parent, String prefix, String baseUrl )
    {
        Body cBody = child.getBody();
        Body pBody = parent.getBody();

        if ( cBody == null && pBody != null )
        {
            cBody = new Body();
            child.setBody( cBody );
        }

        if ( pBody != null )
        {
            if ( cBody.getHead() == null )
            {
                cBody.setHead( pBody.getHead() );
            }
            else
            {
                cBody.setHead( Xpp3Dom.mergeXpp3Dom( (Xpp3Dom) cBody.getHead(), (Xpp3Dom) pBody.getHead() ) );
            }

            cBody.setLinks( mergeLinkItemLists( cBody.getLinks(), pBody.getLinks(), prefix, baseUrl ) );
            cBody.setBreadcrumbs(
                mergeLinkItemLists( cBody.getBreadcrumbs(), pBody.getBreadcrumbs(), prefix, baseUrl ) );

            cBody.setMenus( mergeMenus( cBody.getMenus(), pBody.getMenus(), prefix, baseUrl ) );
        }
    }

    private List mergeMenus( List dominant, List recessive, String prefix, String baseUrl )
    {
        List menus = new ArrayList();

        for ( Iterator it = dominant.iterator(); it.hasNext(); )
        {
            Menu menu = (Menu) it.next();

            menus.add( menu );
        }

        int topCounter = 0;
        for ( Iterator it = recessive.iterator(); it.hasNext(); )
        {
            Menu menu = (Menu) it.next();

            if ( "top".equals( menu.getInherit() ) )
            {
                menus.add( topCounter, menu );
                topCounter++;

                resolveMenuPaths( menu.getItems(), prefix, baseUrl );
            }
            else if ( "bottom".equals( menu.getInherit() ) )
            {
                menus.add( menu );

                resolveMenuPaths( menu.getItems(), prefix, baseUrl );
            }
        }

        return menus;
    }

    private void resolveMenuPaths( List items, String prefix, String baseUrl )
    {
        for ( Iterator i = items.iterator(); i.hasNext(); )
        {
            MenuItem item = (MenuItem) i.next();
            resolveLinkItemPaths( item, prefix, baseUrl );
            resolveMenuPaths( item.getItems(), prefix, baseUrl );
        }
    }

    private void resolveLinkItemPaths( LinkItem item, String prefix, String baseUrl )
    {
        item.setHref( resolvePath( item.getHref(), prefix, baseUrl ) );
    }

    private void resolveLogoPaths( Logo logo, String prefix, String baseUrl )
    {
        logo.setImg( resolvePath( logo.getImg(), prefix, baseUrl ) );
        resolveLinkItemPaths( logo, prefix, baseUrl );
    }

    private List mergeLinkItemLists( List dominant, List recessive, String prefix, String baseUrl )
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

                resolveLinkItemPaths( item, prefix, baseUrl );
            }
        }

        return items;
    }

    private List mergePoweredByLists( List dominant, List recessive, String prefix, String baseUrl )
    {
        List logos = new ArrayList();

        for ( Iterator it = dominant.iterator(); it.hasNext(); )
        {
            Logo logo = (Logo) it.next();

            logos.add( logo );
        }

        for ( Iterator it = recessive.iterator(); it.hasNext(); )
        {
            Logo logo = (Logo) it.next();

            if ( !logos.contains( logo ) )
            {
                logos.add( logo );

                resolveLogoPaths( logo, prefix, baseUrl );
            }
        }

        return logos;
    }

    private static String getParentPrefix( String parentUrl, String childUrl )
    {
        String prefix = parentUrl;
        if ( childUrl.startsWith( parentUrl ) )
        {
            prefix = getRelativePath( childUrl, parentUrl );

            String parentPath = "";
            for ( StringTokenizer tok = new StringTokenizer( prefix, "/" ); tok.hasMoreTokens(); tok.nextToken() )
            {
                parentPath += "../";
            }
            prefix = parentPath;
        }
        else if ( parentUrl.startsWith( childUrl ) )
        {
            prefix = getRelativePath( parentUrl, childUrl );
        }
/*
        else
        {
            String[] parentSplit = splitUrl( parentUrl );
            String[] childSplit = splitUrl( childUrl );

            if ( parentSplit != null && childSplit != null )
            {
                if ( parentSplit[0].equals( childSplit[0] ) && parentSplit[1].equals( childSplit[1] ) )
                {
                    prefix = "";
                    boolean mismatched = false;
                    String parentPath = parentSplit[2].substring( 1 );
                    String childPath = childSplit[2].substring( 1 );
                    StringTokenizer tok = new StringTokenizer( childPath, "/" );
                    while ( tok.hasMoreTokens() )
                    {
                        String part = tok.nextToken();

                        if ( !mismatched && parentPath.startsWith( part ) )
                        {
                            parentPath = parentPath.substring( part.length() + 1 );
                        }
                        else
                        {
                            mismatched = true;
                            prefix += "../";
                        }
                    }
                    prefix += parentPath;
                }
            }
        }
*/

        return prefix;
    }

    private static String[] splitUrl( String url )
    {
        String[] retValue = null;

        int protocolIndex = url.indexOf( "://" );

        if ( protocolIndex >= 0 )
        {
            String protocol = url.substring( 0, protocolIndex );

            String host = url.substring( protocolIndex + 3 );

            int pathIndex = host.indexOf( '/' );

            if ( pathIndex >= 0 )
            {
                String path = host.substring( pathIndex );
                host = host.substring( 0, pathIndex );
                if ( host.length() == 0 && "file".equals( protocol ) )
                {
                    host = "localhost";
                }

                retValue = new String[3];
                retValue[0] = protocol;
                retValue[1] = host;
                retValue[2] = path;
            }
        }
        return retValue;
    }

    private static String getRelativePath( String childUrl, String parentUrl )
    {
        String relative = childUrl.substring( parentUrl.length() );
        if ( relative.startsWith( "/" ) )
        {
            relative = relative.substring( 1 );
        }
        return relative;
    }
}
