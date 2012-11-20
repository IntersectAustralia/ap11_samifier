package au.org.intersect.samifier.domain;


import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import au.org.intersect.samifier.parser.GenomeParserImpl;
import org.junit.Test;
import org.junit.Before;

import java.io.File;

/**
 * * Tests {@link au.org.intersect.samifier.parser.GenomeParserImpl}
 * */
public final class GenomeUnitTest
{
    private File genomeFile = null;

    @Before
    public void oneTimeSetup()
    {
        genomeFile = new File("test/resources/test_genome.gff");
    }

    @Test
    public void testParsing() throws Exception
    {
        GenomeParserImpl genomeParser = new GenomeParserImpl();
        Genome genome = genomeParser.parseGenomeFile(genomeFile);
        assertEquals("GenomeParserImpl has 3 genes", 4, genome.getGeneEntries().size());
        assertTrue("GenomeParserImpl has gene YAL038W", genome.hasGene("YAL038W"));
        assertTrue("GenomeParserImpl has gene YAL038W", genome.hasGene("YDL075W"));
        assertTrue("GenomeParserImpl has gene YPL249C-A", genome.hasGene("YPL249C-A"));
    }

    @Test
    public void testGetGene() throws Exception
    {
        //GeneInfo gene = new GeneInfo()
        GenomeParserImpl genomeParser = new GenomeParserImpl();
        Genome genome = genomeParser.parseGenomeFile(genomeFile);
        assertThat("GenomeParserImpl returns GeneInfo object for known genes", genome.getGene("YAL038W"), instanceOf(GeneInfo.class));
        assertNull("GenomeParserImpl returns null for unknown genes", genome.getGene("UNKNOWN"));
    }
}
