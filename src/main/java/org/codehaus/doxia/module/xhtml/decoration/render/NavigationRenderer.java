package org.codehaus.doxia.module.xhtml.decoration.render;

import org.codehaus.doxia.module.xhtml.decoration.model.Item;
import org.codehaus.doxia.module.xhtml.decoration.model.Menu;
import org.codehaus.doxia.module.xhtml.decoration.render.DecorationRenderer;
import org.codehaus.plexus.util.PathTool;
import org.codehaus.plexus.util.xml.XMLWriter;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: NavigationRenderer.java,v 1.2 2004/11/02 03:40:48 jvanzyl Exp $
 */
public class NavigationRenderer
    implements DecorationRenderer
{
    public void render( XMLWriter writer, RenderingContext renderingContext )
    {
        int menus = renderingContext.getDecorationModel().getMenus().size();

        for ( int i = 0; i < menus; i++ )
        {
            Menu menu = (Menu) renderingContext.getDecorationModel().getMenus().get( i );

            writer.startElement( "h5" );

            writer.writeText( menu.getName() );

            writer.endElement();

            int items = menu.getItems().size();

            writer.startElement( "ul" );

            for ( int j = 0; j < items; j++ )
            {
                Item item = (Item) menu.getItems().get( j );

                writeMenuItem( item, writer, renderingContext );
            }

            writer.endElement();
        }
    }

    private void writeMenuItem( Item item, XMLWriter writer, RenderingContext renderingContext )
    {
        String href = item.getHref();

        String collapse;

        // boolean expandedNavigation = href.equals( renderingContext.outputName
        // );

        String currentGroup = (String) renderingContext.getDecorationModel().getItemGroups().get(
            renderingContext.getOutputName() );

        if ( item.isFoldable() )
        {
            // If we are outputting the page to which this item is referring
            // then we want to expand the list of items.

            if ( item.getGroup().equals( currentGroup ) )
            {
                collapse = "expanded";
            }
            else
            {
                collapse = "collapsed";
            }
        }
        else
        {
            collapse = "none";
        }

        if ( renderingContext.getOutputName().equals( href ) )
        {
            writer.startElement( "li" );

            writer.addAttribute( "class", collapse );

            writer.startElement( "strong" );

            writer.startElement( "a" );

            writer.addAttribute( "href", PathTool.calculateLink( href, renderingContext.getRelativePath() ) );

            writer.writeText( item.getName() );

            writer.endElement();

            writer.endElement();

            writer.endElement();
        }
        else
        {
            writer.startElement( "li" );

            writer.addAttribute( "class", collapse );

            writer.startElement( "a" );

            writer.addAttribute( "href", PathTool.calculateLink( href, renderingContext.getRelativePath() ) );

            writer.writeText( item.getName() );

            writer.endElement();

            writer.endElement();
        }

        int count = item.getItems().size();

        for ( int i = 0; i < count; i++ )
        {
            Item subitem = (Item) item.getItems().get( i );

            if ( subitem.getGroup().equals( currentGroup ) )
            {
                writer.startElement( "ul" );

                writeMenuItem( subitem, writer, renderingContext );

                writer.endElement();
            }
        }
    }
}
