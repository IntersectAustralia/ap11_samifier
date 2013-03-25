package au.org.intersect.samifier.parser;

import static org.junit.Assert.*;

import au.org.intersect.samifier.domain.ProteinToOLNMap;
import au.org.intersect.samifier.parser.ProteinToOLNParser;
import au.org.intersect.samifier.parser.ProteinToOLNParserImpl;
import org.junit.Test;

import java.io.File;

/**
 * * Tests {@link au.org.intersect.samifier.Samifier}
 * */
public final class ProteinToOLNParserImplUnitTest
{

    @Test
    public void testParsingProteinToOLNMappingFile() 
    {
        File mapFile = new File("test/resources/test_accession.txt");
        ProteinToOLNMap proteinToOLNMap = null;
        try {
            ProteinToOLNParser proteinToOLNParser = new ProteinToOLNParserImpl();
            proteinToOLNMap = proteinToOLNParser.parseMappingFile(mapFile);
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }

        assertTrue("Map has key KPYK1_YEAST", proteinToOLNMap.containsProtein("KPYK1_YEAST"));
        assertTrue("Map has key RL31A_YEAST", proteinToOLNMap.containsProtein("RL31A_YEAST"));
        assertTrue("Map has key RL36B_YEAST", proteinToOLNMap.containsProtein("RL36B_YEAST"));
        assertEquals("KPYK1_YEAST maps to YAL038W", "YAL038W", proteinToOLNMap.getOLN("KPYK1_YEAST"));
        assertEquals("RL31A_YEAST maps to YDL075W", "YDL075W", proteinToOLNMap.getOLN("RL31A_YEAST"));
        assertEquals("RL36B_YEAST maps to YPL249C-A", "YPL249C-A", proteinToOLNMap.getOLN("RL36B_YEAST"));
    }

}
