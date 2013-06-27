package au.org.intersect.samifier.runner;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.StringWriter;
import java.util.List;

import static org.junit.Assert.*;

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

            VirtualProteinMergerRunner analyser = new VirtualProteinMergerRunner(mascotFiles, translationTableFile, genomeFile, chromosomeDir, out, null);
            analyser.run();
            List<String> expectedLines = FileUtils.readLines(new File("test/resources/expected_vpm.gff"));
            String [] outputAsArray = out.toString().split(System.getProperty("line.separator"));
            assertEquals(outputAsArray.length, expectedLines.size());
            
            for (int i = 0; i < expectedLines.size(); i++)
            {
                assertEquals("Line " + i + " should be", expectedLines.get(i), outputAsArray[i]);
            }
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @Test
    public void testWithVirtualProteins() {
        try
        {
            String [] mascotFiles = {"test/resources/merger/vp_mascot.txt"};
            File genomeFile = new File("test/resources/merger/vp.gff");
            File chromosomeDir = new File("test/resources/merger/NC_009802.fa");
            File translationTableFile = new File("test/resources/merger/bacterial_translation_table.txt");

            StringWriter out = new StringWriter();

            VirtualProteinMergerRunner analyser = new VirtualProteinMergerRunner(mascotFiles, translationTableFile, genomeFile, chromosomeDir, out, null);
            analyser.run();
            List<String> expectedLines = FileUtils.readLines(new File("test/resources/merger/expected_vp.gff"));
            String [] outputAsArray = out.toString().split(System.getProperty("line.separator"));
            assertEquals(outputAsArray.length, expectedLines.size());
            
            for (int i = 0; i < expectedLines.size(); i++)
            {
                assertEquals("Line " + i + " should be", expectedLines.get(i), outputAsArray[i]);
            }
            File mapFile = new File("test/resources/vp_driven.accession");
            
            genomeFile = new File("test/resources/merger/expected_vp.gff");
            File samFile = File.createTempFile("out", "sam");
            SamifierRunner runner = new SamifierRunner(mascotFiles, genomeFile, mapFile, chromosomeDir, samFile, null, null);
            runner.run();
            expectedLines = FileUtils.readLines(new File("test/resources/vp_driven.sam"));
            List<String> gotLines = FileUtils.readLines(samFile);
            assertEquals("Should generate the expected SAM file", expectedLines, gotLines);
           
        } catch(Exception e) {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
