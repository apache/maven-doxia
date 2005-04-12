package org.codehaus.doxia;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: DoxiaTest.java,v 1.2 2004/11/02 03:40:48 jvanzyl Exp $
 */
public class DoxiaTest
    extends PlexusTestCase
{
    public void testDoxia()
        throws Exception
    {
        Doxia doxia = (Doxia) lookup( Doxia.ROLE );

        assertNotNull( doxia.getParserManager() );

        //assertNotNull( doxia.getSinkManager() );

        assertNotNull( doxia.getMacroManager() );

        assertNotNull( doxia.getPluginManager() );
    }
}