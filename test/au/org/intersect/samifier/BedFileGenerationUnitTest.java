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
        PeptideSequence sequence = new PeptideSequence("ATGTTA", "6M", 100, 20, 26, null);
        String actualBed = Samifier.createBEDLine(sequence, "chrI", "KPYK1_YEAST.q21_p1");
        String lineFeed = System.getProperty("line.separator");
        String expectedBed = "chrI\t20\t26\tKPYK1_YEAST.q21_p1" + lineFeed;
        assertEquals("Should produce a valid BED format line",actualBed, expectedBed);
    }
}
