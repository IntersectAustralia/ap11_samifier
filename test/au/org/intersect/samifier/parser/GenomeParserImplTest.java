package au.org.intersect.samifier.parser;

import au.org.intersect.samifier.domain.Genome;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@SuppressWarnings("unused")
public class GenomeParserImplTest {

    @Test
    public void testParseGenomeFile() throws GenomeFileParsingException {
        GenomeParser parser = new GenomeParserImpl();
        Genome gene = parser.parseGenomeFile(new File("test/resources/test_genome.gff"));
        assertTrue("Gene YAL038W", gene.hasGene("YAL038W"));
        assertEquals("Gene YAL038W should have 1 location", 1, gene.getGene("YAL038W").getLocations().size());
        assertTrue("Gene YAR009C", gene.hasGene("YAR009C"));
        assertEquals("Gene YAR009C should have 1 location", 1, gene.getGene("YAR009C").getLocations().size());
        assertTrue("Gene YDL075W", gene.hasGene("YDL075W"));
        assertEquals("Gene YDL075W should have 3 location", 3, gene.getGene("YDL075W").getLocations().size());
        //new intron types
        assertTrue("Gene YPL249C-A", gene.hasGene("YPL249C-A"));
        assertEquals("Gene YPL249C-A should have 3 location", 3, gene.getGene("YPL249C-A").getLocations().size());
       
        
    }
}
