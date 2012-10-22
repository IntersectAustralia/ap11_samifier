
package au.org.intersect.samifier.runner;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * * Tests {@link au.org.intersect.samifier.Samifier}
 * */
public final class SamifierBuildingUnitTest
{

    @Test
    public void testCreateSAM()
    {
        try {
            String [] mascotFiles = {"test/resources/test_mascot_search_results.txt"};
            File mapFile = new File("test/resources/test_accession.txt");
            File genomeFile = new File("test/resources/test_genome.gff");
            File chromosomeDir = new File("test/resources/");
            File samFile = File.createTempFile("out", "sam");

            SamifierRunner runner = new SamifierRunner(mascotFiles, genomeFile, mapFile, chromosomeDir, samFile, null, null);
            runner.run();

            List<String> expectedLines = FileUtils.readLines(new File("test/resources/expected.sam"));
            List<String> gotLines = FileUtils.readLines(samFile);
            assertEquals("Should generate the expected SAM file", expectedLines, gotLines);
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateSAMWithConfidenceScoreFilter()
    {
        try {
            String [] mascotFiles = {"test/resources/test_mascot_search_results.txt"};
            File mapFile = new File("test/resources/test_accession.txt");
            File genomeFile = new File("test/resources/test_genome.gff");
            File chromosomeDir = new File("test/resources/");
            File samFile = File.createTempFile("out", "sam");

            BigDecimal confidenceScore = new BigDecimal("37.51");

            SamifierRunner runner = new SamifierRunner(mascotFiles, genomeFile, mapFile, chromosomeDir, samFile, null, confidenceScore);
            runner.run();

            List<String> expectedLines = FileUtils.readLines(new File("test/resources/expected_filtered.sam"));
            List<String> gotLines = FileUtils.readLines(samFile);
            assertEquals("Should generate the expected SAM file", expectedLines, gotLines);
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
