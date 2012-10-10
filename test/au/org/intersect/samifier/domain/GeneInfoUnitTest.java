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
        au.org.intersect.samifier.domain.GeneInfo gene = new au.org.intersect.samifier.domain.GeneInfo();
        au.org.intersect.samifier.domain.GeneSequence gs1 = new au.org.intersect.samifier.domain.GeneSequence(au.org.intersect.samifier.domain.GeneSequence.CODING_SEQUENCE, 1, 10, null);
        gene.addLocation(gs1);
        List<au.org.intersect.samifier.domain.GeneSequence> locationList = gene.getLocations();
        assertSame("Adding a location", gs1, locationList.get(0));
    }

    @Test
    public void testAddLocationPreservesOrder()
    {
        au.org.intersect.samifier.domain.GeneInfo gene = new GeneInfo();
        au.org.intersect.samifier.domain.GeneSequence gs1 = new au.org.intersect.samifier.domain.GeneSequence(au.org.intersect.samifier.domain.GeneSequence.CODING_SEQUENCE, 1, 10, null);
        au.org.intersect.samifier.domain.GeneSequence gs2 = new au.org.intersect.samifier.domain.GeneSequence(au.org.intersect.samifier.domain.GeneSequence.CODING_SEQUENCE, 2, 10, null);
        au.org.intersect.samifier.domain.GeneSequence gs3 = new au.org.intersect.samifier.domain.GeneSequence(au.org.intersect.samifier.domain.GeneSequence.CODING_SEQUENCE, 3, 10, null);
        gene.addLocation(gs3);
        gene.addLocation(gs1);
        gene.addLocation(gs2);
        List<au.org.intersect.samifier.domain.GeneSequence> locationList = gene.getLocations();
        assertSame("First gene sequence is gs1", gs1, locationList.get(0));
        assertSame("Second gene sequence is gs2", gs2, locationList.get(1));
        assertSame("Third gene sequence is gs3", gs3, locationList.get(2));
    }

}
