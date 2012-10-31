package au.org.intersect.samifier.domain;

public class GffOutputterGenerator implements ProteinLocationBasedOutputterGenerator
{
    private String genomeFilename;

    public GffOutputterGenerator(String genomeFilename)
    {
        this.genomeFilename = genomeFilename;
    }

    @Override
    public GffOutputter getOutputterFor(ProteinLocation proteinLocation)
    {
        return new GffOutputter(proteinLocation, genomeFilename);
    }
}
