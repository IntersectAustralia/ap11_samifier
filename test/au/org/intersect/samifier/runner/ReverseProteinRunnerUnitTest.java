package au.org.intersect.samifier.runner;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ReverseProteinRunnerUnitTest
{
    @Test
    public void testReverseProteinRunner()
    {
        try
        {
            String [] mascotFiles = {"test/resources/merger/test_mascot_search_results.txt"};
            File genomeFile = new File("test/resources/merger/virtual_protein.gff");
            File chromosomeDir = new File("test/resources/merger/");
            File translationTableFile = new File("test/resources/merger/bacterial_translation_table.txt");

            File resultAnalysisFile = File.createTempFile("out", "txt");
            resultAnalysisFile.deleteOnExit();

            ReverseProteinRunner analyser = new ReverseProteinRunner(mascotFiles, translationTableFile, genomeFile, chromosomeDir);

            analyser.run();

        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
