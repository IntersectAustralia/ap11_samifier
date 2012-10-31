package au.org.intersect.samifier.generator;

import au.org.intersect.samifier.domain.ProteinLocation;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class GlimmerFileLocationGeneratorUnitTest
{
    @Test
    public void testParsingGlimmerFile()
    {
        GlimmerFileLocationGenerator locationGenerator = new GlimmerFileLocationGenerator("test/resources/protein_generator/test_glimmer.txt");
        List<ProteinLocation> proteins = null;
        try
        {
            proteins = locationGenerator.generateLocations();
        }
        catch(Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
        assertEquals("Should have 13 protein locations", 13, proteins.size());
    }


}
