package au.org.intersect.samifier.runner;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.StringWriter;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * * Tests {@link ProteinGeneratorRunner}
 * */
public final class ProteinGeneratorUnitTest
{
    @Test
    public void testGenerateVirtualProteins()
    {
        try {
            File genomeFile = new File("test/resources/protein_generator/test_genome_short.faa");
            File tableFile = new File("test/resources/protein_generator/bacterial_translation_table.txt");

            StringWriter out = new StringWriter();

            ProteinGeneratorRunner runner = new ProteinGeneratorRunner(null, genomeFile, "20", "testdb", out, tableFile, null, null);
            runner.run();

            List<String> expectedLines = FileUtils.readLines(new File("test/resources/protein_generator/test_virtual_protein_file.fa"));
            String [] outputAsArray = out.toString().split(System.getProperty("line.separator"));
            assertEquals(outputAsArray.length, expectedLines.size());
            System.out.println(out);
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
    public void testGenerateProteinsFileFromGlimmerInput()
    {
        try {
            File genomeFile = new File("test/resources/protein_generator/test_genome.faa");
            String glimmerFilePath = "test/resources/protein_generator/test_glimmer.txt";
            File tableFile = new File("test/resources/protein_generator/bacterial_translation_table.txt");
            StringWriter out = new StringWriter();

            ProteinGeneratorRunner runner = new ProteinGeneratorRunner(glimmerFilePath, genomeFile, null, "testdb", out, tableFile, null, null);

            runner.run();
            List<String> expectedLines = FileUtils.readLines(new File("test/resources/protein_generator/test_protein_file.fa"));
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


}
