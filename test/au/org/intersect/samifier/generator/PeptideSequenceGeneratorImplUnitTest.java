package au.org.intersect.samifier.generator;

import au.org.intersect.samifier.*;
import au.org.intersect.samifier.generator.PeptideSequenceGeneratorImpl;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: diego
 * Date: 4/10/12
 * Time: 1:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class PeptideSequenceGeneratorImplUnitTest
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
            PeptideSequenceGeneratorImpl sequenceGenerator = new PeptideSequenceGeneratorImpl(null, null, null);
            parts = sequenceGenerator.extractSequenceParts(chromosomeFile, gene);
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
            PeptideSequenceGeneratorImpl sequenceGenerator = new PeptideSequenceGeneratorImpl(null, null, null);
            parts = sequenceGenerator.extractSequenceParts(chromosomeFile, gene);
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
        File chromosomeDirectory = new File("test/resources/");
        GeneInfo gene = new GeneInfo("chrI", 87286, 87752, GeneInfo.FORWARD, locations);

        Genome genome = new Genome();
        genome.addGene("olnDummy", gene);

        Map<String, String> proteinOLNMap = new HashMap<String, String>();
        proteinOLNMap.put("DummyProtein", "olnDummy");

        PeptideSequence p = null;
        try {
            PeptideSequenceGeneratorImpl sequenceGenerator = new PeptideSequenceGeneratorImpl(genome, proteinOLNMap, chromosomeDirectory);
            p = sequenceGenerator.getPeptideSequence(peptideSearchResult);
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
        File chromosomeDirectory = new File("test/resources/");
        GeneInfo gene = new GeneInfo("chrI", 87286, 87752, GeneInfo.FORWARD, locations);

        Genome genome = new Genome();
        genome.addGene("olnDummy", gene);

        Map<String, String> proteinOLNMap = new HashMap<String, String>();
        proteinOLNMap.put("DummyProtein", "olnDummy");

        PeptideSequence p = null;
        try {
            PeptideSequenceGeneratorImpl sequenceGenerator = new PeptideSequenceGeneratorImpl(genome, proteinOLNMap, chromosomeDirectory);
            p = sequenceGenerator.getPeptideSequence(peptideSearchResult);
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
        File chromosomeDirectory = new File("test/resources/");
        GeneInfo gene = new GeneInfo("chrI", 87286, 87752, GeneInfo.REVERSE, locations);

        Genome genome = new Genome();
        genome.addGene("olnDummy", gene);

        Map<String, String> proteinOLNMap = new HashMap<String, String>();
        proteinOLNMap.put("DummyProtein", "olnDummy");

        PeptideSequence p = null;
        try {
            PeptideSequenceGeneratorImpl sequenceGenerator = new PeptideSequenceGeneratorImpl(genome, proteinOLNMap, chromosomeDirectory);
            p = sequenceGenerator.getPeptideSequence(peptideSearchResult);
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
        assertEquals("Peptide extracted should be GCTGAAATTGATGAT", "GCTGAAATTGATGAT", p.getNucleotideSequence());
        assertEquals("Peptide cigar string should be 3M113N12M", "3M113N12M", p.getCigarString());
    }
}
