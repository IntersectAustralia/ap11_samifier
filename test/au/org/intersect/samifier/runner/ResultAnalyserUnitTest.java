package au.org.intersect.samifier.runner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import au.org.intersect.samifier.ResultsAnalyser;
import au.org.intersect.samifier.reporter.ReportLister;

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
            File resultAnalysisFile = File.createTempFile("out", "txt");
            resultAnalysisFile.deleteOnExit();

            ResultAnalyserRunner analyser = new ResultAnalyserRunner(mascotFile, genomeFile, mapFile, 
            		resultAnalysisFile, chromosomeDir);
            analyser.run();

            List<String> expectedLines = FileUtils.readLines(new File("test/resources/expected_results_analysis.txt"));
            List<String> gotLines = FileUtils.readLines(resultAnalysisFile);
            assertEquals(expectedLines.size(), gotLines.size());
            for(String line : gotLines) System.out.println(line);
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
    
    @Test
    public void testCreateResultsAnalysesWithSql()
    {
        try
        {
            File mascotFile = new File("test/resources/test_mascot_search_results.txt");
            File mapFile = new File("test/resources/test_accession.txt");
            File genomeFile = new File("test/resources/test_genome.gff");
            File chromosomeDir = new File("test/resources/");
            String sqlQuery = "SELECT * FROM RESULT";
            
            File resultAnalysisFile = File.createTempFile("out", "txt");
            resultAnalysisFile.deleteOnExit();

            ResultAnalyserRunner analyser = new ResultAnalyserRunner(mascotFile, genomeFile, mapFile, 
            		resultAnalysisFile, chromosomeDir);
            
            analyser.initMemoryDb();
            analyser.runWithQuery(sqlQuery);

            List<String> expectedLines = FileUtils.readLines(new File("test/resources/expected_results_analysis.txt"));
            List<String> gotLines = FileUtils.readLines(resultAnalysisFile);
            
            assertEquals(expectedLines.size(), gotLines.size());

            for (int cnt = 0; cnt < expectedLines.size(); cnt++)
            {
                String expected = expectedLines.get(cnt);
                String got = gotLines.get(cnt);
                LOG.info(got);
                assertEquals("Should generate the expected result analysis line", expected.trim(), got.trim());
            }
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Test
    public void testCreateResultsAnalysesWithReportList()
    {
        try
        {
            File mascotFile = new File("test/resources/test_mascot_search_results.txt");
            File mapFile = new File("test/resources/test_accession.txt");
            File genomeFile = new File("test/resources/test_genome.gff");
            File chromosomeDir = new File("test/resources/");
            String repList = "test/resources/replist.properties";
            String repId = "all_genes";
            File resultAnalysisFile = File.createTempFile("out", "txt");
            resultAnalysisFile.deleteOnExit();

            ResultAnalyserRunner analyser = new ResultAnalyserRunner(mascotFile, genomeFile, mapFile, 
            		resultAnalysisFile, chromosomeDir);
            ReportLister reportLister = new ReportLister(repList);
            
            analyser.initMemoryDb();
            analyser.runWithQuery(reportLister.getQueryByReportId(repId));

            List<String> expectedLines = FileUtils.readLines(new File("test/resources/expected_results_analysis.txt"));
            List<String> gotLines = FileUtils.readLines(resultAnalysisFile);
            
            assertEquals(expectedLines.size(), gotLines.size());

            for (int cnt = 0; cnt < expectedLines.size(); cnt++)
            {
                String expected = expectedLines.get(cnt);
                String got = gotLines.get(cnt);
                LOG.info(got);
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
