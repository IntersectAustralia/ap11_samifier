package au.org.intersect.samifier;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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

    @Test
    public void testParsingMascotPeptideSearchResults()
    {
        File mascotFile = new File(getClass().getResource("/test_mascot_search_results.txt").getFile());

        File mapFile = new File(getClass().getResource("/test_accession.txt").getFile());
        List<PeptideSearchResult> list = null;
        try {
            Map map = Samifier.parseProteinToOLNMappingFile(mapFile);
            list = Samifier.parseMascotPeptideSearchResults(mascotFile, map);
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
        assertEquals("Parser should find six ", 6, list.size());
    }

    @Test
    public void testExtractSequenceParts()
    {
        File chromosomeFile = new File(getClass().getResource("/chrI.fa").getFile());
        List<GeneSequence> locations = new ArrayList<GeneSequence>();
        locations.add(new GeneSequence(GeneSequence.CODING_SEQUENCE, 71, 79, "+"));
        locations.add(new GeneSequence(GeneSequence.INTRON, 80, 141, "+"));
        locations.add(new GeneSequence(GeneSequence.CODING_SEQUENCE, 142, 213, "+"));
        List<NucleotideSequence> parts = null;
        try {
            parts = Samifier.extractSequenceParts(chromosomeFile, locations);
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
        assertEquals("Creates List of 3 NucleotideSequence objects", 3, parts.size());
        NucleotideSequence codingSequence1 = parts.get(0);
        NucleotideSequence intron = parts.get(1);
        NucleotideSequence codingSequence2 = parts.get(2);

        assertEquals("First NucleotideSequence should be CTACCCTAA", "CTACCCTAA", codingSequence1.getSequence());
        assertEquals("Second NucleotideSequence should be an intron", GeneSequence.INTRON, intron.getType());
        assertEquals("Last NucleotideSequence should be ACTCGTTACCCTGTCCCATTCAACCATACCACTCCGAACCACCATCCATCCCTCTACTTACTACCACTCACC", "ACTCGTTACCCTGTCCCATTCAACCATACCACTCCGAACCACCATCCATCCCTCTACTTACTACCACTCACC", codingSequence2.getSequence());
    }

    @Test
    public void testExtractSequencePartsForShortSequences()
    {
        File chromosomeFile = new File(getClass().getResource("/chrI.fa").getFile());
        List<GeneSequence> locations = new ArrayList<GeneSequence>();
        locations.add(new GeneSequence(GeneSequence.CODING_SEQUENCE, 1, 9, "+"));
        locations.add(new GeneSequence(GeneSequence.INTRON, 10, 11, "+"));
        locations.add(new GeneSequence(GeneSequence.CODING_SEQUENCE, 12, 14, "+"));
        List<NucleotideSequence> parts = null;
        try {
            parts = Samifier.extractSequenceParts(chromosomeFile, locations);
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
        assertEquals("Creates List of 3 NucleotideSequence objects", 3, parts.size());
        NucleotideSequence codingSequence1 = parts.get(0);
        NucleotideSequence intron = parts.get(1);
        NucleotideSequence codingSequence2 = parts.get(2);

        assertEquals("First NucleotideSequence should be CCACACCAC", "CCACACCAC", codingSequence1.getSequence());
        assertEquals("Second NucleotideSequence should be an intron", GeneSequence.INTRON, intron.getType());
        assertEquals("Last NucleotideSequence should be CCA", "CCA", codingSequence2.getSequence());
    }

    @Test
    public void testGetPeptideSequence()
    {
    }
    /*
    @Test
    public void testCreateSAM()
    {
        StringWriter sam = new StringWriter();
        Samifier.createSAM(Genome genome, Map<String,String> proteinOLNMap, List<PeptideSearchResult> peptideSearchResults, File chromosomeDirectory, Writer output);
        assertEquals();
    }
    */
}
