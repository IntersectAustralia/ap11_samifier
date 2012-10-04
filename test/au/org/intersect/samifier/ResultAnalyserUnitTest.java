package au.org.intersect.samifier;

import au.org.intersect.samifier.mascot.PeptideSearchResultsParser;
import au.org.intersect.samifier.mascot.PeptideSearchResultsParserImpl;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: diego
 * Date: 26/09/12
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResultAnalyserUnitTest
{
    private static final Logger LOG = Logger.getLogger(ResultsAnalyser.class);
    @Test
    public void testCreateResultsAnalyses()
    {
        try
        {
            File mascotFile = new File("test/resources/test_mascot_search_results.txt");
            File mapFile = new File("test/resources/test_accession.txt");
            File genomeFile = new File("test/resources/test_genome.gff");
            File chromosomeDir = new File("test/resources/");


            Genome genome = Genome.parse(genomeFile);

            Map<String,String> map = Samifier.parseProteinToOLNMappingFile(mapFile);
            PeptideSearchResultsParser peptideSearchResultsParser = new PeptideSearchResultsParserImpl(map);

            List<PeptideSearchResult> peptideSearchResults = peptideSearchResultsParser.parseResults(mascotFile);

            File resultAnalysisFile = File.createTempFile("out", "txt");
            resultAnalysisFile.deleteOnExit();

            ResultsAnalyser analyser = new ResultsAnalyser(mascotFile, genomeFile, mapFile, resultAnalysisFile, chromosomeDir);

            analyser.createResultAnalysis();

            List<String> expectedLines = FileUtils.readLines(new File("test/resources/expected_results_analysis.txt"));
            List<String> gotLines = FileUtils.readLines(resultAnalysisFile);
            assertEquals(expectedLines.size(), gotLines.size());

            for (int cnt = 0; cnt < expectedLines.size(); cnt++)
            {
                String expected = expectedLines.get(cnt);
                String got = gotLines.get(cnt);
                assertEquals("Should generate the expected result analysis line", expected.trim(), got.trim());
            }
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
