package au.org.intersect.samifier;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import java.io.File;
import java.util.Map;

/**
 * * Tests {@link Genome}
 * */
public final class SamifierUnitTest
{
    @Test
    public void testParsingProteinToOLNMappingFile() 
    {
        File f = new File(getClass().getResource("/test_accession.txt").getFile());
        Map<String,String> map = null;
        try {
            map = Samifier.parseProteinToOLNMappingFile(f);
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
        assertEquals("Map has 2 entries", 2, map.size());
        assertTrue("Map has key KPYK1_YEAST", map.containsKey("KPYK1_YEAST"));
        assertTrue("Map has key RL31A_YEAST", map.containsKey("RL31A_YEAST"));
        assertEquals("KPYK1_YEAST maps to YAL038W", "YAL038W", map.get("KPYK1_YEAST"));
        assertEquals("RL31A_YEAST maps to YDL075W", "YDL075W", map.get("RL31A_YEAST"));
    }

}
