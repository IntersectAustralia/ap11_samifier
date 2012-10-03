
package au.org.intersect.samifier;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import au.org.intersect.samifier.mascot.PeptideSearchResultsParser;
import au.org.intersect.samifier.mascot.PeptideSearchResultsParserImpl;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

/**
 * * Tests {@link Samifier}
 * */
public final class SamifierBuildingUnitTest
{
    private static BigDecimal dummyBigDecimal = null;

    @Test
    public void testExtractSequenceParts()
    {
        File chromosomeFile = new File("test/resources/chrI.fa");
        List<GeneSequence> locations = new ArrayList<GeneSequence>();
        locations.add(new GeneSequence(GeneSequence.CODING_SEQUENCE, 71, 79, "+"));
        locations.add(new GeneSequence(GeneSequence.INTRON, 80, 141, "+"));
        locations.add(new GeneSequence(GeneSequence.CODING_SEQUENCE, 142, 213, "+"));
        GeneInfo gene = new GeneInfo("chrI", 1, 250, GeneInfo.FORWARD, locations);
        List<NucleotideSequence> parts = null;
        try {
            parts = Samifier.extractSequenceParts(chromosomeFile, gene);
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
        File chromosomeFile = new File("test/resources/chrI.fa");
        List<GeneSequence> locations = new ArrayList<GeneSequence>();
        locations.add(new GeneSequence(GeneSequence.CODING_SEQUENCE, 1, 9, "+"));
        locations.add(new GeneSequence(GeneSequence.INTRON, 10, 11, "+"));
        locations.add(new GeneSequence(GeneSequence.CODING_SEQUENCE, 12, 14, "+"));
        GeneInfo gene = new GeneInfo("chrI", 1, 250, GeneInfo.FORWARD, locations);
        List<NucleotideSequence> parts = null;
        try {
            parts = Samifier.extractSequenceParts(chromosomeFile, gene);
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
    public void testGetPeptideSequenceCrossingAnIntronForward()
    {
        PeptideSearchResult peptideSearchResult = new PeptideSearchResult("test", "HP", "DummyProtein", 33, 36, dummyBigDecimal);

        List<GeneSequence> locations = new ArrayList<GeneSequence>();
        locations.add(new GeneSequence(GeneSequence.CODING_SEQUENCE, 87286, 87387, GeneInfo.FORWARD));
        locations.add(new GeneSequence(GeneSequence.CODING_SEQUENCE, 87501, 87752, GeneInfo.FORWARD));
        locations.add(new GeneSequence(GeneSequence.INTRON, 87388, 87500, GeneInfo.FORWARD));
        File chromosomeFile = new File("test/resources/chrI.fa");
        GeneInfo gene = new GeneInfo("chrI", 87286, 87752, GeneInfo.FORWARD, locations);
        PeptideSequence p = null;
        try {
            p = Samifier.getPeptideSequence(peptideSearchResult, chromosomeFile, gene);
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
        assertEquals("Peptide extracted should be CAAGCTGAAATT", "CAAGCTGAAATT", p.getNucleotideSequence());
        assertEquals("Peptide cigar string should be 6M113N6M", "6M113N6M", p.getCigarString());
    }

    @Test
    public void testGetPeptideSequenceWithinCodingSequence()
    {
        PeptideSearchResult peptideSearchResult = new PeptideSearchResult("test", "HP", "DummyProtein", 3, 6, dummyBigDecimal);
        List<GeneSequence> locations = new ArrayList<GeneSequence>();
        locations.add(new GeneSequence(GeneSequence.CODING_SEQUENCE, 87286, 87387, GeneInfo.FORWARD));
        locations.add(new GeneSequence(GeneSequence.CODING_SEQUENCE, 87501, 87752, GeneInfo.FORWARD));
        locations.add(new GeneSequence(GeneSequence.INTRON, 87388, 87500, GeneInfo.FORWARD));
        File chromosomeFile = new File("test/resources/chrI.fa");
        GeneInfo gene = new GeneInfo("chrI", 87286, 87752, GeneInfo.FORWARD, locations);
        PeptideSequence p = null;
        try {
            p = Samifier.getPeptideSequence(peptideSearchResult, chromosomeFile, gene);
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
        assertEquals("Peptide extracted should be TCATCTACTCCC", "TCATCTACTCCC", p.getNucleotideSequence());
        assertEquals("Peptide cigar string should be 12M", "12M", p.getCigarString());
    }

    @Test
    public void testGetPeptideSequenceCrossingAnIntronReverse()
    {
        PeptideSearchResult peptideSearchResult = new PeptideSearchResult("test", "HP", "DummyProtein", 81, 85, dummyBigDecimal);

        List<GeneSequence> locations = new ArrayList<GeneSequence>();
        locations.add(new GeneSequence(GeneSequence.CODING_SEQUENCE, 87286, 87387, GeneInfo.REVERSE));
        locations.add(new GeneSequence(GeneSequence.CODING_SEQUENCE, 87501, 87752, GeneInfo.REVERSE));
        locations.add(new GeneSequence(GeneSequence.INTRON, 87388, 87500, GeneInfo.REVERSE));
        File chromosomeFile = new File("test/resources/chrI.fa");
        GeneInfo gene = new GeneInfo("chrI", 87286, 87752, GeneInfo.REVERSE, locations);
        PeptideSequence p = null;
        try {
            p = Samifier.getPeptideSequence(peptideSearchResult, chromosomeFile, gene);
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
        assertEquals("Peptide extracted should be GCTGAAATTGATGAT", "GCTGAAATTGATGAT", p.getNucleotideSequence());
        assertEquals("Peptide cigar string should be 3M113N12M", "3M113N12M", p.getCigarString());
    }

    @Test
    public void testCreateSAM()
    {
        try {
            File mascotFile = new File("test/resources/test_mascot_search_results.txt");
            File mapFile = new File("test/resources/test_accession.txt");
            File genomeFile = new File("test/resources/test_genome.gff");


            Genome genome = Genome.parse(genomeFile);
            Map<String,String> map = Samifier.parseProteinToOLNMappingFile(mapFile);
            PeptideSearchResultsParser peptideSearchResultsParser = new PeptideSearchResultsParserImpl(map);

            List<PeptideSearchResult> peptideSearchResults = peptideSearchResultsParser.parseResults(mascotFile);
            File chromosomeDir = new File("test/resources/");

            File samFile = File.createTempFile("out", "sam");
            samFile.deleteOnExit();
            FileWriter sam = new FileWriter(samFile);
            Samifier.createSAM(genome, map, peptideSearchResults, chromosomeDir, sam, null);

            List<String> expectedLines = FileUtils.readLines(new File("test/resources/expected.sam"));
            List<String> gotLines = FileUtils.readLines(samFile);
            assertEquals("Should generate the expected SAM file", expectedLines, gotLines);
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
        //assertEquals();
    }
}
