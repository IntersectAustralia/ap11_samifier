package au.org.intersect.samifier.runner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class SamifierRunnerWithIntrons {
	    //@Test
	    public void testCreateSAM()
	    {
	        try {
	            String [] mascotFiles = {"test/resources/fasta/mascot_results.txt"};
	            File mapFile = new File("test/resources/fasta/test_accession.txt");
	            File genomeFile = new File("test/resources/fasta/test_gff.gff");
	            File chromosomeDir = new File("test/resources/fasta/");
	            File samFile = File.createTempFile("out", "sam");
	            String bedFile = "bed_file.bed";

	            SamifierRunner runner = new SamifierRunner(mascotFiles, genomeFile, mapFile, chromosomeDir, samFile, bedFile, null);
	            runner.run();
	            
	            //List<String> expectedLines = FileUtils.readLines(new File("test/resources/expected.sam"));
	            List<String> gotLines = FileUtils.readLines(samFile);
	            //line 1 should contain: TTGTTCTCTCTA
	            for (String line : gotLines) System.out.println(line);
	            assertEquals(true, gotLines.get(0).contains("TTGTTCTCTCTA"));
	            
	            //TTGCTTCTCCTACTGATTATCATAATGGTTGTC
	            assertEquals(true, gotLines.get(1).contains("TTGCTTCTCCTACTGATTATCATAATGGTTGTC"));
	            //ATAATGGTTGTCGTA
	            assertEquals(true, gotLines.get(2).contains("ATAATGGTTGTCGTA"));
	            //CTTCTCCTACTGATTATC
	            assertEquals(true, gotLines.get(3).contains("CTTCTCCTACTGATTATC")); 
	            //TTCTTATTGCTTCTCCTACTGATTATC
	            assertEquals(true, gotLines.get(4).contains("TTCTTATTGCTTCTCCTACTGATTATC"));
	            //ATAATGGTTGTCGTTCTC
	            assertEquals(true, gotLines.get(5).contains("ATAATGGTTGTCGTTCTC"));
	            //assertEquals("Should generate the expected SAM file", expectedLines, gotLines);
	        }
	        catch(Exception e)
	        {
	            fail("Unexpected exception: " + e.getMessage());
	            e.printStackTrace();
	        }
	    }
	 @Test
	 public void testCreateSAMWithReverseGFF()
     {
         try {
             String [] mascotFiles = {"test/resources/fasta/reverse/mascot_results.txt"};
             File mapFile = new File("test/resources/fasta/reverse/test_accession.txt");
             File genomeFile = new File("test/resources/fasta/reverse/test_gff.gff");
             File chromosomeDir = new File("test/resources/fasta/reverse/");
             File samFile = File.createTempFile("out", "sam");

             SamifierRunner runner = new SamifierRunner(mascotFiles, genomeFile, mapFile, chromosomeDir, samFile, null, null);
             runner.run();
             
             //List<String> expectedLines = FileUtils.readLines(new File("test/resources/expected.sam"));
             List<String> gotLines = FileUtils.readLines(samFile);
             //line 1 should contain: TTGTTCTCTCTA
             for (String line : gotLines) System.out.println(line);
             
         }
         catch(Exception e)
         {
             fail("Unexpected exception: " + e.getMessage());
             e.printStackTrace();
         }
     }
}
