package au.org.intersect.samifier.generator;

import au.org.intersect.samifier.domain.Genome;
import au.org.intersect.samifier.parser.GenomeParserImpl;
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
        List<au.org.intersect.samifier.domain.GeneSequence> locations = new ArrayList<au.org.intersect.samifier.domain.GeneSequence>();
        locations.add(new au.org.intersect.samifier.domain.GeneSequence(au.org.intersect.samifier.domain.GeneSequence.CODING_SEQUENCE, 71, 79, "+"));
        locations.add(new au.org.intersect.samifier.domain.GeneSequence(au.org.intersect.samifier.domain.GeneSequence.INTRON, 80, 141, "+"));
        locations.add(new au.org.intersect.samifier.domain.GeneSequence(au.org.intersect.samifier.domain.GeneSequence.CODING_SEQUENCE, 142, 213, "+"));
        au.org.intersect.samifier.domain.GeneInfo gene = new au.org.intersect.samifier.domain.GeneInfo("chrI", 1, 250, au.org.intersect.samifier.domain.GeneInfo.FORWARD, locations);
        List<au.org.intersect.samifier.domain.NucleotideSequence> parts = null;
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
        au.org.intersect.samifier.domain.NucleotideSequence codingSequence1 = parts.get(0);
        au.org.intersect.samifier.domain.NucleotideSequence intron = parts.get(1);
        au.org.intersect.samifier.domain.NucleotideSequence codingSequence2 = parts.get(2);

        assertEquals("First NucleotideSequence should be CTACCCTAA", "CTACCCTAA", codingSequence1.getSequence());
        assertEquals("Second NucleotideSequence should be an intron", au.org.intersect.samifier.domain.GeneSequence.INTRON, intron.getType());
        assertEquals("Last NucleotideSequence should be ACTCGTTACCCTGTCCCATTCAACCATACCACTCCGAACCACCATCCATCCCTCTACTTACTACCACTCACC", "ACTCGTTACCCTGTCCCATTCAACCATACCACTCCGAACCACCATCCATCCCTCTACTTACTACCACTCACC", codingSequence2.getSequence());
    }

    @Test
    public void testExtractSequencePartsForShortSequences()
    {
        File chromosomeFile = new File("test/resources/chrI.fa");
        List<au.org.intersect.samifier.domain.GeneSequence> locations = new ArrayList<au.org.intersect.samifier.domain.GeneSequence>();
        locations.add(new au.org.intersect.samifier.domain.GeneSequence(au.org.intersect.samifier.domain.GeneSequence.CODING_SEQUENCE, 1, 9, "+"));
        locations.add(new au.org.intersect.samifier.domain.GeneSequence(au.org.intersect.samifier.domain.GeneSequence.INTRON, 10, 11, "+"));
        locations.add(new au.org.intersect.samifier.domain.GeneSequence(au.org.intersect.samifier.domain.GeneSequence.CODING_SEQUENCE, 12, 14, "+"));
        au.org.intersect.samifier.domain.GeneInfo gene = new au.org.intersect.samifier.domain.GeneInfo("chrI", 1, 250, au.org.intersect.samifier.domain.GeneInfo.FORWARD, locations);
        List<au.org.intersect.samifier.domain.NucleotideSequence> parts = null;
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
        au.org.intersect.samifier.domain.NucleotideSequence codingSequence1 = parts.get(0);
        au.org.intersect.samifier.domain.NucleotideSequence intron = parts.get(1);
        au.org.intersect.samifier.domain.NucleotideSequence codingSequence2 = parts.get(2);

        assertEquals("First NucleotideSequence should be CCACACCAC", "CCACACCAC", codingSequence1.getSequence());
        assertEquals("Second NucleotideSequence should be an intron", au.org.intersect.samifier.domain.GeneSequence.INTRON, intron.getType());
        assertEquals("Last NucleotideSequence should be CCA", "CCA", codingSequence2.getSequence());
    }

    @Test
    public void testGetPeptideSequenceCrossingAnIntronForward()
    {
        au.org.intersect.samifier.domain.PeptideSearchResult peptideSearchResult = new au.org.intersect.samifier.domain.PeptideSearchResult("test", "HP", "DummyProtein", 33, 36, dummyBigDecimal);

        List<au.org.intersect.samifier.domain.GeneSequence> locations = new ArrayList<au.org.intersect.samifier.domain.GeneSequence>();
        locations.add(new au.org.intersect.samifier.domain.GeneSequence(au.org.intersect.samifier.domain.GeneSequence.CODING_SEQUENCE, 87286, 87387, au.org.intersect.samifier.domain.GeneInfo.FORWARD));
        locations.add(new au.org.intersect.samifier.domain.GeneSequence(au.org.intersect.samifier.domain.GeneSequence.CODING_SEQUENCE, 87501, 87752, au.org.intersect.samifier.domain.GeneInfo.FORWARD));
        locations.add(new au.org.intersect.samifier.domain.GeneSequence(au.org.intersect.samifier.domain.GeneSequence.INTRON, 87388, 87500, au.org.intersect.samifier.domain.GeneInfo.FORWARD));
        File chromosomeDirectory = new File("test/resources/");
        au.org.intersect.samifier.domain.GeneInfo gene = new au.org.intersect.samifier.domain.GeneInfo("chrI", 87286, 87752, au.org.intersect.samifier.domain.GeneInfo.FORWARD, locations);

        Genome genome = new Genome();
        genome.addGene("olnDummy", gene);

        Map<String, String> proteinOLNMap = new HashMap<String, String>();
        proteinOLNMap.put("DummyProtein", "olnDummy");

        au.org.intersect.samifier.domain.PeptideSequence p = null;
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
        au.org.intersect.samifier.domain.PeptideSearchResult peptideSearchResult = new au.org.intersect.samifier.domain.PeptideSearchResult("test", "HP", "DummyProtein", 3, 6, dummyBigDecimal);
        List<au.org.intersect.samifier.domain.GeneSequence> locations = new ArrayList<au.org.intersect.samifier.domain.GeneSequence>();
        locations.add(new au.org.intersect.samifier.domain.GeneSequence(au.org.intersect.samifier.domain.GeneSequence.CODING_SEQUENCE, 87286, 87387, au.org.intersect.samifier.domain.GeneInfo.FORWARD));
        locations.add(new au.org.intersect.samifier.domain.GeneSequence(au.org.intersect.samifier.domain.GeneSequence.CODING_SEQUENCE, 87501, 87752, au.org.intersect.samifier.domain.GeneInfo.FORWARD));
        locations.add(new au.org.intersect.samifier.domain.GeneSequence(au.org.intersect.samifier.domain.GeneSequence.INTRON, 87388, 87500, au.org.intersect.samifier.domain.GeneInfo.FORWARD));
        File chromosomeDirectory = new File("test/resources/");
        au.org.intersect.samifier.domain.GeneInfo gene = new au.org.intersect.samifier.domain.GeneInfo("chrI", 87286, 87752, au.org.intersect.samifier.domain.GeneInfo.FORWARD, locations);

        Genome genome = new Genome();
        genome.addGene("olnDummy", gene);

        Map<String, String> proteinOLNMap = new HashMap<String, String>();
        proteinOLNMap.put("DummyProtein", "olnDummy");

        au.org.intersect.samifier.domain.PeptideSequence p = null;
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
        au.org.intersect.samifier.domain.PeptideSearchResult peptideSearchResult = new au.org.intersect.samifier.domain.PeptideSearchResult("test", "HP", "DummyProtein", 81, 85, dummyBigDecimal);

        List<au.org.intersect.samifier.domain.GeneSequence> locations = new ArrayList<au.org.intersect.samifier.domain.GeneSequence>();
        locations.add(new au.org.intersect.samifier.domain.GeneSequence(au.org.intersect.samifier.domain.GeneSequence.CODING_SEQUENCE, 87286, 87387, au.org.intersect.samifier.domain.GeneInfo.REVERSE));
        locations.add(new au.org.intersect.samifier.domain.GeneSequence(au.org.intersect.samifier.domain.GeneSequence.CODING_SEQUENCE, 87501, 87752, au.org.intersect.samifier.domain.GeneInfo.REVERSE));
        locations.add(new au.org.intersect.samifier.domain.GeneSequence(au.org.intersect.samifier.domain.GeneSequence.INTRON, 87388, 87500, au.org.intersect.samifier.domain.GeneInfo.REVERSE));
        File chromosomeDirectory = new File("test/resources/");
        au.org.intersect.samifier.domain.GeneInfo gene = new au.org.intersect.samifier.domain.GeneInfo("chrI", 87286, 87752, au.org.intersect.samifier.domain.GeneInfo.REVERSE, locations);

        Genome genome = new Genome();
        genome.addGene("olnDummy", gene);

        Map<String, String> proteinOLNMap = new HashMap<String, String>();
        proteinOLNMap.put("DummyProtein", "olnDummy");

        au.org.intersect.samifier.domain.PeptideSequence p = null;
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
