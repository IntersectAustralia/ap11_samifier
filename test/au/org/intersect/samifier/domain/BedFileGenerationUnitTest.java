package au.org.intersect.samifier.domain;


import static org.junit.Assert.*;

import org.junit.Test;


public class BedFileGenerationUnitTest
{

    public BedFileGenerationUnitTest(){}

    @Test
    public void testBedFileGeneration()
    {
        GeneInfo geneInfo = new GeneInfo();
        geneInfo.setChromosome("chrI");
        geneInfo.setStart(80000);
        geneInfo.setStop(80100);
        PeptideSequence sequence = new PeptideSequence("ATGTTA", "6M", 100, 20, 26, geneInfo);
        BedLineOutputter outputter = new BedLineOutputter(sequence, "KPYK1_YEAST");
        String actualBed = outputter.toString();
        String lineFeed = System.getProperty("line.separator");
        String expectedBed = "chrI\t80000\t80100\tKPYK1_YEAST" + lineFeed;
        assertEquals("Should produce a valid BED format line",actualBed, expectedBed);
    }
}
