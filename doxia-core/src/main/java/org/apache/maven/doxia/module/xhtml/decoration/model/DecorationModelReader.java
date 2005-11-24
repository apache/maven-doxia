package org.apache.maven.doxia.module.xhtml.decoration.model;

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

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

public class DecorationModelReader
{
    public DecorationModel createNavigation( String siteFile )
        throws Exception
    {
        File navigationFile = new File( siteFile );

        if ( navigationFile.exists() )
        {
            return createNavigation( new FileReader( navigationFile ) );
        }

        return getDecorationModel();
    }

    public DecorationModel createNavigation( Reader reader )
        throws Exception
    {
        DecorationModel decorationModel = getDecorationModel();

        Xpp3Dom siteElement = Xpp3DomBuilder.build( reader );

        Xpp3Dom flavourChild = siteElement.getChild( "flavour" );

        if ( flavourChild != null )
        {
            decorationModel.setFlavour( flavourChild.getChild( "name" ).getValue() );
        }

        Xpp3Dom be = siteElement.getChild( "bannerLeft" );

        if ( be != null )
        {
            decorationModel.setBannerLeft( processBanner( be ) );
        }

        be = siteElement.getChild( "bannerRight" );

        if ( be != null )
        {
            decorationModel.setBannerRight( processBanner( be ) );
        }

        Xpp3Dom body = siteElement.getChild( "body" );

        int children = body.getChildCount();

        for ( int i = 0; i < children; i++ )
        {
            Xpp3Dom element = body.getChild( i );

            if ( element.getName().equals( "menu" ) )
            {
                Menu menu = new Menu( element.getAttribute( "name" ) );

                int items = element.getChildCount();

                for ( int j = 0; j < items; j++ )
                {
                    Xpp3Dom itemElement = element.getChild( j );

                    menu.addItem( processItem( itemElement, decorationModel ) );
                }

                decorationModel.addMenu( menu );
            }
            else if ( element.getName().equals( "links" ) )
            {
                int count = element.getChildCount();

                for ( int j = 0; j < count; j++ )
                {
                    Xpp3Dom item = element.getChild( j );

                    Link link = new Link( item.getAttribute( "name" ), item.getAttribute( "href" ) );

                    decorationModel.addLink( link );
                }
            }
            else if ( element.getName().equals( "search" ) )
            {
                decorationModel.setSearch( true );
            }
        }

        return decorationModel;
    }

    private static Banner processBanner( Xpp3Dom be )
    {
        Banner b = new Banner();

        Image i = new Image();

        if ( be.getChild( "href" ) != null )
        {
            Link bl = new Link();

            bl.setHref( be.getChild( "href" ).getValue() );

            b.setLink( bl );
        }

        if ( be.getChild( "name" ) != null )
        {
            String value = be.getChild( "name" ).getValue();

            i.setTitle( value );

            b.setName( value );
        }

        if ( be.getChild( "src" ) != null )
        {
            i.setSrc( be.getChild( "src" ).getValue() );

            b.setImage( i );
        }

        return b;
    }

    private DecorationModel getDecorationModel()
    {
        return new DecorationModel();
    }

    private Item processItem( Xpp3Dom itemElement, DecorationModel navigation )
    {
        String name = itemElement.getAttribute( "name" );

        String href = itemElement.getAttribute( "href" );

        Item item = new Item( name, href );

        navigation.addItemGroup( href, name );

        if ( itemElement.getAttribute( "collapse" ) != null )
        {
            item.setFoldable( true );
        }

        int items = itemElement.getChildCount();

        for ( int i = 0; i < items; i++ )
        {
            Xpp3Dom subItemElement = itemElement.getChild( i );

            Item subitem = processItem( subItemElement, navigation );

            subitem.setGroup( item.getGroup() );

            navigation.addItemGroup( subitem.getHref(), subitem.getGroup() );

            item.addItem( subitem );
        }

        return item;
    }
}
