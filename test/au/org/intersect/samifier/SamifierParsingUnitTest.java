package au.org.intersect.samifier;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import au.org.intersect.samifier.mascot.PeptideSearchResultsParser;
import au.org.intersect.samifier.mascot.PeptideSearchResultsParserImpl;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * * Tests {@link Samifier}
 * */
public final class SamifierParsingUnitTest
{

    @Test
    public void testParsingProteinToOLNMappingFile() 
    {
        File f = new File("test/resources/test_accession.txt");
        Map<String,String> map = null;
        try {
            map = Samifier.parseProteinToOLNMappingFile(f);
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
        assertEquals("Map has 3 entries", 3, map.size());
        assertTrue("Map has key KPYK1_YEAST", map.containsKey("KPYK1_YEAST"));
        assertTrue("Map has key RL31A_YEAST", map.containsKey("RL31A_YEAST"));
        assertTrue("Map has key RL36B_YEAST", map.containsKey("RL36B_YEAST"));
        assertEquals("KPYK1_YEAST maps to YAL038W", "YAL038W", map.get("KPYK1_YEAST"));
        assertEquals("RL31A_YEAST maps to YDL075W", "YDL075W", map.get("RL31A_YEAST"));
        assertEquals("RL36B_YEAST maps to YPL249C-A", "YPL249C-A", map.get("RL36B_YEAST"));
    }

}
