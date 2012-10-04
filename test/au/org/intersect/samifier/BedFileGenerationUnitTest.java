package au.org.intersect.samifier;


import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import java.io.File;


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
        String actualBed = Samifier.createBEDLine(sequence, "KPYK1_YEAST");
        String lineFeed = System.getProperty("line.separator");
        String expectedBed = "chrI\t80000\t80100\tKPYK1_YEAST" + lineFeed;
        assertEquals("Should produce a valid BED format line",actualBed, expectedBed);
    }
}
