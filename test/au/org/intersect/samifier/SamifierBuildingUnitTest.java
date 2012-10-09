
package au.org.intersect.samifier;

import static org.junit.Assert.*;

import au.org.intersect.samifier.parser.PeptideSearchResultsParser;
import au.org.intersect.samifier.parser.PeptideSearchResultsParserImpl;
import au.org.intersect.samifier.parser.ProteinToOLNParser;
import au.org.intersect.samifier.parser.ProteinToOLNParserImpl;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
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
            ProteinToOLNParser proteinToOLNParser = new ProteinToOLNParserImpl();
            Map<String, String> proteinToOLNMap = proteinToOLNParser.parseMappingFile(mapFile);
            PeptideSearchResultsParser peptideSearchResultsParser = new PeptideSearchResultsParserImpl(proteinToOLNMap);

            List<PeptideSearchResult> peptideSearchResults = peptideSearchResultsParser.parseResults(mascotFile);
            File chromosomeDir = new File("test/resources/");

            File samFile = File.createTempFile("out", "sam");
            samFile.deleteOnExit();
            FileWriter sam = new FileWriter(samFile);
            Samifier.createSAM(genome, proteinToOLNMap, peptideSearchResults, chromosomeDir, sam, null);

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
