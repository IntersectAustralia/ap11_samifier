package au.org.intersect.samifier.generator;

import au.org.intersect.samifier.domain.GenomeConstant;
import au.org.intersect.samifier.domain.ProteinLocation;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class CodonsPerIntervalLocationGeneratorUnitTest
{
    @Test
    public void testCreateLocations()
    {
        try {
            File genomeFile = new File("test/resources/protein_generator/test_genome_short.faa");
            CodonsPerIntervalLocationGenerator locationGenerator = new CodonsPerIntervalLocationGenerator("20", genomeFile);

            List<ProteinLocation> locations = locationGenerator.generateLocations();
            for (ProteinLocation loc : locations)
            {
                System.out.println(loc);
            }
            assertEquals("Should generate 30 locations", 30, locations.size());
            int forward = 0;
            int forwardHalf = 0;
            int reverse = 0;
            int reverseHalf = 0;
            for (ProteinLocation loc : locations)
            {
                if (loc.getDirection().equals(GenomeConstant.FORWARD_FLAG))
                {
                    forward++;
                    if (loc.getName().matches("p\\d+b\\.\\+\\d+"))
                    {
                        forwardHalf++;
                    }
                }
                else
                {
                    reverse++;
                    if (loc.getName().matches("p\\d+b\\.\\-\\d+"))
                    {
                        reverseHalf++;
                    }
                }
            }
            assertEquals("Should have 15 forward locations", 15, forward);
            assertEquals("Should have 6 forward half interval locations", 6, forwardHalf);
            assertEquals("Should have 15 reverse locations", 15, reverse);
            assertEquals("Should have 6 reverse half interval locations", 6, reverseHalf);
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testProteinName()
    {

    }


}
