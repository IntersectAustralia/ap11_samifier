package au.org.intersect.samifier.generator;

import au.org.intersect.samifier.domain.*;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PeptideSequenceGeneratorImplUnitTest
{
    private static BigDecimal dummyBigDecimal = null;

    @Test
    public void testExtractSequenceParts()
    {
        File chromosomeFile = new File("test/resources/chrI.fa");
        List<GeneSequence> locations = new ArrayList<GeneSequence>();
        locations.add(new GeneSequence("G01", true, 71, 79, 1));
        locations.add(new GeneSequence("G01", false, 80, 141, 1));
        locations.add(new GeneSequence("G01", true, 142, 213, 1));
        GeneInfo gene = new GeneInfo("chrI", "G01", 1, 250, 1);
        gene.setLocations(locations);
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
        locations.add(new GeneSequence("G01", true, 1, 9, 1));
        locations.add(new GeneSequence("G01", false, 10, 11, 1));
        locations.add(new GeneSequence("G01", true, 12, 14, 1));
        GeneInfo gene = new GeneInfo("chrI", "G01", 1, 250, 1);
        gene.setLocations(locations);
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
        locations.add(new GeneSequence("olnDummy", true, 87286, 87387, 1));
        locations.add(new GeneSequence("olnDummy", true, 87501, 87752, 1));
        locations.add(new GeneSequence("olnDummy", false, 87388, 87500, 1));
        File chromosomeDirectory = new File("test/resources/");
        GeneInfo gene = new GeneInfo("chrI", "olnDummy", 87286, 87752, 1);
        gene.setLocations(locations);

        Genome genome = new Genome();
        genome.addGene(gene);

        FileBasedProteinToOLNMap proteinOLNMap = new FileBasedProteinToOLNMap();
        proteinOLNMap.addMapping("DummyProtein", "olnDummy");

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
        locations.add(new GeneSequence("olnDummy", true, 87286, 87387, 1));
        locations.add(new GeneSequence("olnDummy", true, 87501, 87752, 1));
        locations.add(new GeneSequence("olnDummy", true, 87388, 87500, 1));
        File chromosomeDirectory = new File("test/resources/");
        GeneInfo gene = new GeneInfo("chrI", "olnDummy", 87286, 87752, 1);
        gene.setLocations(locations);

        Genome genome = new Genome();
        genome.addGene(gene);

        FileBasedProteinToOLNMap proteinOLNMap = new FileBasedProteinToOLNMap();
        proteinOLNMap.addMapping("DummyProtein", "olnDummy");

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
        locations.add(new GeneSequence("G01", true, 87286, 87387, -1));
        locations.add(new GeneSequence("G01", true, 87501, 87752, -1));
        locations.add(new GeneSequence("G01", false, 87388, 87500, -1));
        File chromosomeDirectory = new File("test/resources/");
        GeneInfo gene = new GeneInfo("chrI", "G01", 87286, 87752, -1);
        gene.setLocations(locations);

        Genome genome = new Genome();
        genome.addGene(gene);

        FileBasedProteinToOLNMap proteinOLNMap = new FileBasedProteinToOLNMap();
        proteinOLNMap.addMapping("DummyProtein", "G01");

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
        assertEquals("Peptide extracted should be ATCATCAATTTCAGC", "ATCATCAATTTCAGC", p.getNucleotideSequence());
        assertEquals("Peptide cigar string should be 3M113N12M", "3M113N12M", p.getCigarString());
    }
}
