
package au.org.intersect.samifier;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * * Tests {@link Samifier}
 * */
public final class SamifierBuildingUnitTest
{

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
        PeptideSearchResult peptideSearchResult = new PeptideSearchResult("test", "HP", "DummyProtein", 3, 4);
        List<NucleotideSequence> sequenceParts = new ArrayList<NucleotideSequence>();
        sequenceParts.add(new NucleotideSequence("CCACACCAC", GeneSequence.CODING_SEQUENCE, 1, 9));
        sequenceParts.add(new NucleotideSequence(null, GeneSequence.INTRON, 10, 11));
        sequenceParts.add(new NucleotideSequence("CCA", GeneSequence.CODING_SEQUENCE, 12, 14));
        PeptideSequence p = null;
        try {
            p = Samifier.getPeptideSequence(peptideSearchResult, sequenceParts);
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
        assertEquals("Peptide extracted should be CACCCA", "CACCCA", p.getNucleotideSequence());
        assertEquals("Peptide cigar string should be 3M2N3M", "3M2N3M", p.getCigarString());
    }

    @Test
    public void testCreateSAM()
    {
        try {
            File mascotFile = new File(getClass().getResource("/test_mascot_search_results.txt").getFile());
            File mapFile = new File(getClass().getResource("/test_accession.txt").getFile());
            File genomeFile = new File(getClass().getResource("/test_genome.gff").getFile());


            Genome genome = Genome.parse(genomeFile);
            Map<String,String> map = Samifier.parseProteinToOLNMappingFile(mapFile);
            List<PeptideSearchResult> peptideSearchResults = Samifier.parseMascotPeptideSearchResults(mascotFile, map);
            File chromosomeDir = new File(getClass().getResource("/chrI.fa").getFile()).getParentFile();

            StringWriter sam = new StringWriter();

            Samifier.createSAM(genome, map, peptideSearchResults, chromosomeDir, sam);
            System.out.println(sam.toString());
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
        //assertEquals();
    }
}
