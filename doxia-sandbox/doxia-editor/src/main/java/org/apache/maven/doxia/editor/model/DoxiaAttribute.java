package org.apache.maven.doxia.editor.model;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DoxiaAttribute
{
    public final static String TYPE = "DOXIA_TYPE";

    public final static DoxiaAttribute TITLE = new DoxiaAttribute( "TITLE" );
    public final static DoxiaAttribute AUTHOR = new DoxiaAttribute( "AUTHOR" );
    public final static DoxiaAttribute DATE = new DoxiaAttribute( "DATE" );
    public final static DoxiaAttribute SECTION_1 = new DoxiaAttribute( "SECTION_1" );
    public final static DoxiaAttribute SECTION_2 = new DoxiaAttribute( "SECTION_2" );
    public final static DoxiaAttribute SECTION_3 = new DoxiaAttribute( "SECTION_3" );
    public final static DoxiaAttribute SECTION_4 = new DoxiaAttribute( "SECTION_4" );
    public final static DoxiaAttribute SECTION_5 = new DoxiaAttribute( "SECTION_5" );
    public final static DoxiaAttribute TEXT = new DoxiaAttribute( "TEXT" );
    public final static DoxiaAttribute PARAGRAPH_SEPARATOR = new DoxiaAttribute( "PARAGRAPH_SEPARATOR" );

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private final String value;

    public DoxiaAttribute( String value )
    {
        this.value = value;
    }

    public static String getType()
    {
        return TYPE;
    }

    public String toString()
    {
        return "DoxiaAttribute: " + value;
    }
}
