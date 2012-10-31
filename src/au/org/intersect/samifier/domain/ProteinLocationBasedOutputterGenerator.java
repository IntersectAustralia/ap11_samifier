package au.org.intersect.samifier.domain;


public interface ProteinLocationBasedOutputterGenerator
{
    public Outputter getOutputterFor(ProteinLocation proteinLocation);
}
