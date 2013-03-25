package au.org.intersect.samifier.runner;

import org.junit.Test;

import java.io.File;
import java.io.StringWriter;

import static org.junit.Assert.fail;

public class VirtualProteinMergerRunnerUnitTest
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

            StringWriter out = new StringWriter();

            VirtualProteinMergerRunner analyser = new VirtualProteinMergerRunner(mascotFiles, translationTableFile, genomeFile, chromosomeDir, out);
            analyser.run();
            System.out.println(out.toString());
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
