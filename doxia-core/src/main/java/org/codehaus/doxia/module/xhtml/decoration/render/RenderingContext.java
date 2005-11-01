package org.codehaus.doxia.module.xhtml.decoration.render;

import org.codehaus.doxia.module.xhtml.decoration.model.DecorationModel;
import org.codehaus.plexus.util.PathTool;

import java.io.File;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class RenderingContext
{
    private File basedir;

    private String relativePath;

    private String outputName;

    private DecorationModel decorationModel;

    public RenderingContext( File basedir, String document, DecorationModel decorationModel )
    {
        this.basedir = basedir;

        // For site comparisons we'll prepend a "/"
        this.outputName = "/" + document.substring( 0, document.lastIndexOf( "." ) + 1 ) + "html";

        relativePath = PathTool.getRelativePath( basedir.getPath(), new File( basedir, document ).getPath() );

        this.decorationModel = decorationModel;
    }

    public File getBasedir()
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
