package org.codehaus.doxia.module.xhtml.decoration.model;

import org.codehaus.plexus.util.StringUtils;

public class PathTool
{
    public static final String getRelativePath( String basedir, String filename )
    {
        basedir = uppercaseDrive( basedir );
        filename = uppercaseDrive( filename );

        /*
         * Verify the arguments and make sure the filename is relative
         * to the base directory.
         */
        if ( basedir == null || basedir.length() == 0 || filename == null
            || filename.length() == 0 || !filename.startsWith( basedir ) )
        {
            return "";
        }

        /*
         * Normalize the arguments.  First, determine the file separator
         * that is being used, then strip that off the end of both the
         * base directory and filename.
         */
        String separator = determineSeparator( filename );
        basedir = StringUtils.chompLast( basedir, separator );
        filename = StringUtils.chompLast( filename, separator );

        /*
         * Remove the base directory from the filename to end up with a
         * relative filename (relative to the base directory).  This
         * filename is then used to determine the relative path.
         */
        String relativeFilename = filename.substring( basedir.length() );

        return determineRelativePath( relativeFilename, separator );
    }

    public static final String getRelativePath( String filename )
    {
        filename = uppercaseDrive( filename );

        if ( filename == null || filename.length() == 0 )
        {
            return "";
        }

        /*
         * Normalize the argument.  First, determine the file separator
         * that is being used, then strip that off the end of the
         * filename.  Then, if the filename doesn't begin with a
         * separator, add one.
         */

        String separator = determineSeparator( filename );
        filename = StringUtils.chompLast( filename, separator );
        if ( !filename.startsWith( separator ) )
        {
            filename = separator + filename;
        }

        return determineRelativePath( filename, separator );
    }

    public static final String getDirectoryComponent( String filename )
    {
        if ( filename == null || filename.length() == 0 )
        {
            return "";
        }

        String separator = determineSeparator( filename );
        String directory = StringUtils.chomp( filename, separator );

        if ( filename.equals( directory ) )
        {
            return ".";
        }
        else
        {
            return directory;
        }
    }

    private static final String determineRelativePath( String filename,
                                                       String separator )
    {
        if ( filename.length() == 0 )
        {
            return "";
        }


        /*
         * Count the slashes in the relative filename, but exclude the
         * leading slash.  If the path has no slashes, then the filename
         * is relative to the current directory.
         */
        int slashCount = StringUtils.countMatches( filename, separator ) - 1;
        if ( slashCount <= 0 )
        {
            return ".";
        }

        /*
         * The relative filename contains one or more slashes indicating
         * that the file is within one or more directories.  Thus, each
         * slash represents a "../" in the relative path.
         */
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < slashCount; i++ )
        {
            sb.append( "../" );
        }

        /*
         * Finally, return the relative path but strip the trailing
         * slash to mimic Anakia's behavior.
         */
        return StringUtils.chop( sb.toString() );
    }

    private static final String determineSeparator( String filename )
    {
        int forwardCount = StringUtils.countMatches( filename, "/" );
        int backwardCount = StringUtils.countMatches( filename, "\\" );

        return forwardCount >= backwardCount ? "/" : "\\";
    }

    static final String uppercaseDrive( String path )
    {
        if ( path == null )
            return null;

        if ( path.length() >= 2 && path.charAt( 1 ) == ':' )
        {
            path = path.substring( 0, 1 ).toUpperCase() + path.substring( 1 );
        }
        return path;
    }

    public static final String calculateLink( String link, String relativePath )
    {
        //This must be some historical feature
        if ( link.startsWith( "/site/" ) )
        {
            return link.substring( 5 );
        }

        //Allows absolute links in nav-bars etc
        if ( link.startsWith( "/absolute/" ) )
        {
            return link.substring( 10 );
        }

        // This traps urls like http://
        if ( link.indexOf( ":" ) >= 0 )
        {
            return link;
        }

        //If relativepath is current directory, just pass the link through
        if ( relativePath.equals( "." ) )
        {
            if ( link.startsWith( "/" ) )
            {
                return link.substring( 1 );
            }
            else
            {
                return link;
            }
        }

        //If we don't do this, you can end up with ..//bob.html rather than ../bob.html
        if ( relativePath.endsWith( "/" ) && link.startsWith( "/" ) )
        {
            return relativePath + "." + link.substring( 1 );
        }

        if ( relativePath.endsWith( "/" ) || link.startsWith( "/" ) )
        {
            return relativePath + link;
        }

        return relativePath + "/" + link;
    }
}
