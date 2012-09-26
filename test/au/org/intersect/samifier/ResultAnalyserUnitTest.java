package au.org.intersect.samifier;

import org.apache.commons.io.FileUtils;
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
    @Test
    public void testCreateResultsAnalyses()
    {
        try
        {
            File mascotFile = new File("test/resources/test_mascot_search_results.txt");
            File mapFile = new File("test/resources/test_accession.txt");
            File genomeFile = new File("test/resources/test_genome.gff");


            Genome genome = Genome.parse(genomeFile);
            Map<String,String> map = Samifier.parseProteinToOLNMappingFile(mapFile);
            List<PeptideSearchResult> peptideSearchResults = Samifier.parseMascotPeptideSearchResults(mascotFile, map);

            File resultAnalysisFile = File.createTempFile("out", "txt");
            resultAnalysisFile.deleteOnExit();
            FileWriter raf = new FileWriter(resultAnalysisFile);
            ResultsAnalyser.createResultAnalysis(genome, map, peptideSearchResults, raf);

            List<String> expectedLines = FileUtils.readLines(new File("test/resources/expected_results_analysis.txt"));
            List<String> gotLines = FileUtils.readLines(resultAnalysisFile);

            assertEquals("Should generate the expected result analysis file", expectedLines, gotLines);
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
