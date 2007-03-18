package org.apache.maven.doxia.macro;

import org.codehaus.plexus.PlexusTestCase;
import org.apache.maven.doxia.macro.manager.MacroManager;

/** @author Jason van Zyl */
public class MacroManagerTest
    extends PlexusTestCase
{
    public void testMacroManager()
        throws Exception
    {
        MacroManager mm = (MacroManager) lookup( MacroManager.ROLE );
        
        assertNotNull( mm );
    }
}
