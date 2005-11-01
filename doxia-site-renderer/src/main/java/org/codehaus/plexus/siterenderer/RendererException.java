/**
 * 
 */
package org.codehaus.plexus.siterenderer;

/**
 * @author Emmanuel Venisse
 *
 */
public class RendererException
    extends Exception
{
    public RendererException( String message )
    {
        super( message );
    }

    public RendererException( String message, Throwable t )
    {
        super( message, t );
    }
}
