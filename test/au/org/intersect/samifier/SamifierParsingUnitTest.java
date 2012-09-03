package au.org.intersect.samifier;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

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

    @Test
    public void testParsingMascotPeptideSearchResults()
    {
        File mascotFile = new File(getClass().getResource("/test_mascot_search_results.txt").getFile());

        File mapFile = new File(getClass().getResource("/test_accession.txt").getFile());
        List<PeptideSearchResult> list = null;
        try {
            Map<String,String> map = Samifier.parseProteinToOLNMappingFile(mapFile);
            list = Samifier.parseMascotPeptideSearchResults(mascotFile, map);
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
        assertEquals("Parser should find six ", 6, list.size());
    }

}
