package au.org.intersect.samifier;


import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
import org.junit.Before;

import java.io.File;
import java.util.List;

/**
 * * Tests {@link Genome}
 * */
public final class GeneInfoUnitTest
{
    @Test
    public void testAddLocation()
    {
        GeneInfo gene = new GeneInfo();
        GeneSequence gs1 = new GeneSequence(GeneSequence.CODING_SEQUENCE, 1, 10, null);
        gene.addLocation(gs1);
        List<GeneSequence> locationList = gene.getLocations();
        assertSame("Adding a location", gs1, locationList.get(0));
    }

    @Test
    public void testAddLocationPreservesOrder()
    {
        GeneInfo gene = new GeneInfo();
        GeneSequence gs1 = new GeneSequence(GeneSequence.CODING_SEQUENCE, 1, 10, null);
        GeneSequence gs2 = new GeneSequence(GeneSequence.CODING_SEQUENCE, 2, 10, null);
        GeneSequence gs3 = new GeneSequence(GeneSequence.CODING_SEQUENCE, 3, 10, null);
        gene.addLocation(gs3);
        gene.addLocation(gs1);
        gene.addLocation(gs2);
        List<GeneSequence> locationList = gene.getLocations();
        assertSame("First gene sequence is gs1", gs1, locationList.get(0));
        assertSame("Second gene sequence is gs2", gs2, locationList.get(1));
        assertSame("Third gene sequence is gs3", gs3, locationList.get(2));
    }

}
