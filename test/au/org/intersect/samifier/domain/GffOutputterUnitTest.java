package au.org.intersect.samifier.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class GffOutputterUnitTest
{
    public GffOutputterUnitTest()
    {
    }

    @Test
    public void testGffOutputWithGenomeFileWithoutExtension()
    {
        ProteinLocation proteinLocation = new ProteinLocation("a",1,2,"+", "1");
        GffOutputter gffOutputter = new GffOutputter(proteinLocation,"test");
        String lineFeed = System.getProperty("line.separator");
        String firstLine = gffOutputter.getOutput().split(lineFeed)[0];
        String firstToken = firstLine.split(" ")[0];
        assertEquals("test", firstToken);
    }

    @Test
    public void testGffOutputWithGenomeFileWithExtension()
    {
        ProteinLocation proteinLocation = new ProteinLocation("a",1,2,"+", "1");
        GffOutputter gffOutputter = new GffOutputter(proteinLocation,"test.txt");
        String lineFeed = System.getProperty("line.separator");
        String firstLine = gffOutputter.getOutput().split(lineFeed)[0];
        String firstToken = firstLine.split(" ")[0];
        assertEquals("test", firstToken);
    }

    @Test
    public void testGffWholeOutput()
    {
        ProteinLocation proteinLocation = new ProteinLocation("glimmer_name",1,2,"+", "1");
        GffOutputter gffOutputter = new GffOutputter(proteinLocation,"test");
        String lineFeed = System.getProperty("line.separator");

        StringBuffer expectedOutput = new StringBuffer();
        expectedOutput.append("test Glimmer gene  1 3 0 + 1 ID=glimmer_name;Name=glimmer_name;Note=");
        expectedOutput.append(lineFeed);
        expectedOutput.append("test Glimmer CDS  1 3 0 + 1 ID=glimmer_name;Name=glimmer_name;Note=");
        expectedOutput.append(lineFeed);
        assertEquals(expectedOutput.toString(), gffOutputter.getOutput());
    }


}
