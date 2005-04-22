package org.codehaus.doxia.module.xhtml.decoration.render;

import org.codehaus.doxia.module.xhtml.decoration.model.DecorationModel;
import org.codehaus.doxia.module.xhtml.decoration.model.PathTool;

import java.io.File;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: RenderingContext.java,v 1.2 2004/09/09 19:54:20 jvanzyl Exp $
 */
public class RenderingContext
{
    private String basedir;

    private String relativePath;

    private String outputName;

    private DecorationModel decorationModel;

    public RenderingContext( String basedir, String document, DecorationModel decorationModel )
    {
        this.basedir = basedir;

        // For site comparisons we'll prepend a "/"
        this.outputName = "/" + document.substring( 0, document.lastIndexOf( "." ) + 1 ) + "html";

        relativePath = PathTool.getRelativePath( basedir, new File( basedir, document ).getPath() );

        this.decorationModel = decorationModel;
    }

    public String getBasedir()
    {
        return basedir;
    }

    public String getRelativePath()
    {
        return relativePath;
    }

    public String getOutputName()
    {
        return outputName;
    }

    public DecorationModel getDecorationModel()
    {
        return decorationModel;
    }
}
