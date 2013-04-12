package au.org.intersect.samifier.generator;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import au.org.intersect.samifier.runner.ProteinGeneratorRunner;

public class ProteinGeneratorWithContigTest {

    @Test
    public void testProteinGeneratorWithContigAndGlimmer()
    {
        try {
            File genomeFile = new File("test/resources/protein_generator/contig_fasta.fa");
            File tableFile = new File("test/resources/protein_generator/bacterial_translation_table.txt");
            String glimmerFile = "test/resources/protein_generator/glimmer_contig.txt";
            StringWriter out = new StringWriter();
            StringWriter gff = new StringWriter();
            StringWriter accession = new StringWriter();

            ProteinGeneratorRunner runner = new ProteinGeneratorRunner(glimmerFile, genomeFile, null, "testdb", out, tableFile, gff, accession);
            runner.run();
            verifyExpectedOutput(out,  "test/resources/protein_generator/expected_contig_file.fa");
            verifyExpectedOutput(gff,  "test/resources/protein_generator/expected_gff_contig_glimmer.gff");
            verifyExpectedOutput(accession, "test/resources/protein_generator/expected_accession_file_contig_glimmer.txt");
        }
        
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @Test
    public void testProteinGeneratorWithContigAndInterval()
    {
        try {
            File genomeFile = new File("test/resources/protein_generator/contig_fasta.fa");
            File tableFile = new File("test/resources/protein_generator/bacterial_translation_table.txt");
            StringWriter out = new StringWriter();
            StringWriter gff = new StringWriter();
            StringWriter accession = new StringWriter();
            
            ProteinGeneratorRunner runner = new ProteinGeneratorRunner(null, genomeFile, "100", "testdb", out, tableFile, gff, accession);
            runner.run();
            verifyExpectedOutput(out,  "test/resources/protein_generator/expected_contig_interval.fa");
            verifyExpectedOutput(gff,  "test/resources/protein_generator/expected_gff_contig_interval.gff");
            verifyExpectedOutput(accession, "test/resources/protein_generator/expected_accession_file_contig_interval.txt");
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void verifyExpectedOutput(StringWriter actualOutput, String fileWithExpectedOutput) throws Exception {
        List<String> expectedLines = FileUtils.readLines(new File(fileWithExpectedOutput));
        String [] outputAsArray = actualOutput.toString().split(System.getProperty("line.separator"));
        assertEquals(expectedLines.size(), outputAsArray.length);
        for (int i = 0; i < expectedLines.size(); i++)
        {
            assertEquals("Line " + i + " should be", expectedLines.get(i), outputAsArray[i]);
        }
    }
}
