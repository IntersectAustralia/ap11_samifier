package au.org.intersect.samifier.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class AccessionOutputterUnitTest
{

    public AccessionOutputterUnitTest()
    {
    }

    @Test
    public void testOutput() throws OutputException {
        ProteinLocation proteinLocation = new ProteinLocation("a",1,2,"+", "1");
        AccessionOutputter outputter = new AccessionOutputter(proteinLocation);

        String actual = outputter.getOutput();

        String expected = "a a a" +System.getProperty("line.separator");

        assertEquals(expected, actual);
    }
}
