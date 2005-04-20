package org.codehaus.doxia.module.xhtml.decoration.render;

import org.codehaus.doxia.module.xhtml.decoration.model.MavenDecorationModel;
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

    private MavenDecorationModel mavenDecorationModel;

    public RenderingContext( String basedir, String document, MavenDecorationModel mavenDecorationModel )
    {
        this.basedir = basedir;

        // For site comparisons we'll prepend a "/"
        this.outputName = "/" + document.substring( 0, document.lastIndexOf( "." ) + 1 ) + "html";

        relativePath = PathTool.getRelativePath( basedir, new File( basedir, document ).getPath() );

        this.mavenDecorationModel = mavenDecorationModel;
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

    public MavenDecorationModel getMavenDecorationModel()
    {
        return mavenDecorationModel;
    }
}
