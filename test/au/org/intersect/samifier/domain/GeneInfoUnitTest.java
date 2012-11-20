package au.org.intersect.samifier.domain;


import static org.junit.Assert.*;

import org.junit.Test;

import java.util.List;

/**
 * * Tests {@link au.org.intersect.samifier.parser.GenomeParserImpl}
 * */
public final class GeneInfoUnitTest
{
    @Test
    public void testAddLocation()
    {
        GeneInfo gene = new GeneInfo();
        GeneSequence gs1 = new GeneSequence("PARENT01", true, 1, 10, 1);
        gene.addLocation(gs1);
        List<GeneSequence> locationList = gene.getLocations();
        assertSame("Adding a location", gs1, locationList.get(0));
    }

    @Test
    public void testAddLocationPreservesOrder()
    {
        GeneInfo gene = new GeneInfo();
        GeneSequence gs1 = new GeneSequence("G01", true, 1, 10, 1);
        GeneSequence gs2 = new GeneSequence("G01", true, 2, 10, 1);
        GeneSequence gs3 = new GeneSequence("G01", true, 3, 10, 1);
        gene.addLocation(gs3);
        gene.addLocation(gs1);
        gene.addLocation(gs2);
        List<GeneSequence> locationList = gene.getLocations();
        assertSame("First gene sequence is gs1", gs1, locationList.get(0));
        assertSame("Second gene sequence is gs2", gs2, locationList.get(1));
        assertSame("Third gene sequence is gs3", gs3, locationList.get(2));
    }

}
