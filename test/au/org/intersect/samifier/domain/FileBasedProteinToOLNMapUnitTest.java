package au.org.intersect.samifier.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileBasedProteinToOLNMapUnitTest
{
    FileBasedProteinToOLNMap map = null;

    @Before
    public void oneTimeSetup()
    {
        map = new FileBasedProteinToOLNMap();
    }

    @Test
    public void testContainsProtein()
    {
        map.addMapping("one", "two");
        assertTrue(map.containsProtein("one"));
    }

    @Test
    public void testDoesNotContainProtein()
    {
        map.addMapping("one", "two");
        assertFalse(map.containsProtein("other"));
    }

    @Test
    public void testMapsCorrectlr()
    {
        map.addMapping("one", "two");
        assertEquals("two", map.getOLN("one"));
    }
}
