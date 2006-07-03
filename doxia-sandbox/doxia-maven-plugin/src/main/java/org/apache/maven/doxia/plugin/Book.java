package org.apache.maven.doxia.plugin;

import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class Book
{
    private String descriptor;

    private List formats;

    private String directory;

    private List includes;

    private List excludes;

    public String getDescriptor()
    {
        return descriptor;
    }

    public List getFormats()
    {
        return formats;
    }

    public String getDirectory()
    {
        return directory;
    }

    public List getIncludes()
    {
        return includes;
    }

    public List getExcludes()
    {
        return excludes;
    }
}
