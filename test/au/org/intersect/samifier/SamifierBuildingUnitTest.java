
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
